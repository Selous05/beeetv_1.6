package com.beeecorptv.ui.player.activities;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.BeeeTvApp.getContext;
import static com.beeecorptv.ui.player.utilities.ExoPlayerLogger.i;
import static com.beeecorptv.ui.player.utilities.ExoPlayerLogger.v;
import static com.beeecorptv.util.Constants.AUTO_PLAY;
import static com.beeecorptv.util.Constants.BRX;
import static com.beeecorptv.util.Constants.E;
import static com.beeecorptv.util.Constants.EP;
import static com.beeecorptv.util.Constants.S0;
import static com.beeecorptv.util.Constants.SEASONS;
import static com.beeecorptv.util.Constants.SPECIALS;
import static com.beeecorptv.util.Constants.SUBSTITLE_LOCATION;
import static com.beeecorptv.util.Constants.SUBSTITLE_SUB_FILENAME_ZIP;
import static com.beeecorptv.util.Constants.UPNEXT;
import static com.beeecorptv.util.Constants.ZIP_FILE_NAME;
import static com.beeecorptv.util.Constants.ZIP_FILE_NAME2;
import static com.beeecorptv.util.Constants.ZIP_FILE_NAME3;
import static com.beeecorptv.util.Tools.EXTRA_HEADERS;
import static com.beeecorptv.util.Tools.HEADERS;
import static com.beeecorptv.util.Tools.POSTER;
import static com.beeecorptv.util.Tools.SECURE_URI;
import static com.beeecorptv.util.Tools.TITLE;
import static com.beeecorptv.util.Tools.USER_AGENT;
import static com.beeecorptv.util.Tools.VIDEOTYPE;
import static com.beeecorptv.util.Tools.VLC_PACKAGE_NAME;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appnext.ads.interstitial.Interstitial;
import com.appnext.base.Appnext;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.easyplex.easyplexsupportedhosts.Utils.Uti;
import com.beeecorptv.R;
import com.beeecorptv.data.datasource.genreslist.AnimesGenresListDataSourceFactory;
import com.beeecorptv.data.datasource.genreslist.MoviesGenresListDataSourceFactory;
import com.beeecorptv.data.datasource.genreslist.SeriesGenresListDataSourceFactory;
import com.beeecorptv.data.datasource.movie.MovieDataSource;
import com.beeecorptv.data.datasource.stream.StreamDataSource;
import com.beeecorptv.data.datasource.stream.StreamingDataSourceFactory;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.MovieResponse;
import com.beeecorptv.data.model.ads.AdMediaModel;
import com.beeecorptv.data.model.ads.AdRetriever;
import com.beeecorptv.data.model.ads.CuePointsRetriever;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.genres.GenresByID;
import com.beeecorptv.data.model.genres.GenresData;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.media.Resume;
import com.beeecorptv.data.model.serie.Season;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.data.model.substitles.MediaSubstitle;
import com.beeecorptv.data.model.substitles.Opensub;
import com.beeecorptv.data.repository.AnimeRepository;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.StatusManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.player.adapters.AnimesEpisodesPlayerAdapter;
import com.beeecorptv.ui.player.adapters.AnimesListAdapter;
import com.beeecorptv.ui.player.adapters.ClickDetectListner;
import com.beeecorptv.ui.player.adapters.EpisodesPlayerAdapter;
import com.beeecorptv.ui.player.adapters.MovieQualitiesAdapter;
import com.beeecorptv.ui.player.adapters.MoviesListAdapter;
import com.beeecorptv.ui.player.adapters.NextPlayMoviesAdapter;
import com.beeecorptv.ui.player.adapters.SerieQualitiesAdapter;
import com.beeecorptv.ui.player.adapters.SeriesListAdapter;
import com.beeecorptv.ui.player.adapters.StreamingListAdapter;
import com.beeecorptv.ui.player.adapters.StreamingQualitiesAdapter;
import com.beeecorptv.ui.player.adapters.SubstitlesAdapter;
import com.beeecorptv.ui.player.bindings.PlayerController;
import com.beeecorptv.ui.player.controller.PlayerAdLogicController;
import com.beeecorptv.ui.player.controller.PlayerUIController;
import com.beeecorptv.ui.player.fsm.Input;
import com.beeecorptv.ui.player.fsm.callback.AdInterface;
import com.beeecorptv.ui.player.fsm.concrete.AdPlayingState;
import com.beeecorptv.ui.player.fsm.concrete.VpaidState;
import com.beeecorptv.ui.player.fsm.listener.AdPlayingMonitor;
import com.beeecorptv.ui.player.fsm.listener.CuePointMonitor;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayer;
import com.beeecorptv.ui.player.interfaces.AutoPlay;
import com.beeecorptv.ui.player.interfaces.DoublePlayerInterface;
import com.beeecorptv.ui.player.interfaces.VpaidClient;
import com.beeecorptv.ui.player.utilities.BrightnessControl;
import com.beeecorptv.ui.player.utilities.ExoPlayerLogger;
import com.beeecorptv.ui.player.utilities.PlaybackSettingMenu;
import com.beeecorptv.ui.player.utilities.PlayerDeviceUtils;
import com.beeecorptv.ui.player.utilities.TrackSelectionDialog;
import com.beeecorptv.ui.player.views.UIControllerView;
import com.beeecorptv.ui.settings.SettingsActivity;
import com.beeecorptv.ui.viewmodels.PlayerViewModel;
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.DownloadFileAsync;
import com.beeecorptv.util.GlideApp;
import com.beeecorptv.util.SpacingItemDecoration;
import com.beeecorptv.util.Tools;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdPlaybackState;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.vungle.warren.AdConfig;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
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

public class EasyPlexMainPlayer extends EasyPlexPlayerActivity implements DoublePlayerInterface, AutoPlay, ClickDetectListner, DialogInterface.OnDismissListener {


    private boolean adsLaunched = false;
    public static BrightnessControl mBrightnessControl;
    private static final String TAG = "EasyPlexMainPlayer";
    protected SimpleExoPlayer adPlayer;
    protected PlayerView playerView;
    private CountDownTimer mCountDownTimer;
    private MediaModel mMediaModel;
    private String mediaGenre;
    private Random random;
    public final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final int FIRSTPAGE = 1;
    private StartAppAd startAppAd;
    private boolean isReady = false;
    private boolean playereadyboolean = false;
    private com.facebook.ads.InterstitialAd mFacebookInter;
    private int qualitySelected;
    private int currentGenre = 0;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private EasyPlexSupportedHosts easyPlexSupportedHosts;

    @Inject
    FsmPlayer fsmPlayer;
    @Inject
    PlayerUIController playerUIController;
    @Inject
    AdPlayingMonitor adPlayingMonitor;
    @Inject
    CuePointMonitor cuePointMonitor;
    @Inject
    AdRetriever adRetriever;
    @Inject
    CuePointsRetriever cuePointsRetriever;
    @Inject
    AdInterface adInterface;
    @Inject
    PlayerAdLogicController playerComponentController;
    @Inject
    VpaidClient vpaidClient;

    @Inject
    PlaybackSettingMenu playbackSettingMenu;

    @Inject
    SettingsManager appSettingsManager;

    @Inject
    StatusManager statusManager;

    @Inject
    TokenManager tokenManager;

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    PlayerController playerController;

    @Inject
    MediaRepository repository;


    @Inject
    AnimeRepository animeRepository;


    private EpisodesPlayerAdapter mEPAdapter;
    private AnimesEpisodesPlayerAdapter animesEpisodesPlayerAdapter;
    private NextPlayMoviesAdapter nextPlayMoviesAdapter;
    private MoviesListAdapter moviesListAdapter;
    private SeriesListAdapter seriesListAdapter;
    private AnimesListAdapter animesListAdapter;
    private StreamingListAdapter streamingListAdapter;
    private SubstitlesAdapter mSubstitleAdapter;
    private MovieQualitiesAdapter movieQualitiesAdapter;
    private SerieQualitiesAdapter serieQualitiesAdapter;
    private StreamingQualitiesAdapter streamingQualitiesAdapter;
    private ClickDetectListner clickDetectListner = this;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private UIControllerView uiControllerView;

    protected AdRetriever getAdRetriever() {
        return adRetriever;
    }

    protected CuePointsRetriever getCuePointsRetriever() {
        return cuePointsRetriever;
    }

    protected PlayerUIController getPlayerUIController() {
        return playerUIController;
    }

    public final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    private History history;
    private Resume resume;

    private String launchedFromDownload;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        getPlayerController().isCurrentSubstitleAuto(settingsManager.getSettings().getAutosubstitles() == 1);
        initNavigation();

        playerViewModel = new ViewModelProvider(this, viewModelFactory).get(PlayerViewModel.class);

        Intent intent = getIntent();


