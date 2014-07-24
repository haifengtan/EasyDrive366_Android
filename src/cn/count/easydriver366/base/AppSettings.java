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

import cn.count.easydrive366.MainActivity;
import cn.count.easydrive366.push.PushUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public final class AppSettings {
	//正式服务器
//	static public String ServerUrl = "http://m.4006678888.com:21000/index.php/";
	//测试服务器
	static public String ServerUrl = "http://yijia366.oicp.net:21000/index.php/";
	
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
	/**用户id  -1表示用户未登录*/
	static public int userid=-1;
	
	/**默认加载的条数*/
	static public int TotalPageCount=12;
	
	/**用户名*/
	static public String username;
	
	/**百度推送使用的userId*/
	static public String pushUserID;
	
	/**百度推送使用的channelID*/
	static public String pushChannelID;
	
	/**是否已经登录*/
	static public boolean isLogin= false;
	
	static public JSONArray driver_type_list;
	
	/**服务请求间隔*/
	static public int update_time=4*60*60;
	
	static public boolean isquiting = false;
	
	/**应用版本*/
	static public String version = "2.1.1";
	
	/**是否输出调试日志*/
	public static boolean isOutputDebug = true;
	
	public static final int READ_TIMEOUT = 10;
	public static final int CONNECT_TIMEOUT = 15;
	
	
	public static int task_id =0;
	
	/**
	 * 拼接发送百度推送信息的URL
	 * @return
	 */
	static public String url_for_send_push_info()
	{
		   return String.format("pushapi/add_pushinfo?userId=%s&channelId=%s&memberId=%d&equipment=3",pushUserID,pushChannelID,userid);
//	    return String.format("pushapi/add_pushinfo?userid=%s&channelid=%s&memberid=%s&equipment=%s",pushUserID,pushChannelID,userid,3);
	}
	
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
	
	/**
	 * 用户登录
	 * @param result
	 * @param context
	 */
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
			//登录成功后提交推送信息到服务器
//			sendPushInfo(context);
		} catch (JSONException e) {
			AppTools.log(e);
		}
	}
	
	/**
	 * 获取用户的信息  如果用户没有登录 则返回默认信息
	 * @param context
	 */
	static public  void restore_login_from_device(Context context){
		SharedPreferences prefs =context.getSharedPreferences(AppSettings.AppTile+"_login", Context.MODE_PRIVATE);
		userid = prefs.getInt("userid", -1);
		isLogin = prefs.getBoolean("isLogin", false);
		username = prefs.getString("username", "");
		pushUserID = prefs.getString("pushUserID", "");
		pushChannelID = prefs.getString("pushChannelID", "");
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
		PushManager.unbind(context);
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
	
	/**
	 * 提交推送信息到服务器
	 * @param context
	 */
	static public void sendPushInfo(Context context){
		System.out.println("推送到服务器");
		if (isLogin) {
			new Thread(new Runnable() {
				@Override
				public void run() {
						GetXML.doGet(ServerUrl
								+ url_for_send_push_info(), "utf-8");
				}
			}).start();
		}
	}
	
	/**
	 * 初始化百度推送
	 */
	static public void initBaiduPush(Context context,Activity activity) {
		// Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
		// 通过share preference实现的绑定标志开关，如果已经成功绑定，就取消这次绑定
		if (!PushUtils.hasBind(context)) {
			PushManager.startWork(context,
					PushConstants.LOGIN_TYPE_API_KEY,
					PushUtils.getMetaValue(activity, "api_key"));
			// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
			// PushManager.enableLbs(getApplicationContext());
		}

	}
}
