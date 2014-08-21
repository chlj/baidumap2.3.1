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
 * 8.��Ϣ�����껥�� com.baidu.mapapi.map.MapView
 * 
 * 
 * ��demo����չʾ��ν��е�������������õ�ַ�������꣩����������������������������ַ��
 * ͬʱչʾ�����ʹ��ItemizedOverlay�ڵ�ͼ�ϱ�ע�����
 * 
 * 
 * @author Administrator
 * 
 */

public class baidu_point_to_info_Activity extends Activity implements
		OnClickListener {

	/**
	 * ��ʾMapView�Ļ����÷�
	 */
	private Button btn_back;

	// UI���
	Button btn1 = null; // ����ַ����Ϊ����
	Button btn2 = null; // �����귴����Ϊ��ַ

	// ��ͼ���
	MapView mMapView = null; // ��ͼView
	// �������
	MKSearch mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * ʹ�õ�ͼsdkǰ���ȳ�ʼ��BMapManager. BMapManager��ȫ�ֵģ���Ϊ���MapView���ã�����Ҫ��ͼģ�鴴��ǰ������
		 * ���ڵ�ͼ��ͼģ�����ٺ����٣�ֻҪ���е�ͼģ����ʹ�ã�BMapManager�Ͳ�Ӧ������
		 */
		MapApplication app = (MapApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey,
					new MapApplication.MyGeneralListener());
		}
		/**
		 * ����MapView��setContentView()�г�ʼ��,��������Ҫ��BMapManager��ʼ��֮��
		 */

		setContentView(R.layout.baidumap_activity_point_to_info);

		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(19);
		// �����������õ����ſؼ�
		mMapView.setBuiltInZoomControls(true);

		// ��ʼ������ģ�飬ע���¼�����
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("����ţ�%d", error);
					Toast.makeText(baidu_point_to_info_Activity.this, str, Toast.LENGTH_LONG).show();
					return;
				}
				//��ͼ�ƶ����õ�
				mMapView.getController().animateTo(res.geoPt);
				
				//   ���ݽ�����ͣ�������ʶ�ǵ�����뻹�Ƿ�������룬 
				 //  MKAddrInfo.MK_GEOCODE - ������룬�ɽֵ�����ת��Ϊ����ֵ 
				 //  MKAddrInfo.MK_REVERSEGEOCODE  ��������룬������ת��Ϊ�ֵ�����

				if (res.type == MKAddrInfo.MK_GEOCODE){
					//������룺ͨ����ַ���������
					String strInfo = String.format("γ�ȣ�%f ���ȣ�%f", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);
					Log.i("xx","strInfo="+strInfo);
					Toast.makeText(baidu_point_to_info_Activity.this, strInfo, Toast.LENGTH_LONG).show();
				}
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
					//��������룺ͨ������������ϸ��ַ���ܱ�poi
					String strInfo = res.strAddr;
					Log.i("xx","strInfo="+strInfo);
					Toast.makeText(baidu_point_to_info_Activity.this, strInfo, Toast.LENGTH_LONG).show();
					
				}
				//����ItemizedOverlayͼ��������ע�����
				ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(null, mMapView);
				//����Item
				OverlayItem item = new OverlayItem(res.geoPt, "", null);
				//�õ���Ҫ���ڵ�ͼ�ϵ���Դ
				Drawable marker = getResources().getDrawable(R.drawable.icon_geo);  
				//Ϊmaker����λ�úͱ߽�
				marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
				//��item����marker
				item.setMarker(marker);
				//����ƫ���� ����
				item.setAnchor(OverlayItem.ALING_CENTER);
				//��ͼ�������item
				itemOverlay.addItem(item);
				
				//�����ͼ����ͼ��
				mMapView.getOverlays().clear();
				//���һ����עItemizedOverlayͼ��
				mMapView.getOverlays().add(itemOverlay);
				//ִ��ˢ��ʹ��Ч
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
		findview(); // �ҵ��ؼ�

	}

	@Override
	protected void onPause() {
		/**
		 * MapView������������Activityͬ������activity����ʱ�����MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView������������Activityͬ������activity�ָ�ʱ�����MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView������������Activityͬ������activity����ʱ�����MapView.destroy()
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

		btn1 = (Button) findViewById(R.id.btn1); // ��ַ��ѯ
		btn2 = (Button) findViewById(R.id.btn2); // ��γ�Ȳ�ѯ
		
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
	 * ��������
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
			// ��Geo����
			// ���ݵ���������ȡ��ַ��Ϣ �첽���������ؽ����MKSearchListener���onGetAddrResult����֪ͨ
			mSearch.reverseGeocode(ptCenter);

		} else if (btn1.equals(v)) {
			EditText editCity = (EditText) findViewById(R.id.city);
			EditText editGeoCodeKey = (EditText) findViewById(R.id.geocodekey);
			// Geo����
			// ���ݵ�ַ����ȡ��ַ��Ϣ �첽���������ؽ����MKSearchListener���onGetAddrResult����֪ͨ
			// strAddr - ��ַ��
			// city - ������
			mSearch.geocode(editGeoCodeKey.getText().toString(), editCity
					.getText().toString());
		}
	}
}
