package com.beeecorptv.ui.player.fsm.concrete;

import static com.google.android.exoplayer2.Player.STATE_IDLE;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.beeecorptv.ui.player.fsm.BaseState;
import com.beeecorptv.ui.player.fsm.Input;
import com.beeecorptv.ui.player.fsm.State;
import com.beeecorptv.ui.player.fsm.concrete.factory.StateFactory;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayer;

/**
 * Created by allensun on 7/31/17.
 */
public class ReceiveAdState extends BaseState {

    @Override
    public State transformToState(Input input, @NonNull StateFactory factory) {

        if (input == Input.SHOW_ADS) {
            return factory.createState(AdPlayingState.class);
        }

        return null;
    }

    @Override
    public void performWorkAndUpdatePlayerUI(@NonNull FsmPlayer fsmPlayer) {
        super.performWorkAndUpdatePlayerUI(fsmPlayer);

        // doesn't need to do any UI work.
        if (isNull(fsmPlayer)) {
            return;
        }

        SimpleExoPlayer moviePlayer = controller.getContentPlayer();

        // this mean, user jump out of the activity lifecycle in ReceivedAdState.
        if (moviePlayer != null && moviePlayer.getPlaybackState() == STATE_IDLE) {
            fsmPlayer.transit(Input.ERROR);
        }

    }
}
