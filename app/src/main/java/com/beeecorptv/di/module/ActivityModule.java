package com.beeecorptv.di.module;

import com.beeecorptv.ui.animes.AnimeDetailsActivity;
import com.beeecorptv.ui.base.BaseActivity;
import com.beeecorptv.ui.casts.CastDetailsActivity;
import com.beeecorptv.ui.downloadmanager.ui.main.DownloadManagerFragment;
import com.beeecorptv.ui.login.LoginActivity;
import com.beeecorptv.ui.login.PasswordForget;
import com.beeecorptv.ui.moviedetails.MovieDetailsActivity;
import com.beeecorptv.ui.moviedetails.MovieNotificationLaunchActivity;
import com.beeecorptv.ui.notifications.NotificationManager;
import com.beeecorptv.ui.payment.Payment;
import com.beeecorptv.ui.payment.PaymentDetails;
import com.beeecorptv.ui.payment.PaymentPaypal;
import com.beeecorptv.ui.payment.PaymentStripe;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.ui.player.activities.EmbedActivity;
import com.beeecorptv.ui.profile.EditProfileActivity;
import com.beeecorptv.ui.register.RegisterActivity;
import com.beeecorptv.ui.register.RegistrationSucess;
import com.beeecorptv.ui.seriedetails.EpisodeDetailsActivity;
import com.beeecorptv.ui.seriedetails.SerieDetailsActivity;
import com.beeecorptv.ui.splash.SplashActivity;
import com.beeecorptv.ui.streaming.StreamingetailsActivity;
import com.beeecorptv.ui.trailer.TrailerPreviewActivity;
import com.beeecorptv.ui.upcoming.UpcomingTitlesActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app. Add bindings for other sub-components here.
 * @ContributesAndroidInjector was introduced removing the need to:
 * a) Create separate components annotated with @Subcomponent (the need to define @Subcomponent classes.)
 * b) Write custom annotations like @PerActivity.
 *
 * @author Yobex.
 */
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract BaseActivity contributeMainActivity();


    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract DownloadManagerFragment contributeMainActivityDown();

    @ContributesAndroidInjector()
    abstract Payment contributePayment();

    @ContributesAndroidInjector()
    abstract PaymentPaypal contributePaymentPaypal();


    @ContributesAndroidInjector()
    abstract PaymentStripe contributePaymentStripe();

    @ContributesAndroidInjector()
    abstract NotificationManager contributeNotificationManager();


    @ContributesAndroidInjector()
    abstract PaymentDetails contributePaymentDetails();

    @ContributesAndroidInjector()
    abstract RegistrationSucess contributeRegistrationSucess();

    @ContributesAndroidInjector()
    abstract EditProfileActivity contributeEditProfileActivity();

    @ContributesAndroidInjector()
    abstract MovieDetailsActivity contributeMovieDetailActivity();

    @ContributesAndroidInjector()
    abstract SerieDetailsActivity contributeSerieDetailActivity();

    @ContributesAndroidInjector()
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector()
    abstract RegisterActivity contributeRegisterActivity();

    @ContributesAndroidInjector()
    abstract TrailerPreviewActivity contributeTrailerPreviewActivity();

    @ContributesAndroidInjector()
    abstract UpcomingTitlesActivity contributeUpcomingTitlesActivity();

    @ContributesAndroidInjector()
    abstract AnimeDetailsActivity contributeAnimeDetailsActivity();

    @ContributesAndroidInjector()
    abstract SplashActivity contributeSplashActivity();

    @ContributesAndroidInjector()
    abstract EmbedActivity contributeEmbedActivity();

    @ContributesAndroidInjector()
    abstract EasyPlexMainPlayer contributeEasyPlexMainPlayer();


    @ContributesAndroidInjector()
    abstract PasswordForget contributePasswordForget();



    @ContributesAndroidInjector()
    abstract CastDetailsActivity contributeCastDetailsActivity();

    @ContributesAndroidInjector()
    abstract StreamingetailsActivity contributeStreamingetailsActivity();

    @ContributesAndroidInjector()
    abstract EpisodeDetailsActivity contributeEpisodeDetailsActivity();

    @ContributesAndroidInjector()
    abstract MovieNotificationLaunchActivity contributeMovieNotificationLaunchActivity();
}
