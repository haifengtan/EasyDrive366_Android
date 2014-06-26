package cn.count.easydrive366;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;

public class SignupActivity extends BaseHttpActivity {
	private Button btnLogin;
	private Button btnSignup;
	private EditText edtUsername;
	private EditText edtPassword;
	private EditText edtRePassword;
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		this._isHideTitleBar = false;
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.modules_signup_activity);
		
		btnSignup =(Button)findViewById(R.id.btn_signup_signup);
		edtUsername = (EditText)findViewById(R.id.edt_signup_username);
		edtPassword = (EditText)findViewById(R.id.edt_signup_passwrod);
		edtRePassword = (EditText)findViewById(R.id.edt_signup_repasswrod);
		
		btnSignup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				signup();
				
			}});
		Intent intent = getIntent();
		edtUsername.setText(intent.getStringExtra("username"));
	}
	
	@Override
	public void processMessage(int msgType, final Object result) {
		super.processMessage(msgType, result);
		if (this.isSuccess(result)){
			AppSettings.login((JSONObject) result,this);
			this.saveLogin(edtUsername.getText().toString(), edtPassword.getText().toString(), true);
			Bundle bundle =new Bundle();
			bundle.putString("result",result.toString());
			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK,intent);
			finish();
			
		}else{
			String message;
			try {
				message = ((JSONObject)result).getString("message");
				this.showMessage(message, null);
			} catch (JSONException e) {
			
				log(e);
			}
			
		}
	}
	private void signup(){
		String username= edtUsername.getText().toString();
		String password = edtPassword.getText().toString();
		String repassword = edtRePassword.getText().toString();
		if (username.equals("")){
			this.showMessage(getResources().getString(R.string.username_not_empty), null);
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
		String url =String.format("api/signup?username=%s&password=%s",username,password);
		//String url =String.format("api/initstep4?userid=%d&username=%s&password=%s",AppSettings.userid,username,password);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtUsername.getWindowToken(), 0);
		this.get(url, 1);
		
	}

}
