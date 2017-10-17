package sdk.hhyk.com.libhhyk_sdk.Util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Utils {

	private static boolean hasTranslateEncode = false;

	public static StateListDrawable getStateListDrawable(Context context,
                                                         int radius, int normal, int pressed) {
		StateListDrawable listDrawable = new StateListDrawable();
		GradientDrawable normalDrawable = new GradientDrawable(
				Orientation.TOP_BOTTOM, new int[] { normal, normal, normal });
		normalDrawable.setCornerRadii(new float[] { dip2px(context, radius),
				dip2px(context, radius), dip2px(context, radius),
				dip2px(context, radius), dip2px(context, radius),
				dip2px(context, radius), dip2px(context, radius),
				dip2px(context, radius) });

		GradientDrawable pressedDrawable = new GradientDrawable(
				Orientation.TOP_BOTTOM, new int[] { pressed, pressed, pressed });
		pressedDrawable.setCornerRadii(new float[] { dip2px(context, radius),
				dip2px(context, radius), dip2px(context, radius),
				dip2px(context, radius), dip2px(context, radius),
				dip2px(context, radius), dip2px(context, radius),
				dip2px(context, radius) });

		listDrawable.addState(new int[] { android.R.attr.state_pressed },
				pressedDrawable);
		listDrawable.addState(new int[] { android.R.attr.state_selected },
				pressedDrawable);
		listDrawable.addState(new int[] {}, normalDrawable);
		return listDrawable;
	}

	public static boolean isHasAlertPermission(Context context,
			String permission) {
		boolean flag = false;
		PackageManager pm = context.getPackageManager();
		String[] permissions;
		try {
			permissions = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_PERMISSIONS).requestedPermissions;// 获取权限列表
			if (permissions != null && permissions.length > 0) {
				for (String string : permissions) {
					if (string.equals(permission)) {
						flag = true;
						break;
					}
				}
			}
		} catch (NameNotFoundException e) {
		}
		return flag;
	}

	public static int dip2px(Context ctx, int dpValue) {
		float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 把字符串编码成base64
	 * 
	 * @param src
	 * @return
	 */
	public static String enBase64(String src) {
		if (null != src) {
			if (hasTranslateEncode) {
				try {
					return Base64.encodeToString(src.getBytes("UTF-8"),
							Base64.NO_WRAP);
				} catch (UnsupportedEncodingException e) {
				}
			} else {
				return src;
			}
		}
		return null;
	}

	/**
	 * 把base64字符串解码成明文
	 * 
	 * @param src
	 * @return
	 */
	public static String deBase64(String src) {
		if (null != src) {
			if (hasTranslateEncode) {
				try {
					return new String(Base64.decode(src.getBytes(),
							Base64.NO_WRAP), "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
			} else {
				return src;
			}
		}
		return null;
	}

	/**
	 * 把指定的字节数组用gzip压缩
	 * @param src
	 * @return 压缩过的字节数组
	 */
	public static byte[] gzip(byte[] src) {
		if (null != src) {
			final ByteArrayOutputStream os = new ByteArrayOutputStream(
					src.length);
			try {
				final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
						os);
				gzipOutputStream.write(src);
				gzipOutputStream.flush();
				gzipOutputStream.close();
				return os.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 把指定的字节数组用gzip解压缩
	 * @param src
	 * @return 解压缩过的字节数组
	 */
	public static byte[] ungzip(byte[] src) {
		if (null != src) {
			final ByteArrayInputStream is = new ByteArrayInputStream(src);
			final ByteArrayOutputStream os = new ByteArrayOutputStream(
					src.length);
			try {
				final GZIPInputStream in = new GZIPInputStream(is);
				final byte[] buffer = new byte[512];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					os.write(buffer, 0, len);
					os.flush();
				}
				in.close();
				return os.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}
	
	/**
	 * md5加密
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	/**
	 * 判断日期是否是同一天,比较年月日
	 * @param day1
	 * @param day2
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isSameDay(long day1, long day2) {
		return isSameDay(new Date(day1), new Date(day2));
	}
	
	/**
	 *  判断日期是否是同一天,比较年月日
	 * @param day1
	 * @param day2
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean isSameDay(Date day1, Date day2) {
	    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	 /**
     * 从系统下载工具中后去下载后的文件Uri
     *
     * @param downloadId
     * @param downloadManager
     * @return
     */
    public static Uri getFileUri(long downloadId, DownloadManager downloadManager) {
        if (Build.VERSION.SDK_INT != 23) {
            return downloadManager.getUriForDownloadedFile(downloadId);
        }
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    if (Build.VERSION.SDK_INT == 23) {
                        String path = cursor.getString(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME));
                        if (!TextUtils.isEmpty(path)) {
                            return Uri.fromFile(new File(path));
                        }
                    }
                }
            }

            return downloadManager.getUriForDownloadedFile(downloadId);
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }

    }
}
