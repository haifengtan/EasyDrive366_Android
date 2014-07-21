package cn.count.easydriver366.base;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class GetXML {
	public static int reponseCode;
	static Context cn;

	/**
	 * 网络数据请求
	 * 
	 * @param url
	 *            请求url
	 * @param params
	 *            参数
	 * @return
	 */
	public static String doPost(String url, List<NameValuePair> params) {
		HttpPost httpPost = new HttpPost(url);
		String result = null;
		HttpClient client = new DefaultHttpClient();

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpPost);
			// 请求超时
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity());// 得到请求数据的返回值
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String doGet(String GET_URL, String str) {
		HttpGet get = null; // 用get方式联网
		HttpResponse reponse = null; // 等待应答
		GET_URL = GET_URL.replace(" ", "%20");
		GET_URL = GET_URL.replace("\\", "%5C");
//		System.out.println("url="+GET_URL);
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			get = new HttpGet(GET_URL);
			reponse = httpClient.execute(get);
			reponseCode = reponse.getStatusLine().getStatusCode();
			if (reponse.getStatusLine().getStatusCode() == 200) {
				byte[] b = EntityUtils.toByteArray(reponse.getEntity());
				String returnMessage = new String(b, str);
				return returnMessage;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (get != null) {
				get.abort();
			}
		}
		return null;
	}

	public static String getServer(String url) {
		String result = "";
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpClient hc = new DefaultHttpClient();

			HttpResponse response = hc.execute(httpGet);

			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("res", "返回结果-----" + result);
		return result;
	}

}
