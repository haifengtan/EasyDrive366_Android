package cn.count.easydrive366;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;

public class LoginActivity extends BaseHttpActivity {
	private Button btnLogin;
	private Button btnSignup;
	private EditText edtUsername;
	private EditText edtPassword;
	private CheckBox chbRememberPassword;
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		this._isHideTitleBar = false;
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.modules_login_activity);
		btnLogin = (Button)findViewById(R.id.btn_login_login);
	
		edtUsername = (EditText)findViewById(R.id.edt_login_username);
		edtPassword = (EditText)findViewById(R.id.edt_login_passwrod);
		chbRememberPassword = (CheckBox)findViewById(R.id.chb_remember_password);
		SharedPreferences pref = this.getSharedPreferences("Login_histroy", MODE_PRIVATE);	
		
		edtUsername.setText(pref.getString("username", ""));
		chbRememberPassword.setChecked(pref.getBoolean("remember_password", false));
		if (chbRememberPassword.isChecked()){
			edtPassword.setText(pref.getString("password", ""));
		}else{
			edtPassword.setText("");
		}
		btnLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				login(edtUsername.getText().toString(),edtPassword.getText().toString());
				
			}});
		findViewById(R.id.img_choose).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				chooseUser();
				
			}});
		
	}
	/*
	private void saveLogin(){
		SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString("username", edtUsername.getText().toString());
		editor.putString("password", edtPassword.getText().toString());
		editor.putBoolean("remember_password", chbRememberPassword.isChecked());
		try{
			JSONObject login = new JSONObject();
			login.put("username", edtUsername.getText().toString());
			login.put("password", edtPassword.getText().toString());
			login.put("remember_password", chbRememberPassword.isChecked());
			String logins = pref.getString("logins", "");
			JSONArray login_list;
			if (logins.equals("")){
				login_list = new JSONArray();
			}else{
				login_list = new JSONArray(logins);
			}
			int index = login_list.length();
			for(int i=0;i<index;i++){
				JSONObject item = login_list.getJSONObject(i);
				if (item.getString("username").trim().equals(edtUsername.getText().toString().trim())){
					index = i;
					break;
				}
			}
			login_list.put(index, login);
			editor.putString("logins", login_list.toString());
			
		}catch(Exception e){
			log(e);
		}
		editor.commit();
	}
	*/
	private void login(final String username,final String password){
		//String username= edtUsername.getText().toString();
		//String password = edtPassword.getText().toString();
		if (!this.isOnline()){
			this.showMessage("请先联网再继续。", null);
			return;
		}
		if (username.equals("")){
			this.showMessage(getResources().getString(R.string.username_not_empty), null);
			return;
		}
		if (password.equals("") ){
			this.showMessage(getResources().getString(R.string.password_not_empty), null);
			return;
		}
		String url =String.format("api/login?username=%s&password=%s&androidversion=%d",username,password,android.os.Build.VERSION.SDK_INT);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtUsername.getWindowToken(), 0);
		this.get(url, 1);
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		super.processMessage(msgType, result);
		if (this.isSuccess(result)){
			//绑定百度推送
			AppSettings.initBaiduPush(getApplicationContext(), this);
			//登录设置
			AppSettings.login((JSONObject) result,this);
			//saveLogin();
			this.saveLogin(edtUsername.getText().toString(), edtPassword.getText().toString(), chbRememberPassword.isChecked());
			Bundle bundle =new Bundle();
			bundle.putString("result",result.toString());
			Intent intent = new Intent();
			intent.putExtras(bundle);
			setResult(RESULT_OK,intent);
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtUsername.getWindowToken(), 0);
			finish();
			
		}else{
			String message;
			try {
				message = ((JSONObject)result).getString("message");
				//this.showMessage(message, null);
			} catch (JSONException e) {
				log(e);
			}
			
		}
	}
	private void setupUser(final JSONObject user){
		try{
			if (user.getBoolean("remember_password")){
				this.login(user.getString("username"), user.getString("password"));
				return;
			}
			this.edtUsername.setText(user.getString("username"));
			this.chbRememberPassword.setChecked(user.getBoolean("remember_password"));
			if (this.chbRememberPassword.isChecked()){
				this.edtPassword.setText(user.getString("password"));
			}else{
				this.edtPassword.setText("");
			}
		}catch(Exception e){
			log(e);
		}
	}
	
	private void chooseUser(){
		SharedPreferences pref = this.getSharedPreferences("Login_histroy",MODE_PRIVATE);
		//SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
		String logins = pref.getString("logins", "");
		if (logins.equals("")){
			return;
		}
		
		
		try{
			final JSONArray list = new JSONArray(logins);
			final String[] items = new String[list.length()];		
			
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				
				String name = item.getString("username");
				
				items[i]=name;
				
				
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle(this.getResources().getString(R.string.app_name)).setItems(items,  new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try{
						setupUser(list.getJSONObject(which));
					}catch(Exception e){
						log(e);
					}
					
					
				}
			});
			builder.show();
		}catch(Exception e){
			
			
			return;
		}
		
		
		
		
	}

}
