package cn.count.easydrive366.article;

import java.util.Map;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppTools;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class ArticleItem extends LinearLayout {
	private Context _context;
	private IArticleItem _callback;
	public ArticleItem(Context context, AttributeSet attrs,Map<String,Object> info,IArticleItem callback) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		_context = context;
		_callback = callback;
		inflater.inflate(R.layout.listitem_article, this);
		TextView title = (TextView)findViewById(R.id.txt_title);
		TextView detail2 = (TextView)findViewById(R.id.txt_description);
		
		TextView detail3 = (TextView)findViewById(R.id.txt_voternum);
		RatingBar ratingbar =(RatingBar)findViewById(R.id.rating_bar);
		ImageView image = (ImageView)findViewById(R.id.img_picture);
		ImageView image2 = (ImageView)findViewById(R.id.img_share);
		ImageView image3 = (ImageView)findViewById(R.id.img_favor);
		
		image2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				_callback.do_share_on(v.getTag());
				
			}});
		image3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				_callback.do_favor_on(v.getTag(),(ImageView)v);
				
			}});
		
		ratingbar.setOnTouchListener(new OnTouchListener() {
          

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					_callback.openRating(v.getTag());
               }
               if (event.getAction() == MotionEvent.ACTION_DOWN) {
                   v.setPressed(true);
               }

               if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                   v.setPressed(false);
               }




               return true;
			}});
		title.setText(info.get("title").toString());
		detail2.setText(info.get("description").toString());
		
		detail3.setText(info.get("star_voternum").toString());
		ratingbar.setRating(Float.parseFloat(info.get("star_num").toString()));
		AppTools.loadImageFromUrl(image, info.get("pic_url").toString());
		int is_favor = Integer.parseInt( info.get("is_favor").toString());
		if (is_favor==1){
			image3.setImageResource(R.drawable.favor);
		}else{
			image3.setImageResource(R.drawable.favorno);
		}
		image3.setTag(info);
		image2.setTag(info);
		ratingbar.setTag(info);
		
	}

}
