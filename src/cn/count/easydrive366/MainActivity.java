package cn.count.easydrive366;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import cn.count.easydrive366.article.ArticleFragment;
import cn.count.easydrive366.article.ArticleListFragment;
import cn.count.easydrive366.components.EDViewPager;
import cn.count.easydrive366.goods.GoodsDetailActivity;
import cn.count.easydrive366.goods.GoodsListFragment;
import cn.count.easydrive366.guide.GuideActivity;
import cn.count.easydrive366.maintab.MineFragment;
import cn.count.easydrive366.maintab.MoreFragment;
import cn.count.easydrive366.maintab.RecommentsFragment;
import cn.count.easydrive366.maintab.SearchFragment;
import cn.count.easydrive366.provider.ProviderDetailActivity;
import cn.count.easydrive366.provider.ProviderListFragment;
import cn.count.easydrive366.push.PushUtils;
import cn.count.easydrive366.user.SettingsFragment;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.CheckUpdate;
import cn.count.easydriver366.service.BackendService;
import cn.count.easydriver366.base.IRightButtonPressed;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 首页
 * 
 * @author admin
 * 
 */
public class MainActivity extends FragmentActivity {
	public static MainActivity instance = null;
	private EDViewPager mTabPager;// 声明对象
	private ImageView _imgHome, _imgGoods, _imgProvider, _imgArticle,
			_imgSettings;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	// private HomeFragment _home;
	// 推荐列表
	private RecommentsFragment _home;
	private boolean _userWantQuit = false;
	private Timer _quitTimer;
	// private GoodsListFragment _goods;
	private SearchFragment _goods;

	// private ProviderListFragment _provider;
	private MineFragment _provider;
	// private ArticleListFragment _article;

	private ArticleFragment _article2;

	// private SettingsFragment _settings;
	private MoreFragment _settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化百度推送
		AppSettings.initBaiduPush(getApplicationContext(), this);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// 获取是否登录的信息
		AppSettings.restore_login_from_device(this);

		// 启动后台服务
		// startBackendService();

		getActionBar().setCustomView(R.layout.actionbar);
		instance = this;
		// this.getActionBar().setDisplayShowTitleEnabled(true);
		// this.setTitle("EasyDrive366");
		// this.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		mTabPager = (EDViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mTabPager.setOffscreenPageLimit(5);

		_imgHome = (ImageView) findViewById(R.id.img_home);
		_imgGoods = (ImageView) findViewById(R.id.img_goods);
		_imgProvider = (ImageView) findViewById(R.id.img_provider);
		_imgArticle = (ImageView) findViewById(R.id.img_article);
		_imgSettings = (ImageView) findViewById(R.id.img_settings);

		findViewById(R.id.layout_home).setOnClickListener(
				new MyOnClickListener(0));
		findViewById(R.id.layout_goods).setOnClickListener(
				new MyOnClickListener(1));
		findViewById(R.id.layout_provider).setOnClickListener(
				new MyOnClickListener(2));
		findViewById(R.id.layout_article).setOnClickListener(
				new MyOnClickListener(3));
		findViewById(R.id.layout_settings).setOnClickListener(
				new MyOnClickListener(4));

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mTabPager.setAdapter(mSectionsPagerAdapter);
		mTabPager.getCurrentItem();

		// 检查更新
		new CheckUpdate(this, false);

		// 检测点击的是图片还是列表
		handle_extra_call();

		SharedPreferences pref = this.getSharedPreferences("first_run",
				MODE_PRIVATE);
		boolean isfirst = pref.getBoolean("isfirst", true);

