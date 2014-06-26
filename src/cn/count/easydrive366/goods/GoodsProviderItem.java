package cn.count.easydrive366.goods;

import java.util.Map;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseListViewV4Fragment.ViewHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class GoodsProviderItem extends LinearLayout {

	public GoodsProviderItem(Context context, AttributeSet attrs,final String title,
			final String phone,final String address,final String voternum,final int star_num,final String url) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.listitem_provider, this);
		
		TextView txtTitle = (TextView)findViewById(R.id.txt_title);
		TextView txtPhone = (TextView)findViewById(R.id.txt_phone);
		TextView txtAddress  = (TextView)findViewById(R.id.txt_address);
		TextView txtVoter = (TextView)findViewById(R.id.txt_voternum);
		RatingBar ratingBar  =(RatingBar)findViewById(R.id.rating_bar);
		ImageView imageView  = (ImageView)findViewById(R.id.img_picture);
		txtTitle.setText(title);
		txtPhone.setText(phone);
		txtAddress.setText(address);
		txtVoter.setText(voternum);
		ratingBar.setRating(star_num);
		AppTools.loadImageFromUrl(imageView,url);
	}
	
}
