package com.baidutest.map;

import android.content.Context;

import com.baidu.mapapi.BMapManager;
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

public class baidu_getAddressByLonLat {
	
	
	private MapApplication app;
	private MKSearch mMKSearch = null; // 搜索模块，也可去掉地图模块独立使用
	public baidu_getAddressByLonLat (Context  context){
		init(context);
		
	}
	
	public  void init( Context  context){
		app = (MapApplication) context;
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(context);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey,
					new MapApplication.MyGeneralListener());
		}
		mMKSearch= new MKSearch();
	}

	
	public  String getgetAddressByLonLat(int lon,int lat){
		 MKSearch search = new MKSearch();
         search.init(app.mBMapManager, new MySearchListener());
         search.reverseGeocode(new GeoPoint(lat ,lon));
         
         
		return "";
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

			 // popupText.setText(sb.toString());

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
	 * 
	 */
}
