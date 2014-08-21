package com.baidutest.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
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
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidutest.map.baidu_single_mark_info_Activity.MySearchListener;
import com.example.testdemo.R;

/**
 * 14.�ٶȵ�ͼ����
 *  gps��λ- ֻ�ڳ�ʼ��ʱ��λ + ͨ������¼����ж�λ
 * 
 * @author Administrator
 * 
 */

public class baidu_map_jiemian_1_Activity extends Activity {

	private Button central_point, traffic;
	// ��λ���
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();

	// ��λͼ��
	locationOverlay myLocationOverlay = null;
	// ��������ͼ��
	private PopupOverlay pop = null;// ��������ͼ�㣬����ڵ�ʱʹ��
	private TextView popupText = null;// ����view
	private View viewCache = null;

	// ��ͼ��أ�ʹ�ü̳�MapView��MyLocationMapViewĿ������дtouch�¼�ʵ�����ݴ���
	// ���������touch�¼���������̳У�ֱ��ʹ��MapView����
	MyLocationMapViewXY mMapView = null; // ��ͼView
	private MapController mMapController = null;

	boolean isRequest = false;// �Ƿ��ֶ���������λ
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

	private ImageView popleft, popright;

	private MKSearch mMKSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	private MapApplication app;

	private ScaleView mScaleView;
	private ZoomControlView mZoomControlView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (MapApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			/**
			 * ���BMapManagerû�г�ʼ�����ʼ��BMapManager
			 */
			app.mBMapManager.init(MapApplication.strKey,
					new MapApplication.MyGeneralListener());
		}

		setContentView(R.layout.baidumap_activity_jiemian_1);
		CharSequence titleLable = "��λ����";
		setTitle(titleLable);

		Button bt1 = (Button) findViewById(R.id.back);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// ��ͼ��ʼ��
		mMapView = (MyLocationMapViewXY) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(14);
		mMapView.getController().enableClick(true);
		 // mMapView.setBuiltInZoomControls(true);
		
		
	
        
      
        
		
		//�����Դ��ĵ�ͼ���ſؼ�
		mMapView.setBuiltInZoomControls(false);
		
		mScaleView = (ScaleView) findViewById(R.id.scaleView);
		mScaleView.setMapView(mMapView);
		mZoomControlView = (ZoomControlView) findViewById(R.id.ZoomControlView);
		mZoomControlView.setMapView(mMapView);
		
		refreshScaleAndZoomControl();
		  
		//��ͼ��ʾ�¼��������� �ýӿڼ�����ͼ��ʾ�¼����û���Ҫʵ�ָýӿ��Դ�����Ӧ�¼���
		mMapView.regMapViewListener(app.mBMapManager, new MKMapViewListener() {
			
			@Override
			public void onMapMoveFinish() {
				 refreshScaleAndZoomControl();
			}
			
			@Override
			public void onMapLoadFinish() {
				
			}
			
			
			
			/**
			 * ��������ʱ��ص�����Ϣ.�����ڴ˷�������������Ű�ť��״̬
			 */
			@Override
			public void onMapAnimationFinish() {
				 refreshScaleAndZoomControl();
			}
			
			@Override
			public void onGetCurrentMap(Bitmap arg0) {
				
			}
			
			@Override
			public void onClickMapPoi(MapPoi arg0) {
				
			}
		});
		
		//��ȡ��ͼ������
		
		
		
		// ���� ��������ͼ��
		createPaopao();

		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// ��ʼ��MKSearch
		mMKSearch = new MKSearch();
		mMKSearch.init(app.mBMapManager, new MySearchListener());

		// ��λͼ���ʼ��
		myLocationOverlay = new locationOverlay(mMapView);
		// ���ö�λ����
		myLocationOverlay.setData(locData);

