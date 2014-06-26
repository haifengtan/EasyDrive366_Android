package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;

public class CarServiceVendorActivity extends BaseListViewActivity {
	private String _shopName;
	private String _address;
	private String _phone;
	private String _description;
	private String code;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_carservice_vendor_activity);
		Intent intent = this.getIntent();
		code = intent.getStringExtra("code");
		this.setRightButtonInVisible();
		this.setupLeftButton();
		restoreFromLocal(1);
		reload_data();
		
		
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_for_get_carservice_vendor(code),1);
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