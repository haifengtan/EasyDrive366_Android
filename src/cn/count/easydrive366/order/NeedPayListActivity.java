package cn.count.easydrive366.order;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.count.easydrive366.BaseListViewActivity;
import cn.count.easydrive366.R;

import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.HttpExecuteGetTask;


public class NeedPayListActivity extends BaseListViewActivity{
	private JSONArray results;
	private String _status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_goodslist);
		
		this.setupLeftButton();
		_status = getIntent().getStringExtra("status");
		
		this.resource_listview_id = R.id.modules_information_listview;
		
		if (_status.equals("notpay"))
			this.resource_listitem_id = R.layout.listitem_needpay;
		else
			this.resource_listitem_id = R.layout.listitem_needpay2;
		
		
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		
		String type = getIntent().getStringExtra("type");
		String url =String.format("order/order_list?userid=%d&status=%s", AppSettings.userid,_status);
		if (type!=null && !type.isEmpty()){
			url = String.format("%s&type=%s", url,type);
		}
		this.get(url, 1);
		if ("finished".equals(_status)){
			this.setBarTitle("我的订单");
		}else{
			this.setBarTitle("待付款");
		}
		
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
			
			JSONArray list = ((JSONObject)result).getJSONArray("result");
			results = new JSONArray();
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				JSONObject good = item.getJSONArray("goods").getJSONObject(0);
				good.put("order_id", item.getString("order_id"));
				good.put("order_total", item.getString("order_total"));
				good.put("order_time", item.getString("order_time"));
				good.put("po", item.getString("po"));
				good.put("order_status_name", item.getString("order_status_name"));
				results.put(i,good);
			}
			
			this.initList(results);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onListItemClick(final View view,final long index){
		if (swipeDetector.swipeDetected()){
			if (swipeDetector.getAction()==Action.RL){
				
			}else if (swipeDetector.getAction()==Action.LR){
			
			}
		}
		if (_list!=null){
			
			Map<String,Object> map = _list.get((int) index);
			String order_id = map.get("order_id").toString();
			
			Intent intent = new Intent(this,OrderDetailActivity.class);
			intent.putExtra("order_id", order_id);
			startActivity(intent);
			
			
		}
	}
	
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("name").toString());
		holder.detail.setText(info.get("description").toString());
		holder.detail3.setText(info.get("order_total").toString());
	//	holder.detail4.setText(info.get("quantity").toString());
		
		AppTools.loadImageFromUrl(holder.image, info.get("pic_url").toString());
		
		
		if (!_status.equals("notpay")){
			
			holder.detail4.setText(info.get("order_time").toString());
			holder.detail2.setText(info.get("po").toString());
			holder.detail5.setText(info.get("order_status_name").toString());
		}else{
			holder.button1.setTag(info.get("order_id"));
			holder.btnDelete.setTag(info.get("order_id"));
		}
		
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_name);
		holder.detail = (TextView)convertView.findViewById(R.id.txt_description);
		holder.detail3 = (TextView)convertView.findViewById(R.id.txt_total);
		holder.image = (ImageView)convertView.findViewById(R.id.img_picture);
		
		
		convertView.setTag(holder);
		if (_status.equals("notpay")){
			holder.btnDelete =(Button)convertView.findViewById(R.id.btn_delete);
			holder.button1 =(Button)convertView.findViewById(R.id.btn_buy);
			holder.button1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String id =(String)v.getTag();
					Intent intent = new Intent(NeedPayListActivity.this, NewOrderActivity.class);
					intent.putExtra("order_id", id);
					startActivity(intent);
					NeedPayListActivity.this.finish();
				}});
			holder.btnDelete.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					String id =(String)v.getTag();
					deleteOrder(id);
				}});
		}else{
			holder.detail4 = (TextView)convertView.findViewById(R.id.txt_order_time);
			holder.detail2 = (TextView)convertView.findViewById(R.id.txt_po);
			holder.detail5 = (TextView)convertView.findViewById(R.id.txt_order_status);
		}
		
		
	}
	private void deleteOrder(final String order_id){
		String url = String.format("order/order_del?userid=%d&orderid=%s", AppSettings.userid,order_id);
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				processDeleteResult(result,order_id);
				
			}}.execute(url);
		
	}
	private void processDeleteResult(final String result,final String order_id){
		try{
			JSONObject json = new JSONObject(result);
			if (AppSettings.isSuccessJSON(json,this)){
				for(Map<String,Object> map : _list){
					if (map.get("order_id").toString().equals(order_id)){
						_list.remove(map);
						break;
					}
				}
				_adapter.notifyDataSetChanged();
			}
		}catch(Exception e){
			log(e);
		}
	}
	
	
	

}
