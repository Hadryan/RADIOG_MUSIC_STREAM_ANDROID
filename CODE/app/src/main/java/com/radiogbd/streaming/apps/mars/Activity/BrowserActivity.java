package com.radiogbd.streaming.apps.mars.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.Bkash;
import com.radiogbd.streaming.apps.mars.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowserActivity extends AppCompatActivity {

    Utility utility = new Utility(this);
    ImageView ivBack;
    TextView tvTitle;
    WebView wvAll;
    String url;
    String tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        url = getIntent().getExtras().getString("url");
        //url = "http://192.168.123.13:9090/result?serviceTitle=radiog%20monthly%20package&status=Subscribed&productName=radiog&reurl=&tid=20192307072042";
        if (getIntent().hasExtra("tag")) {
            tag = getIntent().getExtras().getString("tag");
        }
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        wvAll = (WebView) findViewById(R.id.wv_all);
        utility.setFonts(new View[]{tvTitle});
        if (tag.equals("GR")) {
            tvTitle.setText(utility.getLangauge().equals("bn") ? "ঘুড়ি পোর্টাল" : "Ghoori Portal");
        } else if (tag.equals("SK")) {
            tvTitle.setText(utility.getLangauge().equals("bn") ? getResources().getString(R.string.skitto_message_bn) : getResources().getString(R.string.skitto_message_en));
        } else if (tag.equals("BL")) {
            tvTitle.setText(utility.getLangauge().equals("bn") ? getResources().getString(R.string.bl1_message_bn) : getResources().getString(R.string.bl1_message_en));
        } else {
            tvTitle.setText(utility.getLangauge().equals("bn") ? "অনমোবাইল পোর্টাল" : "OnMobile Portal");
        }
        WebSettings webSettings = wvAll.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        wvAll.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wvAll.loadUrl(url);
        wvAll.setWebViewClient(new MyWebViewClient());
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wvAll.addJavascriptInterface(new WebAppInterface(this), "Android");
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!tag.equals("ON")) {
                utility.showProgress(true);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!tag.equals("ON")) {
                utility.hideProgress();
            }
        }
    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast() {
            Toast.makeText(mContext, "Thanks for your payment", Toast.LENGTH_SHORT).show();
            sendBroadcast(new Intent("bkash.payment.check"));
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvAll.canGoBack()) {
            wvAll.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
