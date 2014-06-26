package cn.count.easydrive366.afterpay;

import cn.count.easydrive366.R;
import cn.count.easydrive366.afterpay.JobSelectActivity.JobKind;
import cn.count.easydrive366.afterpay.JobSelectActivity.Trade;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JobItem extends LinearLayout {
	private LayoutInflater _inflater;
	private Context _context;
	private TextView _txt_trade_name;
	private ImageButton _btn_items;
	private LinearLayout _layout_items;
	private Trade _trade;
	private boolean _isExpand;
	public JobItem(Context context, AttributeSet attrs,Trade tr) {
		super(context, attrs);
		_context = context;
		_inflater = LayoutInflater.from(context);
		_inflater.inflate(R.layout.list_jobitem, this);
		_txt_trade_name = (TextView)findViewById(R.id.txt_trade_name);
		_btn_items = (ImageButton)findViewById(R.id.btn_items);
		_layout_items = (LinearLayout)findViewById(R.id.layout_items);
		_trade = tr;
		_isExpand = false;
		init_view();
		this.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				item_click();
				
			}});
	}
	private void init_view(){
		_txt_trade_name.setText(_trade.trade_name);
	}
	private void item_click(){
		if (_isExpand){
			_layout_items.removeAllViews();
			_isExpand = false;
		}else{
			_isExpand = true;
			for(int i=0;i<_trade.items.size();i++){
				JobKind jk = _trade.items.get(i);
				JobItemCell cell = new JobItemCell(_context,null,jk);
				_layout_items.addView(cell);
			}
		}
	}
	

}
