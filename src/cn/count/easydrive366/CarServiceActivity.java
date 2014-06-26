package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.count.easydrive366.BaseListViewActivity.MyAdapter;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydrive366.carservice.ServiceDetailActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.Menus;

public class CarServiceActivity extends BaseListViewActivity {
	
	private MyAdapter _adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_helpcall_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		resource_listview_id = R.id.moudles_helpcall_listview;
		resource_listitem_id = R.layout.module_listitem;
		restoreFromLocal(1);
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_for_get_carservice(), 1);
	}
	protected void initData(Object result,int msgType){
		try{
			
			JSONArray list = ((JSONObject)result).getJSONObject("result").getJSONArray("data");
			//_companyname = ((JSONObject)result).getJSONObject("result").getString("company");
			
			//this.setupCompanyAndPhone(result);
			 _phone =((JSONObject)result).getJSONObject("result").getString("phone");
			initList(list);
		}catch(Exception e){
			log(e);
		}
		
	}
	@Override
	protected void onListItemClick(final View view,final long index){
		//System.out.println("click");
		if (_list!=null){
			Map<String,Object> map = _list.get((int) index);
			final String code = map.get("Code").toString();
			//Intent intent = new Intent(this,CarServiceDetailActivity.class);
			Intent intent = new Intent(this,CarServiceNoteActivity.class);
			intent.putExtra("code", code);
			intent.putExtra("pageId", "12");
			startActivity(intent);
			
		}
	}
	
	@Override
	protected void initView(){
		/*
		if (_adapter==null){
			ListView lv = (ListView)findViewById(resource_listview_id);
			_adapter =new MyAdapter(HelpCallActivity.this);
			lv.setAdapter(_adapter);
		}else{
			_adapter.notifyDataSetChanged();
		}
		*/
		super.initView();
		TextView txtCompany = (TextView)findViewById(R.id.txt_helpcall_company);
		//txtCompany.setText(_companyname);
		txtCompany.setText(_company);
	}
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		try{
			
			holder.title.setText(info.get("name").toString());
			holder.detail.setText(info.get("description").toString());
			holder.action.setText(info.get("price").toString());
		}catch(Exception e){
			log(e);
		}
		
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_listitem_detail_title);
		holder.detail = (TextView)convertView.findViewById(R.id.txt_listitem_detail_detail);
		holder.action = (TextView)convertView.findViewById(R.id.txt_listitem_detail_right);
		convertView.setTag(holder);
	}

}
