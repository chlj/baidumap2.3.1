package com.baidutest.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidutest.map.baidu_single_mark_Activity.locationOverlay;
import com.example.testdemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 12.GPS
 * 
 * @author Administrator
 * 
 */

public class baidu_search_GPS_Activity extends Activity implements
		OnClickListener {

	private Button mBtnSearch, btn2, mBtnPre, mBtnNext, btn_goback;
	private EditText txt1, txt2;

	int nodeIndex = -2;// 节点索引,供浏览节点时使用
	MKRoute route = null;// 保存驾车/步行路线数据的变量，供浏览节点时使用
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用
	private TextView popupText = null;// 泡泡view
	private View viewCache = null;
	private List<String> busLineIDList = null; // 公交车线路list 字符串
	int busLineIndex = 0;

	// 地图相关，使用继承MapView的MyBusLineMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null; // 地图View
	// 搜索相关
	MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用

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

		setContentView(R.layout.baidumap_activity_search_gps);
		findView();

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(12);

		busLineIDList = new ArrayList<String>();

		// 创建 弹出泡泡图层
		createPaopao();

		// 地图点击事件处理
		mMapView.regMapTouchListner(new MKMapTouchListener() {
			@Override
			public void onMapClick(GeoPoint point) {
				// 在此处理地图点击事件
				// 消隐pop
				if (pop != null) {
					pop.hidePop();
				}
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {

			}

			@Override
			public void onMapLongClick(GeoPoint point) {

			}

		});
		
		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
			}

			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(baidu_search_GPS_Activity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}

				// 找到公交路线poi node
				MKPoiInfo curPoi = null;
				int totalPoiNum = res.getCurrentNumPois();
				// 遍历所有poi，找到类型为公交线路的poi
				busLineIDList.clear();
				for (int idx = 0; idx < totalPoiNum; idx++) {
					if (2 == res.getPoi(idx).ePoiType) {
						// poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路
						curPoi = res.getPoi(idx);
						// 使用poi的uid发起公交详情检索
						busLineIDList.add(curPoi.uid);
						Log.i("xx", "curPoi.uid=" + curPoi.uid);

					}
				}
				SearchNextBusline(); // 查询下一条

				// 没有找到公交信息
				if (curPoi == null) {
					Toast.makeText(baidu_search_GPS_Activity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}
				route = null;
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}

			/**
			 * 获取公交路线结果，展示公交线路
			 */
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
				if (iError != 0 || result == null) {
					Toast.makeText(baidu_search_GPS_Activity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}

				RouteOverlay routeOverlay = new RouteOverlay(
						baidu_search_GPS_Activity.this, mMapView);
				// 此处仅展示一个方案作为示例
				routeOverlay.setData(result.getBusRoute());

				// 自定义 起终点的 图标
				routeOverlay.setStMarker(getResources().getDrawable(
						R.drawable.icon_st));
				routeOverlay.setEnMarker(getResources().getDrawable(
						R.drawable.icon_en));

				// 清除其他图层
				mMapView.getOverlays().clear();
				// 添加路线图层
				mMapView.getOverlays().add(routeOverlay);

				// 刷新地图使生效
				mMapView.refresh();

				// 移动地图到起点
				mMapView.getController().animateTo(
						result.getBusRoute().getStart());

				// 将路线数据保存给全局变量
				route = result.getBusRoute();
				// 重置路线节点索引，节点浏览时使用
				nodeIndex = -1;

				mBtnPre.setVisibility(View.VISIBLE); // 底下 1
				mBtnNext.setVisibility(View.VISIBLE);// 底下 2

				// 查询结果 Toast出来
				Toast.makeText(baidu_search_GPS_Activity.this,
						result.getBusName(), Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub

			}

		});

		// GPS定位
		openGPSSettings();
//
//		// 为获取地理位置信息时设置查询条件
		editText = (EditText) findViewById(R.id.editText);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String bestProvider = lm.getBestProvider(getCriteria(), true);
		
		
//		// 获取位置信息
//		// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
		//Location location = lm.getLastKnownLocation(bestProvider); //2.3.4报错   为 null 
		
		  //从GPS_PROVIDER获取最近的定位信息  
		Location location=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);   //GPS定位为null
	    if(location==null){
	          location=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // 网络定位为null
	     }
	    
        updateView(location);  
        
		
	
