package cn.count.easydrive366.guide;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.ActionActivity;

public class GuideActivity extends ActionActivity {
	private ImageView img;
	private int index=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modules_guide_activity);
		img = (ImageView)findViewById(R.id.image_pic);
		this.addSwipeToView(img);
	}
	private int[] temps = {R.drawable.g01,R.drawable.g02,R.drawable.g03,R.drawable.g04};
	private boolean is_swipe=false;
	public void onLeftSwipe() {
		is_swipe=true;
		index++;
		show_image();
		
	}

	public void onRightSwipe() {
		is_swipe=true;
		index--;
		show_image();
	}
	public void show_image(){
		if (index<0){
			index=0;
		}
		if (index>3){
			index=3;
		}
		img.setImageResource(temps[index]);
	}
	@Override
	public void onClick(View v) {
		if (is_swipe){
			is_swipe=false;
			return;
		}
		if (index==3){
			finish();
		}
	}
}
