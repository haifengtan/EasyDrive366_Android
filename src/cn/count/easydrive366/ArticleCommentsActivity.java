package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.Menus;

public class ArticleCommentsActivity extends BaseListViewActivity {
	
	private ProgressDialog _dialog;
	private String _column_id;
	private String _id;
	private String _title;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_articlecomments_activity);
		this.setupLeftButton();
		Intent intent = getIntent();
		_column_id = intent.getStringExtra("column_id");
		_id = intent.getStringExtra("id");
		_title = intent.getStringExtra("title");
		this.setBarTitle(_title);
		this.setupRightButtonWithText("发表");
		
		
		this.resource_listview_id = R.id.modules_information_listview;
		//this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.module_listview_comments_items;
		restoreFromLocal(1);
		
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		
		this.get(String.format("article/get_review?user_id=%d&column_id=%s&article_id=%s", AppSettings.userid,
				_column_id,_id), 1);
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
			//this.setupCompanyAndPhone(result);
			
			JSONArray obj = ((JSONObject)result).getJSONArray("result"); 	
			this.initList(obj);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		String content = String.format("%s(%d个字)", info.get("content").toString(),info.get("content").toString().length());
		holder.title.setText(content);
		String s = String.format("%s发布于%s",info.get("user_name").toString(),info.get("fmt_create_time").toString());
		holder.detail.setText(s);
		
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_content);
		holder.detail = (TextView)convertView.findViewById(R.id.txt_date);
		//holder.action = (TextView)convertView.findViewById(R.id.txt_listitem_detail_right);
		convertView.setTag(holder);
	}
	@Override 
	protected void onRightButtonPress() {
		Intent intent = new Intent(this,AddCommentsActivity.class);
		intent.putExtra("title", _title);
		intent.putExtra("id", _id);
		intent.putExtra("column_id", _column_id);
		startActivity(intent);
	}
}
