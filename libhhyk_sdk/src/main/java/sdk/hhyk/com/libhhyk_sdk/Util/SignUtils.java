package sdk.hhyk.com.libhhyk_sdk.Util;

import com.halocashkit.order.HaloCashPayOrderUtils;

import sdk.hhyk.com.libhhyk_sdk.PayConfig;

public class SignUtils {

	private static final String ALGORITHM = "RSA";

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static String getAlgorithms(boolean rsa2) {
		return rsa2 ? SIGN_SHA256RSA_ALGORITHMS : SIGN_ALGORITHMS;
	}
	
	public static String getTransdata() {

		HaloCashPayOrderUtils orderUtils = new HaloCashPayOrderUtils();
		orderUtils.setSubject("1111");
		orderUtils.setMerchantid("111111");
		orderUtils.setOuttradeno("123455678877");
		orderUtils.setAmount("0.2");
		orderUtils.setCurrency("HKD");
		orderUtils.setNotifyurl("http://www.iapppay.com/test");
		orderUtils.setCustomerid(123456+"");

		return orderUtils.getTransdata(PayConfig.PRIVATE_KEY);
	}

}
