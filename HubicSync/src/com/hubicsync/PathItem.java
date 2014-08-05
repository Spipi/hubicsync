package com.hubicsync;

public class PathItem {

	String path;
	String MD5Local;
	String MD5Server;
	String modified;
	String access_id;
	boolean sync;
	public PathItem(String path, String access_id, String MD5Local, String MD5Server, String modified, boolean sync){
		this.path = path;
		this.MD5Local = MD5Local;
		this.MD5Server = MD5Server;
		this.modified = modified;
		this.sync = sync;
		this.access_id = access_id;
	}
	public PathItem(String path, String access_id){
		this.path = path;
		this.access_id = access_id;
		this.MD5Local = " ";
		this.MD5Server = " ";
		this.modified = " ";
		this.sync = false;
		
	}
	public void downloadObject(String path){
		
		
	}
	public void updateDatabase(){
		
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getAccessID() {
		return access_id;
	}
	public void setAccessID(String access_id) {
		this.access_id = access_id;
	}
	public String getMD5Local() {
		return MD5Local;
	}
	public void setMD5Local(String mD5Local) {
		MD5Local = mD5Local;
	}
	public String getMD5Server() {
		return MD5Server;
	}
	public void setMD5Server(String mD5Server) {
		MD5Server = mD5Server;
	}
	public String getModified() {
		return modified;
	}
	public boolean getSync() {
		return sync;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public boolean isSync() {
		return sync;
	}
	public void setSync(boolean sync) {
		this.sync = sync;
	}
}
