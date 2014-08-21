package com.baidutest.map;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKStep;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidutest.map.baidu_map_jiemian_3_Activity.locationOverlay;
import com.example.testdemo.R;

/**
 * 19 百度地图搜索 —— 显示 列表
 * 
 * @author Administrator
 * 
 */

public class baidu_map_jiemian_search_show_list_Activity extends Activity {

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
	MyLocationMapViewXYZVSY mMapView = null; // 地图View
	private MapController mMapController = null;

	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位

	private ImageView popleft, popright;

	private MKSearch mMKSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private MapApplication app;

	private String flag = "0";

	private String myLoadString = "未获取到";
	/**
	 * MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;

	private Button btn_search;// 搜索按钮
	private EditText edit_searchkey; // 请输入地点

	private ArrayList<OverlayItem> arraylist = null; // 地图搜索时使用

	private String baidu_map_city = "";

	private SlidingDrawer sd;
	private ListView mSearch_Result_List; // 搜索结果列表

	private GeoPoint myPoint = null;// 我的坐标（gps定位）
	private LocationData locData2 = null;

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

		setContentView(R.layout.baidumap_activity_jiemian_search_show_list);
		CharSequence titleLable = "定位功能";
		setTitle(titleLable);

		baidu_map_city = this.getString(R.string.baidu_map_city);

		mSearch_Result_List = (ListView) findViewById(R.id.list_RouteResult); // 搜索结果列表
		sd = (SlidingDrawer) findViewById(R.id.slidingDrawer); // 抽屉

		Button bt1 = (Button) findViewById(R.id.back);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		viewCache_dw = baidu_map_jiemian_search_show_list_Activity.this
				.getLayoutInflater().inflate(R.layout.act_paopao2, null); // 定位时
																			// 弹出的图层
		viewCache_dw.setTag("0");

		// 地图初始化
		mMapView = (MyLocationMapViewXYZVSY) findViewById(R.id.bmapView);
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
		Drawable marker = baidu_map_jiemian_search_show_list_Activity.this
				.getResources().getDrawable(R.drawable.icon_geo);
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
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"开启定位", Toast.LENGTH_SHORT).show();

				flag = "0";
				mMapView.getOverlays().clear();
				mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法

				// 隐藏
				viewCache.setTag("0");
				viewCache.setVisibility(View.GONE);

				// 地图初始化
				isFirstLoc = true;
				mMapView = (MyLocationMapViewXYZVSY) findViewById(R.id.bmapView);
				mMapController = mMapView.getController();
				mMapView.getController().setZoom(17);
				mMapView.getController().enableClick(true);
				mMapView.setBuiltInZoomControls(true);
				mLocClient = new LocationClient(
						baidu_map_jiemian_search_show_list_Activity.this);
				locData = new LocationData();
				mLocClient.registerLocationListener(myListener);
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);// 打开gps
				option.setCoorType("bd09ll"); // 设置坐标类型
				option.setScanSpan(1000);
				option.disableCache(true);// 禁止启用缓存定位
				option.setPoiNumber(5);// 最多返回POI个数
				option.setPoiDistance(1000); // poi查询距离
				option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
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
				myPoint = point; // 我的坐标
				mMapController.animateTo(point); // 定位在中央

				// 自定义图标
				Drawable marker = baidu_map_jiemian_search_show_list_Activity.this
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
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"交通图", Toast.LENGTH_SHORT).show();
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
					Drawable marker = baidu_map_jiemian_search_show_list_Activity.this
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

		// ------------------begin-------------------------------
		// gps 定位得到我的位置 经纬度
		LocationClient mLocClient = new LocationClient(
				baidu_map_jiemian_search_show_list_Activity.this);
		locData2 = new LocationData();
		LocationClientOption optiona = new LocationClientOption();
		optiona.setOpenGps(true); // 打开gps
		optiona.setCoorType("bd09ll"); // 设置坐标类型为bd09ll
		mLocClient.setLocOption(optiona);
		mLocClient.start();
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return;
				locData2.latitude = location.getLatitude(); // 纬度信息
				locData2.longitude = location.getLongitude(); // 经度信息
				myPoint = new GeoPoint((int) (locData2.latitude * 1e6),
						(int) (locData2.longitude * 1e6)); // 起点

				Log.i("xx", "经度=" + String.valueOf(locData2.longitude) + ",纬度="
						+ String.valueOf(locData2.latitude));

			}

			@Override
			public void onReceivePoi(BDLocation arg0) {

			}
		});

		// ----------------------end---------------------

	}

	// 创建 弹出泡泡图层
	public void createPaopao(final GeoPoint point, String value) {
		if (flag.equals("0")) {

		} else {
			mMapView.getOverlays().clear();
			mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法
		}

		viewCache = baidu_map_jiemian_search_show_list_Activity.this
				.getLayoutInflater().inflate(R.layout.act_paopao2, null);

		viewCache.setTag("0");

		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		popupText.setText(value); // 给气泡中的文本框赋值

		ImageView img = (ImageView) viewCache.findViewById(R.id.popright); // 到这里去

		ImageView img_se = (ImageView) viewCache.findViewById(R.id.popleft); // 附近搜索
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
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"到这里去", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(
						baidu_map_jiemian_search_show_list_Activity.this,
						baidu_map_jiemian_3_Activity.class);
				intent.putExtra("toName", popupText.getText().toString()); // 目的地名称
				intent.putExtra("toLon", String.valueOf(locData.longitude));// 目的地经度
				intent.putExtra("toLat", String.valueOf(locData.latitude));// 目的地纬度
			
				startActivity(intent);

			}
		});
		// 附近搜索
		img_se.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"附近搜索", Toast.LENGTH_SHORT).show();
			}
		});
		popupText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						popupText.getText(), Toast.LENGTH_SHORT).show();
				
				
				
				Intent intent =new Intent();
				intent.setClass(baidu_map_jiemian_search_show_list_Activity.this, baidu_map_infoPage_Activity.class);
				intent.putExtra("showname", popupText.getText().toString());
				intent.putExtra("toLat",String.valueOf(locData.latitude) );
				intent.putExtra("toLon", String.valueOf(locData.longitude));
				startActivity(intent);
				
				
			}
		});

		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};

		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapViewXYZVSY.pop = pop;
		MyLocationMapViewXYZVSY.mPopView = viewCache;

		btn_search = (Button) findViewById(R.id.search); // 搜索按钮
		edit_searchkey = (EditText) findViewById(R.id.searchkey);// 请输入地点

		btn_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String address = edit_searchkey.getText().toString();
				if ("".equals(address.toString())) {
					Toast.makeText(
							baidu_map_jiemian_search_show_list_Activity.this,
							"请输入地点", Toast.LENGTH_SHORT).show();
					return;
					// 开始搜索
				}

				mMKSearch.poiSearchInCity(baidu_map_city, address.toString()); // 城市poi检索.

			}
		});

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

			myLoadString = result.strAddr;

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

		// 返回poi搜索结果
		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {

			mSearch_Result_List.setVisibility(View.GONE);

			// 返回poi搜索结果
			// 错误号可参考MKEvent中的定义
			if (error != 0 || res == null) {
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"抱歉，未找到结果", Toast.LENGTH_LONG).show();
				return;
			}
			// 将地图移动到第一个POI中心点
			if (res.getCurrentNumPois() > 0) {
				// 2. 自定义图标 将结果显示到地图上面
				Drawable marker = baidu_map_jiemian_search_show_list_Activity.this
						.getResources().getDrawable(R.drawable.icon_geo);
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight()); // 为maker定义位置和边界
				int count = res.getAllPoi().size();

				arraylist = new ArrayList<OverlayItem>();

				

				for (int i = 0; i < count; i++) {
					// java.lang.String title, java.lang.String snippe

					// 根据我的坐标 和 终点的坐标 得到 距离
					double distance = DistanceUtil.getDistance(myPoint, res
							.getAllPoi().get(i).pt); // 两点算距离

					DecimalFormat df = new DecimalFormat("#.00");
					String distances = df.format(distance);// 保留2位有效小数

					OverlayItem item = new OverlayItem(
							res.getAllPoi().get(i).pt,
							res.getAllPoi().get(i).name,
							res.getAllPoi().get(i).address + "^" + distances);

					arraylist.add(item);
				}

				OverlayTestA itemOverlay = new OverlayTestA(marker, mMapView);

				mMapView.getOverlays().clear();
				itemOverlay.addItem(arraylist);
				mMapView.getOverlays().add(itemOverlay);
				mMapView.refresh();
				// 弹出气泡
				PopupClickListener popListener = new PopupClickListener() {
					@Override
					public void onClickedPopup(int index) {

					}
				};
				pop = new PopupOverlay(mMapView, popListener);
				// 当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
				for (MKPoiInfo info : res.getAllPoi()) {
					if (info.pt != null) {
						mMapView.getController().animateTo(info.pt);
						break;
					}
				}
			} else if (res.getCityListNum() > 0) {
				// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
				String strInfo = "在";
				for (int i = 0; i < res.getCityListNum(); i++) {
					strInfo += res.getCityListInfo(i).city;
					strInfo += ",";
				}
				strInfo += "找到结果";
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						strInfo, Toast.LENGTH_LONG).show();
			}

			if (arraylist != null && arraylist.size() > 0) {
				Get_Drive_Route_ResultList(arraylist);
				sd.animateOpen(); // 打开抽屉
			}

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

	public List<Map<String, Object>> iList;
	// 驾车 和 步行 适配器
	private baidu_map_driver_route_ListAdapter listItemAdapter = new baidu_map_driver_route_ListAdapter();

	// 获取自驾和步行路线信息列表及点击事件监听
	private void Get_Drive_Route_ResultList(ArrayList<OverlayItem> list) {
		mSearch_Result_List.setVisibility(View.VISIBLE);
		iList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<GeoPoint> data = new ArrayList<GeoPoint>();
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			OverlayItem item = list.get(i);
			// 名称 + 地址
			String xxString = item.getSnippet().toString();
			Log.i("xx", "xxString=" + xxString);
			map.put("title",
					item.getTitle() + " "
							+ item.getSnippet().split("\\^")[1].toString()
							+ "米" + "\n" + "地址："
							+ item.getSnippet().split("\\^")[0].toString());
			map.put("point", item.getPoint());
			map.put("imgv_img", getResources().getDrawable(R.drawable.icon_gcoding));  //列表图标
			
			
			map.put("showname", item.getTitle());
			
			
			iList.add(map);
			data.add(item.getPoint());
		}

		listItemAdapter
				.setContext(baidu_map_jiemian_search_show_list_Activity.this);
		listItemAdapter.setList(iList);
		mSearch_Result_List.setAdapter(listItemAdapter);
		mSearch_Result_List.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int index,
					long arg3) {
				GeoPoint staPoint = (GeoPoint) iList.get(index).get("point");
				String showname = (String) iList.get(index).get("showname");
				pop.hidePop();
				SetPopView(staPoint, showname);
				mMapView.getController().animateTo(staPoint);
				sd.animateClose(); // 抽屉
			}
		});
	}

	/**
	 * 搜索后 弹出的信息
	 * @param pt
	 * @param textName
	 */
	public void SetPopView(final GeoPoint pt, final String textName) {
		View mPopView = LayoutInflater.from(this).inflate(R.layout.act_paopao2,
				null);// 获取要转换的View资源
		popupText = (TextView) mPopView.findViewById(R.id.textcache);
		popupText.setTextSize(14);
		popupText.setText(textName);
		popupText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						textName, Toast.LENGTH_SHORT).show();
				
