package com.beeecorptv.ui.base;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.beeecorptv.util.Constants.EXTENTIONS;
import static com.beeecorptv.util.Constants.FIRST_INSTALL;
import static com.beeecorptv.util.Constants.SWITCH_PUSH_NOTIFICATION;
import static com.beeecorptv.util.Constants.UNITY_ADS_BANNER_HEIGHT;
import static com.beeecorptv.util.Constants.WIFI_CHECK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.appnext.banners.BannerAdRequest;
import com.appnext.banners.BannerSize;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.beeecorptv.BuildConfig;
import com.beeecorptv.R;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.ActivityMainBinding;
import com.beeecorptv.di.Injectable;
import com.beeecorptv.ui.downloadmanager.ui.main.DownloadManagerFragment;
import com.beeecorptv.ui.home.HomeFragment;
import com.beeecorptv.ui.library.LibraryFragment;
import com.beeecorptv.ui.login.LoginActivity;
import com.beeecorptv.ui.manager.AdsManager;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.StatusManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.mylist.ListFragment;
import com.beeecorptv.ui.player.cast.GoogleServicesHelper;
import com.beeecorptv.ui.player.cast.queue.ui.QueueListViewActivity;
import com.beeecorptv.ui.player.cast.settings.CastPreference;
import com.beeecorptv.ui.search.DiscoverFragment;
import com.beeecorptv.ui.settings.SettingsActivity;
import com.beeecorptv.ui.streaming.StreamingFragment;
import com.beeecorptv.ui.viewmodels.SettingsViewModel;
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.Tools;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.vungle.warren.AdConfig;
import com.vungle.warren.BannerAdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import io.github.g00fy2.versioncompare.Version;
import timber.log.Timber;

public class BaseActivity extends AppCompatActivity implements Injectable {


    @Inject
    @Named("sniffer")
    @Nullable
    ApplicationInfo provideSnifferCheck;


    @Inject
    @Named("root")
    @Nullable
    ApplicationInfo provideRootCheck;


    @Inject
    @Named("vpn")
    boolean checkVpn;





    @Inject
    MediaRepository repository;

    private AdView adView;

    private VungleBanner vungleBanner;

    private IronSourceBannerLayout banner;

    ActivityMainBinding mainBinding;

    @Inject
    TokenManager tokenManager;

    @Inject
    @Named("ready")
    boolean settingReady;

    @Inject
    AuthManager authManager;


    @Inject
    SettingsManager settingsManager;


    @Inject
    AdsManager adsManager;


    @Inject
    StatusManager statusManager;


    @Inject
    SharedPreferences sharedPreferences;


    @Inject
    SharedPreferences.Editor editor;

    @Inject ViewModelProvider.Factory viewModelFactory;

    SettingsViewModel settingsViewModel;

    FragmentManager manager;

    private CastContext mCastContext;
    private SessionManagerListener<CastSession> mSessionManagerListener =
            new MySessionManagerListener();
    private CastSession mCastSession;
    private MenuItem mediaRouteMenuItem;
    private MenuItem mQueueMenuItem;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;


