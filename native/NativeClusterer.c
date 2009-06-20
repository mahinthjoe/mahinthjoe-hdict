#include <jni.h>
#include <math.h>
#include <stdlib.h>

double computeDistance(int aX, int aY, int bX, int bY) {
    return sqrt((aX - bX) * (aX - bX) + (aY - bY) * (aY - bY));
}

typedef struct {
    int x;
    int y;
    int cluster;
} point;

#define MAX_LOOP_COUNT 15;

JNIEXPORT void JNICALL
Java_com_google_io_kmeans_NativeClusterer_cluster(JNIEnv* env, jobject jobj,
jobjectArray points, jint numClusters, jint width, jint height) {
    int converged = 0;
    int dirty = 0;
    double curMinDistance = 0;
    int loopCount = 0;
    jsize numPoints = (*env)->GetArrayLength(env, points);
    double distances[numPoints];
    point pointsAry[numPoints];
    point means[numClusters];
    int sumX[numClusters];
    int sumY[numClusters];
    int clusterSizes[numClusters];
    double distance = 0;
    int cluster = 0;
    jobject point;
    jclass pointClass;
    jfieldID xFID, yFID, clusterFID;
    jint ptX = 0, ptY = 0;
    int i, j;

    // cache some JNI objects so we don't keep requerying them
    pointClass = (*env)->FindClass(env, "com/google/io/kmeans/Clusterer$Point");
    xFID = (*env)->GetFieldID(env, pointClass, "x", "I");
    yFID = (*env)->GetFieldID(env, pointClass, "y", "I");
    clusterFID = (*env)->GetFieldID(env, pointClass, "cluster", "I");

    // copy the xs & ys into a flat array, so that we don't make so many JNI calls
    for (i = 0; i < numPoints; ++i) {
        point = (*env)->GetObjectArrayElement(env, points, i);
        pointsAry[i].x = (*env)->GetIntField(env, point, xFID);
        pointsAry[i].y = (*env)->GetIntField(env, point, yFID);
        pointsAry[i].cluster = (*env)->GetIntField(env, point, clusterFID);
        (*env)->DeleteLocalRef(env, point);
    }


    // initialize the means
    for (i = 0; i < numClusters; ++i) {
        means[i].x = rand() % width;
        means[i].y = rand() % height;
        means[i].cluster = i;
    }

    // initializes the distances record
    for (i = 0; i < numPoints; ++i) {
        distances[i] = 100000000.0; // might break for larger resolutions
    }

    // the main loop
    while (!converged) {
        dirty = 0;
        for (i = 0; i < numPoints; ++i) {
            for (j = 0; j < numClusters; ++j) {
                distance = computeDistance(pointsAry[i].x, pointsAry[i].y, means[j].x, means[j].y);
                if (distance < distances[i]) {
                    dirty = 1;
                    distances[i] = distance;
                    pointsAry[i].cluster = j;
                }
            }
        }
        if (!dirty) {
            converged = 1;
            break;
        }
        // clear out the data structures for computing centroids
        for (i = 0; i < numClusters; ++i) {
            sumX[i] = sumY[i] = clusterSizes[i] = 0;
        }
        for (i = 0; i < numPoints; ++i) {
            cluster = pointsAry[i].cluster;
            sumX[cluster] += pointsAry[i].x;
            sumY[cluster] += pointsAry[i].y;
            clusterSizes[cluster] += 1;
        }
        for (i = 0; i < numClusters; ++i) {
            means[i].x = sumX[i] / clusterSizes[i];
            means[i].y = sumY[i] / clusterSizes[i];
        }
        loopCount++;
        converged = converged ? 1 : loopCount > MAX_LOOP_COUNT;
    }

    // copy the final cluster assignments back into the input objects
    for (i = 0; i < numPoints; ++i) {
        point = (*env)->GetObjectArrayElement(env, points, i);
        (*env)->SetIntField(env, point, clusterFID, pointsAry[i].cluster);
        (*env)->DeleteLocalRef(env, point);
    }

    (*env)->DeleteLocalRef(env, pointClass);
    return;
}
