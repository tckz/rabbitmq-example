package cc.breeze;

@SuppressWarnings("serial")
public class NotImplementedException extends RuntimeException {

	public NotImplementedException() {
	}
	
	public NotImplementedException(String mes) {
		super(mes);
	}
	
	public NotImplementedException(String mes, Throwable t) {
		super(mes, t);
	}
}

