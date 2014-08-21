package com.baidutest.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKLine;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKStep;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

/**
 * 16.百度地图界面_3 到这里去
 * 
 * 传 double 类型的 经纬度 + 名称 （toName，toLon，toLat）
 * @author Administrator
 * 
 */

public class baidu_map_jiemian_3_Activity extends Activity {

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
	MyLocationMapViewXYZA mMapView = null; // 地图View
	private MapController mMapController = null;

	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位

	private ImageView popleft, popright;

	private MKSearch mMKSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private MapApplication app;

	private ImageButton ib;// 后退

	private EditText editStart, editEnd; // 起点 ,终点

	private String toName, toLon, toLat;// 目的地名称,经度,纬度

	LocationData locData2 = null; // 初始化定位
	public MyLocationListenner myListener2 = new MyLocationListenner();
	private Button mBtnDrive, mBtnTransit, mBtnWalk; // 自驾,公交,步行
	public String strBtnValue = "1"; // 1-自驾,2-公交,3-步行
	private Button searchRoute;// 搜索
	private ImageButton maprout_btnSwap;// 起点 终点 互相转换

	private GeoPoint mStartPoint, mEndPoint, mTemPoint; // 起点,终点,临时点
	private String mStartStr, mEndStr, mTempStr; // 起点名称,终点名称,临时点名称

	private String baidu_map_city = ""; // 默认城市

	
	private ListView mDrive_Route_Result_List = null; // 驾车步行使用 列表
	private ExpandableListView mBus_Result_List = null; // 公交使用 列表

	public List<Map<String, Object>> iList;
	// 驾车 和 步行 适配器
	private baidu_map_driver_route_ListAdapter listItemAdapter = new baidu_map_driver_route_ListAdapter();
	private SlidingDrawer sd;
	public List<List<Map<String, Object>>> selchilds; // 公汽 查询 二级树


	private baidu_treeAdapter baidu_tree_adapter;
	private List<baidu_bus_demo> mParentList = new ArrayList<baidu_bus_demo>();
	private List<baidu_treeAdapter.TreeNode> treeNode;

	private ImageButton start_add, end_add;

	private Handler mHandler;
	public static final int GPSDW = 1;

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

		setContentView(R.layout.baidumap_activity_jiemian_3);
		
		baidu_map_city=this.getString(R.string.baidu_map_city);
		
