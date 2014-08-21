package com.baidutest.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidutest.map.baidu_single_mark_Activity.locationOverlay;
import com.example.testdemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 12.GPS
 * 
 * @author Administrator
 * 
 */

public class baidu_search_GPS_Activity extends Activity implements
		OnClickListener {

	private Button mBtnSearch, btn2, mBtnPre, mBtnNext, btn_goback;
	private EditText txt1, txt2;

	int nodeIndex = -2;// �ڵ�����,������ڵ�ʱʹ��
	MKRoute route = null;// ����ݳ�/����·�����ݵı�����������ڵ�ʱʹ��
	private PopupOverlay pop = null;// ��������ͼ�㣬����ڵ�ʱʹ��
	private TextView popupText = null;// ����view
	private View viewCache = null;
	private List<String> busLineIDList = null; // ��������·list �ַ���
	int busLineIndex = 0;

	// ��ͼ��أ�ʹ�ü̳�MapView��MyBusLineMapViewĿ������дtouch�¼�ʵ�����ݴ���
	// ���������touch�¼���������̳У�ֱ��ʹ��MapView����
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

		setContentView(R.layout.baidumap_activity_search_gps);
		findView();

		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(12);

		busLineIDList = new ArrayList<String>();

		// ���� ��������ͼ��
		createPaopao();

		// ��ͼ����¼�����
		mMapView.regMapTouchListner(new MKMapTouchListener() {
			@Override
			public void onMapClick(GeoPoint point) {
				// �ڴ˴����ͼ����¼�
				// ����pop
				if (pop != null) {
					pop.hidePop();
				}
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {

			}

			@Override
			public void onMapLongClick(GeoPoint point) {

			}

		});
		
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
			}

			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(baidu_search_GPS_Activity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}

				// �ҵ�����·��poi node
				MKPoiInfo curPoi = null;
				int totalPoiNum = res.getCurrentNumPois();
				// ��������poi���ҵ�����Ϊ������·��poi
				busLineIDList.clear();
				for (int idx = 0; idx < totalPoiNum; idx++) {
					if (2 == res.getPoi(idx).ePoiType) {
						// poi���ͣ�0����ͨ�㣬1������վ��2��������·��3������վ��4��������·
						curPoi = res.getPoi(idx);
						// ʹ��poi��uid���𹫽��������
						busLineIDList.add(curPoi.uid);
						Log.i("xx", "curPoi.uid=" + curPoi.uid);

					}
				}
				SearchNextBusline(); // ��ѯ��һ��

				// û���ҵ�������Ϣ
				if (curPoi == null) {
					Toast.makeText(baidu_search_GPS_Activity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}
				route = null;
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}

			/**
			 * ��ȡ����·�߽����չʾ������·
			 */
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
				if (iError != 0 || result == null) {
					Toast.makeText(baidu_search_GPS_Activity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}

				RouteOverlay routeOverlay = new RouteOverlay(
						baidu_search_GPS_Activity.this, mMapView);
				// �˴���չʾһ��������Ϊʾ��
				routeOverlay.setData(result.getBusRoute());

				// �Զ��� ���յ�� ͼ��
				routeOverlay.setStMarker(getResources().getDrawable(
						R.drawable.icon_st));
				routeOverlay.setEnMarker(getResources().getDrawable(
						R.drawable.icon_en));

				// �������ͼ��
				mMapView.getOverlays().clear();
				// ���·��ͼ��
				mMapView.getOverlays().add(routeOverlay);

				// ˢ�µ�ͼʹ��Ч
				mMapView.refresh();

				// �ƶ���ͼ�����
				mMapView.getController().animateTo(
						result.getBusRoute().getStart());

				// ��·�����ݱ����ȫ�ֱ���
				route = result.getBusRoute();
				// ����·�߽ڵ��������ڵ����ʱʹ��
				nodeIndex = -1;

				mBtnPre.setVisibility(View.VISIBLE); // ���� 1
				mBtnNext.setVisibility(View.VISIBLE);// ���� 2

				// ��ѯ��� Toast����
				Toast.makeText(baidu_search_GPS_Activity.this,
						result.getBusName(), Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub

			}

		});

		// GPS��λ
		openGPSSettings();
//
//		// Ϊ��ȡ����λ����Ϣʱ���ò�ѯ����
		editText = (EditText) findViewById(R.id.editText);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String bestProvider = lm.getBestProvider(getCriteria(), true);
		
		
//		// ��ȡλ����Ϣ
//		// ��������ò�ѯҪ��getLastKnownLocation�������˵Ĳ���ΪLocationManager.GPS_PROVIDER
		//Location location = lm.getLastKnownLocation(bestProvider); //2.3.4����   Ϊ null 
		
		  //��GPS_PROVIDER��ȡ����Ķ�λ��Ϣ  
		Location location=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);   //GPS��λΪnull
	    if(location==null){
	          location=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // ���綨λΪnull
	     }
	    
        updateView(location);  
        
		
	
