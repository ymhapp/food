package com.example.overapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;

import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.overapp.MyOrientationListener.OnOrientationListener;

import java.util.List;

public class MainActivity extends ActionBarActivity implements CloudListener,
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

	// 存储当前定位信息
	public BDLocation currlocation = null;

	// MARKER
	private InfoWindow mInfoWindow;
	private BitmapDescriptor bitmap;
	private RelativeLayout mMarkerLy;

	// 存储LBS数据库的遍历结果
	List<CloudPoiInfo> poiList2;
	// poi相关
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;

	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;

	//
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Context context;
	// 定位相关
	private LocationClient mLocationClient;
	private MyLocationListener mLocationListener;
	private boolean isFirstIn = true;
	private double mLatitude;
	private double mLongtitude;

	LatLng mCenterlLatLng = new LatLng(mLatitude, mLongtitude);

	// 自定义定位图标
	private BitmapDescriptor mIconLocation;
	private float mCurrentX;
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode mLocationMode;
	private MyOrientationListener myOrientationListener;

	// 地图当前状态
	private MapStatus mMapStatus;
	// 地图将要变化成的状态
	private MapStatusUpdate mMapStatusUpdate;
	// 当前经纬度坐标
	private LatLng mCurrentLatLng;

	// LBS检索相关
	private static final String LTAG = MainActivity.class.getSimpleName();

	private static final List<com.baidu.mapapi.cloud.CloudPoiInfo> poiList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		CloudManager.getInstance().init(MainActivity.this);

		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
		sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		keyWorldsView.setAdapter(sugAdapter);

		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		/**
		 * marker点击事件
		 */
		// 初始化覆盖物
		initOverlay();
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			//点击marker
			public boolean onMarkerClick(final Marker marker) {
				// 获取marker的经纬度
				LatLng position = marker.getPosition();
				double marklat = position.latitude;
				double emarklot = position.longitude;
				//遍历LBS中的marker
				for (CloudPoiInfo cpi : poiList2) {
					if (cpi.latitude == marklat && cpi.longitude == emarklot)
						Toast.makeText(MainActivity.this, cpi.address,
								Toast.LENGTH_LONG).show();
				}
				Button button = new Button(getApplicationContext());
				button.setBackgroundResource(R.drawable.markselector);
				button.setFocusable(true);
				//GO button的监听
				button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Intent intent = new Intent();
						// intent.setClass(MainActivity.this,
						// CloudSearchDemo.class);
						// startActivity(intent);
					}
				});

				OnInfoWindowClickListener oinfolistener = null;
				oinfolistener= new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						// 点击button跳转到导航页面
						Intent intent = new Intent();
						intent.setClass(MainActivity.this,
								Introduce.class);
						// 用Bundle携带数据
						Bundle bundle = new Bundle();
						// 将参数mLatitude传递给Latitude
						bundle.putDouble("Latitude", mLatitude);
						bundle.putDouble("Longtitude", mLongtitude);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				};
				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(button), ll, -47, oinfolistener);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});

		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		keyWorldsView.addTextChangedListener(new TextWatcher() {

												 @Override
												 public void afterTextChanged(Editable arg0) {

												 }

												 @Override
												 public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

												 }

												 @Override
												 public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
													 if (cs.length() <= 0) {
														 return;
													 }
													 String city = ((EditText) findViewById(R.id.city)).getText().toString();
													 /**
													  * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
													  */
													 mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
															 .keyword(cs.toString()).city(city));
												 }
											 }
		);

		// 初始化定位
		initLocation();
	}

	/**
	 * 添加覆盖物
	 */
	private void initOverlay() {

	}

	/**
	 * 搜索附近美食
	 */
	private void searchfood() {

		NearbySearchInfo info = new NearbySearchInfo();
		info.ak = "hA4E4f7L0sumUOLd6mVS7TL0NfF6YC3n";
		info.geoTableId = 140125;
		info.radius = 1500;
		info.location = (currlocation.getLongitude() + "," + currlocation
				.getLatitude());
		CloudManager.getInstance().nearbySearch(info);

	}

	/**
	 * 回到我的位置
	 */
	private void centerToMyLocation() {

		LatLng latLng = new LatLng(mLatitude, mLongtitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

	/**
	 * 进行定位
	 */
	private void initLocation() {

		mLocationClient = new LocationClient(this);
		mLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mLocationListener);

		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		mMapView.removeViewAt(1); // 去掉百度logo

		// 初始化图标
		mIconLocation = BitmapDescriptorFactory
				.fromResource(R.drawable.jiantou);

		myOrientationListener = new MyOrientationListener(context);

		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					public void onOrientationChanged(float x) {
						mCurrentX = x;
					}
				});

	}

	/**
	 * 菜单栏
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			// 普通地图
			case R.id.map_common:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
				break;
			// 卫星地图
			case R.id.map_site:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				break;
			// 交通图
			case R.id.map_traffic:
				if (mBaiduMap.isTrafficEnabled()) {
					mBaiduMap.setTrafficEnabled(false);
					item.setTitle("实时交通(off)");
				} else {
					mBaiduMap.setTrafficEnabled(true);
					item.setTitle("实时交通(on)");
				}
				break;
			// 回到我的位置
			case R.id.map_location:
				centerToMyLocation();
				break;
			// 搜索附近美食
			case R.id.map_food:
				searchfood();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// 开启定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		// 开启方向传感
		myOrientationListener.start();
	}

	protected void onStop() {
		super.onStop();
		// 停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// 停止方向传感
		myOrientationListener.stop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}



	/**
	 * 实现定位接口的方法
	 *
	 * @param
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			// 设置自定义图标
			MyLocationConfiguration config = new MyLocationConfiguration(
					mLocationMode, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);

			// mMapview 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			currlocation = location;

			MyLocationData locData = new MyLocationData.Builder()

					.direction(mCurrentX)//
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			mBaiduMap.setMyLocationData(locData); // 设置定位数据

			// 更新经纬度
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();

			if (isFirstIn) {
				isFirstIn = false;

				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLngZoom(ll, 17);
				// 设置地图中心点以及缩放级别
				// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);

				Toast.makeText(context, location.getAddrStr(),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 将获取到的遍历集合存到str
	 */

	public String getAddress(List<CloudPoiInfo> poiList) {
		String str = null;
		for (CloudPoiInfo cloudPoiInfo : poiList) {
			System.out.println(cloudPoiInfo.address);
			str += cloudPoiInfo.address;
		}
		return str;
	}

	/**
	 * 实现LBS搜索的两个方法，获取回调信息
	 *
	 * @param
	 */
	@Override
	public void onGetDetailSearchResult(DetailSearchResult arg0, int arg1) {

	}

	@Override
	public void onGetSearchResult(CloudSearchResult result, int error) {

		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {
			Log.d(LTAG,
					"onGetSearchResult, result length: "
							+ result.poiList.size());
			mBaiduMap.clear();
			BitmapDescriptor bd = BitmapDescriptorFactory
					.fromResource(R.drawable.lbs_mark);
			LatLng ll;
			LatLngBounds.Builder builder = new Builder();

			// 将遍历的结果传给poiList2
			poiList2 = result.poiList;
			for (CloudPoiInfo info : result.poiList) {
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll);
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
			}
			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u);
		}

	}

	@Override
	// 状态保存
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * 影响搜索按钮点击事件
	 *
	 * @param v
	 */
	public void searchButtonProcess(View v) {
		EditText editCity = (EditText) findViewById(R.id.city);
		EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(editCity.getText().toString())
				.keyword(editSearchKey.getText().toString())
				.pageNum(load_Index));
	}

	public void goToNextPage(View v) {
		load_Index++;
		searchButtonProcess(null);
	}

	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}

	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(MainActivity.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

}