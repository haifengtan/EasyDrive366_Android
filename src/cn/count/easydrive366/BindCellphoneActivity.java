package cn.count.easydrive366;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BindCellphoneActivity extends BaseHttpActivity {
	private int _isbind;
	private String _cellphone;
	private Button btnBind;
	private Button btnGetCode;
	private EditText edtCellphone;
	private EditText edtCode;
	private Timer _timer;
	private int Counter=60;
	final Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if (msg.what==1){
				btnGetCode.setEnabled(false);
				btnGetCode.setText(String.format("重新获取验证码(%d)", Counter));
				Counter--;
				if (Counter<=0){
					_timer.cancel();
					_timer = null;
					btnGetCode.setEnabled(true);
					btnGetCode.setText(getString(R.string.get_code));
				}
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.modules_bindcellphone_activity);
		this.setupLeftButton();
		this.setRightButtonInVisible();
		this.setupPhoneButtonInVisible();
		Intent intent = this.getIntent();
		_isbind = intent.getIntExtra("isbind", 0);
		_cellphone = intent.getStringExtra("phone");
		this.init_view();
		
	}
	private void init_view(){
		this.setBarTitle(this.getResources().getString(_isbind==1?R.string.bind:R.string.unbind));
		
		btnBind = (Button)findViewById(R.id.btn_bind);
		btnGetCode=(Button)findViewById(R.id.btn_get_code);
		edtCellphone = (EditText)findViewById(R.id.edt_cellphone);
		edtCode = (EditText)findViewById(R.id.edt_code);
		edtCellphone.setText(_cellphone);
		if (_isbind==0){			
			edtCellphone.setEnabled(false);
			btnBind.setText(getString(R.string.unbind));
		}
		btnBind.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				bindCellphone();
				
			}});
		btnGetCode.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getCode();
				
			}});
	}
	private void getCode(){
		if (edtCellphone.getText().toString().length()<11){
			this.showMessage(this.getString(R.string.input_cellphone),null);
			return;
		}
		this.get(String.format("api/get_sms_code?userid=%d&phone=%s&isbind=%d", AppSettings.userid,edtCellphone.getText().toString(),this._isbind), 1);
	}
	private void bindCellphone(){
		if (edtCode.getText().toString().length()!=6){
			this.showMessage(getString(R.string.input_verify_code),null);
			return;
		}
		if (_isbind==0){
			this.get(String.format("api/reset_phone?userid=%d&code=%s",AppSettings.userid,edtCode.getText().toString()), 3);
		}else{
			this.get(String.format("api/chk_sms_code?userid=%d&code=%s",AppSettings.userid,edtCode.getText().toString()), 2);
		}
		
	}
	@Override
	public void processMessage(int msgType, final Object result) {

		try{
			if (AppTools.isSuccess(result)){
				JSONObject json =(JSONObject)result;
				switch (msgType){
				case 1:
					//this.showMessage(json.getString("result"),null);
					Log.e("Reload", json.getString("result"));
					startWaiting();
					break;
				case 2:
					if (_timer!=null){
						_timer.cancel();
						_timer = null;
					}
					String info = json.getString("result");
					this.showMessage(info, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Bundle bundle = new Bundle();
							bundle.putInt("ïsbind", 0);
							Intent intent = new Intent();
							intent.putExtras(bundle);
							setResult(RESULT_OK,intent);
							finish();
							
						}
					});
					
					break;
				case 3:
					if (_timer!=null){
						_timer.cancel();
						_timer = null;
					}
					String info2 = json.getString("result");
					this.showMessage(info2,  new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Bundle bundle2 = new Bundle();
							bundle2.putInt("ïsbind", 1);
							Intent intent2 = new Intent();
							intent2.putExtras(bundle2);
							setResult(RESULT_OK,intent2);
							finish();
							
						}});
					
					break;
				}
			}
		}catch(Exception e){
			log(e);
		}
		
	}
	
	private void startWaiting(){
		Counter = 60;
		_timer = new Timer();
		_timer.schedule(new TimerTask(){

			@Override
			public void run() {
				Message msg= handler.obtainMessage();
				msg.what =1;
				handler.sendMessage(msg);
				
			}}, 1, 1000);
	}
}
