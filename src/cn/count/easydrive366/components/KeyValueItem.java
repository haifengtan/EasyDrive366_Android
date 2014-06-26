package cn.count.easydrive366.components;

import cn.count.easydrive366.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KeyValueItem extends LinearLayout {
	private LayoutInflater _inflater =null;
	private Context _context;
	private View _parent;
	public KeyValueItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		_inflater = LayoutInflater.from(_context);
		
		_parent =_inflater.inflate(R.layout.listitem_key_value, this);
		
	}
	public void setData(final String key,final String value){
		((TextView)findViewById(R.id.txt_key)).setText(key);
		((TextView)findViewById(R.id.txt_value)).setText(value);
		
		
	}
	public View getContainter(){
		return _parent;
	}
}
