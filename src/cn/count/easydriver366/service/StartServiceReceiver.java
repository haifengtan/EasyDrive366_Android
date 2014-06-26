package cn.count.easydriver366.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartServiceReceiver extends BroadcastReceiver  {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION))   
        {  
			context.startService(new Intent(context,BackendService.class));  
        }  
		
	}
	

}
