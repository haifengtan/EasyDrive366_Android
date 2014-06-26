package cn.count.easydrive366;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;


import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpClient;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;


import android.widget.TextView;

public abstract class BaseListViewActivity extends BaseHttpActivity {
	protected List<Map<String,Object>> _list=null;
	protected MyAdapter _adapter;
	protected int resource_listview_id;
	protected int resource_listitem_id;
	protected PullToRefreshListView mListView;
	protected boolean _isInDeleting=false;
	protected SwipeDetector swipeDetector= new SwipeDetector();
	protected void setupPullToRefresh(){
		mListView = (PullToRefreshListView) findViewById(this.resource_listview_id);
		
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				reload = 1;
				reload_data();
				
			}
		});
		
	
		
	}
	
	@Override
	public void processMessage(int msgType,final Object result){
		
		try{
			if (this.isSuccess(result)){
				initData(result,msgType);
				
				this.runOnUiThread(
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
			_adapter =new MyAdapter(this);
			mListView.setAdapter(_adapter);
			mListView.setOnTouchListener(swipeDetector);
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
	public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }
	public class SwipeDetector implements View.OnTouchListener {

	    

	    private static final String logTag = "SwipeDetector";
	    private static final int MIN_DISTANCE = 100;
	    private float downX, downY, upX, upY;
	    private Action mSwipeDetected = Action.None;

	    public boolean swipeDetected() {
	        return mSwipeDetected != Action.None;
	    }

	    public Action getAction() {
	        return mSwipeDetected;
	    }

	    public boolean onTouch(View v, MotionEvent event) {
	        switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN: {
	            downX = event.getX();
	            downY = event.getY();
	            mSwipeDetected = Action.None;
	            return false; // allow other events like Click to be processed
	        }
	        case MotionEvent.ACTION_MOVE: {
	            upX = event.getX();
	            upY = event.getY();

	            float deltaX = downX - upX;
	            float deltaY = downY - upY;

	            // horizontal swipe detection
	            if (Math.abs(deltaX) > MIN_DISTANCE) {
	                // left or right
	                if (deltaX < 0) {
	                    //Logger.show(Log.INFO,logTag, "Swipe Left to Right");
	                    mSwipeDetected = Action.LR;
	                    return true;
	                }
	                if (deltaX > 0) {
	                    //Logger.show(Log.INFO,logTag, "Swipe Right to Left");
	                    mSwipeDetected = Action.RL;
	                    return true;
	                }
	            } else 

	                // vertical swipe detection
	                if (Math.abs(deltaY) > MIN_DISTANCE) {
	                    // top or down
	                    if (deltaY < 0) {
	                        //Logger.show(Log.INFO,logTag, "Swipe Top to Bottom");
	                        mSwipeDetected = Action.TB;
	                        return false;
	                    }
	                    if (deltaY > 0) {
	                       // Logger.show(Log.INFO,logTag, "Swipe Bottom to Top");
	                        mSwipeDetected = Action.BT;
	                        return false;
	                    }
	                } 
	            return true;
	        }
	        }
	        return false;
	    }
	}
}
