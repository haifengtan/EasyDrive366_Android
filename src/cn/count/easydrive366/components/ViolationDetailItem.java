package cn.count.easydrive366.components;



import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.count.easydrive366.R;
public class ViolationDetailItem extends LinearLayout {
	private LayoutInflater _inflater =null;
	private Context _context;
	public ViolationDetailItem(Context context,AttributeSet attrs){
		super(context,attrs);
		_inflater = LayoutInflater.from(context);
		_context = context;
//		_inflater.inflate(R.layout.partview_violationdetail_items, this);
		_inflater.inflate(R.layout.partview_violationdetail_items2, this);
	}
	public void setData(final String address,final String reason,final String fine,final String mark,final String occurTime){
		((TextView)findViewById(R.id.txt_violationdetailitem_title)).setText(address);
		((TextView)findViewById(R.id.txt_violationdetailitem_detail)).setText(reason);
		((TextView)findViewById(R.id.txt_violationdetailitem_r1)).setText(fine);
		((TextView)findViewById(R.id.txt_violationdetailitem_r2)).setText(mark);
		((TextView)findViewById(R.id.txt_violationdetailitem_r3)).setText(occurTime);
	}
}
