package com.example.xiangjun.qingxinyaoyiyao.function;

import android.widget.TextView;

public class RequestData {
	private TextView show;
	//private RequestKind request_kind;
	//private String url;
	//private String token;
	private String begin;
	private String count;
	private String device_id;
	private String major;
	private String uuid;
	private String minor;
	private String[] page_ids;
	private String type;
	private String comment;
	private String title;
	private String description;
	private String page_url;
	private String icon_url;

	//参数说明：
	//begin:从第几个开始获取;
	//count:一次获取几个;
	//device_id:所需要操作的设备id
	//major:所需要操作的设备major
	//page_id:所需要操作的页面id
	//type:
	public RequestData(String begin, String count, 
	        String device_id, String major,String[] page_ids, String type,
	        String minor, String uuid, String comment,String title, 
	        String description, String page_url, String icon_url){
		//this.show=show;
		//this.request_kind=requestkind;
		//this.url=url;
		//this.token=token;
		this.begin=begin;
		this.count=count;
		this.device_id=device_id;
		this.major=major;
		this.page_ids=page_ids;
		this.type=type;
		this.minor=minor;
		this.uuid=uuid;
		this.comment=comment;
		this.title=title;
		this.description=description;
		this.page_url=page_url;
		this.icon_url=icon_url;
	}
	
//	public RequestKind getRequest_kind(){
//		return request_kind;
//	}
	
//	public String getUrl(){
//		return url;
//	}
//	
//	public String getToken(){
//		return token;
//	}
	
	public String getDevice_id(){
		return device_id;
	}
	public String getCount(){
		return count;
	}
	
	public String getBegin(){
		return begin;
	}
	
	public String[] getPage_ids(){
		return page_ids;
	}
	
	public String getType(){
		return type;
	}
	
	public String getUuid(){
		return uuid;
	}
	
	public String getMinor(){
		return minor;
	}
	
	public String getMajor(){
		return major;
	}
	
	public String getComment(){
		return comment;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getDescription(){
		return description;
	}
	
	public String getPage_url(){
		return page_url;
	}
	
	public String getIcon_url(){
		return icon_url;
	}

}