//		// 监听状态
		lm.addGpsStatusListener(listener);
		// 绑定监听，有4个参数
		// 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
		// 参数2，位置信息更新周期，单位毫秒
		// 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
		// 参数4，监听
		// 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

		// 1秒更新一次，或最小位移变化超过1米更新一次；
		// 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(60000);然后执行handler.sendMessage(),更新位置
		
		   //设置每60秒，每移动十米向LocationProvider获取一次GPS的定位信息  
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
				locationListener);

	}

	private LocationManager lm;
	private static final String TAG = "xx";
	private EditText editText;

	// 位置监听
	private LocationListener locationListener = new LocationListener() {

		/**
		 * 位置信息变化时触发
		 */
		public void onLocationChanged(Location location) {
			//location为变化完的新位置，更新显示
			updateView(location);
			Log.i(TAG, "时间：" + location.getTime());
			Log.i(TAG, "经度：" + location.getLongitude());
			Log.i(TAG, "纬度：" + location.getLatitude());
			Log.i(TAG, "海拔：" + location.getAltitude());
		}

		/**
		 * GPS状态变化时触发
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS状态为可见时
			case LocationProvider.AVAILABLE:
				Log.i(TAG, "当前GPS状态为可见状态");
				break;
			// GPS状态为服务区外时
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG, "当前GPS状态为服务区外状态");
				break;
			// GPS状态为暂停服务时
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG, "当前GPS状态为暂停服务状态");
				break;
			}
		}

		/**
		 * GPS开启时触发
		 */
		public void onProviderEnabled(String provider) {
			updateView(lm.getLastKnownLocation(provider));
		}

		/**
		 * GPS禁用时触发
		 */
		public void onProviderDisabled(String provider) {
			updateView(null);
		}

	};

	// 状态监听
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "第一次定位");
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.i(TAG, "卫星状态改变");
				// 获取当前状态
				GpsStatus gpsStatus = lm.getGpsStatus(null);
				// 获取卫星颗数的默认最大值
				int maxSatellites = gpsStatus.getMaxSatellites();
				// 创建一个迭代器保存所有卫星
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				System.out.println("搜索到：" + count + "颗卫星");
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "定位启动");
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "定位结束");
				break;
			}
		};
	};

	/**
	 * 实时更新文本内容
	 * 
	 * @param location
	 */
	private void updateView(Location location) {
		if (location != null) {
			editText.setText("设备位置信息\n经度：");
			editText.append(String.valueOf(location.getLongitude()));
			editText.append("\n纬度：");
			editText.append(String.valueOf(location.getLatitude()));

			double latitude = location.getLatitude();// 纬度
			double longitude = location.getLongitude();// 经度
			GeoPoint p = new GeoPoint((int) (latitude * 1E6),(int) (longitude * 1E6));
			
			
			mMapView.getController().setZoom(19);
			mMapView.getController().setCenter(p);
			
			
			/** 
	         * 创建图标资源（用于显示在overlayItem所标记的位置） 
	         */  
	        Drawable marker = this.getResources().getDrawable(R.drawable.icon_geo);  
	        // 为maker定义位置和边界  
	        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
	        
			// 定位图层初始化
	        locationOverlay	myLocationOverlay = new locationOverlay(mMapView);
			// 设置定位数据

	        LocationData	locData = new LocationData();
			locData.longitude = longitude;
			locData.latitude = latitude;
			
			

			myLocationOverlay.setData(locData);
			myLocationOverlay.setMarker(marker); // 使用自定义的图标资源
			
			// 添加定位图层
			mMapView.getOverlays().add(myLocationOverlay);

			myLocationOverlay.enableCompass();
			// 修改定位数据后刷新图层生效
			mMapView.refresh();
			
			
			
			
		} else {
			// 清空EditText对象
			editText.getEditableText().clear();
		}
	}

	//继承MyLocationOverlay重写dispatchTap实现点击处理
		 class locationOverlay extends MyLocationOverlay {
				public locationOverlay(MapView mapView) {
					super(mapView);
					// TODO Auto-generated constructor stub
				}

				@Override
				protected boolean dispatchTap() {
					// 处理点击事件,弹出泡泡
				

					return true;

				}

			}
		 
	/**
	 * 返回查询条件
	 * 
	 * @return
	 */
	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		// 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 设置是否要求速度
		criteria.setSpeedRequired(false);
		// 设置是否允许运营商收费
		criteria.setCostAllowed(false);
		// 设置是否需要方位信息
		criteria.setBearingRequired(false);
		// 设置是否需要海拔信息
		criteria.setAltitudeRequired(false);
		// 设置对电源的需求
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}

	private void openGPSSettings() {
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			// Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("GPS状态");
		builder.setMessage("当前GPS不可用，是否设置GPS？");
		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				try {
					startActivity(intent);

				} catch (ActivityNotFoundException ex) {
					intent.setAction(Settings.ACTION_SETTINGS);
					try {
						startActivity(intent);
					} catch (Exception e) {
					}
				}

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create();
		builder.show();
	}

	public void findView() {
		txt1 = (EditText) findViewById(R.id.city);
		txt2 = (EditText) findViewById(R.id.searchkey);

		btn_goback = (Button) findViewById(R.id.back);
		mBtnSearch = (Button) findViewById(R.id.search);
		btn2 = (Button) findViewById(R.id.nextline);
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);

		btn_goback.setOnClickListener(this);
		mBtnSearch.setOnClickListener(this);
		btn2.setOnClickListener(this);
		mBtnPre.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);

		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.search:
			// 发起搜索
			SearchButtonProcess(v);
			break;

		case R.id.nextline:
			// 搜索下一条公交线
			SearchNextBusline();
			break;
		case R.id.pre:
			// 浏览路线节点
			nodeClick(v);
			break;

		case R.id.next:
			// 浏览路线节点
			nodeClick(v);
			break;

		default:
			break;
		}
	}

	/**
	 * 发起检索
	 * 
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		busLineIDList.clear();
		busLineIndex = 0;

		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		if (mBtnSearch.equals(v)) {
			EditText editCity = (EditText) findViewById(R.id.city);
			EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
			// 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
			mSearch.poiSearchInCity(editCity.getText().toString(),
					editSearchKey.getText().toString());
		}

	}

	/**
	 * 查询下一条
	 */
	void SearchNextBusline() {
		if (busLineIndex >= busLineIDList.size()) {
			busLineIndex = 0;
		}
		if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
				&& busLineIDList.size() > 0) {
			mSearch.busLineSearch(((EditText) findViewById(R.id.city))
					.getText().toString(), busLineIDList.get(busLineIndex));
			busLineIndex++;
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
		// 泡泡点击响应回调
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};
		pop = new PopupOverlay(mMapView, popListener);

	}

	/**
	 * 节点浏览示例
	 * 
	 * @param v
	 */
	public void nodeClick(View v) {

		if (nodeIndex < -1 || route == null || nodeIndex >= route.getNumSteps())
			return;
		viewCache = getLayoutInflater().inflate(R.layout.act_paopao2, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		// 上一个节点
		if (mBtnPre.equals(v) && nodeIndex > 0) {
			// 索引减
			nodeIndex--;
			// 移动到指定索引的坐标
			mMapView.getController().animateTo(
					route.getStep(nodeIndex).getPoint());
			// 弹出泡泡
			popupText.setText(route.getStep(nodeIndex).getContent());

			// popupText.setBackgroundResource(R.drawable.icon_geo);
			// pop.showPopup(getBitmapFromView(popupText),
			// route.getStep(nodeIndex).getPoint(),
			// 5);

			pop.showPopup(viewCache, route.getStep(nodeIndex).getPoint(), 5);

			popupText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_GPS_Activity.this,
							popupText.getText().toString(), Toast.LENGTH_SHORT)
							.show();

				}
			});

		}
		// 下一个节点
		if (mBtnNext.equals(v) && nodeIndex < (route.getNumSteps() - 1)) {
			// 索引加
			nodeIndex++;

			// 移动到指定索引的坐标
			mMapView.getController().animateTo(
					route.getStep(nodeIndex).getPoint());
			// 弹出泡泡
			popupText.setText(route.getStep(nodeIndex).getContent());
			// popupText.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_geo));
			// pop.showPopup(getBitmapFromView(popupText),
			// route.getStep(nodeIndex).getPoint(),
			// 5);

			pop.showPopup(viewCache, route.getStep(nodeIndex).getPoint(), 5);

			popupText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_GPS_Activity.this,
							popupText.getText().toString(), Toast.LENGTH_SHORT)
							.show();

				}
			});
		}

	}

	/**
	 * 从view 得到图片
	 * 
	 * @param view
	 * @return
	 */
	public Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
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
		mMapView.destroy();
		mSearch.destory();
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

}
