package com.beeecorptv.ui.splash;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.beeecorptv.ui.base.BaseActivity;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.beeecorptv.R;
import com.beeecorptv.databinding.ActivitySplashBinding;
import com.beeecorptv.di.Injectable;
import com.beeecorptv.ui.login.LoginActivity;
import com.beeecorptv.ui.manager.AdsManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.StatusManager;
import com.beeecorptv.ui.viewmodels.SettingsViewModel;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.GlideApp;
import com.beeecorptv.util.Tools;
import javax.inject.Inject;
import javax.inject.Named;
import dagger.android.AndroidInjection;


public class SplashActivity extends AppCompatActivity implements Injectable {

    ActivitySplashBinding binding;


    private SettingsViewModel settingsViewModel;

    @Inject
    SettingsManager settingsManager;


    @Inject
    AdsManager adsManager;


    @Inject
    StatusManager statusManager;


    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Inject
    @Named("sniffer")
    @Nullable
    ApplicationInfo provideApplicationInfo;

    @Inject
    @Named("package_name")
    String packageName;



    @Inject
    @Named("vpn")
    boolean checkVpn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);

        onHideTaskBar();
        onLoadLogo();
        onHideTaskBar();
        onLoadSplashImage();

        if (provideApplicationInfo != null){

            DialogHelper.snifferAppDetectorDialog(this,provideApplicationInfo.loadLabel(this.getPackageManager()).toString());

        }else if (settingsManager.getSettings().getVpn() ==1 && checkVpn) {


            finishAffinity();

            Toast.makeText(this, R.string.vpn_message, Toast.LENGTH_SHORT).show();

        } else {

            settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);
            settingsViewModel.getSettingsDetails();
            settingsViewModel.settingsMutableLiveData.observe(this, settings -> settingsManager.saveSettings(settings));
            settingsViewModel.adsMutableLiveData.observe(this, ads -> adsManager.saveSettings(ads));

            new Handler(Looper.getMainLooper()).postDelayed(() -> {

//                startActivity(new Intent(this, LoginActivity.class));
//                finish();
//            },2000);

                startActivity(new Intent(SplashActivity.this, BaseActivity.class));
                finish();
            },2000);
        }

    }


    private void onLoadSplashImage() {

        GlideApp.with(getApplicationContext()).asBitmap().load(settingsManager.getSettings().getSplashImage())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(binding.splashImage);

    }


    // Hide TaskBar
    private void onHideTaskBar() {

        Tools.hideSystemPlayerUi(this,true,0);
    }


    // Load Logo
    private void onLoadLogo() {

        Tools.loadMiniLogo(this,binding.logoImageTop);


    }
}
