package cn.count.easydriver366.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.count.easydrive366.R;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.app.ExpandableListActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class BaseExpandableListActivity extends ExpandableListActivity implements HttpClient.IHttpCallback {
	protected JSONObject _result;
	protected SimpleExpandableListAdapter _adapter;
	protected PullToRefreshExpandableListView mListView;
	protected HttpClient httpClient;
	protected Button _rightButton;
	protected String _company;
	protected String _phone;
	private int reload = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		
	
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId()==android.R.id.home){
			this.onLeftButtonPress();
		}
		return super.onOptionsItemSelected(item);
	}
	protected void setBarTitle(final String title) {
		TextView txt = (TextView) findViewById(R.id.txt_navigation_bar_title);
		if (txt != null) {
			txt.setText(title);
		}
	}
	protected void setup_listview(){
		this.setRightButtonInVisible();
		this.setupLeftButton();
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		if (!title.equals("")){
			setBarTitle(title);
		}
		mListView = (PullToRefreshExpandableListView)findViewById(R.id.expandableListView1);
		mListView.setOnRefreshListener(new OnRefreshListener<ExpandableListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
				reload = 1;
				reload_data();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				onListViewItemClick(parent,view,position,id);
			}});
	}
	protected void onListViewItemClick(AdapterView<?> parent, View view,
					int position, long id){
		
	}
	protected void reload_data(){
		this.get(AppSettings.url_get_taxforcarship(), 1);
	}
	protected HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new HttpClient(this);
		}
		return httpClient;
	}
	public void get(String actionAndParameters, final int returnType) {
		
		this.get(actionAndParameters, returnType,this.getResources().getString(R.string.app_loading));
	}
	
	public void get(String actionAndParameters, final int returnType,final String hint) {
		final String url = String.format("%s&reload=%d", actionAndParameters,reload);
		Log.e("Reload", url);
		this.getHttpClient().requestServer(url, returnType);
		reload =0;
		
	}
	protected void setupCompanyAndPhone(final Object json) {
		final JSONObject result = (JSONObject) json;
		try {
			if (!result.getJSONObject("result").isNull("company")){
				_company = result.getJSONObject("result").getString("company");
				_phone = result.getJSONObject("result").getString("phone");
				this.setupPhoneButton();
			}
			
		} catch (Exception e) {
			log(e);
		}
	}
	protected void setupPhoneButton() {
		final View v = findViewById(R.id.btn_phone);
		if (v != null) {
			this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					((Button)v).setText(String.format("拨号：%s", _phone));
					v.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (_phone!=null && !_phone.equals("")){
								Uri uri =Uri.parse(String.format("tel:%s",_phone)); 
								
								Intent it = new Intent(Intent.ACTION_VIEW,uri); 
								startActivity(it); 
							}

						}
					});
					
					
				}
				
			});
			
		}
	}
	protected void setRightButtonInVisible() {
		_rightButton = (Button) findViewById(R.id.title_set_bn);
		if (_rightButton != null) {
			_rightButton.setVisibility(View.GONE);
		}
	}
	protected void onLeftButtonPress() {
		finish();
	}

	protected void setupLeftButton() {
		View v = findViewById(R.id.img_navigationbar_logo);
		if (v != null) {
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onLeftButtonPress();

				}
			});
		}else{
			this.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	@Override
	public void processMessage(int msgType, final Object result) {
		try{
			_result= ((JSONObject)result).getJSONObject("result");
			_result = _result.getJSONObject("data");
			Log.d("Http", _result.toString());
			
			this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					//_adapter.setData(_result);
					init_data(_result);
				}
				
			});
			

		}catch(Exception e){
			log(e);
		}
		
	}
	protected void log(Exception e){
		
	}
	protected List<Map<String, String>> groups = new ArrayList<Map<String,String>>();
	protected List<String> marks = new ArrayList<String>();
	protected List<ArrayList<Map<String,String>>> items = new ArrayList<ArrayList<Map<String,String>>>();
	
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
	
	

	@Override
	public void recordResult(int msgType, Object result) {
		setupCompanyAndPhone(result);
		try{
			this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					if (mListView!=null){
						mListView.onRefreshComplete();
					}
					
				}});
			
		}catch(Exception e){
			log(e);
		}
	}
	@Override
	public void showFailureMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		
	}
}