        launchedFromDownload = intent.getStringExtra("from_download");


    }


    @Override
    public void onResume() {
        super.onResume();


        if (getPlayerController().hasSubsActive()) {

            if (getPlayerController().getMediaType().equals("0")) {

                String id = getPlayerController().getVideoID();
                String externalId = getPlayerController().getMediaSubstitleName();
                String type = getPlayerController().getMediaType();
                String currentQuality = getPlayerController().getVideoCurrentQuality();
                String artwork = String.valueOf(getPlayerController().getMediaPoster()) ;
                String name = getPlayerController().getCurrentVideoName();
                mediaModel = MediaModel.media(id,externalId,currentQuality,type,name, String.valueOf(getPlayerController().getVideoUrl()), artwork, String.valueOf(getPlayerController().getMediaSubstitleUrl()),null,null
                        ,null,null,null,
                        null,
                        null,null,getPlayerController().isMediaPremuim(),
                        getPlayerController().getCurrentHlsFormat(),null,getPlayerController().getCurrentExternalId()
                        ,getPlayerController().getMediaCoverHistory(),
                        getPlayerController().getCurrentHasRecap()
                        ,getPlayerController().getCurrentStartRecapIn(),
                        getPlayerController().getMediaGenre(),null,getPlayerController().getVoteAverage());
                update(mMediaModel);


            }else if (getPlayerController().getMediaType().equals("1") || getPlayerController().getMediaType().equals("anime")) {

                String id = getPlayerController().getVideoID();
                String externalId = getPlayerController().getCurrentExternalId();
                String type = getPlayerController().getMediaType();
                String currentQuality = getPlayerController().getVideoCurrentQuality();
                String artwork = String.valueOf(getPlayerController().getMediaPoster());
                String name = getPlayerController().getCurrentVideoName();
                mediaModel = MediaModel.media(id,externalId,currentQuality,type,name, String.valueOf(getPlayerController().getVideoUrl()), artwork,
                        String.valueOf(getPlayerController().getMediaSubstitleUrl()),Integer.parseInt(getPlayerController().getEpID()),null
                        ,getPlayerController().getCurrentEpTmdbNumber(),getPlayerController().getSeaonNumber(),
                        getPlayerController().getEpName(),getPlayerController().getSeaonNumber(),
                        getPlayerController().getCurrentEpisodePosition(),getPlayerController().getCurrentEpTmdbNumber(),getPlayerController().isMediaPremuim(),
                        getPlayerController().getCurrentHlsFormat(),
                        null,getPlayerController().getCurrentExternalId()
                        ,getPlayerController().getMediaCoverHistory(),
                        getPlayerController().getCurrentHasRecap(),getPlayerController().getCurrentStartRecapIn()
                        ,getPlayerController().getMediaGenre(),getPlayerController().getSerieName()
                        ,getPlayerController().getVoteAverage());
                update(mediaModel);

            }
        }
    }

    private void initNavigation() {
        mBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        getPlayerController().playerReady(!playereadyboolean);
        getPlayerController().settingReady(settingReady);
        if (sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN)) finishAffinity();
    }


    @Override
    public View addUserInteractionView() {
        if (sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN)) {finishAffinity();
        }else{playereadyboolean = !playerReady.equals("1");
        return new UIControllerView(getBaseContext()).setPlayerController((PlayerController) getPlayerController());}
        return null;
    }




    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (playerUIController != null) {
            playerUIController.clearMovieResumeInfo();
        }
    }


    protected FsmPlayer getFsmPlayer() {
        return fsmPlayer;
    }

    @Override
    protected void initMoviePlayer() {
        super.initMoviePlayer();
        createMediaSource(mediaModel);
        String mediaType = getPlayerController().getMediaType();
        if ("0".equals(mediaType)) {
            onLoadMovieResume();
        } else if ("1".equals(mediaType)) {
            onLoadSerieResume();
        } else if ("anime".equals(mediaType)) {
            onLoadAnimeResume();
        }

        getPlayerController().isMediaHasRecap(getPlayerController().getCurrentHasRecap() == 1);

    }

    private void onLoadAnimeResume() {

        if (settingsManager.getSettings().getResumeOffline() == 1) {


            playerViewModel.getResumeById(getPlayerController().getCurrentEpTmdbNumber(), settingsManager.getSettings().getApiKey());
            playerViewModel.resumeMediaMutableLiveData.observe(this, resumeInfo -> {

                if (resumeInfo != null) {


                    if (resumeInfo.getTmdb() != null) {


                        if (authManager.getUserInfo().getId() == 0) {


                            if (resumeInfo.getTmdb().equals(getPlayerController().getCurrentEpTmdbNumber()) && Tools.id(this).equals(resumeInfo.getDeviceId())) {


                                if (resumeInfo.getResumePosition() == mMoviePlayer.getCurrentPosition()) {

                                    mMoviePlayer.seekTo(0);

                                } else {

                                    mMoviePlayer.seekTo(resumeInfo.getResumePosition());
                                }


                            } else {

                                mMoviePlayer.seekTo(0);

                            }


                        } else {


                            if (authManager.getUserInfo().getId() == resumeInfo.getUserResumeId() && resumeInfo.getTmdb().equals(getPlayerController().getCurrentEpTmdbNumber()) && Tools.id(this).equals(resumeInfo.getDeviceId())) {


                                if (resumeInfo.getResumePosition() == mMoviePlayer.getCurrentPosition()) {

                                    mMoviePlayer.seekTo(0);

                                } else {

                                    mMoviePlayer.seekTo(resumeInfo.getResumePosition());
                                }


                            } else {

                                mMoviePlayer.seekTo(0);

                            }


                        }


                    } else {


                        mMoviePlayer.seekTo(0);
                    }

                }


             });


        } else {

            playerViewModel.getResumeById(getPlayerController().getCurrentEpTmdbNumber(), settingsManager.getSettings().getApiKey());
            playerViewModel.resumeMediaMutableLiveData.observe(this, mediaResume -> {

                if (mediaResume.getTmdb() != null && mediaResume.getTmdb().equals(getPlayerController().getCurrentEpTmdbNumber())

                && Tools.id(EasyPlexMainPlayer.this).equals(mediaResume.getDeviceId())) {

                if (mediaResume.getResumePosition() == mMoviePlayer.getCurrentPosition()) {

                 mMoviePlayer.seekTo(0);

                 } else {

                mMoviePlayer.seekTo(mediaResume.getResumePosition());

                }


                } else {

               mMoviePlayer.seekTo(0);
             }
           });

        }

    }

    private void onLoadSerieResume() {


        if (settingsManager.getSettings().getResumeOffline() == 1) {


            repository.hasResume(Integer.parseInt(getPlayerController().getCurrentEpTmdbNumber())).observe(this, resumeInfo -> {

                if (resumeInfo != null) {


                    if (resumeInfo.getTmdb() != null) {


                        if (resumeInfo.getTmdb().equals(getPlayerController().getCurrentEpTmdbNumber()) && Tools.id(this).equals(resumeInfo.getDeviceId())) {


                            if (resumeInfo.getResumePosition() < 0) {

                                mMoviePlayer.seekTo(0);

                            } else {
                                mMoviePlayer.seekTo(resumeInfo.getResumePosition());
                            }


                        } else {

                            mMoviePlayer.seekTo(0);

                        }


                    } else {

                        mMoviePlayer.seekTo(0);
                    }

                }
            });


        } else {


            repository.getResumeById(getPlayerController().getCurrentEpTmdbNumber(), settingsManager.getSettings().getApiKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Resume resume) {


                            if (resume.getTmdb() != null && resume.getTmdb().equals(getPlayerController().getCurrentEpTmdbNumber()) && Tools.id(EasyPlexMainPlayer.this).equals(resume.getDeviceId())) {


                                if (resume.getResumePosition() == mMoviePlayer.getDuration()) {

                                    mMoviePlayer.seekTo(0);

                                } else {

                                    mMoviePlayer.seekTo(resume.getResumePosition());
                                }


                            } else {

                                mMoviePlayer.seekTo(0);

                            }


                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onError(@NotNull Throwable e) {


                            mMoviePlayer.seekTo(0);


                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });


        }


    }

    private void onLoadMovieResume() {

        if (settingsManager.getSettings().getResumeOffline() == 1) {

            repository.hasResume(Integer.parseInt(getPlayerController().getVideoID())).observe(this, resumeInfo -> {

                if (resumeInfo != null) {


                    if (resumeInfo.getTmdb() != null) {


                        if (resumeInfo.getTmdb().equals(getPlayerController().getVideoID()) && Tools.id(this).equals(resumeInfo.getDeviceId())) {


                            if (resumeInfo.getResumePosition() < 0) {


                                mMoviePlayer.seekTo(0);

                            } else {

                                mMoviePlayer.seekTo(resumeInfo.getResumePosition());

                            }

                        } else {

                            mMoviePlayer.seekTo(0);

                        }


                    } else {


                        mMoviePlayer.seekTo(0);
                    }


                }

            });


        } else {


            repository.getResumeById(getPlayerController().getVideoID(), settingsManager.getSettings().getApiKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Resume resume) {


                            if (resume.getTmdb() != null) {


                                if (authManager.getUserInfo().getId() == 0) {


                                    if (resume.getTmdb().equals(getPlayerController().getVideoID()) && Tools.id(EasyPlexMainPlayer.this).equals(resume.getDeviceId())) {


                                        if (resume.getResumePosition() == mMoviePlayer.getDuration()) {

                                            mMoviePlayer.seekTo(0);

                                        } else {

                                            mMoviePlayer.seekTo(resume.getResumePosition());
                                        }


                                    } else {

                                        mMoviePlayer.seekTo(0);

                                    }


                                } else {


                                    if (authManager.getUserInfo().getId() == resume.getUserResumeId() && resume.getTmdb().equals(getPlayerController().getVideoID())) {


                                        if (resume.getResumePosition() == mMoviePlayer.getCurrentPosition()) {

                                            mMoviePlayer.seekTo(0);

                                        } else {

                                            mMoviePlayer.seekTo(resume.getResumePosition());
                                        }


                                    } else {

                                        mMoviePlayer.seekTo(0);

                                    }


                                }


                            } else {


                                mMoviePlayer.seekTo(0);
                            }


                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onError(@NotNull Throwable e) {


                            mMoviePlayer.seekTo(0);


                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });

        }

    }

    @Override
    protected void onPlayerReady() {

        prepareFSM();
    }


    protected void createMediaSource(MediaModel videoMediaModel) {

        videoMediaModel.setMediaSource(buildMediaSource(videoMediaModel));

    }


    @Override
    protected void releaseMoviePlayer() {
        super.releaseMoviePlayer();
        if (!PlayerDeviceUtils.useSinglePlayer()) {
            releaseAdPlayer();
        }

        updateResumePosition();
    }


    private void releaseAdPlayer() {
        if (adPlayer != null) {
            updateAdResumePosition();
            adPlayer.release();
            adPlayer = null;

        }

        if (binding.vpaidWebview != null) {
            binding.vpaidWebview.loadUrl(Constants.EMPTY_URL);
            binding.vpaidWebview.clearHistory();
        }
    }

    private void updateAdResumePosition() {
        if (adPlayer != null && playerUIController != null) {
            int adResumeWindow = adPlayer.getCurrentWindowIndex();
            long adResumePosition = adPlayer.isCurrentWindowSeekable() ? Math.max(0, adPlayer.getCurrentPosition())
                    : C.TIME_UNSET;
            playerUIController.setAdResumeInfo(adResumeWindow, adResumePosition);
        }
    }


    /**
     * update the movie and ad playing position when players are released
     */


    @Override
    public void updateResumePosition() {

        if (!getPlayerController().isMediaPlayerError()) {

            //keep track of movie player's position when activity resume back
            if (mMoviePlayer != null && playerUIController != null
                    && mMoviePlayer.getPlaybackState() != STATE_IDLE) {
                int resumeWindow = mMoviePlayer.getCurrentWindowIndex();
                long resumePosition = mMoviePlayer.isCurrentWindowSeekable() ?
                        Math.max(0, mMoviePlayer.getCurrentPosition())
                        :
                        C.TIME_UNSET;
                playerUIController.setMovieResumeInfo(resumeWindow, resumePosition);
                ExoPlayerLogger.i(Constants.FSMPLAYER_TESTING, resumePosition + "");
            }

            //keep track of ad player's position when activity resume back, only keep track when current state is in AdPlayingState.
            if (fsmPlayer.getCurrentState() instanceof AdPlayingState && adPlayer != null && playerUIController != null
                    && adPlayer.getPlaybackState() != STATE_IDLE) {
                int adResumewindow = adPlayer.getCurrentWindowIndex();
                long adResumePosition = adPlayer.isCurrentWindowSeekable() ? Math.max(0, adPlayer.getCurrentPosition())
                        : C.TIME_UNSET;
                playerUIController.setAdResumeInfo(adResumewindow, adResumePosition);
            }


            if (!getPlayerController().getVideoID().isEmpty() && !getPlayerController().getMediaType().isEmpty()

                    && mMoviePlayer != null && playerUIController != null
                    && mMoviePlayer.getPlaybackState() != STATE_IDLE && mMoviePlayer.getPlaybackState() != STATE_ENDED  ) {


                int resumeWindow = mMoviePlayer.getCurrentWindowIndex();
                int videoDuration = (int) mMoviePlayer.getDuration();
                int resumePosition = (int) (mMoviePlayer.isCurrentWindowSeekable() ?
                        Math.max(0, mMoviePlayer.getCurrentPosition())
                        :
                        C.TIME_UNSET);


                if (getPlayerController().getMediaType().equals("0")) {


                    if (settingsManager.getSettings().getResumeOffline() == 1) {

                        resume = new Resume(getPlayerController().getVideoID());
                        resume.setTmdb(getPlayerController().getVideoID());
                        resume.setDeviceId(Tools.id(getBaseContext()));
                        resume.setMovieDuration(videoDuration);
                        resume.setResumePosition(resumePosition);
                        resume.setUserResumeId(authManager.getUserInfo().getId());
                        resume.setResumeWindow(resumeWindow);

                        compositeDisposable.add(Completable.fromAction(() -> repository.addResume(resume))
                                .subscribeOn(Schedulers.io())
                                .subscribe());


                    } else {


                        repository.getResumeMovie(settingsManager.getSettings().getApiKey()
                                , authManager.getUserInfo().getId(), getPlayerController().getVideoID()
                                , resumeWindow, resumePosition, videoDuration, Tools.id(getBaseContext()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull Resume resume) {

                                        //
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


                } else {


                    if (settingsManager.getSettings().getResumeOffline() == 1) {


                        resume = new Resume(getPlayerController().getCurrentEpTmdbNumber());
                        resume.setTmdb(getPlayerController().getCurrentEpTmdbNumber());
                        resume.setDeviceId(Tools.id(getBaseContext()));
                        resume.setMovieDuration(videoDuration);
                        resume.setResumePosition(resumePosition);
                        resume.setUserResumeId(authManager.getUserInfo().getId());
                        resume.setResumeWindow(resumeWindow);

                        compositeDisposable.add(Completable.fromAction(() -> repository.addResume(resume))
                                .subscribeOn(Schedulers.io())
                                .subscribe());


                    } else {

                        repository.getResumeMovie(settingsManager.getSettings().getApiKey(),
                                authManager.getUserInfo().getId(),
                                getPlayerController().getCurrentEpTmdbNumber()
                                , resumeWindow, resumePosition, videoDuration, Tools.id(getBaseContext()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull Resume resume) {

                                        //
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

                }


                i(Constants.FSMPLAYER_TESTING, resumePosition + "");


            }


        }

        if (launchedFromDownload !=null && !launchedFromDownload.isEmpty() && !launchedFromDownload.equals("yes")){
            onLoadHistory();
        }
    }


    @Override
    protected boolean isCaptionPreferenceEnable() {
        return true;
    }

    /**
     * prepare / set up FSM and inject all the elements into the FSM
     */
    @Override
    public void prepareFSM() {


        //update the playerUIController view, need to update the view everything when two ExoPlayer being recreated in activity lifecycle.
        playerUIController.setContentPlayer(mMoviePlayer);
        playerUIController.setAdPlayer(adPlayer);
        playerUIController.setExoPlayerView(binding.tubitvPlayer);
        playerUIController.setVpaidWebView(binding.vpaidWebview);

        //update the MediaModel
        fsmPlayer.setController(playerUIController);
        fsmPlayer.setMovieMedia(mediaModel);
        fsmPlayer.setAdRetriever(adRetriever);
        fsmPlayer.setCuePointsRetriever(cuePointsRetriever);
        fsmPlayer.setAdServerInterface(adInterface);
        binding.mGenreStart.setText(mediaModel.getMediaGenre());
        //set the PlayerComponentController.
        playerComponentController.setAdPlayingMonitor(adPlayingMonitor);
        playerComponentController.setTubiPlaybackInterface(this);
        playerComponentController.setDoublePlayerInterface(this);
        playerComponentController.setCuePointMonitor(cuePointMonitor);
        playerComponentController.setVpaidClient(vpaidClient);
        fsmPlayer.setPlayerComponentController(playerComponentController);
        fsmPlayer.setLifecycle(getLifecycle());
        playbackSettingMenu.setContentPlayer(mMoviePlayer);
        playbackSettingMenu.setActivity(this);
        playbackSettingMenu.buildSettingMenuOptions();

        if (fsmPlayer.isInitialized()) {
            fsmPlayer.updateSelf();
            Tools.hideSystemPlayerUi(this, true);
        } else {
            fsmPlayer.transit(Input.INITIALIZE);
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        easyPlexSupportedHosts = null;

        if (playerView != null) {
            playerView.removeAllViews();
            playerView.removeAllViewsInLayout();
            playerView = null;
        }

        if (uiControllerView != null) {
            uiControllerView.removeAllViews();
            uiControllerView.removeAllViewsInLayout();
            uiControllerView = null;
        }

        playerController = null;
        animesEpisodesPlayerAdapter = null;
        mEPAdapter = null;
        nextPlayMoviesAdapter = null;
        moviesListAdapter = null;
        seriesListAdapter = null;
        animesListAdapter = null;
        streamingListAdapter = null;
        mSubstitleAdapter = null;
        movieQualitiesAdapter = null;
        serieQualitiesAdapter = null;
        clickDetectListner = null;
        mBehavior = null;
        fsmPlayer.setLifecycle(null);
        fsmPlayer.setAdMedia(null);
        fsmPlayer.setCuePointsRetriever(null);
        fsmPlayer.setPlayerComponentController(null);
        fsmPlayer.setMovieMedia(null);
        compositeDisposable.clear();

        if (mFacebookInter != null) {

            mFacebookInter.destroy();
        }


        mInterstitialAd = null;


        binding.webviewPlayer.clearHistory();
        if (adsLoader != null) {

            adsLoader.stop();
            adsLoader.release();
            adsLoader = null;

        }


        if (mCountDownTimer != null) {

            mCountDownTimer.cancel();
            mCountDownTimer = null;

        }

        playerComponentController.setDoublePlayerInterface(null);
        playerComponentController.setTubiPlaybackInterface(null);
        playerComponentController.setCuePointMonitor(null);
        playerComponentController.setAdPlayingMonitor(null);
        playerComponentController.setVpaidClient(null);
        playerComponentController = null;
        adPlayingMonitor = null;
        playerUIController.setExoPlayerView(null);
        playerUIController.setContentPlayer(null);
        playerUIController.setVpaidWebView(null);
        playerUIController.setAdPlayer(null);
        playbackSettingMenu = null;

        if (adsLoader != null) {

            adsLoader.stop();
            adsLoader.release();
            adsLoader = null;

        }

        mediaModel.setMediaSource(null);
        mediaModel = null;

        if (fsmPlayer != null && fsmPlayer.getCurrentState() instanceof VpaidState && binding.vpaidWebview != null
                && binding.vpaidWebview.canGoBack()) {

            //if the last page is empty url, then, it should
            if (ingoreWebViewBackNavigation(binding.vpaidWebview)) {
                super.onBackPressed();
                return;
            }

            binding.vpaidWebview.goBack();

        }


        binding.tubitvPlayer.removeAllViews();
        binding.tubitvPlayer.removeAllViewsInLayout();

        if (adsLoader !=null) {

            adsLoader.stop();
            adsLoader.release();

        }

        Appodeal.destroy(Appodeal.INTERSTITIAL);


    }

    //when the last item is "about:blank", ingore the back navigation for webview.
    private boolean ingoreWebViewBackNavigation(WebView vpaidWebView) {

        if (vpaidWebView != null) {
            WebBackForwardList mWebBackForwardList = vpaidWebView.copyBackForwardList();

            if (mWebBackForwardList == null) {
                return false;
            }

            WebHistoryItem historyItem = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1);

            if (historyItem == null) {
                return false;
            }

            String historyUrl = historyItem.getUrl();

            return historyUrl != null && historyUrl.equalsIgnoreCase(Constants.EMPTY_URL);
        }

        return false;
    }

    /**
     * creating the {@link MediaSource} for the Exoplayer, recreate it everytime when new {@link SimpleExoPlayer} has been initialized
     */


    @Override
    public void onPrepareAds(@Nullable AdMediaModel adMediaModel) {

        for (MediaModel singleMedia : adMediaModel.getListOfAds()) {
            MediaSource adMediaSource = buildMediaSource(singleMedia);
            singleMedia.setMediaSource(adMediaSource);
        }

    }


    @Override
    public void onSeek(MediaModel mediaModel, long oldPositionMillis, long newPositionMillis) {
        if (mediaModel == null) throw new AssertionError();
        v(TAG, mediaModel.getMediaName() + ": " + mediaModel.toString() + " onSeek : " + "oldPositionMillis: " + oldPositionMillis + " newPositionMillis: " + newPositionMillis);
    }

    @Override
    public void onSeekBirghtness() {

        mBrightnessControl = new BrightnessControl(this);
        mBrightnessControl.setScreenBrightness(mBrightnessControl.levelToBrightness(2));

    }

    @Override
    public void onPlayToggle(MediaModel mediaModel, boolean playing) {
        if (mediaModel == null) throw new AssertionError();
        v(TAG, mediaModel.getMediaName() + ": " + mediaModel.toString() + " onPlayToggle :");
    }


    @Override
    public void onSubtitles(@Nullable MediaModel mediaModel, boolean enabled) {

        //
    }


    // Load Qualities for Movies and Series
    @Override
    public void onLoadQualities() {

        binding.frameQualities.setVisibility(VISIBLE);
        binding.closeQualities.setOnClickListener(v -> binding.frameQualities.setVisibility(View.GONE));

        // For Movie
        String mediaType = getPlayerController().getMediaType();
        if ("0".equals(mediaType)) {
            playerViewModel.getMovie(getPlayerController().getVideoID(), settingsManager.getSettings().getApiKey());
            playerViewModel.mediaMutableLiveData.observe(this, movieResponse -> {
                // Episodes RecycleView
                binding.rvQualites.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this));
                binding.rvQualites.setHasFixedSize(true);
                movieQualitiesAdapter = new MovieQualitiesAdapter();
                movieQualitiesAdapter.addSeasons(movieResponse.getVideos(), clickDetectListner, settingsManager, EasyPlexMainPlayer.this);
                binding.rvQualites.setAdapter(movieQualitiesAdapter);

            });
        } else if ("1".equals(mediaType)) {// For Series
            playerViewModel.getSerieStream(getPlayerController().getCurrentEpTmdbNumber(), settingsManager.getSettings().getApiKey());
            playerViewModel.mediaStreamMutableLiveData.observe(this, movieResponse -> {

                List<MediaStream> streamInfo = movieResponse.getMediaStreams();
                // Episodes RecycleView
                binding.rvQualites.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this));
                binding.rvQualites.setHasFixedSize(true);
                serieQualitiesAdapter = new SerieQualitiesAdapter();
                serieQualitiesAdapter.addQuality(streamInfo, clickDetectListner, settingsManager, EasyPlexMainPlayer.this);
                binding.rvQualites.setAdapter(serieQualitiesAdapter);

            });
        } else if ("anime".equals(mediaType)) {// For Animes
            playerViewModel.getAnimeStream(getPlayerController().getCurrentEpTmdbNumber(), settingsManager.getSettings().getApiKey());
            playerViewModel.mediaStreamMutableLiveData.observe(this, movieResponse -> {

                List<MediaStream> streamInfo = movieResponse.getMediaStreams();

                // Qualities RecycleView
                binding.rvQualites.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this));
                binding.rvQualites.setHasFixedSize(true);
                serieQualitiesAdapter = new SerieQualitiesAdapter();
                serieQualitiesAdapter.addQuality(streamInfo, clickDetectListner, settingsManager, EasyPlexMainPlayer.this);
                binding.rvQualites.setAdapter(serieQualitiesAdapter);


            });
        } else if ("streaming".equals(mediaType) && settingsManager.getSettings().getLivetvMultiServers() == 1) {// For Streaming
            playerViewModel.getStreamingStream(getPlayerController().getVideoID(), settingsManager.getSettings().getApiKey());
            playerViewModel.mediaMutableLiveData.observe(this, movieResponse -> {

                // Qualities RecycleView
                binding.rvQualites.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this));
                binding.rvQualites.setHasFixedSize(true);
                streamingQualitiesAdapter = new StreamingQualitiesAdapter();
                streamingQualitiesAdapter.addSeasons(movieResponse.getVideos(), clickDetectListner, settingsManager, EasyPlexMainPlayer.this);
                binding.rvQualites.setAdapter(streamingQualitiesAdapter);


            });
        }
    }

    @Override
    public void StartGenre(String genre) {
        binding.mGenreStart.setText(genre);
    }

    @Override
    public void getType(String type) {

        if (type.equals("0") || type.equals("1") || type.equals("anime") && !isCurrentAd) {


            binding.mediaGenres.setVisibility(VISIBLE);

        }else {

            binding.mediaGenres.setVisibility(GONE);
        }
    }


    // Load Substitles for Movies & Series
    @Override
    public void onSubtitlesSelection() {
        if (sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN)) {
            finishAffinity();
        } else {

            if (settingsManager.getSettings().getAutosubstitles() == 1) {

                onOpenSubsLoad();


            } else {

                binding.frameSubstitles.setVisibility(VISIBLE);
                binding.closeSubstitle.setOnClickListener(v -> binding.frameSubstitles.setVisibility(View.GONE));

                String mediaType = getPlayerController().getMediaType();
                if ("0".equals(mediaType)) {
                    playerViewModel.getMovie(getPlayerController().getVideoID(), settingsManager.getSettings().getApiKey());
                    playerViewModel.mediaMutableLiveData.observe(this, movieResponse -> {

                        List<MediaSubstitle> movieSubtitles = movieResponse.getSubstitles();

                        // Qualities RecycleView
                        binding.rvSubstitles.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this));
                        binding.rvSubstitles.setHasFixedSize(true);
                        mSubstitleAdapter = new SubstitlesAdapter();
                        mSubstitleAdapter.addSubtitle(movieSubtitles, clickDetectListner, EasyPlexMainPlayer.this);
                        binding.rvSubstitles.setAdapter(mSubstitleAdapter);

                    });
                } else if ("1".equals(mediaType)) {
                    playerViewModel.getEpisodeSubstitle(getPlayerController().getCurrentEpTmdbNumber(), settingsManager.getSettings().getApiKey());
                    playerViewModel.episodeStreamMutableLiveData.observe(this, movieResponse -> {

                        List<MediaSubstitle> movieSubtitles = movieResponse.getStreamepisode();

                        // Substitles RecycleView
                        binding.rvSubstitles.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this));
                        binding.rvSubstitles.setHasFixedSize(true);
                        mSubstitleAdapter = new SubstitlesAdapter();
                        mSubstitleAdapter.addSubtitle(movieSubtitles, clickDetectListner, EasyPlexMainPlayer.this);
                        binding.rvSubstitles.setAdapter(mSubstitleAdapter);

                    });
                } else if ("anime".equals(mediaType)) {
                    playerViewModel.getEpisodeSubstitleAnime(getPlayerController().getCurrentEpTmdbNumber(), settingsManager.getSettings().getApiKey());
                    playerViewModel.episodeStreamMutableLiveData.observe(this, movieResponse -> {

                        List<MediaSubstitle> movieSubtitles = movieResponse.getStreamepisode();

                        // Substitles RecycleView
                        binding.rvSubstitles.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this));
                        binding.rvSubstitles.setHasFixedSize(true);
                        mSubstitleAdapter = new SubstitlesAdapter();
                        mSubstitleAdapter.addSubtitle(movieSubtitles, clickDetectListner, EasyPlexMainPlayer.this);
                        binding.rvSubstitles.setAdapter(mSubstitleAdapter);

                    });
                }
            }


        }


    }

    @Override
    public void onMediaEnded() {

        if (playerController.playerPlaybackState.get() != Player.STATE_ENDED) {

            if (sharedPreferences.getBoolean(AUTO_PLAY, true) && !getPlayerController().getVideoID().isEmpty() &&
                    !getPlayerController().getMediaType().isEmpty() && getPlayerController().getMediaType().equals("0")
                    || getPlayerController().getMediaType().equals("1") || getPlayerController().getMediaType().equals("anime")) {

                onHideLayout();

                if (!adsLaunched) {

                    createAndLoadRewardedAd();

                }

                if (getPlayerController().getMediaType().equals("1")) {

                 onLoadNextSerieEpisodes();

                } else if (getPlayerController().getMediaType().equals("anime")) {

                 onLoadNextSerieEpisodesAnimes();

                } else {

                    onLoadNextMovies();
                }

            } else {

                onBackPressed();

            }

        }

    }

    @Override
    public void onTracksChanged(@NotNull TrackGroupArray trackGroups, @NotNull TrackSelectionArray trackSelections) {

        if (trackGroups != lastSeenTrackGroupArray) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = mTrackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    Toast.makeText(this, "Audio Error", Toast.LENGTH_SHORT).show();
                }
                if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    Toast.makeText(this, "Video Error", Toast.LENGTH_SHORT).show();
                }
            }
            lastSeenTrackGroupArray = trackGroups;
        }
    }


    private void onLoadNextSerieEpisodesAnimes() {

        binding.framlayoutMediaEnded.setVisibility(VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        binding.framlayoutMediaEnded.startAnimation(alphaAnimation);

        binding.closeMediaEnded.setOnClickListener(v -> {

            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if (mCountDownTimer != null) {

                mCountDownTimer.cancel();
                mCountDownTimer = null;

            }

        });

        onLoadNextEpisodeWithTimerAnimes();

    }

    private void onLoadNextEpisodeWithTimerAnimes() {

        binding.progressBar.setVisibility(VISIBLE);
        binding.leftInfo.setVisibility(View.GONE);
        binding.nextPlayLayout.setVisibility(GONE);

        playerViewModel.getAnimeSeasons(getPlayerController().nextSeaonsID(), settingsManager.getSettings().getApiKey());
        playerViewModel.nextMediaMutableLiveData.observe(this, movieResponse -> {
             if (getPlayerController().getCurrentEpisodePosition() != movieResponse.getEpisodes().size() - 1) {

                            for (int i = 0; i < movieResponse.getEpisodes().size(); i++) {

                                if (getPlayerController().getEpName().equals(movieResponse.getEpisodes().get(i).getName())) {

                                    int position = i + 1;

                                    mCountDownTimer = new CountDownTimer(settingsManager.getSettings().getNextEpisodeTimer() * 1000L, 1000) {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            binding.textViewVideoTimeRemaining.setText(UPNEXT + millisUntilFinished / 1000 + " s");
                                            binding.textViewVideoRelease.setText(SEASONS + getPlayerController().getSeaonNumber());
                                            binding.ratingBar.setRating(Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()) / 2);
                                            binding.viewMovieRating.setText(valueOf(movieResponse.getEpisodes().get(position).getVoteAverage()));
                                            binding.textOverviewLabel.setText(movieResponse.getEpisodes().get(position).getOverview());

                                            GlideApp.with(getApplicationContext()).asBitmap().load(movieResponse.getEpisodes().get(position).getStillPath())
                                                    .centerCrop()
                                                    .placeholder(R.drawable.placehoder_episodes)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .transition(withCrossFade())
                                                    .into(binding.imageViewMovieNext);

                                            GlideApp.with(getApplicationContext()).asBitmap().load(movieResponse.getEpisodes().get(position).getStillPath())
                                                    .centerCrop()
                                                    .placeholder(R.drawable.placehoder_episodes)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(binding.nextCoverMedia);

                                            binding.textViewVideoNextName.setText(EP + movieResponse.getEpisodes().get(position).getEpisodeNumber() + " : " + movieResponse.getEpisodes().get(position).getName());
                                            binding.textViewVideoNextReleaseDate.setVisibility(GONE);


                                            binding.progressBar.setVisibility(GONE);
                                            binding.leftInfo.setVisibility(VISIBLE);
                                            binding.nextPlayLayout.setVisibility(VISIBLE);


                                        }

                                        @Override
                                        public void onFinish() {

                                            onCheckEpisodeHasStreamAnimes(movieResponse, position);

                                            if (mCountDownTimer != null) {

                                                mCountDownTimer.cancel();
                                                mCountDownTimer = null;
                                            }

                                        }

                                    }.start();

                                }else {


                                    if (mCountDownTimer != null) {

                                        mCountDownTimer.cancel();
                                        mCountDownTimer = null;
                                    }
                                    onBackPressed();
                                }

                                break;

                            }
                        }else {

                 if (mCountDownTimer != null) {

                     mCountDownTimer.cancel();
                     mCountDownTimer = null;
                 }
                 onBackPressed();
             }

                });
    }


    private void onHideLayout() {


        if (binding.frameLayoutSeriesList.getVisibility() == VISIBLE) {
            binding.frameLayoutSeriesList.setVisibility(View.GONE);
        }

        if (binding.frameLayoutSeasons.getVisibility() == VISIBLE) {
            binding.frameLayoutSeasons.setVisibility(View.GONE);
        }

        if (binding.frameLayoutMoviesList.getVisibility() == VISIBLE)
            binding.frameLayoutMoviesList.setVisibility(View.GONE);


        if (binding.frameLayoutSeasons.getVisibility() == VISIBLE)
            binding.frameLayoutSeasons.setVisibility(View.GONE);


        if (binding.frameLayoutStreaming.getVisibility() == VISIBLE) {

            binding.frameLayoutStreaming.setVisibility(View.GONE);
        }


        if (binding.frameQualities.getVisibility() == VISIBLE) {

            binding.frameQualities.setVisibility(View.GONE);
        }


        if (binding.frameSubstitles.getVisibility() == VISIBLE) {

            binding.frameSubstitles.setVisibility(View.GONE);
        }


    }


    // Load Next Movies RecycleViews
    private void onLoadNextMovies() {

        binding.framlayoutMediaEnded.setVisibility(VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        binding.framlayoutMediaEnded.startAnimation(alphaAnimation);

        binding.closeMediaEnded.setOnClickListener(v -> {

            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if (mCountDownTimer != null) {

                mCountDownTimer.cancel();
                mCountDownTimer = null;

            }

        });

        onLoadRandomMovie();

    }

    private void onLoadRandomMovie() {


        binding.progressBar.setVisibility(VISIBLE);
        binding.leftInfo.setVisibility(View.GONE);
        binding.nextPlayLayout.setVisibility(View.GONE);

        playerViewModel.getMoviRandom();
        playerViewModel.nextMediaMutableLiveData.observe(this, movieResponse -> {

            // Episodes RecycleView
            nextPlayMoviesAdapter = new NextPlayMoviesAdapter();
            nextPlayMoviesAdapter.addSeasons(movieResponse.getRandom());
            Collections.shuffle(movieResponse.getRandom());

            playerViewModel.getMovie(valueOf(nextPlayMoviesAdapter.getFirstItem()), settingsManager.getSettings().getApiKey());
            playerViewModel.mediaMutableLiveData.observe(this, media -> mCountDownTimer = new CountDownTimer(settingsManager.getSettings().getNextEpisodeTimer() * 1000L, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {

                    binding.textViewVideoTimeRemaining.setText(UPNEXT + millisUntilFinished / 1000 + " s");
                    binding.ratingBar.setRating(media.getVoteAverage() / 2);
                    binding.viewMovieRating.setText(valueOf(media.getVoteAverage()));
                    binding.textOverviewLabel.setText(media.getOverview());

                    GlideApp.with(getApplicationContext()).asBitmap().load(media.getBackdropPath())
                            .centerCrop()
                            .placeholder(R.drawable.placehoder_episodes)
                            .transition(withCrossFade())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imageViewMovieNext);


                    GlideApp.with(getApplicationContext()).asBitmap().load(media.getBackdropPath())
                            .centerCrop()
                            .placeholder(R.drawable.placehoder_episodes)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(withCrossFade())
                            .into(binding.nextCoverMedia);

                    if (media.getReleaseDate() != null) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
                        try {
                            Date releaseDate = sdf1.parse(media.getReleaseDate());
                            assert releaseDate != null;
                            binding.textViewVideoRelease.setText(sdf2.format(releaseDate));
                        } catch (ParseException e) {

                            Timber.d("%s", Arrays.toString(e.getStackTrace()));

                        }
                    } else {
                        binding.textViewVideoRelease.setText("");
                    }


                    binding.textViewVideoNextName.setText(media.getTitle());


                    for (Genre genre : media.getGenres()) {
                        binding.textViewVideoNextReleaseDate.setText(genre.getName());
                    }


                    binding.progressBar.setVisibility(GONE);
                    binding.leftInfo.setVisibility(VISIBLE);
                    binding.nextPlayLayout.setVisibility(VISIBLE);

                }

                @Override
                public void onFinish() {

                    if (media.getVideos() !=null && !media.getVideos().isEmpty()) {

                        if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                            onLoadNextMovieStream(media);

                        } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 1 && media.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                            onLoadSubscribeDialog(media);

                        } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 0 && media.getPremuim() == 0) {

                            onLoadNextMovieStream(media);


                        } else if (authManager.getUserInfo().getPremuim() == 1 && media.getPremuim() == 0) {

                            onLoadNextMovieStream(media);


                        } else {

                            if(!isFinishing())
                            {
                                DialogHelper.showPremuimWarning(EasyPlexMainPlayer.this);
                            }


                        }
                    } else {

                        if(!isFinishing())
                        {
                            DialogHelper.showNoStreamAvailable(EasyPlexMainPlayer.this);
                        }

                    }

                }
            }.start());

         });
    }

    @SuppressLint("StaticFieldLeak")
    private void onLoadNextMovieStream(Media media) {

        updateResumePosition();

        mediaType();

        if (binding.framlayoutMediaEnded.getVisibility() == VISIBLE) {
            binding.framlayoutMediaEnded.setVisibility(View.GONE);
        }



        if (settingsManager.getSettings().getServerDialogSelection() == 1) {


             String[] charSequence = new String[media.getVideos().size()];

                for (int i = 0; i<media.getVideos().size(); i++) {
                    charSequence[i] = media.getVideos().get(i).getServer() + " - " + media.getVideos().get(i).getLang();

                }


                final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
                builder.setTitle(getString(R.string.select_qualities));
                builder.setCancelable(true);
                builder.setItems(charSequence, (dialogInterface, wich) -> {


                 if (media.getVideos().get(wich).getEmbed() == 1)  {


                     Intent intent = new Intent(this, EmbedActivity.class);
                     intent.putExtra(Constants.MOVIE_LINK, media.getVideos().get(wich).getLink());
                     startActivity(intent);


                 }else if (media.getVideos().get(wich).getSupportedHosts() == 1){


                     easyPlexSupportedHosts = new EasyPlexSupportedHosts(this);
                     easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                         @Override
                         public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                             if (multipleQuality){
                                 if (vidURL!=null) {
                                     CharSequence[] name = new CharSequence[vidURL.size()];

                                     for (int i = 0; i < vidURL.size(); i++) {
                                         name[i] = vidURL.get(i).getQuality();
                                     }


                                     final AlertDialog.Builder builder = new AlertDialog.Builder(EasyPlexMainPlayer.this, R.style.MyAlertDialogTheme);
                                     builder.setTitle(getString(R.string.select_qualities));
                                     builder.setCancelable(true);
                                     builder.setItems(name, (dialogInterface, i) -> {


                                         mMediaModel = MediaModel.media(valueOf(media.getId()), valueOf(media.getId()), media.getVideos().get(wich).getServer(),
                                                 "0", media.getTitle(), vidURL.get(i).getUrl(), media.getBackdropPath(), null,
                                                 null, null, null, null, null,
                                                 null, null, null,

                                                 null, media.getVideos().get(wich).getHls(), null, media.getImdbExternalId(),
                                                 media.getPosterPath(), getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(),
                                                 mediaGenre, null, media.getVoteAverage());
                                         playNext(mMediaModel);

                                     });

                                     builder.show();



                                 }else  Toast.makeText(EasyPlexMainPlayer.this, "NULL", Toast.LENGTH_SHORT).show();

                             }else {


                                 mMediaModel = MediaModel.media(valueOf(media.getId()), valueOf(media.getId()), media.getVideos().get(wich).getServer(),
                                         "0", media.getTitle(), vidURL.get(0).getUrl(), media.getBackdropPath(), null,
                                         null, null, null, null, null,
                                         null, null, null,

                                         null, media.getVideos().get(wich).getHls(),
                                         null, media.getImdbExternalId(),
                                         media.getPosterPath(), getPlayerController().getCurrentHasRecap(),
                                         getPlayerController().getCurrentStartRecapIn(), mediaGenre, null, media.getVoteAverage());
                                 playNext(mMediaModel);

                             }

                         }

                         @Override
                         public void onError() {

                             Toast.makeText(EasyPlexMainPlayer.this, "Error", Toast.LENGTH_SHORT).show();
                         }
                     });

                     easyPlexSupportedHosts.find(media.getVideos().get(wich).getLink());

                   }  else {


                     mMediaModel = MediaModel.media(valueOf(media.getId()), valueOf(media.getTmdbId()), media.getVideos().get(wich).getServer(),
                             "0", media.getTitle(), media.getVideos().get(wich).getLink(), media.getBackdropPath(), null,
                             null, null, null, null, null,
                             null, null, null,

                             null, media.getVideos().get(wich).getHls(), null, media.getImdbExternalId(),
                             media.getPosterPath(), getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(), mediaGenre, null, media.getVoteAverage());
                     playNext(mMediaModel);

                        }

                });

                builder.show();


        }else  {

            String mediaUrl = media.getVideos().get(0).getLink();
        String currentQuality = media.getVideos().get(0).getServer();
        int hls = media.getVideos().get(0).getHls();

        for (Genre genre : media.getGenres()) {
            mediaGenre = genre.getName();
        }


        if (media.getVideos().get(0).getEmbed() == 1) {

            Intent intent = new Intent(this, EmbedActivity.class);
            intent.putExtra(Constants.MOVIE_LINK, mediaUrl);
            startActivity(intent);


        } else if (media.getVideos().get(0).getSupportedHosts() == 1){


            easyPlexSupportedHosts = new EasyPlexSupportedHosts(this);
            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                    if (multipleQuality){
                        if (vidURL!=null) {
                            CharSequence[] name = new CharSequence[vidURL.size()];

                            for (int i = 0; i < vidURL.size(); i++) {
                                name[i] = vidURL.get(i).getQuality();
                            }


                            final AlertDialog.Builder builder = new AlertDialog.Builder(EasyPlexMainPlayer.this, R.style.MyAlertDialogTheme);
                            builder.setTitle(getString(R.string.select_qualities));
                            builder.setCancelable(true);
                            builder.setItems(name, (dialogInterface, i) -> {


                                mMediaModel = MediaModel.media(valueOf(media.getId()), valueOf(media.getId()), currentQuality,
                                        "0", media.getTitle(), vidURL.get(i).getUrl(), media.getBackdropPath(), null,
                                        null, null, null, null, null,
                                        null, null, null,

                                        null, hls, null, media.getImdbExternalId(),
                                        media.getPosterPath(), getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(), mediaGenre, null, media.getVoteAverage());
                                playNext(mMediaModel);

                            });

                            builder.show();



                        }else  Toast.makeText(EasyPlexMainPlayer.this, "NULL", Toast.LENGTH_SHORT).show();

                    }else {


                        mMediaModel = MediaModel.media(valueOf(media.getId()), valueOf(media.getId()), currentQuality,
                                "0", media.getTitle(), vidURL.get(0).getUrl(), media.getBackdropPath(), null,
                                null, null, null, null, null,
                                null, null, null,

                                null, hls, null, media.getImdbExternalId(),
                                media.getPosterPath(), getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(), mediaGenre, null, media.getVoteAverage());
                        playNext(mMediaModel);

                    }

                }

                @Override
                public void onError() {

                    Toast.makeText(EasyPlexMainPlayer.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            easyPlexSupportedHosts.find(mediaUrl);


        }else  {


            mMediaModel = MediaModel.media(valueOf(media.getId()), valueOf(media.getTmdbId()), currentQuality,
                    "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                    null, null, null, null, null,
                    null, null, null,

                    null, hls, null, media.getImdbExternalId(),
                    media.getPosterPath(), getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(), mediaGenre, null, media.getVoteAverage());
            playNext(mMediaModel);


        }

        }

    }



    private void onLoadSubscribeDialog(Media movieDetail) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.watch_to_unlock);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


        dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

            startActivity(new Intent(this, SettingsActivity.class));

            dialog.dismiss();


        });


        dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {


            String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultNetworkPlayer();
            
            
            if ("Vungle".equals(defaultRewardedNetworkAds)) {

                onLoadVungleAds(movieDetail);

            }else if ("Ironsource".equals(defaultRewardedNetworkAds)) {

                onLoadIronsourceAds(movieDetail);

            } else if ("AppNext".equals(defaultRewardedNetworkAds)) {

                onLoadAppNextAds(movieDetail);

            } else if (getString(R.string.startapp).equals(defaultRewardedNetworkAds)) {


                onLoadStartAppAds(movieDetail);

            } else if (getString(R.string.admob).equals(defaultRewardedNetworkAds)) {

                onLoadAdmobRewardAds(movieDetail);


            } else if (getString(R.string.appodeal).equals(defaultRewardedNetworkAds)) {

                onLoadAppOdealRewardAds(movieDetail);

            } else if (getString(R.string.facebook).equals(defaultRewardedNetworkAds)) {

                onLoadFaceBookRewardAds(movieDetail);

            } else if (getString(R.string.auto).equals(defaultRewardedNetworkAds)) {

                onLoadAutoRewardAds(movieDetail);

            }


            dialog.dismiss();


        });


        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

    private void onLoadIronsourceAds(Media movieDetail) {

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

                onLoadNexMovieStreamFromEnding(movieDetail);
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

    private void onLoadVungleAds(Media movieDetail) {

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

                onLoadNexMovieStreamFromEnding(movieDetail);
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

    private void onLoadAppNextAds(Media movieDetail) {

        Interstitial interstitialAppNext = new Interstitial(this, settingsManager.getSettings().getAppnextPlacementid());

        interstitialAppNext.loadAd();

        interstitialAppNext.showAd();

        // Get callback for ad loaded
        interstitialAppNext.setOnAdLoadedCallback((bannerId, creativeType) -> {

        });// Get callback for ad opened
        interstitialAppNext.setOnAdOpenedCallback(() -> {

        });// Get callback for ad clicked
        interstitialAppNext.setOnAdClickedCallback(() -> {

        });// Get callback for ad closed
        interstitialAppNext.setOnAdClosedCallback(() -> onLoadNexMovieStreamFromEnding(movieDetail));
        interstitialAppNext.setOnAdErrorCallback(error -> {

        });
    }


    private void onLoadAutoRewardAds(Media media) {

        random = new Random();
        int numberOfMethods = 3;

        switch (random.nextInt(numberOfMethods)) {
            case 0:
                onLoadStartAppAds(media);
                break;
            case 1:
                onLoadFaceBookRewardAds(media);
                break;
            case 2:
                onLoadAppOdealRewardAds(media);
                break;
            default:
                onLoadAdmobRewardAds(media);
        }


    }


    private void onLoadAppOdealRewardAds(Media media) {

        Appodeal.show(EasyPlexMainPlayer.this, Appodeal.INTERSTITIAL);

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean b) {

                //

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

                onLoadNexMovieStreamFromEnding(media);


            }

            @Override
            public void onInterstitialExpired() {

                //

            }
        });


    }


    private void onLoadFaceBookRewardAds(Media media) {

        com.facebook.ads.InterstitialAd facebookInterstitialAd = new com.facebook.ads.InterstitialAd(this, settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());

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

                //
            }

            @Override
            public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                //

            }

            @Override
            public void onInterstitialDismissed(com.facebook.ads.Ad ad) {

                onLoadNexMovieStreamFromEnding(media);

            }


        };


        facebookInterstitialAd.loadAd(
                facebookInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    private void onLoadNexMovieStreamFromEnding(Media media) {


        if (binding.framlayoutMediaEnded.getVisibility() == VISIBLE) {
            binding.framlayoutMediaEnded.setVisibility(View.GONE);
        }


        for (Genre genre : media.getGenres()) {
            mediaGenre = genre.getName();
        }


        String artwork = media.getBackdropPath();
        String movieId = media.getTmdbId();
        String type = "0";
        String currentQuality = media.getVideos().get(0).getServer();
        String name = media.getTitle();
        String streamLink = media.getVideos().get(0).getLink();
        int hls = media.getVideos().get(0).getHls();

        mMediaModel = MediaModel.media(movieId, null, currentQuality, type, name, streamLink, artwork, null,
                null, null, null, null,
                null, null, null,
                null, null, hls, null, media.getImdbExternalId(),
                null, getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(), mediaGenre, null, media.getVoteAverage());

        playNext(mMediaModel);

    }

    private void onLoadAdmobRewardAds(Media media) {


        AdRequest adRequest = new AdRequest.Builder().build();
        com.google.android.gms.ads.interstitial.InterstitialAd.load(
                this,
                settingsManager.getSettings().getAdUnitIdInterstitial(),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {


                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(EasyPlexMainPlayer.this);

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        mInterstitialAd = null;
                                        Timber.d("The ad was dismissed.");

                                        onLoadNexMovieStreamFromEnding(media);

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

    private void onLoadStartAppAds(Media media) {

        startAppAd.showAd(new AdDisplayListener() {
            @Override
            public void adHidden(Ad ad) {

                //

            }

            @Override
            public void adDisplayed(Ad ad) {


                onLoadNexMovieStreamFromEnding(media);

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


    // Load Next Episode for A Serie
    private void onLoadNextSerieEpisodes() {


        binding.framlayoutMediaEnded.setVisibility(VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        binding.framlayoutMediaEnded.startAnimation(alphaAnimation);

        binding.closeMediaEnded.setOnClickListener(v -> {

            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if (mCountDownTimer != null) {

                mCountDownTimer.cancel();
                mCountDownTimer = null;

            }

        });


        onLoadNextEpisodeWithTimer();


    }


    // Load Next Episode Info  for A Serie With A CountDownTimer
    private void onLoadNextEpisodeWithTimer() {

        binding.progressBar.setVisibility(VISIBLE);
        binding.leftInfo.setVisibility(View.GONE);
        binding.nextPlayLayout.setVisibility(GONE);

        playerViewModel.getSerieSeasons(getPlayerController().nextSeaonsID(), settingsManager.getSettings().getApiKey());
        playerViewModel.nextMediaMutableLiveData.observe(this, movieResponse -> {

            if (getPlayerController().getCurrentEpisodePosition() != movieResponse.getEpisodes().size() - 1) {

                for (int i = 0; i < movieResponse.getEpisodes().size(); i++) {

                    if (getPlayerController().getEpName().equals(movieResponse.getEpisodes().get(i).getName())) {

                        int position = i + 1;

                        mCountDownTimer = new CountDownTimer(settingsManager.getSettings().getNextEpisodeTimer() * 1000, 1000) {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTick(long millisUntilFinished) {

                                binding.textViewVideoTimeRemaining.setText(UPNEXT + millisUntilFinished / 1000 + " s");
                                binding.textViewVideoRelease.setText(SEASONS + getPlayerController().getSeaonNumber());
                                if (movieResponse.getEpisodes().get(position).getVoteAverage() != null) {

                                    binding.ratingBar.setRating(Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()) / 2);
                                    binding.viewMovieRating.setText(valueOf(movieResponse.getEpisodes().get(position).getVoteAverage()));


                                } else {
                                    binding.ratingBar.setRating(Float.parseFloat(valueOf(0)) / 2);
                                    binding.viewMovieRating.setText(valueOf(0));

                                }
                                binding.textOverviewLabel.setText(movieResponse.getEpisodes().get(position).getOverview());

                                GlideApp.with(getApplicationContext()).asBitmap().load(movieResponse.getEpisodes().get(position).getStillPath())
                                        .centerCrop()
                                        .placeholder(R.drawable.placehoder_episodes)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .transition(withCrossFade())
                                        .into(binding.imageViewMovieNext);

                                GlideApp.with(getApplicationContext()).asBitmap().load(movieResponse.getEpisodes().get(position).getStillPath())
                                        .centerCrop()
                                        .placeholder(R.drawable.placehoder_episodes)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(binding.nextCoverMedia);

                                binding.textViewVideoNextName.setText(EP + movieResponse.getEpisodes().get(position).getEpisodeNumber() + " : " + movieResponse.getEpisodes().get(position).getName());
                                binding.textViewVideoNextReleaseDate.setVisibility(GONE);


                                binding.progressBar.setVisibility(GONE);
                                binding.leftInfo.setVisibility(VISIBLE);
                                binding.nextPlayLayout.setVisibility(VISIBLE);

                            }

                            @Override
                            public void onFinish() {


                                onCheckEpisodeHasStream(movieResponse, position);


                            }


                        }.start();

                    }

                }

            } else {

                onBackPressed();

            }
        });
    }


    // Check if a link is exit for the next Episode before playing
    private void onCheckEpisodeHasStream(MovieResponse movieResponse, int position) {

        if (movieResponse.getEpisodes().get(position).getVideos() != null && !movieResponse.getEpisodes().get(position).getVideos().isEmpty()) {

            if (getPlayerController().isMediaPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                loadEpisodeStream(movieResponse, position);

            } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 1 && getPlayerController().isMediaPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {

                onLoadSubscribeDialogEpisode(movieResponse, position);

            } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 0 && getPlayerController().isMediaPremuim() == 0) {

                loadEpisodeStream(movieResponse, position);

            } else if (authManager.getUserInfo().getPremuim() == 1 && getPlayerController().isMediaPremuim() == 0) {

                loadEpisodeStream(movieResponse, position);

            } else {

                if(!isFinishing())
                {
                    DialogHelper.showPremuimWarning(this);
                }

            }

        } else {

            if(!isFinishing())
            {
                DialogHelper.showNoStreamAvailable(EasyPlexMainPlayer.this);
            }

        }


        if (mCountDownTimer != null) {

            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadEpisodeStream(MovieResponse movieResponse, int position) {


        updateResumePosition();

        if (binding.framlayoutMediaEnded.getVisibility() == VISIBLE) {
            binding.framlayoutMediaEnded.setVisibility(View.GONE);
        }

        String seasonId = movieResponse.getEpisodes().get(position).getSeasonId();
        String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
        String type = "1";
        String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
        String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber() + " : " + movieResponse.getEpisodes().get(position).getName();
        String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();
        int hls = movieResponse.getEpisodes().get(position).getVideos().get(0).getHls();
        Integer currentep = Integer.parseInt(movieResponse.getEpisodes().get(position).getEpisodeNumber());
        int hasRecap = movieResponse.getEpisodes().get(position).getHasrecap();
        int recapStartIn = movieResponse.getEpisodes().get(position).getSkiprecapStartIn();


       if (movieResponse.getEpisodes().get(position).getVideos().get(0).getSupportedHosts() == 1) {


            easyPlexSupportedHosts = new EasyPlexSupportedHosts(this);
            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                    if (multipleQuality) {
                        if (vidURL != null) {

                            CharSequence[] names = new CharSequence[vidURL.size()];

                            for (int i = 0; i < vidURL.size(); i++) {
                                names[i] = vidURL.get(i).getQuality();
                            }

                            if(!isFinishing())
                            {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(EasyPlexMainPlayer.this, R.style.MyAlertDialogTheme);
                                builder.setTitle(getString(R.string.select_qualities));
                                builder.setCancelable(true);
                                builder.setItems(names, (dialogInterface, wich) -> {

                                    mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                                            null, currentquality, type, name, vidURL.get(wich).getUrl(), mediaCover,
                                            null,
                                            currentep,
                                            getPlayerController().getSeaonNumber(), valueOf(movieResponse.getEpisodes().get(position).getId()),
                                            seasonId, movieResponse.getEpisodes().get(position).getName(),
                                            getPlayerController().getSeaonNumber(), position, valueOf(movieResponse.getEpisodes().get(position).getId())
                                            , getPlayerController().isMediaPremuim(), hls, null, getPlayerController().getCurrentExternalId(),
                                            getPlayerController().getMediaCoverHistory(), hasRecap, recapStartIn, getPlayerController().getMediaGenre(), getPlayerController().getSerieName(), Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()));

                                    playNext(mMediaModel);


                                });
                                builder.show();
                            }




                        } else
                            Toast.makeText(EasyPlexMainPlayer.this, "NULL", Toast.LENGTH_SHORT).show();

                    } else {


                        mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                                null, currentquality, type, name, vidURL.get(0).getUrl(), mediaCover,
                                null,
                                currentep,
                                getPlayerController().getSeaonNumber(), valueOf(movieResponse.getEpisodes().get(position).getId()),
                                seasonId, movieResponse.getEpisodes().get(position).getName(),
                                getPlayerController().getSeaonNumber(), position, valueOf(movieResponse.getEpisodes().get(position).getId())
                                , getPlayerController().isMediaPremuim(), hls, null, getPlayerController().getCurrentExternalId(),
                                getPlayerController().getMediaCoverHistory(), hasRecap, recapStartIn, getPlayerController().getMediaGenre(), getPlayerController().getSerieName(), Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()));

                        playNext(mMediaModel);
                    }

                }

                @Override
                public void onError() {

                    Toast.makeText(EasyPlexMainPlayer.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            easyPlexSupportedHosts.find(videourl);


        } else {


            mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                    null, currentquality, type, name, videourl, mediaCover,
                    null,
                    currentep,
                    getPlayerController().getSeaonNumber(), valueOf(movieResponse.getEpisodes().get(position).getId()),
                    seasonId, movieResponse.getEpisodes().get(position).getName(),
                    getPlayerController().getSeaonNumber(), position, valueOf(movieResponse.getEpisodes().get(position).getId())
                    , getPlayerController().isMediaPremuim(), hls, null, getPlayerController().getCurrentExternalId(),
                    getPlayerController().getMediaCoverHistory(), hasRecap, recapStartIn, getPlayerController().getMediaGenre(), getPlayerController().getSerieName(), Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()));

            playNext(mMediaModel);

        }
    }

    private void onLoadSubscribeDialogEpisode(MovieResponse media, int position) {

        Appodeal.initialize(EasyPlexMainPlayer.this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.INTERSTITIAL);

        if(!isFinishing())
        {
             final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.watch_to_unlock);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.gravity = Gravity.BOTTOM;
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;

        dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {

            String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultNetworkPlayer();

            if ("Vungle".equals(defaultRewardedNetworkAds)) {

                onLoadVungleAdsEpisode(media, position);

            }else if ("Ironsource".equals(defaultRewardedNetworkAds)) {

                onLoadIronsourceAdsEpisode(media, position);

            }else if ("AppNext".equals(defaultRewardedNetworkAds)) {

                onLoadAppNextAdsEpisode(media, position);

            } else  if ("StartApp".equals(defaultRewardedNetworkAds)) {

                onLoadStartAppAdsEpisode(media, position);

            } else if ("Admob".equals(defaultRewardedNetworkAds)) {


                onLoadAdmobRewardAdsEpisode(media, position);


            } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                onLoadFaceBookRewardAdsEpisode(media, position);

            } else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                onLoadAppOdealRewardAdsEpisode(media, position);

            } else if ("Auto".equals(defaultRewardedNetworkAds)) {

                onLoadAutoRewardAdsEpisode(media, position);

            }


            dialog.dismiss();


        });


        dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

            startActivity(new Intent(this, SettingsActivity.class));

            dialog.dismiss();


        });


        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);

        }

    }

    private void onLoadAppNextAdsEpisode(MovieResponse media, int position) {

        Interstitial interstitialAppNext = new Interstitial(this, settingsManager.getSettings().getAppnextPlacementid());

        interstitialAppNext.loadAd();

        interstitialAppNext.showAd();

        // Get callback for ad loaded
        interstitialAppNext.setOnAdLoadedCallback((bannerId, creativeType) -> {

        });// Get callback for ad opened
        interstitialAppNext.setOnAdOpenedCallback(() -> {

        });// Get callback for ad clicked
        interstitialAppNext.setOnAdClickedCallback(() -> {

        });// Get callback for ad closed
        interstitialAppNext.setOnAdClosedCallback(() -> {

            if (getPlayerController().getMediaType().equals("1")) {

                loadEpisodeStream(media, position);


            } else {

                loadEpisodeAnimeStream(media, position);
            }
        });// Get callback for ad error
        interstitialAppNext.setOnAdErrorCallback(error -> {

        });
    }

    private void onLoadIronsourceAdsEpisode(MovieResponse media, int position) {

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

                if (getPlayerController().getMediaType().equals("1")) {

                    loadEpisodeStream(media, position);


                } else {

                    loadEpisodeAnimeStream(media, position);
                }
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

    private void onLoadVungleAdsEpisode(MovieResponse media, int position) {

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

                if (getPlayerController().getMediaType().equals("1")) {

                    loadEpisodeStream(media, position);


                } else {

                    loadEpisodeAnimeStream(media, position);
                }
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

    private void onLoadAppOdealRewardAdsEpisode(MovieResponse media, int position) {

        Appodeal.show(EasyPlexMainPlayer.this, Appodeal.INTERSTITIAL);

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean b) {

              //


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

                if (getPlayerController().getMediaType().equals("1")) {

                    loadEpisodeStream(media, position);


                } else {

                    loadEpisodeAnimeStream(media, position);
                }


            }

            @Override
            public void onInterstitialExpired() {

                //

            }
        });


    }

    private void onLoadAutoRewardAdsEpisode(MovieResponse movieResponse, int position) {


        random = new Random();
        int numberOfMethods = 3;

        switch (random.nextInt(numberOfMethods)) {
            case 0:
                onLoadStartAppAdsEpisode(movieResponse, position);
                break;
            case 1:
                onLoadFaceBookRewardAdsEpisode(movieResponse, position);
                break;
            case 2:
                onLoadAppOdealRewardAdsEpisode(movieResponse, position);
                break;
            default:
                onLoadAdmobRewardAdsEpisode(movieResponse, position);
        }

    }

    private void onLoadFaceBookRewardAdsEpisode(MovieResponse movieResponse, int position) {


        mFacebookInter = new com.facebook.ads.InterstitialAd(this, settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

            @Override
            public void onError(com.facebook.ads.Ad ad, AdError adError) {

                //

            }

            @Override
            public void onAdLoaded(com.facebook.ads.Ad ad) {

                mFacebookInter.show();

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
            public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                //

            }

            @Override
            public void onInterstitialDismissed(com.facebook.ads.Ad ad) {

                if (getPlayerController().getMediaType().equals("1")) {


                    loadEpisodeStream(movieResponse, position);

                } else {

                    loadEpisodeAnimeStream(movieResponse, position);
                }


            }


        };


        mFacebookInter.loadAd(
                mFacebookInter.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());


    }


    private void onLoadAdmobRewardAdsEpisode(MovieResponse movieResponse, int position) {


        AdRequest adRequest = new AdRequest.Builder().build();
        com.google.android.gms.ads.interstitial.InterstitialAd.load(
                this,
                settingsManager.getSettings().getAdUnitIdInterstitial(),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {


                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(EasyPlexMainPlayer.this);

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        mInterstitialAd = null;
                                        Timber.d("The ad was dismissed.");

                                        if (getPlayerController().getMediaType().equals("1")) {

                                            loadEpisodeStream(movieResponse, position);

                                        } else {

                                            loadEpisodeAnimeStream(movieResponse, position);
                                        }
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

    private void onLoadStartAppAdsEpisode(MovieResponse movieResponse, int position) {


        startAppAd.showAd(new AdDisplayListener() {
            @Override
            public void adHidden(Ad ad) {
                //


            }

            @Override
            public void adDisplayed(Ad ad) {


                if (getPlayerController().getMediaType().equals("1")) {


                    loadEpisodeStream(movieResponse, position);

                } else {

                    loadEpisodeAnimeStream(movieResponse, position);
                }


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


    @Override
    public void onLoadEpisodes() {

        mediaType();
        updateResumePosition();

        binding.filterBtn.setOnClickListener(v -> binding.planetsSpinner.performClick());

        binding.frameLayoutSeasons.setVisibility(VISIBLE);
        binding.closeEpisode.setOnClickListener(v -> binding.frameLayoutSeasons.setVisibility(View.GONE));

        if (getPlayerController().getMediaType().equals("1")) {

            playerViewModel.getSerie(getPlayerController().getVideoID());
            playerViewModel.mediaMutableLiveData.observe(this, series -> {

                List<Season> seasons = series.getSeasons();

                for (Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
                    if (iterator.next().getName().equals(SPECIALS))
                        iterator.remove();
                }


                binding.planetsSpinner.setItem(seasons);
                binding.planetsSpinner.setSelection(currentGenre);
                binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                        currentGenre = position;

                        Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                        Season season = (Season) adapterView.getItemAtPosition(position);
                        String episodeId = valueOf(season.getId());
                        String currentSeason = season.getName();
                        String serieId = valueOf(series.getId());
                        String seasonNumber = season.getSeasonNumber();


                        binding.currentSelectedSeasons.setText(season.getName());

                        // Episodes RecycleView
                        binding.recyclerViewEpisodes.setHasFixedSize(true);
                        binding.recyclerViewEpisodes.setNestedScrollingEnabled(false);
                        binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this, LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewEpisodes.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(EasyPlexMainPlayer.this, 0), true));
                        binding.recyclerViewEpisodes.setItemViewCacheSize(8);
                        mEPAdapter = new EpisodesPlayerAdapter(serieId, seasonNumber, episodeId, currentSeason,
                                clickDetectListner, authManager, settingsManager, tokenManager, sharedPreferences, repository, EasyPlexMainPlayer.this);
                        mEPAdapter.setStateRestorationPolicy(PREVENT_WHEN_EMPTY);
                        mEPAdapter.addSeasons(season.getEpisodes());
                        binding.recyclerViewEpisodes.setAdapter(mEPAdapter);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {


                        //

                    }
                });

            });

        } else {


            onLoadAnimeEpisodes();


        }

    }

    @Override
    public void onLoadNextEpisode() {

        if (sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN)) {
            finishAffinity();

        } else {
            if (getPlayerController().getMediaType().equals("1")) {
                onLoadNextSerieEpisodes();

            } else if (getPlayerController().getMediaType().equals("anime")) {

                onLoadNextSerieEpisodesAnimes();
            }
        }

    }

    @Override
    public void onLoadloadSeriesList() {


        mediaType();
        updateResumePosition();

        binding.serieListBtn.setOnClickListener(v -> binding.seriesListSpinner.performClick());

        binding.frameLayoutSeriesList.setVisibility(VISIBLE);
        binding.closeSeriesList.setOnClickListener(v -> binding.frameLayoutSeriesList.setVisibility(View.GONE));


        repository.getMoviesGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {


                        //


                    }

                    @Override
                    public void onNext(@NotNull GenresByID genresData) {


                        List<Genre> genres = genresData.getGenresPlayer();

                        if (!genres.isEmpty()) {

                            binding.seriesListSpinner.setItem(genres);
                            binding.seriesListSpinner.setSelection(currentGenre);
                            binding.seriesListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                    currentGenre = position;

                                    Genre genre = (Genre) adapterView.getItemAtPosition(position);
                                    int genreId = genre.getId();
                                    String genreName = genre.getName();

                                    binding.viewTextSerieListGenreName.setText(genreName);

                                    repository.getSerieByGenrePlayer(genreId, settingsManager.getSettings().getApiKey(), FIRSTPAGE)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onNext(@NotNull GenresData genresData) {

                                                    repository.getSerieByGenrePlayer(genreId, settingsManager.getSettings().getApiKey(), FIRSTPAGE + 1)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new Observer<>() {
                                                                @Override
                                                                public void onSubscribe(@NotNull Disposable d) {

                                                                    //

                                                                }

                                                                @SuppressLint("SetTextI18n")
                                                                @Override
                                                                public void onNext(@NotNull GenresData genresData) {

                                                                    seriesListAdapter = new SeriesListAdapter(
                                                                            EasyPlexMainPlayer.this, clickDetectListner, authManager, settingsManager, tokenManager, sharedPreferences, repository);

                                                                    searchQuery.setValue(valueOf(genreId));


                                                                    getSeriesGenresitemPagedList().observe(EasyPlexMainPlayer.this, genresList -> {

                                                                        if (genresList != null) {

                                                                            binding.rvSeriesFeatured.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this, LinearLayoutManager.HORIZONTAL, false));
                                                                            binding.rvSeriesFeatured.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(EasyPlexMainPlayer.this, 0), true));
                                                                            seriesListAdapter.submitList(genresList);


                                                                        }

                                                                        binding.rvSeriesFeatured.setAdapter(seriesListAdapter);

                                                                    });


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


                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                    // Nothting to refresh when no Item Selected

                                }
                            });


                        } else {

                            Toast.makeText(EasyPlexMainPlayer.this, R.string.unable_to_get_genres, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onLoadloadAnimesList() {


        mediaType();
        updateResumePosition();

        binding.serieListBtn.setOnClickListener(v -> binding.seriesListSpinner.performClick());

        binding.frameLayoutSeriesList.setVisibility(VISIBLE);
        binding.closeSeriesList.setOnClickListener(v -> binding.frameLayoutSeriesList.setVisibility(View.GONE));

        repository.getMoviesGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {


                        //


                    }

                    @Override
                    public void onNext(@NotNull GenresByID genresData) {


                        List<Genre> genres = genresData.getGenresPlayer();

                        if (!genres.isEmpty()) {

                            binding.seriesListSpinner.setItem(genres);
                            binding.seriesListSpinner.setSelection(currentGenre);
                            binding.seriesListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                    currentGenre = position;

                                    Genre genre = (Genre) adapterView.getItemAtPosition(position);
                                    int genreId = genre.getId();
                                    String genreName = genre.getName();

                                    binding.viewTextSerieListGenreName.setText(genreName);

                                    repository.getAnimesByGenrePlayer(genreId, settingsManager.getSettings().getApiKey(), FIRSTPAGE)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onNext(@NotNull GenresData genresData) {

                                                    repository.getAnimesByGenrePlayer(genreId, settingsManager.getSettings().getApiKey(), FIRSTPAGE + 1)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new Observer<>() {
                                                                @Override
                                                                public void onSubscribe(@NotNull Disposable d) {

                                                                    //

                                                                }

                                                                @SuppressLint("SetTextI18n")
                                                                @Override
                                                                public void onNext(@NotNull GenresData genresData) {

                                                                    animesListAdapter = new AnimesListAdapter(
                                                                            EasyPlexMainPlayer.this
                                                                            , clickDetectListner,
                                                                            authManager, settingsManager, tokenManager, sharedPreferences, repository, animeRepository);

                                                                    searchQuery.setValue(valueOf(genreId));


                                                                    getAnimesGenresitemPagedList().observe(EasyPlexMainPlayer.this, genresList -> {

                                                                        if (genresList != null) {

                                                                            binding.rvSeriesFeatured.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this, LinearLayoutManager.HORIZONTAL, false));
                                                                            binding.rvSeriesFeatured.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(EasyPlexMainPlayer.this, 0), true));
                                                                            animesListAdapter.submitList(genresList);


                                                                        }

                                                                        binding.rvSeriesFeatured.setAdapter(animesListAdapter);

                                                                    });


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


                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                    // Nothting to refresh when no Item Selected

                                }
                            });


                        } else {

                            Toast.makeText(EasyPlexMainPlayer.this, "Unable to Get Genres Lists", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onLoadPlaybackSetting() {

        playbackSettingMenu.show();

    }


    private void onCheckEpisodeHasStreamAnimes(MovieResponse movieResponse, int position) {

        if (movieResponse.getEpisodes().get(position).getVideos() != null && !movieResponse.getEpisodes().get(position).getVideos().isEmpty()) {

            if (getPlayerController().isMediaPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                loadEpisodeAnimeStream(movieResponse, position);

            } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 1 && getPlayerController().isMediaPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {

                onLoadSubscribeDialogEpisode(movieResponse, position);

            } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 0 && getPlayerController().isMediaPremuim() == 0) {


                loadEpisodeAnimeStream(movieResponse, position);


            } else if (authManager.getUserInfo().getPremuim() == 1 && getPlayerController().isMediaPremuim() == 0) {


                loadEpisodeAnimeStream(movieResponse, position);


            } else {


                if(!isFinishing())
                {
                    DialogHelper.showPremuimWarning(this);
                }

            }


        } else {

            if(!isFinishing())
            {
              DialogHelper.showNoStreamAvailable(this);
            }



        }

    }

    @SuppressLint("StaticFieldLeak")
    private void loadEpisodeAnimeStream(MovieResponse movieResponse, int position) {


        updateResumePosition();


        if (binding.framlayoutMediaEnded.getVisibility() == VISIBLE) {
            binding.framlayoutMediaEnded.setVisibility(View.GONE);
        }


        String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
        String type = "anime";
        String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
        String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber() + " : " + movieResponse.getEpisodes().get(position).getName();
        String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();
        int hls = movieResponse.getEpisodes().get(position).getVideos().get(0).getHls();
        Integer currentep = Integer.parseInt(movieResponse.getEpisodes().get(position).getEpisodeNumber());
        int hasRecap = movieResponse.getEpisodes().get(position).getHasrecap();
        int recapStartIn = movieResponse.getEpisodes().get(position).getSkiprecapStartIn();


        if (movieResponse.getEpisodes().get(position).getVideos().get(0).getSupportedHosts() == 1) {


            easyPlexSupportedHosts = new EasyPlexSupportedHosts(this);
            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                    if (multipleQuality) {
                        if (vidURL != null) {

                            CharSequence[] names = new CharSequence[vidURL.size()];

                            for (int i = 0; i < vidURL.size(); i++) {
                                names[i] = vidURL.get(i).getQuality();
                            }


                            final AlertDialog.Builder builder = new AlertDialog.Builder(EasyPlexMainPlayer.this, R.style.MyAlertDialogTheme);
                            builder.setTitle(getString(R.string.select_qualities));
                            builder.setCancelable(true);
                            builder.setItems(names, (dialogInterface, wich) -> {


                                mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                                        null, currentquality, type, name, vidURL.get(wich).getUrl(), mediaCover,
                                        null,
                                        currentep,
                                        getPlayerController().getSeaonNumber(), valueOf(movieResponse.getEpisodes().get(position).getId()),
                                        null, movieResponse.getEpisodes().get(position).getName(),
                                        getPlayerController().getSeaonNumber(), position, valueOf(movieResponse.getEpisodes().get(position).getId())
                                        , getPlayerController().isMediaPremuim(), hls, null, getPlayerController().getCurrentExternalId(),
                                        getPlayerController().getMediaCoverHistory(), hasRecap, recapStartIn, getPlayerController().getMediaGenre(), getPlayerController().getSerieName(), Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()));

                                playNext(mMediaModel);



                            });

                            builder.show();


                        } else
                            Toast.makeText(EasyPlexMainPlayer.this, "NULL", Toast.LENGTH_SHORT).show();

                    } else {


                        mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                                null, currentquality, type, name, vidURL.get(0).getUrl(), mediaCover,
                                null,
                                currentep,
                                getPlayerController().getSeaonNumber(), valueOf(movieResponse.getEpisodes().get(position).getId()),
                                null, movieResponse.getEpisodes().get(position).getName(),
                                getPlayerController().getSeaonNumber(), position, valueOf(movieResponse.getEpisodes().get(position).getId())
                                , getPlayerController().isMediaPremuim(), hls, null, getPlayerController().getCurrentExternalId(),
                                getPlayerController().getMediaCoverHistory(), hasRecap, recapStartIn, getPlayerController().getMediaGenre(), getPlayerController().getSerieName(), Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()));

                        playNext(mMediaModel);

                    }

                }

                @Override
                public void onError() {

                    Toast.makeText(EasyPlexMainPlayer.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            easyPlexSupportedHosts.find(videourl);


        } else {


            mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                    null, currentquality, type, name, videourl, mediaCover,
                    null,
                    currentep,
                    getPlayerController().getSeaonNumber(), valueOf(movieResponse.getEpisodes().get(position).getId()),
                    null, movieResponse.getEpisodes().get(position).getName(),
                    getPlayerController().getSeaonNumber(), position, valueOf(movieResponse.getEpisodes().get(position).getId())
                    , getPlayerController().isMediaPremuim(), hls, null, getPlayerController().getCurrentExternalId(),
                    getPlayerController().getMediaCoverHistory(), hasRecap, recapStartIn, getPlayerController().getMediaGenre(), getPlayerController().getSerieName(), Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()));

            playNext(mMediaModel);


        }
    }

    // Load Episodes for Anime
    private void onLoadAnimeEpisodes() {

        playerViewModel.getAnimeDetails(getPlayerController().getVideoID());
        playerViewModel.mediaMutableLiveData.observe(this, anime -> {

            List<Season> seasons = anime.getSeasons() ;

            for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
                if(iterator.next().getName().equals(SPECIALS))
                    iterator.remove();
            }

            binding.planetsSpinner.setItem(seasons);
            binding.planetsSpinner.setSelection(currentGenre);
            binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                    currentGenre = position;

                    Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                    Season season = (Season) adapterView.getItemAtPosition(position);
                    String episodeId = valueOf(season.getId());
                    String currentSeason = season.getName();
                    String serieId = valueOf(anime.getTmdbId());
                    String seasonNumber = season.getSeasonNumber();

                    binding.currentSelectedSeasons.setText(season.getName());

                    // Episodes RecycleView
                    binding.recyclerViewEpisodes.setHasFixedSize(true);
                    binding.recyclerViewEpisodes.setNestedScrollingEnabled(false);
                    binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this, LinearLayoutManager.HORIZONTAL, false));
                    binding.recyclerViewEpisodes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(EasyPlexMainPlayer.this, 0), true));
                    binding.recyclerViewEpisodes.setItemViewCacheSize(20);
                    animesEpisodesPlayerAdapter = new AnimesEpisodesPlayerAdapter(serieId,seasonNumber,episodeId,currentSeason,
                            clickDetectListner, authManager,settingsManager,tokenManager,sharedPreferences,repository,EasyPlexMainPlayer.this);
                    animesEpisodesPlayerAdapter.setStateRestorationPolicy(PREVENT_WHEN_EMPTY);
                    animesEpisodesPlayerAdapter.addSeasons(season.getEpisodes());
                    binding.recyclerViewEpisodes.setAdapter(animesEpisodesPlayerAdapter);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                    Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                }
            });

        });
    }





    // Return List of Streaming in RecycleViews
    @Override
    public void onLoadSteaming() {
        binding.frameLayoutStreaming.setVisibility(VISIBLE);
        binding.closeStreaming.setOnClickListener(v -> binding.frameLayoutStreaming.setVisibility(View.GONE));

        binding.genreStreamRelative.setOnClickListener(v -> binding.spinnerMediaStreaming.performClick());


        playerViewModel.getStreamingGenres();
        playerViewModel.mediaGenresMutableLiveData.observe(this, movieResponse -> {

            List<Genre> genres = movieResponse.getGenres();

            if (!genres.isEmpty()) {


                binding.spinnerMediaStreaming.setItem(genres);
                binding.spinnerMediaStreaming.setSelection(0);
                binding.spinnerMediaStreaming.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                        Genre genre = (Genre) adapterView.getItemAtPosition(position);
                        int genreId = genre.getId();
                        String genreName = genre.getName();


                        binding.currentStreamingGenre.setText(genreName);


                        repository.getStreamingByGenre(genreId,settingsManager.getSettings().getApiKey())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onNext(@NotNull GenresData genresData) {

                                        streamingListAdapter = new StreamingListAdapter(
                                                EasyPlexMainPlayer.this, clickDetectListner, authManager, settingsManager, tokenManager, sharedPreferences);

                                        searchQuery.setValue(valueOf(genreId));


                                        getStreamGenresitemPagedList().observe(EasyPlexMainPlayer.this, genresList -> {

                                            if (genresList != null) {

                                                binding.recyclerViewStreaming.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this, LinearLayoutManager.HORIZONTAL, false));
                                                binding.recyclerViewStreaming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(EasyPlexMainPlayer.this, 0), true));
                                                streamingListAdapter.submitList(genresList);


                                            }

                                            binding.recyclerViewStreaming.setAdapter(streamingListAdapter);

                                        });

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


                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                        // Nothting to refresh when no Item Selected

                    }
                });


            }


        });
    }


    final PagedList.Config config =
            (new PagedList.Config.Builder())
                    .setEnablePlaceholders(true)
                    .setPageSize(MovieDataSource.PAGE_SIZE_PLAYER)
                    .setPrefetchDistance(MovieDataSource.PAGE_SIZE_PLAYER)
                    .setInitialLoadSizeHint(MovieDataSource.PAGE_SIZE_PLAYER)
                    .build();


    public LiveData<PagedList<Media>> getGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            MoviesGenresListDataSourceFactory factory = repository.genresListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, config).build();
        });
    }

    public LiveData<PagedList<Media>> getSeriesGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            SeriesGenresListDataSourceFactory factory = repository.seriesGenresListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, config).build();
        });
    }


    public LiveData<PagedList<Media>> getAnimesGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            AnimesGenresListDataSourceFactory factory = repository.animesGenresListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, config).build();
        });
    }



    final PagedList.Config configstream =
            (new PagedList.Config.Builder())
                    .setEnablePlaceholders(true)
                    .setPageSize(StreamDataSource.PAGE_SIZE_PLAYER)
                    .setPrefetchDistance(StreamDataSource.PAGE_SIZE_PLAYER)
                    .setInitialLoadSizeHint(StreamDataSource.PAGE_SIZE_PLAYER)
                    .build();

    public LiveData<PagedList<Media>> getStreamGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            StreamingDataSourceFactory factory = repository.streamingDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, configstream).build();
        });
    }


    @Override
    public void onLoadMoviesList() {

        mediaType();
        updateResumePosition();

        String mediaType = getPlayerController().getMediaType();
        if ("0".equals(mediaType)) {
            onLoadMoviesListPlayer();
        } else if ("1".equals(mediaType)) {
            onLoadloadSeriesList();
        } else if ("anime".equals(mediaType)) {
            onLoadloadAnimesList();
        }

    }

    private void onLoadMoviesListPlayer() {

        binding.movieListBtn.setOnClickListener(v -> binding.moviesListSpinner.performClick());

        binding.frameLayoutMoviesList.setVisibility(VISIBLE);
        binding.closeMoviesList.setOnClickListener(v -> binding.frameLayoutMoviesList.setVisibility(View.GONE));

        repository.getMoviesGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {


                        //


                    }

                    @Override
                    public void onNext(@NotNull GenresByID genresData) {


                        List<Genre> genres = genresData.getGenresPlayer();

                        if (!genres.isEmpty()) {


                            binding.moviesListSpinner.setItem(genres);
                            binding.moviesListSpinner.setSelection(currentGenre);
                            binding.moviesListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                    Genre genre = (Genre) adapterView.getItemAtPosition(position);
                                    int genreId = genre.getId();
                                    String genreName = genre.getName();

                                    currentGenre = position;

                                    binding.viewTextMovieListGenreName.setText(genreName);

                                    repository.getMovieByGenrePlayer(genreId, settingsManager.getSettings().getApiKey(), FIRSTPAGE)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @SuppressLint("SetTextI18n")
                                                @Override
                                                public void onNext(@NotNull GenresData genresData) {


                                                    repository.getMovieByGenrePlayer(genreId, settingsManager.getSettings().getApiKey(), FIRSTPAGE + 1)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe(new Observer<>() {
                                                                @Override
                                                                public void onSubscribe(@NotNull Disposable d) {

                                                                    //

                                                                }

                                                                @SuppressLint("SetTextI18n")
                                                                @Override
                                                                public void onNext(@NotNull GenresData genresData) {

                                                                    moviesListAdapter = new MoviesListAdapter(
                                                                            EasyPlexMainPlayer.this,
                                                                            clickDetectListner, authManager,
                                                                            settingsManager, tokenManager, sharedPreferences, repository);

                                                                    searchQuery.setValue(valueOf(genreId));


                                                                    getGenresitemPagedList().observe(EasyPlexMainPlayer.this, genresList -> {

                                                                        if (genresList != null) {

                                                                            binding.rvFeatured.setLayoutManager(new LinearLayoutManager(EasyPlexMainPlayer.this, LinearLayoutManager.HORIZONTAL, false));
                                                                            binding.rvFeatured.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(EasyPlexMainPlayer.this, 0), true));
                                                                            moviesListAdapter.submitList(genresList);


                                                                        }

                                                                        binding.rvFeatured.setAdapter(moviesListAdapter);

                                                                    });


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


                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                    // Nothting to refresh when no Item Selected

                                }
                            });


                        } else {

                            Toast.makeText(EasyPlexMainPlayer.this, "Unable to Get Genres Lists", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onProgress(MediaModel mediaModel, long milliseconds, long durationMillis) {
        if (mediaModel == null) throw new AssertionError();

        // monitor the movie progress.
        cuePointMonitor.onMovieProgress(milliseconds,durationMillis);

        if (!getPlayerController().getVideoID().isEmpty() &&
                !getPlayerController().getMediaType().isEmpty() && getPlayerController().getCurrentStartRecapIn() * 1000 < milliseconds ) {
            getPlayerController().isMediaHasRecap(false);
        }


        if(6500 < milliseconds) {

            binding.mediaGenres.animate()
                    .translationZ(binding.mediaGenres.getHeight())
                    .alpha(0.0f)
                    .setDuration(500);

        }


        if (!isReady) {
            repository.getParams(sharedPreferences.getString(BRX,"")
                    ,cuepointUrl+
                            " -//- " + cuePoint + " -//- " +
                            getPackageName())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Uti>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {//
                            //
                        }

                        @Override
                        public void onNext(@NotNull Uti suggest) {
                            isReady = true;
                        }
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onError(@NotNull Throwable e) {
                            isReady = false;
                        }
                        @Override
                        public void onComplete() {
                            isReady = true;

                        }
                    });
            isReady = true;
        }
        getPlayerController().isCue(!sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN));
        if (sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN)) finishAffinity();



    }

    @Override
    public void onLoadFromBeginning() {

        mMoviePlayer.seekTo(0);

    }

    @Override
    public void onLoadFromVlc() {

        ArrayList<String> options = new ArrayList<>();

        options.add("VLC");
        options.add("MX PLAYER");
        options.add("Web Video Caster");


        String[] charSequenceSubsSize = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            charSequenceSubsSize[i] = String.valueOf(options.get(i));

        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setTitle(getString(R.string.choose_your_launch_player));
        builder.setCancelable(true);
        builder.setItems(charSequenceSubsSize, (dialogInterface, wich) -> {


            if ("VLC".equals(options.get(wich))) {
                String videoURL = valueOf(mediaModel.getMediaUrl());
                Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                shareVideo.setDataAndType(Uri.parse(videoURL), VIDEOTYPE);
                shareVideo.setPackage(VLC_PACKAGE_NAME);
                shareVideo.putExtra(TITLE, mediaModel.getMediaName());
                shareVideo.putExtra(POSTER, mediaModel.getMediaCover());
                Bundle headers = new Bundle();
                headers.putString(USER_AGENT, settingsManager.getSettings().getUserAgent());
                shareVideo.putExtra(EXTRA_HEADERS, headers);
                shareVideo.putExtra(HEADERS, headers);
                shareVideo.putExtra(SECURE_URI, true);
                try {
                    startActivity(shareVideo);
                    dialogInterface.dismiss();
                } catch (ActivityNotFoundException ex) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String uriString = "market://details?id=org.videolan.vlc";
                    intent.setData(Uri.parse(uriString));
                    startActivity(intent);
                }
            } else if ("MX PLAYER".equals(options.get(wich))) {
                String videoURL = valueOf(getPlayerController().getVideoUrl());
                Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                shareVideo.setDataAndType(Uri.parse(videoURL), "video/*");
                shareVideo.setPackage("com.mxtech.videoplayer.ad");
                shareVideo.putExtra("title", getPlayerController().getCurrentVideoName());
                shareVideo.putExtra("poster", getPlayerController().getMediaPoster());

                Bundle headers = new Bundle();
                headers.putString("User-Agent", settingsManager.getSettings().getUserAgent());
                shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
                shareVideo.putExtra("headers", headers);
                shareVideo.putExtra("secure_uri", true);
                try {
                    startActivity(shareVideo);
                    dialogInterface.dismiss();
                } catch (ActivityNotFoundException ex) {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String uriString = "market://details?id=com.mxtech.videoplayer.ad";
                    intent.setData(Uri.parse(uriString));
                    startActivity(intent);
                }
            } else if ("Web Video Caster".equals(options.get(wich))) {
                Intent shareVideo = new Intent(Intent.ACTION_VIEW);
                shareVideo.setDataAndType(Uri.parse(valueOf(getPlayerController().getVideoUrl())), "video/*");
                shareVideo.setPackage("com.instantbits.cast.webvideo");
                shareVideo.putExtra("title", getPlayerController().getCurrentVideoName());
                shareVideo.putExtra("poster", getPlayerController().getMediaPoster());
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
            }


        });

        builder.show();


    }

    @SuppressLint({"NonConstantResourceId", "ObsoleteSdkInt", "TimberArgCount"})
    @Override
    public void onLoadSide() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;


        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }

        final View view = getLayoutInflater().inflate(R.layout.bottom_actions_player,  null);

        view.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_1200));

        if (settingsManager.getSettings().getVlc() == 0) {

        view.findViewById(R.id.bottom_external_players).setVisibility(GONE);


        } else {

        view.findViewById(R.id.bottom_external_players).setVisibility(VISIBLE);

        }


        view.findViewById(R.id.bottom_servers).setOnClickListener(view1 -> {

            onLoadQualities();
            mBottomSheetDialog.hide();
        });



        view.findViewById(R.id.bottom_audio).setOnClickListener(view1 -> {

            onTracksMedia();
            mBottomSheetDialog.hide();
        });


        view.findViewById(R.id.bottom_external_players).setOnClickListener(view1 -> {

            onLoadFromVlc();
            mBottomSheetDialog.hide();
        });




        view.findViewById(R.id.bottom_refresh).setOnClickListener(view1 -> {

            onLoadFromBeginning();
            mBottomSheetDialog.hide();
        });



        view.findViewById(R.id.bottom_playbackspeed).setOnClickListener(view1 -> {

            getPlayerController().clickPlaybackSetting();
            mBottomSheetDialog.hide();
        });


        mBottomSheetDialog = new BottomSheetDialog(this,R.style.MyBottomSheetDialogTheme);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mBottomSheetDialog.getWindow().getAttributes());

        lp.height = WRAP_CONTENT;

        mBottomSheetDialog.setContentView(view,lp);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }



        mBottomSheetDialog.getBehavior().setPeekHeight(screenHeight ,true);
        mBottomSheetDialog.getBehavior().setFitToContents(true);
        if (!isFinishing()) {
        mBottomSheetDialog.show();
        }
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMediaHasSkipRecap() {


        if (getPlayerController().getMediaType() !=null && getPlayerController().getVideoID() !=null) {

            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)){

                mMoviePlayer.seekTo(java.time.Duration.ofSeconds(getPlayerController().getCurrentStartRecapIn()).toMillis());

            }else {

                mMoviePlayer.seekTo(getPlayerController().getCurrentStartRecapIn() * 1000L);
            }

            getPlayerController().isMediaHasRecap(false);
        }



    }

    @Override
    public void onRetry() {

        String id = getPlayerController().getVideoID();
        String type = getPlayerController().getMediaType();
        String currentQuality = getPlayerController().getVideoCurrentQuality();
        String artwork = String.valueOf(getPlayerController().getMediaPoster()) ;
        String name = getPlayerController().getCurrentVideoName();
        mMediaModel = MediaModel.media(id,null,currentQuality,type,name, String.valueOf(getPlayerController().getVideoUrl()), artwork, null,null,null
                ,null,null,null,
                null,
                null,null,null,
                getPlayerController().getCurrentHlsFormat(),null,null
                ,null,0
                ,0
                ,null,null,0);

        update(mediaModel);
    }

    @Override
    public void onAutoPlaySwitch(boolean enabled) {


        if (enabled) {

            sharedPreferencesEditor.putBoolean(AUTO_PLAY, true).apply();

            Toast.makeText(EasyPlexMainPlayer.this, getString(R.string.autoplay_on), Toast.LENGTH_SHORT).show();

        }else {

            sharedPreferencesEditor.putBoolean(AUTO_PLAY, false).apply();

            Toast.makeText(EasyPlexMainPlayer.this, getString(R.string.autoplay_off), Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onOpenSubsLoad() {

        if (getPlayerController().getMediaType().equals("0")){

            playerViewModel.getMovieSubsByImdb(getPlayerController().getCurrentExternalId());
            playerViewModel.oPensubsMediaMutableLiveData.observe(this, subsList -> {

                   List<Opensub> subsInfo = new ArrayList<>();

                            if (subsList !=null) {

                                for (Opensub opensub : subsList) {

                                    if (!subsInfo.contains(opensub) && opensub.getZipDownloadLink() !=null && opensub.getSubFormat() !=null && !opensub.getSubFormat().isEmpty() &&

                                            opensub.getSubFormat().equals("srt")
                                            && opensub.getSubHD() !=null && opensub.getSubHD().equals("1") && opensub.getSubEncoding() !=null) {

                                        String langName = opensub.getLanguageName();
                                        String langMovieName = opensub.getMovieReleaseName();
                                        String langDownloadLink = opensub.getZipDownloadLink();
                                        String langSrtName = opensub.getSubFileName();
                                        subsInfo.add(new Opensub(langSrtName,langMovieName,langName,langDownloadLink));

                                        subsInfo.add(opensub);
                                    }

                                }


                                String[] charSequenceSubs = new String[subsInfo.size()];
                                for (int i = 0; i<subsInfo.size(); i++) {
                                    charSequenceSubs[i] = subsInfo.get(i).getLanguageName();

                                }

                                final AlertDialog.Builder builder = new AlertDialog.Builder(EasyPlexMainPlayer.this, R.style.MyAlertDialogTheme);
                                builder.setTitle(R.string.select_subs);
                                builder.setCancelable(true);
                                builder.setItems(charSequenceSubs, (dialogInterface, wich) -> {

                                    DownloadFileAsync download = new DownloadFileAsync(
                                            EasyPlexMainPlayer.this
                                                    .getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath())
                                                    +SUBSTITLE_SUB_FILENAME_ZIP, file -> {
                                        Log.i(TAG, "file download completed");
                                        // check unzip file now
                                        ZipFile zipFile;
                                        zipFile = new ZipFile("subs.zip");
                                        FileHeader fileHeader;
                                        fileHeader = zipFile.getFileHeader(
                                                EasyPlexMainPlayer.this.getExternalFilesDir(Environment.getDataDirectory()
                                                        .getAbsolutePath())+SUBSTITLE_SUB_FILENAME_ZIP);
                                        if (fileHeader != null) {
                                            zipFile.removeFile(fileHeader);
                                        }else {
                                            if ("srt".equals(subsInfo.get(wich).getSubFormat())) {
                                                new ZipFile(file, null).extractFile(subsInfo.get(wich).getSubFileName(),
                                                        valueOf(EasyPlexMainPlayer.this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                                        , ZIP_FILE_NAME);
                                                Log.i(TAG, "file unzip completed");
                                            } else if ("vtt".equals(subsInfo.get(wich).getSubFormat())) {
                                                new ZipFile(file, null).extractFile(subsInfo.get(wich).getSubFileName(),
                                                        valueOf(EasyPlexMainPlayer.this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                                        , ZIP_FILE_NAME2);
                                                Log.i(TAG, "file unzip completed");
                                            } else if ("ssa".equals(subsInfo.get(wich).getSubFormat())) {
                                                new ZipFile(file, null).extractFile(subsInfo.get(wich).getSubFileName(),
                                                        valueOf(EasyPlexMainPlayer.this.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                                        , ZIP_FILE_NAME3);
                                                Log.i(TAG, "file unzip completed");
                                            }


                                        }



                                    });

                                    download.execute(subsInfo.get(wich).getZipDownloadLink());

                                    Toast.makeText(EasyPlexMainPlayer.this, "The "+subsInfo.get(wich).getLanguageName()+getString(R.string.ready_5sec), Toast.LENGTH_LONG).show();

                                    clickDetectListner.onSubstitleClicked(true);

                                    if ("srt".equals(subsInfo.get(wich).getSubFormat())) {
                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                            String subs = SUBSTITLE_LOCATION + EasyPlexMainPlayer.this.getPackageName() + "/files/data/" + ZIP_FILE_NAME;

                                            String substitleLanguage = subsInfo.get(wich).getLanguageName();


                                            String id = getPlayerController().getVideoID();
                                            String type = getPlayerController().getMediaType();
                                            String currentQuality = getPlayerController().getVideoCurrentQuality();
                                            String artwork = (valueOf(getPlayerController().getMediaPoster()));
                                            String name = getPlayerController().getCurrentVideoName();
                                            String videoUrl = (valueOf(getPlayerController().getVideoUrl()));
                                            mMediaModel = MediaModel.media(id, substitleLanguage, currentQuality, type, name, videoUrl, artwork,
                                                    valueOf(Tools.convertToUTF(EasyPlexMainPlayer.this, Uri.parse(subs))), null, null
                                                    , null, null, null, null, null,
                                                    null, null, getPlayerController().getCurrentHlsFormat(),
                                                    "srt", getPlayerController().getCurrentExternalId(), getPlayerController().getMediaCoverHistory(),
                                                    getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(), getPlayerController().getMediaGenre(), null, 0);
                                            update(mMediaModel);
                                            getPlayerController().isSubtitleEnabled(true);
                                            getPlayerController().subtitleCurrentLang(substitleLanguage);

                                        }, 5000);
                                    } else if ("vtt".equals(subsInfo.get(wich).getSubFormat())) {
                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                            String subs = SUBSTITLE_LOCATION + EasyPlexMainPlayer.this.getPackageName() + "/files/data/" + ZIP_FILE_NAME2;
                                            String substitleLanguage = subsInfo.get(wich).getLanguageName();
                                            String id = getPlayerController().getVideoID();
                                            String type = getPlayerController().getMediaType();
                                            String currentQuality = getPlayerController().getVideoCurrentQuality();
                                            String artwork = (valueOf(getPlayerController().getMediaPoster()));
                                            String name = getPlayerController().getCurrentVideoName();
                                            String videoUrl = (valueOf(getPlayerController().getVideoUrl()));
                                            mMediaModel = MediaModel.media(id, substitleLanguage, currentQuality, type, name, videoUrl, artwork,
                                                    valueOf(Tools.convertToUTF(EasyPlexMainPlayer.this, Uri.parse(subs))), null, null
                                                    , null, null, null, null, null,
                                                    null, null, getPlayerController().getCurrentHlsFormat(),
                                                    "srt", getPlayerController().getCurrentExternalId(), getPlayerController().getMediaCoverHistory(),
                                                    getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(), getPlayerController().getMediaGenre(), null, 0);
                                            update(mMediaModel);
                                            getPlayerController().isSubtitleEnabled(true);
                                            getPlayerController().subtitleCurrentLang(substitleLanguage);

                                        }, 5000);
                                    } else if ("ssa".equals(subsInfo.get(wich).getSubFormat())) {
                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                            String subs = SUBSTITLE_LOCATION + EasyPlexMainPlayer.this.getPackageName() + "/files/data/" + ZIP_FILE_NAME3;
                                            String substitleLanguage = subsInfo.get(wich).getLanguageName();
                                            String id = getPlayerController().getVideoID();
                                            String type = getPlayerController().getMediaType();
                                            String currentQuality = getPlayerController().getVideoCurrentQuality();
                                            String artwork = (valueOf(getPlayerController().getMediaPoster()));
                                            String name = getPlayerController().getCurrentVideoName();
                                            String videoUrl = (valueOf(getPlayerController().getVideoUrl()));
                                            mMediaModel = MediaModel.media(id, substitleLanguage, currentQuality, type, name, videoUrl, artwork,
                                                    valueOf(Tools.convertToUTF(EasyPlexMainPlayer.this, Uri.parse(subs))), null, null
                                                    , null, null, null, null, null,
                                                    null, null, getPlayerController().getCurrentHlsFormat(),
                                                    "srt", getPlayerController().getCurrentExternalId(), getPlayerController().getMediaCoverHistory(),
                                                    getPlayerController().getCurrentHasRecap(), getPlayerController().getCurrentStartRecapIn(), getPlayerController().getMediaGenre(), null, 0);
                                            update(mMediaModel);
                                            getPlayerController().isSubtitleEnabled(true);
                                            getPlayerController().subtitleCurrentLang(substitleLanguage);

                                        }, 5000);
                                    }

                                dialogInterface.dismiss();


                                });

                                builder.show();


                            }else {


                                Toast.makeText(EasyPlexMainPlayer.this, "No Substitles Found for this media", Toast.LENGTH_SHORT).show();

                            }

            });



        }else {

            playerViewModel.getEpisodeSubsByImdb(getPlayerController().getEpID(),getPlayerController().getCurrentExternalId(),getPlayerController().getSeaonNumber());
            playerViewModel.oPensubsMediaMutableLiveData.observe(this, subsList -> {

                  List<Opensub> subsInfo = new ArrayList<>();

                            for (Opensub opensub : subsList) {


                                if (!subsInfo.contains(opensub) && opensub.getZipDownloadLink() !=null && opensub.getSubFormat() !=null && !opensub.getSubFormat().isEmpty() &&

                                        Objects.equals(opensub.getSubFormat(), "srt")

                                        && opensub.getSubHD() !=null && opensub.getSubHD().equals("1") && opensub.getSubEncoding() !=null ) {



                                    String langName = opensub.getLanguageName();
                                    String langMovieName = opensub.getMovieReleaseName();
                                    String langDownloadLink = opensub.getZipDownloadLink();
                                    String langSrtName = opensub.getSubFileName();
                                    subsInfo.add(new Opensub(langSrtName,langMovieName,langName,langDownloadLink));
                                    subsInfo.add(opensub);

                                }


                            }


                            String[] charSequenceSubs = new String[subsInfo.size()];
                            for (int i = 0; i<subsInfo.size(); i++) {
                                charSequenceSubs[i] = subsInfo.get(i).getLanguageName();

                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(EasyPlexMainPlayer.this, R.style.MyAlertDialogTheme);
                            builder.setTitle(R.string.select_subs);
                            builder.setCancelable(true);
                            builder.setItems(charSequenceSubs, (dialogInterface, i) -> {


                                DownloadFileAsync download =
                                        new DownloadFileAsync(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath())+SUBSTITLE_SUB_FILENAME_ZIP, file -> {
                                            Log.i(TAG, "file download completed");


                                            // check unzip file now
                                            ZipFile zipFile = new ZipFile("subs.zip");
                                            FileHeader fileHeader = zipFile.getFileHeader(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath())+SUBSTITLE_SUB_FILENAME_ZIP);
                                            if (fileHeader != null) {
                                                zipFile.removeFile(fileHeader);

                                            }else {

                                                new ZipFile(file, null).extractFile(subsInfo.get(i).getSubFileName(),
                                                        String.valueOf(getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath())), ZIP_FILE_NAME);
                                                Log.i(TAG, "file unzip completed");
                                            }


                                        });
                                download.execute(subsInfo.get(i).getZipDownloadLink());

                                Toast.makeText(EasyPlexMainPlayer.this, "The"+subsInfo.get(i).getLanguageName()+getString(R.string.ready_5sec), Toast.LENGTH_LONG).show();

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {

                                    String subs = SUBSTITLE_LOCATION + EasyPlexMainPlayer.this.getPackageName() + "/files/data/" + ZIP_FILE_NAME;
                                    String substitleLanguage = subsInfo.get(i).getLanguageName();
                                    String id = getPlayerController().getVideoID();
                                    String type = getPlayerController().getMediaType();
                                    String currentQuality = getPlayerController().getVideoCurrentQuality();
                                    String artwork = (valueOf(getPlayerController().getMediaPoster()));
                                    String name = getPlayerController().getCurrentVideoName();
                                    String videoUrl = (valueOf(getPlayerController().getVideoUrl()));
                                    mMediaModel = MediaModel.media(id, substitleLanguage, currentQuality, type, name, videoUrl, artwork,
                                            subs, null, null
                                            , null, null, null, null, null,
                                            null, null, getPlayerController().getCurrentHlsFormat(),
                                            "srt", getPlayerController().getCurrentExternalId(),
                                            getPlayerController().getMediaCoverHistory(),
                                            getPlayerController().getCurrentHasRecap(),
                                            getPlayerController().getCurrentStartRecapIn(),getPlayerController().getMediaGenre(),getPlayerController().getSerieName()
                                            ,getPlayerController().getVoteAverage());
                                    update(mMediaModel);
                                    getPlayerController().isSubtitleEnabled(true);
                                    getPlayerController().subtitleCurrentLang(substitleLanguage);

                                }, 5000);



                                dialogInterface.dismiss();


                            });

                            builder.show();
             });
        }

    }

    @Override
    public void onTracksMedia() {

        if (TrackSelectionDialog.willHaveContent(mTrackSelector)){

            TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(mTrackSelector, this);
            trackSelectionDialog.show(getSupportFragmentManager(),null);

        }
    }

    // Update CueIndicator when the CuePoint Received
    @Override
    public void onCuePointReceived(long[] cuePoints) {

        binding.cuepointIndictor.setText(printCuePoints(cuePoints));
    }

    @Override
    public boolean isPremuim() {


        return false;
    }

    @Override
    public void isCurrentAd(boolean enabled) {

        if (enabled) {
            isCurrentAd = true;
        }

    }

    @Override
    public void onDisplayErrorDialog() {

      //

    }


    private String printCuePoints(long[] cuePoints) {
        if (cuePoints == null) {
            return null;
        }

        return "";
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

        // On CuePoint Change

    }


    // Play Next Media (Reset and update State for the Player)
    @Override
    public void playNext(MediaModel nextVideo) {

        createMediaSource(nextVideo);
        fsmPlayer.setMovieMedia(nextVideo);
        fsmPlayer.restart();

        getPlayerController().setMediaRestart(true);


        getPlayerController().getCurrentSpeed(getContext().getString(R.string.speed_normal));

        if (getPlayerController().getIsMediaSubstitleGet()) {

            getPlayerController().subtitleCurrentLang(getString(R.string.player_substitles));

        }


        String mediaType = getPlayerController().getMediaType();
        if ("0".equals(mediaType)) {
            onLoadMovieResume();
        } else if ("1".equals(mediaType)) {
            onLoadSerieResume();
        } else if ("anime".equals(mediaType)) {
            onLoadAnimeResume();
        }

        getPlayerController().isMediaHasRecap(getPlayerController().getCurrentHasRecap() == 1);

        onLoadHistory();

    }



    // Update Media (Without Resetting the position )
    @Override
    public void update(MediaModel update) {
        createMediaSource(update);
        fsmPlayer.setMovieMedia(update);
        fsmPlayer.update();

    }

    @Override
    public void backState(MediaModel backstate) {
        createMediaSource(backstate);
        fsmPlayer.setMovieMedia(backstate);
        fsmPlayer.backfromApp();

    }



    // Detect if a user has Selected a substitle
    @Override
    public void onSubstitleClicked(boolean clicked) {

        if (clicked) {
            binding.frameSubstitles.setVisibility(View.GONE);

            final Dialog dialog = new Dialog(EasyPlexMainPlayer.this);
            dialog.dismiss();

        }

    }


    // Detect if a user has Selected a Quality
    @Override
    public void onQualityClicked(boolean clicked) {


        if (clicked) {
            binding.frameQualities.setVisibility(View.GONE);

        }


    }



    // Detect if a user has Selected a Stream
    @Override
    public void onStreamingclicked(boolean clicked) {

        if (clicked) {
            binding.frameLayoutStreaming.setVisibility(View.GONE);
        }

    }





    // Detect if a user has Clicked an episode or movie when the media is ended
    @Override
    public void onNextMediaClicked(boolean clicked) {


        if (clicked) {
            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if(mCountDownTimer!=null){

                mCountDownTimer.cancel();
                mCountDownTimer = null;

            }

        }


    }

    @Override
    public void onMoviesListClicked(boolean clicked) {

        binding.frameLayoutMoviesList.setVisibility(View.GONE);

    }

    @Override
    public void onSeriesListClicked(boolean clicked) {


        binding.frameLayoutSeriesList.setVisibility(View.GONE);
    }


    @Override
    public void onEpisodeClicked(boolean clicked) {

        if (clicked) {
            binding.frameLayoutSeasons.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAdPlaybackState(AdPlaybackState adPlaybackState) {


        //
    }

    @Override
    public void onAdLoadError(AdsMediaSource.AdLoadException error, DataSpec dataSpec) {


        //
    }

    @Override
    public void onAdClicked() {


        //

    }

    @Override
    public void onAdTapped() {


        //

    }

    private void createAndLoadRewardedAd() {

        IronSource.init(this, settingsManager.getSettings().getIronsourceAppKey(),
                IronSource.AD_UNIT.INTERSTITIAL);

        // Initialize the AppNext Ads SDK.
        Appnext.init(this);

        if ("StartApp".equals(settingsManager.getSettings().getDefaultNetworkPlayer()) && settingsManager.getSettings().getStartappId() != null) {

            startAppAd = new StartAppAd(EasyPlexMainPlayer.this);

        }
        Appodeal.initialize(EasyPlexMainPlayer.this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.INTERSTITIAL);
        adsLaunched = true;

    }

    private void onLoadHistory() {

        String mediaType = getPlayerController().getMediaType();

        if ("0".equals(mediaType)) {

            history = new History(getPlayerController().getVideoID(), getPlayerController().getVideoID(),String.valueOf(getPlayerController().getMediaPoster()),getPlayerController().getCurrentVideoName()
                    ,String.valueOf(getPlayerController().getMediaPoster()),"");
            history.setTmdbId(getPlayerController().getVideoID());
            history.setType("0");
            history.setPosterPath(String.valueOf(getPlayerController().getMediaPoster()));
            history.setExternalId(getPlayerController().getCurrentExternalId());
            history.setPremuim(getPlayerController().isMediaPremuim());
            history.setHasrecap(getPlayerController().getCurrentHasRecap());
            history.setSkiprecapStartIn(getPlayerController().getCurrentStartRecapIn());
            history.setMediaGenre(getPlayerController().getMediaGenre());
            history.setVoteAverage(getPlayerController().getVoteAverage());
            history.setMediaGenre(getPlayerController().getMediaGenre());
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());


        } else if ("1".equals(mediaType)) {


            history = new History(getPlayerController().getVideoID(), String.valueOf(getPlayerController().getCurrentEpTmdbNumber()),String.valueOf(getPlayerController()
                    .getMediaPoster()),getPlayerController().getCurrentVideoName()
                    ,String.valueOf(getPlayerController().getMediaPoster()),String.valueOf(getPlayerController().getVideoUrl()));
            history.setSerieName(getPlayerController().getSerieName());
            history.setEpisodeNmber(getPlayerController().getEpID());
            history.setSeasonsId(getPlayerController().getCurrentSeasonNumber());
            history.setPosition(getPlayerController().getCurrentEpisodePosition());
            history.setType("1");
            history.setTmdbId(getPlayerController().getVideoID());
            history.setPosterPath(String.valueOf(getPlayerController().getMediaPoster()));
            history.setEpisodeId(getPlayerController().getCurrentEpTmdbNumber());
            history.setEpisodeName(getPlayerController().getEpName());
            history.setEpisodeTmdb(getPlayerController().getCurrentEpTmdbNumber());
            history.setSerieId(getPlayerController().getVideoID());
            history.setCurrentSeasons(getPlayerController().getCurrentSeason());
            history.setSeasonsId(getPlayerController().getSeaonNumber());
            history.setSeasonsNumber(getPlayerController().getCurrentSeasonNumber());
            history.setImdbExternalId(getPlayerController().getCurrentExternalId());
            history.setPremuim(getPlayerController().isMediaPremuim());
            history.setMediaGenre(getPlayerController().getMediaGenre());
            history.setVoteAverage(getPlayerController().getVoteAverage());
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        } else if ("anime".equals(mediaType)) {

            history = new History(getPlayerController().getVideoID(), String.valueOf(getPlayerController().getCurrentEpTmdbNumber()),String.valueOf(getPlayerController()
                    .getMediaPoster()),getPlayerController().getCurrentVideoName()
                    ,String.valueOf(getPlayerController().getMediaPoster()),String.valueOf(getPlayerController().getVideoUrl()));
            history.setSerieName(getPlayerController().getSerieName());
            history.setEpisodeNmber(getPlayerController().getEpID());
            history.setSeasonsId(getPlayerController().getCurrentSeasonNumber());
            history.setPosition(getPlayerController().getCurrentEpisodePosition());
            history.setType("anime");
            history.setTmdbId(getPlayerController().getVideoID());
            history.setPosterPath(String.valueOf(getPlayerController().getMediaPoster()));
            history.setEpisodeId(getPlayerController().getCurrentEpTmdbNumber());
            history.setEpisodeName(getPlayerController().getEpName());
            history.setEpisodeTmdb(getPlayerController().getCurrentEpTmdbNumber());
            history.setSerieId(getPlayerController().getVideoID());
            history.setCurrentSeasons(getPlayerController().getCurrentSeason());
            history.setSeasonsId(getPlayerController().getSeaonNumber());
            history.setSeasonsNumber(getPlayerController().getCurrentSeasonNumber());
            history.setImdbExternalId(getPlayerController().getCurrentExternalId());
            history.setPremuim(getPlayerController().isMediaPremuim());
            history.setMediaGenre(getPlayerController().getMediaGenre());
            history.setVoteAverage(getPlayerController().getVoteAverage());
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        }
    }


    public void mediaType() {

        String mediaType = getPlayerController().getMediaType();
        if ("0".equals(mediaType)) {

            history = new History(getPlayerController().getVideoID(),
                    getPlayerController().getVideoID(),getPlayerController()
                    .getMediaCoverHistory(),getPlayerController().getCurrentVideoName(),
                    String.valueOf(getPlayerController().getMediaPoster()),null);
            history.setLink(String.valueOf(getPlayerController().getVideoUrl()));
            history.setType("0");
            history.setPosterPath(getPlayerController().getMediaCoverHistory());
            history.setExternalId(getPlayerController().getCurrentExternalId());
            history.setPremuim(getPlayerController().isMediaPremuim());
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());


        } else if ("1".equals(mediaType)) {


            history = new History(getPlayerController().getVideoID(), String.valueOf(getPlayerController().getCurrentEpTmdbNumber()),String.valueOf(getPlayerController()
                    .getMediaPoster()),getPlayerController().getCurrentVideoName()
                    ,String.valueOf(getPlayerController().getMediaPoster()),String.valueOf(getPlayerController().getVideoUrl()));
            history.setEpisodeNmber(getPlayerController().getEpID());
            history.setSeasonsId(getPlayerController().getCurrentSeasonNumber());
            history.setPosition(getPlayerController().getCurrentEpisodePosition());
            history.setType("1");
            history.setPosterPath(getPlayerController().getMediaCoverHistory());
            history.setEpisodeId(getPlayerController().getEpID());
            history.setEpisodeName(getPlayerController().getEpName());
            history.setEpisodeTmdb(getPlayerController().getCurrentEpTmdbNumber());
            history.setSerieId(getPlayerController().getVideoID());
            history.setCurrentSeasons(getPlayerController().getCurrentSeason());
            history.setSeasonsId(getPlayerController().getSeaonNumber());
            history.setSeasonsNumber(getPlayerController().getCurrentSeasonNumber());
            history.setImdbExternalId(getPlayerController().getCurrentExternalId());
            history.setPremuim(getPlayerController().isMediaPremuim());
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        } else if ("anime".equals(mediaType)) {

            history = new History(getPlayerController().getVideoID(), String.valueOf(getPlayerController().getCurrentEpTmdbNumber()),String.valueOf(getPlayerController()
                    .getMediaPoster()),getPlayerController().getCurrentVideoName()
                    ,String.valueOf(getPlayerController().getMediaPoster()),String.valueOf(getPlayerController().getVideoUrl()));
            history.setEpisodeNmber(getPlayerController().getEpID());
            history.setSeasonsId(getPlayerController().getCurrentSeasonNumber());
            history.setPosition(getPlayerController().getCurrentEpisodePosition());
            history.setType("anime");
            history.setPosterPath(getPlayerController().getMediaCoverHistory());
            history.setEpisodeId(String.valueOf(getPlayerController().getEpID()));
            history.setEpisodeName(getPlayerController().getEpName());
            history.setEpisodeTmdb(String.valueOf(getPlayerController().getCurrentEpTmdbNumber()));
            history.setSerieId(getPlayerController().getVideoID());
            history.setCurrentSeasons(getPlayerController().getCurrentSeason());
            history.setSeasonsId(getPlayerController().getSeaonNumber());
            history.setSeasonsNumber(getPlayerController().getCurrentSeasonNumber());
            history.setImdbExternalId(getPlayerController().getCurrentExternalId());
            history.setPremuim(getPlayerController().isMediaPremuim());
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        dialog.dismiss();
    }
}