package service;

import hubic.Access;
import hubic.OpenstackConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.http.Header;

import utils.Connectivity;

import com.hubicsync.PathItem;
import com.hubicsync.conflictFile;

import database.AccessDatasource;
import database.BrowserDatasource;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Pair;

public class HubicSyncService extends Service {
	ThreadService t;
	public static boolean servicerun=false;
	public static boolean con = true;
	// TODO Auto-generated method stub
     
    	 
	   class ThreadService extends Thread {
		private SharedPreferences sharedPref;
		private Connectivity connect;
		private BrowserDatasource bd;
		private AccessDatasource ad;
		   public ThreadService(AccessDatasource ad,BrowserDatasource bd,SharedPreferences sharedPref,Connectivity connect){
			   this.ad = ad;
			   this.bd = bd;
			   this.sharedPref = sharedPref;
			   this.connect = connect;
		   }
		   
		   
    		 public void stopT(){
			   con = false;
		   }
 			@Override
 			public void run() {
 				bd.open();
 	    	   ad.open();
 				while(con){
 			        
 			        Boolean syncConnPref = sharedPref.getBoolean("pref_sync_over_mobile", false);
 			        Integer tp = new Integer (sharedPref.getString("pref_sync_frequency", "2"));
 			       if(tp==-1)
 		      			break;
 			        if(!(!syncConnPref && connect.isConnectedMobile())){
  	  //listing access
  	   List<Access> acs = ad.getAllAccounts();
  	   //extracting folder to sync
  	   List<PathItem> pifs = bd.getAllPathItemsFolderToSync();
      	//select access
  	   for(int i = 0; i<pifs.size();i++){
		   Access ac =null;
	  		   for(int j= 0; j<acs.size(); j++){
	  			   
	  			   
	  			   if(acs.get(j).getId().toString().equals(pifs.get(i).getAccessID())){
	  				   ac = acs.get(j);
	  				   break;
	  			   }
	  		   }
	  		   if(ac!=null){
	  			 setFolderToSync(pifs.get(i),  ac,ad,bd);
	  		   }
      	   
	  		   
  	   }
  	   
  	   //listing files to sync
  	   
  	   List<PathItem> pis = bd.getAllPathItemsToSync();

  	   
  	   
  	   for(int i = 0; i<pis.size() && con;i++){
  		   Access ac =null;
  		   Date date = new Date();
  		   
  		   if(!pis.get(i).getPath().endsWith("/")){
		  		   //select access
  			   
		  		   for(int j= 0; j<acs.size(); j++){
		  			   
		  			   
		  			   if(acs.get(j).getId().toString().equals(pis.get(i).getAccessID())){
		  				   ac = acs.get(j);
		  				   break;
		  			   }
		  		   }
		  		   if(ac!=null){
		  			   //if locally not modified
		  			   
		  			 
		  			   OpenstackConnector oc = new OpenstackConnector(ac);
		  			   oc.setRoot("default");
		  			   Pair<Header[], Integer> ret = oc.GetDetails(pis.get(i).getPath(),pis.get(i).getModified());
		
		  			   if(ret!=null){
		
		  			   if(ret.second.equals(new Integer("401"))){
		  				   
		  				   	ac.refreshAccessToken();
							try {
								ac.retrieveOpenStackAccessToken();
								ad.addAccount(ac);
								ret = oc.GetDetails(pis.get(i).getPath(),pis.get(i).getModified());
				      			  
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
		  			   }
		  			   Header[] hd = ret.first;
		  			   String modified="";
		  			   String md5="";
		  			   
		  			   for (int k = 0; k< hd.length; k++){
		  				   System.out.println("header"+hd[k].getName());
			    				if(hd[k].getName().equals("Last-Modified")){
			    		
			    					modified = hd[k].getValue();
			    					
			    				}
			    				else if(hd[k].getName().equals("ETag")){
			    		
			    					md5 = hd[k].getValue();
			    					
			    				}
		  			   }
		  			 System.out.println("access"+modified+ret.second);
		  			   if(!modified.equals("") || pis.get(i).getModified().equals("new")){
		  				 
		  				   File f = new File(ac.getLocalStorage()+pis.get(i).getPath());
		
							if( (pis.get(i).getModified().equals(modified)|| modified.equals("") &&  pis.get(i).getModified().equals("new"))&& f.exists()){
								//distant file is already there
		       			   
		
								try {
								if(!getMD5(f).equals(pis.get(i).getMD5Local())){
									//local file has changed
				
									conflictFile cf = new conflictFile();
									cf.write(date.toString()+": uploading : "+pis.get(i).getPath());
									Header[] h = oc.UploadFile(pis.get(i).getPath(), ac.getLocalStorage()+pis.get(i).getPath());
									if(h!=null){
										System.out.println("updated");
										pis.get(i).setMD5Local(getMD5(f));
										for (int k1 = 0; k1< h.length; k1++){
											if(h[k1].getName().equals("Last-Modified"))
												pis.get(i).setModified(h[k1].getValue());
										}
										bd.addAccount(pis.get(i));
									}
									
								}
							} catch (NoSuchAlgorithmException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
								
								
							}
							else{
								
								try {
								if(!f.exists()||getMD5(f).equals(pis.get(i).getMD5Local())){
									System.out.println("ddooownloaaaadddiiiing");//
									conflictFile cf = new conflictFile();
									cf.write(date.toString()+": downloading : "+pis.get(i).getPath());
									Header[] h = oc.DownloadFile(pis.get(i).getPath(), pis.get(i).getModified());
									if(h!=null){
										pis.get(i).setMD5Local(getMD5(f));
										for (int k1 = 0; k1< h.length; k1++){
											if(h[k1].getName().equals("Last-Modified"))
												pis.get(i).setModified(h[k1].getValue());
										}
										bd.addAccount(pis.get(i));
									}
									   
								
								
								}
								else{//conflict
									if(f.exists()&&getMD5(f).equals(md5)){
										//conflict check
										pis.get(i).setMD5Local(md5);
										pis.get(i).setModified(modified);
										bd.addAccount(pis.get(i));
									}
									else{
										conflictFile cf = new conflictFile();
										cf.write(date.toString()+": conflict : "+pis.get(i).getPath());
									}
									
								}
							} catch (NoSuchAlgorithmException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							}
		
		  			   System.out.println(hd);
		  			   }
		  			   }
		  		   }
		  	   }
  	   }
		 	}
		 			        else
		 	
		 			        	System.out.println("no via 3G");
		  	   try {
		  		   System.out.println("waiting "+tp*60+"s");
		  		
				Thread.sleep(tp*60*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 			}
 			        
 				}
 
 			};
	public HubicSyncService() {
		super();

		// TODO Auto-generated constructor stub
	}

	public void listf(String directoryName, ArrayList<File> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    if(fList!=null)
	    for (File file : fList) {
	        files.add(file);
	        if (file.isDirectory()) {

	            listf(file.getAbsolutePath(), files);
	        }
	    }
	}
	
	public void onDestroy(){
		if(t!=null){
			t.interrupt();
			t.stopT();
			try {
				t.join();
				servicerun=false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void setFolderToSync(PathItem folder, Access ac, AccessDatasource ad, BrowserDatasource bd){
		//first list distant and add new pathitems with sync attribute
		OpenstackConnector oc = new OpenstackConnector(ac);
		oc.setRoot("default");
		
		//listing distant folder
		ArrayList<String> f;
		try {
			f = oc.listFolderAndSubs(folder.getPath());
			System.out.println("geetting"+folder.getPath());
			if(f==null){
				ac.refreshAccessToken();
				try {
					ac.retrieveOpenStackAccessToken();
					ad.addAccount(ac);
					f = oc.listFolderAndSubs(folder.getPath());
		  			  
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (f!=null){
				
				 
		      	for(int i = 0; i<f.size();i++){
		      		PathItem pi =null ;
		      		System.out.println(f.get(i));
			      		if((pi=bd.getAccount(f.get(i), ac.getId().toString()))==null){
			      			//not in BD
			      			pi =new PathItem(f.get(i).toString(),ac.getId().toString());
			      			if(!f.get(i).endsWith("/"))
			      				pi.setSync(true);
							bd.addAccount(pi);
			      		}
			      		else{
			      			if(!pi.getSync()){
			      				pi.setSync(true);
			      				bd.addAccount(pi);
			      			}
			      		}
		      		
		      	}
		      	
		    }
			//now dealing with local files
	      	ArrayList<File> files = new ArrayList<File>();
	      	listf(ac.getLocalStorage()+folder.getPath(), files);
	      	for(int i = 0; i<files.size();i++){
	      		if(files.get(i).isFile()){
		      		PathItem pi =null ;
		      		String path = files.get(i).getAbsolutePath().substring(ac.getLocalStorage().length());
		      		System.out.println(path);
		      		if((pi=bd.getAccount(path, ac.getId().toString()))==null){
		      			//not in BD
		      			pi =new PathItem(path,ac.getId().toString());
		      			pi.setSync(true);
		      			pi.setModified("new");
						bd.addAccount(pi);
		      		}
	      		}
	      		
	      	}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	 @Override
	  public void onCreate() {
		 final AccessDatasource ad = new AccessDatasource(this);
		 final BrowserDatasource bd = new BrowserDatasource(this);
		 final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		 final Connectivity connect = new Connectivity(this);
     			t = new ThreadService(ad,bd,sharedPref,connect);
     			servicerun = true;
     			t.start();
     			
      	
         
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

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
