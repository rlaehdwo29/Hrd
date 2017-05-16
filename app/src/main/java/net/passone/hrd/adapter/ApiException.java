package net.passone.hrd.adapter;

@SuppressWarnings("serial")
public class ApiException extends Exception {

	public int _errCode = 0;
	public String _msg;
	public ApiException(int errCode, String msg) {
		_errCode = errCode;
		_msg = msg;
	}

	@Override
	public String getMessage() {
		return _msg;
	}

	public int getErrorCode() {
		return _errCode;
	}
}
