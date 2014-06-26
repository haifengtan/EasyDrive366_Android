package cn.count.easydrive366.afterpay;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydrive366.order.OrderDetailActivity;
import cn.count.easydriver366.base.BaseHttpActivity;

public class FinishedActivity extends BaseHttpActivity {
	private String data;
	private String order_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_afterpay_finished);
		getActionBar().setTitle("订单完成");
		this.setupLeftButton();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init_view();
		
	}
	private void init_view(){
		findViewById(R.id.btn_goods).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				goto_goods();
				
			}});
		findViewById(R.id.btn_order).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				goto_order();
				
			}});
		data = getIntent().getStringExtra("data");
		try{
			JSONObject json = new JSONObject(data);
			((TextView)findViewById(R.id.txt_content)).setText(json.getString("content"));
			order_id  = json.getString("order_id");
		}catch(Exception e){
			log(e);
		}
	}
	private void goto_goods(){
		finish();
	}
	private void goto_order(){
		Intent intent = new Intent(this,OrderDetailActivity.class);
		intent.putExtra("order_id", order_id);
		startActivity(intent);
		finish();
	}

}
