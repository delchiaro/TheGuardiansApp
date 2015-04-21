package micc.theguardiansapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {



    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;
private MediaPlayer mPlayer;
    private boolean audioStarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        contentView = (ViewGroup) findViewById(R.id.scrolledLayout);
        fragContainer = new ViewGroup[3];

        fragContainer[0] = (ViewGroup) findViewById(R.id.fragContainer0);
        fragContainer[1] = (ViewGroup) findViewById(R.id.fragContainer1);
        fragContainer[2] = (ViewGroup) findViewById(R.id.fragContainer2);



        MyScrollPager scrollPager = new MyScrollPager(scrollView, contentView, fragContainer);
        scrollView.setOnTouchListener(scrollPager);


        scrollView.post(new Runnable()
        {
            public void run()
            {
                scrollView.scrollTo(0, contentView.getPaddingTop());
            }
        });



        FragmentHelper.setMainActivity(this);

        Intent intent = new Intent(this, BeaconService.class);
        if (intent != null) {
            this.startService(intent);
        }


//         mPlayer = MediaPlayer.create(this, R.raw.hero_florence);
//        try {
//            mPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(audioStarted == false)
//                {
//                    mPlayer.start();
//                    audioStarted = true;
//                }
//                else{
//                    mPlayer.stop();
//                    audioStarted = false;
//                }
//            }
//        });
//




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


}
