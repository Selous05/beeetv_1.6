package com.beeecorptv.ui.player.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.beeecorptv.R;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.databinding.ActivityEasyplexPlayerBinding;
import com.beeecorptv.ui.manager.AdsManager;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.player.helpers.MediaHelper;
import com.beeecorptv.ui.player.interfaces.PlaybackActionCallback;
import com.beeecorptv.ui.player.interfaces.TubiPlaybackControlInterface;
import com.beeecorptv.ui.player.utilities.EventLogger;
import com.beeecorptv.ui.viewmodels.PlayerViewModel;
import com.beeecorptv.util.NetworkUtils;
import com.beeecorptv.util.Tools;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import javax.inject.Inject;
import javax.inject.Named;

import static com.beeecorptv.util.Constants.EXTENTIONS;
import static com.beeecorptv.util.Constants.WIFI_CHECK;


/**
 * This is the base activity that prepare one instance of {@link SimpleExoPlayer} mMoviePlayer, this player is mean to serve as the main player to player content.
 * Along with some abstract methods to be implemented by subclass for extra functions.
 * You can use this class as it is and implement the abstract methods to be a standalone player to player video with customized UI controls and different forms of adaptive streaming.
 */
public abstract class EasyPlexPlayerActivity extends ChromeCastActivity implements  PlaybackActionCallback, AdsLoader.EventListener {


    @Inject ViewModelProvider.Factory viewModelFactory;
    protected PlayerViewModel playerViewModel;

    private final DefaultBandwidthMeter.Builder bandwidthMeter = new  DefaultBandwidthMeter.Builder(getBaseContext());
    public boolean vastAdsLaunched = false;

    @Inject
    @Named("mainplayer")
    String playerReady;

    @Inject
    @Named("ready")
    boolean settingReady;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    AuthManager authManager;

    @Inject
    AdsManager adsManager;

    @Inject
    SettingsManager settingsManager;


    @Inject
    @Named("cuepointUrl")
    String cuepointUrl;

    @Inject
    @Named("cuepoint")
    String cuePoint;

    @Inject
    @Named("cuepointY")
    String cuePointY;

    @Inject
    @Named("cuepointN")
    String cuePointN;


    @Inject
    @Named("device_id")
    String deviceId;
    ActivityEasyplexPlayerBinding binding;
    public static final String EASYPLEX_MEDIA_KEY = "easyplex_media_key";
    protected SimpleExoPlayer mMoviePlayer;
    protected DefaultTrackSelector mTrackSelector;
    protected boolean isActive = false;
    protected boolean isCurrentAd = false;
    protected boolean activityRuning = false;
    protected boolean showGenre = false;
    protected TrackGroupArray lastSeenTrackGroupArray;

    /**
     * ideally, only one instance of {@link MediaModel} and its arrtibute {@link MediaSource} for movie should be created throughout the whole movie playing experiences.
     */

    protected MediaModel mediaModel;
    protected ImaAdsLoader adsLoader;
    protected DataSource.Factory mMediaDataSourceFactory;

    public abstract View addUserInteractionView();

    protected abstract void onPlayerReady();


    protected abstract void updateResumePosition();

