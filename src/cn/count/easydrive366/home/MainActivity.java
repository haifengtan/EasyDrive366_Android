package cn.count.easydrive366.home;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import cn.count.easydrive366.R;

import cn.count.easydriver366.base.ActionActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.CheckUpdate;
import cn.count.easydriver366.base.IRightButtonPressed;
import cn.count.easydriver366.service.BackendService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends ActionActivity {
	public static MainActivity instance = null;  
    
    private ImageView _imgHome, _imgGoods, _imgProvider, _imgArticle,_imgSettings;  
    
    private HomeFragment _home;
    private boolean _userWantQuit=false;
	private Timer _quitTimer;
    private GoodsListFragment _goods;
    private ProviderListFragment _provider;
    private ArticleListFragment _article;
    private SettingsFragment _settings;
    private Fragment _oldFragment;
    private int _index;
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
       
        setContentView(R.layout.home_main_activity);  
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);// 启动activity时不自动弹出软键盘  
        AppSettings.restore_login_from_device(this);
		startBackendService();
		
      
        
        _imgHome = (ImageView) findViewById(R.id.img_home);  
        _imgGoods = (ImageView) findViewById(R.id.img_goods);  
        _imgProvider = (ImageView) findViewById(R.id.img_provider);  
        _imgArticle = (ImageView) findViewById(R.id.img_article);  
        _imgSettings  = (ImageView) findViewById(R.id.img_settings);
        
        this.addSwipeToView(findViewById(R.id.container));
        findViewById(R.id.layout_home).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				openFragment(0);
				
			}});  
        findViewById(R.id.layout_goods).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				openFragment(1);
				
			}});  
        findViewById(R.id.layout_provider).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				openFragment(2);
				
			}});  
        findViewById(R.id.layout_article).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				openFragment(3);
				
			}});
        findViewById(R.id.layout_settings).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				openFragment(4);
				
			}});
  
        
        _index=0;
        openFragment(_index);
        new CheckUpdate(this,false);
        
    }  
	private void startBackendService(){
		if (!isServiceRunning()){
			Intent service = new Intent(this,BackendService.class);
			startService(service);
		}
		
		
	}
	private boolean isServiceRunning(){
		boolean result = false;
		ActivityManager mActivityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
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
	private void openFragment(final int position){
		Fragment f=null;
		if (position==0){
			if (_home==null){
				_home = new HomeFragment();
			}
			f = _home;
		}else if (position ==1){
			if (_goods==null){
				_goods = new GoodsListFragment();
			}
			f=  _goods;
		}else if (position ==2){
			if (_provider==null){
				_provider = new ProviderListFragment();
			}
			f=  _provider;
		}else if (position ==3){
			if (_article==null){
				_article = new ArticleListFragment();
			}
			f=  _article;
		}else if (position ==4){
			if (_settings==null){
				_settings = new SettingsFragment();
			}
			f=  _settings;
		}
		FragmentTransaction trans = this.getFragmentManager().beginTransaction();
		if (_oldFragment !=null)
			trans.remove(_oldFragment);
		trans.replace(R.id.container, f);
		
		trans.commit();
		
		_oldFragment =f;
		this.onPageSelected(position);
		_index = position;
		
	}
	private MenuItem rightMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.main_menu, menu);
      rightMenu = menu.findItem(R.id.action_settings);
      pageChanged(0);
      return true;
    } 
    public void pageChanged(final int index){
    	switch(index){
    	case 0:
    		if (AppSettings.isLogin)
    			rightMenu.setTitle("设置");
    		else
    			rightMenu.setTitle("登录");
    		break;
    	case 4:
    		rightMenu.setTitle("");
    		break;
    	default:
    		rightMenu.setTitle("分类");
    	}
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("menu click", item.toString());
		Fragment f =null;//= mSectionsPagerAdapter.getItem(mTabPager.getCurrentItem());
		if (f instanceof IRightButtonPressed){
			((IRightButtonPressed)f).onRightButtonPress();
		}
		return super.onOptionsItemSelected(item);
	}
	public void login(){
		pageChanged(0);
		_settings.load_user_profile();
	}
	public void logout(){
		AppSettings.logout(this);
		
		 openFragment(0);
	}
	public void gotoTab(final int index){
		openFragment(index);
	}
	public void onLeftSwipe(){
		_index++;
		if (_index>4)
			_index =4;
		openFragment(_index);
    }
    public void onRightSwipe(){
    	_index --;
    	if (_index<0)
    		_index =0;
    	openFragment(_index);
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
	public void onPageSelected(int index) {  
        
        switch (index) {  
        case 0:  
            _imgHome.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_weixin_pressed));  
            _imgGoods.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_address_normal)); 
            _imgProvider.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_find_frd_normal)); 
            _imgArticle.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_normal)); 
            _imgSettings.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_normal)); 
            break;
        case 1:  
        	_imgHome.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_weixin_normal));  
            _imgGoods.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_address_pressed)); 
            _imgProvider.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_find_frd_normal)); 
            _imgArticle.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_normal)); 
            _imgSettings.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_normal)); 
            
            break;  
        case 2:  
        	_imgHome.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_weixin_normal));  
            _imgGoods.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_address_normal)); 
            _imgProvider.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_find_frd_pressed)); 
            _imgArticle.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_normal)); 
            _imgSettings.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_normal));  
            
            break;  
        case 3:  
        	_imgHome.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_weixin_normal));  
            _imgGoods.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_address_normal)); 
            _imgProvider.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_find_frd_normal)); 
            _imgArticle.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_pressed)); 
            _imgSettings.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_normal)); 
            
            break; 
        case 4:  
        	_imgHome.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_weixin_normal));  
            _imgGoods.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_address_normal)); 
            _imgProvider.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_find_frd_normal)); 
            _imgArticle.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_normal)); 
            _imgSettings.setImageDrawable(getResources().getDrawable(  
                    R.drawable.tab_settings_pressed)); 
            
            break; 
        }  
        
      
    }
}
