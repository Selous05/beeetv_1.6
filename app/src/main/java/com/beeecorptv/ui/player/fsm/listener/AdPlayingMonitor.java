package com.beeecorptv.ui.player.fsm.listener;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.beeecorptv.ui.player.fsm.state_machine.FsmAdController;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayer;
import com.beeecorptv.ui.player.utilities.EventLogger;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;


/**
 * Created by allensun on 8/9/17.
 * on Tubitv.com, allengotstuff@gmail.com
 */
public class AdPlayingMonitor extends EventLogger {

    public final FsmAdController fsmPlayer;

    public AdPlayingMonitor(@NonNull FsmPlayer fsmPlayer) {
        super(null);
        this.fsmPlayer = fsmPlayer;
    }

    @Override
    public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady, int playbackState) {
        super.onPlayerStateChanged(eventTime, playWhenReady, playbackState);

        //the last ad has finish playing.
        if (playbackState == Player.STATE_ENDED && playWhenReady) {
            fsmPlayer.removePlayedAdAndTransitToNextState();
        }
    }

    @Override
    public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
        super.onPlayerError(eventTime, error);
        fsmPlayer.removePlayedAdAndTransitToNextState();
    }

    @Override
    public void onDroppedVideoFrames(final EventTime eventTime, final int droppedFrames, final long elapsedM) {
        super.onDroppedVideoFrames(eventTime, droppedFrames, elapsedM);
        seekOrSkip();
    }

    // this is a hack to handle when played corrupted video file, it stuck in the buffering state forever.
    private void seekOrSkip() {
        if (fsmPlayer == null) {
            return;
        }

        if (fsmPlayer instanceof FsmPlayer) {

            if (((FsmPlayer) fsmPlayer).getController() != null) {
                SimpleExoPlayer adPlayer = ((FsmPlayer) fsmPlayer).getController().getAdPlayer();

                if (adPlayer != null && adPlayer.getPlaybackState() == STATE_BUFFERING) {

                    long position = Math.min(adPlayer.getCurrentPosition() + 1000, adPlayer.getDuration());
                    adPlayer.seekTo(position);
                    adPlayer.setPlayWhenReady(true);
                }
            }
        }
    }
}
