package com.baidutest.map;

import java.util.ArrayList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
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
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 11.·���滮��ѯ ��demo����չʾ��ν��мݳ������С�����·���������ڵ�ͼʹ��RouteOverlay��TransitOverlay����
 * ͬʱչʾ��ν��нڵ��������������
 * 
 * @author Administrator
 * 
 */
 //By http://www.th7.cn/Program/Android/201311/159014.shtml ����·��ʾ
public class baidu_search_diy_Activity extends Activity implements
		OnClickListener {

	private Button btn_goback;

	// UI���
	Button mBtnDrive = null; // �ݳ�����
	Button mBtnTransit = null; // ��������
	Button mBtnWalk = null; // ��������
	Button mBtnCusRoute = null; // �Զ���·��
	Button mBtnCusIcon = null; // �Զ������յ�ͼ��

	// ���·�߽ڵ����
	Button mBtnPre = null;// ��һ���ڵ�
	Button mBtnNext = null;// ��һ���ڵ�
	int nodeIndex = -2;// �ڵ�����,������ڵ�ʱʹ��
	MKRoute route = null;// ����ݳ�/����·�����ݵı�����������ڵ�ʱʹ��
	TransitOverlay transitOverlay = null;// ���湫��·��ͼ�����ݵı�����������ڵ�ʱʹ��
	RouteOverlay routeOverlay = null;
	boolean useDefaultIcon = false;
	int searchType = -1;// ��¼���������ͣ����ּݳ�/���к͹���
	private PopupOverlay pop = null;// ��������ͼ�㣬����ڵ�ʱʹ��
	private TextView popupText = null;// ����view
	private View viewCache = null;

	// ��ͼ��أ�ʹ�ü̳�MapView��MyRouteMapViewĿ������дtouch�¼�ʵ�����ݴ���
	// ���������touch�¼���������̳У�ֱ��ʹ��MapView����
	MapView mMapView = null; // ��ͼView
	// �������
	MKSearch mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	private String  baidu_map_city="";
	
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

		setContentView(R.layout.baidumap_activity_search_diy);
		findView();
		baidu_map_city=this.getString(R.string.baidu_map_city);
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
		mMapView.getController().setZoom(12);

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

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// �����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR) {
					// �������е�ַ
					// ArrayList<MKPoiInfo> stPois =
					// res.getAddrResult().mStartPoiList;
					// ArrayList<MKPoiInfo> enPois =
					// res.getAddrResult().mEndPoiList;
					// ArrayList<MKCityListInfo> stCities =
					// res.getAddrResult().mStartCityList;
					// ArrayList<MKCityListInfo> enCities =
					// res.getAddrResult().mEndCityList;
					return;
				}
				// ����ſɲο�MKEvent�еĶ���
				if (error != 0 || res == null) {
					Toast.makeText(baidu_search_diy_Activity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 0;
				routeOverlay = new RouteOverlay(baidu_search_diy_Activity.this,
						mMapView);
				// �˴���չʾһ��������Ϊʾ��
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				// �������ͼ��
				mMapView.getOverlays().clear();
				// ���·��ͼ��
				mMapView.getOverlays().add(routeOverlay);
				// ִ��ˢ��ʹ��Ч
				mMapView.refresh();
				// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				// �ƶ���ͼ�����
				mMapView.getController().animateTo(res.getStart().pt);
				// ��·�����ݱ����ȫ�ֱ���
				route = res.getPlan(0).getRoute(0);
				// ����·�߽ڵ��������ڵ����ʱʹ��
				nodeIndex = -1;
				mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				// �����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR) {
					// �������е�ַ
					// ArrayList<MKPoiInfo> stPois =
					// res.getAddrResult().mStartPoiList;
					// ArrayList<MKPoiInfo> enPois =
					// res.getAddrResult().mEndPoiList;
					// ArrayList<MKCityListInfo> stCities =
					// res.getAddrResult().mStartCityList;
					// ArrayList<MKCityListInfo> enCities =
					// res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(baidu_search_diy_Activity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 1;
				transitOverlay = new TransitOverlay(
						baidu_search_diy_Activity.this, mMapView);

				int count = res.getNumPlan(); // һ���ж�������·

			
				Log.i("xx", "��� �� �յ�һ���ж�������·=" + count);
				for (int i = 0; i < count; i++) {
					MKTransitRoutePlan mp = res.getPlan(i); // ȡ�õ�һ����·
					
					int buscount=0; //��� �� �յ� ����վ��
					
					int countLines= mp.getNumLines() ;//  ���ط��������Ĺ�����·���� (��վ��վ֮�������Ҫתվ ->�ж�����·)
					
					for(int m=0;m<countLines;m++){
						buscount+= mp.getLine(m).getNumViaStops(); //��ȡ������·;���ĳ�վ����
						  MKLine mk=	mp.getLine(m) ; // 
						  String title=	mk.getTitle() ; //  ��ȡ������·������
						  String  id=	mk.getUid()  ; //  ��ȡ������·��id
						  String tip=   mk.getTip();
						  Log.i("xx","title="+title+",id="+id+",tip="+tip);
					}
					// -- begin�˴�չʾ���з�����Ϊʾ��---
					mMapView.getOverlays().clear();
					Log.i("xx", "���ط�������������Ϣ=" + mp.getContent()+",Ԥ�ƻ���ʱ��="+mp.getTime() / 60 +"����,����="+mp.getDistance()+"�ס�;������վ����="+buscount);
					transitOverlay.setData(res.getPlan(i));
					mMapView.getOverlays().add(transitOverlay);
					mMapView.refresh();
					//-- end---------------------
				}
				
				// ----------------begin �˴�չʾ���з�����Ϊʾ��---------
				mMapView.invalidate();
				// �ƶ���ͼ�����
				// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
				mMapView.getController().zoomToSpan(
						transitOverlay.getLatSpanE6(),
						transitOverlay.getLonSpanE6());
				mMapView.getController().animateTo(res.getStart().pt);
				// ----------------end �˴�չʾ���з�����Ϊʾ��---------
				
//				// �˴���չʾһ��������Ϊʾ��
//				transitOverlay.setData(res.getPlan(0));
//				// �������ͼ��
//				mMapView.getOverlays().clear();
//				// ���·��ͼ��
//				mMapView.getOverlays().add(transitOverlay);
//				// ִ��ˢ��ʹ��Ч
//				mMapView.refresh();
//				// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
//				mMapView.getController().zoomToSpan(
//						transitOverlay.getLatSpanE6(),
//						transitOverlay.getLonSpanE6());
//				// �ƶ���ͼ�����
//				mMapView.getController().animateTo(res.getStart().pt);
				// ����·�߽ڵ��������ڵ����ʱʹ��
				nodeIndex = 0;
				mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				// �����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR) {
					// �������е�ַ
					// ArrayList<MKPoiInfo> stPois =
					// res.getAddrResult().mStartPoiList;
					// ArrayList<MKPoiInfo> enPois =
					// res.getAddrResult().mEndPoiList;
					// ArrayList<MKCityListInfo> stCities =
					// res.getAddrResult().mStartCityList;
					// ArrayList<MKCityListInfo> enCities =
					// res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(baidu_search_diy_Activity.this, "��Ǹ��δ�ҵ����",
							Toast.LENGTH_SHORT).show();
					return;
				}

				searchType = 2;
				routeOverlay = new RouteOverlay(baidu_search_diy_Activity.this,
						mMapView);
				// �˴���չʾһ��������Ϊʾ��
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				// �������ͼ��
				mMapView.getOverlays().clear();
				// ���·��ͼ��
				mMapView.getOverlays().add(routeOverlay);
				// ִ��ˢ��ʹ��Ч
				mMapView.refresh();
				// ʹ��zoomToSpan()���ŵ�ͼ��ʹ·������ȫ��ʾ�ڵ�ͼ��
				mMapView.getController().zoomToSpan(
						routeOverlay.getLatSpanE6(),
						routeOverlay.getLonSpanE6());
				// �ƶ���ͼ�����
				mMapView.getController().animateTo(res.getStart().pt);
				// ��·�����ݱ����ȫ�ֱ���
				route = res.getPlan(0).getRoute(0);
				// ����·�߽ڵ��������ڵ����ʱʹ��
				nodeIndex = -1;
				mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);

			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}

			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			@Override
			public void onGetPoiDetailSearchResult(int type, int iError) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void findView() {
		btn_goback = (Button) findViewById(R.id.back);
		btn_goback.setOnClickListener(this);

		// ��ʼ������
		mBtnDrive = (Button) findViewById(R.id.drive);
		mBtnTransit = (Button) findViewById(R.id.transit);
		mBtnWalk = (Button) findViewById(R.id.walk);
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		mBtnCusRoute = (Button) findViewById(R.id.custombutton);
		mBtnCusIcon = (Button) findViewById(R.id.customicon);

		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);

		mBtnDrive.setOnClickListener(this);
		mBtnTransit.setOnClickListener(this);
		mBtnWalk.setOnClickListener(this);
		
		mBtnPre.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
		mBtnCusRoute.setOnClickListener(this);
		mBtnCusIcon.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.drive:
			// ��������
			SearchButtonProcess(v);
			break;
		case R.id.transit:
			// ��������
			SearchButtonProcess(v);
			break;
		case R.id.walk:
			// ��������
			SearchButtonProcess(v);
			break;
		case R.id.pre:
			// ���·�߽ڵ�
			nodeClick(v);
			break;
		case R.id.next:
			// ���·�߽ڵ�
			nodeClick(v);
			break;
		case R.id.custombutton:
			// ����·�߻���ʾ��
			intentToActivity();
			break;
		case R.id.customicon:
			// �ı�ͼ��
			changeRouteIcon();
			break;
		default:
			break;
		}
	}

	/**
	 * ����·�߹滮����ʾ��
	 * 
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		// ��������ڵ��·������
		route = null;
		routeOverlay = null;
		transitOverlay = null;
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		// ����������ť��Ӧ
		EditText editSt = (EditText) findViewById(R.id.start);
		EditText editEn = (EditText) findViewById(R.id.end);

		// ������յ��name���и�ֵ��Ҳ����ֱ�Ӷ����긳ֵ����ֵ�����򽫸��������������
		MKPlanNode stNode = new MKPlanNode();
		stNode.name = editSt.getText().toString();
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = editEn.getText().toString();

		// ʵ��ʹ�����������յ���н�����ȷ���趨
		if (mBtnDrive.equals(v)) {
			mSearch.drivingSearch(baidu_map_city, stNode,baidu_map_city, enNode);
		} else if (mBtnTransit.equals(v)) {
			mSearch.transitSearch(baidu_map_city, stNode, enNode); // (baidu_map_city,ţ������, բŪ���´�)
		} else if (mBtnWalk.equals(v)) {
			mSearch.walkingSearch(baidu_map_city, stNode, baidu_map_city, enNode);
		}
	}

	/**
	 * �ڵ����ʾ��
	 * 
	 * @param v
	 */
	public void nodeClick(View v) {
		viewCache = getLayoutInflater().inflate(R.layout.act_paopao2, null);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		// int searchType = -1;// ��¼���������ͣ����ּݳ�/���к͹���

		if (searchType == 0 || searchType == 2) {
			// �ݳ�������ʹ�õ����ݽṹ��ͬ���������Ϊ�ݳ����У��ڵ����������ͬ
			if (nodeIndex < -1 || route == null
					|| nodeIndex >= route.getNumSteps())
				return;

			// ��һ���ڵ�
			if (mBtnPre.equals(v) && nodeIndex > 0) {
				// ������
				nodeIndex--;
				// �ƶ���ָ������������
				mMapView.getController().animateTo(
						route.getStep(nodeIndex).getPoint());

				popupText.setText(route.getStep(nodeIndex).getContent()); // ���عؼ��������ı�

				// ��������
				// popupText.setBackgroundResource(R.drawable.icon_geo);

				// pop.showPopup(BMapUtil.getBitmapFromView(popupText),
				// route.getStep(nodeIndex).getPoint(),
				// 5);

				pop.showPopup(viewCache, route.getStep(nodeIndex).getPoint(), 5);
				popupText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(baidu_search_diy_Activity.this,
								popupText.getText().toString(),
								Toast.LENGTH_SHORT).show();

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
				// popupText.setBackgroundResource(R.drawable.icon_geo);
				// pop.showPopup(BMapUtil.getBitmapFromView(popupText),
				// route.getStep(nodeIndex).getPoint(),
				// 5);

				pop.showPopup(viewCache, route.getStep(nodeIndex).getPoint(), 5);
				popupText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(baidu_search_diy_Activity.this,
								popupText.getText().toString(),
								Toast.LENGTH_SHORT).show();

					}
				});

			}
		}
		if (searchType == 1) {
			// ��������ʹ�õ����ݽṹ��������ͬ����˵�������ڵ����
			if (nodeIndex < -1 || transitOverlay == null
					|| nodeIndex >= transitOverlay.getAllItem().size())
				return;

			// ��һ���ڵ�
			if (mBtnPre.equals(v) && nodeIndex > 1) {
				// ������
				nodeIndex--;
				// �ƶ���ָ������������
				mMapView.getController().animateTo(
						transitOverlay.getItem(nodeIndex).getPoint());
				// ��������

				// OverlayItem.getTitle()
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle()); // ���ظ�overlay�ı����ı�

				// popupText.setBackgroundResource(R.drawable.icon_geo);
				// pop.showPopup(BMapUtil.getBitmapFromView(popupText),
				// transitOverlay.getItem(nodeIndex).getPoint(),
				// 5);

				pop.showPopup(viewCache, transitOverlay.getItem(nodeIndex)
						.getPoint(), 5);

				popupText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(baidu_search_diy_Activity.this,
								popupText.getText().toString(),
								Toast.LENGTH_SHORT).show();

					}
				});

			}
			// ��һ���ڵ�
			if (mBtnNext.equals(v)
					&& nodeIndex < (transitOverlay.getAllItem().size() - 2)) {
				// ������
				nodeIndex++;
				// �ƶ���ָ������������
				mMapView.getController().animateTo(
						transitOverlay.getItem(nodeIndex).getPoint());

				// ��������
				popupText.setText(transitOverlay.getItem(nodeIndex).getTitle()); // ���ظ�overlay�ı����ı�

				// popupText.setBackgroundResource(R.drawable.icon_geo);
				// pop.showPopup(BMapUtil.getBitmapFromView(popupText),
				// transitOverlay.getItem(nodeIndex).getPoint(),
				// 5);

				pop.showPopup(viewCache, transitOverlay.getItem(nodeIndex)
						.getPoint(), 5);
				popupText.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(baidu_search_diy_Activity.this,
								popupText.getText().toString(),
								Toast.LENGTH_SHORT).show();

					}
				});
			}
		}

	}

	/**
	 * ������������ͼ��
	 */
	public void createPaopao() {

		mMapView.getOverlays().clear();
		mMapView.refresh(); // 2.0.0�汾�������������ˢ�½�֧��refresh����

		// ���ݵ����Ӧ�ص�
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};
		pop = new PopupOverlay(mMapView, popListener);
	}

	/**
	 * ��ת����·��Activity
	 */
	public void intentToActivity() {
		// ��ת������·����ʾdemo
		 Intent intent = new Intent(this, baidu_customRouteOverlayDemo.class);
		 startActivity(intent);
	}

	/**
	 * �л�·��ͼ�꣬ˢ�µ�ͼʹ����Ч ע�⣺ ���յ�ͼ��ʹ�����Ķ���.
	 */
	protected void changeRouteIcon() {
		Button btn = (Button) findViewById(R.id.customicon);
		if (routeOverlay == null && transitOverlay == null) {
			return;
		}
		if (useDefaultIcon) {
			if (routeOverlay != null) {
				routeOverlay.setStMarker(null);
				routeOverlay.setEnMarker(null);
			}
			if (transitOverlay != null) {
				transitOverlay.setStMarker(null);
				transitOverlay.setEnMarker(null);
			}
			btn.setText("�Զ������յ�ͼ��");
			Toast.makeText(this, "��ʹ��ϵͳ���յ�ͼ��", Toast.LENGTH_SHORT).show();
		} else {
			if (routeOverlay != null) {
				routeOverlay.setStMarker(getResources().getDrawable(
						R.drawable.icon_st));
				routeOverlay.setEnMarker(getResources().getDrawable(
						R.drawable.icon_en));
			}
			if (transitOverlay != null) {
				transitOverlay.setStMarker(getResources().getDrawable(
						R.drawable.icon_st));
				transitOverlay.setEnMarker(getResources().getDrawable(
						R.drawable.icon_en));
			}
			btn.setText("ϵͳ���յ�ͼ��");
			Toast.makeText(this, "��ʹ���Զ������յ�ͼ��", Toast.LENGTH_SHORT).show();
		}
		useDefaultIcon = !useDefaultIcon;
		mMapView.refresh();

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
