package com.beeecorptv.ui.home.adapters;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.util.Constants.DEFAULT_WEBVIEW_ADS_RUNNING;
import static com.beeecorptv.util.Constants.PLAYER_HEADER;
import static com.beeecorptv.util.Constants.PLAYER_USER_AGENT;
import static com.beeecorptv.util.Constants.SERVER_BASE_URL;
import static com.google.android.gms.cast.MediaStatus.REPEAT_MODE_REPEAT_OFF;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.appnext.ads.fullscreen.RewardedVideo;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.MovieResponse;
import com.beeecorptv.data.model.episode.LatestEpisodes;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.substitles.ExternalID;
import com.beeecorptv.data.repository.AnimeRepository;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.ItemLastestEpisodesBinding;
import com.beeecorptv.ui.animes.AnimeDetailsActivity;
import com.beeecorptv.ui.base.BaseActivity;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.ui.player.activities.EasyPlexPlayerActivity;
import com.beeecorptv.ui.player.activities.EmbedActivity;
import com.beeecorptv.ui.player.cast.ExpandedControlsActivity;
import com.beeecorptv.ui.player.cast.queue.QueueDataProvider;
import com.beeecorptv.ui.player.cast.utils.Utils;
import com.beeecorptv.ui.settings.SettingsActivity;
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
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.unity3d.ads.IUnityAdsListener;
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
import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Adapter for  Streaming Channels
 *
 * @author Yobex.
 */
public class AnimesWithNewEpisodesAdapter extends RecyclerView.Adapter<AnimesWithNewEpisodesAdapter.StreamingViewHolder> {


    private MaxRewardedAd maxRewardedAd;
    private CountDownTimer mCountDownTimer;
    private boolean webViewLauched = false;
    private IUnityAdsListener iUnityAdsListener;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final int PRELOAD_TIME_S = 2;
    private History history;
    private List<LatestEpisodes> streamingList;
    private Context context;
    private boolean adsLaunched = false;
    private SettingsManager settingsManager;
    private StartAppAd startAppAd;
    private MediaRepository mediaRepository;
    private AnimeRepository animeRepository;
    private AuthManager authManager;
    private TokenManager tokenManager;
    private String mediaGenre;
    private int qualitySelected;
    private RewardedAd mRewardedAd;
    boolean isLoading;
    private String externalId;


    public void addStreaming(Context context, List<LatestEpisodes> castList, SettingsManager settingsManager, MediaRepository mediaRepository, AuthManager authManager, TokenManager tokenManager, AnimeRepository animeRepository) {
        this.streamingList = castList;
        this.context = context;
        this.settingsManager = settingsManager;
        this.mediaRepository = mediaRepository;
        this.authManager = authManager;
        this.tokenManager = tokenManager;
        this.animeRepository = animeRepository;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StreamingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemLastestEpisodesBinding binding = ItemLastestEpisodesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AnimesWithNewEpisodesAdapter.StreamingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamingViewHolder holder, int position) {
        holder.onBind(position);

    }

    @Override
    public int getItemCount() {
        if (streamingList != null) {
            return streamingList.size();
        } else {
            return 0;
        }
    }

    class StreamingViewHolder extends RecyclerView.ViewHolder {

        private final ItemLastestEpisodesBinding binding;


