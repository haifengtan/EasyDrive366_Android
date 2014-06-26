package cn.count.easydrive366.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.constant.WBConstants;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydrive366.share.FavorController;
import cn.count.easydrive366.share.ShareController;

import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class FriendActivity extends BaseHttpActivity implements Response{
	private Button btn_save;
	private TextView txt_ontent;
	private TextView txt_invite_code;
	private ListView lv_items;
	private EditText edt_code;
	private FriendListAdapter _adapter;
	private boolean is_can_invite;
	private List<Friend> _list;
	private ShareController _share;
	private IWeiboShareAPI _weibo;
	private MenuItem _menuFavor;
	private FavorController _favor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_userfriend);
		this.setBarTitle("我的朋友");
		
		
		this.setupLeftButton();
		_favor = new FavorController(this);
		
		_weibo = WeiboShareSDK.createWeiboAPI(this, AppSettings.SINA_WEIBO_ID);
		_share = new ShareController(this,_weibo);
		if (savedInstanceState != null) {
			_weibo.handleWeiboResponse(getIntent(), this);
		}
		init_view();
		load_data();
	}

	private void init_view() {
		btn_save = (Button) findViewById(R.id.btn_save);
		txt_ontent = (TextView) findViewById(R.id.txt_content);
		edt_code = (EditText) findViewById(R.id.edt_code);
		lv_items = (ListView) findViewById(R.id.lv_items);
		txt_invite_code = (TextView) findViewById(R.id.txt_invite_code);
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				do_save();

			}
		});
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edt_code.getWindowToken(), 0);
	}

	private void do_save() {
		String url = String.format("bound/update_invite?userid=%d&code=%s",
				AppSettings.userid, edt_code.getText().toString());
		beginHttp();
		new HttpExecuteGetTask() {

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				if (AppSettings.isSuccessJSON(result, FriendActivity.this)) {
					load_view(result);
					is_can_invite = false;
					edt_code.setEnabled(is_can_invite);
					btn_save.setEnabled(is_can_invite);
					try {
						JSONObject json = new JSONObject(result).getJSONObject("result");
						JSONArray tempList = json.getJSONArray("friends");
						
						_list = new ArrayList<Friend>();
						for (int i = 0; i < tempList.length(); i++) {
							JSONObject item = tempList.getJSONObject(i);
							Friend friend = new Friend();
							friend.date = item.getString("registerDate");
							friend.name = item.getString("name");
							friend.memo = item.getString("is_bounds");

							_list.add(friend);
						}
						_adapter.notifyDataSetChanged();
					} catch (Exception e) {
						log(e);
					}
				}

			}
		}.execute(url);

	}

	private void load_data() {
		String url = String.format("bound/get_my_friends?userid=%d",
				AppSettings.userid);
		beginHttp();
		new HttpExecuteGetTask() {

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				load_view(result);

			}
		}.execute(url);

	}

	private void load_view(final String result) {
		JSONObject json = AppSettings.getSuccessJSON(result,this);
		if (json == null)
			return;
		try {
			if (!json.isNull("is_can_invite")) {

				is_can_invite = json.getInt("is_can_invite") == 1;
			}
			/*
			if (is_can_invite) {
				txt_invite_code.setText(json.getString("invite_code"));

			}
			*/
			txt_invite_code.setText("");
			edt_code.setText(json.getString("invite_code"));
			edt_code.setEnabled(is_can_invite);
			btn_save.setEnabled(is_can_invite);

			txt_ontent.setText(json.getString("content"));
			JSONArray tempList = json.getJSONArray("friends");
			_list = new ArrayList<Friend>();
			for (int i = 0; i < tempList.length(); i++) {
				JSONObject item = tempList.getJSONObject(i);
				Friend friend = new Friend();
				friend.date = item.getString("registerDate");
				friend.name = item.getString("name");
				friend.memo = item.getString("is_bounds");

				_list.add(friend);
			}
			if (_adapter == null) {
				_adapter = new FriendListAdapter(this);
				lv_items.setAdapter(_adapter);

			} else {
				_adapter.notifyDataSetChanged();
			}
			_share.setContent(json.getString("share_title"),
					json.getString("share_intro"),
					json.getString("share_url"));
		} catch (Exception e) {
			log(e);
		}
	}

	private class Friend {
		public String name;
		public String date;
		public String memo;
	}

	private class ViewHolder {
		public TextView txtName;
		public TextView txtDate;
		public TextView txtMemo;
	}

	private class FriendListAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public FriendListAdapter(Context context) {

			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return _list.size();
		}

		@Override
		public Object getItem(int position) {

			return position;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(
						R.layout.listitem_boundlist, null);
				holder.txtDate = (TextView) convertView
						.findViewById(R.id.txtBound);
				holder.txtName = (TextView) convertView
						.findViewById(R.id.txtDate);
				holder.txtMemo = (TextView) convertView
						.findViewById(R.id.txtMemo);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Friend bound = _list.get(position);
			holder.txtDate.setText(bound.date);
			holder.txtName.setText(bound.name);
			holder.txtMemo.setText(bound.memo);
			return convertView;
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.share_menu, menu);
		_menuFavor = menu.findItem(R.id.action_favor);
		_menuFavor.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (R.id.action_favor == item.getItemId()) {

		} else {
			
			 _share.share(item.getItemId());
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onLeftButtonPress(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edt_code.getWindowToken(), 0);
		finish();
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
