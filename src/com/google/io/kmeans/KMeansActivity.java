package com.google.io.kmeans;

import android.app.Activity;
import android.os.Bundle;

/**
 * Simple Activity demonstrating a k-means clustering visualizer.
 */
public class KMeansActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}