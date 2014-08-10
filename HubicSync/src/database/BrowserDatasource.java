package database;

import java.util.ArrayList;
import java.util.List;

import com.hubicsync.PathItem;

import hubic.Access;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class BrowserDatasource {

	  
	  
	  
		  // Database fields
		  private SQLiteDatabase database;
		  private SQLiteHelper dbHelper;
		  private String[] allColumns = { SQLiteHelper.COLUMN_PATH,
		      SQLiteHelper.COLUMN_ACCESS_ID, SQLiteHelper.COLUMN_LAST_MODIFY_SERVER,SQLiteHelper.COLUMN_MD5_SERVER, SQLiteHelper.COLUMN_MD5_LOCAL, SQLiteHelper.COLUMN_SYNC};
		  

		  public BrowserDatasource(Context context) {
		    dbHelper = new SQLiteHelper(context);
		  }

		  public void open() throws SQLException {
		    database = dbHelper.getWritableDatabase();
		  }

		  public void close() {
		    dbHelper.close();
		  }

		  public static final String TABLE_BROWSER = "browser";
		  public static final String COLUMN_ACCESS_ID = "access_id";
		  public static final String COLUMN_PATH = "path";//
		  public static final String COLUMN_LAST_MODIFY_SERVER = "last_modify_server";//
		  public static final String COLUMN_MD5_SERVER= "md5_server";//
		  public static final String COLUMN_MD5_LOCAL = "md5_local";//
		  public static final String COLUMN_SYNC = "sync";//
		  
		  public void addAccount(PathItem pathItem) {
		    ContentValues values = new ContentValues();
		    
		    values.put(SQLiteHelper.COLUMN_PATH, pathItem.getPath().replaceAll("'","\'"));
		    values.put(SQLiteHelper.COLUMN_ACCESS_ID, pathItem.getAccessID());
		    values.put(SQLiteHelper.COLUMN_LAST_MODIFY_SERVER , pathItem.getModified());
		    values.put(SQLiteHelper.COLUMN_MD5_SERVER , pathItem.getMD5Server());
		    values.put(SQLiteHelper.COLUMN_MD5_LOCAL  , pathItem.getMD5Local());
		    values.put(SQLiteHelper.COLUMN_SYNC , pathItem.getSync());

		  
		    database.insertWithOnConflict(SQLiteHelper.TABLE_BROWSER, null,
		    			values,database.CONFLICT_REPLACE);
		   
		   
		   
		  
		    
		   
		  }
		  public void updateAccount(PathItem pathItem){
			  	ContentValues values = new ContentValues();
			  	
			    values.put(SQLiteHelper.COLUMN_PATH, pathItem.getPath().replaceAll("'","\'"));
			    values.put(SQLiteHelper.COLUMN_ACCESS_ID, pathItem.getAccessID());
			    values.put(SQLiteHelper.COLUMN_LAST_MODIFY_SERVER , pathItem.getModified());
			    values.put(SQLiteHelper.COLUMN_MD5_SERVER , pathItem.getMD5Server());
			    values.put(SQLiteHelper.COLUMN_MD5_LOCAL  , pathItem.getMD5Local());
			    values.put(SQLiteHelper.COLUMN_SYNC , pathItem.getSync());
			    database.updateWithOnConflict(SQLiteHelper.TABLE_BROWSER,
				        values,SQLiteHelper.COLUMN_PATH + " = '" + pathItem.getPath()+"'", null,database.CONFLICT_REPLACE);
			  
		  }
		  public int count(final String string, final String substring)
		  {
		     int count = 0;
		     int idx = 0;

		     while ((idx = string.indexOf(substring, idx)) != -1)
		     {
		        idx++;
		        count++;
		     }

		     return count;
		  }
		  public List<PathItem> getChildrenOf(PathItem parent){
			  String path = parent.getPath();

			  
			  List<PathItem> pis = new ArrayList<PathItem>();

			    Cursor cursor = database.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_BROWSER+" where "+SQLiteHelper.COLUMN_ACCESS_ID+" = "+ parent.getAccessID()+" and "+SQLiteHelper.COLUMN_PATH+" like ?;",new String[]{path+"%"});

			    cursor.moveToFirst();
			    while (!cursor.isAfterLast()) {
			    	int count = count(cursor.getString(0),"/");
			    	int countP = count(parent.getPath(),"/");
			    if(!cursor.getString(0).equals(parent.getPath()) && countP==count || countP==count-1 && cursor.getString(0).endsWith("/") ){
				      PathItem pi = cursorToPathItem(cursor);
				      pis.add(pi);
				      
			    	}
			    cursor.moveToNext();
			    }
			    // make sure to close the cursor
			    cursor.close();
			    return pis;
		  }
		  public void deleteComment(Access acc) {
		    long id = acc.getId();
		    System.out.println("Account deleted with id: " + id);
		    database.delete(SQLiteHelper.TABLE_ACCOUNTS, SQLiteHelper.COLUMN_ID
		        + " = " + id, null);
		  }

		  public List<PathItem> getAllPathItems() {
		    List<PathItem> comments = new ArrayList<PathItem>();

		    Cursor cursor = database.query(SQLiteHelper.TABLE_ACCOUNTS,
		        allColumns, null, null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      PathItem comment = cursorToPathItem(cursor);
		      comments.add(comment);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return comments;
		  }

		  public List<PathItem> getAllPathItemsToSync() {
			    List<PathItem> comments = new ArrayList<PathItem>();

			    Cursor cursor = database.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_BROWSER+" where "+SQLiteHelper.COLUMN_SYNC+" = 1",null);


			    cursor.moveToFirst();
			    while (!cursor.isAfterLast()) {
			      PathItem comment = cursorToPathItem(cursor);
			      comments.add(comment);
			      cursor.moveToNext();
			    }
			    // make sure to close the cursor
			    cursor.close();
			    return comments;
		}
		public List<PathItem> getAllPathItemsFolderToSync() {
			    List<PathItem> comments = new ArrayList<PathItem>();

			    Cursor cursor = database.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_BROWSER+" where "+SQLiteHelper.COLUMN_SYNC+" = 1 AND "+SQLiteHelper.COLUMN_PATH +" LIKE '%/'",null);


			    cursor.moveToFirst();
			    while (!cursor.isAfterLast()) {
			      PathItem comment = cursorToPathItem(cursor);
			      comments.add(comment);
			      cursor.moveToNext();
			    }
			    // make sure to close the cursor
			    cursor.close();
			    return comments;
		}
		  private PathItem cursorToPathItem(Cursor cursor) {

			    boolean sync = false;
			    if(cursor.getLong(5)==1)
			    	sync = true;
			PathItem pi = new PathItem(cursor.getString(0),cursor.getString(1), cursor.getString(4), cursor.getString(3),cursor.getString(2),sync);
			
		    return pi;
		  }

		public PathItem getAccount(String name, String Access) {
			System.out.println(name);
			
			 Cursor cursor = database.query(SQLiteHelper.TABLE_BROWSER,allColumns, SQLiteHelper.COLUMN_PATH + " = ? AND "+SQLiteHelper.COLUMN_ACCESS_ID+" = ?",new String[]{name,Access},
				        null, null, null);
			 cursor.moveToFirst();
			 if(cursor.isAfterLast()){cursor.close(); return null;}
			 PathItem pi = cursorToPathItem(cursor);
			 cursor.close();
			 return pi;
		}

		public List<PathItem> getSyncChildrenAndSubsOf(PathItem parent) {
				String path = parent.getPath();

			  
			  List<PathItem> pis = new ArrayList<PathItem>();

			    Cursor cursor = database.rawQuery("SELECT * FROM "+SQLiteHelper.TABLE_BROWSER+" where "+SQLiteHelper.COLUMN_ACCESS_ID+" = "+ parent.getAccessID()+"and "+SQLiteHelper.COLUMN_SYNC+" = 1 and "+SQLiteHelper.COLUMN_PATH+" like ?;",new String[]{path+"%"});

			    cursor.moveToFirst();
			    while (!cursor.isAfterLast()) {
			    	
				      PathItem pi = cursorToPathItem(cursor);
				      pis.add(pi);
				      
			    	
			    cursor.moveToNext();
			    }
			    // make sure to close the cursor
			    cursor.close();
			    return pis;
			
		}
		public void setSyncChildrenAndSubsOf(PathItem parent,boolean sync) {
			String path = parent.getPath();

		  
		  List<PathItem> pis = new ArrayList<PathItem>();


		    database.execSQL("UPDATE "+SQLiteHelper.TABLE_BROWSER+" set "+SQLiteHelper.COLUMN_SYNC+"=0 where "+SQLiteHelper.COLUMN_ACCESS_ID+" = "+ parent.getAccessID()+" and "+SQLiteHelper.COLUMN_PATH+" like ? ;",new String[]{path+"%"});
		    
		
	}
		
}
