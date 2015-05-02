package micc.theguardiansapp.beaconHelper;


import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

/**
 * Created by nagash on 01/05/15.
 */
public class BackgroundBeaconManager
{

    private static final String TAG = "SIMPLE_BEACON_MANAGER";

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, 27910, null);


    private BeaconManager beaconManager;
    Context context;


    public BackgroundBeaconManager(Context context)
    {
        beaconManager = new BeaconManager(context);
        this.context = context;
        init();
    }

    private void init()
    {

        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Beacon vicino", duration);
                toast.show();
            }

            @Override
            public void onExitedRegion(Region region) {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Beacon Lontano", duration);
                toast.show();

            }
        });

        beaconManager.setBackgroundScanPeriod(4000, 10000 );


//
//        // Should be invoked in #onCreate.
//        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
//            @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
//                Log.d(TAG, "Ranged beacons: " + beacons);
//            }
//        });

    }

    public void start()
    {


        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                try {
                    beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS);
                   // beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }


    public void stop()
    {
        // Should be invoked in #onStop.
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }

        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }


}
