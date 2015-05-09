package micc.theguardiansapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import it.sephiroth.android.library.tooltip.TooltipManager;
import micc.theguardiansapp.audioPlayer.AudioPlayer;
import micc.theguardiansapp.audioPlayer.AudioPlayerListener;
import micc.theguardiansapp.beaconHelper.*;
import micc.theguardiansapp.dotsProgressBar.DotsProgressBar;
import micc.theguardiansapp.scrollPager.MyScrollPager;
import micc.theguardiansapp.scrollPager.ScrollPagerListener;


import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.estimote.sdk.Beacon;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.List;


public class MainActivity
        extends Activity
        implements MyBeaconListener, ScrollPagerListener
{

    private static final boolean DEV_MODE = false;
    private static final boolean SIMULATE_BEACON = true;
    private boolean beaconized = false;

    private final String qrCodeHeroString = "http://goo.gl/dqEN3V";
    private final static int DP_BEACON_TOOLTIP = 10;
    private final static int DP_MAX_WIDTH_BEACON_TOOLTIP = 160;

    private static final int DRAWABLE_PLAY = R.drawable.sound_icon_small_3;
    private static final int DRAWABLE_STOP = R.drawable.stop;


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


    MyFragment frag1;

    private ImageButton btnFi;
    private ImageButton btnMi;
    private ImageButton btnNy;

    private final int nFragment = 5;

    private AudioPlayer[] audioPlayer = new AudioPlayer[nFragment];
    private ImageButton[] audioButton = new ImageButton[nFragment];
    String audioTooltipText[] = new String[nFragment];


    private int audioTooltipX = 0;
    private int audioTooltipY = 0;
    private int maxTooltipWidthDp = 250;

    boolean playing = false;


    TooltipManager tooltipManager;
    RelativeLayout tooltipContainerLayout;
    int tooltip_x;
    int tooltip_y;
    private TextView textViewTip;

    DotsProgressBar progressBar;


    ImageView statueImageView;

    SliderLayout slideShow1;
    SliderLayout slideShow2;
    SliderLayout slideShow3;
    MySmallTextSliderView tsv_slide1_1;
    MyTextSliderView tsv_slide1_2;
    MyTextSliderView tsv_slide2_1;
    MyTextSliderView tsv_slide2_2;
    MyTextSliderView tsv_slide3_1;
    MyTextSliderView tsv_slide3_2;


    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    private static String SETTING_BLUETOOTH_4_INFO = "BLUETOOTH_4_PRESENT";
    private static String SETTING_BLUETOOTH_4_CHECKED = "BLUETOOTH_4_CHECKED";

    private static String SETTING_BEACON_ACQUIRED = "BEACON_ACQUIRED";
    private static String SETTINGS = "SETTINGS";


    private void saveBeaconAcquired(boolean beacon_acquired) {
        SharedPreferences.Editor editor = getSharedPreferences(SETTINGS, MODE_PRIVATE).edit();
        editor.putBoolean(SETTING_BEACON_ACQUIRED, beacon_acquired);
        editor.commit();
    }
    private boolean loadIsBeaconAcquired() {
        SharedPreferences prefs = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        boolean beacon_acquired = prefs.getBoolean(SETTING_BEACON_ACQUIRED, false);
        return beacon_acquired;
    }

    private void clearBluetoothInfo() {
        SharedPreferences.Editor editor = getSharedPreferences(SETTINGS, MODE_PRIVATE).edit();
        editor.putBoolean(SETTING_BLUETOOTH_4_CHECKED, false);
        editor.putBoolean(SETTING_BLUETOOTH_4_INFO, false);
        editor.commit();
    }
    private void saveBluetoothInfo(boolean hasBluetooth4) {
        SharedPreferences.Editor editor = getSharedPreferences(SETTINGS, MODE_PRIVATE).edit();
        editor.putBoolean(SETTING_BLUETOOTH_4_CHECKED, true);
        editor.putBoolean(SETTING_BLUETOOTH_4_INFO, hasBluetooth4);
        editor.commit();
    }
    private Boolean loadBluetoothInfo() {
        SharedPreferences prefs = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        boolean bluetooth_4_checked = prefs.getBoolean(SETTING_BLUETOOTH_4_CHECKED, false);

        if(!bluetooth_4_checked)
            return null;

        boolean bt4 = prefs.getBoolean(SETTING_BLUETOOTH_4_INFO, false);
        return bt4;
    }



    Boolean hasBluetooth4 = false;












    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main_relative);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            android.app.ActionBar actionBar = getActionBar();
            setTitle("  Hero");
        }
        else setTitle("Hero");


        android.app.ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        ((View)findViewById(android.R.id.home).getParent()).setVisibility(View.GONE);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayUseLogoEnabled(true);


        beaconManager = new ForegroundBeaconManager(this, this);
        hasBluetooth4 = loadBluetoothInfo();

        if(hasBluetooth4 == null)
        {
            hasBluetooth4 = beaconManager.hasBluetooth();
            saveBluetoothInfo(hasBluetooth4);
        }

        beaconized = loadIsBeaconAcquired();

        textViewTip = (TextView) findViewById(R.id.textViewTip);


        btnFi = (ImageButton) findViewById(R.id.fab_FI);
        btnMi = (ImageButton) findViewById(R.id.fab_MI);
        btnNy = (ImageButton) findViewById(R.id.fab_NY);
        btnFi.setEnabled(false);
        btnMi.setEnabled(false);
        btnNy.setEnabled(false);


        progressBar = (DotsProgressBar) findViewById(R.id.dotsProgressBar);
        progressBar.setDotsCount(4);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setActiveDot(2);


        tooltipManager = TooltipManager.getInstance(this);




        setEventListeners();
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        contentView = (ViewGroup) findViewById(R.id.scrolledLayout);
        fragContainer = new ViewGroup[nFragment];

        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0);
        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1);
        fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2);
        fragContainer[3] = (ViewGroup) findViewById(R.id.fragContainer3);
        fragContainer[4] = (ViewGroup) findViewById(R.id.fragContainer4);


        scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, this, true, false);
        scrollView.setOnTouchListener(scrollPager);
         scrollPager.setDotsPageProgressBar(progressBar);

        statueImageView = (ImageView) findViewById(R.id.statueImageView);



        FragmentHelper.setMainActivity(this);
        frag1 = new MyFragment();

        scrollView.post(new Runnable() {
            public void run() {

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)statueImageView.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.fragContainer0);
                statueImageView.setLayoutParams(params);

                if(beaconized || SIMULATE_BEACON)
                    activateBeaconContents();
                else deactivateBeaconContents();

                if(SIMULATE_BEACON) activateBeaconContents();



            }
        });



            audioInit();

            slideShow1 = (SliderLayout) findViewById(R.id.activity_main_imageSlider_1);
            slideShow2 = (SliderLayout) findViewById(R.id.activity_main_imageSlider_2);
            slideShow3 = (SliderLayout) findViewById(R.id.activity_main_imageSlider_3);


            tsv_slide1_1 = new MySmallTextSliderView(this);
            tsv_slide1_2 = new MyTextSliderView(this);
            tsv_slide2_1 = new MyTextSliderView(this);
            tsv_slide2_2 = new MyTextSliderView(this);
            tsv_slide3_1 = new MyTextSliderView(this);
            tsv_slide3_2 = new MyTextSliderView(this);

            initSlideShow1();
            initSlideShow2();
            initSlideShow3();


    }



    @Override
    public void onPagerGuiInit() {
     //   FragmentHelper.swapFragment(R.id.fragContainer1,  frag1);

        RelativeLayout footer = (RelativeLayout) findViewById(R.id.footer_frag0);
        audioTooltipX = (int) audioButton[0].getX() + dpToPx(16 - 6);
        audioTooltipY = (int) (scrollView.getHeight() - footer.getHeight()/2  );;
    }

    private void audioInit() {

        audioButton[0] = (ImageButton) findViewById(R.id.activity_main_audioButton0);
        audioButton[1] = (ImageButton) findViewById(R.id.activity_main_audioButton1);
        audioButton[2] = (ImageButton) findViewById(R.id.activity_main_audioButton2);
        audioButton[3] = (ImageButton) findViewById(R.id.activity_main_audioButton3);
        audioButton[4] = null;

        audioPlayer[0] = null;
        audioPlayer[1] = new AudioPlayer(getBaseContext());
        audioPlayer[2] = new AudioPlayer(getBaseContext());
        audioPlayer[3] = new AudioPlayer(getBaseContext());
       audioPlayer[4]= null;

        audioPlayer[1].loadAudio(R.raw.saracino_intro_1);
        audioPlayer[2].loadAudio(R.raw.saracino_intro_2);
        audioPlayer[3].loadAudio(R.raw.saracino_intro_3);

        audioTooltipText[0] = "The author: Antonio Pio Saracino";
        audioTooltipText[1] = "The author: Antonio Pio Saracino";
        audioTooltipText[2] = "The author: Antonio Pio Saracino";
        audioTooltipText[3] = "The author: Antonio Pio Saracino";


        audioButton[0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                scrollPager.gotoFragment(1);
                audioButton[1].performClick();
            }
        });

        for(int i = 1; i < 4; i ++ )
        {

            final int index = i;

            tooltipManager.hide(i);

            if(audioButton[i] != null)
            audioButton[i].setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    playing = !playing;
                    if(playing) {
                        audioPlay(index);
                    }
                    else audioStop(index);
                    //audioToggle(index);

                }
            });

            audioPlayer[index].setAudioPlayerListener(new AudioPlayerListener() {
                @Override
                public void onCompletion(boolean inEarpieceMode) {
// non funziona! il gestore di eventi non viene richiamato fino a che non si riattiva lo schermo
//                    if(inEarpieceMode)
//                    {
//                        audioButton[index].setImageResource(DRAWABLE_PLAY);
//                        audioButton[(index + 1) % nFragment].setImageResource(DRAWABLE_STOP);
//                        audioPlayer[(index + 1) % nFragment].play();
//                    }
                    audioCompleted(index);
                    scrollPager.gotoFragment((index + 1) % nFragment);


                }
                @Override
                public void onPaused() { }
                @Override
                public void onStopped() {
                    audioStop(index);
                }
            });

        }

    }

    private void audioToggle(int index)
    {

        if(audioPlayer[index].isPlaying())
            audioStop(index);
        else audioPlay(index);
    }
    private void audioPlay(int index)
    {
        if(index > 0 && index < nFragment && audioPlayer[index] != null) {

            audioPlayer[index].play();
            audioButton[index].setImageResource(DRAWABLE_STOP);

            tooltipManager.create(index)
                    //.anchor(audioButton[index], TooltipManager.Gravity.LEFT)
                    .anchor(new Point(audioTooltipX, audioTooltipY), TooltipManager.Gravity.LEFT)
                            //.anchor(scrollView, TooltipManager.Gravity.CENTER)
                    .actionBarSize(Utils.getActionBarSize(getBaseContext()))
                    .closePolicy(TooltipManager.ClosePolicy.None, -1)
                    .text(audioTooltipText[index])
                    .toggleArrow(true)
                    .withCustomView(R.layout.custom_textview, false)
                    .maxWidth(dpToPx(maxTooltipWidthDp))
                    .showDelay(500)
                    .show();
        }

        switch(index)
        {
            case 1:
                cycleSlideShow1();
                break;
            case 2:
                cycleSlideShow2();
                break;
            case 3:
                cycleSlideShow3();
        }
    }
    private void audioCompleted(int index)
    {
        tooltipManager.hide(index);

        if( index != 0 && index <= nFragment)
        {
            audioButton[index].setImageResource(DRAWABLE_PLAY);

        }
        stopCycleSlideShow1();
        stopCycleSlideShow2();
        stopCycleSlideShow3();
//        switch(index)
//        {
//            case 1:
//                stopCycleSlideShow1();
//                break;
//            case 2:
//                stopCycleSlideShow2();
//                break;
//            case 3:
//                stopCycleSlideShow3();
//        }

    }
    private void audioStop(int index)
    {
        tooltipManager.hide(index);

        if( index != 0 && index <= nFragment)
        {
            if(audioPlayer[index].isPlaying())
                audioPlayer[index].stop();

            audioButton[index].setImageResource(DRAWABLE_PLAY);
        }


        stopCycleSlideShow1();
        stopCycleSlideShow2();
        stopCycleSlideShow3();

//        switch(index)
//        {
//            case 1:
//                stopCycleSlideShow1();
//                break;
//            case 2:
//                stopCycleSlideShow2();
//                break;
//            case 3:
//                stopCycleSlideShow3();
//        }

    }






    private void unloadSlideShow1() {
//        slideShow1.removeAllSliders();
//        slideShow1.addSlider(tsv_slide1_1);
    }
    private void initSlideShow1(){
        tsv_slide1_1
                .description("The author: Antonio Pio Saracino")
                .image(R.drawable.saracino)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide1_2
                .description(getString(R.string.saracino_intro_1))
                .image(R.drawable.bryant_park)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        //slideShow1.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow1.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow1.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());


        slideShow1.addSlider(tsv_slide1_1);
        slideShow1.addSlider(tsv_slide1_2);
        slideShow1.stopAutoCycle();
        slideShow1.setCurrentPosition(0);


    }
    private void loadSlideShow1() {
//        slideShow1.addSlider(tsv_slide1_2);
//        unloadSlideShow2();
    }
    private void cycleSlideShow1() {
        slideShow1.setCurrentPosition(0);
        slideShow1.startAutoCycle(15000, 17000, false);
    }
    private void stopCycleSlideShow1() {
        slideShow1.stopAutoCycle();
    }


    private void unloadSlideShow2() {
//        slideShow1.removeSliderAt(1);
    }
    private void initSlideShow2() {
        tsv_slide2_1
                .description(getString(R.string.saracino_intro_2))
                .image(R.drawable.hero)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide2_2
                .description(getString(R.string.saracino_intro_2))
                .image(R.drawable.hero_cape)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        //slideShow2.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow2.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow2.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());

        slideShow2.addSlider(tsv_slide2_1);
        slideShow2.addSlider(tsv_slide2_2);
        slideShow2.stopAutoCycle();
        slideShow2.setCurrentPosition(0);



    }
    private void loadSlideShow2() {
//        slideShow2.addSlider(tsv_slide2_2);
//        unloadSlideShow1();
    }
    private void cycleSlideShow2() {
        slideShow2.setCurrentPosition(0);
        slideShow2.startAutoCycle(12000, 12000, true);
    }
    private void stopCycleSlideShow2() {
        slideShow2.stopAutoCycle();
    }

    private void initSlideShow3(){
        tsv_slide3_1
                .description(getString(R.string.saracino_intro_3))
                .image(R.drawable.accademia)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide3_2
                .description(getString(R.string.saracino_intro_4))
                .image(R.drawable.a)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        //slideShow3.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow3.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow3.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());

        slideShow3.addSlider(tsv_slide3_1);
        slideShow3.addSlider(tsv_slide3_2);
        slideShow2.stopAutoCycle();

    }
    private void loadSlideShow3() {
//        unloadSlideShow1();
//        unloadSlideShow2();
    }
    private void cycleSlideShow3() {
        //slideShow1.setCurrentPosition(0);
        slideShow3.stopAutoCycle();
    }
    private void stopCycleSlideShow3() {
    slideShow3.stopAutoCycle();
    }




    @Override
    public void onFragmentChanged(int oldFragment, int newFragment) {


        if(playing)
        {
            if(oldFragment != 0)
                audioStop(oldFragment);

            if(newFragment != 0)
                audioPlay(newFragment);
        }

        tooltipManager.remove(999);



        switch(newFragment)
        {
            case 0:
                playing = false;
                showBeaconTooltip(beaconized);

            case 1:
                loadSlideShow1();
                break;
            case 2:
                loadSlideShow2();
                break;
            case 3:
                loadSlideShow3();
                break;
        }

    }

    @Override
    public void onPageChanged(int oldPage, int newPage, int oldFragment, int newFragment) {

    }



    @Override
    protected void onStart() {
        super.onStart();

        if(hasBluetooth4)
        {
            if(!beaconManager.isBluetoothEnabled() )
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            beaconManager.start();
        }
        for (int i = 1; i < 4; i++) {
            audioPlayer[i].onActivityStarted();
        }
    }


    @Override
    protected void onStop() {
        slideShow1.stopAutoCycle();
        slideShow2.stopAutoCycle();
        slideShow3.stopAutoCycle();
        super.onStop();
        if(hasBluetooth4)
            beaconManager.stop();



        for(int i = 1; i < 4; i++) {
            audioPlayer[i].onActivityStopped();
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
         String scanFormat, scanContent; //variabili per i risultati della scan

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null)
        {
            scanContent = scanningResult.getContents();
            scanFormat = scanningResult.getFormatName();

            if(scanContent != null &&  scanContent.compareTo(qrCodeHeroString) == 0)
            {
                if(!beaconized)
                {
                    saveBeaconAcquired(true);
                    activateBeaconContents();
                }
            }
            // textViewFormat.setText("FORMAT: " + scanFormat);
            // textViewContent.setText("CONTENT: " + scanContent);
        }
//        else{
//            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
//            toast.show();
//        }
    }


    private void zxingQrScan() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
        scanIntegrator.initiateScan();
    }




    private void setEventListeners() {
        ImageButton miButton = (ImageButton) findViewById(R.id.fab_MI);
        ImageButton fiButton = (ImageButton) findViewById(R.id.fab_FI);
        ImageButton nyButton = (ImageButton) findViewById(R.id.fab_NY);

        miButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        //  v.setBackground();


                }
                return false;
            }
        });
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


    private void showBeaconTooltip(boolean beaconConnected)
    {
        tooltipManager.remove(999);
        String notNearString = "Activate bluetooth, get closer to the Hero and enjoy the additional content";
        String nearString = "You are approaching the Hero! Enjoy additional app contents";
        String activeString = null;

        int activeLayoutID = R.layout.custom_textview_dark;
        int notActiveLayoutID = R.layout.custom_textview;
        String color;

        if(!hasBluetooth4)
        {
            notNearString = "Scan Hero QR code and enjoy the additional content";
            nearString = "You scanned Hero QR code! Enjoy additional app contents";
        }

        if(beaconConnected) {
            activeString = nearString;
            color = "#90000000";
        }
        else {
            activeString = notNearString;
            color = "#60000000";
        }


        textViewTip.setText(activeString);
        textViewTip.setBackgroundColor(Color.parseColor(color));
//
//        tooltipManager.create(999)
//                //.anchor(new Point((int)fragContainer[0].getWidth()/2- dpToPx(4), dpToPx(DP_BEACON_TOOLTIP) ), TooltipManager.Gravity.BOTTOM)
//                .anchor(new Point(tooltip_x, tooltip_y), TooltipManager.Gravity.CENTER)
//                        //.anchor(scrollView, TooltipManager.Gravity.CENTER)
//                .actionBarSize(Utils.getActionBarSize(getBaseContext()))
//                .closePolicy(TooltipManager.ClosePolicy.None, -1)
//                .text(activeString)
//                .toggleArrow(false)
//                .withCustomView(activeLayoutID, true)
//                .maxWidth(dpToPx(DP_MAX_WIDTH_BEACON_TOOLTIP))
//                .showDelay(300)
//                .show();

    }
    private void deactivateBeaconContents(){

        beaconized = false;

        showBeaconTooltip(false);

        if(!SIMULATE_BEACON) {
            btnFi.setEnabled(false);
            btnMi.setEnabled(false);
            btnNy.setEnabled(false);
            btnFi.setImageResource(R.drawable.layout_city_button_fi);
            btnMi.setImageResource(R.drawable.layout_city_button_mi);
            btnNy.setImageResource(R.drawable.layout_city_button_ny);
        }
    }
    private void activateBeaconContents(){
        beaconized = true;

        showBeaconTooltip(true);

        btnFi.setEnabled(true);
        btnMi.setEnabled(true);
        btnNy.setEnabled(true);
        btnFi.setImageResource(R.drawable.layout_city_button_fi_enabled);
        btnMi.setImageResource(R.drawable.layout_city_button_mi_enabled);
        btnNy.setImageResource(R.drawable.layout_city_button_ny_enabled);

    }

    @Override
    public void onNewBeacons(List<Beacon> newInProximityBeaconList) {
        if(atLeastOneBeacon == false)
        {

            if(!beaconized)
            {
                saveBeaconAcquired(true);
                activateBeaconContents();
            }
            atLeastOneBeacon = true;
        }
    }

    @Override
    public void onRemovedBeacons(List<Beacon> removedBeacons)
    {
        if(atLeastOneBeacon == true && beaconManager.getBeaconSize() == 0)
        {
            atLeastOneBeacon = false;
            //deactivateBeaconContents();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (hasBluetooth4) {
            if (DEV_MODE)
                getMenuInflater().inflate(R.menu.menu_main_dev, menu);
        }
        else
        {
            if(DEV_MODE)
                getMenuInflater().inflate(R.menu.menu_main_nobt_dev, menu);
            else getMenuInflater().inflate(R.menu.menu_main_nobt, menu);

        }

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
            this.saveBeaconAcquired(false);

            clearBluetoothInfo();
            deactivateBeaconContents();
            beaconManager.clear();
            atLeastOneBeacon = false;
            return true;
        }
        else if(id == R.id.action_qrscan)
        {
             zxingQrScan();
        }

        return super.onOptionsItemSelected(item);
    }

}
