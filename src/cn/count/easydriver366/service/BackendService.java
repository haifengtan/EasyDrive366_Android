package cn.count.easydriver366.service;

import java.util.Calendar;

import cn.count.easydriver366.base.AppSettings;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class BackendService extends Service {
	private GetLatestReceiver br;
	private int seconds = 4*60*60;
	@Override
	public void onStart(Intent intent,int startId){
		super.onStart(intent, startId);
		AppSettings.restore_login_from_device(this);
		seconds = AppSettings.update_time;
		this.startRepeatAlarm();
	}
	@Override
	public void onDestroy(){
		this.unregisterReceiver(br);
	}
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	private void startRepeatAlarm(){
		IntentFilter filter = new IntentFilter("cn.count.easydriver366.service.GetLatestReceiverr");
		br = new GetLatestReceiver();
		registerReceiver(br,filter);
		
		Calendar cal = DateUtils.getTimeAfterInSecs(seconds);
		String s = DateUtils.getDateTimeString(cal);
		
		String actionString = "cn.count.easydriver366.service.GetLatestReceiverr";
		Intent broadcastIntent = new Intent(actionString);
		
		broadcastIntent.putExtra("message", "hello, i come from backend service");
		broadcastIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
		PendingIntent pi = this.getDistinctPendingIntent(broadcastIntent, 2);
		AlarmManager am =(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), seconds*1000, pi);
		
	}
	protected PendingIntent getDistinctPendingIntent(Intent intent,int requestId){
		PendingIntent pi = PendingIntent.getBroadcast(this, requestId, intent, 0);
		return pi;
	}
}
