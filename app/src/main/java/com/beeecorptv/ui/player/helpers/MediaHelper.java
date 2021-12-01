package com.beeecorptv.ui.player.helpers;

import static android.content.Context.MODE_PRIVATE;
import static com.beeecorptv.BeeeTvApp.getContext;
import static com.beeecorptv.util.Constants.APP_NAME;
import static com.beeecorptv.util.Constants.PLAYER_HEADER;
import static com.beeecorptv.util.Constants.PLAYER_USER_AGENT;
import static com.beeecorptv.util.Constants.PREF_FILE;
import static com.beeecorptv.util.Tools.REFER;
import static com.beeecorptv.util.Tools.USER_AGENT;
import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;

import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.ui.manager.SettingsManager;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;


/**
 * Created by stoyan on 6/21/17.
 */

public final class MediaHelper {


    private static MediaHelper instance;

    public static synchronized  MediaHelper create(@NonNull MediaModel... models) {
        instance = new MediaHelper(models);
        return instance;
    }

    private MediaHelper(MediaModel[] models) {
        LinkedList<MediaModel> linkedList = new LinkedList<>(Arrays.asList(models));
    }

    public static synchronized  MediaHelper getInstance() {
        Assertions.checkNotNull(instance);
        return instance;
    }


    public static
    @NonNull
    DataSource.Factory buildDataSourceFactory(@NonNull Context context, DefaultBandwidthMeter.Builder bandwidthMeter, SettingsManager settingsManager) {

        return new DefaultDataSourceFactory(context, bandwidthMeter.build(), buildHttpDataSourceFactory(context, bandwidthMeter.build(),settingsManager));

    }



    public static RenderersFactory buildRenderersFactory(
            Context context, boolean preferExtensionRenderer) {

        if (preferExtensionRenderer) {

            return new DefaultRenderersFactory(context)
                    .setEnableDecoderFallback(true).setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON);
        }else {

            return new DefaultRenderersFactory(context).setEnableDecoderFallback(true);
        }
    }


    public static
    @NonNull
    HttpDataSource.Factory buildHttpDataSourceFactory(@NonNull Context context, @NonNull DefaultBandwidthMeter bandwidthMeter) {

        SharedPreferences preferences = getContext().getSharedPreferences(PREF_FILE, MODE_PRIVATE);


        return new DefaultHttpDataSourceFactory(Util.getUserAgent(context, preferences.getString(USER_AGENT, "EasyPlexPlayer")), bandwidthMeter,DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS
                , DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
    }


    public static
    @NonNull
    HttpDataSource.Factory buildHttpDataSourceFactory(@NonNull Context context, @NonNull DefaultBandwidthMeter bandwidthMeter, SettingsManager settingsManager) {




        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(PLAYER_USER_AGENT,
                bandwidthMeter,DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS
                , DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
        dataSourceFactory.getDefaultRequestProperties().set(REFER, PLAYER_HEADER);
        return dataSourceFactory;

    }


    public static
    String userAgent(){
        SharedPreferences preferences = getContext().getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        return String.format(Locale.US,
                "%s (Android %s; %s; %s %s; %s)",
                preferences.getString(APP_NAME, "EasyPlex"),
                Build.VERSION.RELEASE,
                Build.MODEL,
                Build.BRAND,
                Build.DEVICE,
                Locale.getDefault().getLanguage());
    }

}
