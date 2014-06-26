package cn.count.easydrive366.insurance;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class InsuranceList2 extends BaseHttpActivity {
	private JSONArray list;
	private JSONArray list2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.modules_insurancelist2_activity);
		this.setupRightButtonWithText("删除");
		this.setupLeftButton();
		this.setBarTitle("投保单");
		load_data();

	}

	private void load_data() {
		String url = String.format("ins/list_quote?userid=%d",
				AppSettings.userid);
		beginHttp();
		new HttpExecuteGetTask() {

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				process_result(result);

			}
		}.execute(url);
	}

	private void process_result(final String result) {
		if (AppSettings.isSuccessJSON(result, this)) {
			try {
				JSONObject json = new JSONObject(result);
				list = json.getJSONArray("result");
				fill_view();
			} catch (Exception e) {
				log(e);
			}

		}

	}

	private void fill_view() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_items);
		layout.removeAllViews();
		try {
			for (int i = 0; i < list.length(); i++) {
				JSONObject obj = list.getJSONObject(i);
				obj.put("selected", 0);
				InsuranceItem item = new InsuranceItem(this, null, obj);
				layout.addView(item);
				item.setTag(obj.getString("id"));
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String id = (String) v.getTag();
						open_order(id);

					}
				});
			}
		} catch (Exception e) {
			log(e);
		}

	}

	private void open_order(final String id) {
		if (id.isEmpty()) {
			return;
		}
		Intent intent = new Intent(this, BuyInsuranceStep2.class);
		intent.putExtra("id", id);
		intent.putExtra("is_list", true);
		startActivity(intent);
	}

	@Override
	protected void onRightButtonPress() {
		if (list==null){
			return;
		}
		try {
			list2 = new JSONArray();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < list.length(); i++) {
				
				JSONObject info = list.getJSONObject(i);
				if (info.getInt("selected") == 1) {

					if (!info.getString("id").isEmpty()) {
						sb.append(info.getString("id"));
						sb.append(",");
					}else{
						list2.put(info);
					}
				}else{
					list2.put(info);
				}
			}
			if (sb.length()==0){
				this.showMessage("请先选择投保单再继续操作。", null);
				return;
			}
			String url = String.format("ins/del_quote?userid=%d&id=%s",AppSettings.userid,sb.toString().substring(0, sb.toString().length() - 1));
			beginHttp();
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					endHttp();
					if (AppSettings.isSuccessJSON(result, InsuranceList2.this)){
						list =list2;
						fill_view();
					}
					
				}}.execute(url);
		} catch (Exception e) {
			log(e);
		}

	}
}
