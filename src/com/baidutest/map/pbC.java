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
	 * @param x 经度
	 * @param y 纬度
	 * @return
	 */
	public static GeoPoint getPointByXY(double x,double y){
		return  new GeoPoint((int) (y * 1E6),
				(int) (x * 1E6)); // (纬度, 经度)
	}
	
	public static boolean isCheckNet(Context context) {

		// -1：网络不可用 0：移动网络 1：wifi网络 2：未知网络
		int i = isNetworkEnabled(context);
		if (i == -1 || i == 2) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断网络是否可用
	 * 
	 * @return -1：网络不可用 0：移动网络 1：wifi网络 2：未知网络
	 */
	public static int isNetworkEnabled(Context context) {
		int status = -1;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			switch (networkInfo.getType()) {
			case ConnectivityManager.TYPE_MOBILE: { // 移动网络
				status = 0;
				break;
			}
			case ConnectivityManager.TYPE_WIFI: { // wifi网络
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
	 * 设置网络
	 * @param context
	 */
	public static void setNetwork(Context context) {
		final Context con = context;
		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle("网络状态");
		builder.setMessage("当前网络不可用，是否设置网络？");
		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent wifiSettingsIntent = new Intent(
						"android.settings.WIFI_SETTINGS");
				con.startActivity(wifiSettingsIntent);

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create();
		builder.show();
	}
	

}
