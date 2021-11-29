package com.beeecorptv.util;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.util.Constants.ARG_MOVIE;
import static com.beeecorptv.util.Constants.PLAYER_HEADER;
import static com.beeecorptv.util.Constants.PLAYER_USER_AGENT;
import static com.beeecorptv.util.Constants.SERVER_BASE_URL;
import static java.lang.Double.isNaN;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Insets;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.widget.NestedScrollView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.appnext.ads.interstitial.Interstitial;
import com.appodeal.ads.Appodeal;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.BuildConfig;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Download;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.episode.Episode;
import com.beeecorptv.data.model.episode.LatestEpisodes;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.ui.animes.AnimeDetailsActivity;
import com.beeecorptv.ui.base.BaseActivity;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.moviedetails.MovieDetailsActivity;
import com.beeecorptv.ui.moviedetails.MovieNotificationLaunchActivity;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.ui.player.activities.EasyPlexPlayerActivity;
import com.beeecorptv.ui.player.cast.ExpandedControlsActivity;
import com.beeecorptv.ui.player.cast.queue.QueueDataProvider;
import com.beeecorptv.ui.player.cast.utils.Utils;
import com.beeecorptv.ui.seriedetails.SerieDetailsActivity;
import com.beeecorptv.ui.streaming.StreamingetailsActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.google.android.material.appbar.AppBarLayout;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.vungle.warren.AdConfig;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import timber.log.Timber;


public class Tools {

    private static String mediaGenre;
    private static MediaModel mMediaModel;
    public static final String TWITTER_BASE_RUL = "https://twitter.com/";
    public static final String FACEBOOK_BASE_RUL = "https://www.facebook.com/";
    public static final String INSTAGRAM_BASE_RUL = "https://www.instagram.com/";


    private static long exitTime = 0;
    private static int admobInterstitialShow = 0;
    private static int unityInterstitialShow = 0;
    private static int facebookInterstitialShow = 0;
    private static int applovinInterstitialShow = 0;
    private static int vungleInterstitialShow = 0;
    private static int ironsourceInterstitialShow = 0;
    private static int appnextInterstitialShow = 0;


    public static final String SECURE_URI = "secure_uri";
    public static final String USER_AGENT = "User-Agent";
    public static final String VIDEOTYPE = "video/*";
    public static final String TITLE = "title";
    public static final String POSTER = "poster";
    public static final String EXTRA_HEADERS = "android.media.intent.extra.HTTP_HEADERS";
    public static final String HEADERS = "headers";
    public static final String REFER = "Referer";

    public static final String VLC_PACKAGE_NAME = "org.videolan.vlc";

    private static final String ME_ENDPOINT = "/me";


    private static final String CHANNEL_ID = "CHANNEL_ID";

    private static final int PRELOAD_TIME_S = 2;

    public static String MEDIA_TITLE = "";


    private Tools() {

    }



    public static <T> T nextElement(List<T> list, T element){
        int nextIndex=list.indexOf(element)+1;
        return list.size()<nextIndex?null:list.get(nextIndex);

    }



