package sdk.hhyk.com.libhhyk_sdk.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.halocash.lib.interfaces.HaloCashPay;
import com.halocash.lib.interfaces.HaloCashResultCallback;
import com.halocashkit.order.HaloCashPayOrderUtils;
import com.halocashkit.order.RSAHelper;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sdk.hhyk.com.libhhyk_sdk.PayConfig;
import sdk.hhyk.com.libhhyk_sdk.callback.PayCallback;
import sdk.hhyk.com.libhhyk_sdk.callback.PayListener;
import sdk.hhyk.com.libhhyk_sdk.entity.OrderModel;
import sdk.hhyk.com.libhhyk_sdk.entity.PayResult;
import sdk.hhyk.com.libhhyk_sdk.entity.UrlEntity;
import sdk.hhyk.com.libhhyk_sdk.http.OkHttpClientManager;
import sdk.hhyk.com.libhhyk_sdk.view.DtDialog;
import sdk.hhyk.com.libhhyk_sdk.view.ProgressDialog;

@SuppressLint("HandlerLeak")
public class PayManager implements  PayCallback {

	private static final String TAG = "PayManger";
	private static final int	CREATE_ORDER_SUCCESS=200;
	private static final int	CREATE_ORDER_FAIL=400;

	public static PayManager instance;

	private final static Lock lock = new ReentrantLock();

	private Context context;

	private PayListener payListener;


	private ProgressDialog progressDialog;
	private static OkHttpClientManager okHttpClientManager;
    private  String orderID;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (progressDialog!=null){
					progressDialog.dismiss();
				}
				break;


			case 200:
//				Toast.makeText(instance.context, "购买成功.="+msg.obj.toString(), Toast.LENGTH_SHORT)
//						.show();
				openWeb((String)msg.obj);
				break;

			case 400:
				Toast.makeText(instance.context, "购买失败."+msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				break;


			default:
				break;
			}
		}
	};

	private PayManager(Context context){
		okHttpClientManager=OkHttpClientManager.getInstance();
		progressDialog=new ProgressDialog(context,true);
		this.context=context;
	}

	public static PayManager instance(Context context){
	if (instance==null){
		instance=new PayManager(context);
	}

	return instance;
}

	/**
	 *
	 * @param outtradeno 商户订单号
	 * @param subject 商品名称
	 * @param amount 支付金额
	 * @param payListener
	 */
	public void payMent(String  outtradeno,String  subject,String  amount,
						 String  currency,   PayListener payListener) {
		if (progressDialog!=null){
			progressDialog.show();
		}
        orderID=outtradeno;

		HaloCashPayOrderUtils payOrderUtils=new HaloCashPayOrderUtils();
		instance.payListener = payListener;
		payOrderUtils.setSubject(subject);
		payOrderUtils.setMerchantid(PayConfig.MERCHANT_ID);
		payOrderUtils.setOuttradeno(outtradeno);
		payOrderUtils.setAmount(amount);
		payOrderUtils.setCurrency(currency);
		payOrderUtils.setNotifyurl(PayConfig.NOTIFY_URL);
		payOrderUtils.setCustomerid(UUID.randomUUID().toString());

		String param=payOrderUtils.getTransdata(PayConfig.PRIVATE_KEY);
//		init(PayConfig.PAY_INIT, this, new InitProgressView(context));
		okHttpClientManager.postAsyn(PayConfig.URL_PAYAPI_ORDER,this,param);


	}

	/**
	 * 签名校验
	 *
	 * @param signValue
	 * @return
	 * @throws Exception
	 */
	private boolean signCpPaySuccessInfo(String signValue) throws Exception {
		int transdataLast = signValue.indexOf("&sign=");
		String transdata = URLDecoder.decode(signValue.substring("transdata=".length(), transdataLast));
		int signLast = signValue.indexOf("&signtype=");
		String sign = URLDecoder.decode(signValue.substring(transdataLast + "&sign=".length(), signLast));
		String signtype = signValue.substring(signLast + "&signtype=".length());
		if (signtype.equals("RSA") && RSAHelper.verify(transdata, PayConfig.PUBLIC_KEY, sign)) {
			return true;
		} else {
		}
		return false;
	}




	@Override
	public void onPaySuccess(String data) {

		boolean isSignSuc = false;
		mHandler.sendEmptyMessage(1);
		try {
			isSignSuc = signCpPaySuccessInfo(data);
			if (isSignSuc) {
				data = URLDecoder.decode(data, "utf-8");
				String[] orderArray = data.split("&");
				int length = "transdata=".length();
				String transdata = orderArray[0].substring(length);
				JSONObject obj = new JSONObject(transdata);
				String transid = obj.getString("transid");

				Message msg = mHandler.obtainMessage();
				msg.what = CREATE_ORDER_SUCCESS;
				msg.obj = transid;
				mHandler.sendMessage(msg);
//				startPay(transid,0);
			} else {
				Log.e(OkHttpClientManager.class.getSimpleName(),"sign fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    private void openWeb(String transid) {
        DtDialog dialog=new DtDialog(instance.context,transid,orderID);
        dialog.show();

    }
    /**
     * 调用sdk支付接口
     *
     * @param transid: 商户后台服务器下单返回的数据。
     * @param payType: 指定支付方式，0为不指定
     */
    public void startPay( String transid, int payType) {
        /**
         * @param country: 指定国家、币种、语言 比如 "HK,HKD,ZH", 如果为空，代表不指定, 如果指定了就会跳过首次进入设置国家的页面，直接进入支付列表页面。
         */
        final String country = "";
        HaloCashPay.startPay((Activity) context, transid, payType, country, new HaloCashResultCallback() {
            @Override
            /**
             * 返回的支付结果 resultCode 返回的结果码 resultInfo 返回支付结果信息
             */
            public void onPayResult(int resultCode, String resultInfo) {
                Log.e(TAG, "pay result, resultCode: " + resultCode + ", resultInfo: " + resultInfo);
                Toast.makeText(context, resultInfo, Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
	public void onFailure(String message) {
		mHandler.sendEmptyMessage(1);
		Message msg = mHandler.obtainMessage();
		msg.what = CREATE_ORDER_FAIL;
		msg.obj = message;
		mHandler.sendMessage(msg);
	}

}
