package micc.theguardiansapp.beaconHelper;

import com.estimote.sdk.Beacon;

/**
 * Created by nagash on 28/04/15.
 */
public class BeaconID
{
    private String UUID;
    private int minor;
    private int major;

    public BeaconID(Beacon beacon) {
        this.UUID = beacon.getProximityUUID();
        this.minor = beacon.getMinor();
        this.major = beacon.getMajor();
    }




    public boolean compare(BeaconID b2)
    {
        return compareBeacons(this, b2);
    }

    public boolean compare(Beacon b2)
    {
        return compareBeacons(this, new BeaconID(b2));
    }



    public static boolean compareBeacons(BeaconID b1, BeaconID b2) {
        if(b1.UUID == b2.UUID && b1.minor == b2.minor && b1.major == b2.major)
            return true;
        else return false;
    }

    public static boolean compareBeacons(Beacon b1, Beacon b2) {
        return compareBeacons(new BeaconID(b1), new BeaconID(b2));

    }
}
