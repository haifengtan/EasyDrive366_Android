package cn.count.easydrive366;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cn.count.easydrive366.components.InformationRow;
import cn.count.easydrive366.components.InsuranceDetailItem;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;

public class ActivateCodeShowActivity extends BaseHttpActivity {
	private TextView txt_no;
	private TextView txt_code;
	private TextView txt_time;
	private TextView txt_to;
	private JSONArray _contents;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_activatecode_show_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		txt_no = (TextView)findViewById(R.id.txt_no);
		txt_code =(TextView)findViewById(R.id.txt_code);
		txt_time =(TextView)findViewById(R.id.txt_time);
		txt_to=(TextView)findViewById(R.id.txt_to);
		Intent intent = this.getIntent();
		txt_no.setText(intent.getStringExtra("number"));
		txt_code.setText(intent.getStringExtra("code"));
		txt_time.setText(intent.getStringExtra("activate_date"));
		txt_to.setText(intent.getStringExtra("valid_date"));
		//this.get(AppSettings.url_user_activate_code(), 1);
		String tempString = intent.getStringExtra("contents");
		if (tempString!=null && !tempString.isEmpty()){
			try
			{
				 _contents = new JSONArray(tempString);
				 for(int i=0;i<_contents.length();i++){
					 JSONObject item = _contents.getJSONObject(i);
					 addTablerow(item.getString("name"),item.getString("count"),i==_contents.length()-1);
					 
				 }
				
			}
			catch(Exception e){
				log(e);
			}
		}
		
	}
	private void addTablerow(final String name,final String count,final boolean isLast){
		TableLayout table =(TableLayout)findViewById(R.id.table_items);
		if (table!=null){
			TableRow row = new TableRow(this);
			InformationRow detail = new InformationRow(this,null);
			detail.setData(name+"：  ",String.format("%s次",count));
			row.addView(detail);
			table.addView(row);
			if (isLast){
				detail.setEndBackend();
			}
		}
	}
	@Override
	public void processMessage(int msgType, final Object result) {

		if (msgType==1){
			if (AppTools.isSuccess(result)){
				try{
					final JSONObject json = (JSONObject)result;
					this.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							try {
								txt_no.setText(json.getString("number"));
								txt_code.setText(json.getString("code"));
								txt_time.setText(json.getString("activate_date"));
								txt_to.setText(json.getString("valid_date"));
							} catch (JSONException e) {
								log(e);
							}
						}});
					
				}catch(Exception e){
					log(e);
				}
			}
		}
	}
}
