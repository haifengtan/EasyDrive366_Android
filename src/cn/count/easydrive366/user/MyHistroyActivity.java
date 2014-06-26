package cn.count.easydrive366.user;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.count.easydrive366.BaseListViewActivity;
import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydrive366.goods.GoodsDetailActivity;
import cn.count.easydrive366.provider.ProviderDetailActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;

public class MyHistroyActivity extends BaseListViewActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_information_activity);
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		getActionBar().setSubtitle("浏览历史");
		this.resource_listview_id = R.id.modules_information_listview;
		
		this.resource_listitem_id = R.layout.listitem_histroy;
		
		
		reload_data();
		this.setupPullToRefresh();
	}
	@Override
	protected void reload_data(){
		String url = String.format("history/find?userid=%d&type=", AppSettings.userid);
		this.get(url, 1);
	}
	@Override
	protected void initData(Object result, int msgType) {
		try{
			
			JSONArray obj = ((JSONObject)result).getJSONArray("result");		
			for(int i=0;i<obj.length();i++){
				JSONObject item = obj.getJSONObject(i);
				item.put("selected", false);
			}
			this.initList(obj);
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_title);
		holder.detail2 = (TextView) convertView.findViewById(R.id.txt_price);
		holder.detail =(TextView) convertView.findViewById(R.id.txt_description);
		holder.detail3 =(TextView) convertView.findViewById(R.id.txt_time);
		holder.image = (ImageView)convertView.findViewById(R.id.img_picture);
		convertView.setTag(holder);
		
		
	}
	@Override
	protected void setupListItem(ViewHolder holder, Map<String, Object> info) {
		String title = String.format("[%s]%s", info.get("type_name").toString(),info.get("title").toString());
		holder.title.setText(title);
		holder.detail2.setText(info.get("price").toString());
		holder.detail.setText(info.get("description").toString());
		holder.detail3.setText(info.get("occur_time").toString());
		AppTools.loadImageFromUrl(holder.image, info.get("pic_url").toString());
		

	}
	@Override
	protected void onListItemClick(final View view,final long index){
		
		if (_list!=null){
			
			Map<String,Object> map = _list.get((int) index);
			String type_id = map.get("type_id").toString();
			if (type_id.equals("ATL")){
				Intent intent = new Intent(this,BrowserActivity.class);
				intent.putExtra("url", map.get("url").toString());
				intent.putExtra("title", map.get("title").toString());
				startActivity(intent);
			}else if (type_id.equals("GDS")){
				Intent intent = new Intent(this,GoodsDetailActivity.class);
				intent.putExtra("id", Integer.parseInt(map.get("content_id").toString()));
				startActivity(intent);
			}else if (type_id.equals("SPV")){
				Intent intent = new Intent(this,ProviderDetailActivity.class);
				intent.putExtra("id", Integer.parseInt(map.get("content_id").toString()));
				startActivity(intent);
				
			}
		}
	}

}
