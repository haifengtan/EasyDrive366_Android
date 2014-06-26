package cn.count.easydrive366;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;


import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpClient;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.EditText;

public class MaintainEditActivity extends BaseHttpActivity{
	private JSONObject _result;
	private String _average_mileage;
	private String _pre_distance;
	private String _prev_date;
	private String _max_distance;
	private String _max_time;
	private HttpClient _http;
/*	private String _current_date;
	private String _current_miles;
	private String _current_distance;
*/
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_edit_maintain_activity);
		this.setupLeftButton();
		Intent intent = this.getIntent();
		try {
			_result = new JSONObject(intent.getStringExtra("data"));
			initView();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
	}
	private void initView(){
		try{
			
			_average_mileage = _result.getString("average_mileage");
			_pre_distance = _result.getString("prev_distance");
			_prev_date = _result.getString("prev_date");
			_max_distance = _result.getString("max_distance");
			_max_time = _result.getString("max_time");
			/*
			_current_date = _result.getString("current_date");
			_current_miles = _result.getString("current_miles");
			_current_distance = _result.getString("current_distance");
			*/
			((EditText)findViewById(R.id.edt_maintain_average_mileage)).setText(_average_mileage);
			((EditText)findViewById(R.id.edt_maintain_pre_distance)).setText(_pre_distance);
			((TextView)findViewById(R.id.edt_maintain_pre_date)).setText(_prev_date);
			((EditText)findViewById(R.id.edt_maintain_max_distance)).setText(_max_distance);
			((EditText)findViewById(R.id.edt_maintain_max_time)).setText(_max_time);
			
			/*
			((EditText)findViewById(R.id.edt_maintain_current_date)).setText(_current_date);
			((EditText)findViewById(R.id.edt_maintain_current_miles)).setText(_current_miles);
			((EditText)findViewById(R.id.edt_maintain_current_distance)).setText(_current_distance);
			*/
			this.setupRightButtonWithText(this.getResources().getString(R.string.save));
			/*
			((Button)findViewById(R.id.btn_modules_edit_maintain_save)).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					save();
					
				}});
				*/
			findViewById(R.id.edt_maintain_pre_date).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDate();
					
				}});
			findViewById(R.id.imagebutton_date).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDate();
					
				}});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void save(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)findViewById(R.id.edt_maintain_max_distance)).getWindowToken(), 0);
		if (!this.isOnline()){
			this.showMessage(this.getResources().getString(R.string.no_network), null);
			return;
		}
		String max_time = ((EditText)findViewById(R.id.edt_maintain_max_time)).getText().toString();
		try{
			int max_month= Integer.parseInt(max_time);
			if (!(max_month>0 && max_month<=24)){
				this.showMessage(this.getString(R.string.max_time_error), null);
				return;
			}
		}catch(Exception e){
			log(e);
			this.showMessage(this.getString(R.string.max_time_error), null);
		}
		String url = String.format("api/add_maintain_record?user_id=%d&max_distance=%s&max_time=%s&prev_date=%s&prev_distance=%s&average_mileage=%s",
				AppSettings.userid,
				((EditText)findViewById(R.id.edt_maintain_max_distance)).getText(),
				((EditText)findViewById(R.id.edt_maintain_max_time)).getText(),
				((TextView)findViewById(R.id.edt_maintain_pre_date)).getText(),
				((EditText)findViewById(R.id.edt_maintain_pre_distance)).getText(),
				((EditText)findViewById(R.id.edt_maintain_average_mileage)).getText()
				);
		//getHttpClient().requestServer(url, 1);
		this.get(url, 1, this.getResources().getString(R.string.app_uploading));
		
	}
	@Override
	protected void onRightButtonPress(){
		save();
	}
	@Override
	public void processMessage(int msgType, Object result) {
		Log.d("Http",result.toString());
		
		Bundle bundle =new Bundle();
		bundle.putString("result",result.toString());
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK,intent);
		finish();
		
	}
	@Override
	public void recordResult(int msgType, Object result) {
		// TODO Auto-generated method stub
		
	}
	private void chooseDate(){
		String d = ((TextView)findViewById(R.id.edt_maintain_pre_date  )).getText().toString();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final Calendar c  = Calendar.getInstance();
		
		try{
			c.setTime(sdf.parse(d));
			
			Dialog dialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
								c.set(Calendar.YEAR, year);
								c.set(Calendar.MONTH,monthOfYear);
								c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
								//showDialog(sdf.format(c.getTime()));
								((TextView)findViewById(R.id.edt_maintain_pre_date  )).setText(sdf.format(c.getTime()));
						}
					},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
			dialog.show();
		}catch(Exception e){
			log(e);
		}
		
	
		
		
	}
}
