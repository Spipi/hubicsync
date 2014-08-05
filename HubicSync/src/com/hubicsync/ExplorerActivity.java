package com.hubicsync;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

import com.hubicsync.R;
import com.hubicsync.MainActivity.NewAccountDialogFragment;

import database.AccessDatasource;
import database.BrowserDatasource;
import hubic.Access;
import hubic.Connection;
import hubic.OpenstackConnector;
import hubic.musicPlayer;
import hubic.musicPlayer.LocalBinder;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class ExplorerActivity extends ListActivity {
	String path="";
	private Access acc;
	private ExplorerListAdapter adapter;
	private String[] mFileList;
	private String mChosenFile;

	 private musicPlayer mp3Service;
	   private ServiceConnection mp3PlayerServiceConnection = new ServiceConnection() {
	        @Override
	        public void onServiceConnected(ComponentName arg0, IBinder binder) {
	            mp3Service =  ((LocalBinder) binder).getService();
	            System.out.println("biiiinding");
	 
	        }
	        
	 
	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	 
	        }

			
		
	 
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explorer);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container1, new PlaceholderFragment()).commit();
		}
		Bundle bundle = getIntent().getExtras(); 
		 if(bundle.get("PATH")!=null)
			 path = bundle.getString("PATH");
		 if(bundle.get("ACCESS")!=null)
	        {
			 
			acc = (Access) bundle.get("ACCESS");
			this.setTitle(acc.getName());
	        startService(new Intent(this, musicPlayer.class));
	        //bind to our service by first creating a new connectionIntent
	        
	  
	        Intent connectionIntent = new Intent(this, musicPlayer.class);
	        bindService(connectionIntent, mp3PlayerServiceConnection,
	                Context.BIND_AUTO_CREATE);
			fill (path);

	    

	        }
	}
	public ArrayList<ItemExplorer> populateDB(){
		final ArrayList<ItemExplorer> dir1= new ArrayList<ItemExplorer>();
		final BrowserDatasource bds = new BrowserDatasource(this);
		Thread t = new Thread() {
			@Override
			public void run() {
			OpenstackConnector osc = new OpenstackConnector(acc);
			osc.setRoot("default");
			bds.open();
			try {
					ArrayList<String> dir = osc.listFolder(path);
					if (dir == null){
						acc.refreshAccessToken();
						acc.retrieveOpenStackAccessToken();
						AccessDatasource ads = new AccessDatasource(getApplicationContext());
						ads.open();
						ads.addAccount(acc);
						osc = new OpenstackConnector(acc);
						osc.setRoot("default");
						dir = osc.listFolder(path);
					}
					for (int i= 0; i<dir.size(); i++){
						if(!((path.equals("" )&& (dir.get(i).toString().equals(".ovh/") || dir.get(i).toString().equals(".ovhPub/") )||dir.get(i).toString().equals(".thumbnails.hubic/")))){
						PathItem pi ;
						
						if((pi=bds.getAccount(dir.get(i).toString(), acc.getId().toString()))==null){
							pi =new PathItem(dir.get(i).toString(),acc.getId().toString());
							bds.addAccount(pi);
						}
						String name;
						if(dir.get(i).endsWith("/")){
							String [] names = dir.get(i).split("/");
							name=names[names.length-1]+"/";
							dir1.add(new ItemExplorer(path+name,name,"diretory_icon", pi.getSync()));
						}
						else{
							String [] names = dir.get(i).split("/");
							name=names[names.length-1];
							dir1.add(new ItemExplorer(path+name,name,"file_icon",pi.getSync()));
						}
						
						}
					}
					Collections.sort(dir1);
					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				dir1.addAll(getFromDB());
				Collections.sort(dir1);
				e.printStackTrace();
			}
			}
		};
		t.start();
		try {
			t.join();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dir1;
	}
	
	public ArrayList<ItemExplorer> getFromDB(){
		ArrayList<ItemExplorer> dir1= new ArrayList<ItemExplorer>();
		BrowserDatasource bds = new BrowserDatasource(this);
		bds.open();

		List<PathItem> dir = bds.getChildrenOf(new PathItem(path, acc.getId().toString()));
		
		for (int i= 0; i<dir.size(); i++){
			
			PathItem pi = dir.get(i);
			
		
			
			String name;
			if(dir.get(i).getPath().endsWith("/")){
				String [] names = dir.get(i).getPath().split("/");
				name=names[names.length-1]+"/";
				dir1.add(new ItemExplorer(path+name,name,"diretory_icon",pi.getSync()));
			}
			else{
				String [] names = dir.get(i).getPath().split("/");
				name=names[names.length-1];
				dir1.add(new ItemExplorer(path+name,name,"file_icon",pi.getSync()));
			}
			
		}
		return dir1;
	}
	public void fill(final String path){
		ArrayList<ItemExplorer> dir1=populateDB();
		adapter = new  ExplorerListAdapter (this,R.layout.fragment_explorer,dir1);
	    setListAdapter(adapter);

		
	}
	
	
	
	
	

    private String getMD5(File f) throws NoSuchAlgorithmException, IOException{
    	MessageDigest md = MessageDigest.getInstance("MD5");

		int byteArraySize = 2048;
	

	
		InputStream is = new FileInputStream(f);
		md.reset();
		byte[] bytes = new byte[byteArraySize];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			md.update(bytes, 0, numBytes);
		}
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
          sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
		return sb.toString();
	}
	
	public void onListItemClick(ListView l, View v, final int position, long id) {
		
        if(adapter.getItem(position).getName().endsWith("/")){
	      //  Intent intent = new Intent(this, ExplorerActivity.class);
	      path = adapter.getItem(position).getPath();
	      /*  intent.putExtra("ACCESS", acc);
	        intent.putExtra("PATH", adapter.getItem(position).getPath());
	       	startActivity(intent);*/
	      fill(path);
        }
        else{
        	final BrowserDatasource ad = new BrowserDatasource(this);
        	ad.open();
        	final Context ct = this;
        	final PathItem pi= ad.getAccount(adapter.getItem(position).getPath(), acc.getId()+"");
        	CharSequence choices[] = null;
        	if(adapter.getItem(position).getName().toLowerCase().endsWith("mp3") || adapter.getItem(position).getName().toLowerCase().endsWith("ogg"))
        		choices = new CharSequence[] {"Stream","Download", "Sync on my phone"};
        	else 
        		choices = new CharSequence[] {"Download", "Sync on my phone"};
        	final CharSequence colors[] = choices;
        	if(pi.getSync())
        		colors[colors.length-1]= "Stop syncing";
        	
        	
        	adapter.getItem(position).getPathItem();
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("");
        	builder.setItems(colors, new DialogInterface.OnClickListener() {
        	    @Override
        	    public void onClick(DialogInterface dialog, int which) {
        	        // the user clicked on colors[which]
        	    	if(colors[which].equals("Download")){
        	    	

        	    		Thread t = new Thread() {
        	    			@Override
        	    			public void run() {
	        	    			OpenstackConnector osc = new OpenstackConnector(acc);
	        	    			osc.setRoot("default");
	        	    			Header[] hd =osc.DownloadFile(adapter.getItem(position).getPath(),pi.getModified());
	        	    			if(hd!=null)
	        	    			for(int i = 0 ; i< hd.length; i++){
	        	    				
	        	    				if(hd[i].getName().equals("Last-Modified")){
	        	    					
	        	    					try {
	        	    						pi.setModified(hd[i].getValue());
											pi.setMD5Local(getMD5(new File(acc.getLocalStorage()+pi.getPath())));
											ad.addAccount(pi);
										} catch (NoSuchAlgorithmException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
	        	    					
        	
	        	    				}
	        	    			}
        	    			}
        	    		};
        	    		t.start();
        	    		try {
        					t.join();
        				} catch (InterruptedException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        	    		
        	    	}
        	    	else if(colors[which].equals("Sync on my phone")){
        	    		pi.setSync(true);
        	    		ad.addAccount(pi);
        	    		
        	    	}
        	    	else if(colors[which].equals("Stop syncing")){
        	    		pi.setSync(false);
        	    		ad.addAccount(pi);
        	    		
        	    	}
        	    	else if(colors[which].equals("Stream")){
        	    	
        	    		TextView tv = (TextView) findViewById(R.id.editText1);
        	    		tv.setText(adapter.getItem(position).getName());
        	    		mp3Service.stream( acc, "default",ct,adapter.getItem(position).getPath());
        	    		
        	    	}
        	    		
        	    	
        	    }
        	});
        	builder.show();
        	
        	
        	
        	
        }
        
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.explorer, menu);
		return true;
	}
	private static final int FILE_SELECT_CODE = 0;

	private void showFileChooser() {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("*/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);

	    try {
	        startActivityForResult(
	                Intent.createChooser(intent, "Select a File to Upload"),
	                FILE_SELECT_CODE);
	    } catch (android.content.ActivityNotFoundException ex) {
	        // Potentially direct the user to the Market with a Dialog
	        Toast.makeText(this, "Please install a File Manager.", 
	                Toast.LENGTH_SHORT).show();
	    }
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	        case FILE_SELECT_CODE:
	        if (resultCode == RESULT_OK) {
	            // Get the Uri of the selected file 
	            Uri uri = data.getData();
	            Log.d("bla", "File Uri: " + uri.toString());
	            // Get the path
	          
				try {
					final String  path2 = getPath(this, uri);
					final File f = new File(path2);
					
					//uploading
					Thread t = new Thread() {
		    			@Override
		    			public void run() { 
		    				Connection conn = new Connection();
		    				List<NameValuePair> header = new ArrayList<NameValuePair>(1);
		    				header.add(new BasicNameValuePair("X-Auth-Token", acc.getOpenStackAccessToken()));
		    				conn.putFile(acc.getOpenStackUrl()+"/"+"default"+"/"+path+f.getName(),header,path2);
		    			}
		    		};
		    		t.start();
		    		try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Log.d("vla", "File Path: " + path);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            // Get the file instance
	            // File file = new File(path);
	            // Initiate the upload
	        }
	        break;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	public static String getPath(Context context, Uri uri) throws URISyntaxException {
	    if ("content".equalsIgnoreCase(uri.getScheme())) {
	        String[] projection = { "_data" };
	        Cursor cursor = null;

	        try {
	            cursor = context.getContentResolver().query(uri, projection, null, null, null);
	            int column_index = cursor.getColumnIndexOrThrow("_data");
	            if (cursor.moveToFirst()) {
	                return cursor.getString(column_index);
	            }
	        } catch (Exception e) {
	            // Eat it
	        }
	    }
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	} 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_add_file) {
			showFileChooser();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//media player
	
    public void sendPause(View view) {
        // Do something in response to button
    	Button b = (Button) view.findViewById(R.id.button1);
    	b.setBackgroundResource(R.drawable.play);
    	mp3Service.pauseSong(this);
    	
    	
    }


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
		View rootView=null;
		//= inflater.inflate(R.layout.fragment_explorer,
			//		container, false);
			return rootView;
		}
	}

}
