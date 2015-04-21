package micc.theguardiansapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.estimote.sdk.Beacon;

import micc.theguardiansapp.beaconServiceHelper.BeaconBestProximityListener;
import micc.theguardiansapp.beaconServiceHelper.GoodBadBeaconProximityManager;


public class BeaconService extends IntentService implements BeaconBestProximityListener
{


    private View mView;

    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;




    private GoodBadBeaconProximityManager proximityManager;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public BeaconService() {
        super("beaconService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_NOT_STICKY;
    }


    private Dialog dialog = null;

    @Override
    public void onCreate() {
        super.onCreate();


        proximityManager = new GoodBadBeaconProximityManager(this, this);

        proximityManager.scan();
//
//        mView = new MyLoadView(this);
//
//        mParams = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.MATCH_PARENT, 150, 10, 10,
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//                PixelFormat.TRANSLUCENT);
//
//        mParams.gravity = Gravity.CENTER;
//        mParams.setTitle("Window test");
//
//        mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
//        mWindowManager.addView(mView, mParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        proximityManager.stopScan();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

        @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
            return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


    @Override
    public void OnNewBeaconBestProximity(Beacon bestProximity, Beacon oldBestProximity) {

        dialog = new Dialog(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getApplicationContext());

        builder.setMessage("Do you want to see artwork informations?").setTitle("The Guardian App: Artwork found!");
        builder.setPositiveButton("Open Info",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg,
                                        int which) {
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton("Close",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg,
                                        int which) {
                        dialog.cancel();
                        dialog = null;
                    }
                });
        dialog= builder.create();
        dialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();

    }

    @Override
    public void OnNoneBeaconBestProximity(Beacon oldBestProximity) {
        if(dialog != null)
        {
            dialog.dismiss();
        }
    }
}


 class MyLoadView extends View {

    private Paint mPaint;

    public MyLoadView(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setTextSize(50);
        mPaint.setARGB(200, 200, 200, 200);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("test test test", 0, 100, mPaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}