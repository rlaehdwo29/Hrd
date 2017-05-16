package net.passone.hrd.container;

public class ImageItem {
private String item_idx, item_sName,item_sImgUrl;
	
	public ImageItem() {		
	}
	
	public String getidx() {
		return item_idx;
	}
	
	public String getsName() {
		return item_sName;
	}
	public String getsImgUrl() {
		return item_sImgUrl;
	}
	public void setInfo(String idx, String sImgUrl,String sName) {
		item_idx = idx;
		item_sName = sName;	
		item_sImgUrl=sImgUrl;
	}	

}
