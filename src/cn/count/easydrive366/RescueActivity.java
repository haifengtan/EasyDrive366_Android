package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONObject;


import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;

import android.os.Bundle;
import android.widget.TextView;

public class RescueActivity extends BaseListViewActivity {
	private String _shopName;
	private String _address;
	private String _phone;
	private String _description;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_rescue_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		restoreFromLocal(1);
		reload_data();
		
		
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_for_rescue(),1);
	}
	
	@Override
	protected void initData(Object result,int msgType) {
		try{
			JSONObject obj = ((JSONObject)result).getJSONObject("result");
			_shopName = obj.getJSONObject("data").getString("shop_name");
			_address = obj.getJSONObject("data").getString("address");
			_phone = obj.getJSONObject("data").getString("phone");
			_description = obj.getJSONObject("data").getString("description");
		}catch(Exception e){
			log(e);
		}

	}

	@Override
	protected void initView() {
		TextView shopname= (TextView)findViewById(R.id.txt_rescue_ShopName);
		TextView address = (TextView)findViewById(R.id.txt_rescue_Address);
		//TextView phone = (TextView)findViewById(R.id.txt_rescue_Phone);
		TextView description = (TextView)findViewById(R.id.txt_rescue_Description);
		shopname.setText(_shopName);
		address.setText(_address);
		//phone.setText(_phone);
		description.setText(_description);

	}

	@Override
	protected void setupListItem(ViewHolder holder, Map<String, Object> info) {
		// TODO Auto-generated method stub
		

	}

}
