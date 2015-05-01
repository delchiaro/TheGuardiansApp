package micc.theguardiansapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by nagash on 01/05/15.
 */
public class AudioPlayer implements SensorEventListener
{

    private final static int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC ;
    //      AudioManager.STREAM_VOICE_CALL

    private int currentStreamType = DEFAULT_STREAM_TYPE;
    private MediaPlayer mPlayer;
    private final Context context;
    private final Resources res;

    private Uri currentTrackUri;

    private static SensorManager sensorManager = null;
    private static Sensor proximitySensor = null;


    private int field = 0x00000020;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private boolean screenOffPlaying = false;
    private long lastScreenSwitchTime = 0;
    private final int MIN_TIME_BETWEEN_SWITCH_SCREEN = 3000;

    public AudioPlayer(Context context) {
        this.context = context;
        res = context.getResources();
        mPlayer = new MediaPlayer();

        if(sensorManager == null)
        {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            proximitySensor.getMaximumRange();

            try {
                // Yeah, this is hidden field.
                field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
            } catch (Throwable ignored) {
            }

            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(field, "HeroAudioPlayer");

        }


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();

            }
        });

    }

    public void onActivityStarted() {
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        //this.stop();
    }
    public void onActivityStopped() {
        sensorManager.unregisterListener(this);
        if(!screenOffPlaying)
        {
            this.stop();
        }
    }


    private Uri resourceToUri(int resource) {

        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(R.raw.hero_florence) +
                '/' + res.getResourceTypeName(R.raw.hero_florence) +
                '/' + res.getResourceEntryName(R.raw.hero_florence) );

        return uri;


    }



    public void setOnCompletionListener( MediaPlayer.OnCompletionListener listener ){
        mPlayer.setOnCompletionListener(listener);
    }

    public void loadAudio(int resource) {
        loadAudio(resourceToUri(resource));
    }
    public void loadAudio(Uri uri) {
        loadAudio(uri, DEFAULT_STREAM_TYPE);
    }
    public void loadAudio(Uri uri,  int streamType) {
        mPlayer.reset();
        // mPlayer.setOnCompletionListener(this);

        this.currentStreamType = streamType;
        mPlayer.setAudioStreamType(streamType);
        try {
            mPlayer.setDataSource(context, uri);
            mPlayer.prepare();
            currentTrackUri = uri;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void playAudio(int resource) {
        playAudio(resource, 0, AudioManager.STREAM_MUSIC );
    }
    private void playAudio(int resource, int offset, int streamType ) {
        playAudio(resourceToUri(resource), offset, streamType);
    }
    private void playAudio(Uri uri, int offset, int streamType ) {
        loadAudio(uri, streamType);
        mPlayer.seekTo(offset);
        mPlayer.start();
    }



    public void switchToStreamType( int streamType ) {
        this.currentStreamType = streamType;

        boolean isPlaying = mPlayer.isPlaying();
        int current = mPlayer.getCurrentPosition();


        try {

            if(isPlaying) {
                playAudio(this.currentTrackUri, current, streamType);
                //mPlayer.start();
            }
            else
            {
                mPlayer.stop();
                mPlayer.setAudioStreamType(currentStreamType);
                mPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public final void switchToEarpieces() {
        switchToStreamType(AudioManager.STREAM_VOICE_CALL);
    }
    public void switchToSpeaker() {
        switchToStreamType(AudioManager.STREAM_MUSIC);
    }



    public void pause() {
        mPlayer.pause();
    }
    public void play() {
        mPlayer.start();
    }
    public void stop() {
        mPlayer.stop();
        mPlayer.release();

        try {
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.seekTo(0);
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }




    @Override
    public void onSensorChanged(SensorEvent event) {

        long currentTime = System.nanoTime();

        if( lastScreenSwitchTime == 0 || currentTime - lastScreenSwitchTime < MIN_TIME_BETWEEN_SWITCH_SCREEN )
        {
            if (event.values[0] < event.sensor.getMaximumRange()) {
                if (mPlayer.isPlaying() && !wakeLock.isHeld()) {
                    this.switchToEarpieces();
                    screenOffPlaying = true;
                    wakeLock.acquire();
                }
            } else {
                if (wakeLock.isHeld()) {
                    this.switchToSpeaker();
                    screenOffPlaying = false;
                    wakeLock.release();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}
