package com.baidutest.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class pbC {
	/**
	 *  
	 * @param x ����
	 * @param y γ��
	 * @return
	 */
	public static GeoPoint getPointByXY(double x,double y){
		return  new GeoPoint((int) (y * 1E6),
				(int) (x * 1E6)); // (γ��, ����)
	}
	
	public static boolean isCheckNet(Context context) {

		// -1�����粻���� 0���ƶ����� 1��wifi���� 2��δ֪����
		int i = isNetworkEnabled(context);
		if (i == -1 || i == 2) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * �ж������Ƿ����
	 * 
	 * @return -1�����粻���� 0���ƶ����� 1��wifi���� 2��δ֪����
	 */
	public static int isNetworkEnabled(Context context) {
		int status = -1;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			switch (networkInfo.getType()) {
			case ConnectivityManager.TYPE_MOBILE: { // �ƶ�����
				status = 0;
				break;
			}
			case ConnectivityManager.TYPE_WIFI: { // wifi����
				status = 1;
				break;
			}
			default: {
				status = 2;
				break;
			}
			}
		}
		return status;
	}
	
	/**
	 * ��������
	 * @param context
	 */
	public static void setNetwork(Context context) {
		final Context con = context;
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("����״̬");
		builder.setMessage("��ǰ���粻���ã��Ƿ��������磿");
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent wifiSettingsIntent = new Intent(
						"android.settings.WIFI_SETTINGS");
				con.startActivity(wifiSettingsIntent);

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
	

}
