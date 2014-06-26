package cn.count.easydrive366;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;

public class ViolationDetailActivity extends BaseHttpActivity {
	TextView txtTitle;
	TextView txtAddress;
	TextView txtContent;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_violationdetail_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		this.setTitle("违章内容");
		txtTitle = (TextView)findViewById(R.id.txt_title);
		txtAddress = (TextView)findViewById(R.id.txt_address);
		txtContent = (TextView)findViewById(R.id.txt_content);
		Intent intent = this.getIntent();
		String id = intent.getStringExtra("id");
		this.get(String.format("api/get_illegally?userid=%d&id=%s",AppSettings.userid, id), 1);
	}
	@Override
	public void processMessage(int msgType, final Object result) {

		if (AppTools.isSuccess(result)){
			try{
				JSONObject json = ((JSONObject)result).getJSONObject("result").getJSONObject("data");
				final String address = json.getString("Address");
				final String reason = json.getString("Reason");
				final String fine = json.getString("Fine");
				final String mark = json.getString("Mark");
				final String occur = json.getString("OccurTime");
				this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						txtTitle.setText(String.format("%s 罚款%s扣%s", occur,fine,mark));
						txtAddress.setText(address);
						txtContent.setText(reason);
						
					}});
			}catch(Exception e){
				log(e);
			}
			
		}
		
	}

}
