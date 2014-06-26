package cn.count.easydrive366.insurance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;
import cn.count.easydriver366.base.AppTools.ISetDate;

public class BuyInsuranceStep4 extends BaseInsurance {
	private ListView lv_main;
	private String data;
	private List<InsItem> list;
	private ItemAdapter adapter;
	private int index;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buyinsurance_step4);
		this.setupTitle("在线购买保险", "第四步");
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
		StringBuilder sb = new StringBuilder();
		for(Detail detail : list.get(index).list){
			try {
				sb.append(String.format("%s=%.0f%s%s&", detail.insu_code,detail.amount,URLEncoder.encode("|","utf-8"),detail.is_enabled?"1":"0"));
			} catch (UnsupportedEncodingException e) {
				
				e.printStackTrace();
			}
		}
		
		String url = String.format("ins/carins_total?%suserid=%d",
				sb.toString(),
				AppSettings.userid);
				
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
			Intent intent = new Intent(this,BuyInsuranceStep5.class);
			intent.putExtra("data", result);
			startActivity(intent);
			stack_push();
		}
	}
	private void init_view(){
		ActionBar bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		index = -1;
		data  = this.getIntent().getStringExtra("data");
		//data  = testdata;
		list  = new ArrayList<InsItem>();
		try{
			JSONObject json = new JSONObject(data);
			if (AppTools.isSuccess(json)){
				JSONArray items = json.getJSONArray("result");
				for(int i=0;i<items.length();i++){
					InsItem ins = new InsItem(items.getJSONObject(i));
					list.add(ins);
					if (ins.is_default)
						index = i;
				}
				load_view();
				for(int i=0;i<list.size();i++){
					InsItem ins = list.get(i);
					bar.addTab(bar.newTab()
							.setText(ins.title)
							.setTag(i)
							.setTabListener(new TabListener(){

								@Override
								public void onTabSelected(Tab tab,
										FragmentTransaction ft) {
									int pos = (Integer)tab.getTag();
									if (index!=pos){
										index  = pos;
										adapter.notifyDataSetChanged();
									}
									
								}

								@Override
								public void onTabUnselected(Tab tab,
										FragmentTransaction ft) {
								
									
								}

								@Override
								public void onTabReselected(Tab tab,
										FragmentTransaction ft) {
								
									
								}}));
				}
			
			}
			
			
		}catch(Exception e){
			log(e);
		}
		
	}
	
	
	private void load_view(){
		lv_main = (ListView)findViewById(R.id.lv_main);
		adapter = new ItemAdapter(this);
		lv_main.setAdapter(adapter);
		lv_main.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.e("ItemClick",view.getClass().getName());
				
			}});
	}
	
	public class ViewHolder{
		public TextView txtTitle;
		public TextView txtValue;
		public Button btnMore;
		public CheckBox chbItem;
		public Detail detail;
		public LinearLayout container;
	}
	private void selectSubIndex(Detail d,int pos){
		d.subindex = pos;
		d.original_amount = d.amount;
		d.amount = d.items.get(pos).value;
		d.is_enabled =true;
		adapter.notifyDataSetChanged();
	}
	private int _dialog_selected = -1;
	private void chooseItems(final Detail d){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		int index = d.subindex;
		builder.setSingleChoiceItems(d.stringItems(), index, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				_dialog_selected = which;
				
			}
		});
		builder.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectSubIndex(d,_dialog_selected);
				/*
				d.subindex = which;
				d.original_amount = d.amount;
				d.amount = d.items.get(which).value;
				*/
				
			}
		});
		builder.setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
		builder.show();
	}
	public class ItemAdapter extends BaseAdapter{

		private LayoutInflater mInflater = null;
		public ItemAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			if (index>-1)
				return list.get(index).list.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			
			return list.get(index).list.get(position);
		}

		@Override
		public long getItemId(int position) {
		
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_buy_insurance_item, null);
				holder.txtTitle =(TextView) convertView.findViewById(R.id.txt_name);
				holder.txtValue = (TextView)convertView.findViewById(R.id.txt_value);
				holder.chbItem = (CheckBox)convertView.findViewById(R.id.chbItem);
				holder.btnMore =(Button)convertView.findViewById(R.id.btn_more);
				holder.container =(LinearLayout)convertView.findViewById(R.id.layout_row);
				convertView.setTag(holder);
				
			}else{
				holder =(ViewHolder) convertView.getTag();
				holder.chbItem.setOnCheckedChangeListener(null);
				
			}
			Detail d = list.get(index).list.get(position);
			holder.txtTitle.setText(d.insu_name);
			DecimalFormat df = new DecimalFormat("¥###,###.##");
			holder.txtValue.setText(df.format( d.amount));
			holder.chbItem.setChecked(d.is_enabled);
			holder.chbItem.setTag(d);
			holder.btnMore.setTag(d);
			final int p = position;
			holder.detail =d;
			if (d.items.size()>0){
				holder.btnMore.setVisibility(View.VISIBLE);
				holder.btnMore.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						
						chooseItems((Detail)v.getTag());
						
					}});
				
			}else
				holder.btnMore.setVisibility(View.GONE);
			holder.chbItem.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					boolean isChecked = ((CheckBox)v).isChecked();
					Detail d = (Detail)v.getTag();
					if (isChecked){
						d.is_enabled = true;
						d.amount = d.original_amount;
						
					}else{
						d.is_enabled = false;
						d.original_amount = d.amount;
						d.amount = 0.0;
					}
					adapter.notifyDataSetChanged();

					
				}
				});
			return convertView;
		}
		
	}
	private class InsItem{
		public String code;
		public String title;
		public boolean is_default;
		public List<Detail> list;
		public InsItem(final JSONObject json){
			list = new ArrayList<Detail>();
			init_data(json);
		}
		private void init_data(final JSONObject json){
			try{
				code = json.getString("code");
				title = json.getString("title");
				is_default = json.getInt("is_default")==1;
				JSONArray items = json.getJSONArray("clause");
				if (items!=null && items.length()>0){
					for(int i=0;i<items.length();i++){
						JSONObject item = items.getJSONObject(i);
						Detail d = new Detail(item);
						list.add(d);
						
					}
				}
			}catch(Exception e){
				log(e);
			}
		}
	}
	private class Detail{
		public String insu_code;
		public String insu_name;
		public Double amount;
		public boolean is_enabled;
		public Double original_amount;
		public int subindex =-1;
		public List<AmountItem> items;
		
		public Detail(final JSONObject json){
			items = new ArrayList<AmountItem>();
			init_value(json);
		}
		private void init_value(final JSONObject json){
			try{
				insu_code = json.getString("insu_code");
				insu_name = json.getString("insu_name");
				amount = json.getDouble("amount");
				original_amount = amount;
				is_enabled = json.getInt("is_enabled")==1;
				subindex = -1;
				JSONArray list = json.getJSONArray("amount_list");
				if (list!=null && list.length()>0){
					for(int i=0;i<list.length();i++){
						JSONObject item = list.getJSONObject(i);
						Double v = item.getDouble("value");
						AmountItem amountItem = new AmountItem(item.getString("label"),v);
						items.add(amountItem);
						if (Math.abs(v-amount)<0.01)
							subindex = i;
					}
				}
				
			}catch(Exception e){
				log(e);
			}
		}
		private String[] stringItems(){
			String[] results = new String[items.size()];
			
			for(int i=0;i<items.size();i++){
				AmountItem item = items.get(i);
				results[i]= item.label;
			}
			return results;
		}
		
	}
	private class AmountItem{
		public String label;
		public Double value;
		public AmountItem(final String l,final Double v){
			label = l;
			value = v;
		}
		@Override
		public String toString(){
			return label;
		}
	}
	//private static String testdata="{'status':'success','alertmsg':'','result':[{'code':'000001','title':'\\u81ea\\u5b9a\\u4e49','is_default':'1','clause':[{'insu_code':'F0000038','insu_name':'\\u8f66\\u635f','amount':'0','is_enabled':1,'amount_list':[]},{'insu_code':'F0000003','insu_name':'\\u4e09\\u8005','amount':'500000','is_enabled':1,'amount_list':[{'label':'50\\u4e07','value':'500000'},{'label':'30\\u4e07','value':'300000'},{'label':'10\\u4e07','value':'100000'},{'label':'5\\u4e07','value':'50000'}]},{'insu_code':'F0000005','insu_name':'\\u76d7\\u62a2','amount':'0','is_enabled':1,'amount_list':[]},{'insu_code':'F0000004','insu_name':'\\u9a7e\\u9a76\\u5458','amount':0,'is_enabled':0,'amount_list':[{'label':'2\\u4e07','value':'20000'},{'label':'1\\u4e07','value':'10000'}]},{'insu_code':'F0000009','insu_name':'\\u4e58\\u5ba2','amount':0,'is_enabled':0,'amount_list':[{'label':'5\\u4e07','value':'50000'},{'label':'2\\u4e07','value':'20000'},{'label':'1\\u4e07','value':'10000'}]},{'insu_code':'F0000010','insu_name':'\\u73bb\\u7483\\u8fdb\\u53e3','amount':0,'is_enabled':0,'amount_list':[]},{'insu_code':'F0000031','insu_name':'\\u73bb\\u7483\\u56fd\\u4ea7','amount':'0','is_enabled':1,'amount_list':[]},{'insu_code':'F0000012','insu_name':'\\u5212\\u75d5','amount':0,'is_enabled':0,'amount_list':[{'label':'2\\u5343','value':'2000'},{'label':'1\\u5343','value':'1000'}]},{'insu_code':'F0000011','insu_name':'\\u81ea\\u71c3','amount':0,'is_enabled':0,'amount_list':[]},{'insu_code':'F0000030','insu_name':'\\u6d89\\u6c34','amount':0,'is_enabled':0,'amount_list':[]},{'insu_code':'F0000022','insu_name':'\\u4e0d\\u8ba1\\u514d\\u8d54','amount':0,'is_enabled':0,'amount_list':[]}]},{'code':'000002','title':'\\u4fdd\\u969c\\u5168','is_default':'0','clause':[{'insu_code':'F0000038','insu_name':'\\u8f66\\u635f','amount':'0','is_enabled':'1','amount_list':[]},{'insu_code':'F0000003','insu_name':'\\u4e09\\u8005','amount':'500000','is_enabled':'1','amount_list':[{'label':'50\\u4e07','value':'500000'},{'label':'30\\u4e07','value':'300000'},{'label':'10\\u4e07','value':'100000'},{'label':'5\\u4e07','value':'50000'}]},{'insu_code':'F0000005','insu_name':'\\u76d7\\u62a2','amount':'0','is_enabled':'1','amount_list':[]},{'insu_code':'F0000004','insu_name':'\\u9a7e\\u9a76\\u5458','amount':'20000','is_enabled':'1','amount_list':[{'label':'2\\u4e07','value':'20000'},{'label':'1\\u4e07','value':'10000'}]},{'insu_code':'F0000009','insu_name':'\\u4e58\\u5ba2','amount':'50000','is_enabled':'1','amount_list':[{'label':'5\\u4e07','value':'50000'},{'label':'2\\u4e07','value':'20000'},{'label':'1\\u4e07','value':'10000'}]},{'insu_code':'F0000010','insu_name':'\\u73bb\\u7483\\u8fdb\\u53e3','amount':'0','is_enabled':'0','amount_list':[]},{'insu_code':'F0000031','insu_name':'\\u73bb\\u7483\\u56fd\\u4ea7','amount':'0','is_enabled':'1','amount_list':[]},{'insu_code':'F0000012','insu_name':'\\u5212\\u75d5','amount':'0','is_enabled':'0','amount_list':[{'label':'2\\u5343','value':'2000'},{'label':'1\\u5343','value':'1000'}]},{'insu_code':'F0000011','insu_name':'\\u81ea\\u71c3','amount':'0','is_enabled':'1','amount_list':[]},{'insu_code':'F0000030','insu_name':'\\u6d89\\u6c34','amount':'0','is_enabled':'1','amount_list':[]},{'insu_code':'F0000022','insu_name':'\\u4e0d\\u8ba1\\u514d\\u8d54','amount':'0','is_enabled':'1','amount_list':[]}]},{'code':'000003','title':'\\u82b1\\u8d39\\u5c11','is_default':'0','clause':[{'insu_code':'F0000038','insu_name':'\\u8f66\\u635f','amount':'0','is_enabled':'0','amount_list':[]},{'insu_code':'F0000003','insu_name':'\\u4e09\\u8005','amount':'200000','is_enabled':'1','amount_list':[{'label':'50\\u4e07','value':'500000'},{'label':'30\\u4e07','value':'300000'},{'label':'10\\u4e07','value':'100000'},{'label':'5\\u4e07','value':'50000'}]},{'insu_code':'F0000005','insu_name':'\\u76d7\\u62a2','amount':'0','is_enabled':'0','amount_list':[]},{'insu_code':'F0000004','insu_name':'\\u9a7e\\u9a76\\u5458','amount':'0','is_enabled':'0','amount_list':[{'label':'2\\u4e07','value':'20000'},{'label':'1\\u4e07','value':'10000'}]},{'insu_code':'F0000009','insu_name':'\\u4e58\\u5ba2','amount':'0','is_enabled':'0','amount_list':[{'label':'5\\u4e07','value':'50000'},{'label':'2\\u4e07','value':'20000'},{'label':'1\\u4e07','value':'10000'}]},{'insu_code':'F0000010','insu_name':'\\u73bb\\u7483\\u8fdb\\u53e3','amount':'0','is_enabled':'0','amount_list':[]},{'insu_code':'F0000031','insu_name':'\\u73bb\\u7483\\u56fd\\u4ea7','amount':'0','is_enabled':'0','amount_list':[]},{'insu_code':'F0000012','insu_name':'\\u5212\\u75d5','amount':'0','is_enabled':'0','amount_list':[{'label':'2\\u5343','value':'2000'},{'label':'1\\u5343','value':'1000'}]},{'insu_code':'F0000011','insu_name':'\\u81ea\\u71c3','amount':'0','is_enabled':'0','amount_list':[]},{'insu_code':'F0000030','insu_name':'\\u6d89\\u6c34','amount':'0','is_enabled':'0','amount_list':[]},{'insu_code':'F0000022','insu_name':'\\u4e0d\\u8ba1\\u514d\\u8d54','amount':'0','is_enabled':'0','amount_list':[]}]}]}";
	

}
