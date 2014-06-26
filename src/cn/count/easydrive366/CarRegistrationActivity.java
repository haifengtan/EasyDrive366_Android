package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import cn.count.easydrive366.components.CarShopItem;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;

public class CarRegistrationActivity extends BaseListViewActivity {
	private JSONObject _result;
	private JSONArray _shops;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_carregistration_activity);
		this.setupLeftButton();
		this.setupRightButtonWithText("编辑");
		restoreFromLocal(1);
		this.reload_data();
		this.setupScrollView();
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_get_car_registration(), 1);
	}
	@Override
	protected void initData(Object result, int msgType) {
		try{
			if (msgType==1){
				_result= (((JSONObject)result).getJSONObject("result")).getJSONObject("data");
				_shops= (((JSONObject)result).getJSONObject("result")).getJSONArray("list");
				this.saveWithKey("car_registration", _result);
			}
		}catch(Exception e){
			this.log(e);
		}

	}

	@Override
	protected void initView() {
		try{
			//check_date
			//((TextView)findViewById(R.id.txt_carregistration_brand)).setText(_result.getString("brand"));
			((TextView)findViewById(R.id.txt_carregistration_check_date  )).setText(_result.getString("check_date"));
			((TextView)findViewById(R.id.txt_carregistration_car_typename  )).setText(_result.getString("car_typename"));
			((TextView)findViewById(R.id.txt_carregistration_engine_no  )).setText(_result.getString("engine_no"));
			//((TextView)findViewById(R.id.txt_carregistration_issue_date  )).setText(_result.getString("issue_date"));
			((TextView)findViewById(R.id.txt_carregistration_model  )).setText(_result.getString("model"));
			((TextView)findViewById(R.id.txt_carregistration_plate_no  )).setText(_result.getString("plate_no"));
			((TextView)findViewById(R.id.txt_carregistration_registration_date  )).setText(_result.getString("registration_date"));
			((TextView)findViewById(R.id.txt_carregistration_untreated_fine  )).setText(_result.getString("untreated_fine"));
			((TextView)findViewById(R.id.txt_carregistration_untreated_mark  )).setText(_result.getString("untreated_mark"));
			((TextView)findViewById(R.id.txt_carregistration_untreated_number  )).setText(_result.getString("untreated_number"));
			((TextView)findViewById(R.id.txt_carregistration_vin  )).setText(_result.getString("vin"));
			((TextView)findViewById(R.id.txt_carregistration_owner_name  )).setText(_result.getString("owner_name"));
			((TextView)findViewById(R.id.txt_carregistration_owner_license  )).setText(_result.getString("owner_license"));
			this.setupRightButton();
			/*
			((Button)findViewById(R.id.btn_carregistration_edit)).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CarRegistrationActivity.this,CarRegistrationEditActivity.class);
					intent.putExtra("data", _result.toString());
					startActivityForResult(intent,0);
					
				}});
				*/
			((Button)findViewById(R.id.btn_carregistration_search)).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CarRegistrationActivity.this,ViolationSearchActivity.class);
					//intent.putExtra("data", _result.toString());
					startActivityForResult(intent,1);
					
				}});
			LinearLayout items = (LinearLayout)findViewById(R.id.layout_shops);
			items.removeAllViews();
			for(int i=0;i<_shops.length();i++){
				JSONObject obj = _shops.getJSONObject(i);
				CarShopItem item = new CarShopItem(this,null,obj.getString("name"),String.format("%s(%s)", obj.getString("address"),obj.get("phone")));
				item.setTag(obj.getString("url"));
				item.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						String url = (String)v.getTag();
						if (url!=null && !url.isEmpty()){
							Intent intent = new Intent(CarRegistrationActivity.this,BrowserActivity.class);
							intent.putExtra("url", url);
							CarRegistrationActivity.this.startActivity(intent);
							
						}
						
					}});
				items.addView(item);
			}
			this.endRefresh();
		}catch(Exception e){
			log(e);
		}
		

	}
	@Override
	protected void onRightButtonPress(){
		Intent intent = new Intent(CarRegistrationActivity.this,CarRegistrationEditActivity.class);
		intent.putExtra("data", _result.toString());
		startActivityForResult(intent,0);
	}
	@Override
	protected void setupListItem(ViewHolder holder, Map<String, Object> info) {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==0 && resultCode==RESULT_OK){
			Bundle extras = data.getExtras();
			try {
				this.processMessage(1, new JSONObject(extras.getString("result")));
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
		}
	}
}
