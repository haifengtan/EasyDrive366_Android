package cn.count.easydriver366.base;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import cn.count.easydriver366.service.DownloadUtils;

import cn.count.easydrive366.R;
import cn.count.easydrive366.SettingsActivity;
import cn.count.easydrive366.signup.Step1Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

/**
 * 检查版本更新
 * @author Administrator
 *
 */
public class CheckUpdate implements HttpClient.IHttpCallback {
	private Context _context;
	private HttpClient _http;
	private boolean _isSettings;
	private DownloadManager downloadManager;
	private long dmId;
	public CheckUpdate(Context context,boolean isSettings){
		_context = context;
		_isSettings = isSettings;
		_http = new HttpClient(this);
		String url = String.format("api/get_ver?ver=%s&device=android&userid=%d", AppSettings.version,AppSettings.userid);
		_http.requestServer(url, 1);
	}

	@Override
	public void processMessage(int msgType, Object result) {
		final Object json = result;
		if (AppTools.isSuccess(result)){
			Activity activity= (Activity)_context;
			activity.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					check(json);

			}});
		}

	}
	private void needsetup(final String message){

		new AlertDialog.Builder(_context).setTitle(_context.getResources().getString(R.string.hint))
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(message).setPositiveButton(_context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(_context,Step1Activity.class);
				_context.startActivity(intent);

			}
		})
		.setNegativeButton(_context.getResources().getString(R.string.cancel), null).show();
	}
	private void check(final Object result){
		try{
			JSONObject json = ((JSONObject)result).getJSONObject("result");
			final boolean need_set =json.getBoolean("needset");
			final String needsetmsg= json.getString("needsetmsg");


			if (!json.getString("ver").equals(AppSettings.version)){

				final String url = json.getString("android");
				new AlertDialog.Builder(_context).setTitle(_context.getResources().getString(R.string.hint))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(json.getString("msg")).setPositiveButton(_context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						/*
						Intent intent = null;
						intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
						_context.startActivity(intent);
						*/
						/*
						File updateApk = downLoadFile(url);
						openFile(updateApk);
						*/

						//download("http://192.168.1.102/EasyDrive366_1_03.apk");
						download(url);
						if (need_set){
							needsetup(needsetmsg);
						}
					}
				})
				.setNegativeButton(_context.getResources().getString(R.string.cancel), new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (need_set){
							needsetup(needsetmsg);
						}

					}}).show();
			}else{
				if (_isSettings){
					new AlertDialog.Builder(_context).setMessage(json.getString("msg")).show();
				}else{
					if (need_set){
						needsetup(needsetmsg);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}


	}
	@Override
	public void recordResult(int msgType, Object result) {
		// TODO Auto-generated method stub

	}
	private void download(final String url){
		downloadManager = (DownloadManager) _context.getSystemService(android.content.Context.DOWNLOAD_SERVICE);
		Uri resource = Uri.parse(url);   
        DownloadManager.Request request = new DownloadManager.Request(resource);   
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);   
        request.setAllowedOverRoaming(false);   
        //设置文件类型  
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();  
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));  
        request.setMimeType(mimeString);  
        //在通知栏中显示   
        request.setShowRunningNotification(true);  
        request.setVisibleInDownloadsUi(true);  
        //sdcard的目录下的download文件夹  
        request.setDestinationInExternalPublicDir("/download/", "easydrive366.apk");  
        request.setTitle(AppSettings.AppTile);   
        dmId = downloadManager.enqueue(request);
        _context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));  
	}
	private BroadcastReceiver receiver = new BroadcastReceiver() {   
        @Override   
        public void onReceive(Context context, Intent intent) {   
            //这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听  
            Log.v("intent", ""+intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)); 
            Cursor c = downloadManager.query(new DownloadManager.Query().setFilterById(dmId));
            if (c.moveToFirst()){
            	
            	//final String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            	int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                	
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    File file = new File(Uri.parse(uriString).getPath());
                    openFile(file);

                } else {
                    Toast.makeText(_context,"下载更新失败，请卸载之后安装最新版本。",Toast.LENGTH_SHORT).show();
                }
                /*
            	final File file = new File(filename);
            	openFile(file);
            	*/
            }
            //queryDownloadStatus();   
            _context.unregisterReceiver(receiver);
        }   
    };   
    private void queryDownloadStatus() {   
        DownloadManager.Query query = new DownloadManager.Query();   
        query.setFilterById(dmId);   
        Cursor c = downloadManager.query(query);   
        if(c.moveToFirst()) {   
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));   
            switch(status) {   
            case DownloadManager.STATUS_PAUSED:   
                Log.v("down", "STATUS_PAUSED");  
            case DownloadManager.STATUS_PENDING:   
                Log.v("down", "STATUS_PENDING");  
            case DownloadManager.STATUS_RUNNING:   
                //正在下载，不做任何事情  
                Log.v("down", "STATUS_RUNNING");  
                break;   
            case DownloadManager.STATUS_SUCCESSFUL:   
                //完成  
                Log.v("down", "下载完成");  
               
                break;   
            case DownloadManager.STATUS_FAILED:   
                //清除已下载的内容，重新下载  
                Log.v("down", "STATUS_FAILED");  
                downloadManager.remove(dmId);   
                  
                break;   
            }   
        }  
    }  
	protected File downLoadFile(String httpUrl) {

        // TODO Auto-generated method stub
        final String fileName = "updata.apk";
        String sdpath = Environment.getExternalStorageDirectory() + "/";
        String mSavePath = sdpath + "download/";
        File tmpFile = new File(mSavePath);
        if (!tmpFile.exists()) {
                tmpFile.mkdir();
        }
        final File file = new File(mSavePath + fileName);

        try {
        	 	DownloadUtils.download(httpUrl,file,false,null);
        		/*
                URL url = new URL(httpUrl);
                try {
                        HttpURLConnection conn = (HttpURLConnection) url
                                        .openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(5*1000);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buf = new byte[256];
                        conn.connect();
                        double count = 0;
                        if (conn.getResponseCode() >= 400) {
                                Toast.makeText(_context, "连接超时", Toast.LENGTH_SHORT)
                                                .show();
                        } else {
                                while (count <= 100) {
                                        if (is != null) {
                                                int numRead = is.read(buf);
                                                if (numRead <= 0) {
                                                        break;
                                                } else {
                                                        fos.write(buf, 0, numRead);
                                                }

                                        } else {
                                                break;
                                        }

                                }
                        }

                        conn.disconnect();
                        fos.close();
                        is.close();
                        
                } catch (IOException e) {
                        // TODO Auto-generated catch block

                        e.printStackTrace();
                }
                */
        } catch (Exception e) {
                

                e.printStackTrace();
        }

        return file;
	}
	//打开APK程序代码

	private void openFile(File file) {
        // TODO Auto-generated method stub
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
        _context.startActivity(intent);
	}
	@Override
	public void showFailureMessage(String msg) {
		//Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

	}


}
