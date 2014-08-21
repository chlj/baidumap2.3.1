package com.baidutest.map;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKStep;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidutest.map.baidu_map_jiemian_3_Activity.locationOverlay;
import com.example.testdemo.R;

/**
 * 19 �ٶȵ�ͼ���� ���� ��ʾ �б�
 * 
 * @author Administrator
 * 
 */

public class baidu_map_jiemian_search_show_list_Activity extends Activity {

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
	MyLocationMapViewXYZVSY mMapView = null; // ��ͼView
	private MapController mMapController = null;

	boolean isRequest = false;// �Ƿ��ֶ���������λ
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

	private ImageView popleft, popright;

	private MKSearch mMKSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	private MapApplication app;

	private String flag = "0";

	private String myLoadString = "δ��ȡ��";
	/**
	 * MKMapViewListener ���ڴ����ͼ�¼��ص�
	 */
	MKMapViewListener mMapListener = null;

	private Button btn_search;// ������ť
	private EditText edit_searchkey; // ������ص�

	private ArrayList<OverlayItem> arraylist = null; // ��ͼ����ʱʹ��

	private String baidu_map_city = "";

	private SlidingDrawer sd;
	private ListView mSearch_Result_List; // ��������б�

	private GeoPoint myPoint = null;// �ҵ����꣨gps��λ��
	private LocationData locData2 = null;

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

		setContentView(R.layout.baidumap_activity_jiemian_search_show_list);
		CharSequence titleLable = "��λ����";
		setTitle(titleLable);

		baidu_map_city = this.getString(R.string.baidu_map_city);

		mSearch_Result_List = (ListView) findViewById(R.id.list_RouteResult); // ��������б�
		sd = (SlidingDrawer) findViewById(R.id.slidingDrawer); // ����

		Button bt1 = (Button) findViewById(R.id.back);
		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		viewCache_dw = baidu_map_jiemian_search_show_list_Activity.this
				.getLayoutInflater().inflate(R.layout.act_paopao2, null); // ��λʱ
																			// ������ͼ��
		viewCache_dw.setTag("0");

