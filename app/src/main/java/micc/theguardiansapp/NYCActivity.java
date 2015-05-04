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


    private static final int DRAWABLE_PLAY = R.drawable.sound_icon_small_3;
    private static final int DRAWABLE_STOP = R.drawable.stop;



    private AudioPlayer[] audioPlayer = new AudioPlayer[2];
    private ImageButton[] audioButton = new ImageButton[2];
    String audioTooltipText[] = new String[2];
    private final int nFragment = 2;

    boolean playing = false;


    TooltipManager tooltipManager;
    DotsProgressBar progressBar;


    SliderLayout slideShow[] = new SliderLayout[nFragment];

    SliderLayout slideShow1;
    SliderLayout slideShow2;

    MyTextSliderView tsv_slide1_1;
    MyTextSliderView tsv_slide1_2;
    MyTextSliderView tsv_slide2_1;
    MyTextSliderView tsv_slide2_2;
    MyTextSliderView tsv_slide2_3;
    MyTextSliderView tsv_slide2_4;
    MyTextSliderView tsv_slide2_5;
    MyTextSliderView tsv_slide2_6;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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



        slideShow[0] = (SliderLayout) findViewById(R.id.slider_nyc);
        slideShow[1] = (SliderLayout) findViewById(R.id.slider_nyc_2);
        // slideShowInit();
        audioInit();




    }


    private void audioInit() {

        audioButton[0] = (ImageButton) findViewById(R.id.audio_1_button);
        audioButton[1] = (ImageButton) findViewById(R.id.audio_2_button);

        audioPlayer[0] = new AudioPlayer(getBaseContext());
        audioPlayer[1] = new AudioPlayer(getBaseContext());
        audioPlayer[0].loadAudio(R.raw.saracino_ny_1);
        audioPlayer[1].loadAudio(R.raw.saracino_ny_2);


        audioTooltipText[0] = "The author: Antonio Pio Saracino";
        audioTooltipText[1] = "The author: Antonio Pio Saracino";



        for(int i = 0; i < nFragment; i ++ )
        {

            final int index = i;

            tooltipManager.hide(i);

            if(audioButton[i] != null)
            {
                audioButton[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAudioButtonClicked(index);
                    }
                });

                audioPlayer[index].setAudioPlayerListener(new AudioPlayerListener() {
                    @Override
                    public void onCompletion(boolean inEarpieceMode) {
                        onAudioPlayerFinished(index);
                    }

                    @Override
                    public void onPaused() {
                    }

                    @Override
                    public void onStopped() {
                        onAudioPlayerStopped(index);
                    }
                });

            }

        }

    }


    private void slideShowInit() {


        tsv_slide1_1 = new MyTextSliderView(this);
        tsv_slide1_2 = new MyTextSliderView(this);

        tsv_slide2_1 = new MyTextSliderView(this);
        tsv_slide2_2 = new MyTextSliderView(this);
        tsv_slide2_3 = new MyTextSliderView(this);
        tsv_slide2_4 = new MyTextSliderView(this);
        tsv_slide2_5 = new MyTextSliderView(this);
        tsv_slide2_6 = new MyTextSliderView(this);


        tsv_slide1_1
                .description(getString(R.string.saracino_speech_nyc_1))
                .image(R.drawable.hero_garden)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        tsv_slide1_2
                .description(getString(R.string.saracino_speech_nyc_2))
                .image(R.drawable.superhero_garden)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);



        slideShow[0].setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow[0].setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow1.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());

        slideShow[0].addSlider(tsv_slide1_1);
        slideShow[0].addSlider(tsv_slide1_2);
        slideShow[0].stopAutoCycle();
        slideShow[0].setCurrentPosition(0);





        tsv_slide2_1
                .description(getString(R.string.saracino_speech_nyc_3))
                .image(R.drawable.hero_1)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_slide2_2
                .description(getString(R.string.saracino_speech_nyc_4))
                .image(R.drawable.hero_2)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);


        //slideShow[1].setPresetTransformer(SliderLayout.Transformer.DepthPage);
        slideShow[1].setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        //slideShow2.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());

        slideShow[1].addSlider(tsv_slide2_1);
        slideShow[1].addSlider(tsv_slide2_2);
