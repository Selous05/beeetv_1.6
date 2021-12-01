package com.beeecorptv.ui.player.bindings;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;
import androidx.databinding.ObservableInt;
import com.beeecorptv.R;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.ui.player.controller.PlayerUIController;
import com.beeecorptv.ui.player.enums.ScaleMode;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayerApi;
import com.beeecorptv.ui.player.interfaces.PlaybackActionCallback;
import com.beeecorptv.ui.player.interfaces.TubiPlaybackControlInterface;
import com.beeecorptv.ui.player.presenters.ScalePresenter;
import com.beeecorptv.ui.player.utilities.ExoPlayerLogger;
import com.beeecorptv.ui.player.utilities.PlayerDeviceUtils;
import com.beeecorptv.ui.player.views.EasyPlexPlayerView;
import com.beeecorptv.util.Tools;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.video.VideoListener;
import org.jetbrains.annotations.NotNull;
import java.util.Observable;
import javax.inject.Inject;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;
import static com.beeecorptv.BeeeTvApp.getContext;
import static com.beeecorptv.util.Constants.AUTO_PLAY;
import static com.beeecorptv.util.Constants.CUSTOM_SEEK_CONTROL_STATE;
import static com.beeecorptv.util.Constants.DEFAULT_FREQUENCY;
import static com.beeecorptv.util.Constants.DEFAULT_MEDIA_COVER;
import static com.beeecorptv.util.Constants.EDIT_CUSTOM_SEEK_CONTROL_STATE;
import static com.beeecorptv.util.Constants.PREF_FILE;

/**
 * This class contains business logic of user interaction between user and player action. This class will be serving
 * as interface between Player UI and Business logic, such as seek, pause, UI logic for displaying ads vs movie.
 */
public class PlayerController extends Observable implements TubiPlaybackControlInterface, Player.EventListener, SeekBar.OnSeekBarChangeListener {


    private static final String TAG = PlayerController.class.getSimpleName();

    SharedPreferences preferences;
    private boolean isDraggingSeekBar;
    private static int controlstate = 1;


    /**
     * Media action states
     */
    public final ObservableInt playerPlaybackState = new ObservableInt(Player.STATE_IDLE);

    public final ObservableBoolean isVideoPlayWhenReady = new ObservableBoolean(false);


    public final ObservableBoolean isUserDraggingSeekBar = new ObservableBoolean(false);


    public final ObservableField<String> currentSpeed = new ObservableField<>(getContext().getString(R.string.speed_normal));


    public final ObservableBoolean isPlayerError = new ObservableBoolean(false);


    public final ObservableFloat voteAverage = new ObservableFloat(0);


    public final ObservableField<String> nextSeasonsID = new ObservableField<>("");


    // Return Media Name
    public final ObservableField<String> videoName = new ObservableField<>("");

    // Return Media Genre
    public final ObservableField<String> mediaGenreString = new ObservableField<>("");

    // Return getSerieTvShowName Genre
    public final ObservableField<String> getSerieTvShowName = new ObservableField<>("");


    // Return Media Name
    public final ObservableField<String> mediaTypeName = new ObservableField<>("");

    // Return Current Media TMDB Number (EX : 168222)
    public final ObservableField<String> getCurrentMediaTmdbNumber = new ObservableField<>("");


    public final ObservableField<String> getExternalId = new ObservableField<>("");



    // Return Episode Position ( Json )
    public final ObservableInt episodePosition = new ObservableInt();


    // Return Media Current Stream Link
    public final ObservableField<Uri> videoCurrentLink = new ObservableField<>();


    // Return Media Current Substitle Link
    public final ObservableField<String> videoCurrentSubs = new ObservableField<>(getContext().getString(R.string.player_substitles));



    // Return Media Current Substitle Link
    public final ObservableField<String> mediaToMyList = new ObservableField<>(getContext().getString(R.string.add_to_my_list_player));


    // Return Media Current Quality Link
    public final ObservableField<String> videoCurrentQuality = new ObservableField<>(getContext().getString(R.string.select_subs_player));


    // Return Media ID
    public final ObservableField<String> videoID = new ObservableField<>("");



    public final ObservableField<String> currentSeasonId = new ObservableField<>("");


