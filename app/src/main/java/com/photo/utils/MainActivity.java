package com.photo.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** 测试
 * Created by li on 2016/6/13.
 */
public class MainActivity extends Activity{
    private WebView webView;
    String url;
    private ProgressDialog progressDialog;
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中...");
        progressDialog.setCanceledOnTouchOutside(false);
        webView= (WebView) findViewById(R.id.webview);
        //设置支持JavaScript脚本
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setUserAgentString(XmlDB.getInstance(NewsWebViewActivity.this).getKeyString(Constant.access_token, ""));
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //设置可以支持缩放
        webSettings.setSupportZoom(true);
        //设置默认缩放方式尺寸是far
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        //设置出现缩放工具
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDefaultFontSize(20);
        setWebView();
    }
    private void setWebView(){
        url="http://172.16.131.163/infusion/Role/Dispensary";
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            // url拦截
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                // 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
                view.loadUrl(url);
                // 相应完成返回true
                return true;
                //return super.shouldOverrideUrlLoading(view, url);
            }

            // 页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
//                mProgressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            // 页面加载完成
            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
//                mProgressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            // WebView加载的所有资源url
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });
        // 设置WebChromeClient
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            ;

            @Override
            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            ;

            @Override
            // 处理javascript中的prompt
            public boolean onJsPrompt(WebView view, String url, String message,
                                      String defaultValue, final JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            ;

            //设置网页加载的进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                mProgressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }

            //设置程序的Title
            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
    }
}
