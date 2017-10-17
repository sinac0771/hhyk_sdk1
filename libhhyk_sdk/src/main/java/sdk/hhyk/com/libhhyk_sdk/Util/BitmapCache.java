package sdk.hhyk.com.libhhyk_sdk.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class BitmapCache {

	private static Map<String, Bitmap> table = new HashMap<String, Bitmap>();

	private static int density;
	/**
	 * ic_dt_close.png base64编码
	 */
	public static final String close_png = "iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyNpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDE0IDc5LjE1MTQ4MSwgMjAxMy8wMy8xMy0xMjowOToxNSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIChNYWNpbnRvc2gpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjM3MzQxQjZGMjZFQjExRTVCMTE3RjQxQTEwMDY5QjAyIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOjM3MzQxQjcwMjZFQjExRTVCMTE3RjQxQTEwMDY5QjAyIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6MzczNDFCNkQyNkVCMTFFNUIxMTdGNDFBMTAwNjlCMDIiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6MzczNDFCNkUyNkVCMTFFNUIxMTdGNDFBMTAwNjlCMDIiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz4+QAz8AAAEkUlEQVR42uyc7UsUQRzH507tEizt4fSiEh9IQaSEsEvsYLkg3/SA+KZ/oEP6g+Kwv6AXcdADpC/y0KMoESpEKDCONFKuzDRIT81+P28u1nV3b/d2ZnZub7/w9eTQmZ0PMzu/eQzs7e0RX8aqxR+BQMAnoSOsPLUu5HsSHAGHwU3gRnA9OASuo3+zDd4C/wH/Aq+Bc+Bl8KrIhw0gJc41CAvdAe4Et1EYToTQsuAF8GcKk1sN4gmoBdwH7lbVDNZCOB/B72ntqghA58ED9FOklsCvwIuyAjoFVmgzclPY/NLgH7IAqgFfBffT32XQX/AM+DV4101A2APdou8bGbUCfkp7QuGAusBD4COShzN58Dj4k0hAUfC1Cov7MuA3dgEFy8hIqUA4hD6zYvefgmXAuVzBowd89hgvQFcqHE5Z5bAK6IJd8pJLoZ0ME0CNtLfymm7QsjkCVEPjnJAHAYVo2WqcAIpKHASyGlBHywXURIcPXlc/LattQHFCXJlQEy0s43W7gM6C20n1qI2W2TKgqJPcwuFw3dTU1EhPT08D75JhHplMZgTzdJhU1CqgFqe1J5VK3Y7FYgOTk5OjPCFh2ul0enRwcHAA83SYXLteh6QHqM/pgycSifFcLrfc3Nwc4QWpCAdqTgTzwjwZJHuxFCCspt1Oc5mfn/+tKEqSFyQtHMwL82SQdJc2LtIC6iCMJth5QeIIB3WUFFZfDAF1smwGrCFxhqOuJIaA2li/K1hBEgQH1WoECFc86zlkeAgSFtQOJIFwUMfUkbUaUIRnvKKGhAW1CkkwHHWgfAhQmHdQZxeSS3AOsAhqBqdEFkguwiFGTey4qIFPKUguwym+h/alXva5JxKSEQj83mU4qHXwQ+262H1evZgdSHSw6yYcFG6xeaBdF3NlhVTb3CSAc4BFkPgylRpQ3o0H0DYxu3ESJ+X1AG27DQebVTnBJAdt6wHadBsOvnPKjbgZa1MP0LrbcJwOSxhqQw/QmgxwJIG0pgcoJwscCSDl9AAtywRHDSkej4uG9FUP0CqNIKWBU9Tc3JxISBtGTQyVlQ2OC5AWjQJF1IKMcARDWjADxOzsA68pC86QNksBKp59kBKOAEi4VXjXDBDqndNcxsbGhniPyrWQME8GyX7QfmG0T3qEOFgCwo0EqVTqTiKReMF7yqK3t7chmUwODQ8PPwFYTl4P2EE9Vn9htpEcZ/XvVtnMxiNSODF0AFDQJFDKVhGcrBaOlQmzl+CdKoCzQ8tK7AL6SQpHiryuGVpW24BQePhjxcNwVkiJAy6lAGFMgOettjwIB8v0jJQ4cGdl0h4Po014ENAEsTAHZnVVAyPMaQ/BmSYWD9jZWfZ5C571AJxZWhbCGhAqXeGQZmkZCC9ARUiZCoSTsQvHbCxmRf6hXgvCfTQ3idzHwp+bBYK8AaFwXzFeRdFP5Fnrl+ZiAbX8qyksqpXWqHOCwSzRGvOFVYK8r8fBXbOXiH89Tkn5FyzZFG5YPwM+DT5BCvsiMVQIqUKGPB1M4uc67YW+g78RgVd07V+A59+CZw6o9j8pX7r6J8AAxqiHCmh2xFAAAAAASUVORK5CYII=";

	/**
	 * 取关闭图片
	 * 
	 * @return {@link Bitmap}
	 */
	public static Bitmap getClosePng(Context context) {
		final byte[] decode = Base64.decode(close_png, Base64.NO_WRAP);
		Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
		if (bitmap != null)
			bitmap = Bitmap.createScaledBitmap(bitmap,
					Utils.dip2px(context, 28), Utils.dip2px(context, 28), true);
		return bitmap;
	}

	public static Bitmap getBitmapFromAsset(Context ctx, String path,
                                            int width, int height) {
		if (table.containsKey(path)) {
			return table.get(path);
		}

		InputStream in = null;
		try {
			in = ctx.getAssets().open(path);
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			if (width != 0 && height != 0 && bitmap != null)
				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			table.put(path, bitmap);
			return bitmap;
		} catch (IOException e) {
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public static Bitmap getBitmapFromSd(Context ctx, String path, int width,
                                         int height) {
		if (table.containsKey(path)) {
			return table.get(path);
		}

		InputStream in = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				return null;
			}
			in = new FileInputStream(file);
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			if (width != 0 && height != 0 && bitmap != null)
				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			table.put(path, bitmap);
			return bitmap;
		} catch (IOException e) {
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {

				}
			}
		}
	}

	public static Drawable getDrawableFromAsset(Context ctx, String path) {
		Bitmap bitmap = getBitmapFromAsset(ctx, path, 0, 0);
		return getDrawable(ctx, bitmap);
	}

	public static Drawable getCloseDb(Context ctx) {
		Bitmap bitmap = getClosePng(ctx);
		return getDrawable(ctx, bitmap);
	}

	public static Drawable getDrawableFromAsset(Context ctx, String path,
                                                int width, int height) {
		Bitmap bitmap = getBitmapFromAsset(ctx, path, width, height);
		return getDrawable(ctx, bitmap);
	}

	public static Drawable getDrawableFromSd(Context ctx, String path,
                                             int width, int height) {
		Bitmap bitmap = getBitmapFromSd(ctx, path, width, height);
		return getDrawable(ctx, bitmap);
	}

	public static Drawable getDrawable(Context ctx, Bitmap bitmap) {

		if (bitmap == null) {
			return null;
		}

		if (density == 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) ctx
					.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(metrics);
			density = metrics.densityDpi;
		}

		BitmapDrawable d = new BitmapDrawable(bitmap);
		d.setTargetDensity(density);
		return d;
	}

	public static void remove(Bitmap bitmap) {
		for (String key : table.keySet()) {
			Bitmap bitmap2 = table.get(key);
			if (bitmap2 == bitmap) {
				table.remove(key);
				return;
			}
		}
	}

	public static void clear() {
		table.clear();
		System.gc();
	}

	static class NinePatchChunk {

		public static final int NO_COLOR = 0x00000001;
		public static final int TRANSPARENT_COLOR = 0x00000000;

		public Rect mPaddings = new Rect();

		public int mDivX[];
		public int mDivY[];
		public int mColor[];

		private static void readIntArray(int[] data, ByteBuffer buffer) {
			for (int i = 0, n = data.length; i < n; ++i) {
				data[i] = buffer.getInt();
			}
		}

		private static void checkDivCount(int length) {
			if (length == 0 || (length & 0x01) != 0) {
				throw new RuntimeException("invalid nine-patch: " + length);
			}
		}

		public static NinePatchChunk deserialize(byte[] data) {
			ByteBuffer byteBuffer = ByteBuffer.wrap(data).order(
					ByteOrder.nativeOrder());

			byte wasSerialized = byteBuffer.get();
			if (wasSerialized == 0)
				return null;

			NinePatchChunk chunk = new NinePatchChunk();
			chunk.mDivX = new int[byteBuffer.get()];
			chunk.mDivY = new int[byteBuffer.get()];
			chunk.mColor = new int[byteBuffer.get()];

			checkDivCount(chunk.mDivX.length);
			checkDivCount(chunk.mDivY.length);

			// skip 8 bytes
			byteBuffer.getInt();
			byteBuffer.getInt();

			chunk.mPaddings.left = byteBuffer.getInt();
			chunk.mPaddings.right = byteBuffer.getInt();
			chunk.mPaddings.top = byteBuffer.getInt();
			chunk.mPaddings.bottom = byteBuffer.getInt();

			// skip 4 bytes
			byteBuffer.getInt();

			readIntArray(chunk.mDivX, byteBuffer);
			readIntArray(chunk.mDivY, byteBuffer);
			readIntArray(chunk.mColor, byteBuffer);

			return chunk;
		}
	}

	// public static NinePatchDrawable getNinePatchDrawable(Context ctx,
	// String path) {
	// try {
	// Bitmap bm = BitmapFactory.decodeStream(ctx.getAssets().open(
	// Constants.ASSETS_RES_PATH + path));
	// byte[] chunk = bm.getNinePatchChunk();
	// boolean isChunk = NinePatch.isNinePatchChunk(chunk);
	// if (!isChunk) {
	// return null;
	// }
	// Rect rect = new Rect();
	// // rect.left = 20;
	// // rect.top = 20;
	// // rect.right = 20;
	// // rect.bottom = 20;
	// NinePatchChunk npc = NinePatchChunk.deserialize(chunk);
	// NinePatchDrawable d = new NinePatchDrawable(bm, chunk,
	// npc.mPaddings, null);
	// d.getPadding(rect);
	// return d;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	public static NinePatchDrawable getNinePatchDrawable(Context ctx,
                                                         String path) {
		try {
			Bitmap bm = BitmapFactory.decodeStream(ctx.getAssets().open(path));

			byte[] chunk = bm.getNinePatchChunk();
			boolean isChunk = NinePatch.isNinePatchChunk(chunk);
			if (!isChunk) {
				return null;
			}
			Rect rect = new Rect();
			// rect.left = 20;
			// rect.top = 20;
			// rect.right = 20;
			// rect.bottom = 20;
			NinePatchChunk npc = NinePatchChunk.deserialize(chunk);
			NinePatchDrawable d = new NinePatchDrawable(bm, chunk,
					npc.mPaddings, null);
			d.getPadding(rect);
			if (density == 0) {
				DisplayMetrics metrics = new DisplayMetrics();
				WindowManager wm = (WindowManager) ctx
						.getSystemService(Context.WINDOW_SERVICE);
				wm.getDefaultDisplay().getMetrics(metrics);
				density = metrics.densityDpi;
			}

			d.setTargetDensity((int) (density * (density * 1.0f / 240)));
			return d;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isImgExistFromAsset(Context ctx, String path) {
		InputStream in = null;
		try {
			in = ctx.getAssets().open(path);
			if (in != null) {
				return true;
			}
		} catch (IOException e) {
		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {

				}
			}
		}
		return false;
	}
}
