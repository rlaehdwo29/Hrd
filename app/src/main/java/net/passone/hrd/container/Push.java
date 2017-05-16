package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect()
public class Push {

	@JsonProperty("result")			public String result;		// �����ڵ�
	@JsonProperty("message")			public String message;			// ���� �޽���


}

