package cn.count.easydrive366.card;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydrive366.components.InformationRow;
import cn.count.easydriver366.base.BaseHttpActivity;

public class CardDetailActivity extends BaseHttpActivity {
	private TextView txt_name;
	private TextView txt_identity;
	private TextView txt_cell;
	private TextView txt_address;
	private TextView txt_bf_name;
	private TextView txt_bf_identity;
	private TextView txt_bf_identity_label;
	private TableRow tr_bf;
	private JSONArray _contents;
	private JSONObject data;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_carddetail_activity);
		this.setRightButtonInVisible();
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		txt_name = (TextView)findViewById(R.id.txt_name);
		txt_identity =(TextView)findViewById(R.id.txt_identity);
		txt_cell =(TextView)findViewById(R.id.txt_cell);
		txt_address=(TextView)findViewById(R.id.txt_address);
		tr_bf =(TableRow)findViewById(R.id.row_bf);
		txt_bf_identity = (TextView)findViewById(R.id.txt_bf_identity);
		txt_bf_identity_label=(TextView)findViewById(R.id.txt_bf_identity_label);
		initData();
		
		
	}
	private void initData(){
		Intent intent = this.getIntent();
		String temps = intent.getStringExtra("data");
		try{
			data = new JSONObject(temps);
			
			txt_name.setText(data.getString("insured_name"));
			txt_identity.setText(data.getString("insured_idcard"));
			txt_cell.setText(data.getString("insured_phone"));
			txt_address.setText(data.getString("insured_address"));
			
			((TextView)findViewById(R.id.txt_cardno)).setText(data.getString("po"));
			((TextView)findViewById(R.id.txt_valid)).setText(data.getString("valid"));
			String fieldname = "is_agreed_bf";
			if (data.isNull(fieldname)){
				fieldname = "is_agreed_bf ";
			}
			if (!data.isNull(fieldname)){
				
				boolean is_agreed_bf = data.getBoolean(fieldname);
				if (is_agreed_bf){
					txt_bf_name = (TextView)findViewById(R.id.txt_bf_name);
					txt_bf_name.setText(data.getString("bf_name"));
					txt_bf_identity.setText(data.getString("bf_id"));
				}else{
					txt_bf_identity_label.setText("");
					txt_bf_identity.setText(data.getString("is_agreed_bf_label"));
					tr_bf.setVisibility(View.GONE);
				}
			}
			
			
			_contents = data.getJSONArray("list");
			for(int i=0;i<_contents.length();i++){
				 JSONObject item = _contents.getJSONObject(i);
				 addTablerow(item.getString("InsuName"),item.getString("Amount"),i==_contents.length()-1,i==0);
				 
			 }
		}catch(Exception e){
			log(e);
		}
		
		
	}
	private void addTablerow(final String name,final String count,final boolean isLast,final boolean isFirst){
		TableLayout table =(TableLayout)findViewById(R.id.table_items);
		if (table!=null){
			TableRow row = new TableRow(this);
			InformationRow detail = new InformationRow(this,null);
			detail.setData(name+"：  ",count);
			row.addView(detail);
			table.addView(row);
			if (isFirst){
				detail.setBeginBackend();
			}
			if (isLast){
				detail.setEndBackend();
			}
		}
	}
}
