package micc.theguardiansapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ScrollView;



public class MainActivity extends ActionBarActivity {



    private ScrollView scrollView;
    private ViewGroup contentView;
    private ViewGroup[] fragContainer;

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
