package cn.count.easydrive366.signup;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;

public class Step2Activity extends BaseHttpActivity {
	private Button btnNext;
	
	private EditText edtVIN;
	private EditText edtEngine_no;
	private EditText edtRegistration_date;
	private EditText edtOwner_name;
	private EditText edtOwner_license;
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		this._isHideTitleBar = true;
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.modules_step2_activity);
		this.setupLeftButton();
		this.setRightButtonInVisible();
		this.setupPhoneButtonInVisible();
		btnNext =(Button)findViewById(R.id.btn_ok);
		edtVIN = (EditText)findViewById(R.id.edt_vin);
		edtEngine_no = (EditText)findViewById(R.id.edt_engine_no);
		edtRegistration_date = (EditText)findViewById(R.id.edt_registration_date);
		edtOwner_name = (EditText)findViewById(R.id.edt_owner_name);
		edtOwner_license = (EditText)findViewById(R.id.edt_owner_license);
		btnNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				nextStep();
				
			}});
		findViewById(R.id.row_registration_date).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				chooseDate(edtRegistration_date);
				
			}});
		Intent intent = this.getIntent();
		edtVIN.setText(intent.getStringExtra("vin"));
		edtEngine_no.setText(intent.getStringExtra("engine_no"));
		edtRegistration_date.setText(intent.getStringExtra("registration_date"));
		edtOwner_name.setText(intent.getStringExtra("owner_name"));
		edtOwner_license.setText(intent.getStringExtra("owner_license"));
	}
	
	@Override
	public void processMessage(int msgType, final Object result) {
		super.processMessage(msgType, result);
		if (this.isSuccess(result)){
			try{
				Intent intent =new Intent(this,Step3Activity.class);
				JSONObject json = (JSONObject)result;
				intent.putExtra("name", json.getJSONObject("result").getString("name"));
				intent.putExtra("car_type", json.getJSONObject("result").getString("car_type"));
				intent.putExtra("init_date", json.getJSONObject("result").getString("init_date"));
				startActivity(intent);
				finish();
			}catch(Exception e){
				log(e);
			}
			
		}else{
			String message;
			try {
				message = ((JSONObject)result).getString("message");
				this.showMessage(message, null);
			} catch (JSONException e) {
			
				log(e);
			}
			
		}
	}
	private void nextStep(){
		String vin= edtVIN.getText().toString();
		String engine_no = edtEngine_no.getText().toString();
		String registration_date = edtRegistration_date.getText().toString();
		String owner_name = edtOwner_name.getText().toString();
		String owner_license = edtOwner_license.getText().toString();
		/*
		if (vin.equals("")){
			this.showMessage("请输入VIN码。", null);
			return;
		}
		if (engine_no.equals("") ){
			this.showMessage("请输入发动机号。", null);
			return;
		}
		if (registration_date.equals("")){
			this.showMessage("请输入初登日期。", null);
			return;
		}
		*/
		String url =String.format("api/wizardstep2?userid=%d&vin=%s&engine_no=%s&registration_date=%s&owner_name=%s&owner_license=%s",
				AppSettings.userid,vin,engine_no,registration_date,owner_name,owner_license);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtVIN.getWindowToken(), 0);
		this.get(url, 1);
		
	}
	
}
