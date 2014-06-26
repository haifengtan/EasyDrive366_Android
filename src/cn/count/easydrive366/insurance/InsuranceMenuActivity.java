package cn.count.easydrive366.insurance;

import java.util.ArrayList;
import java.util.List;
import cn.count.easydrive366.R;
import cn.count.easydrive366.components.HomeMenuItem;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.Menus;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class InsuranceMenuActivity extends Activity {
	private TableLayout _tableLayout;
	private List<HomeMenu> menus;
	
	private ProgressDialog _dialog;
	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	private Menus mainMenu;
	private boolean _fromCache=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.moudles_home_activity);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setupMenu();

		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						_fromCache = false;
						new GetDataTask().execute();
					}
				});

	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.finish();
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onDestroy() {
		for(HomeMenu item : menus){
			if (item.menuItem!=null && item.menuItem._br!=null){
				this.unregisterReceiver(item.menuItem._br);
			}
		}
		super.onDestroy();
	}


	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private boolean _needSleep = false;
		
		public GetDataTask() {
			super();
		}

		public GetDataTask(boolean needSleep) {
			super();
			_needSleep = needSleep;
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			if (_needSleep) {
				try {
					Thread.sleep(2000);

				} catch (InterruptedException e) {
				}
			}

			mainMenu.updateInsurance(_fromCache);

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
		protected void onPreExecute() {
			_dialog = new ProgressDialog(InsuranceMenuActivity.this);
			_dialog.setMessage(getResources().getString(R.string.app_loading));
			_dialog.show();
		}

	}

	private void setupMenu() {

		_tableLayout = (TableLayout) findViewById(R.id.tablelout_in_home_activity);
		initMenuItems();
		fillMenu();
		new GetDataTask(true).execute();
	}

	private void addTableRow(HomeMenu menu) {
		TableRow tr = new TableRow(this);
		HomeMenuItem item = new HomeMenuItem(this, null);
		
		item.setData(menu);
		tr.addView(item);
		_tableLayout.addView(tr);
	}

	private void fillMenu() {
		for (int i = 0; i < menus.size(); i++) {
			HomeMenu menu = menus.get(i);
			addTableRow(menu);
		}
	}

	private void initMenuItems() {
		mainMenu = new Menus(this);
		menus = mainMenu.getInsuranceMenus();
	}

}
