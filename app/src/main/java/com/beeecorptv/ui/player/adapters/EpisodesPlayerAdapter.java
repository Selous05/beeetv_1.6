package com.beeecorptv.ui.player.adapters;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.appnext.ads.interstitial.Interstitial;
import com.appnext.base.Appnext;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.R;
import com.beeecorptv.data.model.episode.Episode;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.media.Resume;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.RowPlayerEpisodesBinding;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.ui.player.activities.EmbedActivity;
import com.beeecorptv.ui.settings.SettingsActivity;
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.Tools;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


/**
 * Adapter for Serie Episodes.
 *
 * @author Yobex.
 */
public class EpisodesPlayerAdapter extends RecyclerView.Adapter<EpisodesPlayerAdapter.EpisodesPlayerViewHolder> {


    private MaxInterstitialAd maxInterstitialAd;
    private List<Episode> castList;
    final String currenserieid;
    final String currentSeasons;
    final String seasonsid;
    final String currentSeasonsNumber;
    MediaModel mMediaModel;
    final SharedPreferences sharedPreferences;
    final ClickDetectListner clickDetectListner;
    private final AuthManager authManager;
    private final SettingsManager settingsManager;
    private StartAppAd startAppAd;
    private final TokenManager tokenManager;
    private boolean adsLaunched = false;
    private final MediaRepository mediaRepository;
    private int qualitySelected;
    private final Context context;


    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;

    public EpisodesPlayerAdapter(String serieid , String seasonsid, String seasonsidpostion, String currentseason,
                                 ClickDetectListner clickDetectListner,AuthManager authManager,SettingsManager settingsManager,TokenManager tokenManager,
                                 SharedPreferences sharedPreferences,MediaRepository mediaRepository,Context context) {

        this.currenserieid = serieid;
        this.currentSeasons = seasonsid;
        this.seasonsid = seasonsidpostion;
        this.currentSeasonsNumber = currentseason;
        this.clickDetectListner = clickDetectListner;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.tokenManager =tokenManager;
        this.sharedPreferences = sharedPreferences;
        this.mediaRepository = mediaRepository;
        this.context = context;

    }

    public void addSeasons(List<Episode> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodesPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowPlayerEpisodesBinding binding = RowPlayerEpisodesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new EpisodesPlayerAdapter.EpisodesPlayerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesPlayerViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }


    class EpisodesPlayerViewHolder extends RecyclerView.ViewHolder {


        private final RowPlayerEpisodesBinding binding;