    // Return Media ID
    public final ObservableField<String> currentEpisodeName = new ObservableField<>("");



    // Return Current Episode Season Number for a Serie or Anime
    public final ObservableField<String> currentSeasonsNumber = new ObservableField<>("");


    // Return Current Episode IMDB Number for a Serie or Anime
    public final ObservableField<String> currentEpisodeImdbNumber = new ObservableField<>("");


    // Return if media Has An ID
    public final ObservableField<Boolean> videoHasID = new ObservableField<>(false);


    public final ObservableField<Boolean> youCanHide = new ObservableField<>(true);


    // Return if media External Id (TMDB)
    public final ObservableField<String> videoExternalID = new ObservableField<>("");



    // Return Remaining Time for the current Media
    public final ObservableField<String> timeRemaining = new ObservableField<>();


    // Return Media Type
    public final ObservableField<String> mediaType = new ObservableField<>("");


    // Return Media Substile in Uri Format
    public final ObservableField<Uri> mediaSubstitleUri = new ObservableField<>();


    // Return Media Duration
    public final ObservableField<Long> mediaDuration = new ObservableField<>(0L);


    // Return Media Current Time ( For SeekBar )
    public final ObservableField<Long> mediaCurrentTime = new ObservableField<>(0L);


    public final ObservableField<Long> mediaVolume = new ObservableField<>(0L);


    // Return Media Current Buffred Position ( For SeekBar )
    public final ObservableField<Long> mediaBufferedPosition = new ObservableField<>(0L);

    // Return Media Current Remaining Time in String Format
    public final ObservableField<String> mediaRemainInString = new ObservableField<>("");


    // Return Media Media Position
    public final ObservableField<String> mediaPositionInString = new ObservableField<>("");


    // Return True if the media Has an Active Substitle
    public final ObservableField<Boolean> mediaHasSubstitle = new ObservableField<>(false);


    public final ObservableField<Boolean> lg = new ObservableField<>(false);


    // Return Current Episode Cover
    public final ObservableField<String> currentMediaCover = new ObservableField<>("");


    // Return True if the media is Ended
    public final ObservableField<Boolean> mediaEnded = new ObservableField<>(false);


    public final ObservableField<Boolean> isPlayerReady = new ObservableField<>(false);


    public final ObservableField<Boolean> autoSubstitleActivated = new ObservableField<>(false);


    // Return True if Current Media is a Live Streaming
    public final ObservableField<Boolean> isLive = new ObservableField<>(false);


    // Return True if Current User Has a Premuim Membership
    public final ObservableField<Boolean> isUserPremuim = new ObservableField<>(false);


    // Return Episode Id for a Serie
    public final ObservableField<String> episodeId = new ObservableField<>("4:3");


    // Return Seasons Id for a Serie
    public final ObservableField<String> episodeSeasonsId = new ObservableField<>("");


    // Return Seasons Id for a Serie
    public final ObservableField<String> episodeSeasonsNumber = new ObservableField<>("");


    // Return if Current Media is Premuim
    public final ObservableInt mediaPremuim = new ObservableInt();


    // Return True if the User has enabled the Substitle
    public final ObservableField<Boolean> mediaSubstitleGet = new ObservableField<>(false);


    public final ObservableField<Boolean> isAutoPlayEnabled = new ObservableField<>(false);


    public final ObservableField<Boolean> isStreamOnFavorite = new ObservableField<>(false);


    /**
     * Ad information
     */

    // Return ads Click Url
    public final ObservableField<String> adClickUrl = new ObservableField<>("");


    // Return Number of Ads Left
    public final ObservableInt numberOfAdsLeft = new ObservableInt(0);


    // Return True if Current Media is playing an ADS
    public final ObservableField<Boolean> isCurrentAd = new ObservableField<>(false);

    public final ObservableField<Boolean> hideGenre = new ObservableField<>(false);


    public final ObservableField<Boolean> isMediaHasSkipRecap = new ObservableField<>(false);

    public final ObservableField<Boolean> isCue = new ObservableField<>(false);

    public final ObservableField<Boolean> isSkippable = new ObservableField<>(false);


    // Return True if Current Media has reached a CuePoint
    public final ObservableField<Boolean> isCuePointReached = new ObservableField<>(false);


