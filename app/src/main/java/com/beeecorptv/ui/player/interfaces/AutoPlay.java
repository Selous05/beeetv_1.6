package com.beeecorptv.ui.player.interfaces;


import com.beeecorptv.data.model.media.MediaModel;

/**
 * Created by allensun on 6/7/18.
 * on Tubitv.com, allengotstuff@gmail.com
 */

public interface AutoPlay {

    void playNext(MediaModel nextVideo);
    void update(MediaModel update);
    void backState(MediaModel backstate);
}
