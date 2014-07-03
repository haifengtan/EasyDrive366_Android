package cn.count.easydrive366.goods;


import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.count.easydrive366.BaseListViewActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.baidumap.SearchShopActivity;
import cn.count.easydrive366.insurance.BuyInsuranceStep1;
import cn.count.easydrive366.order.NewOrderActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;

public class GoodsListActivity extends BaseListViewActivity {
	private boolean _isSearching=false;
	private String _types;
	private String _key;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.modules_goodslist);
		this.setupRightButtonWithText("分类");
		this.setupPhoneButtonInVisible();
		this.setBarTitle("推荐商品");
		this.setupLeftButton();
		this.resource_listview_id = R.id.modules_information_listview;
		//this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.listitem_goods;
		//搜索关键字
		_key = getIntent().getStringExtra("key");
		//搜索条目分类
		_types= getIntent().getStringExtra("types");
		//是否是搜索页面跳转过来
		_isSearching=getIntent().getStringExtra("isSearching").equals("YES")?true:false;
		//加载数据
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		
		System.out.println("url="+String.format("library/get_list?userid=%d&type=%s&keyword=%s", AppSettings.userid,_types,_key));
		if (_isSearching){
//			this.get(String.format("library/get_list?userid=%d&type=%s&keyword=%s", AppSettings.userid,_types,_key), 1);

			this.get(String.format("goods/get_goods_list?userid=%d&type=%s&keyword=%s", AppSettings.userid,_types,_key), 1);

			_isSearching = false;
			
		}else{
			this.get(String.format("goods/get_goods_list?userid=%d", AppSettings.userid), 1);
		}
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
			
			JSONArray list = ((JSONObject)result).getJSONArray("result");
			
			this.initList(list);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onListItemClick(final View view,final long index){
		
		if (_list!=null){
			
			Map<String,Object> map = _list.get((int) index);
			int id = Integer.parseInt(map.get("id").toString());
			Intent intent = new Intent(this,GoodsDetailActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
			
		}
	}
	
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("name").toString());
		holder.detail2.setText(info.get("price").toString());
		holder.detail3.setText(info.get("stand_price").toString());
		holder.detail4.setText(info.get("discount").toString());
		holder.detail5.setText(info.get("buyer").toString());
		AppTools.loadImageFromUrl(holder.image, info.get("pic_url").toString());
		holder.button1.setTag(info);
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_title);
		holder.detail2 = (TextView) convertView.findViewById(R.id.txt_price);
		holder.detail3 = (TextView) convertView.findViewById(R.id.txt_stand_price);
		holder.detail4 = (TextView) convertView.findViewById(R.id.txt_discount);
		holder.detail5 = (TextView) convertView.findViewById(R.id.txt_buyer);
		holder.image = (ImageView)convertView.findViewById(R.id.img_picture);
		holder.button1 =(Button)convertView.findViewById(R.id.btn_buy);
		convertView.setTag(holder);
	
		holder.button1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Map<String,Object> info =(Map<String,Object>)v.getTag();
				String id = info.get("id").toString();
				if (info.get("is_carins").toString().equalsIgnoreCase("0")){
					Intent intent = new Intent(
							GoodsListActivity.this,
							NewOrderActivity.class);
					intent.putExtra("id", id);
					startActivity(intent);
				}else{
					String web_url = info.get("web_url").toString();
					Intent intent = new Intent(GoodsListActivity.this,BuyInsuranceStep1.class);
					intent.putExtra("web_url", web_url);
					intent.putExtra("goods_id", id);
					startActivity(intent);
				}
			}});
	}
	@Override
	protected void onRightButtonPress() {
		Intent intent = new Intent(this,SearchShopActivity.class);
		intent.putExtra("isSearching", true);
		intent.putExtra("type", "goods");
		intent.putExtra("title", "Goods");
		this.startActivityForResult(intent, 1);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode==1 && resultCode==RESULT_OK){
			_types= intent.getStringExtra("types");
			_key = intent.getStringExtra("key");
			_isSearching = true;
			this.reload_data();
		}
	}
	
	
}
