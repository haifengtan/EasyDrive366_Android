package cn.count.easydrive366;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseExpandableListActivity;

public class CarServiceWikiActivity extends BaseExpandableListActivity {
	
	private String code;
	private String pageId;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_carservice_wiki_activity);
		Intent intent = this.getIntent();
		code = intent.getStringExtra("code");
		pageId = intent.getStringExtra("pageId");
		this.setup_listview();
		reload_data();
		
		
	}
	@Override
	protected void setup_listview(){
		this.setRightButtonInVisible();
		this.setupLeftButton();
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		if (!title.equals("")){
			setBarTitle(title);
		}
		mListView = (PullToRefreshExpandableListView)findViewById(R.id.expandableListView1);
		
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				onListViewItemClick(parent,view,position,id);
			}});
		
	}
	@Override
	protected void reload_data(){
		this.get(String.format("article/get_article_list?user_id=%d&&column_id=%s%s2", AppSettings.userid,pageId,code), 1);
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		try{
			final JSONArray list = ((JSONObject)result).getJSONArray("result");
			//_result = _result.getJSONObject("data");
			Log.d("Http", list.toString());
			
			this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					//_adapter.setData(_result);
					init_list(list);
				}
				
			});
			

		}catch(Exception e){
			log(e);
		}
		
	}
	
	protected void init_list(JSONArray _result){
		groups.clear();
		items.clear();
		marks.clear();
		try{
			for(int i=0;i<_result.length();i++){
				JSONObject article = _result.getJSONObject(i);
				String key = article.getString("catalog_name");
				Map<String,String> map = new HashMap<String,String>();
				map.put("key", key);
				groups.add(map);
				marks.add(article.getString("catalog_name"));
				JSONArray list =article.getJSONArray("article");
				ArrayList<Map<String,String>> target = new ArrayList<Map<String,String>>();
				
				for(int j=0;j<list.length();j++){
					Map<String,String> mapitem = new HashMap<String,String>();
					JSONObject item = list.getJSONObject(j);
					mapitem.put("author", item.getString("author"));
					mapitem.put("summary", item.getString("summary"));
					mapitem.put("title", item.getString("title"));
					mapitem.put("url", item.getString("url"));
					mapitem.put("fmt_update_time", item.getString("fmt_update_time"));
					mapitem.put("column_id", item.getString("column_id"));
					mapitem.put("id", item.getString("id"));
					
					target.add(mapitem);
				}
				items.add(target);
			}
			if (_adapter==null){
				
				_adapter = new SimpleExpandableListAdapter(this,groups,R.layout.group_section,new String[]{"key"},
						new int[]{R.id.txt_group_section},items,
						R.layout.group_wiki_item,new String[]{"author","title","summary","fmt_update_time"},new int[]{R.id.txt_group_author,R.id.txt_group_title,R.id.txt_group_detail,R.id.txt_group_date});
				
				//mListView.setAdapter((ListAdapter) _adapter);
				setListAdapter(_adapter);
						
			}else{
				_adapter.notifyDataSetChanged();
				mListView.onRefreshComplete();
			}
		}catch(Exception e){
			log(e);
		}
	}
	@Override
	protected void onListViewItemClick(AdapterView<?> parent, View view,
			int position, long id){
		Log.e("ItemClick", parent.toString());
	}
	@Override
	public boolean onChildClick (ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
		Map<String,String> mapitem = items.get(groupPosition).get(childPosition);
		String url = mapitem.get("url");
		Intent intent = new Intent(this,BrowserActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("title", mapitem.get("title"));
		intent.putExtra("column_id", mapitem.get("column_id"));
		intent.putExtra("id", mapitem.get("id"));
		intent.putExtra("title", mapitem.get("title"));
		startActivity(intent);
		return true;
	}

}
