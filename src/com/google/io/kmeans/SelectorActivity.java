package com.google.io.kmeans;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectorActivity extends Activity {
    protected static final String HDICT_URI = "http://hdict-io09.appspot.com/index.html";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ((Button)findViewById(R.id.pure_dalvik_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        Intent intent = new Intent();
		        intent.setClassName(SelectorActivity.this, "com.google.io.kmeans.KMeansActivity");
		        intent.setAction(Intent.ACTION_VIEW);
		        intent.putExtra(KMeansActivity.CLUSTERER_CLASSNAME, "com.google.io.kmeans.DalvikClusterer");
		        startActivity(intent);
			}
        });
        
        ((Button)findViewById(R.id.native_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        Intent intent = new Intent();
		        intent.setClassName(SelectorActivity.this, "com.google.io.kmeans.KMeansActivity");
		        intent.setAction(Intent.ACTION_VIEW);
		        intent.putExtra(KMeansActivity.CLUSTERER_CLASSNAME, "com.google.io.kmeans.NativeClusterer");
		        startActivity(intent);
			}
        });
        
        ((Button)findViewById(R.id.webview_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        Intent intent = new Intent();
		        intent.setClassName(SelectorActivity.this, "com.google.io.kmeans.KMeansWebViewActivity");
		        intent.setAction(Intent.ACTION_VIEW);
		        startActivity(intent);
			}
        });
        
        ((Button)findViewById(R.id.browser_button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(HDICT_URI)));
			}
        });
    }
}
