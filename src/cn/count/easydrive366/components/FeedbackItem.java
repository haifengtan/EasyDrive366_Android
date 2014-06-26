package cn.count.easydrive366.components;

import cn.count.easydrive366.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FeedbackItem extends LinearLayout {
	private LayoutInflater _inflater =null;
	private Context _context;
	public FeedbackItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		_inflater = LayoutInflater.from(context);
		_context = context;
		_inflater.inflate(R.layout.listitem_feedback, this);
		this.setBackgroundResource(R.drawable.signup_hit_input2);
	}
	public void setData(final String username,final String content,final String d){
		((TextView)findViewById(R.id.txt_username)).setText(username);
		((TextView)findViewById(R.id.txt_date)).setText(d);
		((TextView)findViewById(R.id.txt_content)).setText(content);
		
		
	}
	public void setBeginBackend(){
		this.setBackgroundResource(R.drawable.signup_hit_input1);
	}
	public void setEndBackend(){
		this.setBackgroundResource(R.drawable.signup_hit_input6);
	}
}