        EpisodesPlayerViewHolder(@NonNull RowPlayerEpisodesBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        void onBind(final int position) {

            final Episode episode = castList.get(position);

            Tools.onLoadMediaCoverEpisode(context, binding.epcover, episode.getStillPath());

            if (!adsLaunched) {

                createAndLoadRewardedAd();

            }

            binding.eptitle.setText(episode.getName());
            binding.epnumber.setText(episode.getEpisodeNumber() + " -");
            binding.epoverview.setText(episode.getOverview());


            if (settingsManager.getSettings().getResumeOffline() == 1) {


                mediaRepository.hasResume(episode.getId()).observe((EasyPlexMainPlayer) context, resumeInfo -> {

                    if (resumeInfo != null) {

                        if (resumeInfo.getTmdb() != null && resumeInfo.getResumePosition() != null

                                && resumeInfo.getTmdb().equals(String.valueOf(episode.getId())) && Tools.id(context).equals(resumeInfo.getDeviceId())) {


                            double d = resumeInfo.getResumePosition();

                            double moveProgress = d * 100 / resumeInfo.getMovieDuration();

                            binding.resumeProgressBar.setVisibility(View.VISIBLE);
                            binding.resumeProgressBar.setProgress((int) moveProgress);

                            binding.timeRemaning.setText(Tools.getProgressTime((resumeInfo.getMovieDuration() - resumeInfo.getResumePosition()), true));



                        } else {

                            binding.resumeProgressBar.setProgress(0);
                            binding.resumeProgressBar.setVisibility(GONE);
                            binding.timeRemaning.setVisibility(GONE);

                        }

                    } else {


                        binding.resumeProgressBar.setProgress(0);
                        binding.resumeProgressBar.setVisibility(GONE);
                        binding.timeRemaning.setVisibility(GONE);

                    }

                });


            } else {


                mediaRepository.getResumeById(String.valueOf(episode.getId()), settingsManager.getSettings().getApiKey())
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


                                if (resume.getTmdb() != null && resume.getResumePosition() != null

                                        && resume.getTmdb().equals(String.valueOf(episode.getId())) && Tools.id(context).equals(resume.getDeviceId())) {


                                    double d = resume.getResumePosition();

                                    double moveProgress = d * 100 / resume.getMovieDuration();

                                    binding.resumeProgressBar.setVisibility(View.VISIBLE);
                                    binding.resumeProgressBar.setProgress((int) moveProgress);

                                    binding.timeRemaning.setText(Tools.getProgressTime((resume.getMovieDuration() - resume.getResumePosition()), true));


                                } else {

                                    binding.resumeProgressBar.setProgress(0);
                                    binding.resumeProgressBar.setVisibility(GONE);
                                    binding.timeRemaning.setVisibility(GONE);

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


            binding.epLayout.setOnClickListener(v -> {


                if (episode.getVideos().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                } else {


                    if (((EasyPlexMainPlayer) context).getPlayerController().isMediaPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {


                        onLoadEpisodeStart(episode, position, context);


                    } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 1 && ((EasyPlexMainPlayer) context).getPlayerController().isMediaPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                        onLoadSubscribeDialog(episode, position, context);

                    } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 0 && ((EasyPlexMainPlayer) context).getPlayerController().isMediaPremuim() == 0) {


                        onLoadEpisodeStart(episode, position, context);


                    } else if (authManager.getUserInfo().getPremuim() == 1 && ((EasyPlexMainPlayer) context).getPlayerController().isMediaPremuim() == 0) {


                        onLoadEpisodeStart(episode, position, context);


                    } else {

                        DialogHelper.showPremuimWarning(context);

                    }

                }

            });

        }


        private void onLoadSubscribeDialog(Episode media, int position, Context context) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.watch_to_unlock);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {


                String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultNetworkPlayer();


                if (context.getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

                    onLoadApplovinAds(media,position,context);

                } else if ("Vungle".equals(defaultRewardedNetworkAds)) {

                    onLoadVungleAds(media, position, context);

                }else if ("Ironsource".equals(defaultRewardedNetworkAds)) {

                    onLoadIronsourceAds(media, position, context);

                }else if ("AppNext".equals(defaultRewardedNetworkAds)) {

                    onLoadAppNextAds(media, position, context);

                }else if ("StartApp".equals(defaultRewardedNetworkAds)) {

                    onLoadStartAppAds(media, position, context);

                } else if ("UnityAds".equals(defaultRewardedNetworkAds)) {

                    onLoadUnityAds(media, position, context);


                } else if ("Admob".equals(defaultRewardedNetworkAds)) {


                    onLoadAdmobRewardAds(media, position, context);


                } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                    onLoadFaceBookRewardAds(media, position, context);

                } else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media, position, context);

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

        private void onLoadApplovinAds(Episode episode, int position, Context context) {

            maxInterstitialAd.showAd();
            maxInterstitialAd.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {

                    //
                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                    //
                }

                @Override
                public void onAdHidden(MaxAd ad) {

                    onLoadEpisodeStart(episode, position, context);
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
            });
        }

        private void onLoadAppNextAds(Episode episode, int position, Context context) {

            Interstitial interstitialAppNext = new Interstitial(EpisodesPlayerAdapter.this.context, settingsManager.getSettings().getAppnextPlacementid());

            interstitialAppNext.loadAd();

            interstitialAppNext.showAd();

            // Get callback for ad loaded
            interstitialAppNext.setOnAdLoadedCallback((bannerId, creativeType) -> {

            });// Get callback for ad opened
            interstitialAppNext.setOnAdOpenedCallback(() -> {

            });// Get callback for ad clicked
            interstitialAppNext.setOnAdClickedCallback(() -> {

            });// Get callback for ad closed
            interstitialAppNext.setOnAdClosedCallback(() ->  onLoadEpisodeStart(episode, position, context));
            interstitialAppNext.setOnAdErrorCallback(error -> {

            });
        }

