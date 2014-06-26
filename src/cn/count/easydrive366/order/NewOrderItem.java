package cn.count.easydrive366.order;

import org.json.JSONObject;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;

public class NewOrderItem extends LinearLayout {
	private LayoutInflater _inflater = null;
	private Context _context;
	private EditText edtQuantity;
	private TextView txtOrderTotal;
	private String product_id;
	private Double price;
	public int quantity;
	private int min_quantity=1;
	private int max_quantity=Integer.MAX_VALUE;
	public IOrderQuantityChanged changed;
	public NewOrderItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		_inflater = LayoutInflater.from(context);
		_context = context;
		_inflater.inflate(R.layout.neworder_item, this);
		edtQuantity = (EditText)findViewById(R.id.edt_quantity);
		txtOrderTotal = (TextView)findViewById(R.id.txt_order_total);
		((Button)findViewById(R.id.btn_up)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				addQuantity();
				
			}});
		((Button)findViewById(R.id.btn_down)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				reduceQuantity();
				
			}});		
		
	}
	private void addQuantity(){
		if (quantity<max_quantity){
			quantity++;
			txtOrderTotal.setText(String.format("%.02f元", quantity*price));
			updateQuantity();
		}
		
	}
	private void reduceQuantity(){
		if (quantity>min_quantity){
			
			quantity--;
			txtOrderTotal.setText(String.format("%.02f元", quantity*price));
			if (quantity<=0)
				quantity =1;
			updateQuantity();
		}
	}
	private void updateQuantity(){
		edtQuantity.setText(String.format("%d",quantity));
		if (this.changed!=null){
			this.changed.changed(product_id, quantity);
		}
	}
	public void setData(final JSONObject json,final int min,final int max){
		min_quantity = min;
		max_quantity = max;
		try{
			ImageView picImage = (ImageView)findViewById(R.id.img_picture);
			String url = json.getString("pic_url");
			AppTools.loadImageFromUrl(picImage, url);
			((TextView)findViewById(R.id.txt_name)).setText(json.getString("name"));
			((TextView)findViewById(R.id.txt_description)).setText(json.getString("description"));
			((TextView)findViewById(R.id.txt_price)).setText(json.getString("price"));
			TextView txt=(TextView)findViewById(R.id.txt_stand_price);
			txt.setText(json.getString("stand_price"));
			txt.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			((TextView)findViewById(R.id.txt_discount)).setText(json.getString("discount"));
			((TextView)findViewById(R.id.txt_buyer)).setText(json.getString("buyer"));
			((TextView)findViewById(R.id.txt_price_order)).setText(json.getString("price"));
			final String q = json.getString("quantity");
			
			edtQuantity.setText(q);
			quantity = Integer.parseInt(q);
			product_id = json.getString("id");
			price = json.getDouble("price_num");
			txtOrderTotal.setText(String.format("%.02f元", quantity*price));
		}catch(Exception e){
			Log.e(AppSettings.AppTile, e.getMessage());
		}
	}

}
