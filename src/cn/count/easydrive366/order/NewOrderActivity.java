package cn.count.easydrive366.order;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Button;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class NewOrderActivity extends BaseHttpActivity {
	private LinearLayout tblItems;
	private String product_id;
	private String order_id;
	private String order_total;
	private String t_id;
	private int min_quantity;
	private int max_quantity;
	private Map<String,Integer> _map= new HashMap<String,Integer>();
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_neworder_activity);
		this.setupLeftButton();
		setBarTitle("新订单");
		Intent intent= getIntent();
		product_id = intent.getStringExtra("id");
		load_data();
	}
	private void load_data(){
		String url;
		if (product_id!=null && !product_id.isEmpty()){
			url = String.format("order/order_new?userid=%d&goodsid=%s", AppSettings.userid,product_id);
		}else{
			order_id = getIntent().getStringExtra("order_id");
			url = String.format("order/order_edit?userid=%d&orderid=%s", AppSettings.userid,order_id);
		}
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				load_view(result);
				
			}}.execute(url);
	}
	private void load_view(final String result){
		tblItems = (LinearLayout)findViewById(R.id.tb_items);
		tblItems.removeAllViews();
		JSONObject json = AppSettings.getSuccessJSON(result,this);
		if (json==null) return;
		try{
			order_id = json.getString("order_id");
			order_total = json.getString("order_total");
			JSONArray goods = json.getJSONArray("goods");
			
			for(int i=0;i<goods.length();i++){
				//TableRow tr = new TableRow(this);
				NewOrderItem item = new NewOrderItem(this, null);
				JSONObject json_item =goods.getJSONObject(i);
				min_quantity = json_item.getInt("min_quantity");
				max_quantity = json_item.getInt("max_quantity");
				t_id = json_item.getString("id");
				item.setData(json_item,min_quantity,max_quantity);
				item.changed = new IOrderQuantityChanged(){

					@Override
					public void changed(String id, int quantity) {
						_map.put(id, quantity);
						
					}};
				//tr.addView(item);
				tblItems.addView(item);
			}
			findViewById(R.id.btn_submit).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					submit();
					
				}});
		}catch(Exception e){
			log(e);
		}
	}
	private void submit(){
		if (_map.size()==0 && t_id!=null && !t_id.isEmpty()){
			_map.put(t_id, 1);
		}
		StringBuilder sb = new StringBuilder();
		for(String key :_map.keySet()){
			sb.append(String.format("goodsid=%s&quantity=%d&", key,_map.get(key)));
		}
		
	
		String url = String.format("order/order_save?userid=%d&%sorderid=%s", AppSettings.userid,sb.toString(),order_id);
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				processResult(result);
				
			}}.execute(url);
		
	}
	private void processResult(final String result){
		try{
			JSONObject json = new JSONObject(result);
			if (AppSettings.isSuccessJSON(json,this)){
				Intent intent = new Intent(this,PayActivity.class);
				intent.putExtra("json", result);
				startActivity(intent);
				finish();
				
			}
		}catch(Exception e){
			log(e);
		}
	
	}
}
