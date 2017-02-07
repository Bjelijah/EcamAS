package com.howell.activity.fragment;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.howell.action.CenterAction;
import com.howell.ecam.R;
import com.howell.utils.ServerConfigSp;

/**
 * Created by Administrator on 2017/1/25.
 */

public class WebFragment extends Fragment {
    View mView;
    WebView mWebView;

    private String mErrorHtml = "";
    private View myView = null;
    private WebChromeClient.CustomViewCallback myCallback = null;
    private String mURL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("123","on web fragment onCreateView");
        mView = inflater.inflate(R.layout.fragment_web,container,false);

        initWeb();
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("123"," web fragment on create");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView!=null && CenterAction.getInstance().ismIsUpdata()){
            mWebView.loadUrl(getURL());
            CenterAction.getInstance().setmIsUpdata(false);
        }
    }

    @Override
    public void onPause() {
        Log.i("123","fragment pause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("123","fragment stop");
        super.onStop();
    }

    public void initWeb(){
        mWebView = (WebView) mView.findViewById(R.id.fragment_web_webview);
        if (getURL()==null)return;
        mWebView.loadUrl(mURL);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.requestFocus();
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.addJavascriptInterface(new MyWebClickCallback(),"demo");
        mErrorHtml = "<html><body><h1>Page not find！</h1></body></html>";
    }

    @Nullable
    private String getURL(){
        String ip = ServerConfigSp.loadCenterIP(getActivity().getApplicationContext());
        int port = ServerConfigSp.loadCenterPort(getActivity().getApplicationContext());
        if (ip==null)return null;
        mURL = "http://"+ip+":"+port;
        return mURL;
    }

    public boolean clickBack(){
        if (mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return false;
    }

    public boolean returnHomePage(){
        mWebView.loadUrl(mURL);
        return true;
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.loadUrl(request.getUrl().toString());
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            Log.i("123","onpage finished");
            addClickListner();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            view.loadData(mErrorHtml,"test/html","UTF-8");
        }
    }

    class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Log.i("123","on Show custom view");
//            super.onShowCustomView(view, callback);
            if (myCallback!=null){
                myCallback.onCustomViewHidden();
                myCallback = null;
                return;
            }
            long id = Thread.currentThread().getId();
            Log.i("123","thread id="+id);

            ViewGroup parent = (ViewGroup) mWebView.getParent();
            String s = parent.getClass().getName();
            Log.i("123","parent name="+s);
            view.setBackgroundColor(getResources().getColor(R.color.black));

            parent.removeView(mWebView);
            parent.addView(view);
            myView = view;
            myCallback = callback;

            setFullScreen();

        }

        @Override
        public void onHideCustomView() {
            hideCustomViewFun();
        }
    }

    public class MyWebClickCallback{
        @JavascriptInterface
        public void onWebClick(){
            Log.i("123","web click");
        }
        @JavascriptInterface
        public void onWebPrint(String msg){
            Log.i("123",msg);
        }
    }


    private void hideCustomViewFun(){
        if (myView==null)return;
        if (myCallback!=null){
            myCallback.onCustomViewHidden();
            myCallback = null;
        }
        ViewGroup parent = (ViewGroup) myView.getParent();
        parent.removeView(myView);
        parent.addView(mWebView);
        myView = null;
        quitFullScreen();
    }

    private void setFullScreen(){
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getActivity().getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    private void quitFullScreen(){
        WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
        attrs.flags &=(~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().setAttributes(attrs);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if(getActivity().getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    private void addClickListner(){

        String url= "javascript:function android_fun(){" +
                "window.demo.onWebPrint(\" 1111 \");"+
//                        "var objs = document.getElementById(\"divVideo\");"+
                "var objs=document.getElementsByClassName(\"col-md-9\");"+
//                        "objs.style.cursor = \'pointer\'"+
//                        "var obj=objs.getElementById(\"divVideo\");"+
                "window.demo.onWebPrint(\" 22222 \");"+
                "window.demo.onWebPrint(objs.toString());"+

//                        "alert(obj)"+
                "objs[0].onclick=function(){"+
                "window.demo.onWebClick();"+
                "alert(\" test \")"+
                "};"+
                "}"+
                "android_fun()";
        mWebView.loadUrl(url);
    }
}
