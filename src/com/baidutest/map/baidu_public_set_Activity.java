package com.baidutest.map;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 2. ��ͼģʽ, ���ſؼ�,ָ����γ��
 * @author Administrator
 *
 */
public class baidu_public_set_Activity extends Activity implements
		OnClickListener {

	/**
	 * ��ʾMapView�Ļ����÷�
	 */
	private Button btn_back;

	final static String TAG = "xx";
	/**
	 * MapView �ǵ�ͼ���ؼ�
	 */
	private MapView mMapView = null;
	/**
	 * ��MapController��ɵ�ͼ����
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener ���ڴ����ͼ�¼��ص�
	 */
	MKMapViewListener mMapListener = null;

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

		setContentView(R.layout.baidumap_activity_public_set);

		mMapView = (MapView) findViewById(R.id.bmapView);
		/**
		 * ��ȡ��ͼ������
		 */
		mMapController = mMapView.getController();
		/**
		 * ���õ�ͼ�Ƿ���Ӧ����¼� .
		 */
		mMapController.enableClick(true);
		/**
		 * ���õ�ͼ���ż���
		 */
		mMapController.setZoom(16);

		// �����Ƿ������ͼ
		mMapView.setSatellite(false);
		// ���õ�ͼģʽΪ��ͨ��ͼ
		mMapView.setTraffic(true);
		// �����������õ����ſؼ�
		mMapView.setBuiltInZoomControls(true);
		// ��ʾ�����߿ؼ���Ĭ���ڵ�ͼ���½�չʾ�����߿ؼ�
		mMapView.showScaleControl(true);

		// �þ�γ�ȳ�ʼ�����ĵ�
		GeoPoint point = pbC.getPointByXY(112.898416, 29.821723); // (����,γ��)
		mMapController.setCenter(point);

	
		
		/**
		 * MapView������������Activityͬ������activity����ʱ�����MapView.onPause()
		 */
		mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * �ڴ˴����ͼ�ƶ���ɻص� ���ţ�ƽ�ƵȲ�����ɺ󣬴˻ص�������
				 */
			}

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * �ڴ˴����ͼpoi����¼� ��ʾ��ͼpoi���Ʋ��ƶ����õ� ���ù���
				 * mMapController.enableClick(true); ʱ���˻ص����ܱ�����
				 * 
				 */
				String title = "";
				if (mapPoiInfo != null) {
					title = mapPoiInfo.strText;
					
			    	int x=	mapPoiInfo.geoPt.getLongitudeE6(); //���� 112898416
			    	double x1= x / 1E6; //112.898416
			    	
			    	int y=	mapPoiInfo.geoPt.getLatitudeE6(); //γ�� 29821723
			    	double y1=y / 1E6; //29.821723
			    	String s="����="+String.valueOf(x1)+"γ��="+String.valueOf(y1);
			    	
					Toast.makeText(baidu_public_set_Activity.this, title+" "+s,
							Toast.LENGTH_SHORT).show();
				
					// �����ƶ�����Ļ�����м�( ��������λ�õ��Զ�����ʽ�ƶ�����ͼ���� ���Ը����ĵ�GeoPoint����ʼ������ʾ��ͼ��)
					  mMapController.animateTo(mapPoiInfo.geoPt); //�����ƶ�����Ļ�����м�
					 
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 * �����ù� mMapView.getCurrentMap()�󣬴˻ص��ᱻ���� ���ڴ˱����ͼ���洢�豸
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 * ��ͼ��ɴ������Ĳ�������: animationTo()���󣬴˻ص�������
				 */
			}

			/**
			 * �ڴ˴����ͼ������¼�
			 */
			@Override
			public void onMapLoadFinish() {
				Toast.makeText(baidu_public_set_Activity.this, "��ͼ�������",
						Toast.LENGTH_SHORT).show();

			}
		};
		mMapView.regMapViewListener(MapApplication.getInstance().mBMapManager,
				mMapListener);

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

}
