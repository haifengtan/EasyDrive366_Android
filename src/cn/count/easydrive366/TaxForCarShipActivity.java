package cn.count.easydrive366;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;



import android.os.Bundle;

import android.widget.SimpleExpandableListAdapter;

import cn.count.easydriver366.base.BaseExpandableListActivity;
import cn.count.easydriver366.base.AppSettings;


public class TaxForCarShipActivity extends BaseExpandableListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_taxforcarship_activity);
		this.setup_listview();
		reload_data();
	
	}
	@Override
	protected void reload_data(){
		this.get(AppSettings.url_get_taxforcarship(), 1);
	}
	@Override
	protected void init_data(JSONObject _result){
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
				
				marks.add(_result.getJSONObject(key).getString("marks"));
				JSONArray list = _result.getJSONObject(key).getJSONArray("list");
				ArrayList<Map<String,String>> target = new ArrayList<Map<String,String>>();
				
				for(int i=0;i<list.length();i++){
					Map<String,String> mapitem = new HashMap<String,String>();
					JSONObject item = list.getJSONObject(i);
					mapitem.put("price", item.getString("price"));
					mapitem.put("description", item.getString("description"));
					target.add(mapitem);
				}
				items.add(target);
			}
			if (_adapter==null){
				
				_adapter = new SimpleExpandableListAdapter(this,groups,R.layout.group_section,new String[]{"key"},
						new int[]{R.id.txt_group_section},items,
						R.layout.group_item,new String[]{"price","description"},new int[]{R.id.txt_group_price,R.id.txt_group_item});
				
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
