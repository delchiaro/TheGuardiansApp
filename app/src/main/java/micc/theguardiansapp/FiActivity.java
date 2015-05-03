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

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import it.sephiroth.android.library.tooltip.TooltipManager;
import micc.theguardiansapp.audioPlayer.AudioPlayer;
import micc.theguardiansapp.audioPlayer.AudioPlayerListener;
import micc.theguardiansapp.dotsProgressBar.DotsProgressBar;
import micc.theguardiansapp.scrollPager.MyScrollPager;
import micc.theguardiansapp.scrollPager.ScrollPagerListener;


public class FiActivity extends ActionBarActivity implements ScrollPagerListener {


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
    SliderLayout slideShow3;

    TextSliderView tsv_slide1_1;
    TextSliderView tsv_slide1_2;
    TextSliderView tsv_slide1_3;

    TextSliderView tsv_slide2_1;
    TextSliderView tsv_slide2_2;
    TextSliderView tsv_slide2_3;

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
    ImageView audioButton1;
    ImageView audioButton2;

    boolean audio1playing = false;
    boolean audio2playing = false;

    MediaPlayer mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fi);
        setTitle("Florence");



        progressBar = (DotsProgressBar) findViewById(R.id.dotsProgressBarFI);
        progressBar.setDotsCount(nFragment+1);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setActiveDot(2);


        tooltipManager = TooltipManager.getInstance(this);




        scrollView = (ScrollView) findViewById(R.id.scroll_view_fi);
        contentView = (ViewGroup) findViewById(R.id.scrolledLayout_fi);


        fragContainer = new ViewGroup[3];
        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0_fi);
        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1_fi);
        fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2_fi);



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


        slideShow1 = (SliderLayout) findViewById(R.id.slider_accademia);
        slideShow2 = (SliderLayout) findViewById(R.id.slider_accademia_2);
        slideShow3 = (SliderLayout) findViewById(R.id.slider_hero_in_accademia);



        tsv_slide1_1 = new TextSliderView(this);
        tsv_slide1_2 = new TextSliderView(this);
        tsv_slide1_3 = new TextSliderView(this);

        tsv_slide2_1 = new TextSliderView(this);
        tsv_slide2_2 = new TextSliderView(this);
        tsv_slide2_3 = new TextSliderView(this);

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


    private void audioInit() {

        audioButton[0] = (ImageButton) findViewById(R.id.audio_1_button);
        audioButton[1] = (ImageButton) findViewById(R.id.audio_2_button);

        audioPlayer[0] = new AudioPlayer(getBaseContext());
        audioPlayer[1] = new AudioPlayer(getBaseContext());
        audioPlayer[0].loadAudio(R.raw.innocenti_florence_en);
        audioPlayer[1].loadAudio(R.raw.tartufieri_florence_it);


        audioTooltipText[0] = "Innocenti";
        audioTooltipText[1] = "Tartuferi";



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
                    scrollPager.gotoFragment((index + 1) % (nFragment+1));


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

<<<<<<< HEAD

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
=======
        tsv_accademia
                .description(getString(R.string.saracino_speech_fi_1))
                .image(R.drawable.s_f)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_accademia1
                .description(getString(R.string.saracino_speech_fi_2))
                .image(R.drawable.d)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_accademia2
                .description(getString(R.string.saracino_speech_fi_3))
                .image(R.drawable.s_f)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
>>>>>>> franco's

    }






