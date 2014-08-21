package com.baidutest.map;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.testdemo.R.string;

public class baidu_map_point_demo {
   
	private GeoPoint point; //点
	private String name;// 名称
	private String type ;// 类型
	
	public GeoPoint getPoint() {
		return point;
	}
	public void setPoint(GeoPoint point) {
		this.point = point;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public baidu_map_point_demo(){}
	
	public baidu_map_point_demo(GeoPoint mGeoPoint ,String mNAME){
		point=mGeoPoint;
		name=mNAME;
	}

	public baidu_map_point_demo(GeoPoint mGeoPoint ,String mNAME,String types){
		point=mGeoPoint;
		name=mNAME;
		type=types;
	}
}
