package cn.count.easydrive366.afterpay;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class AccidentActivity extends BaseHttpActivity {
	private String data;
	private String order_id;
	private EditText edt_name;
	private EditText edt_cellphone;
	private TextView txt_job;
	private EditText edt_idcard;
	/*
	private JSONArray type_list;
	private String current_type;
	*/
	private int job_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_afterpay_accident);
		this.setupLeftButton();
		getActionBar().setTitle("保险信息");
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
		if (edt_idcard.getText().toString().trim().isEmpty()){
			this.showMessage("身份证号码不能为空！",null);
			return;
		}
		if (edt_cellphone.getText().toString().trim().isEmpty()){
			this.showMessage("手机不能为空！",null);
			return;
		}
		if (job_id==0){
			this.showMessage("请选择职业分类！",null);
			return;
		}
		
		
		String url = String.format("order/order_ins_accident?userid=%d&orderid=%s&name=%s&phone=%s&type=%d&idcard=%s",
				AppSettings.userid,
				order_id,
				edt_name.getText().toString(),
				edt_cellphone.getText().toString(),
				job_id,
				edt_idcard.getText().toString());
				
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				JSONObject json = AppSettings.getSuccessJSON(result,AccidentActivity.this);
				if (json!=null){
					Intent intent = new Intent(AccidentActivity.this,FinishedActivity.class);
					intent.putExtra("data", json.toString());
					startActivity(intent);
					AccidentActivity.this.finish();
				}
				
				
			}}.execute(url);
	}
	private String[] types;
	private int index=-1;
	private void init_view(){
		edt_name = (EditText)findViewById(R.id.edt_name);
		edt_cellphone = (EditText)findViewById(R.id.edt_cellphone);
		edt_idcard = (EditText)findViewById(R.id.edt_idcard);
		txt_job = (TextView)findViewById(R.id.txt_job);
		data = getIntent().getStringExtra("data");
		try{
			JSONObject json = new JSONObject(data);
			
			order_id  = json.getString("order_id");
			edt_name.setText(json.getString("name"));
			edt_cellphone.setText(json.getString("phone"));
			
			edt_idcard.setText(json.getString("idcard"));
			txt_job.setText(json.getString("type_name"));
			/*
			type_list = json.getJSONArray("list_type");
			current_type = json.getString("type");
			
			types = new String[type_list.length()];
			for(int i=0;i<type_list.length();i++){
				types[i] = type_list.getJSONObject(i).getString("label");
				if (current_type.equals(type_list.getJSONObject(i).getString("value"))){
					index =i;
				}
			}
			*/
			findViewById(R.id.row_job).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					choose_job();
					
				}});
			job_id = json.getInt("type");
		}catch(Exception e){
			log(e);
		}
	}
	private void choose_job(){
		Intent intent = new Intent(this,JobSelectActivity.class);
		intent.putExtra("order_id", this.order_id);
		intent.putExtra("job_id", job_id);
		startActivityForResult(intent,1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==1 && resultCode==Activity.RESULT_OK && data!=null){
			job_id =data.getIntExtra("job_id", job_id);
			txt_job.setText(data.getStringExtra("job_name"));
			
		}
	}
	/*
	private void choose_job_old(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		
		builder.setSingleChoiceItems(types, index, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				index = which;
				
			}
		});
		builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				txt_job.setText(types[index]);
			}
		});
		builder.setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
		builder.show();
	}
	*/
}
