package cn.count.easydriver366.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public abstract class HttpExecuteGetTask extends
		AsyncTask<String, Void, String> {
	private static final String DEBUG_TAG = "Http.Get";

	@Override
	protected String doInBackground(String... urls) {
		try {
			return httpGetUrl(urls[0]);
		} catch (Exception e) {

			return "Unable to retrieve the url. URL may be invalid. return:"
					+ e.getMessage();
		}
	}
	protected String getServerUrl(){
		return AppSettings.ServerUrl;
	}
	@Override
	abstract protected void onPostExecute(String result);
	private static String session_id="";
	private String httpGetUrl(final String urlString) {
		try {

			String httpUrl = getServerUrl() + urlString;
			if (AppSettings.task_id>0){
				httpUrl = String.format("%s&taskid=%d", httpUrl,AppSettings.task_id);
				AppSettings.task_id =0;
			}
			if (AppSettings.isOutputDebug)
				Log.e("Http begin", httpUrl);
			
			HttpClient client = new DefaultHttpClient();
			
			HttpGet get = new HttpGet(httpUrl);
			if (session_id!=null && !session_id.isEmpty()){
				get.setHeader("Cookie", "ci_session="+session_id);
			}
			HttpResponse response = client.execute(get);
			int status_code = response.getStatusLine().getStatusCode();
			if (status_code >= 300) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			// return EntityUtils.toString(entity);
			String result = retrieveInputStream(entity);
			//session_id="";
			try{
				CookieStore cs = ((AbstractHttpClient) client).getCookieStore();
				List<Cookie> cookies = cs.getCookies();
				for(int i=0;i<cookies.size();i++){
					if ("ci_session".equals(cookies.get(i).getName())){
						session_id = cookies.get(i).getValue();
					}
				}
			}catch(Exception e){
				
			}
			if (AppSettings.isOutputDebug)
				Log.e("Http end", result);
			return result;
			/*
			 * InputStream inputStream = entity.getContent();
			 * ByteArrayOutputStream content = new ByteArrayOutputStream(); int
			 * readBytes =0; byte[] sBuffer = new byte[512]; while
			 * ((readBytes=inputStream.read(sBuffer))!=-1){
			 * content.write(sBuffer, 0, readBytes); } String result = new
			 * String(content.toByteArray()); return result;
			 */
		} catch (Exception e) {
			if (e!=null)
				AppSettings.log(e.getMessage());
		}
		return null;
	}

	protected String retrieveInputStream(HttpEntity httpEntity) {

		int length = (int) httpEntity.getContentLength();
		// the number of bytes of the content, or a negative number if unknown.
		// If the content length is known but exceeds Long.MAX_VALUE, a negative
		// number is returned.
		// length==-1，下面这句报错，println needs a message
		if (length < 0)
			length = 10000;
		StringBuffer stringBuffer = new StringBuffer(length);
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					httpEntity.getContent(), HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(DEBUG_TAG, e.getMessage());
		} catch (IllegalStateException e) {
			Log.e(DEBUG_TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(DEBUG_TAG, e.getMessage());
		}
		return stringBuffer.toString();
	}

	private String httpGetUrl_old(final String urlString) throws IOException {
		InputStream is = null;
		try {
			URL url = new URL(AppSettings.ServerUrl + urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(1000 * AppSettings.READ_TIMEOUT);
			conn.setConnectTimeout(1000 * AppSettings.CONNECT_TIMEOUT);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			int response = conn.getResponseCode();
			is = conn.getInputStream();
			String result = AppSettings.readInputStream(is);
			AppSettings.log(result);
			return result;
		} finally {
			if (is != null)
				is.close();

		}
	}

}
