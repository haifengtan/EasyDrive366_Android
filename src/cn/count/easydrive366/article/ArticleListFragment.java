package cn.count.easydrive366.article;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.constant.WBConstants;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.baidumap.SearchShopActivity;
import cn.count.easydrive366.comments.ItemCommentsActivity;
import cn.count.easydrive366.share.FavorController;
import cn.count.easydrive366.share.ShareController;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseListViewFragment;
import cn.count.easydriver366.base.BaseListViewV4Fragment;

public class ArticleListFragment extends BaseListViewV4Fragment implements Response {
	private boolean _isSearching=false;
	private String _types;
	private String _key;
	private ImageView _imageView;
	private List<Album> _imageList;
	private int _index;
	private ShareController _share;
	private IWeiboShareAPI _weibo;
	private TextView txt_index;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		containerView = inflater.inflate(R.layout.modules_article_list,container,false);
		
		this.resource_listview_id = R.id.modules_information_listview;
		this.resource_listitem_id = R.layout.listitem_article;
		_weibo = WeiboShareSDK.createWeiboAPI(this.getActivity(), AppSettings.SINA_WEIBO_ID);
		_share = new ShareController(this.getActivity(),_weibo);
		
		if (savedInstanceState != null) {
			_weibo.handleWeiboResponse(this.getActivity().getIntent(), this);
		}
		this.setupPullToRefresh();
		_imageView = (ImageView)containerView.findViewById(R.id.img_picture);
		txt_index = (TextView)containerView.findViewById(R.id.txt_index);
		
		this.addSwipeToView(_imageView);
		this.cache_load(1);
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
	private int _select_share_index=-1;
	private void begin_share(final Object obj){
		Map<String,Object> map = (Map<String,Object>)obj;
		_share.setContent(map.get("share_title").toString(),
				map.get("share_intro").toString(),
				map.get("share_url").toString());
		_share.shareByIndex(_select_share_index);
	}
	private void do_share_on(final Object obj){
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
	private void do_favor_on(final Object obj,ImageView view){
		FavorController favor = new FavorController(this);
		Map<String,Object> map = (Map<String,Object>)obj;
		int is_favor = Integer.parseInt(map.get("is_favor").toString());
		favor.init(is_favor, view, map.get("id").toString(), "ATL");
		favor.click_menu();
		map.put("is_favor", is_favor==1?0:1);
	}
	private void openRating(final Object obj){
		Map<String,Object> map = (Map<String,Object>)obj;
		Intent intent =new Intent(this.getActivity(),ItemCommentsActivity.class);
		intent.putExtra("id",map.get("id").toString());
		intent.putExtra("type", "article");
		startActivity(intent);
	}
	@Override
	protected void setupListItem(ViewHolder holder,Map<String,Object> info){
		holder.title.setText(info.get("title").toString());
		holder.detail2.setText(info.get("description").toString());
		
		holder.detail3.setText(info.get("star_voternum").toString());
		holder.ratingbar.setRating(3/*Float.parseFloat(info.get("star").toString())*/);
		//com.koushikdutta.urlimageviewhelper.UrlImageViewHelper.setUrlDrawable
		
		AppTools.loadImageFromUrl(holder.image, info.get("pic_url").toString());
		int is_favor = Integer.parseInt( info.get("is_favor").toString());
		if (is_favor==1){
			holder.image3.setImageResource(R.drawable.favor);
		}else{
			holder.image3.setImageResource(R.drawable.favorno);
		}
		holder.image3.setTag(info);
		holder.image2.setTag(info);
		holder.ratingbar.setTag(info);
	}
	@Override
	protected void initListItem(ViewHolder holder,View convertView){
		holder.title = (TextView)convertView.findViewById(R.id.txt_title);
		holder.detail2 = (TextView)convertView.findViewById(R.id.txt_description);
		
		holder.detail3 = (TextView)convertView.findViewById(R.id.txt_voternum);
		holder.ratingbar =(RatingBar)convertView.findViewById(R.id.rating_bar);
		holder.image = (ImageView)convertView.findViewById(R.id.img_picture);
		holder.image2 = (ImageView)convertView.findViewById(R.id.img_share);
		holder.image3 = (ImageView)convertView.findViewById(R.id.img_favor);
		convertView.setTag(holder);
		holder.image2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				do_share_on(v.getTag());
				
			}});
		holder.image3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				do_favor_on(v.getTag(),(ImageView)v);
				
			}});
		
		holder.ratingbar.setOnTouchListener(new OnTouchListener() {
          

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					openRating(v.getTag());
               }
               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   v.setPressed(true);
               }

               if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                   v.setPressed(false);
               }




               return true;
			}});
		
		
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
