package com.beeecorptv.ui.home.adapters;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.util.Constants.ARG_MOVIE;
import static com.beeecorptv.util.Constants.DEFAULT_WEBVIEW_ADS_RUNNING;
import static com.beeecorptv.util.Constants.PLAYER_HEADER;
import static com.beeecorptv.util.Constants.PLAYER_USER_AGENT;
import static com.beeecorptv.util.Constants.SERVER_BASE_URL;
import static com.beeecorptv.util.Constants.WIFI_CHECK;
import static com.google.android.gms.cast.MediaStatus.REPEAT_MODE_REPEAT_OFF;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.appnext.ads.fullscreen.RewardedVideo;
import com.appnext.base.Appnext;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Animes;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.local.entity.Series;
import com.beeecorptv.data.model.episode.Episode;
import com.beeecorptv.data.model.featureds.Featured;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.media.StatusFav;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.data.repository.AnimeRepository;
import com.beeecorptv.data.repository.AuthRepository;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.RowItemFeaturedBinding;
import com.beeecorptv.ui.animes.AnimeDetailsActivity;
import com.beeecorptv.ui.base.BaseActivity;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.moviedetails.MovieDetailsActivity;
import com.beeecorptv.ui.player.activities.EasyPlexMainPlayer;
import com.beeecorptv.ui.player.activities.EasyPlexPlayerActivity;
import com.beeecorptv.ui.player.activities.EmbedActivity;
import com.beeecorptv.ui.player.cast.ExpandedControlsActivity;
import com.beeecorptv.ui.player.cast.queue.QueueDataProvider;
import com.beeecorptv.ui.player.cast.utils.Utils;
import com.beeecorptv.ui.seriedetails.SerieDetailsActivity;
import com.beeecorptv.ui.settings.SettingsActivity;
import com.beeecorptv.ui.streaming.StreamingetailsActivity;
import com.beeecorptv.ui.trailer.TrailerPreviewActivity;
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.GlideApp;
import com.beeecorptv.util.NetworkUtils;
import com.beeecorptv.util.Tools;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.ads.UnityAdsImplementation;
import com.vungle.warren.AdConfig;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


/**
 * Adapter for Featured Movies.
 *
 * @author Yobex.
 */
