package mine.android.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import mine.android.HeavenClock.R;
import mine.android.ctrl.ClockCtrl;

/**
 * Created by Heaven on 15/7/19
 */
public class AlarmView extends Activity {
    private WebView webView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        webView = (WebView) findViewById(R.id.detailView);

        // WebView 设置
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

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
        webView.addJavascriptInterface(new ClockCtrl(handler, webView), "clock");
        webView.loadUrl("file:///android_asset/alarmView.html");
    }
}
