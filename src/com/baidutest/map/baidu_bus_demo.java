package com.baidutest.map;

import android.util.Log;

import com.baidu.mapapi.search.MKTransitRouteResult;

public class baidu_bus_demo {
	// Log.i("xx", "���ط�������������Ϣ=" + mp.getContent()+",Ԥ�ƻ���ʱ��="+mp.getTime() / 60
	// +"����,����="+mp.getDistance()+"�ס�;������վ����="+buscount);
	// ���ط�������������Ϣ=���ݵ���1����_b4·,Ԥ�ƻ���ʱ��=42����,����=9505�ס�;������վ����=6
	private MKTransitRouteResult res;
	private String content;// ����������Ϣ
	private String time;// Ԥ�ƻ���ʱ��
	private String distance;// ����
	private String buscount; // ;������վ����
    private Integer i ; // res �еڼ�����·����
    
    


	public MKTransitRouteResult getRes() {
		return res;
	}

	public void setRes(MKTransitRouteResult res) {
		this.res = res;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getBuscount() {
		return buscount;
	}

	public void setBuscount(String buscount) {
		this.buscount = buscount;
	}
	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

}
