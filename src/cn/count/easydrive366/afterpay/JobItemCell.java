package cn.count.easydrive366.afterpay;

import cn.count.easydrive366.R;
import cn.count.easydrive366.afterpay.JobSelectActivity.JobKind;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JobItemCell extends LinearLayout {
	private JobKind jobKind;
	private CheckBox chb;
	public JobItemCell(Context context, AttributeSet attrs,JobKind jk) {
		super(context, attrs);
		jobKind = jk;
		LayoutInflater.from(context).inflate(R.layout.list_jobitemcell, this);
		((TextView)findViewById(R.id.txt_title)).setText(jk.name);
		((TextView)findViewById(R.id.txt_level)).setText(jk.level);
		((TextView)findViewById(R.id.txt_content)).setText(jk.content);
		chb = (CheckBox)findViewById(R.id.chb_select);
		chb.setChecked(jk.isSelect);
		jk.chb = chb;
		chb.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				jobKind.select_changed(chb.isChecked(),chb);
				
			}});
		/*
		chb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				jobKind.select_changed(isChecked,chb);
				
			}});
		*/
	}

}
