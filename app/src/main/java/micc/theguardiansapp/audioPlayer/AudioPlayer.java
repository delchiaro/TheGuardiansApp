package micc.theguardiansapp.audioPlayer;

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

import micc.theguardiansapp.R;

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
    private PowerManager.WakeLock wakeLockProximityScreenOff;
    private PowerManager.WakeLock wakeLockPreventScreenOff;

    private boolean screenOffPlaying = false;
    private long lastScreenSwitchTime = 0;
    private final int MIN_TIME_BETWEEN_SWITCH_SCREEN = 3000;

    private AudioPlayerListener listener = null;

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
        }
        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLockProximityScreenOff = powerManager.newWakeLock(field, "HeroAudioPlayer");

        wakeLockPreventScreenOff = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

        speakerPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
                if(listener != null)
                    listener.onCompletion();
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
                "://" + res.getResourcePackageName(resource) +
                '/' + res.getResourceTypeName(resource) +
                '/' + res.getResourceEntryName(resource) );

        return uri;


    }



    public void setAudioPlayerListener(AudioPlayerListener listener) {
        this.listener = listener;
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
        play();
    }



    public final void switchPlayer() {
        int seek = activePlayer.getCurrentPosition();

        activePlayer.stop();
        try {
            activePlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaPlayer m = activePlayer;
        activePlayer = notActivePlayer;
        notActivePlayer = m;

        activePlayer.seekTo(seek);
        activePlayer.start();
    }



    public void pause() {
        activePlayer.pause();
        if(wakeLockPreventScreenOff.isHeld())
            wakeLockPreventScreenOff.release();

        if(listener!=null)
            listener.onPaused();
    }
    public void play() {
        activePlayer.start();
        wakeLockPreventScreenOff.acquire();
    }
    public void stop() {
        if(wakeLockPreventScreenOff.isHeld())
           wakeLockPreventScreenOff.release();
        activePlayer.stop();
        try {
            activePlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        activePlayer.seekTo(0);

        if(listener!=null)
            listener.onStopped();
    }

    public boolean isPlaying() {
        return activePlayer.isPlaying();
    }




    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY)
        {

            if (event.values[0] < event.sensor.getMaximumRange())
            {
                if (speakerPlayer.isPlaying() && !wakeLockProximityScreenOff.isHeld())
                {
                    this.switchPlayer();
                    screenOffPlaying = true;
                    if(wakeLockPreventScreenOff.isHeld())
                        wakeLockPreventScreenOff.release();
                    wakeLockProximityScreenOff.acquire();
                }
            }
            else
            {
                if (wakeLockProximityScreenOff.isHeld())
                {
                    this.switchPlayer();
                    screenOffPlaying = false;
                    wakeLockProximityScreenOff.release();
                    if(!wakeLockPreventScreenOff.isHeld())
                        wakeLockPreventScreenOff.acquire();
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}
