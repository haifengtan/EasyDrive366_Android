package cn.count.easydrive366.afterpay;

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

public class ContentsActivity extends BaseHttpActivity {
	private String data;
	private String order_id;
	private EditText edt_name;
	private EditText edt_cellphone;
	private EditText edt_address;
	private EditText edt_idcard;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_afterpay_contents);
		getActionBar().setTitle("保险信息");
		this.setupLeftButton();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init_view();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,100,0,"完成").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId()==100){
			doSave();
		}
		return super.onOptionsItemSelected(item);
	}
	private void doSave(){
		if (edt_name.getText().toString().trim().isEmpty()){
			this.showMessage("姓名不能为空！",null);
			return;
		}
		if (edt_idcard.getText().toString().trim().isEmpty()){
			this.showMessage("身份证号码不能为空！",null);
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
		String url = String.format("order/order_ins_content?userid=%d&orderid=%s&name=%s&phone=%s&address=%s&idcard=%s",
				AppSettings.userid,
				order_id,
				edt_name.getText().toString(),
				edt_cellphone.getText().toString(),
				edt_address.getText().toString(),
				edt_idcard.getText().toString());
				
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				JSONObject json = AppSettings.getSuccessJSON(result,ContentsActivity.this);
				if (json!=null){
					Intent intent = new Intent(ContentsActivity.this,FinishedActivity.class);
					intent.putExtra("data", json.toString());
					startActivity(intent);
					ContentsActivity.this.finish();
				}
				
				
				
			}}.execute(url);
	}
	private void init_view(){
		edt_name = (EditText)findViewById(R.id.edt_name);
		edt_cellphone = (EditText)findViewById(R.id.edt_cellphone);
		edt_address = (EditText)findViewById(R.id.edt_address);
		edt_idcard = (EditText)findViewById(R.id.edt_idcard);
		data = getIntent().getStringExtra("data");
		try{
			JSONObject json = new JSONObject(data);
			
			order_id  = json.getString("order_id");
			edt_name.setText(json.getString("name"));
			edt_cellphone.setText(json.getString("phone"));
			edt_address.setText(json.getString("address"));
			edt_idcard.setText(json.getString("idcard"));
			
		}catch(Exception e){
			log(e);
		}
	}
}