public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {


    private MaxRewardedAd maxRewardedAd;
    private EasyPlexSupportedHosts easyPlexSupportedHosts;
    private String livegenre;
    private CountDownTimer mCountDownTimer;
    private boolean webViewLauched = false;
    private MediaModel mMediaModel;
    private IUnityAdsListener iUnityAdsListener;
    private boolean isMovieFav = false;
    private List<Featured> castList;
    private SharedPreferences preferences;
    private AuthManager authManager;
    private SettingsManager settingsManager;
    private Context context;
    private TokenManager tokenManager;
    private MediaRepository mediaRepository;
    private AuthRepository authRepository;
    private AnimeRepository animeRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private StartAppAd startAppAd;
    private boolean adsLaunched = false;
    private RewardedAd mRewardedAd;
    boolean isLoading;
    private History history;
    private static final int PRELOAD_TIME_S = 2;
    private static final String TAG = "FeaturedAdapter";
    private int qualitySelected;
    private String mediaGenre;
    private Series series;
    private Animes animes;


    @SuppressLint("NotifyDataSetChanged")
    public void addFeatured(List<Featured> castList, Context context, SharedPreferences preferences,
                            MediaRepository mediaRepository, AuthManager authManager, SettingsManager settingsManager,
                            TokenManager tokenManager, AnimeRepository animeRepository, AuthRepository authRepository) {
        this.castList = castList;
        this.context = context;
        this.preferences = preferences;
        this.mediaRepository = mediaRepository;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.tokenManager = tokenManager;
        this.animeRepository = animeRepository;
        this.authRepository = authRepository;
        notifyDataSetChanged();


    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowItemFeaturedBinding binding = RowItemFeaturedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new FeaturedViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return Math.min(castList.size(), settingsManager.getSettings().getFeaturedHomeNumbers());
        } else {
            return 0;
        }
    }
    class FeaturedViewHolder extends RecyclerView.ViewHolder {


        private final RowItemFeaturedBinding binding;

        FeaturedViewHolder(@NonNull RowItemFeaturedBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }



        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        void onBind(final int position) {

            final Featured featured = castList.get(position);

            if (!adsLaunched) {

                createAndLoadRewardedAd();

            }

            if ("Anime".equals(featured.getType())) {
                onLoadFeaturedAnimes(featured);
            } else if ("Serie".equals(featured.getType())) {
                onLoadFeaturedSeries(featured);
            } else if ("Movie".equals(featured.getType())) {
                onLoadFeaturedMovies(featured);
            } else if ("Streaming".equals(featured.getType())) {
                onLoadFeaturedStreaming(featured);
            }else if ("Custom".equals(featured.getType())) {
                onLoadFeaturedCustom(featured);
            }

            GlideApp.with(context).asBitmap().load(featured.getPosterPath())
                    .fitCenter()
                    .placeholder(R.color.app_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .override(Tools.getScreenWidth(((BaseActivity)context)), Tools.getHeight(((BaseActivity)context)))
                    .into(binding.itemMovieImage);


            GlideApp.with(context).asBitmap().load(featured.getMiniposter())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .override(Tools.getScreenWidth(((BaseActivity)context)), Tools.getHeight(((BaseActivity)context)))
                    .into(binding.miniPoster);


            if (featured.getEnableMiniposter() == 1) {

                binding.miniPoster.setVisibility(View.VISIBLE);

            }else {

                binding.miniPoster.setVisibility(GONE);


            }


        }

        private void onLoadFeaturedCustom(Featured featured) {

            binding.customAdFeatured.setVisibility(View.VISIBLE);
            binding.linearAddFavorite.setVisibility(GONE);
            binding.linearInfoTrailer.setVisibility(GONE);
            binding.PlayButtonIcon.setVisibility(GONE);
            binding.mgenres.setVisibility(GONE);
            binding.movietitle.setText(featured.getTitle());
            binding.rootLayout.setOnClickListener(v -> {

                if (featured.getCustomLink() !=null &&  !featured.getCustomLink().isEmpty()) {
                   context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(featured.getCustomLink())));
                }else {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://codecanyon.net/user/yobex")));
                }
            });
        }

        @SuppressLint("SetTextI18n")
        private void onLoadFeaturedStreaming(Featured featured) {

            binding.customAdFeatured.setVisibility(GONE);

            binding.rootLayout.setOnClickListener(view -> mediaRepository.getStream(String.valueOf(featured.getFeaturedId()),
                    settingsManager.getSettings().getCue())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Media media) {

                            Intent intent = new Intent(context, StreamingetailsActivity.class);
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);

                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            //

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    }));

            if (featured.getPremuim() == 1) {

                binding.btnPremuim.setVisibility(View.VISIBLE);
            }

            Tools.setMargins(binding.PlayButtonIcon, 100, 0, 100, 0);

            binding.featutedMainInfo.setVisibility(GONE);
            binding.linearAddFavorite.setVisibility(GONE);
            binding.linearInfoTrailer.setVisibility(GONE);
            binding.viewIslive.setVisibility(View.VISIBLE);
            binding.PlayButtonIcon.setText(R.string.watch_live_streaming);
            binding.movietitle.setText(featured.getTitle());
            binding.mgenres.setText(featured.getGenre());

            binding.PlayButtonIcon.setOnClickListener(view -> mediaRepository.getStream(String.valueOf(featured.getFeaturedId())
                    , settingsManager.getSettings().getCue())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                            //
                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Media movieDetail) {


                            if (settingsManager.getSettings().getLivetvMultiServers() == 1) {


                                if (movieDetail.getVideos() != null && !movieDetail.getVideos().isEmpty()) {


                                    if (movieDetail.getVip() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                        onLoadStreamStreaming(movieDetail);

                                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getVip() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                        onLoadSubscribeDialog(movieDetail, "streaming");

                                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getVip() == 0) {


                                        onLoadStreamStreaming(movieDetail);


                                    } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getVip() == 0) {


                                        onLoadStreamStreaming(movieDetail);


                                    } else {

                                        DialogHelper.showPremuimWarning(context);

                                    }
                                } else {


                                    DialogHelper.showNoStreamAvailable(context);
                                }

                            } else {


                                if (movieDetail.getLink() != null && !movieDetail.getLink().isEmpty()) {


                                    if (movieDetail.getVip() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                        onLoadStreamStreaming(movieDetail);


                                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getVip() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                        onLoadSubscribeDialog(movieDetail, "streaming");

                                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getVip() == 0) {


                                        onLoadStreamStreaming(movieDetail);


                                    } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getVip() == 0) {


                                        onLoadStreamStreaming(movieDetail);


                                    } else {

                                        DialogHelper.showPremuimWarning(context);

                                    }
                                } else {

                                    DialogHelper.showNoStreamAvailable(context);
                                }

                            }


                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            //
                        }

                        @Override
                        public void onComplete() {
                            //
                        }
                    }));

        }

        private void onLoadStreamStreaming(Media movieDetail) {


            for (Genre genre : movieDetail.getGenres()) {
                binding.mgenres.setText(genre.getName());
                livegenre = genre.getName();
            }

            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                    .getCurrentCastSession();

            if (settingsManager.getSettings().getLivetvMultiServers() == 1) {


                if (settingsManager.getSettings().getServerDialogSelection() == 1) {
                    String[] charSequence = new String[movieDetail.getVideos().size()];


                    for (int i = 0; i < movieDetail.getVideos().size(); i++) {
                        charSequence[i] = movieDetail.getVideos().get(i).getServer() + " - " + movieDetail.getVideos().get(i).getLang();

                    }

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                    builder.setTitle(context.getString(R.string.select_qualities));
                    builder.setCancelable(true);
                    builder.setItems(charSequence, (dialogInterface, wich) -> {


                        if (movieDetail.getVideos().get(wich).getHeader() !=null && !movieDetail.getVideos().get(wich).getHeader().isEmpty()) {

                            PLAYER_HEADER = movieDetail.getVideos().get(wich).getHeader();
                        }


                        if (movieDetail.getVideos().get(wich).getUseragent() !=null && !movieDetail.getVideos().get(wich).getUseragent().isEmpty()) {

                            PLAYER_USER_AGENT = movieDetail.getVideos().get(wich).getUseragent();
                        }



                        if (movieDetail.getVideos().get(wich).getEmbed() == 1) {

                            startStreamFromEmbed(movieDetail.getVideos().get(wich).getLink());

                        }  else {

                            if (castSession != null && castSession.isConnected()) {

                                startStreamCasting(movieDetail, movieDetail.getVideos().get(wich).getLink());

                            }else {

                                if (settingsManager.getSettings().getVlc() == 1) {

                                    startStreamFromExternalLaunchers(movieDetail,movieDetail.getVideos().get(wich).getLink()
                                            ,movieDetail.getVideos().get(wich).getHls(),movieDetail.getVideos().get(wich));

                                }else {

                                    Tools.startLiveStreaming(context,movieDetail,movieDetail.getVideos().get(wich).getLink());
                                }

                            }

                        }

                    });

                    builder.show();

                }else {


                       if (movieDetail.getVideos().get(0).getHeader() !=null && !movieDetail.getVideos().get(0).getHeader().isEmpty()) {

                        PLAYER_HEADER = movieDetail.getVideos().get(0).getHeader();
                    }


                    if (movieDetail.getVideos().get(0).getUseragent() !=null && !movieDetail.getVideos().get(0).getUseragent().isEmpty()) {

                        PLAYER_USER_AGENT = movieDetail.getVideos().get(0).getUseragent();
                    }


                    if (movieDetail.getVideos().get(0).getEmbed() == 1) {

                        startStreamFromEmbed(movieDetail.getVideos().get(0).getLink());

                    }  else {

                        if (castSession != null && castSession.isConnected()) {

                            startStreamCasting(movieDetail, movieDetail.getVideos().get(0).getLink());

                        }else {

                            if (settingsManager.getSettings().getVlc() == 1) {

                                startStreamFromExternalLaunchers(movieDetail,movieDetail.getVideos().get(0).getLink()
                                        ,movieDetail.getVideos().get(0).getHls(), movieDetail.getVideos().get(0));

                            }else {

                                Tools.startLiveStreaming(context,movieDetail,movieDetail.getVideos().get(0).getLink());
                            }

                        }

                    }


                }

            }else {

                if (movieDetail.getEmbed() == 1) {

                    startStreamFromEmbed(movieDetail.getLink());


                }  else {

                    if (castSession != null && castSession.isConnected()) {

                        startStreamCasting(movieDetail, movieDetail.getLink());

                    }else {

                        if (settingsManager.getSettings().getVlc() == 1) {

                            startStreamFromExternalLaunchers(movieDetail, movieDetail.getLink(), movieDetail.getHls(), null);

                        }else {

                            Tools.startLiveStreaming(context,movieDetail,movieDetail.getLink());
                        }

                    }

                }

            }

        }

        private void onLoadFeaturedSeries(Featured featured) {

            onLoadRating(featured.getVoteAverage());

            binding.customAdFeatured.setVisibility(GONE);

            onCheckFavoriteSeries(featured);


            try {
                onLoadDate(featured.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            binding.addToFavorite.setOnClickListener(v -> {

                if (settingsManager.getSettings().getFavoriteonline() == 1 && tokenManager.getToken().getAccessToken() != null) {

                    if (isMovieFav) {

                        authRepository.getDeleteSerieOnline(String.valueOf(featured.getFeaturedId()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .cache()
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull StatusFav statusFav) {

                                        Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onError(@NotNull Throwable e) {


                                        //

                                    }

                                    @Override
                                    public void onComplete() {

                                        //

                                    }
                                });

                        binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                    } else {

                        Timber.i("Added To Watchlist");

                        authRepository.getAddSerieOnline(String.valueOf(featured.getFeaturedId()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .cache()
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull StatusFav statusFav) {

                                        Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();


                                    }

                                    @Override
                                    public void onError(@NotNull Throwable e) {

                                        //


                                    }

                                    @Override
                                    public void onComplete() {

                                        //

                                    }
                                });

                        binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                    }

                }  else {

                    series  = new Series(String.valueOf(featured.getFeaturedId()),String.valueOf(featured.getFeaturedId()),featured.getPosterPath(),featured.getTitle());

                    if (mediaRepository.isSerieFavorite(Integer.parseInt(String.valueOf(featured.getFeaturedId())))) {

                        Timber.i("Removed From Watchlist");
                        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeFavoriteSeries(series))
                                .subscribeOn(Schedulers.io())
                                .subscribe());

                        binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                        Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

                    }else {

                        Timber.i("Added To Watchlist");
                        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addFavoriteSerie(series))
                                .subscribeOn(Schedulers.io())
                                .subscribe());

                        binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                        Toast.makeText(context, "Added To Watchlist", Toast.LENGTH_SHORT).show();
                    }

                }

            });



            binding.PlayButtonIcon.setOnClickListener(view -> mediaRepository.getSerie(String.valueOf(featured.getFeaturedId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Media movieDetail) {


                            if (movieDetail.getSeasons().get(0).getEpisodes().get(0).getVideos() != null
                                    && movieDetail.getSeasons().get(0).getEpisodes().get(0).getVideos().isEmpty()) {

                                DialogHelper.showNoStreamAvailable(context);

                            } else {

                                if (movieDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                    onLoadStreamSerie(movieDetail);


                                } else if (settingsManager.getSettings().getEnableWebview() == 1) {


                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.episode_webview);
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());

                                    lp.gravity = Gravity.BOTTOM;
                                    lp.width = MATCH_PARENT;
                                    lp.height = MATCH_PARENT;


                                    mCountDownTimer = new CountDownTimer(DEFAULT_WEBVIEW_ADS_RUNNING, 1000) {
                                        @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            if (!webViewLauched) {

                                                WebView webView = dialog.findViewById(R.id.webViewVideoBeforeAds);
                                                webView.getSettings().setJavaScriptEnabled(true);
                                                webView.setWebViewClient(new WebViewClient());
                                                WebSettings webSettings = webView.getSettings();
                                                webSettings.setJavaScriptEnabled(true);
                                                webSettings.setSupportMultipleWindows(false);
                                                webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                                                if (settingsManager.getSettings().getWebviewLink() != null && !settingsManager.getSettings().getWebviewLink().isEmpty()) {

                                                    webView.loadUrl(settingsManager.getSettings().getWebviewLink());
                                                } else {

                                                    webView.loadUrl(SERVER_BASE_URL + "webview");
                                                }

                                                webViewLauched = true;
                                            }

                                        }

                                        @Override
                                        public void onFinish() {

                                            dialog.dismiss();
                                            onLoadStreamSerie(movieDetail);
                                            webViewLauched = false;

                                            if (mCountDownTimer != null) {

                                                mCountDownTimer.cancel();
                                                mCountDownTimer = null;

                                            }
                                        }

                                    }.start();


                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);


                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                    onLoadSubscribeDialog(movieDetail, "serie");

                                } else if (settingsManager.getSettings().getWachAdsToUnlockPlayer() == 0 && movieDetail.getPremuim() == 0) {


                                    onLoadStreamSerie(movieDetail);

                                } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getPremuim() == 0) {

                                    onLoadStreamSerie(movieDetail);


                                } else {

                                    DialogHelper.showPremuimWarning(context);

                                }
                            }

                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                            //


                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    }));


            binding.movietitle.setText(featured.getTitle());
            binding.mgenres.setText(featured.getGenre());

            binding.infoTrailer.setOnClickListener(v -> mediaRepository.getSerie(String.valueOf(featured.getFeaturedId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Media media) {

                            if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                                DialogHelper.showWifiWarning(context);

                            } else {

                                Intent intent = new Intent(context, TrailerPreviewActivity.class);
                                intent.putExtra(ARG_MOVIE, media);
                                context.startActivity(intent);
                                Animatoo.animateFade(context);
                            }


                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                            //


                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    }));


            binding.rootLayout.setOnClickListener(view -> mediaRepository.getSerie(String.valueOf(featured.getFeaturedId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Media media) {

                            Intent intent = new Intent(context, SerieDetailsActivity.class);
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);

                        }

                        @Override
                        public void onError(@NotNull Throwable e) {

                            //


                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    }));


            if (featured.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            } else {

                binding.moviePremuim.setVisibility(GONE);
            }
        }

        private void onLoadStreamSerie(Media media) {


            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                    .getCurrentCastSession();

             if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                String[] charSequence = new String[media.getSeasons().get(0).getEpisodes().get(0).getVideos().size()];
                for (int i = 0; i<media.getSeasons().get(0).getEpisodes().get(0).getVideos().size(); i++) {
                    charSequence[i] = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(i).getServer());

                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                builder.setTitle(R.string.source_quality);
                builder.setCancelable(true);
                builder.setItems(charSequence, (dialogInterface, wich) -> {


                    if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getHeader() !=null &&

                            !media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getHeader().isEmpty()) {

                        PLAYER_HEADER = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getHeader();
                    }


                    if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getUseragent() !=null

                            && !media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getUseragent().isEmpty()) {

                        PLAYER_USER_AGENT = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getUseragent();
                    }



                    String tvseasonid = String.valueOf(media.getSeasons().get(0).getId());
                    Integer currentep = Integer.parseInt(media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber());
                    String currentepname = media.getSeasons().get(0).getEpisodes().get(0).getName();
                    String currenteptmdbnumber = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
                    String currentseasons = media.getSeasons().get(0).getSeasonNumber();
                    String currentseasonsNumber = media.getSeasons().get(0).getSeasonNumber();
                    String currentepimdb = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
                    String artwork = media.getSeasons().get(0).getEpisodes().get(0).getStillPath();
                    float voteAverage = Float.parseFloat(media.getSeasons().get(0).getEpisodes().get(0).getVoteAverage());
                    String type = "1";
                    String currentquality =  media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getServer();
                    String name = "S0" + currentseasons + "E" + media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber() + " : " +
                            media.getSeasons().get(0).getEpisodes().get(0).getName();
                    String serieCover = media.getPosterPath();
                    Integer episodeHasRecap = media.getSeasons().get(0).getEpisodes().get(0).getHasrecap();
                    Integer episodeRecapStartIn = media.getSeasons().get(0).getEpisodes().get(0).getSkiprecapStartIn();
                    int hls = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getHls();

                    for (Genre genre : media.getGenres()) {
                        mediaGenre = genre.getName();
                    }

                    if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getEmbed() == 1) {

                        Intent intent = new Intent(context, EmbedActivity.class);
                        intent.putExtra(Constants.MOVIE_LINK, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());
                        context.startActivity(intent);

                    }else if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getSupportedHosts() == 1){

                        easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                        if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                            easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                        }

                        easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                        easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                            @Override
                            public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                if (multipleQuality) {
                                    if (vidURL != null) {
                                        CharSequence[] names = new CharSequence[vidURL.size()];

                                        for (int i = 0; i < vidURL.size(); i++) {
                                            names[i] = vidURL.get(i).getQuality();
                                        }

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                        builder.setTitle(context.getString(R.string.select_qualities));
                                        builder.setCancelable(true);
                                        builder.setItems(names, (dialogInterface, i) -> {

                                            if (castSession != null && castSession.isConnected()) {


                                                onLoadChromcastSeries(media.getSeasons().get(0).getEpisodes().get(0), castSession, vidURL.get(i).getUrl()
                                                ,media);


                                            }else {

                                                mMediaModel =  MediaModel.media(media.getId(),
                                                        null,
                                                        currentquality, type, name, vidURL.get(wich).getUrl(), artwork,
                                                        null, currentep
                                                        , currentseasons, currentepimdb, tvseasonid,
                                                        currentepname,
                                                        currentseasonsNumber, null,
                                                        currenteptmdbnumber, media.getPremuim(),hls,
                                                        null,media.getImdbExternalId(),serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                                                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                                                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,mMediaModel);
                                                intent.putExtra(ARG_MOVIE, media);
                                                context.startActivity(intent);

                                            }


                                        });

                                        builder.show();


                                    } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {

                                    if (castSession != null && castSession.isConnected()) {


                                        onLoadChromcastSeries(media.getSeasons().get(0).getEpisodes().get(0), castSession, vidURL.get(wich).getUrl()
                                                ,media);


                                    }else {

                                        mMediaModel =  MediaModel.media(media.getId(),
                                                null,
                                                currentquality, type, name, vidURL.get(0).getUrl(), artwork,
                                                null, currentep
                                                , currentseasons, currentepimdb, tvseasonid,
                                                currentepname,
                                                currentseasonsNumber, null,
                                                currenteptmdbnumber, media.getPremuim(),hls,
                                                null,media.getImdbExternalId(),serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,mMediaModel);
                                        intent.putExtra(ARG_MOVIE, media);
                                        context.startActivity(intent);

                                    }


                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());


                    }else {


                        if (castSession != null && castSession.isConnected()) {


                            onLoadChromcastSeries(media.getSeasons().get(0).getEpisodes().get(0), castSession, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink()
                                    ,media);


                        }else {

                            mMediaModel =  MediaModel.media(media.getId(),
                                    null,
                                    currentquality, type, name, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(), artwork,
                                    null, currentep
                                    , currentseasons, currentepimdb, tvseasonid,
                                    currentepname,
                                    currentseasonsNumber, null,
                                    currenteptmdbnumber, media.getPremuim(),hls,
                                    null,media.getImdbExternalId(),serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,mMediaModel);
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);

                        }

                    }

                });

                 builder.show();

             }else {




                 if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getHeader() !=null &&

                         !media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getHeader().isEmpty()) {

                     PLAYER_HEADER = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getHeader();
                 }


                 if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getUseragent() !=null

                         && !media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getUseragent().isEmpty()) {

                     PLAYER_USER_AGENT = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getUseragent();
                 }


                 String tvseasonid = String.valueOf(media.getSeasons().get(0).getId());
                    Integer currentep = Integer.parseInt(media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber());
                    String currentepname = media.getSeasons().get(0).getEpisodes().get(0).getName();
                    String currenteptmdbnumber = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
                    String currentseasons = media.getSeasons().get(0).getSeasonNumber();
                    String currentseasonsNumber = media.getSeasons().get(0).getSeasonNumber();
                    String currentepimdb = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
                    String artwork = media.getSeasons().get(0).getEpisodes().get(0).getStillPath();
                    float voteAverage = Float.parseFloat(media.getSeasons().get(0).getEpisodes().get(0).getVoteAverage());
                    String type = "1";
                    String currentquality =  media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getServer();
                    String name = "S0" + currentseasons + "E" + media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber() + " : " +
                            media.getSeasons().get(0).getEpisodes().get(0).getName();
                    String videourl =  media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink();
                    String serieCover = media.getPosterPath();
                    Integer episodeHasRecap = media.getSeasons().get(0).getEpisodes().get(0).getHasrecap();
                    Integer episodeRecapStartIn = media.getSeasons().get(0).getEpisodes().get(0).getSkiprecapStartIn();
                    int hls = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getHls();

                    for (Genre genre : media.getGenres()) {
                        mediaGenre = genre.getName();
                    }

                    if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getEmbed() == 1) {

                        Intent intent = new Intent(context, EmbedActivity.class);
                        intent.putExtra(Constants.MOVIE_LINK, videourl);
                        context.startActivity(intent);

                    }else if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getSupportedHosts() == 1){

                        easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                        if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                            easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                        }

                        easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

                        easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                            @Override
                            public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                                if (multipleQuality) {
                                    if (vidURL != null) {
                                        CharSequence[] names = new CharSequence[vidURL.size()];

                                        for (int i = 0; i < vidURL.size(); i++) {
                                            names[i] = vidURL.get(i).getQuality();
                                        }

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                        builder.setTitle(context.getString(R.string.select_qualities));
                                        builder.setCancelable(true);
                                        builder.setItems(names, (dialogInterface, wich) -> {


                                            if (castSession != null && castSession.isConnected()) {


                                                onLoadChromcastSeries(media.getSeasons().get(0).getEpisodes().get(0), castSession, vidURL.get(wich).getUrl()
                                                        ,media);


                                            }else {

                                                mMediaModel =  MediaModel.media(media.getId(),
                                                        null,
                                                        currentquality, type, name, vidURL.get(wich).getUrl(), artwork,
                                                        null, currentep
                                                        , currentseasons, currentepimdb, tvseasonid,
                                                        currentepname,
                                                        currentseasonsNumber, null,
                                                        currenteptmdbnumber, media.getPremuim(),hls,
                                                        null,media.getImdbExternalId(),serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                                                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                                                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,mMediaModel);
                                                intent.putExtra(ARG_MOVIE, media);
                                                context.startActivity(intent);

                                            }

                                        });

                                        builder.show();


                                    } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {

                                    if (castSession != null && castSession.isConnected()) {


                                        onLoadChromcastSeries(media.getSeasons().get(0).getEpisodes().get(0), castSession, vidURL.get(0).getUrl()
                                                ,media);


                                    }else {


                                        mMediaModel =  MediaModel.media(media.getId(),
                                                null,
                                                currentquality, type, name, vidURL.get(0).getUrl(), artwork,
                                                null, currentep
                                                , currentseasons, currentepimdb, tvseasonid,
                                                currentepname,
                                                currentseasonsNumber, null,
                                                currenteptmdbnumber, media.getPremuim(),hls,
                                                null,media.getImdbExternalId(),serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,mMediaModel);
                                        intent.putExtra(ARG_MOVIE, media);
                                        context.startActivity(intent);
                                    }

                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(videourl);


                    }else {



                        if (castSession != null && castSession.isConnected()) {


                            onLoadChromcastSeries(media.getSeasons().get(0).getEpisodes().get(0), castSession, videourl
                                    ,media);


                        }else {

                            mMediaModel =  MediaModel.media(media.getId(),
                                    null,
                                    currentquality, type, name, videourl, artwork,
                                    null, currentep
                                    , currentseasons, currentepimdb, tvseasonid,
                                    currentepname,
                                    currentseasonsNumber, null,
                                    currenteptmdbnumber, media.getPremuim(),hls,
                                    null,media.getImdbExternalId(),serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,mMediaModel);
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);
                        }


                    }


             }

        }

        private void onLoadChromcastSeries(Episode episode, CastSession castSession, String url, Media media) {

            String currentepname = episode.getName();
            String artwork = episode.getStillPath();
            String name = media.getName() + " : " +"S0" + media.getSeasons().get(0).getSeasonNumber() + "E" + episode.getEpisodeNumber() + " : " + episode.getName();


            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, name);
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, currentepname);

            movieMetadata.addImage(new WebImage(Uri.parse(artwork)));
            List<MediaTrack> tracks = new ArrayList<>();


            MediaInfo mediaInfo = new MediaInfo.Builder(url)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(movieMetadata)
                    .setMediaTracks(tracks)
                    .build();

            final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            if (remoteMediaClient == null) {
                Timber.tag("TAG").w("showQueuePopup(): null RemoteMediaClient");
                return;
            }
            final QueueDataProvider provider = QueueDataProvider.getInstance(context);
            PopupMenu popup = new PopupMenu(context, binding.PlayButtonIcon);
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
                                REPEAT_MODE_REPEAT_OFF, null);
                    } else {
                        return false;
                    }
                } else {
                    if (provider1.getCount() == 0) {
                        remoteMediaClient.queueLoad(newItemArray, 0,
                                REPEAT_MODE_REPEAT_OFF, null);
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

        private void onLoadFeaturedAnimes(Featured featured) {

            onLoadRating(featured.getVoteAverage());
            try {
                onLoadDate(featured.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            binding.customAdFeatured.setVisibility(GONE);
            binding.movietitle.setText(featured.getTitle());

            if (featured.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            } else {

                binding.moviePremuim.setVisibility(GONE);
            }

            binding.rootLayout.setOnClickListener(view -> animeRepository.getAnimeDetails(String.valueOf(featured.getFeaturedId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Media media) {

                            Intent intent = new Intent(context, AnimeDetailsActivity.class);
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);

                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            //
                        }

                        @Override
                        public void onComplete() {
                            //
                        }

                    }));


            binding.addToFavorite.setOnClickListener(v -> {

                if (settingsManager.getSettings().getFavoriteonline() == 1 && tokenManager.getToken().getAccessToken() != null) {

                    if (isMovieFav) {

                        authRepository.getDeleteAnimeOnline(String.valueOf(featured.getFeaturedId()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .cache()
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull StatusFav statusFav) {

                                        Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onError(@NotNull Throwable e) {


                                        //

                                    }

                                    @Override
                                    public void onComplete() {

                                        //

                                    }
                                });

                        binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                    } else {

                        Timber.i("Added To Watchlist");

                        authRepository.getAddAnimeOnline(String.valueOf(featured.getFeaturedId()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .cache()
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull StatusFav statusFav) {

                                        Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();


                                    }

                                    @Override
                                    public void onError(@NotNull Throwable e) {

                                        //


                                    }

                                    @Override
                                    public void onComplete() {

                                        //

                                    }
                                });

                        binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                    }

                }  else {

                    animes  = new Animes(String.valueOf(featured.getFeaturedId()),String.valueOf(featured.getFeaturedId()),featured.getPosterPath(),featured.getTitle());

                    if (mediaRepository.isAnimeFavorite(Integer.parseInt(String.valueOf(featured.getFeaturedId())))) {

                        Timber.i("Removed From Watchlist");
                        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeFavoriteSeries(series))
                                .subscribeOn(Schedulers.io())
                                .subscribe());

                        binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                        Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

                    }else {

                        Timber.i("Added To Watchlist");
                        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addFavoriteAnime(animes))
                                .subscribeOn(Schedulers.io())
                                .subscribe());

                        binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                        Toast.makeText(context, "Added To Watchlist", Toast.LENGTH_SHORT).show();
                    }

                }

            });

            binding.infoTrailer.setOnClickListener(v -> animeRepository.getAnimeDetails(String.valueOf(featured.getFeaturedId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Media media) {

                            if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                                DialogHelper.showWifiWarning(context);

                            } else {

                                Intent intent = new Intent(context, TrailerPreviewActivity.class);
                                intent.putExtra(ARG_MOVIE, media);
                                context.startActivity(intent);
                                Animatoo.animateFade(context);
                            }
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            //
                        }

                        @Override
                        public void onComplete() {

                            //
                        }

                    }));




            binding.PlayButtonIcon.setOnClickListener(v -> animeRepository.getAnimeDetails(String.valueOf(featured.getFeaturedId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@NotNull Media movieDetail) {

                            if (movieDetail.getSeasons().get(0).getEpisodes().get(0).getVideos() != null && movieDetail.getSeasons().get(0).getEpisodes().get(0).getVideos().isEmpty()) {

                                DialogHelper.showNoStreamAvailable(context);

                            } else {

                                if (movieDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                    onLoadStreamAnimes(movieDetail);

                                } else if (settingsManager.getSettings().getEnableWebview() == 1) {

                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.episode_webview);
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());

                                    lp.gravity = Gravity.BOTTOM;
                                    lp.width = MATCH_PARENT;
                                    lp.height = MATCH_PARENT;


                                    mCountDownTimer = new CountDownTimer(DEFAULT_WEBVIEW_ADS_RUNNING, 1000) {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            if (!webViewLauched) {

                                                WebView webView = dialog.findViewById(R.id.webViewVideoBeforeAds);
                                                webView.getSettings().setJavaScriptEnabled(true);
                                                webView.setWebViewClient(new WebViewClient());
                                                WebSettings webSettings = webView.getSettings();
                                                webSettings.setSupportMultipleWindows(false);
                                                webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                                                if (settingsManager.getSettings().getWebviewLink() != null && !settingsManager.getSettings().getWebviewLink().isEmpty()) {

                                                    webView.loadUrl(settingsManager.getSettings().getWebviewLink());
                                                } else {

                                                    webView.loadUrl(SERVER_BASE_URL + "webview");
                                                }

                                                webViewLauched = true;
                                            }

                                        }

                                        @Override
                                        public void onFinish() {

                                            dialog.dismiss();
                                            onLoadStreamAnimes(movieDetail);
                                            webViewLauched = false;

                                            if (mCountDownTimer != null) {

                                                mCountDownTimer.cancel();
                                                mCountDownTimer = null;

                                            }
                                        }

                                    }.start();


                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);


                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {

                                    onLoadSubscribeDialog(movieDetail, "anime");

                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getPremuim() == 0) {

                                    onLoadStreamAnimes(movieDetail);

                                } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getPremuim() == 0) {


                                    onLoadStreamAnimes(movieDetail);

                                } else {

                                    DialogHelper.showPremuimWarning(context);

                                }
                            }

                        }


                        @Override
                        public void onError(@NotNull Throwable e) {

                            //
                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    }));



            binding.infoTrailer.setOnClickListener(v -> animeRepository.getAnimeDetails(String.valueOf(featured.getFeaturedId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Media media) {

                            if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                                DialogHelper.showWifiWarning(context);

                            } else {


                                Intent intent = new Intent(context, TrailerPreviewActivity.class);
                                intent.putExtra(ARG_MOVIE, media);
                                context.startActivity(intent);
                                Animatoo.animateFade(context);
                            }
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            //

                        }

                        @Override
                        public void onComplete() {
                            //
                        }

                    }));




            if (mediaRepository.isAnimeFavorite(Integer.parseInt(String.valueOf(featured.getFeaturedId())))) {

                binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

            } else {


                binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

            }


        }

        private void onLoadRating(float voteAverage) {

            binding.viewMovieRating.setText(String.valueOf(voteAverage));

        }

        private void onLoadStreamAnimes(Media media) {

            String tvseasonid = String.valueOf(media.getSeasons().get(0).getId());
            Integer currentep = Integer.parseInt(media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber());
            String currentepname = media.getSeasons().get(0).getEpisodes().get(0).getName();
            String currenteptmdbnumber = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getTmdbId());
            String currentseasons = media.getSeasons().get(0).getSeasonNumber();
            String currentseasonsNumber = media.getSeasons().get(0).getSeasonNumber();
            String currentepimdb = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getTmdbId());
            String artwork = media.getSeasons().get(0).getEpisodes().get(0).getStillPath();
            String type = "anime";
            String currentquality =  media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getServer();
            String name = "S0" + currentseasons + "E" + media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber() + " : " + media.getSeasons().get(0).getEpisodes().get(0).getName();
            String videourl =  media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink();
            String serieCover = media.getBackdropPath();
            Integer episodeHasRecap = media.getSeasons().get(0).getEpisodes().get(0).getHasrecap();
            Integer episodeRecapStartIn = media.getSeasons().get(0).getEpisodes().get(0).getSkiprecapStartIn();
            int hls = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getHls();
            float voteAverage = Float.parseFloat(media.getSeasons().get(0).getEpisodes().get(0).getVoteAverage());


            for (Genre genre : media.getGenres()) {
                mediaGenre = genre.getName();
            }

            if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getEmbed() == 1) {

                Intent intent = new Intent(context, EmbedActivity.class);
                intent.putExtra(Constants.MOVIE_LINK, videourl);
                context.startActivity(intent);

            }else if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getSupportedHosts() == 1){



                easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

                if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                    easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
                }

                easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);


                easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                        if (multipleQuality) {
                            if (vidURL != null) {

                                CharSequence[] names = new CharSequence[vidURL.size()];

                                for (int i = 0; i < vidURL.size(); i++) {
                                    names[i] = vidURL.get(i).getQuality();
                                }


                                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                builder.setTitle(context.getString(R.string.select_qualities));
                                builder.setCancelable(true);
                                builder.setItems(names, (dialogInterface, wich) -> {


                                    mMediaModel =  MediaModel.media(media.getId(),
                                            null,
                                            currentquality, type, name, vidURL.get(wich).getUrl(), artwork,
                                            null, currentep
                                            , currentseasons, currentepimdb, tvseasonid,
                                            currentepname,
                                            currentseasonsNumber, null,
                                            currenteptmdbnumber, media.getPremuim(),hls,
                                            null,null,serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                                    Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                                    intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, mMediaModel);
                                    intent.putExtra(ARG_MOVIE, media);
                                    context.startActivity(intent);


                                });

                                builder.show();


                            } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                        } else {

                            mMediaModel =  MediaModel.media(media.getId(),
                                    null,
                                    currentquality, type, name, vidURL.get(0).getUrl(), artwork,
                                    null, currentep
                                    , currentseasons, currentepimdb, tvseasonid,
                                    currentepname,
                                    currentseasonsNumber, null,
                                    currenteptmdbnumber, media.getPremuim(),hls,
                                    null,null,serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, mMediaModel);
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);

                        }

                    }

                    @Override
                    public void onError() {

                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

                easyPlexSupportedHosts.find(videourl);


            }else {

                mMediaModel =  MediaModel.media(media.getId(),
                        null,
                        currentquality, type, name, videourl, artwork,
                        null, currentep
                        , currentseasons, currentepimdb, tvseasonid,
                        currentepname,
                        currentseasonsNumber, null,
                        currenteptmdbnumber, media.getPremuim(),hls,
                        null,null,serieCover,episodeHasRecap,episodeRecapStartIn,mediaGenre,media.getName(),voteAverage);

                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, mMediaModel);
                intent.putExtra(ARG_MOVIE, media);
                context.startActivity(intent);
            }

        }

        private void onCheckFavoriteSeries(Featured featured) {

            if (settingsManager.getSettings().getFavoriteonline() == 1) {

                authRepository.getisSerieFavoriteOnline(String.valueOf(featured.getFeaturedId()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .cache()
                            .subscribe(new Observer<>() {
                                @Override
                                public void onSubscribe(@NotNull Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(@NotNull StatusFav statusFav) {

                                    if (statusFav.getStatus() == 1) {

                                        isMovieFav = true;

                                        binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                                    } else {

                                        isMovieFav = false;

                                        binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                                    }

                                }

                                @Override
                                public void onError(@NotNull Throwable e) {

                                    //
                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });


            }else {

             if (mediaRepository.isSerieFavorite(Integer.parseInt(String.valueOf(featured.getFeaturedId())))) {


             binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

            } else {


             binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

            }

            }
        }


        @SuppressLint("UseCompatLoadingForDrawables")
        private void onLoadFeaturedMovies(Featured featured) {

            onLoadRating(featured.getVoteAverage());

            binding.customAdFeatured.setVisibility(GONE);

            onCheckFavoriteMovies(featured);

            try {
                onLoadDate(featured.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            binding.addToFavorite.setOnClickListener(v -> mediaRepository.getMovie(String.valueOf(featured.getFeaturedId()),settingsManager.getSettings().getApiKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Media media) {

                            if (settingsManager.getSettings().getFavoriteonline() == 1 && tokenManager.getToken().getAccessToken() != null) {

                                if (isMovieFav) {

                                    authRepository.getDeleteMovieOnline(String.valueOf(featured.getFeaturedId()))
                                            .subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @Override
                                                public void onNext(@NotNull StatusFav statusFav) {

                                                    Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();


                                                }

                                                @Override
                                                public void onError(@NotNull Throwable e) {
                                                    //

                                                }

                                                @Override
                                                public void onComplete() {

                                                    //

                                                }
                                            });

                                    binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                                } else {

                                    Timber.i("Added To Watchlist");

                                    authRepository.getAddMovieOnline(String.valueOf(featured.getFeaturedId()))
                                            .subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<>() {
                                                @Override
                                                public void onSubscribe(@NotNull Disposable d) {

                                                    //

                                                }

                                                @Override
                                                public void onNext(@NotNull StatusFav statusFav) {

                                                    Toast.makeText(context, "Added " + featured.getTitle() + " To Watchlist", Toast.LENGTH_SHORT).show();

                                                }

                                                @Override
                                                public void onError(@NotNull Throwable e) {

                                                    //

                                                }

                                                @Override
                                                public void onComplete() {

                                                    //

                                                }
                                            });


                                    binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);


                                }

                            } else {

                                if (mediaRepository.isMovieFavorite(Integer.parseInt(String.valueOf(featured.getFeaturedId())))) {


                                    Timber.i("Removed From Watchlist");
                                    compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeFavorite(media))
                                            .subscribeOn(Schedulers.io())
                                            .subscribe());

                                    binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                                    Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

                                } else {

                                    Timber.i("Added To Watchlist");
                                    compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addFavoriteMovie(media))
                                            .subscribeOn(Schedulers.io())
                                            .subscribe());

                                    binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                                    Toast.makeText(context, "Added To Watchlist", Toast.LENGTH_SHORT).show();
                                }
                            }


                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            DialogHelper.showNoStreamAvailable(context);
                        }

                        @Override
                        public void onComplete() {

                            //
                        }

                    }));


            if (mediaRepository.hasHistory(Integer.parseInt(String.valueOf(featured.getFeaturedId())))) {

                binding.PlayButtonIcon.setBackground(context.getResources().getDrawable(R.drawable.btn_gradient_watch_video));
                binding.PlayButtonIcon.setText(R.string.resume);

            } else {

                binding.PlayButtonIcon.setBackground(context.getResources().getDrawable(R.drawable.btn_gradient));
                binding.PlayButtonIcon.setText(R.string.lecture);

            }


            if (featured.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            } else {

                binding.moviePremuim.setVisibility(GONE);
            }

            binding.infoTrailer.setOnClickListener(v -> mediaRepository.getMovie(String.valueOf(featured.getFeaturedId()), settingsManager.getSettings().getApiKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Media media) {


                            if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                                DialogHelper.showWifiWarning(context);

                            } else {

                                Intent intent = new Intent(context, TrailerPreviewActivity.class);
                                intent.putExtra(ARG_MOVIE, media);
                                context.startActivity(intent);
                                Animatoo.animateFade(context);
                            }


                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            DialogHelper.showNoStreamAvailable(context);
                        }

                        @Override
                        public void onComplete() {
                            //
                        }

                    }));


            binding.movietitle.setText(featured.getTitle());

            binding.mgenres.setText(featured.getGenre());


            binding.rootLayout.setOnClickListener(view -> mediaRepository.getMovie(String.valueOf(featured.getFeaturedId()),settingsManager.getSettings().getApiKey())
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .cache()
                   .subscribe(new Observer<>() {
                       @Override
                       public void onSubscribe(@NotNull Disposable d) {

                           //

                       }

                       @Override
                       public void onNext(@io.reactivex.rxjava3.annotations.NonNull Media media) {

                           Intent intent = new Intent(context, MovieDetailsActivity.class);
                           intent.putExtra(ARG_MOVIE, media);
                           context.startActivity(intent);

                       }

                       @Override
                       public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                           DialogHelper.showNoStreamAvailable(context);
                       }

                       @Override
                       public void onComplete() {

                           //
                       }

                   }));

            binding.PlayButtonIcon.setOnClickListener(view -> onLoadFeaturedStream(featured));
        }

        private void onLoadDate(String date) throws ParseException {

            if (date != null && !date.trim().isEmpty()) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
                Date releaseDate = sdf1.parse(date);
                assert releaseDate != null;
                binding.textMovieRelease.setText(sdf2.format(releaseDate));
            } else {
                binding.textMovieRelease.setText("");}


        }

        private void onCheckFavoriteMovies(Featured featured) {

            if (settingsManager.getSettings().getFavoriteonline() == 1 && tokenManager.getToken().getAccessToken() != null) {

                authRepository.getisMovieFavoriteOnline(String.valueOf(featured.getFeaturedId()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<>() {
                            @Override
                            public void onSubscribe(@NotNull Disposable d) {

                                //

                            }

                            @Override
                            public void onNext(@NotNull StatusFav statusFav) {

                                if (statusFav.getStatus() == 1) {

                                    isMovieFav = true;

                                    binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                                } else {

                                    isMovieFav = false;

                                    binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                                }

                            }

                            @Override
                            public void onError(@NotNull Throwable e) {

                                //

                            }

                            @Override
                            public void onComplete() {

                                //

                            }
                        });


            }else {

                if (mediaRepository.isMovieFavorite(Integer.parseInt(String.valueOf(featured.getFeaturedId())))) {


                    binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                } else {


                    binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                }

            }

        }


        private void onLoadFeaturedStream(Featured featured) {

               mediaRepository.getMovie(String.valueOf(featured.getFeaturedId()),settingsManager.getSettings().getApiKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<>() {
                        @Override
                        public void onSubscribe(@NotNull Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Media media) {

                            if (media.getVideos() != null && !media.getVideos().isEmpty()) {


                                if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {


                                    onLoadStream(media);


                                } else if (settingsManager.getSettings().getEnableWebview() == 1) {


                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.episode_webview);
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());

                                    lp.gravity = Gravity.BOTTOM;
                                    lp.width = MATCH_PARENT;
                                    lp.height = MATCH_PARENT;


                                    mCountDownTimer = new CountDownTimer(DEFAULT_WEBVIEW_ADS_RUNNING, 1000) {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            if (!webViewLauched) {

                                                WebView webView = dialog.findViewById(R.id.webViewVideoBeforeAds);
                                                webView.getSettings().setJavaScriptEnabled(true);
                                                webView.setWebViewClient(new WebViewClient());
                                                WebSettings webSettings = webView.getSettings();
                                                webSettings.setSupportMultipleWindows(false);
                                                webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                                                if (settingsManager.getSettings().getWebviewLink() != null && !settingsManager.getSettings().getWebviewLink().isEmpty()) {

                                                    webView.loadUrl(settingsManager.getSettings().getWebviewLink());
                                                } else {

                                                    webView.loadUrl(SERVER_BASE_URL + "webview");
                                                }

                                                webViewLauched = true;
                                            }

                                        }

                                        @Override
                                        public void onFinish() {

                                            dialog.dismiss();
                                            onLoadStream(media);
                                            webViewLauched = false;

                                            if (mCountDownTimer != null) {

                                                mCountDownTimer.cancel();
                                                mCountDownTimer = null;

                                            }
                                        }

                                    }.start();


                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);


                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && media.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                    onLoadSubscribeDialog(media, "movie");

                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && media.getPremuim() == 0) {


                                    onLoadStream(media);


                                } else if (authManager.getUserInfo().getPremuim() == 1 && media.getPremuim() == 0) {


                                    onLoadStream(media);


                                } else {

                                    DialogHelper.showPremuimWarning(context);

                                }

                            } else {


                                DialogHelper.showNoStreamAvailable(context);
                            }

                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            DialogHelper.showNoStreamAvailable(context);
                        }

                        @Override
                        public void onComplete() {
                            //
                        }

                    });


        }



        private void onLoadSubscribeDialog(Media media, String type) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_subscribe);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {

             Toast.makeText(context, R.string.loading_rewards, Toast.LENGTH_SHORT).show();

            String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();


                if (context.getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

                    maxRewardedAd = MaxRewardedAd.getInstance( settingsManager.getSettings().getApplovinRewardUnitid(), (BaseActivity) context );
                    maxRewardedAd.loadAd();

                    onLoadApplovinAds(media,type);

                }else if ("Vungle".equals(defaultRewardedNetworkAds)) {

                onLoadVungleAds(media,type);

                }else if ("Ironsource".equals(defaultRewardedNetworkAds)) {

                    onLoadIronsourceAds(media,type);

                }else if ("AppNext".equals(defaultRewardedNetworkAds)) {

                    onLoadAppNextAds(media,type);

                } else if ("StartApp".equals(defaultRewardedNetworkAds)) {

                    onLoadStartAppAds(media,type);

                } else if ("UnityAds".equals(defaultRewardedNetworkAds)) {

                    onLoadUnityAds(media,type);


                } else if ("Admob".equals(defaultRewardedNetworkAds)) {

                    onLoadAdmobRewardAds(media,type);

                } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                    onLoadFaceBookRewardAds(media,type);

                } else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media,type);

                } else if ("Auto".equals(defaultRewardedNetworkAds)) {

                    onLoadAutoRewardAds(media,type);
                }

                dialog.dismiss();

            });

            dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

                context.startActivity(new Intent(context, SettingsActivity.class));

                dialog.dismiss();


            });


            dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

        private void onLoadApplovinAds(Media media, String type) {

            if (maxRewardedAd.isReady()) {

                maxRewardedAd.showAd();
            }

            maxRewardedAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {

                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

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

                @Override
                public void onRewardedVideoStarted(MaxAd ad) {

                }

                @Override
                public void onRewardedVideoCompleted(MaxAd ad) {

                    switch (type) {
                        case "movie":
                            onLoadStream(media);
                            break;
                        case "anime":
                            onLoadStreamAnimes(media);
                            break;
                        case "serie":
                            onLoadStreamSerie(media);
                            break;
                        case "streaming":
                            onLoadStreamStreaming(media);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + type);
                    }

                }

                @Override
                public void onUserRewarded(MaxAd ad, MaxReward reward) {

                }
            });
        }

        private void onLoadAppNextAds(Media media, String type) {

            RewardedVideo mAppNextAdsVideoRewarded = new RewardedVideo(context, settingsManager.getSettings().getAppnextPlacementid());
            mAppNextAdsVideoRewarded.loadAd();
            mAppNextAdsVideoRewarded.showAd();

            // Get callback for ad loaded
            mAppNextAdsVideoRewarded.setOnAdLoadedCallback((s, appnextAdCreativeType) -> {
                //
            });

            mAppNextAdsVideoRewarded.setOnAdOpenedCallback(() -> {
                //
            });
            mAppNextAdsVideoRewarded.setOnAdClickedCallback(() -> {

            });

            mAppNextAdsVideoRewarded.setOnAdClosedCallback(() -> {
                switch (type) {
                    case "movie":
                        onLoadStream(media);
                        break;
                    case "anime":
                        onLoadStreamAnimes(media);
                        break;
                    case "serie":
                        onLoadStreamSerie(media);
                        break;
                    case "streaming":
                        onLoadStreamStreaming(media);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }
            });
            mAppNextAdsVideoRewarded.setOnAdErrorCallback(error -> Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show());
            // Get callback when the user saw the video until the end (video ended)
            mAppNextAdsVideoRewarded.setOnVideoEndedCallback(() -> {

            });
        }

        private void onLoadIronsourceAds(Media media, String type) {

            IronSource.showRewardedVideo(settingsManager.getSettings().getIronsourceRewardPlacementName());

            IronSource.setRewardedVideoListener(new RewardedVideoListener() {
                /**
                 * Invoked when the RewardedVideo ad view has opened.
                 * Your Activity will lose focus. Please avoid performing heavy
                 * tasks till the video ad will be closed.
                 */
                @Override
                public void onRewardedVideoAdOpened() {

                    //
                }
                /*Invoked when the RewardedVideo ad view is about to be closed.
                Your activity will now regain its focus.*/
                @Override
                public void onRewardedVideoAdClosed() {

                    //
                }

                @Override
                public void onRewardedVideoAvailabilityChanged(boolean available) {
                    //Change the in-app 'Traffic Driver' state according to availability.
                }

                @Override
                public void onRewardedVideoAdRewarded(Placement placement) {

                    switch (type) {
                        case "movie":
                            onLoadStream(media);
                            break;
                        case "anime":
                            onLoadStreamAnimes(media);
                            break;
                        case "serie":
                            onLoadStreamSerie(media);
                            break;
                        case "streaming":
                            onLoadStreamStreaming(media);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + type);
                    }
                }

                @Override
                public void onRewardedVideoAdShowFailed(IronSourceError error) {

                    //

                }
                /*Invoked when the end user clicked on the RewardedVideo ad
                 */
                @Override
                public void onRewardedVideoAdClicked(Placement placement) {
                    //

                }

                @Override
                public void onRewardedVideoAdStarted(){
                    //
                }
                /* Invoked when the video ad finishes plating. */
                @Override
                public void onRewardedVideoAdEnded(){

                    //
                }
            });
        }

        private void onLoadVungleAds(Media media, String type) {

            Vungle.loadAd(settingsManager.getSettings().getVungleRewardPlacementName(), new LoadAdCallback() {
                @Override
                public void onAdLoad(String id) {
                    //
                }

                @Override
                public void onError(String id, VungleException e) {

                    //
                }
            });



            Vungle.playAd(settingsManager.getSettings().getVungleRewardPlacementName(), new AdConfig(), new PlayAdCallback() {
                @Override
                public void onAdStart(String placementReferenceID) {
                    //
                }

                @Override
                public void onAdViewed(String placementReferenceID) {
                    //
                }

                // Deprecated
                @Override
                public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                    switch (type) {
                        case "movie":
                            onLoadStream(media);
                            break;
                        case "anime":
                            onLoadStreamAnimes(media);
                            break;
                        case "serie":
                            onLoadStreamSerie(media);
                            break;
                        case "streaming":
                            onLoadStreamStreaming(media);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + type);
                    }
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

        private void onLoadAutoRewardAds(Media media, String type) {

            Random random = new Random();
            int numberOfMethods = 7;

            switch(random.nextInt(numberOfMethods)) {
                case 0:
                    onLoadStartAppAds(media, type);
                    break;
                case 1:
                    onLoadUnityAds(media, type);
                    break;
                case 2:
                    onLoadAppOdealRewardAds(media, type);
                    break;
                case 3:
                    onLoadFaceBookRewardAds(media, type);
                    break;
                case 4:
                    onLoadVungleAds(media, type);
                    break;
                case 5:
                    onLoadAppNextAds(media, type);
                    break;
                case 6:
                    onLoadIronsourceAds(media, type);
                    break;
                default:
                    onLoadAdmobRewardAds(media, type);
            }
        }


        @SuppressLint("StaticFieldLeak")
        private void onLoadStream(Media media) {

                if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                    String[] charSequence = new String[media.getVideos().size()];
                    for (int i = 0; i < media.getVideos().size(); i++) {
                        charSequence[i] = String.valueOf(media.getVideos().get(i).getServer());

                    }


                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                    builder.setTitle(context.getString(R.string.select_qualities));
                    builder.setCancelable(true);
                    builder.setItems(charSequence, (dialogInterface, wich) -> {


                        if (media.getVideos().get(wich).getHeader() !=null && !media.getVideos().get(wich).getHeader().isEmpty()) {

                            PLAYER_HEADER = media.getVideos().get(wich).getHeader();
                        }


                        if (media.getVideos().get(wich).getUseragent() !=null && !media.getVideos().get(wich).getUseragent().isEmpty()) {

                            PLAYER_USER_AGENT = media.getVideos().get(wich).getUseragent();
                        }

                       if (media.getVideos().get(wich).getEmbed() == 1) {

                            Intent intent = new Intent(context, EmbedActivity.class);
                            intent.putExtra(Constants.MOVIE_LINK, media.getVideos().get(wich).getLink());
                            context.startActivity(intent);
                            dialogInterface.dismiss();

                        } else if (media.getVideos().get(wich).getSupportedHosts() == 1){


                            startSupportedHostsStream(media,wich,media.getVideos().get(wich));

                        } else   {

                           CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                   .getCurrentCastSession();
                           if (castSession != null && castSession.isConnected()) {

                               onLoadChromcast(media, castSession, media.getVideos().get(wich).getLink());


                           }  else { if (settingsManager.getSettings().getVlc() == 1) {


                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_bottom_stream);
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());

                                    lp.gravity = Gravity.BOTTOM;
                                    lp.width = MATCH_PARENT;
                                    lp.height = MATCH_PARENT;




                               LinearLayout vlc = dialog.findViewById(R.id.vlc);
                               LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                               LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                               LinearLayout webcast = dialog.findViewById(R.id.webCast);


                               vlc.setOnClickListener(v12 -> {
                                   Tools.streamMediaFromVlc(context,media.getVideos().get(wich).getLink(),media,settingsManager, media.getVideos().get(wich));
                                   dialog.hide();
                               });

                               mxPlayer.setOnClickListener(v12 -> {
                                   Tools.streamMediaFromMxPlayer(context,media.getVideos().get(wich).getLink(),media,settingsManager);
                                   dialog.hide();

                               });

                               webcast.setOnClickListener(v12 -> {
                                   Tools.streamMediaFromMxWebcast(context,media.getVideos().get(wich).getLink(),media,settingsManager);
                                   dialog.hide();

                               });


                              easyplexPlayer.setOnClickListener(v12 -> {

                                        startStreamFromDialog(media,wich, media.getVideos().get(0).getLink());

                                        dialog.hide();


                                    });

                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);

                                    dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                            dialog.dismiss());
                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);


                                } else {

                                    startStreamFromDialog(media,wich, media.getVideos().get(wich).getLink());


                                }


                            }


                        }


                      dialogInterface.dismiss();



                    });

                    builder.show();

                } else {



                    if (media.getVideos().get(0).getHeader() !=null && !media.getVideos().get(0).getHeader().isEmpty()) {

                        PLAYER_HEADER = media.getVideos().get(0).getHeader();
                    }


                    if (media.getVideos().get(0).getUseragent() !=null && !media.getVideos().get(0).getUseragent().isEmpty()) {

                        PLAYER_USER_AGENT = media.getVideos().get(0).getUseragent();
                    }

                    if (media.getVideos().get(0).getEmbed() == 1) {


                        Intent intent = new Intent(context, EmbedActivity.class);
                        intent.putExtra(Constants.MOVIE_LINK, media.getVideos().get(0).getLink());
                        context.startActivity(intent);


                    } else if (media.getVideos().get(0).getSupportedHosts() == 1) {


                        startSupportedHostsStream(media,0, media.getVideos().get(0));


                    }else {

                        CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                .getCurrentCastSession();
                        if (castSession != null && castSession.isConnected())
                            onLoadChromcast(media, castSession, media.getVideos().get(0).getLink());
                        else startStreamFromDialog(media, 0, media.getVideos().get(0).getLink());
                    }
           }



        }

        private void startSupportedHostsStream(Media media, int wich, MediaStream mediaStream) {

            easyPlexSupportedHosts = new EasyPlexSupportedHosts(context);

            if (settingsManager.getSettings().getHxfileApiKey() !=null && !settingsManager.getSettings().getHxfileApiKey().isEmpty())  {

                easyPlexSupportedHosts.setApikey(settingsManager.getSettings().getHxfileApiKey());
            }

            easyPlexSupportedHosts.setMainApiServer(SERVER_BASE_URL);

            easyPlexSupportedHosts.onFinish(new EasyPlexSupportedHosts.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<EasyPlexSupportedHostsModel> vidURL, boolean multipleQuality) {

                    if (multipleQuality){
                        if (vidURL!=null) {
                            //This video you can choose qualities

                            String[] charSequence = new String[vidURL.size()];
                            for (int i = 0; i <vidURL.size(); i++) {
                                charSequence[i] = String.valueOf(vidURL.get(i).getQuality());

                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                            builder.setTitle(context.getString(R.string.select_qualities));
                            builder.setCancelable(true);
                            builder.setItems(charSequence, (dialogInterface, i) -> {


                                CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                        .getCurrentCastSession();
                                if (castSession != null && castSession.isConnected()) {

                                    onLoadChromcast(media, castSession, vidURL.get(i).getUrl());


                                } else  if (settingsManager.getSettings().getVlc() == 1) {


                                    final Dialog dialog = new Dialog(context);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_bottom_stream);
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());

                                    lp.gravity = Gravity.BOTTOM;
                                    lp.width = MATCH_PARENT;
                                    lp.height = MATCH_PARENT;


                                    LinearLayout vlc = dialog.findViewById(R.id.vlc);
                                    LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                                    LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                                    LinearLayout webcast = dialog.findViewById(R.id.webCast);


                                    vlc.setOnClickListener(v12 -> {
                                        Tools.streamMediaFromVlc(context,vidURL.get(i).getUrl(),media,settingsManager, mediaStream);
                                        dialog.hide();
                                    });

                                    mxPlayer.setOnClickListener(v12 -> {
                                        Tools.streamMediaFromMxPlayer(context,vidURL.get(i).getUrl(),media,settingsManager);
                                        dialog.hide();

                                    });

                                    webcast.setOnClickListener(v12 -> {
                                        Tools.streamMediaFromMxWebcast(context,vidURL.get(i).getUrl(),media,settingsManager);
                                        dialog.hide();

                                    });


                                    easyplexPlayer.setOnClickListener(v12 -> {

                                        onLoadFeaturedStreamFromDailog(media,vidURL.get(i).getUrl(),vidURL.get(i).getQuality(),wich);
                                        dialog.hide();


                                    });

                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);

                                    dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                            dialog.dismiss());


                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);


                                }else {

                                    onLoadFeaturedStreamFromDailog(media,vidURL.get(i).getUrl(),vidURL.get(i).getQuality(),wich);
                                }



                            });

                            builder.show();



                        }else  Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                    }else {



                        if (settingsManager.getSettings().getVlc() == 1) {


                            final Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_bottom_stream);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());

                            lp.gravity = Gravity.BOTTOM;
                            lp.width = MATCH_PARENT;
                            lp.height = MATCH_PARENT;


                            LinearLayout vlc = dialog.findViewById(R.id.vlc);
                            LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
                            LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
                            LinearLayout webcast = dialog.findViewById(R.id.webCast);


                            vlc.setOnClickListener(v12 -> {
                                Tools.streamMediaFromVlc(context,vidURL.get(0).getUrl(),media,settingsManager, mediaStream);
                                dialog.hide();
                            });

                            mxPlayer.setOnClickListener(v12 -> {
                                Tools.streamMediaFromMxPlayer(context,vidURL.get(0).getUrl(),media,settingsManager);
                                dialog.hide();

                            });

                            webcast.setOnClickListener(v12 -> {
                                Tools.streamMediaFromMxWebcast(context,vidURL.get(0).getUrl(),media,settingsManager);
                                dialog.hide();

                            });

                            easyplexPlayer.setOnClickListener(v12 -> {
                                onLoadFeaturedStreamFromDailog(media,vidURL.get(0).getUrl(),vidURL.get(0).getQuality(),wich);

                                dialog.hide();


                            });

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);

                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                    dialog.dismiss());
                            dialog.show();
                            dialog.getWindow().setAttributes(lp);

                    }else {

                            onLoadFeaturedStreamFromDailog(media,vidURL.get(0).getUrl(),vidURL.get(0).getQuality(),wich);}
                    }

                }

                @Override
                public void onError() {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            });

            easyPlexSupportedHosts.find(media.getVideos().get(wich).getLink());
        }

        private void onLoadFeaturedStreamFromDailog(Media media, String url, String quality, int wich) {



            for (Genre genre : media.getGenres()) {
                mediaGenre = genre.getName();
            }

            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(), null
                    , quality, "0", media.getTitle(),
                    url, media.getBackdropPath(),
                    null, null
                    , null, null,
                    null, null,
                    null,
                    null,
                    null,
                    media.getPremuim(), media.getVideos().get(wich).getHls(),
                    media.getSubstype(), media.getImdbExternalId(), media.getPosterPath()
                    , media.getHasrecap(), media.getSkiprecapStartIn(), mediaGenre,null,media.getVoteAverage()));
            intent.putExtra(ARG_MOVIE, media);
            context.startActivity(intent);


            history = new History(media.getId(), media.getId(), media.getPosterPath(), media.getTitle(), media.getBackdropPath(), media.getLink());
            history.setType("0");
            history.setTmdbId(media.getId());
            history.setBackdropPath(media.getBackdropPath());
            history.setExternalId(media.getImdbExternalId());
            history.setPremuim(media.getPremuim());
            history.setHasrecap(media.getHasrecap());
            history.setSkiprecapStartIn(media.getSkiprecapStartIn());
            history.setMediaGenre(mediaGenre);
            history.setVoteAverage(media.getVoteAverage());

            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());





        }

        private void startStreamFromDialog(Media media, int wich, String downloadUrl) {


            for (Genre genre : media.getGenres()) {
                mediaGenre = genre.getName();
            }


            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(), null
                    , media.getVideos().get(wich).getServer(), "0", media.getTitle(),
                    downloadUrl, media.getBackdropPath(),
                    null, null
                    , null, null,
                    null, null,
                    null,
                    null,
                    null, media.getPremuim(), media.getVideos().get(wich).getHls(), media.getSubstype(), media.getImdbExternalId(), media.getPosterPath()

                    , media.getHasrecap(), media.getSkiprecapStartIn(), mediaGenre,null,media.getVoteAverage()));
            intent.putExtra(ARG_MOVIE, media);
            context.startActivity(intent);

            history = new History(media.getId(), media.getId(), media.getPosterPath(), media.getTitle(), media.getBackdropPath(), media.getLink());
            history.setType("0");
            history.setTmdbId(media.getId());
            history.setBackdropPath(media.getBackdropPath());
            history.setExternalId(media.getImdbExternalId());
            history.setPremuim(media.getPremuim());
            history.setHasrecap(media.getHasrecap());
            history.setSkiprecapStartIn(media.getSkiprecapStartIn());
            history.setMediaGenre(mediaGenre);
            history.setVoteAverage(media.getVoteAverage());

            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        }

        private void onLoadMainPlayerStreamYoutube(Media media, int wich, String downloadUrl) {

            for (Genre genre : media.getGenres()) {
                mediaGenre = genre.getName();
            }


            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(), null
                    , media.getVideos().get(wich).getServer(), "0", media.getTitle(),
                    downloadUrl, media.getBackdropPath(),
                    null, null
                    , null, null,
                    null, null,
                    null,
                    null,
                    null, media.getPremuim(), media.getVideos().get(wich).getHls()
                    , media.getSubstype(), media.getImdbExternalId(), media.getPosterPath(), media.getHasrecap(), media.getSkiprecapStartIn(), mediaGenre,null,media.getVoteAverage()));
            intent.putExtra(ARG_MOVIE, media);
            context.startActivity(intent);

            history = new History(media.getId(), media.getId(), media.getPosterPath(), media.getTitle(), media.getBackdropPath(), media.getLink());
            history.setType("0");
            history.setTmdbId(media.getId());
            history.setExternalId(media.getImdbExternalId());
            history.setPremuim(media.getPremuim());
            history.setBackdropPath(media.getBackdropPath());
            history.setHasrecap(media.getHasrecap());
            history.setSkiprecapStartIn(media.getSkiprecapStartIn());
            history.setMediaGenre(mediaGenre);
            history.setVoteAverage(media.getVoteAverage());
            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());


        }


        private void onLoadAppOdealRewardAds(Media media, String type) {

            Appodeal.show((BaseActivity) context, Appodeal.REWARDED_VIDEO);

            Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
                @Override
                public void onRewardedVideoLoaded(boolean isPrecache) {

                    //

                }

                @Override
                public void onRewardedVideoFailedToLoad() {

                    //


                }

                @Override
                public void onRewardedVideoShown() {


                    //


                }

                @Override
                public void onRewardedVideoShowFailed() {

                    //

                }

                @Override
                public void onRewardedVideoClicked() {
                    //

                }

                @Override
                public void onRewardedVideoFinished(double amount, String name) {

                    switch (type) {
                        case "movie":
                            onLoadStream(media);
                            break;
                        case "anime":
                            onLoadStreamAnimes(media);
                            break;
                        case "serie":
                            onLoadStreamSerie(media);
                            break;
                        case "streaming":
                            onLoadStreamStreaming(media);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + type);
                    }

                }

                @Override
                public void onRewardedVideoClosed(boolean finished) {

                    //

                }

                @Override
                public void onRewardedVideoExpired() {


                    //


                }

            });

        }


        private void onLoadFaceBookRewardAds(Media media, String type) {


            com.facebook.ads.InterstitialAd facebookInterstitialAd = new com.facebook.ads.InterstitialAd(context, settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());

            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

                @Override
                public void onError(com.facebook.ads.Ad ad, AdError adError) {

                    //

                }

                @Override
                public void onAdLoaded(com.facebook.ads.Ad ad) {

                    facebookInterstitialAd.show();

                }

                @Override
                public void onAdClicked(com.facebook.ads.Ad ad) {

                    //

                }

                @Override
                public void onLoggingImpression(com.facebook.ads.Ad ad) {


                    //vvvvvv
                }

                @Override
                public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {

                    //

                }

                @Override
                public void onInterstitialDismissed(com.facebook.ads.Ad ad) {

                    switch (type) {
                        case "movie":
                            onLoadStream(media);
                            break;
                        case "anime":
                            onLoadStreamAnimes(media);
                            break;
                        case "serie":
                            onLoadStreamSerie(media);
                            break;
                        case "streaming":
                            onLoadStreamStreaming(media);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + type);
                    }

                }


            };


            facebookInterstitialAd.loadAd(
                    facebookInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());


        }

        private void onLoadAdmobRewardAds(Media media, String type) {


            if (mRewardedAd == null) {
                Toast.makeText(context, "The rewarded ad wasn't ready yet", Toast.LENGTH_SHORT).show();
                return;
            }

            mRewardedAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdShowedFullScreenContent() {
                            //
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            mRewardedAd = null;
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mRewardedAd = null;
                            // Preload the next rewarded ad.
                            loadRewardedAd();
                        }
                    });
            mRewardedAd.show(
                    (BaseActivity) context,
                    new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            switch (type) {
                                case "movie":
                                    onLoadStream(media);
                                    break;
                                case "anime":
                                    onLoadStreamAnimes(media);
                                    break;
                                case "serie":
                                    onLoadStreamSerie(media);
                                    break;
                                case "streaming":
                                    onLoadStreamStreaming(media);
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + type);
                            }
                        }
                    });


        }

        private void loadRewardedAd() {

            if (mRewardedAd == null) {
                isLoading = true;
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(
                        context,
                        settingsManager.getSettings().getAdUnitIdRewarded(),
                        adRequest,
                        new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                mRewardedAd = null;

                                isLoading = false;

                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                                isLoading = false;
                                mRewardedAd = rewardedAd;
                            }
                        });
            }
        }

        private void onLoadUnityAds(Media media, String type) {

            if (UnityAdsImplementation.isReady()) {
                UnityAds.show((BaseActivity) context, "rewardedVideo", new IUnityAdsShowListener() {
                    @Override
                    public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
                        //
                    }

                    @Override
                    public void onUnityAdsShowStart(String placementId) {
                        //
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


            iUnityAdsListener = new IUnityAdsListener() {

                @Override
                public void onUnityAdsReady(String s) {


                    //

                }

                @Override
                public void onUnityAdsStart(String s) {

                    //

                }


                @Override
                public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

                    switch (type) {
                        case "movie":
                            onLoadStream(media);
                            break;
                        case "anime":
                            onLoadStreamAnimes(media);
                            break;
                        case "serie":
                            onLoadStreamSerie(media);
                            break;
                        case "streaming":
                            onLoadStreamStreaming(media);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + type);
                    }
                    UnityAds.removeListener(iUnityAdsListener);

                }

                @Override
                public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                   //

                }
            };

            UnityAds.addListener(iUnityAdsListener);

        }

        private void onLoadChromcast(Media media, CastSession castSession, String downloadUrl) {

            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, media.getTitle());
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, media.getTitle());

            movieMetadata.addImage(new WebImage(Uri.parse(media.getPosterPath())));
            List<MediaTrack> tracks = new ArrayList<>();


            MediaInfo mediaInfo = new MediaInfo.Builder(downloadUrl)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setMetadata(movieMetadata)
                    .setMediaTracks(tracks)
                    .build();

            final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            if (remoteMediaClient == null) {
                Timber.tag(TAG).w("showQueuePopup(): null RemoteMediaClient");
                return;
            }
            final QueueDataProvider provider = QueueDataProvider.getInstance(context);
            PopupMenu popup = new PopupMenu(context, binding.PlayButtonIcon);
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
                                REPEAT_MODE_REPEAT_OFF, null);
                    } else {
                        return false;
                    }
                } else {
                    if (provider1.getCount() == 0) {
                        remoteMediaClient.queueLoad(newItemArray, 0,
                                REPEAT_MODE_REPEAT_OFF, null);
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

        private void onLoadStartAppAds(Media media, String type) {

            startAppAd.setVideoListener(() -> {
                switch (type) {
                    case "movie":
                        onLoadStream(media);
                        break;
                    case "anime":
                        onLoadStreamAnimes(media);
                        break;
                    case "serie":
                        onLoadStreamSerie(media);
                        break;
                    case "streaming":
                        onLoadStreamStreaming(media);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }
            });

            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                @Override
                public void onReceiveAd(Ad ad) {
                    startAppAd.showAd();
                }

                @Override
                public void onFailedToReceiveAd(Ad ad) {
                    Toast.makeText(context, R.string.cant_show, Toast.LENGTH_SHORT).show();
                }
            });
        }



        private void startStreamCasting(Media movieDetail, String link) {

            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, movieDetail.getTitle());
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, livegenre);
            movieMetadata.addImage(new WebImage(Uri.parse(movieDetail.getPosterPath())));

            MediaInfo mediaInfo = new MediaInfo.Builder(link)
                    .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                    .setMetadata(movieMetadata)
                    .build();

            CastSession castSession =
                    CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
            if (castSession == null || !castSession.isConnected()) {
                Timber.tag(TAG).w("showQueuePopup(): not connected to a cast device");
                return;
            }
            final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            if (remoteMediaClient == null) {
                Timber.tag(TAG).w("showQueuePopup(): null RemoteMediaClient");
                return;
            }



            final QueueDataProvider provider = QueueDataProvider.getInstance(context);
            PopupMenu popup = new PopupMenu(context, binding.PlayButtonIcon);
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


        private void createAndLoadRewardedAd() {

            Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.REWARDED_VIDEO );

            IronSource.init((BaseActivity) context, settingsManager.getSettings().getIronsourceAppKey(), IronSource.AD_UNIT.REWARDED_VIDEO);

            // Initialize the AppNext Ads SDK.
            Appnext.init(context);

            if (settingsManager.getSettings().getDefaultRewardedNetworkAds() != null && "Admob".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

             loadRewardedAd();

            }

            if ("StartApp".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            startAppAd = new StartAppAd(context);

            } else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

                Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.REWARDED_VIDEO);

            } else if ("Auto".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

                startAppAd = new StartAppAd(context);
                Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.REWARDED_VIDEO);
            }
            adsLaunched = true;
        }

        private void startStreamFromExternalLaunchers(Media movieDetail, String link, int hls, MediaStream mediaStream) {


            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_bottom_stream);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;


            LinearLayout vlc = dialog.findViewById(R.id.vlc);
            LinearLayout mxPlayer = dialog.findViewById(R.id.mxPlayer);
            LinearLayout easyplexPlayer = dialog.findViewById(R.id.easyplexPlayer);
            LinearLayout webcast = dialog.findViewById(R.id.webCast);


            vlc.setOnClickListener(v12 -> {
                Tools.streamMediaFromVlc(context,link,movieDetail,settingsManager, mediaStream);
                dialog.hide();
            });

            mxPlayer.setOnClickListener(v12 -> {
                Tools.streamMediaFromMxPlayer(context,link,movieDetail,settingsManager);
                dialog.hide();

            });

            webcast.setOnClickListener(v12 -> {
                Tools.streamMediaFromMxWebcast(context,link,movieDetail,settingsManager);
                dialog.hide();

            });


            easyplexPlayer.setOnClickListener(v12 -> {
                startStreamFromDialog(movieDetail, link,hls);
                dialog.hide();


            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);



        }

        private void startStreamFromDialog(Media movieDetail, String link, int hls) {

            String artwork = movieDetail.getPosterPath();
            String name = movieDetail.getName();
            String type = "streaming";
            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(movieDetail.getId(),
                    null,null,type, name, link, artwork, null
                    , null, null,null,
                    null,null,
                    null,
                    null,null,null,hls,null,null,
                    null,0,0,null,null,0));
            intent.putExtra(ARG_MOVIE, movieDetail);
            context.startActivity(intent);

        }

        private void startStreamFromEmbed(String link) {


            Intent intent = new Intent(context, EmbedActivity.class);
            intent.putExtra(Constants.MOVIE_LINK, link);
            context.startActivity(intent);
        }


    }





    @Override
    public void onViewDetachedFromWindow(@NonNull FeaturedViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        adsLaunched = false;
        mRewardedAd = null;
        UnityAds.removeListener(iUnityAdsListener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        adsLaunched = false;
        mRewardedAd = null;
        UnityAds.removeListener(iUnityAdsListener);
    }
}
