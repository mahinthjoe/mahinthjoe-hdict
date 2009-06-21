package com.google.io.kmeans;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Simple Activity demonstrating a k-means clustering visualizer.
 */
public class KMeansActivity extends Activity {
    public static final String CLUSTERER_CLASSNAME = "com.google.io.kmeans.clustererclass";
    KMeansView view;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    		

        Intent intent = getIntent();
		Class<? extends Clusterer> clazz;
		try {
			// Fscking generics.
			clazz = (Class<? extends Clusterer>) Class.forName(intent.getExtras().getString(CLUSTERER_CLASSNAME)).asSubclass(Clusterer.class);
		} catch (Exception e) {
			// either the key wasn't present in intent, or points to an invalid class
			// either way, just fall back on default
			clazz = null;
		}
    	if (clazz == null) {
    		// no classname specified, fall back on default View mode
            setContentView(R.layout.clusterer);
            view = (KMeansView) findViewById(R.id.kmeans_view);
    	} else {
    		view = new KMeansView(this, clazz);
    		setContentView(view);
    	}
    }

	@Override
	protected void onPause() {
		super.onPause();
		view.killThread();
		finish(); // let's just force a restart
	}
}