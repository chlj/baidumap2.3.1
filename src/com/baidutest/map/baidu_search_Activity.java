package com.baidutest.map;
import java.util.ArrayList;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
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
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 9.poi ��ͼ���� com.baidu.mapapi.map.MapView
 * 
 * 
 * ��demo����չʾ��ν��е�������������õ�ַ�������꣩����������������������������ַ��
 * ͬʱչʾ�����ʹ��ItemizedOverlay�ڵ�ͼ�ϱ�ע�����
 * 
 * 
 * @author Administrator
 * 
 */

public class baidu_search_Activity extends Activity implements OnClickListener {

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

	/**
	 * �����ؼ������봰��
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index;
	private ArrayList<OverlayItem> arraylist = null;

	public View mPopView = null;
	private PopupOverlay pop = null;// ��������ͼ�㣬����ڵ�ʱʹ��
	private TextView popupText = null;// ����view
	private ArrayList<OverlayItem> mItems = null;
	private OverlayItem mCurItem = null;
	private MyOverlays mOverlays = null;
	/**
	 * ��MapController��ɵ�ͼ����
	 */
	private MapController mMapController = null;

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

		setContentView(R.layout.baidumap_activity_search);

		arraylist = new ArrayList<OverlayItem>();

		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);

		// mMapView.getController().enableClick(true);
		// mMapView.getController().setZoom(12);
		// // �����������õ����ſؼ�
		// mMapView.setBuiltInZoomControls(true);

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
		mMapController.setZoom(12);

		mMapView.setBuiltInZoomControls(true);

		// ��ʼ������ģ�飬ע���¼�����
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {

			@Override
			public void onGetAddrResult(MKAddrInfo res, int error) {

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

			/**
			 * �ڴ˴���poi�������
			 */
			@Override
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(baidu_search_Activity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}
				// ����ͼ�ƶ�����һ��POI���ĵ�
				if (res.getCurrentNumPois() > 0) {
					// 1. ��poi�����ʾ����ͼ��
					// MyPoiOverlay poiOverlay = new MyPoiOverlay(
					// baidu_search_Activity.this, mMapView, mSearch);
					// poiOverlay.setData(res.getAllPoi());
					// mMapView.getOverlays().clear();
					// mMapView.getOverlays().add(poiOverlay);
					// mMapView.refresh();

					// 2. �Զ���ͼ�� �������ʾ����ͼ����

					Drawable marker = baidu_search_Activity.this.getResources()
							.getDrawable(R.drawable.icon_geo);
					marker.setBounds(0, 0, marker.getIntrinsicWidth(),
							marker.getIntrinsicHeight()); // Ϊmaker����λ�úͱ߽�
					int count = res.getAllPoi().size();
					for (int i = 0; i < count; i++) {
						OverlayItem item = new OverlayItem(res.getAllPoi().get(
								i).pt, res.getAllPoi().get(i).uid, res
								.getAllPoi().get(i).name);

						arraylist.add(item);
					}
					OverlayTest itemOverlay = new OverlayTest(marker, mMapView);
					mMapView.getOverlays().clear();
					itemOverlay.addItem(arraylist);
					mMapView.getOverlays().add(itemOverlay);
					mMapView.refresh();
					//��������
					PopupClickListener popListener = new PopupClickListener() {
						@Override
						public void onClickedPopup(int index) {

						}
					};
					pop = new PopupOverlay(mMapView, popListener);
					

					// 3. ʹ�� baidu_more_mark_Activity.java �е�ʵ��
					// Drawable marker =
					// baidu_search_Activity.this.getResources()
					// .getDrawable(R.drawable.icon_geo);
					// marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					// marker.getIntrinsicHeight()); // Ϊmaker����λ�úͱ߽�
					// mOverlays = new MyOverlays(getResources().getDrawable(
					// R.drawable.icon_geo), mMapView);
					// int count = res.getAllPoi().size();
					//
					// for (int i = 0; i < count; i++) {
					// OverlayItem item = new OverlayItem(res.getAllPoi().get(
					// i).pt, res.getAllPoi().get(i).uid, res
					// .getAllPoi().get(i).name);
					// item.setAnchor(item.ALING_CENTER); // ͼƬ���� �͵��غ���һ��
					// item.setMarker(marker);
					//
					// mOverlays.addItem(item);
					//
					// }
					// mItems = new ArrayList<OverlayItem>();
					// mItems.addAll(mOverlays.getAllItem());
					// mMapView.getOverlays().add(mOverlays);
					// mMapView.refresh();
					// PopupClickListener popListener = new PopupClickListener()
					// {
					// @Override
					// public void onClickedPopup(int index) {
					//
					// }
					// };
					// pop = new PopupOverlay(mMapView, popListener);

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
					Toast.makeText(baidu_search_Activity.this, strInfo,
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
				// TODO Auto-generated method stub

			}

			/**
			 * ���½����б�
			 */
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				if (res == null || res.getAllSuggestions() == null) {
					return;
				}
				sugAdapter.clear();
				for (MKSuggestionInfo info : res.getAllSuggestions()) {
					if (info.key != null)
						sugAdapter.add(info.key);
				}
				sugAdapter.notifyDataSetChanged();

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

		btn1 = (Button) findViewById(R.id.btn1); // ��ѯ
		btn1.setOnClickListener(this);

		btn2 = (Button) findViewById(R.id.btn2); // ��һ��
		btn2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.btn1:
			arraylist.clear();
			searchButtonProcess(v);
			break;
		case R.id.btn2:
			arraylist.clear();
			goToNextPage(v);
			break;
		default:
			break;
		}
	}

	/**
	 * Ӱ��������ť����¼�
	 * 
	 * @param v
	 */
	public void searchButtonProcess(View v) {
		EditText editCity = (EditText) findViewById(R.id.city);
		EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
		mSearch.poiSearchInCity(editCity.getText().toString(), editSearchKey
				.getText().toString()); // ����poi����.
		// public int poiSearchInCity(java.lang.String city, java.lang.String
		// key)

	}

	public void goToNextPage(View v) {
		// ������һ��poi
		int flag = mSearch.goToPoiPage(++load_Index); // ��ȡָ��ҳ�ĵ�poi���.
		if (flag != 0) {
			Toast.makeText(baidu_search_Activity.this, "��������ʼ��Ȼ����������һ������",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 3.
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyOverlays extends ItemizedOverlay {

		public MyOverlays(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(final int index) {
			final OverlayItem item = getItem(index);
			mCurItem = item;
			Toast.makeText(
					baidu_search_Activity.this,
					"index=" + String.valueOf(index) + ",item.getSnippet()="
							+ item.getSnippet(), Toast.LENGTH_LONG).show();

			mPopView = getLayoutInflater().inflate(R.layout.act_paopao2, null);
			popupText = (TextView) mPopView.findViewById(R.id.textcache);

			mMapController.animateTo(item.getPoint()); // ��λ������

			popupText.setText(item.getSnippet());

			pop.showPopup(mPopView, item.getPoint(), 5);

			popupText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_Activity.this,
							item.getSnippet().toString(), Toast.LENGTH_SHORT)
							.show();
				}
			});

			// ��̬���ز���
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop != null) {
				pop.hidePop();
			}
			return false;
		}

	}

	/*
	 * *2. Ҫ����overlay����¼�ʱ��Ҫ�̳�ItemizedOverlay ���������¼�ʱ��ֱ������ItemizedOverlay.
	 */
	class OverlayTest extends ItemizedOverlay<OverlayItem> {
		// ��MapView����ItemizedOverlay
		public OverlayTest(Drawable marker, MapView mapView) {
			super(marker, mapView);
		}

		protected boolean onTap(final int index) {
			// �ڴ˴���item����¼�

//			String aa = "index=" + index + "*"
//					+ arraylist.get(index).getTitle() + "*"
//					+ arraylist.get(index).getSnippet();
//			Toast.makeText(baidu_search_Activity.this, aa, Toast.LENGTH_SHORT)
//					.show();

			mPopView = getLayoutInflater().inflate(R.layout.act_paopao2, null);
			popupText = (TextView) mPopView.findViewById(R.id.textcache);

			popupText.setText(arraylist.get(index).getSnippet());

			pop.showPopup(mPopView, arraylist.get(index).getPoint(), 5);

			popupText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_Activity.this,
							arraylist.get(index).getSnippet(),
							Toast.LENGTH_SHORT).show();
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

	/**
	 * ����
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyPoiOverlay extends PoiOverlay {
		MKSearch mSearch;

		public MyPoiOverlay(Activity activity, MapView mapView, MKSearch search) {
			super(activity, mapView);
			mSearch = search;
		}

		@Override
		protected boolean onTap(int i) {
			super.onTap(i);
			MKPoiInfo info = getPoi(i);
			if (info.hasCaterDetails) {
				mSearch.poiDetailSearch(info.uid);
			}
			return true;
		}
	}
}
