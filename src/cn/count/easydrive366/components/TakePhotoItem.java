package cn.count.easydrive366.components;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppTools;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TakePhotoItem extends RelativeLayout {
	private LayoutInflater _inflater =null;
	private Context _context;
	
	public Object target;
	public ICallback callback;
	private ImageView _image;
	public TakePhotoItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		_inflater = LayoutInflater.from(_context);
		
		View view =_inflater.inflate(R.layout.listitem_take_photo, this);
		
		_image =(ImageView)findViewById(R.id.img_avatar);
		_image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if (callback!=null && target!=null){
					callback.onImageClick(target);
				}
				
			}});
		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (callback!=null && target!=null){
					callback.onItemClick(target,_image);
				}
				
			}});
	}
	public void setData(final String title,final String url,int backgroundId){
		((TextView)findViewById(R.id.txt_title)).setText(title);
		
		AppTools.loadImageFromUrl(_image, url, R.drawable.m);
		this.setBackgroundResource(backgroundId);
		
	}
	
	public interface ICallback{
		void onItemClick(Object obj,ImageView img);
		void onImageClick(Object obj);
	}
}
