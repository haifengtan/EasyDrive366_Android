package cn.count.easydriver366.service;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.count.easydrive366.R;
import cn.count.easydrive366.HomeActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.HttpClient;
import cn.count.easydriver366.base.NetworkUtils;
import android.app.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.widget.Toast;


public class GetLatestReceiver extends BroadcastReceiver implements HttpClient.IHttpCallback {
	private static int NOTIFICATION_ID = 1;
	private Context context;
	private static final boolean isDebug = false;
	private boolean _isInApp=false;

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		if (isDebug)
			Toast.makeText(arg0, "Loading...", Toast.LENGTH_LONG).show();
		this.context = arg0;
		_isInApp = arg1.getBooleanExtra("isInApp", false);
		run();
	}

	

	private void deleteNotification() {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}
	/*
	private void showNotification(final String title,final String content){
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(title)
		        .setContentText(content);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, WelcomeActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(WelcomeActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}
	*/
	private void showNotification(final String title,final String content) {
		if (isDebug)
			Toast.makeText(context, "start...", Toast.LENGTH_LONG).show();
		Notification notification = new Notification(R.drawable.ic_launcher,
				title, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, HomeActivity.class), 0);
		notification.setLatestEventInfo(context, title, content,
				contentIntent);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		NOTIFICATION_ID=NOTIFICATION_ID+1;
		notificationManager.notify(NOTIFICATION_ID, notification);
		if (isDebug)
			Toast.makeText(context, "end...", Toast.LENGTH_LONG).show();
	}
	public void run() {
		if (context==null){
			return;
		}
		AppSettings.restore_login_from_device(context);
		if (!AppSettings.isLogin){
			return;
		}
		if (!this._isInApp && DateUtils.is_SleepTime()){
			return;
		}
		if ( NetworkUtils.getNetworkState(context) <1){
			return;
		}
		HttpClient client = new HttpClient(this);
		if (_isInApp){
			for(int i=1;i<=AppSettings.TotalPageCount;i++){
				final String url = String.format("api/get_latest?userid=%d&keyname=%02d",AppSettings.userid,i);
				client.sendHttp(AppSettings.ServerUrl, url, i);
				//sendHttp(AppSettings.ServerUrl+url,i);
			}
		}else{
			//final String url = AppSettings.url_for_get_news();
			final String url = AppSettings.url_pull_msg();
			client.sendHttp(AppSettings.ServerUrl, url, 1);
		}
		
	}
	
	@Override
	public void processMessage(int msgType, Object result) {
		try{
			JSONObject json = (JSONObject)result;
			if (AppTools.isSuccess(json)){
				if (_isInApp){
					Intent intent = new Intent();
					intent.putExtra("key", String.format("%02d", msgType));
					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
					final String updated_time = sdf.format(new Date());
					json.getJSONObject("result").put("updated_time", updated_time);
					intent.putExtra("json",json.getJSONObject("result").toString());
					intent.setAction("cn.count.easydrive366.components.HomeMenuItem$LatestInformationReceiver");
					context.sendBroadcast(intent);
				}else{
					JSONArray list = json.getJSONObject("result").getJSONArray("data");
					for(int i=0;i<list.length();i++){
						JSONObject item = list.getJSONObject(i);
						String description = item.getString("description");
						if (!description.equals("")){
							this.showNotification("易驾366",item.getString("description"));
						}
						
					}
					
				}
				
				//Looper.prepare();
				
			}
		}catch(Exception e){
			AppTools.log(e);
		}
		
	}

	@Override
	public void recordResult(int msgType, Object result) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void showFailureMessage(String msg) {
		//Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		
	}
}
