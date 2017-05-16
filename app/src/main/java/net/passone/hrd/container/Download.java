package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/*
 * API ó�� ���?
 */
@JsonAutoDetect()
@JsonIgnoreProperties(ignoreUnknown = true) 

public class Download {

	@JsonProperty("result")	public String result;		// �����ڵ�
	@JsonProperty("message")	public String message;			// ���� �޽���
	@JsonProperty("url")	public String url;			// ���� �޽���

}