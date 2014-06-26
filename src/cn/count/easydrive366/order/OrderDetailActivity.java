package cn.count.easydrive366.order;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.afterpay.AfterPayController;
import cn.count.easydrive366.goods.GoodsDetailActivity;
import cn.count.easydrive366.insurance.UploadInsPhotoActivity;
import cn.count.easydrive366.user.BoundActivity;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;

public class OrderDetailActivity extends BaseHttpActivity {
	private String order_id;
	private String order_status;
	private String order_status_name;
	private boolean is_exform;
	private String next_form;
	private boolean is_upload;
	private int order_count;
	private JSONObject json;
	private int _goods_id;
	private String order_url;
	private String coupon_url;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_orderdetail_activity);
		this.setBarTitle("订单详情");
		this.setupLeftButton();
		
		this.setupRightButtonWithText("编辑");
		Intent intent= getIntent();
		order_id = intent.getStringExtra("order_id");
		load_data();
	}
	private void load_data(){
		String url = String.format("order/order_detail?userid=%d&orderid=%s", AppSettings.userid,order_id);
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				load_view(result);
				
			}}.execute(url);
		
	}
	private void load_view(final String result){
		json = AppSettings.getSuccessJSON(result,this);
		if (json!=null){
			try{
				order_status = json.getString("order_status");
				order_status_name = json.getString("order_status_name");
				next_form = json.getString("next_form");
				is_upload = json.getInt("is_upload")==1;
				is_exform = json.getInt("is_exform")==1;
				order_url = json.getString("order_url").trim();
				coupon_url = json.optString("coupon_url");
				final String status_url = json.getString("status_url").trim();
				
				if (order_url!=null && !order_url.isEmpty()){
					findViewById(R.id.layout_order_id).setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(OrderDetailActivity.this,BrowserActivity.class);
							intent.putExtra("url", order_url);
							startActivity(intent);
							
						}});
				}
				if (coupon_url!=null && !coupon_url.isEmpty()){
					findViewById(R.id.layout_coupon).setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(OrderDetailActivity.this,BrowserActivity.class);
							intent.putExtra("url", coupon_url);
							startActivity(intent);
							
						}});
				}
				findViewById(R.id.layout_goods).setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						int id = _goods_id;
						Intent intent = new Intent(OrderDetailActivity.this,
								GoodsDetailActivity.class);
						intent.putExtra("id", id);
						startActivity(intent);
						
					}});
				if (!is_exform){
					this.rightTopMenu.setVisible(false);
				}
				order_count = 0;
				if (json.getJSONArray("goods").length()>0){
					JSONObject  goods = json.getJSONArray("goods").getJSONObject(0);
					order_count += goods.getInt("quantity");
					//set goods
					((TextView)findViewById(R.id.txt_title)).setText(goods.getString("name"));
					((TextView)findViewById(R.id.txt_description)).setText(goods.getString("description"));
					((TextView)findViewById(R.id.txt_stand_price)).setText(goods.getString("stand_price"));
					((TextView)findViewById(R.id.txt_discount)).setText(goods.getString("discount"));
					((TextView)findViewById(R.id.txt_price)).setText(goods.getString("price"));
					((TextView)findViewById(R.id.txt_buyer)).setText(goods.getString("buyer"));
					ImageView img = (ImageView)findViewById(R.id.img_picture);
					AppTools.loadImageFromUrl(img, goods.getString("pic_url"));
					_goods_id = goods.optInt("id");
					
				}
				//sets order informaiton
				((TextView)findViewById(R.id.txt_order_id)).setText(json.getString("order_id"));
				((TextView)findViewById(R.id.txt_order_time)).setText(json.getString("order_time"));
				((TextView)findViewById(R.id.txt_coupon_code)).setText(json.getString("coupon_code"));
				((TextView)findViewById(R.id.txt_count)).setText(String.format("%d", order_count));
				((TextView)findViewById(R.id.txt_order_total)).setText(json.getString("order_total"));
				((TextView)findViewById(R.id.txt_order_status)).setText(json.getString("order_status_name"));
				findViewById(R.id.layout_status).setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						if (status_url!=null && !status_url.isEmpty()){
							Intent intent = new Intent(OrderDetailActivity.this,BrowserActivity.class);
							intent.putExtra("url", status_url);
							OrderDetailActivity.this.startActivity(intent);
							
						}
						
					}});
				if (order_status.equals("notpay")){
					this.rightTopMenu.setVisible(false);
					findViewById(R.id.layout_contents).setVisibility(View.GONE);
					findViewById(R.id.layout_accident).setVisibility(View.GONE);
					findViewById(R.id.layout_finished).setVisibility(View.GONE);
					findViewById(R.id.layout_address).setVisibility(View.GONE);
					findViewById(R.id.layout_shouldpay).setVisibility(View.GONE);
					findViewById(R.id.layout_pay).setVisibility(View.GONE);
					findViewById(R.id.layout_upload).setVisibility(View.GONE);
					findViewById(R.id.layout_get_bounds).setVisibility(View.GONE);
					findViewById(R.id.btn_pay).setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							doPay();
							
						}});
				}else{
					findViewById(R.id.btn_pay).setVisibility(View.GONE);
					if (is_upload){
						findViewById(R.id.layout_upload).setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								doUpload();
								
							}});
					}else{
						findViewById(R.id.layout_upload).setVisibility(View.GONE);
					}
					if (next_form.equals("address")){
						findViewById(R.id.layout_contents).setVisibility(View.GONE);
						findViewById(R.id.layout_accident).setVisibility(View.GONE);
						findViewById(R.id.layout_finished).setVisibility(View.GONE);
						((TextView)findViewById(R.id.txt_shipping)).setText(json.getString("shipping"));
						((TextView)findViewById(R.id.txt_name1)).setText(json.getString("name"));
						((TextView)findViewById(R.id.txt_phone1)).setText(json.getString("phone"));
						((TextView)findViewById(R.id.txt_address1)).setText(json.getString("address"));
					}else if (next_form.equals("ins_contents")){
						findViewById(R.id.layout_address).setVisibility(View.GONE);
						findViewById(R.id.layout_accident).setVisibility(View.GONE);
						findViewById(R.id.layout_finished).setVisibility(View.GONE);
						((TextView)findViewById(R.id.txt_idcard2)).setText(json.getString("idcard"));
						((TextView)findViewById(R.id.txt_name2)).setText(json.getString("name"));
						((TextView)findViewById(R.id.txt_phone2)).setText(json.getString("phone"));
						((TextView)findViewById(R.id.txt_address2)).setText(json.getString("address"));
					}else if (next_form.equals("ins_accident")){
						findViewById(R.id.layout_contents).setVisibility(View.GONE);
						findViewById(R.id.layout_address).setVisibility(View.GONE);
						findViewById(R.id.layout_finished).setVisibility(View.GONE);
						((TextView)findViewById(R.id.txt_idcard3)).setText(json.getString("idcard"));
						((TextView)findViewById(R.id.txt_name3)).setText(json.getString("name"));
						((TextView)findViewById(R.id.txt_phone3)).setText(json.getString("phone"));
						((TextView)findViewById(R.id.txt_typename)).setText(json.getString("typename"));
					}else if (next_form.equals("finished")){
						findViewById(R.id.layout_contents).setVisibility(View.GONE);
						findViewById(R.id.layout_accident).setVisibility(View.GONE);
						findViewById(R.id.layout_address).setVisibility(View.GONE);
						((TextView)findViewById(R.id.txt_content)).setText(json.getString("content"));
						
					}
					//should pay
					String temp = json.getString("discount");
					((TextView)findViewById(R.id.txt_pay_discount)).setText(temp);
					((TextView)findViewById(R.id.txt_bounds)).setText(json.getString("bounds"));
					((TextView)findViewById(R.id.txt_order_pay)).setText(json.getString("order_pay"));
					//bounds
					((TextView)findViewById(R.id.txt_get_bounds)).setText(json.getString("get_bounds"));
					findViewById(R.id.layout_get_bounds).setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							if (status_url!=null && !status_url.isEmpty()){
								Intent intent = new Intent(OrderDetailActivity.this,BoundActivity.class);
								
								OrderDetailActivity.this.startActivity(intent);
								
							}
							
						}});
					//pay
					JSONArray pays = json.getJSONArray("pay");
					TableLayout tblPays = (TableLayout)findViewById(R.id.tb_pays);
					for(int i=0;i<pays.length();i++){
						final JSONObject pay = pays.getJSONObject(i);
						int index;
						if (i==0)
							index=0;
						else if (i==pays.length()-1)
							index=-1;
						else
							index =1;
						TableRow tr = new TableRow(this);
						PayPayItem payItem = new PayPayItem(this,null);
						payItem.setData(pay.getString("bank_name"), pay.getString("account"), index);
						tr.addView(payItem);
						tblPays.addView(tr);
						tr.setTag(pay.get("pay_url"));
						
						tr.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								String url = (String)v.getTag();
								Intent intent = new Intent(OrderDetailActivity.this,BrowserActivity.class);
								intent.putExtra("url", url);
								OrderDetailActivity.this.startActivity(intent);
							}});
					}
					
				}
				
				
			}catch(Exception e){
				log(e);
			}
		}
		
	}
	private void doPay(){
		Intent intent = new Intent(this,NewOrderActivity.class);
		intent.putExtra("order_id", this.order_id);
		startActivity(intent);
	}
	private void doUpload(){
		Intent intent = new Intent(this,UploadInsPhotoActivity.class);
		intent.putExtra("order_id", this.order_id);
		startActivity(intent);
	}
	@Override
	protected void onRightButtonPress() {
		String url = String.format("order/order_exform?userid=%d&orderid=%s",AppSettings.userid,this.order_id);
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				process_result(result);
				
			}}.execute(url);
		
	}
	private void process_result(final String result){
		JSONObject j = AppSettings.getSuccessJSON(result, this);
		if (j!=null){
			AfterPayController controller = new AfterPayController(this);
			controller.dispatch(j);
		}
	}
}

