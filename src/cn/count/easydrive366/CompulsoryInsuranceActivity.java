package cn.count.easydrive366;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import cn.count.easydriver366.base.BaseExpandableListActivity;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.AppSettings;


public class CompulsoryInsuranceActivity extends BaseExpandableListActivity {
	private JSONObject _result;
	private JSONObject _type0;
	private JSONObject _type1;
	private JSONObject _type2; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_compulsoryinsurance_activity);
		this.setup_listview();
		
		
		Button type0 =(Button)findViewById(R.id.btn_compulsory_insurance_0); 
		type0.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setData(_type0);
				
			}
			
		});
		Button type1 =(Button)findViewById(R.id.btn_compulsory_insurance_1); 
		type1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setData(_type1);
				
				
			}
			
		});
		Button type2 =(Button)findViewById(R.id.btn_compulsory_insurance_2); 
		type2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setData(_type2);
				
				
			}
			
		});
		reload_data();
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_get_compulsory_details(), 1);
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		try{
			if (msgType==1){
				_result=( ((JSONObject)result).getJSONObject("result")).getJSONObject("data");
				
				_type0 = _result.getJSONObject("type_0");
				_type1 = _result.getJSONObject("type_1");
				_type2 = _result.getJSONObject("type_2");
				this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						setData(_type0);
						
					}});
				
			}
		}catch(Exception e){
			this.log(e);
		}

	}
	protected void setData(JSONObject obj){
		_result = obj;
		groups.clear();
		items.clear();
		marks.clear();
		try{
			List<String> keys = new ArrayList<String>();
			for(Iterator<String> it =_result.keys();it.hasNext();){
				keys.add(it.next());
			}
			Collections.sort(keys);
			for(int j=0;j<keys.size();j++){
				String key= keys.get(j);
				Map<String,String> map = new HashMap<String,String>();
				map.put("key", key);
				groups.add(map);
				
				JSONArray list = _result.getJSONArray(key);
				ArrayList<Map<String,String>> target = new ArrayList<Map<String,String>>();
				
				for(int i=0;i<list.length();i++){
					Map<String,String> mapitem = new HashMap<String,String>();
					JSONObject item = list.getJSONObject(i);
					mapitem.put("price", item.getString("price"));
					mapitem.put("name", item.getString("name"));
					target.add(mapitem);
				}
				items.add(target);
			}
			if (_adapter==null){
				
				_adapter = new SimpleExpandableListAdapter(this,groups,R.layout.group_section,new String[]{"key"},
						new int[]{R.id.txt_group_section},items,
						R.layout.group_item,new String[]{"price","name"},new int[]{R.id.txt_group_price,R.id.txt_group_item});
				
				//mListView.setAdapter((ListAdapter) _adapter);
				setListAdapter(_adapter);
						
			}else{
				_adapter.notifyDataSetChanged();
				mListView.onRefreshComplete();
			}
		}catch(Exception e){
			log(e);
		}
		
	}
	
	
}
