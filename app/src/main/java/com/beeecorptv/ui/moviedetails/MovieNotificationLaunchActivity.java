package com.beeecorptv.ui.moviedetails;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.util.Constants.ARG_MOVIE;
import static com.beeecorptv.util.Constants.SERVER_BASE_URL;
import static com.beeecorptv.util.Constants.UPNEXT;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.appnext.ads.fullscreen.RewardedVideo;
import com.appnext.base.Appnext;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.LayoutEpisodeNotifcationBinding;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.player.activities.EmbedActivity;
import com.beeecorptv.ui.player.cast.queue.ui.QueueListViewActivity;
import com.beeecorptv.ui.player.cast.settings.CastPreference;
import com.beeecorptv.ui.settings.SettingsActivity;
import com.beeecorptv.ui.viewmodels.MovieDetailViewModel;
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.GlideApp;
import com.beeecorptv.util.Tools;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.ads.UnityAdsImplementation;
import com.vungle.warren.AdConfig;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


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


public class MovieNotificationLaunchActivity extends AppCompatActivity {


    private MaxRewardedAd maxRewardedAd;
    private RewardedAd rewardedAd;
    Random random;
    @Inject ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;
    private MenuItem mediaRouteMenuItem;
    private MenuItem mQueueMenuItem;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastSession mCastSession;
    private CountDownTimer mCountDownTimer;
    LayoutEpisodeNotifcationBinding binding;
    private int qualitySelected;
    private StartAppAd startAppAd;
    boolean isLoading;
    @Inject
    SettingsManager settingsManager;

    @Inject
    AuthManager authManager;

    @Inject
    TokenManager tokenManager;


    @Inject
    MediaRepository mediaRepository;

    private History history;
    private String mediaGenre;
    private String externalId;
    private Media media;


    private CastStateListener mCastStateListener;
    private CastContext mCastContext;
    private final SessionManagerListener<CastSession> mSessionManagerListener =
            new MySessionManagerListener();

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

            Toast.makeText(MovieNotificationLaunchActivity.this, getString(R.string.unable_cast), Toast.LENGTH_SHORT).show();
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

