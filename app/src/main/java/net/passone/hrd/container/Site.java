package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;


/*
 * ����
 */
@JsonAutoDetect()
public class Site {

	@JsonProperty("siteid")			public String siteid;		
	@JsonProperty("sitename")		public String sitename;			


}
