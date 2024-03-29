package micc.theguardiansapp;

import android.app.Activity;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.lang.ref.WeakReference;

import it.sephiroth.android.library.tooltip.TooltipManager;
import micc.theguardiansapp.audioPlayer.AudioPlayer;
import micc.theguardiansapp.audioPlayer.AudioPlayerListener;
import micc.theguardiansapp.dotsProgressBar.DotsProgressBar;
import micc.theguardiansapp.scrollPager.MyScrollPager;
import micc.theguardiansapp.scrollPager.ScrollPagerListener;


public class FiActivity extends Activity implements ScrollPagerListener {


    private static final int DRAWABLE_PLAY = R.drawable.sound_icon_small_3;
    private static final int DRAWABLE_STOP = R.drawable.stop;



    private AudioPlayer[] audioPlayer = new AudioPlayer[2];
    private ImageButton[] audioButton = new ImageButton[2];
    String audioTooltipText[] = new String[2];
    private final int nFragment = 3;

    boolean playing = false;


    TooltipManager tooltipManager;
    DotsProgressBar progressBar;


    SliderLayout slideShow1;
    SliderLayout slideShow2;
    SliderLayout slideShow3;

    TextSliderView tsv_slide1_1;
    TextSliderView tsv_slide1_2;
    TextSliderView tsv_slide1_3;

    WeakReference<MyTextSliderView> tsv_slide2_1;
    WeakReference<MyTextSliderView> tsv_slide2_2;
    WeakReference<MyTextSliderView> tsv_slide2_3;

    TextSliderView tsv_slide3_1;
    TextSliderView tsv_slide3_2;
    TextSliderView tsv_slide3_3;
    TextSliderView tsv_slide3_4;
    TextSliderView tsv_slide3_5;
    TextSliderView tsv_slide3_6;


    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    MyScrollPager scrollPager;

    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;



