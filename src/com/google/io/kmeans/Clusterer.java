package com.google.io.kmeans;

/**
 * An interface representing a class that knows how to implement k-means clustering.
 */
public interface Clusterer {
	/**
	 * A class representing a 2D point. We don't use android.graphics.Point because that
	 * class changes its hash value based on its coordinates, where this app needs to
	 * use an instance-based hash.
	 */
	public static class Point {
		public int x;
		public int y;
		public int cluster;

		public Point(int d, int e) {
			this.x = d;
			this.y = e;
		}

		public void set(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public void setCluster(int cluster) {
			this.cluster = cluster;
		}
	}

	/**
	 * Runs a k-means clustering pass on the indicated data.
	 * @param points The points to be clustered
	 * @param numClusters The number of clusters to group the points into
	 * @param width the range on the x-axis to use (range will be 0->width)
	 * @param height the range on the y-axis to use (range will be 0->height)
	 */
	public void cluster(Point[] points, int numClusters, int width, int height);
}
