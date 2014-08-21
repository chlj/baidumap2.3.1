package com.baidutest.map;

import java.util.ArrayList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Ground;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**6. ���������б��
 * com.baidu.mapapi.map.MapView
 * 
 * @author Administrator
 * 
 */

public class baidu_more_mark_Activity extends Activity implements
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
	 * MapView �ǵ�ͼ���ؼ�
	 */
	private MapView mMapView = null;
	public View mPopView = null;
	private PopupOverlay pop = null;// ��������ͼ�㣬����ڵ�ʱʹ��
	private TextView popupText = null;// ����view
	private ArrayList<OverlayItem> mItems = null;
	private OverlayItem mCurItem = null;
	private MyOverlay mOverlay = null;



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

		setContentView(R.layout.baidumap_activity_more_mark);
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
		mMapController.setZoom(19);

		// �����������õ����ſؼ�
		mMapView.setBuiltInZoomControls(true);

		initOverlay();

		findview(); // �ҵ��ؼ�

	}

	public void initOverlay() {

		/**
		 * ����ͼ����Դ��������ʾ��overlayItem����ǵ�λ�ã�
		 */
		Drawable marker = this.getResources().getDrawable(R.drawable.icon_geo);
		// Ϊmaker����λ�úͱ߽�
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		/**
		 * �����Զ���overlay
		 */
		mOverlay = new MyOverlay(getResources()
				.getDrawable(R.drawable.icon_geo), mMapView);
		/**
		 * ׼��overlay ����
		 */
		GeoPoint p1 = pbC.getPointByXY(112.898416, 29.821723); // (����,γ��)
		OverlayItem item1 = new OverlayItem(p1, "������1", "������a");
		item1.setAnchor(item1.ALING_CENTER); // ͼƬ���� �͵��غ���һ��
		/**
		 * ����overlayͼ�꣬�粻���ã���ʹ�ô���ItemizedOverlayʱ��Ĭ��ͼ��.
		 */
		item1.setMarker(marker);

		GeoPoint p2 = pbC.getPointByXY(112.899111, 29.822437); // (����,γ��)
		OverlayItem item2 = new OverlayItem(p2, "������2", "������b");
		item2.setAnchor(item2.ALING_CENTER); // ͼƬ���� �͵��غ���һ��
		item2.setMarker(marker);

		/**
		 * ��item ��ӵ�overlay�� ע�⣺ ͬһ��itmeֻ��addһ��
		 */
		mOverlay.addItem(item1);
		mOverlay.addItem(item2);
		/**
		 * ��������item���Ա�overlay��reset���������
		 */
		mItems = new ArrayList<OverlayItem>();
		mItems.addAll(mOverlay.getAllItem());

		// �þ�γ�ȳ�ʼ�����ĵ� (�����ʼ�����ĵ�)
		GeoPoint point = pbC.getPointByXY(112.898416, 29.821723); // (����,γ��)
		mMapController.setCenter(point); // �������ĵ�

		/**
		 * ��overlay �����MapView��
		 */
		mMapView.getOverlays().add(mOverlay);

		/**
		 * ˢ�µ�ͼ
		 */
		mMapView.refresh();

		/**
		 * ���ͼ����Զ���View.
		 */

		/**
		 * ����һ��popupoverlay
		 */
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {

			}
		};
		pop = new PopupOverlay(mMapView, popListener);

	}

	/**
	 * �������Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {
		mOverlay.removeAll();

		if (pop != null) {
			pop.hidePop();
		}
		mMapView.refresh();
	}

	/**
	 * �������Overlay
	 * 
	 * @param view
	 */
	public void resetOverlay(View view) {
		clearOverlay(null);
		// ����add overlay
		mOverlay.addItem(mItems);
		mMapView.refresh();
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

	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(final int index) {
			final OverlayItem item = getItem(index);
			mCurItem = item;
			Toast.makeText(
					baidu_more_mark_Activity.this,
					"index=" + String.valueOf(index) + ",item.getSnippet()="
							+ item.getSnippet(), Toast.LENGTH_LONG).show();


			mPopView = getLayoutInflater().inflate(R.layout.act_paopao2, null);
			popupText = (TextView) mPopView.findViewById(R.id.textcache);
			
			popupText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
				
					
					if (index == 0) {
						Toast.makeText(baidu_more_mark_Activity.this,
								"����������ҽԺ", Toast.LENGTH_SHORT).show();
					} else if (index == 1) {
						Toast.makeText(baidu_more_mark_Activity.this,
								"������ʵ��Сѧ", Toast.LENGTH_SHORT).show();

					}
				}
			});

			mMapController.animateTo(item.getPoint()); // ��λ������
			if (index == 0) {
				popupText.setText("����������ҽԺ");
			} else if (index == 1) {
				popupText.setText("������ʵ��Сѧ");
			}
			pop.showPopup(mPopView, item.getPoint(), 5);

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

}