		// ��ͼ��ʼ��
		mMapView = (MyLocationMapViewXYZVSY) findViewById(R.id.bmapView);
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
		Drawable marker = baidu_map_jiemian_search_show_list_Activity.this
				.getResources().getDrawable(R.drawable.icon_geo);
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
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"������λ", Toast.LENGTH_SHORT).show();

				flag = "0";
				mMapView.getOverlays().clear();
				mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����

				// ����
				viewCache.setTag("0");
				viewCache.setVisibility(View.GONE);

				// ��ͼ��ʼ��
				isFirstLoc = true;
				mMapView = (MyLocationMapViewXYZVSY) findViewById(R.id.bmapView);
				mMapController = mMapView.getController();
				mMapView.getController().setZoom(17);
				mMapView.getController().enableClick(true);
				mMapView.setBuiltInZoomControls(true);
				mLocClient = new LocationClient(
						baidu_map_jiemian_search_show_list_Activity.this);
				locData = new LocationData();
				mLocClient.registerLocationListener(myListener);
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);// ��gps
				option.setCoorType("bd09ll"); // ������������
				option.setScanSpan(1000);
				option.disableCache(true);// ��ֹ���û��涨λ
				option.setPoiNumber(5);// ��෵��POI����
				option.setPoiDistance(1000); // poi��ѯ����
				option.setPoiExtraInfo(true); // �Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
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
				myPoint = point; // �ҵ�����
				mMapController.animateTo(point); // ��λ������

				// �Զ���ͼ��
				Drawable marker = baidu_map_jiemian_search_show_list_Activity.this
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
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"��ͨͼ", Toast.LENGTH_SHORT).show();
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
					Drawable marker = baidu_map_jiemian_search_show_list_Activity.this
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

		// ------------------begin-------------------------------
		// gps ��λ�õ��ҵ�λ�� ��γ��
		LocationClient mLocClient = new LocationClient(
				baidu_map_jiemian_search_show_list_Activity.this);
		locData2 = new LocationData();
		LocationClientOption optiona = new LocationClientOption();
		optiona.setOpenGps(true); // ��gps
		optiona.setCoorType("bd09ll"); // ������������Ϊbd09ll
		mLocClient.setLocOption(optiona);
		mLocClient.start();
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return;
				locData2.latitude = location.getLatitude(); // γ����Ϣ
				locData2.longitude = location.getLongitude(); // ������Ϣ
				myPoint = new GeoPoint((int) (locData2.latitude * 1e6),
						(int) (locData2.longitude * 1e6)); // ���

				Log.i("xx", "����=" + String.valueOf(locData2.longitude) + ",γ��="
						+ String.valueOf(locData2.latitude));

			}

			@Override
			public void onReceivePoi(BDLocation arg0) {

			}
		});

		// ----------------------end---------------------

	}

	// ���� ��������ͼ��
	public void createPaopao(final GeoPoint point, String value) {
		if (flag.equals("0")) {

		} else {
			mMapView.getOverlays().clear();
			mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����
		}

		viewCache = baidu_map_jiemian_search_show_list_Activity.this
				.getLayoutInflater().inflate(R.layout.act_paopao2, null);

		viewCache.setTag("0");

		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		popupText.setText(value); // �������е��ı���ֵ

		ImageView img = (ImageView) viewCache.findViewById(R.id.popright); // ������ȥ

		ImageView img_se = (ImageView) viewCache.findViewById(R.id.popleft); // ��������
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
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"������ȥ", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(
						baidu_map_jiemian_search_show_list_Activity.this,
						baidu_map_jiemian_3_Activity.class);
				intent.putExtra("toName", popupText.getText().toString()); // Ŀ�ĵ�����
				intent.putExtra("toLon", String.valueOf(locData.longitude));// Ŀ�ĵؾ���
				intent.putExtra("toLat", String.valueOf(locData.latitude));// Ŀ�ĵ�γ��
			
				startActivity(intent);

			}
		});
		// ��������
		img_se.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"��������", Toast.LENGTH_SHORT).show();
			}
		});
		popupText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						popupText.getText(), Toast.LENGTH_SHORT).show();
				
				
				
				Intent intent =new Intent();
				intent.setClass(baidu_map_jiemian_search_show_list_Activity.this, baidu_map_infoPage_Activity.class);
				intent.putExtra("showname", popupText.getText().toString());
				intent.putExtra("toLat",String.valueOf(locData.latitude) );
				intent.putExtra("toLon", String.valueOf(locData.longitude));
				startActivity(intent);
				
				
			}
		});

		// ���ݵ����Ӧ�ص�
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};

		pop = new PopupOverlay(mMapView, popListener);
		MyLocationMapViewXYZVSY.pop = pop;
		MyLocationMapViewXYZVSY.mPopView = viewCache;

		btn_search = (Button) findViewById(R.id.search); // ������ť
		edit_searchkey = (EditText) findViewById(R.id.searchkey);// ������ص�

		btn_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String address = edit_searchkey.getText().toString();
				if ("".equals(address.toString())) {
					Toast.makeText(
							baidu_map_jiemian_search_show_list_Activity.this,
							"������ص�", Toast.LENGTH_SHORT).show();
					return;
					// ��ʼ����
				}

				mMKSearch.poiSearchInCity(baidu_map_city, address.toString()); // ����poi����.

			}
		});

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

			myLoadString = result.strAddr;

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

		// ����poi�������
		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {

			mSearch_Result_List.setVisibility(View.GONE);

			// ����poi�������
			// ����ſɲο�MKEvent�еĶ���
			if (error != 0 || res == null) {
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"��Ǹ��δ�ҵ����", Toast.LENGTH_LONG).show();
				return;
			}
			// ����ͼ�ƶ�����һ��POI���ĵ�
			if (res.getCurrentNumPois() > 0) {
				// 2. �Զ���ͼ�� �������ʾ����ͼ����
				Drawable marker = baidu_map_jiemian_search_show_list_Activity.this
						.getResources().getDrawable(R.drawable.icon_geo);
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight()); // Ϊmaker����λ�úͱ߽�
				int count = res.getAllPoi().size();

				arraylist = new ArrayList<OverlayItem>();

				

				for (int i = 0; i < count; i++) {
					// java.lang.String title, java.lang.String snippe

					// �����ҵ����� �� �յ������ �õ� ����
					double distance = DistanceUtil.getDistance(myPoint, res
							.getAllPoi().get(i).pt); // ���������

					DecimalFormat df = new DecimalFormat("#.00");
					String distances = df.format(distance);// ����2λ��ЧС��

					OverlayItem item = new OverlayItem(
							res.getAllPoi().get(i).pt,
							res.getAllPoi().get(i).name,
							res.getAllPoi().get(i).address + "^" + distances);

					arraylist.add(item);
				}

				OverlayTestA itemOverlay = new OverlayTestA(marker, mMapView);

				mMapView.getOverlays().clear();
				itemOverlay.addItem(arraylist);
				mMapView.getOverlays().add(itemOverlay);
				mMapView.refresh();
				// ��������
				PopupClickListener popListener = new PopupClickListener() {
					@Override
					public void onClickedPopup(int index) {

					}
				};
				pop = new PopupOverlay(mMapView, popListener);
				// ��ePoiTypeΪ2��������·����4��������·��ʱ�� poi����Ϊ��
				for (MKPoiInfo info : res.getAllPoi()) {
					if (info.pt != null) {
						mMapView.getController().animateTo(info.pt);
						break;
					}
				}
			} else if (res.getCityListNum() > 0) {
				// ������ؼ����ڱ���û���ҵ����������������ҵ�ʱ�����ذ����ùؼ�����Ϣ�ĳ����б�
				String strInfo = "��";
				for (int i = 0; i < res.getCityListNum(); i++) {
					strInfo += res.getCityListInfo(i).city;
					strInfo += ",";
				}
				strInfo += "�ҵ����";
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						strInfo, Toast.LENGTH_LONG).show();
			}

			if (arraylist != null && arraylist.size() > 0) {
				Get_Drive_Route_ResultList(arraylist);
				sd.animateOpen(); // �򿪳���
			}

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

	public List<Map<String, Object>> iList;
	// �ݳ� �� ���� ������
	private baidu_map_driver_route_ListAdapter listItemAdapter = new baidu_map_driver_route_ListAdapter();

	// ��ȡ�ԼݺͲ���·����Ϣ�б�����¼�����
	private void Get_Drive_Route_ResultList(ArrayList<OverlayItem> list) {
		mSearch_Result_List.setVisibility(View.VISIBLE);
		iList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		List<GeoPoint> data = new ArrayList<GeoPoint>();
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			OverlayItem item = list.get(i);
			// ���� + ��ַ
			String xxString = item.getSnippet().toString();
			Log.i("xx", "xxString=" + xxString);
			map.put("title",
					item.getTitle() + " "
							+ item.getSnippet().split("\\^")[1].toString()
							+ "��" + "\n" + "��ַ��"
							+ item.getSnippet().split("\\^")[0].toString());
			map.put("point", item.getPoint());
			map.put("imgv_img", getResources().getDrawable(R.drawable.icon_gcoding));  //�б�ͼ��
			
			
			map.put("showname", item.getTitle());
			
			
			iList.add(map);
			data.add(item.getPoint());
		}

		listItemAdapter
				.setContext(baidu_map_jiemian_search_show_list_Activity.this);
		listItemAdapter.setList(iList);
		mSearch_Result_List.setAdapter(listItemAdapter);
		mSearch_Result_List.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int index,
					long arg3) {
				GeoPoint staPoint = (GeoPoint) iList.get(index).get("point");
				String showname = (String) iList.get(index).get("showname");
				pop.hidePop();
				SetPopView(staPoint, showname);
				mMapView.getController().animateTo(staPoint);
				sd.animateClose(); // ����
			}
		});
	}

	/**
	 * ������ ��������Ϣ
	 * @param pt
	 * @param textName
	 */
	public void SetPopView(final GeoPoint pt, final String textName) {
		View mPopView = LayoutInflater.from(this).inflate(R.layout.act_paopao2,
				null);// ��ȡҪת����View��Դ
		popupText = (TextView) mPopView.findViewById(R.id.textcache);
		popupText.setTextSize(14);
		popupText.setText(textName);
		popupText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						textName, Toast.LENGTH_SHORT).show();
				
				Intent intent =new Intent();
				intent.setClass(baidu_map_jiemian_search_show_list_Activity.this, baidu_map_infoPage_Activity.class);
				intent.putExtra("showname", textName);
				intent.putExtra("toLat",String.valueOf(pt.getLatitudeE6() / 1e6) );
				intent.putExtra("toLon", String.valueOf(pt.getLongitudeE6() /1e6));
				startActivity(intent);
				
			}
		});

		ImageView img = (ImageView) mPopView.findViewById(R.id.popright); // ������ȥ

		ImageView img_se = (ImageView) mPopView.findViewById(R.id.popleft); // ��������
		// ������ȥ
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"������ȥ", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(
						baidu_map_jiemian_search_show_list_Activity.this,
						baidu_map_jiemian_3_Activity.class);

				intent.putExtra("toName", textName.toString()); // Ŀ�ĵ�����
				intent.putExtra("toLon",
						String.valueOf(pt.getLongitudeE6() / 1e6));// Ŀ�ĵؾ���
				intent.putExtra("toLat",
						String.valueOf(pt.getLatitudeE6() / 1e6));// Ŀ�ĵ�γ��

				startActivity(intent);

			}
		});
		// ��������
		img_se.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(
						baidu_map_jiemian_search_show_list_Activity.this,
						"��������", Toast.LENGTH_SHORT).show();
			}
		});

		pop.showPopup(mPopView, pt, 0);
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

			myLoadString = location.getAddrStr(); // �п���Ϊnull
			// �����ȡ���� ��ֻ�ܸ��� ��γ�� ȥ��ѯ ʵ����
			Log.i("xx", "myLoadString=" + myLoadString);
			Log.i("xx", "����2=" + locData.longitude + ",γ��=" + locData.latitude);

			if (myLoadString == null) {
				GeoPoint gp = new GeoPoint((int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6));
				mMKSearch.reverseGeocode(gp); // mSearchΪ MKSearch����
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

	/*
	 * ��� ����ɫ������
	 * ����ʹ�� Ҫ����overlay����¼�ʱ��Ҫ�̳�ItemizedOverlay ���������¼�ʱ��ֱ������ItemizedOverlay.
	 */
	class OverlayTestA extends ItemizedOverlay<OverlayItem> {
		// ��MapView����ItemizedOverlay
		public OverlayTestA(Drawable marker, MapView mapView) {
			super(marker, mapView);
		}

		protected boolean onTap(final int index) {
			// �ڴ˴���item����¼�
	
			View mPopView = getLayoutInflater().inflate(R.layout.act_paopao2,
					null);
			popupText = (TextView) mPopView.findViewById(R.id.textcache);
			
			popupText.setText(arraylist.get(index).getTitle().toString());
			
			pop.showPopup(mPopView, arraylist.get(index).getPoint(), 5);

			popupText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(
							baidu_map_jiemian_search_show_list_Activity.this,
							arraylist.get(index).getTitle(),
							Toast.LENGTH_SHORT).show();
				}
			});

			ImageView img = (ImageView) mPopView.findViewById(R.id.popright); // ������ȥ

			ImageView img_se = (ImageView) mPopView.findViewById(R.id.popleft); // ��������

			// ������ȥ
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(
							baidu_map_jiemian_search_show_list_Activity.this,
							"������ȥ", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(
							baidu_map_jiemian_search_show_list_Activity.this,
							baidu_map_jiemian_3_Activity.class);

					intent.putExtra("toName", popupText.getText().toString()); // Ŀ�ĵ�����
					intent.putExtra(
							"toLon",
							String.valueOf(arraylist.get(index).getPoint()
									.getLongitudeE6() / 1e6));// Ŀ�ĵؾ���
					intent.putExtra(
							"toLat",
							String.valueOf(arraylist.get(index).getPoint()
									.getLatitudeE6() / 1e6));// Ŀ�ĵ�γ��

					startActivity(intent);

				}
			});
			// ��������
			img_se.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Toast.makeText(
							baidu_map_jiemian_search_show_list_Activity.this,
							"��������", Toast.LENGTH_SHORT).show();
				}
			});

			return true;

		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// �ڴ˴���MapView�ĵ���¼��������� trueʱ
			super.onTap(pt, mapView);
			mapView.getController().animateTo(pt); // ��λ����
			if (pop != null) {
				pop.hidePop();
			}
			return false;
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

				createPaopao(new GeoPoint((int) (locData.latitude * 1e6),
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
class MyLocationMapViewXYZVSY extends MapView {
	static PopupOverlay pop = null;// ��������ͼ�㣬���ͼ��ʹ��
	static View mPopView = null;// ��������ͼ�㣬���ͼ��ʹ��

	public MyLocationMapViewXYZVSY(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLocationMapViewXYZVSY(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapViewXYZVSY(Context context, AttributeSet attrs,
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
