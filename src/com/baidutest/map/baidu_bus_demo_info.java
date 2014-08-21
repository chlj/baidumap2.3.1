package com.baidutest.map;

import android.util.Log;

import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.baidu.mapapi.search.MKTransitRouteResult;

public class baidu_bus_demo_info {
	
	
	 // Log.i("xx", "title=" + title + ",id=" + id + ",tip=" + tip);
	
	private MKTransitRoutePlan mk;
	private String title;
	private String busid;
	private String tip;
	public MKTransitRoutePlan getMk() {
		return mk;
	}
	public void setMk(MKTransitRoutePlan mk) {
		this.mk = mk;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getbusId() {
		return busid;
	}
	public void setbusId(String id) {
		this.busid = id;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}

    
    


	

}
