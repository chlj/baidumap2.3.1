package com.baidutest.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKLine;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKStep;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

/**
 * 16.�ٶȵ�ͼ����_3 ������ȥ
 * 
 * �� double ���͵� ��γ�� + ���� ��toName��toLon��toLat��
 * @author Administrator
 * 
 */

public class baidu_map_jiemian_3_Activity extends Activity {

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
	MyLocationMapViewXYZA mMapView = null; // ��ͼView
	private MapController mMapController = null;

	boolean isRequest = false;// �Ƿ��ֶ���������λ
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ

	private ImageView popleft, popright;

	private MKSearch mMKSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	private MapApplication app;

	private ImageButton ib;// ����

	private EditText editStart, editEnd; // ��� ,�յ�

	private String toName, toLon, toLat;// Ŀ�ĵ�����,����,γ��

	LocationData locData2 = null; // ��ʼ����λ
	public MyLocationListenner myListener2 = new MyLocationListenner();
	private Button mBtnDrive, mBtnTransit, mBtnWalk; // �Լ�,����,����
	public String strBtnValue = "1"; // 1-�Լ�,2-����,3-����
	private Button searchRoute;// ����
	private ImageButton maprout_btnSwap;// ��� �յ� ����ת��

	private GeoPoint mStartPoint, mEndPoint, mTemPoint; // ���,�յ�,��ʱ��
	private String mStartStr, mEndStr, mTempStr; // �������,�յ�����,��ʱ������

	private String baidu_map_city = ""; // Ĭ�ϳ���

	
	private ListView mDrive_Route_Result_List = null; // �ݳ�����ʹ�� �б�
	private ExpandableListView mBus_Result_List = null; // ����ʹ�� �б�

	public List<Map<String, Object>> iList;
	// �ݳ� �� ���� ������
	private baidu_map_driver_route_ListAdapter listItemAdapter = new baidu_map_driver_route_ListAdapter();
	private SlidingDrawer sd;
	public List<List<Map<String, Object>>> selchilds; // ���� ��ѯ ������


	private baidu_treeAdapter baidu_tree_adapter;
	private List<baidu_bus_demo> mParentList = new ArrayList<baidu_bus_demo>();
	private List<baidu_treeAdapter.TreeNode> treeNode;

	private ImageButton start_add, end_add;

	private Handler mHandler;
	public static final int GPSDW = 1;

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

		setContentView(R.layout.baidumap_activity_jiemian_3);
		
		baidu_map_city=this.getString(R.string.baidu_map_city);
		
