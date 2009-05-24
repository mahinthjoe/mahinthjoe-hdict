package com.google.io.kmeans;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.google.io.kmeans.Clusterer.Point;

/**
 * Class that demonstrates how to integrate Dalvik with browser JavaScript.
 */
public class KMeansWebViewActivity extends Activity {
	private static final String URI = "http://hdict-io09.appspot.com/index2.html";
	private WebView webView;

	/**
	 * Inner class that is injected into the JavaScript namespace and proxies a Clusterer
	 */
	class ClustererProxy {
		String[] xVals;
		String[] yVals;
		Point[] points;
		int[] ret;

		/**
		 * Executes a k-means cluster run on the input (x, y) points.
		 * @param xs The x-coordinates of the points to be clustered
		 * @param ys The y-coordinates of the points to be clustered
		 * @return the cluster assignments in range (0->6)
		 */
		public String cluster(String xs, String ys) {
			// set up the Points in the format expected by the Clusterer interface
			xVals = xs.split(",");
			yVals = ys.split(",");
			points = new Point[xVals.length];
			for (int i = 0; i < xVals.length; ++i) {
				points[i] = new Point(Integer.parseInt(xVals[i]), Integer.parseInt(yVals[i]));	
				points[i].cluster = -1;
			}

			// run the algorithm
			Clusterer clusterer = new DalvikClusterer(); // for a good time, try NativeClusterer!
			clusterer.cluster(points, 6, webView.getWidth(), webView.getHeight());
			ret = new int[xVals.length];
			for (int i = 0; i < xVals.length; ++i) {
				ret[i] = points[i].cluster;
			}
			
			// use strings as inputs/returns since addJavaScriptInterface doesn't handle arrays
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < xVals.length; ++i) {
				sb.append(ret[i]);
				if (i < (xVals.length - 1)) {
					sb.append(",");
				}
			}
			return sb.toString();
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alternate);
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new ClustererProxy(), "CLUSTER");
		webView.clearCache(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.loadUrl(URI);
	}
}