package com.google.io.kmeans;

/**
 * An implementation of a k-means clustering algorithm in C. That is, this class is a proxy
 * for a JNI call to a C implementation.
 */
public class NativeClusterer implements Clusterer {
	static {
		System.loadLibrary("clusterer");
	}
	public native void cluster(Point[] points, int numClusters, int width, int height);
}
