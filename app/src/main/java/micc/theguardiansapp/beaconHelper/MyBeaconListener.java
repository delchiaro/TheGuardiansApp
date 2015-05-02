package micc.theguardiansapp.beaconHelper;

import com.estimote.sdk.Beacon;

import java.util.List;
import java.util.Map;

/**
 * Created by nagash on 27/04/15.
 */
public interface MyBeaconListener {

    public void onNewBeacons(List<Beacon> newInProximityBeaconList);
    public void onRemovedBeacons( List<Beacon> removedBeacons);

}
