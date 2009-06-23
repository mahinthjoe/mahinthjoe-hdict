package com.google.io.kmeans;

import java.util.Random;

/**
 * An implementation of a 15-pass k-means clusterer in Java code, suitable for
 * running on the Dalvik virtual machine on an Android device.
 */
public class DalvikClusterer implements Clusterer {
	private static final int MAX_LOOP_COUNT = 15;
	private final double[] distances = new double[KMeansThread.POINT_COUNT];
	private final Random random = new Random(System.currentTimeMillis());

	public void cluster(Point[] points, int numClusters, int width, int height) {
		boolean converged = false;
		boolean dirty;
		double distance;
		double curMinDistance;
		int loopCount = 0;
		Point point;

		// randomly pick some points to be the centroids of the groups, for the
		// first pass
		Point[] means = new Point[numClusters];
		for (int i = 0; i < numClusters; ++i) {
			means[i] = new Point(random.nextInt(width), random.nextInt(height));
			means[i].cluster = i;
		}

		// initialize data
		for (int i = 0; i < points.length; ++i) {
			distances[i] = Double.MAX_VALUE;
		}
		int[] sumX = new int[numClusters];
		int[] sumY = new int[numClusters];
		int[] clusterSizes = new int[numClusters];

		double tmpX, tmpY;

		// main loop
		while (!converged) {
			dirty = false;
			// compute which group each point is closest to
			for (int i = 0; i < points.length; ++i) {
				point = points[i];
				curMinDistance = distances[i];
				for (Point mean : means) {
					distance = computeDistance(point, mean);
					if (distance < curMinDistance) {
						dirty = true;
						distances[i] = distance;
						curMinDistance = distance;
						point.cluster = mean.cluster;
					}
				}
			}
			// if we did no work, break early (greedy algorithm has converged)
			if (!dirty) {
				converged = true;
				break;
			}
			// compute the new centroids of the groups, since contents have
			// changed
			for (int i = 0; i < numClusters; ++i) {
				sumX[i] = sumY[i] = clusterSizes[i] = 0;
			}
			for (int i = 0; i < points.length; ++i) {
				point = points[i];
				sumX[point.cluster] += point.x;
				sumY[point.cluster] += point.y;
				clusterSizes[point.cluster] += 1;
			}
			for (int i = 0; i < numClusters; ++i) {
				try {
					tmpX = sumX[i] / clusterSizes[i];
					tmpY = sumY[i] / clusterSizes[i];
					means[i].x = (int) tmpX;
					means[i].y = (int) tmpY;
				} catch (ArithmeticException e) {
					// means a Divide-By-Zero error, b/c no points were associated with this cluster.
					// rare, so reset the cluster to have a new random center
					means[i].x = random.nextInt(width);
					means[i].y = random.nextInt(height);
				}
			}

			// bail out after at most MAX_LOOP_COUNT passes
			loopCount++;
			converged = converged || (loopCount > MAX_LOOP_COUNT);
		}
	}

	/**
	 * Computes the Cartesian distance between two points.
	 */
	private double computeDistance(Point a, Point b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
}
