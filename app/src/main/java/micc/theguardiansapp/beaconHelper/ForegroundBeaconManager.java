package micc.theguardiansapp.beaconHelper;


import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import micc.theguardiansapp.beaconHelper.MyBeaconListener;

/**
 * Created by nagash on 01/05/15.
 */
public class ForegroundBeaconManager
{

    private static final String TAG = "SIMPLE_BEACON_MANAGER";

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final int MAJOR = 27910;
    private static final Region MY_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, MAJOR, null);

    private int BEACON_TIMEOUT = 5000; // millisecondi
    private int SCANNING_PERIOD = 1000;
    private int SCANNING_DELAY = 0;

    private int REFRESH_BEACON_SLEEP_TIME = 3000;


    private static final int PROXIMITY_DISTANCE = 5;


    Thread refresherThread;
    private MyBeaconListener listener;

    private volatile Map<Integer, Beacon> proximityBeacons;
    private Map<Integer, Long> minor_istant_map = new HashMap<>(10);

    private BeaconManager beaconManager;
    Context context;

    private volatile boolean runRefreshTask = false;









    public ForegroundBeaconManager(Context context, MyBeaconListener listener) {
        beaconManager = new BeaconManager(context);
        this.context = context;
        this.listener = listener;
        proximityBeacons = new HashMap<>(10);

        init();
    }

    private void init() {
        //beaconManager.setBackgroundScanPeriod(1000, 3000 );

        beaconManager.setForegroundScanPeriod(SCANNING_PERIOD, SCANNING_DELAY);

        // Should be invoked in #onCreate.
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {

            @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                Log.d(TAG, "Ranged beacons: " + beacons);
                onBeaconProximity(beacons);
            }

        });


    }

    public void start()  {
         // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(MY_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
        //startRefreshTask();
    }

    public void stop() {

        //stopRefreshTask();
        // Should be invoked in #onStop.
        try {
            beaconManager.stopRanging(MY_BEACONS);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }

        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }








    public final int getBeaconSize(){
        return proximityBeacons.size();
    }

    public final Map<Integer, Beacon> getProximityBeacons(){
        return new HashMap<>(proximityBeacons);
    }

    protected final double getDistance(Beacon beacon) {
        return Utils.computeAccuracy(beacon);
    }

    protected final boolean isInProximity(Beacon beacon)
    {
        double distance = getDistance(beacon);
        if(distance <= PROXIMITY_DISTANCE )
            return true;
        else return false;
    }

    private void onBeaconProximity(List<Beacon> detectedBeacons)
    {
        long istant = System.currentTimeMillis();


        // proximityBeaconsNewList: COSTRUISCO LA LISTA DI TUTTI I BEACONS ENTRO LA PROSSIMITÁ SCELTA
        List<Beacon> proximityBeaconsNewList =  new ArrayList<>(detectedBeacons.size());

        for (Beacon beacon : detectedBeacons)
        {
            if (isInProximity(beacon))
            {
                proximityBeaconsNewList.add(beacon);
            }
        }


        List<Beacon> newInOld = new ArrayList<>(proximityBeaconsNewList.size());
        List<Beacon> newNotInOld = new ArrayList<>(proximityBeaconsNewList.size());
        HashMap<Integer, Beacon> oldNotNew = new HashMap<>(proximityBeacons);

        for(Beacon b2 : proximityBeaconsNewList)
        {
            oldNotNew.remove(b2.getMinor());

            if ( proximityBeacons.containsKey(b2.getMinor()) )
            {
                newInOld.add(b2);
            }
            else
            {
                newNotInOld.add(b2);
            }

        }
        // newInOld contiene tutti gli elementi della nuova lista che sono anche nella vecchia lista.
        // newNotInOld contiene tutti gli elementi della nuova lista che non sono nella vecchia lista.
        // oldNotInNew contiene tutti gli elementi della vecchia lista che non sono nella nuova.


        List<Beacon> removedBeacons = new ArrayList<>(this.proximityBeacons.size());


        for( Beacon b : newInOld)
        {
            // voglio refreshare tutti gli istanti di last refresh di ogni beacon considerato vicino
            // se necessario eliminarlo e poi re inserirlo, altrimenti re inserirlo e basta (se fa..)
            minor_istant_map.remove(b.getMinor());
            minor_istant_map.put(b.getMinor(), istant );
        }
        for( Beacon b : newNotInOld)
        {
            // Inserisco tutti i nuovi beacon rilevati che non erano giá in lista
            minor_istant_map.put(b.getMinor(), istant);
            proximityBeacons.put(b.getMinor(), b);
        }





        for( Beacon b : oldNotNew.values() )
        {
            // per ogni beacon vecchio che a questo refresh non è presente,
            // se è passato troppo tempo dall'ultimo refresh verrá eliminato.

            long bIstant =  minor_istant_map.get(b.getMinor());
            if(istant-bIstant > BEACON_TIMEOUT)
            {
                proximityBeacons.remove(b.getMinor());
                minor_istant_map.remove(b.getMinor());
                removedBeacons.add(b);
            }
        }


        listener.onNewBeacons(newNotInOld);
        listener.onRemovedBeacons(removedBeacons);
    }









//* * * * * * * * * * * *  REFRESH TASK * * * * * * * * * * * * * * * * * * * * * * * * * * * *


    private class RefreshTask extends AsyncTask<Void, Void, Boolean>
    {

        private volatile List<Beacon> removedBeacons;

        public boolean  refreshBeacons()
        {
            long istant = System.currentTimeMillis();
            removedBeacons = new ArrayList<>(proximityBeacons.size());

            for( Beacon b : proximityBeacons.values() )
            {
                // per ogni beacon, se è passato troppo tempo dall'ultima volta che è stato trovato
                // col bluetooth, verrá eliminato.

                long bIstant =  minor_istant_map.get(b.getMinor());
                if(istant-bIstant < BEACON_TIMEOUT)
                {
                    proximityBeacons.remove(b);
                    removedBeacons.add(b);
                }
            }
            return( removedBeacons.size() > 0);
        }


        @Override
        protected Boolean doInBackground(Void... params) {


            try {
                Thread.currentThread().sleep(REFRESH_BEACON_SLEEP_TIME);
            }
            catch (InterruptedException e) {
            }
            return refreshBeacons();
        }



        @Override
        protected void onPostExecute(Boolean removedSomething) {
            super.onPostExecute(removedSomething);
            if(removedSomething) listener.onRemovedBeacons(removedBeacons);
            if(runRefreshTask)
                new RefreshTask().execute();

        }


    }



    private void startRefreshTask(){
        if(this.runRefreshTask == false )
        {
            this.runRefreshTask = true;
            new RefreshTask().execute();
        }
    }

    private void stopRefreshTask() {
        this.runRefreshTask = false;
    }









//    private void startThread()
//    {
//
//        AsyncTask t = new
//
//        refresherThread = new Thread(new Runnable() {
//
//            public void run()
//            {
//
//                while(!Thread.currentThread().isInterrupted())
//                {
//
//
//
//
//                }
//
//            }
//
//
//
//        });
//    }
//    private void stopThread()
//    {
//        refresherThread.interrupt();
//    }




    //
//    private class BeaconProximityTask extends AsyncTask<List<Beacon>, Void, Boolean>
//    {
//
//        @Override
//        protected Boolean doInBackground(List<Beacon> ... detectedBeacons) {
//            onBeaconProximity(detectedBeacons[0]);
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
//            super.onPostExecute(aBoolean);
//        }
//    }


}
