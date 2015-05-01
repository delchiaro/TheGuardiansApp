package micc.theguardiansapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import micc.theguardiansapp.beaconHelper.*;

import com.estimote.sdk.Beacon;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class MainActivity extends ActionBarActivity implements MyBeaconListener {



    private static final int REFRESH_BEACON_DELAY = 5000;
    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;
    private MediaPlayer mPlayer;
    private boolean audioStarted = false;

    private boolean atLeastOneBeacon = false;

    private static final int REQUEST_ENABLE_BT = 1234;


    private BackgroundBeaconManager backgroundBeaconManager;

    private ForegroundBeaconManager beaconManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean emulazioneBeacon = true;
        if(getIntent().getBooleanExtra("showinfo", false) || emulazioneBeacon ) {

            setContentView(R.layout.activity_main);
            setTitle("Hero");

            setEventListeners();
            scrollView = (ScrollView) findViewById(R.id.scroll_view);
            contentView = (ViewGroup) findViewById(R.id.scrolledLayout);
            fragContainer = new ViewGroup[4];

            fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0);
            fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1);
            fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2);
            fragContainer[3] = (ViewGroup) findViewById(R.id.fragContainer3);


            MyScrollPager scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, true, false);
            scrollView.setOnTouchListener(scrollPager);


            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.scrollTo(0, contentView.getPaddingTop());
                }
            });


            FragmentHelper.setMainActivity(this);

//            Intent intent = new Intent(this, BeaconService.class);
//
//            if (intent != null) {
//                this.startService(intent);
//            }






            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(audioStarted == false)
                    {
                        mPlayer = MediaPlayer.create(getBaseContext(), R.raw.hero_florence);
                        mPlayer.start();
                        audioStarted = true;
                    }
                    else{
                        mPlayer.stop();
                        mPlayer.reset();
                        audioStarted = false;
                    }
                }
            });



        }
        else
        {

            setContentView(R.layout.activity_main_noinfo);

        }



       // backgroundBeaconManager = new BackgroundBeaconManager(this);
        beaconManager = new ForegroundBeaconManager(this, this);




    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.start();
    }


    @Override
    protected void onStop() {
        super.onStop();
        beaconManager.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    private void setEventListeners() {
        FloatingActionButton miButton = (FloatingActionButton) findViewById(R.id.fab_MI);
        FloatingActionButton fiButton = (FloatingActionButton) findViewById(R.id.fab_FI);
        FloatingActionButton nyButton = (FloatingActionButton) findViewById(R.id.fab_NY);

        miButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMI();
            }
        });

        fiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFI();
            }
        });

        nyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNY();
            }
        });

    }

    private void onClickMI() {
        Intent intent = new Intent(this, MiActivity.class);
        startActivity(intent);
    }

    private void onClickFI() {
        Intent intent = new Intent(this, FiActivity.class);
        startActivity(intent);
    }

    private void onClickNY() {
        Intent intent = new Intent(this, MiActivity.class);
        startActivity(intent);
    }

    private void deactivateBeaconContents(){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, "Beacon Lontano", duration);
        toast.show();
    }
    private void activateBeaconContents(){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, "Beacon Vicino", duration);
        toast.show();
    }

    @Override
    public void onNewBeacons(List<Beacon> newInProximityBeaconList) {
        if(atLeastOneBeacon == false && beaconManager.getBeaconSize() > 0)
        {
            atLeastOneBeacon = true;
            activateBeaconContents();
        }
    }

    @Override
    public void onRemovedBeacons(List<Beacon> removedBeacons)
    {
        if(atLeastOneBeacon == true && beaconManager.getBeaconSize() == 0)
        {
            atLeastOneBeacon = false;
            deactivateBeaconContents();
        }
    }
}
