package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;
public class DriverLicenseActivity extends BaseListViewActivity {
	private JSONObject _result;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_driverlicense_activity);
		this.setupLeftButton();
		this.setupRightButtonWithText("编辑");
		restoreFromLocal(1);
		this.get(AppSettings.url_get_license_type(), 2,"");
		reload_data();
		this.setupScrollView();
	}
	@Override
	protected void reload_data() {
		this.get(AppSettings.url_get_driver_license(), 1);
		
	}
	@Override
	public void processMessage(int msgType,final Object result){
		if (msgType==1){
			super.processMessage(msgType, result);
		}else if (msgType==2){
			if (this.isSuccess(result)){
				try {
					AppSettings.driver_type_list = ((JSONObject)result).getJSONArray("result");
				} catch (JSONException e) {
					log(e);
				}
			}
		}
	}
	@Override
	protected void initData(Object result, int msgType) {
		try{
			if (msgType==1){
				_result= (((JSONObject)result).getJSONObject("result")).getJSONObject("data");
				this.saveWithKey("driver_license", _result);
			}
		}catch(Exception e){
			this.log(e);
		}
		
		

	}

	@Override
	protected void initView() {
		try{
			this.endRefresh();
			((TextView)findViewById(R.id.txt_driverlicense_name)).setText(_result.getString("name"));
			((TextView)findViewById(R.id.txt_driverlicense_check_date  )).setText(_result.getString("check_date"));
			((TextView)findViewById(R.id.txt_driverlicense_end_date  )).setText(_result.getString("end_date"));
			((TextView)findViewById(R.id.txt_driverlicense_init_date  )).setText(_result.getString("init_date"));
			((TextView)findViewById(R.id.txt_driverlicense_mark  )).setText(_result.getString("mark"));
			((TextView)findViewById(R.id.txt_driverlicense_mark_end_date  )).setText(_result.getString("mark_end_date"));
			((TextView)findViewById(R.id.txt_driverlicense_number  )).setText(_result.getString("number"));
			((TextView)findViewById(R.id.txt_driverlicense_renew_date  )).setText(_result.getString("renew_date"));
			//((TextView)findViewById(R.id.txt_driverlicense_start_date  )).setText(_result.getString("start_date"));
			((TextView)findViewById(R.id.txt_driverlicense_car_type  )).setText(_result.getString("car_type"));
			/*
			Button btn= (Button)findViewById(R.id.btn_driverlicense_edit);
			btn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(DriverLicenseActivity.this,DriverLicenseEditActivity.class);
					intent.putExtra("data", _result.toString());
					startActivityForResult(intent,0);
					
				}});
			*/
			this.setupRightButton();
			
		}catch(Exception e){
			log(e);
		}
		
		

	}
	@Override
	protected void onRightButtonPress(){
		Intent intent = new Intent(DriverLicenseActivity.this,DriverLicenseEditActivity.class);
		intent.putExtra("data", _result.toString());
		startActivityForResult(intent,0);
	}
	@Override
	protected void setupListItem(ViewHolder holder, Map<String, Object> info) {
		
		
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==0 && resultCode==RESULT_OK){
			Bundle extras = data.getExtras();
			try {
				this.processMessage(1, new JSONObject(extras.getString("result")));
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
		}
	}

}
