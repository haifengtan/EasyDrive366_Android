package cn.count.easydrive366;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;

public class InformationDetailActivity extends BaseHttpActivity {
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
		this.setTitle("详细信息");
		txtTitle = (TextView)findViewById(R.id.txt_title);
		txtAddress = (TextView)findViewById(R.id.txt_address);
		txtContent = (TextView)findViewById(R.id.txt_content);
		txtAddress.setVisibility(android.view.View.GONE);
		Intent intent = this.getIntent();
		String id = intent.getStringExtra("id");
		this.get(String.format("api/get_news_by_id?userid=%d&newsid=%s",AppSettings.userid, id), 1);
	}
	@Override
	public void processMessage(int msgType, final Object result) {

		if (AppTools.isSuccess(result)){
			try{
				JSONObject json = ((JSONObject)result).getJSONObject("result").getJSONArray("data").getJSONObject(0);
				final String title = json.getString("fmt_createDate");
				final String description = json.getString("description");
				
				this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						txtTitle.setText(title);
						
						txtContent.setText(description);
						
					}});
			}catch(Exception e){
				log(e);
			}
			
		}
		
	}
}
