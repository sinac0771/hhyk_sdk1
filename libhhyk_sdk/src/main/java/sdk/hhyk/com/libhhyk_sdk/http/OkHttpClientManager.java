package sdk.hhyk.com.libhhyk_sdk.http;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.halocash.volley.SSLSocketFactoryCompat;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sdk.hhyk.com.libhhyk_sdk.PayConfig;
import sdk.hhyk.com.libhhyk_sdk.callback.PayCallback;

public class OkHttpClientManager {

	private static OkHttpClientManager instance;
    private Handler mDelivery;
    private  OkHttpClient mOkHttpClient;
    private Gson mGson;

private OkHttpClientManager(){
    mOkHttpClient=setHttpClient();
    mGson=new Gson();
    mDelivery = new Handler(Looper.getMainLooper());
};
    public static OkHttpClientManager getInstance() {
        if (instance==null){
            synchronized (OkHttpClientManager.class){
            instance=new OkHttpClientManager();
            }
        }
        return instance;
    }
    private Response getAsyn(String url ) throws IOException{

        Request request=new Request.Builder().url(url).build();

        Call call=mOkHttpClient.newCall(request);

        return call.execute();
    }
    private String getAsString(String url ) throws IOException{
        Response execute=getAsyn(url);
        return execute.body().string();
    }
    private void  _getAsyn(String url , ResultCallback callback){

        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    private Response _post(String url, Param... params) throws IOException
    {
        Request request = buildPostRequest(url, params);
        Response response = mOkHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return 字符串
     */
    private String _postAsString(String url, Param... params) throws IOException
    {
        Response response = _post(url, params);
        return response.body().string();
    }

    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Param... params)
    {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }
    /**
     * 异步的post请求
     *
     * @param url
     * @param callback
     * @param params
     */
    private void _postAsyn(String url, final ResultCallback callback, Map<String, String> params)
    {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    private Param[] map2Params(Map<String, String> params)
    {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries)
        {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }
    private Request buildPostRequest(String url, Param[] params)
    {
        if (params == null)
        {
            params = new Param[0];
        }
        FormBody.Builder  builder = new FormBody.Builder();
        for (Param param : params)
        {
            builder.add(param.key, param.value);

        }

        FormBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }




    private void deliveryResult(final ResultCallback callback, final Request request)
    {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(request, e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try
                {
                    final String string = response.body().string();
                    if (callback.mType == String.class)
                    {
                        sendSuccessResultCallback(string, callback);
                    } else
                    {
                        Object o = mGson.fromJson(string, callback.mType);
                        sendSuccessResultCallback(o, callback);
                    }


                } catch (IOException e)
                {
                    sendFailedStringCallback(response.request(), e, callback);
                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    sendFailedStringCallback(response.request(), e, callback);
                }
            }
        });
    }
    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                    callback.onError(request, e);
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onResponse(object);
                }
            }
        });
    }
    public static abstract class ResultCallback<T>{

        private Type mType;

        protected ResultCallback() {
            mType=getSuperclassTypeParameter(getClass());
        }

        protected static Type getSuperclassTypeParameter(Class<? > aClass){

            Type superclass=aClass.getGenericSuperclass();
            if (superclass instanceof  Class){
                throw new RuntimeException("missing type parameter");
            }

            ParameterizedType parameterized=(ParameterizedType)superclass;


            return  $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }
        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);

    }


    public static class Param
    {
        public Param()
        {
        }

        public Param(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    private OkHttpClient setHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
//                    // 自定义一个信任所有证书的TrustManager，添加SSLSocketFactory的时候要用到
            final X509TrustManager trustAllCert =
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    };
            final SSLSocketFactory sslSocketFactory = new SSLSocketFactoryCompat(trustAllCert);
            builder.sslSocketFactory(sslSocketFactory, trustAllCert);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }


    public static final MediaType JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    /**
     * 对外
     */
    public  void postAsyn(String url, final PayCallback callback, final String params)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = setHttpClient();
                Log.e(OkHttpClientManager.class.getSimpleName(), "商户下单请求参数：" + params + "商户下单地址 = " + PayConfig.URL_PAYAPI_ORDER);
                RequestBody body = RequestBody.create(JSON, params);
                Request request = new Request.Builder().url(PayConfig.URL_PAYAPI_ORDER).post(body).build();
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String orderData = response.body().string();
                        Log.d(OkHttpClientManager.class.getSimpleName(), "response:" + orderData + "response result = " + response.isSuccessful());

                        if (orderData.contains("code")) {
//                            Log.e(OkHttpClientManager.class.getSimpleName(),"create transid error:"+orderData);
//                            Message msg = myHandler.obtainMessage();
//                            msg.what = CREATE_ORDER_FAIL;
//                            msg.obj = orderData;
//                            myHandler.sendMessage(msg);
                            if (callback!=null){
                                callback.onFailure(orderData);
                            }
                        } else {
                            if (callback!=null){
                                callback.onPaySuccess(orderData);
                            }
//                            boolean isSignSuc = signCpPaySuccessInfo(orderData);
//                            if (isSignSuc) {
//                                orderData = URLDecoder.decode(orderData, "utf-8");
//                                String[] orderArray = orderData.split("&");
//                                int length = "transdata=".length();
//                                String transdata = orderArray[0].substring(length);
//                                JSONObject obj = new JSONObject(transdata);
//                                String transid = obj.getString("transid");
//                                Message msg = myHandler.obtainMessage();
//                                msg.what = CREATE_ORDER_SUCCESS;
//                                msg.obj = transid;
//                                myHandler.sendMessage(msg);
//                            } else {
//                                Log.e(OkHttpClientManager.class.getSimpleName(),"sign fail");
//                            }
                        }
                    } else {
                        Log.e(OkHttpClientManager.class.getSimpleName(), "response failure");
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    Log.e(OkHttpClientManager.class.getSimpleName(), "response failure，error is " + e.getMessage() + "，cause is " + e.getCause());
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
