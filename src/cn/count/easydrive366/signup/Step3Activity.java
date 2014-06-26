package cn.count.easydrive366.signup;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.count.easydrive366.ActivateCodeActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.SignupActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;

public class Step3Activity extends BaseHttpActivity {
	private Button btnNext;
	
	private EditText edtName;
	private EditText edtCar_type;
	private EditText edtInit_date;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		this._isHideTitleBar = true;
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.modules_step3_activity);
		this.setupLeftButton();
		this.setRightButtonInVisible();
		this.setupPhoneButtonInVisible();
		btnNext =(Button)findViewById(R.id.btn_ok);
		edtName = (EditText)findViewById(R.id.edt_name);
		edtCar_type = (EditText)findViewById(R.id.edt_car_type);
		edtInit_date = (EditText)findViewById(R.id.edt_init_date);
		
		
		btnNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				nextStep();
				
			}});
		findViewById(R.id.row_init_date).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				chooseDate(edtInit_date);
				
			}});
		findViewById(R.id.row_car_type).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				chooseDriverType();
				
			}});
		
		Intent intent = this.getIntent();
		edtName.setText(intent.getStringExtra("name"));
		edtCar_type.setText(intent.getStringExtra("car_type"));
		edtInit_date.setText(intent.getStringExtra("init_date"));
		this.get(AppSettings.url_get_license_type(), 2,"");
	}
	
	@Override
	public void processMessage(int msgType, final Object result) {
		super.processMessage(msgType, result);
		if (msgType==2){
			if (this.isSuccess(result)){
				try {
					AppSettings.driver_type_list = ((JSONObject)result).getJSONArray("result");
				} catch (JSONException e) {
					log(e);
				}
			}
			return;
		}
		if (this.isSuccess(result)){
			try{
				/*
				Intent intent =new Intent(this,SignupActivity.class);
				JSONObject json = (JSONObject)result;
				intent.putExtra("username", json.getJSONObject("result").getString("username"));
				
				startActivity(intent);
				*/
				Intent intent = new Intent(this,ActivateCodeActivity.class);
				intent.putExtra("isGuide", true);
				JSONObject json = (JSONObject)result;
				intent.putExtra("remark", json.getJSONObject("result").optString("remark"));
				startActivity(intent);
				
				finish();
			}catch(Exception e){
				log(e);
			}
			
		}else{
			String message;
			try {
				message = ((JSONObject)result).getString("message");
				this.showMessage(message, null);
			} catch (JSONException e) {
			
				log(e);
			}
			
		}
	}
	private void nextStep(){
		String name= edtName.getText().toString();
		String car_type = edtCar_type.getText().toString();
		String init_date = edtInit_date.getText().toString();
		
		/*
		if (name.equals("")){
			this.showMessage("请输入姓名。", null);
			return;
		}
		if (car_type.equals("") ){
			this.showMessage("请输入准驾车型。", null);
			return;
		}
		if (init_date.equals("")){
			this.showMessage("请输入初登日期。", null);
			return;
		}
		*/
		String url =String.format("api/wizardstep3?userid=%d&name=%s&type=%s&init_date=%s",AppSettings.userid,name,car_type,init_date);
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtName.getWindowToken(), 0);
		this.get(url, 1);
		
	}
	private ArrayList <Integer> MultiChoiceID = new ArrayList <Integer>();
	private void chooseDriverType(){
		final JSONArray list = AppSettings.driver_type_list;
		if (list==null){
			return;
		}
		String types =edtCar_type.getText().toString();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		MultiChoiceID.clear();
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(this.getResources().getString(R.string.app_name));
		
		final String[] items = new String[list.length()];		
		boolean[] checkedItems= new boolean[list.length()];
		try{
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				String code = item.getString("code");
				String name = item.getString("name");
				String years = item.getString("years");
				items[i]=String.format("%s--%s(%s)",code,name,years);
				if (types.contains(code)){
					checkedItems[i]=true;
					MultiChoiceID.add(i);
				}else{
					checkedItems[i]=false;
				}
			}
		}catch(Exception e){
			log(e);
		}
		
		builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (isChecked){
					if (!MultiChoiceID.contains(which))
						MultiChoiceID.add(which);
				}else{
					if (MultiChoiceID.contains(which)){
						int i_index = MultiChoiceID.indexOf(which);
						MultiChoiceID.remove(i_index);
					}
				}
				
			}
		});
		builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
                changeDriverType();
			}
		});
		builder.setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
		builder.show();
	}
	private void changeDriverType(){
		final JSONArray list = AppSettings.driver_type_list;
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<MultiChoiceID.size();i++){
			int index = MultiChoiceID.get(i);
			try{
				sb.append(list.getJSONObject(index).getString("code")).append(",");
			}catch(Exception e){
				log(e);
			}
			
		}
		String result =sb.toString();
		result = result.substring(0, result.length()-1);
		edtCar_type.setText(result);
		
	}
}
