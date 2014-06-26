package cn.count.easydrive366.baidumap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.count.easydrive366.R;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.AppTools;
import cn.count.easydriver366.base.BaseHttpActivity;

public class ShowLocationActivity extends BaseHttpActivity {
	private BMapManager _mapManager = null;
	private MapView _mapView = null;
	public LocationClient mLocationClient = null;
	private LocationData locData = null;
	private MyLocationOverlay myLocationOverlay = null;
	public BDLocationListener myListener = new MyLocationListener();
	private boolean isRequest=false;
	private boolean isFirstLoc = false;
	private List<ShopLocation> items= new ArrayList<ShopLocation>();
	private Button button =null;
	private MapView.LayoutParams layoutParam = null;
	private boolean _isFull;
	private int _index=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init map step1
		_mapManager = new BMapManager(getApplication());
		_mapManager.init(AppSettings.BAIDUMAPKEY, null);

		setContentView(R.layout.modules_map_showlocation_activity);
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		
		
		
		
		// init map step2
		_mapView = (MapView) findViewById(R.id.bmapsView);
		_mapView.setBuiltInZoomControls(true);
		MapController mapController = _mapView.getController();
		GeoPoint point = new GeoPoint((int) (37.10052 * 1E6),
				(int) (120.404586 * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mapController.setCenter(point);// 设置地图中心点
		mapController.setZoom(16);// 设置地图zoom级别
		
		
		button = new Button(this);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				popupClick();
				
			}
			
		});
		
		locData = new LocationData();
		myLocationOverlay = new MyLocationOverlay(_mapView);
		myLocationOverlay.setData(locData);
		_mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		_mapView.refresh();
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		mLocationClient.setAK(AppSettings.BAIDUMAPKEY);
		mLocationClient.registerLocationListener( myListener );    //注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);//禁止启用缓存定位
		option.setPoiNumber(5);	//最多返回POI个数	
		option.setPoiDistance(1000); //poi查询距离		
		option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息		
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else 
			Log.d("LocSDK3", "locClient is null or not started");
		
		_isFull = this.getIntent().getBooleanExtra("isFull", true);
		if (_isFull){
			this.setupRightButtonWithText("搜索");
		}else{
			this.setRightButtonInVisible();
			String json = getIntent().getStringExtra("shoplist");
			_index = getIntent().getIntExtra("index", -1);
			try{
				this.processMessage(1, new JSONObject(json));
			}catch(Exception e){
				log(e);
			}
		}
	}
	
	

	@Override
	protected void onRightButtonPress() {
		//goto search
		Intent intent = new Intent(this,SearchShopActivity.class);
		startActivity(intent);
		finish();
	}



	@Override
	protected void onDestroy() {
		_mapView.destroy();
		if (_mapManager != null) {
			_mapManager.destroy();
			_mapManager = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		_mapView.onPause();
		if (_mapManager != null) {
			_mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				_mapView.onResume();
				if (_mapManager != null) {
					_mapManager.start();
				}
				
			}});
		
		super.onResume();
	}
	private void popupClick(){
		_mapView.removeView(button);
		int index = (Integer) button.getTag();
		ShopLocation sl = items.get(index);
		Intent intent =new Intent(this,ShowShopInformationActivity.class);
		intent.putExtra("name", sl.name);
		intent.putExtra("address", sl.address);
		intent.putExtra("phone", sl.phone);
		intent.putExtra("description", sl.description);
		intent.putExtra("latitude", sl.latitude);
		intent.putExtra("longtitude", sl.longtitude);
		startActivity(intent);
		finish();
	}
	private void getBusiness(LocationData locationData){
		get(AppSettings.get_business(locationData.latitude, locationData.longitude, "09"),1);
	}
	
	@Override
	public void processMessage(int msgType, final Object result) {
		switch(msgType){
		case 1:
			if (AppTools.isSuccess(result)){
				try{
					items.clear();
					JSONArray list = ((JSONObject)result).getJSONArray("result");
					for(int index=0;index<list.length();index++){
						JSONObject item = list.getJSONObject(index);
						ShopLocation sl = new ShopLocation(item);
						items.add(sl);
					}
					this.runOnUiThread(new Runnable(){

						@Override
						public void run() {
							update_shops();
							
						}});
				}catch(Exception e){
					log(e);
				}
			}
			break;
		default:
				break;
		}
	}
	private void update_shops()
	{
		ShopOverlay overlay = new ShopOverlay(this.getResources().getDrawable(R.drawable.icon_gcoding),_mapView);
		for(int i=0;i<items.size();i++){
			if (_index>-1){
				if (i==_index){
					ShopLocation sl = items.get(i);
					OverlayItem item = new OverlayItem(sl.point(),sl.name,sl.description);
					overlay.addItem(item);
					_mapView.getController().setCenter(sl.point());	
					_mapView.removeView(button);
					button.setText(item.getTitle()+"\r\n"+item.getSnippet());
					button.setTag(_index);
					GeoPoint pt = item.getPoint();//new GeoPoint ((int)(mLat5*1E6),(int)(mLon5*1E6));
			         //创建布局参数
			         layoutParam  = new MapView.LayoutParams(
			               //控件宽,继承自ViewGroup.LayoutParams
			               MapView.LayoutParams.WRAP_CONTENT,
			                //控件高,继承自ViewGroup.LayoutParams
			               MapView.LayoutParams.WRAP_CONTENT,
			               //使控件固定在某个地理位置
			                pt,
			                0,
			                -64,
			               //控件对齐方式
			                 MapView.LayoutParams.BOTTOM_CENTER);
			         //添加View到MapView中
			         _mapView.addView(button,layoutParam);
				}
			}else{
				ShopLocation sl = items.get(i);
				OverlayItem item = new OverlayItem(sl.point(),sl.name,sl.description);
				overlay.addItem(item);
				if (!_isFull && i==0){
					_mapView.getController().setCenter(sl.point());
				}
			}
		}
		_mapView.getOverlays().add(overlay);
		_mapView.refresh();
		
				
	}
	public class ShopOverlay extends ItemizedOverlay<OverlayItem>{

		public ShopOverlay(Drawable mark, MapView mapView) {
			super(mark, mapView);			
		}
		
		@Override
		protected boolean onTap(int index) {
			OverlayItem item = getItem(index);
			_mapView.removeView(button);
			button.setText(item.getTitle()+"\r\n"+item.getSnippet());
			button.setTag(index);
			GeoPoint pt = item.getPoint();//new GeoPoint ((int)(mLat5*1E6),(int)(mLon5*1E6));
	         //创建布局参数
	         layoutParam  = new MapView.LayoutParams(
	               //控件宽,继承自ViewGroup.LayoutParams
	               MapView.LayoutParams.WRAP_CONTENT,
	                //控件高,继承自ViewGroup.LayoutParams
	               MapView.LayoutParams.WRAP_CONTENT,
	               //使控件固定在某个地理位置
	                pt,
	                0,
	                -64,
	               //控件对齐方式
	                 MapView.LayoutParams.BOTTOM_CENTER);
	         //添加View到MapView中
	         _mapView.addView(button,layoutParam);
			return super.onTap(index);
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mapView) {
			
			_mapView.removeView(button);
			return super.onTap(pt, mapView);
		}
		
		
	}
	public class ShopLocation
	{
		public String code;
		public String name;
		public String address;
		public String phone;
		public String description;
		public double latitude;
		public double longtitude;
		public ShopLocation(JSONObject item){
			try{
				latitude = item.getDouble("y");
				longtitude = item.getDouble("x");
				code = item.optString("code");
				name = item.optString("name");
				address = item.optString("address");
				phone = item.optString("phone");
				description = item.optString("description");
				
			}catch(Exception e){
				log(e);
			}
		}
		public GeoPoint point(){
			return new GeoPoint((int)(latitude*1e6),(int)(longtitude*1e6));
		}
	}
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			/*
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			} 
	 
			Log.d("EasyDrive366", sb.toString());
			*/
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			myLocationOverlay.setData(locData);
			_mapView.refresh();
			if (!isFirstLoc){
				mLocationClient.stop();
				mLocationClient.unRegisterLocationListener(myListener);
				if (_isFull){
					_mapView.getController().animateTo(new GeoPoint((int)(locData.latitude*1e6),(int)(locData.longitude*1e6)));					
					getBusiness(locData);	
				}else{
					_mapView.getController().animateTo(new GeoPoint((int)(locData.latitude*1e6),(int)(locData.longitude*1e6)));	
				}
				isFirstLoc = true;
				
			}
			
		}
	
		public void onReceivePoi(BDLocation poiLocation) {
				if (poiLocation == null){
					return ;
				}
				StringBuffer sb = new StringBuffer(256);
				sb.append("Poi time : ");
				sb.append(poiLocation.getTime());
				sb.append("\nerror code : ");
				sb.append(poiLocation.getLocType());
				sb.append("\nlatitude : ");
				sb.append(poiLocation.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(poiLocation.getLongitude());
				sb.append("\nradius : ");
				sb.append(poiLocation.getRadius());
				if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
					sb.append("\naddr : ");
					sb.append(poiLocation.getAddrStr());
				} 
				if(poiLocation.hasPoi()){
					sb.append("\nPoi:");
					sb.append(poiLocation.getPoi());
				}else{				
					sb.append("noPoi information");
				}
				Log.d("EasyDrive366", sb.toString());
			}

	}
}