        binding = DataBindingUtil.setContentView(this, R.layout.layout_episode_notifcation);

        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);

        // ViewModel to cache, retrieve data for MovieDetailsActivity
        movieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel.class);

        mCastStateListener = newState -> {
            if (newState != CastState.NO_DEVICES_AVAILABLE) {
                showIntroductoryOverlay();
            }
        };
        mCastContext = CastContext.getSharedInstance(this);

        Intent intent = getIntent();
        media = intent.getParcelableExtra(ARG_MOVIE);


        if (authManager.getUserInfo().getPremuim() != 1 ) {

            onInitRewards();
        }

        binding.closeMediaEnded.setOnClickListener(v -> onBackPressed());


        new Handler(Looper.getMainLooper()).postDelayed(binding.cardView::performClick,500);

        binding.cardView.setOnClickListener(v -> mCountDownTimer = new CountDownTimer(5000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                binding.textViewVideoTimeRemaining.setText(UPNEXT + millisUntilFinished / 1000 + " s");

            }

            @Override
            public void onFinish() {

                binding.miniPlay.performClick();

            }


        }.start());

        mediaRepository.getMovie(media.getId(), settingsManager.getSettings().getApiKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(@NotNull Media movieDetail) {

                        onLoadSerieEpisodeInfo(movieDetail);
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

    private void onInitRewards() {

        // Initialize the AppNext Ads SDK.
        Appnext.init(this);

        Appodeal.initialize(MovieNotificationLaunchActivity.this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO );

        IronSource.init(this, settingsManager.getSettings().getIronsourceAppKey(),IronSource.AD_UNIT.REWARDED_VIDEO);

        String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();

        if (getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

            maxRewardedAd = MaxRewardedAd.getInstance( settingsManager.getSettings().getApplovinRewardUnitid(), this);
            maxRewardedAd.loadAd();

        }
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
            intent = new Intent(MovieNotificationLaunchActivity.this, CastPreference.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_show_queue) {
            intent = new Intent(MovieNotificationLaunchActivity.this, QueueListViewActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        return mCastContext.onDispatchVolumeKeyEventBeforeJellyBean(event)
                || super.dispatchKeyEvent(event);
    }

    @SuppressLint("SetTextI18n")
    private void onLoadSerieEpisodeInfo(Media movieDetail) {


        binding.ratingBar.setRating(movieDetail.getVoteAverage() / 2);
        binding.viewMovieRating.setText(valueOf(movieDetail.getVoteAverage()));
        binding.textViewVideoRelease.setVisibility(GONE);
        binding.textOverviewLabel.setText(movieDetail.getOverview());

        GlideApp.with(getApplicationContext()).asBitmap().load(movieDetail.getBackdropPath())
                .centerCrop()
                .placeholder(R.drawable.placehoder_episodes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(binding.imageViewMovieNext);

        GlideApp.with(getApplicationContext()).asBitmap().load(movieDetail.getBackdropPath())
                .centerCrop()
                .placeholder(R.drawable.placehoder_episodes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.nextCoverMedia);


        binding.textViewVideoNextName.setText(movieDetail.getTitle());
        binding.textViewVideoNextReleaseDate.setVisibility(GONE);

        binding.progressBar.setVisibility(GONE);
        binding.leftInfo.setVisibility(VISIBLE);

         binding.miniPlay.setOnClickListener(v -> {


             if (movieDetail.getVideos() !=null && !movieDetail.getVideos().isEmpty()) {


                 if (movieDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                     onLoadStream(movieDetail);

                 }  else   if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {

                     onLoadSubscribeDialog(movieDetail);

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


    }


    private void onLoadSubscribeDialog(Media movieDetail) {

        final Dialog dialog = new Dialog(MovieNotificationLaunchActivity.this);
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

                onLoadApplovinAds(movieDetail);

            }else if (getString(R.string.vungle).equals(defaultRewardedNetworkAds)) {

                onLoadVungleAds(movieDetail);

            }else if (getString(R.string.ironsource).equals(defaultRewardedNetworkAds)) {

                onLoadIronsourceAds(movieDetail);

            }else if (getString(R.string.appnext).equals(defaultRewardedNetworkAds)) {

                onLoadAppNextAds(movieDetail);

            }else if (getString(R.string.startapp).equals(defaultRewardedNetworkAds)) {


                onLoadStartAppAds(movieDetail);

            } else if (getString(R.string.unityads).equals(defaultRewardedNetworkAds)) {

                onLoadUnityAds(movieDetail);


            } else if (getString(R.string.admob).equals(defaultRewardedNetworkAds)) {

                onLoadAdmobRewardAds(movieDetail);


            }else if (getString(R.string.appodeal).equals(defaultRewardedNetworkAds)) {

                onLoadAppOdealRewardAds(movieDetail);

            } else if (getString(R.string.facebook).equals(defaultRewardedNetworkAds)) {

                onLoadFaceBookRewardAds(movieDetail);

            }else if (getString(R.string.auto).equals(defaultRewardedNetworkAds)) {

                onLoadAutoRewardAds(movieDetail);

            }


            dialog.dismiss();


        });


        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                dialog.dismiss());



        if (!isFinishing()) {
            dialog.show();
        }
        dialog.getWindow().setAttributes(lp);

    }

    private void onLoadApplovinAds(Media movieDetail) {


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

                onLoadStream(movieDetail);

            }

            @Override
            public void onUserRewarded(MaxAd ad, MaxReward reward) {

            }
        });

    }

    private void onLoadAppNextAds(Media movieDetail) {


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

        mAppNextAdsVideoRewarded.setOnAdClosedCallback(() -> onLoadStream(movieDetail));

        mAppNextAdsVideoRewarded.setOnAdErrorCallback(error -> Toast.makeText(MovieNotificationLaunchActivity.this, ""+error, Toast.LENGTH_SHORT).show());

        // Get callback when the user saw the video until the end (video ended)
        mAppNextAdsVideoRewarded.setOnVideoEndedCallback(() -> {


        });


    }

    private void onLoadVungleAds(Media movieDetail) {

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

                onLoadStream(movieDetail);

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

    private void onLoadAutoRewardAds(Media media) {

        random = new Random();
        int numberOfMethods = 4;

        switch(random.nextInt(numberOfMethods)) {
            case 0:
                onLoadStartAppAds(media);
                break;
            case 1:
                onLoadUnityAds(media);
                break;
            case 2:
                onLoadAppOdealRewardAds(media);
                break;
            case 3:
                onLoadFaceBookRewardAds(media);
                break;
            default:
                onLoadAdmobRewardAds(media);
        }

    }

    private void onLoadAppOdealRewardAds(Media media) {

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

                onLoadStream(media);

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


    private void onLoadFaceBookRewardAds(Media movieDetail) {

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

                onLoadStream(movieDetail);

            }


        };


        facebookInterstitialAd.loadAd(
                facebookInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }

    private void onLoadStartAppAds(Media movieDetail) {

        startAppAd.setVideoListener(() -> onLoadStream(movieDetail));

        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {

                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                DialogHelper.showAdsFailedWarning(MovieNotificationLaunchActivity.this);

            }
        });
    }


    private void onLoadAdmobRewardAds(Media movieDetail) {

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
                        MovieNotificationLaunchActivity.this.loadRewardedAd();
                    }
                });
        rewardedAd.show(
                MovieNotificationLaunchActivity.this,
                rewardItem ->       onLoadStream(movieDetail));
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

                            MovieNotificationLaunchActivity.this.isLoading = false;

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                            MovieNotificationLaunchActivity.this.isLoading = false;
                            MovieNotificationLaunchActivity.this.rewardedAd = rewardedAd;
                        }
                    });
        }

    }


    private void onLoadUnityAds(Media movieDetail) {


        if (UnityAdsImplementation.isReady()) {

            UnityAds.show (MovieNotificationLaunchActivity.this, settingsManager.getSettings().getUnityRewardPlacementId(), new IUnityAdsShowListener() {
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

                    onLoadStream(movieDetail);
                }
            });
        }

    }



    private void onLoadIronsourceAds(Media movieDetail) {


        IronSource.showRewardedVideo(settingsManager.getSettings().getIronsourceRewardPlacementName());

        IronSource.setRewardedVideoListener(new RewardedVideoListener() {
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

                onLoadStream(movieDetail);

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

    private void onLoadStream(Media movieDetail) {

        this.externalId = movieDetail.getImdbExternalId();
        for (Genre genre : movieDetail.getGenres()) {
           this.mediaGenre = genre.getName();

        }

        if (settingsManager.getSettings().getServerDialogSelection() == 1) {
            String[] charSequence = new String[movieDetail.getVideos().size()];
            for (int i = 0; i<movieDetail.getVideos().size(); i++) {
                charSequence[i] = String.valueOf(movieDetail.getVideos().get(i).getServer());

            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
            builder.setTitle(getString(R.string.select_qualities));
            builder.setCancelable(true);
            builder.setItems(charSequence, (dialogInterface, wich) -> {


                if (movieDetail.getVideos().get(wich).getEmbed() == 1)  {


                    startStreamFromEmbed(movieDetail.getVideos().get(wich).getLink());


                } else if (movieDetail.getVideos().get(wich).getSupportedHosts() == 1){


                    startSupportedHostsStream(movieDetail,wich);


                } else {

                    if (mCastSession !=null && mCastSession.isConnected()) {

                        Tools.streamFromChromcast(this,movieDetail,movieDetail.getGenres().get(wich).getName(),movieDetail.getVideos().get(wich).getLink(),
                                binding.miniPlay);

                    } else   if (settingsManager.getSettings().getVlc() == 1) {

                        startStreamNormalLink(movieDetail,wich, movieDetail.getVideos().get(wich).getLink(),movieDetail.getVideos().get(wich));


                    } else {

                        startStreamFromDialog(movieDetail, wich, externalId, movieDetail.getVideos().get(wich).getLink(), movieDetail.getVideos().get(wich));
                    }

                }

            });

            builder.show();

        }else {

            if (movieDetail.getVideos().get(0).getEmbed() == 1) {

                startStreamFromEmbed(movieDetail.getVideos().get(0).getLink());


            } else if (movieDetail.getVideos().get(0).getSupportedHosts() == 1){


                startSupportedHostsStream(movieDetail,0);


            }else {

                if (mCastSession !=null && mCastSession.isConnected()) {


                    Tools.streamFromChromcast(this,movieDetail,movieDetail.getGenres().get(0).getName(),movieDetail.getVideos().get(0).getLink()
                            ,binding.miniPlay);

                }else {

                    startStreamFromDialog(movieDetail,0, externalId, movieDetail.getVideos().get(0).getLink(), movieDetail.getVideos().get(0));

                }

            }

        }
    }

    private void startStreamNormalLink(Media movieDetail, int wich, String url, MediaStream mediaStream) {


        final Dialog dialog = new Dialog(MovieNotificationLaunchActivity.this);
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

        if (!isFinishing()) {
          dialog.show();
        }

        dialog.getWindow().setAttributes(lp);

        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

       dialog.dismiss());



        if (!isFinishing()) {
            dialog.show();
        }
        dialog.getWindow().setAttributes(lp);


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
        history.setMediaGenre(mediaGenre);
        history.setVoteAverage(movieDetail.getVoteAverage());
        movieDetailViewModel.addhistory(history);

    }


    private void startStreamFromEmbed(String link) {

        Intent intent = new Intent(this, EmbedActivity.class);
        intent.putExtra(Constants.MOVIE_LINK, link);
        startActivity(intent);
        finish();
    }


    private void startSupportedHostsStream(Media movieDetail, int wich) {

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


                        final AlertDialog.Builder builder = new AlertDialog.Builder(MovieNotificationLaunchActivity.this, R.style.MyAlertDialogTheme);
                        builder.setTitle(getString(R.string.select_qualities));
                        builder.setCancelable(true);
                        builder.setItems(name, (dialogInterface, i) -> {


                            if (mCastSession !=null && mCastSession.isConnected()) {

                                Tools.streamFromChromcast(MovieNotificationLaunchActivity.this,movieDetail,movieDetail.getGenres().get(wich).getName()
                                        ,movieDetail.getVideos().get(wich).getLink(),binding.miniPlay);

                            }else {


                                if (settingsManager.getSettings().getVlc() == 1) {


                                    startStreamNormalLink(movieDetail,wich,vidURL.get(i).getUrl(), movieDetail.getVideos().get(wich));


                                } else {

                                    startStreamFromDialog(movieDetail, wich, externalId, vidURL.get(i).getUrl(), movieDetail.getVideos().get(wich));
                                }


                            }

                        });

                        if (!isFinishing()) {
                            builder.show();
                        }



                    }else
                        Toast.makeText(MovieNotificationLaunchActivity.this, "NULL", Toast.LENGTH_SHORT).show();

                }else {

                    startStreamFromDialog(movieDetail, wich, externalId, vidURL.get(0).getUrl(), movieDetail.getVideos().get(wich));
                }

            }

            @Override
            public void onError() {

               //
            }
        });

        easyPlexSupportedHosts.find(movieDetail.getVideos().get(wich).getLink());

        finish();
    }


    @Override
    protected void onDestroy() {


        if (startAppAd !=null) {

            startAppAd = null;
        }

        if (mCountDownTimer !=null) {

          mCountDownTimer.cancel();
        }

        Appodeal.destroy(Appodeal.BANNER);
        Appodeal.destroy(Appodeal.INTERSTITIAL);
        Appodeal.destroy(Appodeal.REWARDED_VIDEO);
        Glide.get(this).clearMemory();
        binding = null;
        super.onDestroy();


    }


    private void onLoadMainPlayerStreamYoutube(Media movieDetail, int wich, String downloadUrl, MediaStream mediaStream) {

        Tools.startMainStream(this,movieDetail, downloadUrl,"",mediaGenre, mediaStream, settingsManager);
        history = new History(movieDetail.getId(), movieDetail.getId(), movieDetail.getPosterPath(), movieDetail.getTitle(), movieDetail.getBackdropPath(), null);
        history.setUserHistoryId(authManager.getUserInfo().getId());
        history.setLink(movieDetail.getVideos().get(wich).getLink());
        history.setType("0");
        history.setTmdbId(movieDetail.getId());
        history.setExternalId(externalId);
        history.setPremuim(movieDetail.getPremuim());
        history.setHasrecap(movieDetail.getHasrecap());
        history.setSkiprecapStartIn(movieDetail.getSkiprecapStartIn());
        history.setMediaGenre(mediaGenre);
        history.setVoteAverage(movieDetail.getVoteAverage());
        movieDetailViewModel.addhistory(history);
        finish();

    }

    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {


            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                        MovieNotificationLaunchActivity.this, mediaRouteMenuItem)
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }



}