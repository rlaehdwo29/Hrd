package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


/*
 * ����
 */
@JsonAutoDetect()
@JsonIgnoreProperties(ignoreUnknown = true) 

public class Version {

	@JsonProperty("version")			public String version;		// �����ڵ�
	@JsonProperty("link")			public String link;			// ���� �޽���
	@JsonProperty("new")			public String newcontent;			// ���� �޽���
	@JsonProperty("servercheck")	public int servercheck;			// ���� �޽���
	@JsonProperty("serverstr")	public String serverstr;			// ���� �޽���

}