<<<<<<< HEAD
    private void unloadSlideShow1() {
//        slideShow1.removeAllSliders();
//        slideShow1.addSlider(tsv_slide1_1);
    }
    private void initSlideShow1(){
        tsv_slide1_1
                .image(R.drawable.david)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide1_2
                .image(R.drawable.hero)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_slide1_3
                .image(R.drawable.hero_2)
=======
        tsv_accademia3
                .description(getString(R.string.tartuferi_speech_1))
                .image(R.drawable.a)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_accademia4
                .description(getString(R.string.tartuferi_speech_2))
                .image(R.drawable.s_f)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_accademia5
                .description(getString(R.string.tartuferi_speech_3))
                .image(R.drawable.s_p)
>>>>>>> franco's
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        slideShow1.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow1.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow1.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());
        slideShow1.setCustomAnimation(new DescriptionAnimation());

        slideShow1.addSlider(tsv_slide1_1);
        slideShow1.addSlider(tsv_slide1_2);
        slideShow1.addSlider(tsv_slide1_3);

        slideShow1.stopAutoCycle();
        slideShow1.setCurrentPosition(0);


    }
    private void loadSlideShow1() {
//        slideShow1.addSlider(tsv_slide1_2);
//        unloadSlideShow2();
    }
    private void cycleSlideShow1() {
        slideShow1.setCurrentPosition(0);
        slideShow1.startAutoCycle(10000, 10000, false);
    }
    private void stopCycleSlideShow1() {
        slideShow1.stopAutoCycle();
    }


    private void unloadSlideShow2() {
//        slideShow1.removeSliderAt(1);
    }
    private void initSlideShow2() {
        tsv_slide2_1
                .description(getString(R.string.tartuferi_speech_1))
                .image(R.drawable.david)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide2_2
                .description(getString(R.string.tartuferi_speech_2))
                .image(R.drawable.hero)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide2_3
                .description(getString(R.string.tartuferi_speech_3))
                .image(R.drawable.hero_2)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        slideShow2.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow2.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow2.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());
        slideShow2.setCustomAnimation(new DescriptionAnimation());

        slideShow2.addSlider(tsv_slide2_1);
        slideShow2.addSlider(tsv_slide2_2);
        slideShow2.addSlider(tsv_slide2_3);

        slideShow2.stopAutoCycle();
        slideShow2.setCurrentPosition(0);
    }
    private void loadSlideShow2() {
//        slideShow2.addSlider(tsv_slide2_2);
//        unloadSlideShow1();
    }
    private void cycleSlideShow2() {
        slideShow2.setCurrentPosition(0);
        slideShow2.startAutoCycle(22000, 22000, true);
    }
    private void stopCycleSlideShow2() {
        slideShow2.stopAutoCycle();
    }



    private void unloadSlideShow3() {
//        slideShow3.removeSliderAt(1);
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
        //slideShow1.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());
        slideShow3.setCustomAnimation(new DescriptionAnimation());

<<<<<<< HEAD
        slideShow3.addSlider(tsv_slide3_1);
        slideShow3.addSlider(tsv_slide3_2);
        slideShow3.addSlider(tsv_slide3_3);
        slideShow3.addSlider(tsv_slide3_4);
        slideShow3.addSlider(tsv_slide3_5);
        slideShow3.addSlider(tsv_slide3_6);

        slideShow1.stopAutoCycle();
        slideShow1.setCurrentPosition(0);
=======
        sliderShowHero.addSlider(tsv_hero1);
        sliderShowHero.addSlider(tsv_hero2);
        sliderShowHero.addSlider(tsv_hero3);
        sliderShowHero.addSlider(tsv_hero4);
        sliderShowHero.addSlider(tsv_hero5);
        sliderShowHero.addSlider(tsv_hero6);


        sliderShowHero.stopAutoCycle();
        sliderShowHero.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        //sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        sliderShowHero.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderShowHero.setCustomAnimation(new DescriptionAnimation());



>>>>>>> franco's
    }
    private void loadSlideShow3() {
//        slideShow2.addSlider(tsv_slide2_2);
//        unloadSlideShow1();
    }
    private void cycleSlideShow3() {
        slideShow3.setCurrentPosition(0);
        slideShow3.startAutoCycle(10000, 10000, true);
    }
    private void stopCycleSlideShow3() {
        slideShow3.stopAutoCycle();
    }


    @Override
    public void onFragmentChanged(int oldFragment, int newFragment) {


        if(playing)
        {

            audioStop(oldFragment);
            if(newFragment != 2)
                audioPlay(newFragment);
            else playing = false;
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
//
//
//    private static final int DRAWABLE_PLAY = R.drawable.play;
//    private static final int DRAWABLE_STOP = R.drawable.stop;
//
//
//
//    private AudioPlayer[] audioPlayer = new AudioPlayer[4];
//    private ImageButton[] audioButton = new ImageButton[4];
//    String audioTooltipText[] = new String[4];
//
//    private final int nFragment = 4;
//
//    boolean playing = false;
//
//
//    TooltipManager tooltipManager;
//    DotsProgressBar progressBar;
//
//
//
//    private int dpToPx(int dp) {
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
//        return px;
//    }
//
//
//
//    private ScrollView scrollView;
//    private ViewGroup contentView;
//    private ViewGroup[] fragContainer;
//    ImageView audioButton1;
//    ImageView audioButton2;
//
//    boolean audio1playing = false;
//    boolean audio2playing = false;
//
//    MediaPlayer mPlayer;
//
//    SliderLayout sliderShowAccademia;
//    SliderLayout sliderShowAccademia2;
//    SliderLayout sliderShowHero;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fi);
//        setTitle("Firenze");
//
//
//
//
//        progressBar = (DotsProgressBar) findViewById(R.id.dotsProgressBarFI);
//        progressBar.setDotsCount(4);
//        progressBar.setVisibility(View.VISIBLE);
//        progressBar.setActiveDot(2);
//
//
//        tooltipManager = TooltipManager.getInstance(this);
//
//
//
//        scrollView = (ScrollView) findViewById(R.id.scroll_view_fi);
//        contentView = (ViewGroup) findViewById(R.id.scrolledLayout_fi);
//
//        fragContainer = new ViewGroup[3];
//        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0_fi);
//        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1_fi);
//        fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2_fi);
//
//
//        MyScrollPager scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, true, false);
//        scrollPager.setOnScrollListener(this);
//        scrollView.setOnTouchListener(scrollPager);
//
//        scrollPager.setDotsPageProgressBar(progressBar);
//
//        scrollView.post(new Runnable() {
//            public void run() {
//                scrollView.scrollTo(0, contentView.getPaddingTop());
//            }
//        });
//
//
//
//
//
//        audioButton1 = (ImageView) findViewById(R.id.audio_1_button);
//        audioButton2 = (ImageView) findViewById(R.id.audio_2_button);
//
//        sliderShowAccademia = (SliderLayout) findViewById(R.id.slider_accademia);
//        sliderShowAccademia2 = (SliderLayout) findViewById(R.id.slider_accademia_2);
//        sliderShowHero = (SliderLayout) findViewById(R.id.slider_hero_in_accademia);
//
//
//
//
//        setUpSliders();
//        setUpEvents();
//    }
//
//
//    private void setUpSliders()
//    {
//
//
//        TextSliderView tsv_accademia = new TextSliderView(this);
//        TextSliderView tsv_accademia1 = new TextSliderView(this);
//        TextSliderView tsv_accademia2 = new TextSliderView(this);
//
//
//
//        //textSliderView.description("Hero").image(R.drawable.guardian_hero);
//
//        tsv_accademia
//                .description(getString(R.string.saracino_speech_fi_1))
//                .image(R.drawable.david)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_accademia1
//                .description(getString(R.string.saracino_speech_fi_2))
//                .image(R.drawable.hero)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_accademia2
//                .description(getString(R.string.saracino_speech_fi_3))
//                .image(R.drawable.hero_2)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        sliderShowAccademia.addSlider(tsv_accademia);
//        sliderShowAccademia.addSlider(tsv_accademia1);
//        sliderShowAccademia.addSlider(tsv_accademia2);
//
//        sliderShowAccademia.stopAutoCycle();
//        sliderShowAccademia.setPresetTransformer(SliderLayout.Transformer.DepthPage);
//        //sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
//        sliderShowAccademia.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
//        sliderShowAccademia.setCustomAnimation(new DescriptionAnimation());
//
//
//
//        MyTextSliderView tsv_accademia3 = new MyTextSliderView(this);
//        MyTextSliderView tsv_accademia4 = new MyTextSliderView(this);
//        MyTextSliderView tsv_accademia5 = new MyTextSliderView(this);
//
//
//        tsv_accademia3
//                .description(getString(R.string.tartuferi_speech_1))
//                .image(R.drawable.david)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_accademia4
//                .description(getString(R.string.tartuferi_speech_2))
//                .image(R.drawable.hero)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_accademia5
//                .description(getString(R.string.tartuferi_speech_3))
//                .image(R.drawable.hero_2)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//
//        sliderShowAccademia2.addSlider(tsv_accademia3);
//        sliderShowAccademia2.addSlider(tsv_accademia4);
//        sliderShowAccademia2.addSlider(tsv_accademia5);
//
//        sliderShowAccademia2.stopAutoCycle();
//        sliderShowAccademia2.setPresetTransformer(SliderLayout.Transformer.DepthPage);
//        //sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
//        sliderShowAccademia2.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
//        sliderShowAccademia2.setCustomAnimation(new DescriptionAnimation());
//
//
//        // HERO
//
//        TextSliderView tsv_hero1 = new TextSliderView(this);
//        TextSliderView tsv_hero2 = new TextSliderView(this);
//        TextSliderView tsv_hero3 = new TextSliderView(this);
//        TextSliderView tsv_hero4 = new TextSliderView(this);
//        TextSliderView tsv_hero5 = new TextSliderView(this);
//        TextSliderView tsv_hero6 = new TextSliderView(this);
//
//
//
//        //textSliderView.description("Hero").image(R.drawable.guardian_hero);
//
//
//
//
//
//
//        tsv_hero1
//                .description("Genesis of Hero")
//                .image(R.drawable.cava)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_hero2
//                .description("Genesis of Hero")
//                .image(R.drawable.blocco)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_hero3
//                .description("Genesis of Hero")
//                .image(R.drawable.blocco2)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_hero4
//                .description("Genesis of Hero")
//                .image(R.drawable.osso)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_hero5
//                .description("Genesis of Hero")
//                .image(R.drawable.osso2)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//        tsv_hero6
//                .description("Genesis of Hero")
//                .image(R.drawable.fresa)
//                .setScaleType(BaseSliderView.ScaleType.CenterInside);
//
//
//
//
//
//        sliderShowHero.addSlider(tsv_hero1);
//        sliderShowHero.addSlider(tsv_hero2);
//        sliderShowHero.addSlider(tsv_hero3);
//        sliderShowHero.addSlider(tsv_hero4);
//        sliderShowHero.addSlider(tsv_hero5);
//        sliderShowHero.addSlider(tsv_hero6);
//
//
//        sliderShowHero.stopAutoCycle();
//        sliderShowHero.setPresetTransformer(SliderLayout.Transformer.DepthPage);
//        //sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
//        sliderShowHero.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
//        sliderShowHero.setCustomAnimation(new DescriptionAnimation());
//
//
//
//    }
//
//
//    private void stopAudio1()
//    {
//        audio1playing = false;
//        audioButton1.setImageResource(R.drawable.play);
//        if(mPlayer != null ) {
//            mPlayer.stop();
//            mPlayer.reset();
//        }
//
//    }
//    private void stopAudio2()
//    {
//        audio2playing = false;
//        audioButton2.setImageResource(R.drawable.play);
//        if(mPlayer != null ) {
//            mPlayer.stop();
//            mPlayer.reset();
//        }
//
//    }
//    private void stopAudio()
//    {
//        stopAudio1();
//        stopAudio2();
//    }
//    private void playAudio1()
//    {
//        stopAudio1();
//        stopAudio2();
//        audio1playing = true;
//        audioButton1.setImageResource(R.drawable.stop);
//        mPlayer = MediaPlayer.create(getBaseContext(), R.raw.innocenti_florence_en);
//        mPlayer.start();
//    }
//    private void playAudio2()
//    {
//        stopAudio1();
//        stopAudio2();
//        audio2playing = true;
//        audioButton2.setImageResource(R.drawable.stop);
//        mPlayer = MediaPlayer.create(getBaseContext(), R.raw.tartufieri_florence_it);
//        mPlayer.start();
//    }
//
//
//
//    private void setUpEvents()
//    {
//
//        audioButton1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!audio1playing)
//                {
//                    playAudio1();
//
//                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            stopAudio();
//                        }
//
//                    });
//                }
//                else
//                {
//                    stopAudio1();
//                }
//            }
//        });
//
//
//
//        audioButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!audio2playing)
//                {
//                    playAudio2();
//
//                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            stopAudio();
//                        }
//
//                    });
//                }
//                else
//                {
//                    stopAudio2();
//                }
//            }
//        });
//    }
//
//
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_fi, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onFragmentChanged(int oldFragment, int newFragment) {
//
//    }
//
//    @Override
//    public void onPageChanged(int oldPage, int newPage, int oldFragment, int newFragment) {
//
//    }
}