    // Return Ads Remaining Time in String Format
    public final ObservableField<String> adsRemainInString = new ObservableField<>("");


    public final ObservableField<String> mediaCoverHistory = new ObservableField<>("");
    public final ObservableInt hasRecap = new ObservableInt();
    public final ObservableInt recapStartIn = new ObservableInt(0);

    public final ObservableField<Boolean> mediaRestart = new ObservableField<>(false);

    public final ObservableField<Boolean> playerReady = new ObservableField<>(false);

    public final ObservableField<Boolean> settingReady = new ObservableField<>(false);

    private PlayerUIController controller;
    private float mInitVideoAspectRatio;
    private ScalePresenter mScalePresenter;
    private final VideoListener mVideoListener = new VideoListener() {
        @Override
        public void onVideoSizeChanged(final int width, final int height, final int unappliedRotationDegrees,
                                       final float pixelWidthHeightRatio) {
            ExoPlayerLogger.d(TAG, "onVideoSizeChanged");
            mInitVideoAspectRatio = height == 0 ? 1 : (width * pixelWidthHeightRatio) / height;
        }

        @Override
        public void onRenderedFirstFrame() {
            ExoPlayerLogger.d(TAG, "onRenderedFirstFrame");
        }
    };
    private final Handler mProgressUpdateHandler = new Handler(Looper.getMainLooper());
    private static  Runnable mOnControlStateChange;


    /**
     * the Exoplayer instance which this {@link PlayerController} is controlling.
     */
    private SimpleExoPlayer mPlayer;
    /**
     * this is the current mediaModel being played, it could be a ad or actually video
     */
    private MediaModel mMediaModel;
    private PlaybackActionCallback mPlaybackActionCallback;
    private final Runnable updateProgressAction = this::updateProgress;
    private EasyPlexPlayerView mEasyPlexPlayerView;


    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;


    /**
     * Every time the FsmPlayer change states between
     * AdPlayingState and MoviePlayingState,
     * current controller instance need to update the video instance.
     *
     * @param mediaModel the current video that will be played by the {@link PlayerController#mPlayer} instance.
     */
    public void setMediaModel(MediaModel mediaModel, Context context) {


        preferences = getContext().getSharedPreferences(PREF_FILE, MODE_PRIVATE);


        if (mediaModel == null) {
            ExoPlayerLogger.e(TAG, "setMediaModel is null");
        } else {

            this.mMediaModel = mediaModel;

            //mark flag for ads to movie
            isCurrentAd.set(mMediaModel.isAd());
            mPlaybackActionCallback.isCurrentAd(mMediaModel.isAd());

            mScalePresenter = new ScalePresenter(mEasyPlexPlayerView.getContext(), this);


            if (mMediaModel.isAd()) {

                if (!PlayerDeviceUtils.isTVDevice(context)
                        && !TextUtils.isEmpty(mMediaModel.getClickThroughUrl())) {
                    adClickUrl.set(mMediaModel.getClickThroughUrl());
                }

                videoName.set(context.getString(R.string.commercial));

                mediaHasSubstitle.set(false);

            } else {

                if (mediaType.get().equals("streaming")) {

                    isLive.set(true);

                }

                setModelMediaInfo(mediaModel);

            }
        }


        isAutoPlayEnabled.set(preferences.getBoolean(AUTO_PLAY, true));


        autoSubstitleActivated.set(preferences.getBoolean(AUTO_PLAY, true));


        lg.set(preferences.getString(FsmPlayerApi.decodeServerMainApi2(), FsmPlayerApi.decodeServerMainApi4()).equals(FsmPlayerApi.decodeServerMainApi4()));

    }






