package micc.theguardiansapp;

import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.getbase.floatingactionbutton.FloatingActionButton;


public class FiActivity extends ActionBarActivity {


    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;
    FloatingActionButton fabAudio1;
    FloatingActionButton fabAudio2;

    boolean audio1playing = false;
    boolean audio2playing = false;

    MediaPlayer mPlayer;

    SliderLayout sliderShowAccademia;
    SliderLayout sliderShowHero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fi);
        setTitle("Florence City");



        scrollView = (ScrollView) findViewById(R.id.scroll_view_fi);
        contentView = (ViewGroup) findViewById(R.id.scrolledLayout_fi);
        fragContainer = new ViewGroup[2];

        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0_fi);
        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1_fi);

        MyScrollPager scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, true, false);
        scrollView.setOnTouchListener(scrollPager);


        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(0, contentView.getPaddingTop());
            }
        });





        fabAudio1 = (FloatingActionButton) findViewById(R.id.fab_fi_audio_1);
        fabAudio2 = (FloatingActionButton) findViewById(R.id.fab_fi_audio_2);

        sliderShowAccademia = (SliderLayout) findViewById(R.id.slider_accademia);
        sliderShowHero = (SliderLayout) findViewById(R.id.slider_hero_in_accademia);




        setUpSliders();
        setUpEvents();
    }


    private void setUpSliders()
    {

        MyTextSliderView textSliderView = new MyTextSliderView(this);
        MyTextSliderView textSliderView0 = new MyTextSliderView(this);
        MyTextSliderView textSliderView2 = new MyTextSliderView(this);


        //textSliderView.description("Hero").image(R.drawable.guardian_hero);

        textSliderView
                .description("David of Michelangelo")
                .image("http://upload.wikimedia.org/wikipedia/commons/d/d7/Michelangelo's_David.JPG")
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        textSliderView0
                .description("David in Accademia Gallery")
                .image("http://www.miragu.com/dbFoto/David.JPG")
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        textSliderView2
                .description("Accademia Gallery")
                .image(" http://www.guidedflorencetour.com/data/uploads/accademia_gallery_tour/006-accademia_florence.jpg\n")
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        sliderShowAccademia.addSlider(textSliderView);
        sliderShowAccademia.addSlider(textSliderView0);
        sliderShowAccademia.addSlider(textSliderView2);

        sliderShowAccademia.stopAutoCycle();
        sliderShowAccademia.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        //sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        sliderShowAccademia.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderShowAccademia.setCustomAnimation(new DescriptionAnimation());





        // HERO

        MyTextSliderView textSliderView3 = new MyTextSliderView(this);
        MyTextSliderView textSliderView4 = new MyTextSliderView(this);


        //textSliderView.description("Hero").image(R.drawable.guardian_hero);

        textSliderView3
                .description("Game of Thrones")
                .image("http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg")
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        textSliderView4
                .description("Hero")
                .image(R.drawable.guardian_hero)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);




        sliderShowHero.addSlider(textSliderView4);
        sliderShowHero.addSlider(textSliderView3);


        sliderShowHero.stopAutoCycle();
        sliderShowHero.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        //sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        sliderShowHero.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderShowHero.setCustomAnimation(new DescriptionAnimation());



    }


    private void stopAudio1()
    {
        audio1playing = false;
        fabAudio1.setIcon(R.drawable.play);
        if(mPlayer != null ) {
            mPlayer.stop();
            mPlayer.reset();
        }

    }
    private void stopAudio2()
    {
        audio2playing = false;
        fabAudio2.setIcon(R.drawable.play);
        if(mPlayer != null ) {
            mPlayer.stop();
            mPlayer.reset();
        }

    }
    private void stopAudio()
    {
        stopAudio1();
        stopAudio2();
    }
    private void playAudio1()
    {
        stopAudio1();
        stopAudio2();
        audio1playing = true;
        fabAudio1.setIcon(R.drawable.stop);
        mPlayer = MediaPlayer.create(getBaseContext(), R.raw.innocenti_florence_en);
        mPlayer.start();
    }
    private void playAudio2()
    {
        stopAudio1();
        stopAudio2();
        audio2playing = true;
        fabAudio2.setIcon(R.drawable.stop);
        mPlayer = MediaPlayer.create(getBaseContext(), R.raw.tartufieri_florence_it);
        mPlayer.start();
    }



    private void setUpEvents()
    {

        fabAudio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!audio1playing)
                {
                    playAudio1();

                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopAudio();
                        }

                    });
                }
                else
                {
                    stopAudio1();
                }
            }
        });



        fabAudio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!audio2playing)
                {
                    playAudio2();

                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopAudio();
                        }

                    });
                }
                else
                {
                    stopAudio2();
                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fi, menu);
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
}
