package cn.count.easydrive366;

import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import cn.count.easydrive366.components.InsuranceView;


import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;


public class BusinessInsuranceActivity extends BaseHttpActivity {
	private JSONObject _result;
	private JSONObject _curr=null;
	private JSONObject _renew=null;
	private JSONObject _price = null;
	private TableLayout tableLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_businessinsurance_activity);
		this.setupLeftButton();
		restoreFromLocal(1);
		reload_data();
		this.setupScrollView();
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_get_business_insurance(), 1);
		this.get(AppSettings.url_get_suggestion_count(), 2,"");
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		try{
			if (msgType==1){
				_result= (((JSONObject)result).getJSONObject("result")).getJSONObject("data");
				Log.e("Business Insurance", _result.toString());
				_curr  =_result.getJSONObject("curr");
				if (!_result.isNull("renew")){
					_renew =_result.getJSONObject("renew");
				}
				if (!_result.isNull("pricelist")){
					_price = _result.getJSONObject("pricelist");
				}
				this.runOnUiThread(initViewThread);
			}else if (msgType==2){
				final int count = ((JSONObject)result).getInt("result");
				
				
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						if (count==0){
							setRightButtonInVisible();
						}else{
							setupRightButtonWithText(getResources().getString(R.string.insurance_suggestion));
						}
						
						
				}});
				/*
				final Button btn = (Button)findViewById(R.id.btn_modules_businessinsurance_suggestion);
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						if (count==0){
							btn.setVisibility(View.GONE);
						}else{
							btn.setOnClickListener(buttonSuggestionPress);
						}
						
						
				}});
				*/
				
			}
		}catch(Exception e){
			this.log(e);
		}

	}
	@Override 
	protected void onRightButtonPress(){
		Intent intent = new Intent(BusinessInsuranceActivity.this,BusinessSuggestionActivity.class);
		startActivity(intent);
	}
	private OnClickListener buttonSuggestionPress = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(BusinessInsuranceActivity.this,BusinessSuggestionActivity.class);
			startActivity(intent);
			
			
		}};
	private void addTableRow(final String title,final JSONObject json){
		InsuranceView view = new InsuranceView(this,null);
		view.setData(json,title);
		TableRow tr = new TableRow(this);
		tr.addView(view);
	
		tableLayout.addView(tr);
		View tr2 = new View(this);
		tr2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,20));
		tableLayout.addView(tr2);

	}
	private void initView(){
		tableLayout = (TableLayout)findViewById(R.id.table_modules_businessinsurance_table);
		tableLayout.removeAllViewsInLayout();
		try{
			if (_curr!=null){
				this.addTableRow(_curr.getString("title"),_curr);
			}
			if (_renew!=null){
				this.addTableRow(_renew.getString("title"),_renew);
			}
			if (_price!=null){
				this.addTableRow(_price.getString("title"),_price);
			}
			
		}catch(Exception e){
			log(e);
		}
		
		this.endRefresh();
	}
	private Runnable initViewThread = new Runnable(){

		@Override
		public void run() {
			initView();
		}
		
	};

	

}
