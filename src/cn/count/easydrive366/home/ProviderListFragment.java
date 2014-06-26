package cn.count.easydrive366.home;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import cn.count.easydrive366.R;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydrive366.baidumap.SearchShopActivity;
import cn.count.easydrive366.provider.ProviderDetailActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseListViewFragment;

public class ProviderListFragment extends BaseListViewFragment {
	private boolean _isSearching=false;
	private String _types;
	private String _key;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		containerView = inflater.inflate(R.layout.modules_goodslist,container,false);
		
		this.resource_listview_id = R.id.modules_information_listview;
		//this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.listitem_provider;
		
		this.setupPullToRefresh();
		this.cache_load(1);
		return containerView;
	}
	@Override
	protected void reload_data(){
		if (_isSearching){
			this.get(String.format("api/get_service_list?userid=%d&type=%s&keyword=%s", AppSettings.userid,_types,_key), 1);
			_isSearching = false;
			
		}else{
			this.get(String.format("api/get_service_list?userid=%d", AppSettings.userid), 1);
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
			String code = map.get("code").toString();
			Intent intent = new Intent(this.getActivity(),ProviderDetailActivity.class);
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
	public void onRightButtonPress() {
		Intent intent = new Intent(this.getActivity(),SearchShopActivity.class);
		intent.putExtra("isSearching", true);
		intent.putExtra("type", "provider");
		intent.putExtra("title", "Provider");
		this.startActivityForResult(intent, 1);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode==1 && resultCode==android.app.Activity.RESULT_OK){
			_types= intent.getStringExtra("types");
			_key = intent.getStringExtra("key");
			_isSearching = true;
			this.reload_data();
		}
	}

}
