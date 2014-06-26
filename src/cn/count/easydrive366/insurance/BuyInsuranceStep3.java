package cn.count.easydrive366.insurance;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;
import cn.count.easydriver366.base.AppTools.ISetDate;

public class BuyInsuranceStep3 extends BaseInsurance {
	private EditText edt_brand;
	private TextView txt_model;
	private EditText edt_exhause;
	private TextView txt_biz_date;
	private EditText edt_passengers;
	private EditText edt_price;
	private TextView txt_com_date;
	private String data;
	private String brand;
	private String brand_id;
	private List<BrandType> items;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buyinsurance_step3);
		this.setupTitle("在线购买保险", "第三步");
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
		if (edt_price.getText().toString().trim().isEmpty()){
			this.showMessage("参考价格不能为空！",null);
			return;
		}
		if (txt_biz_date.getText().toString().trim().isEmpty()){
			this.showMessage("商业险起期不能为空！",null);
			return;
		}
		if (txt_com_date.getText().toString().trim().isEmpty()){
			this.showMessage("交强险起期不能为空！",null);
			return;
		}
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edt_brand.getWindowToken(), 0);
		try{
			String url = String.format("ins/carins_clause?userid=%d&biz_valid=%s&com_valid=%s&price=%s&brand=%s&brand_id=%s", 
					AppSettings.userid,
					URLEncoder.encode(txt_biz_date.getText().toString().trim(),"utf-8"),
					URLEncoder.encode(txt_com_date.getText().toString().trim(),"utf-8"),
					edt_price.getText().toString().trim(),
					URLEncoder.encode(edt_brand.getText().toString().trim(),"utf-8"),
					URLEncoder.encode(brand_id,"utf-8"));
			beginHttp();
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					endHttp();
					process_result(result);
					
				}}.execute(url);
		}catch(Exception e){
			this.showDialog("日期格式不对！");
		}
		
			
	}
	private void process_result(final String result){
		
		if (AppSettings.isSuccessJSON(result, this)){
			Intent intent = new Intent(this,BuyInsuranceStep4.class);
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
		data  = this.getIntent().getStringExtra("data");
	}
	
	
	private void load_view(){
		edt_brand = (EditText)findViewById(R.id.edt_brand);
		txt_model = (TextView)findViewById(R.id.txt_model);
		edt_exhause = (EditText)findViewById(R.id.edt_exhause);
		txt_biz_date = (TextView)findViewById(R.id.txt_biz_valid);
		edt_passengers = (EditText)findViewById(R.id.edt_passengers);
		edt_price = (EditText)findViewById(R.id.edt_price);
		txt_com_date = (TextView)findViewById(R.id.txt_com_valid);
		
		findViewById(R.id.layout_biz_valid).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AppTools.chooseDate(txt_biz_date.getText().toString(), BuyInsuranceStep3.this, new ISetDate(){

					@Override
					public void setDate(String date) {
						txt_biz_date.setText(date);
					}});
				
			}});
		findViewById(R.id.layout_com_valid).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AppTools.chooseDate(txt_com_date.getText().toString(), BuyInsuranceStep3.this, new ISetDate(){

					@Override
					public void setDate(String date) {
						txt_com_date.setText(date);
					}});
				
			}});
		findViewById(R.id.layout_brand).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				choose_brand();
			}});
		
	}
	private void load_data(){
		JSONObject json = AppSettings.getSuccessJSON(data,this);
		if (json!=null){
			try{
				edt_brand.setText(json.getString("brand"));
				txt_model.setText(json.getString("model"));
				edt_exhause.setText(json.getString("exhause"));
				txt_biz_date.setText(json.getString("biz_valid"));
				edt_passengers.setText(json.getString("passengers"));
				edt_price.setText(json.getString("price"));
				txt_com_date.setText(json.getString("com_valid"));
				brand = json.getString("brand");
				brand_id = json.getString("brand_id");
				JSONArray temps = json.getJSONArray("list");
				items = new ArrayList<BrandType>();
				for(int i=0;i<temps.length();i++){
					BrandType bt = new BrandType(temps.getJSONObject(i));
					bt.is_selected = bt.brand.equals(brand) && bt.brand_id.equals(brand_id);
					if (bt.is_selected){
						selected_index = i;
					}
					items.add(bt);
				}
			}catch(Exception e){
				log(e);
			}
		}
	}
	private int selected_index=-1;
	private ItemAdapter adapter;
	private void choose_brand(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("选择品牌");
		if (adapter==null){
			adapter = new ItemAdapter(this);
		}
		
		builder.setSingleChoiceItems(adapter, selected_index, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.e("select row", dialog.toString());
				selected_index = which;
				adapter.notifyDataSetChanged();
			}
			});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				update_selected();
				
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selected_index =-1;
				
			}
		});
		builder.show();
	}
	private void update_selected(){
		if (selected_index<0){
			return;
		}
		BrandType bt = items.get(selected_index);
		brand_id = bt.brand_id;
		edt_brand.setText(bt.brand);
		txt_model.setText(bt.model);
		edt_exhause.setText(bt.exhause);
		edt_price.setText(bt.price);
		edt_passengers.setText(bt.passengers);
	}
	private class BrandType{
		public String brand;
		public String brand_id;
		public String exhause;
		public String model;
		public String passengers;
		public String price;
		public boolean is_selected=false;
		public BrandType(JSONObject json){
			init_data(json);
		}
		private void init_data(final JSONObject json){
			try{
				brand = json.getString("brand");
				brand_id = json.getString("brand_id");
				exhause = json.getString("exhause");
				model = json.getString("model");
				passengers = json.getString("passengers");
				price = json.getString("price");
			}catch(Exception e){
				log(e);
			}
		}
	}
	private class ViewHolder{
		public TextView txt_brand;
		public TextView txt_brand_id;
		public TextView txt_model;
		public TextView txt_price;
		public TextView txt_passengers;
		public TextView txt_exhause;
		public CheckBox chb_select;
	}
	private class ItemAdapter extends BaseAdapter{
		private LayoutInflater mInflater = null;
		public ItemAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView ==null)
			{
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_brandtype, null);
				holder.txt_brand = (TextView)convertView.findViewById(R.id.txt_brand);
				holder.txt_brand_id = (TextView)convertView.findViewById(R.id.txt_brand_id);
				holder.txt_model = (TextView)convertView.findViewById(R.id.txt_model);
				holder.txt_price = (TextView)convertView.findViewById(R.id.txt_price);
				holder.txt_passengers = (TextView)convertView.findViewById(R.id.txt_passenages);
				holder.txt_exhause = (TextView)convertView.findViewById(R.id.txt_exhause);
				holder.chb_select =(CheckBox)convertView.findViewById(R.id.chb_select);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			BrandType bt = items.get(position);
			holder.txt_brand.setText(bt.brand);
			holder.txt_brand_id.setText(bt.brand_id);
			holder.txt_model.setText(bt.model);
			holder.txt_price.setText(bt.price);
			holder.txt_passengers.setText(bt.passengers);
			holder.txt_exhause.setText(bt.exhause);
			holder.chb_select.setChecked(position==selected_index);
			return convertView;
		}
		
	}
	

}
