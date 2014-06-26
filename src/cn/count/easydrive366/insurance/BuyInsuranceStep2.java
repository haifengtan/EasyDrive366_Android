package cn.count.easydrive366.insurance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.AppTools.ISetDate;

import cn.count.easydriver366.base.HttpExecuteGetTask;

public class BuyInsuranceStep2 extends BaseInsurance{
	private String goods_id;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buyinsurance_step2);
		this.setupTitle("在线购买保险", "第二步");
		this.setupLeftButton();
		this.setupRightButtonWithText("下一步");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		goods_id = getIntent().getStringExtra("goods_id");
		init_view();
		boolean is_list = getIntent().getBooleanExtra("is_list", false);
		if (is_list){
			stack_init();
		}
	}
	@Override
	protected void onRightButtonPress() {
		doSave();
	}
	private void doSave(){
		if (edt_license.getText().toString().trim().isEmpty()){
			this.showMessage("车牌号码不能为空！",null);
			return;
		}
		if (edt_vin.getText().toString().trim().isEmpty()){
			this.showMessage("VIN不能为空！",null);
			return;
		}
		if (edt_engine_no.getText().toString().trim().isEmpty()){
			this.showMessage("发动机号码不能为空！",null);
			return;
		}
		if (edt_init_date.getText().toString().trim().isEmpty()){
			this.showMessage("初登日期不能为空！",null);
			return;
		}
		if (edt_owner.getText().toString().trim().isEmpty()){
			this.showMessage("车主姓名不能为空！",null);
			return;
		}
		if (edt_name.getText().toString().trim().isEmpty()){
			this.showMessage("被保险人姓名不能为空！",null);
			return;
		}
		
		String id =edt_identity.getText().toString().trim();
		if (id.equals("") || id.length()!=18 || !personIdValidation(id)){
			this.showMessage(this.getResources().getString(R.string.id_is_wrong), null);
			return;
		}
		if (edt_cellphone.getText().toString().trim().isEmpty()){
			this.showMessage("手机号码不能为空！",null);
			return;
		}
		String url=null;
		try {
			url = String.format("ins/carins_confirm?userid=%d&car_id=%s&vin=%s&engine_no=%s&registration_date=%s"+
								"&owner_name=%s&name=%s&license_id=%s&phone=%s"+
								"&owner_license=%s&owner_phone=%s&policy_hoder=%s&policy_hoder_license=%s", 
					AppSettings.userid,
					URLEncoder.encode(edt_license.getText().toString(),"utf-8"),
					edt_vin.getText().toString(),
					edt_engine_no.getText().toString(),
					edt_init_date.getText().toString(),
					URLEncoder.encode(edt_owner.getText().toString(),"utf-8"),
					URLEncoder.encode(edt_name.getText().toString(),"utf-8"),
					edt_identity.getText().toString(),
					edt_cellphone.getText().toString(),
					edt_owner_license.getText(),
					edt_owner_phone.getText(),
					URLEncoder.encode(edt_hoder.getText().toString(),"utf-8"),
					URLEncoder.encode(edt_hoder_license.getText().toString(),"utf-8")
					);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.get(url, 1);
		
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				process_result(result);
				
			}}.execute(url);
			
	}
	
	@Override
	public void processMessage(int msgType, Object result) {
		process_result(result.toString());
	}
	private void process_result(final String result){
		
		if (AppSettings.isSuccessJSON(result, this)){
			Intent intent = new Intent(this,BuyInsuranceStep3.class);
			intent.putExtra("data", result);
			startActivity(intent);
			stack_push();
		}
	}
	private void init_view(){
		load_parameters();
		load_view();
		load_data();
	}
	private void load_parameters(){
		
	}
	private EditText edt_license;
	private EditText edt_vin;
	private EditText edt_engine_no;
	private EditText edt_init_date;
	private EditText edt_owner;
	private EditText edt_name;
	private EditText edt_identity;
	private EditText edt_cellphone;
	
	private EditText edt_owner_license;
	private EditText edt_owner_phone;
	private EditText edt_hoder;
	private EditText edt_hoder_license;
	
	private CheckBox chb_hoder;
	private CheckBox chb_buyer;
	private void load_view(){
		edt_license = (EditText)findViewById(R.id.edt_license);
		edt_vin = (EditText)findViewById(R.id.edt_vin);
		edt_engine_no = (EditText)findViewById(R.id.edt_engine_no);
		edt_init_date = (EditText)findViewById(R.id.edt_init_date);
		edt_owner = (EditText)findViewById(R.id.edt_owner);
		edt_name = (EditText)findViewById(R.id.edt_name);
		edt_identity = (EditText)findViewById(R.id.edt_identity);
		edt_cellphone = (EditText)findViewById(R.id.edt_cellphone);
		
		edt_owner_license = (EditText)findViewById(R.id.edt_owner_license);
		edt_owner_phone = (EditText)findViewById(R.id.edt_owner_phone);
		edt_hoder = (EditText)findViewById(R.id.edt_hoder);
		edt_hoder_license = (EditText)findViewById(R.id.edt_hoder_license);
		
		chb_hoder =(CheckBox)findViewById(R.id.chk_hoder);
		chb_buyer =(CheckBox)findViewById(R.id.chk_buyer);
		
		findViewById(R.id.layout_init_date).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AppTools.chooseDate(edt_init_date.getText().toString(), BuyInsuranceStep2.this, new ISetDate(){

					@Override
					public void setDate(String date) {
						edt_init_date.setText(date);
					}});
				
			}});
	}
	private void load_data(){
		String id = getIntent().getStringExtra("id");
		
		String url;
		if (id!=null && !id.isEmpty()){
			url  = String.format("ins/carins_info?userid=%d&id=%s", AppSettings.userid,id);
		}else{
			if (goods_id!=null && !goods_id.isEmpty()){
				url  = String.format("ins/carins_info?userid=%d&goods_id=%s", AppSettings.userid,goods_id);
			}else{
				url  = String.format("ins/carins_info?userid=%d", AppSettings.userid);
			}
			
		}
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				process_data(result);
				
			}}.execute(url);
	}
	private void process_data(final String result){
		JSONObject json = AppSettings.getSuccessJSON(result,this);
		if (json!=null){
			try{
				String d = json.getString("registration_date").trim();
				if (!d.isEmpty() && d.length()>10)
					d = d.substring(0,10);
				edt_license.setText(json.getString("car_id"));
				edt_vin.setText(json.getString("vin"));
				edt_engine_no.setText(json.getString("engine_no"));
				edt_init_date.setText(d);
				edt_owner.setText(json.getString("owner_name"));
				edt_name.setText(json.getString("name"));
				edt_identity.setText(json.getString("license_id"));
				edt_cellphone.setText(json.getString("phone"));
				
				edt_owner_license.setText(json.getString("owner_license"));
				edt_owner_phone.setText(json.getString("owner_phone"));
				edt_hoder.setText(json.getString("policy_hoder"));
				edt_hoder_license.setText(json.getString("policy_hoder_license"));
				
				chb_hoder.setChecked(true);
				chb_buyer.setChecked(true);
				chb_hoder.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						hoder_checked();
						
					}});
				chb_buyer.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						buyer_checked();
						
					}});
				edt_owner.addTextChangedListener(new TextWatcher(){

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
			
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
					
						
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (chb_hoder.isChecked()){
							edt_hoder.setText(edt_owner.getText().toString());
						}
						if (chb_buyer.isChecked()){
							edt_name.setText(edt_owner.getText().toString());
						}
						
					}});
				edt_owner_license.addTextChangedListener(new TextWatcher(){

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (chb_hoder.isChecked()){
							edt_hoder_license.setText(edt_owner_license.getText().toString());
						}
						if (chb_buyer.isChecked()){
							edt_identity.setText(edt_owner_license.getText().toString());
						}
						
					}});
				edt_owner_phone.addTextChangedListener(new TextWatcher(){

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						
					}

					@Override
					public void afterTextChanged(Editable s) {
						
						if (chb_buyer.isChecked()){
							edt_cellphone.setText(edt_owner_phone.getText().toString());
						}
						
					}});
				
			}catch(Exception e){
				log(e);
			}
		}
	}
	private void hoder_checked(){
		if (chb_hoder.isChecked()){
			edt_hoder_license.setText(edt_owner_license.getText().toString());
			edt_hoder.setText(edt_owner.getText().toString());
		}
	}
	private void buyer_checked(){
		if (chb_buyer.isChecked()){
			edt_name.setText(edt_owner.getText().toString());
			edt_identity.setText(edt_owner_license.getText().toString());
			edt_cellphone.setText(edt_owner_phone.getText().toString());
		}
	}
	

}
