package com.google.io.kmeans;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

/**
 * A custom Android view that repeatedly runs a k-means clustering algorithm in a thread, and
 * draws it into a SurfaceView.  See also the LunarLander sample.
 */
public class KMeansView extends SurfaceView implements Callback {
	private KMeansThread thread;

	/**
	 * Constructor called when instantiated via declarative XML layout.
	 * @param context the Android application context
	 * @param attrs the XML attributes specified in the XML declaration
	 */
	public KMeansView(Context context, AttributeSet attrs) {
		super(context, attrs);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// this ctor means the view is instantiated via XML; use DalvikClusterer as default
		thread = new KMeansThread(holder, new DalvikClusterer());
	}
	
	/**
	 * Constructor that allows specification of the Clusterer to use.
	 * @param context the Android application context
	 * @param clustererClass the Clusterer instance to use
	 */
	public KMeansView(Context context, Class<? extends Clusterer> clustererClass) {
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		try {
			thread = new KMeansThread(holder, (Clusterer) clustererClass.newInstance());
		} catch (Exception e) {
			Log.w("KMeansView constructor", "failed to instantiate Clusterer; falling back on Dalvik default");
			thread = new KMeansThread(holder, new DalvikClusterer());
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		thread.setRunning(true);
		thread.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		killThread();
	}
	
	public void killThread() {
		thread.setRunning(false);
		boolean done = false;
		while (!done) {
			try {
				thread.join();
				done = true;
			} catch (InterruptedException e) {
			}
		}
	}
}
