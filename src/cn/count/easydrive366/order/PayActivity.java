package cn.count.easydrive366.order;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.android.app.sdk.AliPay;


import cn.count.easydrive366.afterpay.AfterPayController;
import cn.count.easydrive366.alipay.*;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

public class PayActivity extends BaseHttpActivity{
	private TableLayout tblItems;
	private LinearLayout tblPays;
	private CheckBox chbUse;
	private TextView txtOrderPay;
	private String order_id;
	private String json;
	private JSONObject data;
	private String title;
	private String body;
	private String price;
	private String bounds_num;
	private boolean isUseDiscount;
	private String bankid;
	private String bankname;
	private IWXAPI api;
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_needpay_activity);
		this.setBarTitle("付款");
		this.setupLeftButton();
		api = WXAPIFactory.createWXAPI(this, AppSettings.WEIXIN_ID);
    	api.registerApp(AppSettings.WEIXIN_ID);
		
		Intent intent= getIntent();
		json = intent.getStringExtra("json");
		if (json==null || json.isEmpty()){
			order_id = intent.getStringExtra("order_id");
			load_data();
		}else{
			load_view(json);
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction("wx.pay.success");
		wx_receiver = new Wx_Receiver(this);
		this.registerReceiver(wx_receiver, filter);
	}
	private Wx_Receiver wx_receiver;
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(wx_receiver);
		super.onDestroy();
	}

	private void load_data(){
		String url = String.format("order/order_info?userid=%d&orderid=%s", AppSettings.userid,order_id);
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				load_view(result);
				
			}}.execute(url);
	}
	private void setupUseDiscount(final boolean isUsed){
		try{
			isUseDiscount = isUsed;
			price = data.getString(isUsed?"order_pay":"order_pay_2");
			txtOrderPay.setText(price);
		}catch(Exception e){
			log(e);
		}
	}
	private void load_view(final String result){
		txtOrderPay=(TextView)findViewById(R.id.txt_order_pay);
		chbUse = (CheckBox)findViewById(R.id.chb_usedicount);
		chbUse.setChecked(false);
		chbUse.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				setupUseDiscount(isChecked);
				
			}});
		
		data = AppSettings.getSuccessJSON(result,this);
		try{
			isUseDiscount =false;
			order_id = data.getString("order_id");
			((TextView)findViewById(R.id.txt_order_total)).setText(data.getString("order_total"));
			((TextView)findViewById(R.id.txt_discount)).setText(data.getString("discount"));
			((TextView)findViewById(R.id.txt_bounds)).setText(data.getString("bounds"));
			bounds_num = data.getString("bounds_num");
			txtOrderPay.setText(data.getString("order_pay_2"));
			tblItems = (TableLayout)findViewById(R.id.tb_items);
			tblPays = (LinearLayout)findViewById(R.id.tb_pays);
			tblItems.removeAllViews();
			tblPays.removeAllViews();
			price = data.getString(isUseDiscount?"order_pay":"order_pay_2");
			JSONArray goods = data.getJSONArray("goods");
			
			
			for(int i=0;i<goods.length();i++){
				JSONObject item = goods.getJSONObject(i);
				/*
				TableRow tr = new TableRow(this);
				PayGoodsItem goodsItem = new PayGoodsItem(this,null);
				goodsItem.setData(item.getString("name"), item.getString("price"), item.getString("quantity"));
				tr.addView(goodsItem);
				tblItems.addView(tr);
				*/
				title = item.getString("name");
				body  = item.getString("name");
				//price = item.getString("price");
				break;
			}
			
			JSONArray pays = data.getJSONArray("pay");
			for(int i=0;i<pays.length();i++){
				final JSONObject pay = pays.getJSONObject(i);
				int index;
				if (i==0)
					index=0;
				else if (i==pays.length()-1)
					index=-1;
				else
					index =1;
				
				
				PayPayItem payItem = new PayPayItem(this,null);
				
				payItem.setData(pay.getString("bank_name"), pay.getString("account"), index);
				
				tblPays.addView(payItem);
				payItem.setTag(pay);
				payItem.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						try{
							JSONObject pay = (JSONObject)v.getTag();
							
							bankid=pay.getString("bank_id");
							bankname=pay.getString("bank_name");
							if (pay.getString("bank_id").equals("00001")){
								
								alipayStart();
							}else if (pay.getString("bank_id").equals("00000")){
								afterPay();
							}else if (pay.getString("bank_id").equals("62000")){
								up_pay();
							}else if (pay.getString("bank_id").equals("60000")){
								wx_pay();
							}
						}catch(Exception e){
							log(e);
						}
						
						
					}});
			}
			
		}catch(Exception e){
			log(e);
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
					JSONObject json = new JSONObject(result);
					String tn = json.optString("tn");
					start_up_pay(tn);
				}catch(Exception e){
					log(e);
				}
				
			}}.execute(url);
	}
	private void start_up_pay(final String tn){
		String serverMode = "00"; //00 - production
		int ret = UPPayAssistEx.startPay(this, null, null, tn, serverMode);
		if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND){
			UPPayAssistEx.installUPPayPlugin(this);
		}
	}
	private void wx_pay(){
		price = price.replace("元", "");
		String url = String.format("pay_wechat/get_prepay?userid=%d&orderid=%s&total=%s&name=%s", 
				AppSettings.userid,
				this.order_id,
				price,
				title);
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				JSONObject json = AppSettings.getSuccessJSON(result, PayActivity.this);
				if (json!=null){
					try{
						PayReq request = new PayReq();
						request.appId = AppSettings.WEIXIN_ID;
						request.partnerId = AppSettings.WEIXIN_PARTNERID;
						request.prepayId = json.optString("prepayid");
						request.nonceStr = json.optString("noncestr");
						request.packageValue = "Sign=WXPay";
						request.sign = json.optString("sign");
						request.timeStamp = json.optString("timestamp");
						api.sendReq(request);
					}catch(Exception e){
						log(e);
					}
				}
				
			}}.execute(url);
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		if (data == null){
			return;
		}
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")){
			afterPay();
		}
	}
	private static String TAG = "Alipay";
	private static final int RQF_PAY = 1;
	private static final int RQF_LOGIN = 2;
	private void alipayStart(){
		price = price.replace("元", "");
		if (Float.parseFloat(price)<0.009){
			this.showMessage("应付金额为0不能使用支付宝支付。", null);
			return;
		}
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
					AliPay alipay = new AliPay(PayActivity.this, mHandler);
					
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
				Toast.makeText(PayActivity.this, result.getResult(),
						Toast.LENGTH_SHORT).show();
				//resultStatus={9000};memo={};result={partner="2088901722164970"&out_trade_no="85a76a260d36252503208acee1081451"&subject="易驾366审车服务卡"&body="易驾366审车服务卡"&total_fee="0.01"&notify_url="http%3A%2F%2Fm.4006678888.com%3A21000%2Findex.php%2Fpaylog%2Fnoti_action"&service="mobile.securitypay.pay"&_input_charset="UTF-8"&return_url="http%3A%2F%2Fm.4006678888.com%3A21000%2Findex.php%2Fpaylog%2Freturn_action"&payment_type="1"&seller_id="2088901722164970"&it_b_pay="1m"&success="true"&sign_type="RSA"&sign="J4QZ6PmdgNUMdVBdV8gRbQm1o0MEoepMF53UZbAA3SJbSgWLceyb6984dGTMEt98AYhafqC3fEmqForlJeYXwZEuzdx0gu/o2zIgwFDHx0MOCQ1XimUKcE8pft4SqlIoD6110bU/+PVacMKQDi9Uj6Heevk7Nc9ZyuE9G6WSdmI="}
			}
				break;
			default:
				break;
			}
		};
	};
	private boolean finish_pay=false;
	public void afterPay(){
		if (finish_pay){
			return;
		}
		finish_pay=true;
		String bounds = "0";
		if (this.isUseDiscount)
			bounds = bounds_num;
		String url =String.format("order/order_payed?userid=%d&orderid=%s&orderpay=%s&bounds=%s&bankid=%s&account=%s",
				AppSettings.userid,this.order_id,price,bounds,bankid,bankname);
		beginHttp();
		
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				JSONObject json = AppSettings.getSuccessJSON(result,PayActivity.this);
				if (json!=null){
					
					AfterPayController controller = new AfterPayController(PayActivity.this);
					controller.dispatch(json);
					finish();
				}
				
				
			}}.execute(url);
	}
	
	public static class Wx_Receiver extends BroadcastReceiver{
		private PayActivity parent;
		public Wx_Receiver(){
			
		}
		public Wx_Receiver(PayActivity p){
			parent = p;
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("Receive wx pay success", intent.toString());
			if (intent.getIntExtra("result", -1)==0){
				if (parent!=null)
					parent.afterPay();
			}else{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.app_name);
				builder.setMessage(String.format("支付失败，微信返回结果：%d，请联系客服。", intent.getIntExtra("result", -1)));
				builder.show();
			}
		}
		
	}

	
}
