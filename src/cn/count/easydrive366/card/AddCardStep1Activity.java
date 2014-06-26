package cn.count.easydrive366.card;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;

public class AddCardStep1Activity extends BaseHttpActivity {
	private EditText edtCode;
	private TextView txtInfor;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_addcardstep1_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		txtInfor = (TextView)findViewById(R.id.txt_info);
		edtCode =(EditText)findViewById(R.id.edt_code);
		
		this.get(AppSettings.get_addcardstep0(), 1);
		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				activateCode();
				
			}});
		
	}
	@Override
	public void processMessage(int msgType, final Object result) {

		if (msgType==1){
			try{
				JSONObject json = (JSONObject)result;
				final String info = json.getJSONObject("result").getString("data");
				this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						txtInfor.setText(info);
						
					}});
			}catch(Exception e){
				log(e);
			}
		}else if (msgType==2){
			try{
				JSONObject json = (JSONObject)result;
				final String number = json.getJSONObject("result").getJSONObject("data").getString("number");
				Intent intent = new Intent(this,AddCardStep2Activity.class);
				intent.putExtra("number", number);
				startActivity(intent);
			}catch(Exception e){
				log(e);
			}
		}
		
	}
	private void activateCode(){
		String code = edtCode.getText().toString();
		if (code.equals("")){
			this.showMessage("请输入激活码！", null);
			return;
		}
		this.get(AppSettings.add_inscard_step1(code), 2);
	}
}
