package cn.count.easydriver366.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.count.easydrive366.R;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public abstract class BaseListViewV4Fragment extends BaseHttpV4Fragment {
	protected List<Map<String,Object>> _list=null;
	protected MyAdapter _adapter;
	protected int resource_listview_id;
	protected int resource_listitem_id;
	protected PullToRefreshListView mListView;
	protected boolean _isInDeleting=false;
	
	protected void setupPullToRefresh(){
		this.setupPhoneButtonInVisible();
		mListView = (PullToRefreshListView)containerView.findViewById(this.resource_listview_id);
		
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				reload = 1;
				reload_data();
				
			}
		});

	
		
	}
	protected void cache_load(final int msgType){
		if (this.is_update_expired(msgType)){
			reload_data();
			return;
		}
		String result = this.getOfflineResult(msgType);
		if (result!=null && !result.isEmpty()){
			try{
				processMessage(msgType, new JSONObject(result));
			}catch(Exception e){
				reload_data();
			}
			
		}else{
			reload_data();
		}
	}
	
	@Override
	public void processMessage(int msgType,final Object result){
		Log.e("Http", result.toString());
		try{
			if (this.isSuccess(result)){
				initData(result,msgType);
				
				this.getActivity().runOnUiThread(
						new Runnable(){

							@Override
							public void run() {
								initView();
								
						
							}
					
						}
				);
				
			}
		}catch(Exception e){
			log(e);
		}
	}
	@Override 
	protected void endRefresh(){
		if (mListView!=null){
			mListView.onRefreshComplete();
		}
		super.endRefresh();
		
	}
	abstract protected void initData(Object result,int msgType);
	
	protected void initList(JSONArray list){
		try{
			_list = new ArrayList<Map<String,Object>>();
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				Map<String,Object> map = new HashMap<String,Object>();
				
				for(Iterator<String> it = item.keys();it.hasNext();){
					String key = it.next();
					map.put(key, item.getString(key));
					
				}
				_list.add(map);
			}
		}catch(Exception e){
			log(e);
		}
	}
	protected void initView(){
		if (_adapter==null){
			//ListView lv = (ListView)findViewById(resource_listview_id);
			_adapter =new MyAdapter(this.getActivity());
			mListView.setAdapter(_adapter);
			mListView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					onListItemClick(arg1,arg3);
					
				}});
		}else{
			_adapter.notifyDataSetChanged();
		}
		mListView.onRefreshComplete();
	}
	protected void onListItemClick(final View view,final long index){
		
	}
	
	abstract protected void setupListItem(ViewHolder holder,Map<String,Object> info);
	
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_listitem_detail_title);
		holder.detail = (TextView)convertView.findViewById(R.id.txt_listitem_detail_detail);
		holder.action = (TextView)convertView.findViewById(R.id.txt_listitem_detail_right);
		convertView.setTag(holder);
//		convertView.setBackgroundDrawable(R.drawable.corner_list_item);
		convertView.setBackgroundResource(R.drawable.bk2);
	}
	public class ViewHolder{
		
		public TextView title;
		public TextView detail;
		public TextView action;
		public CheckBox selected;
		public ImageView image;
		public TextView detail2;
		public TextView detail3;
		public TextView detail4;
		public TextView detail5;
		public RatingBar ratingbar;
		public Button button1;
		public Button btnDelete;
		public ImageButton imgButton;
		public ImageView image2;
		public ImageView image3;
		
	}
	public class MyAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater = null;
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			if (_list==null)
				return 0;
			else
				return _list.size();
		}

		@Override
		public Object getItem(int position) {
	
			return position;
		}

		@Override
		public long getItemId(int position) {
	
            return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView==null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(resource_listitem_id, null);
				initListItem(holder,convertView);
				
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			if (holder.selected!=null){
				if (_isInDeleting){
					holder.selected.setVisibility(android.view.View.VISIBLE);
					
					
				}else{
					holder.selected.setVisibility(android.view.View.GONE);
				}
			}
			
			Map<String,Object> info = _list.get(position);
			setupListItem(holder,info);
			return convertView;
		}
		
	}
}
