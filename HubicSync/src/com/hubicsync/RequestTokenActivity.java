package com.hubicsync;

import com.hubicsync.R;

import database.AccessDatasource;
import hubic.Access;
import hubic.OpenstackConnector;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.os.Build;

public class RequestTokenActivity extends Activity {
	private WebView webView;
	private Access acc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		// Get the message from the intent
		Bundle bundle = getIntent().getExtras();

        if(bundle.getString("NAME")!= null)
        {
        	acc = new Access(bundle.getString("NAME"));
    		webView = (WebView) findViewById(R.id.webView1);
    		
    		RequestTokenThread rtt = new RequestTokenThread(webView, this);
    		//new Thread(rtt).start();
    		webView.setWebViewClient(new WebViewClient(){
    			
    			
    			
    			
    			public boolean shouldOverrideUrlLoading(WebView view, String url)
    			   {
   
    			         // This line we let me load only pages inside Firstdroid Webpage
    				  if(url!=null){
    						Uri uri=Uri.parse(url);
    						System.out.println(uri.getHost());
    						if(uri.getHost().contains("localhost")){

    							setAccessCode(uri.getQueryParameter("code"));
    						}
    					}
    			    return false;    
    			   // Return true to override url loading (In this case do nothing).
    		
    			    }
    		}
    		
    				
    				
    				);
    		webView.getSettings().setJavaScriptEnabled(true);
    		webView.loadUrl(acc.getRequestTokenURL());
        	
        }
		
	}

	public void setAccessCode(final String code){
		ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
		final AccessDatasource ads = new AccessDatasource(this.getApplicationContext());
		ads.open();
		Thread t = new Thread() {
			@Override
			public void run() {
				acc.retrieveRefreshAndAccessToken(code);
				try {
					acc.retrieveOpenStackAccessToken();
					


					acc.setId(ads.addAccount(acc).getId());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
			Intent intent = new Intent(this, ExplorerActivity.class);
	          
            intent.putExtra("ACCESS", acc);
           	startActivity(intent);
			dialog.dismiss();
			this.finish();
		
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
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
			View rootView = inflater.inflate(R.layout.fragment_display_message,
					container, false);
			return rootView;
		}
	}

}
