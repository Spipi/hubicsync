package hubic;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.AccessDatasource;
import android.webkit.WebView;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
public class Access implements Serializable{
	private Long id;
	private String client_id;
	private String client_secret;
	private String api_url;
	private String refresh_token;
	private String access_token;
	private String openstack_access_token;
	private String openstack_access_token_expiration;
	private String openstack_url;
	private String expiration;
	private String type = "hubic";
	private String name;
	
	public Access(String name){
		this.name=name;
		
		client_id = "api_hubic_SJ2ajWvwfEbiwDu0zg3RQm2GmOYR7F89";
		client_secret= "UKkvvZJkOFyPzvVIxqTYNQ4KfY4ygUTVhMut4Jy564RmjY4WQCOi5kWpKmnKQRlA";
		api_url = "https://api.hubic.com/oauth/auth/?";
	}
	public String getRequestTokenURL(){
		SecureRandom random = new SecureRandom();

		String rand = new BigInteger(130, random).toString(32);
		
		String url = "https://api.hubic.com/oauth/auth/?client_id="+client_id+"&redirect_uri=http://localhost:8000/&scope=usage.r,account.r,getAllLinks.r,credentials.r,activate.w,links.drw&response_type=code&state=RandomString_"+rand;

	
		
		return url;
	
	}
	public void connect(String account){
		
	}
	public String getOpenStackAccessToken(){
		
		return openstack_access_token;
	}
	public String getOpenStackUrl(){
		return openstack_url;
	}
	public void retrieveOpenStackAccessToken() throws Exception{
		if(access_token != null){
			Connection conn = new Connection();
			System.out.println("Accesstoken"+access_token);
			List<NameValuePair> header = new ArrayList<NameValuePair>(1);
			header.add(new BasicNameValuePair("Authorization", "Bearer "+access_token));
		   	String result = conn.sendGet("https://api.hubic.com/1.0/account/credentials", header);
		   	System.out.println("Authorization"+ "Bearer "+access_token+result);
			JSONObject object = new JSONObject(result);
			openstack_access_token= object.get("token").toString();
			openstack_url= object.get("endpoint").toString();
			System.out.println("retrieveOpenStackAccessToken "+openstack_url);
			openstack_access_token_expiration= object.get("expires").toString();
		
		}
		
	}
	public void retrieveAccessToken(){
		if(refresh_token!=null){

			
		}
		
	}
	
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClient_secret() {
		return client_secret;
	}
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
	public String getApi_url() {
		return api_url;
	}
	public void setApi_url(String api_url) {
		this.api_url = api_url;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getOpenstack_access_token() {
		return openstack_access_token;
	}
	public void setOpenstack_access_token(String openstack_access_token) {
		this.openstack_access_token = openstack_access_token;
	}
	public String getOpenstack_access_token_expiration() {
		return openstack_access_token_expiration;
	}
	public void setOpenstack_access_token_expiration(String openstack_access_token_expiration) {
		this.openstack_access_token_expiration = openstack_access_token_expiration;
	}
	public String getOpenstack_url() {
		return openstack_url;
	}
	public void setOpenstack_url(String openstack_url) {
		this.openstack_url = openstack_url;
	}
	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	public void setAccessToken(String access_token){
		this.access_token = access_token;
		
	}
	public void refreshAccessToken(){
		byte[] data;
		try {
			data = new String(client_id+":"+client_secret).getBytes("UTF-8");
			
			String encod = "Basic "+ Base64.encodeToString(data, Base64.NO_WRAP);
			//String encod ="Basic "+ Base64.encode((new String("api_hubic_1366206728U6faUvDSfE1iFImoFAFUIfDRbJytlaY0:gXfu3KUIO1K57jUsW7VgKmNEhOWIbFdy7r8Z2xBdZn5K6SMkMmnU4lQUcnRy5E26")).getBytes(), Base64.DEFAULT).toString();
			Connection conn = new Connection();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			System.out.println(encod);
			List<NameValuePair> header = new ArrayList<NameValuePair>(2);
			header.add(new BasicNameValuePair("Authorization", encod));
		   	header.add(new BasicNameValuePair("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"));
		    nameValuePairs.add(new BasicNameValuePair("refresh_token",refresh_token));
		    nameValuePairs.add(new BasicNameValuePair("grant_type","refresh_token"));
		    String rep = conn.sendPost("https://api.hubic.com/oauth/token/%20HTTP/1.1", header,nameValuePairs);
			JSONObject object = new JSONObject(rep);
			this.access_token= object.get("access_token").toString();
			this.expiration= object.get("expires_in").toString();
			System.out.println("fuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu"+rep);
			
		
		 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void retrieveRefreshAndAccessToken(String code){
		byte[] data;
		try {
			data = new String(client_id+":"+client_secret).getBytes("UTF-8");
			
			String encod = "Basic "+ Base64.encodeToString(data, Base64.NO_WRAP);
			//String encod =ActivityActivity"Basic "+ Base64.encode((new String("api_hubic_1366206728U6faUvDSfE1iFImoFAFUIfDRbJytlaY0:gXfu3KUIO1K57jUsW7VgKmNEhOWIbFdy7r8Z2xBdZn5K6SMkMmnU4lQUcnRy5E26")).getBytes(), Base64.DEFAULT).toString();
			Connection conn = new Connection();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			System.out.println(encod);
			List<NameValuePair> header = new ArrayList<NameValuePair>(2);
			header.add(new BasicNameValuePair("Authorization", encod));
		   	header.add(new BasicNameValuePair("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"));
		   	nameValuePairs.add(new BasicNameValuePair("code",code));
		    nameValuePairs.add(new BasicNameValuePair("redirect_uri","http%3A%2F%2Flocalhost:8000%2F"));
		    nameValuePairs.add(new BasicNameValuePair("grant_type","authorization_code"));
		    System.out.println("before req");
		    String rep = conn.sendPost("https://api.hubic.com/oauth/token/%20HTTP/1.1", header,nameValuePairs);
		    System.out.println("after req");
			JSONObject object = new JSONObject(rep);
			
			this.access_token= object.get("access_token").toString();
			this.refresh_token= object.get("refresh_token").toString();
			this.expiration= object.get("expires_in").toString();
			System.out.println(this.expiration);
			
		
		 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getLocalStorage() {
		// TODO Auto-generated method stub
		return "/sdcard/hubic/"+name+"/";
	}
	public String getLocalTmp() {
		// TODO Auto-generated method stub
		return "/sdcard/hubic/tmp/";
	}
	
	
}
