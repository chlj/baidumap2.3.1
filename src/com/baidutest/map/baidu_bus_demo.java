package com.baidutest.map;

import android.util.Log;

import com.baidu.mapapi.search.MKTransitRouteResult;

public class baidu_bus_demo {
	// Log.i("xx", "返回方案具体描述信息=" + mp.getContent()+",预计花费时间="+mp.getTime() / 60
	// +"分钟,距离="+mp.getDistance()+"米。途经公交站个数="+buscount);
	// 返回方案具体描述信息=杭州地铁1号线_b4路,预计花费时间=42分钟,距离=9505米。途经公交站个数=6
	private MKTransitRouteResult res;
	private String content;// 具体描述信息
	private String time;// 预计花费时间
	private String distance;// 距离
	private String buscount; // 途经公交站个数
    private Integer i ; // res 中第几条线路方案
    
    


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