    private class MySessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionEnded(@NotNull CastSession session, int error) {
            if (session == mCastSession) {
                mCastSession = null;
            }
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(@NotNull CastSession session, boolean wasSuspended) {
            mCastSession = session;
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(@NotNull CastSession session, @NotNull String sessionId) {
            mCastSession = session;
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarting(@NotNull CastSession session) {

            //
        }

        @Override
        public void onSessionStartFailed(@NotNull CastSession session, int error) {

            Toast.makeText(BaseActivity.this, getString(R.string.unable_cast), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onSessionEnding(@NotNull CastSession session) {

            //
        }

        @Override
        public void onSessionResuming(@NotNull CastSession session, @NotNull String sessionId) {

            //
        }

        @Override
        public void onSessionResumeFailed(@NotNull CastSession session, int error) {

            //
        }

        @Override
        public void onSessionSuspended(@NotNull CastSession session, int reason) {

            //
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        Appodeal.initialize(this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.BANNER);

        IronSource.init(this, settingsManager.getSettings().getIronsourceAppKey(),IronSource.AD_UNIT.BANNER);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Tools.id(this));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);

        mCastStateListener = newState -> {
            if (newState != CastState.NO_DEVICES_AVAILABLE) {
                showIntroductoryOverlay();
            }
        };


        if (GoogleServicesHelper.available(this)) {

            mCastContext = CastContext.getSharedInstance(this);
        }


        Tools.setSystemBarTransparent(this);

        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        manager = getSupportFragmentManager();



        if (settingsManager.getSettings().getEnableCustomMessage() == 1) {

            DialogHelper.showCustomAlert( this,settingsManager.getSettings().getCustomMessage());

        }
        onCheckFirstInstall();
        onLoadCheckVersion();
        wifiCheck();
        notificationManager();
        setExtentions();
        onCheckFlagSecure();

        if (authManager.getUserInfo().getPremuim() != 1 ) {
            onLoadBottomBanners();
        }

        mainBinding.navigation.getMenu().findItem(R.id.navigation_live).setVisible(settingsManager.getSettings().getStreaming() != 0);
        mainBinding.navigation.getMenu().findItem(R.id.navigation_download).setVisible(settingsManager.getSettings().getEnableDownload() != 0);


        if (settingsManager.getSettings().getMantenanceMode() == 0) {

            onNavigationUI();

        }else {

            mainBinding.navigation.setVisibility(View.GONE);

        }


        changeFragment(new HomeFragment(), HomeFragment.class.getSimpleName());

        onHideTaskBar();

        if(tokenManager.getToken() == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }


        if (settingsManager.getSettings().getEnableBannerBottom() != 1 || authManager.getUserInfo().getPremuim() == 1) {
            return;
        }



        if (settingsManager.getSettings().getVpn() ==1 && checkVpn){

          finishAffinity();

            Toast.makeText(this, R.string.vpn_message, Toast.LENGTH_SHORT).show();

        }


    }

    private void onLoadBottomBanners() {


        if (authManager.getUserInfo().getPremuim() != 1 ) {
            onLoadAppoDealBanner();
            onLoadAdmobBanner();
            onLoadUnityBanner();
            onLoadIronsrcBanner();
            onLoadAppNextBanner();
            onLoadVungleBanner();
            onLoadApplovinBanner();
            onLoadStartAppBanner();
        }
    }



    private void onLoadStartAppBanner() {


        if (settingsManager.getSettings().getStartappBanner() == 1) {

            mainBinding.startAppBanner.setVisibility(View.VISIBLE);

        }else {


            mainBinding.startAppBanner.setVisibility(GONE);
        }


    }

    private void onLoadApplovinBanner() {

        if (settingsManager.getSettings().getApplovinBanner() == 1) {

            MaxAdView maxAdView = new MaxAdView(settingsManager.getSettings().getApplovinBannerUnitid(),this);
            final boolean isTablet = AppLovinSdkUtils.isTablet( this );
            final int heightPx = AppLovinSdkUtils.dpToPx( this, isTablet ? 90 : 50 );
            maxAdView.setLayoutParams( new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, heightPx ) );
            mainBinding.maxAdView.addView(maxAdView);
            maxAdView.loadAd();

            maxAdView.setListener(new MaxAdViewAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {

                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                }

                @Override
                public void onAdHidden(MaxAd ad) {

                }

                @Override
                public void onAdClicked(MaxAd ad) {

                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {

                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                }

                @Override
                public void onAdExpanded(MaxAd ad) {

                }

                @Override
                public void onAdCollapsed(MaxAd ad) {

                }
            });
            maxAdView.setRevenueListener(ad -> {

            });
        }
    }

    private void onLoadVungleBanner() {

        if (settingsManager.getSettings().getVungleBanner() == 1) {

            final BannerAdConfig bannerAdConfig = new BannerAdConfig();
            bannerAdConfig.setAdSize(AdConfig.AdSize.BANNER);

            if (Banners.canPlayAd(settingsManager.getSettings().getVungleBannerPlacementName(), bannerAdConfig.getAdSize())) {
                vungleBanner = Banners.getBanner(settingsManager.getSettings().getVungleBannerPlacementName(), bannerAdConfig, new PlayAdCallback() {
                    @Override
                    public void creativeId(String creativeId) {
                        //

                    }

                    @Override
                    public void onAdStart(String placementId) {
                        //
                    }

                    @Override
                    public void onAdEnd(String placementId, boolean completed, boolean isCTAClicked) {
                        //
                    }

                    @Override
                    public void onAdEnd(String placementId) {
                        //
                    }

                    @Override
                    public void onAdClick(String placementId) {
                        //
                    }

                    @Override
                    public void onAdRewarded(String placementId) {
                        //
                    }

                    @Override
                    public void onAdLeftApplication(String placementId) {
                        //
                    }

                    @Override
                    public void onError(String placementId, VungleException exception) {

                        //
                    }

                    @Override
                    public void onAdViewed(String placementId) {
                        //
                    }
                });
                mainBinding.VungleBannerContainerIron.addView(vungleBanner);
            }
        }else {

            mainBinding.VungleBannerContainerIron.setVisibility(GONE);
        }
    }

    private void onLoadAppNextBanner() {

        if (settingsManager.getSettings().getAppnextBanner() == 1) {

            com.appnext.banners.BannerView appNextBanner = new  com.appnext.banners.BannerView(this);
            appNextBanner.setPlacementId(settingsManager.getSettings().getAppnextPlacementid());
            appNextBanner.setBannerSize(BannerSize.BANNER);
            mainBinding.bannerAppNext.loadAd(new BannerAdRequest());

        }else {

            mainBinding.bannerAppNext.setVisibility(GONE);
        }
    }

    private void onCheckFirstInstall() {

        if (!sharedPreferences.getBoolean(FIRST_INSTALL, false)) {
            editor = sharedPreferences.edit();
            editor.putBoolean(FIRST_INSTALL, Boolean.TRUE);
            editor.apply();
            settingsViewModel.getInstalls();
        }

    }

    private void onCheckFlagSecure() {

        if(settingsManager.getSettings().getFlagSecure() == 1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }


    private void setExtentions() {


        if (sharedPreferences.getBoolean(EXTENTIONS,true)){

            editor.putBoolean(EXTENTIONS, true).apply();

        }else {

            editor.putBoolean(EXTENTIONS, false).apply();
        }

    }



    private void onLoadIronsrcBanner() {

        if (settingsManager.getSettings().getIronsourceBanner() == 1) {

            banner = IronSource.createBanner(this, ISBannerSize.BANNER);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(MATCH_PARENT,
                    WRAP_CONTENT);
            mainBinding.bannerContainerIron.addView(banner, 0, layoutParams);


            banner.setBannerListener(new BannerListener() {
                @Override
                public void onBannerAdLoaded() {
                    banner.setVisibility(View.VISIBLE);
                }
                @Override
                public void onBannerAdLoadFailed(IronSourceError error) {

                    //

                }
                @Override
                public void onBannerAdClicked() {

                    //

                }
                @Override
                public void onBannerAdScreenPresented() {

                    //

                }
                @Override
                public void onBannerAdScreenDismissed() {

                    //

                }
                @Override
                public void onBannerAdLeftApplication() {

                    banner.removeBannerListener();

                }
            });

            IronSource.loadBanner(banner, settingsManager.getSettings().getIronsourceBannerPlacementName());
        }

    }

    private void onLoadUnityBanner() {

        if (settingsManager.getSettings().getUnityadsBanner() != 1) {
            return;
        }

        BannerView bottomBanner = new BannerView(BaseActivity.this, "banner", new UnityBannerSize(UnityBannerSize.getDynamicSize(this).getWidth(), UNITY_ADS_BANNER_HEIGHT));
        bottomBanner.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerView) {
                mainBinding.unityBannerViewContainer.setVisibility(View.VISIBLE);
                Timber.d("ready");
            }

            @Override
            public void onBannerClick(BannerView bannerView) {

                //
            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                Timber.d("Banner Error%s", bannerErrorInfo);
                mainBinding.unityBannerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onBannerLeftApplication(BannerView bannerView) {

                bannerView.destroy();
            }
        });

        mainBinding.unityBannerViewContainer.addView(bottomBanner);
        bottomBanner.load();

    }


