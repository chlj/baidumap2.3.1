package com.baidutest.map;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
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
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 5. ������Ե����ɫ
 * com.baidutest.map.MyLocationMapView3
 * 
 * �������� MKSearch��
 * 
 * @author Administrator
 * 
 */
public class baidu_single_point_point_Activity extends Activity implements
		OnClickListener {

	/**
	 * ��ʾMapView�Ļ����÷�
	 */
	private Button btn_back;

	final static String TAG = "xx";

	/**
	 * ��MapController��ɵ�ͼ����
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener ���ڴ����ͼ�¼��ص�
	 */
	MKMapViewListener mMapListener = null;

	/**
	 * MyLocationMapView3 �ǵ�ͼ���ؼ�
	 */
	private MyLocationMapView3 mMapView = null;

	public View mPopView = null;
	private PopupOverlay pop = null;// ��������ͼ�㣬����ڵ�ʱʹ��
	private TextView popupText = null;// ����view
	// ��λͼ��
	private locationOverlay myLocationOverlay = null;
	private LocationData locData = null;

	private double x;
	private double y;

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

		setContentView(R.layout.baidumap_activity_single_point_point);

		mMapView = (MyLocationMapView3) findViewById(R.id.bmapView);
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
		mMapController.setCenter(point); // �������ĵ�

		createPaopao(point, "����������ҽԺ"); // ��������

		/**
		 * ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã�
		 */
		Drawable marker = this.getResources().getDrawable(R.drawable.icon_geo);
		// Ϊmaker����λ�úͱ߽�
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		// ��λͼ���ʼ��
		myLocationOverlay = new locationOverlay(mMapView);
		// ���ö�λ����

		locData = new LocationData();
		locData.longitude = Double.valueOf(112.898416);
		locData.latitude = Double.valueOf(29.821723);

		myLocationOverlay.setData(locData);

		myLocationOverlay.setMarker(marker); // ʹ���Զ����ͼ����Դ

		// ��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);

		myLocationOverlay.enableCompass();
		// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();

		findview(); // �ҵ��ؼ�

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
					// title = mapPoiInfo.strText;

					// ���֮ǰ�ı�ǵ�

				

					createPaopao(mapPoiInfo.geoPt, mapPoiInfo.strText); // ��������

					/**
					 * ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã�
					 */
					Drawable marker = baidu_single_point_point_Activity.this
							.getResources().getDrawable(R.drawable.icon_geo);
					// Ϊmaker����λ�úͱ߽�
					marker.setBounds(0, 0, marker.getIntrinsicWidth(),
							marker.getIntrinsicHeight());

					// ��λͼ���ʼ��
					myLocationOverlay = new locationOverlay(mMapView);
					// ���ö�λ����

					locData = new LocationData();
					locData.longitude = Double.valueOf(mapPoiInfo.geoPt
							.getLongitudeE6() / 1E6);
					locData.latitude = Double.valueOf(mapPoiInfo.geoPt
							.getLatitudeE6() / 1E6);

					myLocationOverlay.setData(locData);

					myLocationOverlay.setMarker(marker); // ʹ���Զ����ͼ����Դ

					// ��Ӷ�λͼ��
					mMapView.getOverlays().add(myLocationOverlay);

					myLocationOverlay.enableCompass();
					// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
					mMapView.refresh();

					mMapController.animateTo(mapPoiInfo.geoPt);
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
				// Toast.makeText(baidu_single_mark_info_Activity.this,
				// "��ͼ�������",
				// Toast.LENGTH_SHORT).show();

			}
		};

		mMapView.regMapViewListener(MapApplication.getInstance().mBMapManager,
				mMapListener);

	}

	// �̳�MyLocationOverlay��дdispatchTapʵ�ֵ������
	class locationOverlay extends MyLocationOverlay {
		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			if (mPopView.getTag().equals("0")) {
				mPopView.setVisibility(View.VISIBLE);
				mPopView.setTag("1");
			} else if (mPopView.getTag().equals("1")) {
				mPopView.setVisibility(View.GONE);
				mPopView.setTag("0");
			}
			return true;

		}

	}


	// ���� ��������ͼ��
	public void createPaopao(final GeoPoint point, String value) {
		
		
		mMapView.getOverlays().clear();
		mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����
		
		
		mPopView = baidu_single_point_point_Activity.this. getLayoutInflater().inflate(R.layout.act_paopao2, null);
	
		mPopView.setTag("0");
		popupText = (TextView) mPopView.findViewById(R.id.textcache);
		popupText.setText(value); // �������е��ı���ֵ

		ImageView img = (ImageView) mPopView.findViewById(R.id.popright);

		//
		mMapView.addView(mPopView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));

		MapView.LayoutParams mapviewParams = (MapView.LayoutParams) mPopView
				.getLayoutParams();
		mapviewParams.point = point; // point��Ҫ��ʾ������
		mMapView.updateViewLayout(mPopView, mapviewParams);
		mPopView.setVisibility(View.GONE);

		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		popupText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_single_point_point_Activity.this,
						popupText.getText(), Toast.LENGTH_SHORT).show();
			}
		});

		// ���ݵ����Ӧ�ص�
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};
		
		
		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapView3.pop = pop;
		MyLocationMapView3.mPopView = mPopView;
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

/**
 * �̳�MapView��дonTouchEventʵ�����ݴ������
 * 
 * @author hejin
 * 
 */
class MyLocationMapView3 extends MapView {
	static PopupOverlay pop = null;// ��������ͼ�㣬���ͼ��ʹ��

	static View mPopView = null;// ��������ͼ�㣬���ͼ��ʹ��

	public MyLocationMapView3(Context context) {
		super(context);
	}

	public MyLocationMapView3(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapView3(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// ��������
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP) {
				pop.hidePop();

				mPopView.setVisibility(View.GONE);
				mPopView.setTag("0");
			}
		}
		return true;
	}

}
