package cn.count.easydrive366.goods;


import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import cn.count.easydrive366.BaseListViewActivity;
import cn.count.easydrive366.R;

import cn.count.easydrive366.baidumap.ShowLocationActivity;
import cn.count.easydrive366.provider.ProviderDetailActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;

public class GoodsShopListActivity extends BaseListViewActivity {
	private int goods_id;
	private String shoplist;
	private boolean has_data;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.modules_goodslist);
		this.setupRightButtonWithText("地图");
		this.setupPhoneButtonInVisible();
		this.setBarTitle("服务网点");
		this.setupLeftButton();
		this.resource_listview_id = R.id.modules_information_listview;
		//this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.listitem_provider;
		restoreFromLocal(1);
		goods_id = getIntent().getIntExtra("id", 0);
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		has_data = false;
		
		this.get(String.format("goods/list_goods_service?userid=%d&id=%d", AppSettings.userid,goods_id), 1);
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
		
			JSONArray list = ((JSONObject)result).getJSONArray("result");
			shoplist = result.toString();
			has_data =true;
			
			this.initList(list);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onListItemClick(final View view,final long index){
		
		if (_list!=null){
			
			Map<String,Object> map = _list.get((int) index);
			String code = map.get("code").toString();
			Intent intent = new Intent(this,ProviderDetailActivity.class);
			intent.putExtra("code", code);
			startActivity(intent);
			
		}
	}
	
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("name").toString());
		holder.detail2.setText(info.get("address").toString());
		holder.detail3.setText(info.get("phone").toString());
		holder.detail4.setText(info.get("star_voternum").toString());
		holder.ratingbar.setRating(Float.parseFloat(info.get("star_num").toString()));
		AppTools.loadImageFromUrl(holder.image, info.get("pic_url").toString());
		
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_title);
		holder.detail2 = (TextView)convertView.findViewById(R.id.txt_phone);
		holder.detail3 = (TextView)convertView.findViewById(R.id.txt_address);
		holder.detail4 = (TextView)convertView.findViewById(R.id.txt_voternum);
		holder.ratingbar =(RatingBar)convertView.findViewById(R.id.rating_bar);
		holder.image = (ImageView)convertView.findViewById(R.id.img_picture);
		convertView.setTag(holder);
	
		
	}
	@Override
	protected void onRightButtonPress() {
		if (has_data){
			Intent intent = new Intent(this,ShowLocationActivity.class);
			intent.putExtra("isFull", false);
			intent.putExtra("shoplist", shoplist);
			startActivity(intent);
		}
	}
	
	
}
