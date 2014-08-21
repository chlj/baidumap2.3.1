package com.baidutest.map;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  4. 根据一个点得到相应的信息
 * com.baidutest.map.MyLocationMapView2
 * 
 * 根据搜索 MKSearch类
 * 
 * @author Administrator
 * 
 */
public class baidu_single_mark_info_Activity extends Activity implements
		OnClickListener {

	/**
	 * 演示MapView的基本用法
	 */
	private Button btn_back;

	final static String TAG = "xx";

	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;

	private TextView txt_map_title;
	/**
	 * MyLocationMapView2 是地图主控件
	 */
	private MyLocationMapView2 mMapView = null;

	public View mPopView = null;
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用
	private TextView popupText = null;// 泡泡view
	// 定位图层
	private locationOverlay myLocationOverlay = null;
	private LocationData locData = null;

	private MKSearch mMKSearch = null; // 搜索模块，也可去掉地图模块独立使用

	
	private double x;
	private double y;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		MapApplication app = (MapApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey,
					new MapApplication.MyGeneralListener());
		}
		/**
		 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
		 */

		setContentView(R.layout.baidumap_activity_single_mark_info);

		mMapView = (MyLocationMapView2) findViewById(R.id.bmapView);
		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(16);

		// 设置是否打开卫星图
		mMapView.setSatellite(false);
		// 设置地图模式为交通地图
		mMapView.setTraffic(true);
		// 设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(true);
		// 显示比例尺控件，默认在地图左下角展示比例尺控件
		mMapView.showScaleControl(true);

		
		
		// 用经纬度初始化中心点
		GeoPoint point = pbC.getPointByXY(112.898416, 29.821723); // (经度,纬度)
		mMapController.setCenter(point); // 设置中心点
		
		createPaopao(point); // 创建气泡

		/**
		 * 创建图标资源（用于显示在overlayItem所标记的位置）
		 */
		Drawable marker = this.getResources().getDrawable(R.drawable.icon_geo);
		// 为maker定义位置和边界
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		// 定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);
		// 设置定位数据

		locData = new LocationData();
		locData.longitude = Double.valueOf(112.898416);
		locData.latitude = Double.valueOf(29.821723);

		myLocationOverlay.setData(locData);

		myLocationOverlay.setMarker(marker); // 使用自定义的图标资源

		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);

		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();

		findview(); // 找到控件

		// 初始化MKSearch
		mMKSearch = new MKSearch();
		mMKSearch.init(app.mBMapManager, new MySearchListener());
		
		
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
				String title = "";
				if (mapPoiInfo != null) {
					title = mapPoiInfo.strText;
					Toast.makeText(baidu_single_mark_info_Activity.this, title,
							Toast.LENGTH_SHORT).show();
					
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
//				Toast.makeText(baidu_single_mark_info_Activity.this, "地图加载完成",
//						Toast.LENGTH_SHORT).show();

			}
		};
		
		mMapView.regMapViewListener(MapApplication.getInstance().mBMapManager,
				mMapListener);
		

	}

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	class locationOverlay extends MyLocationOverlay {
		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// 处理点击事件,弹出泡泡
			String ss = "我的位置";

			TextView textcache = (TextView) mPopView
					.findViewById(R.id.textcache);

			GeoPoint point = new GeoPoint((int) (29.821723 * 1E6),
					(int) (112.898416 * 1E6)); // (纬度, 经度)

			int x = point.getLongitudeE6(); // 经度 112898416
			double x1 = x / 1E6; // 112.898416

			int y = point.getLatitudeE6(); // 纬度 29821723
			double y1 = y / 1E6; // 29.821723

			String s = "经度=" + String.valueOf(x1) + "纬度=" + String.valueOf(y1);

			textcache.setText(s);

			if (mPopView.getTag().equals("0")) {
				mPopView.setVisibility(View.VISIBLE);
				mPopView.setTag("1");
			} else if (mPopView.getTag().equals("1")) {
				mPopView.setVisibility(View.GONE);
				mPopView.setTag("0");
			}

			return true;

		}

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
//			if (null != result.poiList) {
//				// 遍历所有的兴趣点信息
//				for (MKPoiInfo poiInfo : result.poiList) {
//					sb.append("----------------------------------------")
//							.append("/n");
//					sb.append("名称：").append(poiInfo.name).append("/n");
//					sb.append("地址：").append(poiInfo.address).append("/n");
//					sb.append("经度：")
//							.append(poiInfo.pt.getLongitudeE6() / 1000000.0f)
//							.append("/n");
//					sb.append("纬度：")
//							.append(poiInfo.pt.getLatitudeE6() / 1000000.0f)
//							.append("/n");
//					sb.append("电话：").append(poiInfo.phoneNum).append("/n");
//					sb.append("邮编：").append(poiInfo.postCode).append("/n");
//					// poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路
//					sb.append("类型：").append(poiInfo.ePoiType).append("/n");
//				}
//			}
			
			
			// 将地址信息、兴趣点信息显示在TextView上
			Toast.makeText(baidu_single_mark_info_Activity.this, sb.toString(),
					Toast.LENGTH_SHORT).show();

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
	 *  得到经纬度 得到一个点对象
	 * @param x
	 * @param y
	 * @return
	 */
	
	public GeoPoint getPointByXY(double x,double y){
		return  new GeoPoint((int) (y * 1E6),
				(int) (x * 1E6)); // (纬度, 经度)
	}
	
	// 创建 弹出泡泡图层
	public void createPaopao( final GeoPoint point) {

		mPopView = getLayoutInflater().inflate(R.layout.act_paopao2, null);
		mPopView.setTag("0");
		popupText = (TextView) mPopView.findViewById(R.id.textcache);

		ImageView img = (ImageView) mPopView.findViewById(R.id.popright);

		//
		mMapView.addView(mPopView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));

	

		MapView.LayoutParams mapviewParams = (MapView.LayoutParams) mPopView
				.getLayoutParams();
		mapviewParams.point = point; // point是要显示的坐标
		mMapView.updateViewLayout(mPopView, mapviewParams);
		mPopView.setVisibility(View.GONE);

		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		popupText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 根据地理坐标点获取地址信息 异步函数，返回结果在MKSearchListener里的onGetAddrResult方法通知
				mMKSearch.reverseGeocode(point);

			}
		});

		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};
		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapView2.pop = pop;
		MyLocationMapView2.mPopView = mPopView;
	}

	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
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

	public void findview() {
		btn_back = (Button) findViewById(R.id.back);
		btn_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}

	}

}

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author hejin
 * 
 */
class MyLocationMapView2 extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	static View mPopView = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapView2(Context context) {
		super(context);
	}

	public MyLocationMapView2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP) {
				pop.hidePop();

				mPopView.setVisibility(View.GONE);
				mPopView.setTag("0");
			}
		}
		return true;
	}

}
