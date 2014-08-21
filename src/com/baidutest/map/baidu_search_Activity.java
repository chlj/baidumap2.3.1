package com.baidutest.map;
import java.util.ArrayList;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
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
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 9.poi 地图搜索 com.baidu.mapapi.map.MapView
 * 
 * 
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 * 同时展示了如何使用ItemizedOverlay在地图上标注结果点
 * 
 * 
 * @author Administrator
 * 
 */

public class baidu_search_Activity extends Activity implements OnClickListener {

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

	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index;
	private ArrayList<OverlayItem> arraylist = null;

	public View mPopView = null;
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用
	private TextView popupText = null;// 泡泡view
	private ArrayList<OverlayItem> mItems = null;
	private OverlayItem mCurItem = null;
	private MyOverlays mOverlays = null;
	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController = null;

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

		setContentView(R.layout.baidumap_activity_search);

		arraylist = new ArrayList<OverlayItem>();

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);

		// mMapView.getController().enableClick(true);
		// mMapView.getController().setZoom(12);
		// // 设置启用内置的缩放控件
		// mMapView.setBuiltInZoomControls(true);

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
		mMapController.setZoom(12);

		mMapView.setBuiltInZoomControls(true);

		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetAddrResult(MKAddrInfo res, int error) {

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

			/**
			 * 在此处理poi搜索结果
			 */
			@Override
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(baidu_search_Activity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}
				// 将地图移动到第一个POI中心点
				if (res.getCurrentNumPois() > 0) {
					// 1. 将poi结果显示到地图上
					// MyPoiOverlay poiOverlay = new MyPoiOverlay(
					// baidu_search_Activity.this, mMapView, mSearch);
					// poiOverlay.setData(res.getAllPoi());
					// mMapView.getOverlays().clear();
					// mMapView.getOverlays().add(poiOverlay);
					// mMapView.refresh();

					// 2. 自定义图标 将结果显示到地图上面

					Drawable marker = baidu_search_Activity.this.getResources()
							.getDrawable(R.drawable.icon_geo);
					marker.setBounds(0, 0, marker.getIntrinsicWidth(),
							marker.getIntrinsicHeight()); // 为maker定义位置和边界
					int count = res.getAllPoi().size();
					for (int i = 0; i < count; i++) {
						OverlayItem item = new OverlayItem(res.getAllPoi().get(
								i).pt, res.getAllPoi().get(i).uid, res
								.getAllPoi().get(i).name);

						arraylist.add(item);
					}
					OverlayTest itemOverlay = new OverlayTest(marker, mMapView);
					mMapView.getOverlays().clear();
					itemOverlay.addItem(arraylist);
					mMapView.getOverlays().add(itemOverlay);
					mMapView.refresh();
					//弹出气泡
					PopupClickListener popListener = new PopupClickListener() {
						@Override
						public void onClickedPopup(int index) {

						}
					};
					pop = new PopupOverlay(mMapView, popListener);
					

					// 3. 使用 baidu_more_mark_Activity.java 中的实现
					// Drawable marker =
					// baidu_search_Activity.this.getResources()
					// .getDrawable(R.drawable.icon_geo);
					// marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					// marker.getIntrinsicHeight()); // 为maker定义位置和边界
					// mOverlays = new MyOverlays(getResources().getDrawable(
					// R.drawable.icon_geo), mMapView);
					// int count = res.getAllPoi().size();
					//
					// for (int i = 0; i < count; i++) {
					// OverlayItem item = new OverlayItem(res.getAllPoi().get(
					// i).pt, res.getAllPoi().get(i).uid, res
					// .getAllPoi().get(i).name);
					// item.setAnchor(item.ALING_CENTER); // 图片居中 和点重合在一起
					// item.setMarker(marker);
					//
					// mOverlays.addItem(item);
					//
					// }
					// mItems = new ArrayList<OverlayItem>();
					// mItems.addAll(mOverlays.getAllItem());
					// mMapView.getOverlays().add(mOverlays);
					// mMapView.refresh();
					// PopupClickListener popListener = new PopupClickListener()
					// {
					// @Override
					// public void onClickedPopup(int index) {
					//
					// }
					// };
					// pop = new PopupOverlay(mMapView, popListener);

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
					Toast.makeText(baidu_search_Activity.this, strInfo,
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
				// TODO Auto-generated method stub

			}

			/**
			 * 更新建议列表
			 */
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				if (res == null || res.getAllSuggestions() == null) {
					return;
				}
				sugAdapter.clear();
				for (MKSuggestionInfo info : res.getAllSuggestions()) {
					if (info.key != null)
						sugAdapter.add(info.key);
				}
				sugAdapter.notifyDataSetChanged();

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

		btn1 = (Button) findViewById(R.id.btn1); // 查询
		btn1.setOnClickListener(this);

		btn2 = (Button) findViewById(R.id.btn2); // 下一组
		btn2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btn1:
			arraylist.clear();
			searchButtonProcess(v);
			break;
		case R.id.btn2:
			arraylist.clear();
			goToNextPage(v);
			break;
		default:
			break;
		}
	}

	/**
	 * 影响搜索按钮点击事件
	 * 
	 * @param v
	 */
	public void searchButtonProcess(View v) {
		EditText editCity = (EditText) findViewById(R.id.city);
		EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
		mSearch.poiSearchInCity(editCity.getText().toString(), editSearchKey
				.getText().toString()); // 城市poi检索.
		// public int poiSearchInCity(java.lang.String city, java.lang.String
		// key)

	}

	public void goToNextPage(View v) {
		// 搜索下一组poi
		int flag = mSearch.goToPoiPage(++load_Index); // 获取指定页的的poi结果.
		if (flag != 0) {
			Toast.makeText(baidu_search_Activity.this, "先搜索开始，然后再搜索下一组数据",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 3.
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyOverlays extends ItemizedOverlay {

		public MyOverlays(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(final int index) {
			final OverlayItem item = getItem(index);
			mCurItem = item;
			Toast.makeText(
					baidu_search_Activity.this,
					"index=" + String.valueOf(index) + ",item.getSnippet()="
							+ item.getSnippet(), Toast.LENGTH_LONG).show();

			mPopView = getLayoutInflater().inflate(R.layout.act_paopao2, null);
			popupText = (TextView) mPopView.findViewById(R.id.textcache);

			mMapController.animateTo(item.getPoint()); // 定位在中央

			popupText.setText(item.getSnippet());

			pop.showPopup(mPopView, item.getPoint(), 5);

			popupText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_Activity.this,
							item.getSnippet().toString(), Toast.LENGTH_SHORT)
							.show();
				}
			});

			// 动态加载布局
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop != null) {
				pop.hidePop();
			}
			return false;
		}

	}

	/*
	 * *2. 要处理overlay点击事件时需要继承ItemizedOverlay 不处理点击事件时可直接生成ItemizedOverlay.
	 */
	class OverlayTest extends ItemizedOverlay<OverlayItem> {
		// 用MapView构造ItemizedOverlay
		public OverlayTest(Drawable marker, MapView mapView) {
			super(marker, mapView);
		}

		protected boolean onTap(final int index) {
			// 在此处理item点击事件

//			String aa = "index=" + index + "*"
//					+ arraylist.get(index).getTitle() + "*"
//					+ arraylist.get(index).getSnippet();
//			Toast.makeText(baidu_search_Activity.this, aa, Toast.LENGTH_SHORT)
//					.show();

			mPopView = getLayoutInflater().inflate(R.layout.act_paopao2, null);
			popupText = (TextView) mPopView.findViewById(R.id.textcache);

			popupText.setText(arraylist.get(index).getSnippet());

			pop.showPopup(mPopView, arraylist.get(index).getPoint(), 5);

			popupText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_Activity.this,
							arraylist.get(index).getSnippet(),
							Toast.LENGTH_SHORT).show();
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

	/**
	 * 搜索
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyPoiOverlay extends PoiOverlay {
		MKSearch mSearch;

		public MyPoiOverlay(Activity activity, MapView mapView, MKSearch search) {
			super(activity, mapView);
			mSearch = search;
		}

		@Override
		protected boolean onTap(int i) {
			super.onTap(i);
			MKPoiInfo info = getPoi(i);
			if (info.hasCaterDetails) {
				mSearch.poiDetailSearch(info.uid);
			}
			return true;
		}
	}
}
