package cn.count.easydriver366.base;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class HttpClient {
	private IHttpCallback mCallback;

	
	public HttpClient(IHttpCallback callback){
		mCallback = callback;
	}
	
	
	
	public void requestServer(final String urlParams,final int returnType){
		sendHttp(AppSettings.ServerUrl,urlParams,returnType);
	}
	public void sendHttp(final String serverUrl,final String urlParams,final int msgType){
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				String url =serverUrl +urlParams+"&timestamp="+String.valueOf(System.currentTimeMillis());
				if (AppSettings.task_id>0){
					url = String.format("%s&taskid=%d", url,AppSettings.task_id);
					AppSettings.task_id =0;
				}
				if (AppSettings.isOutputDebug)
					Log.e("Http Begin", url);
				HttpGet httpGet = new HttpGet(url);
				DefaultHttpClient client = new DefaultHttpClient();
				try{
				
					HttpResponse response = client.execute(httpGet);
					if (response.getStatusLine().getStatusCode()==200){
						HttpEntity entity = response.getEntity();
						InputStream inputStream = entity.getContent();
						ByteArrayOutputStream content = new ByteArrayOutputStream();
						int readBytes =0;
						byte[] sBuffer = new byte[512];
						while ((readBytes=inputStream.read(sBuffer))!=-1){
							content.write(sBuffer, 0, readBytes);
						}
						String result = new String(content.toByteArray());
						if (AppSettings.isOutputDebug)
							Log.e("Http end", result);
						processResponse(msgType,result);
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					client.getConnectionManager().shutdown();
				}
				
			}
			
		});
		t.start();
		
	}
	
	public void postHttp(final String serverUrl,final String urlParams,final List<NameValuePair> params,final int msgType){
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				String url =serverUrl +urlParams;
				HttpPost http = new HttpPost(url);
				DefaultHttpClient client = new DefaultHttpClient();
				try{
					//params.add(new BasicNameValuePair("timestamp",String.valueOf(System.currentTimeMillis())));
					http.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
					HttpResponse response = client.execute(http);
					if (response.getStatusLine().getStatusCode()==200){
						HttpEntity entity = response.getEntity();
						InputStream inputStream = entity.getContent();
						ByteArrayOutputStream content = new ByteArrayOutputStream();
						int readBytes =0;
						byte[] sBuffer = new byte[512];
						while ((readBytes=inputStream.read(sBuffer))!=-1){
							content.write(sBuffer, 0, readBytes);
						}
						String result = new String(content.toByteArray());
						processResponse(msgType,result);
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					client.getConnectionManager().shutdown();
				}
				
			}
			
		});
		t.start();
		
	}
	public String uploadFile(String actionUrl, String name, InputStream fInput) {
		StringBuffer b = new StringBuffer();
		String newName = "image.jpg";
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + name
					+ "\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);

			/* 取得文件的FileInputStream */
			BufferedInputStream fStream = new BufferedInputStream(fInput);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			/* close streams */
			fStream.close();
			ds.flush();

			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			/* 关闭DataOutputStream */
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b.toString().trim();
	}
	public String uploadFile(String actionUrl, String name, byte[] bytes) {
		String newName = "upload.jpg";
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		StringBuffer b = new StringBuffer();
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + name
					+ "\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);

			ds.write(bytes, 0, bytes.length);
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			ds.flush();

			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			/* 关闭DataOutputStream */
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b.toString().trim();
	}

	private void processResponse(final int msgType,final String result){
		try{
			if (mCallback!=null){
				Object resultObj=null;
				if (result.startsWith("[")){
					resultObj=new JSONArray(result);
				}else{
					resultObj = new JSONObject(result);
				}
				mCallback.processMessage(msgType,resultObj);
				mCallback.recordResult(msgType, resultObj);
				if (AppTools.isSuccess(resultObj)){
					JSONObject obj = (JSONObject)resultObj;
					if (!obj.isNull("alertmsg")){
						String alertmsg = obj.getString("alertmsg");
						if (!alertmsg.equals("")){
							//Toast.makeText((Context) mCallback, obj.getString("alertmsg"), Toast.LENGTH_LONG).show();
							mCallback.showFailureMessage(obj.getString("alertmsg"));
						}
					}
				}else{
					JSONObject obj = (JSONObject)resultObj;
					if (!obj.isNull("message")){
						if (!obj.getString("message").equals(""))
							mCallback.showFailureMessage(obj.getString("message"));
							//Toast.makeText((Context) mCallback, obj.getString("message"), Toast.LENGTH_LONG).show();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void dispose(){
		mCallback = null;
		
	}
	
	public interface IHttpCallback{
		void processMessage(final int msgType,final Object result);
		void recordResult(final int msgType,final Object result);
		void showFailureMessage(String msg);
	}
}
