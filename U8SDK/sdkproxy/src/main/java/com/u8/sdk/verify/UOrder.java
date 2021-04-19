package com.u8.sdk.verify;

public class UOrder {

	private String order;
	private String extension;
	
	public UOrder(String order, String ext){
		this.order = order;
		this.extension = ext;
	}
	
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	
	
}
