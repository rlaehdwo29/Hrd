package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;


@JsonAutoDetect()
public class Uri {

	@JsonProperty("url")			public String url;		// URL �ּ�

}
