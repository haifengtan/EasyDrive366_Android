package cn.count.easydrive366.maintab;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.count.easydrive366.ActivateCodeActivity;
import cn.count.easydrive366.BindCellphoneActivity;
import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.CarRegistrationEditActivity;
import cn.count.easydrive366.DriverLicenseEditActivity;
import cn.count.easydrive366.MainActivity;
import cn.count.easydrive366.MaintainEditActivity;
import cn.count.easydrive366.PasswordResetActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.UserFeedbackActivity;
import cn.count.easydrive366.card.AddCardStep1Activity;
import cn.count.easydrive366.card.CardViewActivity;
import cn.count.easydrive366.order.NeedPayListActivity;
import cn.count.easydrive366.signup.Step1Activity;
import cn.count.easydrive366.user.BoundActivity;
import cn.count.easydrive366.user.FriendActivity;
import cn.count.easydrive366.user.MyFavorActivity;
import cn.count.easydrive366.user.MyHistroyActivity;
import cn.count.easydrive366.user.SettingsFragment;
import cn.count.easydrive366.user.SetupUserActivity;
import cn.count.easydrive366.user.TaskListActivity;
import cn.count.easydrive366.user.UesrProfileActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpV4Fragment;
import cn.count.easydriver366.base.CheckUpdate;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class MoreFragment extends BaseHttpV4Fragment {
	private Button logoutButton;
	private TextView txtVersion;
	private TextView txtBind;
	private TextView txtCellphone;
	private TextView txtActivate_code;
	private boolean _isActivate = false;
	private int _isbind = 1;
	private String _cellphone;
	private int BINDCELLPHONE = 1;
	private int SETUP_USER =2;
	private String _number;
	private String _code;
	private String _activate_date;
	private String _valid_date;
	private JSONArray _contents;
	private TextView txtNickname;
	private TextView txtSignature;
	private TextView txtBound;
	private TextView txtExp;
	private ProgressBar pbExp;
	private ImageView imgAvater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		containerView = inflater.inflate(R.layout.fragment_more,container,false);
		this.setupLeftButton();
		
		this.init_view();
		this.setBarTitle(this.getResources().getString(R.string.menu_settings));
		
		return containerView;

	}

	private void init_view() {
		
		
		containerView.findViewById(R.id.row_favorite).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						open_my_favor();

					}
				});
		containerView.findViewById(R.id.row_histroy).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						open_my_histroy();

					}
				});
		containerView.findViewById(R.id.row_resetpassword).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						changePassword();

					}
				});

		
		containerView.findViewById(R.id.row_setup).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				register_step();
			}
		});
		
		containerView.findViewById(R.id.row_choose_cellphone).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// start activity bindcellphone
						Intent intent = new Intent(MoreFragment.this.getActivity(),
								BindCellphoneActivity.class);
						intent.putExtra("phone", _cellphone);
						intent.putExtra("isbind", _isbind);
						startActivityForResult(intent, BINDCELLPHONE);

					}
				});

		containerView.findViewById(R.id.row_choose_check_version).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						new CheckUpdate(MoreFragment.this.getActivity(), true);

					}
				});
		containerView.findViewById(R.id.row_choose_findpassword).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						reset_password();

					}
				});
		containerView.findViewById(R.id.row_choose_user_feedback).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						user_feedback();

					}
				});
		containerView.findViewById(R.id.row_card).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				check_activate_code();

			}
		});

		
		/*
		 * findViewById(R.id.row_cardview).setOnClickListener(new
		 * OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { cardView(); }});
		 * findViewById(R.id.row_cardadd).setOnClickListener(new
		 * OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { cardAdd();
		 * 
		 * }});
		 */
		
		txtBind = (TextView) containerView.findViewById(R.id.txt_bindCellphone);
		txtVersion = (TextView) containerView.findViewById(R.id.txt_version);
		txtCellphone = (TextView) containerView.findViewById(R.id.img_choose_cellphone);
		// txtActivate_code = (TextView)findViewById(R.id.txt_activate_code);
		txtVersion.setText(String.format("V%s >", AppSettings.version));

		this.logoutButton = (Button) containerView.findViewById(R.id.btn_logout);
		logoutButton.setText(String.format("注销-%s", AppSettings.username));
		containerView.findViewById(R.id.btn_logout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logout();

			}
		});
		this.load_user_profile();
	}
	
	private void changePassword() {
		Intent intent = new Intent(this.getActivity(), PasswordResetActivity.class);
		startActivity(intent);
	}

	private void find_password() {
		this.get(String.format("api/sms_reset_pwd?userid=%d",
				AppSettings.userid), 5);
	}

	@Override
	public void processMessage(int msgType, final Object result) {

		if (msgType == 1) {
			if (AppTools.isSuccess(result)) {
				try {
					JSONObject json = (JSONObject) result;
					_cellphone = json.getJSONObject("result")
							.getString("phone");
					// _number =
					// json.getJSONObject("result").getString("number");
					// _code = json.getJSONObject("result").getString("code");
					// _activate_date =
					// json.getJSONObject("result").getString("activate_date");
					// _valid_date =
					// json.getJSONObject("result").getString("valid_date");
					// _contents =
					// json.getJSONObject("result").getJSONArray("contents");
					// _isActivate = !_code.isEmpty();
					if (json.getJSONObject("result").getString("status")
							.equals("02")) {
						this.getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								txtBind.setText(getString(R.string.unbind));
								txtCellphone.setText(String.format("%s >",
										_cellphone));
							}
						});

						_isbind = 0;
					}
					if (_isActivate) {
						this.getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// txtActivate_code.setText(getResources().getString(R.string.has_activate_code));
							}
						});
					}
				} catch (Exception e) {
					log(e);
				}
			}

		} else if (msgType == 11) {
			if (AppTools.isSuccess(result)) {
				try {
					this.getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// txtActivate_code.setText(getResources().getString(R.string.has_activate_code));
						}
					});

					this._isActivate = true;

				} catch (Exception e) {
					log(e);
				}
			}

		} else if (msgType == 2) {
			// maintain
			try {
				JSONObject json = (JSONObject) result;
				this.setup_maintain(json.getJSONObject("result").getJSONObject(
						"data"));
			} catch (Exception e) {
				log(e);
			}

		} else if (msgType == 3) {
			try {
				JSONObject json = (JSONObject) result;
				this.setup_driver(json.getJSONObject("result").getJSONObject(
						"data"));
			} catch (Exception e) {
				log(e);
			}
		} else if (msgType == 4) {
			try {
				JSONObject json = (JSONObject) result;
				this.setup_car_registration(json.getJSONObject("result")
						.getJSONObject("data"));
			} catch (Exception e) {
				log(e);
			}
		} else if (msgType == 5) {
			try {
				if (AppTools.isSuccess(result)) {
					this.showMessage(((JSONObject) result).getString("result"),
							null);
					changePassword();
				}
			} catch (Exception e) {
				log(e);
			}
		}

	}

	private void check_activate_code() {
		Intent intent = new Intent(this.getActivity(), ActivateCodeActivity.class);

		startActivity(intent);
		/*
		 * Intent intent; if (this._isActivate){ intent = new
		 * Intent(this,ActivateCodeShowActivity.class);
		 * intent.putExtra("number", _number); intent.putExtra("code", _code);
		 * intent.putExtra("activate_date",_activate_date);
		 * intent.putExtra("valid_date",_valid_date); if (_contents !=null)
		 * intent.putExtra("contents", _contents.toString()); }else{ intent =
		 * new Intent(this,ActivateCodeActivity.class); } startActivity(intent);
		 */
	}

	private void setup_maintain(final JSONObject result) {
		Intent intent = new Intent(this.getActivity(), MaintainEditActivity.class);
		/*
		 * JSONObject result = new JSONObject(); try{
		 * 
		 * result.put("average_mileage", "30"); result.put("prev_distance",
		 * "0"); result.put("prev_date", ""); result.put("max_distance",
		 * "5000"); result.put("max_time", "6"); }catch(Exception e){
		 * 
		 * } result = this.loadWithKey("maintain", result);
		 */
		intent.putExtra("data", result.toString());
		startActivityForResult(intent, 0);
	}

	private void setup_driver(final JSONObject result) {
		Intent intent = new Intent(this.getActivity(), DriverLicenseEditActivity.class);
		/*
		 * JSONObject result = new JSONObject(); try{
		 * 
		 * result.put("name", ""); result.put("init_date", "1990-01-01");
		 * result.put("number", ""); result.put("car_type", "C1");
		 * 
		 * }catch(Exception e){
		 * 
		 * } result = this.loadWithKey("driver_license", result);
		 */
		intent.putExtra("data", result.toString());
		startActivityForResult(intent, 0);
	}

	private void setup_car_registration(final JSONObject result) {
		Intent intent = new Intent(this.getActivity(), CarRegistrationEditActivity.class);
		/*
		 * JSONObject result = new JSONObject(); try{
		 * 
		 * result.put("engine_no", ""); result.put("vin", "");
		 * result.put("registration_date", "1990-01-01"); result.put("plate_no",
		 * this.getResources().getString(R.string.default_plate_no));
		 * 
		 * }catch(Exception e){
		 * 
		 * } result = this.loadWithKey("car_registration", result);
		 */
		intent.putExtra("data", result.toString());
		startActivityForResult(intent, 0);
	}

	private void logout() {
		
		((MainActivity)this.getActivity()).logout();
		
	}

	

	private void reset_password() {
		if (this._isbind == 1) {
			this.confirm("找回密码操作需要绑定手机，请先绑定手机。",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(MoreFragment.this.getActivity(),
									BindCellphoneActivity.class);
							intent.putExtra("phone", _cellphone);
							intent.putExtra("isbind", _isbind);
							startActivityForResult(intent, BINDCELLPHONE);

						}
					});
		} else {
			this.confirm("找回密码操作将向您绑定手机发送随机初始密码（短信免费），请确认要找回密码？",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							find_password();

						}
					});
		}
	}

	private void user_feedback() {
		Intent intent = new Intent(this.getActivity(),
				UserFeedbackActivity.class);
		intent.putExtra("phone", _cellphone);
		intent.putExtra("isbind", _isbind);
		startActivity(intent);
	}

	private void register_step() {
		Intent intent = new Intent(this.getActivity(), Step1Activity.class);
		startActivity(intent);
	}

	private void cardView() {
		Intent intent = new Intent(this.getActivity(), CardViewActivity.class);
		startActivity(intent);
	}

	private void cardAdd() {
		Intent intent = new Intent(this.getActivity(), AddCardStep1Activity.class);
		startActivity(intent);
	}

	
	private void open_my_order(){
		Intent intent = new Intent(this.getActivity(), NeedPayListActivity.class);
		intent.putExtra("status", "finished");
		startActivity(intent);
	}
	private void open_my_profile(){
		Intent intent = new Intent(this.getActivity(), UesrProfileActivity.class);
		
		startActivity(intent);
	}
	private void open_my_favor(){
		Intent intent = new Intent(this.getActivity(), MyFavorActivity.class);
		
		startActivity(intent);
	}
	private void open_my_histroy(){
		Intent intent = new Intent(this.getActivity(), MyHistroyActivity.class);
		
		startActivity(intent);
	}
	

	public void load_user_profile() {
		if (!AppSettings.isLogin){
			logoutButton.setText("注销");
			return;
		}
		String url = String.format("bound/get_user_set?userid=%d",
				AppSettings.userid);
		
		new HttpExecuteGetTask() {

			@Override
			protected void onPostExecute(String result) {
				
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
			_cellphone = json.getString("phone");

			if (json.getString("status").equals("02")) {
				txtBind.setText(getString(R.string.unbind));
				txtCellphone.setText(String.format("%s >", _cellphone));

				_isbind = 0;
			}

			
			
			logoutButton.setText(String.format("注销-%s", AppSettings.username));

		} catch (Exception e) {
			log(e);
		}
	}
	
}