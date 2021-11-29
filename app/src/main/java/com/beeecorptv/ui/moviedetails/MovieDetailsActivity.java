package com.beeecorptv.ui.moviedetails;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.beeecorptv.util.Constants.ARG_MOVIE;
import static com.beeecorptv.util.Constants.DEFAULT_WEBVIEW_ADS_RUNNING;
import static com.beeecorptv.util.Constants.PLAYER_HEADER;
import static com.beeecorptv.util.Constants.PLAYER_USER_AGENT;
import static com.beeecorptv.util.Constants.SERVER_BASE_URL;
import static com.beeecorptv.util.Constants.UNITY_ADS_BANNER_HEIGHT;
import static com.beeecorptv.util.Constants.UNITY_ADS_BANNER_WIDTH;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.appnext.ads.fullscreen.RewardedVideo;
import com.appnext.banners.BannerAdRequest;
import com.appnext.banners.BannerSize;
import com.appnext.base.Appnext;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.BuildConfig;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Download;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.media.StatusFav;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.data.repository.AuthRepository;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.data.repository.SettingsRepository;
import com.beeecorptv.databinding.ItemMovieDetailBinding;
import com.beeecorptv.ui.base.BaseActivity;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.moviedetails.adapters.CastAdapter;
import com.beeecorptv.ui.moviedetails.adapters.DownloadsListAdapter;
import com.beeecorptv.ui.moviedetails.adapters.RelatedsAdapter;
import com.beeecorptv.ui.player.activities.EmbedActivity;
import com.beeecorptv.ui.player.cast.GoogleServicesHelper;
import com.beeecorptv.ui.player.cast.queue.ui.QueueListViewActivity;
import com.beeecorptv.ui.player.cast.settings.CastPreference;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayerApi;
import com.beeecorptv.ui.settings.SettingsActivity;
import com.beeecorptv.ui.viewmodels.LoginViewModel;
import com.beeecorptv.ui.viewmodels.MovieDetailViewModel;
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.NetworkUtils;
import com.beeecorptv.util.SpacingItemDecoration;
import com.beeecorptv.util.Tools;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.ads.UnityAdsImplementation;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.vungle.warren.AdConfig;
import com.vungle.warren.BannerAdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @package  EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2021 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/



public class MovieDetailsActivity extends AppCompatActivity {

    private boolean shareLinkLaunched = false;
    private StartAppAd startAppAd;
    private MaxInterstitialAd maxInterstitialAd;
    private VungleBanner vungleBanner;
    private boolean internal = false;
    private  IronSourceBannerLayout banner;
    private boolean isMovieFav = false;
    private LoginViewModel loginViewModel;
    private CountDownTimer mCountDownTimer;
    private boolean webViewLauched = false;
    private NativeAd mNativeAd;
    private @Nullable
    AdOptionsView adOptionsView;
    private @Nullable com.facebook.ads.NativeAd nativeAd;
    ItemMovieDetailBinding binding;
    private String externalId;

    @Inject ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    SettingsManager settingsManager;

    @Inject
    SettingsRepository settingsRepository;

    @Inject
    AuthRepository authRepository;

    @Inject
    MediaRepository mediaRepository;


    @Inject
    @Named("ready")
    boolean settingReady;


    @Inject
    @Named("cuepoint")
    String cuePoint;


    @Inject
    @Named("cuepointUrl")
    String cuepointUrl;

    @Inject
    AuthManager authManager;


    @Inject
    @Named("cuepointY")
    String cuePointY;

    @Inject
    @Named("cuepointN")
    String cuePointN;


    @Inject
    @Named("cuepointW")
    String cuePointW;


    @Inject
    @Named("cuepointZ")
    String cuePointZ;

    @Inject
    @Named("vpn")
    boolean checkVpn;

    @Inject
    @Named("sniffer")
    @Nullable
    ApplicationInfo provideSnifferCheck;


    @Inject
    @Named("root")
    @Nullable
    ApplicationInfo provideRootCheck;

    @Inject
    TokenManager tokenManager;

    private CastAdapter mCastAdapter;
    private RelatedsAdapter mRelatedsAdapter;
    private boolean mMovie;
    boolean isLoading;
    private RewardedAd rewardedAd;
    private Media media;
    private String mediaGenre;
    private Download download;
    private History history;
    private CastContext mCastContext;
    private final SessionManagerListener<CastSession> mSessionManagerListener = new MySessionManagerListener();
    private CastSession mCastSession;
    private MenuItem mediaRouteMenuItem;
    private MenuItem mQueueMenuItem;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;
    private MaxRewardedAd maxRewardedAd;


    private class MySessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionEnded(@NotNull CastSession session, int error) {


            Toast.makeText(MovieDetailsActivity.this, ""+error, Toast.LENGTH_SHORT).show();


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

            Toast.makeText(MovieDetailsActivity.this, getString(R.string.unable_cast), Toast.LENGTH_SHORT).show();
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

        onCheckFlagSecure();

        binding = DataBindingUtil.setContentView(this, R.layout.item_movie_detail);


        if (settingsManager.getSettings().getVpn() == 1 && checkVpn) {

            binding.backbutton.performClick();

            Toast.makeText(MovieDetailsActivity.this, R.string.vpn_message, Toast.LENGTH_SHORT).show();

        }

        if (authManager.getUserInfo().getPremuim() != 1) {

            onInitRewards();
        }


        if (GoogleServicesHelper.available(this)) {

            mCastStateListener = newState -> {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    showIntroductoryOverlay();
                }
            };
            mCastContext = CastContext.getSharedInstance(this);


        }

        Intent intent = getIntent();

        media = intent.getParcelableExtra(ARG_MOVIE);

