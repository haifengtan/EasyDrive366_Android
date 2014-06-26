package cn.count.easydrive366.insurance;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import android.widget.TextView;
import android.widget.CheckBox;
import cn.count.easydrive366.BaseListViewActivity;
import cn.count.easydrive366.R;

import cn.count.easydriver366.base.AppSettings;

public class InsuranceList extends BaseListViewActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.modules_goodslist);
		this.setupRightButtonWithText("删除");
		this.setupLeftButton();
		this.setBarTitle("投保单");
		this.resource_listview_id = R.id.modules_information_listview;
		//this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.listitem_insurance;
		
		
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		
		this.get(String.format("ins/list_quote?userid=%d", AppSettings.userid), 1);
		
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
			
			JSONArray list = ((JSONObject)result).getJSONArray("result");
			for(int i=0;i<list.length();i++){
				JSONObject obj= list.getJSONObject(i);
				obj.put("selected", 0);
			}
			this.initList(list);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onListItemClick(final View view,final long index){
		
		if (_list!=null){
			
			Map<String,Object> map = _list.get((int) index);
			String id =map.get("id").toString();
			Intent intent = new Intent(this,BuyInsuranceStep2.class);
			intent.putExtra("id", id);
			intent.putExtra("is_list", true);
			startActivity(intent);
			
		}
	}
	
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("car_id").toString());
		holder.detail2.setText(info.get("price_time").toString());
		holder.detail3.setText(info.get("status_name").toString());
		holder.selected.setTag(info);
		if (info.get("selected").toString().equals("1")){
			holder.selected.setChecked(true);
		}else{
			holder.selected.setChecked(false);
		}
		
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_car_id);
		holder.detail2 = (TextView) convertView.findViewById(R.id.txt_price_time);
		holder.detail3 = (TextView) convertView.findViewById(R.id.txt_status_name);
		View v = convertView.findViewById(R.id.chb_insurance_item);
		holder.selected = (CheckBox)v;
		convertView.setTag(holder);
		holder.selected.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Map<String,Object> info = (Map<String,Object>)buttonView.getTag();
				info.put("selected", isChecked?1:0);
				
			}});
	}
	@Override
	protected void onRightButtonPress() {
		for(int i=0;i<_list.size();i++){
			Map<String,Object> info = _list.get(i);
			if (info.get("selected").toString().equals("1")){
				Log.e("Deleted", info.toString());
			}
		}
	}


}
