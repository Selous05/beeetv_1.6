package com.beeecorptv.ui.animes;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.beeecorptv.util.Constants.ARG_MOVIE;
import static com.beeecorptv.util.Constants.SEASONS;
import static com.beeecorptv.util.Constants.UNITY_ADS_BANNER_HEIGHT;
import static com.beeecorptv.util.Constants.UNITY_ADS_BANNER_WIDTH;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.appnext.ads.interstitial.Interstitial;
import com.appnext.banners.BannerAdRequest;
import com.appnext.banners.BannerSize;
import com.appnext.base.Appnext;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.BuildConfig;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Animes;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.MovieResponse;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.media.Resume;
import com.beeecorptv.data.model.media.StatusFav;
import com.beeecorptv.data.model.serie.Season;
import com.beeecorptv.data.repository.AuthRepository;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.ItemAnimeDetailBinding;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.moviedetails.adapters.CastAdapter;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.ui.player.activities.EasyPlexPlayerActivity;
import com.beeecorptv.ui.player.activities.EmbedActivity;
import com.beeecorptv.ui.player.cast.queue.ui.QueueListViewActivity;
import com.beeecorptv.ui.player.cast.settings.CastPreference;
import com.beeecorptv.ui.seriedetails.RelatedsSeriesAdapter;
import com.beeecorptv.ui.settings.SettingsActivity;
import com.beeecorptv.ui.viewmodels.AnimeViewModel;
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
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
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
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.ads.UnityAdsImplementation;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.unity3d.services.banners.UnityBanners;
import com.vungle.warren.AdConfig;
import com.vungle.warren.BannerAdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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


public class AnimeDetailsActivity extends AppCompatActivity {

    private MaxInterstitialAd maxInterstitialAd;
    private IronSourceBannerLayout banner;
    private EasyPlexSupportedHosts easyPlexSupportedHosts;
    private com.google.android.gms.ads.nativead.NativeAd mNativeAd;
    private @Nullable AdOptionsView adOptionsView;
    private MediaView nativeAdMedia;
    private NativeAd nativeAd;
    private boolean mAnime;
    private boolean mEpisodesLoaded;
    ItemAnimeDetailBinding binding;
    CastAdapter mCastAdapter;
    private boolean internal = false;
    @Inject ViewModelProvider.Factory viewModelFactory;


    private boolean isMovieFav = false;

    @Inject
    AuthRepository authRepository;

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    private AnimeViewModel animeViewModel;


    @Inject
    @Named("ready")
    boolean settingReady;


    EpisodeAnimeAdapter mEpisodesSerieAdapter;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TokenManager tokenManager;

    @Inject
    SettingsManager settingsManager;
    private StartAppAd startAppAd;
    private RewardedAd mRewardedAd;
    private BannerView bottomBanner;
    boolean isLoading;

    @Inject
    AuthManager authManager;


    @Inject
    MediaRepository mediaRepository;

    private LoginViewModel loginViewModel;



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

    private MovieDetailViewModel movieDetailViewModel;
    private com.facebook.ads.AdView facebookBanner;
    private  Media anime;
    private Animes animes;
    private CastContext mCastContext;
    private final SessionManagerListener<CastSession> mSessionManagerListener =
            new MySessionManagerListener();
    private CastSession mCastSession;
    private MenuItem mediaRouteMenuItem;
    private MenuItem mQueueMenuItem;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;
    private String mediaGenre;

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

            Toast.makeText(AnimeDetailsActivity.this, getString(R.string.unable_cast), Toast.LENGTH_SHORT).show();
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

        binding = DataBindingUtil.setContentView(this,R.layout.item_anime_detail);

        // Initialize the AppNext Ads SDK.
        Appnext.init(this);

        Appodeal.initialize(this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.INTERSTITIAL | Appodeal.BANNER | Appodeal.REWARDED_VIDEO );

        IronSource.init(this, settingsManager.getSettings().getIronsourceAppKey(), IronSource.AD_UNIT.OFFERWALL,IronSource.AD_UNIT.REWARDED_VIDEO,IronSource.AD_UNIT.INTERSTITIAL,IronSource.AD_UNIT.BANNER);


        if (settingsManager.getSettings().getApplovinInterstitial() == 1 && settingsManager.getSettings().getApplovinInterstitialUnitid() !=null && !settingsManager.getSettings().getApplovinInterstitialUnitid().isEmpty()) {

            maxInterstitialAd = new MaxInterstitialAd(settingsManager.getSettings().getApplovinInterstitialUnitid(), this );
            maxInterstitialAd.loadAd();
        }


        mCastStateListener = newState -> {
            if (newState != CastState.NO_DEVICES_AVAILABLE) {
                showIntroductoryOverlay();
            }
        };
        mCastContext = CastContext.getSharedInstance(this);



        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();


        Intent intent = getIntent();
        anime = intent.getParcelableExtra(ARG_MOVIE);


        mCastAdapter = new CastAdapter(settingsManager,this, internal);


        mAnime = false;
        mEpisodesLoaded = false;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(GONE);

        Tools.setSystemBarTransparent(this);

        animeViewModel = new ViewModelProvider(this, viewModelFactory).get(AnimeViewModel.class);

        movieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel.class);

        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);


        if (appLinkData !=null) {

            animeViewModel.getAnimeDetails(appLinkData.getLastPathSegment());


        }else if ((anime.getId() !=null)) {

            animeViewModel.getAnimeDetails(anime.getId());

        }


        if (!isFinishing()) {

         initMovieDetails();

        }
        if (authManager.getUserInfo().getPremuim() != 1 && settingsManager.getSettings().getAdFaceAudienceInterstitial() == 1
                && settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience() !=null) {

            Tools.onLoadFacebookAudience(this,settingsManager.getSettings().getAdFaceAudienceInterstitial()

                    ,settingsManager.getSettings().getFacebookShowInterstitial()

                    ,settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());

        }



        // Load  Admob Interstitial Ads
        if (authManager.getUserInfo().getPremuim() != 1) {

            Tools.onLoadAdmobInterstitialAds(this,settingsManager.getSettings().getAdInterstitial(),settingsManager.getSettings().getAdShowInterstitial(),
                    settingsManager.getSettings().getAdUnitIdInterstitial());

        }


        if (authManager.getUserInfo().getPremuim() != 1 && settingsManager.getSettings().getAdFaceAudienceBanner() == 1 && settingsManager.getSettings().getAdUnitIdFacebookBannerAudience() !=null) {

            onLoadFacebookBanner();

        }


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


        if (settingsManager.getSettings().getApplovinBanner() == 1 && maxInterstitialAd.isReady() ) {
            maxInterstitialAd.showAd();
        }
    }

    private void onLoadVungleInterStetial() {


        if (settingsManager.getSettings().getVungleInterstitial() == 1 ) {

            Vungle.loadAd(settingsManager.getSettings().getVungleInterstitialPlacementName(), new LoadAdCallback() {
                @Override
                public void onAdLoad(String id) {

                    //
                }

                @Override
                public void onError(String id, VungleException e) {

                    //
                }
            });

            Vungle.playAd(settingsManager.getSettings().getVungleInterstitialPlacementName(), new AdConfig(), new PlayAdCallback() {
                @Override
                public void onAdStart(String placementReferenceID) {
                    //
                }

                @Override
                public void onAdViewed(String placementReferenceID) {
                    //
                }

                // Deprecated
                @Override
                public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                    //
                }

                @Override
                public void onAdEnd(String placementReferenceID) {
                    //
                }

                @Override
                public void onAdClick(String placementReferenceID)
                {
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

    }

    private void onLoadVungleBanner() {


        // Check for banner ad availability and display
        final BannerAdConfig bannerAdConfig = new BannerAdConfig();
        bannerAdConfig.setAdSize(AdConfig.AdSize.BANNER);

        if (Banners.canPlayAd(settingsManager.getSettings().getVungleBannerPlacementName(), bannerAdConfig.getAdSize())) {
            VungleBanner vungleBanner = Banners.getBanner(settingsManager.getSettings().getVungleBannerPlacementName(), bannerAdConfig, new PlayAdCallback() {
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


    private void onCheckFlagSecure() {

        if(settingsManager.getSettings().getFlagSecure() == 1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    private void onLoadAppNextInterStetial() {

        if (settingsManager.getSettings().getAppnextPlacementid() !=null) {

            Interstitial interstitialAppNext = new Interstitial(this, settingsManager.getSettings().getAppnextPlacementid());

            interstitialAppNext.loadAd();

            interstitialAppNext.showAd();

            // Get callback for ad loaded
            interstitialAppNext.setOnAdLoadedCallback((bannerId, creativeType) -> {
                //
            });// Get callback for ad opened
            interstitialAppNext.setOnAdOpenedCallback(() -> {
                //
            });// Get callback for ad clicked
            interstitialAppNext.setOnAdClickedCallback(() -> {
                //
            });// Get callback for ad closed
            interstitialAppNext.setOnAdClosedCallback(() -> {
                //
            });// Get callback for ad error
            interstitialAppNext.setOnAdErrorCallback(error -> {
                //
            });

        }

    }

    private void onLoadAppNextBanner() {

        if (settingsManager.getSettings().getAppnextPlacementid() !=null) {

            com.appnext.banners.BannerView appNextBanner = new  com.appnext.banners.BannerView(AnimeDetailsActivity.this);
            appNextBanner.setPlacementId(settingsManager.getSettings().getAppnextPlacementid());
            appNextBanner.setBannerSize(BannerSize.BANNER);
            binding.bannerAppNext.loadAd(new BannerAdRequest());

        }


    }
    private void onLoadIronsrcInterStetial() {

        if (settingsManager.getSettings().getIronsourceInterstitial() == 1) {

            IronSource.loadInterstitial();

            IronSource.setInterstitialListener(new InterstitialListener() {
                /**
                 * Invoked when Interstitial Ad is ready to be shown after load function was called.
                 */
                @Override
                public void onInterstitialAdReady() {

                    IronSource.showInterstitial(settingsManager.getSettings().getIronsourceInterstitialPlacementName());

                }
                /**
                 * invoked when there is no Interstitial Ad available after calling load function.
                 */
                @Override
                public void onInterstitialAdLoadFailed(IronSourceError error) {

                    //
                }
                /**
                 * Invoked when the Interstitial Ad Unit is opened
                 */
                @Override
                public void onInterstitialAdOpened() {

                    //
                }
                /*
                 * Invoked when the ad is closed and the user is about to return to the application.
                 */
                @Override
                public void onInterstitialAdClosed() {
                    //
                }
                /**
                 * Invoked when Interstitial ad failed to show.
                 * @param error - An object which represents the reason of showInterstitial failure.
                 */
                @Override
                public void onInterstitialAdShowFailed(IronSourceError error) {

                    //
                }
                /*
                 * Invoked when the end user clicked on the interstitial ad, for supported networks only.
                 */
                @Override
                public void onInterstitialAdClicked() {

                    //
                }
                /** Invoked right before the Interstitial screen is about to open.
                 *  NOTE - This event is available only for some of the networks.
                 *  You should NOT treat this event as an interstitial impression, but rather use InterstitialAdOpenedEvent
                 */
                @Override
                public void onInterstitialAdShowSucceeded() {

                    //
                }
            });
        }

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

    private void onLoadAppoDealInterStetial() {

    Tools.onLoadAppoDealInterStetial(this,settingsManager.getSettings().getAppodealInterstitial() );

    }

    private void initMovieDetails() {

        if (anime != null) {

            // Observe data from AnimeViewmodel
            animeViewModel.animeDetailMutableLiveData.observe(this, animeDetail -> {


                animes  = new Animes(animeDetail.getId(),animeDetail.getId(),animeDetail.getPosterPath(),animeDetail.getName());

                onLoadImage(animeDetail.getPosterPath());
                onLoadTitle(animeDetail.getName());
                onLoadSeasons(animeDetail);
                onLoadDate(animeDetail.getFirstAirDate());
                onLoadSynopsis(animeDetail.getOverview());
                onLoadRating(animeDetail.getVoteAverage());
                onLoadGenres(animeDetail.getGenres());
                onLoadBackButton();
                onLoadViews(animeDetail.getViews());
                onLoadRelatedsMovies(Integer.parseInt(animeDetail.getId()));
                onLoadAnimeCast(animeDetail);
                if(!settingReady)finishAffinity();
                if (authManager.getUserInfo().getPremuim() != 1 ) {
                    onLoadBanner();
                    onLoadFacebookBanner();
                    onLoadAppoDealBanner();
                    onLoadAppoDealInterStetial();
                    onLoadStartAppBannerInter();
                    onLoadUnityInterstetial();
                    onLoadUnityBanner();
                    onLoadFacebookNativeAds();
                    onLoadAdmobNativeAds();
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
                    binding.flAdplaceholder.setVisibility(GONE);
                    binding.nativeAdLayout.setVisibility(GONE);
                    binding.nativeAdLayout2.setVisibility(GONE);
                }



                 // Share button function
                    binding.shareIcon.setOnClickListener(v -> {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        Bundle extras = new Bundle();
                        extras.putString(Intent.EXTRA_TEXT, "Seen " +  " '" + animeDetail.getName()+"' "
                                + "on " + settingsManager.getSettings().getAppName() +"  yet ?"
                                + Uri.parse("https://" + BuildConfig.APPLICATION_ID +".app/animes/"+animeDetail.getId()) + " - " + " Install the application via "+Uri.parse(settingsManager.getSettings().getAppUrlAndroid()));
                        sendIntent.putExtras(extras);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    });


                checkMediaFavorite(animeDetail);


                // Don't translate anime word
                mediaRepository.hasHistory2(Integer.parseInt(animeDetail.getId()),"anime").observe(this, history -> {

                    if (history !=null) {

                        binding.resumePlay.setVisibility(View.VISIBLE);
                        binding.resumePlayTitle.setText(history.getTitle());

                        if (settingsManager.getSettings().getResumeOffline() == 1) {

                            mediaRepository.hasResume(Integer.parseInt(history.getEpisodeId())).observe(this, resumeInfo -> {

                                if (resumeInfo !=null){


                                    if (resumeInfo.getTmdb() != null && resumeInfo.getResumePosition()
                                            !=null && resumeInfo.getTmdb().equals(history.getEpisodeTmdb())
                                            && Tools.id(AnimeDetailsActivity.this).equals(resumeInfo.getDeviceId())) {


                                        double d = resumeInfo.getResumePosition();

                                        double moveProgress = d * 100 / resumeInfo.getMovieDuration();


                                        binding.linearResumeProgressBar.setVisibility(View.VISIBLE);
                                        binding.resumeProgressBar.setVisibility(View.VISIBLE);
                                        binding.resumeProgressBar.setProgress((int) moveProgress);
                                        binding.timeRemaning.setText(Tools.getProgressTime((resumeInfo.getMovieDuration() - resumeInfo.getResumePosition()), true));




                                    } else {

                                        binding.resumeProgressBar.setProgress(0);
                                        binding.resumeProgressBar.setVisibility(GONE);
                                        binding.timeRemaning.setVisibility(GONE);
                                        binding.linearResumeProgressBar.setVisibility(GONE);

                                    }


                                }else {


                                    binding.resumeProgressBar.setProgress(0);
                                    binding.resumeProgressBar.setVisibility(GONE);
                                    binding.timeRemaning.setVisibility(GONE);
                                    binding.linearResumeProgressBar.setVisibility(GONE);

                                }





                            });

                        }else {



                            mediaRepository.getResumeById(String.valueOf(history.getEpisodeTmdb()),settingsManager.getSettings().getApiKey())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<Resume>() {
                                        @Override
                                        public void onSubscribe(@NotNull Disposable d) {

                                            //

                                        }

                                        @SuppressLint("TimberArgCount")
                                        @Override
                                        public void onNext(@NotNull Resume resume) {


                                            if (resume.getTmdb() != null && resume.getResumePosition()
                                                    !=null && resume.getTmdb().equals(history.getEpisodeTmdb())
                                                    && Tools.id(AnimeDetailsActivity.this).equals(resume.getDeviceId())) {


                                                double d = resume.getResumePosition();

                                                double moveProgress = d * 100 / resume.getMovieDuration();



                                                binding.linearResumeProgressBar.setVisibility(View.VISIBLE);
                                                binding.resumeProgressBar.setVisibility(View.VISIBLE);
                                                binding.resumeProgressBar.setProgress((int) moveProgress);
                                                binding.timeRemaning.setText(Tools.getProgressTime((resume.getMovieDuration() - resume.getResumePosition()), true));




                                            } else {

                                                binding.resumeProgressBar.setProgress(0);
                                                binding.resumeProgressBar.setVisibility(GONE);
                                                binding.timeRemaning.setVisibility(GONE);
                                                binding.linearResumeProgressBar.setVisibility(GONE);

                                            }

                                        }

                                        @SuppressLint("ClickableViewAccessibility")
                                        @Override
                                        public void onError(@NotNull Throwable e) {



                                            //

                                        }

                                        @Override
                                        public void onComplete() {

                                            //

                                        }
                                    });
                        }



                        binding.topResume.setOnClickListener(v -> {

                            if (animeDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() !=null) {

                                onLoadResumeFromHistory(history,animeDetail);


                            }else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && animeDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {

                                onLoadSubscribeDialog(history,animeDetail);

                            }else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && animeDetail.getPremuim() == 0 ){

                                onLoadResumeFromHistory(history,animeDetail);


                            } else if (authManager.getUserInfo().getPremuim() == 1 && animeDetail.getPremuim() == 0){


                                onLoadResumeFromHistory(history,animeDetail);


                            }else {

                                DialogHelper.showPremuimWarning(this);

                            }
                        });

                    }else {


                        binding.resumePlay.setVisibility(GONE);

                    }


                });



                binding.scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                    int scrollY =  binding.scrollView.getScrollY();
                    int color = Color.parseColor("#E6070707"); // ideally a global variable
                    if (scrollY < 256) {
                        int alpha = (scrollY << 24) | (-1 >>> 8) ;
                        color &= (alpha);

                        binding.serieName.setText("");
                        binding.serieName.setVisibility(View.GONE);


                    }else {

                        binding.serieName.setText(animeDetail.getName());
                        binding.serieName.setVisibility(View.VISIBLE);

                    }
                    binding.toolbar.setBackgroundColor(color);

                });

                onLoadToolbar();


                // Report Anime if something didn't work
                binding.report.setOnClickListener(v -> onLoadReport(animeDetail.getName(),animeDetail.getPosterPath()));


                // Button to handle Trailer
                binding.ButtonPlayTrailer.setOnClickListener(v -> onLoadTrailer(animeDetail.getPreviewPath(), animeDetail.getTitle(), animeDetail.getBackdropPath(),animeDetail.getTrailerUrl()));

                // Favorite button function
                binding.favoriteIcon.setOnClickListener(view -> {


                    if (settingsManager.getSettings().getFavoriteonline() == 1) {

                        if (isMovieFav) {

                            authRepository.getDeleteAnimeOnline(animeDetail.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .cache()
                                    .subscribe(new Observer<>() {
                                        @Override
                                        public void onSubscribe(@NotNull Disposable d) {

                                            //

                                        }

                                        @Override
                                        public void onNext(@NotNull StatusFav statusFav) {

                                            Toast.makeText(AnimeDetailsActivity.this, "Removed From Watchlist", Toast.LENGTH_SHORT).show();


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

                            authRepository.getAddAnimeOnline(animeDetail.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .cache()
                                    .subscribe(new Observer<>() {
                                        @Override
                                        public void onSubscribe(@NotNull Disposable d) {

                                            //

                                        }

                                        @Override
                                        public void onNext(@NotNull StatusFav statusFav) {

                                            Toast.makeText(AnimeDetailsActivity.this, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

                                            Timber.i("Added To Watchlist");
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

                        onFavoriteClick(animes);
                    }

                });







            });


            mAnime = true;
            checkAllDataLoaded();


        }
    }



    private void checkMediaFavorite(Media animeDetail) {


        if (settingsManager.getSettings().getFavoriteonline() == 1 && tokenManager.getToken().getAccessToken() !=null) {

            loginViewModel.isAnimeFavoriteOnline(animeDetail.getId());
            loginViewModel.isSerieFavoriteOnlineMutableLiveData.observe(this, favAddOnline -> {

                if (favAddOnline.getStatus() == 1) {

                    isMovieFav = true;

                    binding.favoriteIcon.setImageResource(R.drawable.ic_in_favorite);

                } else {

                    isMovieFav = false;

                    binding.favoriteIcon.setImageResource(R.drawable.add_from_queue);

                }

            });

        } else {

            if (mediaRepository.isAnimeFavorite(Integer.parseInt(animeDetail.getId()))) {


                binding.favoriteIcon.setImageResource(R.drawable.ic_in_favorite);

            } else {


                binding.favoriteIcon.setImageResource(R.drawable.add_from_queue);

            }
        }
    }

    private void onLoadAnimeCast(Media animeDetail) {



        if (settingsManager.getSettings().getDefaultCastOption().equals("IMDB")){

            if (animeDetail.getTmdbId() !=null) {

                internal = false;

                mCastAdapter = new CastAdapter(settingsManager,this, false);

                animeViewModel.getSerieCast(Integer.parseInt(animeDetail.getTmdbId()));
                animeViewModel.serieCreditsMutableLiveData.observe(this, credits -> {

                    mCastAdapter = new CastAdapter(settingsManager,this, internal);
                    mCastAdapter.addCasts(credits.getCasts());

                    // Starring RecycleView
                    binding.recyclerViewCastMovieDetail.setAdapter(mCastAdapter);
                    binding.recyclerViewCastMovieDetail.setHasFixedSize(true);
                    binding.recyclerViewCastMovieDetail.setNestedScrollingEnabled(false);
                    binding.recyclerViewCastMovieDetail.setLayoutManager(new LinearLayoutManager(AnimeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    binding.recyclerViewCastMovieDetail.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));

                });
            }

        }else {

            // Starring RecycleView

            internal = true;

            mCastAdapter = new CastAdapter(settingsManager,this, true);

            mCastAdapter.addCasts(animeDetail.getCast());
            binding.recyclerViewCastMovieDetail.setAdapter(mCastAdapter);
            binding.recyclerViewCastMovieDetail.setHasFixedSize(true);
            binding.recyclerViewCastMovieDetail.setNestedScrollingEnabled(false);
            binding.recyclerViewCastMovieDetail.setLayoutManager(new LinearLayoutManager(AnimeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerViewCastMovieDetail.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));


        }

    }

    private void onLoadResumeFromHistory(History history, Media serieDetail) {

        mediaRepository.getAnimeEpisodeDetails(history.getEpisodeId(), settingsManager.getSettings().getApiKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(@NotNull MovieResponse movieResponse) {


                        if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                            String[] charSequence = new String[movieResponse.getEpisodes().get(0).getVideos().size()];
                            for (int i = 0; i < movieResponse.getEpisodes().get(0).getVideos().size(); i++) {
                                charSequence[i] = String.valueOf(movieResponse.getEpisodes().get(0).getVideos().get(i).getServer());

                            }


                            final AlertDialog.Builder builder = new AlertDialog.Builder(AnimeDetailsActivity.this
                                    , R.style.MyAlertDialogTheme);
                            builder.setTitle(R.string.source_quality);
                            builder.setCancelable(true);
                            builder.setItems(charSequence, (dialogInterface, wich) -> {


                                if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getSupportedHosts() == 1) {


                                    easyPlexSupportedHosts = new EasyPlexSupportedHosts(AnimeDetailsActivity.this);
                                    easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                        @Override
                                        public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                            if (multipleQuality) {
                                                if (vidURL != null) {

                                                    CharSequence[] name = new CharSequence[vidURL.size()];

                                                    for (int i = 0; i < vidURL.size(); i++) {
                                                        name[i] = vidURL.get(i).getQuality();
                                                    }

                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(AnimeDetailsActivity.this, R.style.MyAlertDialogTheme);
                                                    builder.setTitle(AnimeDetailsActivity.this.getString(R.string.select_qualities));
                                                    builder.setCancelable(true);
                                                    builder.setItems(name, (dialogInterface, i) -> {


                                                        if (settingsManager.getSettings().getVlc() == 1) {


                                                            final Dialog dialog = new Dialog(AnimeDetailsActivity.this);
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


                                                            webcast.setOnClickListener(v12 -> {


                                                                Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                                                                shareVideo.setDataAndType(Uri.parse(vidURL.get(i).getUrl()), "video/*");
                                                                shareVideo.setPackage("com.instantbits.cast.webvideo");
                                                                shareVideo.putExtra("title", history.getTitle());
                                                                shareVideo.putExtra("poster", history.getPosterPath());
                                                                Bundle headers = new Bundle();
                                                                headers.putString("Referer", settingsManager.getSettings().getAppName());
                                                                headers.putString("User-Agent", settingsManager.getSettings().getUserAgent());
                                                                shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                                                                shareVideo.putExtra("headers", headers);
                                                                shareVideo.putExtra("secure_uri", true);
                                                                try {
                                                                    startActivity(shareVideo);
                                                                } catch (ActivityNotFoundException ex) {
                                                                    // Open Play Store if it fails to launch the app because the package doesn't exist.
                                                                    // Alternatively you could use PackageManager.getLaunchIntentForPackage() and check for null.
                                                                    // You could try catch this and launch the Play Store website if it fails but this shouldn’t
                                                                    // fail unless the Play Store is missing.
                                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                    String uriString = "market://details?id=com.instantbits.cast.webvideo";
                                                                    intent.setData(Uri.parse(uriString));
                                                                    startActivity(intent);
                                                                }


                                                            });


                                                            vlc.setOnClickListener(v12 -> {

                                                                Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                                                                shareVideo.setDataAndType(Uri.parse(vidURL.get(i).getUrl()), "video/*");
                                                                shareVideo.setPackage("org.videolan.vlc");
                                                                shareVideo.putExtra("title", history.getTitle());
                                                                shareVideo.putExtra("poster", history.getPosterPath());
                                                                Bundle headers = new Bundle();
                                                                headers.putString("User-Agent", settingsManager.getSettings().getAppName());
                                                                shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                                                                shareVideo.putExtra("headers", headers);
                                                                shareVideo.putExtra("secure_uri", true);
                                                                try {
                                                                    startActivity(shareVideo);
                                                                    dialog.hide();
                                                                } catch (ActivityNotFoundException ex) {

                                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                    String uriString = "market://details?id=org.videolan.vlc";
                                                                    intent.setData(Uri.parse(uriString));
                                                                    startActivity(intent);
                                                                }
                                                            });


                                                            mxPlayer.setOnClickListener(v12 -> {

                                                                Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                                                                shareVideo.setDataAndType(Uri.parse(vidURL.get(i).getUrl()), "video/*");
                                                                shareVideo.setPackage("com.mxtech.videoplayer.ad");
                                                                shareVideo.putExtra("title", history.getTitle());
                                                                shareVideo.putExtra("poster", history.getPosterPath());
                                                                Bundle headers = new Bundle();
                                                                headers.putString("User-Agent", settingsManager.getSettings().getAppName());
                                                                shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                                                                shareVideo.putExtra("headers", headers);
                                                                shareVideo.putExtra("secure_uri", true);
                                                                try {
                                                                    startActivity(shareVideo);
                                                                    dialog.hide();
                                                                } catch (ActivityNotFoundException ex) {

                                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                    String uriString = "market://details?id=com.mxtech.videoplayer.ad";
                                                                    intent.setData(Uri.parse(uriString));
                                                                    startActivity(intent);
                                                                }


                                                            });


                                                            easyplexPlayer.setOnClickListener(v12 -> {
                                                                onLoadMainPlayerStreamYoutube(vidURL.get(i).getUrl(), history, movieResponse, serieDetail);
                                                                dialog.hide();


                                                            });

                                                            dialog.show();
                                                            dialog.getWindow().setAttributes(lp);

                                                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                    dialog.dismiss());


                                                            dialog.show();
                                                            dialog.getWindow().setAttributes(lp);


                                                        } else {

                                                            onLoadMainPlayerStreamYoutube(vidURL.get(i).getUrl(), history, movieResponse, serieDetail);
                                                        }

                                                    });

                                                    builder.show();


                                                } else
                                                    Toast.makeText(AnimeDetailsActivity.this, "NULL", Toast.LENGTH_SHORT).show();

                                            } else {

                                                onLoadMainPlayerStreamYoutube(vidURL.get(0).getUrl(), history, movieResponse, serieDetail);


                                                Timber.i("URL IS :%s", vidURL.get(0).getUrl());
                                            }

                                        }

                                        @Override
                                        public void onError() {

                                            Toast.makeText(AnimeDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    easyPlexSupportedHosts.find(movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());


                                } else {


                                    if (settingsManager.getSettings().getVlc() == 1) {


                                        final Dialog dialog = new Dialog(AnimeDetailsActivity.this);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.dialog_bottom_stream);
                                        dialog.setCancelable(false);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                        lp.copyFrom(dialog.getWindow().getAttributes());

                                        lp.gravity = Gravity.BOTTOM;
                                        lp.width = MATCH_PARENT;
                                        lp.height = MATCH_PARENT;


                                        LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                        LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                        LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);

                                        vlc.setOnClickListener(v12 -> {

                                            Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                                            shareVideo.setDataAndType(Uri.parse(movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink()), "video/*");
                                            shareVideo.setPackage("org.videolan.vlc");
                                            shareVideo.putExtra("title", movieResponse.getEpisodes().get(0).getName());
                                            shareVideo.putExtra("poster", movieResponse.getEpisodes().get(0).getStillPath());
                                            Bundle headers = new Bundle();
                                            headers.putString("User-Agent", settingsManager.getSettings().getAppName());
                                            shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                                            shareVideo.putExtra("headers", headers);
                                            shareVideo.putExtra("secure_uri", true);
                                            try {
                                                startActivity(shareVideo);
                                                dialog.hide();
                                            } catch (ActivityNotFoundException ex) {

                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                String uriString = "market://details?id=org.videolan.vlc";
                                                intent.setData(Uri.parse(uriString));
                                                startActivity(intent);
                                            }


                                        });


                                        mxPlayer.setOnClickListener(v12 -> {

                                            Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                                            shareVideo.setDataAndType(Uri.parse(movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink()), "video/*");
                                            shareVideo.setPackage("com.mxtech.videoplayer.ad");
                                            shareVideo.putExtra("title", movieResponse.getEpisodes().get(0).getName());
                                            shareVideo.putExtra("poster", movieResponse.getEpisodes().get(0).getStillPath());
                                            Bundle headers = new Bundle();
                                            headers.putString("User-Agent", settingsManager.getSettings().getAppName());
                                            shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                                            shareVideo.putExtra("headers", headers);
                                            shareVideo.putExtra("secure_uri", true);
                                            try {
                                                startActivity(shareVideo);
                                                dialog.hide();
                                            } catch (ActivityNotFoundException ex) {

                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                String uriString = "market://details?id=com.mxtech.videoplayer.ad";
                                                intent.setData(Uri.parse(uriString));
                                                startActivity(intent);
                                            }


                                        });


                                        easyplexPlayer.setOnClickListener(v12 -> {

                                            onLoadMainPlayerStream(history, movieResponse, serieDetail);
                                            dialog.hide();


                                        });

                                        dialog.show();
                                        dialog.getWindow().setAttributes(lp);

                                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                dialog.dismiss());


                                        dialog.show();
                                        dialog.getWindow().setAttributes(lp);


                                    } else {

                                        onLoadMainPlayerStream(history, movieResponse, serieDetail);

                                    }

                                }


                            });

                            builder.show();

                        } else {


                            if (movieResponse.getEpisodes().get(0).getVideos().get(0).getEmbed() == 1) {


                                Intent intent = new Intent(AnimeDetailsActivity.this, EmbedActivity.class);
                                intent.putExtra(Constants.MOVIE_LINK, movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());
                                startActivity(intent);


                            } else if (movieResponse.getEpisodes().get(0).getVideos().get(0).getSupportedHosts() == 1) {


                                easyPlexSupportedHosts = new EasyPlexSupportedHosts(AnimeDetailsActivity.this);
                                easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                    @Override
                                    public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                        if (multipleQuality) {
                                            if (vidURL != null) {

                                                CharSequence[] name = new CharSequence[vidURL.size()];

                                                for (int i = 0; i < vidURL.size(); i++) {
                                                    name[i] = vidURL.get(i).getQuality();
                                                }

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(AnimeDetailsActivity.this, R.style.MyAlertDialogTheme);
                                                builder.setTitle(AnimeDetailsActivity.this.getString(R.string.select_qualities));
                                                builder.setCancelable(true);
                                                builder.setItems(name, (dialogInterface, i) -> {


                                                    if (settingsManager.getSettings().getVlc() == 1) {


                                                        final Dialog dialog = new Dialog(AnimeDetailsActivity.this);
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


                                                        webcast.setOnClickListener(v12 -> {


                                                            Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                                                            shareVideo.setDataAndType(Uri.parse(vidURL.get(i).getUrl()), "video/*");
                                                            shareVideo.setPackage("com.instantbits.cast.webvideo");
                                                            shareVideo.putExtra("title", history.getTitle());
                                                            shareVideo.putExtra("poster", history.getPosterPath());
                                                            Bundle headers = new Bundle();
                                                            headers.putString("Referer", settingsManager.getSettings().getAppName());
                                                            headers.putString("User-Agent", settingsManager.getSettings().getUserAgent());
                                                            shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                                                            shareVideo.putExtra("headers", headers);
                                                            shareVideo.putExtra("secure_uri", true);
                                                            try {
                                                                startActivity(shareVideo);
                                                            } catch (ActivityNotFoundException ex) {
                                                                // Open Play Store if it fails to launch the app because the package doesn't exist.
                                                                // Alternatively you could use PackageManager.getLaunchIntentForPackage() and check for null.
                                                                // You could try catch this and launch the Play Store website if it fails but this shouldn’t
                                                                // fail unless the Play Store is missing.
                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                String uriString = "market://details?id=com.instantbits.cast.webvideo";
                                                                intent.setData(Uri.parse(uriString));
                                                                startActivity(intent);
                                                            }


                                                        });


                                                        vlc.setOnClickListener(v12 -> {

                                                            Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                                                            shareVideo.setDataAndType(Uri.parse(vidURL.get(i).getUrl()), "video/*");
                                                            shareVideo.setPackage("org.videolan.vlc");
                                                            shareVideo.putExtra("title", history.getTitle());
                                                            shareVideo.putExtra("poster", history.getPosterPath());
                                                            Bundle headers = new Bundle();
                                                            headers.putString("User-Agent", settingsManager.getSettings().getAppName());
                                                            shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                                                            shareVideo.putExtra("headers", headers);
                                                            shareVideo.putExtra("secure_uri", true);
                                                            try {
                                                                startActivity(shareVideo);
                                                                dialog.hide();
                                                            } catch (ActivityNotFoundException ex) {

                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                String uriString = "market://details?id=org.videolan.vlc";
                                                                intent.setData(Uri.parse(uriString));
                                                                startActivity(intent);
                                                            }
                                                        });


                                                        mxPlayer.setOnClickListener(v12 -> {

                                                            Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                                                            shareVideo.setDataAndType(Uri.parse(vidURL.get(i).getUrl()), "video/*");
                                                            shareVideo.setPackage("com.mxtech.videoplayer.ad");
                                                            shareVideo.putExtra("title", history.getTitle());
                                                            shareVideo.putExtra("poster", history.getPosterPath());
                                                            Bundle headers = new Bundle();
                                                            headers.putString("User-Agent", settingsManager.getSettings().getAppName());
                                                            shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                                                            shareVideo.putExtra("headers", headers);
                                                            shareVideo.putExtra("secure_uri", true);
                                                            try {
                                                                startActivity(shareVideo);
                                                                dialog.hide();
                                                            } catch (ActivityNotFoundException ex) {

                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                String uriString = "market://details?id=com.mxtech.videoplayer.ad";
                                                                intent.setData(Uri.parse(uriString));
                                                                startActivity(intent);
                                                            }


                                                        });


                                                        easyplexPlayer.setOnClickListener(v12 -> {
                                                            onLoadMainPlayerStreamYoutube(vidURL.get(i).getUrl(), history, movieResponse, serieDetail);
                                                            dialog.hide();


                                                        });

                                                        dialog.show();
                                                        dialog.getWindow().setAttributes(lp);

                                                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                                dialog.dismiss());


                                                        dialog.show();
                                                        dialog.getWindow().setAttributes(lp);


                                                    } else {

                                                        onLoadMainPlayerStreamYoutube(vidURL.get(i).getUrl(), history, movieResponse, serieDetail);
                                                    }

                                                });

                                                builder.show();


                                            } else
                                                Toast.makeText(AnimeDetailsActivity.this, "NULL", Toast.LENGTH_SHORT).show();

                                        } else {

                                            onLoadMainPlayerStreamYoutube(vidURL.get(0).getUrl(), history, movieResponse, serieDetail);


                                            Timber.i("URL IS :%s", vidURL.get(0).getUrl());
                                        }

                                    }

                                    @Override
                                    public void onError() {

                                        Toast.makeText(AnimeDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                easyPlexSupportedHosts.find(movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());


                            } else {

                                onLoadMainPlayerStream(history, movieResponse, serieDetail);
                            }


                        }


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
    }

    private void onLoadMainPlayerStream(History history, MovieResponse movieResponse, Media serieDetail) {

        float voteAverage = Float.parseFloat(movieResponse.getEpisodes().get(0).getVoteAverage());

        String name = history.getTitle();
        String tvseasonid = history.getSeasonsId();
        Integer currentep = Integer.parseInt(movieResponse.getEpisodes().get(0).getEpisodeNumber());
        String currentepname = movieResponse.getEpisodes().get(0).getName();
        String currenteptmdbnumber = String.valueOf(movieResponse.getEpisodes().get(0).getId());
        String currentseasons = history.getCurrentSeasons();
        String currentseasonsNumber = history.getSeasonsNumber();
        String currentepimdb = String.valueOf(movieResponse.getEpisodes().get(0).getId());
        String artwork = movieResponse.getEpisodes().get(0).getStillPath();
        String type = "anime";
        String currentquality = movieResponse.getEpisodes().get(0).getVideos().get(0).getServer();
        String videourl = movieResponse.getEpisodes().get(0).getVideos().get(0).getLink();
        int hls = movieResponse.getEpisodes().get(0).getVideos().get(0).getHls();

        Intent intent = new Intent(AnimeDetailsActivity.this, EasyPlexMainPlayer.class);

        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                MediaModel.media(history.getSerieId(), null,
                        currentquality, type, name, videourl, artwork, null, currentep
                        , currentseasons,
                        currentepimdb,
                        tvseasonid, currentepname,
                        currentseasonsNumber, history.getPosition(),
                        currenteptmdbnumber,
                        history.getPremuim(),hls,null
                        ,history.getImdbExternalId(),
                        serieDetail.getPosterPath()
                        ,movieResponse.getEpisodes().get(0).getHasrecap(),movieResponse.getEpisodes().get(0).getSkiprecapStartIn(),mediaGenre,serieDetail.getName(),voteAverage));
        intent.putExtra(ARG_MOVIE, serieDetail);
        startActivity(intent);
    }

    private void onLoadMainPlayerStreamYoutube(String downloadUrl, History history, MovieResponse movieResponse, Media serieDetail) {

        String name = history.getTitle();
        String tvseasonid = history.getSeasonsId();
        Integer currentep = Integer.parseInt(movieResponse.getEpisodes().get(0).getEpisodeNumber());
        String currentepname = movieResponse.getEpisodes().get(0).getName();
        String currenteptmdbnumber = String.valueOf(movieResponse.getEpisodes().get(0).getId());
        String currentseasons = history.getCurrentSeasons();
        String currentseasonsNumber = history.getSeasonsNumber();
        String currentepimdb = String.valueOf(movieResponse.getEpisodes().get(0).getId());
        String artwork = movieResponse.getEpisodes().get(0).getStillPath();
        String type = "anime";
        float voteAverage = Float.parseFloat(movieResponse.getEpisodes().get(0).getVoteAverage());
        String currentquality = movieResponse.getEpisodes().get(0).getVideos().get(0).getServer();
        int hls = movieResponse.getEpisodes().get(0).getVideos().get(0).getHls();

        Intent intent = new Intent(AnimeDetailsActivity.this, EasyPlexMainPlayer.class);

        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                MediaModel.media(history.getSerieId(), null,
                        currentquality, type, name, downloadUrl, artwork, null, currentep
                        , currentseasons,
                        currentepimdb,
                        tvseasonid, currentepname,
                        currentseasonsNumber, history.getPosition(),
                        currenteptmdbnumber,
                        history.getPremuim(),hls,null
                        ,history.getImdbExternalId(),
                        serieDetail.getPosterPath()
                        ,movieResponse.getEpisodes().get(0).getHasrecap(),movieResponse.getEpisodes().get(0).getSkiprecapStartIn(),mediaGenre,serieDetail.getName(),voteAverage));
        intent.putExtra(ARG_MOVIE, serieDetail);
        startActivity(intent);
    }

    private void onLoadSubscribeDialog(History history, Media animeDetail) {


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
            if ("StartApp".equals(defaultRewardedNetworkAds)) {


                onLoadStartAppAds(history,animeDetail);

            } else if ("UnityAds".equals(defaultRewardedNetworkAds)) {

                onLoadUnityAds(history,animeDetail);


            } else if ("Admob".equals(defaultRewardedNetworkAds)) {


                onLoadAdmobRewardAds(history,animeDetail);


            } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                onLoadFaceBookRewardAds(history,animeDetail);

            }else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                onLoadAppOdealRewardAds(history,animeDetail);

            }else if ("Auto".equals(defaultRewardedNetworkAds)) {

                onLoadAutoRewardAds(history,animeDetail);

            }


            dialog.dismiss();


        });


        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

    private void onLoadAutoRewardAds(History history, Media animeDetail) {

        Random random = new Random();
        int numberOfMethods = 4;

        switch(random.nextInt(numberOfMethods)) {
            case 0:
                onLoadStartAppAds(history,animeDetail);
                break;
            case 1:
                onLoadUnityAds(history,animeDetail);
                break;
            case 3:
                onLoadFaceBookRewardAds(history,animeDetail);
                break;
            default:
                onLoadAdmobRewardAds(history,animeDetail);
        }
    }

    private void onLoadAppOdealRewardAds(History history, Media animeDetail) {

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

                onLoadResumeFromHistory(history,animeDetail);

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

    private void onLoadFaceBookRewardAds(History history, Media animeDetail) {

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

                onLoadResumeFromHistory(history,animeDetail);

            }


        };


        facebookInterstitialAd.loadAd(
                facebookInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    private void onLoadAdmobRewardAds(History history, Media animeDetail) {

        if (mRewardedAd == null) {
            Toast.makeText(this, "The rewarded ad wasn't ready yet", Toast.LENGTH_SHORT).show();
            return;
        }
        mRewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        //
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mRewardedAd = null;
                        // Preload the next rewarded ad.
                        loadRewardedAd();
                    }
                });
        mRewardedAd.show(
                AnimeDetailsActivity.this,
                rewardItem ->       onLoadResumeFromHistory(history,animeDetail));
    }

    private void loadRewardedAd() {

        if (mRewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    AnimeDetailsActivity.this,
                    settingsManager.getSettings().getAdUnitIdRewarded(),
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            mRewardedAd = null;

                            isLoading = false;

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                            isLoading = false;
                            mRewardedAd = rewardedAd;
                        }
                    });
        }
    }

    private void onLoadUnityAds(History history, Media animeDetail) {

        if (UnityAdsImplementation.isReady()) {
            UnityAds.show (this, settingsManager.getSettings().getUnityRewardPlacementId(), new IUnityAdsShowListener() {
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

                    onLoadResumeFromHistory(history,animeDetail);
                }
            });
        }

    }

    private void onLoadStartAppAds(History history, Media animeDetail) {

        startAppAd.setVideoListener(() -> onLoadResumeFromHistory(history,animeDetail));

        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {

                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                //

            }
        });
    }


    private void onLoadToolbar() {

        Tools.loadToolbar(this,binding.toolbar,binding.appbar);


    }


    private void onLoadAppoDealBanner() {

        if (settingsManager.getSettings().getAppodealBanner() == 1) {

            Appodeal.setBannerViewId(R.id.appodealBannerView);
            Appodeal.show(AnimeDetailsActivity.this, Appodeal.BANNER_VIEW);

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

                            if ( binding.adChoicesContainer != null) {
                                adOptionsView = new AdOptionsView(AnimeDetailsActivity.this, nativeAd, binding.nativeAdLayout);
                                binding.adChoicesContainer.removeAllViews();
                                binding.adChoicesContainer.addView(adOptionsView, 0);
                            }

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


    private void onLoadAdmobNativeAds() {

        if (settingsManager.getSettings().getAdUnitIdNativeEnable() == 1) {


            AdLoader.Builder builder = new AdLoader.Builder(this, settingsManager.getSettings().getAdUnitIdNative());

            // OnLoadedListener implementation.
            builder.forNativeAd(
                    nativeAd -> {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        boolean isDestroyed = false;
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

                        NativeAdView adView =
                                (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, null);
                        populateNativeAdView(nativeAd, adView);
                        binding.flAdplaceholder.removeAllViews();
                        binding.flAdplaceholder.addView(adView);
                    });

            VideoOptions videoOptions =
                    new VideoOptions.Builder().build();

            NativeAdOptions adOptions =
                    new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader =
                    builder
                            .withAdListener(
                                    new com.google.android.gms.ads.AdListener() {
                                        @Override
                                        public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                                            //
                                        }
                                    })
                            .build();

            adLoader.loadAd(new AdRequest.Builder().build());



        }


    }


    private void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
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
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
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

        nativeAdMedia = adView.findViewById(R.id.native_ad_media);
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

    private void onLoadUnityBanner() {

        if (settingsManager.getSettings().getUnityadsBanner() == 1) {
            bottomBanner = new BannerView(AnimeDetailsActivity.this, settingsManager.getSettings().getUnityBannerPlacementId(), new UnityBannerSize(UNITY_ADS_BANNER_WIDTH, UNITY_ADS_BANNER_HEIGHT));
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

    private void onLoadUnityInterstetial() {

        Tools.onLoadUnityInterstetial(this,settingsManager.getSettings().getUnityadsInterstitial()
                ,settingsManager.getSettings().getUnityShow(),UnityAdsImplementation.isReady(), settingsManager);
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




    private void onLoadBanner() {

        if (settingsManager.getSettings().getAdUnitIdBanner() !=null && settingsManager.getSettings().getAdBanner() == 1) {

            Tools.onLoadAdmobBanner(this,binding.adViewContainer,settingsManager.getSettings().getAdUnitIdBanner());

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

            facebookBanner =
                    new com.facebook.ads.AdView(this,
                            settingsManager.getSettings().getAdUnitIdFacebookBannerAudience(),
                            com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            binding.bannerContainer.addView(facebookBanner);

            facebookBanner.loadAd(facebookBanner.buildLoadAdConfig().withAdListener(adListener).build());

        }else {

            binding.bannerContainer.setVisibility(GONE);
        }


    }



    // Add or Remove Anime from Favorite
    public void onFavoriteClick(Animes animes) {

        if (mediaRepository.isAnimeFavorite(Integer.parseInt(animes.getId()))) {

            Timber.i("Removed From Watchlist");
            animeViewModel.removeTvFromFavorite(animes);

            binding.favoriteIcon.setImageResource(R.drawable.add_from_queue);

            Toast.makeText(this, "Removed: " + animes.getName(),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.removeFavorite(animes);

        }else {

            Timber.i("Added To Watchlist");
            animeViewModel.addtvFavorite(animes);

            binding.favoriteIcon.setImageResource(R.drawable.ic_in_favorite);

            Toast.makeText(this, "Added: " + animes.getName(),
                    Toast.LENGTH_SHORT).show();
        }



    }



    // Load Seasons & episodes
    @SuppressLint("SetTextI18n")
    private void onLoadSeasons(Media serieDetail) {

        if (serieDetail.getSeasons() !=null && !serieDetail.getSeasons().isEmpty()) {

            binding.mseason.setText(SEASONS + serieDetail.getSeasons().size());

            binding.planetsSpinner.setItem(serieDetail.getSeasons());
            binding.planetsSpinner.setSelection(0);
            binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                    if(!settingReady)finishAffinity();

                    Season season = (Season) adapterView.getItemAtPosition(position);
                    String episodeId = String.valueOf(season.getId());
                    String currentSeason = season.getName();
                    String seasonNumber = season.getSeasonNumber();

                    // Episodes RecycleView
                    binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(AnimeDetailsActivity.this));
                    binding.recyclerViewEpisodes.setHasFixedSize(true);

                    mEpisodesSerieAdapter = new EpisodeAnimeAdapter(serieDetail.getId(),seasonNumber,episodeId,currentSeason,sharedPreferences,authManager,settingsManager,mediaRepository
                            ,serieDetail.getName(),serieDetail.getPremuim(),tokenManager,
                            AnimeDetailsActivity.this,serieDetail.getPosterPath()
                            ,serieDetail,mediaGenre,serieDetail.getImdbExternalId());

                    mEpisodesSerieAdapter.addSeasons(season.getEpisodes());
                    binding.recyclerViewEpisodes.setAdapter(mEpisodesSerieAdapter);


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                    // do nothing if no season selected

                }
            });

        }
        mEpisodesLoaded = true;
        checkAllDataLoaded();


    }




    // Load the anime rating
    private void onLoadRating(float voteAverage) {

        binding.viewMovieRating.setText(String.valueOf(voteAverage));
        binding.ratingBar.setRating(voteAverage / 2);
    }



    @Override
    protected void onResume() {

        mCastContext.addCastStateListener(mCastStateListener);
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        if (mCastSession == null) {
            mCastSession = CastContext.getSharedInstance(this).getSessionManager()
                    .getCurrentCastSession();
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
            Toast.makeText(AnimeDetailsActivity.this, R.string.sniffer_message, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }

        if (settingsManager.getSettings().getRootDetection() == 1 &&  provideRootCheck != null) {
            Toast.makeText(AnimeDetailsActivity.this, R.string.root_warning, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }



        if (settingsManager.getSettings().getVpn() ==1 && checkVpn) {


            finishAffinity();

            Toast.makeText(this, R.string.vpn_message, Toast.LENGTH_SHORT).show();

        }

        super.onResume();
        IronSource.onResume(this);
    }



    @Override
    protected void onPause() {
        mCastContext.removeCastStateListener(mCastStateListener);
        mCastContext.getSessionManager().removeSessionManagerListener(
                mSessionManagerListener, CastSession.class);

        super.onPause();
        IronSource.onPause(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);


        getMenuInflater().inflate(R.menu.menu, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(AnimeDetailsActivity.this, menu,
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
            intent = new Intent(AnimeDetailsActivity.this, CastPreference.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_show_queue) {
            intent = new Intent(AnimeDetailsActivity.this, QueueListViewActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event)
                || super.dispatchKeyEvent(event);
    }


    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {

            new Handler(Looper.getMainLooper()).postDelayed(() -> {

                mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                        AnimeDetailsActivity.this, mediaRouteMenuItem)
                        .setTitleText(getString(R.string.introducing_cast))
                        .setOverlayColor(R.color.primary)
                        .setSingleTime()
                        .setOnOverlayDismissedListener(
                                () -> mIntroductoryOverlay = null)
                        .build();
                mIntroductoryOverlay.show();

            }, 100);
        }
    }


    // Share button function
    private void onLoadShare(String title, int imdb) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg)+ " " + title + " For more information please check"+("https://www.imdb.com/title/tt" + imdb));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);



    }



    @SuppressLint("SetTextI18n")
    private void onLoadViews(String views) {

        binding.viewMovieViews.setText(getString(R.string.views)+Tools.getViewFormat(Integer.parseInt(views)));

    }


    private void onLoadRelatedsMovies(int id) {

        animeViewModel.getRelatedsAnimes(id);
        animeViewModel.movieRelatedsMutableLiveData.observe(this, relateds -> {
            RelatedsSeriesAdapter relatedsAdapter  = new RelatedsSeriesAdapter();
            relatedsAdapter.addToContent(relateds.getRelateds());

            // Relateds Movies RecycleView

            binding.rvMylike.setAdapter(relatedsAdapter);
            binding.rvMylike.setHasFixedSize(true);
            binding.rvMylike.setNestedScrollingEnabled(false);
            binding.rvMylike.setLayoutManager(new LinearLayoutManager(AnimeDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvMylike.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));


            if (relatedsAdapter.getItemCount() == 0) {

                binding.relatedNotFound.setVisibility(View.VISIBLE);

            }else {

                binding.relatedNotFound.setVisibility(GONE);

            }



        });
    }

    // Handle Back Button
    private void onLoadBackButton() {

        binding.backbutton.setOnClickListener(v -> {
            onBackPressed();
            Animatoo.animateSplit(this);

        });
    }



    // Load Anime Trailer
    private void onLoadTrailer(String previewPath, String title, String backdrop, String trailerUrl) {


        if (sharedPreferences.getBoolean(Constants.WIFI_CHECK, false) &&
                NetworkUtils.isWifiConnected(this)) {

            DialogHelper.showWifiWarning(AnimeDetailsActivity.this);

        }else {

            Tools.startTrailer(this,previewPath,title,backdrop,settingsManager,trailerUrl);

        }

    }


    // Display Anime Poster
    private void onLoadImage(String imageURL){

        Tools.onLoadMediaCover(this,binding.imageMoviePoster,imageURL);

    }

    // Display Anime Title
    private void onLoadTitle(String title){

        binding.serieTitle.setText(title);
    }


    // Display Anime Release Date
    private void onLoadDate(String date){

        Tools.dateFormat(date, binding.mrelease);
    }


    // Display Anime Synopsis or Overview
    private void onLoadSynopsis(String synopsis){
        binding.serieOverview.setText(synopsis);
    }


    private void onLoadReport(String title, String posterpath) {


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


            editTextMessage.getText().toString();


            if (editTextMessage.getText() !=null) {

                animeViewModel.sendReport(settingsManager.getSettings().getApiKey(),title,editTextMessage.getText().toString());
                animeViewModel.reportMutableLiveData.observe(AnimeDetailsActivity.this, report -> {


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

    // Anime Genres
    private void onLoadGenres(List<Genre> genresList) {
        for (Genre genre : genresList) {
            binding.mgenres.setText(genre.getName());
        }

        if (genresList !=null) {

            for (Genre genre : genresList) {
                mediaGenre = genre.getName();
            }

        }
    }



    private void checkAllDataLoaded() {
        if (mAnime && mEpisodesLoaded) {

            binding.progressBar.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }





    @Override
    protected void onDestroy() {
        if (mNativeAd != null) {
            mNativeAd.destroy();
            mNativeAd = null;
        }

        if (nativeAdMedia != null) {
            nativeAdMedia.destroy();
            nativeAdMedia = null;
        }

        if (nativeAd != null) {
            nativeAd.unregisterView();
            nativeAd.destroy();
            nativeAd = null;
        }


        binding.appodealBannerView.removeAllViews();
        binding.appodealBannerView.removeAllViewsInLayout();


        if (bottomBanner!=null) {

            bottomBanner.destroy();
            bottomBanner = null;
        }


        if (startAppAd !=null) {

            startAppAd = null;
        }

        if (mRewardedAd !=null) {

            mRewardedAd = null;
        }


        if (facebookBanner !=null) {

            facebookBanner.destroy();
            facebookBanner = null;

        }


        UnityBanners.destroy();
        Appodeal.destroy(Appodeal.BANNER);
        Appodeal.destroy(Appodeal.INTERSTITIAL);
        Appodeal.destroy(Appodeal.REWARDED_VIDEO);
        Glide.get(this).clearMemory();
        binding = null;
        super.onDestroy();

    }

}