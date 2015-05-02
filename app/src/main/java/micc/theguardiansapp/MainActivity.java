package micc.theguardiansapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import micc.theguardiansapp.beaconHelper.*;
//import micc.theguardiansapp.beaconHelper.GoodBadBeaconProximityManager;
import micc.theguardiansapp.beaconServiceHelper.*;
import micc.theguardiansapp.beaconServiceHelper.BeaconBestProximityListener;

import com.estimote.sdk.Beacon;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.IOException;


public class MainActivity extends ActionBarActivity implements BeaconBestProximityListener {



    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;
    private MediaPlayer mPlayer;
    private boolean audioStarted = false;



    private GoodBadBeaconProximityManager proximityManager;



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

            Intent intent = new Intent(this, BeaconService.class);

            if (intent != null) {
                this.startService(intent);
            }






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




        proximityManager = new micc.theguardiansapp.beaconServiceHelper.GoodBadBeaconProximityManager(this, this);
        proximityManager.scan();

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
        Intent intent = new Intent(this, NYCActivity.class);
        startActivity(intent);
    }








    @Override
    public void OnNewBeaconBestProximity(Beacon bestProximity, Beacon oldBestProximity) {

    }

    @Override
    public void OnNoneBeaconBestProximity(Beacon oldBestProximity) {

    }
}