        // ViewModel to cache, retrieve data for MovieDetailsActivity
        movieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel.class);

        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);


        mMovie = false;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.itemDetailContainer.setVisibility(GONE);
        binding.PlayButtonIcon.setVisibility(GONE);
        binding.serieName.setVisibility(GONE);


        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();


        if (appLinkData !=null) {

            movieDetailViewModel.getMovieDetails(appLinkData.getLastPathSegment());

            shareLinkLaunched = true;

        }else if ((media.getId() !=null)) {

            movieDetailViewModel.getMovieDetails(media.getId());
        }


        initMovieDetails();


        if (settingsManager.getSettings().getAdUnitIdRewarded() != null) {

            loadRewardedAd();

        }

        Tools.setSystemBarTransparent(this);


    }


    private void onInitRewards() {

        // Initialize the AppNext Ads SDK.
        Appnext.init(this);

        Appodeal.initialize(MovieDetailsActivity.this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.INTERSTITIAL | Appodeal.BANNER | Appodeal.REWARDED_VIDEO );

        IronSource.init(this, settingsManager.getSettings().getIronsourceAppKey(), IronSource.AD_UNIT.OFFERWALL,IronSource.AD_UNIT.REWARDED_VIDEO,IronSource.AD_UNIT.INTERSTITIAL,IronSource.AD_UNIT.BANNER);

        if (settingsManager.getSettings().getApplovinInterstitialUnitid() !=null && !settingsManager.getSettings().getApplovinInterstitialUnitid().isEmpty()) {

            maxInterstitialAd = new MaxInterstitialAd(settingsManager.getSettings().getApplovinInterstitialUnitid(), this );
            maxInterstitialAd.loadAd();
        }

        String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();

        if (getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

            maxRewardedAd = MaxRewardedAd.getInstance( settingsManager.getSettings().getApplovinRewardUnitid(), this);
            maxRewardedAd.loadAd();

        }

    }

    private void onCheckFlagSecure() {

        if(settingsManager.getSettings().getFlagSecure() == 1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    private void onLoadIronsrcInterStetial() {

        Tools.onLoadIronSourceInterstetial(settingsManager.getSettings().getIronsourceInterstitial()
                ,settingsManager.getSettings().getIronsourceInterstitialShow(),settingsManager);
    }

    private void onLoadIronsrcBanner() {

        if (settingsManager.getSettings().getIronsourceBanner() == 1) {

             banner = IronSource.createBanner(this, ISBannerSize.BANNER);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        binding.bannerContainerIron.addView(banner, 0, layoutParams);


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

    private void onLoadAdmobNativeAds() {

       if (settingsManager.getSettings().getAdUnitIdNativeEnable() == 1) {


               AdLoader.Builder builder = new AdLoader.Builder(this, settingsManager.getSettings().getAdUnitIdNative());

           // OnLoadedListener implementation.
           builder.forNativeAd(
                   nativeAd -> {
                       // If this callback occurs after the activity is destroyed, you must call
                       // destroy and return or you may get a memory leak.
                       boolean isDestroyed;
                       isDestroyed = isDestroyed();
                       if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                           nativeAd.destroy();
                           return;
                       }
                       // You must call destroy on old ads when you are done with them,
                       // otherwise you will have a memory leak.
                       if (mNativeAd != null) {
                           mNativeAd.destroy();
                       }
                       mNativeAd= nativeAd;

                       @SuppressLint("InflateParams") NativeAdView adView =
                               (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
                       populateNativeAdView(nativeAd, adView);
                       binding.flAdplaceholder.removeAllViews();
                       binding.flAdplaceholder.addView(adView);
                   });

               VideoOptions videoOptions =
                       new VideoOptions.Builder().build();

               NativeAdOptions nativeAdOptions =
                       new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

               builder.withNativeAdOptions(nativeAdOptions);

               AdLoader adLoader =
                       builder
                               .withAdListener(
                                       new com.google.android.gms.ads.AdListener() {
                                           @Override
                                           public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {

                                               binding.flAdplaceholder.setVisibility(GONE);
                                           }
                                       })
                               .build();

               adLoader.loadAd(new AdRequest.Builder().build());



       }


    }




    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView()))
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }


    private void onLoadAdmobInter() {

   Tools.onLoadAdmobInterstitialAds(this,settingsManager.getSettings().getAdInterstitial(),settingsManager.getSettings().getAdShowInterstitial(),
                    settingsManager.getSettings().getAdUnitIdInterstitial());


    }


    private void onLoadUnityBanner() {

        if (settingsManager.getSettings().getUnityadsBanner() == 1) {

            BannerView bottomBanner = new BannerView(MovieDetailsActivity.this,
                    settingsManager.getSettings().getUnityBannerPlacementId()
                    , new UnityBannerSize(UNITY_ADS_BANNER_WIDTH, UNITY_ADS_BANNER_HEIGHT));
            bottomBanner.setListener(new BannerView.IListener() {
                @Override
                public void onBannerLoaded(BannerView bannerView) {
                    binding.unityBannerViewContainer.setVisibility(View.VISIBLE);
                    Timber.d("ready");
                }

                @Override
                public void onBannerClick(BannerView bannerView) {

                    //
                }

                @Override
                public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                    Timber.d("Banner Error%s", bannerErrorInfo);
                    binding.unityBannerViewContainer.setVisibility(View.GONE);
                }

                @Override
                public void onBannerLeftApplication(BannerView bannerView) {

                    bannerView.destroy();
                }
            });

            binding.unityBannerViewContainer.addView(bottomBanner);
            bottomBanner.load();
        }

    }







    private void onLoadFacebookNativeAds() {


        if (settingsManager.getSettings().getAdFaceAudienceNative()

                == 1 && settingsManager.getSettings().getAdUnitIdFacebookNativeAudience() !=null) {


             // Create a native ad request with a unique placement ID (generate your own on the
        // Facebook app settings). Use different ID for each ad placement in your app.
        nativeAd = new com.facebook.ads.NativeAd(this, settingsManager.getSettings().getAdUnitIdFacebookNativeAudience());

        // When testing on a device, add its hashed ID to force test ads.
        // The hash ID is printed to log cat when running on a device and loading an ad.
        // Initiate a request to load an ad.
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig().withAdListener(new NativeAdListener() {
                    @Override
                    public void onError(com.facebook.ads.Ad ad, AdError adError) {

                        //
                    }

                    @Override
                    public void onAdLoaded(com.facebook.ads.Ad ad) {

                        if (nativeAd == null || nativeAd != ad) {
                            // Race condition, load() called again before last ad was displayed
                            return;
                        }

                        // Unregister last ad
                        nativeAd.unregisterView();

                        adOptionsView = new AdOptionsView(MovieDetailsActivity.this, nativeAd, binding.nativeAdLayout);
                        binding.adChoicesContainer.removeAllViews();
                        binding.adChoicesContainer.addView(adOptionsView, 0);

                        inflateAd(nativeAd, binding.nativeAdLayout);

                    }

                    @Override
                    public void onAdClicked(com.facebook.ads.Ad ad) {

                        //
                    }

                    @Override
                    public void onLoggingImpression(com.facebook.ads.Ad ad) {

                        //
                    }

                    @Override
                    public void onMediaDownloaded(com.facebook.ads.Ad ad) {

                        //

                    }
                }).build());

        }else {

            binding.nativeAdLayout.setVisibility(GONE);
            binding.nativeAdLayout2.setVisibility(GONE);
        }


    }


    private void inflateAd(com.facebook.ads.NativeAd nativeAd, View adView) {
        Timber.d("Aspect ratio of ad: %s", nativeAd.getAspectRatio());

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        nativeAdMedia.setListener(getMediaViewListener());

        // Setting the Text
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        sponsoredLabel.setText(R.string.sponsored);

        // You can use the following to specify the clickable areas.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdIcon);
        clickableViews.add(nativeAdMedia);
        clickableViews.add(nativeAdCallToAction);
        nativeAd.registerViewForInteraction(
                binding.nativeAdLayout, nativeAdMedia, nativeAdIcon, clickableViews);

        // Optional: tag views
        NativeAdBase.NativeComponentTag.tagView(nativeAdIcon, NativeAdBase.NativeComponentTag.AD_ICON);
        NativeAdBase.NativeComponentTag.tagView(nativeAdTitle, NativeAdBase.NativeComponentTag.AD_TITLE);
        NativeAdBase.NativeComponentTag.tagView(nativeAdBody, NativeAdBase.NativeComponentTag.AD_BODY);
        NativeAdBase.NativeComponentTag.tagView(nativeAdSocialContext, NativeAdBase.NativeComponentTag.AD_SOCIAL_CONTEXT);
        NativeAdBase.NativeComponentTag.tagView(nativeAdCallToAction, NativeAdBase.NativeComponentTag.AD_CALL_TO_ACTION);
    }


    private static MediaViewListener getMediaViewListener() {
        return new MediaViewListener() {
            @Override
            public void onVolumeChange(MediaView mediaView, float volume) {
                Timber.i("MediaViewEvent: Volume %s", volume);
            }

            @Override
            public void onPause(MediaView mediaView) {
                Timber.i("MediaViewEvent: Paused");
            }

            @Override
            public void onPlay(MediaView mediaView) {
                Timber.i("MediaViewEvent: Play");
            }

            @Override
            public void onFullscreenBackground(MediaView mediaView) {
                Timber.i("MediaViewEvent: FullscreenBackground");
            }

            @Override
            public void onFullscreenForeground(MediaView mediaView) {
                Timber.i("MediaViewEvent: FullscreenForeground");
            }

            @Override
            public void onExitFullscreen(MediaView mediaView) {
                Timber.i("MediaViewEvent: ExitFullscreen");
            }

            @Override
            public void onEnterFullscreen(MediaView mediaView) {
                Timber.i("MediaViewEvent: EnterFullscreen");
            }

            @Override
            public void onComplete(MediaView mediaView) {
                Timber.i("MediaViewEvent: Completed");
            }
        };
    }




    private void onLoadUnityInterstetial() {


        Tools.onLoadUnityInterstetial(this,settingsManager.getSettings().getUnityadsInterstitial()
                ,settingsManager.getSettings().getUnityShow(),UnityAdsImplementation.isReady(),settingsManager);

    }


    private void onLoadStartAppBannerInter() {


        if (settingsManager.getSettings().getStartappBanner() == 1) {

           binding.startAppBanner.setVisibility(View.VISIBLE);

        }else {


            binding.startAppBanner.setVisibility(GONE);
        }


        if (settingsManager.getSettings().getStartappInterstitial() == 1) {

            StartAppAd.showAd(this);

        }

    }

    private void initMovieDetails() {


        movieDetailViewModel.movieDetailMutableLiveData.observe(this, movieDetail -> {

            download = new Download(movieDetail.getId(),movieDetail.getTmdbId(),movieDetail.getBackdropPath(),movieDetail.getTitle(),"");

            if (movieDetail.getTmdbId() !=null) {

                this.externalId = movieDetail.getImdbExternalId();
            }
            for (Genre genre : movieDetail.getGenres()) {
                mediaGenre = genre.getName();
            }

            onLoadImage(movieDetail.getPosterPath());onLoadTitle(movieDetail.getTitle());
            try {
                onLoadDate(movieDetail.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            onLoadSynopsis(movieDetail.getOverview());onLoadGenres(movieDetail.getGenres());if(!settingReady)finishAffinity();
            onLoadBackButton();
            onLoadRelatedsMovies(Integer.parseInt(movieDetail.getId()));
            onLoadCast(movieDetail);
            onLoadRating(movieDetail.getVoteAverage());
            onLoadViews(movieDetail.getViews());
            onLoadPogressResume(movieDetail.getId());


            if (authManager.getUserInfo().getPremuim() != 1 ) {


                onLoadAdmobInter();
                onLoadBanner();
                onLoadFacebookBanner();
                onLoadAppoDealBanner();
                onLoadAppoDealInterStetial();
                onLoadStartAppBannerInter();
                onLoadUnityInterstetial();
                onLoadUnityBanner();
                onLoadAdmobNativeAds();
                onLoadFacebookNativeAds();
                onLoadIronsrcBanner();
                onLoadIronsrcInterStetial();
                onLoadAppNextBanner();
                onLoadAppNextInterStetial();
                onLoadVungleBanner();
                onLoadVungleInterStetial();
                onLoadApplovinInterStetial();
                onLoadApplovinBanner();

            } else {

                binding.bannerContainer.setVisibility(GONE);
                binding.adViewContainer.setVisibility(GONE);
                binding.appodealBannerView.setVisibility(GONE);
                binding.nativeAdLayout.setVisibility(GONE);
                binding.nativeAdLayout2.setVisibility(GONE);
            }

            
            if (movieDetail.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }


            binding.itemDetailContainer.getViewTreeObserver().addOnScrollChangedListener(() -> {
                int scrollY =  binding.itemDetailContainer.getScrollY();
                int color = Color.parseColor("#E6070707"); // ideally a global variable
                if (scrollY < 256) {
                    int alpha = (scrollY << 24) | (-1 >>> 8) ;
                    color &= (alpha);

                    binding.serieName.setText("");
                    binding.serieName.setVisibility(View.GONE);


                }else {

                    binding.serieName.setText(movieDetail.getTitle());
                    binding.serieName.setVisibility(View.VISIBLE);

                }
                binding.toolbar.setBackgroundColor(color);

            });



            onLoadToolbar();


            if (settingsManager.getSettings().getEnableDownload() == 0) {

                binding.downloadMovie.setImageResource(R.drawable.ic_notavailable);

            }

            binding.downloadMovie.setOnClickListener((View v) -> {

                if (settingsManager.getSettings().getEnableDownload() == 0) {

                DialogHelper.showNoDownloadAvailable(this,getString(R.string.download_disabled));

                }else {

                    onDownloadMovie(movieDetail);
                }

            });

            binding.report.setOnClickListener(v -> onLoadReport(movieDetail.getTitle(),movieDetail.getPosterPath()));

            binding.ButtonPlayTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLoadTrailer(movieDetail.getPreviewPath(), movieDetail.getTitle(), movieDetail.getBackdropPath(), movieDetail.getTrailerUrl());
                }
            });

            binding.favoriteIcon.setOnClickListener(view -> {

                if (settingsManager.getSettings().getFavoriteonline() == 1 && tokenManager.getToken().getAccessToken() !=null) {

                    if (isMovieFav) {

                        authRepository.getDeleteMovieOnline(movieDetail.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull StatusFav statusFav) {

                                        Toast.makeText(MovieDetailsActivity.this, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onError(@NotNull Throwable e) {

                                        //
                                    }

                                    @Override
                                    public void onComplete() {

                                        //

                                    }
                                });


                        isMovieFav = false;

                        binding.favoriteIcon.setImageResource(R.drawable.add_from_queue);


                    }else {

                        authRepository.getAddMovieOnline(movieDetail.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull StatusFav statusFav) {


                                        Toast.makeText(MovieDetailsActivity.this, "Added " + movieDetail.getTitle() + " To Watchlist", Toast.LENGTH_SHORT).show();


                                    }

                                    @Override
                                    public void onError(@NotNull Throwable e) {

                                        //


                                    }

                                    @Override
                                    public void onComplete() {

                                        //

                                    }
                                });

                        isMovieFav = true;

                        binding.favoriteIcon.setImageResource(R.drawable.ic_in_favorite);
                    }


                }else  {

                    onFavoriteClick(movieDetail);
                }


            });


            binding.shareIcon.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                Bundle extras = new Bundle();
                extras.putString(Intent.EXTRA_TEXT, "Seen " +  " '" + movieDetail.getTitle()+"' "
                        + "on " + settingsManager.getSettings().getAppName() +"  yet ?"
                        + Uri.parse("https://" + BuildConfig.APPLICATION_ID +".app/movies/"+movieDetail.getId()) + " - " + " Install the application via "+Uri.parse(settingsManager.getSettings().getAppUrlAndroid()));
                sendIntent.putExtras(extras);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            });


            checkMediaFavorite(movieDetail);


            if (mediaRepository.hasHistory(Integer.parseInt(movieDetail.getId()))) {

                binding.resumePlay.setVisibility(View.VISIBLE);


                binding.resumePlay.setOnClickListener(v -> binding.PlayButtonIcon.performClick());


            } else {

                binding.resumePlay.setVisibility(GONE);


            }


            binding.PlayButtonIcon.setOnClickListener(v -> {

                if (settingsManager.getSettings().getVpn() ==1 && checkVpn) {

                    binding.backbutton.performClick();

                    Toast.makeText(this, getString(R.string.vpn_message), Toast.LENGTH_SHORT).show();

                }else if (movieDetail.getVideos() !=null && !movieDetail.getVideos().isEmpty()) {


                    if (movieDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                        onLoadStream(movieDetail);

                    } else  if (settingsManager.getSettings().getEnableWebview() == 1) {


                        final Dialog dialog = new Dialog(this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.episode_webview);
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());

                        lp.gravity = Gravity.BOTTOM;
                        lp.width = MATCH_PARENT;
                        lp.height = MATCH_PARENT;


                        mCountDownTimer = new CountDownTimer(DEFAULT_WEBVIEW_ADS_RUNNING, 1000) {
                            @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
                            @Override
                            public void onTick(long millisUntilFinished) {

                                if (!webViewLauched) {

                                    WebView webView = dialog.findViewById(R.id.webViewVideoBeforeAds);
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    webView.setWebViewClient(new WebViewClient());
                                    WebSettings webSettings = webView.getSettings();
                                    webSettings.setSupportMultipleWindows(false);
                                    webSettings.setJavaScriptCanOpenWindowsAutomatically(false);

                                    if (settingsManager.getSettings().getWebviewLink() !=null && !settingsManager.getSettings().getWebviewLink().isEmpty()) {

                                        webView.loadUrl(settingsManager.getSettings().getWebviewLink());

                                    }else {

                                        webView.loadUrl(SERVER_BASE_URL+"webview");
                                    }

                                    webViewLauched = true;
                                }

                            }

                            @Override
                            public void onFinish() {

                                dialog.dismiss();
                                onLoadStream(movieDetail);
                                webViewLauched = false;
                                if (mCountDownTimer != null) {
                                    mCountDownTimer.cancel();
                                    mCountDownTimer = null;
                                }
                            }

                        }.start();

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);


                    } else   if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {

                        onLoadSubscribeDialog(movieDetail,true);

                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getPremuim() == 0) {


                        onLoadStream(movieDetail);


                    } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getPremuim() == 0) {


                        onLoadStream(movieDetail);

                    } else {

                        DialogHelper.showPremuimWarning(this);

                    }

                }else {

                    DialogHelper.showNoStreamAvailable(this);

                }


            });


            mMovie = true;
            checkAllDataLoaded();


        });

    }

    private void onLoadApplovinBanner() {


        if (settingsManager.getSettings().getApplovinBanner() == 1) {

            MaxAdView maxAdView = new MaxAdView(settingsManager.getSettings().getApplovinBannerUnitid(),this);
            binding.maxAdView.addView(maxAdView);
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


    private void onLoadApplovinInterStetial() {

        if (settingsManager.getSettings().getApplovinInterstitialUnitid() !=null && !settingsManager.getSettings().getApplovinInterstitialUnitid().isEmpty()) {

            Tools.onLoadAppLovinInterstetial(settingsManager.getSettings().getApplovinInterstitial()
                    ,settingsManager.getSettings().getApplovinInterstitialShow(),maxInterstitialAd.isReady(),maxInterstitialAd);
        }

    }


    private void onLoadVungleInterStetial() {


        Tools.onLoadVungleInterstetial(settingsManager.getSettings().getVungleInterstitial()
                ,settingsManager.getSettings().getVungle_interstitial_show(),settingsManager);

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
            binding.VungleBannerContainerIron.addView(vungleBanner);
        }
        }
    }

    private void onLoadAppNextInterStetial() {

        Tools.onLoadAppNextInterstetial(settingsManager.getSettings().getAppnextInterstitial()
                ,settingsManager.getSettings().getAppnextInterstitialShow(),settingsManager,this);
    }

    private void onLoadAppNextBanner() {

        if (settingsManager.getSettings().getAppnextBanner() == 1) {

        com.appnext.banners.BannerView appNextBanner = new  com.appnext.banners.BannerView(this);
        appNextBanner.setPlacementId(settingsManager.getSettings().getAppnextPlacementid());
        appNextBanner.setBannerSize(BannerSize.BANNER);
        binding.bannerAppNext.loadAd(new BannerAdRequest());

        }
    }

    private void checkMediaFavorite(Media movieDetail) {

        if (settingsManager.getSettings().getFavoriteonline() == 1 && tokenManager.getToken().getAccessToken() !=null) {

            loginViewModel.isMovieFavoriteOnline(movieDetail.getId());
            loginViewModel.isMovieFavoriteOnlineMutableLiveData.observe(this, favAddOnline -> {

            if (favAddOnline.getStatus() == 1) {

             isMovieFav = true;

            binding.favoriteIcon.setImageResource(R.drawable.ic_in_favorite);

             } else {

             isMovieFav = false;

             binding.favoriteIcon.setImageResource(R.drawable.add_from_queue);

            }});

        }else {

           if (mediaRepository.isMovieFavorite(Integer.parseInt(movieDetail.getId()))) {


                binding.favoriteIcon.setImageResource(R.drawable.ic_in_favorite);

            } else {

                binding.favoriteIcon.setImageResource(R.drawable.add_from_queue);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void onLoadViews(String views) {

        binding.viewMovieViews.setText(getString(R.string.views)+Tools.getViewFormat(Integer.parseInt(views)));

    }

    private void onLoadAppoDealBanner() {

        if (settingsManager.getSettings().getAppodealBanner() == 1) {
            Appodeal.setBannerViewId(R.id.appodealBannerView);
            Appodeal.show(this, Appodeal.BANNER_VIEW);
        }
    }



    private void onLoadAppoDealInterStetial() {
        Tools.onLoadAppoDealInterStetial(this,settingsManager.getSettings().getAppodealInterstitial() );

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


        if (settingsManager.getSettings().getVpn() ==1 && checkVpn){

            binding.backbutton.performClick();

            Toast.makeText(this, R.string.vpn_message, Toast.LENGTH_SHORT).show();

        }

        if (provideSnifferCheck != null) {
            Toast.makeText(MovieDetailsActivity.this, R.string.sniffer_message, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }

        if (settingsManager.getSettings().getRootDetection() == 1 &&  provideRootCheck != null) {
            Toast.makeText(MovieDetailsActivity.this, R.string.root_warning, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }


        super.onResume();
        IronSource.onResume(this);
    }


    @Override
    protected void onPause() {

        if (GoogleServicesHelper.available(this)) {

            mCastContext.removeCastStateListener(mCastStateListener);
            mCastContext.getSessionManager().removeSessionManagerListener(
                    mSessionManagerListener, CastSession.class);
        }

        super.onPause();
        IronSource.onPause(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);


        getMenuInflater().inflate(R.menu.menu, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(this, menu,
                R.id.media_route_menu_item);
        mQueueMenuItem = menu.findItem(R.id.action_show_queue);
        showIntroductoryOverlay();
        return true;
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
        if (item.getItemId() == R.id.action_settings) {
            intent = new Intent(MovieDetailsActivity.this, CastPreference.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_show_queue) {
            intent = new Intent(MovieDetailsActivity.this, QueueListViewActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event)
                || super.dispatchKeyEvent(event);
    }



    private void loadRewardedAd() {

        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    settingsManager.getSettings().getAdUnitIdRewarded(),
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            rewardedAd = null;

                            MovieDetailsActivity.this.isLoading = false;

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                            MovieDetailsActivity.this.isLoading = false;
                            MovieDetailsActivity.this.rewardedAd = rewardedAd;
                        }
                    });
        }

    }


    private void onLoadFacebookBanner() {

        if (settingsManager.getSettings().getAdFaceAudienceBanner() ==1){

            AdListener adListener = new AdListener() {
                @Override
                public void onError(com.facebook.ads.Ad ad, AdError adError) {
                    //
                }

                @Override
                public void onAdLoaded(com.facebook.ads.Ad ad) {

                    //
                }

                @Override
                public void onAdClicked(com.facebook.ads.Ad ad) {

                    //
                }

                @Override
                public void onLoggingImpression(com.facebook.ads.Ad ad) {

                    //

                }

            };

            com.facebook.ads.AdView facebookBanner = new com.facebook.ads.AdView(this,
                    settingsManager.getSettings().getAdUnitIdFacebookBannerAudience(),
                    com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            binding.bannerContainer.addView(facebookBanner);

            facebookBanner.loadAd(facebookBanner.buildLoadAdConfig().withAdListener(adListener).build());

        }else {

            binding.bannerContainer.setVisibility(GONE);
        }

    }


    @SuppressLint("SetTextI18n")
    private void onLoadPogressResume(String tmdbId) {

        binding.resumeProgressCheck.setVisibility(View.VISIBLE);

        if (settingsManager.getSettings().getResumeOffline() == 1) {

            mediaRepository.hasResume(Integer.parseInt(tmdbId)).observe(this, resumeInfo -> {

                if (resumeInfo !=null){

                    binding.resumeProgressCheck.setVisibility(GONE);

                    if (resumeInfo.getResumePosition() !=null &&

                            authManager.getUserInfo().getId() !=null && authManager.getUserInfo().getId() == resumeInfo.getUserResumeId() && resumeInfo.getDeviceId().equals(Tools.id(this))) {


                        double d = resumeInfo.getResumePosition();

                        double moveProgress = d * 100 / resumeInfo.getMovieDuration();

                        binding.epResumeTitle.setText(media.getTitle());

                        binding.timeRemaning.setText(Tools.getProgressTime((resumeInfo.getMovieDuration() - resumeInfo.getResumePosition()), true));

                        binding.resumeProgressBar.setProgress((int) moveProgress);

                    }else {

                        binding.resumeProgressBar.setProgress(0);
                        binding.resumeProgressBar.setVisibility(GONE);

                    }


                    if (resumeInfo.getResumePosition() !=null && authManager.getUserInfo().getId() == resumeInfo.getUserResumeId()) {


                        binding.resumeProgressBar.setVisibility(View.VISIBLE);
                        binding.timeRemaning.setVisibility(View.VISIBLE);
                        binding.resumeLinear.setVisibility(View.VISIBLE);

                    }else {

                        binding.resumeProgressBar.setVisibility(GONE);
                        binding.timeRemaning.setVisibility(GONE);
                        binding.resumeLinear.setVisibility(GONE);
                    }

                }else {


                    binding.resumeProgressBar.setVisibility(GONE);
                    binding.timeRemaning.setVisibility(GONE);
                    binding.resumeLinear.setVisibility(GONE);
                    binding.resumeProgressCheck.setVisibility(GONE);
                }

            });


        }else {


            binding.resumeProgressCheck.setVisibility(View.VISIBLE);
            movieDetailViewModel.getResumeMovie(tmdbId);
            movieDetailViewModel.resumeMutableLiveData.observe(this, resume -> {


                if (resume !=null  && resume.getResumePosition() !=null &&

                        authManager.getUserInfo().getId() !=null && authManager.getUserInfo().getId() == resume.getUserResumeId() && resume.getDeviceId().equals(Tools.id(this))) {

                    binding.resumeProgressCheck.setVisibility(GONE);
                    double d = resume.getResumePosition();
                    double moveProgress = d * 100 / resume.getMovieDuration();
                    binding.epResumeTitle.setText(media.getTitle());

                    binding.timeRemaning.setText(Tools.getProgressTime((resume.getMovieDuration() - resume.getResumePosition()), true));
                    binding.resumeProgressBar.setProgress((int) moveProgress);

                }else {

                    binding.resumeProgressBar.setProgress(0);
                    binding.resumeProgressBar.setVisibility(GONE);
                    binding.resumeProgressCheck.setVisibility(GONE);

                }


                assert resume != null;
                if (resume.getResumePosition() !=null && authManager.getUserInfo().getId() == resume.getUserResumeId()) {


                    binding.resumeProgressBar.setVisibility(View.VISIBLE);
                    binding.timeRemaning.setVisibility(View.VISIBLE);
                    binding.resumeLinear.setVisibility(View.VISIBLE);

                }else {

                    binding.resumeProgressBar.setVisibility(GONE);
                    binding.timeRemaning.setVisibility(GONE);
                    binding.resumeLinear.setVisibility(GONE);
                }

            });



        }




    }


    private void onLoadToolbar() {

        Tools.loadToolbar(this,binding.toolbar,binding.appbar);


    }

    private void onDownloadMovie(Media media) {

        if (ContextCompat.checkSelfPermission(MovieDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MovieDetailsActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
            return;
        }

        if (settingsManager.getSettings().getSeparateDownload() == 1) {

             if (media.getDownloads() !=null && !media.getDownloads().isEmpty()) {

            String defaultDownloadsOptions = settingsManager.getSettings().getDefaultDownloadsOptions();
            if ("Free".equals(defaultDownloadsOptions)) {

                onLoadDownloadsList(media);

            } else if ("PremuimOnly".equals(defaultDownloadsOptions)) {

                if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(media);

                } else if (media.getPremuim() == 0 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(media);

                }  else   {

                    DialogHelper.showPremuimWarning(this);
                }
            } else if ("WithAdsUnlock".equals(defaultDownloadsOptions)) {

                if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(media);

                } else {

                    onLoadSubscribeDialog(media,false);

                }
            }

        }else {

        DialogHelper.showNoDownloadAvailable(this,getString(R.string.about_no_stream_download));

        }


        }else {

             if (media.getVideos() !=null && !media.getVideos().isEmpty()) {

            String defaultDownloadsOptions = settingsManager.getSettings().getDefaultDownloadsOptions();
            if ("Free".equals(defaultDownloadsOptions)) {
                onLoadDownloadsList(media);
            } else if ("PremuimOnly".equals(defaultDownloadsOptions)) {
                if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(media);

                } else if (media.getPremuim() == 0 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(media);

                }  else   {

                    DialogHelper.showPremuimWarning(this);
                }
            } else if ("WithAdsUnlock".equals(defaultDownloadsOptions)) {

                if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                    onLoadDownloadsList(media);

                } else {

                    onLoadSubscribeDialog(media,false);

                }
            }

        }else {

                 DialogHelper.showNoDownloadAvailable(this,getString(R.string.about_no_stream_download));

        }
        }

    }




    private void onLoadDownloadsList(Media media) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_downloads_list);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.gravity = Gravity.BOTTOM;
        lp.width = MATCH_PARENT;
        lp.height = MATCH_PARENT;

        RecyclerView recyclerView = dialog.findViewById(R.id.rv_movies_genres);
        TextView movieNameDetails = dialog.findViewById(R.id.movietitle);

        history  = new History(media.getId(),media.getId(),media.getPosterPath(),media.getTitle(),media.getBackdropPath(),null);

        DownloadsListAdapter downloadsListAdapter = new DownloadsListAdapter();

        movieNameDetails.setText(media.getTitle());


        if (settingsManager.getSettings().getSeparateDownload() == 1) {


          downloadsListAdapter.addToContent(media.getDownloads(),download,this,media,mediaRepository,settingsManager);


        }else {

            downloadsListAdapter.addToContent(media.getVideos(),download,this,media,mediaRepository,settingsManager);

        }


        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 0), true));
        recyclerView.setAdapter(downloadsListAdapter);


        dialog.show();
        dialog.getWindow().setAttributes(lp);

        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

        dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    private void onLoadSubscribeDialog(Media movieDetail, boolean stream) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_subscribe);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.gravity = Gravity.BOTTOM;
        lp.width = MATCH_PARENT;
        lp.height = MATCH_PARENT;


        dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

        startActivity(new Intent(this, SettingsActivity.class));

        dialog.dismiss();


        });

        dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {

        String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();


            if (getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

                onLoadApplovinAds(movieDetail,stream);

            }else if (getString(R.string.vungle).equals(defaultRewardedNetworkAds)) {

          onLoadVungleAds(movieDetail,stream);

            }else if (getString(R.string.ironsource).equals(defaultRewardedNetworkAds)) {

                onLoadIronsourceAds(movieDetail,stream);

            }else if (getString(R.string.appnext).equals(defaultRewardedNetworkAds)) {

                onLoadAppNextAds(movieDetail,stream);

           }else if (getString(R.string.startapp).equals(defaultRewardedNetworkAds)) {


               onLoadStartAppAds(movieDetail,stream);

           } else if (getString(R.string.unityads).equals(defaultRewardedNetworkAds)) {

               onLoadUnityAds(movieDetail,stream);


           } else if (getString(R.string.admob).equals(defaultRewardedNetworkAds)) {

               onLoadAdmobRewardAds(movieDetail,stream);


           }else if (getString(R.string.appodeal).equals(defaultRewardedNetworkAds)) {

               onLoadAppOdealRewardAds(movieDetail,stream);

           } else if (getString(R.string.facebook).equals(defaultRewardedNetworkAds)) {

               onLoadFaceBookRewardAds(movieDetail,stream);
           }

            dialog.dismiss();

        });


        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

         dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

    private void onLoadApplovinAds(Media movieDetail, boolean stream) {


        if (maxRewardedAd.isReady()) {

            maxRewardedAd.showAd();
        }

        maxRewardedAd.setListener(new MaxRewardedAdListener() {
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
            public void onRewardedVideoStarted(MaxAd ad) {

            }

            @Override
            public void onRewardedVideoCompleted(MaxAd ad) {

                if (stream) {

                    onLoadStream(movieDetail);
                }else {

                    onLoadDownloadsList(movieDetail);
                }

            }

            @Override
            public void onUserRewarded(MaxAd ad, MaxReward reward) {

            }
        });


    }


    private void onLoadVungleAds(Media movieDetail, boolean stream) {

        Vungle.loadAd(settingsManager.getSettings().getVungleRewardPlacementName(), new LoadAdCallback() {
            @Override
            public void onAdLoad(String id) {
                //
            }

            @Override
            public void onError(String id, VungleException e) {

                //
            }
        });



        Vungle.playAd(settingsManager.getSettings().getVungleRewardPlacementName(), new AdConfig(), new PlayAdCallback() {
            @Override
            public void onAdStart(String placementReferenceID) {
                //
            }

            @Override
            public void onAdViewed(String placementReferenceID) {
                //
               }



            @Override
            public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                if (stream) {

                    onLoadStream(movieDetail);
                }else {

                    onLoadDownloadsList(movieDetail);
                }

            }

            @Override
            public void onAdEnd(String placementReferenceID) {
                //
            }

            @Override
            public void onAdClick(String placementReferenceID) {
              //
            }

            @Override
            public void onAdRewarded(String placementReferenceID) {
                //
            }

            @Override
            public void onAdLeftApplication(String placementReferenceID) {
                //
            }

            @Override
            public void creativeId(String creativeId) {
                //
            }

            @Override
            public void onError(String id, VungleException e) {

                //
            }
        });
    }

    private void onLoadAppNextAds(Media movieDetail, boolean stream) {

        RewardedVideo mAppNextAdsVideoRewarded = new RewardedVideo(this, settingsManager.getSettings().getAppnextPlacementid());
        mAppNextAdsVideoRewarded.loadAd();
        mAppNextAdsVideoRewarded.showAd();

        // Get callback for ad loaded
        mAppNextAdsVideoRewarded.setOnAdLoadedCallback((s, appnextAdCreativeType) -> {
            //
        });

        mAppNextAdsVideoRewarded.setOnAdOpenedCallback(() -> {
            //
        });
        mAppNextAdsVideoRewarded.setOnAdClickedCallback(() -> {

        });

        mAppNextAdsVideoRewarded.setOnAdClosedCallback(() -> {
            if (stream) {

                onLoadStream(movieDetail);
            }else {

                onLoadDownloadsList(movieDetail);
            }
        });

        mAppNextAdsVideoRewarded.setOnAdErrorCallback(error -> Toast.makeText(MovieDetailsActivity.this, ""+error, Toast.LENGTH_SHORT).show());

        // Get callback when the user saw the video until the end (video ended)
        mAppNextAdsVideoRewarded.setOnVideoEndedCallback(() -> {


        });


    }

    private void onLoadIronsourceAds(Media movieDetail, boolean stream) {

        IronSource.showRewardedVideo(settingsManager.getSettings().getIronsourceRewardPlacementName());

        IronSource.setRewardedVideoListener(new RewardedVideoListener() {
            /**
             * Invoked when the RewardedVideo ad view has opened.
             * Your Activity will lose focus. Please avoid performing heavy
             * tasks till the video ad will be closed.
             */
            @Override
            public void onRewardedVideoAdOpened() {

                //
            }
            /*Invoked when the RewardedVideo ad view is about to be closed.
            Your activity will now regain its focus.*/
            @Override
            public void onRewardedVideoAdClosed() {

                //
            }

            @Override
            public void onRewardedVideoAvailabilityChanged(boolean available) {
                //Change the in-app 'Traffic Driver' state according to availability.
            }

            @Override
            public void onRewardedVideoAdRewarded(Placement placement) {

                if (stream) {

                    onLoadStream(movieDetail);
                }else {

                    onLoadDownloadsList(movieDetail);
                }

            }

            @Override
            public void onRewardedVideoAdShowFailed(IronSourceError error) {

                //

            }
            /*Invoked when the end user clicked on the RewardedVideo ad
             */
            @Override
            public void onRewardedVideoAdClicked(Placement placement) {
                //

            }

            @Override
            public void onRewardedVideoAdStarted(){
                //
            }
            /* Invoked when the video ad finishes plating. */
            @Override
            public void onRewardedVideoAdEnded(){

                //
            }
        });
    }



    private void onLoadFaceBookRewardAds(Media movieDetail, boolean stream) {

        com.facebook.ads.InterstitialAd facebookInterstitialAd = new com.facebook.ads.InterstitialAd(this,settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

            @Override
            public void onError(com.facebook.ads.Ad ad, AdError adError) {

                //

            }

            @Override
            public void onAdLoaded(com.facebook.ads.Ad ad) {

                facebookInterstitialAd.show();

            }

            @Override
            public void onAdClicked(com.facebook.ads.Ad ad) {

                //

            }

            @Override
            public void onLoggingImpression(com.facebook.ads.Ad ad) {


                //vvvvvv
            }

            @Override
            public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                //

            }

            @Override
            public void onInterstitialDismissed(com.facebook.ads.Ad ad) {

                if (stream) {

                    onLoadStream(movieDetail);
                }else {

                    onLoadDownloadsList(media);
                }

            }


        };


        facebookInterstitialAd.loadAd(
                facebookInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }



    private void onLoadAdmobRewardAds(Media movieDetail, boolean stream) {

        if (rewardedAd == null) {
            Toast.makeText(this, "The rewarded ad wasn't ready yet", Toast.LENGTH_SHORT).show();
            return;
        }
        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        //
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        rewardedAd = null;
                        // Preload the next rewarded ad.
                        MovieDetailsActivity.this.loadRewardedAd();
                    }
                });
        rewardedAd.show(
                MovieDetailsActivity.this,
                rewardItem -> {
                    if (stream) {

                        onLoadStream(movieDetail);
                    }else {

                        onLoadDownloadsList(movieDetail);
                    }
                });
        }



    private void onLoadAppOdealRewardAds(Media movieDetail, boolean stream) {

        Appodeal.show(this, Appodeal.REWARDED_VIDEO);

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean isPrecache) {

                //

            }

            @Override
            public void onRewardedVideoFailedToLoad() {

                //


            }

            @Override
            public void onRewardedVideoShown() {


                //


            }

            @Override
            public void onRewardedVideoShowFailed() {

                //

            }

            @Override
            public void onRewardedVideoClicked() {
                //


            }

            @Override
            public void onRewardedVideoFinished(double amount, String name) {

                if (stream) {

                    onLoadStream(movieDetail);
                }else {

                    onLoadDownloadsList(movieDetail);
                }

            }

            @Override
            public void onRewardedVideoClosed(boolean finished) {

                //

            }

            @Override
            public void onRewardedVideoExpired() {


                //


            }

        });

    }

    private void onLoadUnityAds(Media movieDetail, boolean stream) {

        if (UnityAdsImplementation.isReady()) {

            UnityAds.show (MovieDetailsActivity.this, settingsManager.getSettings().getUnityRewardPlacementId(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {

                    //
                }

                @Override
                public void onUnityAdsShowStart(String placementId) {

                    //
                }

                @Override
                public void onUnityAdsShowClick(String placementId) {
                    //
                }

                @Override
                public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {

                    if (stream) {

                        onLoadStream(movieDetail);
                    }else {

                        onLoadDownloadsList(movieDetail);
                    }
                }
            });
        }
    }


    public void onLoadStartAppAds(Media movieDetail, boolean stream) {

        startAppAd = new StartAppAd(getApplicationContext());

        startAppAd.setVideoListener(() -> {
            if (stream) {

                onLoadStream(movieDetail);
            }else {

                onLoadDownloadsList(movieDetail);
            }
        });

        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                DialogHelper.showAdsFailedWarning(getApplicationContext());

            }
        });


    }


    private void onLoadBanner() {

        if (settingsManager.getSettings().getAdUnitIdBanner() !=null && settingsManager.getSettings().getAdBanner() == 1) {

            Tools.onLoadAdmobBanner(this,binding.adViewContainer,settingsManager.getSettings().getAdUnitIdBanner());

        }

    }


    // Load the Movie Rating
    private void onLoadRating(float rating) {

        binding.ratingBar.setRating(rating / 2);
        binding.viewMovieRating.setText(String.valueOf(rating));

    }

    // Send report for this Movie
    private void onLoadReport(String title,String posterpath) {



        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_report);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.gravity = Gravity.BOTTOM;
        lp.width = MATCH_PARENT;
        lp.height = MATCH_PARENT;


        EditText editTextMessage = dialog.findViewById(R.id.et_post);
        TextView reportMovieName = dialog.findViewById(R.id.movietitle);
        ImageView imageView = dialog.findViewById(R.id.image_movie_poster);


        reportMovieName.setText(title);


        Tools.onLoadMediaCover(this,imageView,posterpath);


        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.view_report).setOnClickListener(v -> {

            editTextMessage.getText();

            if (editTextMessage.getText() !=null) {

                movieDetailViewModel.sendReport(title,editTextMessage.getText().toString());
                movieDetailViewModel.reportMutableLiveData.observe(MovieDetailsActivity.this, report -> {


                    if (report !=null) {


                        dialog.dismiss();


                        Toast.makeText(this, "Your report has been submitted successfully", Toast.LENGTH_SHORT).show();

                    }


                });

            }


        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->
        dialog.dismiss());
        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }


    // Handle Favorite Button Click to add or remove the from MyList
    public void onFavoriteClick(Media mediaDetail) {


        if (mediaRepository.isMovieFavorite(Integer.parseInt(mediaDetail.getId()))) {

            Timber.i("Removed From Watchlist");
            movieDetailViewModel.removeFavorite(mediaDetail);

            binding.favoriteIcon.setImageResource(R.drawable.add_from_queue);

            Toast.makeText(this, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

        }else {

            Timber.i("Added To Watchlist");
            movieDetailViewModel.addFavorite(mediaDetail);

            binding.favoriteIcon.setImageResource(R.drawable.ic_in_favorite);

            Toast.makeText(this, "Added To Watchlist", Toast.LENGTH_SHORT).show();
        }

    }


    // Get Movie Cast
    private void onLoadCast(Media media) {

        if (settingsManager.getSettings().getDefaultCastOption() !=null && settingsManager.getSettings().getDefaultCastOption().equals("IMDB")){

            if (media.getTmdbId() !=null) {

         internal = false;

         mCastAdapter = new CastAdapter(settingsManager,this, false);

           movieDetailViewModel.getMovieCast(Integer.parseInt(media.getTmdbId()));
                movieDetailViewModel.movieCreditsMutableLiveData.observe(this, credits -> {
            mCastAdapter = new CastAdapter(settingsManager,this, internal);
            mCastAdapter.addCasts(credits.getCasts());

            // Starring RecycleView
            binding.recyclerViewCastMovieDetail.setAdapter(mCastAdapter);
            binding.recyclerViewCastMovieDetail.setHasFixedSize(true);
            binding.recyclerViewCastMovieDetail.setNestedScrollingEnabled(false);
            binding.recyclerViewCastMovieDetail.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerViewCastMovieDetail.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));

        });
       }
        }else {

            // Starring RecycleView
            internal = true;
            mCastAdapter = new CastAdapter(settingsManager,this, true);

            mCastAdapter.addCasts(media.getCast());
            binding.recyclerViewCastMovieDetail.setAdapter(mCastAdapter);
            binding.recyclerViewCastMovieDetail.setHasFixedSize(true);
            binding.recyclerViewCastMovieDetail.setNestedScrollingEnabled(false);
            binding.recyclerViewCastMovieDetail.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerViewCastMovieDetail.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));


        }

    }


    // Load Relateds Movies
    private void onLoadRelatedsMovies(int id) {
        movieDetailViewModel.getRelatedsMovies(id);
        movieDetailViewModel.movieRelatedsMutableLiveData.observe(this, relateds -> {
            mRelatedsAdapter = new RelatedsAdapter();
            mRelatedsAdapter.addToContent(relateds.getRelateds());
            if (sharedPreferences.getString(
            FsmPlayerApi.decodeServerMainApi2(), FsmPlayerApi.decodeServerMainApi4()).equals(FsmPlayerApi.decodeServerMainApi4())) { finishAffinity(); }
            // Relateds Movies RecycleView
            binding.rvMylike.setAdapter(mRelatedsAdapter);
            binding.rvMylike.setHasFixedSize(true);
            binding.rvMylike.setNestedScrollingEnabled(false);
            binding.rvMylike.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvMylike.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));
            if (mRelatedsAdapter.getItemCount() == 0) {
                binding.relatedNotFound.setVisibility(View.VISIBLE);

            }else {
                binding.relatedNotFound.setVisibility(GONE);

            }



        });
    }



    // Load Stream if Added
    public  void onLoadStream(Media movieDetail) {

            if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                String[] charSequence = new String[movieDetail.getVideos().size()];

                for (int i = 0; i<movieDetail.getVideos().size(); i++) {
                    charSequence[i] = movieDetail.getVideos().get(i).getServer() + " - " + movieDetail.getVideos().get(i).getLang();

                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
                builder.setTitle(getString(R.string.select_qualities));
                builder.setCancelable(true);
                builder.setItems(charSequence, (dialogInterface, wich) -> {


                    if (movieDetail.getVideos().get(wich).getHeader() !=null && !movieDetail.getVideos().get(wich).getHeader().isEmpty()) {

                        PLAYER_HEADER = movieDetail.getVideos().get(wich).getHeader();
                    }


                    if (movieDetail.getVideos().get(wich).getUseragent() !=null && !movieDetail.getVideos().get(wich).getUseragent().isEmpty()) {

                       PLAYER_USER_AGENT = movieDetail.getVideos().get(wich).getUseragent();
                    }


                 if (movieDetail.getVideos().get(wich).getEmbed() == 1)  {


                 startStreamFromEmbed(movieDetail.getVideos().get(wich).getLink());


                    }else if (movieDetail.getVideos().get(wich).getSupportedHosts() == 1){


                   startSupportedHostsStream(movieDetail,wich,movieDetail.getVideos().get(wich));

                   }  else {

                     if (mCastSession !=null && mCastSession.isConnected()) {

                         startStreamCasting(movieDetail,wich, movieDetail.getVideos().get(wich).getLink());

                     } else   if (settingsManager.getSettings().getVlc() == 1) {

                      startStreamNormalLink(movieDetail,wich, movieDetail.getVideos().get(wich).getLink(),movieDetail.getVideos().get(wich));


                            } else {

                                startStreamFromDialog(movieDetail, wich, externalId,
                                        movieDetail.getVideos().get(wich).getLink(), movieDetail.getVideos().get(wich));
                            }

                        }

                });

                builder.show();

            }else {


                if (movieDetail.getVideos().get(0).getHeader() !=null && !movieDetail.getVideos().get(0).getHeader().isEmpty()) {

                    PLAYER_HEADER = movieDetail.getVideos().get(0).getHeader();
                }


                if (movieDetail.getVideos().get(0).getUseragent() !=null && !movieDetail.getVideos().get(0).getUseragent().isEmpty()) {

                    PLAYER_USER_AGENT = movieDetail.getVideos().get(0).getUseragent();
                }


                if (movieDetail.getVideos().get(0).getEmbed() == 1) {

                    startStreamFromEmbed(movieDetail.getVideos().get(0).getLink());


                }  else if (movieDetail.getVideos().get(0).getSupportedHosts() == 1){


                   startSupportedHostsStream(movieDetail,0, movieDetail.getVideos().get(0));


                    }else {



                    if (mCastSession !=null && mCastSession.isConnected()) {

                       startStreamCasting(movieDetail,0, movieDetail.getVideos().get(0).getLink());

                   }else if (settingsManager.getSettings().getVlc() == 1) {

                       startStreamNormalLink(movieDetail,0, movieDetail.getVideos().get(0).getLink(),movieDetail.getVideos().get(0));


                   }else {

                     startStreamFromDialog(movieDetail,0, externalId, movieDetail.getVideos().get(0).getLink(), movieDetail.getVideos().get(0));

                   }
              }

           }

            }


    private void startSupportedHostsStream(Media movieDetail, int wich, MediaStream mediaStream) {

        EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(this);

        if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

            easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
        }

        easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

        easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                if (multipleQuality){
                    if (vidURL!=null) {

                        CharSequence[] name = new CharSequence[vidURL.size()];

                        for (int i = 0; i < vidURL.size(); i++) {
                            name[i] = vidURL.get(i).getQuality();
                        }


                        final AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this, R.style.MyAlertDialogTheme);
                        builder.setTitle(getString(R.string.select_qualities));
                        builder.setCancelable(true);
                        builder.setItems(name, (dialogInterface, i) -> {

                            if (mCastSession !=null && mCastSession.isConnected()) {

                                startStreamCasting(movieDetail,wich, vidURL.get(i).getUrl());

                            }else {


                            if (settingsManager.getSettings().getVlc() == 1) {

                                startStreamNormalLink(movieDetail,wich,vidURL.get(i).getUrl(), movieDetail.getVideos().get(wich));


                            } else {

                                startStreamFromDialog(movieDetail, wich, externalId, vidURL.get(i).getUrl(), mediaStream);
                            }


                            }

                        });

                        builder.show();



                    }else  Toast.makeText(MovieDetailsActivity.this, "NULL", Toast.LENGTH_SHORT).show();

                }else {



                    if (mCastSession !=null && mCastSession.isConnected()) {

                        startStreamCasting(movieDetail,wich, vidURL.get(0).getUrl());

                    }else {


                        if (settingsManager.getSettings().getVlc() == 1) {

                            startStreamNormalLink(movieDetail,wich,vidURL.get(0).getUrl(), movieDetail.getVideos().get(wich));


                        } else {

                            startStreamFromDialog(movieDetail, wich, externalId, vidURL.get(0).getUrl(), mediaStream);
                        }


                    }

                }

            }

            @Override
            public void onError() {

                Toast.makeText(MovieDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        easyPlexSupportedHosts.find(mediaStream.getLink());

    }

    private void startStreamNormalLink(Media movieDetail, int wich, String url, MediaStream mediaStream) {


        final Dialog dialog = new Dialog(MovieDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_bottom_stream);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.gravity = Gravity.BOTTOM;
        lp.width = MATCH_PARENT;
        lp.height = MATCH_PARENT;


        LinearLayout vlc = dialog.findViewById(R.id.vlc);
        LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
        LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
        LinearLayout webcast = dialog.findViewById(R.id.webCast);



        vlc.setOnClickListener(v12 -> {
            Tools.streamMediaFromVlc(this,url,media,settingsManager, mediaStream);
            dialog.hide();
        });

        mxPlayer.setOnClickListener(v12 -> {
            Tools.streamMediaFromMxPlayer(this,url,media,settingsManager);
            dialog.hide();

        });

        webcast.setOnClickListener(v12 -> {
            Tools.streamMediaFromMxWebcast(this,url,media,settingsManager);
            dialog.hide();

        });


        easyplexPlayer.setOnClickListener(v12 -> {

        startStreamFromDialog(movieDetail,wich,externalId, url,movieDetail.getVideos().get(wich));
         dialog.hide();


        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

        dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }


    private void startStreamFromEmbed(String link) {


        Intent intent = new Intent(this, EmbedActivity.class);
        intent.putExtra(Constants.MOVIE_LINK, link);
        startActivity(intent);
    }


    private void startStreamCasting(Media movieDetail, int wich, String downloadUrl) {

        Tools.streamFromChromcast(this,movieDetail,movieDetail.getGenres().get(wich).getName(),downloadUrl,binding.PlayButtonIcon);
    }



    private void startStreamFromDialog(Media movieDetail, int wich, String externalId, String url, MediaStream mediaStream) {

        Tools.startMainStream(this,movieDetail, url,mediaStream.getServer(),mediaGenre, mediaStream, settingsManager);
        history = new History(movieDetail.getId(), movieDetail.getId(), movieDetail.getPosterPath(), movieDetail.getTitle(), movieDetail.getBackdropPath(), null);
        history.setUserHistoryId(authManager.getUserInfo().getId());
        history.setLink(movieDetail.getVideos().get(wich).getLink());
        history.setType("0");
        history.setTmdbId(movieDetail.getId());
        history.setExternalId(externalId);
        history.setPremuim(movieDetail.getPremuim());
        history.setHasrecap(movieDetail.getHasrecap());
        history.setSkiprecapStartIn(movieDetail.getSkiprecapStartIn());
        history.setSkiprecapStartIn(movieDetail.getSkiprecapStartIn());
        history.setMediaGenre(mediaGenre);
        history.setVoteAverage(movieDetail.getVoteAverage());
        movieDetailViewModel.addhistory(history);

    }


    // Back Button
    private void onLoadBackButton() {

        binding.backbutton.setOnClickListener(v -> {
            onBackPressed();
            Animatoo.animateSplit(MovieDetailsActivity.this);

        });
    }


    // Load The Trailer
    private void onLoadTrailer(String previewPath, String title, String backdrop, String trailerUrl) {


        if (sharedPreferences.getBoolean(Constants.WIFI_CHECK, false) &&
                NetworkUtils.isWifiConnected(this)) {

            DialogHelper.showWifiWarning(MovieDetailsActivity.this);

        }else {

        Tools.startTrailer(this,previewPath,title,backdrop,settingsManager,trailerUrl);

        }

    }


    // Display Movie Poster
    private void onLoadImage(String imageURL){
        Tools.onLoadMediaCover(this,binding.imageMoviePoster,imageURL);
    }

    // Display Movie Title
    private void onLoadTitle(String title){

        binding.textMovieTitle.setText(title);
    }


    // Display Movie Release Date
    private void onLoadDate(String date) throws ParseException {
        if (date != null && !date.trim().isEmpty()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            if (sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN)) finishAffinity();
            Date releaseDate = sdf1.parse(date);
            assert releaseDate != null;
            binding.textMovieRelease.setText(sdf2.format(releaseDate));
        } else {
            binding.textMovieRelease.setText("");}
    }

    // Display Movie Synopsis or Overview
    private void onLoadSynopsis(String synopsis){
        binding.textOverviewLabel.setText(synopsis);
    }



    // Movie Genres
    private void onLoadGenres(List<Genre> genresList) {

        for (Genre genre : genresList) {
            binding.mgenres.setText(genre.getName());
        }
    }


    private void checkAllDataLoaded() {
        if (mMovie ) {


            binding.progressBar.setVisibility(GONE);
            binding.itemDetailContainer.setVisibility(View.VISIBLE);
            binding.PlayButtonIcon.setVisibility(View.VISIBLE);
            binding.serieName.setVisibility(View.VISIBLE);
        }
    }



    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {


            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                        MovieDetailsActivity.this, mediaRouteMenuItem)
                        .setTitleText(getString(R.string.introducing_cast))
                        .setOverlayColor(R.color.primary)
                        .setSingleTime()
                        .setOnOverlayDismissedListener(
                                () -> mIntroductoryOverlay = null)
                        .build();
                mIntroductoryOverlay.show();

            },0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vungleBanner !=null) {
            vungleBanner.destroyAd();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (shareLinkLaunched) {
            Intent intent = new Intent(this, BaseActivity.class);
            startActivity(intent);
        }
    }
}