//		// ����״̬
		lm.addGpsStatusListener(listener);
		// �󶨼�������4������
		// ����1���豸����GPS_PROVIDER��NETWORK_PROVIDER����
		// ����2��λ����Ϣ�������ڣ���λ����
		// ����3��λ�ñ仯��С���룺��λ�þ���仯������ֵʱ��������λ����Ϣ
		// ����4������
		// ��ע������2��3���������3��Ϊ0�����Բ���3Ϊ׼������3Ϊ0����ͨ��ʱ������ʱ���£�����Ϊ0������ʱˢ��

		// 1�����һ�Σ�����Сλ�Ʊ仯����1�׸���һ�Σ�
		// ע�⣺�˴�����׼ȷ�ȷǳ��ͣ��Ƽ���service��������һ��Thread����run��sleep(60000);Ȼ��ִ��handler.sendMessage(),����λ��
		
		   //����ÿ60�룬ÿ�ƶ�ʮ����LocationProvider��ȡһ��GPS�Ķ�λ��Ϣ  
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
				locationListener);

	}

	private LocationManager lm;
	private static final String TAG = "xx";
	private EditText editText;

	// λ�ü���
	private LocationListener locationListener = new LocationListener() {

		/**
		 * λ����Ϣ�仯ʱ����
		 */
		public void onLocationChanged(Location location) {
			//locationΪ�仯�����λ�ã�������ʾ
			updateView(location);
			Log.i(TAG, "ʱ�䣺" + location.getTime());
			Log.i(TAG, "���ȣ�" + location.getLongitude());
			Log.i(TAG, "γ�ȣ�" + location.getLatitude());
			Log.i(TAG, "���Σ�" + location.getAltitude());
		}

		/**
		 * GPS״̬�仯ʱ����
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS״̬Ϊ�ɼ�ʱ
			case LocationProvider.AVAILABLE:
				Log.i(TAG, "��ǰGPS״̬Ϊ�ɼ�״̬");
				break;
			// GPS״̬Ϊ��������ʱ
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG, "��ǰGPS״̬Ϊ��������״̬");
				break;
			// GPS״̬Ϊ��ͣ����ʱ
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG, "��ǰGPS״̬Ϊ��ͣ����״̬");
				break;
			}
		}

		/**
		 * GPS����ʱ����
		 */
		public void onProviderEnabled(String provider) {
			updateView(lm.getLastKnownLocation(provider));
		}

		/**
		 * GPS����ʱ����
		 */
		public void onProviderDisabled(String provider) {
			updateView(null);
		}

	};

	// ״̬����
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			// ��һ�ζ�λ
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.i(TAG, "��һ�ζ�λ");
				break;
			// ����״̬�ı�
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Log.i(TAG, "����״̬�ı�");
				// ��ȡ��ǰ״̬
				GpsStatus gpsStatus = lm.getGpsStatus(null);
				// ��ȡ���ǿ�����Ĭ�����ֵ
				int maxSatellites = gpsStatus.getMaxSatellites();
				// ����һ��������������������
				Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
						.iterator();
				int count = 0;
				while (iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				System.out.println("��������" + count + "������");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STARTED:
				Log.i(TAG, "��λ����");
				break;
			// ��λ����
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG, "��λ����");
				break;
			}
		};
	};

	/**
	 * ʵʱ�����ı�����
	 * 
	 * @param location
	 */
	private void updateView(Location location) {
		if (location != null) {
			editText.setText("�豸λ����Ϣ\n���ȣ�");
			editText.append(String.valueOf(location.getLongitude()));
			editText.append("\nγ�ȣ�");
			editText.append(String.valueOf(location.getLatitude()));

			double latitude = location.getLatitude();// γ��
			double longitude = location.getLongitude();// ����
			GeoPoint p = new GeoPoint((int) (latitude * 1E6),(int) (longitude * 1E6));
			
			
			mMapView.getController().setZoom(19);
			mMapView.getController().setCenter(p);
			
			
			/** 
	         * ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã� 
	         */  
	        Drawable marker = this.getResources().getDrawable(R.drawable.icon_geo);  
	        // Ϊmaker����λ�úͱ߽�  
	        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());  
	        
			// ��λͼ���ʼ��
	        locationOverlay	myLocationOverlay = new locationOverlay(mMapView);
			// ���ö�λ����

	        LocationData	locData = new LocationData();
			locData.longitude = longitude;
			locData.latitude = latitude;
			
			

			myLocationOverlay.setData(locData);
			myLocationOverlay.setMarker(marker); // ʹ���Զ����ͼ����Դ
			
			// ��Ӷ�λͼ��
			mMapView.getOverlays().add(myLocationOverlay);

			myLocationOverlay.enableCompass();
			// �޸Ķ�λ���ݺ�ˢ��ͼ����Ч
			mMapView.refresh();
			
			
			
			
		} else {
			// ���EditText����
			editText.getEditableText().clear();
		}
	}

	//�̳�MyLocationOverlay��дdispatchTapʵ�ֵ������
		 class locationOverlay extends MyLocationOverlay {
				public locationOverlay(MapView mapView) {
					super(mapView);
					// TODO Auto-generated constructor stub
				}

				@Override
				protected boolean dispatchTap() {
					// �������¼�,��������
				

					return true;

				}

			}
		 
	/**
	 * ���ز�ѯ����
	 * 
	 * @return
	 */
	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		// ���ö�λ��ȷ�� Criteria.ACCURACY_COARSE�Ƚϴ��ԣ�Criteria.ACCURACY_FINE��ȽϾ�ϸ
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// �����Ƿ�Ҫ���ٶ�
		criteria.setSpeedRequired(false);
		// �����Ƿ�������Ӫ���շ�
		criteria.setCostAllowed(false);
		// �����Ƿ���Ҫ��λ��Ϣ
		criteria.setBearingRequired(false);
		// �����Ƿ���Ҫ������Ϣ
		criteria.setAltitudeRequired(false);
		// ���öԵ�Դ������
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}

	private void openGPSSettings() {
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			// Toast.makeText(this, "GPSģ������", Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("GPS״̬");
		builder.setMessage("��ǰGPS�����ã��Ƿ�����GPS��");
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				try {
					startActivity(intent);

				} catch (ActivityNotFoundException ex) {
					intent.setAction(Settings.ACTION_SETTINGS);
					try {
						startActivity(intent);
					} catch (Exception e) {
					}
				}

			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create();
		builder.show();
	}

	public void findView() {
		txt1 = (EditText) findViewById(R.id.city);
		txt2 = (EditText) findViewById(R.id.searchkey);

		btn_goback = (Button) findViewById(R.id.back);
		mBtnSearch = (Button) findViewById(R.id.search);
		btn2 = (Button) findViewById(R.id.nextline);
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);

		btn_goback.setOnClickListener(this);
		mBtnSearch.setOnClickListener(this);
		btn2.setOnClickListener(this);
		mBtnPre.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);

		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.search:
			// ��������
			SearchButtonProcess(v);
			break;

		case R.id.nextline:
			// ������һ��������
			SearchNextBusline();
			break;
		case R.id.pre:
			// ���·�߽ڵ�
			nodeClick(v);
			break;

		case R.id.next:
			// ���·�߽ڵ�
			nodeClick(v);
			break;

		default:
			break;
		}
	}

	/**
	 * �������
	 * 
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		busLineIDList.clear();
		busLineIndex = 0;

		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		if (mBtnSearch.equals(v)) {
			EditText editCity = (EditText) findViewById(R.id.city);
			EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
			// ����poi�������ӵõ�����poi���ҵ�������·���͵�poi����ʹ�ø�poi��uid���й�����������
			mSearch.poiSearchInCity(editCity.getText().toString(),
					editSearchKey.getText().toString());
		}

	}

	/**
	 * ��ѯ��һ��
	 */
	void SearchNextBusline() {
		if (busLineIndex >= busLineIDList.size()) {
			busLineIndex = 0;
		}
		if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
				&& busLineIDList.size() > 0) {
			mSearch.busLineSearch(((EditText) findViewById(R.id.city))
					.getText().toString(), busLineIDList.get(busLineIndex));
			busLineIndex++;
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
		// ���ݵ����Ӧ�ص�
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};
		pop = new PopupOverlay(mMapView, popListener);

	}

	/**
	 * �ڵ����ʾ��
	 * 
	 * @param v
	 */
	public void nodeClick(View v) {

		if (nodeIndex < -1 || route == null || nodeIndex >= route.getNumSteps())
			return;
		viewCache = getLayoutInflater().inflate(R.layout.act_paopao2, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		// ��һ���ڵ�
		if (mBtnPre.equals(v) && nodeIndex > 0) {
			// ������
			nodeIndex--;
			// �ƶ���ָ������������
			mMapView.getController().animateTo(
					route.getStep(nodeIndex).getPoint());
			// ��������
			popupText.setText(route.getStep(nodeIndex).getContent());

			// popupText.setBackgroundResource(R.drawable.icon_geo);
			// pop.showPopup(getBitmapFromView(popupText),
			// route.getStep(nodeIndex).getPoint(),
			// 5);

			pop.showPopup(viewCache, route.getStep(nodeIndex).getPoint(), 5);

			popupText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_GPS_Activity.this,
							popupText.getText().toString(), Toast.LENGTH_SHORT)
							.show();

				}
			});

		}
		// ��һ���ڵ�
		if (mBtnNext.equals(v) && nodeIndex < (route.getNumSteps() - 1)) {
			// ������
			nodeIndex++;

			// �ƶ���ָ������������
			mMapView.getController().animateTo(
					route.getStep(nodeIndex).getPoint());
			// ��������
			popupText.setText(route.getStep(nodeIndex).getContent());
			// popupText.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_geo));
			// pop.showPopup(getBitmapFromView(popupText),
			// route.getStep(nodeIndex).getPoint(),
			// 5);

			pop.showPopup(viewCache, route.getStep(nodeIndex).getPoint(), 5);

			popupText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_GPS_Activity.this,
							popupText.getText().toString(), Toast.LENGTH_SHORT)
							.show();

				}
			});
		}

	}

	/**
	 * ��view �õ�ͼƬ
	 * 
	 * @param view
	 * @return
	 */
	public Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
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
		mMapView.destroy();
		mSearch.destory();
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

}
