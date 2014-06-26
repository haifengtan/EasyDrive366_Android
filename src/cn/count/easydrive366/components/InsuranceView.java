package cn.count.easydrive366.components;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppTools;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class InsuranceView extends LinearLayout {
	private LayoutInflater mInflater = null;
	private Context mContext;
	public InsuranceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mInflater.inflate(R.layout.partview_businessinsurance,this);
	}
	public void setData(final JSONObject data,final String title){
		try{
			//{"total":"4669.84","valid":"2012-05-23~2013-05-22","com":"760.00","tax":"420.00","company":"英大泰和财产保险股份有限公司  ","list":[{"Amount":"保额","PolicyId":"","InsuName":"","DeductibleFee":"不计免赔","InsuType":"","Fee":"保费"},{"Amount":"500,000.00","PolicyId":"b6b8a5c9-8e76-404a-956f-bde5ce437953","InsuName":"三者","DeductibleFee":"0.00","InsuType":"F0000003","Fee":"1,190.00"},{"Amount":"70,345.60","PolicyId":"b6b8a5c9-8e76-404a-956f-bde5ce437953","InsuName":"盗抢","DeductibleFee":"0.00","InsuType":"F0000005","Fee":"325.28"},{"Amount":"0.00","PolicyId":"b6b8a5c9-8e76-404a-956f-bde5ce437953","InsuName":"不计免赔","DeductibleFee":"0.00","InsuType":"F0000022","Fee":"452.20"},{"Amount":"0.00","PolicyId":"b6b8a5c9-8e76-404a-956f-bde5ce437953","InsuName":"玻璃国产","DeductibleFee":"0.00","InsuType":"F0000031","Fee":"131.40"},{"Amount":"98,800.00","PolicyId":"b6b8a5c9-8e76-404a-956f-bde5ce437953","InsuName":"车损","DeductibleFee":"0.00","InsuType":"F0000038","Fee":"1,390.96"}],"po":"1140605082012002707YD ","biz":"3489.84"}
			((TextView)findViewById(R.id.txt_partview_business_title)).setText(title);
			((TextView)findViewById(R.id.txt_partview_business_companyname)).setText(data.getString("company").trim());
			((TextView)findViewById(R.id.txt_partview_business_insurance_no)).setText(data.getString("po").trim());
			((TextView)findViewById(R.id.txt_partview_business_date_to_date)).setText(data.getString("valid").trim());
			((TextView)findViewById(R.id.txt_partview_business_tax)).setText(data.getString("tax").trim());
			((TextView)findViewById(R.id.txt_partview_business_compulsory)).setText(data.getString("com").trim());
			((TextView)findViewById(R.id.txt_partview_business_biz)).setText(data.getString("biz").trim());
			TableLayout tablelayout= (TableLayout)findViewById(R.id.table_partview_business_items);
			/*
			TableRow tr= new TableRow(mContext);
			tr.addView(createTextView("InsuName"));
			tr.addView(createTextView("Amount"));
			tr.addView(createTextView("Fee"));
			tr.addView(createTextView("DeductibleFee"));
			tr.setBackgroundResource(R.drawable.sign_input2);
			tablelayout.addView(tr);
			*/
			JSONArray list = data.getJSONArray("list");
			for(int i=0;i<list.length();i++){
				JSONObject item = list.getJSONObject(i);
				TableRow row= new TableRow(mContext);
				/*
				row.addView(createTextView(item.getString("InsuName")));
				row.addView(createTextView(item.getString("Amount")));
				row.addView(createTextView(item.getString("Fee")));
				row.addView(createTextView(item.getString("DeductibleFee")));
				if (i==0){
					row.setBackgroundResource(R.drawable.signup_hit_input1);
				}else if (i==list.length()-1){
					row.setBackgroundResource(R.drawable.signup_hit_input6);
				}else{
					row.setBackgroundResource(R.drawable.signup_hit_input2);
				}
				*/
				InsuranceDetailItem detail = new InsuranceDetailItem(mContext,null);
				detail.setData(item.getString("InsuName").trim(), item.getString("Amount").trim(), item.getString("Fee").trim(), item.getString("DeductibleFee").trim());
				row.addView(detail);
				tablelayout.addView(row);
				if (i==list.length()-1){
					detail.setEndBackend();
				}
			}
			
		}catch(Exception e){
			AppTools.log(e);
		}
	}
	private TextView createTextView(final String title){
		TextView tv = new TextView(mContext);
		tv.setText(title);
		//tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL));
		return tv;
	}

}
