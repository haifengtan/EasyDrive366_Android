package cn.count.easydrive366.home;

import java.util.ArrayList;
import java.util.List;
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

import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.baidumap.SearchShopActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseListViewFragment;

public class ArticleListFragment extends BaseListViewFragment {
	private boolean _isSearching=false;
	private String _types;
	private String _key;
	private ImageView _imageView;
	private List<Album> _imageList;
	private int _index;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		containerView = inflater.inflate(R.layout.modules_article_list,container,false);
		
		this.resource_listview_id = R.id.modules_information_listview;
		this.resource_listitem_id = R.layout.listitem_article;
		
		this.setupPullToRefresh();
		_imageView = (ImageView)containerView.findViewById(R.id.img_picture);
		this.addSwipeToView(_imageView);
		this.cache_load(1);
		return containerView;
	}
	@Override
	protected void reload_data(){
		if (_isSearching){
			this.get(String.format("library/get_list?userid=%d&type=%s&keyword=%s", AppSettings.userid,_types,_key), 1);
			_isSearching = false;
			
		}else{
			this.get(String.format("library/get_list?userid=%d", AppSettings.userid), 1);
		}
	}
	@Override
	protected void initData(Object result,int msgType){
		try{
			JSONObject json = ((JSONObject)result).getJSONObject("result");
			JSONArray list = json.getJSONArray("data");
			
			this.initList(list);
			list = json.getJSONArray("album");
			_imageList = new ArrayList<Album>();
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				Album album = new Album();
				album.pic_url = item.getString("pic_url");
				album.url = item.getString("url");
				album.sortid = item.getInt("sortid");
				_imageList.add(album);
			}
			if (_imageList.size()>0){
				_index =0;
				showPicture();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private void showPicture(){
		String url = _imageList.get(_index).pic_url;
		this.loadImageFromUrl(_imageView, url);
	}
	@Override
	public void onLeftSwipe(){
		if (_imageList!=null && _imageList.size()>0){
			_index--;
	    	if (_index<0){
	    		_index = _imageList.size()-1;
	    	}
	    	showPicture();
		}
    	
    }
	@Override
    public void onRightSwipe(){
		if (_imageList!=null && _imageList.size()>0){
			_index++;
	    	if (_index>_imageList.size()-1){
	    		_index = 0;
	    	}
	    	showPicture();
		}
    }
	
	@Override
	protected void onListItemClick(final View view,final long index){
		
		if (_list!=null){
			
			Map<String,Object> map = _list.get((int) index);
			String url = map.get("url").toString();
			Intent intent = new Intent(this.getActivity(),BrowserActivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
			
		}
	}
	
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("title").toString());
		holder.detail2.setText(info.get("description").toString());
		
		holder.detail3.setText(info.get("star_voternum").toString());
		holder.ratingbar.setRating(3/*Float.parseFloat(info.get("star").toString())*/);
		AppTools.loadImageFromUrl(holder.image, info.get("pic_url").toString());
		
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_title);
		holder.detail2 = (TextView)convertView.findViewById(R.id.txt_description);
		
		holder.detail3 = (TextView)convertView.findViewById(R.id.txt_voternum);
		holder.ratingbar =(RatingBar)convertView.findViewById(R.id.rating_bar);
		holder.image = (ImageView)convertView.findViewById(R.id.img_picture);
		convertView.setTag(holder);
	
		
	}
	@Override
	public void onRightButtonPress() {
		Intent intent = new Intent(this.getActivity(),SearchShopActivity.class);
		intent.putExtra("isSearching", true);
		intent.putExtra("type", "article");
		intent.putExtra("title", "Article");
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
	private class Album{
		public String pic_url;
		public String url;
		public int sortid;
	}

}
