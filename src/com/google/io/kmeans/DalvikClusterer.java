package com.google.io.kmeans;

import java.util.HashMap;
import java.util.Random;

/**
 * An implementation of a 15-pass k-means clusterer in Java code, suitable for
 * running on the Dalvik virtual machine on an Android device.
 */
public class DalvikClusterer implements Clusterer {
	private static final int MAX_LOOP_COUNT = 15;
	private final HashMap<Point, Double> distances = new HashMap<Point, Double>(
			KMeansThread.POINT_COUNT);
	private final Random random = new Random(System.currentTimeMillis());

	public void cluster(Point[] points, int numClusters, int width, int height) {
		boolean converged = false;
		boolean dirty;
		double distance;
		double curMinDistance;
		int loopCount = 0;
		
		// randomly pick some points to be the centroids of the groups, for the first pass
		Point[] means = new Point[numClusters];
		for (int i = 0; i < numClusters; ++i) {
			means[i] = new Point(random.nextInt(width), random.nextInt(height));
			means[i].cluster = i;
		}

		// initialize data
		distances.clear();
		for (Point point : points) {
			distances.put(point, Double.MAX_VALUE);
		}
		int[] sumX = new int[numClusters];
		int[] sumY = new int[numClusters];
		int[] clusterSizes = new int[numClusters];

		// main loop
		while (!converged) {
			dirty = false;
			// compute which group each point is closest to
			for (Point point : points) {
				curMinDistance = distances.get(point);
				for (Point mean : means) {
					distance = computeDistance(point, mean);
					if (distance < curMinDistance) {
						dirty = true;
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
			// compute the new centroids of the groups, since contents have changed
			for (int i = 0; i < numClusters; ++i) {
				sumX[i] = sumY[i] = clusterSizes[i] = 0;
			}
			for (Point point : points) {
				sumX[point.cluster] += point.x;
				sumY[point.cluster] += point.y;
				clusterSizes[point.cluster] += 1;
			}
			for (int i = 0; i < numClusters; ++i) {
				means[i].x = (int)(sumX[i] / clusterSizes[i]);
				means[i].y = (int)(sumY[i] / clusterSizes[i]);
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
		return Math.sqrt((a.x - b.x) * (a.x - b.x)
				+ (a.y - b.y) * (a.y - b.y));
	}
}
