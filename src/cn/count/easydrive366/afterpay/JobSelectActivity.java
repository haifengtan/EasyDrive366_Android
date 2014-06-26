package cn.count.easydrive366.afterpay;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class JobSelectActivity extends BaseHttpActivity {
	private List<Trade> list;
	private String order_id;
	private int job_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_jobselect_activity);
		this.setupLeftButton();
		getActionBar().setTitle("职业分类");
		this.setupRightButtonWithText("完成");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init_data();
		
	}
	
	@Override
	protected void onRightButtonPress() {
		if (current_jk!=null){
			Intent intent= new Intent();
			intent.putExtra("job_id", current_jk.id);
			intent.putExtra("job_name", current_jk.name);
			this.setResult(RESULT_OK, intent);
			finish();
		}else{
			this.showMessage("请选择一个职业分类",null);
		}
	}

	private void init_data(){
		list = new ArrayList<Trade>();
		Intent intent = getIntent();
		job_id = intent.getIntExtra("job_id", 0);
		order_id = intent.getStringExtra("order_id");
		
		String url = String.format("order/list_career?userid=%d&orderid=%s", AppSettings.userid,order_id);
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
			
			try{
				JSONObject json = new JSONObject(result);
				JSONArray items = json.getJSONArray("result");
				for(int i=0;i<items.length();i++){
					JSONObject temp = items.getJSONObject(i);
					Trade t = new Trade(temp);
					list.add(t);
				}
				init_view();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	private void init_view(){
		LinearLayout sv = (LinearLayout)findViewById(R.id.sv_items);
		sv.removeAllViews();
		for(int i=0;i<list.size();i++){
			JobItem ji = new JobItem(this,null,list.get(i));
			sv.addView(ji);
		}
	}
	public class Trade{
		public String trade_id;
		public String trade_name;
		public List<JobKind> items;
		public Trade(JSONObject json){
			init_json(json);
		}
		private void init_json(JSONObject json){
			try{
				trade_id = json.getString("trade_id");
				trade_name = json.getString("trade_name");
				items = new ArrayList<JobKind>();
				JSONArray list = json.getJSONArray("list");
				for(int i=0;i<list.length();i++){
					JSONObject item = list.getJSONObject(i);
					JobKind jobKind = new JobKind(item);
					items.add(jobKind);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	public class JobKind{
		public int id;
		public String name;
		public String level;
		public String content;
		public boolean isSelect;
		public CheckBox chb;
		public JobKind(JSONObject json){
			init_json(json);
		}
	
		private void init_json(JSONObject json){
			try{
				id = json.getInt("id");
				name = json.getString("name");
				level = json.getString("level");
				content = json.getString("content");
				if (id==job_id){
					isSelect=true;
					set_select(this);
				}else{
					isSelect = false;
				}
			
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		public void  select_changed(boolean isChecked,CheckBox chb){
			JobSelectActivity.this.select_changed(isChecked,chb,this);
		}
	}
	private JobKind current_jk;
	private void set_select(JobKind jk){
		getActionBar().setSubtitle(String.format("选择[%s]", jk.name));
		current_jk = jk;
	}
	private void  select_changed(boolean isChecked,CheckBox chb,JobKind jk){
		int j_id=0;
		if (isChecked){
			j_id = jk.id;
			set_select(jk);
		}
		for(Trade t : list){
			for(JobKind j :t.items){
				if (isChecked && j.id==j_id){
						j.chb.setChecked(true);
						j.isSelect=true;
						continue;
				}
				j.isSelect = false;
				if (j.chb!=null)
					j.chb.setChecked(false);
				
				
			}
		}
	
	}
}
