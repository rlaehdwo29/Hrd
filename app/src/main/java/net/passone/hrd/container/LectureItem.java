package net.passone.hrd.container;

public class LectureItem {
private String part, page,subject,readflag,movieurl,htmlurl,size,classkey,filename,chasi,title,contentskey,classcount,studykey,cdeurl,filepath;
private boolean selected;
	public int isdown;

	public LectureItem() {
	}
	
	public String getPart() {
		return part;
	}
	
	public String getPage() {
		return page;
	}
	public String getSubject() {
		return subject;
	}
	public String getReadFlag() {
		return readflag;
	}
	public String getMovieurl() {
		return movieurl;
	}
	public String getHtmlurl() {
		return htmlurl;
	}
	public String getSize() {
		return size;
	}
	public String getClasskey() {
		return classkey;
	}
	public String getFilename() {
		return filename;
	}
	public String getTitle() {
		return title;
	}
	public String getChasi() {
		return chasi;
	}
	public String getContentsKey() {
		return contentskey;
	}
	public String getClassCount()
	{
		return classcount;
	}
	public String getStudykey()
	{
		return studykey;
	}
	public String getCdeUrl()
	{
		return cdeurl;
	}
	public int getIsDown() {
		return isdown;
	}
	public void setFilePath(String filepath)
	{
		this.filepath=filepath;
	}
	public String getFilepath() {
		return filepath;
	}

	public void setLecture(String part, String page,String subject, String readflag,String movieurl,String htmlurl,String contentskey,String cdeurl) {
		this.part = part;
		this.page = page;	
		this.subject = subject;	
		this.readflag = readflag;	
		this.movieurl = movieurl;	
		this.htmlurl = htmlurl;	
		this.contentskey=contentskey;
		this.cdeurl=cdeurl;
	}	
	public void setDownLecture(String part, String page,String subject, String readflag,String movieurl,String htmlurl,String size,String classkey,String filename,String chasi,String title,String contentskey,String classcount,String studykey) {
		this.part = part;
		this.page = page;	
		this.subject = subject;	
		this.readflag = readflag;	
		this.movieurl = movieurl;	
		this.htmlurl = htmlurl;	
		this.size = size;	
		this.classkey = classkey;	
		this.filename = filename;	
		this.title=title;
		this.chasi=chasi;
		this.contentskey=contentskey;
		this.classcount=classcount;
		this.studykey=studykey;

	}	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public void setIsDown(int isdown){
		this.isdown=isdown;
	}

}
