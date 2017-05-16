package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect()
public class ImageBox {

	@JsonProperty("idx")			public String idx;		// �����ڵ�
	@JsonProperty("sName")			public String sName;			// ���� �޽���
	@JsonProperty("sImgUrl")		public String sImgUrl;			// ���� �޽���
	
}