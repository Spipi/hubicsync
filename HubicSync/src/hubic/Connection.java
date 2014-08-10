package hubic;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.hubicsync.conflictFile;

import android.os.Environment;
import android.util.Pair;
 
public class Connection {


	 
		private final String USER_AGENT = "Mozilla/5.0";
	 

 
		// HTTP GET request
		public String[] sendGetWithErrorCode(String url,List<NameValuePair> header){
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet hg = new HttpGet(url);
			for(int i=0; i<header.size(); i++){
		    	hg.setHeader(header.get(i).getName().toString(), header.get(i).getValue().toString());
		    	
		    }
		    try {


		        HttpResponse response = httpclient.execute(hg);
		        String [] res =new  String[2];
		        res[0] = ""+ response.getStatusLine().getStatusCode();
		        res[1] = inputStreamToString(response.getEntity().getContent()).toString();
		        return res ;
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
		    return null;
			
		}
		public Pair<Header[], Integer> sendGetWithHeader(String url,List<NameValuePair> header){
			System.out.println("lalalalaconn");
			HttpClient httpclient = new DefaultHttpClient();
			URL urlU;
			String encodedUrl =url;
		
			HttpGet hg = new HttpGet(encodedUrl);
			System.out.println("lalalalaconn2");
			for(int i=0; i<header.size(); i++){
		    	hg.setHeader(header.get(i).getName().toString(), header.get(i).getValue().toString());
		    	
		    }
		    try {


		        HttpResponse response = httpclient.execute(hg);
		        String [] res =new  String[2];
		        
		        res[1] = inputStreamToString(response.getEntity().getContent()).toString();
		       return new Pair<Header[], Integer>(response.getAllHeaders(), new Integer(response.getStatusLine().getStatusCode()));
		        
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
		    return null;
			
		}
		public String sendGet(String url,List<NameValuePair> header) throws Exception {
			
			return sendGetWithErrorCode(url,header)[1];
			
	 
		}
	 
		// HTTP POST request
		public String sendPost(String url, List<NameValuePair> header, List<NameValuePair> nameValuePairs) throws Exception {
				System.out.println(url);
			 HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost(url);
			    for(int i=0; i<header.size(); i++){
			    	httppost.setHeader(header.get(i).getName().toString(), header.get(i).getValue().toString());
			    	
			    }
			    

			    try {
			        // Add your data
			       
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			        // Execute HTTP Post Request
			        HttpResponse response = httpclient.execute(httppost);
			        
			        return  inputStreamToString(response.getEntity().getContent()).toString();
			        
			    } catch (ClientProtocolException e) {
			        // TODO Auto-generated catch block
			    } catch (IOException e) {
			        // TODO Auto-generated catch block
			    }
			    return null;
		}
		private StringBuilder inputStreamToString(InputStream is) throws IOException {
		    String line = "";
		    StringBuilder total = new StringBuilder();
		    
		    // Wrap a BufferedReader around the InputStream
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		    // Read response until the end
		    while ((line = rd.readLine()) != null) { 
		        total.append(line+"\n"); 
		    }
		    
		    // Return full string
		    return total;
		}
		public Header[] putFile(String urlString, List<NameValuePair> header,  String path){
			HttpClient httpclient = new DefaultHttpClient();
			System.out.println("putting");
			File f = new File(path);
			HttpGet hgg = new HttpGet(urlString);
			
			HttpPut hg = new HttpPut(hgg.getURI());
			for(int i=0; i<header.size(); i++){
		    	hg.setHeader(header.get(i).getName().toString(), header.get(i).getValue().toString());
		    	
		    }
		
			System.out.println("putting2");
		    try {
		    	hg.setEntity(new FileEntity(f, "binary/octet-stream"));
		    	System.out.println("putting2.2");
		        HttpResponse response = httpclient.execute(hg);
		        System.out.println("putting3");
		       System.out.println("code :" +response.getStatusLine().getStatusCode());		      
		        return response.getAllHeaders() ;
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    } 
		    return null;
		}
		public Header[] downloadFile(String urlString, List<NameValuePair> header,  String path){
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet hg = new HttpGet(urlString);
			for(int i=0; i<header.size(); i++){
		    	hg.setHeader(header.get(i).getName().toString(), header.get(i).getValue().toString());
		    	
		    }
		    try {
		    	 
		    	
		        HttpResponse response = httpclient.execute(hg);
		       
		        InputStream inputStream = response.getEntity().getContent();
		       
		         int totalSize = (int)response.getEntity().getContentLength();
		        //variable to store total downloaded bytes
		        int downloadedSize = 0;
				
				//	path= "/sdcard/hubic/hubic/é/Documents/Textes/pensées/zemmour/soucis.txt";
			
				
		        File file = new File(path);
		        File tmp = file;
		       if(!response.getHeaders("content-type").equals("inode/directory")){
		        file.getParentFile().mkdirs();
		        
		        System.out.println(path+"creeating folders" + file.getParentFile().getAbsolutePath());
		        //this will be used to write the downloaded data into the file we created
		        FileOutputStream fileOutput = new FileOutputStream(file);
		        //create a buffer...
		        byte[] buffer = new byte[1024];
		        int bufferLength = 0; //used to store a temporary size of the buffer
		
		        //now, read through the input buffer and write the contents to the file
			        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
			                //add the data in the buffer to the file in the file output stream (the file on the sd card
			                fileOutput.write(buffer, 0, bufferLength);
			                //add up the size so we know how much is downloaded
			                downloadedSize += bufferLength;
			                
			
			        }
		       }
		       else
		    	   file.mkdirs();

		       
		        
		        return response.getAllHeaders() ;
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    	conflictFile cf = new conflictFile();
				cf.write(": proto exception : "+path);
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    	conflictFile cf = new conflictFile();
				cf.write(": io exception : "+path +e.toString());//
		    }
		    return null;
		}
		
	}

