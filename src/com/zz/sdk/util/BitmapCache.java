package com.zz.sdk.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class BitmapCache {

	private static Map<String, Bitmap> table = new HashMap<String, Bitmap>();

	private static int density;

	public static Bitmap getBitmap(Context ctx, String path) {
		if (table.containsKey(path)) {
			return table.get(path);
		}

		InputStream in = null;
		try {
			in = ctx.getAssets().open(path);
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			table.put(path, bitmap);
			return bitmap;
		} catch (IOException e) {
			return Bitmap.createBitmap(50, 50, Bitmap.Config.RGB_565);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static Drawable getDrawable(Context ctx, String path) {
		Bitmap bitmap = getBitmap(ctx, path);
		return getDrawable(ctx, bitmap);
	}

	public static Drawable getDrawable(Context ctx, Bitmap bitmap) {

		if (density == 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) ctx
					.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(metrics);
			density = metrics.densityDpi;
		}

		BitmapDrawable d = new BitmapDrawable(bitmap);
		d.setTargetDensity((int) (density * (density * 1.0f / 240)));
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

	public static NinePatchDrawable getNinePatchDrawable(Context ctx,
			String path) {
		try {
			Bitmap bm = BitmapFactory.decodeStream(ctx.getAssets().open(
					Constants.ASSETS_RES_PATH + path));
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
			return d;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
