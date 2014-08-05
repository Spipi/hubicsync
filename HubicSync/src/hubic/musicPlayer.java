package hubic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;




public class musicPlayer extends Service implements OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer mMediaPlayer = new MediaPlayer();
    Intent intent;
    Access acc;
	String root;
	Context ct;
	
    public final IBinder localBinder = new LocalBinder();
    
    @Override
    public void onCreate() {
 
    }

    public void initMediaPlayer() {

    	mMediaPlayer.setOnErrorListener(this);
    }
    public musicPlayer(){
    	super();

		// TODO Auto-generated constructor stub
	
    }
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
	    // ... react appropriately ...
	    // The MediaPlayer has moved to the Error state, must be reset!
		return false;
	}
	@Override
	   public void onDestroy() {
	       if (mMediaPlayer != null) mMediaPlayer.release();
	   }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }


	public void playSong(Context c) {

            this.mMediaPlayer .start();
         
    }
	public int getCurrentPosition(){
		
		return mMediaPlayer.getCurrentPosition();
	}
    public void pauseSong(Context c) {
    	if(mMediaPlayer.isPlaying())
    		this.mMediaPlayer.pause();
    	else
    		 this.mMediaPlayer .start();
    }
	public void stream (Access acc, String root, Context ct, String path){
		this.acc= acc;
		this.root= root;
		this.ct = ct;
		Connection conn = new Connection();
		HashMap<String,String> header = new HashMap<String,String>();
		header.put("X-Auth-Token", acc.getOpenStackAccessToken());

		String url = acc.getOpenStackUrl()+"/"+root+"/"+path; // your URL here
		
		
		try {

			
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		
			mMediaPlayer.release();
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(this,Uri.parse(url), header	);
    		mMediaPlayer.prepare(); // might take long! (for buffering, etc)
    		mMediaPlayer.start();
    		String songName;
    		// assign the song name to songName
    		
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	public class LocalBinder extends Binder{
        public musicPlayer getService(){
            return musicPlayer.this;
        }
    }
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return localBinder;
	}

	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return mMediaPlayer.isPlaying();
	}
}

