package net.passone.hrd.container;

public class CurriculumItem {
private String curridx, orderby,subject,leccount,fileurl;
	
	public CurriculumItem() {		
	}
	
	public String getIdx() {
		return curridx;
	}
	
	public String getOrderby() {
		return orderby;
	}
	public String getSubject() {
		return subject;
	}
	public String getLeccnt() {
		return leccount;
	}
	public String getFileurl() {
		return fileurl;
	}
	public void setCurriculum(String curridx, String orderby,String subject, String leccount,String fileurl) {
		this.curridx = curridx;
		this.orderby = orderby;	
		this.subject = subject;	
		this.leccount = leccount;	
		this.fileurl = fileurl;	

	}	

}
