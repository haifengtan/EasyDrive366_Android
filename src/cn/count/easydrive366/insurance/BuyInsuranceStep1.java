package cn.count.easydrive366.insurance;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class BuyInsuranceStep1 extends BaseInsurance {
	private WebView _webView;
	private Handler mHandler = new Handler();
	private String goods_id;
	@Override
	protected void onRightButtonPress() {
		Intent intent =new Intent(this,BuyInsuranceStep2.class);
		if (goods_id!=null && !goods_id.isEmpty()){
			intent.putExtra("goods_id", goods_id);
		}
		startActivity(intent);
		
		stack_push();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		stack_init();
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.modules_browser_activity);
		this.setupTitle("在线购买保险", "第一步");
		this.setupLeftButton();
		this.setupRightButtonWithText("下一步");
		_webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = _webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		_webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		/*
		_webView.addJavascriptInterface(new Object() {
			public void clickOnAndroid() {
				mHandler.post(new Runnable() {
					public void run() {
						_webView.loadUrl("javascript:wave()");
					}
				});
			}
		}, "Browser");
		*/
		
		//_webView.loadUrl(url);
		
		String web_url = getIntent().getStringExtra("web_url");
		if (web_url!=null && !web_url.isEmpty()){
			goods_id = getIntent().getStringExtra("goods_id");
			
			_webView.loadUrl(web_url);
		}else{
			load_data();
		}
		
		 
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {       
        if ((keyCode == KeyEvent.KEYCODE_BACK) && _webView.canGoBack()) {       
            _webView.goBack();       
                   return true;       
        }       
        return super.onKeyDown(keyCode, event);       
    }
	private void load_data(){
		beginHttp();
		String url = String.format("ins/carins_intro?userid=%d", AppSettings.userid);
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				process_data(result);
				
			}}.execute(url);
	}
	private void process_data(final String result){
		try{
			JSONObject json =AppSettings.getSuccessJSON(result,this);
			if (json!=null){
				this.rightTopMenu.setEnabled(true);
				_webView.loadUrl(json.getString("web_url"));
			}else{
				this.rightTopMenu.setEnabled(false);
				
			}
		}
		catch(Exception e){
			log(e);
		}
	}
	
	
	

}