		findView();

	}

	public void findView() {
		mDrive_Route_Result_List = (ListView) findViewById(R.id.list_RouteResult); // �ݳ�
		// ����ʹ��
		mBus_Result_List = (ExpandableListView) findViewById(R.id.home_expandableListView); // ����ʹ��

		sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);

		mDrive_Route_Result_List.setVisibility(View.GONE);
		mBus_Result_List.setVisibility(View.GONE);

		// �˳�
		ib = (ImageButton) findViewById(R.id.maprout_btnBack);
		ib.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		editStart = (EditText) findViewById(R.id.start); // ���
		editEnd = (EditText) findViewById(R.id.end);// �յ�

		central_point = (Button) findViewById(R.id.central_point);
		traffic = (Button) findViewById(R.id.traffic);

		// ������λ
		central_point.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(baidu_map_jiemian_3_Activity.this, "������λ",
						Toast.LENGTH_SHORT).show();

				mMapView.getOverlays().clear();
				mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����

				isFirstLoc = true;

				// ��ͼ��ʼ��
				mMapView = (MyLocationMapViewXYZA) findViewById(R.id.bmapView);
				mMapController = mMapView.getController();

				mMapView.getController().setZoom(17);
				mMapView.getController().enableClick(true);
				mMapView.setBuiltInZoomControls(true);

				// ���� ��������ͼ��
				// createPaopao();

				// ��λ��ʼ��
				mLocClient = new LocationClient(
						baidu_map_jiemian_3_Activity.this);
				locData = new LocationData();
				mLocClient.registerLocationListener(myListener);
				LocationClientOption option = new LocationClientOption();
				option.setOpenGps(true);// ��gps
				option.setCoorType("bd09ll"); // ������������
				option.setScanSpan(1000);
				mLocClient.setLocOption(option);
				mLocClient.requestLocation();
				mLocClient.start();

				// ��λͼ���ʼ��
				myLocationOverlay = new locationOverlay(mMapView);
				// ���ö�λ����
				myLocationOverlay.setData(locData);

				// �Զ���ͼ��
				Drawable marker = baidu_map_jiemian_3_Activity.this
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
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "��ͨͼ",
						Toast.LENGTH_SHORT).show();
				// mMapView.setTraffic(true);

				// mMapView.setSatellite(true); // ����ͼ
			}
		});

		// Ŀ�ĵس�ʼ��
		toName = this.getIntent().getStringExtra("toName").toString();
		toLon = this.getIntent().getStringExtra("toLon").toString();
		toLat = this.getIntent().getStringExtra("toLat").toString();

		mEndPoint = new GeoPoint((int) (Double.valueOf(toLat) * 1e6),
				(int) (Double.valueOf(toLon) * 1e6));

		mStartStr = "�ҵ�λ��";
		editStart.setText(mStartStr.toString()); // ���

		// gps ��λ�õ��ҵ�λ�� ��γ��
		LocationClient mLocClient = new LocationClient(
				baidu_map_jiemian_3_Activity.this);
		locData2 = new LocationData();
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��gps
		option.setCoorType("bd09ll"); // ������������Ϊbd09ll
		mLocClient.setLocOption(option);
		mLocClient.start();
		mLocClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return;
				locData2.latitude = location.getLatitude(); // γ����Ϣ
				locData2.longitude = location.getLongitude(); // ������Ϣ
				mStartPoint = new GeoPoint((int) (locData2.latitude * 1e6),
						(int) (locData2.longitude * 1e6)); // ���

				Log.i("xx", "����=" + String.valueOf(locData2.longitude) + ",γ��="
						+ String.valueOf(locData2.latitude));

			}

			@Override
			public void onReceivePoi(BDLocation arg0) {

			}
		});

		mEndStr = toName;
		editEnd.setText(mEndStr.toString()); // �յ�

		// �ڵ�ͼ������ʾ�� �յ�
		// ��ͼ��ʼ��

		mMapView = (MyLocationMapViewXYZA) findViewById(R.id.bmapView);
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
		mMapController.setZoom(17);

		// �����Ƿ������ͼ
		mMapView.setSatellite(false);
		// ���õ�ͼģʽΪ��ͨ��ͼ
		mMapView.setTraffic(false);
		// �����������õ����ſؼ�
		mMapView.setBuiltInZoomControls(true);
		// ��ʾ�����߿ؼ���Ĭ���ڵ�ͼ���½�չʾ�����߿ؼ�
		mMapView.showScaleControl(true);

		// �þ�γ�ȳ�ʼ�����ĵ�

		mMapController.setCenter(mEndPoint); // �������ĵ�

		/**
		 * ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã�
		 */
		Drawable marker = this.getResources().getDrawable(R.drawable.end_point);
		// Ϊmaker����λ�úͱ߽�
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		// ��λͼ���ʼ��
		myLocationOverlay = new locationOverlay(mMapView);
		// ���ö�λ����

		locData = new LocationData();
		locData.longitude = Double.valueOf(toLon);
		locData.latitude = Double.valueOf(toLat);

		myLocationOverlay.setData(locData);

		myLocationOverlay.setMarker(marker); // ʹ���Զ����ͼ����Դ

		// ��Ӷ�λͼ��
		mMapView.getOverlays().add(myLocationOverlay);

		myLocationOverlay.enableCompass();
		// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
		mMapView.refresh();

		// �趨������ť����Ӧ
		mBtnDrive = (Button) findViewById(R.id.drive);
		mBtnTransit = (Button) findViewById(R.id.transit);
		mBtnWalk = (Button) findViewById(R.id.walk);

		// �Լ�
		mBtnDrive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strBtnValue = "1";
				mBtnDrive.setBackgroundResource(R.drawable.btn_map_drive_on);
				mBtnTransit.setBackgroundResource(R.drawable.btn_map_transit);
				mBtnWalk.setBackgroundResource(R.drawable.btn_map_walk);
			}
		});
		// ����
		mBtnTransit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strBtnValue = "2";
				mBtnDrive.setBackgroundResource(R.drawable.btn_map_drive);
				mBtnTransit
						.setBackgroundResource(R.drawable.btn_map_transit_on);
				mBtnWalk.setBackgroundResource(R.drawable.btn_map_walk);
			}
		});
		// ����
		mBtnWalk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strBtnValue = "3";
				mBtnDrive.setBackgroundResource(R.drawable.btn_map_drive);
				mBtnTransit.setBackgroundResource(R.drawable.btn_map_transit);
				mBtnWalk.setBackgroundResource(R.drawable.btn_map_walk_on);
			}
		});

		// ����
		searchRoute = (Button) findViewById(R.id.searchRoute);
		searchRoute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "����",
						Toast.LENGTH_SHORT).show();
				search(strBtnValue); // �����¼�
			}
		});

		// ��� �յ� ����ת��
		maprout_btnSwap = (ImageButton) findViewById(R.id.maprout_btnSwap);
		maprout_btnSwap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "��� �յ� ����ת��",
						Toast.LENGTH_SHORT).show();
				// ������
				mTemPoint = mStartPoint;
				mStartPoint = mEndPoint;
				mEndPoint = mTemPoint;
				// ������
				mTempStr = mStartStr;
				mStartStr = mEndStr;
				mEndStr = mTempStr;
				// ��ʾ����
				editStart.setText(mStartStr.toString());
				editEnd.setText(mEndStr.toString());

				// ���յ���ʾ�ڵ�ͼ����

				mMapView.getOverlays().clear();
				mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����

				// ��ͼ��ʼ��
				mMapView = (MyLocationMapViewXYZA) findViewById(R.id.bmapView);
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
				mMapController.setZoom(17);

				// �����Ƿ������ͼ
				mMapView.setSatellite(false);
				// ���õ�ͼģʽΪ��ͨ��ͼ
				mMapView.setTraffic(false);
				// �����������õ����ſؼ�
				mMapView.setBuiltInZoomControls(true);
				// ��ʾ�����߿ؼ���Ĭ���ڵ�ͼ���½�չʾ�����߿ؼ�
				mMapView.showScaleControl(true);

				// �þ�γ�ȳ�ʼ�����ĵ�

				mMapController.setCenter(mEndPoint); // �������ĵ�

				/**
				 * ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã�
				 */

				Drawable marker = getResources().getDrawable(
						R.drawable.end_point);
				// Ϊmaker����λ�úͱ߽�
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight());

				// ��λͼ���ʼ��
				myLocationOverlay = new locationOverlay(mMapView);
				// ���ö�λ����

				locData = new LocationData();

				locData.longitude = Double.valueOf(mEndPoint.getLongitudeE6() / 1e6);
				locData.latitude = Double.valueOf(mEndPoint.getLatitudeE6() / 1e6);

				myLocationOverlay.setData(locData);

				myLocationOverlay.setMarker(marker); // ʹ���Զ����ͼ����Դ

				// ��Ӷ�λͼ��
				mMapView.getOverlays().add(myLocationOverlay);

				myLocationOverlay.enableCompass();
				// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
				mMapView.refresh();

			}
		});

		pop = new PopupOverlay(mMapView, new PopupClickListener() {

			@Override
			public void onClickedPopup(int index) {
				// sd.animateOpen();
			}
		});

		start_add = (ImageButton) findViewById(R.id.start_add); // ѡΪ���
		start_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "ѡΪ���",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(baidu_map_jiemian_3_Activity.this,
						baidu_map_jiemian_select_Activity.class);
				intent.putExtra("type", "0");
				intent.putExtra("toName", mStartStr);
				startActivityForResult(intent, 0);
			}
		});

		end_add = (ImageButton) findViewById(R.id.end_add);// ѡΪ�յ�
		end_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "ѡΪ�յ�",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(baidu_map_jiemian_3_Activity.this,
						baidu_map_jiemian_select_Activity.class);
				intent.putExtra("type", "1");
				intent.putExtra("toName", mEndStr);
				startActivityForResult(intent, 1);
			}
		});

		editStart.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i("xx", "�ı���");
			}
		});

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				switch (msg.what) {
				case GPSDW: // ѡ���յ�� ����ͼ��
					
					if(mEndPoint!=null){
						// ���յ���ʾ�ڵ�ͼ����
						mMapView.getOverlays().clear();
						mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����

						// ��ͼ��ʼ��
						mMapView = (MyLocationMapViewXYZA) findViewById(R.id.bmapView);
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
						mMapController.setZoom(17);

						// �����Ƿ������ͼ
						mMapView.setSatellite(false);
						// ���õ�ͼģʽΪ��ͨ��ͼ
						mMapView.setTraffic(false);
						// �����������õ����ſؼ�
						mMapView.setBuiltInZoomControls(true);
						// ��ʾ�����߿ؼ���Ĭ���ڵ�ͼ���½�չʾ�����߿ؼ�
						mMapView.showScaleControl(true);

						// �þ�γ�ȳ�ʼ�����ĵ�

						mMapController.setCenter(mEndPoint); // �������ĵ�

						/**
						 * ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã�
						 */

						Drawable marker = getResources().getDrawable(R.drawable.end_point);
						// Ϊmaker����λ�úͱ߽�
						marker.setBounds(0, 0, marker.getIntrinsicWidth(),
								marker.getIntrinsicHeight());

						// ��λͼ���ʼ��
						myLocationOverlay = new locationOverlay(mMapView);
						// ���ö�λ����

						locData = new LocationData();

						locData.longitude = Double
								.valueOf(mEndPoint.getLongitudeE6() / 1e6);
						locData.latitude = Double.valueOf(mEndPoint.getLatitudeE6() / 1e6);

						myLocationOverlay.setData(locData);

						myLocationOverlay.setMarker(marker); // ʹ���Զ����ͼ����Դ

						// ��Ӷ�λͼ��
						mMapView.getOverlays().add(myLocationOverlay);

						myLocationOverlay.enableCompass();
						// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
						mMapView.refresh();
					}
					
					
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {

			// ���
			String lon = data.getStringExtra("lon").toString();// ����
			String lat = data.getStringExtra("lat").toString();// γ��
			String name = data.getStringExtra("name").toString();// ����

			mStartStr = name;
			if(lon.equals("") &&lat.equals("")){
				mStartPoint=null;
			}
			else{
				mStartPoint = new GeoPoint(Integer.valueOf(lat),
						Integer.valueOf(lon)); // ���
			}
			
			editStart.setText(mStartStr);

			

		} else if (requestCode == 1) {
			// �յ�
			String lon = data.getStringExtra("lon").toString();// ����
			String lat = data.getStringExtra("lat").toString();// γ��
			String name = data.getStringExtra("name").toString();// ����

			mEndStr = name;
			if(lon.equals("") &&lat.equals("")){
				mEndPoint=null;
			}
			else{
				mEndPoint = new GeoPoint(Integer.valueOf(lat), Integer.valueOf(lon)); // �յ�
			}
			

			editEnd.setText(mEndStr);

			/**
			 * ������Ϣ
			 */
			Message msg=Message.obtain();
			msg.what=GPSDW;
			mHandler.sendMessage(msg);
			
			

		}
	}

	/**
	 * ʵ��MKSearchListener�ӿ�,����ʵ���첽�������񣬵õ��������
	 * 
	 * @author liufeng
	 */
	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo result, int arg1) {

			String string = "���ص�ַ��Ϣ�������";
			Log.i("xx", "string=" + string);

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

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int arg1) {
			// ���ع�����������Ϣ�������
			String string = "���ع�����������Ϣ�������";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			// ���ؼݳ�·���������
			String string = "���ؼݳ�·���������";
			Log.i("xx", "string=" + string);
			// �����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				return;
			}
			// ����ſɲο�MKEvent�еĶ���
			if (error != 0 || res == null) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "��Ǹ��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
				mDrive_Route_Result_List.setVisibility(View.GONE);
				mBus_Result_List.setVisibility(View.GONE);
				return;
			}

			RouteOverlay routeOverlay = new RouteOverlay(
					baidu_map_jiemian_3_Activity.this, mMapView);
			// �˴���չʾһ��������Ϊʾ��
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			// �������ͼ��
			mMapView.getOverlays().clear();
			// ���·��ͼ��
			mMapView.getOverlays().add(routeOverlay);
			// ִ��ˢ��ʹ��Ч
			mMapView.refresh();
			// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			// �ƶ���ͼ�����
			mMapView.getController().animateTo(res.getStart().pt);

			// ��·�����ݱ����ȫ�ֱ���
			MKRoute mkRoute = res.getPlan(0).getRoute(0);

			if (mkRoute != null) {
				// ����һ��list�� չʾ ����
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < mkRoute.getNumSteps(); i++) {
					map = new HashMap<String, Object>();
					MKStep mkStep = mkRoute.getStep(i);
					map.put("title", mkStep.getContent()); // ���عؼ��������ı�
					map.put("point", mkStep.getPoint()); // ���عؼ����������

					Log.i("xx", "�ݳ�·��" + i + "=" + mkStep.getContent() + ","
							+ mkStep.getPoint());
				}
				sd.animateOpen();// �򿪳���
				Get_Drive_Route_ResultList(mkRoute);// �ݳ� ���� ����

			}

		}

		@Override
		public void onGetPoiDetailSearchResult(int result, int arg1) {
			// ����poi������Ϣ�����Ľ��
			String string = "����poi������Ϣ�����Ľ��";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetPoiResult(MKPoiResult result, int arg1, int arg2) {
			// ����poi�������
			String string = "����poi�������";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult result, int arg1,
				int arg2) {
			// ���ط���̴����.
			String string = "���ط���̴����.";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult result, int arg1) {
			// �����������Ϣ�������
			String string = "�����������Ϣ�������";
			Log.i("xx", "string=" + string);
		}

		@Override
		public void onGetTransitRouteResult(final MKTransitRouteResult res,
				int error) {
			// ���ع����������
			String string = "���ع����������";
			Log.i("xx", "string=" + string);

			// �����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				return;
			}
			if (error != 0 || res == null) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "��Ǹ��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
				mDrive_Route_Result_List.setVisibility(View.GONE);
				mBus_Result_List.setVisibility(View.GONE);
				return;
			}

			int count = res.getNumPlan(); // һ���ж�������·

			List<baidu_bus_demo> list_bus = new ArrayList<baidu_bus_demo>();

			Log.i("xx", "��� �� �յ�һ���ж�������·=" + count);
			for (int i = 0; i < count; i++) {
				MKTransitRoutePlan mp = res.getPlan(i); // ȡ�õ�һ����·
				int buscount = 0; // ��� �� �յ� ����վ��
				int countLines = mp.getNumLines();// ���ط��������Ĺ�����·���� (��վ��վ֮�������Ҫתվ
													// ->�ж�����·)
				
		
				for (int m = 0; m < countLines; m++) {
					buscount += mp.getLine(m).getNumViaStops(); // ��ȡ������·;���ĳ�վ����
					MKLine mk = mp.getLine(m); //
					String title = mk.getTitle(); // ��ȡ������·������
					String id = mk.getUid(); // ��ȡ������·��id
					String tip = mk.getTip();
					// Log.i("xx", "title=" + title + ",id=" + id + ",tip=" +
					// tip);

				}

				baidu_bus_demo bs = new baidu_bus_demo();
				bs.setRes(res);
				bs.setContent(mp.getContent());
				bs.setTime(String.valueOf(mp.getTime() / 60));
				bs.setDistance(String.valueOf(mp.getDistance()));
				bs.setBuscount(String.valueOf(buscount));
				bs.setI(i);
				list_bus.add(bs);

				// Log.i("xx",
				// "���ط�������������Ϣ=" + mp.getContent() + ",Ԥ�ƻ���ʱ��="
				// + mp.getTime() / 60 + "����,����="
				// + mp.getDistance() + "�ס�;������վ����=" + buscount);
			}

			Log.i("xx", "list_bus.size()=" + list_bus.size()); // �õ��˸��� ��·����

			// ѭ������ �õ���Ӧ�Ӽ�����

			mParentList = list_bus;
			baidu_tree_adapter = new baidu_treeAdapter(
					baidu_map_jiemian_3_Activity.this,
					baidu_treeAdapter.PaddingLeft >> 1, mParentList);
			treeNode = baidu_tree_adapter.GetTreeNode();

			for (int i = 0; i < list_bus.size(); i++) {
				baidu_treeAdapter.TreeNode node = new baidu_treeAdapter.TreeNode();
				node.parent = mParentList.get(i).getContent();

				List<baidu_bus_demo_info> list = new ArrayList<baidu_bus_demo_info>(); // �Ӽ�
				MKTransitRoutePlan mp = res.getPlan(i); // ȡ�õ�һ����·
				int countLines = mp.getNumLines();// ���ط��������Ĺ�����·���� (��վ��վ֮�������Ҫתվ
				for (int m = 0; m < countLines; m++) {
					MKLine mk = mp.getLine(m); //
					String title = mk.getTitle(); // ��ȡ������·������
					String id = mk.getUid(); // ��ȡ������·��id
					String tip = mk.getTip();
					Log.i("xx", "title=" + title + ",id=" + id + ",tip=" + tip);

					baidu_bus_demo_info bs = new baidu_bus_demo_info();
					bs.setMk(mp);
					bs.setbusId(mk.getUid());
					bs.setTip(mk.getTip());
					bs.setTitle(mk.getTitle());
					list.add(bs);
				}
				node.childs.addAll(list);
				treeNode.add(node);
			}
			baidu_tree_adapter.UpdateTreeNode(treeNode);
			mBus_Result_List.setAdapter(baidu_tree_adapter);
			// ȥ��ϵͳ�Դ��ķָ���
			mBus_Result_List.setDivider(null);
			mBus_Result_List.setCacheColorHint(0); // �����϶��б��ʱ���ֹ���ֺ�ɫ����
			mBus_Result_List
					.setOnChildClickListener(new OnChildClickListener() {
						@Override
						public boolean onChildClick(ExpandableListView parent,
								View v, int groupPosition, int childPosition,
								long id) {

							baidu_tree_adapter.notifyDataSetChanged();

							sd.animateClose(); // �رճ���
							TransitOverlay transitOverlay = new TransitOverlay(
									baidu_map_jiemian_3_Activity.this, mMapView);

							// �˴���չʾһ��������Ϊʾ��
							transitOverlay.setData(res.getPlan(groupPosition));

							// �������ͼ��
							mMapView.getOverlays().clear();
							// ���·��ͼ��
							mMapView.getOverlays().add(transitOverlay);
							// ִ��ˢ��ʹ��Ч
							mMapView.refresh();
							// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
							mMapView.getController().zoomToSpan(
									transitOverlay.getLatSpanE6(),
									transitOverlay.getLonSpanE6());
							// �ƶ���ͼ�����
							mMapView.getController().animateTo(
									res.getStart().pt);

							return false;
						}
					});

			mBus_Result_List
					.setOnGroupClickListener(new OnGroupClickListener() {
						@Override
						public boolean onGroupClick(ExpandableListView parent,
								View v, int groupPosition, long id) {

							return false;
						}
					});

			sd.animateOpen(); // �򿪳���

			mDrive_Route_Result_List.setVisibility(View.GONE);
			mBus_Result_List.setVisibility(View.VISIBLE);

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			// ���ز���·���������
			String string = "���ز���·���������";
			Log.i("xx", "string=" + string);

			// �����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
			if (error == MKEvent.ERROR_ROUTE_ADDR) {
				return;
			}
			if (error != 0 || res == null) {
				Toast.makeText(baidu_map_jiemian_3_Activity.this, "��Ǹ��δ�ҵ����",
						Toast.LENGTH_SHORT).show();
				mDrive_Route_Result_List.setVisibility(View.GONE);
				mBus_Result_List.setVisibility(View.GONE);
				return;
			}

			RouteOverlay routeOverlay = new RouteOverlay(
					baidu_map_jiemian_3_Activity.this, mMapView);
			// �˴���չʾһ��������Ϊʾ��
			routeOverlay.setData(res.getPlan(0).getRoute(0));
			// �������ͼ��
			mMapView.getOverlays().clear();
			// ���·��ͼ��
			mMapView.getOverlays().add(routeOverlay);
			// ִ��ˢ��ʹ��Ч
			mMapView.refresh();
			// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
			mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
					routeOverlay.getLonSpanE6());
			// �ƶ���ͼ�����
			mMapView.getController().animateTo(res.getStart().pt);
			// ��·�����ݱ����ȫ�ֱ���
			MKRoute mkRoute = res.getPlan(0).getRoute(0);

			if (mkRoute != null) {
				// ����һ��list�� չʾ ����
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < mkRoute.getNumSteps(); i++) {
					map = new HashMap<String, Object>();
					MKStep mkStep = mkRoute.getStep(i);
					map.put("title", mkStep.getContent()); // ���عؼ��������ı�
					map.put("point", mkStep.getPoint()); // ���عؼ����������

					Log.i("xx", "����·��" + i + "=" + mkStep.getContent() + ","
							+ mkStep.getPoint());
				}
				sd.animateOpen();// �򿪳���
				Get_Drive_Route_ResultList(mkRoute); // �ݳ� ���� ����
			}
		}

	}

	// ��ȡ�ԼݺͲ���·����Ϣ�б�����¼�����
	private void Get_Drive_Route_ResultList(MKRoute mkRoute) {
		mDrive_Route_Result_List.setVisibility(View.VISIBLE);
		mBus_Result_List.setVisibility(View.GONE);

		iList = new ArrayList<Map<String, Object>>();
		// ��ͷ
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", mStartStr);
		map.put("imgv_img", getResources().getDrawable(R.drawable.sta_point));
		iList.add(map);

		List<GeoPoint> data = new ArrayList<GeoPoint>();
		for (int i = 0; i < mkRoute.getNumSteps(); i++) {
			map = new HashMap<String, Object>();
			MKStep mkStep = mkRoute.getStep(i);
			map.put("title", mkStep.getContent());
			map.put("point", mkStep.getPoint());
			// map.put("imgv_img",
			// MapRouteActivity.this.getResources().getDrawable(R.drawable.pcslist));
			iList.add(map);
			data.add(mkStep.getPoint());
		}

		// ��β
		map = new HashMap<String, Object>();
		map.put("title", mEndStr);
		map.put("imgv_img", getResources().getDrawable(R.drawable.end_point));
		iList.add(map);

		listItemAdapter.setContext(baidu_map_jiemian_3_Activity.this);
		listItemAdapter.setList(iList);
		mDrive_Route_Result_List.setAdapter(listItemAdapter);
		mDrive_Route_Result_List
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View v,
							int index, long arg3) {
						// SearchBackToMap_int(index);
						GeoPoint staPoint = (GeoPoint) iList.get(index).get(
								"point");
						String strTitle = (String) iList.get(index)
								.get("title");
						if (mStartStr.equals(strTitle) == false
								&& mEndStr.equals(strTitle) == false) {
							pop.hidePop();
							SetPopView(staPoint, strTitle);
							mMapView.getController().animateTo(staPoint);
							sd.animateClose(); // ����
						}
					}
				});
	}

	public void SetPopView(GeoPoint pt, String textName) {
		Bitmap[] bmps = new Bitmap[3];
		View popview = LayoutInflater.from(this).inflate(R.layout.act_paopao2,
				null);// ��ȡҪת����View��Դ

		TextView TestText = (TextView) popview.findViewById(R.id.textcache);
		TestText.setTextSize(14);
		TestText.setText(textName);// ��ÿ�����Title�ڵ��������ı���ʽ��ʾ����

		Bitmap popbitmap = convertViewToBitmap(popview);
		// bmps[0] =
		// BitmapFactory.decodeStream(mContext.getAssets().open("marker1.png"));
		bmps[1] = popbitmap;
		// BitmapFactory.decodeStream(mContext.getAssets().open("marker2.png"));
		// bmps[2] =
		// BitmapFactory.decodeStream(mContext.getAssets().open("marker3.png"));

		pop.showPopup(popbitmap, pt, 0);
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
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
	public class locationOverlay extends MyLocationOverlay {
		public locationOverlay(MapView mapView) {
			super(mapView);
		}

		@Override
		protected boolean dispatchTap() {
			// �������¼�,��������
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

	/**
	 * ����
	 * 
	 * @param value
	 */
	public void search(String value) {
		// ������յ��name���и�ֵ��Ҳ����ֱ�Ӷ����긳ֵ����ֵ�����򽫸��������������
		
		//���
		if("".equals(editStart.getText().toString())){
			 Toast.makeText(baidu_map_jiemian_3_Activity.this, "��ѡ�����", Toast.LENGTH_SHORT).show();
			 mStartStr="";
			 mStartPoint=null ;
			 return ;
		}
		// �յ�
		if("".equals(editEnd.getText().toString())){
			 Toast.makeText(baidu_map_jiemian_3_Activity.this, "��ѡ���յ�", Toast.LENGTH_SHORT).show();
			 mEndStr="";
			 mEndPoint=null ;
			 return ;
		}
		
		MKPlanNode startNode = new MKPlanNode();
		if(mStartPoint!=null){
			startNode.pt = mStartPoint;
		}
		
		startNode.name = mStartStr;
		MKPlanNode endNode = new MKPlanNode();
		
		if(mEndPoint!=null){
			endNode.pt = mEndPoint;
		}
		
		endNode.name = mEndStr;

		/**
		 * ���� ��������·���������ԣ�������4�ֲ��Կ�ѡ�� 1������������MKSearch.EBUS_NO_SUBWAY
		 * 2��ʱ�����ȣ�MKSearch.EBUS_TIME_FIRST 3�����ٻ��ˣ�MKSearch.EBUS_TRANSFER_FIRST
		 * 4�����ٲ��о��룺MKSearch.EBUS_WALK_FIRST
		 * 
		 * ��������ѡ����������������ٻ��ˣ�����;ת����������
		 */

		/**
		 * MKSearch.ECAR_AVOID_JAM MKSearch.ECAR_DIS_FIRST
		 * MKSearch.ECAR_FEE_FIRST MKSearch.ECAR_TIME_FIRST
		 */
		// ʵ��ʹ�����������յ����baidu_map_city������ȷ���趨

		// ��ʼ��MKSearch
		mMKSearch = new MKSearch();
		mMKSearch.init(app.mBMapManager, new MySearchListener());

		if (value == "1") {
			// �ݳ�
			mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
			mMKSearch.drivingSearch(baidu_map_city, startNode, baidu_map_city, endNode);
		} else if (value == "2") {
			// ����
			mMKSearch.setTransitPolicy(MKSearch.EBUS_TRANSFER_FIRST); // 3�����ٻ���
			mMKSearch.transitSearch(baidu_map_city, startNode, endNode);
		} else if (value == "3") {
			// ����
			mMKSearch.walkingSearch(baidu_map_city, startNode, baidu_map_city, endNode);
		}
	}
}

/**
 * �̳�MapView��дonTouchEventʵ�����ݴ������
 * 
 * @author hejin
 * 
 */
class MyLocationMapViewXYZA extends MapView {
	static PopupOverlay pop = null;// ��������ͼ�㣬���ͼ��ʹ��

	public MyLocationMapViewXYZA(Context context) {
		super(context);

	}

	public MyLocationMapViewXYZA(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLocationMapViewXYZA(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!super.onTouchEvent(event)) {
			// ��������
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}

		return true;
	}
}
