package cn.count.easydrive366.order;

import cn.count.easydrive366.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PayGoodsItem extends LinearLayout {
	private LayoutInflater _inflater;
	private Context _context;
	public PayGoodsItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		_inflater = LayoutInflater.from(context);
		_context = context;
		_inflater.inflate(R.layout.needpay_goods_item, this);
	}
	public void setData(final String name,final String price,final String quantity){
		((TextView)findViewById(R.id.txt_name)).setText(name);
		((TextView)findViewById(R.id.txt_price)).setText(price);
		((TextView)findViewById(R.id.txt_quantity)).setText(quantity);
	}

}
