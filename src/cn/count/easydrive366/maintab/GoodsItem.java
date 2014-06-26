package cn.count.easydrive366.maintab;

import org.json.JSONObject;

import cn.count.easydrive366.R;

import cn.count.easydrive366.goods.GoodsDetailActivity;
import cn.count.easydrive366.insurance.BuyInsuranceStep1;
import cn.count.easydrive366.order.NewOrderActivity;
import cn.count.easydriver366.base.AppTools;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GoodsItem extends LinearLayout {
	private Context _context;
	private String id;
	private String is_carins;
	private String web_url;

	public GoodsItem(Context context, AttributeSet attrs, final JSONObject item) {
		super(context, attrs);
		_context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.listitem_goods, this);
		setup_view(item);
	}

	private void setup_view(final JSONObject item) {
		((TextView) findViewById(R.id.txt_title)).setText(item
				.optString("name"));
		((TextView) findViewById(R.id.txt_price)).setText(item
				.optString("price"));
		((TextView) findViewById(R.id.txt_stand_price)).setText(item
				.optString("stand_price"));
		((TextView) findViewById(R.id.txt_discount)).setText(item
				.optString("discount"));
		((TextView) findViewById(R.id.txt_buyer)).setText(item
				.optString("buyer"));
		((TextView) findViewById(R.id.txt_description)).setText(item
				.optString("description"));
		id = item.optString("id");
		is_carins = item.optString("is_carins");
		web_url = item.optString("web_url");
		ImageView image = (ImageView) findViewById(R.id.img_picture);
		AppTools.loadImageFromUrl(
				image, item.optString("pic_url"));
		Button btnBuy = (Button) findViewById(R.id.btn_buy);

		btnBuy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (is_carins.equalsIgnoreCase("0")) {
					Intent intent = new Intent(_context, NewOrderActivity.class);
					intent.putExtra("id", id);
					_context.startActivity(intent);
				} else {

					Intent intent = new Intent(_context,
							BuyInsuranceStep1.class);
					intent.putExtra("web_url", web_url);
					intent.putExtra("goods_id", id);
					_context.startActivity(intent);
				}

			}
		});
		this.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int _id = Integer.parseInt(id);
				Intent intent = new Intent(_context,
						GoodsDetailActivity.class);
				intent.putExtra("id", _id);
				_context.startActivity(intent);
				
			}});
	}

}
