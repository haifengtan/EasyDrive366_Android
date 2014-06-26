package cn.count.easydrive366;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;





import cn.count.easydrive366.article.ArticleListActivity;
import cn.count.easydrive366.baidumap.ShowLocationActivity;
import cn.count.easydrive366.components.HomeMenuItem;
import cn.count.easydrive366.goods.GoodsListActivity;
import cn.count.easydrive366.provider.ProviderListActivity;

import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.CheckUpdate;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.Menus;


import cn.count.easydriver366.service.BackendService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private TableLayout _tableLayout;
	private List<HomeMenu> menus;
	private boolean _userWantQuit=false;
	private Timer _quitTimer;
	private ProgressDialog _dialog;
	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if (AppSettings.isquiting){
			finish();
			System.exit(0);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.moudles_home_activity);
		
		AppSettings.restore_login_from_device(this);
		startBackendService();
		setupMenu();
		String rightButtonTitle ="登录";
		if (AppSettings.isLogin){
			rightButtonTitle=this.getResources().getString(R.string.menu_settings);
		}
		((Button)findViewById(R.id.title_set_bn)).setText(rightButtonTitle);
		
		findViewById(R.id.title_set_bn).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				settingsButtonPress();
				
			}
			
		});
		findViewById(R.id.img_navigationbar_logo).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				logoClicked();
				
			}
			
		});
		/*
		findViewById(R.id.btn_map).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gotoMap();
				
			}});
		findViewById(R.id.btn_goods).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gotoGoods();
				
			}});
		findViewById(R.id.btn_provider).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gotoProvider();
				
			}});
		findViewById(R.id.btn_article).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gotoArticle();
				
			}});
		*/
		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				new GetDataTask().execute();
			}
		});

		mScrollView = mPullRefreshScrollView.getRefreshableView();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mPullRefreshScrollView.getWindowToken(), 0);
		new CheckUpdate(this,false);
		
		//this.addSwipeToView(this.mPullRefreshScrollView);
		//com.koushikdutta.urlimageviewhelper.UrlImageViewHelper.setUrlDrawable(imageView, url);
	}
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private boolean _needSleep=false;
		public GetDataTask(){
			super();
		}
		public GetDataTask(boolean needSleep){
			super();
			_needSleep = needSleep;
		}
		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			if (_needSleep){
				try {
					Thread.sleep(2000);
					
				} catch (InterruptedException e) {
				}
			}
			
			
			Intent intent = new Intent("cn.count.easydriver366.service.GetLatestReceiverr");
			intent.putExtra("isInApp", true);
			sendBroadcast(intent);
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here
			_dialog.dismiss();
			
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshScrollView.onRefreshComplete();

			super.onPostExecute(result);
		}
		@Override
		protected void onPreExecute(){
			_dialog = new ProgressDialog(HomeActivity.this);
			_dialog.setMessage(getResources().getString(R.string.app_loading));
			_dialog.show();
		}
		 
	}
	private void startBackendService(){
		if (!isServiceRunning()){
			Intent service = new Intent(this,BackendService.class);
			this.startService(service);
		}
		
		
	}
	private boolean isServiceRunning(){
		boolean result = false;
		ActivityManager mActivityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList=mActivityManager.getRunningServices(50);
		final String serviceName =  "cn.count.easydriver366.service.BackendService";
		for(int i=0;i<mServiceList.size();i++){
			if (mServiceList.get(i).service.getClassName().equals(serviceName)){
				result = true;
				break;
			}
		}
		return result;
	}
	private void setupMenu(){
		View v = findViewById(R.id.btn_phone);
		if (v != null) {
			v.setVisibility(View.GONE);
		}
		
		_tableLayout = (TableLayout)findViewById(R.id.tablelout_in_home_activity);
		initMenuItems();
		fillMenu();
		new GetDataTask(true).execute();
		
	}
	private void addTableRow(HomeMenu menu){
		TableRow tr = new TableRow(this);
		HomeMenuItem item = new HomeMenuItem(this,null);
		item.setData(menu);
		tr.addView(item);
		_tableLayout.addView(tr);
	}
	private void fillMenu(){
		for(int i=0;i<menus.size();i++){
			HomeMenu menu = menus.get(i);
			addTableRow(menu);
		}
	}
	
	private void initMenuItems(){
		menus = (new Menus(this)).getMenus();
	}
	@Override
	public void onBackPressed() {
		if (_userWantQuit){
			AppSettings.isquiting = true;
			this.finish();
			System.exit(0);
		}else{
			Toast.makeText(this, this.getResources().getString(R.string.exit_question), Toast.LENGTH_LONG).show();
			_userWantQuit = true;
			if (_quitTimer==null){
				_quitTimer = new Timer();
			}
			TimerTask task = new TimerTask(){

				@Override
				public void run() {
					_userWantQuit = false;
					
				};
			};
			_quitTimer.schedule(task, 2000);

							
		}
		
	}
	
	public void logout(){
		AppSettings.logout(this);
		Intent intent = new Intent(this,WelcomeActivity.class);
		startActivity(intent);
		
	}
	private void settingsButtonPress(){
		
		
		
		if (AppSettings.isLogin){
			Intent intent = new Intent(this,SettingsActivity.class);
			
			startActivityForResult(intent,1);
		}else{
			Intent intent = new Intent(this,WelcomeActivity.class);
			startActivityForResult(intent,2);
		}
		
		
		
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		String rightButtonTitle ="登录";
		if (AppSettings.isLogin){
			rightButtonTitle=this.getResources().getString(R.string.menu_settings);
		}
		((Button)findViewById(R.id.title_set_bn)).setText(rightButtonTitle);
		new GetDataTask().execute();
		
	   
	  }
	private void showDialog(int title, CharSequence message) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(title);
	    builder.setMessage(message);
	    builder.setPositiveButton("OK", null);
	    builder.show();
	  }
	
	private void logoClicked()
	{
		
	}
	private void gotoMap(){
		Intent intent = new Intent(this,ShowLocationActivity.class);
		
		startActivity(intent);
	}
	private void gotoGoods(){
		Intent intent = new Intent(this,GoodsListActivity.class);
		startActivity(intent);
	}
	private void gotoProvider(){
		Intent intent = new Intent(this,ProviderListActivity.class);
		startActivity(intent);
	}
	private void gotoArticle(){
		Intent intent = new Intent(this,ArticleListActivity.class);
		startActivity(intent);
	}
}