        private void onLoadIronsourceAds(Episode episode, int position, Context context) {

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

                    onLoadEpisodeStart(episode, position, context);
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

                @Override
                public void onInterstitialAdShowSucceeded() {

                    //
                }
            });
        }

        private void onLoadVungleAds(Episode episode, int position, Context context) {

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

                    onLoadEpisodeStart(episode, position, context);
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


        private void onLoadStartAppAds(Episode episode, int position, Context context) {

            startAppAd.showAd(new AdDisplayListener() {
                @Override
                public void adHidden(Ad ad) {
                    //
                }

                @Override
                public void adDisplayed(Ad ad) {

                    onLoadEpisodeStart(episode, position, context);
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


        private void onLoadUnityAds(Episode episode, int position, Context context) {

            if (UnityAdsImplementation.isReady()) {
                UnityAds.show((EasyPlexMainPlayer) context, "rewardedVideo", new IUnityAdsShowListener() {
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

            IUnityAdsListener iUnityAdsListener = new IUnityAdsListener() {

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


                    onLoadEpisodeStart(episode, position, context);


                }

                @Override
                public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                    DialogHelper.showAdsFailedWarning(context);

                }
            };

            // Add the listener to the SDK:
            UnityAds.addListener(iUnityAdsListener);

        }


        private void onLoadAdmobRewardAds(Episode episode, int position, Context context) {


            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.interstitial.InterstitialAd.load(
                    context,
                    settingsManager.getSettings().getAdUnitIdInterstitial(),
                    adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {


                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;

                            mInterstitialAd.show((EasyPlexMainPlayer) context);
                            interstitialAd.setFullScreenContentCallback(
                                    new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            // Called when fullscreen content is dismissed.
                                            // Make sure to set your reference to null so you don't
                                            // show it a second time.
                                            mInterstitialAd = null;
                                            Timber.d("The ad was dismissed.");

                                            onLoadEpisodeStart(episode, position, context);
                                        }


                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            // Called when fullscreen content is shown.
                                            Timber.d("The ad was shown.");


                                        }
                                    });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            mInterstitialAd = null;

                        }
                    });
        }


        private void onLoadFaceBookRewardAds(Episode episode, int position, Context context) {

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

                    onLoadEpisodeStart(episode, position, context);

                }


            };


            facebookInterstitialAd.loadAd(
                    facebookInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());


        }


        private void onLoadAppOdealRewardAds(Episode episode, int position, Context context) {

            Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
                @Override
                public void onInterstitialLoaded(boolean b) {

                    Appodeal.show((EasyPlexMainPlayer) context, Appodeal.INTERSTITIAL);

                }

                @Override
                public void onInterstitialFailedToLoad() {

                    //

                }

                @Override
                public void onInterstitialShown() {

                    //

                }

                @Override
                public void onInterstitialShowFailed() {

                    //

                }

                @Override
                public void onInterstitialClicked() {

                    //

                }

                @Override
                public void onInterstitialClosed() {

                    onLoadEpisodeStart(episode, position, context);


                }

                @Override
                public void onInterstitialExpired() {

                    //

                }
            });


        }


        private void createAndLoadRewardedAd() {


            if ("AppLovin".equals(settingsManager.getSettings().getDefaultNetworkPlayer())) {

                maxInterstitialAd = new MaxInterstitialAd(settingsManager.getSettings().getApplovinInterstitialUnitid(), (EasyPlexMainPlayer) context );
                maxInterstitialAd.loadAd();
            }


            if ("AppNext".equals(settingsManager.getSettings().getDefaultNetworkPlayer())) {
                // Initialize the AppNext Ads SDK.
                Appnext.init(context);
            }

            if ("Ironsource".equals(settingsManager.getSettings().getDefaultNetworkPlayer())
                    && settingsManager.getSettings().getIronsourceAppKey() !=null ) {

                IronSource.init((EasyPlexMainPlayer) context, settingsManager.getSettings().getIronsourceAppKey(),
                        IronSource.AD_UNIT.INTERSTITIAL);

            }

            if ("StartApp".equals(settingsManager.getSettings().getDefaultNetworkPlayer()) && settingsManager.getSettings().getStartappId() !=null ) {


                if (settingsManager.getSettings().getStartappId() !=null) {

                    startAppAd = new StartAppAd(context);
                }



            } else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds()) && settingsManager.getSettings().getAdUnitIdAppodealRewarded() !=null) {

                Appodeal.initialize((EasyPlexMainPlayer) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.INTERSTITIAL);

            }

            adsLaunched = true;
        }


            @SuppressLint("StaticFieldLeak")
            private void onLoadEpisodeStart (Episode episode,int position, Context context){


                clickDetectListner.onEpisodeClicked(true);

                ((EasyPlexMainPlayer) (context)).mediaType();
                ((EasyPlexMainPlayer) (context)).updateResumePosition();


                if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                    String[] charSequence = new String[episode.getVideos().size()];

                    for (int i = 0; i<episode.getVideos().size(); i++) {
                        charSequence[i] = episode.getVideos().get(i).getServer() + " - " + episode.getVideos().get(i).getLang();

                    }

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                    builder.setTitle(context.getString(R.string.select_qualities));
                    builder.setCancelable(true);
                    builder.setItems(charSequence, (dialogInterface, wich) -> {


                        if (episode.getVideos().get(wich).getEmbed() == 1) {

                            onLoadStreamEmbed(episode.getVideos().get(wich).getLink());

                        } else if (episode.getVideos().get(wich).getSupportedHosts() == 1) {


                            EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);
                            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                                @Override
                                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                    if (multipleQuality) {
                                        if (vidURL != null) {

                                            CharSequence[] name = new CharSequence[vidURL.size()];

                                            for (int i = 0; i < vidURL.size(); i++) {
                                                name[i] = vidURL.get(i).getQuality();
                                            }


                                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                            builder.setTitle(context.getString(R.string.select_qualities));
                                            builder.setCancelable(true);
                                            builder.setItems(name, (dialogInterface, i) -> reuseEpisode(episode, vidURL.get(i).getUrl(),position));

                                            builder.show();


                                        } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                    } else {

                                        reuseEpisode(episode, vidURL.get(0).getUrl(), position);
                                    }

                                }

                                @Override
                                public void onError() {

                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });

                            easyPlexSupportedHosts.find(episode.getVideos().get(wich).getLink());


                        } else {


                            reuseEpisode(episode, episode.getVideos().get(wich).getLink(), position);

                        }



                    });

                    builder.show();

                }else {


                    String episodeStreamLink = episode.getVideos().get(0).getLink();

                    if (episode.getVideos().get(0).getEmbed() == 1) {

                        onLoadStreamEmbed(episodeStreamLink);

                    } else if (episode.getVideos().get(0).getSupportedHosts() == 1) {


                        EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);
                        easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                            @Override
                            public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                if (multipleQuality) {
                                    if (vidURL != null) {

                                        CharSequence[] name = new CharSequence[vidURL.size()];

                                        for (int i = 0; i < vidURL.size(); i++) {
                                            name[i] = vidURL.get(i).getQuality();
                                        }


                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                        builder.setTitle(context.getString(R.string.select_qualities));
                                        builder.setCancelable(true);
                                        builder.setItems(name, (dialogInterface, wich) -> {

                                            reuseEpisode(episode, vidURL.get(wich).getUrl(),position);


                                        });

                                        builder.show();


                                    } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {
                                    reuseEpisode(episode, vidURL.get(0).getUrl(), position);
                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(episode.getVideos().get(0).getLink());


                    } else {

                        reuseEpisode(episode, episode.getVideos().get(0).getLink(), position);

                    }

                }

            }
        private void onLoadStreamEmbed(String episodeStreamLink) {

            Intent intent = new Intent(context, EmbedActivity.class);
            intent.putExtra(Constants.MOVIE_LINK, episodeStreamLink);
            context.startActivity(intent);
        }


        private void reuseEpisode(Episode episode, String url, int position) {

            String tvSeasonsId = seasonsid;
            Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
            String currentEpisodeName = episode.getName();
            String currentEpisodeNumber = String.valueOf(episode.getId());
            String currentSeason = currentSeasons;
            String currentTvSeasonsNumber = currentSeasonsNumber;
            String currentEpisodeTmdb = String.valueOf(episode.getId());
            String artwork = episode.getStillPath();
            String type = "1";
            String currentQuality = episode.getVideos().get(0).getServer();
            String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
            int hls = episode.getVideos().get(0).getHls();
            float voteAverage = Float.parseFloat(episode.getVoteAverage());


            mMediaModel = MediaModel.media(currenserieid, null, currentQuality, type, name, url, artwork, null, currentEpisode
                    , currentSeason, currentEpisodeTmdb,
                    tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber, position,
                    currentEpisodeNumber, ((EasyPlexMainPlayer) context).getPlayerController().isMediaPremuim(), hls, null, ((EasyPlexMainPlayer) context).getPlayerController().getCurrentExternalId()
                    , ((EasyPlexMainPlayer) context).getPlayerController().getMediaCoverHistory(),
                    episode.getHasrecap(),
                    episode.getSkiprecapStartIn(),
                    ((EasyPlexMainPlayer) context).getPlayerController().getMediaGenre(),((EasyPlexMainPlayer) context).getPlayerController().getSerieName(),voteAverage);

            ((EasyPlexMainPlayer) context).playNext(mMediaModel);
        }

        }




        @Override
        public void onViewDetachedFromWindow(@NonNull EpisodesPlayerViewHolder holder) {
            super.onViewDetachedFromWindow(holder);

            adsLaunched = false;
            mInterstitialAd = null;
            Appodeal.destroy(Appodeal.BANNER);
            Appodeal.destroy(Appodeal.INTERSTITIAL);
            Appodeal.destroy(Appodeal.REWARDED_VIDEO);
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);

            adsLaunched = false;
            mInterstitialAd = null;
            Appodeal.destroy(Appodeal.BANNER);
            Appodeal.destroy(Appodeal.INTERSTITIAL);
            Appodeal.destroy(Appodeal.REWARDED_VIDEO);
        }

    }
