package cn.count.easydrive366.afterpay;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TableLayout;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class AddressActivity extends BaseHttpActivity {
	private String data;
	private String order_id;
	private EditText edt_name;
	private EditText edt_cellphone;
	private EditText edt_address;
	private EditText edt_name1;
	private EditText edt_cellphone1;
	private EditText edt_address1;
	private CheckBox chb_auto;
	private boolean is_auto;
	private boolean a_visible;
	private TableLayout table;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_afterpay_address);
		this.setupLeftButton();
		getActionBar().setTitle("配送信息");
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
		if (item.getItemId()==100)
			doSave();
		return super.onOptionsItemSelected(item);
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
		String url = String.format("order/order_address?userid=%d&orderid=%s&name=%s&phone=%s&address=%s&a_visible=%s&a_checked=%s&a_name=%s&a_phone=%s&a_address=%s",
				AppSettings.userid,
				this.order_id,
				edt_name.getText().toString(),
				edt_cellphone.getText().toString(),
				edt_address.getText().toString(),
				a_visible?"1":"0",
				is_auto?"1":"0",
				edt_name1.getText().toString(),
				edt_cellphone1.getText().toString(),
				edt_address1.getText().toString());
				
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				JSONObject json = AppSettings.getSuccessJSON(result,AddressActivity.this);
				if (json!=null){
					Intent intent = new Intent(AddressActivity.this,FinishedActivity.class);
					intent.putExtra("data", json.toString());
					startActivity(intent);
					AddressActivity.this.finish();
				}
				
				
			}}.execute(url);
	}
	private void init_view(){
		edt_name = (EditText)findViewById(R.id.edt_name);
		edt_cellphone = (EditText)findViewById(R.id.edt_cellphone);
		edt_address = (EditText)findViewById(R.id.edt_address);
		edt_name1 = (EditText)findViewById(R.id.edt_name1);
		edt_cellphone1 = (EditText)findViewById(R.id.edt_cellphone1);
		edt_address1 = (EditText)findViewById(R.id.edt_address1);
		chb_auto = (CheckBox)findViewById(R.id.chb_auto);
		table = (TableLayout)findViewById(R.id.table_auto);
		data = getIntent().getStringExtra("data");
		try{
			JSONObject json = new JSONObject(data);
			a_visible = json.getInt("a_visible")==1;
			if (!a_visible){
				table.setVisibility(View.GONE);
			}
			order_id  = json.getString("order_id");
			edt_name.setText(json.getString("name"));
			edt_cellphone.setText(json.getString("phone"));
			edt_address.setText(json.getString("address"));
			edt_name1.setText(json.getString("a_name"));
			edt_cellphone1.setText(json.getString("a_phone"));
			edt_address1.setText(json.getString("a_address"));
			is_auto =json.getInt("a_checked")==1;
			chb_auto.setChecked(is_auto);
			chb_auto.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					is_auto = isChecked;
					
					
				}});
		}catch(Exception e){
			log(e);
		}
	}
}
