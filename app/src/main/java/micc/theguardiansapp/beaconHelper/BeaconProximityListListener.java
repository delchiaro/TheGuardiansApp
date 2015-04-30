package micc.theguardiansapp.beaconHelper;

import com.estimote.sdk.Beacon;

import java.util.List;

/**
 * Created by nagash on 27/04/15.
 */
public interface BeaconProximityListListener {

    public void onListChanged(List<Beacon> inProximityBeaconList, List<Beacon> noMoreInProximityBeaconList  );


}
