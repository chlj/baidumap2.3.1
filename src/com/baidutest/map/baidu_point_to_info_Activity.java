package com.baidutest.map;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
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

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 8.信息与坐标互换 com.baidu.mapapi.map.MapView
 * 
 * 
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 * 同时展示了如何使用ItemizedOverlay在地图上标注结果点
 * 
 * 
 * @author Administrator
 * 
 */

public class baidu_point_to_info_Activity extends Activity implements
		OnClickListener {

	/**
	 * 演示MapView的基本用法
	 */
	private Button btn_back;

	// UI相关
	Button btn1 = null; // 将地址编码为坐标
	Button btn2 = null; // 将坐标反编码为地址

	// 地图相关
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

		setContentView(R.layout.baidumap_activity_point_to_info);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(19);
		// 设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(true);

		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("错误号：%d", error);
					Toast.makeText(baidu_point_to_info_Activity.this, str, Toast.LENGTH_LONG).show();
					return;
				}
				//地图移动到该点
				mMapView.getController().animateTo(res.geoPt);
				
				//   数据结果类型，用来标识是地理编码还是反地理编码， 
				 //  MKAddrInfo.MK_GEOCODE - 地理编码，由街道名称转换为坐标值 
				 //  MKAddrInfo.MK_REVERSEGEOCODE  反地理编码，由坐标转换为街道名称

				if (res.type == MKAddrInfo.MK_GEOCODE){
					//地理编码：通过地址检索坐标点
					String strInfo = String.format("纬度：%f 经度：%f", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);
					Log.i("xx","strInfo="+strInfo);
					Toast.makeText(baidu_point_to_info_Activity.this, strInfo, Toast.LENGTH_LONG).show();
				}
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
					//反地理编码：通过坐标点检索详细地址及周边poi
					String strInfo = res.strAddr;
					Log.i("xx","strInfo="+strInfo);
					Toast.makeText(baidu_point_to_info_Activity.this, strInfo, Toast.LENGTH_LONG).show();
					
				}
				//生成ItemizedOverlay图层用来标注结果点
				ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(null, mMapView);
				//生成Item
				OverlayItem item = new OverlayItem(res.geoPt, "", null);
				//得到需要标在地图上的资源
				Drawable marker = getResources().getDrawable(R.drawable.icon_geo);  
				//为maker定义位置和边界
				marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
				//给item设置marker
				item.setMarker(marker);
				//设置偏移量 居中
				item.setAnchor(OverlayItem.ALING_CENTER);
				//在图层上添加item
				itemOverlay.addItem(item);
				
				//清除地图其他图层
				mMapView.getOverlays().clear();
				//添加一个标注ItemizedOverlay图层
				mMapView.getOverlays().add(itemOverlay);
				//执行刷新使生效
				mMapView.refresh();

			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub

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

			}

		});
		findview(); // 找到控件

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

		btn1 = (Button) findViewById(R.id.btn1); // 地址查询
		btn2 = (Button) findViewById(R.id.btn2); // 经纬度查询
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btn1:
			SearchButtonProcess(v);
			break;
		case R.id.btn2:
			SearchButtonProcess(v);
			break;
		default:
			break;
		}

	}

	/**
	 * 发起搜索
	 * 
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		if (btn2.equals(v)) {
			EditText lat = (EditText) findViewById(R.id.lat);
			EditText lon = (EditText) findViewById(R.id.lon);
			GeoPoint ptCenter = new GeoPoint((int) (Float.valueOf(lat.getText()
					.toString()) * 1e6), (int) (Float.valueOf(lon.getText()
					.toString()) * 1e6));
			// 反Geo搜索
			// 根据地理坐标点获取地址信息 异步函数，返回结果在MKSearchListener里的onGetAddrResult方法通知
			mSearch.reverseGeocode(ptCenter);

		} else if (btn1.equals(v)) {
			EditText editCity = (EditText) findViewById(R.id.city);
			EditText editGeoCodeKey = (EditText) findViewById(R.id.geocodekey);
			// Geo搜索
			// 根据地址名获取地址信息 异步函数，返回结果在MKSearchListener里的onGetAddrResult方法通知
			// strAddr - 地址名
			// city - 城市名
			mSearch.geocode(editGeoCodeKey.getText().toString(), editCity
					.getText().toString());
		}
	}
}
