package com.hubicsync;
import hubic.Access;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;

import com.hubicsync.R;

import database.AccessDatasource;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
public class AccountListActivity extends ListFragment{


	    private AccountListAdapter adapter;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	   
	        fill();
	    }
	    private void fill()
	    {


	       List<ItemAccount>dir = new ArrayList<ItemAccount>();
	       AccessDatasource ads = new AccessDatasource(this.getActivity().getApplicationContext());
    		ads.open();
    		List<Access> acc = ads.getAllAccounts();
    		
    		for (int i = 0; i<acc.size(); i++){
                                //String formated = lastModDate.toString();
                                dir.add(new ItemAccount(acc.get(i).getName(),acc.get(i)));
    		}
         
    		Collections.sort(dir);

	              
	         adapter = new AccountListAdapter(this.getActivity().getApplicationContext(),R.layout.accountsview,dir);
	         this.setListAdapter(adapter);
	    }
	    @Override
		public void onListItemClick(ListView l, View v, int position, long id) {
	            Access acc= adapter.getItem(position).getAcc();
	            
	            Intent intent = new Intent(getActivity(), ExplorerActivity.class);
	          
	            intent.putExtra("ACCESS", acc);
               	startActivity(intent);
	            
	        }
	    private void onFileClick(ItemAccount o)
	    {
	        
	    }
	
}
