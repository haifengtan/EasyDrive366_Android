package cn.count.easydrive366.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.constant.WBConstants;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydrive366.baidumap.ShowLocationActivity;
import cn.count.easydrive366.comments.ItemCommentsActivity;
import cn.count.easydrive366.goods.GoodsDetailActivity;
import cn.count.easydrive366.goods.GoodsListFragment;
import cn.count.easydrive366.order.NewOrderActivity;
import cn.count.easydrive366.share.FavorController;
import cn.count.easydrive366.share.ShareController;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class ProviderDetailActivity extends BaseHttpActivity  implements Response {
	protected List<Map<String,Object>> _list=null;
	private String code;
	
	private List<Album> _imageList;
	private int _index=-1;
	private ImageView _imageView;
	private TextView txtIndex;
	private ShareController _share;
	private MenuItem _menuFavor;
	private FavorController _favor;
	private IWeiboShareAPI _weibo;
	private String _content_url;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_provider_detail);
		this.setupLeftButton();
		this.setTitle("详情");
		_favor = new FavorController(this);
		_weibo = WeiboShareSDK.createWeiboAPI(this, AppSettings.SINA_WEIBO_ID);
		if (savedInstanceState != null) {
			_weibo.handleWeiboResponse(getIntent(), this);
		}
		_share = new ShareController(this,_weibo);
		code = getIntent().getStringExtra("code");
		if (code!=null && !code.isEmpty()){
			beginHttp();
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					endHttp();
					load_view(result);
					
				}}.execute(String.format("api/get_service_info?userid=%d&code=%s", AppSettings.userid,code));
			
		}
		_imageView = (ImageView)findViewById(R.id.img_picture);
		this.addSwipeToView(_imageView);
		findViewById(R.id.layout_rating).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openRating();
				
			}
		});
		findViewById(R.id.layout_phone).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openPhone();
				
			}
		});
		findViewById(R.id.layout_address).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openAddress();
				
			}
		});
		findViewById(R.id.layout_content).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openContent();
				
			}
		});
		txtIndex = (TextView)findViewById(R.id.txt_index);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.share_menu, menu);
	      _menuFavor = menu.findItem(R.id.action_favor);
	      return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (R.id.action_favor==item.getItemId()){
			
		}else{
			_share.share(item.getItemId());
		}
		
		return super.onOptionsItemSelected(item);
	}
	private void openRating(){
		Intent intent =new Intent(this,ItemCommentsActivity.class);
		intent.putExtra("id",code);
		intent.putExtra("type", "provider");
		startActivityForResult(intent,5);
	}
	private void openPhone(){
		Uri uri =Uri.parse(String.format("tel:%s",_phone)); 
		
		Intent it = new Intent(Intent.ACTION_VIEW,uri); 
		startActivity(it); 
	}
	
	private void openContent(){
		if (_content_url!=null && !_content_url.isEmpty()){
			Intent intent = new Intent(this,BrowserActivity.class);
			intent.putExtra("url", _content_url);
			startActivity(intent);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==5){
			((TextView)findViewById(R.id.txt_star)).setText(data.getStringExtra("star"));
			((TextView)findViewById(R.id.txt_voternum)).setText(data.getStringExtra("star_voternum"));
			((RatingBar)findViewById(R.id.rating_bar)).setRating(data.getIntExtra("star_num",(int)((RatingBar)findViewById(R.id.rating_bar)).getRating()));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void openAddress(){
		Intent intent = new Intent(this,ShowLocationActivity.class);
		intent.putExtra("isFull", false);
		try{
			JSONObject result=new JSONObject();
			result.put("status", "success");
			JSONArray list = new JSONArray();
			list.put(json);
			result.put("result", list);
			intent.putExtra("shoplist", result.toString());
		}catch(Exception e){
			log(e);
		}
		startActivity(intent);
	}
	private JSONObject json;
	private void load_view(final String result){
		json =  AppSettings.getSuccessJSON(result,this);
		if (json==null) return;
		try{
			LinearLayout items = (LinearLayout)findViewById(R.id.layout_items);
			items.removeAllViewsInLayout();
			((TextView)findViewById(R.id.txt_name)).setText(json.getString("name"));
			((TextView)findViewById(R.id.txt_phone)).setText(json.getString("phone"));
			((TextView)findViewById(R.id.txt_address)).setText(json.getString("address"));
			((TextView)findViewById(R.id.txt_star)).setText(json.getString("star"));
			((TextView)findViewById(R.id.txt_voternum)).setText(json.getString("star_voternum"));
			((RatingBar)findViewById(R.id.rating_bar)).setRating(json.getInt("star_num"));
			((TextView)findViewById(R.id.txt_content)).setText(json.getString("description"));
			_content_url = json.getString("intro_url");
			this.loadImageFromUrl(_imageView, json.getString("pic_url"), R.drawable.default_640x234);
			_phone =json.getString("phone");
			_share.setContent(json);
			_favor.init(json.getInt("is_favor"), _menuFavor, code, "SPV");
			JSONArray list = json.getJSONArray("goods");
			_list = new ArrayList<Map<String,Object>>();
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				final Map<String,Object> map = new HashMap<String,Object>();
				
				for(Iterator<String> it = item.keys();it.hasNext();){
					String key = it.next();
					map.put(key, item.getString(key));
					
				}
				_list.add(map);
				ProviderItem g = new ProviderItem(this,null);
				g.setData(map);
				items.addView(g);
				g.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
						int id = Integer.parseInt(map.get("id").toString());
						Intent intent = new Intent(ProviderDetailActivity.this,
								GoodsDetailActivity.class);
						intent.putExtra("id", id);
						startActivity(intent);
						
					}});
			}
			
			_imageList = new ArrayList<Album>();
			list = json.getJSONArray("album");
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				Album album = new Album();
				album.pic_url = item.getString("pic_url");
				album.remark = item.getString("remark");
				_imageList.add(album);
				
			}
			/*
			if (_imageList.size()>0){
				_index = 0;
				showPicture();
			}
			*/
			
		}catch(Exception e){
			log(e);
		}
	}
	private void showPicture(){
		String url = _imageList.get(_index).pic_url;
		this.loadImageFromUrl(_imageView, url,R.drawable.default_640x234);
		txtIndex.setText(String.format("%d/%d", _index+1,_imageList.size()));
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
	
	
	private class Album{
		public String pic_url;
		public String remark;
	}
	@Override
	protected void onNewIntent(Intent intent) {
		//super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		_weibo.handleWeiboResponse(intent, this);
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
