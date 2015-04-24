package micc.theguardiansapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.daimajia.slider.library.Animations.BaseAnimationInterface;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by nagash on 24/04/15.
 */
public class DescriptionAnimation implements BaseAnimationInterface {
    /**
     * When current item is going to leave, let's make the description layout disappear.
     */
    @Override
    public void onPrepareCurrentItemLeaveScreen(View current) {
        View descriptionLayout = current.findViewById(R.id.description_layout);
        if (descriptionLayout != null) {
            current.findViewById(R.id.description_layout).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * When next item is coming to show, let's hide the description layout.
     */
    @Override
    public void onPrepareNextItemShowInScreen(View next) {
        View descriptionLayout = next.findViewById(R.id.description_layout);
        if (descriptionLayout != null) {
            next.findViewById(R.id.description_layout).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCurrentItemDisappear(View current) {

    }

    /**
     * When next item is shown, let's make an animation to show the
     * description layout.
     */
    @Override
    public void onNextItemAppear(View next) {

        View descriptionLayout = next.findViewById(R.id.description_layout);
        if (descriptionLayout != null) {
            float layoutY = ViewHelper.getY(descriptionLayout);
            next.findViewById(R.id.description_layout).setVisibility(View.VISIBLE);
            ValueAnimator animator = ObjectAnimator.ofFloat(
                    descriptionLayout, "y", layoutY + descriptionLayout.getHeight(),
                    layoutY).setDuration(500);
            animator.start();
        }
    }
}