package cn.count.easydrive366.baidumap;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import cn.count.easydrive366.R;

import cn.count.easydrive366.baidumap.ShowLocationActivity.ShopLocation;
import cn.count.easydrive366.baidumap.ShowLocationActivity.ShopOverlay;
import cn.count.easydriver366.base.AppSettings;
import cn.count.easydriver366.base.BaseHttpActivity;

public class BaiduMapNavigationActivity extends BaseHttpActivity {
	private BMapManager _mapManager = null;
	private MapView _mapView = null;
	public LocationClient mLocationClient = null;
	private LocationData locData = null;
	private MyLocationOverlay myLocationOverlay = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	private boolean isFirstLoc = false;
	
	private Button button =null;
	private MapView.LayoutParams layoutParam = null;
	private double latitude;
	private double longtitude;
	private MKSearch _search;
	private RouteOverlay routeOverlay;
	private MKRoute route;
	private int nodeIndex = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init map step1
		_mapManager = new BMapManager(getApplication());
		_mapManager.init(AppSettings.BAIDUMAPKEY, null);

		setContentView(R.layout.modules_map_showlocation_activity);
		
		this.setRightButtonInVisible(); 
		this.setupLeftButton();
		this.setupPhoneButtonInVisible();
		 
		latitude = getIntent().getDoubleExtra("latitude", 0);
		longtitude = getIntent().getDoubleExtra("longtitude", 0);
		
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
				_mapView.removeView(button);
				
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
		addOverlay();
	}
	private void addOverlay()
	{
		ShopOverlay overlay = new ShopOverlay(this.getResources().getDrawable(R.drawable.icon_gcoding),_mapView);
		String name = getIntent().getStringExtra("name");
		String description = getIntent().getStringExtra("description");
		
		GeoPoint p = new GeoPoint((int)(latitude*1e6),(int)(longtitude*1e6));
		OverlayItem item = new OverlayItem(p,name,description);
		overlay.addItem(item);
		_mapView.getOverlays().add(overlay);
		_mapView.refresh();
		
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
		_mapView.onResume();
		if (_mapManager != null) {
			_mapManager.start();
		}
		super.onResume();
	}
	private void initSearch()
	{
		_search = new MKSearch();
		_search.init(_mapManager, new MKSearchListener(){

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				//起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(BaiduMapNavigationActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
			
				createRouteMap(res);
			    
				
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub
				
			}});
	}
	private void createRouteMap(final MKDrivingRouteResult res){
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				routeOverlay = new RouteOverlay(BaiduMapNavigationActivity.this, _mapView);
			    // 此处仅展示一个方案作为示例
			    routeOverlay.setData(res.getPlan(0).getRoute(0));
			    //清除其他图层
			    _mapView.getOverlays().clear();
			    //添加路线图层
			    _mapView.getOverlays().add(routeOverlay);
			    //执行刷新使生效
			    _mapView.refresh();
			    // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			    _mapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
			    //移动地图到起点
			    _mapView.getController().animateTo(res.getStart().pt);
			    //将路线数据保存给全局变量
			    route = res.getPlan(0).getRoute(0);
			    //重置路线节点索引，节点浏览时使用
			    nodeIndex = -1;
				
			}
			
		});
	}
	private void calclateRoute(LocationData sourcePoint)
	{
		if (_search==null){
			initSearch();
		}
		route = null;
		routeOverlay = null;
		MKPlanNode startNode = new MKPlanNode();
		startNode.pt = new GeoPoint((int)(sourcePoint.latitude*1e6),(int)(sourcePoint.longitude*1e6));
		MKPlanNode endNode = new MKPlanNode();
		endNode.pt = new GeoPoint((int)(latitude*1e6),(int)(longtitude*1e6));
		_search.drivingSearch("青岛", startNode, "青岛", endNode);
		
	}
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			myLocationOverlay.setData(locData);
			_mapView.refresh();
			if (!isFirstLoc){
				mLocationClient.stop();
				mLocationClient.unRegisterLocationListener(myListener);
				_mapView.getController().animateTo(new GeoPoint((int)(locData.latitude*1e6),(int)(locData.longitude*1e6)));
				isFirstLoc = true;
				calclateRoute(locData);
				
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
	                -32,
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
}
