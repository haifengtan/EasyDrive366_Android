package cn.count.easydrive366;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class CarRegistrationEditActivity extends BaseHttpActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_edit_carregistration_activity);
		this.setupLeftButton();
		Intent intent =this.getIntent();
		String data =intent.getStringExtra("data");
		try {
			JSONObject json = new JSONObject(data);
			initView(json);
		} catch (JSONException e) {
			log(e);
		}
		
		
	}
	private void initView(final JSONObject json){
		try {
			EditText t1 = ((EditText)findViewById(R.id.edt_carregistration_plate_no));
			String s1 =json.getString("plate_no").toUpperCase(); 
			if (s1.equals("")){
				s1 =this.getResources().getString(R.string.default_plate_no);
			}
			t1.setText(s1);
			((EditText)findViewById(R.id.edt_carregistration_engine_no  )).setText(json.getString("engine_no").toUpperCase());
			((EditText)findViewById(R.id.edt_carregistration_vin  )).setText(json.getString("vin").toUpperCase());
			((TextView)findViewById(R.id.edt_carregistration_registration_date  )).setText(json.getString("registration_date"));
			((EditText)findViewById(R.id.edt_carregistration_owner_name  )).setText(json.getString("owner_name").toUpperCase());
			((EditText)findViewById(R.id.edt_carregistration_owner_license  )).setText(json.getString("owner_license").toUpperCase());
			this.setupRightButtonWithText(this.getResources().getString(R.string.save));
			/*
			findViewById(R.id.btn_carregistration_save).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					save();
					
				}
				
			});
			*/
			findViewById(R.id.edt_carregistration_registration_date).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDate();
					
				}});
			findViewById(R.id.txt_driverlicense_choose_date).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDate();
					
				}});
			findViewById(R.id.tr_choose_date).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDate();
					
				}});
		} catch (Exception e) {
			log(e);
		}
	}
	@Override
	protected void onRightButtonPress(){
		save();
	}
	private void save(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)findViewById(R.id.edt_carregistration_plate_no)).getWindowToken(), 0);
		if (!this.isOnline()){
			this.showMessage(this.getResources().getString(R.string.no_network), null);
			return;
		}
		String url =String.format("api/add_car_registration?user_id=%d&car_id=%s&engine_no=%s&vin=%s&init_date=%s&owner_name=%s&owner_license=%s",
				AppSettings.userid,
				((EditText)findViewById(R.id.edt_carregistration_plate_no)).getText().toString().toUpperCase(),
				((EditText)findViewById(R.id.edt_carregistration_engine_no)).getText().toString().toUpperCase(),
				((EditText)findViewById(R.id.edt_carregistration_vin)).getText().toString().toUpperCase(),
				((TextView)findViewById(R.id.edt_carregistration_registration_date)).getText(),
				((EditText)findViewById(R.id.edt_carregistration_owner_name)).getText().toString().toUpperCase(),
				((EditText)findViewById(R.id.edt_carregistration_owner_license)).getText().toString().toUpperCase()
				);
		this.get(url, 2,this.getResources().getString(R.string.app_uploading));
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		
		super.processMessage(msgType, result);
		Bundle bundle =new Bundle();
		bundle.putString("result",result.toString());
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK,intent);
		finish();
	}
	private void chooseDate(){
		String d = ((TextView)findViewById(R.id.edt_carregistration_registration_date  )).getText().toString();
		if (d.equals("")){
			d = "2000-01-01";
		}
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
								((TextView)findViewById(R.id.edt_carregistration_registration_date  )).setText(sdf.format(c.getTime()));
						}
					},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
			dialog.show();
		}catch(Exception e){
			log(e);
		}
		
	
		
		
	}
}