    public void setModelMediaInfo(@NonNull MediaModel mediaModel) {

        if (mMediaModel.getMediaCover() !=null) {

            currentMediaCover.set(String.valueOf(mMediaModel.getMediaCover()));

        }else {

            currentMediaCover.set(preferences.getString(DEFAULT_MEDIA_COVER,""));
        }


        if (mMediaModel.getMediaSubstitleUrl() != null) {
            mediaHasSubstitle.set(true);
            mediaSubstitleUri.set(mMediaModel.getMediaSubstitleUrl());
            triggerSubtitlesToggle(true);
        }




        if (!TextUtils.isEmpty(mMediaModel.getSeasonId())) {
            currentSeasonsNumber.set(mMediaModel.getSeasonId());
        }


        if (!TextUtils.isEmpty(mMediaModel.getEpImdb())) {
            currentEpisodeImdbNumber.set(mMediaModel.getEpImdb());
        }



        if (!TextUtils.isEmpty(mMediaModel.getTvSeasonId())) {
            nextSeasonsID.set(mMediaModel.getTvSeasonId());
        }




        if (!TextUtils.isEmpty(mMediaModel.getCurrentEpName())) {
            currentEpisodeName.set(mMediaModel.getCurrentEpName());
        }

        if (mMediaModel.getEpId() != null) {

            episodeId.set(String.valueOf(mMediaModel.getEpId()));
        }


        if (!TextUtils.isEmpty(mMediaModel.getCurrentSeasonsNumber())) {
            episodeSeasonsId.set(mMediaModel.getCurrentSeasonsNumber());
        }


        if (mMediaModel.getEpisodePostionNumber() != null) {

            episodePosition.set(mMediaModel.getEpisodePostionNumber());

        }


        if (mMediaModel.getCurrentEpTmdbNumber() != null) {

            getCurrentMediaTmdbNumber.set(mMediaModel.getCurrentEpTmdbNumber());

        }


        switch (getMediaType()) {
            case "0":
                mediaTypeName.set(mEasyPlexPlayerView.getContext().getString(R.string.lists_movies));
                break;
            case "1":
                mediaTypeName.set(mEasyPlexPlayerView.getContext().getString(R.string.lists_series));
                break;
            case "anime":
                mediaTypeName.set(mEasyPlexPlayerView.getContext().getString(R.string.lists_animes));
                break;
            default:
                mediaTypeName.set(mEasyPlexPlayerView.getContext().getString(R.string.lists_streaming));
                break;
        }



        if (Boolean.TRUE.equals(isStreamOnFavorite.get())) {

            mediaToMyList.set("Added");

        }else {

         mediaToMyList.set("Add To MyList");

        }


        if (!TextUtils.isEmpty(mMediaModel.getMediaName())) {
            videoName.set(mMediaModel.getMediaName());
        }

        if (!TextUtils.isEmpty(mMediaModel.getMediaGenres())) {
            mediaGenreString.set(mMediaModel.getMediaGenres());
            mPlaybackActionCallback.StartGenre(mMediaModel.getMediaGenres());
        }

        if (!TextUtils.isEmpty(mMediaModel.getSerieName())) {
            getSerieTvShowName.set(mMediaModel.getSerieName());
        }


        voteAverage.set(mMediaModel.getVoteAverage());


        if (!TextUtils.isEmpty(mMediaModel.getVideoid())) {
            videoID.set(mMediaModel.getVideoid());
            videoHasID.set(true);

        }


        if (!TextUtils.isEmpty(mMediaModel.getTvSeasonId())) {
            currentSeasonId.set(mMediaModel.getTvSeasonId());
        }

        if (mMediaModel.getIsPremuim() != null) {

            mediaPremuim.set(mMediaModel.getIsPremuim());

        }


        if (mMediaModel.getIsPremuim() != null) {

            mediaPremuim.set(mMediaModel.getIsPremuim());

        }


        if (!TextUtils.isEmpty(mMediaModel.getCurrentExternalId())) {
            getExternalId.set(mMediaModel.getCurrentExternalId());
        }



        if (!TextUtils.isEmpty(mMediaModel.getMediaGenre())) {
            videoExternalID.set(mMediaModel.getMediaGenre());
        }

        if (!TextUtils.isEmpty(mMediaModel.getType())) {
            mediaType.set(mMediaModel.getType());
            mPlaybackActionCallback.getType(mMediaModel.getType());
        }



        if (!TextUtils.isEmpty(mMediaModel.getCurrentQuality())) {
            videoCurrentQuality.set(mMediaModel.getCurrentQuality());
        }



        videoCurrentLink.set(mediaModel.getMediaUrl());




        if (!TextUtils.isEmpty(mMediaModel.getMediaCoverHistory())) {
            mediaCoverHistory.set(mMediaModel.getMediaCoverHistory());
        }



        hasRecap.set(mMediaModel.getHasRecap());

        recapStartIn.set(mMediaModel.getGetSkiprecapStartIn());



    }




