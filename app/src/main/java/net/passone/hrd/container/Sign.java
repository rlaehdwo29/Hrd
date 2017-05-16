package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


/*
 * ����
 */
@JsonAutoDetect()
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sign {

	@JsonProperty("result")			public String result;		// �����ڵ�
	@JsonProperty("message")			public String message;			// ���� �޽���
	@JsonProperty("mno")			public String mno;			// ���� �޽���


}
