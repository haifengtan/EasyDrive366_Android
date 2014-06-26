package cn.count.easydrive366.insurance;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class BuyInsuranceStep7 extends BaseInsurance {
	private String data;
	private String order_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buyinsurance_step7);
		this.setupLeftButton();
		this.setupRightButtonWithText("下一步");
		this.setupTitle("在线购买保险", "填写配送地址");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init_view();
		
	}
	
	@Override
	protected void onRightButtonPress() {
		doSave();
	}

	

	private void doSave(){
		
		if (edt_name.getText().toString().trim().isEmpty()){
			this.showMessage("姓名不能为空！",null);
			return;
		}
		if (edt_cellphone.getText().toString().trim().isEmpty()){
			this.showMessage("手机不能为空！",null);
			return;
		}
		if (edt_address.getText().toString().trim().isEmpty()){
			this.showMessage("地址不能为空！",null);
			return;
		}
		String url = String.format("ins/carins_upload?userid=%d&name=%s&phone=%s&address=%s",
				AppSettings.userid,
				edt_name.getText().toString(),
				edt_cellphone.getText().toString(),
				edt_address.getText().toString());
				
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				process_result(result);
				
			}}.execute(url);
			
	}
	private void process_result(final String result){
		
		if (AppSettings.isSuccessJSON(result, this)){
			Intent intent = new Intent(this,UploadInsPhotoActivity.class);
			intent.putExtra("data", result);
			intent.putExtra("o_id", this.order_id);
			startActivity(intent);
			stack_push();
		}
	}
	private void init_view(){
		this.order_id = getIntent().getStringExtra("orderid");
		String url = String.format("ins/carins_address?userid=%d&orderid=%s&bounds=%s&bankid=%s&account=%s", 
				AppSettings.userid,
				getIntent().getStringExtra("orderid"),
				getIntent().getStringExtra("bounds"),
				getIntent().getStringExtra("bank_id"),
				getIntent().getStringExtra("account")
				);
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				process_data(result);
			}}.execute(url);
	}
	private EditText edt_name;
	private EditText edt_cellphone;
	private EditText edt_address;
	
	private void process_data(String result){
		edt_name = (EditText)findViewById(R.id.edt_name);
		edt_cellphone = (EditText)findViewById(R.id.edt_cellphone);
		edt_address = (EditText)findViewById(R.id.edt_address);
		JSONObject json = AppSettings.getSuccessJSON(result, this);
		if (json!=null){
			try{
				edt_name.setText(json.getString("name"));
				edt_cellphone.setText(json.getString("phone"));
				edt_address.setText(json.getString("address"));
			}catch(Exception e){
				log(e);
			}
		}
	}
}
