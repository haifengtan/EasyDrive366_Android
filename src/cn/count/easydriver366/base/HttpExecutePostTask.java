package cn.count.easydriver366.base;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class HttpExecutePostTask extends AsyncTask<String, Void, String> {
	private static final String DEBUG_TAG = "Http.Post";	
	@Override
	protected String doInBackground(String... urls) {
		try{
			return httpPostUrl(urls[0],urls[1]);
		}catch(Exception e){
			
			return "Unable to retrieve the url. URL may be invalid. return:"+e.getMessage();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		
	}
	private String httpPostUrl(final String urlString,final String parameters) throws IOException
	{
		InputStream is = null;
		try
		{
			URL url = new URL(AppSettings.ServerUrl+urlString);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setReadTimeout(1000*AppSettings.READ_TIMEOUT);
			conn.setConnectTimeout(1000*AppSettings.CONNECT_TIMEOUT);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
			writer.write(parameters);
			conn.connect();
			int response = conn.getResponseCode();
			
			is = conn.getInputStream();
			String result = AppSettings.readInputStream(is);
			return result;
		}
		finally
		{
			if (is!=null)
				is.close();
			
		}
	}
	
}
