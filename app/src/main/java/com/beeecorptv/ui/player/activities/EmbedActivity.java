package com.beeecorptv.ui.player.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.beeecorptv.R;
import com.beeecorptv.databinding.ActivityEmbedBinding;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.Tools;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


public class EmbedActivity extends AppCompatActivity {


    ActivityEmbedBinding binding;


    @Inject
    SettingsManager settingsManager;


    /**
     * Called when the activity is first created.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_embed);
        Tools.setSystemBarTransparent(this);
        Tools.hideSystemPlayerUi(this,true,0);
        Intent receivedIntent = getIntent();
        String mMovieId = receivedIntent.getStringExtra(Constants.MOVIE_LINK);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies( binding.webView, true);

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebChromeClient(new WebChromeClient());

        binding.webView.getSettings().setUserAgentString(settingsManager.getSettings().getUserAgent());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webView.getSettings().setAllowContentAccess(true);
        binding.webView.getSettings().setBlockNetworkImage(false);
        binding.webView.loadUrl(mMovieId);

    }

}
