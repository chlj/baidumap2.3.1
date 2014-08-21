package com.baidutest.map;

import java.util.ArrayList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Ground;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**6. 给多个点进行标记
 * com.baidu.mapapi.map.MapView
 * 
 * @author Administrator
 * 
 */

public class baidu_more_mark_Activity extends Activity implements
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
	 * MapView 是地图主控件
	 */
	private MapView mMapView = null;
	public View mPopView = null;
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用
	private TextView popupText = null;// 泡泡view
	private ArrayList<OverlayItem> mItems = null;
	private OverlayItem mCurItem = null;
	private MyOverlay mOverlay = null;



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

		setContentView(R.layout.baidumap_activity_more_mark);
		mMapView = (MapView) findViewById(R.id.bmapView);

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
		mMapController.setZoom(19);

		// 设置启用内置的缩放控件
		mMapView.setBuiltInZoomControls(true);

		initOverlay();

		findview(); // 找到控件

	}

	public void initOverlay() {

		/**
		 * 创建图标资源（用于显示在overlayItem所标记的位置）
		 */
		Drawable marker = this.getResources().getDrawable(R.drawable.icon_geo);
		// 为maker定义位置和边界
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources()
				.getDrawable(R.drawable.icon_geo), mMapView);
		/**
		 * 准备overlay 数据
		 */
		GeoPoint p1 = pbC.getPointByXY(112.898416, 29.821723); // (经度,纬度)
		OverlayItem item1 = new OverlayItem(p1, "覆盖物1", "覆盖物a");
		item1.setAnchor(item1.ALING_CENTER); // 图片居中 和点重合在一起
		/**
		 * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
		 */
		item1.setMarker(marker);

		GeoPoint p2 = pbC.getPointByXY(112.899111, 29.822437); // (经度,纬度)
		OverlayItem item2 = new OverlayItem(p2, "覆盖物2", "覆盖物b");
		item2.setAnchor(item2.ALING_CENTER); // 图片居中 和点重合在一起
		item2.setMarker(marker);

		/**
		 * 将item 添加到overlay中 注意： 同一个itme只能add一次
		 */
		mOverlay.addItem(item1);
		mOverlay.addItem(item2);
		/**
		 * 保存所有item，以便overlay在reset后重新添加
		 */
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());

		// 用经纬度初始化中心点 (必须初始化中心点)
		GeoPoint point = pbC.getPointByXY(112.898416, 29.821723); // (经度,纬度)
		mMapController.setCenter(point); // 设置中心点

		/**
		 * 将overlay 添加至MapView中
		 */
		mMapView.getOverlays().add(mOverlay);

		/**
		 * 刷新地图
		 */
		mMapView.refresh();

		/**
		 * 向地图添加自定义View.
		 */

		/**
		 * 创建一个popupoverlay
		 */
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};
		pop = new PopupOverlay(mMapView, popListener);

	}

	/**
	 * 清除所有Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {
		mOverlay.removeAll();

		if (pop != null) {
			pop.hidePop();
		}
		mMapView.refresh();
	}

	/**
	 * 重新添加Overlay
	 * 
	 * @param view
	 */
	public void resetOverlay(View view) {
		clearOverlay(null);
		// 重新add overlay
		mOverlay.addItem(mItems);
		mMapView.refresh();
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

	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(final int index) {
			final OverlayItem item = getItem(index);
			mCurItem = item;
			Toast.makeText(
					baidu_more_mark_Activity.this,
					"index=" + String.valueOf(index) + ",item.getSnippet()="
							+ item.getSnippet(), Toast.LENGTH_LONG).show();


			mPopView = getLayoutInflater().inflate(R.layout.act_paopao2, null);
			popupText = (TextView) mPopView.findViewById(R.id.textcache);
			
			popupText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
				
					
					if (index == 0) {
						Toast.makeText(baidu_more_mark_Activity.this,
								"监利县人民医院", Toast.LENGTH_SHORT).show();
					} else if (index == 1) {
						Toast.makeText(baidu_more_mark_Activity.this,
								"监利县实验小学", Toast.LENGTH_SHORT).show();

					}
				}
			});

			mMapController.animateTo(item.getPoint()); // 定位在中央
			if (index == 0) {
				popupText.setText("监利县人民医院");
			} else if (index == 1) {
				popupText.setText("监利县实验小学");
			}
			pop.showPopup(mPopView, item.getPoint(), 5);

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

}
