package com.baidutest.map;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class baidu_map_infoPage_Activity extends Activity {

	private Button btn_back;
	private String showName = ""; // 显示的名称
	private GeoPoint mGeoPoint = null;// 传过来的坐标点
	private TextView txtTvName, txtTvAddress;// 名称,地址

	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener 用于处理地图事件回调
	 */
	private MKMapViewListener mMapListener = null;
	private MKSearch mMKSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private MapApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		app = (MapApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey,
					new MapApplication.MyGeneralListener());
		}

		setContentView(R.layout.baidumap_activity_info_page);
		findView();
		initData();
	}

	/**
	 * 找到控件
	 */
	public void findView() {
		btn_back = (Button) findViewById(R.id.back);
		txtTvName = (TextView) findViewById(R.id.TvName);// 名称
		txtTvAddress= (TextView) findViewById(R.id.TvAddress);
	}

	/**
	 * 初始化数据
	 */
	public void initData() {
		showName = this.getIntent().getStringExtra("showname");// 显示的名称
		txtTvName.setText(showName.toString());

		String lon = this.getIntent().getStringExtra("toLon").toString();// 经度
		String lat = this.getIntent().getStringExtra("toLat").toString();// 纬度
		mGeoPoint = new GeoPoint((int) (Double.valueOf(lat) * 1e6),
				(int) (Double.valueOf(lon) * 1e6));// 传过来的坐标点

		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();

			}
		});

		// 初始化MKSearch
		mMKSearch = new MKSearch();

		mMKSearch.init(app.mBMapManager, new MySearchListener());
		// 根据地理坐标点获取地址信息 异步函数，返回结果在MKSearchListener里的onGetAddrResult方法通知
		mMKSearch.reverseGeocode(mGeoPoint);

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
			txtTvAddress.setText(result.strAddr.toString());

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
	 * 得到经纬度 得到一个点对象
	 * 
	 * @param x
	 * @param y
	 * @return
	 */

	public GeoPoint getPointByXY(double x, double y) {
		return new GeoPoint((int) (y * 1E6), (int) (x * 1E6)); // (纬度, 经度)
	}

}
