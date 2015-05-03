package micc.theguardiansapp.audioPlayer;

/**
 * Created by nagash on 02/05/15.
 */
public interface AudioPlayerListener
{
    public void onCompletion(boolean inEarpieceMode);
    public void onPaused();
    public void onStopped();
}
