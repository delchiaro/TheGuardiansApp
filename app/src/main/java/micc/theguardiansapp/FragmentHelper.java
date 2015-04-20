package micc.theguardiansapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;

/**
 * Created by nagash on 20/04/15.
 */
public class FragmentHelper {


    private static FragmentHelper istance = null;
    private static MainActivity mainActivity = null;

    public static FragmentHelper instance(){
        if(istance == null) istance = new FragmentHelper();
        return istance;
    }

    private FragmentHelper() {

    }

    public static void setMainActivity(MainActivity activity) {
        // if(mainActivity == null) { // NO!!!! IN QUESTO MODO chiudendo e riaprendo l'app non cambio l'address della main activiti -> force close al primo fragment transaction !!!
        mainActivity = activity;
        //}
        // settabile solo 1 volta
    }




    //Metodo per lo swap di fragments
    public static void swapFragment(int containerID, Fragment newFragment) {
        if (containerID != newFragment.getId()) {
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(containerID, newFragment);
            //transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }



    public static int dpToPx(int dimensionInDp)
    {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimensionInDp, mainActivity.getResources().getDisplayMetrics());
        return px;
    }

}
