package service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HubicReceiver  extends BroadcastReceiver {   
	 
    @Override
    public void onReceive(Context context, Intent intent) {

     Intent myIntent = new Intent(context, HubicSyncService.class);
     context.startService(myIntent);
    }
}