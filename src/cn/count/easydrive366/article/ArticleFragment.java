package cn.count.easydrive366.article;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.constant.WBConstants;

import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.R;

import cn.count.easydrive366.baidumap.SearchShopActivity;
import cn.count.easydrive366.comments.ItemCommentsActivity;
import cn.count.easydrive366.share.FavorController;
import cn.count.easydrive366.share.ShareController;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpV4Fragment;
import cn.count.easydriver366.base.BaseListViewV4Fragment.ViewHolder;

public class ArticleFragment extends BaseHttpV4Fragment implements Response,IArticleItem {
	private boolean _isSearching=false;
	protected List<Map<String,Object>> _list=null;
	private String _types;
	private String _key;
	private ImageView _imageView;
	private List<Album> _imageList;
	private int _index;
	private ShareController _share;
	private IWeiboShareAPI _weibo;
	private TextView txt_index;
	private LinearLayout layout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		containerView = inflater.inflate(R.layout.modules_article_fragment,container,false);
		
	
		_weibo = WeiboShareSDK.createWeiboAPI(this.getActivity(), AppSettings.SINA_WEIBO_ID);
		_share = new ShareController(this.getActivity(),_weibo);
		
		if (savedInstanceState != null) {
			_weibo.handleWeiboResponse(this.getActivity().getIntent(), this);
		}
		
		_imageView = (ImageView)containerView.findViewById(R.id.img_picture);
		txt_index = (TextView)containerView.findViewById(R.id.txt_index);
		layout = (LinearLayout)containerView.findViewById(R.id.layout_items);
		this.addSwipeToView(_imageView);
		this.cache_load(1);
		this.setupScrollView();
		return containerView;
	}
	@Override
	public void onClick(View v) {
		show_url();
		
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
	@Override
	public void processMessage(int msgType, final Object result) {

	
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
			this.getActivity().runOnUiThread(new Runnable(){

				@Override
				public void run() {
					if (_imageList.size()>0){
						_index =0;
						showPicture();
					}
					show_datas();

				}});
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private void show_url(){
		String url =_imageList.get(_index).url;
		Intent intent = new Intent(this.getActivity(),BrowserActivity.class);
		intent.putExtra("url", url);
		this.getActivity().startActivity(intent);
	}
	private void showPicture(){
		String url = _imageList.get(_index).pic_url;
		this.loadImageFromUrl(_imageView, url);
		String p = String.format("%d/%d", _index+1,_imageList.size());
		txt_index.setText(p);
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
	private void show_datas(){
		layout.removeAllViews();
		for(int i=0;i<_list.size();i++){
			Map<String,Object> item = _list.get(i);
			ArticleItem ai = new ArticleItem(this.getActivity(),null,item,this);
			layout.addView(ai);
			ai.setTag(item);
			ai.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					onRowClick(v.getTag());
					
				}});
		}
		this.endRefresh();
	}
	
	private void onRowClick(final Object obj){
		Map<String,Object> map = (Map<String,Object>)obj;
		String url = map.get("url").toString();
		Intent intent = new Intent(this.getActivity(),BrowserActivity.class);
		intent.putExtra("url", url);
		startActivity(intent);
	}
	private int _select_share_index=-1;
	private void begin_share(final Object obj){
		Map<String,Object> map = (Map<String,Object>)obj;
		_share.setContent(map.get("share_title").toString(),
				map.get("share_intro").toString(),
				map.get("share_url").toString());
		_share.shareByIndex(_select_share_index);
	}
	@Override
	public void do_share_on(final Object obj){
		// pop up share choose;
		String[] items = {this.getActivity().getResources().getString(R.string.share_weixin),
				this.getActivity().getResources().getString(R.string.share_weixin_friends),
				this.getActivity().getResources().getString(R.string.share_weibo),/*
				this.getActivity().getResources().getString(R.string.share_email),
				this.getActivity().getResources().getString(R.string.share_text)*/};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
		_select_share_index=-1;
		builder.setSingleChoiceItems(items, _select_share_index, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				_select_share_index = which;
				
			}
		});
		builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				begin_share(obj);
			}
		});
		builder.setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
		builder.show();
	}
	@Override
	public void do_favor_on(final Object obj,ImageView view){
		FavorController favor = new FavorController(this);
		Map<String,Object> map = (Map<String,Object>)obj;
		int is_favor = Integer.parseInt(map.get("is_favor").toString());
		favor.init(is_favor, view, map.get("id").toString(), "ATL");
		favor.click_menu();
		map.put("is_favor", is_favor==1?0:1);
	}
	@Override
	public void openRating(final Object obj){
		Map<String,Object> map = (Map<String,Object>)obj;
		Intent intent =new Intent(this.getActivity(),ItemCommentsActivity.class);
		intent.putExtra("id",map.get("id").toString());
		intent.putExtra("type", "article");
		startActivity(intent);
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
	

	@Override
	public void onResponse(BaseResponse arg0) {
		switch (arg0.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			break;
		}

	}
	
}