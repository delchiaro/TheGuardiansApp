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

import com.daimajia.slider.library.Animations.*;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

public class NYCActivity extends ActionBarActivity {

    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;
    ImageView audioButton1;
    ImageView audioButton2;

    boolean audio1playing = false;
    boolean audio2playing = false;

    MediaPlayer mPlayer;

    SliderLayout sliderShowNYC;
    SliderLayout sliderShowNYC2;
    SliderLayout sliderShowNYC3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nyc);
        setTitle("New York City");

        scrollView = (ScrollView) findViewById(R.id.scroll_view_nyc);
        contentView = (ViewGroup) findViewById(R.id.scrolledLayout_nyc);

        fragContainer = new ViewGroup[3];
        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0_nyc);
        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1_nyc);
        fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2_nyc);


        MyScrollPager scrollPager = new MyScrollPager(scrollView, contentView, fragContainer, true, false);
        scrollView.setOnTouchListener(scrollPager);

        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(0, contentView.getPaddingTop());
            }
        });

        audioButton1 = (ImageView) findViewById(R.id.audio_1_button);
        audioButton2 = (ImageView) findViewById(R.id.audio_2_button);

        sliderShowNYC = (SliderLayout) findViewById(R.id.slider_nyc);
        sliderShowNYC2 = (SliderLayout) findViewById(R.id.slider_nyc_2);

        setUpSliders();
        setUpEvents();
    }


    private void setUpSliders()
    {
        MyTextSliderView tsv_nyc = new MyTextSliderView(this);
        MyTextSliderView tsv_nyc2 = new MyTextSliderView(this);

        tsv_nyc
            .description("Hero in Bryant Park")
            .image(R.drawable.hero_garden)
            .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_nyc
            .description("Hero in Bryant Park")
            .image(R.drawable.superhero_garden)
            .setScaleType(BaseSliderView.ScaleType.CenterInside);

        sliderShowNYC.addSlider(tsv_nyc);
        sliderShowNYC.addSlider(tsv_nyc2);
        sliderShowNYC.stopAutoCycle();
        sliderShowNYC.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sliderShowNYC.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderShowNYC.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());






        MyTextSliderView tsv_nyc3 = new MyTextSliderView(this);
        MyTextSliderView tsv_nyc4 = new MyTextSliderView(this);

        tsv_nyc3
             .description("Hero in Bryant Park")
             .image(R.drawable.hero_1)
             .setScaleType(BaseSliderView.ScaleType.CenterInside);

        tsv_nyc4
             .description("Hero in Bryant Park")
             .image(R.drawable.hero_2)
             .setScaleType(BaseSliderView.ScaleType.CenterInside);

        sliderShowNYC2.addSlider(tsv_nyc3);
        sliderShowNYC2.addSlider(tsv_nyc4);
        sliderShowNYC2.stopAutoCycle();
        sliderShowNYC2.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sliderShowNYC2.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderShowNYC2.setCustomAnimation(new com.daimajia.slider.library.Animations.DescriptionAnimation());


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
        getMenuInflater().inflate(R.menu.menu_ny, menu);
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
