package cn.count.easydrive366.components;

import cn.count.easydrive366.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CarShopItem extends LinearLayout {

	public CarShopItem(Context context, AttributeSet attrs,final String title ,final String detail) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.listitem_shopitem, this);
		((TextView)findViewById(R.id.txt_title)).setText(title);
		((TextView)findViewById(R.id.txt_detail)).setText(detail);
	}

}
