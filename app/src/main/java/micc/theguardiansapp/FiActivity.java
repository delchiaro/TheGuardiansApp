package micc.theguardiansapp;

import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.getbase.floatingactionbutton.FloatingActionButton;

import micc.theguardiansapp.scrollPager.MyScrollPager;


public class FiActivity extends ActionBarActivity {


    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;
    ImageView audioButton1;
    ImageView audioButton2;

    boolean audio1playing = false;
    boolean audio2playing = false;

    MediaPlayer mPlayer;

    SliderLayout sliderShowAccademia;
    SliderLayout sliderShowAccademia2;
    SliderLayout sliderShowHero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fi);
        setTitle("City of Florence");



        scrollView = (ScrollView) findViewById(R.id.scroll_view_fi);
        contentView = (ViewGroup) findViewById(R.id.scrolledLayout_fi);

        fragContainer = new ViewGroup[3];
        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0_fi);
        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1_fi);
        fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2_fi);


        MyScrollPager scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, true, false);
        scrollView.setOnTouchListener(scrollPager);


        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(0, contentView.getPaddingTop());
            }
        });





        audioButton1 = (ImageView) findViewById(R.id.audio_1_button);
        audioButton2 = (ImageView) findViewById(R.id.audio_2_button);

        sliderShowAccademia = (SliderLayout) findViewById(R.id.slider_accademia);
        sliderShowAccademia2 = (SliderLayout) findViewById(R.id.slider_accademia_2);
        sliderShowHero = (SliderLayout) findViewById(R.id.slider_hero_in_accademia);




        setUpSliders();
        setUpEvents();
    }


    private void setUpSliders()
    {


        TextSliderView tsv_accademia = new TextSliderView(this);
        TextSliderView tsv_accademia1 = new TextSliderView(this);
        TextSliderView tsv_accademia2 = new TextSliderView(this);



        //textSliderView.description("Hero").image(R.drawable.guardian_hero);

        tsv_accademia
                .description("Michelangelo's David")
                .image(R.drawable.david)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_accademia1
                .description("Saracino's Hero")
                .image(R.drawable.hero)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_accademia2
                .description("Hero (Another View)")
                .image(R.drawable.hero_2)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        sliderShowAccademia.addSlider(tsv_accademia);
        sliderShowAccademia.addSlider(tsv_accademia1);
        sliderShowAccademia.addSlider(tsv_accademia2);

        sliderShowAccademia.stopAutoCycle();
        sliderShowAccademia.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        //sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        sliderShowAccademia.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderShowAccademia.setCustomAnimation(new DescriptionAnimation());



        sliderShowAccademia2.addSlider(tsv_accademia);
        sliderShowAccademia2.addSlider(tsv_accademia1);
        sliderShowAccademia2.addSlider(tsv_accademia2);

        sliderShowAccademia2.stopAutoCycle();
        sliderShowAccademia2.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        //sliderShow.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
        sliderShowAccademia2.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderShowAccademia2.setCustomAnimation(new DescriptionAnimation());


        // HERO

        TextSliderView tsv_hero1 = new TextSliderView(this);
        TextSliderView tsv_hero2 = new TextSliderView(this);
        TextSliderView tsv_hero3 = new TextSliderView(this);
        TextSliderView tsv_hero4 = new TextSliderView(this);
        TextSliderView tsv_hero5 = new TextSliderView(this);
        TextSliderView tsv_hero6 = new TextSliderView(this);



        //textSliderView.description("Hero").image(R.drawable.guardian_hero);






        tsv_hero1
                .description("Genesis of Hero")
                .image(R.drawable.cava)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_hero2
                .description("Genesis of Hero")
                .image(R.drawable.blocco)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_hero3
                .description("Genesis of Hero")
                .image(R.drawable.blocco2)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_hero4
                .description("Genesis of Hero")
                .image(R.drawable.osso)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_hero5
                .description("Genesis of Hero")
                .image(R.drawable.osso2)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_hero6
                .description("Genesis of Hero")
                .image(R.drawable.fresa)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);





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



    }


    private void stopAudio1()
    {
        audio1playing = false;
        audioButton1.setImageResource(R.drawable.play);
        if(mPlayer != null ) {
            mPlayer.stop();
            mPlayer.reset();
        }

    }
    private void stopAudio2()
    {
        audio2playing = false;
        audioButton2.setImageResource(R.drawable.play);
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
        audioButton1.setImageResource(R.drawable.stop);
        mPlayer = MediaPlayer.create(getBaseContext(), R.raw.innocenti_florence_en);
        mPlayer.start();
    }
    private void playAudio2()
    {
        stopAudio1();
        stopAudio2();
        audio2playing = true;
        audioButton2.setImageResource(R.drawable.stop);
        mPlayer = MediaPlayer.create(getBaseContext(), R.raw.tartufieri_florence_it);
        mPlayer.start();
    }



    private void setUpEvents()
    {

        audioButton1.setOnClickListener(new View.OnClickListener() {
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



        audioButton2.setOnClickListener(new View.OnClickListener() {
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
