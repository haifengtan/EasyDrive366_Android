package cn.count.easydrive366.maintab;

import java.util.List;

import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.MainActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.WelcomeActivity;

import cn.count.easydrive366.components.HomeMenuItem;
import cn.count.easydrive366.order.NeedPayListActivity;
import cn.count.easydrive366.user.BoundActivity;
import cn.count.easydrive366.user.FriendActivity;
import cn.count.easydrive366.user.SetupUserActivity;
import cn.count.easydrive366.user.TaskListActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpV4Fragment;
import cn.count.easydriver366.base.HomeMenu;
import cn.count.easydriver366.base.HttpExecuteGetTask;

import cn.count.easydriver366.base.IRightButtonPressed;
import cn.count.easydriver366.base.Menus;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class MineFragment extends BaseHttpV4Fragment implements IRightButtonPressed {
	private TableLayout _tableLayout;
	private List<HomeMenu> menus;
	
	private Menus mainMenu;
	private ProgressDialog _dialog;
	private boolean _fromCache=true;
	PullToRefreshScrollView mPullRefreshScrollView;
	ScrollView mScrollView;
	View containerView;
	private TextView txtNickname;
	private TextView txtSignature;
	private TextView txtBound;
	private TextView txtExp;
	private ProgressBar pbExp;
	private ImageView imgAvater;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		containerView = inflater.inflate(R.layout.fragment_mine,container,false);
		
	
		setupMenu();
		
				
		mPullRefreshScrollView = (PullToRefreshScrollView) containerView.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				_fromCache= false;
				new GetDataTask().execute();
			}
		});

		mScrollView = mPullRefreshScrollView.getRefreshableView();
		txtNickname = (TextView) containerView.findViewById(R.id.txt_nickname);
		txtSignature = (TextView) containerView.findViewById(R.id.txt_signature);
		txtBound = (TextView) containerView.findViewById(R.id.txt_bound);
		txtBound.setMovementMethod(LinkMovementMethod.getInstance());
		txtExp = (TextView) containerView.findViewById(R.id.txt_exp);
		imgAvater = (ImageView) containerView.findViewById(R.id.img_picture);
		pbExp = (ProgressBar) containerView.findViewById(R.id.pb_exp);
		setup_profile();
		this.load_user_profile();
		return containerView;
		
	}
	private void setup_profile(){
		containerView.findViewById(R.id.txt_bound).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				open_bound();

			}
		});
		containerView.findViewById(R.id.btn_pay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				open_pay();

			}
		});
		containerView.findViewById(R.id.btn_insurance).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						open_insurance();

					}
				});
		containerView.findViewById(R.id.btn_task).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				open_task();

			}
		});
		containerView.findViewById(R.id.btn_friend).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				open_friend();

			}
		});
		imgAvater.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				openSetupUser();
				
			}});
	}
	private void openSetupUser(){
		Intent intent = new Intent(this.getActivity(),SetupUserActivity.class);
		startActivityForResult(intent, 2);
	}
	private void open_bound() {
		Intent intent = new Intent(this.getActivity(), BoundActivity.class);
		startActivity(intent);
	}
	private void open_pay() {
		Intent intent = new Intent(this.getActivity(), NeedPayListActivity.class);
		intent.putExtra("status", "notpay");
		startActivity(intent);
	}

	private void open_insurance() {
		/*
		Intent intent = new Intent(this.getActivity(), NeedPayListActivity.class);
		intent.putExtra("status", "finished");
		intent.putExtra("type", "ins");
		startActivity(intent);
		*/
		Intent intent = new Intent(this.getActivity(), BrowserActivity.class);
		intent.putExtra("url", String.format("http://m.4006678888.com:21000/index.php/prize/summary?userid=%d", AppSettings.userid));
		
		startActivity(intent);
	}

	private void open_task() {
		Intent intent = new Intent(this.getActivity(), TaskListActivity.class);
		startActivity(intent);
	}

	private void open_friend() {
		Intent intent = new Intent(this.getActivity(), FriendActivity.class);
		startActivity(intent);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.load_user_profile();
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
			mainMenu.updateHome(_fromCache);
			
			/*
			Intent intent = new Intent("cn.count.easydriver366.service.GetLatestReceiverr");
			intent.putExtra("isInApp", true);
			HomeFragment.this.getActivity().sendBroadcast(intent);
			*/
			
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
			_dialog = new ProgressDialog(MineFragment.this.getActivity());
			_dialog.setMessage(getResources().getString(R.string.app_loading));
			_dialog.show();
		}
		 
	}
	
	private void setupMenu(){
		View v = containerView.findViewById(R.id.btn_phone);
		if (v != null) {
			v.setVisibility(View.GONE);
		}
		
		_tableLayout = (TableLayout)containerView.findViewById(R.id.tablelout_in_home_activity);
		initMenuItems();
		fillMenu();
		new GetDataTask(true).execute();
		
	}
	private void addTableRow(HomeMenu menu){
		TableRow tr = new TableRow(this.getActivity());
		HomeMenuItem item = new HomeMenuItem(this.getActivity(),null);
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
		mainMenu =new Menus(this.getActivity());
		menus = mainMenu.getMenus();
	}
	
	public void logout(){
		AppSettings.logout(this.getActivity());
		Intent intent = new Intent(MineFragment.this.getActivity(),WelcomeActivity.class);
		startActivity(intent);
		
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
		
		
	}
	public void load_user_profile() {
		if (!AppSettings.isLogin){
			return;
		}
		String url = String.format("bound/get_user_set?userid=%d",
				AppSettings.userid);
		beginHttp();
		new HttpExecuteGetTask() {

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				load_user_view(result);

			}
		}.execute(url);
	}

	private void load_user_view(final String result) {
		JSONObject json = AppSettings.getSuccessJSON(result,this.getActivity());
		if (json==null)
			return;
		/*
		 * {"exp":"1","msg_maintain":"2013-10-31保养,请提前预约","phone":"18605328170",
		 * "level":"1",
		 * "msg_car":"违章0次计0分罚0元\n14年03月至14年05月年审","status":"02","exp_nextlevel"
		 * :"10","nickname":"","photourl":"",
		 * "msg_driver":"已扣0分2014-11-05扣分到期","signature":"","bound":"0"}
		 */
		try {
			

			
			txtNickname.setText(AppSettings.getDefaultString(
					json.getString("nickname"), "（未设置）"));
			txtExp.setText(json.getString("exp_label"));
			// txtBound.setText(Html.fromHtml( String.format("<a>我的积分:%s</a>",
			// json.getString("bound"))));
			txtBound.setText(String.format("我的积分:%s", json.getString("bound")));
			txtSignature.setText(AppSettings.getDefaultString(
					json.getString("signature"), "啥也没有说"));
			this.loadImageFromUrl(imgAvater, json.getString("photourl"));
			

		} catch (Exception e) {
			log(e);
		}
	}
	
	
}