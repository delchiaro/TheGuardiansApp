package micc.theguardiansapp.beaconHelper;

import android.app.Activity;
import android.util.TimeFormatException;
import android.util.TimeUtils;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.security.Timestamp;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * Created by nagash on 27/04/15.
 */
public class BeaconProximityLister implements  BeaconProximityListener
{


    private BeaconHelper helper;
    private BeaconProximityListListener listener;
    private List<Beacon> proximityBeacons;
    private Map<BeaconID, Long> beaconRefreshIstant = new HashMap<>();
    private int proximityDistanceMeters;

    public BeaconProximityLister (Activity activity, BeaconProximityListListener listener, int proximityDistanceInMeters) {
        this.listener = listener;
        helper  = new BeaconHelper(activity);
        helper.addProximityListener(this);
        proximityBeacons = new ArrayList<>(20);
        this.proximityDistanceMeters = proximityDistanceInMeters;
    }

    public void scan() {
        helper.scanBeacons();
    }
    public void stopScan() {
        helper.stopScan();
    }



    protected double getDistance(Beacon beacon) {
        return Utils.computeAccuracy(beacon);
    }

    protected boolean isInProximity(Beacon beacon)
    {
        double distance = getDistance(beacon);
        if(distance <= proximityDistanceMeters )
            return true;
        else return false;
    }

    protected boolean compareBeacons(Beacon b1, Beacon b2) {

        return BeaconID.compareBeacons(b1, b2);
//        if(b1.getProximityUUID() == b2.getProximityUUID()
//                && b1.getMinor() == b2.getMinor()
//                && b1.getMajor() == b2.getMajor())
//            return true;
//        else return false;

    }


    @Override
    public void OnBeaconProximity(List<Beacon> detectedBeacons)
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



        List<Beacon> oldInNew = new ArrayList<>(this.proximityBeacons.size());
        List<Beacon> oldNotInNew = new ArrayList<>(this.proximityBeacons.size());
        List<Beacon> newNotInOld = new ArrayList<>(proximityBeaconsNewList.size());

        for(Beacon b1 : this.proximityBeacons)
        {
            boolean isInNewList = false;
            for(Beacon b2 : proximityBeaconsNewList )
            {
                if(compareBeacons(b1,b2))
                {
                    isInNewList = true;
                    oldInNew.add(b1);
                    break;
                }
            }
            if(isInNewList == false)
                oldNotInNew.add(b1);
        }
        for(Beacon b2 : proximityBeaconsNewList)
        {
            boolean isInOldList = false;
            for(Beacon b1 : this.proximityBeacons )
            {
                if(compareBeacons(b1,b2))
                {
                    isInOldList = true;
                    break;
                }
            }
            if(isInOldList == false)
                newNotInOld.add(b2);
        }


        for( Beacon b : oldNotInNew )
        {
            beaconRefreshIstant.get(new BeaconID(b));
            // avrá un indirizzo diverso non troverá nulla.. va fatta una ricerca

        }



    }


}
