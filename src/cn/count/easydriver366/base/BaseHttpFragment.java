package cn.count.easydriver366.base;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.count.easydrive366.R;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseHttpFragment extends Fragment implements
		HttpClient.IHttpCallback,OnClickListener,IRightButtonPressed {
	private static String BaseHttpClientTAG = "BaseHttpActivity";
	protected HttpClient httpClient;
	// protected ImageButton _rightButton;
	protected Button _rightButton;
	protected String _company;
	protected String _phone;
	protected boolean _isHideTitleBar = true;
	protected ProgressDialog _dialog;
	protected PullToRefreshScrollView mPullRefreshScrollView;
	protected int reload = 0;
	protected View containerView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

	}

	protected void setupScrollView() {
		mPullRefreshScrollView = (PullToRefreshScrollView) containerView.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						reload = 1;
						reload_data();
					}
				});
	}

	protected void reload_data() {

	}

	protected void endRefresh() {
		if (mPullRefreshScrollView != null) {
			mPullRefreshScrollView.onRefreshComplete();
		}

	}

	protected HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new HttpClient(this);
		}
		return httpClient;
	}

	public void get(String actionAndParameters, final int returnType) {

		this.get(actionAndParameters, returnType, this.getResources()
				.getString(R.string.app_loading));
	}

	public void get(String actionAndParameters, final int returnType,
			final String hint) {
		if (!this.isOnline()) {
			this.restoreFromLocal(returnType);
			return;
		}
		if (!hint.equals("")) {
			if (_dialog != null) {
				_dialog.dismiss();
				_dialog = null;
			}
			_dialog = new ProgressDialog(this.getActivity());
			_dialog.setMessage(hint);
			_dialog.show();
		}
		final String url = String.format("%s&reload=%d", actionAndParameters,
				reload);
		Log.e("Reload", url);
		this.getHttpClient().requestServer(url, returnType);
		reload = 0;

	}
	protected void beginHttp(){
		if (_dialog !=null){
			_dialog.dismiss();
			_dialog = null;
		}
		_dialog = new ProgressDialog(this.getActivity());
		_dialog.setMessage(this.getResources().getString(R.string.app_loading));
		_dialog.show();
	}
	protected void endHttp(){
		if (_dialog !=null){
			_dialog.dismiss();
			_dialog = null;
		}
	}
	

	@Override
	public void processMessage(int msgType, final Object result) {

		Log.e(BaseHttpClientTAG, result.toString());

	}

	protected void setupCompanyAndPhone(final Object json) {
		final JSONObject result = (JSONObject) json;
		try {
			if (!result.getJSONObject("result").isNull("company")) {
				_company = result.getJSONObject("result").getString("company");
				_phone = result.getJSONObject("result").getString("phone");
				this.setupPhoneButton();
			}

		} catch (Exception e) {
			log(e);
		}
	}

	public void restoreFromLocal(final int msgType) {
		if (this.isOnline()) {
			return;
		}
		String result = this.getOfflineResult(msgType);
		if (!result.equals("")) {
			try {
				this.setupCompanyAndPhone(new JSONObject(result));
				this.processMessage(msgType, new JSONObject(result));

			} catch (JSONException e) {
				log(e);
			}
		}
	}

	public String getOfflineResult(final int msgType) {

		SharedPreferences prefs = this.getActivity().getPreferences(android.app.Activity.MODE_PRIVATE);
		String result = prefs.getString(getKey(msgType), "");
		return result;

	}
	private long lastUpdateTime(final int msgType){
		final String key =getKey(msgType); 
		SharedPreferences prefs =this.getActivity().getSharedPreferences(key, Context.MODE_PRIVATE);
		long result = prefs.getLong(key+"_datetime", 0);
		return result;
	}
	protected boolean is_update_expired(final int msgType){
		long lastTime = this.lastUpdateTime(msgType);
		if (lastTime==0)
			return false;
		else{
			float diff = (System.currentTimeMillis()-lastTime)/1000/60;
			if (diff>AppSettings.update_time){
				//Log.e("Time diff", String.valueOf(diff));
				return true;
			}else
				return false;
		}
	}
	private String getKey(final int msgType) {
		
		return String.format("json_%d_by_userid_%d__%s", msgType,
				AppSettings.userid,this.getClass().getName());
	}

	@Override
	public void recordResult(final int msgType, final Object result) {
		if (this.isSuccess(result)) {
			SharedPreferences prefs = this.getActivity().getPreferences(android.app.Activity.MODE_PRIVATE);
			Editor editor = prefs.edit();
			final String key =getKey(msgType); 
			editor.putString(key, result.toString());
			editor.putLong(key+"_datetime", System.currentTimeMillis());
			editor.commit();
			JSONObject jsonResult = (JSONObject) result;
			if (jsonResult.optJSONObject("result") != null) {
				this.setupCompanyAndPhone(result);
			}
		} else {
			this.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					endRefresh();

				}
			});

		}
		if (_dialog != null) {
			_dialog.dismiss();
			_dialog = null;
		}
	}

	public boolean isSuccess(final Object jsonobj) {
		return AppTools.isSuccess(jsonobj);
	}

	protected void log(Exception e) {
		if (e!=null)
			Log.e(BaseHttpClientTAG, e.getMessage());
	}

	protected void showMessage(final String info,
			final DialogInterface.OnClickListener okListener) {
		final Context context = this.getActivity();

		this.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(context)
						.setTitle(getResources().getString(R.string.hint))
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(info)
						.setPositiveButton(
								getResources().getString(R.string.ok),
								okListener).show();
				// .setNegativeButton(getResources().getString(R.string.cancel),
				// null).show();
			}
		});

	}

	protected void confirm(final String info,
			final DialogInterface.OnClickListener okListener) {
		final Context context = this.getActivity();

		this.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new AlertDialog.Builder(context)
						.setTitle(getResources().getString(R.string.hint))
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(info)
						.setPositiveButton(
								getResources().getString(R.string.ok),
								okListener)
						.setNegativeButton(
								getResources().getString(R.string.cancel), null)
						.show();
			}
		});

	}

	protected void showDialog(String str) {
		new AlertDialog.Builder(this.getActivity()).setMessage(str).show();
	}

	public void onRightButtonPress() {

	}

	protected void setBarTitle(final String title) {
		TextView txt = (TextView)containerView.findViewById(R.id.txt_navigation_bar_title);
		if (txt != null) {
			txt.setText(title);
		}
	}

	protected void setRightButtonInVisible() {
		_rightButton = (Button) containerView.findViewById(R.id.title_set_bn);
		if (_rightButton != null) {
			_rightButton.setVisibility(View.GONE);
		}
	}

	protected void setupRightButton() {
		this.setupRightButtonWithText(getResources().getString(R.string.edit));
	}

	protected void setupRightButtonWithText(final String buttonText) {
		_rightButton = (Button)containerView.findViewById(R.id.title_set_bn);
		if (_rightButton != null) {
			_rightButton.setText(buttonText);
			_rightButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onRightButtonPress();

				}

			});
		}

	}

	protected void onLeftButtonPress() {
		this.getActivity().finish();
	}

	protected void setupLeftButton() {
		View v =containerView.findViewById(R.id.img_navigationbar_logo);
		if (v != null) {
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onLeftButtonPress();

				}
			});
		}
	}

	protected void setupPhoneButtonInVisible() {
		View v = containerView.findViewById(R.id.btn_phone);
		if (v != null) {
			v.setVisibility(View.GONE);
		}
	}

	protected void setupPhoneButton() {
		final View v = containerView.findViewById(R.id.btn_phone);
		final Activity instance = this.getActivity();

		if (v != null) {
			this.getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Intent intent =instance.getIntent();
					String title = intent.getStringExtra("title");
					if (title != null && !title.equals("")) {
						setBarTitle(title);
					}
					((Button) v).setText(String.format("拨号：%s", _phone));
					v.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (_phone != null && !_phone.equals("")) {
								Uri uri = Uri.parse(String.format("tel:%s",
										_phone));

								Intent it = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(it);
							}

						}
					});

				}

			});

		}
	}

	protected boolean isOnline() {
		return NetworkUtils.getNetworkState(this.getActivity()) > 0;
	}

	protected void makeCall(final String phone) {
		startActivity(AppTools.getPhoneAction(phone));
	}

	protected void saveWithKey(String key, JSONObject value) {
		SharedPreferences prefs = this.getActivity().getSharedPreferences(
				AppSettings.AppTile, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(key, value.toString());
		editor.commit();
	}

	protected JSONObject loadWithKey(String key, JSONObject defaultValue) {
		SharedPreferences prefs = this.getActivity().getSharedPreferences(
				AppSettings.AppTile, Context.MODE_PRIVATE);
		String value = prefs.getString(key, "");
		if (value.equals("")) {
			return defaultValue;
		}
		try {
			JSONObject result = new JSONObject(value);
			return result;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	@Override
	public void showFailureMessage(String msg) {
		final String m = msg;
		final Activity instance = this.getActivity();
		this.getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(instance, m, Toast.LENGTH_LONG)
						.show();

			}

		});

	}

	protected void chooseDate(final EditText edtDate) {
		String d = edtDate.getText().toString();
		if (d.equals("")) {
			d = "2000-01-01";
		}
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final Calendar c = Calendar.getInstance();

		try {
			c.setTime(sdf.parse(d));

			Dialog dialog = new DatePickerDialog(this.getActivity(),
					new DatePickerDialog.OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							c.set(Calendar.YEAR, year);
							c.set(Calendar.MONTH, monthOfYear);
							c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
							// showDialog(sdf.format(c.getTime()));
							edtDate.setText(sdf.format(c.getTime()));
						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
			dialog.show();
		} catch (Exception e) {
			log(e);
		}

	}

	protected boolean personIdValidation(String text) {
		String regx = "[0-9]{17}X";
		String reg1 = "[0-9]{15}";
		String regex = "[0-9]{18}";
		boolean flag = text.matches(regx) || text.matches(reg1)
				|| text.matches(regex);
		return flag;
	}

	protected void saveLogin(final String username, final String password,
			final boolean isRemember) {
		SharedPreferences pref = this.getActivity().getSharedPreferences("Login_histroy",
				Activity.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.putBoolean("remember_password", isRemember);
		try {
			JSONObject login = new JSONObject();
			login.put("username", username);
			login.put("password", password);
			login.put("remember_password", isRemember);
			String logins = pref.getString("logins", "");
			JSONArray login_list;
			if (logins.equals("")) {
				login_list = new JSONArray();
			} else {
				login_list = new JSONArray(logins);
			}
			int index = login_list.length();
			for (int i = 0; i < index; i++) {
				JSONObject item = login_list.getJSONObject(i);
				if (item.getString("username").trim().equals(username.trim())) {
					index = i;
					break;
				}
			}
			login_list.put(index, login);
			editor.putString("logins", login_list.toString());

		} catch (Exception e) {
			log(e);
		}
		editor.commit();
	}

	public void httpGetUrl(final String url, final HttpCallback callback) {
		new HttpExecuteGetTask() {
			@Override
			protected void onPostExecute(String result) {

				callback.processResponse(result);
			}
		}.execute(url);

	}

	public void httpPostUrl(final String url, final List<NameValuePair> params,
			final HttpCallback callback) {
		new HttpExecutePostTask() {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				callback.processResponse(result);
			}

		}.execute(url, AppSettings.getOutputParameters(params));
	}

	public void loadImageFromUrl(ImageView imageView, final String url) {
		AppTools.loadImageFromUrl(
				imageView, url);
	}

	public void loadImageFromUrl(ImageView imageView, final String url,
			int resourceId) {
		AppTools.loadImageFromUrl(
				imageView, url, resourceId);
	}

	@Override
	public void onClick(View v) {
		
		
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
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	onRightSwipe();
                    
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }
    public void onLeftSwipe(){
    	Toast.makeText(this.getActivity(), "Left Swipe", Toast.LENGTH_SHORT).show();
    }
    public void onRightSwipe(){
    	Toast.makeText(this.getActivity(), "Right Swipe", Toast.LENGTH_SHORT).show();
    }
}
