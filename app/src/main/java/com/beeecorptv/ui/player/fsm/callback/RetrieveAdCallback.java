package com.beeecorptv.ui.player.fsm.callback;


import com.beeecorptv.data.model.ads.AdMediaModel;

/**
 * Created by allensun on 8/2/17.
 */
public interface RetrieveAdCallback {

    void onReceiveAd(AdMediaModel mediaModels);

    void onError();

    void onEmptyAdReceived();
}
