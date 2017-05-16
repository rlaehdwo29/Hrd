package net.passone.hrd.common;

import java.io.ByteArrayOutputStream;

public class MyByteArrayOutputStream extends ByteArrayOutputStream {
	 @Override
	    public synchronized byte[] toByteArray() {
	        return buf;
	    }
}
