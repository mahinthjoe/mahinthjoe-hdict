package com.google.io.kmeans;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

/**
 * A custom Android view that repeatedly runs a k-means clustering algorithm in a thread, and
 * draws it into a SurfaceView.  See also the LunarLander sample.
 */
public class KMeansView extends SurfaceView implements Callback {
	private KMeansThread thread;

	public KMeansView(Context context, AttributeSet attrs) {
		super(context, attrs);

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		// use either the Dalvik version, or the native version; don't uncomment both
		thread = new KMeansThread(holder, new DalvikClusterer());
		//thread = new KMeansThread(holder, new NativeClusterer());
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
