package mine.android.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import mine.android.view.MainView;

/**
 * Created by Heaven on 15/7/18
 */
public class ContextAPI {
    public static Context get() {
        return MainView.getContext();
    }

    public static void makeToast(String str) {
        Toast.makeText(get(), str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化webview
     * @param webView webview
     */
    public static void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        ProgressDialog progressDialog = new ProgressDialog(ContextAPI.get());
        progressDialog.setMessage("Loading...");
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


    }
}
