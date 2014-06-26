package cn.count.easydrive366.provider;

import java.util.Map;

import cn.count.easydrive366.R;
import cn.count.easydrive366.order.NewOrderActivity;
import cn.count.easydriver366.base.AppTools;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProviderItem extends LinearLayout {

	private LayoutInflater _inflater = null;
	private Context _context;
	
	public ProviderItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		_inflater = LayoutInflater.from(_context);
		_inflater.inflate(R.layout.listitem_goods, this);
	}
	public void setData(Map<String,Object> info){
		TextView title = (TextView) findViewById(R.id.txt_title);
		TextView detail2 = (TextView) findViewById(R.id.txt_price);
		TextView detail3 = (TextView) findViewById(R.id.txt_stand_price);
		TextView detail4 = (TextView) findViewById(R.id.txt_discount);
		TextView detail5 = (TextView) findViewById(R.id.txt_buyer);
		TextView detail =(TextView) findViewById(R.id.txt_description);
		ImageView image = (ImageView) findViewById(R.id.img_picture);
		Button button1 = (Button) findViewById(R.id.btn_buy);
		
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id = (String) v.getTag();
				Intent intent = new Intent(
						_context,
						NewOrderActivity.class);
				intent.putExtra("id", id);
				_context.startActivity(intent);
			}
		});
		title.setText(info.get("name").toString());
		detail.setText(info.get("description").toString());
		detail2.setText(info.get("price").toString());
		detail3.setText(info.get("stand_price").toString());
		detail4.setText(info.get("discount").toString());
		detail5.setText(info.get("buyer").toString());
		AppTools.loadImageFromUrl(
				image, info.get("pic_url").toString());
		button1.setTag(info.get("id"));
	}

}
