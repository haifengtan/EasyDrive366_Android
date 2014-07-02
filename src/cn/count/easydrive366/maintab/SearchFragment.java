package cn.count.easydrive366.maintab;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import cn.count.easydrive366.R;


import cn.count.easydrive366.goods.GoodsListActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseListViewV4Fragment;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SearchFragment extends BaseListViewV4Fragment {
	private ProgressDialog _dialog;
	private EditText txtSearch;
	private boolean _isSearching=false;
	private String _type;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		containerView =inflater.inflate(R.layout.modules_searchshop_activity, container,
				false);
		_isSearching = true;
		_type = "goods";
		
		this.setBarTitle("搜索");
		
		
		txtSearch = (EditText)containerView.findViewById(R.id.txt_search);
		this._isInDeleting = true;
		this.resource_listview_id = R.id.modules_information_listview;
		// this.resource_listitem_id = R.layout.module_listitem;
		this.resource_listitem_id = R.layout.listitem_searchshop;
		
		mListView = (PullToRefreshListView) containerView.findViewById(this.resource_listview_id);
		reload_data();
		return containerView;
	}

	@Override
	protected void reload_data() {
		if (_isSearching){
			this.get(String.format("api/get_service_type?userid=%d&type=%s", AppSettings.userid,_type), 1);
		}else{
			this.get(String.format("api/get_service_type?userid=%d", AppSettings.userid), 1);
		}
	}

	@Override
	protected void initData(Object result, int msgType) {
		try {
		
			
			JSONArray obj = ((JSONObject) result).getJSONArray("result");
			for (int i = 0; i < obj.length(); i++) {
				JSONObject item = obj.getJSONObject(i);
				if (item.getString("code").equals("00")){
					item.put("selected", true);
				}else{
					item.put("selected", false);
				}
			}
			this.initList(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onListItemClick(final View view, final long index) {

//		if (_list != null) {
//
//			Map<String, Object> map = _list.get((int) index);
//			ViewHolder holder = (ViewHolder) view.getTag();
//			holder.selected.toggle();
//			map.put("selected", holder.selected.isChecked());
//		}
		String key = txtSearch.getText().toString();
		String types =  _list.get((int) index).get("code").toString();
		Intent intent = new Intent(this.getActivity(), GoodsListActivity.class);
		intent.putExtra("key", key);
		intent.putExtra("types",types);
		startActivity(intent);
	}

	@Override
	protected void setupListItem(ViewHolder holder, Map<String, Object> info) {
		holder.title.setText(info.get("name").toString());
		
//		holder.selected.setChecked(info.get("selected").toString().equals("true"));
	}

	@Override
	protected void initListItem(ViewHolder holder, View convertView) {
		holder.title = (TextView) convertView
				.findViewById(R.id.txt_listitem_detail_title);
		
//		holder.selected = (CheckBox) convertView.findViewById(R.id.chk_item);
		convertView.setTag(holder);
	}

	@Override
	public void onRightButtonPress() {
//		InputMethodManager imm = (InputMethodManager)this.getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
//		
//		StringBuilder sb = new StringBuilder();
//		for(int i=0;i<_list.size();i++){
//			Map<String, Object> map = _list.get(i);
//			Object obj = map.get("selected");
//			if (obj.equals(true)){
//				sb.append(map.get("code"));
//				sb.append(",");
//			}
//			
//		}
//		String key = txtSearch.getText().toString();
//		String types = sb.toString();
//		Intent intent = new Intent(this.getActivity(), GoodsListActivity.class);
//		intent.putExtra("key", key);
//		intent.putExtra("types",types);
//		startActivity(intent);
		
		
		String key = txtSearch.getText().toString();
		String types = "";
		Intent intent = new Intent(this.getActivity(), GoodsListActivity.class);
		intent.putExtra("key", key);
		intent.putExtra("types",types);
		startActivity(intent);
	}

	@Override
	public void processMessage(int msgType, final Object result) {
		if (msgType == 1) {
			super.processMessage(msgType, result);
		}
	}
}