		findView();

	}

	public void findView() {
		mDrive_Route_Result_List = (ListView) findViewById(R.id.list_RouteResult); // 驾车
		// 步行使用
		mBus_Result_List = (ExpandableListView) findViewById(R.id.home_expandableListView); // 公交使用

		sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);

		mDrive_Route_Result_List.setVisibility(View.GONE);
		mBus_Result_List.setVisibility(View.GONE);

		// 退出
		ib = (ImageButton) findViewById(R.id.maprout_btnBack);
		ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		editStart = (EditText) findViewById(R.id.start); // 起点
		editEnd = (EditText) findViewById(R.id.end);// 终点

		central_point = (Button) findViewById(R.id.central_point);
		traffic = (Button) findViewById(R.id.traffic);

		// 开启定位
		central_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(baidu_map_jiemian_3_Activity.this, "开启定位",
						Toast.LENGTH_SHORT).show();

				mMapView.getOverlays().clear();
				mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法

				isFirstLoc = true;

				// 地图初始化
				mMapView = (MyLocationMapViewXYZA) findViewById(R.id.bmapView);
				mMapController = mMapView.getController();

				mMapView.getController().setZoom(17);
				mMapView.getController().enableClick(true);
				mMapView.setBuiltInZoomControls(true);

				// 创建 弹出泡泡图层
				// createPaopao();

				// 定位初始化
				mLocClient = new LocationClient(
						baidu_map_jiemian_3_Activity.this);
				locData = new LocationData();
				mLocClient.registerLocationListener(myListener);
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);// 打开gps
				option.setCoorType("bd09ll"); // 设置坐标类型
				option.setScanSpan(1000);
				mLocClient.setLocOption(option);
				mLocClient.requestLocation();
				mLocClient.start();

				// 定位图层初始化
				myLocationOverlay = new locationOverlay(mMapView);
				// 设置定位数据
				myLocationOverlay.setData(locData);

				// 自定义图标
				Drawable marker = baidu_map_jiemian_3_Activity.this
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
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "交通图",
						Toast.LENGTH_SHORT).show();
				// mMapView.setTraffic(true);

				// mMapView.setSatellite(true); // 卫星图
			}
		});

		// 目的地初始化
		toName = this.getIntent().getStringExtra("toName").toString();
		toLon = this.getIntent().getStringExtra("toLon").toString();
		toLat = this.getIntent().getStringExtra("toLat").toString();

		mEndPoint = new GeoPoint((int) (Double.valueOf(toLat) * 1e6),
				(int) (Double.valueOf(toLon) * 1e6));

		mStartStr = "我的位置";
		editStart.setText(mStartStr.toString()); // 起点

		// gps 定位得到我的位置 经纬度
		LocationClient mLocClient = new LocationClient(
				baidu_map_jiemian_3_Activity.this);
		locData2 = new LocationData();
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型为bd09ll
		mLocClient.setLocOption(option);
		mLocClient.start();
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return;
				locData2.latitude = location.getLatitude(); // 纬度信息
				locData2.longitude = location.getLongitude(); // 经度信息
				mStartPoint = new GeoPoint((int) (locData2.latitude * 1e6),
						(int) (locData2.longitude * 1e6)); // 起点

				Log.i("xx", "经度=" + String.valueOf(locData2.longitude) + ",纬度="
						+ String.valueOf(locData2.latitude));

			}

			@Override
			public void onReceivePoi(BDLocation arg0) {

			}
		});

		mEndStr = toName;
		editEnd.setText(mEndStr.toString()); // 终点

		// 在地图上面显示出 终点
		// 地图初始化

		mMapView = (MyLocationMapViewXYZA) findViewById(R.id.bmapView);
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
		mMapController.setZoom(17);

		// 设置是否打开卫星图
		mMapView.setSatellite(false);
		// 设置地图模式为交通地图
		mMapView.setTraffic(false);
		// 设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(true);
		// 显示比例尺控件，默认在地图左下角展示比例尺控件
		mMapView.showScaleControl(true);

		// 用经纬度初始化中心点

		mMapController.setCenter(mEndPoint); // 设置中心点

		/**
		 * 创建图标资源（用于显示在overlayItem所标记的位置）
		 */
		Drawable marker = this.getResources().getDrawable(R.drawable.end_point);
		// 为maker定义位置和边界
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		// 定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);
		// 设置定位数据

		locData = new LocationData();
		locData.longitude = Double.valueOf(toLon);
		locData.latitude = Double.valueOf(toLat);

		myLocationOverlay.setData(locData);

		myLocationOverlay.setMarker(marker); // 使用自定义的图标资源

		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);

		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();

		// 设定搜索按钮的响应
		mBtnDrive = (Button) findViewById(R.id.drive);
		mBtnTransit = (Button) findViewById(R.id.transit);
		mBtnWalk = (Button) findViewById(R.id.walk);

		// 自驾
		mBtnDrive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strBtnValue = "1";
				mBtnDrive.setBackgroundResource(R.drawable.btn_map_drive_on);
				mBtnTransit.setBackgroundResource(R.drawable.btn_map_transit);
				mBtnWalk.setBackgroundResource(R.drawable.btn_map_walk);
			}
		});
		// 公交
		mBtnTransit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strBtnValue = "2";
				mBtnDrive.setBackgroundResource(R.drawable.btn_map_drive);
				mBtnTransit
						.setBackgroundResource(R.drawable.btn_map_transit_on);
				mBtnWalk.setBackgroundResource(R.drawable.btn_map_walk);
			}
		});
		// 步行
		mBtnWalk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strBtnValue = "3";
				mBtnDrive.setBackgroundResource(R.drawable.btn_map_drive);
				mBtnTransit.setBackgroundResource(R.drawable.btn_map_transit);
				mBtnWalk.setBackgroundResource(R.drawable.btn_map_walk_on);
			}
		});

		// 搜索
		searchRoute = (Button) findViewById(R.id.searchRoute);
		searchRoute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "搜索",
						Toast.LENGTH_SHORT).show();
				search(strBtnValue); // 搜索事件
			}
		});

		// 起点 终点 互相转换
		maprout_btnSwap = (ImageButton) findViewById(R.id.maprout_btnSwap);
		maprout_btnSwap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "起点 终点 互相转换",
						Toast.LENGTH_SHORT).show();
				// 换坐标
				mTemPoint = mStartPoint;
				mStartPoint = mEndPoint;
				mEndPoint = mTemPoint;
				// 换名称
				mTempStr = mStartStr;
				mStartStr = mEndStr;
				mEndStr = mTempStr;
				// 显示名称
				editStart.setText(mStartStr.toString());
				editEnd.setText(mEndStr.toString());

				// 将终点显示在地图上面

				mMapView.getOverlays().clear();
				mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法

				// 地图初始化
				mMapView = (MyLocationMapViewXYZA) findViewById(R.id.bmapView);
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
				mMapController.setZoom(17);

				// 设置是否打开卫星图
				mMapView.setSatellite(false);
				// 设置地图模式为交通地图
				mMapView.setTraffic(false);
				// 设置启用内置的缩放控件
				mMapView.setBuiltInZoomControls(true);
				// 显示比例尺控件，默认在地图左下角展示比例尺控件
				mMapView.showScaleControl(true);

				// 用经纬度初始化中心点

				mMapController.setCenter(mEndPoint); // 设置中心点

				/**
				 * 创建图标资源（用于显示在overlayItem所标记的位置）
				 */

				Drawable marker = getResources().getDrawable(
						R.drawable.end_point);
				// 为maker定义位置和边界
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight());

				// 定位图层初始化
				myLocationOverlay = new locationOverlay(mMapView);
				// 设置定位数据

				locData = new LocationData();

				locData.longitude = Double.valueOf(mEndPoint.getLongitudeE6() / 1e6);
				locData.latitude = Double.valueOf(mEndPoint.getLatitudeE6() / 1e6);

				myLocationOverlay.setData(locData);

				myLocationOverlay.setMarker(marker); // 使用自定义的图标资源

				// 添加定位图层
				mMapView.getOverlays().add(myLocationOverlay);

				myLocationOverlay.enableCompass();
				// 修改定位数据后刷新图层生效
				mMapView.refresh();

			}
		});

		pop = new PopupOverlay(mMapView, new PopupClickListener() {

			@Override
			public void onClickedPopup(int index) {
				// sd.animateOpen();
			}
		});

		start_add = (ImageButton) findViewById(R.id.start_add); // 选为起点
		start_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "选为起点",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(baidu_map_jiemian_3_Activity.this,
						baidu_map_jiemian_select_Activity.class);
				intent.putExtra("type", "0");
				intent.putExtra("toName", mStartStr);
				startActivityForResult(intent, 0);
			}
		});

		end_add = (ImageButton) findViewById(R.id.end_add);// 选为终点
		end_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "选为终点",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(baidu_map_jiemian_3_Activity.this,
						baidu_map_jiemian_select_Activity.class);
				intent.putExtra("type", "1");
				intent.putExtra("toName", mEndStr);
				startActivityForResult(intent, 1);
			}
		});

		editStart.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i("xx", "改变了");
			}
		});

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				switch (msg.what) {
				case GPSDW: // 选择终点后 更改图标
					
					if(mEndPoint!=null){
						// 将终点显示在地图上面
						mMapView.getOverlays().clear();
						mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法

						// 地图初始化
						mMapView = (MyLocationMapViewXYZA) findViewById(R.id.bmapView);
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
						mMapController.setZoom(17);

						// 设置是否打开卫星图
						mMapView.setSatellite(false);
						// 设置地图模式为交通地图
						mMapView.setTraffic(false);
						// 设置启用内置的缩放控件
						mMapView.setBuiltInZoomControls(true);
						// 显示比例尺控件，默认在地图左下角展示比例尺控件
						mMapView.showScaleControl(true);

						// 用经纬度初始化中心点

						mMapController.setCenter(mEndPoint); // 设置中心点

						/**
						 * 创建图标资源（用于显示在overlayItem所标记的位置）
						 */

						Drawable marker = getResources().getDrawable(R.drawable.end_point);
						// 为maker定义位置和边界
						marker.setBounds(0, 0, marker.getIntrinsicWidth(),
								marker.getIntrinsicHeight());

						// 定位图层初始化
						myLocationOverlay = new locationOverlay(mMapView);
						// 设置定位数据

						locData = new LocationData();

						locData.longitude = Double
								.valueOf(mEndPoint.getLongitudeE6() / 1e6);
						locData.latitude = Double.valueOf(mEndPoint.getLatitudeE6() / 1e6);

						myLocationOverlay.setData(locData);

						myLocationOverlay.setMarker(marker); // 使用自定义的图标资源

						// 添加定位图层
						mMapView.getOverlays().add(myLocationOverlay);

						myLocationOverlay.enableCompass();
						// 修改定位数据后刷新图层生效
						mMapView.refresh();
					}
					
					
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {

			// 起点
			String lon = data.getStringExtra("lon").toString();// 经度
			String lat = data.getStringExtra("lat").toString();// 纬度
			String name = data.getStringExtra("name").toString();// 名称

			mStartStr = name;
			if(lon.equals("") &&lat.equals("")){
				mStartPoint=null;
			}
			else{
				mStartPoint = new GeoPoint(Integer.valueOf(lat),
						Integer.valueOf(lon)); // 起点
			}
			
			editStart.setText(mStartStr);

			

		} else if (requestCode == 1) {
			// 终点
			String lon = data.getStringExtra("lon").toString();// 经度
			String lat = data.getStringExtra("lat").toString();// 纬度
			String name = data.getStringExtra("name").toString();// 名称

			mEndStr = name;
			if(lon.equals("") &&lat.equals("")){
				mEndPoint=null;
			}
			else{
				mEndPoint = new GeoPoint(Integer.valueOf(lat), Integer.valueOf(lon)); // 终点
			}
			

			editEnd.setText(mEndStr);

			/**
			 * 发送消息
			 */
			Message msg=Message.obtain();
			msg.what=GPSDW;
			mHandler.sendMessage(msg);
			
			

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

			String string = "返回地址信息搜索结果";
			Log.i("xx", "string=" + string);

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

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int arg1) {
			// 返回公交车详情信息搜索结果
			String string = "返回公交车详情信息搜索结果";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			// 返回驾乘路线搜索结果
			String string = "返回驾乘路线搜索结果";
			Log.i("xx", "string=" + string);
			// 起点或终点有歧义，需要选择具体的城市列表或地址列表
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				return;
			}
			// 错误号可参考MKEvent中的定义
			if (error != 0 || res == null) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				mDrive_Route_Result_List.setVisibility(View.GONE);
				mBus_Result_List.setVisibility(View.GONE);
				return;
			}

			RouteOverlay routeOverlay = new RouteOverlay(
					baidu_map_jiemian_3_Activity.this, mMapView);
			// 此处仅展示一个方案作为示例
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			// 清除其他图层
			mMapView.getOverlays().clear();
			// 添加路线图层
			mMapView.getOverlays().add(routeOverlay);
			// 执行刷新使生效
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			// 移动地图到起点
			mMapView.getController().animateTo(res.getStart().pt);

			// 将路线数据保存给全局变量
			MKRoute mkRoute = res.getPlan(0).getRoute(0);

			if (mkRoute != null) {
				// 放在一个list中 展示 出来
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < mkRoute.getNumSteps(); i++) {
					map = new HashMap<String, Object>();
					MKStep mkStep = mkRoute.getStep(i);
					map.put("title", mkStep.getContent()); // 返回关键点描述文本
					map.put("point", mkStep.getPoint()); // 返回关键点地理坐标

					Log.i("xx", "驾车路线" + i + "=" + mkStep.getContent() + ","
							+ mkStep.getPoint());
				}
				sd.animateOpen();// 打开抽屉
				Get_Drive_Route_ResultList(mkRoute);// 驾车 或者 步行

			}

		}

		@Override
		public void onGetPoiDetailSearchResult(int result, int arg1) {
			// 返回poi相信信息搜索的结果
			String string = "返回poi相信信息搜索的结果";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetPoiResult(MKPoiResult result, int arg1, int arg2) {
			// 返回poi搜索结果
			String string = "返回poi搜索结果";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int arg1,
				int arg2) {
			// 返回分享短串结果.
			String string = "返回分享短串结果.";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult result, int arg1) {
			// 返回联想词信息搜索结果
			String string = "返回联想词信息搜索结果";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetTransitRouteResult(final MKTransitRouteResult res,
				int error) {
			// 返回公交搜索结果
			String string = "返回公交搜索结果";
			Log.i("xx", "string=" + string);

			// 起点或终点有歧义，需要选择具体的城市列表或地址列表
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				return;
			}
			if (error != 0 || res == null) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				mDrive_Route_Result_List.setVisibility(View.GONE);
				mBus_Result_List.setVisibility(View.GONE);
				return;
			}

			int count = res.getNumPlan(); // 一共有多少条线路

			List<baidu_bus_demo> list_bus = new ArrayList<baidu_bus_demo>();

			Log.i("xx", "起点 到 终点一共有多少条线路=" + count);
			for (int i = 0; i < count; i++) {
				MKTransitRoutePlan mp = res.getPlan(i); // 取得第一条线路
				int buscount = 0; // 起点 到 终点 的总站数
				int countLines = mp.getNumLines();// 返回方案包含的公交线路段数 (从站与站之间可能需要转站
													// ->有多条线路)
				
		
				for (int m = 0; m < countLines; m++) {
					buscount += mp.getLine(m).getNumViaStops(); // 获取公交线路途经的车站个数
					MKLine mk = mp.getLine(m); //
					String title = mk.getTitle(); // 获取公交线路的名称
					String id = mk.getUid(); // 获取公交线路的id
					String tip = mk.getTip();
					// Log.i("xx", "title=" + title + ",id=" + id + ",tip=" +
					// tip);

				}

				baidu_bus_demo bs = new baidu_bus_demo();
				bs.setRes(res);
				bs.setContent(mp.getContent());
				bs.setTime(String.valueOf(mp.getTime() / 60));
				bs.setDistance(String.valueOf(mp.getDistance()));
				bs.setBuscount(String.valueOf(buscount));
				bs.setI(i);
				list_bus.add(bs);

				// Log.i("xx",
				// "返回方案具体描述信息=" + mp.getContent() + ",预计花费时间="
				// + mp.getTime() / 60 + "分钟,距离="
				// + mp.getDistance() + "米。途经公交站个数=" + buscount);
			}

			Log.i("xx", "list_bus.size()=" + list_bus.size()); // 得到了父级 线路方案

			// 循环父级 得到相应子级详情

			mParentList = list_bus;
			baidu_tree_adapter = new baidu_treeAdapter(
					baidu_map_jiemian_3_Activity.this,
					baidu_treeAdapter.PaddingLeft >> 1, mParentList);
			treeNode = baidu_tree_adapter.GetTreeNode();

			for (int i = 0; i < list_bus.size(); i++) {
				baidu_treeAdapter.TreeNode node = new baidu_treeAdapter.TreeNode();
				node.parent = mParentList.get(i).getContent();

				List<baidu_bus_demo_info> list = new ArrayList<baidu_bus_demo_info>(); // 子级
				MKTransitRoutePlan mp = res.getPlan(i); // 取得第一条线路
				int countLines = mp.getNumLines();// 返回方案包含的公交线路段数 (从站与站之间可能需要转站
				for (int m = 0; m < countLines; m++) {
					MKLine mk = mp.getLine(m); //
					String title = mk.getTitle(); // 获取公交线路的名称
					String id = mk.getUid(); // 获取公交线路的id
					String tip = mk.getTip();
					Log.i("xx", "title=" + title + ",id=" + id + ",tip=" + tip);

					baidu_bus_demo_info bs = new baidu_bus_demo_info();
					bs.setMk(mp);
					bs.setbusId(mk.getUid());
					bs.setTip(mk.getTip());
					bs.setTitle(mk.getTitle());
					list.add(bs);
				}
				node.childs.addAll(list);
				treeNode.add(node);
			}
			baidu_tree_adapter.UpdateTreeNode(treeNode);
			mBus_Result_List.setAdapter(baidu_tree_adapter);
			// 去掉系统自带的分隔线
			mBus_Result_List.setDivider(null);
			mBus_Result_List.setCacheColorHint(0); // 设置拖动列表的时候防止出现黑色背景
			mBus_Result_List
					.setOnChildClickListener(new OnChildClickListener() {
						@Override
						public boolean onChildClick(ExpandableListView parent,
								View v, int groupPosition, int childPosition,
								long id) {

							baidu_tree_adapter.notifyDataSetChanged();

							sd.animateClose(); // 关闭抽屉
							TransitOverlay transitOverlay = new TransitOverlay(
									baidu_map_jiemian_3_Activity.this, mMapView);

							// 此处仅展示一个方案作为示例
							transitOverlay.setData(res.getPlan(groupPosition));

							// 清除其他图层
							mMapView.getOverlays().clear();
							// 添加路线图层
							mMapView.getOverlays().add(transitOverlay);
							// 执行刷新使生效
							mMapView.refresh();
							// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
							mMapView.getController().zoomToSpan(
									transitOverlay.getLatSpanE6(),
									transitOverlay.getLonSpanE6());
							// 移动地图到起点
							mMapView.getController().animateTo(
									res.getStart().pt);

							return false;
						}
					});

			mBus_Result_List
					.setOnGroupClickListener(new OnGroupClickListener() {
						@Override
						public boolean onGroupClick(ExpandableListView parent,
								View v, int groupPosition, long id) {

							return false;
						}
					});

			sd.animateOpen(); // 打开抽屉

			mDrive_Route_Result_List.setVisibility(View.GONE);
			mBus_Result_List.setVisibility(View.VISIBLE);

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			// 返回步行路线搜索结果
			String string = "返回步行路线搜索结果";
			Log.i("xx", "string=" + string);

			// 起点或终点有歧义，需要选择具体的城市列表或地址列表
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				return;
			}
			if (error != 0 || res == null) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "抱歉，未找到结果",
						Toast.LENGTH_SHORT).show();
				mDrive_Route_Result_List.setVisibility(View.GONE);
				mBus_Result_List.setVisibility(View.GONE);
				return;
			}

			RouteOverlay routeOverlay = new RouteOverlay(
					baidu_map_jiemian_3_Activity.this, mMapView);
			// 此处仅展示一个方案作为示例
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			// 清除其他图层
			mMapView.getOverlays().clear();
			// 添加路线图层
			mMapView.getOverlays().add(routeOverlay);
			// 执行刷新使生效
			mMapView.refresh();
			// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			// 移动地图到起点
			mMapView.getController().animateTo(res.getStart().pt);
			// 将路线数据保存给全局变量
			MKRoute mkRoute = res.getPlan(0).getRoute(0);

			if (mkRoute != null) {
				// 放在一个list中 展示 出来
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < mkRoute.getNumSteps(); i++) {
					map = new HashMap<String, Object>();
					MKStep mkStep = mkRoute.getStep(i);
					map.put("title", mkStep.getContent()); // 返回关键点描述文本
					map.put("point", mkStep.getPoint()); // 返回关键点地理坐标

					Log.i("xx", "步行路线" + i + "=" + mkStep.getContent() + ","
							+ mkStep.getPoint());
				}
				sd.animateOpen();// 打开抽屉
				Get_Drive_Route_ResultList(mkRoute); // 驾车 或者 步行
			}
		}

	}

	// 获取自驾和步行路线信息列表及点击事件监听
	private void Get_Drive_Route_ResultList(MKRoute mkRoute) {
		mDrive_Route_Result_List.setVisibility(View.VISIBLE);
		mBus_Result_List.setVisibility(View.GONE);

		iList = new ArrayList<Map<String, Object>>();
		// 开头
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", mStartStr);
		map.put("imgv_img", getResources().getDrawable(R.drawable.sta_point));
		iList.add(map);

		List<GeoPoint> data = new ArrayList<GeoPoint>();
		for (int i = 0; i < mkRoute.getNumSteps(); i++) {
			map = new HashMap<String, Object>();
			MKStep mkStep = mkRoute.getStep(i);
			map.put("title", mkStep.getContent());
			map.put("point", mkStep.getPoint());
			// map.put("imgv_img",
			// MapRouteActivity.this.getResources().getDrawable(R.drawable.pcslist));
			iList.add(map);
			data.add(mkStep.getPoint());
		}

		// 结尾
		map = new HashMap<String, Object>();
		map.put("title", mEndStr);
		map.put("imgv_img", getResources().getDrawable(R.drawable.end_point));
		iList.add(map);

		listItemAdapter.setContext(baidu_map_jiemian_3_Activity.this);
		listItemAdapter.setList(iList);
		mDrive_Route_Result_List.setAdapter(listItemAdapter);
		mDrive_Route_Result_List
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View v,
							int index, long arg3) {
						// SearchBackToMap_int(index);
						GeoPoint staPoint = (GeoPoint) iList.get(index).get(
								"point");
						String strTitle = (String) iList.get(index)
								.get("title");
						if (mStartStr.equals(strTitle) == false
								&& mEndStr.equals(strTitle) == false) {
							pop.hidePop();
							SetPopView(staPoint, strTitle);
							mMapView.getController().animateTo(staPoint);
							sd.animateClose(); // 抽屉
						}
					}
				});
	}

	public void SetPopView(GeoPoint pt, String textName) {
		Bitmap[] bmps = new Bitmap[3];
		View popview = LayoutInflater.from(this).inflate(R.layout.act_paopao2,
				null);// 获取要转换的View资源

		TextView TestText = (TextView) popview.findViewById(R.id.textcache);
		TestText.setTextSize(14);
		TestText.setText(textName);// 将每个点的Title在弹窗中以文本形式显示出来

		Bitmap popbitmap = convertViewToBitmap(popview);
		// bmps[0] =
		// BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
		bmps[1] = popbitmap;
		// BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
		// bmps[2] =
		// BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));

		pop.showPopup(popbitmap, pt, 0);
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
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
	public class locationOverlay extends MyLocationOverlay {
		public locationOverlay(MapView mapView) {
			super(mapView);
		}

		@Override
		protected boolean dispatchTap() {
			// 处理点击事件,弹出泡泡
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

	/**
	 * 搜索
	 * 
	 * @param value
	 */
	public void search(String value) {
		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		
		//起点
		if("".equals(editStart.getText().toString())){
			 Toast.makeText(baidu_map_jiemian_3_Activity.this, "请选择起点", Toast.LENGTH_SHORT).show();
			 mStartStr="";
			 mStartPoint=null ;
			 return ;
		}
		// 终点
		if("".equals(editEnd.getText().toString())){
			 Toast.makeText(baidu_map_jiemian_3_Activity.this, "请选择终点", Toast.LENGTH_SHORT).show();
			 mEndStr="";
			 mEndPoint=null ;
			 return ;
		}
		
		MKPlanNode startNode = new MKPlanNode();
		if(mStartPoint!=null){
			startNode.pt = mStartPoint;
		}
		
		startNode.name = mStartStr;
		MKPlanNode endNode = new MKPlanNode();
		
		if(mEndPoint!=null){
			endNode.pt = mEndPoint;
		}
		
		endNode.name = mEndStr;

		/**
		 * 设置 公交换乘路线搜索策略，有以下4种策略可选择： 1）不含地铁：MKSearch.EBUS_NO_SUBWAY
		 * 2）时间优先：MKSearch.EBUS_TIME_FIRST 3）最少换乘：MKSearch.EBUS_TRANSFER_FIRST
		 * 4）最少步行距离：MKSearch.EBUS_WALK_FIRST
		 * 
		 * 我们这里选择的搜索策略是最少换乘，即中途转车次数最少
		 */

		/**
		 * MKSearch.ECAR_AVOID_JAM MKSearch.ECAR_DIS_FIRST
		 * MKSearch.ECAR_FEE_FIRST MKSearch.ECAR_TIME_FIRST
		 */
		// 实际使用中请对起点终点城市baidu_map_city进行正确的设定

		// 初始化MKSearch
		mMKSearch = new MKSearch();
		mMKSearch.init(app.mBMapManager, new MySearchListener());

		if (value == "1") {
			// 驾车
			mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
			mMKSearch.drivingSearch(baidu_map_city, startNode, baidu_map_city, endNode);
		} else if (value == "2") {
			// 公交
			mMKSearch.setTransitPolicy(MKSearch.EBUS_TRANSFER_FIRST); // 3）最少换乘
			mMKSearch.transitSearch(baidu_map_city, startNode, endNode);
		} else if (value == "3") {
			// 步行
			mMKSearch.walkingSearch(baidu_map_city, startNode, baidu_map_city, endNode);
		}
	}
}

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author hejin
 * 
 */
class MyLocationMapViewXYZA extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapViewXYZA(Context context) {
		super(context);

	}

	public MyLocationMapViewXYZA(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapViewXYZA(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// 消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}

		return true;
	}
}
