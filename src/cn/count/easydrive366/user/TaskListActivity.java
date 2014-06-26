package cn.count.easydrive366.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.count.easydrive366.R;

import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class TaskListActivity extends BaseHttpActivity {
	
	private ListView lvItems;
	private BoundAdapter _adapter;
	private List<Task> _list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_standard_listview);
		setBarTitle("我的任务");
		this.setupLeftButton();
		init_view();
		load_data();
	}
	private void init_view(){
	
		lvItems=(ListView)findViewById(R.id.lvItems);
	}
	private void load_data(){
		String url = String.format("bound/get_task_list?userid=%d", AppSettings.userid);
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				load_view(result);
				
			}}.execute(url);
		
	}
	private void load_view(final String result){
		JSONObject json = AppSettings.getSuccessJSON(result,this);
		try{
		
			JSONArray tempList = json.getJSONArray("data");
			_list = new ArrayList<Task>();
			for(int i=0;i<tempList.length();i++){
				JSONObject item = tempList.getJSONObject(i);
				Task task = new Task();
				task.remark = item.getString("remark");
				task.title = item.getString("title");
				task.bounds = item.getString("bounds");
				task.ation_url = item.getString("action_url");
				task.pic_url = item.getString("pic_url");
				task.id= item.getInt("id");
				task.page_id = item.getString("page_id");
				
				_list.add(task);
			}
			if (_adapter==null){
				_adapter = new BoundAdapter(this);
				lvItems.setAdapter(_adapter);
				lvItems.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						onListItemClick(arg2);
						
					}});
				
			}else{
				_adapter.notifyDataSetChanged();
			}
		}catch(Exception e){
			log(e);
		}
		
	}
	private void onListItemClick(final int index){
		Task task = _list.get(index);
		new TaskDispatch(this,task).execute();
	}
	
	private class ViewHolder{
		public TextView txtTitle;
		public TextView txtRemark;
		public ImageView imgPicture;
	}
	private class BoundAdapter extends BaseAdapter{
		private LayoutInflater mLayoutInflater;
		public BoundAdapter(Context context){
		
			mLayoutInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			
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
				convertView = mLayoutInflater.inflate(R.layout.listitem_task, null);
				holder.txtRemark = (TextView)convertView.findViewById(R.id.txt_remark);
				holder.txtTitle = (TextView)convertView.findViewById(R.id.txt_title);
				holder.imgPicture = (ImageView)convertView.findViewById(R.id.img_picture);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			Task task = _list.get(position);
			holder.txtRemark.setText(task.remark);
			holder.txtTitle.setText(task.title);
			loadImageFromUrl(holder.imgPicture, task.pic_url);
			return convertView;
		}
		
	}
}
