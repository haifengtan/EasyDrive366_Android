package cn.count.easydrive366;


import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;


import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CheckBox;



import cn.count.easydrive366.user.Task;
import cn.count.easydrive366.user.TaskDispatch;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.Menus;


public class InformationActivity extends BaseListViewActivity {
	
	private ProgressDialog _dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_information_activity);
		this.setupLeftButton();
		
		this.setupRightButtonWithText("删除");
		this.resource_listview_id = R.id.modules_information_listview;
		//this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.module_listitem_information;
		restoreFromLocal(1);
		
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_for_get_news(), 1);
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
			//this.setupCompanyAndPhone(result);
			result = ((JSONObject)result).getJSONObject("result");
			JSONArray obj = ((JSONObject)result).getJSONArray("data");		
			for(int i=0;i<obj.length();i++){
				JSONObject item = obj.getJSONObject(i);
				item.put("selected", false);
			}
			this.initList(obj);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onListItemClick(final View view,final long index){
		
		if (_list!=null){
			
			Map<String,Object> map = _list.get((int) index);
			if (_isInDeleting){
				ViewHolder holder = (ViewHolder)view.getTag();
				holder.selected.toggle();
				map.put("selected", holder.selected.isChecked());
				
				return;
			}
			/*
			TextView txtTitle = (TextView)view.findViewById(R.id.txt_listitem_detail_title);
			TextView txtDetail = (TextView)view.findViewById(R.id.txt_listitem_detail_detail);
			if (txtTitle!=null){
				txtTitle.setTextColor(Color.GRAY);
			}
			if (txtDetail!=null){
				txtDetail.setTextColor(Color.GRAY);
			}*/
			view.findViewById(R.id.reddot).setVisibility(View.INVISIBLE);
			map.put("is_readed", 1);
			final String action = map.get("action").toString();
			final String url = map.get("url").toString();
			if (!url.equals("")){
				//open browser;
				getInformation(map.get("id").toString());
				Intent intent = new Intent(this,BrowserActivity.class);
				intent.putExtra("url", url);
				startActivity(intent);
			}else if (!action.equals("01")){
				Task task = new Task();
				task.ation_url = "";
				task.page_id = action;
				task.id =Integer.parseInt(map.get("id").toString());
				new TaskDispatch(this,task).execute();
				/*
				getInformation(map.get("id").toString());
				Menus menus = new Menus(this);
				//Class<?> intentClass = menus.findMenuItemClassByKey(action);
				HomeMenu item = menus.findMenuByKey(action);
				if (item!=null){
					Intent intent = new Intent(this,item.activityClass);
					intent.putExtra("title", item.name);
					startActivity(intent);
				}
				*/
			}else{
				Intent intent = new Intent(this,InformationDetailActivity.class);
				intent.putExtra("id", map.get("id").toString());
				startActivity(intent);
			}
			
		}
	}
	private void getInformation(final String id){
		this.get(String.format("api/get_news_by_id?userid=%d&newsid=%s",AppSettings.userid, id), -1);
	}
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("fmt_createDate").toString());
		holder.detail.setText(info.get("description").toString());
		//holder.action.setText(info.get("action").toString());
		//holder.action.setText("");
		if (_isInDeleting){
			holder.selected.setChecked(info.get("selected").toString().equals("true"));
		}
		
		String is_readed = info.get("is_readed").toString();
		if (is_readed.equals("0")){
			//holder.title.setTextColor(Color.RED);
			//holder.detail.setTextColor(Color.BLUE);
			
			
		}else{
			holder.image.setVisibility(View.INVISIBLE);
		}
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_listitem_detail_title);
		holder.detail = (TextView)convertView.findViewById(R.id.txt_listitem_detail_detail);
		//holder.action = (TextView)convertView.findViewById(R.id.txt_listitem_detail_right);
		holder.selected = (CheckBox)convertView.findViewById(R.id.chk_item);
		holder.image = (ImageView)convertView.findViewById(R.id.reddot);
		convertView.setTag(holder);
		//LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.layout_delete);
		
	}
	@Override
	protected void onRightButtonPress() {
		if (!_isInDeleting){
			_isInDeleting = true;
			this.setupRightButtonWithText("完成");
		
			
		}else{
			_isInDeleting =false;
			this.setupRightButtonWithText("删除");
			StringBuilder sb = new StringBuilder();
			for(int i=_list.size()-1;i>-1;i--){
				Map<String,Object> map = _list.get(i);
				if (map.get("selected").toString().equals("true")){
					sb.append(map.get("id").toString()+",");
					_list.remove(i);
				}
				
			}
			if (sb.length()>0){
				this.get(String.format("api/del_news?userid=%d&newsid=%s",AppSettings.userid,sb.toString()),2);
				
			}
			
			
		}
		//_adapter.notifyDataSetChanged();
		this.runOnUiThread(
				new Runnable(){

					@Override
					public void run() {
						_adapter.notifyDataSetChanged();
						
				
					}
			
				}
		);
	}
	@Override
	public void processMessage(int msgType,final Object result){
		if (msgType==1){
			super.processMessage(msgType, result);
	}
	}
	
}
