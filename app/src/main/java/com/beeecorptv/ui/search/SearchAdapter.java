package com.beeecorptv.ui.search;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.beeecorptv.util.Constants.ARG_MOVIE;
import static com.beeecorptv.util.Constants.SERVER_BASE_URL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.easyplex.easyplexsupportedhosts.EasyPlexSupportedHosts;
import com.easyplex.easyplexsupportedhosts.Model.EasyPlexSupportedHostsModel;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.data.repository.AnimeRepository;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.ItemSuggest2Binding;
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
import com.beeecorptv.util.Constants;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.Tools;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
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
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.ads.UnityAdsImplementation;
import com.vungle.warren.AdConfig;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


/**
 * Adapter for Search Results (Movies,Series,Animes,Streaming).
 *
 * @author Yobex.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {


    private MaxRewardedAd maxRewardedAd;
    private List<Media> castList;
    private Context context;
    private RewardedAd mReward;
    boolean isLoading;
    private boolean adsLaunched = false;
    private SettingsManager settingsManager;
    private StartAppAd startAppAd;
    private int qualitySelected;
    private MediaRepository repository;
    private AnimeRepository animeRepository;
    private AuthManager authManager;
    private TokenManager tokenManager;
    private History history;
    private static final int PRELOAD_TIME_S = 2;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String mediaGenre;
    private EasyPlexSupportedHosts easyPlexSupportedHosts;


    public void setSearch(List<Media> castList, Context context, SettingsManager
            settingsManager, MediaRepository repository, AuthManager authManager, TokenManager tokenManager, AnimeRepository animeRepository) {
        this.castList = castList;
        this.context = context;
        this.settingsManager = settingsManager;
        this.repository = repository;
        this.authManager = authManager;
        this.tokenManager = tokenManager;
        this.animeRepository = animeRepository;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemSuggest2Binding binding = ItemSuggest2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private final ItemSuggest2Binding binding;


        SearchViewHolder(@NonNull ItemSuggest2Binding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = castList.get(position);

            if (!adsLaunched) {

                createAndLoadRewardedAd();

            }

            if ( media.getBackdropPath() !=null &&  !media.getBackdropPath().isEmpty()) {
                Tools.onLoadMediaCover(context, binding.itemMovieImage, media.getBackdropPath());
            }else {

                Tools.onLoadMediaCoverEmptyCovers(context, binding.itemMovieImage, media.getBackdropPath());
            }


            if ("Anime".equals(media.getType())) {
                binding.eptitle.setText(media.getName());
                binding.epoverview.setText(media.getOverview());
                binding.viewMovieViews.setText(context.getResources().getString(R.string.animes));
            } else if ("Streaming".equals(media.getType())) {
                binding.eptitle.setText(media.getName());
                binding.epoverview.setText(media.getOverview());
                binding.viewMovieViews.setText(context.getResources().getString(R.string.streaming));
            } else if ("Movie".equals(media.getType())) {
                binding.eptitle.setText(media.getTitle());
                binding.epoverview.setText(media.getOverview());
                binding.viewMovieViews.setText(context.getResources().getString(R.string.movies));
            } else if ("Serie".equals(media.getType())) {
                binding.eptitle.setText(media.getName());
                binding.epoverview.setText(media.getOverview());
                binding.viewMovieViews.setText(context.getResources().getString(R.string.series));
            }



            binding.downloadEpisode.setOnClickListener(v -> {

                switch (media.getType()) {
                    case "Anime":
                        animeRepository.getAnimeDetails(media.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull Media movieDetail) {

                                        if (movieDetail.getSeasons().get(0).getEpisodes().get(0).getVideos().isEmpty()) {

                                            DialogHelper.showNoStreamAvailable(context);

                                        } else {

                                            if (movieDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                                onLoadStreamAnime(movieDetail);


                                            } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1
                                                    && movieDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                                onLoadSubscribeDialog(movieDetail, "2");

                                            } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getPremuim() == 0) {


                                                onLoadStreamAnime(movieDetail);

                                            } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getPremuim() == 0) {


                                                onLoadStreamAnime(movieDetail);


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
                                });


                        break;
                    case "Streaming":

                        repository.getStream(media.getId(), settingsManager.getSettings().getApiKey())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull Media movieDetail) {


                                        if (settingsManager.getSettings().getLivetvMultiServers() == 1) {


                                            if (movieDetail.getVideos() !=null && !movieDetail.getVideos().isEmpty()) {


                                                if (movieDetail.getVip() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                                    onLoadStreamStream(movieDetail);


                                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getVip() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                                    onLoadSubscribeDialog(movieDetail, "streaming");

                                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getVip() == 0) {


                                                    onLoadStreamStream(movieDetail);

                                                } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getVip() == 0) {


                                                    onLoadStreamStream(movieDetail);


                                                } else {

                                                    DialogHelper.showPremuimWarning(context);

                                                }

                                         }else {

                                                DialogHelper.showNoStreamAvailable(context);

                                            }
                                        }else {



                                            if (movieDetail.getLink().isEmpty()) {

                                                DialogHelper.showNoStreamAvailable(context);

                                            } else {

                                                if (movieDetail.getVip() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                                    onLoadStreamStream(movieDetail);


                                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getVip() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                                    onLoadSubscribeDialog(movieDetail, "streaming");

                                                } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getVip() == 0) {


                                                    onLoadStreamStream(movieDetail);

                                                } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getVip() == 0) {


                                                    onLoadStreamStream(movieDetail);


                                                } else {

                                                    DialogHelper.showPremuimWarning(context);

                                                }
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
                                });

                        break;
                    case "Serie":

                        repository.getSerie(media.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull Media movieDetail) {


                                        if (movieDetail.getSeasons().get(0).getEpisodes().get(0).getVideos().isEmpty()) {

                                            DialogHelper.showNoStreamAvailable(context);

                                        } else {

                                            if (movieDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                                onLoadStreamSerie(movieDetail);

                                            } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                                onLoadSubscribeDialog(movieDetail, "1");

                                            } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getPremuim() == 0) {


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
                                });

                        break;
                    case "Movie":
                        repository.getMovie(media.getId(), settingsManager.getSettings().getApiKey())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<>() {
                                    @Override
                                    public void onSubscribe(@NotNull Disposable d) {

                                        //

                                    }

                                    @Override
                                    public void onNext(@NotNull Media movieDetail) {

                                        if (movieDetail.getVideos().isEmpty()) {

                                            DialogHelper.showNoStreamAvailable(context);

                                        } else {


                                            if (movieDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                                                onLoadStreamMovie(movieDetail);


                                            } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                                onLoadSubscribeDialog(movieDetail, "0");

                                            } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getPremuim() == 0) {


                                                onLoadStreamMovie(movieDetail);

                                            } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getPremuim() == 0) {


                                                onLoadStreamMovie(movieDetail);


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
                                });

                        break;
                    default:
                        DialogHelper.showNoStreamAvailable(context);
                        break;
                }
            });

            binding.rootLayout.setOnClickListener(view -> {
                Intent intent;
                switch (media.getType()) {
                    case "Anime":
                        intent = new Intent(context, AnimeDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);
                        break;
                    case "Streaming":
                        intent = new Intent(context, StreamingetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);
                        break;
                    case "Movie":
                        intent = new Intent(context, MovieDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);
                        break;
                    case "Serie":
                        intent = new Intent(context, SerieDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);
                        break;
                }


            });

        }

        private void onLoadStreamStream(Media movieDetail) {

            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                    .getCurrentCastSession();

            if (settingsManager.getSettings().getLivetvMultiServers() == 1) {

                if (movieDetail.getVideos() !=null && !movieDetail.getVideos().isEmpty()) {


                    if (settingsManager.getSettings().getServerDialogSelection() == 1) {


                        String[] charSequence = new String[movieDetail.getVideos().size()];

                        for (int i = 0; i < movieDetail.getVideos().size(); i++) {
                            charSequence[i] = movieDetail.getVideos().get(i).getServer() + " - " + movieDetail.getVideos().get(i).getLang();

                        }

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                        builder.setTitle(context.getString(R.string.select_qualities));
                        builder.setCancelable(true);
                        builder.setItems(charSequence, (dialogInterface, wich) -> {


                            if (movieDetail.getVideos().get(wich).getEmbed() == 1) {

                                startStreamFromEmbed(movieDetail.getVideos().get(wich).getLink());

                            } else {

                                if (castSession !=null && castSession.isConnected()) {

                                    startStreamCasting(movieDetail, movieDetail.getVideos().get(wich).getLink());

                                }else {

                                    if (settingsManager.getSettings().getVlc() == 1) {

                                        startStreamFromExternalLaunchers(movieDetail,movieDetail.getVideos().get(wich).getLink()
                                                ,movieDetail.getVideos().get(wich).getHls(),movieDetail.getVideos().get(wich));

                                    }else {

                                        Tools.startMainStreamStreaming(context,movieDetail, movieDetail.getVideos().get(wich).getLink(),movieDetail.getVideos().get(wich).getHls());
                                    }

                                }

                            }

                        });

                        builder.show();

                    }else {


                        if (movieDetail.getVideos().get(0).getEmbed() == 1) {

                            startStreamFromEmbed(movieDetail.getVideos().get(0).getLink());

                        } else {

                            if (castSession !=null && castSession.isConnected()) {

                                startStreamCasting(movieDetail, movieDetail.getVideos().get(0).getLink());

                            }else {

                                if (settingsManager.getSettings().getVlc() == 1) {

                                    startStreamFromExternalLaunchers(movieDetail,movieDetail.getVideos().get(0).getLink(),movieDetail.getVideos().get(0).getHls(), movieDetail.getVideos().get(0));

                                }else {

                                    Tools.startMainStreamStreaming(context,movieDetail, movieDetail.getVideos().get(0).getLink(),movieDetail.getVideos().get(0).getHls());
                                }

                            }

                        }
                    }

                }else {

                    DialogHelper.showNoStreamAvailable(context);

                }


            }else {

                if (movieDetail.getLink() !=null && !movieDetail.getLink().isEmpty()) {


                    if (movieDetail.getEmbed() == 1) {

                        startStreamFromEmbed(movieDetail.getLink());

                    }else {

                        if (castSession !=null && castSession.isConnected()) {

                            startStreamCasting(movieDetail, movieDetail.getLink());

                        }else {

                            if (settingsManager.getSettings().getVlc() == 1) {

                                startStreamFromExternalLaunchers(movieDetail, movieDetail.getLink(), movieDetail.getHls(), null);

                            }else {

                                startStreamFromDialogStreaming(movieDetail, movieDetail.getLink(),movieDetail.getHls());
                            }

                        }

                    }

                }else {

                    DialogHelper.showNoStreamAvailable(context);
                }
            }


        }

        private void startStreamFromDialogStreaming(Media movieDetail, String link, int hls) {

            Tools.startMainStreamStreaming(context,movieDetail, link,hls);

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
                 Tools.startMainStreamStreaming(context,movieDetail, link,hls);
                dialog.hide();


            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);

            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

        @SuppressLint("StaticFieldLeak")
        private void onLoadStreamAnime(Media media) {

            for (Genre genre : media.getGenres()) {
                mediaGenre = genre.getName();
            }


            if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                String[] charSequence = new String[media.getSeasons().get(0).getEpisodes().get(0).getVideos().size()];
                for (int i = 0; i < media.getSeasons().get(0).getEpisodes().get(0).getVideos().size(); i++) {
                    charSequence[i] = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(i).getServer());

                }


                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                builder.setTitle(R.string.source_quality);
                builder.setCancelable(true);
                builder.setItems(charSequence, (dialogInterface, wich) -> {


                  if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getEmbed() == 1) {

                        Intent intent = new Intent(context, EmbedActivity.class);
                        intent.putExtra(Constants.MOVIE_LINK, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());
                        context.startActivity(intent);


                    }  else if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getSupportedHosts() == 1) {


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
                                        CharSequence[] name = new CharSequence[vidURL.size()];

                                        for (int i = 0; i < vidURL.size(); i++) {
                                            name[i] = vidURL.get(i).getQuality();
                                        }


                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                        builder.setTitle(context.getString(R.string.select_qualities));
                                        builder.setCancelable(true);
                                        builder.setItems(name, (dialogInterface, i) -> {

                                            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                                    .getCurrentCastSession();
                                            if (castSession != null && castSession.isConnected()) {

                                                onLoadCastAnime(media,vidURL.get(i).getUrl());


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
                                                    Tools.streamEpisodeFromVlc(context,vidURL.get(i).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                    dialog.hide();
                                                });

                                                mxPlayer.setOnClickListener(v12 -> {
                                                    Tools.streamEpisodeFromMxPlayer(context,vidURL.get(i).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                    dialog.hide();

                                                });

                                                webcast.setOnClickListener(v12 -> {

                                                    Tools.streamEpisodeFromMxWebcast(context,vidURL.get(i).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                    dialog.hide();

                                                });

                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                    onLoadMainPlayerStreamAnime(media, vidURL.get(i).getUrl(),  media.getImdbExternalId());
                                                    dialog.hide();


                                                });

                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);

                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                        dialog.dismiss());


                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);


                                            } else {

                                                onLoadMainPlayerStreamAnime(media, vidURL.get(i).getUrl(),  media.getImdbExternalId());


                                            }


                                        });

                                        builder.show();


                                    } else
                                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {


                                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                            .getCurrentCastSession();
                                    if (castSession != null && castSession.isConnected()) {

                                        onLoadCastAnime(media,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink());

                                    }  else {

                                        onLoadMainPlayerStreamAnime(media, vidURL.get(0).getUrl(),  media.getImdbExternalId());
                                    }

                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());


                    } else {


                      CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                              .getCurrentCastSession();
                      if (castSession != null && castSession.isConnected()) {

                          onLoadCastAnime(media,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());


                      } else if (settingsManager.getSettings().getVlc() == 1) {


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
                              Tools.streamEpisodeFromVlc(context,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink()
                                      ,media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                              dialog.hide();
                          });

                          mxPlayer.setOnClickListener(v12 -> {
                              Tools.streamEpisodeFromMxPlayer(context,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),
                                      media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                              dialog.hide();

                          });

                          webcast.setOnClickListener(v12 -> {

                              Tools.streamEpisodeFromMxWebcast(context,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink()
                                      ,media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                              dialog.hide();

                          });

                            easyplexPlayer.setOnClickListener(v12 -> {
                                onLoadMainPlayerStreamAnime(media, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),  media.getImdbExternalId());

                          dialog.hide();


                            });

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);

                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                    dialog.dismiss());


                            dialog.show();
                            dialog.getWindow().setAttributes(lp);


                        } else {

                            onLoadMainPlayerStreamAnime(media, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),  media.getImdbExternalId());


                        }

                    }


                });


                builder.show();

            } else {

                if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getEmbed() == 1) {


                    Intent intent = new Intent(context, EmbedActivity.class);
                    intent.putExtra(Constants.MOVIE_LINK, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink());
                    context.startActivity(intent);


                } else if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getSupportedHosts() == 1) {

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

                                    CharSequence[] name = new CharSequence[vidURL.size()];

                                    for (int i = 0; i < vidURL.size(); i++) {
                                        name[i] = vidURL.get(i).getQuality();
                                    }


                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                    builder.setTitle(context.getString(R.string.select_qualities));
                                    builder.setCancelable(true);
                                    builder.setItems(name, (dialogInterface, wich) -> {

                                        CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                                .getCurrentCastSession();
                                        if (castSession != null && castSession.isConnected()) {

                                            onLoadCastAnime(media,vidURL.get(wich).getUrl());


                                        }  else  if (settingsManager.getSettings().getVlc() == 1) {

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
                                                Tools.streamEpisodeFromVlc(context,vidURL.get(wich).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                dialog.hide();
                                            });

                                            mxPlayer.setOnClickListener(v12 -> {
                                                Tools.streamEpisodeFromMxPlayer(context,vidURL.get(wich).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                dialog.hide();

                                            });

                                            webcast.setOnClickListener(v12 -> {

                                                Tools.streamEpisodeFromMxWebcast(context,vidURL.get(wich).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                dialog.hide();

                                            });

                                            easyplexPlayer.setOnClickListener(v12 -> {

                                                onLoadMainPlayerStreamAnime(media, vidURL.get(wich).getUrl(),  media.getImdbExternalId());

                                                dialog.hide();


                                            });

                                            dialog.show();
                                            dialog.getWindow().setAttributes(lp);

                                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                            dialog.dismiss());


                                            dialog.show();
                                            dialog.getWindow().setAttributes(lp);


                                        } else {

                                            onLoadMainPlayerStreamAnime(media, vidURL.get(wich).getUrl(),  media.getImdbExternalId());


                                        }


                                    });

                                    builder.show();


                                } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                            } else {

                                CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                        .getCurrentCastSession();
                                if (castSession != null && castSession.isConnected()) {

                                    onLoadCastAnime(media,vidURL.get(0).getUrl());

                                }

                                 else {

                                    onLoadMainPlayerStreamAnime(media, vidURL.get(0).getUrl(),  media.getImdbExternalId());

                                }


                            }

                        }

                        @Override
                        public void onError() {

                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    easyPlexSupportedHosts.find(media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink());


                } else {


                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                            .getCurrentCastSession();
                    if (castSession != null && castSession.isConnected()) {

                        onLoadCastAnime(media,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink());

                    } else  {

                        onLoadMainPlayerStreamAnime(media, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink(),  media.getImdbExternalId());
                    }


                }


            }


        }

        private void onLoadCastAnime(Media media, String downloadUrl) {


            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                    .getCurrentCastSession();

            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, media.getSeasons().get(0).getEpisodes().get(0).getName());
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, media.getSeasons().get(0).getEpisodes().get(0).getName());

            movieMetadata.addImage(new WebImage(Uri.parse(media.getSeasons().get(0).getEpisodes().get(0).getStillPath())));
            List<MediaTrack> tracks = new ArrayList<>();

            MediaInfo mediaInfo = new MediaInfo.Builder(downloadUrl)
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
            PopupMenu popup = new PopupMenu(context, binding.rootLayout);
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
                                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
                    } else {
                        return false;
                    }
                } else {
                    if (provider1.getCount() == 0) {
                        remoteMediaClient.queueLoad(newItemArray, 0,
                                MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
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

        private void onLoadMainPlayerStreamAnime(Media media, String url, String serieIdExternal) {

            String tvseasonid = String.valueOf(media.getSeasons().get(0).getId());
            Integer currentep = Integer.parseInt(media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber());
            String currentepname = media.getSeasons().get(0).getEpisodes().get(0).getName();
            String currenteptmdbnumber = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
            String currentseasons = media.getSeasons().get(0).getSeasonNumber();
            String currentseasonsNumber = media.getSeasons().get(0).getSeasonNumber();
            String currentepimdb = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
            String artwork = media.getSeasons().get(0).getEpisodes().get(0).getStillPath();
            String typemedia = "anime";
            String currentquality = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getServer();
            String name = "S0" + currentseasons + "E" + media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber() + " : " + media.getSeasons().get(0).getEpisodes().get(0).getName();
            String serieCover = media.getPosterPath();
            Integer episodeHasRecap = media.getSeasons().get(0).getEpisodes().get(0).getHasrecap();
            Integer episodeRecapStartIn = media.getSeasons().get(0).getEpisodes().get(0).getSkiprecapStartIn();
            int hls = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getHls();
            float voteAverage = Float.parseFloat(media.getSeasons().get(0).getEpisodes().get(0).getVoteAverage());


            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                    MediaModel.media(media.getId(),
                            null,
                            currentquality, typemedia, name, url, artwork,
                            null, currentep
                            , currentseasons, currentepimdb, tvseasonid,
                            currentepname,
                            currentseasonsNumber, null,
                            currenteptmdbnumber, media.getPremuim(), hls,
                            null, serieIdExternal, serieCover, episodeHasRecap, episodeRecapStartIn, mediaGenre, media.getName(), voteAverage));
            context.startActivity(intent);

            history = new History(media.getId(), media.getId(), serieCover, name, "", "");
            history.setSerieName(media.getName());
            history.setPosterPath(serieCover);
            history.setTitle(name);
            history.setBackdropPath(artwork);
            history.setEpisodeNmber(String.valueOf(currentep));
            history.setSeasonsId(tvseasonid);
            history.setPosition(0);
            history.setType(typemedia);
            history.setTmdbId(media.getId());
            history.setEpisodeId(currenteptmdbnumber);
            history.setEpisodeName(currentepname);
            history.setEpisodeTmdb(currenteptmdbnumber);
            history.setSerieId(media.getId());
            history.setCurrentSeasons(currentseasons);
            history.setSeasonsId(tvseasonid);
            history.setSeasonsNumber(media.getSeasons().get(0).getName());
            history.setImdbExternalId(serieIdExternal);
            history.setPremuim(media.getPremuim());
            history.setVoteAverage(voteAverage);
            history.setMediaGenre(mediaGenre);
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());
        }


        private void onLoadAnimeYoutube(Media media, String downloadUrl, String imdbId) {

            String tvseasonid = String.valueOf(media.getSeasons().get(0).getId());
            Integer currentep = Integer.parseInt(media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber());
            String currentepname = media.getSeasons().get(0).getEpisodes().get(0).getName();
            String currenteptmdbnumber = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
            String currentseasons = media.getSeasons().get(0).getSeasonNumber();
            String currentseasonsNumber = media.getSeasons().get(0).getSeasonNumber();
            String currentepimdb = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
            String artwork = media.getSeasons().get(0).getEpisodes().get(0).getStillPath();
            String typemedia = "anime";
            String currentquality = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getServer();
            String name = "S0" + currentseasons + "E" + media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber() + " : " + media.getSeasons().get(0).getEpisodes().get(0).getName();
            String serieCover = media.getPosterPath();
            Integer episodeHasRecap = media.getSeasons().get(0).getEpisodes().get(0).getHasrecap();
            Integer episodeRecapStartIn = media.getSeasons().get(0).getEpisodes().get(0).getSkiprecapStartIn();
            int hls = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getHls();
            float voteAverage = Float.parseFloat(media.getSeasons().get(0).getEpisodes().get(0).getVoteAverage());


            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                    MediaModel.media(media.getId(),
                            null,
                            currentquality, typemedia, name, downloadUrl, artwork,
                            null, currentep
                            , currentseasons, currentepimdb, tvseasonid,
                            currentepname,
                            currentseasonsNumber, null,
                            currenteptmdbnumber, media.getPremuim(), hls,
                            null, imdbId, serieCover, episodeHasRecap, episodeRecapStartIn, mediaGenre, media.getName(), voteAverage));
            context.startActivity(intent);

            history = new History(media.getId(), media.getId(), serieCover, name, "", "");
            history.setSerieName(media.getName());
            history.setPosterPath(serieCover);
            history.setTitle(name);
            history.setBackdropPath(artwork);
            history.setEpisodeNmber(String.valueOf(currentep));
            history.setSeasonsId(tvseasonid);
            history.setPosition(0);
            history.setType(typemedia);
            history.setTmdbId(media.getId());
            history.setEpisodeId(currenteptmdbnumber);
            history.setEpisodeName(currentepname);
            history.setEpisodeTmdb(currenteptmdbnumber);
            history.setSerieId(media.getId());
            history.setCurrentSeasons(currentseasons);
            history.setSeasonsId(tvseasonid);
            history.setSeasonsNumber(media.getSeasons().get(0).getName());
            history.setImdbExternalId(media.getImdbExternalId());
            history.setPremuim(media.getPremuim());
            history.setVoteAverage(voteAverage);
            history.setMediaGenre(mediaGenre);
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());
        }

        @SuppressLint("StaticFieldLeak")
        private void onLoadStreamSerie(Media media) {


            if (settingsManager.getSettings().getServerDialogSelection() == 1) {

                String[] charSequence = new String[media.getSeasons().get(0).getEpisodes().get(0).getVideos().size()];
                for (int i = 0; i < media.getSeasons().get(0).getEpisodes().get(0).getVideos().size(); i++) {
                    charSequence[i] = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(i).getServer());

                }


                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                builder.setTitle(R.string.source_quality);
                builder.setCancelable(true);
                builder.setItems(charSequence, (dialogInterface, wich) -> {


                 if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getEmbed() == 1) {


                        Intent intent = new Intent(context, EmbedActivity.class);
                        intent.putExtra(Constants.MOVIE_LINK, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());
                        context.startActivity(intent);


                    } else if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getSupportedHosts() == 1) {

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

                                        CharSequence[] name = new CharSequence[vidURL.size()];

                                        for (int i = 0; i < vidURL.size(); i++) {
                                            name[i] = vidURL.get(i).getQuality();
                                        }


                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                        builder.setTitle(context.getString(R.string.select_qualities));
                                        builder.setCancelable(true);
                                        builder.setItems(name, (dialogInterface, i) -> {


                                            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                                    .getCurrentCastSession();
                                            if (castSession != null && castSession.isConnected()) {


                                                onLoadCastAnime(media,vidURL.get(i).getUrl());


                                            } else if (settingsManager.getSettings().getVlc() == 1) {

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
                                                    Tools.streamEpisodeFromVlc(context,vidURL.get(i).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                    dialog.hide();
                                                });

                                                mxPlayer.setOnClickListener(v12 -> {
                                                    Tools.streamEpisodeFromMxPlayer(context,vidURL.get(i).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                    dialog.hide();

                                                });

                                                webcast.setOnClickListener(v12 -> {

                                                    Tools.streamEpisodeFromMxWebcast(context,vidURL.get(i).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                    dialog.hide();

                                                });

                                                easyplexPlayer.setOnClickListener(v12 -> {

                                                    onLoadMainPlayerStreamSerie(media, vidURL.get(i).getUrl(),  media.getImdbExternalId());
                                                    dialog.hide();


                                                });

                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);

                                                dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                        dialog.dismiss());


                                                dialog.show();
                                                dialog.getWindow().setAttributes(lp);


                                            } else {

                                                onLoadMainPlayerStreamSerie(media, vidURL.get(i).getUrl(),  media.getImdbExternalId());



                                            }


                                        });

                                        builder.show();


                                    } else
                                        Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                                } else {


                                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                            .getCurrentCastSession();
                                    if (castSession != null && castSession.isConnected()) {


                                        onLoadCastAnime(media,vidURL.get(0).getUrl());


                                    }  else {

                                        onLoadMainPlayerStreamSerie(media, vidURL.get(0).getUrl(),  media.getImdbExternalId());

                                    }



                                }

                            }

                            @Override
                            public void onError() {

                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        easyPlexSupportedHosts.find(media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());


                    } else {

                     CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                             .getCurrentCastSession();
                     if (castSession != null && castSession.isConnected()) {

                         onLoadCastAnime(media,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());


                     }  else   if (settingsManager.getSettings().getVlc() == 1) {


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
                             Tools.streamEpisodeFromVlc(context,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                             dialog.hide();
                         });

                         mxPlayer.setOnClickListener(v12 -> {
                             Tools.streamEpisodeFromMxPlayer(context,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                             dialog.hide();

                         });

                         webcast.setOnClickListener(v12 -> {

                             Tools.streamEpisodeFromMxWebcast(context,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                             dialog.hide();

                         });

                            easyplexPlayer.setOnClickListener(v12 -> {
                                onLoadMainPlayerStreamSerie(media, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),  media.getImdbExternalId());

                           dialog.hide();


                            });

                            dialog.show();
                            dialog.getWindow().setAttributes(lp);

                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                    dialog.dismiss());


                            dialog.show();
                            dialog.getWindow().setAttributes(lp);


                        } else {


                         if (castSession != null && castSession.isConnected()) {

                             onLoadCastAnime(media,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink());

                         }else {

                             onLoadMainPlayerStreamSerie(media, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),  media.getImdbExternalId());
                         }


                        }

                    }


                });


                builder.show();

            } else {

                if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getEmbed() == 1) {


                    Intent intent = new Intent(context, EmbedActivity.class);
                    intent.putExtra(Constants.MOVIE_LINK, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink());
                    context.startActivity(intent);


                } else if (media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getSupportedHosts() == 1) {

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

                                    CharSequence[] name = new CharSequence[vidURL.size()];

                                    for (int i = 0; i < vidURL.size(); i++) {
                                        name[i] = vidURL.get(i).getQuality();
                                    }


                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                                    builder.setTitle(context.getString(R.string.select_qualities));
                                    builder.setCancelable(true);
                                    builder.setItems(name, (dialogInterface, wich) -> {

                                        CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                                .getCurrentCastSession();

                                        if (castSession != null && castSession.isConnected()) {

                                            onLoadCastAnime(media,vidURL.get(wich).getUrl());

                                        }  else if (settingsManager.getSettings().getVlc() == 1) {

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
                                                Tools.streamEpisodeFromVlc(context,media.
                                                                getSeasons().get(0).getEpisodes().get(0).getVideos().get(wich).getLink(),media.getSeasons().get(0).getEpisodes().get(0)
                                                        ,settingsManager);
                                                dialog.hide();
                                            });

                                            mxPlayer.setOnClickListener(v12 -> {
                                                Tools.streamEpisodeFromMxPlayer(context,vidURL.get(wich).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                dialog.hide();

                                            });

                                            webcast.setOnClickListener(v12 -> {

                                                Tools.streamEpisodeFromMxWebcast(context,vidURL.get(wich).getUrl(),media.getSeasons().get(0).getEpisodes().get(0),settingsManager);
                                                dialog.hide();

                                            });

                                            easyplexPlayer.setOnClickListener(v12 -> {

                                                onLoadMainPlayerStreamSerie(media, vidURL.get(wich).getUrl(), media.getImdbExternalId());


                                            dialog.hide();

                                            });

                                            dialog.show();
                                            dialog.getWindow().setAttributes(lp);

                                            dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                                    dialog.dismiss());


                                            dialog.show();
                                            dialog.getWindow().setAttributes(lp);


                                        } else {


                                            if (castSession != null && castSession.isConnected()) {

                                                onLoadCastAnime(media,vidURL.get(wich).getUrl());

                                            }else  {

                                                onLoadMainPlayerStreamSerie(media, vidURL.get(wich).getUrl(),  media.getImdbExternalId());
                                            }

                                        }


                                    });

                                    builder.show();


                                } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                            } else {


                                CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                        .getCurrentCastSession();

                                if (castSession != null && castSession.isConnected()) {

                                    onLoadCastAnime(media,vidURL.get(0).getUrl());

                                }

                                 else {

                                    onLoadMainPlayerStreamSerie(media, vidURL.get(0).getUrl(),  media.getImdbExternalId());

                                }


                            }

                        }

                        @Override
                        public void onError() {

                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    easyPlexSupportedHosts.find(media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink());


                } else {


                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                            .getCurrentCastSession();

                    if (castSession != null && castSession.isConnected()) {

                        onLoadCastAnime(media,media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink());

                    }else  {


                        onLoadMainPlayerStreamSerie(media, media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getLink(),  media.getImdbExternalId());
                    }

                }


            }
        }


        private void onLoadMainPlayerStreamSerie(Media media, String url, String serieIdExternal) {

            String tvseasonid = String.valueOf(media.getSeasons().get(0).getId());
            Integer currentep = Integer.parseInt(media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber());
            String currentepname = media.getSeasons().get(0).getEpisodes().get(0).getName();
            String currenteptmdbnumber = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
            String currentseasons = media.getSeasons().get(0).getSeasonNumber();
            String currentseasonsNumber = media.getSeasons().get(0).getSeasonNumber();
            String currentepimdb = String.valueOf(media.getSeasons().get(0).getEpisodes().get(0).getId());
            String artwork = media.getSeasons().get(0).getEpisodes().get(0).getStillPath();
            String typemedia = "1";
            String currentquality = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getServer();
            String name = "S0" + currentseasons + "E" + media.getSeasons().get(0).getEpisodes().get(0).getEpisodeNumber() + " : " + media.getSeasons().get(0).getEpisodes().get(0).getName();
            String serieCover = media.getPosterPath();
            Integer episodeHasRecap = media.getSeasons().get(0).getEpisodes().get(0).getHasrecap();
            Integer episodeRecapStartIn = media.getSeasons().get(0).getEpisodes().get(0).getSkiprecapStartIn();
            int hls = media.getSeasons().get(0).getEpisodes().get(0).getVideos().get(0).getHls();
            float voteAverage = Float.parseFloat(media.getSeasons().get(0).getEpisodes().get(0).getVoteAverage());


            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                    MediaModel.media(media.getId(),
                            null,
                            currentquality, typemedia, name, url, artwork,
                            null, currentep
                            , currentseasons, currentepimdb, tvseasonid,
                            currentepname,
                            currentseasonsNumber, null,
                            currenteptmdbnumber, media.getPremuim(), hls,
                            null, serieIdExternal, serieCover, episodeHasRecap, episodeRecapStartIn, mediaGenre, media.getName(), voteAverage));
            context.startActivity(intent);

            history = new History(media.getId(), media.getId(), serieCover, name, "", "");
            history.setSerieName(media.getName());
            history.setPosterPath(serieCover);
            history.setTitle(name);
            history.setBackdropPath(artwork);
            history.setEpisodeNmber(String.valueOf(currentep));
            history.setSeasonsId(tvseasonid);
            history.setPosition(0);
            history.setType(typemedia);
            history.setTmdbId(media.getId());
            history.setEpisodeId(currenteptmdbnumber);
            history.setEpisodeName(currentepname);
            history.setEpisodeTmdb(currenteptmdbnumber);
            history.setSerieId(media.getId());
            history.setCurrentSeasons(currentseasons);
            history.setSeasonsId(tvseasonid);
            history.setSeasonsNumber(media.getSeasons().get(0).getName());
            history.setImdbExternalId(serieIdExternal);
            history.setPremuim(media.getPremuim());
            history.setVoteAverage(voteAverage);
            history.setMediaGenre(mediaGenre);
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());



        }


        private void onLoadStreamMovie(Media media) {


            for (Genre genre : media.getGenres()) {
                mediaGenre = genre.getName();
            }


            if (settingsManager.getSettings().getServerDialogSelection() == 1) {


                String[] charSequence = new String[media.getVideos().size()];
                for (int i = 0; i < media.getVideos().size(); i++) {
                    charSequence[i] = String.valueOf(media.getVideos().get(i).getServer());

                }


                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                builder.setTitle(context.getString(R.string.select_qualities));
                builder.setCancelable(true);
                builder.setItems(charSequence, (dialogInterface, wich) -> {


                    if (media.getVideos().get(wich).getEmbed() == 1) {


                        startStreamFromEmbed(media.getVideos().get(wich).getLink());


                    } else if (media.getVideos().get(wich).getSupportedHosts() == 1) {


                        startSupportedHostsStreamMovie(media, wich,media.getVideos().get(wich));


                    } else {

                        CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                .getCurrentCastSession();
                        if (castSession != null && castSession.isConnected()) {

                            startStreamCasting(media, media.getVideos().get(wich).getLink());

                        } else {


                            if (settingsManager.getSettings().getVlc() == 1) {

                                startStreamNormalLink(media, wich,media.getVideos().get(wich));


                            } else {

                                startStreamFromDialog(media, wich);
                            }

                        }

                    }


                });

                builder.show();

            } else {


                if (media.getVideos().get(0).getEmbed() == 1) {


                    startStreamFromEmbed(media.getVideos().get(0).getLink());


                }else if (media.getVideos().get(0).getSupportedHosts() == 1) {


                    startSupportedHostsStreamMovie(media, 0, media.getVideos().get(0));


                } else {

                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                            .getCurrentCastSession();
                    if (castSession != null && castSession.isConnected()) {

                        startStreamCasting(media, media.getVideos().get(0).getLink());

                    } else {

                        if (settingsManager.getSettings().getVlc() == 1) {

                            startStreamNormalLink(media, 0, media.getVideos().get(0));


                        } else {

                            startStreamFromDialog(media, 0);
                        }

                    }
                }


            }


    }

    private void startSupportedHostsStreamMovie(Media media, int wich, MediaStream mediaStream) {


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

                        CharSequence[] name = new CharSequence[vidURL.size()];

                        for (int i = 0; i < vidURL.size(); i++) {
                            name[i] = vidURL.get(i).getQuality();
                        }


                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                        builder.setTitle(context.getString(R.string.select_qualities));
                        builder.setCancelable(true);
                        builder.setItems(name, (dialogInterface, i) -> {


                            CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                                    .getCurrentCastSession();
                            if (castSession != null && castSession.isConnected()) {

                                startStreamCasting(media, vidURL.get(i).getUrl());


                            } else if (settingsManager.getSettings().getVlc() == 1) {


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

                                        onStartStreamFromSupportedHost(media, vidURL.get(i).getUrl(), media.getVideos().get(wich).getServer());
                                        dialog.hide();


                                    });

                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);

                                    dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                            dialog.dismiss());


                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);


                                } else {


                                if (castSession != null && castSession.isConnected()) {

                                    startStreamCasting(media, vidURL.get(i).getUrl());


                                }else {

                                    onStartStreamFromSupportedHost(media, vidURL.get(i).getUrl(), media.getVideos().get(wich).getServer());

                                }

                            }


                        });

                        builder.show();


                    } else Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();

                } else {

                    CastSession castSession = CastContext.getSharedInstance(context).getSessionManager()
                            .getCurrentCastSession();

                    if (castSession != null && castSession.isConnected()) {

                        startStreamCasting(media, vidURL.get(0).getUrl());

                    } else if (settingsManager.getSettings().getVlc() == 1) {


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

                        onStartStreamFromSupportedHost(media, vidURL.get(0).getUrl(), media.getVideos().get(wich).getServer());
                        dialog.hide();

                        });

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);

                        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                                dialog.dismiss());


                        dialog.show();
                        dialog.getWindow().setAttributes(lp);


                    }else {


                        if (castSession != null && castSession.isConnected()) {

                            startStreamCasting(media, vidURL.get(0).getUrl());

                        }

                       else {

                            onStartStreamFromSupportedHost(media, vidURL.get(0).getUrl(), media.getVideos().get(wich).getServer());

                        }

                    }

                }

            }

            @Override
            public void onError() {

                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        easyPlexSupportedHosts.find(media.getVideos().get(wich).getLink());


    }

    private void onStartStreamFromSupportedHost(Media movieDetail, String url, String server) {

        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                MediaModel.media(movieDetail.getId(),
                        null, server, "0", movieDetail.getTitle(),
                        url, movieDetail.getBackdropPath(), null, null
                        , null, null, null, null,
                        null,
                        null, null, movieDetail.getPremuim(), 0, movieDetail.getSubstype(), movieDetail.getImdbExternalId()

                        , movieDetail.getPosterPath(), movieDetail.getHasrecap(), movieDetail.getSkiprecapStartIn(), mediaGenre, null, movieDetail.getVoteAverage()));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);


        history = new History(movieDetail.getId(), movieDetail.getId(), movieDetail.getPosterPath(), movieDetail.getTitle(), movieDetail.getBackdropPath(), "");

        history.setUserHistoryId(authManager.getUserInfo().getId());

        history.setType("0");
        history.setTmdbId(movieDetail.getId());
        history.setExternalId(movieDetail.getImdbExternalId());
        history.setPremuim(movieDetail.getPremuim());
        history.setVoteAverage(movieDetail.getVoteAverage());
        compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                .subscribeOn(Schedulers.io())
                .subscribe());


    }


    private void onLoadMainPlayerStreamYoutube(Media movieDetail, int wich, String downloadUrl) {


        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                MediaModel.media(movieDetail.getId(),
                        null, movieDetail.getVideos().get(wich).getServer(), "0", movieDetail.getTitle(),
                        downloadUrl, movieDetail.getBackdropPath(), null, null
                        , null, null, null, null,
                        null,
                        null, null, movieDetail.getPremuim(), 0, movieDetail.getSubstype(), movieDetail.getImdbExternalId()

                        , movieDetail.getPosterPath(), movieDetail.getHasrecap(), movieDetail.getSkiprecapStartIn(), mediaGenre, null, movieDetail.getVoteAverage()));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);


        history = new History(movieDetail.getId(), movieDetail.getId(), movieDetail.getPosterPath(), movieDetail.getTitle(), movieDetail.getBackdropPath(), "");

        history.setUserHistoryId(authManager.getUserInfo().getId());

        if (authManager.getUserInfo().getId() != null && authManager.getUserInfo().getId() == history.getUserHistoryId()) {

            history.setLink(movieDetail.getVideos().get(wich).getLink());
            history.setType("0");
            history.setTmdbId(movieDetail.getId());
            history.setExternalId(movieDetail.getImdbExternalId());
            history.setPremuim(movieDetail.getPremuim());
            history.setVoteAverage(movieDetail.getVoteAverage());
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        }


    }

    private void startStreamFromEmbed(String link) {


        Intent intent = new Intent(context, EmbedActivity.class);
        intent.putExtra(Constants.MOVIE_LINK, link);
        context.startActivity(intent);
    }


    private void startStreamFromDialog(Media media, int wich) {

        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY,
                MediaModel.media(media.getId(),
                        null, media.getVideos().get(wich).getServer(), "0", media.getTitle(),
                        media.getVideos().get(wich).getLink(), media.getBackdropPath(), null, null
                        , null, null, null, null,
                        null,
                        null, null,
                        media.getPremuim(), media.getVideos().get(wich).getHls(), media.getSubstype(), media.getImdbExternalId()
                        , media.getPosterPath(), media.getHasrecap(), media.getSkiprecapStartIn(), mediaGenre, media.getName(), media.getVoteAverage()));
        intent.putExtra(ARG_MOVIE, media);
        context.startActivity(intent);

        history = new History(media.getId(), media.getId(), media.getPosterPath(), media.getTitle(), media.getBackdropPath(), "");

        history.setUserHistoryId(authManager.getUserInfo().getId());

        if (authManager.getUserInfo().getId() != null && authManager.getUserInfo().getId() == history.getUserHistoryId()) {

            history.setLink(media.getVideos().get(wich).getLink());
            history.setType("0");
            history.setTmdbId(media.getId());
            history.setExternalId(media.getImdbExternalId());
            history.setPremuim(media.getPremuim());
            history.setVoteAverage(media.getVoteAverage());
            compositeDisposable.add(Completable.fromAction(() -> repository.addhistory(history))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

        }


    }

    private void startStreamNormalLink(Media media, int wich, MediaStream mediaStream) {


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
            Tools.streamMediaFromVlc(context,media.getVideos().get(wich).getLink(),media,settingsManager, mediaStream);
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


            startStreamFromDialog(media, wich);
            dialog.hide();

        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

                dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }


    private void startStreamCasting(Media movieDetail, String downloadUrl) {

        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, movieDetail.getTitle());
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, mediaGenre);

        movieMetadata.addImage(new WebImage(Uri.parse(movieDetail.getPosterPath())));


        MediaInfo mediaInfo = new MediaInfo.Builder(downloadUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(movieMetadata)
                .build();

        CastSession castSession =
                CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
        if (castSession == null || !castSession.isConnected()) {

            return;
        }
        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }


        final QueueDataProvider provider = QueueDataProvider.getInstance(context);
        PopupMenu popup = new PopupMenu(context, binding.downloadEpisode);
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


    private void onLoadStartAppAds(Media movieDetail, String s) {

        startAppAd.setVideoListener(() -> {

            if ("0".equals(s)) {
                onLoadStreamMovie(movieDetail);
            } else if ("1".equals(s)) {
                onLoadStreamSerie(movieDetail);
            } else if ("2".equals(s)) {
                onLoadStreamAnime(movieDetail);
            }
            if (s.equals("streaming")) {

                onLoadStreamStream(movieDetail);

            }


        });

        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {

                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                DialogHelper.showAdsFailedWarning(context);

            }
        });
    }


    private void createAndLoadRewardedAd() {

        if (settingsManager.getSettings().getDefaultRewardedNetworkAds() != null && "Admob".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            loadRewardedAd();

        }

        if (settingsManager.getSettings().getDefaultRewardedNetworkAds() != null && context.getString(R.string.applovin).equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            maxRewardedAd = MaxRewardedAd.getInstance( settingsManager.getSettings().getApplovinRewardUnitid(), (BaseActivity) context);
            maxRewardedAd.loadAd();

        }

        Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.REWARDED_VIDEO );

        IronSource.init((BaseActivity) context, settingsManager.getSettings().getIronsourceAppKey(), IronSource.AD_UNIT.REWARDED_VIDEO,IronSource.AD_UNIT.INTERSTITIAL);

        // Initialize the AppNext Ads SDK.
        Appnext.init(context);


        if ("StartApp".equals(settingsManager.getSettings().getDefaultNetworkPlayer())) {


            if (settingsManager.getSettings().getStartappId() != null) {

                startAppAd = new StartAppAd(context);
            }


        } else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds()) && settingsManager.getSettings().getAdUnitIdAppodealRewarded() != null) {

            Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.REWARDED_VIDEO );

        } else if ("Auto".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {


            if (settingsManager.getSettings().getStartappId() != null) {

                startAppAd = new StartAppAd(context);
            }


            if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() != null) {

                Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(), Appodeal.REWARDED_VIDEO );


            }

        }

        adsLaunched = true;
    }


    private void onLoadSubscribeDialog(Media movieDetail, String type) {

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
        dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {
        context.startActivity(new Intent(context, SettingsActivity.class));
        dialog.dismiss();
        });

        dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {

            String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();


            if (context.getString(R.string.applovin).equals(defaultRewardedNetworkAds)) {

                onLoadApplovinAds(movieDetail,type);

            }else if (context.getString(R.string.vungle).equals(defaultRewardedNetworkAds)) {

                onLoadVungleAds(movieDetail, type);

            }else if (context.getString(R.string.ironsource).equals(defaultRewardedNetworkAds)) {

                onLoadIronsourceAds(movieDetail, type);

            }else if (context.getString(R.string.appnext).equals(defaultRewardedNetworkAds)) {

                onLoadAppNextAds(movieDetail, type);

            }else if (context.getString(R.string.startapp).equals(defaultRewardedNetworkAds)) {

                onLoadStartAppAds(movieDetail, type);

            } else if (context.getString(R.string.unityads).equals(defaultRewardedNetworkAds)) {


                onLoadUnityAds(movieDetail, type);

            } else if (context.getString(R.string.admob).equals(defaultRewardedNetworkAds)) {

                onLoadAdmobRewardAds(movieDetail, type);

            }else if (context.getString(R.string.facebook).equals(defaultRewardedNetworkAds)) {

                onLoadFaceBookRewardAds(movieDetail, type);

            } else if (context.getString(R.string.appodeal).equals(defaultRewardedNetworkAds)) {

                onLoadAppOdealRewardAds(movieDetail, type);

            }
            dialog.dismiss();


        });


        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

        private void onLoadApplovinAds(Media movieDetail, String type) {

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

                    if ("0".equals(type)) {
                        onLoadStreamMovie(movieDetail);
                    } else if ("1".equals(type)) {
                        onLoadStreamSerie(movieDetail);
                    } else if ("2".equals(type)) {
                        onLoadStreamAnime(movieDetail);
                    } else if ("streaming".equals(type)) {
                        onLoadStreamStream(movieDetail);
                    }

                }

                @Override
                public void onUserRewarded(MaxAd ad, MaxReward reward) {

                }
            });
        }

        private void onLoadAppNextAds(Media movieDetail, String type) {

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

                if ("0".equals(type)) {
                    onLoadStreamMovie(movieDetail);
                } else if ("1".equals(type)) {
                    onLoadStreamSerie(movieDetail);
                } else if ("2".equals(type)) {
                    onLoadStreamAnime(movieDetail);
                } else if ("streaming".equals(type)) {
                    onLoadStreamStream(movieDetail);
                }
            });

            mAppNextAdsVideoRewarded.setOnAdErrorCallback(error -> Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show());
            // Get callback when the user saw the video until the end (video ended)
            mAppNextAdsVideoRewarded.setOnVideoEndedCallback(() -> {

            });

        }

        private void onLoadIronsourceAds(Media movieDetail, String s) {

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

                    if ("0".equals(s)) {
                        onLoadStreamMovie(movieDetail);
                    } else if ("1".equals(s)) {
                        onLoadStreamSerie(movieDetail);
                    } else if ("2".equals(s)) {
                        onLoadStreamAnime(movieDetail);
                    } else if ("streaming".equals(s)) {
                        onLoadStreamStream(movieDetail);
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

        private void onLoadVungleAds(Media movieDetail, String s) {

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



                @Override
                public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                    if ("0".equals(s)) {
                        onLoadStreamMovie(movieDetail);
                    } else if ("1".equals(s)) {
                        onLoadStreamSerie(movieDetail);
                    } else if ("2".equals(s)) {
                        onLoadStreamAnime(movieDetail);
                    } else if ("streaming".equals(s)) {
                        onLoadStreamStream(movieDetail);
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

        private void onLoadAppOdealRewardAds(Media movieDetail, String s) {

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

                if ("0".equals(s)) {
                    onLoadStreamMovie(movieDetail);
                } else if ("1".equals(s)) {
                    onLoadStreamSerie(movieDetail);
                } else if ("2".equals(s)) {
                    onLoadStreamAnime(movieDetail);
                } else if ("streaming".equals(s)) {
                    onLoadStreamStream(movieDetail);
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

    private void onLoadFaceBookRewardAds(Media movieDetail, String s) {

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

                switch (s) {
                    case "0":

                        onLoadStreamMovie(movieDetail);

                        break;
                    case "1":

                        onLoadStreamSerie(movieDetail);


                        break;
                    case "2":

                        onLoadStreamAnime(movieDetail);

                        break;
                    case "streaming":

                        onLoadStreamStream(movieDetail);

                        break;
                }
            }


        };


        facebookInterstitialAd.loadAd(
                facebookInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }

    private void onLoadAdmobRewardAds(Media movieDetail, String s) {

        if (mReward == null) {
            Toast.makeText(context, "The rewarded ad wasn't ready yet", Toast.LENGTH_SHORT).show();
            return;
        }

        mReward.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        //
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mReward = null;
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mReward = null;
                        // Preload the next rewarded ad.
                        loadRewardedAd();
                    }
                });

        mReward.show((BaseActivity) context, rewardItem -> {

            switch (s) {
                case "0":

                    onLoadStreamMovie(movieDetail);

                    break;
                case "1":

                    onLoadStreamSerie(movieDetail);


                    break;
                case "2":

                    onLoadStreamAnime(movieDetail);

                    break;
                default:

                    onLoadStreamStream(movieDetail);

                    break;
            }

        });
    }


    private void loadRewardedAd() {

        if (mReward == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(context,
                    settingsManager.getSettings().getAdUnitIdRewarded(),
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            mReward = null;

                            isLoading = false;

                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {

                            isLoading = false;
                            mReward = rewardedAd;
                        }
                    });
        }

    }


    private void onLoadUnityAds(Media movieDetail, String type) {

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

                    if ("0".equals(type)) {
                        onLoadStreamMovie(movieDetail);
                    } else if ("1".equals(type)) {
                        onLoadStreamSerie(movieDetail);
                    } else if ("2".equals(type)) {
                        onLoadStreamAnime(movieDetail);
                    } else if ("streaming".equals(type)) {
                        onLoadStreamStream(movieDetail);
                    }

                }
            });
        }
    }


        private void onLoadCastStream(Media movieDetail, String downloadUrl) {

            MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
            movieMetadata.putString(MediaMetadata.KEY_TITLE, movieDetail.getTitle());
            movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, movieDetail.getTitle());

            movieMetadata.addImage(new WebImage(Uri.parse(movieDetail.getPosterPath())));


            MediaInfo mediaInfo = new MediaInfo.Builder(downloadUrl)
                    .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                    .setMetadata(movieMetadata)
                    .build();

            CastSession castSession =
                    CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
            if (castSession == null || !castSession.isConnected()) {

                return;
            }
            final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
            if (remoteMediaClient == null) {
                return;
            }


            final QueueDataProvider provider = QueueDataProvider.getInstance(context);
            PopupMenu popup = new PopupMenu(context, binding.rootLayout);
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

        private void onLoadMainPlayerStreamYoutubeStream(Media movieDetail, String downloadUrl) {

        String artwork = movieDetail.getPosterPath();
        String name = movieDetail.getName();
        String streaming = "streaming";
        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(movieDetail.getId(), null, null, streaming, name, downloadUrl, artwork, null
                , null, null, null,
                null, null,
                null,
                null, null, movieDetail.getVip(), movieDetail.getHls(), null, null,
                null, 0, 0, null, null, 0));
        intent.putExtra(ARG_MOVIE, movieDetail);
        context.startActivity(intent);

    }


   }


}
