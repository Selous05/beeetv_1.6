 package com.beeecorptv.ui.notifications;

 import static com.beeecorptv.util.Constants.ARG_MOVIE;

 import android.annotation.SuppressLint;
 import android.app.NotificationChannel;
 import android.app.PendingIntent;
 import android.app.TaskStackBuilder;
 import android.content.Context;
 import android.content.Intent;
 import android.graphics.Bitmap;
 import android.graphics.drawable.Drawable;
 import android.media.RingtoneManager;
 import android.net.Uri;
 import android.os.Build;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.core.app.NotificationCompat;

 import com.bumptech.glide.Glide;
 import com.bumptech.glide.request.target.CustomTarget;
 import com.bumptech.glide.request.transition.Transition;
 import com.beeecorptv.R;
 import com.beeecorptv.data.local.entity.Media;
 import com.beeecorptv.data.model.episode.LatestEpisodes;
 import com.beeecorptv.data.repository.AnimeRepository;
 import com.beeecorptv.ui.manager.SettingsManager;
 import com.beeecorptv.ui.seriedetails.EpisodeDetailsActivity;
 import com.beeecorptv.ui.splash.SplashActivity;
 import com.beeecorptv.util.Constants;
 import com.beeecorptv.util.GlideApp;
 import com.beeecorptv.util.Tools;
 import com.google.firebase.messaging.FirebaseMessagingService;
 import com.google.firebase.messaging.RemoteMessage;

 import org.jetbrains.annotations.NotNull;

 import java.util.Map;
 import java.util.Objects;

 import javax.inject.Inject;

 import dagger.android.AndroidInjection;


 /**
 * EasyPlex - Android Movie Portal App
 * @package     EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2021 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationManager extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "CHANNEL_ID";


    @Inject
    SettingsManager settingsManager;

    @Inject
    AnimeRepository animeRepository;


    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        AndroidInjection.inject(this);

         if (remoteMessage.getData().size() > 0) {
            createNotification(remoteMessage);

        }

    }


    private void createNotification(RemoteMessage remoteMessage) {

        Map<String, String> remoteData = remoteMessage.getData();

        String imdb = remoteData.get("tmdb");
        String type = remoteData.get("type");
        String title = remoteData.get("title");
        String message = remoteData.get("message");
        String image = remoteData.get("image");
        String link = remoteData.get("link");

        if (link !=null && !link.isEmpty()) {


            if (image !=null && !image.isEmpty()) {

                final Bitmap[] bitmap = {null};

                GlideApp.with(getApplicationContext())
                        .asBitmap()
                        .load(image)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                bitmap[0] = resource;
                                notificationLink(bitmap[0], title, message,link);

                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                //
                            }
                        });

            } else {

                onLoadNotificationFromLink(link,title,message);

            }


        }else if (Objects.equals(type, "0")) {

            final Bitmap[] bitmap = {null};

            Media movieDetail = new Media();
            assert imdb != null;
            movieDetail.setId(imdb);

            GlideApp.with(getApplicationContext())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            bitmap[0] = resource;
                            notificationMovie(bitmap[0], movieDetail, title, message);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                            //
                        }
                    });

        } else if (Objects.equals(type, "1")) {

            final Bitmap[] bitmap = {null};

            Media movieDetail = new Media();
            assert imdb != null;
            movieDetail.setId(imdb);

            GlideApp.with(getApplicationContext())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            bitmap[0] = resource;
                            notificationSerie(bitmap[0], movieDetail, title, message);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                            //
                        }

                    });



        } else if (Objects.equals(type, "2")) {

            final Bitmap[] bitmap = {null};

            Media movieDetail = new Media();
            assert imdb != null;
            movieDetail.setId(imdb);

            GlideApp.with(getApplicationContext())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            bitmap[0] = resource;
                            notificationAnime(bitmap[0], movieDetail, title, message);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                            //
                        }
                    });


        } else if (Objects.equals(type, "3")) {

            final Bitmap[] bitmap = {null};

            Media movieDetail = new Media();
            assert imdb != null;
            movieDetail.setId(imdb);

            GlideApp.with(getApplicationContext())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            bitmap[0] = resource;
                            notificationStreaming(bitmap[0], movieDetail, title, message);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            //
                        }
                    });


        } else if (Objects.equals(type, "episode")) {


            final Bitmap[] bitmap = {null};

            LatestEpisodes latestEpisodes = new LatestEpisodes();
            assert imdb != null;
            latestEpisodes.setType("serie");
            latestEpisodes.setEpisodeId(Integer.parseInt(imdb));


            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            bitmap[0] = resource;
                            notificationEpisode(bitmap[0], latestEpisodes, title, message);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            //
                        }
                    });


        }else if (Objects.equals(type, "episode_anime")) {


            final Bitmap[] bitmap = {null};

            LatestEpisodes latestEpisodes = new LatestEpisodes();
            assert imdb != null;
            latestEpisodes.setType("anime");
            latestEpisodes.setAnimeEpisodeId(Integer.parseInt(imdb));


            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            bitmap[0] = resource;
                            notificationEpisodeAnime(bitmap[0], latestEpisodes, title, message);

                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            //
                        }
                    });
        }
        else if (Objects.equals(type, "custom")) {


            if (image !=null && !image.isEmpty()) {

                final Bitmap[] bitmap = {null};

                GlideApp.with(getApplicationContext())
                        .asBitmap()
                        .load(image)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                bitmap[0] = resource;
                                notificationCustom(bitmap[0],title,message);

                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                                //
                            }
                        });


            } else  {


                Intent intent = new Intent(NotificationManager.this, SplashActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                stackBuilder.addNextIntentWithParentStack(intent);

                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.notification_smal_size)
                                .setContentTitle(title)
                                .setContentText(message)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(resultPendingIntent);


                android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                            settingsManager.getSettings().getAppName(),
                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                if (settingsManager.getSettings().getNotificationSeparated() == 1) {

                    notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

                }else {

                    notificationManager.notify(0, notificationBuilder.build());
                }

            }


   }


    }


     private void onLoadNotificationFromLink(String link, String title, String message) {

         Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
         TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
         stackBuilder.addNextIntentWithParentStack(intent);
         PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

         Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
         NotificationCompat.Builder notificationBuilder =
                 new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                         .setSmallIcon(R.drawable.notification_smal_size)
                         .setContentTitle(title)
                         .setContentText(message)
                         .setAutoCancel(true)
                         .setSound(defaultSoundUri)
                         .setContentIntent(resultPendingIntent);


         android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

         // Since android Oreo notification channel is needed.
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                     settingsManager.getSettings().getAppName(),
                     android.app.NotificationManager.IMPORTANCE_DEFAULT);
             notificationManager.createNotificationChannel(channel);
         }

         if (settingsManager.getSettings().getNotificationSeparated() == 1) {

             notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

         }else {

             notificationManager.notify(0, notificationBuilder.build());
         }
     }

     private void notificationEpisodeAnime(Bitmap bitmap, LatestEpisodes latestEpisodes, String title, String message) {

        Intent intent = new Intent(NotificationManager.this, EpisodeDetailsActivity.class);
        intent.putExtra(ARG_MOVIE, latestEpisodes);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intent);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_smal_size)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                        .setContentIntent(resultPendingIntent);


        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    settingsManager.getSettings().getAppName(),
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (settingsManager.getSettings().getNotificationSeparated() == 1) {

            notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

        }else {

            notificationManager.notify(0, notificationBuilder.build());
        }

    }

    private void notificationLink(Bitmap bitmap, String title, String message, String link) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(Constants.MOVIE_LINK, link);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_smal_size)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                        .setContentIntent(resultPendingIntent);


        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    settingsManager.getSettings().getAppName(),
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (settingsManager.getSettings().getNotificationSeparated() == 1) {

            notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

        }else {

            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    private void notificationEpisode(Bitmap bitmap, LatestEpisodes latestEpisodes, String title, String message) {

        Intent intent = new Intent(NotificationManager.this, EpisodeDetailsActivity.class);
        intent.putExtra(ARG_MOVIE, latestEpisodes);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(intent);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.notification_smal_size)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                        .setContentIntent(resultPendingIntent);


        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    settingsManager.getSettings().getAppName(),
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        if (settingsManager.getSettings().getNotificationSeparated() == 1) {

            notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

        }else {

            notificationManager.notify(0, notificationBuilder.build());
        }

    }

    private void notificationStreaming(Bitmap bitmap, Media movieDetail, String title, String message) {


        Tools.onLaunchNotification(this,movieDetail,title,message,bitmap,settingsManager,settingsManager.getSettings().getNotificationStyle(),"streaming");

    }

    private void notificationCustom(Bitmap bitmap, String title, String message) {


            Intent intent = new Intent(NotificationManager.this, SplashActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addNextIntentWithParentStack(intent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.notification_smal_size)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                            .setContentIntent(resultPendingIntent);


            android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        settingsManager.getSettings().getAppName(),
                        android.app.NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

        if (settingsManager.getSettings().getNotificationSeparated() == 1) {

            notificationManager.notify(Tools.createRandomCode(2), notificationBuilder.build());

        }else {

            notificationManager.notify(0, notificationBuilder.build());
        }



    }

    private void notificationAnime(Bitmap bitmap, Media movieDetail, String title, String message) {


        Tools.onLaunchNotification(this,movieDetail,title,message,bitmap,settingsManager,settingsManager.getSettings().getNotificationStyle(),"anime");

    }

    private void notificationSerie(Bitmap bitmap, Media movieDetail, String title, String message) {


        Tools.onLaunchNotification(this,movieDetail,title,message,bitmap,settingsManager,settingsManager.getSettings().getNotificationStyle(),"serie");

    }

    private void notificationMovie(Bitmap bitmap, @NotNull Media movieDetail, String title, String message) {

        Tools.onLaunchNotification(this,movieDetail,title,message,bitmap,settingsManager,settingsManager.getSettings().getNotificationStyle(),"movie");

    }


}
