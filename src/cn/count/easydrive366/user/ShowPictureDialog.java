package cn.count.easydrive366.user;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppTools;

import android.app.DialogFragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ShowPictureDialog extends DialogFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	}
	 @Override  
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	            Bundle savedInstanceState) {  
	        View v = inflater.inflate(R.layout.showpicture_dialog ,container, false);
	        ImageView img  =(ImageView)v.findViewById(R.id.imgPic);
	        img.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					dismiss();
					
				}});
	        Bundle b = this.getArguments();
	        String url = b.getString("url");
	        AppTools.loadImageFromUrl(img, url);
	        return v;
	 }
	

}