    /**
     * Every time the FsmPlayer change states between
     * AdPlayingState and MoviePlayingState,
     * {@link PlayerController#mPlayer} instance need to update .
     *
     * @param player the current player that is playing the video
     */
    public void setPlayer(@NonNull SimpleExoPlayer player, @NonNull PlaybackActionCallback playbackActionCallback,
                          @NonNull EasyPlexPlayerView easyPlexPlayerView) {


        if (this.mPlayer == player) {
            return;
        }


        mEasyPlexPlayerView = easyPlexPlayerView;

        //remove the old listener
        if (mPlayer != null) {
            this.mPlayer.removeListener(this);
        }


        this.mPlayer = player;
        mPlayer.addListener(this);
        mPlayer.addVideoListener(mVideoListener);
        playerPlaybackState.set(mPlayer.getPlaybackState());
        mPlaybackActionCallback = playbackActionCallback;
        updateProgress();


    }


    public void setAvailableAdLeft(int count) {
        numberOfAdsLeft.set(count);
    }

    public void updateTimeTextViews(long position, long duration) {
        //translate the movie remaining time number into display string, and update the UI
        mediaRemainInString.set(Tools.getProgressTime((duration - position), true));
        adsRemainInString.set(mEasyPlexPlayerView.getContext().getString(R.string.up_next) + Tools.getProgressTime((duration - position), true));
        mediaPositionInString.set(Tools.getProgressTime(position, false));

    }


    /**
     * Get current player control state
     *
     * @return Current control state
     */
    public int getState() {
        return controlstate;
    }


    /**
     * Set current player state
     */
    public static  void setState(final int state) {
        controlstate = state;

        if (mOnControlStateChange != null) {
            mOnControlStateChange.run();
        }
    }

    /**
     * Check if it is during custom seek
     *
     * @return True if custom seek is performing
     */
    public boolean isDuringCustomSeek() {

        return controlstate == CUSTOM_SEEK_CONTROL_STATE || controlstate == EDIT_CUSTOM_SEEK_CONTROL_STATE;

    }



    @Override
    public void setPremuim(boolean premuim) {

        if (premuim) {

            isUserPremuim.set(true);

        }


    }

    @Override
    public String getMediaCoverHistory() {

        return mediaCoverHistory.get();
    }

    @Override
    public String getMediaGenre() {
        return mediaGenreString.get();
    }

    @Override
    public String getSerieName() {
        return getSerieTvShowName.get();
    }

    @Override
    public float getVoteAverage() {
        return voteAverage.get();
    }

    @Override
    public int getCurrentHasRecap() {
        return hasRecap.get();
    }


    @Override
    public void setMediaRestart(boolean enabled) {
       mediaRestart.set(enabled);
    }


    @Override
    public int getCurrentStartRecapIn() {
        return recapStartIn.get();
    }


    @Override
    public void toHideGenre(boolean enabled) {

        hideGenre.set(enabled);
    }

    @Override
    public void isUserDraggingSeekBar() {

        isUserDraggingSeekBar.set(isDraggingSeekBar);

    }

    @Override
    public boolean isMediaPlayerError() {
        return isPlayerError.get();
    }

    @Override
    public void isMediaHasRecap(boolean enabled) {

        isMediaHasSkipRecap.set(enabled);

    }


    @Override
    public void mediaHasSkipRecap() {

       mPlaybackActionCallback.onMediaHasSkipRecap();

    }

    @Override
    public void onAdsPlay(boolean playing,boolean isAdsSkippable) {

       isCurrentAd.set(playing);
       isSkippable.set(isAdsSkippable);


    }



    @Override
    public void triggerSubtitlesToggle(final boolean enabled) {


        if (mEasyPlexPlayerView == null) {
            ExoPlayerLogger.e(TAG, "triggerSubtitlesToggle() --> tubiExoPlayerView is null");
            return;
        }

        //trigger the hide or show subtitles.
        View subtitles = mEasyPlexPlayerView.getSubtitleView();
        if (subtitles != null) {
            subtitles.setVisibility(enabled ? VISIBLE : View.INVISIBLE);
        }

        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {
            mPlaybackActionCallback.onSubtitles(mMediaModel, enabled);
        }

        mediaSubstitleGet.set(enabled);
    }

