package sdk.hhyk.com.libhhyk_sdk.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;


@SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
public class DWebView extends WebView {
    private Context mContext;
//    /**
//     * 默认背景白色
//     */
//    private String mTitleBg = "ffffff";
//    /**
//     * 默认字体黑色
//     */
//    private String mTitleTextColor = "FF000000";
//    private String mTitle;
    private ProgressDialog progressDialog;
    /**
     * 默认是显示
     */
    private boolean reviceHtmlError;

    public DWebView(Context context) {
        super(context);
        mContext = context;
        progressDialog=new ProgressDialog(context,true);
        progressDialog.setCancelable(true);
        setWebChromeClient(new WebChromeClient());
        WebSettings settings = getSettings();

        // 不让左右滑动
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setBlockNetworkImage(false);
//        settings.setPluginState(PluginState.ON);
        settings.setAllowFileAccess(true);
//		settings.setPluginsEnabled(true);
        settings.setLoadWithOverviewMode(true);

        setWebViewClient(new HelloWebViewClient());

    }




    public class HelloWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            if (progressDialog!=null){
                progressDialog.show();
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if (reviceHtmlError) {
                return;
            }
            super.onPageFinished(view, url);


        }

        /*
         * (non-Javadoc)
         *
         * @see
         * android.webkit.WebViewClient#onReceivedError(android.webkit.WebView,
         * int, java.lang.String, java.lang.String)
         */
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            if (progressDialog!=null&&progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }


    /*
     * (non-Javadoc)
     *
     * @see android.webkit.WebView#loadUrl(java.lang.String)
     */
    @Override
    public void loadUrl(String url) {
        reviceHtmlError = false;
        super.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (canGoBack()) {
                goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
