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
	private String showName = ""; // ��ʾ������
	private GeoPoint mGeoPoint = null;// �������������
	private TextView txtTvName, txtTvAddress;// ����,��ַ

	/**
	 * ��MapController��ɵ�ͼ����
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener ���ڴ����ͼ�¼��ص�
	 */
	private MKMapViewListener mMapListener = null;
	private MKSearch mMKSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	private MapApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager. BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
		 * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
		 */
		app = (MapApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey,
					new MapApplication.MyGeneralListener());
		}

		setContentView(R.layout.baidumap_activity_info_page);
		findView();
		initData();
	}

	/**
	 * �ҵ��ؼ�
	 */
	public void findView() {
		btn_back = (Button) findViewById(R.id.back);
		txtTvName = (TextView) findViewById(R.id.TvName);// ����
		txtTvAddress= (TextView) findViewById(R.id.TvAddress);
	}

	/**
	 * ��ʼ������
	 */
	public void initData() {
		showName = this.getIntent().getStringExtra("showname");// ��ʾ������
		txtTvName.setText(showName.toString());

		String lon = this.getIntent().getStringExtra("toLon").toString();// ����
		String lat = this.getIntent().getStringExtra("toLat").toString();// γ��
		mGeoPoint = new GeoPoint((int) (Double.valueOf(lat) * 1e6),
				(int) (Double.valueOf(lon) * 1e6));// �������������

		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();

			}
		});

		// ��ʼ��MKSearch
		mMKSearch = new MKSearch();

		mMKSearch.init(app.mBMapManager, new MySearchListener());
		// ���ݵ���������ȡ��ַ��Ϣ �첽���������ؽ����MKSearchListener���onGetAddrResult����֪ͨ
		mMKSearch.reverseGeocode(mGeoPoint);

	}

	/**
	 * ʵ��MKSearchListener�ӿ�,����ʵ���첽�������񣬵õ��������
	 * 
	 * @author liufeng
	 */
	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int arg1) {
			// ���ص�ַ��Ϣ�������
			if (result == null) {
				return;
			}
			StringBuffer sb = new StringBuffer();
			// ��γ������Ӧ��λ��
			sb.append(result.strAddr).append("*"); // ��ַ��
			sb.append(result.strBusiness); // ��Ȧ����

			// �жϸõ�ַ�����Ƿ���POI��Point of Interest,����Ȥ�㣩
			// if (null != result.poiList) {
			// // �������е���Ȥ����Ϣ
			// for (MKPoiInfo poiInfo : result.poiList) {
			// sb.append("----------------------------------------")
			// .append("/n");
			// sb.append("���ƣ�").append(poiInfo.name).append("/n");
			// sb.append("��ַ��").append(poiInfo.address).append("/n");
			// sb.append("���ȣ�")
			// .append(poiInfo.pt.getLongitudeE6() / 1000000.0f)
			// .append("/n");
			// sb.append("γ�ȣ�")
			// .append(poiInfo.pt.getLatitudeE6() / 1000000.0f)
			// .append("/n");
			// sb.append("�绰��").append(poiInfo.phoneNum).append("/n");
			// sb.append("�ʱࣺ").append(poiInfo.postCode).append("/n");
			// // poi���ͣ�0����ͨ�㣬1������վ��2��������·��3������վ��4��������·
			// sb.append("���ͣ�").append(poiInfo.ePoiType).append("/n");
			// }
			// }

			// ����ַ��Ϣ����Ȥ����Ϣ��ʾ��TextView��
			txtTvAddress.setText(result.strAddr.toString());

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int arg1) {
			// ���ع�����������Ϣ�������

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int arg1) {
			// ���ؼݳ�·���������

		}

		@Override
		public void onGetPoiDetailSearchResult(int result, int arg1) {
			// ����poi������Ϣ�����Ľ��

		}

		@Override
		public void onGetPoiResult(MKPoiResult result, int arg1, int arg2) {
			// ����poi�������

		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int arg1,
				int arg2) {
			// ���ط���̴����.

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult result, int arg1) {
			// �����������Ϣ�������

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int arg1) {
			// ���ع����������

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int arg1) {
			// ���ز���·���������

		}

	}

	/**
	 * �õ���γ�� �õ�һ�������
	 * 
	 * @param x
	 * @param y
	 * @return
	 */

	public GeoPoint getPointByXY(double x, double y) {
		return new GeoPoint((int) (y * 1E6), (int) (x * 1E6)); // (γ��, ����)
	}

}
