package cn.count.easydrive366.insurance;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.android.app.sdk.AliPay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import cn.count.easydrive366.R;
import cn.count.easydrive366.afterpay.AfterPayController;
import cn.count.easydrive366.alipay.Keys;
import cn.count.easydrive366.alipay.Result;
import cn.count.easydrive366.alipay.Rsa;
import cn.count.easydrive366.components.KeyValueItem;
import cn.count.easydrive366.order.PayActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class BuyInsuranceStep6 extends BaseInsurance {
	private String data;
	private boolean useDiscount=false;
	private String order_pay;
	private String order_pay2;
	private String order_id;
	private String price;
	private String bounds;
	private String bounds_num;
	private String bank_id;
	private String account;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buyinsurance_step6);
		this.setupTitle("在线购买保险", "第六步");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		init_view();
		
	}
	
	
	private TextView txt_pay;
	private void init_view(){
		data  = this.getIntent().getStringExtra("data");
		try{
			JSONObject json = new JSONObject(data).getJSONObject("result");
			order_pay = json.getString("order_pay");
			order_pay2 = json.getString("order_pay_2");
			order_id = json.getString("order_id");
			price =useDiscount?order_pay:order_pay2;
			bounds =json.getString("bounds");
			bounds_num = json.optString("bounds_num");
			if (bounds_num==null || bounds_num.isEmpty()){
				bounds_num = "0";
			}
			((TextView)findViewById(R.id.txt_order_total)).setText(json.getString("order_total"));
			((TextView)findViewById(R.id.txt_bounds)).setText(bounds);
			txt_pay =(TextView)findViewById(R.id.txt_order_pay); 
			txt_pay.setText(price);
			CheckBox chb = (CheckBox)findViewById(R.id.chb_usediscount);
			(chb).setChecked(useDiscount);
			chb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					useDiscount = isChecked;
					price =useDiscount?order_pay:order_pay2;
					txt_pay.setText(price);
				}});
			LinearLayout table = (LinearLayout)findViewById(R.id.table_pay);
			table.removeAllViewsInLayout();
			JSONArray list = json.getJSONArray("pay");
			for(int i=0;i<list.length();i++){
				final JSONObject pay = list.getJSONObject(i);
				bank_id = pay.getString("bank_id"); 
				
				KeyValueItem kv = new KeyValueItem(this,null);
				kv.setData(pay.getString("bank_name"), pay.getString("account"));
				
				table.addView(kv);
				kv.setTag(pay);
				kv.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						order_pay_by(v.getTag());
						
					}});
			}
			
			
		}catch(Exception e){
			log(e);
		}
	}
	private void order_pay_by(final Object pay){
		try{
			JSONObject json = (JSONObject)pay;
			this.bank_id = json.getString("bank_id");
			this.account = json.getString("account");
			if (bank_id.equals("00001")){
				alipayStart();
			}else if (bank_id.equals("00000")){
				afterPay();
			}else if (bank_id.equals("62000")){
				//up_pay();
			}
		}catch(Exception e){
			
		}
		
	}
	private void up_pay(){
		
		price = price.replace("元", "");
		String url = String.format("UnionPay/PayNewOrder/%s/%s", this.order_id,price);
		new HttpExecuteGetTask(){
			@Override
			protected String getServerUrl(){
				return "http://payment.yijia366.cn/";
			}
			@Override
			protected void onPostExecute(String result) {
				try{
					
				}catch(Exception e){
					log(e);
				}
				
			}}.execute(url);
	}
	private static String TAG = "Alipay";
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;
	private void alipayStart(){
		try {
			
			String info = getNewOrderInfo(0);
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			
			// start the pay.
			Log.i(TAG, "info = " + info);

			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(BuyInsuranceStep6.this, mHandler);
					
					//设置为沙箱模式，不设置默认为线上环境
					//alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);

					Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, R.string.remote_call_failed,
					Toast.LENGTH_SHORT).show();
		}
	}
	private String getNewOrderInfo(int position) {
		//price = "0.01";
		price = price.replace("元", "");
		String title = "在线购买保险";
		String body = order_id;
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(this.order_id/*getOutTradeNo()*/);
		sb.append("\"&subject=\"");
		sb.append(title);
		sb.append("\"&body=\"");
		sb.append(body);
		sb.append("\"&total_fee=\"");
		sb.append(price);
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode("http://m.4006678888.com:21000/index.php/paylog/noti_action"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.4006678888.com:21000/index.php/paylog/return_action"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);

			switch (msg.what) {
			case RQF_PAY:
				result.parseResult();
				if (result.isSignOk){
					//success;
					afterPay();
				}
				break;
			case RQF_LOGIN: {
				Toast.makeText(BuyInsuranceStep6.this, result.getResult(),
						Toast.LENGTH_SHORT).show();
				//resultStatus={9000};memo={};result={partner="2088901722164970"&out_trade_no="85a76a260d36252503208acee1081451"&subject="易驾366审车服务卡"&body="易驾366审车服务卡"&total_fee="0.01"&notify_url="http%3A%2F%2Fm.4006678888.com%3A21000%2Findex.php%2Fpaylog%2Fnoti_action"&service="mobile.securitypay.pay"&_input_charset="UTF-8"&return_url="http%3A%2F%2Fm.4006678888.com%3A21000%2Findex.php%2Fpaylog%2Freturn_action"&payment_type="1"&seller_id="2088901722164970"&it_b_pay="1m"&success="true"&sign_type="RSA"&sign="J4QZ6PmdgNUMdVBdV8gRbQm1o0MEoepMF53UZbAA3SJbSgWLceyb6984dGTMEt98AYhafqC3fEmqForlJeYXwZEuzdx0gu/o2zIgwFDHx0MOCQ1XimUKcE8pft4SqlIoD6110bU/+PVacMKQDi9Uj6Heevk7Nc9ZyuE9G6WSdmI="}
			}
				break;
			default:
				break;
			}
		};
	};
	private void afterPay(){
		Intent intent = new Intent(this,BuyInsuranceStep7.class);
		
		intent.putExtra("orderid", this.order_id);
		intent.putExtra("bounds",useDiscount?this.bounds_num:"0");
		intent.putExtra("bank_id", this.bank_id);
		intent.putExtra("account", this.account);
		startActivity(intent);
		this.finish();
	}
}
