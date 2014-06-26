package cn.count.easydrive366.photo;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;
import cn.count.easydrive366.R;
import cn.count.easydrive366.baidumap.ShowLocationActivity.MyLocationListener;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;

public class TakePhotoActivity extends BaseHttpActivity {
	private static int WIDTH = 768;
	private static int HEIGHT = 1024;
	protected DisplayMetrics dm;
	protected static final String imgPath = Environment
			.getExternalStorageDirectory().getPath() + "/DCIM/Camera";
	protected static final File PHOTO_DIR = new File(imgPath);

	/* 用来标识请求照相功能的activity */
	protected static final int CAMERA_WITH_DATA = 3023;
	/* 用来标识请求gallery的activity */
	protected static final int PHOTO_PICKED_WITH_DATA = 3021;
	protected static final int imageScale = 3;
	private ImageView imageView;
	protected Bitmap photo;
	private double latitude = 0;
	private double longitude = 0;
	private String FileName;
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private Bitmap bm;// 需要旋转的图片资源Bitmap
	private float scaleW = 1;// 横向缩放系数，1表示不变
	private float scaleH = 1;// 纵向缩放系数，1表示不变
	private float curDegrees = 90;// 当前旋转度数
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		setContentView(R.layout.modules_takephoto_activity);

		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		this.setupRightButtonWithText("拍照");
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imageView = (ImageView) findViewById(R.id.image);

		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.setAK(AppSettings.BAIDUMAPKEY);
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.d("LocSDK4", "locClient is null or not started");

	}

	@Override
	protected void onRightButtonPress() {
		doPickPhotoAction();
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
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
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
			// imageView.setImageURI(uri);
			imageView.setImageBitmap(getSmallBitmap(filename));
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

			imageView.setImageBitmap(getSmallBitmap(filename));
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
					"%supload/do_upload?userid=%d&y=%f&x=%f&from=android",
					AppSettings.ServerUrl, AppSettings.userid, this.latitude,
					this.longitude);
			String result = getHttpClient().uploadFile(actionUrl, "userfile",
					baos.toByteArray());
			JSONObject json = new JSONObject(result);
			this.showMessage("上传成功！", null);
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

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			latitude = location.getLatitude();
			longitude = location.getLongitude();

			mLocationClient.stop();
			mLocationClient.unRegisterLocationListener(myListener);

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}

	}

}
