package cn.count.easydrive366.insurance;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.R;
import cn.count.easydrive366.components.TakePhotoItem;
import cn.count.easydrive366.user.ShowPictureDialog;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

public class UploadInsPhotoActivity extends BaseInsurance {
	private String data;
	private TableLayout table;
	private TextView txt_help_title;
	private String url;
	private int _typeid;
	private ImageView _imgAvatar;
	private static int WIDTH = 640;
	private static int HEIGHT = 480;
	protected DisplayMetrics dm;
	private String order_id;
	protected static final String imgPath = Environment
			.getExternalStorageDirectory().getPath() + "/DCIM/Camera";
	protected static final File PHOTO_DIR = new File(imgPath);

	/* 用来标识请求照相功能的activity */
	protected static final int CAMERA_WITH_DATA = 3023;
	/* 用来标识请求gallery的activity */
	protected static final int PHOTO_PICKED_WITH_DATA = 3021;
	protected static final int imageScale = 3;
	
	protected Bitmap photo;
	
	private String FileName;
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private Bitmap bm;// 需要旋转的图片资源Bitmap
	private float scaleW = 1;// 横向缩放系数，1表示不变
	private float scaleH = 1;// 纵向缩放系数，1表示不变
	private float curDegrees = 90;// 当前旋转度数
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		String strVer = android.os.Build.VERSION.RELEASE;
		strVer = strVer.substring(0, 3).trim();
		float fv = Float.valueOf(strVer);
		if (fv > 2.3) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() // 这里可以替换为detectAll()
																			// 就包括了磁盘读写和网络I/O
					.penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
					.penaltyLog() // 打印logcat
					.penaltyDeath().build());
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modules_upload_ins_phone_activity);
		this.setupTitle("在线购买保险", "图片上传");
		this.setupLeftButton();
		this.setupRightButtonWithText("完成");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		data = getIntent().getStringExtra("data");
		
		
		
		order_id = getIntent().getStringExtra("order_id");
		if (order_id!=null && !order_id.isEmpty()){
			String url = String.format("order/order_upload?userid=%d&orderid=%s", AppSettings.userid,order_id);
			beginHttp();
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					endHttp();
					data = result;
					init_view();
					
				}}.execute(url);
		}else{
			this.order_id = getIntent().getStringExtra("o_id");
			init_view();
		}
	}
	
	@Override
	protected void onRightButtonPress() {
		if (stacks!=null){
			try{
				Activity a = stacks.pop();
				while(a!=null){
					
					a.finish();
					a = stacks.pop();
				}
			}catch(Exception e){
				
			}
			
		}
		finish();
		
	}

	
	private void init_view(){
		table = (TableLayout)findViewById(R.id.table_actions);
		txt_help_title = (TextView)findViewById(R.id.txt_help_title);
		findViewById(R.id.layout_help).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				goto_help();
				
			}});
		
		//data  = this.getIntent().getStringExtra("data");
		JSONObject json = AppSettings.getSuccessJSON(data,this);
		if (json!=null){
			try{
				if (!json.isNull("order_id")){
					this.order_id = json.getString("order_id");
				}
				JSONArray helps = json.getJSONArray("help");
				if (helps.length()>0){
					JSONObject help = helps.getJSONObject(0);
					txt_help_title.setText(help.getString("title"));
					url = help.getString("url");
				}
				JSONArray actions = json.getJSONArray("action");
				
				for(int i=0;i<actions.length();i++){
					JSONObject action = actions.getJSONObject(i);
					TableRow tr = new TableRow(this);
					TakePhotoItem tpi = new TakePhotoItem(this,null);
					tpi.target = action;
					int bid=R.drawable.signup_hit_input1;
					if (i>0){
						if (i==actions.length()-1)
							bid=R.drawable.signup_hit_input6;
						else
							bid=R.drawable.signup_hit_input2; 
					}
					tpi.setData(action.getString("title"), action.getString("photourl"),bid);
					tpi.callback = new TakePhotoItem.ICallback() {
						
						@Override
						public void onItemClick(Object obj,ImageView img) {
							onCellItemClick(obj,img);
							
						}
						
						@Override
						public void onImageClick(Object obj) {
							onCellImageClick(obj);
							
						}
					};
					tr.addView(tpi);
					table.addView(tr);
				}
				
			}catch(Exception e){
				log(e);
			}
		}
	}
	private void goto_help(){
		Intent intent = new Intent(this,BrowserActivity.class);
		intent.putExtra("url", url);
		//intent.putExtra("title", this.txt_help_title.getText().toString());
		startActivity(intent);
	}
	private void onCellItemClick(Object obj,ImageView img){
		this._imgAvatar = img;
		try{
			JSONObject json = (JSONObject)obj;
			_typeid = json.getInt("id");
			
			doPickPhotoAction();
		}catch(Exception e){
			log(e);
		}
		
	}
	private void onCellImageClick(Object obj){
		try{
			JSONObject json = (JSONObject)obj;
			String url = String.format("ins/carins_get_photo?userid=%d&typeid=%d", AppSettings.userid,json.getInt("id"));
			beginHttp();
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					endHttp();
					showOriginal(result);
				}}.execute(url);
		}catch(Exception e){
			log(e);
		}
	}
	private void showOriginal(final String result){
		try{
			JSONObject json = new JSONObject(result);
			ShowPictureDialog d = new ShowPictureDialog();
			d.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			Bundle b = new Bundle();
			b.putString("url", json.getString("result"));
			d.setArguments(b);
			d.show(getFragmentManager(), "image");
		}catch(Exception e){
			log(e);
		}
		
	}
	protected boolean checkSoftStage() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) { // 判断是否存在SD卡
			File file = new File(imgPath);
			if (!file.exists()) {
				file.mkdir();
			}
			return true;
		} else {
			new AlertDialog.Builder(this)
					.setMessage(
							"Testing to mobile phone no memory card! Please insert the cell phone memory card and open this application.")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).show();
			return false;
		}
	}

	protected void doPickPhotoAction() {
		Context context = this;

		// Wrap our context to inflate list items using correct theme
		final Context dialogContext = new ContextThemeWrapper(context,
				android.R.style.Theme_Light);
		String cancel = "取消";
		String[] choices;
		choices = new String[2];
		choices[0] = getString(R.string.take_photo); // 拍照
		choices[1] = getString(R.string.pick_photo); // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle(R.string.choose_photo);
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0: {
							if (checkSoftStage()) {// 判断是否有SD卡
								doTakePhoto();// 用户点击了从照相机获取
							} else {
								showMessage(
										"你的手机没有存储卡，无法拍照！",
										null);
							}
							break;

						}
						case 1:
							doPickPhotoFromGallery();// 从相册中去获取
							break;
						}
					}
				});
		builder.setNegativeButton(cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		builder.create().show();
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			if (!PHOTO_DIR.exists()) {
				boolean iscreat = PHOTO_DIR.mkdirs();// 创建照片的存储目录
				Log.e(AppSettings.AppTile, "" + iscreat);
			}
			FileName = System.currentTimeMillis() + ".jpg";
			mCurrentPhotoFile = new File(PHOTO_DIR, FileName);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mCurrentPhotoFile));

			// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			showMessage(getText(R.string.photoPickerNotFoundText).toString(),
					null);
		}
	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			/*
			 * intent.putExtra("crop", "true"); intent.putExtra("aspectX", 1);
			 * intent.putExtra("aspectY", 1); intent.putExtra("outputX",
			 * dm.widthPixels / imageScale - 6); intent.putExtra("outputY",
			 * dm.widthPixels / imageScale - 6);
			 * 
			 * intent.putExtra("return-data", true);
			 */
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			showMessage(getText(R.string.sysError).toString(), null);
		}
	}

	/**
	 * 销毁图片文件
	 */
	protected void destoryBimap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	public String getPath(Uri uri) {
		/*
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
		*/
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
	    if (cursor == null) { // Source is Dropbox or other similar local file path
	        return uri.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        return cursor.getString(idx); 
	    }
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		destoryBimap(photo);
		String filename = null;
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
			/*
			 * photo = data.getParcelableExtra("data"); if (photo.getWidth() <
			 * photo.getHeight()) { Matrix matrix = new Matrix();
			 * matrix.reset(); matrix.setRotate(90); photo =
			 * Bitmap.createBitmap(photo, 0,0, photo.getWidth(),
			 * photo.getHeight(),matrix, true); }
			 */
			
			Uri uri = data.getData();
			filename = getPath(uri);
			
			
			
			_imgAvatar.setImageBitmap(getSmallBitmap(filename));
			break;
		}
		case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			/*
			 * Bundle bundle = data.getExtras(); photo = (Bitmap)
			 * bundle.get("data");
			 */
			File f = new File(PHOTO_DIR, FileName);
			filename = f.getAbsolutePath();
			/*
			 * if (f.exists()) { //
			 * 此外，你还可以使用BitmapFactory的decodeResource方法获得一个Bitmap对象 //
			 * 使用decodeResource方法时传入的是一个 drawable的资源id //
			 * 还有一个decodeStream方法，这个方法传入一个图片文件的输入流即可！ bm =
			 * BitmapFactory.decodeFile(filename); // 设置ImageView的显示图片 //
			 * imageViewPhoto.setImageBitmap(bm); } else { Toast.makeText(this,
			 * "文件不存在！", Toast.LENGTH_SHORT).show(); return; }
			 */

			_imgAvatar.setImageBitmap(getSmallBitmap(filename));
			/*
			 * int bmpW = bm .getWidth(); int bmpH = bm .getHeight(); //
			 * 设置图片放大比例 double scale = 0.2; // 计算出这次要放大的比例 scaleW = ( float)
			 * (scaleW * scale); scaleH = ( float) (scaleH * scale); //
			 * 产生reSize后的Bitmap对象 // 注意这个Matirx是android.graphics包下的那个 Matrix mt
			 * = new Matrix(); mt.postScale( scaleW, scaleH); mt.setRotate(
			 * curDegrees); Bitmap resizeBmp = Bitmap. createBitmap(bm, 0, 0,
			 * bmpW, bmpH, mt, true);
			 * 
			 * imageView.setImageBitmap(resizeBmp);
			 */

			break;
		}
		}
		// photo = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
		sendImageToServer(filename);
	}

	private void sendImageToServer(String filename) {
		photo = getSmallBitmap(filename);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.JPEG, 40, baos);
		try {
			String actionUrl = String.format(
					"%supload/upload_carins?userid=%d&typeid=%d&orderid=%s",
					AppSettings.ServerUrl, AppSettings.userid,_typeid,this.order_id);
			String result = getHttpClient().uploadFile(actionUrl, "userfile",
					baos.toByteArray());
			if (AppSettings.isSuccessJSON(result, this)){
				//JSONObject json = new JSONObject(result);
				this.showMessage("上传成功！", null);
			}
		} catch (Exception e) {

		}
	}

	// 计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, WIDTH, HEIGHT);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, options);
	}
}
