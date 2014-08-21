package com.baidutest.map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

/**
 * 15.�ٶȵ�ͼ����_2 gps��λ- ֻ�ڳ�ʼ��ʱ��λ + ͨ������¼����ж�λ
 * 
 * @author Administrator
 * 
 */

public class baidu_map_jiemian_2_Activity extends Activity {

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

	private View viewCache_dw = null;

	// ��ͼ��أ�ʹ�ü̳�MapView��MyLocationMapViewĿ������дtouch�¼�ʵ�����ݴ���
	// ���������touch�¼���������̳У�ֱ��ʹ��MapView����
	MyLocationMapViewXYZ mMapView = null; // ��ͼView
	private MapController mMapController = null;

	boolean isRequest = false;// �Ƿ��ֶ���������λ
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

	private ImageView popleft, popright;

	private MKSearch mMKSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	private MapApplication app;

	private String flag = "0";
	
	private String myLoadString="δ��ȡ��";
	/**
	 * MKMapViewListener ���ڴ����ͼ�¼��ص�
	 */
	MKMapViewListener mMapListener = null;

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

		setContentView(R.layout.baidumap_activity_jiemian_2);
		CharSequence titleLable = "��λ����";
		setTitle(titleLable);

		Button bt1 = (Button) findViewById(R.id.back);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		viewCache_dw = baidu_map_jiemian_2_Activity.this.getLayoutInflater()
				.inflate(R.layout.act_paopao2, null); // ��λʱ ������ͼ��
		viewCache_dw.setTag("0");

		// ��ͼ��ʼ��
		mMapView = (MyLocationMapViewXYZ) findViewById(R.id.bmapView);
		mMapController = mMapView.getController();
		mMapView.getController().setZoom(17);
		mMapView.getController().enableClick(true);
		mMapView.setBuiltInZoomControls(true);

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

		// ���� ��������ͼ��
		createPaopao(new GeoPoint((int) (locData.latitude * 1e6),
				(int) (locData.longitude * 1e6)), "���粻�ȶ�ʱ��ʾ");

