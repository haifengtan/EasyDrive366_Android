package cn.count.easydrive366.baidumap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.BaseHttpActivity;

public class ShowShopInformationActivity extends BaseHttpActivity {
	private TextView txt_name;
	private TextView txt_address;
	private TextView txt_phone;
	private TextView txt_description;
	private double latitude;
	private double longtitude;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_showshopinformation_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		txt_name = (TextView)findViewById(R.id.txt_name);
		txt_address =(TextView)findViewById(R.id.txt_address);
		txt_phone =(TextView)findViewById(R.id.txt_phone);
		txt_description=(TextView)findViewById(R.id.txt_description);
		Intent intent = this.getIntent();
		txt_name.setText(intent.getStringExtra("name"));
		txt_address.setText(intent.getStringExtra("address"));
		txt_phone.setText(intent.getStringExtra("phone"));
		txt_description.setText(intent.getStringExtra("description"));
		latitude = intent.getDoubleExtra("latitude", 0);
		longtitude = intent.getDoubleExtra("longtitude",0);
		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gotoNavigation();
				
			}});
		
		
	}
	private void gotoNavigation()
	{
		Intent intent =new Intent(this,BaiduMapNavigationActivity.class);
		intent.putExtra("name", txt_name.getText());
		intent.putExtra("address", txt_address.getText());
		intent.putExtra("phone", txt_phone.getText());
		intent.putExtra("description", txt_description.getText());
		intent.putExtra("latitude", latitude);
		intent.putExtra("longtitude", longtitude);
		startActivity(intent);
		finish();
	}
}