    private int audioTooltipX = 0;
    private int audioTooltipY = 0;
    private int maxTooltipWidthDp = 230;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fi_relative);
        setTitle("Florence");

        android.app.ActionBar actionBar = getActionBar();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            ViewGroup home = (ViewGroup) findViewById(android.R.id.home).getParent();
            ( (ImageView) home.getChildAt(0) ) .setImageResource(R.drawable.ic_action_back);

        }
        else
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);

            actionBar.setLogo(null); // forgot why this one but it helped
            View homeIcon = findViewById(android.R.id.home);
            ((View) homeIcon).setVisibility(View.GONE);
        }

        progressBar = (DotsProgressBar) findViewById(R.id.dotsProgressBarFI);
        //progressBar.setDotsCount(nFragment+1);
        progressBar.setVisibility(View.VISIBLE);


        tooltipManager = TooltipManager.getInstance(this);




        scrollView = (ScrollView) findViewById(R.id.scroll_view_fi);
        contentView = (ViewGroup) findViewById(R.id.scrolledLayout_fi);


        fragContainer = new ViewGroup[3];
        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0_fi);
        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1_fi);
        fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2_fi);



        scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, this, true, false);
        scrollView.setOnTouchListener(scrollPager);

        scrollPager.setDotsPageProgressBar(progressBar);


        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(0, contentView.getPaddingTop());
            }
        });



        audioInit();


        slideShow1 = (SliderLayout) findViewById(R.id.slider_accademia);
        slideShow2 = (SliderLayout) findViewById(R.id.slider_accademia_2);
        slideShow3 = (SliderLayout) findViewById(R.id.slider_hero_in_accademia);



        tsv_slide1_1 = new TextSliderView(this);
        tsv_slide1_2 = new TextSliderView(this);
        tsv_slide1_3 = new TextSliderView(this);

        tsv_slide2_1 =  new WeakReference<MyTextSliderView>(new MyTextSliderView(this));
        tsv_slide2_2 =  new WeakReference<MyTextSliderView>(new MyTextSliderView(this));
        tsv_slide2_3 =  new WeakReference<MyTextSliderView>(new MyTextSliderView(this));

        tsv_slide3_1 = new TextSliderView(this);
        tsv_slide3_2 = new TextSliderView(this);
        tsv_slide3_3 = new TextSliderView(this);
        tsv_slide3_4 = new TextSliderView(this);
        tsv_slide3_5 = new TextSliderView(this);
        tsv_slide3_6 = new TextSliderView(this);



        initSlideShow1();
        initSlideShow2();
        initSlideShow3();



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void audioInit() {

        audioButton[0] = (ImageButton) findViewById(R.id.audio_1_button);
        audioButton[1] = (ImageButton) findViewById(R.id.audio_2_button);

        audioPlayer[0] = new AudioPlayer(getBaseContext());
        audioPlayer[1] = new AudioPlayer(getBaseContext());
        audioPlayer[0].loadAudio(R.raw.innocenti_florence_en);
        audioPlayer[1].loadAudio(R.raw.tartufieri_florence_it);


        audioTooltipText[0] = "Curator: Matteo Innocenti";
        audioTooltipText[1] = "Accademia Gallery director: Angelo Tartuferi";



        for(int i = 0; i < 2; i ++ )
        {

            final int index = i;

            tooltipManager.hide(i);

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
                    scrollPager.gotoFragment((index + 1) % (nFragment));


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
        if(index >= 0 && index < nFragment-1) {
            audioPlayer[index].play();
            audioButton[index].setImageResource(DRAWABLE_STOP);

            tooltipManager.create(index)
                    //.anchor(audioButton[index], TooltipManager.Gravity.LEFT)
                    .anchor(new Point(audioTooltipX, audioTooltipY) , TooltipManager.Gravity.LEFT)
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
            case 0:
                cycleSlideShow1();
                break;
            case 1:
                cycleSlideShow2();
                break;
        }
    }
    private void audioCompleted(int index)
    {
        tooltipManager.hide(index);

        if( index >= 0 && index < nFragment - 1)
        {
            audioButton[index].setImageResource(DRAWABLE_PLAY);
        }
//        if(index == nFragment-1) {
//            playing = false;
//        }

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

        if( index >= 0 && index < nFragment)
        {
            if(audioPlayer[index].isPlaying())
                audioPlayer[index].stop();

            audioButton[index].setImageResource(DRAWABLE_PLAY);
        }


        stopCycleSlideShow1();
        stopCycleSlideShow2();

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
        slideShow1.removeAllSliders();
    }
    private void initSlideShow1(){
        tsv_slide1_1
                .description("Saracino's Hero")
                .image(R.drawable.s_f)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide1_2
                .description("Michelangelo's David")
                .image(R.drawable.d)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

//        tsv_slide1_3
//                .description("Saracino's Hero")
//                .image(R.drawable.s_f)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        slideShow1.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow1.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow1.setCustomAnimation(new DescriptionAnimation());

        //loadSlideShow1();
        //slideShow1.addSlider(tsv_slide1_3);

        slideShow1.stopAutoCycle();


    }
    private void loadSlideShow1() {
        slideShow1.addSlider(tsv_slide1_1);
        slideShow1.addSlider(tsv_slide1_2);
        slideShow1.setCurrentPosition(0);
    }
    private void cycleSlideShow1() {
        slideShow1.startAutoCycle(9500, 20000, false);
    }
    private void stopCycleSlideShow1() {
        slideShow1.stopAutoCycle();
    }


    private void unloadSlideShow2() {
       slideShow2.removeAllSliders();
    }
    private void initSlideShow2() {
        tsv_slide2_1.get()
                .description(getString(R.string.tartuferi_speech_1))
                .image(R.drawable.a)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide2_2.get()
                .description(getString(R.string.tartuferi_speech_2))
                .image(R.drawable.s_f)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide2_3.get()
                .description(getString(R.string.tartuferi_speech_3))
                .image(R.drawable.s_p)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        slideShow2.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow2.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow2.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());
        //slideShow2.setCustomAnimation(new DescriptionAnimation());


        slideShow2.stopAutoCycle();
    }
    private void loadSlideShow2() {
        slideShow2.addSlider(tsv_slide2_1.get());
        slideShow2.addSlider(tsv_slide2_2.get());
        slideShow2.addSlider(tsv_slide2_3.get());

    }
    private void cycleSlideShow2() {
        slideShow2.startAutoCycle(23000, 26000, true);
    }
    private void stopCycleSlideShow2() {
        slideShow2.stopAutoCycle();
    }



    private void unloadSlideShow3() {
        slideShow3.removeAllSliders();
    }
    private void initSlideShow3() {
        tsv_slide3_1
                .description("Genesis of Hero: Carrara's Marble Cave")
                .image(R.drawable.cava)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide3_2
                .description("Genesis of Hero")
                .image(R.drawable.blocco)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide3_3
                .description("Genesis of Hero")
                .image(R.drawable.blocco2)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_slide3_4
                .description("Genesis of Hero")
                .image(R.drawable.fresa)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide3_5
                .description("Genesis of Hero")
                .image(R.drawable.osso)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide3_6
                .description("Genesis of Hero")
                .image(R.drawable.osso2)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);


        slideShow3.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow3.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        slideShow3.setCustomAnimation(new DescriptionAnimation());

        //loadSlideShow3();


    }
    private void loadSlideShow3() {
        slideShow3.addSlider(tsv_slide3_1);
        slideShow3.addSlider(tsv_slide3_2);
//        slideShow3.addSlider(tsv_slide3_3);
        slideShow3.addSlider(tsv_slide3_4);
//        slideShow3.addSlider(tsv_slide3_5);
        slideShow3.addSlider(tsv_slide3_6);

    }
    private void cycleSlideShow3() {
        slideShow3.startAutoCycle(10000, 10000, true);
    }
    private void stopCycleSlideShow3() {
        slideShow3.stopAutoCycle();
    }


    @Override
    public void onFragmentChanged(int oldFragment, int newFragment) {



        if(playing)
        {
            if(oldFragment != 2)
                audioStop(oldFragment);

            if(newFragment != 2)
                audioPlay(newFragment);
        }

        switch(newFragment)
        {
            case 0:
               // loadSlideShow1();
               // unloadSlideShow3();
                break;
            case 1:
               // loadSlideShow2();
                break;
            case 2:
                playing = false;
               // loadSlideShow3();
               // unloadSlideShow1();

        }
        switch(oldFragment)
        {
//            case 0:
//                unloadSlideShow1();
//                break;
//            case 1:
//                unloadSlideShow2();
//                break;
            case 2:
                playing = false;
                //unloadSlideShow3();
        }

    }

    @Override
    public void onPageChanged(int oldPage, int newPage, int oldFragment, int newFragment) {

    }

    @Override
    public void onPagerGuiInit() {

        audioTooltipX = (int) audioButton[0].getX() + audioButton[0].getWidth()/ 2 - dpToPx(12);
        audioTooltipY = (int) (scrollView.getHeight() - audioButton[0].getHeight() +dpToPx(5));;

    }


    @Override
    protected void onStart() {
        super.onStart();
        loadSlideShow1();
        loadSlideShow2();
        loadSlideShow3();

        for (int i = 0; i < nFragment - 1; i++) {
            audioPlayer[i].onActivityStarted();
        }
    }


    @Override
    protected void onStop() {

        unloadSlideShow1();
        unloadSlideShow2();
        unloadSlideShow3();
        slideShow1.stopAutoCycle();
        slideShow2.stopAutoCycle();
        slideShow3.stopAutoCycle();
        super.onStop();

        for (int i = 0; i < nFragment - 1; i++) {
            audioPlayer[i].onActivityStopped();
        }
    }

}
