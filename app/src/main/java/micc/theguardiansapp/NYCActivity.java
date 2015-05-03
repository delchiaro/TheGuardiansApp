package micc.theguardiansapp;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.daimajia.slider.library.Animations.*;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import it.sephiroth.android.library.tooltip.TooltipManager;
import micc.theguardiansapp.audioPlayer.AudioPlayer;
import micc.theguardiansapp.audioPlayer.AudioPlayerListener;
import micc.theguardiansapp.dotsProgressBar.DotsProgressBar;
import micc.theguardiansapp.scrollPager.MyScrollPager;
import micc.theguardiansapp.scrollPager.ScrollPagerListener;


public class NYCActivity extends ActionBarActivity implements ScrollPagerListener {


    private static final int DRAWABLE_PLAY = R.drawable.play;
    private static final int DRAWABLE_STOP = R.drawable.stop;



    private AudioPlayer[] audioPlayer = new AudioPlayer[2];
    private ImageButton[] audioButton = new ImageButton[2];
    String audioTooltipText[] = new String[2];
    private final int nFragment = 2;

    boolean playing = false;


    TooltipManager tooltipManager;
    DotsProgressBar progressBar;


    SliderLayout slideShow1;
    SliderLayout slideShow2;

    MyTextSliderView tsv_slide1_1;
    MyTextSliderView tsv_slide1_2;
    MyTextSliderView tsv_slide2_1;
    MyTextSliderView tsv_slide2_2;

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    MyScrollPager scrollPager;

    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;
    ImageView audioButton1;
    ImageView audioButton2;

    boolean audio1playing = false;
    boolean audio2playing = false;

    MediaPlayer mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nyc);
        setTitle("New York City");



        progressBar = (DotsProgressBar) findViewById(R.id.dotsProgressBarNY);
        progressBar.setDotsCount(nFragment);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setActiveDot(2);


        tooltipManager = TooltipManager.getInstance(this);




        scrollView = (ScrollView) findViewById(R.id.scroll_view_nyc);
        contentView = (ViewGroup) findViewById(R.id.scrolledLayout_nyc);

        fragContainer = new ViewGroup[2];
        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0_nyc);
        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1_nyc);


        scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, true, false);
        scrollPager.setOnScrollListener(this);
        scrollView.setOnTouchListener(scrollPager);

        scrollPager.setDotsPageProgressBar(progressBar);


        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(0, contentView.getPaddingTop());
            }
        });



        audioInit();


        slideShow1 = (SliderLayout) findViewById(R.id.slider_nyc);
        slideShow2 = (SliderLayout) findViewById(R.id.slider_nyc_2);



        tsv_slide1_1 = new MyTextSliderView(this);
        tsv_slide1_2 = new MyTextSliderView(this);
        tsv_slide2_1 = new MyTextSliderView(this);
        tsv_slide2_2 = new MyTextSliderView(this);

        initSlideShow1();
        initSlideShow2();


    }


    private void audioInit() {

        audioButton[0] = (ImageButton) findViewById(R.id.audio_1_button);
        audioButton[1] = (ImageButton) findViewById(R.id.audio_2_button);

        audioPlayer[0] = new AudioPlayer(getBaseContext());
        audioPlayer[1] = new AudioPlayer(getBaseContext());
        audioPlayer[0].loadAudio(R.raw.saracino_ny_1);
        audioPlayer[1].loadAudio(R.raw.saracino_ny_2);


        audioTooltipText[0] = "The astist: Saracino";
        audioTooltipText[1] = "The astist: Saracino";



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
        if(index >= 0 && index < nFragment) {
            audioPlayer[index].play();
            audioButton[index].setImageResource(DRAWABLE_STOP);

            tooltipManager.create(index)
                    .anchor(new Point((int)scrollView.getWidth()/2, (int)scrollView.getHeight() - dpToPx(25) ), TooltipManager.Gravity.TOP)
                            //.anchor(scrollView, TooltipManager.Gravity.CENTER)
                    .actionBarSize(Utils.getActionBarSize(getBaseContext()))
                    .closePolicy(TooltipManager.ClosePolicy.None, -1)
                    .text(audioTooltipText[index])
                    .toggleArrow(false)
                    .withCustomView(R.layout.custom_textview, false)
                    .maxWidth(400)
                    .showDelay(300)
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

        if( index >= 0 && index < nFragment)
        {
            audioButton[index].setImageResource(DRAWABLE_PLAY);
        }
        if(index == nFragment-1) {
            playing = false;
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
//        slideShow1.removeAllSliders();
//        slideShow1.addSlider(tsv_slide1_1);
    }
    private void initSlideShow1(){
        tsv_slide1_1
                .description(getString(R.string.saracino_speech_nyc_1))
                .image(R.drawable.hero_garden)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide1_2
                .description(getString(R.string.saracino_speech_nyc_2))
                .image(R.drawable.superhero_garden)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        slideShow1.setPresetTransformer(SliderLayout.Transformer.DepthPage);
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
        slideShow1.startAutoCycle(26000, 11000, false);
    }
    private void stopCycleSlideShow1() {
        slideShow1.stopAutoCycle();
    }


    private void unloadSlideShow2() {
//        slideShow1.removeSliderAt(1);
    }
    private void initSlideShow2() {
        tsv_slide2_1
                .description(getString(R.string.saracino_speech_nyc_3))
                .image(R.drawable.hero_1)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_slide2_2
                .description(getString(R.string.saracino_speech_nyc_4))
                .image(R.drawable.hero_2)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        slideShow2.setPresetTransformer(SliderLayout.Transformer.DepthPage);
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
        slideShow2.startAutoCycle(15000, 22000, true);
    }
    private void stopCycleSlideShow2() {
        slideShow2.stopAutoCycle();
    }


    @Override
    public void onFragmentChanged(int oldFragment, int newFragment) {


        if(playing)
        {
             audioStop(oldFragment);
             audioPlay(newFragment);
        }

        switch(newFragment)
        {
            case 0:
                loadSlideShow1();
                break;
            case 1:
                loadSlideShow2();
                break;

        }

    }

    @Override
    public void onPageChanged(int oldPage, int newPage, int oldFragment, int newFragment) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        for (int i = 0; i < nFragment; i++) {
            audioPlayer[i].onActivityStarted();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        for(int i = 0; i < nFragment; i++) {
            audioPlayer[i].onActivityStopped();
        }

    }
}