    private void onLoadAdmobBanner() {


        if (authManager.getUserInfo().getPremuim() == 1 || settingsManager.getSettings().getAdBanner() != 1 || settingsManager.getSettings().getAdUnitIdBanner() == null) {
            return;
        }
        adView = new AdView(this);
        adView.setAdUnitId(settingsManager.getSettings().getAdUnitIdBanner());
        mainBinding.adViewContainer.removeAllViews();
        mainBinding.adViewContainer.addView(adView);
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        AdRequest adRequest = new AdRequest.Builder().build();
        // Start loading the ad in the background.
        adView.loadAd(adRequest);


    }


    // Determine the screen width (less decorations) to use for the ad width.
    private AdSize getAdSize() {


        // Determine the screen width (less decorations) to use for the ad width.
        android.view.Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = mainBinding.adViewContainer.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);


    }


    private void onLoadAppoDealBanner() {

        if (settingsManager.getSettings().getAppodealBanner() != 1) {
            return;
        }

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int i, boolean b) {

                mainBinding.appodealBannerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onBannerFailedToLoad() {

                //
            }

            @Override
            public void onBannerShown() {

                //
            }

            @Override
            public void onBannerShowFailed() {

                //
            }

            @Override
            public void onBannerClicked() {

                //
            }

            @Override
            public void onBannerExpired() {

                //

            }
        });
        Appodeal.show(this, Appodeal.BANNER_VIEW);


    }

    private void wifiCheck() {

        if (!sharedPreferences.getBoolean(WIFI_CHECK, true)) {

            editor.putBoolean(WIFI_CHECK, false).apply();

        }
    }


    public void changeFragment(Fragment fragment, String tagFragmentName) {

        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment currentFragment = mFragmentManager.getPrimaryNavigationFragment();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }

        Fragment fragmentTemp = mFragmentManager.findFragmentByTag(tagFragmentName);
        if (fragmentTemp == null) {
            fragmentTemp = fragment;
            fragmentTransaction.add(R.id.fragment_container, fragmentTemp, tagFragmentName);
        } else {
            fragmentTransaction.show(fragmentTemp);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commitNowAllowingStateLoss();
    }



    @SuppressLint("NonConstantResourceId")
    private void onNavigationUI() {
        mainBinding.navigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    changeFragment(new DiscoverFragment(), DiscoverFragment.class
                            .getSimpleName());
                    break;
                case R.id.navigation_browse:
                    changeFragment(new LibraryFragment(), LibraryFragment.class.getSimpleName());
                    break;
                case R.id.navigation_download:
                    changeFragment(new DownloadManagerFragment(), DownloadManagerFragment.class
                            .getSimpleName());
                    break;

                case R.id.navigation_live:
                    changeFragment(new StreamingFragment(), StreamingFragment.class
                            .getSimpleName());
                    break;

                default:
                    changeFragment(new HomeFragment(), HomeFragment.class
                            .getSimpleName());
            }
            return true;
        });
    }

    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
            new Handler().post(() -> {
                mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                        BaseActivity.this, mediaRouteMenuItem)
                        .setTitleText(getString(R.string.introducing_cast))
                        .setOverlayColor(R.color.main_color)
                        .setSingleTime()
                        .setOnOverlayDismissedListener(
                                () -> mIntroductoryOverlay = null)
                        .build();
                mIntroductoryOverlay.show();
            });
        }
    }

    @Override
    protected void onResume() {

        if (GoogleServicesHelper.available(this)) {
        mCastContext.addCastStateListener(mCastStateListener);
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
        }
        }
        if (mQueueMenuItem != null) {
            mQueueMenuItem.setVisible(
                    (mCastSession != null) && mCastSession.isConnected());
        }

        if (provideSnifferCheck != null) {
            Toast.makeText(BaseActivity.this, R.string.sniffer_message, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }

        if (provideRootCheck != null) {
            Toast.makeText(BaseActivity.this, R.string.rooted_message, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }


        if (settingsManager.getSettings().getVpn() ==1 && checkVpn) {


            finishAffinity();

            Toast.makeText(this, R.string.vpn_message, Toast.LENGTH_SHORT).show();

        }

        super.onResume();

        String downloadOnProgress = getIntent().getStringExtra("download_on_progress");

        if (downloadOnProgress != null && !downloadOnProgress.isEmpty() &&  downloadOnProgress.equals("yes")) {

            changeFragment(new DownloadManagerFragment(), DownloadManagerFragment.class
                    .getSimpleName());

        }

    }

    @Override
    protected void onPause() {

        if (GoogleServicesHelper.available(this)) {
            mCastContext.removeCastStateListener(mCastStateListener);
            mCastContext.getSessionManager().removeSessionManagerListener(
                    mSessionManagerListener, CastSession.class);
        }
        if (adView !=null){

            adView.pause();
        }
        super.onPause();
    }



    private void onLoadCheckVersion() {

        settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);
        settingsViewModel.getSettingsDetails();
        settingsViewModel.settingsMutableLiveData.observe(this, settings -> {

        boolean result;

            result = new Version(BuildConfig.VERSION_NAME).isLowerThan(settings.getLatestVersion());

            if (result) {

                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_update_alert);
                dialog.setCancelable(settingsManager.getSettings().getForceUpdate() == 0);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.gravity = Gravity.BOTTOM;
                lp.width = MATCH_PARENT;
                lp.height = MATCH_PARENT;

                LinearLayout linearprogressactive = dialog.findViewById(R.id.linearprogressactive);
                LinearLayout downloadProgress = dialog.findViewById(R.id.downloadProgress);
                ProgressBar progressBar = dialog.findViewById(R.id.download_progress_bar_downloading);
                TextView textView = dialog.findViewById(R.id.downloadProgres);
                LinearLayout updateLinear = dialog.findViewById(R.id.updateLinear);

                textView.setVisibility(GONE);

                dialog.findViewById(R.id.getUpdateLink).setOnClickListener(v -> {


                    if (settingsManager.getSettings().getForce_inappupdate() == 1) {


                    String finalFileName = settingsManager.getSettings().getAppName()+settings.getLatestVersion().replaceAll("\\s+", "") + ".apk";

                    DownloadImpl.with(this)
                            .target(new File(sharedPreferences.getString(Constants.DEFAULT_DOWNLOAD_DIRECTORY,
                                    String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))),
                                    finalFileName))
                            .setUniquePath(true)
                            .setEnableIndicator(false)
                            .setDownloadingListener((url, downloaded, length, usedTime) -> {
                            })
                            .url(settings.getUrl())
                            .enqueue(new DownloadListenerAdapter() {
                                @Override
                                public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                                    super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);

                                    textView.setVisibility(View.VISIBLE);
                                    updateLinear.setVisibility(GONE);
                                    linearprogressactive.setVisibility(View.VISIBLE);
                                    downloadProgress.setVisibility(View.VISIBLE);


                                }




                                @Override
                                public void onProgress(String url, long downloaded, long length, long usedTime) {
                                    super.onProgress(url, downloaded, length, usedTime);
                                    textView.setText("Downloaded:" + Tools.byte2FitMemorySize(downloaded) + " Remaining :" + (usedTime / 1000) + "s");

                                    int mProgress = (int) ((downloaded) / (float) length * 100);

                                    progressBar.setProgress(mProgress);

                                    Timber.i(" progress:" + downloaded + " url:" + url);
                                }

                                @Override
                                public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {

                                    textView.setText("Completed");
                                    progressBar.setProgress(100);

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.setDataAndType(FileProvider.getUriForFile(BaseActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(path.getPath()))
                                            , "application/vnd.android.package-archive");
                                    startActivity(intent);

                                    return super.onResult(throwable, path, url, extra);


                                }
                            });


                    }else {

                        if (settings.getUrl() !=null &&  !settings.getUrl().isEmpty()) {

                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settings.getUrl())));


                        } else {

                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://codecanyon.net/user/yobex")));

                        }

                    }


                });

                ImageButton closeButton = dialog.findViewById(R.id.bt_close);

                if (settingsManager.getSettings().getForceUpdate() == 1)
                    closeButton.setVisibility(GONE);

                dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());
                TextView updateTitle = dialog.findViewById(R.id.update_title);
                updateTitle.setText(settings.getUpdateTitle());
                TextView customAlert = dialog.findViewById(R.id.custom_alert_text);
                customAlert.setText(settings.getReleaseNotes());
                dialog.show();
                dialog.getWindow().setAttributes(lp);

            }


        });



    }


    // Top level Firebase Cloud Messaging singleton that provides methods for subscribing or unsubscribing

    private void notificationManager() {

        if (sharedPreferences.getBoolean(SWITCH_PUSH_NOTIFICATION,true)){

            FirebaseMessaging.getInstance().subscribeToTopic("/topics/all");

        }else {

            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/all");
        }
    }


    // Hide TaskBar
    private void onHideTaskBar() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Tools.setSystemBarTransparent(this);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        getMenuInflater().inflate(R.menu.menu, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
                R.id.media_route_menu_item);
        mQueueMenuItem = menu.findItem(R.id.action_show_queue);
        showIntroductoryOverlay();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_show_queue).setVisible(
                (mCastSession != null) && mCastSession.isConnected());
        menu.findItem(R.id.action_settings).setVisible(
                (mCastSession != null) && mCastSession.isConnected());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;


        if (item.getItemId() == R.id.mylist) {

       changeFragment(new ListFragment(), ListFragment.class.getSimpleName());

        }else if (item.getItemId() == R.id.settings) {

        startActivity(new Intent(this, SettingsActivity.class));


        }else
        if (item.getItemId() == R.id.action_settings) {
            intent = new Intent(BaseActivity.this, CastPreference.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_show_queue) {
            intent = new Intent(BaseActivity.this, QueueListViewActivity.class);
            startActivity(intent);
        }
        return true;
    }




    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {

         return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event) || super.dispatchKeyEvent(event);

    }

    @Override
    public void onBackPressed(){

       if (mainBinding.navigation.getSelectedItemId() == R.id.navigation_home)
        {
            doExitApp();
        }
        else
        {
            mainBinding.navigation.setSelectedItemId(R.id.navigation_home);
        }

    }


    public void doExitApp() {
        Tools.doExitApp(this);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        settingsManager.deleteSettings();
        mCastContext = null;
        mediaRouteMenuItem = null;
        mQueueMenuItem = null;
        mSessionManagerListener = null;
        if (adView !=null) {
        adView.destroy();

        }

        if (vungleBanner !=null) {
            vungleBanner.destroyAd();
        }
    }
}
