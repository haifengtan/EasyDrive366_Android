package cn.count.easydrive366.carservice;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.HttpExecuteGetTask;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VendorFragment extends Fragment {
	private View view;
	private String _shopName;
	private String _address;
	private String _phone;
	private String _description;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.modules_carservice_vendor_activity, container,false);
		Bundle bundle = this.getArguments();
		String code = bundle.getString("code");
		String url =AppSettings.url_for_get_carservice_vendor(code);
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				process_data(result);
				
			}}.execute(url);
		return view;
	}
	private void process_data(final String result){
		JSONObject json = AppSettings.getSuccessJSON(result,this.getActivity());
		if (json!=null){
			try{
				_shopName = json.getJSONObject("data").getString("shop_name");
				_address = json.getJSONObject("data").getString("address");
				_phone = json.getJSONObject("data").getString("phone");
				_description = json.getJSONObject("data").getString("description");
			
				
				TextView shopname= (TextView)view.findViewById(R.id.txt_rescue_ShopName);
				TextView address = (TextView)view.findViewById(R.id.txt_rescue_Address);
				
				TextView description = (TextView)view.findViewById(R.id.txt_rescue_Description);
				shopname.setText(_shopName);
				address.setText(_address);
				
				description.setText(_description);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
}
