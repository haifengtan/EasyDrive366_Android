package cn.count.easydrive366.card;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import cn.count.easydrive366.BaseListViewActivity;
import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.InformationDetailActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.Menus;

public class CardViewActivity extends BaseListViewActivity {

	private ProgressDialog _dialog;
	private JSONArray jsonList;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_cardview_activity);
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		this.setRightButtonInVisible();
		this.resource_listview_id = R.id.modules_information_listview;
		//this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.module_listitem;
		restoreFromLocal(1);
		
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.get_cardlist(), 1);
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
			//this.setupCompanyAndPhone(result);
			result = ((JSONObject)result).getJSONObject("result");
			jsonList = ((JSONObject)result).getJSONArray("data");	
			/*
			for(int i=0;i<obj.length();i++){
				JSONObject item = obj.getJSONObject(i);
				
			}
			*/
			this.initList(jsonList);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onListItemClick(final View view,final long index){
		try {
			JSONObject obj = jsonList.getJSONObject((int) index);
			Intent intent = new Intent(this,CardDetailActivity.class);
			intent.putExtra("data", obj.toString());
			startActivity(intent);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("title").toString());
		holder.detail.setText(info.get("valid").toString());
		
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_listitem_detail_title);
		holder.detail = (TextView)convertView.findViewById(R.id.txt_listitem_detail_detail);
		
		convertView.setTag(holder);
		
	}
	@Override
	protected void onRightButtonPress() {
		
	}
	@Override
	public void processMessage(int msgType,final Object result){
		if (msgType==1){
			super.processMessage(msgType, result);
	}
	}

}
