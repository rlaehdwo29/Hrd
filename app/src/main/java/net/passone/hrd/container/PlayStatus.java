package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

/*
 * API ó�� ���?
 */
@JsonAutoDetect()
public class PlayStatus {

	@JsonProperty("result")	public String result;		// �����ڵ�
	@JsonProperty("moviename")	public String moviename;
	@JsonProperty("htmlname")	public String htmlname;
	@JsonProperty("pagename")	public String pagename;
	@JsonProperty("ncnt")	public String ncnt;
	@JsonProperty("tcnt")	public String tcnt;
	@JsonProperty("classkey")	public String classkey;
	@JsonProperty("classcount")	public String classcount;
	@JsonProperty("studykey")	public String studykey;
	@JsonProperty("sns")	public String sns;
	@JsonProperty("marker")	public String marker;
	@JsonProperty("finish")	public String finish;
	@JsonProperty("markertime")	public String markertime;
	@JsonProperty("controlpbar")	public String controlpbar;
	@JsonProperty("part")	public String part;
	@JsonProperty("page")	public String page;
	@JsonProperty("prevpart")	public String prevpart;
	@JsonProperty("prevpage")	public String prevpage;
	@JsonProperty("nextpart")	public String nextpart;
	@JsonProperty("nextpage")	public String nextpage;

}