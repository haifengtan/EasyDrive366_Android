package cn.count.easydriver366.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public final class AppSettings {

	//static public String ServerUrl = "http://m.4006678888.com:21000/index.php/";
	static public String ServerUrl = "http://119.167.144.23:21000/index.php/";
	
	static public final String AppTile="cn.count.EasyDrive366";
	static public String LatestNewsKey="LatestNews";
	
	static public String WEIXIN_PARTNERID="1218610801";
	
	//below is for debug
	/*
	static public String WEIXIN_ID ="wxbf4902100b6d4a69";
	static public String SINA_WEIBO_ID="4276960189";  
	static public String BAIDUMAPKEY="nu9WO5Zg0SBHQXvegwM60DWH";//for debug   //old:30d50073a606ac3ce0b7f8a187e8248b
	*/
	// below is for release
	
	static public String WEIXIN_ID ="wxe590795397d68fd3";//"wxe590795397d68fd3";
	
	static public String SINA_WEIBO_ID="4031912721";  //"4031912721"
	static public String BAIDUMAPKEY="939cb0c01d09f55edbf15645e42a1624";//for release
	
	static public int userid=-1;
	static public int TotalPageCount=12;
	static public String username;
	static public boolean isLogin= false;
	static public JSONArray driver_type_list;
	static public int update_time=4*60*60;
	static public boolean isquiting = false;
	static public String version = "2.1.1";
	
	public static boolean isOutputDebug = true;
	public static final int READ_TIMEOUT = 10;
	public static final int CONNECT_TIMEOUT = 15;
	
	
	public static int task_id =0;
	static public String url_for_get_news()
	{
		
	    return String.format("api/get_news?userid=%d",userid);
	}
	static public String url_pull_msg(){
		return String.format("pushapi/pull_msg?userid=%d", userid);
	}
	static public String url_for_get_helpcalls()
	{
	    return String.format("api/get_helps?userid=%d",userid);
	}
	static public String url_for_get_carservice()
	{
	    return String.format("api/get_check_helps?userid=%d",userid);
	}
	
	static public String url_for_get_carservice_vendor(final String code)
	{
	    return String.format("api/get_help_service?userid=%d&code=%s",userid,code);
	}
	static public String url_for_get_carservice_note(final String code)
	{
	    return String.format("api/get_help_note?userid=%d&code=%s",userid,code);
	}
	
	static public String url_for_rescue(){
	    return String.format("api/get_rescues?userid=%d",userid);
	}

	static public String url_for_illegallys(){
	    return String.format("api/get_illegally_list?userid=%d",userid);
	}
	static public String url_for_insurance_list(){
	    return String.format("api/get_insurance_process_list?userid=%d",userid);
	}

	static public String url_for_maintain_list(){
	    return String.format("api/get_car_maintain_list?userid=%d",userid);
	}

	static public String url_for_post_maintain_record(){
	    return String.format("api/add_maintain_record?userid=%d",userid);
	}
	static public String url_for_get_maintain_record(){
	    return String.format("api/get_maintain_record?userid=%d",userid);
	}
	static public String url_getlatest(){
	    return String.format("api/get_latest?userid=%d",userid);
	}

	static public String url_get_driver_license(){
	    return String.format("api/get_driver_license?userid=%d",userid);
	}
	static public String url_get_car_registration(){
	    return String.format("api/get_car_registration?userid=%d",userid);
	}

	static public String url_get_suggestion_insurance(){
	    return String.format("api/get_suggestion_of_insurance?userid=%d",userid);
	    
	}
	static public String url_get_license_type(){
		return "api/get_license_type?userid=0";
	}
	
	static public String url_get_business_insurance(){
		return String.format("api/get_Policys?userid=%d", userid);
	}
	
	static public String url_get_suggestion_count(){
		return String.format("api/get_count_of_suggestion?userid=%d", userid);
	}
	static public String url_get_compulsory_details(){
		return String.format("api/get_compulsory_details?userid=%d",userid);
	}
	static public String url_get_taxforcarship(){
		return String.format("api/get_taxforcarship?userid=%d",userid);
	}
	static public String url_get_user_phone(){
		return String.format("api/get_user_phone?userid=%d", userid);
	}
	static public String url_get_activate_code(){
		return String.format("api/had_activate_code?userid=%d", userid);
	}
	static public String url_add_activate_code(String code){
		return String.format("api/add_activate_code?userid=%d&code=%s", userid,code);
	}
	static public String url_user_activate_code(){
		return String.format("api/get_activate_code?userid=%d", userid);
	}
	static public String get_activate_code_list(){
		return String.format("api/get_activate_code_list?userid=%d", userid);
	}
	static public String get_business(double latitude,double longitude,String typecode){
		return String.format("api/get_position?userid=%d&x=%f&y=%f&type=%s", userid,latitude,longitude,typecode);
	}
	static public String get_cardlist(){
		return String.format("api/get_inscard_list?userid=%d", userid);
	}
	static public String get_addcardstep0()
	{
		return String.format("api/add_inscard_step0?userid=%d", userid);
		
	}
	static public String add_inscard_step1(final String code){
		return String.format("api/add_inscard_step1?userid=%d&code=%s", userid,code);
	}
	static public String add_inscard_step2(final String number,final String name,final String identity,final String cell,final String address,final boolean is_agreed_bf,final String bf_name,final String bf_identity){
		return String.format("api/add_inscard_step2?userid=%d&number=%s&insured_name=%s&insured_idcard=%s&insured_phone=%s&insured_address=%s&is_agreed_bf=%s&bf_name=%s&bf_id=%s", 
				userid,
				number,
				name,
				identity,
				cell,
				address,
				is_agreed_bf?"true":"false",
				bf_name,
				bf_identity);
	}
	static public String get_userfeedback(){
		return String.format("api/get_feedback_user?userid=%d", userid);
	}
	static public void login(JSONObject result,Context context) {
		try {
			
			userid = result.getJSONObject("result").getInt("id");
			username = result.getJSONObject("result").getString("username");
			isLogin = true;
			int update_time = 4*60*60;
			if (!result.getJSONObject("result").isNull("update_time")){
				//Log.i("Update_time",result.getJSONObject("result").getString("update_time"));
				JSONObject json = result.getJSONObject("result");
				update_time = Integer.parseInt(json.getString("update_time"));
			}
			SharedPreferences prefs =context.getSharedPreferences(AppSettings.AppTile+"_login", Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putInt("userid", userid);
			editor.putBoolean("isLogin", isLogin);
			editor.putString("username", username);
			editor.putInt("update_time", update_time);
			editor.commit();
		} catch (JSONException e) {
			AppTools.log(e);
		}
	}
	static public  void restore_login_from_device(Context context){
		SharedPreferences prefs =context.getSharedPreferences(AppSettings.AppTile+"_login", Context.MODE_PRIVATE);
		userid = prefs.getInt("userid", -1);
		isLogin = prefs.getBoolean("isLogin", false);
		username = prefs.getString("username", "");
		update_time = prefs.getInt("update_time", 4*60*60);
	}
	static public void logout(Context context){
		AppSettings.isLogin = false;
		AppSettings.userid = -1;
		SharedPreferences prefs =context.getSharedPreferences(AppSettings.AppTile+"_login", Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putInt("userid", -1);
		editor.putBoolean("isLogin", false);
		editor.putString("username", "");
		editor.putInt("update_time", 4*60*60);
		editor.commit();
	}
	
public static String readInputStream(InputStream stream) throws IOException,UnsupportedEncodingException{
		
		Reader reader =null;
		reader = new InputStreamReader(stream,"UTF-8");
		char[] buffer = new char[256];
		StringBuilder sb = new StringBuilder();
		while (reader.read(buffer)!=-1)
		{
			sb.append(buffer);
		}
		return sb.toString();
	}
	public static String getOutputParameters(List<NameValuePair> params){
		
		
		StringBuilder result = new StringBuilder();
	    boolean first = true;
	    try
	    {
	    	for (NameValuePair pair : params)
		    {
		        if (first)
		            first = false;
		        else
		            result.append("&");

		        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
		        result.append("=");
		        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		    }

	    	
	    }catch( UnsupportedEncodingException e){
	    	log(e.getMessage());
	    }
	    
	    return result.toString();
		
	}
	public static void log(String logInformation)
	{
		if (isOutputDebug && logInformation!=null){
			
			Log.d("Http.Information", logInformation);
		}
		
	}
	public static boolean isSuccessJSON(final JSONObject json,Context context)
	{
		return AppTools.isSuccess(json,context);
	}
	public static boolean isSuccessJSON(final String result,Context context)
	{
		return AppTools.isSuccess(result,context);
	}
	public static JSONObject getSuccessJSON(final String result,Context context)
	{
		try
		{
			final JSONObject json = new JSONObject(result);
			if (AppTools.isSuccess(json,context)){
				return json.getJSONObject("result");
			}
			
			
		}catch(Exception e)
		{
			log(e.getMessage());
		}
		return null;
	}
	private static JSONObject getSuccessJSON(final String result)
	{
		try
		{
			final JSONObject json = new JSONObject(result);
			if (AppTools.isSuccess(json)){
				return json.getJSONObject("result");
			}
			
			
		}catch(Exception e)
		{
			log(e.getMessage());
		}
		return null;
	}
	public static JSONArray getSuccessJSONList(final String result)
	{
		try
		{
			JSONObject json = new JSONObject(result);
			if (AppTools.isSuccess(json)){
				return json.getJSONArray("result");
			}
			
			
		}catch(Exception e)
		{
			log(e.getMessage());
		}
		return null;
	}
	public static String getDefaultString(final String item,final String d){
		if (item ==null)
			return d;
		if (item.isEmpty())
			return d;
		return item;
	}
	
}