    @Override
    public void triggerAutoPlay(boolean enabled) {

        isAutoPlayEnabled.set(enabled);


    }


    @Override
    public boolean isCue() {
        return isCue.get();
    }


    @Override
    public void onCheckedChanged(boolean enabled) {

        mPlaybackActionCallback.onAutoPlaySwitch(enabled);

    }

    @Override
    public void seekBy(final long millisecond) {
        if (mPlayer == null) {
            ExoPlayerLogger.e(TAG, "seekBy() ---> player is empty");
            return;
        }

        long currentPosition = mPlayer.getCurrentPosition();
        long seekPosition = currentPosition + millisecond;

        //lower bound
        seekPosition = seekPosition < 0 ? 0 : seekPosition;
        //upper bound
        seekPosition = Math.min(seekPosition, mPlayer.getDuration());

        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {

            mPlaybackActionCallback.onSeek(mMediaModel, currentPosition, seekPosition);
        }

        seekToPosition(seekPosition);
    }




    @Override
    public void seekByBrightness() {
        if (mPlayer == null) {
            ExoPlayerLogger.e(TAG, "seekBy() ---> player is empty");
            return;
        }
        mPlaybackActionCallback.onSeekBirghtness();
    }



    @Override
    public void seekTo(final long millisecond) {
        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {
            long currentProgress = mPlayer != null ? mPlayer.getCurrentPosition() : 0;
            mPlaybackActionCallback.onSeek(mMediaModel, currentProgress, millisecond);
        }

        seekToPosition(millisecond);


        loadPreview(millisecond, millisecond);


    }

    @Override
    public void isSubtitleEnabled(boolean enabled) {

        mediaSubstitleGet.get();


    }

    @Override
    public void subtitleCurrentLang(String lang) {

        videoCurrentSubs.set(lang);

    }


    @Override
    public Integer isMediaPremuim(){


       return mediaPremuim.get();

    }


    @Override
    public boolean hasSubsActive() {

        return mediaHasSubstitle.get();
    }

    @Override
    public void loadPreview(long millisecond, long max) {


    }

