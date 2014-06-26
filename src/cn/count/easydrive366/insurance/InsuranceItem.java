package cn.count.easydrive366.insurance;

import org.json.JSONObject;

import cn.count.easydrive366.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InsuranceItem extends LinearLayout {
	private JSONObject _item;
	private Context _context;
	public InsuranceItem(Context context, AttributeSet attrs,JSONObject obj) {
		super(context, attrs);
		_item = obj;
		_context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.listitem_insurance, this);
		set_data();
		
	}
	private void set_data(){
		try{
			((TextView)findViewById(R.id.txt_car_id)).setText(_item.getString("car_id"));
			((TextView)findViewById(R.id.txt_price_time)).setText(_item.getString("price_time"));
			((TextView)findViewById(R.id.txt_status_name)).setText(_item.getString("status_name"));
			CheckBox chb = (CheckBox)findViewById(R.id.chb_insurance_item);
			if (_item.getString("id").isEmpty()){
				chb.setVisibility(View.GONE);
			}else{
				chb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						item_changed(isChecked);
						
					}});
			}
			
			
		}catch(Exception e){
			Log.e("Insurace Item", e.toString());
		}
	}
	private void item_changed(boolean isChecked){
		try{
			_item.put("selected", isChecked?1:0);
		}catch(Exception e){
			//nothing;
		}
	}

}
