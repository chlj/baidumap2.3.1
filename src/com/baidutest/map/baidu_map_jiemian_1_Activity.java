package com.baidutest.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidutest.map.baidu_single_mark_info_Activity.MySearchListener;
import com.example.testdemo.R;

/**
 * 14.百度地图界面
 *  gps定位- 只在初始化时定位 + 通过点击事件进行定位
 * 
 * @author Administrator
 * 
 */

public class baidu_map_jiemian_1_Activity extends Activity {

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

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MyLocationMapViewXY mMapView = null; // 地图View
	private MapController mMapController = null;

	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位

	private ImageView popleft, popright;

	private MKSearch mMKSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private MapApplication app;

	private ScaleView mScaleView;
	private ZoomControlView mZoomControlView;
	
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

		setContentView(R.layout.baidumap_activity_jiemian_1);
		CharSequence titleLable = "定位功能";
		setTitle(titleLable);

		Button bt1 = (Button) findViewById(R.id.back);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 地图初始化
		mMapView = (MyLocationMapViewXY) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		 // mMapView.setBuiltInZoomControls(true);
		
		
	
        
      
        
		
		//隐藏自带的地图缩放控件
		mMapView.setBuiltInZoomControls(false);
		
		mScaleView = (ScaleView) findViewById(R.id.scaleView);
		mScaleView.setMapView(mMapView);
		mZoomControlView = (ZoomControlView) findViewById(R.id.ZoomControlView);
		mZoomControlView.setMapView(mMapView);
		
		refreshScaleAndZoomControl();
		  
		//地图显示事件监听器。 该接口监听地图显示事件，用户需要实现该接口以处理相应事件。
		mMapView.regMapViewListener(app.mBMapManager, new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				 refreshScaleAndZoomControl();
			}
			
			@Override
			public void onMapLoadFinish() {
				
			}
			
			
			
			/**
			 * 动画结束时会回调此消息.我们在此方法里面更新缩放按钮的状态
			 */
			@Override
			public void onMapAnimationFinish() {
				 refreshScaleAndZoomControl();
			}
			
			@Override
			public void onGetCurrentMap(Bitmap arg0) {
				
			}
			
			@Override
			public void onClickMapPoi(MapPoi arg0) {
				
			}
		});
		
		//获取地图控制器
		
		
		
		// 创建 弹出泡泡图层
		createPaopao();

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

		// 自定义图标
		Drawable marker = baidu_map_jiemian_1_Activity.this.getResources()
				.getDrawable(R.drawable.icon_geo);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); // 为maker定义位置和边界

		myLocationOverlay.setMarker(marker);
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		
		 /**
         * 设置地图俯角
         */
         mMapController.setOverlooking(-30); // 出现指南针
         
         
		 myLocationOverlay.enableCompass(); //   启用指南针传感器的更新。
		 
		 
		// 修改定位数据后刷新图层生效
		mMapView.refresh();

		central_point = (Button) findViewById(R.id.central_point);
		traffic = (Button) findViewById(R.id.traffic);
		
	// 开启定位
		central_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_1_Activity.this, "开启定位",
						Toast.LENGTH_SHORT).show();
				mLocClient.start();

			}
		});

		// 交通图
		traffic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_1_Activity.this, "交通图",
						Toast.LENGTH_SHORT).show();
				mMapView.setTraffic(true);
				
				mMapView.setSatellite(true); //卫星图
			}
		});

		 //算两点之间的距离
		GeoPoint p1LL = new GeoPoint(39971802, 116347927);

		GeoPoint p2LL = new GeoPoint(39892131, 116498555);

		double distance = DistanceUtil.getDistance(p1LL, p2LL);

		Toast.makeText(baidu_map_jiemian_1_Activity.this,
				"distance=" + distance, Toast.LENGTH_SHORT).show();

		
		
		// 使用MKLocationManager类的requestLocationUpdates 来注册位置监听，用 removeUpdates 来取消位置监听。
		
		
		
	
		 
	
	 
		
	}

	private void refreshScaleAndZoomControl(){
        //更新缩放按钮的状态
        mZoomControlView.refreshZoomButtonStatus(Math.round(mMapView.getZoomLevel()));
        mScaleView.refreshScaleView(Math.round(mMapView.getZoomLevel()));
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

			popupText.setText(sb.toString());

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
	 * 创建弹出泡泡图层
	 */
	public void createPaopao() {

		mMapView.getOverlays().clear();
		mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法

		viewCache = getLayoutInflater().inflate(R.layout.act_paopao2, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		popleft = (ImageView) viewCache.findViewById(R.id.popleft);

		popright = (ImageView) viewCache.findViewById(R.id.popright);

		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};

		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapViewXY.pop = pop;

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
			}
			else{
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
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			// 处理点击事件,弹出泡泡

			GeoPoint point = new GeoPoint((int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6));
			// mMapController.animateTo(point); // 定位在中央

			// 根据地理坐标点获取地址信息 异步函数，返回结果在MKSearchListener里的onGetAddrResult方法通知
			// mMKSearch.reverseGeocode(point);

			// 先显示
			popupText.setText("根据经纬度去查信息");

			// 根据 经纬度去获取信息

			pop.showPopup(viewCache, new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)), 8);

			// 再触发事件
			popupText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_map_jiemian_1_Activity.this,
							popupText.getText(), Toast.LENGTH_SHORT).show();
				}
			});
			popleft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_map_jiemian_1_Activity.this, "搜索",
							Toast.LENGTH_SHORT).show();

				}
			});

			popright.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_map_jiemian_1_Activity.this, "到这里去",
							Toast.LENGTH_SHORT).show();

				}
			});
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
class MyLocationMapViewXY extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapViewXY(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapViewXY(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapViewXY(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}

//		// 获得屏幕点击的位置
//		int x = (int) event.getX();
//		int y = (int) event.getY();
//		// 将像素坐标转为地址坐标
//		GeoPoint pt = this.getProjection().fromPixels(x, y);
//		return super.onTouchEvent(event);

		 return true;
	}
}
