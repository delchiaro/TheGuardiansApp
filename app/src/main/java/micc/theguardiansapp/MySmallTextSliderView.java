package micc.theguardiansapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;

/**
 * Created by nagash on 24/04/15.
 */
public class MySmallTextSliderView extends BaseSliderView {

    public MySmallTextSliderView(Context context) {
        super(context);
    }
    @Override
    public View getView() {


        View v = LayoutInflater.from(getContext()).inflate(R.layout.my_small_text_slider_layout,null);
        ImageView target = (ImageView)v.findViewById(R.id.daimajia_slider_image);
        TextView description = (TextView)v.findViewById(R.id.description);
        description.setText(getDescription());
        bindEventAndShow(v, target);



        return v;
    }
}