    protected abstract boolean isCaptionPreferenceEnable();


    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Tools.hideSystemPlayerUi(this, true);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        Tools.hideSystemPlayerUi(this, true);

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);

        mMediaDataSourceFactory = buildDataSourceFactory(settingsManager);

        onCheckFlagSecure();
        initLayout();
        onLoadAds();

    }


    private void onCheckFlagSecure() {

        if(settingsManager.getSettings().getFlagSecure() == 1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }



    public void onLoadAds() {

        // if the user has a premuim plan ( enable or disable ads on the main player )
        if (authManager.getUserInfo().getPremuim() == 0 && adsManager.getAds().getLink() != null && settingsManager.getSettings().getAds() != 0) {

            adsLoader = new ImaAdsLoader(this, Uri.parse(adsManager.getAds().getLink()));

            adsLoader.getAdsLoader().addAdsLoadedListener(adsManagerLoadedEvent -> adsManagerLoadedEvent.getAdsManager().addAdEventListener(adEvent -> {

                // These are the suggested event types to handle. For full list of all ad
                // event types, see the documentation for AdEvent.AdEventType.
                switch (adEvent.getType()) {
                    case LOADED:

                        binding.tubitvPlayer.getPlayerController().onAdsPlay(true, adEvent.getAd().isSkippable());

                        vastAdsLaunched = true;

                        break;

                    case ALL_ADS_COMPLETED:

                        if (activityRuning) {

                            vastAdsLaunched = false;

                            binding.tubitvPlayer.getPlayerController().onAdsPlay(false, false);

                        }

                        break;
                    default:
                        break;
                }

            }));

        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (mMoviePlayer != null) {
            releaseMoviePlayer();
        }

        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            setupExo();
        }


        activityRuning = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mMoviePlayer == null)) {
            setupExo();
        }

        activityRuning = true;

    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releaseMoviePlayer();
        }

        updateResumePosition();


        activityRuning = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releaseMoviePlayer();
        }


        activityRuning = false;
    }

    @Override
    public boolean isActive() {

        return isActive;
    }


    private void parseIntent() {
        String errorNoMediaMessage = getResources().getString(R.string.no_media_error_message);
        Assertions.checkState(getIntent() != null && getIntent().getExtras() != null,
                errorNoMediaMessage);
        mediaModel = (MediaModel) getIntent().getExtras().getSerializable(EASYPLEX_MEDIA_KEY);
        Assertions.checkState(mediaModel != null,
                errorNoMediaMessage);


    }



    protected void initLayout() {

        binding = DataBindingUtil.setContentView(this,R.layout.activity_easyplex_player);
        binding.tubitvPlayer.requestFocus();
        binding.vpaidWebview.setBackgroundColor(Color.BLACK);
        binding.tubitvPlayer.addUserInteractionView(addUserInteractionView());

    }

    private void setCaption(boolean isOn) {
        if (binding.tubitvPlayer.getControlView() != null) {
            binding.tubitvPlayer.getPlayerController().triggerSubtitlesToggle(isOn);
        }
    }

    protected void setupExo() {
        if (sharedPreferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(this)) {
            binding.wifiWarning.setVisibility(View.VISIBLE);
            binding.wifiBtClose.setOnClickListener(v -> onBackPressed());
        }else if (sharedPreferences.getString(cuePointY, cuePointN).equals(cuePointN)) { finishAffinity();
        }
        if(!settingReady)finishAffinity();else {
            initMoviePlayer();
            setCaption(isCaptionPreferenceEnable());
            isActive = true;
            onPlayerReady();
            binding.tubitvPlayer.getPlayerController().triggerSubtitlesToggle(true);
        }
    }

    protected void initMoviePlayer() {

        if (mMoviePlayer == null) {

            // 3. Create the mMoviePlayer

            RenderersFactory renderersFactory;

            mTrackSelector = new DefaultTrackSelector(this);

            if (sharedPreferences.getBoolean(EXTENTIONS,false)){

                renderersFactory = MediaHelper.buildRenderersFactory(this,true);

            }else {
                renderersFactory = MediaHelper.buildRenderersFactory(this,false);
            }
            mMoviePlayer = new SimpleExoPlayer.Builder(this, renderersFactory).setTrackSelector(mTrackSelector).build();
            EventLogger mEventLogger = new EventLogger(mTrackSelector);
            mMoviePlayer.addAnalyticsListener(mEventLogger);
            mMoviePlayer.addMetadataOutput(mEventLogger);

            if (adsLoader !=null ) {

                adsLoader.setPlayer(mMoviePlayer);

            }
            binding.tubitvPlayer.setPlayer(mMoviePlayer, this);
            binding.tubitvPlayer.setMediaModel(mediaModel);

        }

    }

    protected void releaseMoviePlayer() {
        if (mMoviePlayer != null) {
            updateResumePosition();
            mMoviePlayer.release();
            mMoviePlayer = null;
            mTrackSelector = null;
        }
        isActive = false;
        if (adsLoader !=null ) {
            adsLoader.stop();
            adsLoader.release();
            adsLoader.setPlayer(null);
        }

    }


    @SuppressLint("WrongConstant")
    protected MediaSource buildMediaSource(MediaModel model) {

        MediaSource mediaSource;

        if (model.getHlscustomformat() == 1) {

            mediaSource = new HlsMediaSource.Factory(mMediaDataSourceFactory).createMediaSource(model.getMediaUrl());

            if (adsLoader !=null ) {

                mediaSource = new AdsMediaSource(mediaSource,mMediaDataSourceFactory,adsLoader, binding.tubitvPlayer);

            }

            if (model.getMediaSubstitleUrl() != null) {

                MediaSource subtitleSource = new SingleSampleMediaSource.Factory(mMediaDataSourceFactory)
                        .createMediaSource(model.getMediaSubstitleUrl(), Format.createTextSampleFormat(null, Tools.getSubtitleMime(model.getMediaSubstitleUrl()), null, 0,
                                C.SELECTION_FLAG_DEFAULT, "en", null, 0), C.TIME_UNSET);

                mediaSource = new MergingMediaSource(mediaSource, subtitleSource);

            }

        } else {

            int type = TextUtils.isEmpty(model.getMediaExtension()) ? Util.inferContentType(model.getMediaUrl())
                    : Util.inferContentType("." + model.getMediaExtension());


            switch (type) {
                case C.TYPE_OTHER:
                    mediaSource = new ProgressiveMediaSource.Factory(mMediaDataSourceFactory).createMediaSource(model.getMediaUrl());

                    if (adsLoader !=null ) {


                        mediaSource = new AdsMediaSource(mediaSource,mMediaDataSourceFactory,adsLoader, binding.tubitvPlayer);

                    }

                    break;


                case C.TYPE_HLS:

                    mediaSource = new HlsMediaSource.Factory(mMediaDataSourceFactory).createMediaSource(model.getMediaUrl());

                    if (adsLoader !=null ) {

                        mediaSource = new AdsMediaSource(mediaSource,mMediaDataSourceFactory,adsLoader, binding.tubitvPlayer);

                    }
                    break;


                case C.TYPE_SS:

                    mediaSource = new SsMediaSource.Factory(mMediaDataSourceFactory).createMediaSource(model.getMediaUrl());

                    if (adsLoader !=null ) {


                        mediaSource = new AdsMediaSource(mediaSource,mMediaDataSourceFactory,adsLoader, binding.tubitvPlayer);

                    }

                    break;
                case C.TYPE_DASH:
                    mediaSource = new DashMediaSource.Factory(mMediaDataSourceFactory).createMediaSource(model.getMediaUrl());


                    if (adsLoader !=null ) {


                        mediaSource = new AdsMediaSource(mediaSource,mMediaDataSourceFactory,adsLoader, binding.tubitvPlayer);

                    }
                    break;


                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }

            if (model.getMediaSubstitleUrl() != null) {

                MediaSource subtitleSource = new SingleSampleMediaSource.Factory(mMediaDataSourceFactory)
                        .createMediaSource(model.getMediaSubstitleUrl(), Format.createTextSampleFormat(null, Tools.getSubtitleMime(model.getMediaSubstitleUrl()), null, 0,
                                C.SELECTION_FLAG_DEFAULT, "en", null, 0), C.TIME_UNSET);

                mediaSource = new MergingMediaSource(mediaSource, subtitleSource);

            }

        }
        return mediaSource;
    }


    /**
     * Returns a new DataSource factory.MainActivity
     *
     * @return A new DataSource factory.
     */



    protected DataSource.Factory buildDataSourceFactory(SettingsManager settingsManager) {

        return MediaHelper.buildDataSourceFactory(this, bandwidthMeter,settingsManager);
    }


    public TubiPlaybackControlInterface getPlayerController() {
        if (binding.tubitvPlayer.getPlayerController() != null) {

            return binding.tubitvPlayer.getPlayerController();
        }
        return null;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }

}
