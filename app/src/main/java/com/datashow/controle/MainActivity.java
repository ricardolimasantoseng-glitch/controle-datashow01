package com.datashow.controle;

import android.app.Activity;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView web;
    private ConsumerIrManager ir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ir = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);

        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);

        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new IrBridge(), "AndroidIR");
        web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    private class IrBridge {

        @JavascriptInterface
        public boolean hasEmitter() {
            return ir != null && ir.hasIrEmitter();
        }

        @JavascriptInterface
        public boolean transmit(int freq, String pattern) {
            if (ir == null || !ir.hasIrEmitter()) return false;
            try {
                String[] parts = pattern.split(",");
                int[] p = new int[parts.length];
                for (int i = 0; i < parts.length; i++) p[i] = Integer.parseInt(parts[i].trim());
                ir.transmit(freq, p);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
