package com.baidutest.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R;

/**
 * 13. �ٶ�GPS ��demo����չʾ��ν�϶�λSDKʵ�ֶ�λ����ʹ��MyLocationOverlay���ƶ�λλ��
 * 
 * @author Administrator
 * 
 */

public class baidu_search_GPS_2_Activity extends Activity {
	// ��λ���
		LocationClient mLocClient;
		LocationData locData = null;
		public MyLocationListenner myListener = new MyLocationListenner();
		
		//��λͼ��
		locationOverlay myLocationOverlay = null;
		//��������ͼ��
		private PopupOverlay   pop  = null;//��������ͼ�㣬����ڵ�ʱʹ��
		private TextView  popupText = null;//����view
		private View viewCache = null;
		
		//��ͼ��أ�ʹ�ü̳�MapView��MyLocationMapViewĿ������дtouch�¼�ʵ�����ݴ���
		//���������touch�¼���������̳У�ֱ��ʹ��MapView����
		MyLocationMapViewX mMapView = null;	// ��ͼView
		private MapController mMapController = null;
		
		boolean isRequest = false;//�Ƿ��ֶ���������λ
		boolean isFirstLoc = true;//�Ƿ��״ζ�λ
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.baidumap_activity_search_gps_2);
	        CharSequence titleLable="��λ����";
	        setTitle(titleLable);

	        
			//��ͼ��ʼ��
	        mMapView = (MyLocationMapViewX)findViewById(R.id.bmapView);
	        mMapController = mMapView.getController();
	        mMapView.getController().setZoom(14);
	        mMapView.getController().enableClick(true);
	        mMapView.setBuiltInZoomControls(true);
	      //���� ��������ͼ��
	        createPaopao();
	        
	        //��λ��ʼ��
	        mLocClient = new LocationClient( this );
	        locData = new LocationData();
	        mLocClient.registerLocationListener( myListener );
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true);//��gps
	        option.setCoorType("bd09ll");     //������������
	        option.setScanSpan(1000);
	        mLocClient.setLocOption(option);
	        mLocClient.start();
	        
	       
	       
	        //��λͼ���ʼ��
			myLocationOverlay = new locationOverlay(mMapView);
			//���ö�λ����
		    myLocationOverlay.setData(locData);
		    
		    //�Զ���ͼ�� 
			Drawable marker = baidu_search_GPS_2_Activity.this.getResources()
					.getDrawable(R.drawable.icon_geo);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					marker.getIntrinsicHeight()); // Ϊmaker����λ�úͱ߽�
			
		    myLocationOverlay.setMarker(marker);
		    //��Ӷ�λͼ��
			mMapView.getOverlays().add(myLocationOverlay);
			
			myLocationOverlay.enableCompass(); //   ����ָ���봫�����ĸ��¡�
		
			//�޸Ķ�λ���ݺ�ˢ��ͼ����Ч
			mMapView.refresh();
			
	    }
	    /**
		 * ������������ͼ��
		 */
		public void createPaopao(){
			
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
	        MyLocationMapViewX.pop = pop;
			
		}
		/**
	     * ��λSDK��������
	     */
	    public class MyLocationListenner implements BDLocationListener {
	    	
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	            if (location == null)
	                return ;
	            
	            locData.latitude = location.getLatitude();
	            locData.longitude = location.getLongitude();
	            //�������ʾ��λ����Ȧ����accuracy��ֵΪ0����
	            locData.accuracy = location.getRadius();
	            // �˴��������� locData�ķ�����Ϣ, �����λ SDK δ���ط�����Ϣ���û������Լ�ʵ�����̹�����ӷ�����Ϣ��
	            locData.direction = location.getDerect();
	            //���¶�λ����
	            myLocationOverlay.setData(locData);
	            //����ͼ������ִ��ˢ�º���Ч
	            mMapView.refresh();
	            //���ֶ�����������״ζ�λʱ���ƶ�����λ��
	            if (isRequest || isFirstLoc){
	            	//�ƶ���ͼ����λ��
	            	Log.d("LocationOverlay", "receive location, animate to it");
	                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
	                isRequest = false;
	                myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
	            }
	            //�״ζ�λ���
	            isFirstLoc = false;
	        }
	        
	        public void onReceivePoi(BDLocation poiLocation) {
	            if (poiLocation == null){
	                return ;
	            }
	        }
	    }
	    
	    //�̳�MyLocationOverlay��дdispatchTapʵ�ֵ������
	  	public class locationOverlay extends MyLocationOverlay{

	  		public locationOverlay(MapView mapView) {
	  			super(mapView);
	  			// TODO Auto-generated constructor stub
	  		}
	  		@Override
	  		protected boolean dispatchTap() {
	  			// TODO Auto-generated method stub
	  			//�������¼�,��������
	  			
				
	  		     popupText.setText("���ݾ�γ��ȥ����Ϣ");
 
				// mMapController.animateTo(item.getPoint()); // ��λ������
				
	  		   popupText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(baidu_search_GPS_2_Activity.this, popupText.getText(), Toast.LENGTH_SHORT).show();
					
				}
			});
				
				pop.showPopup(viewCache, new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)),
						8);
				
//				pop.showPopup(BMapUtil.getBitmapFromView(popupText),
//						new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)),
//						8);
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
	    	//�˳�ʱ���ٶ�λ
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
//	        getMenuInflater().inflate(R.menu.activity_main, menu);
	        return true;
	    }

	}
	/**
	 * �̳�MapView��дonTouchEventʵ�����ݴ������
	 * @author hejin
	 *
	 */
	class MyLocationMapViewX extends MapView{
		static PopupOverlay   pop  = null;//��������ͼ�㣬���ͼ��ʹ��
		public MyLocationMapViewX(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		public MyLocationMapViewX(Context context, AttributeSet attrs){
			super(context,attrs);
		}
		public MyLocationMapViewX(Context context, AttributeSet attrs, int defStyle){
			super(context, attrs, defStyle);
		}
		@Override
	    public boolean onTouchEvent(MotionEvent event){
			if (!super.onTouchEvent(event)){
				//��������
				if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
					pop.hidePop();
			}
			return true;
		}
	}

