package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


import cn.count.easydriver366.base.AppSettings;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;

public class InsuranceProcessListActivity extends BaseListViewActivity {
	private MyAdapter _adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_insurance_process_list_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		this.resource_listview_id = R.id.listview;
		this.resource_listitem_id = R.layout.module_listitem;
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_for_insurance_list(), 1);
	}
	@Override
	protected void initData(Object result, int msgType) {
		JSONObject json = (JSONObject)result;
		try{
			JSONArray list = json.getJSONObject("result").getJSONArray("data");
			this.setupCompanyAndPhone(result);
			this.initList(list);
		}catch(Exception e){
			log(e);
		}

	}

	
	//{"money":"1200","date":"2012-12-10","address":"青岛平安保险公司","company":"2012-12-05"}
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("date").toString());
		holder.detail.setText(info.get("address").toString());
		holder.action.setText(info.get("money").toString());
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_listitem_detail_title);
		holder.detail = (TextView)convertView.findViewById(R.id.txt_listitem_detail_detail);
		holder.action = (TextView)convertView.findViewById(R.id.txt_listitem_detail_right);
		convertView.setTag(holder);
	}

}
