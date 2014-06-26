package cn.count.easydriver366.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.count.easydrive366.R;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public final class AppTools {
	public static void log(Exception e){
		if (e !=null)
			Log.e("Application Log", e.getMessage());
	}
	public static boolean isSuccess(final String result,Context context){
		if (result==null){
			return false;
		}
		try{
			JSONObject json = new JSONObject(result);
			boolean r = isSuccess(json);
			if (context!=null){
				if (!json.isNull("alertmsg") && !json.getString("alertmsg").isEmpty()){
					Toast.makeText(context, json.getString("alertmsg"), Toast.LENGTH_LONG).show();
				}
				if (!r && !json.isNull("message") && !json.getString("message").isEmpty()){
					Toast.makeText(context, json.getString("message"), Toast.LENGTH_LONG).show();
				}
			}
			
			return r;
		}catch(Exception e){
			if (e!=null)
				log(e);
		}
		return false;
	}
	public static boolean isSuccess(final JSONObject result,Context context){
		try{
			
			boolean r = isSuccess(result);
			if (!result.isNull("alertmsg") && !result.getString("alertmsg").isEmpty()){
				Toast.makeText(context, result.getString("alertmsg"), Toast.LENGTH_LONG).show();
			}
			if (!r && !result.isNull("message") && !result.getString("message").isEmpty()){
				Toast.makeText(context, result.getString("message"), Toast.LENGTH_LONG).show();
			}
			return r;
		}catch(Exception e){
			log(e);
		}
		return false;
	}
	public static boolean isSuccess(final Object jsonobj){
		boolean result= false;
		if (jsonobj==null){
			return result;
		}
		if (jsonobj instanceof JSONObject){
			try{
				final String status = ((JSONObject)jsonobj).getString("status");
				result = status.equals("success");
			}catch(Exception e){
				log(e);
			}
			 
		}else if (jsonobj instanceof JSONArray){
			try{
				result =  ((JSONArray)jsonobj).length()>0;
			}catch(Exception e){
				log(e);
			}
		}
		return result;
	}
	public static Intent getPhoneAction(final String phone){
		Intent intent=null;
		try {
			intent = new Intent(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + phone));
			//startActivity(intent);
			
		} catch (Exception e) {
			log(e);
		}
		return intent;
	}
	public static Intent getBrowserAction(final String url){
		//Uri uri = Uri.parse("http://www.google.com"); //浏览器 
		//Uri uri =Uri.parse("tel:1232333"); //拨号程序 
		//Uri uri=Uri.parse("geo:39.899533,116.036476"); //打开地图定位 
		//Intent it = new Intent(Intent.ACTION_VIEW,uri); 
		
		Intent intent = null;
		intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
		return intent;
	}
	public static void loadImageFromUrl(ImageView imageView,final String url){
		if (imageView == null)
			return;
		if (url == null)
			return;
		if (url.isEmpty())
			return;
		if (!url.toLowerCase().startsWith("http://")){
			imageView.setImageResource(R.drawable.m);
		}else{
			com.koushikdutta.urlimageviewhelper.UrlImageViewHelper.setUrlDrawable(
					imageView, url);
		}
	}
	public static void loadImageFromUrl(ImageView imageView,final String url,int resourceId){
		if (imageView==null)
			return;
		if (url ==null)
			return;
		if (url.isEmpty())
			return;
		if (!url.toLowerCase().startsWith("http://")){
			imageView.setImageResource(R.drawable.m);
		}else{
			com.koushikdutta.urlimageviewhelper.UrlImageViewHelper.setUrlDrawable(imageView, url,resourceId);
		}
	}
	public static void chooseDate(String d,Context context,final ISetDate callback){
		
		if (d==null)
			return;
		d = d.trim();
		if (d.equals("")){
			d = "2000-01-01";
		}
		if (d.length()>10)
			d = d.substring(0, 9);
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final Calendar c  = Calendar.getInstance();
		
		try{
			c.setTime(sdf.parse(d));
			
			Dialog dialog = new DatePickerDialog(context,
					new DatePickerDialog.OnDateSetListener() {
						
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear,
								int dayOfMonth) {
								c.set(Calendar.YEAR, year);
								c.set(Calendar.MONTH,monthOfYear);
								c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
								
								callback.setDate( sdf.format(c.getTime()));
						}
					},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
			dialog.show();
		}catch(Exception e){
			log(e);
		}
	}
	public interface ISetDate{
		void setDate(final String date);
	}
}
