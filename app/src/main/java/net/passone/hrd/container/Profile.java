package net.passone.hrd.container;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;


	@JsonAutoDetect()
	public class Profile {

		@JsonProperty("result")			public String result;		// �����ڵ�
		@JsonProperty("message")			public String message;			// ���� �޽���
		@JsonProperty("nickname")			public String nickname;			// ���� �޽���
		@JsonProperty("profile_img")			public String profile_img;			// ���� �޽���
		@JsonProperty("profile_tmb")			public String profile_tmb;			// ���� �޽���

	}

