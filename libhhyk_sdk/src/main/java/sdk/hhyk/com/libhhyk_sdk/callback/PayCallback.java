package sdk.hhyk.com.libhhyk_sdk.callback;


public interface PayCallback {
	
	public void onPaySuccess(String data);
	public void onFailure(String message);

}
