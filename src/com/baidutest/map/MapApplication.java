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
		crashHandler.init(this); //�쳣

		
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(
					MapApplication.getInstance().getApplicationContext(),
					"BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
		}
	}
	

	
	public static MapApplication getInstance() {
		return mInstance;
	}

	// �����¼���������������ͨ�������������Ȩ��֤�����
	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(
						MapApplication.getInstance().getApplicationContext(),
						"���������������", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(
						MapApplication.getInstance().getApplicationContext(),
						"������ȷ�ļ���������", Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			// ����ֵ��ʾkey��֤δͨ��
			if (iError != 0) {
				// ��ȨKey����
				Toast.makeText(
						MapApplication.getInstance().getApplicationContext(),
						"���� MapApplication.java�ļ�������ȷ����ȨKey,������������������Ƿ�������error: "
								+ iError, Toast.LENGTH_LONG).show();
				MapApplication.getInstance().m_bKeyRight = false;
			} else {
				MapApplication.getInstance().m_bKeyRight = true;
				Toast.makeText(
						MapApplication.getInstance().getApplicationContext(),
						"key��֤�ɹ�", Toast.LENGTH_LONG).show();
			}
		}
	}
	
}
