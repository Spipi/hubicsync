package database;

import java.util.ArrayList;
import java.util.List;

import hubic.Access;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AccessDatasource {

	  
	  
	  
		  // Database fields
		  private SQLiteDatabase database;
		  private SQLiteHelper dbHelper;
		  private String[] allColumns = { SQLiteHelper.COLUMN_ID,
		      SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_TYPE,SQLiteHelper.COLUMN_ACCESS_TOKEN, SQLiteHelper.COLUMN_REFRESH_TOKEN, SQLiteHelper.COLUMN_ACCESS_TOKEN_EXPIRATION, SQLiteHelper.COLUMN_OPENSTACK_ACCESS_TOKEN, SQLiteHelper.COLUMN_OPENSTACK_ACCESS_TOKEN_EXPIRATION, SQLiteHelper.COLUMN_OPENSTACK_URL };
		  
		  private Context context;
		  public AccessDatasource(Context context) {
		    dbHelper = new SQLiteHelper(context);
		    this.context = context;
		  }

		  public void open() throws SQLException {
		    database = dbHelper.getWritableDatabase();
		  }

		  public void close() {
		    dbHelper.close();
		  }


		  
		  public Access addAccount(Access account) {
		    ContentValues values = new ContentValues();
		    values.put(SQLiteHelper.COLUMN_NAME, account.getName());
		    values.put(SQLiteHelper.COLUMN_TYPE, account.getType());
		    values.put(SQLiteHelper.COLUMN_ACCESS_TOKEN , account.getAccess_token());
		    values.put(SQLiteHelper.COLUMN_REFRESH_TOKEN , account.getRefresh_token());
		    values.put(SQLiteHelper.COLUMN_ACCESS_TOKEN_EXPIRATION  , account.getExpiration());
		    values.put(SQLiteHelper.COLUMN_OPENSTACK_ACCESS_TOKEN , account.getOpenstack_access_token());
		    values.put(SQLiteHelper.COLUMN_OPENSTACK_URL , account.getOpenstack_url());
			System.out.println("addAccount "+account.getOpenstack_url());
		    values.put(SQLiteHelper.COLUMN_OPENSTACK_ACCESS_TOKEN_EXPIRATION, account.getOpenstack_access_token_expiration());
		    Cursor cursor = null;
		    long insertId = 0;
		    if(account.getId()!=null){
		    	cursor = database.query(SQLiteHelper.TABLE_ACCOUNTS,
				        allColumns, SQLiteHelper.COLUMN_ID + " = " + account.getId(), null,
				        null, null, null);
		    	insertId = account.getId();
		    }
		    
		    if(cursor==null || cursor.isAfterLast())
		    	insertId = database.insert(SQLiteHelper.TABLE_ACCOUNTS, null,
				        values);
		   
		    else
		    	database.updateWithOnConflict(SQLiteHelper.TABLE_ACCOUNTS,
				        values,SQLiteHelper.COLUMN_ID + " = " + insertId, null,database.CONFLICT_REPLACE);
		    
		    cursor = database.query(SQLiteHelper.TABLE_ACCOUNTS,
			        allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
			        null, null, null);
		    cursor.moveToFirst();
		    Access newAccess = cursorToAccess(cursor);
		    System.out.println("afteraddAccount "+newAccess.getOpenstack_url());
		    cursor.close();
		    return newAccess;
		  }

		  public void deleteComment(Access acc) {
		    long id = acc.getId();
		    System.out.println("Account deleted with id: " + id);
		    database.delete(SQLiteHelper.TABLE_ACCOUNTS, SQLiteHelper.COLUMN_ID
		        + " = " + id, null);
		  }

		  public List<Access> getAllAccounts() {
		    List<Access> comments = new ArrayList<Access>();

		    Cursor cursor = database.query(SQLiteHelper.TABLE_ACCOUNTS,
		        allColumns, null, null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Access comment = cursorToAccess(cursor);
		      comments.add(comment);
		      cursor.moveToNext();
		    }
		    // make sure to close the cursor
		    cursor.close();
		    return comments;
		  }

		  
		  private Access cursorToAccess(Cursor cursor) {
			 
		    Access acc = new Access(cursor.getString(1));
		    acc.setId(cursor.getLong(0));
		    acc.setType(cursor.getString(2));
		    acc.setAccess_token(cursor.getString(3));
		    acc.setRefresh_token(cursor.getString(4));
		    acc.setExpiration(cursor.getString(5));
		    acc.setOpenstack_access_token(cursor.getString(6));
		    acc.setOpenstack_url(cursor.getString(8));
		    acc.setOpenstack_access_token_expiration(cursor.getString(7));
		    return acc;
		  }
		
}
