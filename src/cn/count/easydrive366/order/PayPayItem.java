package cn.count.easydrive366.order;

import cn.count.easydrive366.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PayPayItem extends LinearLayout {
	private LayoutInflater _inflater;
	private Context _context;
	public PayPayItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		_inflater = LayoutInflater.from(context);
		_context = context;
		_inflater.inflate(R.layout.needpay_pay_item, this);
		
	}
	public void setData(final String title,final String detail,final int index){
		((TextView)findViewById(R.id.txt_title)).setText(title);
		((TextView)findViewById(R.id.txt_detail)).setText(detail);
		
	}

}
