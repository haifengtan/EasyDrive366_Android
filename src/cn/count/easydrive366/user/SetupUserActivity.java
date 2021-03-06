package cn.count.easydrive366.user;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import cn.count.easydrive366.BrowserActivity;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;
import cn.count.easydriver366.base.HttpExecuteGetTask;

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
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class SetupUserActivity extends BaseHttpActivity {
	private TextView _txtUsername;
	private TextView _txtBound;
	private TextView _txtExperience;
	private EditText _edtName;
	private EditText _edtSign;
	private ImageView _imgAvatar;
	private String _pictureUrl;
	private String exp_url;
	
	
	private static int WIDTH = 640;
	private static int HEIGHT = 480;
	protected DisplayMetrics dm;
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
		setContentView(R.layout.modules_setupuser_activity);
		this.setupLeftButton();
		this.setupRightButtonWithText("保存");
		setupView();
		load_data();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		/*
		 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mPullRefreshScrollView.getWindowToken(), 0);
		 
		 */
	}
	
	private void setupView(){
		_txtUsername = (TextView)findViewById(R.id.txt_username);
		_txtBound = (TextView)findViewById(R.id.txt_bound);
		_txtExperience =  (TextView)findViewById(R.id.txt_experience);
		_edtName =  (EditText)findViewById(R.id.edt_name);
		_edtSign =  (EditText)findViewById(R.id.edt_sign);
		_imgAvatar = (ImageView)findViewById(R.id.img_avatar);
		_imgAvatar.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				clickOnSmallAvatar();
				
			}});
		findViewById(R.id.layout_avatar).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				uploadAvatar();
				
			}});
		findViewById(R.id.layout_bounds).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gotoBounds();
				
			}});
		findViewById(R.id.layout_experience).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				gotoExperience();
				
			}});
	}
	
	
	@Override
	protected void onRightButtonPress() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(_edtName.getWindowToken(), 0);
		doSave();
	}

	private void doSave(){
		String url;
		try {
			url = String.format("bound/save_user_info?userid=%d&nickname=%s&signature=%s",
					
					AppSettings.userid,
					URLEncoder.encode(_edtName.getText().toString(),"utf-8"),
					URLEncoder.encode(_edtSign.getText().toString(),"utf-8"));
			beginHttp();
			new HttpExecuteGetTask(){

				@Override
				protected void onPostExecute(String result) {
					endHttp();
					//AppSettings.isSuccessJSON(result,SetupUserActivity.this);
					if (AppSettings.isSuccessJSON(result, SetupUserActivity.this)){
						SetupUserActivity.this.setResult(Activity.RESULT_OK);
						finish();
						
					}
				}}.execute(url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	private void load_data(){
		String url = String.format("bound/get_user_info?userid=%d", AppSettings.userid);
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				processData(result);
			}}.execute(url);
	}
	private void processData(final String result){
		JSONObject json = AppSettings.getSuccessJSON(result,this);
		if (json==null)
			return;
		try{
			_txtUsername.setText(json.getString("username"));
			_txtBound.setText(json.getString("bound"));
			_txtExperience.setText(String.format("%s",json.getString("exp_label")));
			_edtName.setText(json.getString("nickname"));
			_edtSign.setText(json.getString("signature"));
			_pictureUrl = json.getString("photourl");
			if (_pictureUrl.startsWith("http://")){
				this.loadImageFromUrl(_imgAvatar, _pictureUrl);
			}else{
				// load local image
			}
			exp_url = json.getString("exp_url");
		}catch(Exception e){
			log(e);
		}
	}
	private void clickOnSmallAvatar(){
		String url = String.format("bound/get_user_photo?userid=%d", AppSettings.userid);
		beginHttp();
		new HttpExecuteGetTask(){

			@Override
			protected void onPostExecute(String result) {
				endHttp();
				showOriginal(result);
			}}.execute(url);
		
		
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
	private void uploadAvatar(){
		this.doPickPhotoAction();
	}
	private void gotoBounds(){
		Intent intent = new Intent(this,BoundActivity.class);
		startActivity(intent);
	}
	private void gotoExperience(){
		Intent intent = new Intent(this,BrowserActivity.class);
		intent.putExtra("url", exp_url);
		startActivity(intent);
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
					"%supload/upload_user_photo?userid=%d",
					AppSettings.ServerUrl, AppSettings.userid);
			String result = getHttpClient().uploadFile(actionUrl, "userfile",
					baos.toByteArray());
			//JSONObject json = new JSONObject(result);
			if (AppSettings.isSuccessJSON(result, this)){
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
