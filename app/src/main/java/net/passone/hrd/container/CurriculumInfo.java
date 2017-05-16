package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/*
 * API ó�� ���?
 */
@JsonAutoDetect()
@JsonIgnoreProperties(ignoreUnknown = true) 

public class CurriculumInfo {

	@JsonProperty("curridx")	public String curridx;		
	@JsonProperty("orderby")	public String orderby;			
	@JsonProperty("subject")	public String subject;			
	@JsonProperty("leccount")	public String leccount;			
	@JsonProperty("fileurl")	public String fileurl;			

}