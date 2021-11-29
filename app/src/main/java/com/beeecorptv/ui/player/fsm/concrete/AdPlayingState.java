package com.beeecorptv.ui.player.fsm.concrete;

import android.view.View;
import android.webkit.WebView;
import androidx.annotation.NonNull;
import com.beeecorptv.ui.player.utilities.PlayerDeviceUtils;
import com.beeecorptv.ui.player.views.EasyPlexPlayerView;
import com.beeecorptv.util.Constants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.beeecorptv.ui.player.controller.PlayerAdLogicController;
import com.beeecorptv.ui.player.controller.PlayerUIController;
import com.beeecorptv.ui.player.fsm.BaseState;
import com.beeecorptv.ui.player.fsm.Input;
import com.beeecorptv.ui.player.fsm.State;
import com.beeecorptv.ui.player.fsm.concrete.factory.StateFactory;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayer;
import com.beeecorptv.data.model.ads.AdMediaModel;
import com.beeecorptv.data.model.media.MediaModel;


/**
 * Created by allensun on 7/31/17.
 */
public class AdPlayingState extends BaseState {

    @Override
    public State transformToState(Input input, @NonNull StateFactory factory) {

        if (input == Input.NEXT_AD) {
            return factory.createState(AdPlayingState.class);
        } else if (input == Input.AD_CLICK) {
            return factory.createState(VastAdInteractionSandBoxState.class);
        } else if (input == Input.AD_FINISH) {
            return factory.createState(MoviePlayingState.class);
        } else if (input == Input.VPAID_MANIFEST) {
            return factory.createState(VpaidState.class);
        }
        return null;
    }

    @Override
    public void performWorkAndUpdatePlayerUI(@NonNull FsmPlayer fsmPlayer) {
        super.performWorkAndUpdatePlayerUI(fsmPlayer);

        if (isNull(fsmPlayer)) {
            return;
        }

        //reset the ad player position everytime when a transition to AdPlaying occur
        controller.clearAdResumeInfo();

        playingAdAndPauseMovie(controller, adMedia, componentController, fsmPlayer);
    }

    private void playingAdAndPauseMovie(PlayerUIController controller, AdMediaModel adMediaModel,
                                        PlayerAdLogicController componentController, FsmPlayer fsmPlayer) {

        SimpleExoPlayer adPlayer = controller.getAdPlayer();
        SimpleExoPlayer moviePlayer = controller.getContentPlayer();

        // then setup the player for ad to playe
        MediaModel adMedia = adMediaModel.nextAD();

        if (adMedia != null) {

            if (adMedia.isVpaid()) {
                fsmPlayer.transit(Input.VPAID_MANIFEST);
                return;
            }

            hideVpaidNShowPlayer(controller);

            moviePlayer.setPlayWhenReady(false);

            // We need save movie play position before play ads for single player instance case
            if (PlayerDeviceUtils.useSinglePlayer() && !controller.isPlayingAds) {
                long resumePosition = Math.max(0, moviePlayer.getCurrentPosition());
                controller.setMovieResumeInfo(moviePlayer.getCurrentWindowIndex(), resumePosition);
            }

            //prepare the moviePlayer with data source and set it play

            boolean haveResumePosition = controller.getAdResumePosition() != C.TIME_UNSET;

            //prepare the mediaSource to AdPlayer
            adPlayer.prepare(adMedia.getMediaSource(), !haveResumePosition, true);
            controller.isPlayingAds = true;

            if (haveResumePosition) {
                adPlayer.seekTo(adPlayer.getCurrentWindowIndex(), controller.getAdResumePosition());
            }

            //update the ExoPlayerView with AdPlayer and AdMedia
            EasyPlexPlayerView easyPlexPlayerView = (EasyPlexPlayerView) controller.getExoPlayerView();
            easyPlexPlayerView.setPlayer(adPlayer, componentController.getTubiPlaybackInterface());
            easyPlexPlayerView.setMediaModel(adMedia);
            //update the numbers of ad left to give user indicator
            easyPlexPlayerView.setAvailableAdLeft(adMediaModel.nubmerOfAd());

            //Player the Ad.
            adPlayer.setPlayWhenReady(true);
            adPlayer.addAnalyticsListener(componentController.getAdPlayingMonitor());
            adPlayer.setMetadataOutput(componentController.getAdPlayingMonitor());

            //hide the subtitle view when ad is playing
            ((EasyPlexPlayerView) controller.getExoPlayerView()).getSubtitleView().setVisibility(View.INVISIBLE);
        }
    }

    private void hideVpaidNShowPlayer(final PlayerUIController imcontroller) {

        imcontroller.getExoPlayerView().setVisibility(View.VISIBLE);

        WebView vpaidEWebView = imcontroller.getVpaidWebView();
        if (vpaidEWebView != null) {
            vpaidEWebView.setVisibility(View.GONE);
            vpaidEWebView.loadUrl(Constants.EMPTY_URL);
            vpaidEWebView.clearHistory();
        }
    }

}
