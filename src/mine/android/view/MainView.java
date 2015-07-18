package mine.android.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import mine.android.HeavenClock.R;

/**
 * Created by Heaven on 15/7/18
 */
public class MainView extends Activity {
    private static Context context = null;
    private WebView webView;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;

        webView = (WebView) findViewById(R.id.mainView);

        // WebView 设置
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setJavaScriptEnabled(true);

        // 设置加载过程显示的进入信息
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressDialog.setMessage(newProgress + "%");
            }
        });

        //js java 映射
//        webView.addJavascriptInterface(new JavascriptUser());
        webView.loadUrl("file:///android_asset/mainView.html");
    }
}