    @Override
    public void triggerPlayOrPause(final boolean setPlay) {

        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(setPlay);
            isVideoPlayWhenReady.set(setPlay);
        }

        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {
            mPlaybackActionCallback.onPlayToggle(mMediaModel, setPlay);


        }
    }



    /**
     * Change Video Scale
     */
    @Override
    public void scale() {

        mScalePresenter.doScale();
        ScaleMode scaleMode = mScalePresenter.getCurrentScaleMode();
        Toast.makeText(mEasyPlexPlayerView.getContext(), "" + scaleMode.getDescription(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLoadEpisodes() {

        mPlaybackActionCallback.onLoadEpisodes();

    }

    @Override
    public void onLoadStreaming() {

        mPlaybackActionCallback.onLoadSteaming();

    }

    /**
     * Return Movie or SERIE  Name
     */
    @Override
    public String getCurrentVideoName() {
        return videoName.get();
    }


    @Override
    public int getCurrentEpisodePosition() {
        return episodePosition.get();
    }


    @Override
    public String getEpID() {
        return episodeId.get();
    }



    /**
     * Get Next Season ID for TV-SERIE
     */

    @Override
    public String nextSeaonsID() {

        return nextSeasonsID.get();

    }


    /**
     * Get Current Season
     */
    @Override
    public String getCurrentSeason() {
        return episodeSeasonsId.get();
    }

    @Override
    public String getCurrentSeasonNumber() {
        return episodeSeasonsNumber.get();
    }


    /**
     * Get Current Video Quality (Servers)
     */
    @Override
    public String getVideoCurrentQuality() {
        return videoCurrentQuality.get();
    }


    /**
     * Get Episode Name
     */

    @Override
    public String getEpName() {
        return currentEpisodeName.get();
    }


    @Override
    public String getSeaonNumber() {
        return currentSeasonsNumber.get();
    }

    @Override
    public int getCurrentHlsFormat() {
        return 0;
    }



    @Override
    public String getCurrentExternalId() {

        return getExternalId.get();
    }


    /**
     * Get Movie or TV ID
     */

    @Override
    public String getVideoID() {
        return videoID.get();
    }


    @Override
    public String getCurrentSeasonId() {
        return currentSeasonId.get();
    }


    @Override
    public String getMediaSubstitleName() {


       return  videoExternalID.get();

    }


    /**
     * Get Media Stream Link
     */
    @Override
    public Uri getVideoUrl() {
        return videoCurrentLink.get();
    }

    @Override
    public Uri getMediaSubstitleUrl() {

        return mediaSubstitleUri.get();
    }

    @Override
    public Uri getMediaPoster() {
        return Uri.parse(currentMediaCover.get());
    }


    @Override
    public void getCurrentSpeed(String speed) {

       currentSpeed.set(speed);
    }


    /**
     * Get Media Type
     */
    @Override
    public String getMediaType() {
        return mediaType.get();
    }

    @Override
    public String getCurrentEpTmdbNumber() {

        return getCurrentMediaTmdbNumber.get();
    }


    /**
     * return Media or ad
     */

    @Override
    public boolean isCurrentVideoAd() {
        return isCurrentAd.get();
    }

    @Override
    public void isCurrentSubstitleAuto(boolean enabled) {

        autoSubstitleActivated.set(enabled);
    }


    @Override
    public void onTracksMedia() {

        mPlaybackActionCallback.onTracksMedia();

    }

    @Override
    public void clickPlaybackSetting() {

        if (mPlaybackActionCallback == null) {
            ExoPlayerLogger.w(TAG, "clickPlaybackSetting params is null");
            return;
        }

        mPlaybackActionCallback.onLoadPlaybackSetting();
    }

    @Override
    public void onLoadFromBeginning() {

        mPlaybackActionCallback.onLoadFromBeginning();

    }


    @Override
    public void onLoadSide() {


        mPlaybackActionCallback.onLoadSide();

    }


    /**
     * Release Player
     */

    @Override
    public void closePlayer() {

     ((EasyPlexMainPlayer) (mEasyPlexPlayerView.getContext())).onBackPressed();
      mPlaybackActionCallback = null;

    }




    // Return Movies List
    @Override
    public void loadMoviesList() {

        mPlaybackActionCallback.onLoadMoviesList();

    }


    // Return Next Episode for TV-Serie
    @Override
    public void nextEpisode() {

        mPlaybackActionCallback.onLoadNextEpisode();

    }


    @Override
    public void isCue(boolean enabled) {

        isCue.set(enabled);
    }



    @Override
    public void playerReady(boolean enabled) {

        playerReady.set(enabled);
    }


    @Override
    public void settingReady(boolean enabled) {

        settingReady.set(enabled);
    }




    @Override
    public void mediaHasSubstitle(boolean enabled) {

        mediaHasSubstitle.set(enabled);
    }

    @Override
    public boolean getIsMediaSubstitleGet() {

        return mediaSubstitleGet.get();
    }


    // Substitles
    @Override
    public void clickOnSubs() {

        mPlaybackActionCallback.onSubtitlesSelection();


    }

    public PlayerUIController getController() {
        return controller;

    }

    public void setController(@NonNull PlayerUIController controller) {
        this.controller = controller;

    }

    //------------------------------player playback listener-------------------------------------------//

    @Override
    public void onTimelineChanged(@NotNull Timeline timeline, @Player.TimelineChangeReason int reason) {
        setPlaybackState();
        updateProgress();
    }

    @Override
    public void onPositionDiscontinuity(final int reason) {

        setPlaybackState();
        updateProgress(); }


    @Override
    public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
        playerPlaybackState.set(playbackState);
        isVideoPlayWhenReady.set(playWhenReady);
        updateProgress();


        if (!isCurrentVideoAd() && playbackState == Player.STATE_ENDED) {

            mPlaybackActionCallback.onMediaEnded();
        }


    }

    @Override
    public void onRepeatModeChanged(final int repeatMode) {


        //

    }

    @Override
    public void onShuffleModeEnabledChanged(final boolean shuffleModeEnabled) {


        //

    }

    @Override
    @SuppressWarnings("ReferenceEquality")
    public void onTracksChanged(@NotNull TrackGroupArray trackGroups, @NotNull TrackSelectionArray trackSelections) {

       // mPlaybackActionCallback.onTracksChanged(trackGroups,trackSelections);
    }



    @Override
    public void onLoadingChanged(final boolean isLoading) {

        ExoPlayerLogger.i(TAG, "onLoadingChanged");


    }

    @Override
    public void onPlayerError(final @NotNull ExoPlaybackException error) {
        ExoPlayerLogger.i(TAG, "onPlayerError");
        isPlayerError.set(true);
        if (error.getCause() instanceof BehindLiveWindowException && getMediaType().equals("streaming")) {

            mPlaybackActionCallback.onRetry();

        }

    }



    @Override
    public void onPlaybackParametersChanged(final @NotNull PlaybackParameters playbackParameters) {


        ExoPlayerLogger.d(TAG, "onPlaybackParametersChanged");


    }

    @Override
    public void onSeekProcessed() {

        ExoPlayerLogger.d(TAG, "onSeekProcessed");


    }

    //-----------------------------------------SeekBar listener--------------------------------------------------------------//




    @Override
    public void setVideoAspectRatio(float widthHeightRatio) {

        if (mEasyPlexPlayerView != null) {
            mEasyPlexPlayerView.setAspectRatio(widthHeightRatio);
        }
        ExoPlayerLogger.i(TAG, "setVideoAspectRatio " + widthHeightRatio);

    }


    @Override
    public float getInitVideoAspectRatio() {
        ExoPlayerLogger.i(TAG, "getInitVideoAspectRatio " + mInitVideoAspectRatio);
        return mInitVideoAspectRatio;
    }

    @Override
    public void setResizeMode(final int resizeMode) {
        if (mEasyPlexPlayerView != null) {
            mEasyPlexPlayerView.setResizeMode(resizeMode);
        }
    }




    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {

        //

    }



    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
        isDraggingSeekBar = true;
        ExoPlayerLogger.i(TAG, "onStartTrackingTouch");
    }




    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {

        if (mPlayer != null) {
            seekTo(Tools.progressToMilli(mPlayer.getDuration(), seekBar));
        }

        isDraggingSeekBar = false;
        ExoPlayerLogger.i(TAG, "onStopTrackingTouch");
    }


    //---------------------------------------private method---------------------------------------------------------------------------//

    private void setPlaybackState() {
        int playBackState = mPlayer == null ? Player.STATE_IDLE : mPlayer.getPlaybackState();
        playerPlaybackState.set(playBackState);
    }

    private void seekToPosition(long positionMs) {
        if (mPlayer != null) {
            mPlayer.seekTo(mPlayer.getCurrentWindowIndex(), positionMs);
        }
    }

    private void updateProgress() {

        long position = mPlayer == null ? 0 : mPlayer.getCurrentPosition();
        long duration = mPlayer == null ? 0 : mPlayer.getDuration();
        long bufferedPosition = mPlayer == null ? 0 : mPlayer.getBufferedPosition();

        //only update the seekBar UI when user are not interacting, to prevent UI interference
        if (!isDraggingSeekBar && !isDuringCustomSeek()) {
            updateSeekBar(position, duration, bufferedPosition);
            updateTimeTextViews(position, duration);
        }

        ExoPlayerLogger.i(TAG, "updateProgress:----->" + mediaCurrentTime.get());



        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {
            mPlaybackActionCallback.onProgress(mMediaModel, position, duration);
        }else {

            return;
        }

        mProgressUpdateHandler.removeCallbacks(updateProgressAction);



        // Schedule an update if necessary.
        if (!(playerPlaybackState.get() == Player.STATE_IDLE || playerPlaybackState.get() == Player.STATE_ENDED || !mPlaybackActionCallback
                .isActive())) {

            //don't post the updateProgress event when user pause the video
            if (mPlayer != null && !mPlayer.getPlayWhenReady()) {
                return;
            }

            long delayMs;
            delayMs = DEFAULT_FREQUENCY;
            mProgressUpdateHandler.postDelayed(updateProgressAction, delayMs);
        }
    }

    private void updateSeekBar(long position, long duration, long bufferedPosition) {
        //update progressBar.
        mediaCurrentTime.set(position);
        mediaDuration.set(duration);
        mediaBufferedPosition.set(bufferedPosition);
    }

}