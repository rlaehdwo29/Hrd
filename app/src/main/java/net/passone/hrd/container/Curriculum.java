package net.passone.hrd.container;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/*
 * API ó�� ���?
 */
@JsonAutoDetect()
@JsonIgnoreProperties(ignoreUnknown = true) 

public class Curriculum {

	@JsonProperty("classkey")	public String classkey;		
	@JsonProperty("startdate")	public String startdate;			
	@JsonProperty("enddate")	public String enddate;			
	@JsonProperty("fullenddate")	public String fullenddate;		
	@JsonProperty("cdeyn")	public String cdeyn;			
	@JsonProperty("curriculum")	public List<CurriculumInfo> curriculum;			
}