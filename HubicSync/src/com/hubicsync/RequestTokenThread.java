package com.hubicsync;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.net.Uri;
import android.webkit.WebView;

public class RequestTokenThread implements Runnable{
	WebView wv;
	RequestTokenActivity activity;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean cont = true;
		while(cont){
			System.out.println("houuua");
			if(wv.getUrl()!=null){
				Uri uri=Uri.parse(wv.getUrl());
				System.out.println(uri.getHost());
				if(uri.getHost().contains("localhost")){
					cont=false;
					activity.setAccessCode(uri.getQueryParameter("code"));
				}
			}
			
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public  RequestTokenThread(WebView wv,  RequestTokenActivity requestTokenActivity){
		this.wv=wv;
		this.activity = requestTokenActivity;
	}
}
