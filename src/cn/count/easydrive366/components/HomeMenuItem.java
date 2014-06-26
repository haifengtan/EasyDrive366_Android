package cn.count.easydrive366.components;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import cn.count.easydrive366.R;
import cn.count.easydrive366.baidumap.ShowLocationActivity;
import cn.count.easydrive366.barcode.ShowBarcodeActivity;
import cn.count.easydrive366.photo.TakePhotoActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.HttpExecuteGetTask;
import cn.count.easydriver366.service.GetLatestReceiver;

public class HomeMenuItem extends LinearLayout {
	private LayoutInflater _inflater =null;
	private Context _context;
	private HomeMenu _menuItem;
	private TextView _title;
	private TextView _description;
	private String _company;
	private String _phone;
	private ImageView _img;
	public LatestInformationReceiver _br;
	public HomeMenuItem(Context context,AttributeSet attrs){
		super(context,attrs);
		
		_inflater = LayoutInflater.from(context);
		_context = context;
		//_inflater.inflate(R.layout.home_menu_item, this);
		//_inflater.inflate(R.layout.home_menu_items_one_line, this);
		_inflater.inflate(R.layout.home_menu_item_with_image, this);
		_title = (TextView)findViewById(R.id.listitem_title);
		_description =(TextView)findViewById(R.id.listitem_content);
		_img = (ImageView)findViewById(R.id.listitem_pic);
		findViewById(R.id.listitem_pic).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				makeCall();
				
			}});
		this.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clickHandler();
				
			}
			
		});
		IntentFilter filter = new IntentFilter("cn.count.easydrive366.components.HomeMenuItem$LatestInformationReceiver");
		_br = new LatestInformationReceiver();
		_context.registerReceiver(_br,filter);
		
	}
	
	public void setData(HomeMenu menuItem){
		if (_img!=null){
			_img.setImageDrawable(_context.getResources().getDrawable(menuItem.ImageId));
		}
		_menuItem = menuItem;
		_menuItem.menuItem = this;
		_title.setText(_menuItem.name);
		final String oldValue = loadJson();
		if (oldValue.equals("")){
			_description.setText(_menuItem.description);
		}else{
			processData(oldValue);
		}
		
	}
	private void makeCall(){
		
		
		if (_phone!=null && !_phone.equals("")){
			Uri uri =Uri.parse(String.format("tel:%s",_phone)); 
			
			Intent it = new Intent(Intent.ACTION_VIEW,uri); 
			_context.startActivity(it); 
		}
		
		//chooseActions();
	}
	private void chooseActions()
	{
		

		// Wrap our context to inflate list items using correct theme
		final Context dialogContext = new ContextThemeWrapper(_context,
				android.R.style.Theme_Light);
		String cancel = "取消";
		String[] choices;
		choices = new String[4];
		choices[0] = "打电话："+this._phone;
		choices[1] = "地图";
		choices[2] = "拍照上传";
		choices[3] = "二维码演示";
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("请选择：");
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0: 
							if (_phone!=null && !_phone.equals("")){
								Uri uri =Uri.parse(String.format("tel:%s",_phone)); 
								
								Intent it = new Intent(Intent.ACTION_VIEW,uri); 
								_context.startActivity(it); 
							}
							break;
						case 1:
							Intent intent = new Intent(_context,ShowLocationActivity.class);
							
							_context.startActivity(intent);
							break;
						case 2:
							
							Intent intent2 = new Intent(_context,TakePhotoActivity.class);
							_context.startActivity(intent2);
							break;
						case 3:
							Intent intent3 = new Intent(_context,ShowBarcodeActivity.class);
							_context.startActivity(intent3);
							break;
						}
					}
				});
		builder.setNegativeButton(cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		builder.create().show();
		
	}
	private void clickHandler(){
		if ((_menuItem!=null) && (_menuItem.activityClass!=null)){
			
			Intent intent=new Intent(_context,_menuItem.activityClass);
			intent.putExtra("title", _menuItem.name);
			if (_menuItem.params!=null){
				for(Entry<String, String> entry :_menuItem.params.entrySet()){
					String key = entry.getKey();
					String v = entry.getValue();
					intent.putExtra(key, v);
				}
			}
			_context.startActivity(intent);
		}
	}
	private void processData(final String json){
		try{
			final JSONObject result = new JSONObject(json);
			//_company = result.getString("company");
			//_phone = result.getString("phone");
			final String d = result.getString("latest");
			((Activity) _context).runOnUiThread(new Runnable(){

				@Override
				public void run() {
					_description.setText(d);
				}});
			
			
			
		}catch(Exception e){
			AppTools.log(e);
		}
	}
	private void processJson(final String key,final String json){
		if (_menuItem.key.equals(key)){
			saveJson(json);
			
			processData(json);
		}
		
	}
	private String getKey(){
		return String.format("%s_userid_%d", AppSettings.LatestNewsKey,AppSettings.userid);
	}
	private String loadJson(){
		SharedPreferences prefs =_context.getSharedPreferences(getKey(), Context.MODE_PRIVATE);
		String result = prefs.getString(_menuItem.key, "");
		return result;
	}
	private long lastUpdateTime(){
		SharedPreferences prefs =_context.getSharedPreferences(getKey(), Context.MODE_PRIVATE);
		long result = prefs.getLong(_menuItem.key+"_datetime", 0);
		return result;
	}
	private boolean is_update_expired(){
		long lastTime = this.lastUpdateTime();
		if (lastTime==0)
			return false;
		else{
			float diff = (System.currentTimeMillis()-lastTime)/1000/60;
			if (diff>AppSettings.update_time){
				//Log.e("Time diff", String.valueOf(diff));
				return true;
			}else
				return false;
		}
	}
	private void saveJson(final String json){
		SharedPreferences prefs =_context.getSharedPreferences(getKey(), Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(_menuItem.key, json);
		editor.putLong(_menuItem.key+"_datetime", System.currentTimeMillis());
		editor.commit();
		
	}
	public class LatestInformationReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			final String key= bundle.getString("key");
			final String json = bundle.getString("json");
			
			processJson(key,json);
			Log.i("Receiver", json);
		}
		
	}
	
	public void getLatest(final boolean fromCache){
		if (_menuItem!=null){
			if (fromCache && !this.is_update_expired()){
				
				final String oldValue = loadJson();
				if (oldValue!=null && !oldValue.isEmpty()){
				
					processData(oldValue);
					return;
				}
			}
				
			final String url = String.format("api/get_latest?userid=%d&keyname=%s",AppSettings.userid,_menuItem.key);
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					try{
						JSONObject json = new JSONObject(result);
						if (AppSettings.isSuccessJSON(json,_context)){
							processJson(_menuItem.key,json.getJSONObject("result").toString());
						}
					}catch(Exception e){
						
						//Log.e("Error", e.getMessage());
					}
					
					
				}}.execute(url);
		}
		
	}
}