        StreamingViewHolder(@NonNull ItemLastestEpisodesBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(final int position) {

            final LatestEpisodes latestEpisodes = streamingList.get(position);

            if (!adsLaunched) {

                createAndLoadRewardedAd();

                initLoadRewardedAd();

            }


            mediaRepository.getExternalId(String.valueOf(latestEpisodes.getSerieTmdb()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<ExternalID>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull ExternalID externalID) {

                            externalId = externalID.getImdbId();

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

            GlideApp.with(context).asBitmap().load(latestEpisodes.getStillPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .placeholder(R.color.app_background)
                    .into(binding.itemMovieImage);

            binding.infoSerie.setOnClickListener(v -> animeRepository.getAnimeDetails(String.valueOf(latestEpisodes.getId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Media>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Media movieDetail) {

                            Intent intent = new Intent(context, AnimeDetailsActivity.class);
                            intent.putExtra(Constants.ARG_MOVIE, movieDetail);
                            context.startActivity(intent);

                        }


                        @Override
                        public void onError(@NotNull Throwable e) {

                            //
                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    }));


            String name = "S0" + latestEpisodes.getSeasonNumber() + "E" + latestEpisodes.getEpisodeNumber() + " : " + latestEpisodes.getEpisodeName();
            binding.movietitle.setText(latestEpisodes.getName() + " : " + name);
            binding.ratingBar.setRating(latestEpisodes.getVoteAverage() / 2);
            binding.viewMovieRating.setText(String.valueOf(latestEpisodes.getVoteAverage()));

            binding.rootLayout.setOnClickListener(v -> {
                if (latestEpisodes.getLink().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                } else {

                    if (latestEpisodes.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                        onLoadStream(latestEpisodes);


                    }else  if (settingsManager.getSettings().getEnableWebview() == 1) {

                        final Dialog dialog = new Dialog(context);
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
                                onLoadStream(latestEpisodes);
                                webViewLauched = false;

                                if (mCountDownTimer != null) {

                                    mCountDownTimer.cancel();
                                    mCountDownTimer = null;

                                }
                            }

                        }.start();

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);


                    }  else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && latestEpisodes.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                        onLoadSubscribeDialog(latestEpisodes);

                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && latestEpisodes.getPremuim() == 0) {


                        onLoadStream(latestEpisodes);

                    } else if (authManager.getUserInfo().getPremuim() == 1 && latestEpisodes.getPremuim() == 0) {


                        onLoadStream(latestEpisodes);


                    } else {

                        DialogHelper.showPremuimWarning(context);

                    }
                }
            });

        }

        private void onLoadSubscribeDialog(LatestEpisodes media) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_subscribe);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;

            dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {

                String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultNetworkPlayer();


                if (context.getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

                    maxRewardedAd = MaxRewardedAd.getInstance( settingsManager.getSettings().getApplovinRewardUnitid(), (BaseActivity) context );
                    maxRewardedAd.loadAd();

                    onLoadApplovinAds(media);

                }else if ("Vungle".equals(defaultRewardedNetworkAds)) {

                    onLoadVungleAds(media);

                }else if ("Ironsource".equals(defaultRewardedNetworkAds)) {

                    onLoadIronsourceAds(media);

                }else if ("AppNext".equals(defaultRewardedNetworkAds)) {

                    onLoadAppNextAds(media);

                }else  if ("StartApp".equals(defaultRewardedNetworkAds)) {

                    onLoadStartAppAds(media);

                } else if ("UnityAds".equals(defaultRewardedNetworkAds)) {

                    onLoadUnityAds(media);


                } else if ("Admob".equals(defaultRewardedNetworkAds)) {

                onLoadAdmobRewardAds(media);

                } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                    onLoadFaceBookRewardAds(media);

                } else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media);

                } else if ("Auto".equals(defaultRewardedNetworkAds)) {

                    onLoadAutoRewardAds(media);

                }

                dialog.dismiss();


            });


            dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

                context.startActivity(new Intent(context, SettingsActivity.class));

                dialog.dismiss();


            });


            dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

        private void onLoadApplovinAds(LatestEpisodes media) {


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

                    onLoadStream(media);

                }

                @Override
                public void onUserRewarded(MaxAd ad, MaxReward reward) {

                }
            });
        }

        private void onLoadAppNextAds(LatestEpisodes media) {

            RewardedVideo mAppNextAdsVideoRewarded = new RewardedVideo(context, settingsManager.getSettings().getAppnextPlacementid());
            mAppNextAdsVideoRewarded.loadAd();
            mAppNextAdsVideoRewarded.showAd();

            // Get callback for ad loaded
            mAppNextAdsVideoRewarded.setOnAdLoadedCallback((s, appnextAdCreativeType) -> {});

            mAppNextAdsVideoRewarded.setOnAdOpenedCallback(() -> {
            });
            mAppNextAdsVideoRewarded.setOnAdClickedCallback(() -> {

            });

            mAppNextAdsVideoRewarded.setOnAdClosedCallback(() -> onLoadStream(media));

            mAppNextAdsVideoRewarded.setOnAdErrorCallback(error -> Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show());

            // Get callback when the user saw the video until the end (video ended)
            mAppNextAdsVideoRewarded.setOnVideoEndedCallback(() -> {
            });

        }

        private void onLoadIronsourceAds(LatestEpisodes media) {

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
                    onLoadStream(media);
                }

                @Override
                public void onRewardedVideoAdShowFailed(IronSourceError error) {

                    //

                }

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

        private void onLoadVungleAds(LatestEpisodes media) {

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

                // Deprecated
                @Override
                public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {
                    onLoadStream(media);
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

        private void onLoadAutoRewardAds(LatestEpisodes media) {

            Random random = new Random();
            int numberOfMethods = 7;

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
                case 4:
                    onLoadVungleAds(media);
                    break;
                case 5:
                    onLoadAppNextAds(media);
                    break;
                case 6:
                    onLoadIronsourceAds(media);
                    break;
                default:
                    onLoadAdmobRewardAds(media);
            }
        }

        private void onLoadAppOdealRewardAds(LatestEpisodes media) {

            Appodeal.show((BaseActivity) context, Appodeal.REWARDED_VIDEO);

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

        private void onLoadFaceBookRewardAds(LatestEpisodes media) {

            com.facebook.ads.InterstitialAd facebookInterstitialAd = new com.facebook.ads.InterstitialAd(context, settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());

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

                    onLoadStream(media);

                }


            };


            facebookInterstitialAd.loadAd(
                    facebookInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }

        private void onLoadAdmobRewardAds(LatestEpisodes media) {

            if (mRewardedAd == null) {
                Toast.makeText(context, "The rewarded ad wasn't ready yet", Toast.LENGTH_SHORT).show();
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
                            initLoadRewardedAd();
                        }
                    });
            mRewardedAd.show((BaseActivity) context, rewardItem -> onLoadStream(media));
        }

        private void initLoadRewardedAd() {

            if (mRewardedAd == null) {
                isLoading = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(
                        context,
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

        private void onLoadUnityAds(LatestEpisodes media) {

            if (UnityAdsImplementation.isReady()) {
                UnityAds.show((BaseActivity) context, "rewardedVideo", new IUnityAdsShowListener() {
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
                        //
                    }
                });
            }


           iUnityAdsListener = new IUnityAdsListener() {

                @Override
                public void onUnityAdsReady(String s) {

                    //

                }

                @Override
                public void onUnityAdsStart(String s) {

                    //

                }

                @Override
                public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

                    onLoadStream(media);
                    UnityAds.removeListener(iUnityAdsListener);

                }

                @Override
                public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                    //
                }
            };

            // Add the listener to the SDK:
            UnityAds.addListener(iUnityAdsListener);
        }

        private void onLoadStartAppAds(LatestEpisodes media) {


            startAppAd.showAd(new AdDisplayListener() {
                @Override
                public void adHidden(Ad ad) {

                    //
                }

                @Override
                public void adDisplayed(Ad ad) {

                    onLoadStream(media);

                }

                @Override
                public void adClicked(Ad ad) {

                    //
                }

                @Override
                public void adNotDisplayed(Ad ad) {

                    //
                }

            });

        }

        private void onLoadStream(LatestEpisodes media) {
            onLoadStreamOnline(media);
        }


        private void startSupportedHostsStream(LatestEpisodes media, String link) {
            EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);
            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                    if (multipleQuality) {
                        if (vidURL != null) {
                            CharSequence[] names = new CharSequence[vidURL.size()];

                            for (int i = 0; i < vidURL.size(); i++) {
                                names[i] = vidURL.get(i).getQuality();
                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                            builder.setTitle(context.getString(R.string.select_qualities));
                            builder.setCancelable(true);
                            builder.setItems(names, (dialogInterface, wich) -> {



                                CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                        .getCurrentCastSession();
                                if (castSession != null && castSession.isConnected()) {

                                    startStreamCasting(media,vidURL.get(wich).getUrl());

                                }  else if (settingsManager.getSettings().getVlc() == 1) {

                                    final Dialog dialog = new Dialog(context);
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
                                        Tools.streamLatestEpisodeFromVlc(context,vidURL.get(wich).getUrl(),media,settingsManager);
                                        dialog.hide();
                                    });

                                    mxPlayer.setOnClickListener(v12 -> {
                                        Tools.streamLatestEpisodeFromMxPlayer(context,vidURL.get(wich).getUrl(),media,settingsManager);
                                        dialog.hide();

                                    });

                                    webcast.setOnClickListener(v12 -> {

                                        Tools.streamLatestEpisodeFromMxWebcast(context,vidURL.get(wich).getUrl(),media,settingsManager);
                                        dialog.hide();

                                    });

                                    easyplexPlayer.setOnClickListener(v12 -> {
                                        onLoadNormalStream(media, vidURL.get(wich).getUrl());
                                        dialog.hide();


                                    });

                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);

                                    dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                            dialog.dismiss());


                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);


                                } else {

                                    onLoadNormalStream(media, vidURL.get(wich).getUrl());

                                }


                            });

                            builder.show();


                        } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                    } else {


                        onLoadNormalStream(media, vidURL.get(0).getUrl());

                    }

                }

                @Override
                public void onError() {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            easyPlexSupportedHosts.find(link);


        }

        @SuppressLint("StaticFieldLeak")
        private void onLoadStreamOnline(LatestEpisodes media) {

            mediaRepository.getAnimeEpisodeDetails(String.valueOf(media.getAnimeEpisodeId()),settingsManager.getSettings().getApiKey())
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

                                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                builder.setTitle(R.string.source_quality);
                                builder.setCancelable(true);
                                builder.setItems(charSequence, (dialogInterface, wich) -> {


                                    if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader() !=null &&

                                            !movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader().isEmpty()) {

                                        PLAYER_HEADER = movieResponse.getEpisodes().get(0).getVideos().get(wich).getHeader();
                                    }


                                    if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent() !=null &&
                                            !movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent().isEmpty()) {

                                        PLAYER_USER_AGENT = movieResponse.getEpisodes().get(0).getVideos().get(wich).getUseragent();
                                    }

                                    for (Genre genre : media.getGenres()) {
                                        mediaGenre = genre.getName();
                                    }

                                    if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getEmbed() == 1) {

                                        Intent intent = new Intent(context, EmbedActivity.class);
                                        intent.putExtra(Constants.MOVIE_LINK, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());
                                        context.startActivity(intent);

                                    } else if (movieResponse.getEpisodes().get(0).getVideos().get(wich).getSupportedHosts() == 1) {

                                        startSupportedHostsStream(media, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());


                                    } else {

                                        CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                                .getCurrentCastSession();
                                        if (castSession != null && castSession.isConnected()) {

                                            startStreamCasting(media, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());

                                        } else {

                                            if (settingsManager.getSettings().getVlc() == 1) {

                                                final Dialog dialog = new Dialog(context);
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
                                                    Tools.streamLatestEpisodeFromVlc(context, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink(), media, settingsManager);
                                                    dialog.hide();
                                                });

                                                mxPlayer.setOnClickListener(v12 -> {
                                                    Tools.streamLatestEpisodeFromMxPlayer(context, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink(), media, settingsManager);
                                                    dialog.hide();

                                                });

                                                webcast.setOnClickListener(v12 -> {

                                                    Tools.streamLatestEpisodeFromMxWebcast(context, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink(), media, settingsManager);
                                                    dialog.hide();

                                                });

                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                    onLoadNormalStream(media, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());
                                                    dialog.hide();


                                                });

                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);

                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                        dialog.dismiss());


                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);


                                            } else {

                                                onLoadNormalStream(media, movieResponse.getEpisodes().get(0).getVideos().get(wich).getLink());

                                            }


                                        }


                                    }


                                });

                                builder.show();

                            } else {



                                if (media.getHeader() !=null &&

                                        !media.getHeader().isEmpty()) {

                                    PLAYER_HEADER = media.getHeader();
                                }


                                if (media.getUseragent() !=null &&
                                        !media.getUseragent().isEmpty()) {

                                    PLAYER_USER_AGENT = media.getUseragent();
                                }

                                String videourl = media.getLink();


                                for (Genre genre : media.getGenres()) {
                                    mediaGenre = genre.getName();
                                }

                                if (media.getEmbed().equals("1")) {

                                    Intent intent = new Intent(context, EmbedActivity.class);
                                    intent.putExtra(Constants.MOVIE_LINK, videourl);
                                    context.startActivity(intent);

                                } else if (media.getSupportedHosts() == 1) {

                                    startSupportedHostsStream(media, movieResponse.getEpisodes().get(0).getVideos().get(0).getLink());


                                } else {


                                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                            .getCurrentCastSession();
                                    if (castSession != null && castSession.isConnected()) {

                                        startStreamCasting(media, media.getLink());

                                    } else {


                                        if (settingsManager.getSettings().getVlc() == 1) {

                                            final Dialog dialog = new Dialog(context);
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
                                                Tools.streamLatestEpisodeFromVlc(context, media.getLink(), media, settingsManager);
                                                dialog.hide();
                                            });

                                            mxPlayer.setOnClickListener(v12 -> {
                                                Tools.streamLatestEpisodeFromMxPlayer(context, media.getLink(), media, settingsManager);
                                                dialog.hide();

                                            });

                                            webcast.setOnClickListener(v12 -> {

                                                Tools.streamLatestEpisodeFromMxWebcast(context, media.getLink(), media, settingsManager);
                                                dialog.hide();

                                            });

                                            easyplexPlayer.setOnClickListener(v12 -> {

                                                onLoadNormalStream(media, media.getLink());
                                                dialog.hide();


                                            });

                                            dialog.show();
                                            dialog.getWindow().setAttributes(lp);

                                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                    dialog.dismiss());


                                            dialog.show();
                                            dialog.getWindow().setAttributes(lp);


                                        } else {

                                            onLoadNormalStream(media, media.getLink());
                                        }


                                    }
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

        private void startStreamCasting(LatestEpisodes media, String downloadUrl) {

            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();

            String name = "S0" + media.getSeasonNumber() + "E" + media.getEpisodeNumber() + " : " + media.getEpisodeName();


            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, name);
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, media.getName());

            movieMetadata.addImage(new WebImage(Uri.parse(media.getPosterPath())));
            List<MediaTrack> tracks = new ArrayList<>();

            MediaInfo mediaInfo = new MediaInfo.Builder(downloadUrl)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(movieMetadata)
                    .setMediaTracks(tracks)
                    .build();

            final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            if (remoteMediaClient == null) {
                Timber.tag("TAG").w("showQueuePopup(): null RemoteMediaClient");
                return;
            }
            final QueueDataProvider provider = QueueDataProvider.getInstance(context);
            PopupMenu popup = new PopupMenu(context, binding.cardView);
            popup.getMenuInflater().inflate(
                    provider.isQueueDetached() || provider.getCount() == 0
                            ? R.menu.detached_popup_add_to_queue
                            : R.menu.popup_add_to_queue, popup.getMenu());
            PopupMenu.OnMenuItemClickListener clickListener = menuItem -> {
                QueueDataProvider provider1 = QueueDataProvider.getInstance(context);
                MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo).setAutoplay(
                        true).setPreloadTime(PRELOAD_TIME_S).build();
                MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};
                String toastMessage = null;
                if (provider1.isQueueDetached() && provider1.getCount() > 0) {
                    if ((menuItem.getItemId() == R.id.action_play_now)
                            || (menuItem.getItemId() == R.id.action_add_to_queue)) {
                        MediaQueueItem[] items = Utils
                                .rebuildQueueAndAppend(provider1.getItems(), queueItem);
                        remoteMediaClient.queueLoad(items, provider1.getCount(),
                                REPEAT_MODE_REPEAT_OFF, null);
                    } else {
                        return false;
                    }
                } else {
                    if (provider1.getCount() == 0) {
                        remoteMediaClient.queueLoad(newItemArray, 0,
                                REPEAT_MODE_REPEAT_OFF, null);
                    } else {
                        int currentId = provider1.getCurrentItemId();
                        if (menuItem.getItemId() == R.id.action_play_now) {
                            remoteMediaClient.queueInsertAndPlayItem(queueItem, currentId, null);
                        } else if (menuItem.getItemId() == R.id.action_play_next) {
                            int currentPosition = provider1.getPositionByItemId(currentId);
                            if (currentPosition == provider1.getCount() - 1) {
                                //we are adding to the end of queue
                                remoteMediaClient.queueAppendItem(queueItem, null);
                            } else {
                                int nextItemId = provider1.getItem(currentPosition + 1).getItemId();
                                remoteMediaClient.queueInsertItems(newItemArray, nextItemId, null);
                            }
                            toastMessage = context.getString(
                                    R.string.queue_item_added_to_play_next);
                        } else if (menuItem.getItemId() == R.id.action_add_to_queue) {
                            remoteMediaClient.queueAppendItem(queueItem, null);
                            toastMessage = context.getString(R.string.queue_item_added_to_queue);
                        } else {
                            return false;
                        }
                    }
                }
                if (menuItem.getItemId() == R.id.action_play_now) {
                    Intent intent = new Intent(context, ExpandedControlsActivity.class);
                    context.startActivity(intent);
                }
                if (!TextUtils.isEmpty(toastMessage)) {
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
                }
                return true;
            };
            popup.setOnMenuItemClickListener(clickListener);
            popup.show();
        }

        private void onLoadNormalStream(LatestEpisodes media, String url) {

            for (Genre genre : media.getGenres()) {
                mediaGenre = genre.getName();
            }

            int seasondbId = media.getAnimeSeasonId();
            String currentep = String.valueOf(media.getEpisodeNumber());
            String currentepimdb = String.valueOf(media.getAnimeEpisodeId());
            String currentepname = media.getEpisodeName();
            String artwork = media.getStillPath();
            String type = "anime";
            String currentquality = media.getServer();
            String name = "S0" + media.getSeasonNumber() + "E" + media.getEpisodeNumber() + " : " + media.getEpisodeName();
            float voteAverage = media.getVoteAverage();


            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                    MediaModel.media(String.valueOf(media.getId()),
                            null,
                            currentquality, type, name, url, artwork,
                            null, Integer.parseInt(currentep)
                            , String.valueOf(media.getSeasonNumber()), currentepimdb, String.valueOf(seasondbId),
                            currentepname,
                            media.getSeasonsName(), 0,
                            String.valueOf(media.getAnimeEpisodeId()), media.getPremuim(), media.getHls(),
                            null, externalId, media.getPosterPath(), media.getHasrecap(), media.getSkiprecapStartIn(), mediaGenre, media.getName(), voteAverage));
            context.startActivity(intent);


            history = new History(String.valueOf(media.getId()), currentepimdb, media.getStillPath(), name, "", "");
            history.setVoteAverage(voteAverage);
            history.setSerieName(media.getName());
            history.setPosterPath(media.getPosterPath());
            history.setTitle(name);
            history.setBackdropPath(media.getStillPath());
            history.setEpisodeNmber(String.valueOf(media.getEpisodeNumber()));
            history.setSeasonsId(String.valueOf(seasondbId));
            history.setSeasondbId(seasondbId);
            history.setType(type);
            history.setTmdbId(String.valueOf(media.getId()));
            history.setPosition(0);
            history.setEpisodeId(currentepimdb);
            history.setEpisodeName(media.getEpisodeName());
            history.setEpisodeTmdb(currentepimdb);
            history.setSerieId(String.valueOf(media.getId()));
            history.setCurrentSeasons(String.valueOf(media.getSeasonNumber()));
            history.setSeasonsId(String.valueOf(seasondbId));
            history.setSeasonsNumber(media.getSeasonsName());
            history.setImdbExternalId(externalId);
            history.setPremuim(media.getPremuim());
            history.setMediaGenre(mediaGenre);

            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        }


        private void onLoadYoutubeStream(LatestEpisodes media, String downloadUrl) {

            for (Genre genre : media.getGenres()) {
                mediaGenre = genre.getName();
            }

            if (settingsManager.getSettings().getVlc() == 1) {

                final Dialog dialog = new Dialog(context);
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
                    Tools.streamLatestEpisodeFromVlc(context,downloadUrl,media,settingsManager);
                    dialog.hide();
                });

                mxPlayer.setOnClickListener(v12 -> {
                    Tools.streamLatestEpisodeFromMxPlayer(context,downloadUrl,media,settingsManager);
                    dialog.hide();

                });
                webcast.setOnClickListener(v12 -> {

                    Tools.streamLatestEpisodeFromMxWebcast(context,downloadUrl,media,settingsManager);
                    dialog.hide();

                });

                easyplexPlayer.setOnClickListener(v12 -> {

                    onStartYoutubeLink(media,downloadUrl);
                    dialog.hide();

                });

                dialog.show();
                dialog.getWindow().setAttributes(lp);

                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                        dialog.dismiss());


                dialog.show();
                dialog.getWindow().setAttributes(lp);


            }else {

                onStartYoutubeLink(media,downloadUrl);
            }


        }

        private void onStartYoutubeLink(LatestEpisodes media, String downloadUrl) {


            int seasondbId = media.getAnimeSeasonId();
            String currentep = String.valueOf(media.getEpisodeNumber());
            String currentepimdb = String.valueOf(media.getAnimeEpisodeId());
            String currentepname = media.getEpisodeName();
            String artwork = media.getStillPath();
            String type = "anime";
            String currentquality = media.getServer();
            String name = "S0" + media.getSeasonNumber() + "E" + media.getEpisodeNumber() + " : " + media.getEpisodeName();
            float voteAverage = media.getVoteAverage();

            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                    MediaModel.media(String.valueOf(media.getId()),
                            null,
                            currentquality, type, name, downloadUrl, artwork,
                            null, Integer.parseInt(currentep)
                            , String.valueOf(media.getSeasonNumber()), currentepimdb, String.valueOf(seasondbId),
                            currentepname,
                            media.getSeasonsName(), 0,
                            String.valueOf(media.getAnimeEpisodeId()), media.getPremuim(), media.getHls(),
                            null, externalId, media.getPosterPath(), media.getHasrecap(), media.getSkiprecapStartIn(), mediaGenre, media.getName(), voteAverage));
            context.startActivity(intent);
            history = new History(String.valueOf(media.getId()), currentepimdb, media.getStillPath(), name, "", "");
            history.setSerieName(media.getName());
            history.setPosterPath(media.getPosterPath());
            history.setTitle(name);
            history.setBackdropPath(media.getStillPath());
            history.setEpisodeNmber(String.valueOf(media.getEpisodeNumber()));
            history.setSeasonsId(String.valueOf(seasondbId));
            history.setSeasondbId(seasondbId);
            history.setType(type);
            history.setTmdbId(String.valueOf(media.getId()));
            history.setPosition(0);
            history.setEpisodeId(currentepimdb);
            history.setEpisodeName(media.getEpisodeName());
            history.setEpisodeTmdb(currentepimdb);
            history.setSerieId(String.valueOf(media.getId()));
            history.setCurrentSeasons(String.valueOf(media.getSeasonNumber()));
            history.setSeasonsId(String.valueOf(seasondbId));
            history.setSeasonsNumber(media.getSeasonsName());
            history.setImdbExternalId(externalId);
            history.setPremuim(media.getPremuim());
            history.setVoteAverage(media.getVoteAverage());
            history.setMediaGenre(mediaGenre);

            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());
        }


        private void createAndLoadRewardedAd() {


            if ("StartApp".equals(settingsManager.getSettings().getDefaultNetworkPlayer())) {


                if (settingsManager.getSettings().getStartappId() != null) {

                    startAppAd = new StartAppAd(context);
                }


            } else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds()) && settingsManager.getSettings().getAdUnitIdAppodealRewarded() != null) {

                Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.REWARDED_VIDEO);

            } else if ("Auto".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {


                if (settingsManager.getSettings().getStartappId() != null) {

                    startAppAd = new StartAppAd(context);
                }


                if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() != null) {

                    Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.REWARDED_VIDEO);

                }

            }

            adsLaunched = true;
        }

    }

}



