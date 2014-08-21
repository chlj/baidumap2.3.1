package com.baidutest.map;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;


import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class MapApplication extends Application {
	private static MapApplication mInstance = null;
	public boolean m_bKeyRight = true;
	BMapManager mBMapManager = null;

	public static final String strKey = "5yhmwjTOmu9nTZqevQoUWVAI";
	


	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	
		
		initEngineManager(this);

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this); //异常

		
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(
					MapApplication.getInstance().getApplicationContext(),
					"BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}
	

	
	public static MapApplication getInstance() {
		return mInstance;
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(
						MapApplication.getInstance().getApplicationContext(),
						"您的网络出错啦！", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(
						MapApplication.getInstance().getApplicationContext(),
						"输入正确的检索条件！", Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				Toast.makeText(
						MapApplication.getInstance().getApplicationContext(),
						"请在 MapApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "
								+ iError, Toast.LENGTH_LONG).show();
				MapApplication.getInstance().m_bKeyRight = false;
			} else {
				MapApplication.getInstance().m_bKeyRight = true;
				Toast.makeText(
						MapApplication.getInstance().getApplicationContext(),
						"key认证成功", Toast.LENGTH_LONG).show();
			}
		}
	}
	
}
