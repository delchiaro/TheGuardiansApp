package micc.theguardiansapp.dotsProgressBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import micc.theguardiansapp.R;




public class DotsProgressBar extends View {

	private float mRadius;

    private Paint mPaintArrow  = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Handler mHandler = new Handler();
	private int mIndex = 0;
	private int widthSize, heightSize;
	private int margin = 5;
	private int mDotCount = 3;
    private boolean arrowAtStartEnd = true;
    private boolean arrowFilled = false;
    private boolean arrowLateral = false;
    private boolean arrowLateralLeft = false;
    private int arrowMargin = 5;



    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public DotsProgressBar(Context context) {
		super(context);
		init(context);
	}

	public DotsProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DotsProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mRadius = context.getResources().getDimension(R.dimen.circle_indicator_radius);
		// dot fill color
		mPaintFill.setStyle(Style.FILL);
		mPaintFill.setColor(Color.BLACK);

        if(arrowFilled) mPaintArrow.setStyle(Style.FILL);
        else mPaintArrow.setStyle(Style.STROKE);
        mPaintArrow.setColor(Color.BLACK);
        mPaintArrow.setStrokeWidth((float)Math.ceil(dpToPx((int)mRadius)/8));

		// dot background color
		mPaint.setStyle(Style.FILL);
		mPaint.setColor(0x33000000);
		//start();
	}

	public void setDotsCount(int count) {
		mDotCount = count;
        invalidate();
	}

	public void start() {
		mIndex = -1;
		mHandler.removeCallbacks(mRunnable);
		mHandler.post(mRunnable);
	}

    public boolean setActiveDot(int index) {
        if(index >= 0 && index < mDotCount)
        {
            mIndex = index;
            invalidate();
            return true;
        }
        else return false;
    }


	public void stop() {
		mHandler.removeCallbacks(mRunnable);
	}

	private int step = 1;
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			mIndex += step;
			if (mIndex < 0) {
				mIndex = 1;
				step = 1;
			} else if (mIndex > (mDotCount - 1)) {
				if ((mDotCount - 2) >= 0) {
					mIndex = mDotCount - 2;
					step = -1;
				} else{
					mIndex = 0;
					step = 1;
				}

			}

			invalidate();

			mHandler.postDelayed(mRunnable, 300);

		}

	};

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		widthSize = (int) mRadius * 2 + getPaddingLeft() + getPaddingRight();
		heightSize =  MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(widthSize, heightSize);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//float dX = (widthSize - mDotCount * mRadius * 2 - (mDotCount - 1) * margin) / 2.0f;
		float dX = widthSize/2;

        //float dY = heightSize / 2;
        float dY = (heightSize - mDotCount * mRadius * 2 - (mDotCount - 1) * margin) / 2.0f;

        float arrowLateralStartY = (dY - mRadius);


        if(arrowAtStartEnd)
        {
            Point arrowHeadPoint;
            Point arrowLeftDown;
            Point arrowrightDown;

            if( arrowLateral )
            {
                int dX_arrow;
                if(arrowLateralLeft)
                    dX_arrow = (int) (dX	- (2 * mRadius + arrowMargin));
                else  dX_arrow = (int) (dX	+ (2 * mRadius + arrowMargin));


                arrowHeadPoint = new Point(dX_arrow, (int) (dY - mRadius));
                arrowLeftDown = new Point((int) (dX_arrow - mRadius), (int)dY );
                arrowrightDown = new Point((int) (dX_arrow + mRadius), (int) dY);
            }
            else
            {
                arrowHeadPoint = new Point((int)dX,  (int) (dY - (2 * mRadius + arrowMargin ) ));
                arrowLeftDown = new Point((int)(dX - mRadius),  (int) (dY - (2 * mRadius + arrowMargin - mRadius)));
                arrowrightDown = new Point((int)(dX + mRadius),  (int) (dY - (2 * mRadius + arrowMargin - mRadius)));

            }

            Path path = new Path();

            path.moveTo(arrowrightDown.x, arrowrightDown.y);
            path.lineTo(arrowHeadPoint.x, arrowHeadPoint.y);
            path.lineTo(arrowLeftDown.x, arrowLeftDown.y);

            canvas.drawPath(path, mPaintArrow);
        }
		for (int i = 0; i < mDotCount; i++) {
			if (i == mIndex) {
				canvas.drawCircle(dX, dY, mRadius, mPaintFill);
			} else {
				canvas.drawCircle(dX, dY, mRadius, mPaint);
			}

			dY += (2 * mRadius + margin);
		}


        if(arrowAtStartEnd)
        {
            dY -= (2 * mRadius + margin);

            Point arrowHeadPoint;
            Point arrowLeftDown;
            Point arrowrightDown;
            Point arrowHeadPointStart = null;

            if( arrowLateral )
            {
                int dX_arrow;
                if(arrowLateralLeft)
                    dX_arrow = (int) (dX	- (2 * mRadius + arrowMargin));
                else  dX_arrow = (int) (dX	+ (2 * mRadius + arrowMargin));


                arrowHeadPointStart = new Point(dX_arrow , (int) arrowLateralStartY);
                arrowHeadPoint = new Point(dX_arrow, (int) (dY - mRadius));
                arrowLeftDown = new Point((int) (dX_arrow - mRadius), (int)dY );
                arrowrightDown = new Point((int) (dX_arrow + mRadius), (int) dY);

                Path path1 = new Path();
                path1.moveTo(arrowHeadPointStart.x, arrowHeadPointStart.y);
                path1.lineTo(arrowHeadPoint.x, arrowHeadPoint.y);

                canvas.drawPath(path1, mPaintArrow);
            }
            else
            {
                arrowHeadPoint = new Point((int) dX, (int) (dY + (2 * mRadius + arrowMargin)));
                arrowLeftDown = new Point((int) (dX - mRadius), (int) (dY + (2 * mRadius + arrowMargin - mRadius)));
                arrowrightDown = new Point((int) (dX + mRadius), (int) (dY + (2 * mRadius + arrowMargin - mRadius)));
            }
            Path path = new Path();
            path.moveTo(arrowrightDown.x, arrowrightDown.y);
            path.lineTo(arrowHeadPoint.x, arrowHeadPoint.y);
            path.lineTo(arrowLeftDown.x, arrowLeftDown.y);

            canvas.drawPath(path, mPaintArrow);
        }

	}
}