//        slideShow[1].addSlider(tsv_slide2_3);
//        slideShow[1].addSlider(tsv_slide2_4);
//        slideShow[1].addSlider(tsv_slide2_5);

        slideShow[1].stopAutoCycle();
        slideShow[1].setCurrentPosition(0);
    }

    private void slideShowStartCycle(int index)  {
        switch(index)
        {
            case 0:
                slideShow[0].setCurrentPosition(0);
                slideShow[0].startAutoCycle(26000, 11000, true);
                break;
            case 1:
                slideShow[1].setCurrentPosition(1);
                slideShow[1].startAutoCycle(15000, 22000, true);
                break;

        }
    }
    private void slideShowStopCycle(int index) {
        if(slideShow[index] != null )
            slideShow[0].stopAutoCycle();
    }
    private int scrollViewGoNext()
    {
        int oldIndex = scrollPager.getActiveFragment();
        int newIndex = scrollPager.gotoFragment((oldIndex + 1) % scrollPager.nFragment() );

        return newIndex;
    }

    @Override
    public void onFragmentChanged(int oldFragment, int newFragment) {



        if(playing) {
            onAudioButtonClicked(oldFragment);
            onAudioButtonClicked(newFragment);
        }

//        switch(newFragment)
//        {
//            case 0:
//                loadSlideShow1();
//                break;
//            case 1:
//                loadSlideShow2();
//                break;
//
//        }

    }


    /**
     * Prepara l'ambiente alla riproduzione dell'audio, senza iniziare la riproduzione dell'audioPlayer.
     *
     * @param index
     * @return ritorna true se è stata compiuta la preparazione per il PLAY,
     *          false se è stata compiuta la preparazione per lo STOP
     */
    private boolean prepareAudioToggle(int index) {
        if(playing == false)
        {
            if(audioButton[index] != null)
                audioButton[index].setImageResource(DRAWABLE_STOP);
            slideShowStartCycle(index);
            showAudioTooltip(index);
            playing = true;
        }
        else if(playing == true)
        {

            if(audioButton[index] != null)
                audioButton[index].setImageResource(DRAWABLE_PLAY);
             slideShowStopCycle(index);
            hideAudioTooltip(index);

            playing = false;
        }
        return playing;
    }




    private void onAudioButtonClicked(int index) {
        if(audioPlayer[index] != null )
        {
            if(prepareAudioToggle(index)) {
                audioPlayer[index].play();

            }
            else audioPlayer[index].stop();
        }
    }

    private void onAudioPlayerFinished(int index) {
        onAudioButtonClicked(index);
        int next = scrollViewGoNext();
        if(index != nFragment-1)
            onAudioButtonClicked(next);
    }

    private void onAudioPlayerStopped(int index) {

    }

    private void showAudioTooltip(int index) {

        tooltipManager.create(index)
                .anchor(new Point((int)scrollView.getWidth()/2, (int)scrollView.getHeight() - dpToPx(35) ), TooltipManager.Gravity.TOP)
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

    private void hideAudioTooltip(int index) {
        tooltipManager.hide(index);
    }





    @Override
    protected void onStart() {
        super.onStart();
        for (int i = 0; i < nFragment; i++) {
            if(audioPlayer[i] != null)
                audioPlayer[i].onActivityStarted();
        }

        slideShowInit();
    }


    @Override
    protected void onStop() {

        slideShow[0].stopAutoCycle();
        slideShow[1].stopAutoCycle();
        tsv_slide1_1=null;
        tsv_slide2_2=null;
        tsv_slide2_1=null;
        tsv_slide2_2=null;
        tsv_slide2_3=null;
        tsv_slide2_4=null;
        tsv_slide2_5=null;
        tsv_slide2_6=null;


        super.onStop();


        for(int i = 0; i < nFragment; i++) {
            if(audioPlayer[i] != null)
                audioPlayer[i].onActivityStopped();
        }

    }





    @Override
    public void onPageChanged(int oldPage, int newPage, int oldFragment, int newFragment) {

    }
}
