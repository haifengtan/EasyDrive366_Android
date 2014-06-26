package cn.count.easydrive366.afterpay;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

public class AfterPayController {
	private Context _context;
	public AfterPayController(Context context){
		_context = context;
	}
	public void dispatch(final JSONObject json){
		try{
			String next_form = json.getString("next_form").trim().toLowerCase();
			Intent intent=null;
			if (next_form.equals("finished")){
				intent = new Intent(_context,FinishedActivity.class);
				
			}else if (next_form.equals("address")){
				intent = new Intent(_context,AddressActivity.class);
			}else if (next_form.equals("ins_contents")){
				intent = new Intent(_context,ContentsActivity.class);
			}else if (next_form.equals("ins_accident")){
				intent = new Intent(_context,AccidentActivity.class);
			}
			if (intent!=null){
				intent.putExtra("data", json.toString());
				_context.startActivity(intent);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
