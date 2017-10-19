package sdk.hhyk.com.libhhyk_sdk.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;


/**
 * 对话框显示
 * 
 * @author smallwei
 * 
 */
public class DtDialog extends Dialog {
	private Context mContext;
	private int width;
	private int height;
	private String transid;
	private String orderID;


	public DtDialog(Context context ,String transid,String orderID) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager windowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics=new DisplayMetrics();
	   windowManager.getDefaultDisplay().getMetrics(metrics);
		width=metrics.widthPixels;
		height=metrics.heightPixels;
		this.transid=transid;
		this.orderID=orderID;
		init(context,  width, height);
	}

	public DtDialog(Context context, int theme,String transid,String orderID) {
		super(context, theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager windowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics=new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		width=metrics.widthPixels;
		height=metrics.heightPixels;
		this.transid=transid;
		this.orderID=orderID;
		init(context,  width, height);
	}

	private void init(Context context, int width, int height) {
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams layoutParams=new WindowManager.LayoutParams();
		layoutParams.width=width;
		layoutParams.height=height;
		this.
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().getAttributes().windowAnimations = 0;
		
		if (!(context instanceof Activity)) {
			getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		}
		
		if (android.os.Build.VERSION.SDK_INT >= 14) {// 4.0 需打开硬件加速
			getWindow().setFlags(0x1000000, 0x1000000);
		}
		this.mContext = context;
		setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {

			}
		});

		DView dView=new DView(context,width,height);

		dView.initData(transid,orderID);

		dView.setCloseInterface(new CloseInterface() {
			@Override
			public void close() {
				cancel();
			}
		});


		setContentView(dView,layoutParams);

	}
	public interface CloseInterface{
		void close();
	}
}


