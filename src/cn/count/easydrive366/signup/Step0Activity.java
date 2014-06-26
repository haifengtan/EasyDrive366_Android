package cn.count.easydrive366.signup;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;

public class Step0Activity extends BaseHttpActivity {
	private Button btnNext;
	
	private EditText edtCar_no;
	
	private EditText edt_invite_code;
	private EditText edt_phone;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		this._isHideTitleBar = true;
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.module_step0_activity);
		this.setupLeftButton();
		this.setRightButtonInVisible();
		this.setupPhoneButtonInVisible();
		btnNext =(Button)findViewById(R.id.btn_ok);
		edtCar_no = (EditText)findViewById(R.id.edt_car_no);
		
		edt_invite_code = (EditText)findViewById(R.id.edt_invite_code);
		edt_phone = (EditText)findViewById(R.id.edt_phone);
		edtCar_no.setText("鲁");
		btnNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				nextStep();
				
			}});
		this.get(String.format("api/wizardstep?userid=%d",AppSettings.userid), 2);
	}
	
	@Override
	public void processMessage(int msgType, final Object result) {
		
		if (this.isSuccess(result)){
			if (msgType==1){
				try{
					if (AppSettings.isSuccessJSON((JSONObject)result, this)){
						Intent intent =new Intent(this,Step1Activity.class);
					
						startActivity(intent);
						finish();
					}
					
				}catch(Exception e){
					log(e);
				}
			}
			if (msgType==2){
				final JSONObject json = (JSONObject)result;
				this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						try{
							edtCar_no.setText(json.getJSONObject("result").getString("car_id"));
							edt_invite_code.setText(json.getJSONObject("result").optString("invite_code"));
							edt_phone.setText(json.getJSONObject("result").getString("phone"));
						}catch(Exception e){
							log(e);
						}
					}});
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
		String car_no= edtCar_no.getText().toString();
		
		String invite_code = edt_invite_code.getText().toString();
		if (car_no.equals("")){
			this.showMessage("请输入车牌号码！", null);
			return;
		}
		
		
		String url =String.format("api/wizardstep0?userid=%d&car_id=%s&invite_code=%s&phone=%s&sign=1",
					AppSettings.userid,
					car_no.toUpperCase(),
					
					invite_code.toUpperCase(),
					edt_phone.getText().toString());
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtCar_no.getWindowToken(), 0);
		this.get(url, 1);
		
	}
}
