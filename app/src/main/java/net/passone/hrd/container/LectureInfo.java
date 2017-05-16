package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/*
 * API ó�� ���?
 */
@JsonAutoDetect()
@JsonIgnoreProperties(ignoreUnknown = true) 

public class LectureInfo {

	@JsonProperty("PART")	public String part;		
	@JsonProperty("PAGE")	public String page;			
	@JsonProperty("subject")	public String subject;			
	@JsonProperty("readflag")	public String readflag;			
	@JsonProperty("movieurl")	public String movieurl;			
	@JsonProperty("htmlurl")	public String htmlurl;			
	@JsonProperty("size")	public String size;			
	@JsonProperty("contentskey")	public String contentskey;			
	@JsonProperty("cdeurl")	public String cdeurl;			

}