package cn.count.easydrive366;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cn.count.easydrive366.components.CodeListTable;
import cn.count.easydrive366.components.InformationRow;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;

public class ActivateCodeActivity extends BaseHttpActivity {
	
	private EditText edtCode;
	private String _number;
	private String _code;
	private String _activate_date;
	private String _valid_date;
	private JSONArray _contents;
	private TableLayout _table;
	private List<List<CodeInfo>> items;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_activatecode_activity);
		Intent intent = this.getIntent();
		boolean isGuide = intent.getBooleanExtra("isGuide", false);
		if (isGuide){
			this.setupRightButtonWithText("完成");
		}else{
			this.setRightButtonInVisible();
		}
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		edtCode = (EditText)findViewById(R.id.edt_code);
		_table = (TableLayout)findViewById(R.id.table_cards);
		TextView txt_remark =(TextView)findViewById(R.id.txt_remark);
		
		String remark = this.getIntent().getStringExtra("remark");
		if (remark!=null && !remark.isEmpty()){
			txt_remark.setText(remark);
		}
		findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activate_code();
				
			}
		});
		this.get(AppSettings.get_activate_code_list(), 2);
	}
	@Override
	protected void onRightButtonPress(){
		this.finish();
	}
	private void activate_code(){
		
		if (edtCode.getText().toString().trim().length()==0){
			this.showMessage("请输入激活码！", null);
			return;
		}
		String url =String.format("api/add_activate_code_list?userid=%d&code=%s", AppSettings.userid,
				edtCode.getText().toString());
		this.get(url, 1);
	}
	private void show_activate_code(){
		Intent intent = new Intent(this,ActivateCodeShowActivity.class);
		intent.putExtra("number", _number);
		intent.putExtra("code", _code);
		intent.putExtra("activate_date",_activate_date);
		intent.putExtra("valid_date",_valid_date);
		if (_contents !=null)
			intent.putExtra("contents", _contents.toString());
		startActivity(intent);
	}
	public class CodeInfo
	{
		public String title;
		public String detail;
		public CodeInfo(final String t,final String d){
			title =t;
			detail = d;
		}
	}
	private void addTables1(){
		_table.removeAllViewsInLayout();
		for(int i=0;i<items.size();i++){
			List<CodeInfo> item = items.get(i);
			TableRow row = new TableRow(this);
			CodeListTable codeTable = new CodeListTable(this,null);
			codeTable.setData(item);
			
			row.addView(codeTable);
			_table.addView(row);	
		}
		
	
	}
	private void addTables(){
		_table.removeAllViewsInLayout();
		for(int i=0;i<items.size();i++){
			List<CodeInfo> list = items.get(i);
			for(int index=0;index<list.size();index++){
				CodeInfo codeinfo = list.get(index);
				TableRow row = new TableRow(this);
				InformationRow detail = new InformationRow(this,null);
				detail.setData(codeinfo.title,codeinfo.detail);
				row.addView(detail);
				_table.addView(row);
				if (index==0){
					detail.setBeginBackend();
				}else if (index==list.size()-1){
					detail.setEndBackend();
				}
			}
			
			View v = new View(this);
			v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 20));
			
			_table.addView(v);
		}
	}
	private void process_list(final JSONArray list){
		try{
			items  = new ArrayList<List<CodeInfo>>();
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				JSONArray contents = item.getJSONArray("contents");
				List<CodeInfo> _tempList = new ArrayList<CodeInfo>();
				_tempList.add(new CodeInfo("卡号",item.getString("number")));
				_tempList.add(new CodeInfo("激活码",item.getString("code")));
				_tempList.add(new CodeInfo("激活日期",item.getString("activate_date")));
				_tempList.add(new CodeInfo("有效期至",item.getString("valid_date")));
				_tempList.add(new CodeInfo("服务项目：",""));
				for(int index=0;index<contents.length();index++){
					JSONObject item2 = contents.getJSONObject(index);
					_tempList.add(new CodeInfo(item2.getString("name"),item2.getString("count")+"次"));
				}
				items.add(_tempList);
			}
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					addTables();
					
				}});
		}catch(Exception e){
			log(e);
		}
	}
	@Override
	public void processMessage(int msgType, final Object result) {

		if (AppTools.isSuccess(result)){
			try{
				JSONObject json = (JSONObject)result;
				/*
				_number = json.getJSONObject("result").getString("number");
				_code = json.getJSONObject("result").getString("code");
				_activate_date = json.getJSONObject("result").getString("activate_date");
				_valid_date = json.getJSONObject("result").getString("valid_date");
				_contents = json.getJSONObject("result").getJSONArray("contents");
				*/
				JSONArray list = json.getJSONArray("result");
				process_list(list);
			}catch(Exception e){
				log(e);
			}
			
			if (msgType==1){
				this.showMessage("您的账户已经激活。",null);
			}
			
		}else{
			try{
				String message = ((JSONObject)result).getString("message");
				this.showMessage(message,null);
			}catch(Exception e){
				log(e);
			}
		}
		
	}
}
