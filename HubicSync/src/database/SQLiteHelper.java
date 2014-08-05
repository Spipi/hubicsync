package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper{
	//refresh_token
	//access_token
	//access_token_expiration
	//openstack_access_token
	//openstack_access_token_expiration
	//name
	//type
	
	
	
	
	/*Browser:
	 * 
	 * access_id
	 * path
	 * last_modify_server
	 * md5_server
	 * md5_local
	 * 
	 * 
	 * 
	 */
	  public static final String TABLE_ACCOUNTS = "accounts";
	  public static final String COLUMN_ID = "id";
	  public static final String COLUMN_ACCESS_TOKEN = "access_token";//
	  public static final String COLUMN_REFRESH_TOKEN = "refresh_token";//
	  public static final String COLUMN_ACCESS_TOKEN_EXPIRATION = "access_token_expiration";//
	  public static final String COLUMN_OPENSTACK_ACCESS_TOKEN = "openstack_access_token";//
	  public static final String COLUMN_OPENSTACK_URL = "openstack_url";
	  public static final String COLUMN_OPENSTACK_ACCESS_TOKEN_EXPIRATION = "openstack_access_token_expiration";
	  public static final String COLUMN_NAME = "name";
	  public static final String COLUMN_TYPE = "type";
	  
	  
	  
	  public static final String TABLE_BROWSER = "browser";
	  public static final String COLUMN_ACCESS_ID = "access_id";
	  public static final String COLUMN_PATH = "path";//
	  public static final String COLUMN_LAST_MODIFY_SERVER = "last_modify_server";//
	  public static final String COLUMN_MD5_SERVER= "md5_server";//
	  public static final String COLUMN_MD5_LOCAL = "md5_local";//
	  public static final String COLUMN_SYNC = "sync";//
	  
	  private static final String DATABASE_NAME = "hubicsync.db";
	  private static final int DATABASE_VERSION = 1;

	  // Database creation sql statement
	  private static final String DATABASE_CREATE = "create table "
	      + TABLE_ACCOUNTS + "(" + COLUMN_ID
	      + " integer primary key autoincrement, " + COLUMN_NAME
	      + " text not null, " + COLUMN_TYPE
	      + " text not null,"  + COLUMN_ACCESS_TOKEN
	      + " text not null, " + COLUMN_REFRESH_TOKEN
	      + " text not null, " + COLUMN_ACCESS_TOKEN_EXPIRATION
	      + " text not null, " + COLUMN_OPENSTACK_ACCESS_TOKEN 
	      + " text not null, " + COLUMN_OPENSTACK_URL
	      + " text not null, " + COLUMN_OPENSTACK_ACCESS_TOKEN_EXPIRATION
	      + " text not null);";
	  
	  private static final String DATABASE_CREATE_BROWSER = "create table "
		      + TABLE_BROWSER + "(" + COLUMN_PATH
		      + " text primary key, " + COLUMN_ACCESS_ID
		      + " integer not null, " + COLUMN_LAST_MODIFY_SERVER 
		      + " text not null,"  + COLUMN_MD5_SERVER
		      + " text not null, " + COLUMN_MD5_LOCAL
		      + " text not null, " + COLUMN_SYNC
		      + " integer not null);";

	  public SQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    

	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		  database.execSQL(DATABASE_CREATE_BROWSER);
	    database.execSQL(DATABASE_CREATE);
	    
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(SQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_BROWSER);
	    onCreate(db);
	  }

}