				Intent intent =new Intent();
				intent.setClass(baidu_map_jiemian_search_show_list_Activity.this, baidu_map_infoPage_Activity.class);
				intent.putExtra("showname", textName);
				intent.putExtra("toLat",String.valueOf(pt.getLatitudeE6() / 1e6) );
				intent.putExtra("toLon", String.valueOf(pt.getLongitudeE6() /1e6));
				startActivity(intent);
				
			}
		});

		ImageView img = (ImageView) mPopView.findViewById(R.id.popright); // 到这里去

		ImageView img_se = (ImageView) mPopView.findViewById(R.id.popleft); // 附近搜索
		// 到这里去
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"到这里去", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(
						baidu_map_jiemian_search_show_list_Activity.this,
						baidu_map_jiemian_3_Activity.class);

				intent.putExtra("toName", textName.toString()); // 目的地名称
				intent.putExtra("toLon",
						String.valueOf(pt.getLongitudeE6() / 1e6));// 目的地经度
				intent.putExtra("toLat",
						String.valueOf(pt.getLatitudeE6() / 1e6));// 目的地纬度

				startActivity(intent);

			}
		});
		// 附近搜索
		img_se.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"附近搜索", Toast.LENGTH_SHORT).show();
			}
		});

		pop.showPopup(mPopView, pt, 0);
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

			myLoadString = location.getAddrStr(); // 有可能为null
			// 如果获取不到 就只能根据 经纬度 去查询 实现了
			Log.i("xx", "myLoadString=" + myLoadString);
			Log.i("xx", "经度2=" + locData.longitude + ",纬度=" + locData.latitude);

			if (myLoadString == null) {
				GeoPoint gp = new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6));
				mMKSearch.reverseGeocode(gp); // mSearch为 MKSearch对象
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

	/*
	 * 点点 变颜色的那种
	 * 搜索使用 要处理overlay点击事件时需要继承ItemizedOverlay 不处理点击事件时可直接生成ItemizedOverlay.
	 */
	class OverlayTestA extends ItemizedOverlay<OverlayItem> {
		// 用MapView构造ItemizedOverlay
		public OverlayTestA(Drawable marker, MapView mapView) {
			super(marker, mapView);
		}

		protected boolean onTap(final int index) {
			// 在此处理item点击事件
	
			View mPopView = getLayoutInflater().inflate(R.layout.act_paopao2,
					null);
			popupText = (TextView) mPopView.findViewById(R.id.textcache);
			
			popupText.setText(arraylist.get(index).getTitle().toString());
			
			pop.showPopup(mPopView, arraylist.get(index).getPoint(), 5);

			popupText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(
							baidu_map_jiemian_search_show_list_Activity.this,
							arraylist.get(index).getTitle(),
							Toast.LENGTH_SHORT).show();
				}
			});

			ImageView img = (ImageView) mPopView.findViewById(R.id.popright); // 到这里去

			ImageView img_se = (ImageView) mPopView.findViewById(R.id.popleft); // 附近搜索

			// 到这里去
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(
							baidu_map_jiemian_search_show_list_Activity.this,
							"到这里去", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(
							baidu_map_jiemian_search_show_list_Activity.this,
							baidu_map_jiemian_3_Activity.class);

					intent.putExtra("toName", popupText.getText().toString()); // 目的地名称
					intent.putExtra(
							"toLon",
							String.valueOf(arraylist.get(index).getPoint()
									.getLongitudeE6() / 1e6));// 目的地经度
					intent.putExtra(
							"toLat",
							String.valueOf(arraylist.get(index).getPoint()
									.getLatitudeE6() / 1e6));// 目的地纬度

					startActivity(intent);

				}
			});
			// 附近搜索
			img_se.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Toast.makeText(
							baidu_map_jiemian_search_show_list_Activity.this,
							"附近搜索", Toast.LENGTH_SHORT).show();
				}
			});

			return true;

		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// 在此处理MapView的点击事件，当返回 true时
			super.onTap(pt, mapView);
			mapView.getController().animateTo(pt); // 定位到点
			if (pop != null) {
				pop.hidePop();
			}
			return false;
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

				createPaopao(new GeoPoint((int) (locData.latitude * 1e6),
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
class MyLocationMapViewXYZVSY extends MapView {
	static PopupOverlay pop = null;// 弹出泡泡图层，点击图标使用
	static View mPopView = null;// 弹出泡泡图层，点击图标使用

	public MyLocationMapViewXYZVSY(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapViewXYZVSY(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapViewXYZVSY(Context context, AttributeSet attrs,
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
