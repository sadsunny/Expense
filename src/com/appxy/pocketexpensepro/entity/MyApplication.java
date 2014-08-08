package com.appxy.pocketexpensepro.entity;

import java.util.ArrayList;

import android.app.Application;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.util.LruCache;

public class MyApplication extends Application {

	public static Bitmap nowbitmap;
	public static Bitmap savebitmap;
	public static Bitmap tempbitmap;
	public static Bitmap process_src;
	public static boolean isFront = false;
	public static boolean where = false;
	public static int oritation;
	public static boolean isUpdate = false;
	public static boolean isPad = false;
	public static boolean isAdd = false;
	public static boolean islist = false;
	public static boolean islistchanged = false;
	public static int listitemid = 0;
	public static String addpath;
	public static String folder_path;
	public static int folder_id = 0;
	public static int degree;
	public static int sizeid = 0;
	public static int stateheight = 0;
	public static boolean photofrom = false;
	public static String photopath;
	public static byte[] photodata;
	public static float photowidth, photoheight;
	public static float camerawidth, cameraheight;
	public static int displaywidth, dispalyheight;
	public static LruCache<String, Bitmap> mMemoryCache;
	public static Matrix matrix;
	public static boolean isHomePress = false;
	public static int howint = 0;
	public static int isFirstIn = 0;
	
	public void onCreate() {
		super.onCreate();

		nowbitmap = null;
		savebitmap = null;
		tempbitmap = null;
		process_src = null;
		matrix = new Matrix();
	}

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);

		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

}
