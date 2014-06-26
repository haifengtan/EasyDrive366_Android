package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import cn.count.easydrive366.components.ViolationDetailItem;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;

public class ViolationSearchActivity extends BaseHttpActivity {
	private LinearLayout _tableLayout;
	private JSONObject _result;
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_violationsearch_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		this.setTitle("违章查询");
		restoreFromLocal(1);
		//reload=1;
		reload_data();
		this.setupScrollView();
		
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_for_illegallys(), 1);
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		
		if (msgType==1){
			if (this.isSuccess(result)){
				initData((JSONObject)result);
			}
		}
	}
	private void initData(JSONObject json){
		try{
			_result = json.getJSONObject("result");
			
			this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					initView();
					
				}});

		}catch(Exception e){
			log(e);
		}
		
	}
	private void initView(){
		this.endRefresh();
		_tableLayout = (LinearLayout)findViewById(R.id.table_modules_violationsearch_table);
		_tableLayout.removeAllViewsInLayout();
		try{
			JSONArray list= _result.getJSONArray("data");
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				addTableRow(item.getString("Address"),item.getString("Reason"),item.getString("Fine"),item.getString("Mark"),
						item.getString("OccurTime"),
						item.getString("id"));
			}
			
		}catch(Exception e){
			log(e);
		}
	}
	private void addTableRow(final String address,final String reason,final String fine,final String mark,final String occurTime,final String id){
		
		ViolationDetailItem item = new ViolationDetailItem(this,null);
		item.setData(address,reason,fine,mark,occurTime);
		
		_tableLayout.addView(item);
		item.setOnClickListener(new View.OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ViolationSearchActivity.this,ViolationDetailActivity.class);
				intent.putExtra("id", id);
				startActivity(intent);
				
			}
		});
	}
	
}
