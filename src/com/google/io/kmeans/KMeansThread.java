package com.google.io.kmeans;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.google.io.kmeans.Clusterer.Point;

/**
 * A Thread which repeatedly runs a k-means clustering algorithm and draws the results into
 * a SurfaceView.
 */
public final class KMeansThread extends Thread {
	public static final int POINT_COUNT = 500;
	public static final int[] COLORS = new int[] { Color.RED,
			Color.argb(255, 255, 69, 0), Color.YELLOW, Color.GREEN, Color.BLUE,
			Color.argb(255, 255, 0, 255) };

	Point[] points;
	private SurfaceHolder holder;
	private boolean sizeSet = false;
	private boolean running = false;
	private long clusterDuration;
	private Clusterer clusterer;
	private Random random = new Random(System.currentTimeMillis());
	private int width;
	private int height;

	@Override
	public void run() {
		long start;
		while (running) {
			while (!sizeSet) {
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {}
				continue;
			}
			// randomly pick a new set of points for each pass, then do the work
			prepare();
			start = System.currentTimeMillis();
			clusterer.cluster(points, COLORS.length, width, height);
			clusterDuration = System.currentTimeMillis() - start;
			draw(points);
		}
	}

	/**
	 * Draws a set of points into the SurfaceView provided to this class.
	 * @param points the points to be drawn
	 */
	private void draw(Point[] points) {
		Paint paint = new Paint();
		paint.setAntiAlias(false);
		Canvas canvas = holder.lockCanvas();
		try {
			long start = System.currentTimeMillis();
			canvas.drawARGB(255, 0, 0, 0); // black the screen
			for (Point point : points) {
				paint.setColor(COLORS[point.cluster]);
				canvas.drawPoint((int)point.x, (int)point.y, paint);
			}
			long duration = System.currentTimeMillis() - start;
			canvas.drawText("" + duration, 10, 10, paint);
			canvas.drawText("" + clusterDuration, 10, 50, paint);
		} finally {
			holder.unlockCanvasAndPost(canvas);
		}
	}

	/**
	 * Preferred constructor.
	 * @param holder a handle to get the SurfaceView to draw into
	 * @param clusterer a k-means clustering implementation
	 */
	public KMeansThread(SurfaceHolder holder, Clusterer clusterer) {
		this.holder = holder;
		this.clusterer = clusterer;
		points = new Point[POINT_COUNT];
		for (int i = 0; i < POINT_COUNT; ++i) {
			points[i] = new Point(0, 0);
		}
	}

	/**
	 * Used to halt the thread.
	 * @param b if true, keep running; if false, terminate
	 */
	public void setRunning(boolean b) {
		running = b;
	}

	/**
	 * Used to indicate the size of the surface. This can be commonly called; e.g. on device
	 * reorientation.
	 * @param width new width
	 * @param height new height
	 */
	public void setSurfaceSize(int width, int height) {
		this.width = width;
		this.height = height;
		sizeSet = true;
	}

	/**
	 * Resets the state: picks a new random set of points.
	 */
	private void prepare() {
		for (Point point : points) {
			point.set(random.nextInt(width), random.nextInt(height));
			point.setCluster(-1);
		}
	}
}
