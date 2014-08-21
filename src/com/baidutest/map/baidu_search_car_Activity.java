package com.baidutest.map;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
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
import com.example.testdemo.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
 * 10.������·��ѯ
 * 
 * ��demo����չʾ��ν��й�����·�����������ʹ��RouteOverlay�ڵ�ͼ�ϻ��� ͬʱչʾ������·�߽ڵ㲢��������
 * 
 * 
 * @author Administrator
 * 
 */

public class baidu_search_car_Activity extends Activity implements
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

		setContentView(R.layout.baidumap_activity_search_car);
		findView();

		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(12);
		mMapView.setBuiltInZoomControls(true);

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
					Toast.makeText(baidu_search_car_Activity.this, "��Ǹ��δ�ҵ����",
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
					Toast.makeText(baidu_search_car_Activity.this, "��Ǹ��δ�ҵ����",
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
					Toast.makeText(baidu_search_car_Activity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_LONG).show();
					return;
				}

				RouteOverlay routeOverlay = new RouteOverlay(
						baidu_search_car_Activity.this, mMapView);
				// �˴���չʾһ��������Ϊʾ��
				routeOverlay.setData(result.getBusRoute());

				//�Զ��� ���յ�� ͼ��
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
				Toast.makeText(baidu_search_car_Activity.this,
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
					Toast.makeText(baidu_search_car_Activity.this,
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
					Toast.makeText(baidu_search_car_Activity.this,
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
