package com.baidutest.map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

/**
 * 15.百度地图界面_2 gps定位- 只在初始化时定位 + 通过点击事件进行定位
 * 
 * @author Administrator
 * 
 */

public class baidu_map_jiemian_2_Activity extends Activity {

	private Button central_point, traffic;
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();

	// 定位图层
	locationOverlay myLocationOverlay = null;
	// 弹出泡泡图层
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用
	private TextView popupText = null;// 泡泡view
	private View viewCache = null;

	private View viewCache_dw = null;

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MyLocationMapViewXYZ mMapView = null; // 地图View
	private MapController mMapController = null;

	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位

	private ImageView popleft, popright;

	private MKSearch mMKSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private MapApplication app;

	private String flag = "0";
	
	private String myLoadString="未获取到";
	/**
	 * MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (MapApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey,
					new MapApplication.MyGeneralListener());
		}

		setContentView(R.layout.baidumap_activity_jiemian_2);
		CharSequence titleLable = "定位功能";
		setTitle(titleLable);

		Button bt1 = (Button) findViewById(R.id.back);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		viewCache_dw = baidu_map_jiemian_2_Activity.this.getLayoutInflater()
				.inflate(R.layout.act_paopao2, null); // 定位时 弹出的图层
		viewCache_dw.setTag("0");

		// 地图初始化
		mMapView = (MyLocationMapViewXYZ) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(17);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);

		// 定位初始化
		mLocClient = new LocationClient(this);
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 初始化MKSearch
		mMKSearch = new MKSearch();
		mMKSearch.init(app.mBMapManager, new MySearchListener());

		
	
	
		
		// 定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locData);

		// 创建 弹出泡泡图层
		createPaopao(new GeoPoint((int) (locData.latitude * 1e6),
				(int) (locData.longitude * 1e6)), "网络不稳定时显示");

		// 自定义图标
		Drawable marker = baidu_map_jiemian_2_Activity.this.getResources()
				.getDrawable(R.drawable.icon_geo);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); // 为maker定义位置和边界

		myLocationOverlay.setMarker(marker);

		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);

		myLocationOverlay.enableCompass(); // 启用指南针传感器的更新。

		// 修改定位数据后刷新图层生效
		mMapView.refresh();

		central_point = (Button) findViewById(R.id.central_point);
		traffic = (Button) findViewById(R.id.traffic);

		// 开启定位
		central_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_2_Activity.this, "开启定位",
						Toast.LENGTH_SHORT).show();

				flag = "0";
				mMapView.getOverlays().clear();
				mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法

				// 隐藏
				viewCache.setTag("0");
				viewCache.setVisibility(View.GONE);

				// 地图初始化
				isFirstLoc = true;
				mMapView = (MyLocationMapViewXYZ) findViewById(R.id.bmapView);
				mMapController = mMapView.getController();
				mMapView.getController().setZoom(17);
				mMapView.getController().enableClick(true);
				mMapView.setBuiltInZoomControls(true);
				mLocClient = new LocationClient(
						baidu_map_jiemian_2_Activity.this);
				locData = new LocationData();
				mLocClient.registerLocationListener(myListener);
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);// 打开gps
				option.setCoorType("bd09ll"); // 设置坐标类型
				option.setScanSpan(1000);
				option.disableCache(true);//禁止启用缓存定位
				option.setPoiNumber(5);//最多返回POI个数
				option.setPoiDistance(1000); //poi查询距离
				option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
				mLocClient.setLocOption(option);
				mLocClient.requestLocation();
				
				mLocClient.start();

				// 初始化MKSearch
				mMKSearch = new MKSearch();
				mMKSearch.init(app.mBMapManager, new MySearchListener());

				// 定位图层初始化
				myLocationOverlay = new locationOverlay(mMapView);
				// 设置定位数据
				myLocationOverlay.setData(locData);

				// 创建 弹出泡泡图层
				createPaopao(new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)), "locData中获取位置信息");

				GeoPoint point = new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6));
				mMapController.animateTo(point); // 定位在中央

				// 自定义图标
				Drawable marker = baidu_map_jiemian_2_Activity.this
						.getResources().getDrawable(R.drawable.icon_geo);
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight()); // 为maker定义位置和边界

				myLocationOverlay.setMarker(marker);
				// 添加定位图层
				mMapView.getOverlays().add(myLocationOverlay);

				myLocationOverlay.enableCompass(); // 启用指南针传感器的更新。

				// 修改定位数据后刷新图层生效
				mMapView.refresh();

			}
		});

		// 交通图
		traffic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_2_Activity.this, "交通图",
						Toast.LENGTH_SHORT).show();
				mMapView.setTraffic(true);

				mMapView.setSatellite(true); // 卫星图
			}
		});

		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调 缩放，平移等操作完成后，此回调被触发
				 */
			}

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件 显示底图poi名称并移动至该点 设置过：
				 * mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */

				flag = "1";
				String title = "";
				
				if (mapPoiInfo != null) {
					
					// 清除之前的标记点

					createPaopao(mapPoiInfo.geoPt, mapPoiInfo.strText); // 创建气泡

					/**
					 * 创建图标资源（用于显示在overlayItem所标记的位置）
					 */
					Drawable marker = baidu_map_jiemian_2_Activity.this
							.getResources().getDrawable(R.drawable.icon_geo);
					// 为maker定义位置和边界
					marker.setBounds(0, 0, marker.getIntrinsicWidth(),
							marker.getIntrinsicHeight());

					// 定位图层初始化
					myLocationOverlay = new locationOverlay(mMapView);
					// 设置定位数据

					locData = new LocationData();
					locData.longitude = Double.valueOf(mapPoiInfo.geoPt
							.getLongitudeE6() / 1E6);
					locData.latitude = Double.valueOf(mapPoiInfo.geoPt
							.getLatitudeE6() / 1E6);

					myLocationOverlay.setData(locData);

					myLocationOverlay.setMarker(marker); // 使用自定义的图标资源

					// 添加定位图层
					mMapView.getOverlays().add(myLocationOverlay);

					myLocationOverlay.enableCompass();
					// 修改定位数据后刷新图层生效
					mMapView.refresh();

					mMapController.animateTo(mapPoiInfo.geoPt);
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 * 当调用过 mMapView.getCurrentMap()后，此回调会被触发 可在此保存截图至存储设备
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 * 地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
			}

			/**
			 * 在此处理地图载完成事件
			 */
			@Override
			public void onMapLoadFinish() {
				// Toast.makeText(baidu_single_mark_info_Activity.this,
				// "地图加载完成",
				// Toast.LENGTH_SHORT).show();

			}
		};

		mMapView.regMapViewListener(MapApplication.getInstance().mBMapManager,
				mMapListener);

	}

	// 创建 弹出泡泡图层
	public void createPaopao(final GeoPoint point, String value) {
		if(flag.equals("0")){
			
		}
		else{
			mMapView.getOverlays().clear();
			mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法
		}
		

		viewCache = baidu_map_jiemian_2_Activity.this.getLayoutInflater()
				.inflate(R.layout.act_paopao2, null);

		viewCache.setTag("0");
		
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		popupText.setText(value); // 给气泡中的文本框赋值

		ImageView img = (ImageView) viewCache.findViewById(R.id.popright);  // 到这里去

		ImageView img_se = (ImageView) viewCache.findViewById(R.id.popleft); //附近搜索
		//
		mMapView.addView(viewCache, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));

		MapView.LayoutParams mapviewParams = (MapView.LayoutParams) viewCache
				.getLayoutParams();
		mapviewParams.point = point; // point是要显示的坐标
		mMapView.updateViewLayout(viewCache, mapviewParams);
		viewCache.setVisibility(View.GONE);

		// 到这里去
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_2_Activity.this,
						"到这里去", Toast.LENGTH_SHORT).show();
				Intent intent =new Intent();
				intent.setClass( baidu_map_jiemian_2_Activity.this, baidu_map_jiemian_3_Activity.class);
				intent.putExtra("toName", popupText.getText().toString()); // 目的地名称
				intent.putExtra("toLon",String.valueOf(locData.longitude));//  目的地经度
				intent.putExtra("toLat",String.valueOf(locData.latitude));//  目的地纬度
				Log.i("xx","name="+popupText.getText().toString()+",经度="+String.valueOf(locData.longitude)+",纬度="+String.valueOf(locData.latitude));
		        startActivity(intent);
				
			}
		});
		 // 附近搜索
		img_se.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(baidu_map_jiemian_2_Activity.this,
						"附近搜索", Toast.LENGTH_SHORT).show();
			}
		});
		popupText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_2_Activity.this,
						popupText.getText(), Toast.LENGTH_SHORT).show();
			}
		});

		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};

		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapViewXYZ.pop = pop;
		MyLocationMapViewXYZ.mPopView = viewCache;

	}

	/**
	 * 实现MKSearchListener接口,用于实现异步搜索服务，得到搜索结果
	 * 
	 * @author liufeng
	 */
	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int arg1) {
			// 返回地址信息搜索结果
			if (result == null) {
				return;
			}
			StringBuffer sb = new StringBuffer();
			// 经纬度所对应的位置
			sb.append(result.strAddr).append("*"); // 地址名
			sb.append(result.strBusiness); // 商圈名称

			// 判断该地址附近是否有POI（Point of Interest,即兴趣点）
			// if (null != result.poiList) {
			// // 遍历所有的兴趣点信息
			// for (MKPoiInfo poiInfo : result.poiList) {
			// sb.append("----------------------------------------")
			// .append("/n");
			// sb.append("名称：").append(poiInfo.name).append("/n");
			// sb.append("地址：").append(poiInfo.address).append("/n");
			// sb.append("经度：")
			// .append(poiInfo.pt.getLongitudeE6() / 1000000.0f)
			// .append("/n");
			// sb.append("纬度：")
			// .append(poiInfo.pt.getLatitudeE6() / 1000000.0f)
			// .append("/n");
			// sb.append("电话：").append(poiInfo.phoneNum).append("/n");
			// sb.append("邮编：").append(poiInfo.postCode).append("/n");
			// // poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路
			// sb.append("类型：").append(poiInfo.ePoiType).append("/n");
			// }
			// }

			// 将地址信息、兴趣点信息显示在TextView上

			
			myLoadString=result.strAddr;

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int arg1) {
			// 返回公交车详情信息搜索结果

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int arg1) {
			// 返回驾乘路线搜索结果

		}

		@Override
		public void onGetPoiDetailSearchResult(int result, int arg1) {
			// 返回poi相信信息搜索的结果

		}

		@Override
		public void onGetPoiResult(MKPoiResult result, int arg1, int arg2) {
			// 返回poi搜索结果

		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int arg1,
				int arg2) {
			// 返回分享短串结果.

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult result, int arg1) {
			// 返回联想词信息搜索结果

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int arg1) {
			// 返回公交搜索结果

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int arg1) {
			// 返回步行路线搜索结果

		}

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
					
		   
		    
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			
			
			myLoadString=location.getAddrStr(); // 有可能为null 
			// 如果获取不到 就只能根据 经纬度 去查询 实现了
			Log.i("xx","myLoadString="+myLoadString); 
			Log.i("xx", "经度2=" + locData.longitude + ",纬度=" + locData.latitude);
			
			if(myLoadString==null){
				  GeoPoint gp = new GeoPoint((int)(locData.latitude* 1e6), 
			                (int)(locData.longitude *  1e6));
				    mMKSearch.reverseGeocode(gp); //mSearch为 MKSearch对象
			}
			
			
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();
			// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
			locData.direction = location.getDerect();
			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mMapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				Log.d("LocationOverlay", "receive location, animate to it");
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
			} else {
				mLocClient.stop();
			}
			// 首次定位完成
			isFirstLoc = false;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	class locationOverlay extends MyLocationOverlay {
		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			if (flag.equals("0")) {
//              事件很难被触发
				
//				final TextView popupText = (TextView) viewCache_dw
//						.findViewById(R.id.textcache);
//				popupText.setText("我的位置"); // 给气泡中的文本框赋值
//				popupText.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Toast.makeText(baidu_map_jiemian_2_Activity.this,
//								popupText.getText().toString(),
//								Toast.LENGTH_SHORT).show();
//					}
//				});
//				pop.showPopup(viewCache_dw, new GeoPoint(
//						(int) (locData.latitude * 1e6),
//						(int) (locData.longitude * 1e6)), 8);
				
				
				createPaopao(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)), myLoadString);

			}

			if (viewCache.getTag().equals("0")) {
				viewCache.setVisibility(View.VISIBLE);
				viewCache.setTag("1");
			} else if (viewCache.getTag().equals("1")) {
				viewCache.setVisibility(View.GONE);
				viewCache.setTag("0");
			}
			return true;
		}

	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		if (mLocClient != null)
			mLocClient.stop();
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author hejin
 * 
 */
class MyLocationMapViewXYZ extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用
	static View mPopView = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapViewXYZ(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapViewXYZ(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapViewXYZ(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP) {
				pop.hidePop();
			}
			mPopView.setVisibility(View.GONE);
			mPopView.setTag("0");
		}
		return true;
	}
}
