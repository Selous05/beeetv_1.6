package com.beeecorptv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.multidex.MultiDexApplication;
import com.applovin.sdk.AppLovinSdk;
import com.appodeal.ads.Appodeal;
import com.beeecorptv.di.AppInjector;
import com.beeecorptv.ui.downloadmanager.core.DownloadNotifier;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.util.Tools;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.stringcare.library.SC;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.vungle.warren.InitCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import timber.log.Timber;

/**
 * Application level class.
 *
 * @author Yobex.
 */
public class BeeeTvApp extends MultiDexApplication implements HasAndroidInjector {



    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Inject
    SettingsManager settingsManager;


    @SuppressLint("StaticFieldLeak")
    private static Context context;


    @Inject
    @Named("vpn")
    boolean isVpnRunning;




    @Override
    public void onCreate() {
        SC.init(this);
        super.onCreate();

        DownloadNotifier downloadNotifier = DownloadNotifier.getInstance(this);
        downloadNotifier.makeNotifyChans();
        downloadNotifier.startUpdate();
        SC.init(this);
        AppInjector.init(this);


        Appodeal.disableLocationPermissionCheck();

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus -> {});

        if (BuildConfig.DEBUG) {Timber.plant(new Timber.DebugTree());}

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

       // Initialize Vungle Network SDK
        Vungle.init(settingsManager.getSettings().getVungleAppid(), this, new InitCallback() {
            @Override
            public void onSuccess() {
                //
            }

            @Override
            public void onError(VungleException exception) {
              //
            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {
                // Ad has become available to play for a cache optimized placement
            }
        });

        // Initialize the Unity Ads  Network SDK
        if (settingsManager.getSettings().getUnityGameId() !=null) {

            UnityAds.initialize(this, settingsManager.getSettings().getUnityGameId(), false, new IUnityAdsInitializationListener() {
                @Override
                public void onInitializationComplete() {
                   //
                }

                @Override
                public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                    //
                }
            });
        }



        AppLovinSdk.getInstance(this).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, configuration -> {
            // AppLovin SDK is initialized, start loading ads
        });

        // Initialize the StartAppSDK
        StartAppSDK.init(this,settingsManager.getSettings().getStartappId(),false);
        StartAppSDK.enableReturnAds(false);

        // Disable StartAppSDK Splash
        StartAppAd.disableSplash();
        Timber.i("Creating EasyPlex Application");
        BeeeTvApp.context = getApplicationContext();

        onInitPaypal();

    }


    public static boolean hasNetwork() {
        return Tools.checkIfHasNetwork(context);
    }

    private void onInitPaypal() {

        if (settingsManager.getSettings().getPaypalClientId() !=null &&

                !settingsManager.getSettings().getPaypalClientId().isEmpty() &&
                settingsManager.getSettings().getPaypalCurrency() !=null &&
                !settingsManager.getSettings().getPaypalCurrency().isEmpty()) {

            CheckoutConfig config = new CheckoutConfig(this,
                    settingsManager.getSettings().getPaypalClientId(),
                    Environment.LIVE,
                    String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
                    CurrencyCode.valueOf(settingsManager.getSettings().getPaypalCurrency()),
                    UserAction.PAY_NOW
            );
            PayPalCheckout.setConfig(config);
        }
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        AppInjector.init(this);
        return androidInjector;
    }

}

/*
 * Application has activities that is why we implement HasActivityInjector interface.
 * Activities have fragments so we have to implement HasFragmentInjector/HasSupportFragmentInjector
 * in our activities.
 * No child fragment and don’t inject anything in your fragments, no need to implement
 * HasSupportFragmentInjector.
 */
