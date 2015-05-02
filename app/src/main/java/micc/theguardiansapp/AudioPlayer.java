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

import java.io.IOException;

/**
 * Created by nagash on 01/05/15.
 */
public class AudioPlayer implements SensorEventListener
{

    private final static int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC ;
    //      AudioManager.STREAM_VOICE_CALL

    private int currentStreamType = DEFAULT_STREAM_TYPE;
    private MediaPlayer speakerPlayer;
    private MediaPlayer earpiecePlayer;

    private MediaPlayer activePlayer;
    private MediaPlayer notActivePlayer;

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
        speakerPlayer = new MediaPlayer();
        earpiecePlayer = new MediaPlayer();
        activePlayer = speakerPlayer;
        notActivePlayer = earpiecePlayer;

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
        speakerPlayer.setOnCompletionListener(listener);
    }

    public void loadAudio(int resource) {
        loadAudio(resourceToUri(resource));
    }
    public void loadAudio(Uri uri) {
        speakerPlayer.reset();
        speakerPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            speakerPlayer.setDataSource(context, uri);
            speakerPlayer.prepare();
            currentTrackUri = uri;
        } catch (IOException e) {
            e.printStackTrace();
        }


        earpiecePlayer.reset();
        earpiecePlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        try {
            earpiecePlayer.setDataSource(context, uri);
            earpiecePlayer.prepare();
            currentTrackUri = uri;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public void playAudio(int resource) {
        playAudio(resource, 0);
    }
    private void playAudio(int resource, int offset ) {
        playAudio(resourceToUri(resource), offset);
    }
    private void playAudio(Uri uri, int offset ) {
        loadAudio(uri);
        activePlayer.seekTo(offset);
        activePlayer.start();
    }



    public final void switchPlayer() {
        int seek = activePlayer.getCurrentPosition();
        stop();
        MediaPlayer m = activePlayer;
        activePlayer = notActivePlayer;
        notActivePlayer = m;

        activePlayer.seekTo(seek);
        activePlayer.start();

    }



    public void pause() {
        activePlayer.pause();
    }
    public void play() {
        activePlayer.start();
    }
    public void stop() {
        activePlayer.stop();

        try {
            activePlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        activePlayer.seekTo(0);
    }

    public boolean isPlaying() {
        return activePlayer.isPlaying();
    }




    @Override
    public void onSensorChanged(SensorEvent event) {

        long currentTime = System.nanoTime();

        if( lastScreenSwitchTime == 0 || currentTime - lastScreenSwitchTime < MIN_TIME_BETWEEN_SWITCH_SCREEN )
        {
            if (event.values[0] < event.sensor.getMaximumRange()) {
                if (speakerPlayer.isPlaying() && !wakeLock.isHeld()) {
                    this.switchPlayer();
                    screenOffPlaying = true;
                    wakeLock.acquire();
                }
            } else {
                if (wakeLock.isHeld()) {
                    this.switchPlayer();
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