		/* 如果是第一次启动则跳转到引导页面 */
		if (isfirst) {
			Editor editor = pref.edit();
			editor.putBoolean("isfirst", false);
			editor.commit();
			Intent intent = new Intent(this, GuideActivity.class);
			startActivity(intent);
		}

	}

	private void handle_extra_call() {
		Intent intent = this.getIntent();
		Uri uri = intent.getData();
		if (uri == null) {
			return;
		}
		String type = uri.getQueryParameter("type").toLowerCase();
		String id = uri.getQueryParameter("id");
		String name = uri.getQueryParameter("name");

		if (type.equals("spv")) {
			Intent i = new Intent(this, ProviderDetailActivity.class);
			i.putExtra("code", id);
			startActivity(i);
		} else if (type.equals("gds")) {
			Intent i = new Intent(this, GoodsDetailActivity.class);
			i.putExtra("id", Integer.parseInt(id));
			startActivity(i);
		}
	}

	/**
	 * 启动后台服务
	 */
	private void startBackendService() {
		if (!isServiceRunning()) {
			Intent service = new Intent(this, BackendService.class);
			startService(service);
		}

	}

	private boolean isServiceRunning() {
		boolean result = false;
		ActivityManager mActivityManager = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
				.getRunningServices(50);
		final String serviceName = "cn.count.easydriver366.service.BackendService";
		for (int i = 0; i < mServiceList.size(); i++) {
			if (mServiceList.get(i).service.getClassName().equals(serviceName)) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

		super.onActivityResult(arg0, arg1, arg2);
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

	public void pageChanged(final int index) {
		this.mTabPager.setEnabled(index != 3 && index != 0);
		switch (index) {
		case 0:
			if (AppSettings.isLogin)
				rightMenu.setTitle("设置");
			else
				rightMenu.setTitle("登录");
			break;
		case 1:
			rightMenu.setTitle("查询");

			break;
		default:
			rightMenu.setTitle("");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("menu click", item.toString());
		Fragment f = mSectionsPagerAdapter.getItem(mTabPager.getCurrentItem());
		if (f instanceof IRightButtonPressed) {
			((IRightButtonPressed) f).onRightButtonPress();
		}
		return super.onOptionsItemSelected(item);
	}

	public void login() {
		pageChanged(0);
		_settings.load_user_profile();
	}

	public void logout() {
		AppSettings.logout(this);

		mTabPager.setCurrentItem(0);
	}

	public void gotoTab(final int index) {
		mTabPager.setCurrentItem(index);
	}

	@Override
	public void onBackPressed() {
		if (_userWantQuit) {
			AppSettings.isquiting = true;
			this.finish();
			System.exit(0);
		} else {
			Toast.makeText(this,
					this.getResources().getString(R.string.exit_question),
					Toast.LENGTH_LONG).show();
			_userWantQuit = true;
			if (_quitTimer == null) {
				_quitTimer = new Timer();
			}
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					_userWantQuit = false;

				};
			};
			_quitTimer.schedule(task, 2000);

		}

	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			if (index == 4 && !AppSettings.isLogin) {
				gotoLogin();
				return;
			}
			mTabPager.setCurrentItem(index);
			Log.d("Tab", String.format("Select=%d", index));
		}
	};

	private void gotoLogin() {
		this._home.settingsButtonPress();
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		public void onPageSelected(int index) {
			pageChanged(index);
			switch (index) {
			case 0:
				_imgHome.setImageDrawable(getResources().getDrawable(
						R.drawable.tuijian_pressed));
				_imgGoods.setImageDrawable(getResources().getDrawable(
						R.drawable.fenlei_normal));
				_imgProvider.setImageDrawable(getResources().getDrawable(
						R.drawable.wode_normal));
				_imgArticle.setImageDrawable(getResources().getDrawable(
						R.drawable.baike_normal));
				_imgSettings.setImageDrawable(getResources().getDrawable(
						R.drawable.gengduo_normal));
				break;
			case 1:
				_imgHome.setImageDrawable(getResources().getDrawable(
						R.drawable.tuijian_normal));
				_imgGoods.setImageDrawable(getResources().getDrawable(
						R.drawable.fenlei_pressed));
				_imgProvider.setImageDrawable(getResources().getDrawable(
						R.drawable.wode_normal));
				_imgArticle.setImageDrawable(getResources().getDrawable(
						R.drawable.baike_normal));
				_imgSettings.setImageDrawable(getResources().getDrawable(
						R.drawable.gengduo_normal));

				break;
			case 2:
				_imgHome.setImageDrawable(getResources().getDrawable(
						R.drawable.tuijian_normal));
				_imgGoods.setImageDrawable(getResources().getDrawable(
						R.drawable.fenlei_normal));
				_imgProvider.setImageDrawable(getResources().getDrawable(
						R.drawable.wode_pressed));
				_imgArticle.setImageDrawable(getResources().getDrawable(
						R.drawable.baike_normal));
				_imgSettings.setImageDrawable(getResources().getDrawable(
						R.drawable.gengduo_normal));

				break;
			case 3:
				_imgHome.setImageDrawable(getResources().getDrawable(
						R.drawable.tuijian_normal));
				_imgGoods.setImageDrawable(getResources().getDrawable(
						R.drawable.fenlei_normal));
				_imgProvider.setImageDrawable(getResources().getDrawable(
						R.drawable.wode_normal));
				_imgArticle.setImageDrawable(getResources().getDrawable(
						R.drawable.baike_pressed));
				_imgSettings.setImageDrawable(getResources().getDrawable(
						R.drawable.gengduo_normal));

				break;
			case 4:
				_imgHome.setImageDrawable(getResources().getDrawable(
						R.drawable.tuijian_normal));
				_imgGoods.setImageDrawable(getResources().getDrawable(
						R.drawable.fenlei_normal));
				_imgProvider.setImageDrawable(getResources().getDrawable(
						R.drawable.wode_normal));
				_imgArticle.setImageDrawable(getResources().getDrawable(
						R.drawable.baike_normal));
				_imgSettings.setImageDrawable(getResources().getDrawable(
						R.drawable.gengduo_pressed));

				break;
			}

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Log.d("Tab", String.format("Wanted=%d", position));
			if (position == 0) {
				if (_home == null) {
					_home = new RecommentsFragment();
				}
				return _home;
			} else if (position == 1) {
				if (_goods == null) {
					_goods = new SearchFragment();
				}
				return _goods;
			} else if (position == 2) {
				if (_provider == null) {
					_provider = new MineFragment();
				}
				return _provider;
			} else if (position == 3) {
				/*
				 * if (_article==null){ _article = new ArticleListFragment(); }
				 */
				if (_article2 == null) {
					_article2 = new ArticleFragment();
				}
				return _article2;
			} else if (position == 4) {
				if (_settings == null) {
					_settings = new MoreFragment();
				}
				return _settings;
			} else {
				ComingSoonFragment f = new ComingSoonFragment();
				return f;
			}

		}

		@Override
		public int getCount() {

			return 5;
		}
		/*
		 * @Override public int getItemPosition(Object object) { return
		 * this.POSITION_NONE;
		 * 
		 * }
		 * 
		 * @Override public CharSequence getPageTitle(int position) { Locale l =
		 * Locale.getDefault(); return "Title";
		 * 
		 * }
		 */
	}

}
