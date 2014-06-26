package cn.count.easydrive366;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.EditText;

import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;

public class DriverLicenseEditActivity extends BaseHttpActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_edit_driverlicense_activity);
		this.setupLeftButton();
		Intent intent =this.getIntent();
		String data =intent.getStringExtra("data");
		try {
			JSONObject json = new JSONObject(data);
			initView(json);
		} catch (JSONException e) {
			log(e);
		}
		
		
	}
	private void initView(final JSONObject json){
		try {
			((EditText)findViewById(R.id.edt_driverlicense_name)).setText(json.getString("name"));
			((TextView)findViewById(R.id.edt_driverlicense_init_date  )).setText(json.getString("init_date"));
			((EditText)findViewById(R.id.edt_driverlicense_number  )).setText(json.getString("number"));
			((TextView)findViewById(R.id.edt_driverlicense_car_type  )).setText(json.getString("car_type"));
			/*
			findViewById(R.id.btn_driverlicense_save).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					save();
					
				}
				
			});
			*/
			this.setupRightButtonWithText(this.getResources().getString(R.string.save));
			findViewById(R.id.edt_driverlicense_car_type).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDriverType();
					
				}});
			findViewById(R.id.edt_driverlicense_init_date).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDate();
					
				}});
			
			findViewById(R.id.txt_driverlicense_choose_driver_type).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDriverType();
					
				}});
			findViewById(R.id.txt_driverlicense_choose_date).setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					chooseDate();
					
				}});
		} catch (Exception e) {
			log(e);
		}
	}
	@Override
	protected void onRightButtonPress(){
		save();
	}
	
	private void save(){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText)findViewById(R.id.edt_driverlicense_name)).getWindowToken(), 0);
		if (!this.isOnline()){
			this.showMessage(this.getResources().getString(R.string.no_network), null);
			return;
		}
		String name = ((EditText)findViewById(R.id.edt_driverlicense_name)).getText().toString().trim();
		String id = ((EditText)findViewById(R.id.edt_driverlicense_number)).getText().toString().trim().toUpperCase();
		if (name.equals("")){
			this.showMessage(this.getResources().getString(R.string.name_is_empty), null);
			return;
		}
		if (id.equals("") || id.length()!=18 || !personIdValidation(id)){
			this.showMessage(this.getResources().getString(R.string.id_is_wrong), null);
			return;
		}
		String url =String.format("api/add_driver_license?user_id=%d&name=%s&license_id=%s&type=%s&init_date=%s",
				AppSettings.userid,
				((EditText)findViewById(R.id.edt_driverlicense_name)).getText().toString().toUpperCase(),
				((EditText)findViewById(R.id.edt_driverlicense_number)).getText().toString().toUpperCase(),
				((TextView)findViewById(R.id.edt_driverlicense_car_type)).getText(),
				((TextView)findViewById(R.id.edt_driverlicense_init_date)).getText()
				);
		this.get(url, 2,this.getResources().getString(R.string.app_uploading));
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		
		super.processMessage(msgType, result);
		Bundle bundle =new Bundle();
		bundle.putString("result",result.toString());
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK,intent);
		finish();
	}
	private ArrayList <Integer> MultiChoiceID = new ArrayList <Integer>();
	private void chooseDriverType(){
		final JSONArray list = AppSettings.driver_type_list;
		if (list==null){
			return;
		}
		String types =((TextView)findViewById(R.id.edt_driverlicense_car_type)).getText().toString();
		
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
				
//				String str = "";
//                int size = MultiChoiceID.size();
//                for (int i = 0 ;i < size; i++) {
//                	str+= items[MultiChoiceID.get(i)] + ", ";
//                }
//                showDialog(str);
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
		((TextView)findViewById(R.id.edt_driverlicense_car_type  )).setText(result);
		
	}
	private void chooseDate(){
		String d = ((TextView)findViewById(R.id.edt_driverlicense_init_date  )).getText().toString();
		if (d.equals("")){
			d = "1990-01-01";
		}
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final Calendar c  = Calendar.getInstance();
		
		try{
			c.setTime(sdf.parse(d));
			
			Dialog dialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
								c.set(Calendar.YEAR, year);
								c.set(Calendar.MONTH,monthOfYear);
								c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
								//showDialog(sdf.format(c.getTime()));
								((TextView)findViewById(R.id.edt_driverlicense_init_date  )).setText(sdf.format(c.getTime()));
						}
					},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
			dialog.show();
		}catch(Exception e){
			log(e);
		}
		
	
		
		
	}
}
