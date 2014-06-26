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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class BoundActivity extends BaseHttpActivity {
	private Button btnGo;
	private TextView txtContent;
	private ListView lvItems;
	private BoundAdapter _adapter;
	private List<Bound> _list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_userbound);
		this.setBarTitle("积分交易明细");
		
		this.setupLeftButton();
		init_view();
		load_data();
	}
	private void init_view(){
		btnGo = (Button)findViewById(R.id.btnGo);
		txtContent = (TextView)findViewById(R.id.txtContent);
		lvItems=(ListView)findViewById(R.id.lvItems);
		btnGo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BoundActivity.this,TaskListActivity.class);
				startActivity(intent);
				
			}});
	}
	private void load_data(){
		String url = String.format("bound/get_bounds_his?userid=%d", AppSettings.userid);
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
		if (json==null){
			return;
		}
		try{
			txtContent.setText(json.getString("content"));
			JSONArray tempList = json.getJSONArray("data");
			_list = new ArrayList<Bound>();
			for(int i=0;i<tempList.length();i++){
				JSONObject item = tempList.getJSONObject(i);
				Bound bound = new Bound();
				bound.bound = item.getString("bounds");
				bound.date = item.getString("from_time");
				bound.memo = item.getString("from_type");
				bound.left = item.getString("this_bounds");
				_list.add(bound);
			}
			if (_adapter==null){
				_adapter = new BoundAdapter(this);
				lvItems.setAdapter(_adapter);
				
			}else{
				_adapter.notifyDataSetChanged();
			}
		}catch(Exception e){
			log(e);
		}
	}
	private class Bound{
		public String date;
		public String bound;
		public String memo;
		public String left;
	}
	private class ViewHolder{
		public TextView txtDate;
		public TextView txtBound;
		public TextView txtMemo;
		public TextView txtLeft;
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
				convertView = mLayoutInflater.inflate(R.layout.listitem_boundlist, null);
				holder.txtBound = (TextView)convertView.findViewById(R.id.txtBound);
				holder.txtDate = (TextView)convertView.findViewById(R.id.txtDate);
				holder.txtMemo = (TextView)convertView.findViewById(R.id.txtMemo);
				holder.txtLeft = (TextView)convertView.findViewById(R.id.txtLeft);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			Bound bound = _list.get(position);
			holder.txtBound.setText(bound.bound);
			holder.txtDate.setText(bound.date);
			holder.txtMemo.setText(bound.memo);
			holder.txtLeft.setText(bound.left);
			return convertView;
		}
		
	}
}
