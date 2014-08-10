package hubic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.hubicsync.ItemExplorer;
import com.hubicsync.R;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.TextView;




public class musicPlayer extends Service implements OnPreparedListener, OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY = "com.example.action.PLAY";
    MediaPlayer mMediaPlayer = new MediaPlayer();
    Intent intent;
    Access acc;
	String root;
	Context ct;
	int current;
	ArrayList<ItemExplorer> plist;


    public final IBinder localBinder = new LocalBinder();
    
    @Override
    public void onCreate() {
 
    }

    public void initMediaPlayer() {

    	mMediaPlayer.setOnErrorListener(this);
    }
    public musicPlayer(){
    	super();
    	current = 0;
    	plist = new ArrayList<ItemExplorer>();
		// TODO Auto-generated constructor stub
	
    }
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
	    // ... react appropriately ...
		playNext();
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
    public void playNext(){
    	int lastCurrent = current;
    	System.out.println("tesst"+current);
    	current++;
    	while(current<plist.size() && !plist.get(current).getPath().toLowerCase().endsWith("mp3") && !plist.get(current).getPath().toLowerCase().endsWith("ogg") && !plist.get(current).getPath().toLowerCase().endsWith("flac")){
    		current++;
    		System.out.println(current);
    	}
    	if(lastCurrent != current && current<plist.size())
    		stream(current);
    	else{
        	System.out.println("bef"+current);

    		current--;
        	System.out.println("aft"+current);

        	if(mMediaPlayer.isPlaying())
        		this.mMediaPlayer.pause();
    	}
    } 
    public void playPrevious(){
    	current--;
    	while(plist.size()>0&&current>=0 && !plist.get(current).getPath().toLowerCase().endsWith("mp3") && !plist.get(current).getPath().toLowerCase().endsWith("ogg") && !plist.get(current).getPath().toLowerCase().endsWith("flac")){
    		current--;
    		
    	}
    	if(plist.size()>0&&current>=0)
    		stream(current);
    	else
    		pauseSong(ct);
    }
    public void setAcc(Access acc){
    	this.acc = acc;
    }
    public void setRoot(String root){
    	this.root = root;
    }
    public void setContext(Context ct){
    	this.ct =ct;
    }
    public void setPlist(ArrayList<ItemExplorer> dir1){
    	this.plist = dir1;
    }
	public void stream (int nb){

		current = nb;
		HashMap<String,String> header = new HashMap<String,String>();
		header.put("X-Auth-Token", acc.getOpenStackAccessToken());

		String url = acc.getOpenStackUrl()+"/"+root+"/"+plist.get(nb).getPath(); // your URL here
		
		
		try {

			
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		
			mMediaPlayer.release();
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			//check if file exists locally
			File f = new File(acc.getLocalStorage()+plist.get(nb).getPath());
			if (f.exists())
				mMediaPlayer.setDataSource(acc.getLocalStorage()+plist.get(nb).getPath());
			else
				mMediaPlayer.setDataSource(this,Uri.parse(url), header	);
    		mMediaPlayer.prepare(); // might take long! (for buffering, etc)
    		mMediaPlayer.start();
 
    		// assign the song name to songName
    		
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			playNext();
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			playNext();
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			playNext();
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			playNext();
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

	   public String getPlayed(){
		  if(current < plist.size() && (plist.get(current).getPath().toLowerCase().endsWith("mp3") || plist.get(current).getPath().toLowerCase().endsWith("ogg") || plist.get(current).getPath().toLowerCase().endsWith("flac")))
	    	return plist.get(current).getName();
		  else return " ";
	    }
	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		playNext();
	}
}

