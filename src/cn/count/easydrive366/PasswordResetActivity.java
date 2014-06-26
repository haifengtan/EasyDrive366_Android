package cn.count.easydrive366;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;

public class PasswordResetActivity extends BaseHttpActivity {
	private EditText password1;
	private EditText password2;
	private EditText oldPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.modules_password_reset_activity);
		this.setupLeftButton();
		this.setRightButtonInVisible();
		this.setupPhoneButtonInVisible();
		this.init_view();
		this.setBarTitle(this.getResources().getString(R.string.reset_password));
		
	}
	private void init_view(){
		oldPassword = (EditText)findViewById(R.id.edt_oldpassword);
		password1 = (EditText)findViewById(R.id.edt_password);
		password2 = (EditText)findViewById(R.id.edt_repassword);
		findViewById(R.id.btn_change_password).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				changePassword();
				
			}});
		
		
		
		
		
		
	}
	private void changePassword(){
		String oldpassword = oldPassword.getText().toString();
		
		String password = password1.getText().toString();
		String repassword = password2.getText().toString();
		if (oldpassword.equals("") ){
			this.showMessage(getResources().getString(R.string.need_old_password), null);
			return;
		}
		if (password.equals("") || password.length()<6){
			this.showMessage(getResources().getString(R.string.password_not_empty), null);
			return;
		}
		if (!password.equals(repassword)){
			this.showMessage(getResources().getString(R.string.password_not_match), null);
			return;
		}
		//String url =String.format("api/r_u_p?userid=%d&pwd=%s",AppSettings.userid,password);
		String url =String.format("api/reset_user_pwd?userid=%d&oldpwd=%s&newpwd=%s",AppSettings.userid,oldpassword,password);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(password1.getWindowToken(), 0);
		this.get(url, 1);
		
		
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		if (AppTools.isSuccess(result)){
			this.showMessage(this.getString(R.string.password_change_success), null);
			this.finish();
			return;
		}
		try{
			
			JSONObject json = (JSONObject)result;
			this.showMessage(json.getString("message"), null);
		}catch(Exception e){
			log(e);
		}
		
		
		
	}
}
