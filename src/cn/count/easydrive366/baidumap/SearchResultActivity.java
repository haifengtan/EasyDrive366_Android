package cn.count.easydrive366.baidumap;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

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

import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.Menus;

public class SearchResultActivity extends BaseListViewActivity {
	private ProgressDialog _dialog;
	private String _result;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_information_activity);
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		this.setupRightButtonWithText("地图");
		
		this.resource_listview_id = R.id.modules_information_listview;
		//this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.listitem_searchresult;
		
		mListView = (PullToRefreshListView) findViewById(this.resource_listview_id);
		reload_data();
		
	}
	@Override
	protected void reload_data(){
		this.get(String.format("api/get_service_list?userid=%d&type=%s&keyword=%s",AppSettings.userid,getIntent().getStringExtra("types"),getIntent().getStringExtra("key")), 1);
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
			//this.setupCompanyAndPhone(result);
			_result = result.toString();
			JSONArray obj = ((JSONObject)result).getJSONArray("result");		
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
		//Map<String,Object> map = _list.get((int) index);
		Intent intent = new Intent(this,ShowLocationActivity.class);
		intent.putExtra("isFull", false);
		intent.putExtra("index", (int)index);
		intent.putExtra("shoplist",_result);
		startActivity(intent);
		
	}
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("name").toString());
		holder.detail.setText(info.get("description").toString());
		holder.action.setText(info.get("phone").toString());
		//holder.action.setText("");
		
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_listitem_detail_title);
		holder.detail = (TextView)convertView.findViewById(R.id.txt_listitem_detail_detail);
		holder.action = (TextView)convertView.findViewById(R.id.txt_action);
		
		convertView.setTag(holder);
		//LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.layout_delete);
		
	}
	@Override
	protected void onRightButtonPress() {
		Intent intent = new Intent(this,ShowLocationActivity.class);
		intent.putExtra("isFull", false);
		intent.putExtra("shoplist",_result);
		startActivity(intent);
	}
	@Override
	public void processMessage(int msgType,final Object result){
		if (msgType==1){
			super.processMessage(msgType, result);
	}
	}
}
