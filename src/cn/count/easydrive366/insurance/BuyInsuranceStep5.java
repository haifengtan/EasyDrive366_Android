package cn.count.easydrive366.insurance;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import android.widget.CheckBox;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydrive366.components.InsuranceDetailItem;
import cn.count.easydriver366.base.AppSettings;

import cn.count.easydriver366.base.HttpExecuteGetTask;

public class BuyInsuranceStep5 extends BaseInsurance {
	private String data;
	private boolean is_com;
	private String total;
	private String total_nocomm;
	
	private String total_only_comm;
	
	private boolean is_biz;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buyinsurance_step5);
		this.setupTitle("在线购买保险", "第五步");
		this.setupLeftButton();
		this.setupRightButtonWithText("下一步");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init_view();
		
	}
	@Override
	protected void onRightButtonPress() {
		doSave();
	}
	private void doSave(){
		
		
		String url = String.format("ins/carins_check?userid=%d&is_comm=%d&is_biz=%d",
				AppSettings.userid,is_com?1:0,is_biz?1:0);
				
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				process_result(result);
				
			}}.execute(url);
			
	}
	private void process_result(final String result){
		
		if (AppSettings.isSuccessJSON(result, this)){
			Intent intent = new Intent(this,BuyInsuranceStep6.class);
			intent.putExtra("data", result);
			startActivity(intent);
			stack_push();
		}
	}
	private CheckBox txt_section0;
	private CheckBox txt_section1;
	private TextView txt_section2;
	
	private TableLayout table_biz;
	private TextView txt_com_com;
	private TextView txt_com_tax;
	private TextView txt_com_total;
	private TextView txt_total;
	private void init_view(){
		txt_section0 = (CheckBox)findViewById(R.id.txt_section0);
		txt_section1 = (CheckBox)findViewById(R.id.txt_section1);
		txt_section2 = (TextView)findViewById(R.id.txt_section2);
		
		txt_com_com = (TextView)findViewById(R.id.txt_com_com);
		txt_com_tax = (TextView)findViewById(R.id.txt_com_tax);
		txt_com_total = (TextView)findViewById(R.id.txt_com_total);
		txt_total = (TextView)findViewById(R.id.txt_total);
		table_biz = (TableLayout)findViewById(R.id.table_biz);
		
		
		
		data  = this.getIntent().getStringExtra("data");
		
		
		try{
			JSONObject json = new JSONObject(data).getJSONObject("result");
			JSONObject biz = json.getJSONObject("biz");
			JSONObject com = json.getJSONObject("com");
			is_com =com.getInt("is_com")==1;
			is_biz =biz.getInt("is_biz")==1;
			txt_section0.setText(biz.getString("title"));
			txt_section1.setText(com.getString("title"));
			txt_section2.setText(json.getString("price_content"));
			txt_section1.setChecked(is_com);
			txt_section0.setChecked(is_biz);
			txt_com_com.setText(com.getString("com"));
			txt_com_tax.setText(com.getString("tax"));
			txt_com_total.setText(com.getString("com_total"));
			total = json.getString("total");
			total_nocomm = json.getString("total_nocomm");
			total_only_comm= json.getString("total_only_comm");
			txt_total.setText(is_com?total:total_nocomm);
			
			JSONArray bizList= biz.getJSONArray("list");
			//table_biz.removeAllViews();
			for(int i=0;i<bizList.length();i++){
				JSONObject item = bizList.getJSONObject(i);
				TableRow row = new TableRow(this);
				InsuranceDetailItem detail = new InsuranceDetailItem(this,null);
				detail.setData(item.getString("insu_name"),item.getString("amount"),item.getString("fee"),item.getString("no_excuse"));
				row.addView(detail);
				table_biz.addView(row);
			}
			txt_section1.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					is_com = txt_section1.isChecked();
					txt_total.setText(is_com?total:total_nocomm);
					
				}});
			txt_section0.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					is_biz= txt_section0.isChecked();
					txt_total.setText(is_biz?total:total_only_comm);
					
				}});
			/*
			txt_section1.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					is_com = isChecked;
					txt_total.setText(is_com?total:total_nocomm);
				}});
			txt_section0.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					is_biz= isChecked;
					txt_total.setText(is_biz?total:total_only_comm);
				}});
			*/
		}catch(Exception e){
			log(e);
		}
	}
}
