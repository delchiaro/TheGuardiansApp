package micc.theguardiansapp;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import it.sephiroth.android.library.tooltip.TooltipManager;
import micc.theguardiansapp.audioPlayer.AudioPlayer;
import micc.theguardiansapp.beaconHelper.*;
import micc.theguardiansapp.scrollPager.MyScrollPager;
import micc.theguardiansapp.scrollPager.ScrollPagerListener;

import com.estimote.sdk.Beacon;
import com.getbase.floatingactionbutton.FloatingActionButton;


import java.util.List;


public class MainActivity
        extends ActionBarActivity
        implements MyBeaconListener, ScrollPagerListener
{



    private static final int REFRESH_BEACON_DELAY = 5000;



    private static SensorManager sensorManager;
    private static Sensor proximitySensor;



    MyScrollPager scrollPager;
    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;

    private boolean atLeastOneBeacon = false;

    private static final int REQUEST_ENABLE_BT = 1234;


    private BackgroundBeaconManager backgroundBeaconManager;

    private ForegroundBeaconManager beaconManager;


    AudioPlayer ap;

    FloatingActionButton fab;

    TooltipManager tooltipManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        boolean emulazioneBeacon = true;
        if(getIntent().getBooleanExtra("showinfo", false) || emulazioneBeacon ) {

            setContentView(R.layout.activity_main);
            setTitle("Hero");


            tooltipManager = TooltipManager.getInstance(this);


            ap = new AudioPlayer(getBaseContext());
            ap.loadAudio(R.raw.hero_florence);

            setEventListeners();
            scrollView = (ScrollView) findViewById(R.id.scroll_view);
            contentView = (ViewGroup) findViewById(R.id.scrolledLayout);
            fragContainer = new ViewGroup[4];

            fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0);
            fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1);
            fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2);
            fragContainer[3] = (ViewGroup) findViewById(R.id.fragContainer3);


            scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, true, false);
            scrollPager.setOnScrollListener(this);
            scrollView.setOnTouchListener(scrollPager);


            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.scrollTo(0, contentView.getPaddingTop());
                }
            });


            //FragmentHelper.setMainActivity(this);

//            Intent intent = new Intent(this, BeaconService.class);
//
//            if (intent != null) {
//                this.startService(intent);
//            }




            fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    scrollPager.gotoPage(2);
                    if(ap.isPlaying() == false)
                    {
//
//                         AudioManager m_amAudioManager;
//                        m_amAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                        m_amAudioManager.setMode(AudioManager.MODE_IN_CALL);
//                        m_amAudioManager.setSpeakerphoneOn(false);
//
                        ap.play();
                        //ap.switchToEarpieces();

                        tooltipManager.create(0)
                                .anchor(fab, TooltipManager.Gravity.LEFT)
                                .actionBarSize(Utils.getActionBarSize(getBaseContext()))
                                .closePolicy(TooltipManager.ClosePolicy.None, -1)
                                .text(R.string.hello_world)
                                .toggleArrow(true)
                                .maxWidth(400)
                                .showDelay(300)
                                //.withCallback(this)
                                .show();
                    }
                    else{
                        ap.stop();
                        tooltipManager.hide(0);
                    }
                }
            });


//
//            final ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);
//
//            final ToolTip toolTip = new ToolTip()
//                    .withText("A beautiful View")
//                    .withColor(R.color.outside_color_gray)
//                    .withShadow()
//                    .withAnimationType(ToolTip.AnimationType.FROM_TOP)
//                    .withClickRemove(false);
//
//            ToolTipView myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.floating_action_button));

            //toolTipRelativeLayout.showToolTipForView(toolTip, fab);


            TooltipManager.getInstance(this)
                    .create(100)
                    .anchor(new Point(500, 500), TooltipManager.Gravity.BOTTOM)
                    .closePolicy(TooltipManager.ClosePolicy.TouchOutside, 3000)
                    .activateDelay(800)
                    .text("Something to display in the tooltip...")
                    .maxWidth(500)
                    .show();

        }
        else
        {

            setContentView(R.layout.activity_main_noinfo);

        }



       // backgroundBeaconManager = new BackgroundBeaconManager(this);
        beaconManager = new ForegroundBeaconManager(this, this);





    }



    @Override
    public void onFragmentChanged(int oldFragment, int newFragment) {

    }




    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.start();
        ap.onActivityStarted();
    }


    @Override
    protected void onStop() {
        super.onStop();
        beaconManager.stop();
        ap.onActivityStopped();
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