		// �Զ���ͼ��
		Drawable marker = baidu_map_jiemian_2_Activity.this.getResources()
				.getDrawable(R.drawable.icon_geo);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); // Ϊmaker����λ�úͱ߽�

		myLocationOverlay.setMarker(marker);

		// ��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);

		myLocationOverlay.enableCompass(); // ����ָ���봫�����ĸ��¡�

		// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();

		central_point = (Button) findViewById(R.id.central_point);
		traffic = (Button) findViewById(R.id.traffic);

		// ������λ
		central_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_2_Activity.this, "������λ",
						Toast.LENGTH_SHORT).show();

				flag = "0";
				mMapView.getOverlays().clear();
				mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����

				// ����
				viewCache.setTag("0");
				viewCache.setVisibility(View.GONE);

				// ��ͼ��ʼ��
				isFirstLoc = true;
				mMapView = (MyLocationMapViewXYZ) findViewById(R.id.bmapView);
				mMapController = mMapView.getController();
				mMapView.getController().setZoom(17);
				mMapView.getController().enableClick(true);
				mMapView.setBuiltInZoomControls(true);
				mLocClient = new LocationClient(
						baidu_map_jiemian_2_Activity.this);
				locData = new LocationData();
				mLocClient.registerLocationListener(myListener);
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);// ��gps
				option.setCoorType("bd09ll"); // ������������
				option.setScanSpan(1000);
				option.disableCache(true);//��ֹ���û��涨λ
				option.setPoiNumber(5);//��෵��POI����
				option.setPoiDistance(1000); //poi��ѯ����
				option.setPoiExtraInfo(true); //�Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
				mLocClient.setLocOption(option);
				mLocClient.requestLocation();
				
				mLocClient.start();

				// ��ʼ��MKSearch
				mMKSearch = new MKSearch();
				mMKSearch.init(app.mBMapManager, new MySearchListener());

				// ��λͼ���ʼ��
				myLocationOverlay = new locationOverlay(mMapView);
				// ���ö�λ����
				myLocationOverlay.setData(locData);

				// ���� ��������ͼ��
				createPaopao(new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)), "locData�л�ȡλ����Ϣ");

				GeoPoint point = new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6));
				mMapController.animateTo(point); // ��λ������

				// �Զ���ͼ��
				Drawable marker = baidu_map_jiemian_2_Activity.this
						.getResources().getDrawable(R.drawable.icon_geo);
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight()); // Ϊmaker����λ�úͱ߽�

				myLocationOverlay.setMarker(marker);
				// ��Ӷ�λͼ��
				mMapView.getOverlays().add(myLocationOverlay);

				myLocationOverlay.enableCompass(); // ����ָ���봫�����ĸ��¡�

				// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
				mMapView.refresh();

			}
		});

		// ��ͨͼ
		traffic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_2_Activity.this, "��ͨͼ",
						Toast.LENGTH_SHORT).show();
				mMapView.setTraffic(true);

				mMapView.setSatellite(true); // ����ͼ
			}
		});

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

				flag = "1";
				String title = "";
				
				if (mapPoiInfo != null) {
					
					// ���֮ǰ�ı�ǵ�

					createPaopao(mapPoiInfo.geoPt, mapPoiInfo.strText); // ��������

					/**
					 * ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã�
					 */
					Drawable marker = baidu_map_jiemian_2_Activity.this
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

	// ���� ��������ͼ��
	public void createPaopao(final GeoPoint point, String value) {
		if(flag.equals("0")){
			
		}
		else{
			mMapView.getOverlays().clear();
			mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����
		}
		

		viewCache = baidu_map_jiemian_2_Activity.this.getLayoutInflater()
				.inflate(R.layout.act_paopao2, null);

		viewCache.setTag("0");
		
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		popupText.setText(value); // �������е��ı���ֵ

		ImageView img = (ImageView) viewCache.findViewById(R.id.popright);  // ������ȥ

		ImageView img_se = (ImageView) viewCache.findViewById(R.id.popleft); //��������
		//
		mMapView.addView(viewCache, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));

		MapView.LayoutParams mapviewParams = (MapView.LayoutParams) viewCache
				.getLayoutParams();
		mapviewParams.point = point; // point��Ҫ��ʾ������
		mMapView.updateViewLayout(viewCache, mapviewParams);
		viewCache.setVisibility(View.GONE);

		// ������ȥ
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_2_Activity.this,
						"������ȥ", Toast.LENGTH_SHORT).show();
				Intent intent =new Intent();
				intent.setClass( baidu_map_jiemian_2_Activity.this, baidu_map_jiemian_3_Activity.class);
				intent.putExtra("toName", popupText.getText().toString()); // Ŀ�ĵ�����
				intent.putExtra("toLon",String.valueOf(locData.longitude));//  Ŀ�ĵؾ���
				intent.putExtra("toLat",String.valueOf(locData.latitude));//  Ŀ�ĵ�γ��
				Log.i("xx","name="+popupText.getText().toString()+",����="+String.valueOf(locData.longitude)+",γ��="+String.valueOf(locData.latitude));
		        startActivity(intent);
				
			}
		});
		 // ��������
		img_se.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(baidu_map_jiemian_2_Activity.this,
						"��������", Toast.LENGTH_SHORT).show();
			}
		});
		popupText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_2_Activity.this,
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
		MyLocationMapViewXYZ.pop = pop;
		MyLocationMapViewXYZ.mPopView = viewCache;

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

			
			myLoadString=result.strAddr;

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
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
					
		   
		    
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			
			
			myLoadString=location.getAddrStr(); // �п���Ϊnull 
			// �����ȡ���� ��ֻ�ܸ��� ��γ�� ȥ��ѯ ʵ����
			Log.i("xx","myLoadString="+myLoadString); 
			Log.i("xx", "����2=" + locData.longitude + ",γ��=" + locData.latitude);
			
			if(myLoadString==null){
				  GeoPoint gp = new GeoPoint((int)(locData.latitude* 1e6), 
			                (int)(locData.longitude *  1e6));
				    mMKSearch.reverseGeocode(gp); //mSearchΪ MKSearch����
			}
			
			
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
			} else {
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
	class locationOverlay extends MyLocationOverlay {
		public locationOverlay(MapView mapView) {
			super(mapView);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean dispatchTap() {
			if (flag.equals("0")) {
//              �¼����ѱ�����
				
//				final TextView popupText = (TextView) viewCache_dw
//						.findViewById(R.id.textcache);
//				popupText.setText("�ҵ�λ��"); // �������е��ı���ֵ
//				popupText.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Toast.makeText(baidu_map_jiemian_2_Activity.this,
//								popupText.getText().toString(),
//								Toast.LENGTH_SHORT).show();
//					}
//				});
//				pop.showPopup(viewCache_dw, new GeoPoint(
//						(int) (locData.latitude * 1e6),
//						(int) (locData.longitude * 1e6)), 8);
				
				
				createPaopao(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)), myLoadString);

			}

			if (viewCache.getTag().equals("0")) {
				viewCache.setVisibility(View.VISIBLE);
				viewCache.setTag("1");
			} else if (viewCache.getTag().equals("1")) {
				viewCache.setVisibility(View.GONE);
				viewCache.setTag("0");
			}
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
class MyLocationMapViewXYZ extends MapView {
	static PopupOverlay pop = null;// ��������ͼ�㣬���ͼ��ʹ��
	static View mPopView = null;// ��������ͼ�㣬���ͼ��ʹ��

	public MyLocationMapViewXYZ(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapViewXYZ(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapViewXYZ(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// ��������
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP) {
				pop.hidePop();
			}
			mPopView.setVisibility(View.GONE);
			mPopView.setTag("0");
		}
		return true;
	}
}
