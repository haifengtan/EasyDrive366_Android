package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import cn.count.easydrive366.BaseListViewActivity.ViewHolder;
import cn.count.easydriver366.base.AppSettings;

public class CarServiceNoteActivity extends BaseListViewActivity {
	private String _shopName;
	private String _address;
	private String _phone;
	private String _description;
	private String code;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		code = intent.getStringExtra("code");
		setContentView(R.layout.modules_carservice_note_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		restoreFromLocal(1);
		reload_data();
		
		
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_for_get_carservice_note(code),1);
	}
	
	@Override
	protected void initData(Object result,int msgType) {
		try{
			JSONObject obj = ((JSONObject)result).getJSONObject("result");
			JSONArray list = obj.getJSONArray("data");
			JSONObject item = list.getJSONObject(0);
			_shopName =item.getString("title");
		
			_description = item.getString("description");
		}catch(Exception e){
			log(e);
		}

	}

	@Override
	protected void initView() {
		TextView shopname= (TextView)findViewById(R.id.txt_rescue_ShopName);
	
		TextView description = (TextView)findViewById(R.id.txt_rescue_Description);
		shopname.setText(_shopName);
		description.setText(_description);

	}

	@Override
	protected void setupListItem(ViewHolder holder, Map<String, Object> info) {
		// TODO Auto-generated method stub
		

	}

}