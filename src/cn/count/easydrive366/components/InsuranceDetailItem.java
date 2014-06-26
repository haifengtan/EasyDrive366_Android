package cn.count.easydrive366.components;

import cn.count.easydrive366.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InsuranceDetailItem extends LinearLayout {
	private LayoutInflater _inflater =null;
	private Context _context;
	public InsuranceDetailItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		_inflater = LayoutInflater.from(context);
		_context = context;
		_inflater.inflate(R.layout.insurance_items, this);
		this.setBackgroundResource(R.drawable.signup_hit_input2);
	}
	public void setData(final String f1,final String f2,final String f3,final String f4){
		((TextView)findViewById(R.id.txt_insurance_items_field1)).setText(f1);
		((TextView)findViewById(R.id.txt_insurance_items_field2)).setText(f2);
		((TextView)findViewById(R.id.txt_insurance_items_field3)).setText(f3);
		((TextView)findViewById(R.id.txt_insurance_items_field4)).setText(f4);
		
	}
	public void setEndBackend(){
		this.setBackgroundResource(R.drawable.signup_hit_input6);
	}
}