		// �Զ���ͼ��
		Drawable marker = baidu_map_jiemian_1_Activity.this.getResources()
				.getDrawable(R.drawable.icon_geo);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); // Ϊmaker����λ�úͱ߽�

		myLocationOverlay.setMarker(marker);
		// ��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);
		
		 /**
         * ���õ�ͼ����
         */
         mMapController.setOverlooking(-30); // ����ָ����
         
         
		 myLocationOverlay.enableCompass(); //   ����ָ���봫�����ĸ��¡�
		 
		 
		// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();

		central_point = (Button) findViewById(R.id.central_point);
		traffic = (Button) findViewById(R.id.traffic);
		
	// ������λ
		central_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_1_Activity.this, "������λ",
						Toast.LENGTH_SHORT).show();
				mLocClient.start();

			}
		});

		// ��ͨͼ
		traffic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_1_Activity.this, "��ͨͼ",
						Toast.LENGTH_SHORT).show();
				mMapView.setTraffic(true);
				
				mMapView.setSatellite(true); //����ͼ
			}
		});

		 //������֮��ľ���
		GeoPoint p1LL = new GeoPoint(39971802, 116347927);

		GeoPoint p2LL = new GeoPoint(39892131, 116498555);

		double distance = DistanceUtil.getDistance(p1LL, p2LL);

		Toast.makeText(baidu_map_jiemian_1_Activity.this,
				"distance=" + distance, Toast.LENGTH_SHORT).show();

		
		
		// ʹ��MKLocationManager���requestLocationUpdates ��ע��λ�ü������� removeUpdates ��ȡ��λ�ü�����
		
		
		
	
		 
	
	 
		
	}

	private void refreshScaleAndZoomControl(){
        //�������Ű�ť��״̬
        mZoomControlView.refreshZoomButtonStatus(Math.round(mMapView.getZoomLevel()));
        mScaleView.refreshScaleView(Math.round(mMapView.getZoomLevel()));
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

			popupText.setText(sb.toString());

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
	 * ������������ͼ��
	 */
	public void createPaopao() {

		mMapView.getOverlays().clear();
		mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����

		viewCache = getLayoutInflater().inflate(R.layout.act_paopao2, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		popleft = (ImageView) viewCache.findViewById(R.id.popleft);

		popright = (ImageView) viewCache.findViewById(R.id.popright);

		// ���ݵ����Ӧ�ص�
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};

		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapViewXY.pop = pop;

	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// �������ʾ��λ����Ȧ����accuracy��ֵΪ0����
			locData.accuracy = location.getRadius();
			// �˴��������� locData�ķ�����Ϣ, �����λ SDK δ���ط�����Ϣ���û������Լ�ʵ�����̹�����ӷ�����Ϣ��
			locData.direction = location.getDerect();
			// ���¶�λ����
			myLocationOverlay.setData(locData);
			// ����ͼ������ִ��ˢ�º���Ч
			mMapView.refresh();
			// ���ֶ�����������״ζ�λʱ���ƶ�����λ��
			if (isRequest || isFirstLoc) {
				// �ƶ���ͼ����λ��
				Log.d("LocationOverlay", "receive location, animate to it");
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
			}
			else{
				mLocClient.stop();
			}
			// �״ζ�λ���
			isFirstLoc = false;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	// �̳�MyLocationOverlay��дdispatchTapʵ�ֵ������
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			// TODO Auto-generated method stub
			// �������¼�,��������

			GeoPoint point = new GeoPoint((int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6));
			// mMapController.animateTo(point); // ��λ������

			// ���ݵ���������ȡ��ַ��Ϣ �첽���������ؽ����MKSearchListener���onGetAddrResult����֪ͨ
			// mMKSearch.reverseGeocode(point);

			// ����ʾ
			popupText.setText("���ݾ�γ��ȥ����Ϣ");

			// ���� ��γ��ȥ��ȡ��Ϣ

			pop.showPopup(viewCache, new GeoPoint(
					(int) (locData.latitude * 1e6),
					(int) (locData.longitude * 1e6)), 8);

			// �ٴ����¼�
			popupText.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_map_jiemian_1_Activity.this,
							popupText.getText(), Toast.LENGTH_SHORT).show();
				}
			});
			popleft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_map_jiemian_1_Activity.this, "����",
							Toast.LENGTH_SHORT).show();

				}
			});

			popright.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_map_jiemian_1_Activity.this, "������ȥ",
							Toast.LENGTH_SHORT).show();

				}
			});
			return true;
		}

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
		// �˳�ʱ���ٶ�λ
		if (mLocClient != null)
			mLocClient.stop();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

/**
 * �̳�MapView��дonTouchEventʵ�����ݴ������
 * 
 * @author hejin
 * 
 */
class MyLocationMapViewXY extends MapView {
	static PopupOverlay pop = null;// ��������ͼ�㣬���ͼ��ʹ��

	public MyLocationMapViewXY(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapViewXY(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapViewXY(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// ��������
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}

//		// �����Ļ�����λ��
//		int x = (int) event.getX();
//		int y = (int) event.getY();
//		// ����������תΪ��ַ����
//		GeoPoint pt = this.getProjection().fromPixels(x, y);
//		return super.onTouchEvent(event);

		 return true;
	}
}
