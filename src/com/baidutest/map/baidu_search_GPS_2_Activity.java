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
 * 13. 百度GPS 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置
 * 
 * @author Administrator
 * 
 */

public class baidu_search_GPS_2_Activity extends Activity {
	// 定位相关
		LocationClient mLocClient;
		LocationData locData = null;
		public MyLocationListenner myListener = new MyLocationListenner();
		
		//定位图层
		locationOverlay myLocationOverlay = null;
		//弹出泡泡图层
		private PopupOverlay   pop  = null;//弹出泡泡图层，浏览节点时使用
		private TextView  popupText = null;//泡泡view
		private View viewCache = null;
		
		//地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
		//如果不处理touch事件，则无需继承，直接使用MapView即可
		MyLocationMapViewX mMapView = null;	// 地图View
		private MapController mMapController = null;
		
		boolean isRequest = false;//是否手动触发请求定位
		boolean isFirstLoc = true;//是否首次定位
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.baidumap_activity_search_gps_2);
	        CharSequence titleLable="定位功能";
	        setTitle(titleLable);

	        
			//地图初始化
	        mMapView = (MyLocationMapViewX)findViewById(R.id.bmapView);
	        mMapController = mMapView.getController();
	        mMapView.getController().setZoom(14);
	        mMapView.getController().enableClick(true);
	        mMapView.setBuiltInZoomControls(true);
	      //创建 弹出泡泡图层
	        createPaopao();
	        
	        //定位初始化
	        mLocClient = new LocationClient( this );
	        locData = new LocationData();
	        mLocClient.registerLocationListener( myListener );
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true);//打开gps
	        option.setCoorType("bd09ll");     //设置坐标类型
	        option.setScanSpan(1000);
	        mLocClient.setLocOption(option);
	        mLocClient.start();
	        
	       
	       
	        //定位图层初始化
			myLocationOverlay = new locationOverlay(mMapView);
			//设置定位数据
		    myLocationOverlay.setData(locData);
		    
		    //自定义图标 
			Drawable marker = baidu_search_GPS_2_Activity.this.getResources()
					.getDrawable(R.drawable.icon_geo);
			marker.setBounds(0, 0, marker.getIntrinsicWidth(),
					marker.getIntrinsicHeight()); // 为maker定义位置和边界
			
		    myLocationOverlay.setMarker(marker);
		    //添加定位图层
			mMapView.getOverlays().add(myLocationOverlay);
			
			myLocationOverlay.enableCompass(); //   启用指南针传感器的更新。
		
			//修改定位数据后刷新图层生效
			mMapView.refresh();
			
	    }
	    /**
		 * 创建弹出泡泡图层
		 */
		public void createPaopao(){
			
			mMapView.getOverlays().clear();
			mMapView.refresh(); // 2.0.0版本起，清除覆盖物后的刷新仅支持refresh方法

			viewCache = getLayoutInflater().inflate(R.layout.act_paopao2, null);
			popupText = (TextView) viewCache.findViewById(R.id.textcache);
			// 泡泡点击响应回调
			PopupClickListener popListener = new PopupClickListener() {
				@Override
				public void onClickedPopup(int index) {

				}
			};
			pop = new PopupOverlay(mMapView, popListener);
	        MyLocationMapViewX.pop = pop;
			
		}
		/**
	     * 定位SDK监听函数
	     */
	    public class MyLocationListenner implements BDLocationListener {
	    	
	        @Override
	        public void onReceiveLocation(BDLocation location) {
	            if (location == null)
	                return ;
	            
	            locData.latitude = location.getLatitude();
	            locData.longitude = location.getLongitude();
	            //如果不显示定位精度圈，将accuracy赋值为0即可
	            locData.accuracy = location.getRadius();
	            // 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
	            locData.direction = location.getDerect();
	            //更新定位数据
	            myLocationOverlay.setData(locData);
	            //更新图层数据执行刷新后生效
	            mMapView.refresh();
	            //是手动触发请求或首次定位时，移动到定位点
	            if (isRequest || isFirstLoc){
	            	//移动地图到定位点
	            	Log.d("LocationOverlay", "receive location, animate to it");
	                mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
	                isRequest = false;
	                myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
	            }
	            //首次定位完成
	            isFirstLoc = false;
	        }
	        
	        public void onReceivePoi(BDLocation poiLocation) {
	            if (poiLocation == null){
	                return ;
	            }
	        }
	    }
	    
	    //继承MyLocationOverlay重写dispatchTap实现点击处理
	  	public class locationOverlay extends MyLocationOverlay{

	  		public locationOverlay(MapView mapView) {
	  			super(mapView);
	  			// TODO Auto-generated constructor stub
	  		}
	  		@Override
	  		protected boolean dispatchTap() {
	  			// TODO Auto-generated method stub
	  			//处理点击事件,弹出泡泡
	  			
				
	  		     popupText.setText("根据经纬度去查信息");
 
				// mMapController.animateTo(item.getPoint()); // 定位在中央
				
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
	    	//退出时销毁定位
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
	 * 继承MapView重写onTouchEvent实现泡泡处理操作
	 * @author hejin
	 *
	 */
	class MyLocationMapViewX extends MapView{
		static PopupOverlay   pop  = null;//弹出泡泡图层，点击图标使用
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
				//消隐泡泡
				if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
					pop.hidePop();
			}
			return true;
		}
	}

