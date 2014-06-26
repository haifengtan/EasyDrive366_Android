package cn.count.easydrive366;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import cn.count.easydrive366.signup.Step1Activity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


public class WelcomeActivity extends Activity {
	private boolean _userWantQuit=false;
	private Timer _quitTimer;
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (AppSettings.isquiting){
			finish();
			System.exit(0);
		}
		setContentView(R.layout.activity_welcome);
		AppSettings.restore_login_from_device(this);
		/*
		if (AppSettings.isLogin){
			this.goHome();
		}else{
			findViewById(R.id.btn_welcome_login).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent =new Intent(WelcomeActivity.this,LoginActivity.class);
					startActivityForResult(intent,1);
					
				}
				
			});
			findViewById(R.id.btn_welcome_signup).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent =new Intent(WelcomeActivity.this,SignupActivity.class);
					startActivityForResult(intent,2);
					
				}
				
			});
		}
		*/
		findViewById(R.id.btn_welcome_login).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent =new Intent(WelcomeActivity.this,LoginActivity.class);
				startActivityForResult(intent,1);
				
			}
			
		});
		findViewById(R.id.btn_welcome_signup).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent =new Intent(WelcomeActivity.this,SignupActivity.class);
				//Intent intent =new Intent(WelcomeActivity.this,Step1Activity.class);
				startActivityForResult(intent,2);
				
			}
			
		});
		
	}

	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		if (AppSettings.isLogin){
			goHome(1);
		}
		/*
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode==1 || requestCode==2) && resultCode==RESULT_OK){
			Bundle extras = data.getExtras();
			try {
				this.processMessage(requestCode, new JSONObject(extras.getString("result")));
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
		}*/
	}
	private void processMessage(final int msgType,final JSONObject result){
		try{
			/*
			AppSettings.userid = result.getJSONObject("result").getInt("id");
			AppSettings.username= result.getJSONObject("result").getString("username");
			AppSettings.isLogin= true;
			*/
			this.goHome(1);
		}catch(Exception e){
			AppTools.log(e);
		}
	}
	private void goHome(final int code){
		/*
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		if (imm.isActive()) { 
	
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		 
		} 
		*/
		/*
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		*/
		setResult(code,null);
		finish();
	}
	
	
	@Override
	public void onBackPressed() {
		setResult(-100,null);
		finish();
		/*
		if (_userWantQuit){
			this.finish();
			AppSettings.isquiting = true;
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

							
		};
		*/
	}
}

