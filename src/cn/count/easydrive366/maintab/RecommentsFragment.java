package cn.count.easydrive366.maintab;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cn.count.easydrive366.HomeFragment;
import cn.count.easydrive366.MainActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.WelcomeActivity;

import cn.count.easydrive366.components.HomeMenuItem;
import cn.count.easydrive366.signup.Step0Activity;
import cn.count.easydrive366.user.Task;
import cn.count.easydrive366.user.TaskDispatch;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.HttpExecuteGetTask;
import cn.count.easydriver366.base.IRightButtonPressed;
import cn.count.easydriver366.base.Menus;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class RecommentsFragment extends Fragment implements IRightButtonPressed, OnClickListener{
	private LinearLayout _tableLayout;
	private List<HomeMenu> menus;
	
	private Menus mainMenu;
	private ProgressDialog _dialog;
	private boolean _fromCache=true;
	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	View view;
	private ImageView _imageView;
	private TextView txt_index;
	private int _index;
	private List<Album> _imageList;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		view = inflater.inflate(R.layout.frag_recomments,container,false);
		
	
				
		mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				_fromCache= false;
				load_data();
			}
		});

		mScrollView = mPullRefreshScrollView.getRefreshableView();
		
		_imageView = (ImageView)view.findViewById(R.id.img_picture);
		txt_index = (TextView)view.findViewById(R.id.txt_index);
		
		this.addSwipeToView(_imageView);
		get_imagelist();
		load_data();
		return view;
		
	}
	
	/**
	 * 加载推荐列表数据
	 */
	private void load_data(){
		String url = String.format("goods/get_goods_list?userid=%d",
				AppSettings.userid);
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				process_list(result);
				
			}}.execute(url);
		
	}
	
	
	private void process_list(final String result){
		_tableLayout = (LinearLayout)view.findViewById(R.id.tablelout_in_home_activity);
		_tableLayout.removeAllViews();
		JSONArray list = AppSettings.getSuccessJSONList(result);
		if (list!=null){
			for(int i=0;i<list.length();i++){
				JSONObject item = list.optJSONObject(i);
				if (item!=null){
					TableRow tr = new TableRow(this.getActivity());
					GoodsItem c = new GoodsItem(this.getActivity(),null,item);
					tr.addView(c);
					_tableLayout.addView(tr);
				}
			}
		}
		mPullRefreshScrollView.onRefreshComplete();
		
		
	}
	
	
	public void logout(){
		AppSettings.logout(this.getActivity());
		Intent intent = new Intent(RecommentsFragment.this.getActivity(),WelcomeActivity.class);
		startActivity(intent);
		
	}
	public void settingsButtonPress(){
		
		
		
		if (AppSettings.isLogin){
			/*
			Intent intent = new Intent(this.getActivity(),SettingsActivity.class);
			
			startActivityForResult(intent,1);
			*/
			/*
			MainActivity main = (MainActivity)this.getActivity();
			main.gotoTab(4);
			*/
			Intent intent = new Intent(this.getActivity(),Step0Activity.class);
			startActivity(intent);
		}else{
			Intent intent = new Intent(this.getActivity(),WelcomeActivity.class);
			startActivityForResult(intent,2);
		}
		
		
		
	}
	
	
	
	private void showDialog(int title, CharSequence message) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
	    builder.setTitle(title);
	    builder.setMessage(message);
	    builder.setPositiveButton("OK", null);
	    builder.show();
	  }
	
	
	@Override
	public void onRightButtonPress() {
		if (AppSettings.isLogin){
			Intent intent = new Intent(this.getActivity(),Step0Activity.class);
			startActivity(intent);
			//MainActivity main = (MainActivity)this.getActivity();
			//main.gotoTab(4);
		}else{
			Intent intent = new Intent(this.getActivity(),WelcomeActivity.class);
			startActivityForResult(intent,2);
		}
		
		
	}
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    protected GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
   
    
    protected void addSwipeToView(View v){
    	// Gesture detection
        gestureDetector = new GestureDetector(this.getActivity(), new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        v.setOnClickListener(this);
        v.setOnTouchListener(gestureListener);
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	onLeftSwipe();
                	return true;
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	onRightSwipe();
                	return true;
                    
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }
    public void onLeftSwipe(){
    	if (_imageList!=null && _imageList.size()>0){
			_index--;
	    	if (_index<0){
	    		_index = _imageList.size()-1;
	    	}
	    	showPicture();
		}
    }
    public void onRightSwipe(){
    	if (_imageList!=null && _imageList.size()>0){
			_index++;
	    	if (_index>_imageList.size()-1){
	    		_index = 0;
	    	}
	    	showPicture();
		}
    }
    private void showPicture(){
		String url = _imageList.get(_index).pic_url;
		this.loadImageFromUrl(_imageView, url);
		String p = String.format("%d/%d", _index+1,_imageList.size());
		txt_index.setText(p);
	}
    public void loadImageFromUrl(ImageView imageView, final String url) {
		if (imageView == null)
			return;
		if (url == null)
			return;
		if (url.isEmpty())
			return;
		AppTools.loadImageFromUrl(
				imageView, url);
	}
    private void get_imagelist(){
    	String url = String.format("api/get_mainform?userid=%d", AppSettings.userid);
    	new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				process_result(result);
				
			}}.execute(url);
    }
	private void process_result(final String result){
		if (AppSettings.isSuccessJSON(result, this.getActivity())){
			try{
				JSONArray list = (new JSONObject(result)).getJSONArray("result");
				_imageList = new ArrayList<Album>();
				for(int i=0;i<list.length();i++){
					JSONObject item = list.getJSONObject(i);
					Album album = new Album();
					album.pic_url = item.getString("pic_url");
					album.url = item.getString("url");
					album.sortid = item.getInt("sortid");
					album.id = item.getInt("id");
					album.page_id = item.getString("page_id");
					_imageList.add(album);
				}
				if (_imageList.size()>0){
					_index =0;
					showPicture();
				}
			}catch(Exception e){
				
			}
		}
	}
	private class Album{
		public String pic_url;
		public String url;
		public int sortid;
		public int id;
		public String page_id;
	}

	@Override
	public void onClick(View v) {
		//String url = _imageList.get(_index).pic_url;
		if (_index>-1 && _index<_imageList.size()){
			Album album = _imageList.get(_index);
			Task task = new Task();
			task.id = album.id;
			task.page_id = album.page_id;
			task.ation_url = album.url;
			new TaskDispatch(this.getActivity(),task).execute();
		}
	}
}