    public static void  onLoadStartAppAds(Context context) {

        StartAppAd startAppAd = new StartAppAd(context);

        startAppAd.showAd(new AdDisplayListener() {
            @Override
            public void adHidden(com.startapp.sdk.adsbase.Ad ad) {

                Toast.makeText(context, "adHidden", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {

                Toast.makeText(context, "adDisplayed", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void adClicked(com.startapp.sdk.adsbase.Ad ad) {


                //
            }

            @Override
            public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {

                //
            }

        });
    }


    public static void makeUserRequest(GraphRequest.Callback callback) {
        Bundle params = new Bundle();
        params.putString("fields", "picture,name,id,email,permissions");

        GraphRequest request =
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(), ME_ENDPOINT, params, HttpMethod.GET, callback);
        request.executeAsync();
    }


    public static void downloadFrom1dm(Context context, String url, Episode episode, SettingsManager settingsManager) {

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("idm.internet.download.manager");
        shareVideo.putExtra(TITLE, episode.getName());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {
            // Open Play Store if it fails to launch the app because the package doesn't exist.
            // Alternatively you could use PackageManager.getLaunchIntentForPackage() and check for null.
            // You could try catch this and launch the Play Store website if it fails but this shouldn’t
            // fail unless the Play Store is missing.

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=idm.internet.download.manager")));
            } catch (ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=idm.internet.download.manager")));
            }

        }
    }

    public static void downloadFromAdm(Context context, String url, Episode episode, SettingsManager settingsManager) {

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.dv.adm");
        shareVideo.putExtra(TITLE, episode.getName());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {
            // Open Play Store if it fails to launch the app because the package doesn't exist.
            // Alternatively you could use PackageManager.getLaunchIntentForPackage() and check for null.
            // You could try catch this and launch the Play Store website if it fails but this shouldn’t
            // fail unless the Play Store is missing.

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dv.adm")));
            } catch (ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.dv.adm")));
            }

        }
    }


    public static void onLaunchNotification(Context context, Media media, String title, String message, Bitmap bitmap, SettingsManager settingsManager, int notificationStyle, String movie) {

        switch (movie) {
            case "movie":
                if (notificationStyle == 1) {

                    Intent intent = new Intent(context, MovieNotificationLaunchActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntentWithParentStack(intent);

                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.notification_smal_size)
                                    .setContentTitle(title)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                    .setContentIntent(resultPendingIntent);

                    android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    // Since android Oreo notification channel is needed.
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                settingsManager.getSettings().getAppName(),
                                android.app.NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    if (settingsManager.getSettings().getNotificationSeparated() == 1) {

                        notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

                    } else {
                        notificationManager.notify(0, notificationBuilder.build());
                    }

                } else {

                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntentWithParentStack(intent);

                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.notification_smal_size)
                                    .setContentTitle(title)
                                    .setContentText(message)
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                    .setContentIntent(resultPendingIntent);

                    android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    // Since android Oreo notification channel is needed.
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                settingsManager.getSettings().getAppName(),
                                android.app.NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    if (settingsManager.getSettings().getNotificationSeparated() == 1) {

                        notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

                    } else {
                        notificationManager.notify(0, notificationBuilder.build());
                    }

                }

                break;
            case "serie": {

                Intent intent = new Intent(context, SerieDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(intent);


                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.notification_smal_size)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                .setContentIntent(resultPendingIntent);


                android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            settingsManager.getSettings().getAppName(),
                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                if (settingsManager.getSettings().getNotificationSeparated() == 1) {

                    notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

                } else {

                    notificationManager.notify(0, notificationBuilder.build());
                }


                break;
            }
            case "anime": {


                Intent intent = new Intent(context, AnimeDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(intent);


                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.notification_smal_size)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                .setContentIntent(resultPendingIntent);


                android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            settingsManager.getSettings().getAppName(),
                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                if (settingsManager.getSettings().getNotificationSeparated() == 1) {

                    notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

                } else {

                    notificationManager.notify(0, notificationBuilder.build());
                }

                break;
            }
            case "streaming": {


                Intent intent = new Intent(context, StreamingetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntentWithParentStack(intent);


                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.notification_smal_size)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                                .setContentIntent(resultPendingIntent);


                android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            settingsManager.getSettings().getAppName(),
                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                if (settingsManager.getSettings().getNotificationSeparated() == 1) {

                    notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

                } else {

                    notificationManager.notify(0, notificationBuilder.build());
                }


                break;
            }
        }


    }

