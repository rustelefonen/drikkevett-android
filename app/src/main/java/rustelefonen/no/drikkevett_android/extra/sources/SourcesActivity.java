package rustelefonen.no.drikkevett_android.extra.sources;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import rustelefonen.no.drikkevett_android.R;

/**
 * Created by simenfonnes on 26.07.2016.
 */

public class SourcesActivity extends AppCompatActivity {

    public WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sources_layout);
        webView = (WebView) findViewById(R.id.sources_web_view);
        webView.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColor));
        webView.loadUrl("file:///android_asset/sources.html");
    }
}
