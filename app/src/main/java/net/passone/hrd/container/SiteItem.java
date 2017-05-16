package net.passone.hrd.container;

public class SiteItem {
private String siteid, sitename;
	
	public SiteItem() {		
	}
	
	public String getsiteId() {
		return siteid;
	}
	
	public String getsiteName() {
		return sitename;
	}
	
	public void setSite(String siteid, String sitename) {
		this.siteid = siteid;
		this.sitename = sitename;	
	}	

}