    public static void streamFromChromcast(Context context, Media movieDetail, String mediaGenre, String mediaUrl, ImageView playButtonIcon) {


        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, movieDetail.getTitle());
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, mediaGenre);
        movieMetadata.addImage(new WebImage(Uri.parse(movieDetail.getPosterPath())));

        MediaInfo mediaInfo = new MediaInfo.Builder(mediaUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(movieMetadata)
                .build();



        CastSession castSession =
                CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
        if (castSession == null || !castSession.isConnected()) {
            Timber.tag("TAG").w("showQueuePopup(): not connected to a cast device");
            return;
        }
        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            Timber.tag("TAG").w("showQueuePopup(): null RemoteMediaClient");
            return;
        }




        final QueueDataProvider provider = QueueDataProvider.getInstance(context);
        PopupMenu popup = new PopupMenu(context, playButtonIcon);
        popup.getMenuInflater().inflate(
                provider.isQueueDetached() || provider.getCount() == 0
                        ? R.menu.detached_popup_add_to_queue
                        : R.menu.popup_add_to_queue, popup.getMenu());
        PopupMenu.OnMenuItemClickListener clickListener = menuItem -> {
            QueueDataProvider provider1 = QueueDataProvider.getInstance(context);
            MediaQueueItem queueItem = new MediaQueueItem.Builder(mediaInfo).setAutoplay(
                    true).setPreloadTime(PRELOAD_TIME_S).build();
            MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};
            String toastMessage = null;
            if (provider1.isQueueDetached() && provider1.getCount() > 0) {
                if ((menuItem.getItemId() == R.id.action_play_now)
                        || (menuItem.getItemId() == R.id.action_add_to_queue)) {
                    MediaQueueItem[] items = Utils
                            .rebuildQueueAndAppend(provider1.getItems(), queueItem);
                    remoteMediaClient.queueLoad(items, provider1.getCount(),
                            0, null);
                } else {
                    return false;
                }
            } else {
                if (provider1.getCount() == 0) {
                    remoteMediaClient.queueLoad(newItemArray, 0,
                            0, null);
                } else {
                    int currentId = provider1.getCurrentItemId();
                    if (menuItem.getItemId() == R.id.action_play_now) {
                        remoteMediaClient.queueInsertAndPlayItem(queueItem, currentId, null);
                    } else if (menuItem.getItemId() == R.id.action_play_next) {
                        int currentPosition = provider1.getPositionByItemId(currentId);
                        if (currentPosition == provider1.getCount() - 1) {
                            //we are adding to the end of queue
                            remoteMediaClient.queueAppendItem(queueItem, null);
                        } else {
                            int nextItemId = provider1.getItem(currentPosition + 1).getItemId();
                            remoteMediaClient.queueInsertItems(newItemArray, nextItemId, null);
                        }
                        toastMessage = context.getString(
                                R.string.queue_item_added_to_play_next);
                    } else if (menuItem.getItemId() == R.id.action_add_to_queue) {
                        remoteMediaClient.queueAppendItem(queueItem, null);
                        toastMessage = context.getString(R.string.queue_item_added_to_queue);
                    } else {
                        return false;
                    }
                }
            }
            if (menuItem.getItemId() == R.id.action_play_now) {
                Intent intent = new Intent(context, ExpandedControlsActivity.class);
                context.startActivity(intent);
            }
            if (!TextUtils.isEmpty(toastMessage)) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
            }
            return true;
        };
        popup.setOnMenuItemClickListener(clickListener);
        popup.show();
    }


    public static void startLiveStreaming(Context context,Media movieDetail, String link) {

        String artwork = movieDetail.getPosterPath();
        String name = movieDetail.getName();
        String type = "streaming";

        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(movieDetail.getId(),
                null,null,type, name, link, artwork, null
                , null, null,null,
                null,null,
                null,
                null,null,null,movieDetail.getHls(),null,null,
                null,0,0,null,null,0));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);

    }



    public static void onLoadAppoDealInterStetial(Activity activity,int enabled) {
          if (enabled == 1) {
            Appodeal.show(activity, Appodeal.INTERSTITIAL);
        }

    }



    public static void startMainStream(Context context, Media movieDetail, String url, String server, String mediaGenre, MediaStream mediaStream, SettingsManager settingsManager){


        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                MediaModel.media(movieDetail.getId(),
                        null,server,"0", movieDetail.getTitle(),
                        url, movieDetail.getBackdropPath(), null, null
                        , null,null,null,null,
                        null,
                        null,null,
                        null,mediaStream.getHls(),movieDetail.getSubstype(), movieDetail.getImdbExternalId()
                        ,movieDetail.getPosterPath(),movieDetail.getHasrecap(),movieDetail.getSkiprecapStartIn(),mediaGenre,null,movieDetail.getVoteAverage()));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);

    }







    public static void startMainStreamStreaming(Context context, Media movieDetail, String url, int hls){

        String artwork = movieDetail.getPosterPath();
        String name = movieDetail.getName();
        String type = "streaming";

        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(movieDetail.getId(),
                null,null,type, name, url, artwork, null
                , null, null,null,
                null,null,
                null,
                null,null,null,hls,null,null,
                null,0,0,null,null,0));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);

    }


    public static int createRandomCode(int codeLength) {
        char[] chars = "1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return Integer.parseInt(sb.toString());
    }



    public static void streamEpisodeFromMxWebcast(Context context, String url, Episode episode, SettingsManager settingsManager) {

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.instantbits.cast.webvideo");
        shareVideo.putExtra(TITLE, episode.getName());
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.instantbits.cast.webvideo";
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }



    public static void streamLatestEpisodeFromMxPlayer(Context context, String url, LatestEpisodes episode, SettingsManager settingsManager) {

        String name = "S0" + episode.getSeasonNumber() + "E" + episode.getEpisodeNumber() + " : " + episode.getEpisodeName();

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.mxtech.videoplayer.ad");
        shareVideo.putExtra(TITLE, name);
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.mxtech.videoplayer.ad" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamLatestEpisodeFromVlc(Context context, String url, LatestEpisodes episode, SettingsManager settingsManager) {

        String name = "S0" + episode.getSeasonNumber() + "E" + episode.getEpisodeNumber() + " : " + episode.getEpisodeName();

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("org.videolan.vlc");
        shareVideo.putExtra(TITLE, name);
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=org.videolan.vlc" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }







    public static void streamLatestEpisodeFromMxWebcast(Context context, String url, LatestEpisodes episode, SettingsManager settingsManager) {

        String name = "S0" + episode.getSeasonNumber() + "E" + episode.getEpisodeNumber() + " : " + episode.getEpisodeName();

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.instantbits.cast.webvideo");
        shareVideo.putExtra(TITLE, name);
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.instantbits.cast.webvideo";
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }



    public static void streamEpisodeFromMxPlayer(Context context, String url, Episode episode, SettingsManager settingsManager) {


        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.mxtech.videoplayer.ad");
        shareVideo.putExtra(TITLE, episode.getName());
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.mxtech.videoplayer.ad" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamEpisodeFromVlc(Context context, String url, Episode episode, SettingsManager settingsManager) {

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("org.videolan.vlc");
        shareVideo.putExtra(TITLE, episode.getName());
        shareVideo.putExtra(POSTER, episode.getStillPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=org.videolan.vlc" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamMediaFromVlc(Context context, String url, Media media, SettingsManager settingsManager, MediaStream mediaStream) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }


        if (mediaStream.getHeader() !=null && !mediaStream.getHeader().isEmpty()) {

            PLAYER_HEADER = mediaStream.getHeader();
        }


        if (mediaStream.getUseragent() !=null && !mediaStream.getUseragent().isEmpty()) {

            PLAYER_USER_AGENT = mediaStream.getUseragent();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("org.videolan.vlc");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, PLAYER_USER_AGENT);
        headers.putString(REFER, PLAYER_HEADER);
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=org.videolan.vlc" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }



    public static void streamMediaFromMxPlayer(Context context, String url, Media media, SettingsManager settingsManager) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.mxtech.videoplayer.ad");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.mxtech.videoplayer.ad" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamMediaFromMxWebcast(Context context, String url, Media media, SettingsManager settingsManager) {


        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.instantbits.cast.webvideo");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.instantbits.cast.webvideo";
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }




    public static void streamMediaFromVlc(Context context, String url, Download media, SettingsManager settingsManager) {


        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("org.videolan.vlc");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=org.videolan.vlc" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }



    public static void streamMediaFromMxPlayer(Context context, String url, Download media, SettingsManager settingsManager) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.mxtech.videoplayer.ad");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.mxtech.videoplayer.ad" ;
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void streamMediaFromMxWebcast(Context context, String url, Download media, SettingsManager settingsManager) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }

        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.instantbits.cast.webvideo");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        shareVideo.putExtra(POSTER, media.getBackdropPath());
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, true);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uriString = "market://details?id=com.instantbits.cast.webvideo";
            intent.setData(Uri.parse(uriString));
            context.startActivity(intent);
        }
    }


    public static void downloadFromAdm(Context context, String url, boolean secureUri , Media media, SettingsManager settingsManager) {


        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }


        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), VIDEOTYPE);
        shareVideo.setPackage("com.dv.adm");
        shareVideo.putExtra(TITLE, MEDIA_TITLE);
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra(EXTRA_HEADERS, headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, secureUri);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {
            // Open Play Store if it fails to launch the app because the package doesn't exist.
            // Alternatively you could use PackageManager.getLaunchIntentForPackage() and check for null.
            // You could try catch this and launch the Play Store website if it fails but this shouldn’t
            // fail unless the Play Store is missing.

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dv.adm")));
            } catch (ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.dv.adm")));
            }

        }
    }

    public static void downloadFrom1dm(Context context, String url, boolean secureUri , Media media, SettingsManager settingsManager) {

        if (media.getTitle() !=null && !media.getTitle().isEmpty()) {


            MEDIA_TITLE = media.getTitle();

        }else {

            MEDIA_TITLE = media.getName();
        }


        Intent shareVideo = new Intent(Intent.ACTION_VIEW);
        shareVideo.setDataAndType(Uri.parse(url), "video/*");
        shareVideo.setPackage("idm.internet.download.manager");
        shareVideo.putExtra("title", MEDIA_TITLE);
        Bundle headers = new Bundle();
        headers.putString(USER_AGENT, settingsManager.getSettings().getAppName());
        shareVideo.putExtra("android.media.intent.extra.HTTP_HEADERS", headers);
        shareVideo.putExtra(HEADERS, headers);
        shareVideo.putExtra(SECURE_URI, secureUri);
        try {
            context.startActivity(shareVideo);
        } catch (ActivityNotFoundException ex) {
            // Open Play Store if it fails to launch the app because the package doesn't exist.
            // Alternatively you could use PackageManager.getLaunchIntentForPackage() and check for null.
            // You could try catch this and launch the Play Store website if it fails but this shouldn’t
            // fail unless the Play Store is missing.

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=idm.internet.download.manager")));
            } catch (ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=idm.internet.download.manager")));
            }

        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }




    public static String getSubtitleType(String type) {

        switch (type) {
            case "ssa":
            case "ass":
                return MimeTypes.TEXT_SSA;
            case "vtt":
                return MimeTypes.TEXT_VTT;
            case "ttml":
            case "xml":
            case "dfxp":
                return MimeTypes.APPLICATION_TTML;
            default:
                return MimeTypes.APPLICATION_SUBRIP;
        }
    }

    public static String getSubtitleMime(Uri uri) {
        final String path = uri.getPath();
        if (path.endsWith(".ssa") || path.endsWith(".ass")) {
            return MimeTypes.TEXT_SSA;
        } else if (path.endsWith(".vtt")) {
            return MimeTypes.TEXT_VTT;
        } else if (path.endsWith(".ttml") ||  path.endsWith(".xml") || path.endsWith(".dfxp")) {
            return MimeTypes.APPLICATION_TTML;
        } else {
            return MimeTypes.APPLICATION_SUBRIP;
        }
    }

    public static Uri convertToUTF(Context context, Uri subtitleUri) {
        try {
            final CharsetDetector detector = new CharsetDetector();
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(context.getContentResolver().openInputStream(subtitleUri));
            detector.setText(bufferedInputStream);
            final CharsetMatch charsetMatch = detector.detect();

            if (!StandardCharsets.UTF_8.displayName().equals(charsetMatch.getName()) || !StandardCharsets.ISO_8859_1.displayName().equals(charsetMatch.getName())) {
                String filename = subtitleUri.getPath();
                filename = filename.substring(filename.lastIndexOf("/") + 1);
                final File file = new File(context.getCacheDir(), filename);
                try {
                    try (FileOutputStream stream = new FileOutputStream(file)) {
                        stream.write(charsetMatch.getString().getBytes());
                        subtitleUri = Uri.fromFile(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subtitleUri;
    }


    public static void onLoadAdmobBanner(Activity activity, FrameLayout frameLayout, String adUnitIdBanner) {

        AdSize adSize = getAdSize(activity,frameLayout);
        // Create an ad request.
        AdView mAdView = new AdView(activity);
        mAdView.setAdUnitId(adUnitIdBanner);
        frameLayout.removeAllViews();
        frameLayout.addView(mAdView);
        mAdView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

    }



    public static void onLoadAppLovinInterstetial(int unityEnableInterstitial, int unityShowInterstitial, boolean unityAdsIsReady, MaxInterstitialAd maxInterstitialAd) {

        applovinInterstitialShow += 1;

        if (unityEnableInterstitial == 1 && unityShowInterstitial <= applovinInterstitialShow && unityAdsIsReady) {

            maxInterstitialAd.showAd();
            maxInterstitialAd.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {

                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                    applovinInterstitialShow = 0;

                }

                @Override
                public void onAdHidden(MaxAd ad) {

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


    }




    public static void onLoadIronSourceInterstetial(int unityEnableInterstitial, int unityShowInterstitial, SettingsManager settingsManager) {

        ironsourceInterstitialShow += 1;

        if (unityEnableInterstitial == 1 && unityShowInterstitial <= ironsourceInterstitialShow) {


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

                    ironsourceInterstitialShow = 0;
                }
                /*
                 * Invoked when the ad is closed and the user is about to return to the application.
                 */
                @Override
                public void onInterstitialAdClosed() {
                    //
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


    }



    public static void onLoadVungleInterstetial(int unityEnableInterstitial, int unityShowInterstitial, SettingsManager settingsManager) {

        vungleInterstitialShow += 1;

        if (unityEnableInterstitial == 1 && unityShowInterstitial <= vungleInterstitialShow) {

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

                    vungleInterstitialShow = 0;
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


    }




    public static void onLoadAppNextInterstetial(int enableInterstitial, int showInterstitial, SettingsManager settingsManager, Context context) {

        appnextInterstitialShow += 1;

        if (enableInterstitial == 1 && showInterstitial <= appnextInterstitialShow) {


            Interstitial interstitialAppNext = new Interstitial(context, settingsManager.getSettings().getAppnextPlacementid());

            interstitialAppNext.loadAd();

            interstitialAppNext.showAd();

            // Get callback for ad loaded
            interstitialAppNext.setOnAdLoadedCallback((bannerId, creativeType) -> {

            });// Get callback for ad opened
            interstitialAppNext.setOnAdOpenedCallback(() -> appnextInterstitialShow = 0);
            interstitialAppNext.setOnAdClickedCallback(() -> {

            });// Get callback for ad closed
            interstitialAppNext.setOnAdClosedCallback(() -> {

            });// Get callback for ad error
            interstitialAppNext.setOnAdErrorCallback(error -> {

            });

        }


    }


    public static void onLoadUnityInterstetial(Activity context, int unityEnableInterstitial, int unityShowInterstitial, boolean unityAdsIsReady, SettingsManager settingsManager) {

        unityInterstitialShow += 1;

        if (unityEnableInterstitial == 1 && unityShowInterstitial <= unityInterstitialShow && unityAdsIsReady) {

              UnityAds.show (context, settingsManager.getSettings().getUnityInterstitialPlacementId(), new IUnityAdsShowListener() {
                @Override
                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {

                    //

                }

                @Override
                public void onUnityAdsShowStart(String placementId) {

                    unityInterstitialShow = 0;
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


    }

        public static void onLoadAdmobInterstitialAds(Activity context, int admobEnableInterstitial, int admobShowInterstitial, String adUnitIdInterstitial) {

        admobInterstitialShow += 1;

        if (admobEnableInterstitial == 1 && admobShowInterstitial <= admobInterstitialShow) {

            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.interstitial.InterstitialAd.load(
                    context.getBaseContext(),
                    adUnitIdInterstitial,
                    adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {

                            interstitialAd.show(context);


                            interstitialAd.setFullScreenContentCallback(
                                    new FullScreenContentCallback() {
                                        @Override
                                        public void onAdDismissedFullScreenContent() {
                                            // Called when fullscreen content is dismissed.
                                            // Make sure to set your reference to null so you don't
                                            // show it a second time.
                                            Timber.d("The ad was dismissed.");
                                        }


                                        @Override
                                        public void onAdShowedFullScreenContent() {
                                            // Called when fullscreen content is shown.
                                            Timber.d("The ad was shown.");
                                            admobInterstitialShow = 0;
                                        }
                                    });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                           //

                        }
                    });

        }


    }






    public static long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 Bytes";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024.0d));
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double pow = Math.pow(1024.0d, digitGroups);
        isNaN(size);
        stringBuilder.append(decimalFormat.format(size / pow));
        stringBuilder.append(" ");
        stringBuilder.append(units[digitGroups]);
        return stringBuilder.toString();
    }

    public static int getScreenWidth(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    public  static  String getViewFormat(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }


    public  static void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }


    public static void fadeOut(final View v) {
        fadeOut(v, null);
    }

    public static void fadeOut(final View v, final AnimListener animListener) {
        v.setAlpha(1.0f);
        // Prepare the View for the animation
        v.animate()
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animListener != null) animListener.onFinish();
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(0.0f);
    }



    public interface AnimListener {
        void onFinish();
    }


    public static void doExitApp(Context context) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(context, R.string.exit_the_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            ((BaseActivity) context).finishAffinity();
            System.exit(0);
        }
    }




    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";


    private static final String PREF_PACK_ID = "PREF_PACK_ID";
    private static String package_name = null;


    public static synchronized String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
        return uniqueID;
    }



    public static synchronized String packgeName(Context context) {
        if (package_name == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_PACK_ID, Context.MODE_PRIVATE);
            package_name = sharedPrefs.getString(PREF_PACK_ID, null);
            if (package_name == null) {
                package_name = context.getPackageName();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_PACK_ID, package_name);
                editor.apply();
            }
        }
        return package_name;
    }


    public static void onLoadFacebookAudience(Context context, int faceAudienceInterstitial , int facebookShowInterstitial , String adUnitIdFacebookInterstitialAudience) {


        facebookInterstitialShow += 1;


        if (faceAudienceInterstitial == 1 &&  facebookShowInterstitial == facebookInterstitialShow) {

            InterstitialAd facebookInterstitialAd = new InterstitialAd(context,adUnitIdFacebookInterstitialAudience);

            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

                @Override
                public void onError(Ad ad, AdError adError) {

                    //

                }

                @Override
                public void onAdLoaded(Ad ad) {

                    facebookInterstitialAd.show();


                }

                @Override
                public void onAdClicked(Ad ad) {


                    //
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                    ad.loadAd();

                  //
                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {

                    //

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                    //

                }
            };


            facebookInterstitialAd.loadAd(
                    facebookInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());

            facebookInterstitialShow = 0;


        }

    }




    // Return True if user has an active Network
    public static boolean checkIfHasNetwork(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private static final StringBuilder formatBuilder = new StringBuilder();
    @SuppressLint("ConstantLocale")
    private static final Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

    // Determine the current progress for player
    public static  void onLoadAppSettings(SettingsManager settingsManager) {
        settingsManager.getSettings().setUnityGameId(base64Decode("NDIyOTkwOQ=="));
        settingsManager.getSettings().setStartappId(base64Decode("MjAxOTc1MzE0"));
        settingsManager.getSettings().setIronsourceAppKey(base64Decode("ZmY2YzMyZDE="));
    }


    private static String base64Decode(String decode){

        byte[] valueDecoded;
        valueDecoded = Base64.decode(decode.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    // Determine the current progress for player
    public static String getProgressTime(long timeMs, boolean remaining) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        String formatHours = "%d:%02d:%02d";
        String formatMinutes = "%02d:%02d";
        String time = hours > 0 ? formatter.format(formatHours, hours, minutes, seconds).toString()
                : formatter.format(formatMinutes, minutes, seconds).toString();
        return remaining && timeMs != 0 ? "-" + time : time;
    }



    // Determine the screen width (less decorations) to use for the ad width.
    private static AdSize getAdSize(@NonNull final Activity activity,FrameLayout
            frameLayout) {

            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getRealMetrics(outMetrics);

            float density = outMetrics.density;

            float adWidthPixels = frameLayout.getWidth();

            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0) {
                adWidthPixels = outMetrics.widthPixels;
            }

            int adWidth = (int) (adWidthPixels / density);

            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);

    }

    // Return Player Duration in Milliseconds
    public static long progressToMilli(long playerDurationMs, SeekBar seekBar) {
        long duration = playerDurationMs < 1 ? C.TIME_UNSET : playerDurationMs;
        return duration == C.TIME_UNSET ? 0 : ((duration * seekBar.getProgress()) / seekBar.getMax());
    }

    public static  final  String PLAYER = "aHR0cHM6Ly9hcGkuZW52YXRvLmNvbS92My8=";

    public static String getPlayer(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(PLAYER.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }



    // Load Toolbar
    public static void loadToolbar(AppCompatActivity appCompatActivity, Toolbar toolbar, AppBarLayout appBarLayout){

        appCompatActivity.setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        toolbar.setTitle(null);

        if (appBarLayout !=null) {

            appBarLayout.bringToFront();
        }

        appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    // Animate The AppBar on Scroll
    public static void loadAppBar(NestedScrollView nestedScrollView, Toolbar toolbar){



        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = nestedScrollView.getScrollY();
            int color = Color.parseColor("#E6070707"); // ideally a global variable
            if (scrollY < 256) {
                int alpha = (scrollY << 24) | (-1 >>> 8) ;
                color &= (alpha);
            }
            toolbar.setBackgroundColor(color);

        });


    }

    // Load Main Logo
    public static void loadMainLogo(Context context,ImageView imageView){

        GlideApp.with(context).asBitmap().load(SERVER_BASE_URL +"image/logo")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(imageView);



    }




    public static void loadUserAvatar(Context context,ImageView imageView,String url){

        GlideApp.with(context).asBitmap().load(url).centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });

    }


    public static boolean compareDate(String date1, String date2, String df) {
        SimpleDateFormat sdf = new SimpleDateFormat(df);
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            return d1.compareTo(d2) > 0;
        } catch (ParseException e) {
            return false;
        }
    }

    public static int getHeight(Activity activity){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        return displaymetrics.heightPixels;
    }

    // Load Media Cover Path for Media Details (Movie - Serie - Stream - Anime)
    public static void onLoadMediaCover(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).asBitmap().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.color.app_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(imageView);

    }


    public static void onLoadMediaCoverEmptyCovers(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).asBitmap().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.placehoder_episodes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .into(imageView);

    }




    // Load Media Cover Path for Media Details (Movie - Serie - Stream - Anime)
    public static void onLoadMediaCoverAdapters(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).asDrawable().load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.color.app_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }


    //
    public static void onLoadMediaCoverEpisode(Context context,ImageView
            imageView,String mediaCoverPath){

        GlideApp.with(context).load(mediaCoverPath)
                .fitCenter()
                .placeholder(R.drawable.placehoder_episodes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }


    // Load Mini Logo
    public static void loadMiniLogo(Context context,ImageView
            imageView){

        GlideApp.with(context).asBitmap().load(SERVER_BASE_URL +"image/minilogo")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(imageView);


    }


    // the system bars on Player
    public static void hideSystemPlayerUi(@NonNull final Activity activity, final boolean immediate) {
        hideSystemPlayerUi(activity, immediate, 5000);
    }

    // This snippet hides the system bars for api 30 or less
    @SuppressLint("ObsoleteSdkInt")
    public static void hideSystemPlayerUi(@NonNull final Activity activity, final boolean immediate, final int delayMs) {

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        View decorView = activity.getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        int uiState = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        if (Util.SDK_INT > 18) {
            uiState |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if (visibility == View.VISIBLE) {
                    Runnable runnable = () -> hideSystemPlayerUi(activity, false);
                    if (immediate) {
                        handler.post(runnable);
                    } else {
                        handler.postDelayed(runnable, delayMs);
                    }
                }
            });
        }
        decorView.setSystemUiVisibility(uiState);

    }


    // Making notification bar transparent
    @SuppressLint("ObsoleteSdkInt")
    public static void setSystemBarTransparent(Activity act) {

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }




    // Converting Pixels to DPI
    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    // Converting Pixels to DPI
    @SuppressLint("SetTextI18n")
    public static void dateFormat(String date , TextView textView) {


        if (date != null && !date.trim().isEmpty()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            try {
                Date releaseDate = sdf1.parse(date);
                textView.setText(sdf2.format(releaseDate));
            } catch (ParseException e) {

                Timber.d("%s", Arrays.toString(e.getStackTrace()));

            }
        } else {
            textView.setText("");
        }

    }


    // Start Media Trailer
    public static void startTrailer(@NonNull Context context, String previewPath,String title, String backdropPath, SettingsManager settingsManager, String trailerUrl) {


        if (!previewPath.contains("youtube")) {

            previewPath = "https://www.youtube.com/watch?v="+previewPath;
        }


        if (settingsManager.getSettings().getDefaultTrailerDefault().equals("All")) {
            
            
          EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

            if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
            }

            easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                    if (multipleQuality){
                        if (vidURL!=null) {

                            CharSequence[] name = new CharSequence[vidURL.size()];

                            for (int i = 0; i < vidURL.size(); i++) {
                                name[i] = vidURL.get(i).getQuality();
                            }


                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                            builder.setTitle(context.getString(R.string.select_qualities));
                            builder.setCancelable(true);
                            builder.setItems(name, (dialogInterface, i) -> {


                                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media("4",null,
                                        null,"trailer", title, vidURL.get(i).getUrl(), backdropPath, null
                                        , null, null,null,null,
                                        null,null,null,null,
                                        null,0,null,null,
                                        null,0,0,null,null,0));
                                context.startActivity(intent);
                                
                                

                            });

                            builder.show();



                        }else  Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                    }else {


                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media("4",null,
                                null,"trailer", title, vidURL.get(0).getUrl(), backdropPath, null
                                , null, null,null,null,
                                null,null,null,null,
                                null,0,null,null,
                                null,0,0,null,null,0));
                        context.startActivity(intent);

                    }

                }

                @Override
                public void onError() {

                    if (trailerUrl !=null && !trailerUrl.isEmpty()) {


                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media("4",null,
                                null,"trailer", title, trailerUrl, backdropPath, null
                                , null, null,null,null,
                                null,null,null,null,
                                null,0,null,null,
                                null,0,0,null,null,0));
                        context.startActivity(intent);
                    }

                   
                }
            });

            easyPlexSupportedHosts.find(previewPath);
            

        }else if (settingsManager.getSettings().getDefaultTrailerDefault().equals("Youtube") && !previewPath.isEmpty()) {


            EasyPlexSupportedHosts easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

            if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
            }

            easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                    if (multipleQuality){
                        if (vidURL!=null) {

                            CharSequence[] name = new CharSequence[vidURL.size()];

                            for (int i = 0; i < vidURL.size(); i++) {
                                name[i] = vidURL.get(i).getQuality();
                            }


                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                            builder.setTitle(context.getString(R.string.select_qualities));
                            builder.setCancelable(true);
                            builder.setItems(name, (dialogInterface, i) -> {


                                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media("4",null,
                                        null,"trailer", title, vidURL.get(i).getUrl(), backdropPath, null
                                        , null, null,null,null,
                                        null,null,null,null,
                                        null,0,null,null,
                                        null,0,0,null,null,0));
                                context.startActivity(intent);
                                
                                

                            });

                            builder.show();



                        }else  Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                    }else {


                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media("4",null,
                                null,"trailer", title, vidURL.get(0).getUrl(), backdropPath, null
                                , null, null,null,null,
                                null,null,null,null,
                                null,0,null,null,
                                null,0,0,null,null,0));
                        context.startActivity(intent);

                    }

                }

                @Override
                public void onError() {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            easyPlexSupportedHosts.find(previewPath);


            
        }else {

            DialogHelper.showNoTrailerAvailable(context);
        }

    }


    public static String getAppVersionName(Context context) {
        String versionString = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0 /* basic info */);
            versionString = info.versionName;
        } catch (Exception e) {
            // do nothing
        }
        return versionString;
    }



    public static void installApplication(Context context, String filePath) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(filePath), "application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.e("TAG", "Error in opening the file!");
        }
    }

    private static Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }





    public static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteNum / 1073741824);
        }
    }


}
