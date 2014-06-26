package cn.count.easydrive366;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class Splash extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash_activity);
		
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				Intent intent = new Intent(Splash.this,MainActivity.class);
				Splash.this.startActivity(intent);
				Splash.this.finish();
				
			}}, 5000);
	}

}
