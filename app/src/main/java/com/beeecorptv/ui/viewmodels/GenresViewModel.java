package com.beeecorptv.ui.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import com.beeecorptv.data.datasource.anime.AnimeDataSourceFactory;
import com.beeecorptv.data.datasource.anime.AnimeLatestDataSourceFactory;
import com.beeecorptv.data.datasource.genreslist.AnimesGenresListDataSourceFactory;
import com.beeecorptv.data.datasource.genreslist.ByGenreListDataSource;
import com.beeecorptv.data.datasource.genreslist.ByGenresListDataSourceFactory;
import com.beeecorptv.data.datasource.genreslist.MoviesGenresListDataSourceFactory;
import com.beeecorptv.data.datasource.genreslist.SeriesGenresListDataSourceFactory;
import com.beeecorptv.data.datasource.movie.MovieLatestDataSourceFactory;
import com.beeecorptv.data.datasource.movie.MovieRatingDataSourceFactory;
import com.beeecorptv.data.datasource.movie.MovieViewsDataSourceFactory;
import com.beeecorptv.data.datasource.movie.MovieYearDataSourceFactory;
import com.beeecorptv.data.datasource.series.SerieDataSourceFactory;
import com.beeecorptv.data.datasource.series.SerieLatestDataSourceFactory;
import com.beeecorptv.data.datasource.series.SerieRatingDataSourceFactory;
import com.beeecorptv.data.datasource.series.SerieViewsDataSourceFactory;
import com.beeecorptv.data.datasource.series.SerieYearsDataSourceFactory;
import com.beeecorptv.data.datasource.movie.MovieDataSourceFactory;
import com.beeecorptv.data.datasource.series.SerieDataSource;
import com.beeecorptv.data.datasource.anime.AnimeRatingDataSourceFactory;
import com.beeecorptv.data.datasource.anime.AnimeViewsDataSourceFactory;
import com.beeecorptv.data.datasource.anime.AnimeYearsDataSourceFactory;
import com.beeecorptv.data.datasource.stream.StreamDataSource;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.GenresByID;
import com.beeecorptv.data.remote.ApiInterface;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.ui.manager.SettingsManager;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MoviesFragment & SeriesFragment
 *
 * @author Yobex.
 */
public class GenresViewModel extends ViewModel {

    private final MediaRepository mediaRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<GenresByID> movieDetailMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    public final MutableLiveData<String> type = new MutableLiveData<>();
    public final LiveData<PagedList<Media>> itemPagedList;
    public final LiveData<PagedList<Media>> moviesLastestPagedList;
    public final LiveData<PagedList<Media>> moviesRatingPagedList;
    public final LiveData<PagedList<Media>> moviesViewsgPagedList;
    public final LiveData<PagedList<Media>> seriePagedList;
    public final LiveData<PagedList<Media>> serieRatingPagedList;
    public final LiveData<PagedList<Media>> serieYearPagedList;
    public final  LiveData<PagedList<Media>> serieViewsgPagedList;
    public final LiveData<PagedList<Media>> serieLatestPagedList;
    public final LiveData<PagedList<Media>> animePagedList;
    public final LiveData<PagedList<Media>> moviePagedList;
    public final LiveData<PagedList<Media>> moviesyearPagedList;
    public final LiveData<PageKeyedDataSource<Integer, Media>> liveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> moviesRatingliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> moviesviewsliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> moviesyeargliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> movieslatestliveDataSource;


    // Animes
    public final LiveData<PageKeyedDataSource<Integer, Media>> animeLatestliveDataSource;

    public final LiveData<PageKeyedDataSource<Integer, Media>> animeliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> animeRatingliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> animeYearliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> animeViewsliveDataSource;
    public final LiveData<PagedList<Media>> animeRatingPagedList;
    public final LiveData<PagedList<Media>> animeLatestPagedList;
    public final LiveData<PagedList<Media>> animeYearPagedList;
    public final LiveData<PagedList<Media>> animeViewsgPagedList;



    // Series
    public final LiveData<PageKeyedDataSource<Integer, Media>> serieliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> serieRatingliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> serieYearliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> serieViewsliveDataSource;
    public final LiveData<PageKeyedDataSource<Integer, Media>> serieLatestliveDataSource;


    @Inject
    GenresViewModel(MediaRepository mediaRepository,ApiInterface requestInterface,SettingsManager settingsManager) {
        this.mediaRepository = mediaRepository;


        MovieViewsDataSourceFactory movieViewsDataSourceFactory = new MovieViewsDataSourceFactory(requestInterface,settingsManager);
        moviesviewsliveDataSource = movieViewsDataSourceFactory.getItemLiveDataSource();



        MovieRatingDataSourceFactory movieRatingDataSourceFactory = new MovieRatingDataSourceFactory(requestInterface,settingsManager);
        moviesRatingliveDataSource = movieRatingDataSourceFactory.getItemLiveDataSource();


        MovieLatestDataSourceFactory movieLatestDataSourceFactory = new MovieLatestDataSourceFactory(requestInterface,settingsManager);
        movieslatestliveDataSource = movieLatestDataSourceFactory.getItemLiveDataSource();


        MovieYearDataSourceFactory movieYearDataSourceFactory = new MovieYearDataSourceFactory(requestInterface,settingsManager);
        moviesyeargliveDataSource = movieYearDataSourceFactory.getItemLiveDataSource();


        MovieDataSourceFactory movieDataSourceFactory = new MovieDataSourceFactory(requestInterface,settingsManager);
        liveDataSource = movieDataSourceFactory.getItemLiveDataSource();


        SerieLatestDataSourceFactory serieLatestDataSourceFactory = new SerieLatestDataSourceFactory(requestInterface,settingsManager);
        serieLatestliveDataSource = serieLatestDataSourceFactory.getItemLiveDataSource();

        SerieDataSourceFactory serieDataSourceFactory = new SerieDataSourceFactory(requestInterface,settingsManager);
        serieliveDataSource = serieDataSourceFactory.getItemLiveDataSource2();

        SerieRatingDataSourceFactory serieRatingDataSourceFactory = new SerieRatingDataSourceFactory(requestInterface,settingsManager);
        serieRatingliveDataSource = serieRatingDataSourceFactory.getItemLiveDataSource();

        SerieYearsDataSourceFactory serieYearsDataSourceFactory = new SerieYearsDataSourceFactory(requestInterface,settingsManager);
        serieYearliveDataSource = serieYearsDataSourceFactory.getItemLiveDataSource();

        SerieViewsDataSourceFactory serieViewsDataSourceFactory = new SerieViewsDataSourceFactory(requestInterface,settingsManager);
        serieViewsliveDataSource = serieViewsDataSourceFactory.getItemLiveDataSource();


        AnimeDataSourceFactory animeDataSourceFactory = new AnimeDataSourceFactory(requestInterface,settingsManager);
        animeliveDataSource = animeDataSourceFactory.getItemLiveDataSource3();

        AnimeRatingDataSourceFactory animeRatingDataSourceFactory = new AnimeRatingDataSourceFactory(requestInterface,settingsManager);
        animeRatingliveDataSource = animeRatingDataSourceFactory.getItemLiveDataSource();

        AnimeLatestDataSourceFactory animeLatestDataSourceFactory = new AnimeLatestDataSourceFactory(requestInterface,settingsManager);
        animeLatestliveDataSource = animeLatestDataSourceFactory.getItemLiveDataSource();

        AnimeYearsDataSourceFactory animeYearsDataSourceFactory = new AnimeYearsDataSourceFactory(requestInterface,settingsManager);
        animeYearliveDataSource = animeYearsDataSourceFactory.getItemLiveDataSource();


        AnimeViewsDataSourceFactory animeViewsDataSourceFactory = new AnimeViewsDataSourceFactory(requestInterface,settingsManager);
        animeViewsliveDataSource = animeViewsDataSourceFactory.getItemLiveDataSource();

        PagedList.Config globalConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(StreamDataSource.PAGE_SIZE)
                        .setPrefetchDistance(StreamDataSource.PAGE_SIZE)
                        .setInitialLoadSizeHint(StreamDataSource.PAGE_SIZE)
                        .build();

        PagedList.Config serieCongig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(SerieDataSource.PAGE_SIZE)
                        .setPrefetchDistance(SerieDataSource.PAGE_SIZE)
                        .setInitialLoadSizeHint(SerieDataSource.PAGE_SIZE)
                        .build();



        itemPagedList = (new LivePagedListBuilder(movieDataSourceFactory, globalConfig)).build();

        seriePagedList = (new LivePagedListBuilder(serieDataSourceFactory, serieCongig)).build();

        serieRatingPagedList = (new LivePagedListBuilder(serieRatingDataSourceFactory, serieCongig)).build();

        serieLatestPagedList = (new LivePagedListBuilder(serieLatestDataSourceFactory, serieCongig)).build();

        serieYearPagedList = (new LivePagedListBuilder(serieYearsDataSourceFactory, serieCongig)).build();

        serieViewsgPagedList = (new LivePagedListBuilder(serieViewsDataSourceFactory, serieCongig)).build();

        animePagedList = (new LivePagedListBuilder(animeDataSourceFactory, serieCongig)).build();

        animeRatingPagedList = (new LivePagedListBuilder(animeRatingDataSourceFactory, serieCongig)).build();

        animeLatestPagedList = (new LivePagedListBuilder(animeLatestDataSourceFactory, serieCongig)).build();


        animeYearPagedList = (new LivePagedListBuilder(animeYearsDataSourceFactory, serieCongig)).build();

        animeViewsgPagedList = (new LivePagedListBuilder(animeViewsDataSourceFactory, serieCongig)).build();



        moviePagedList = (new LivePagedListBuilder(movieDataSourceFactory, serieCongig)).build();

        moviesLastestPagedList = (new LivePagedListBuilder(movieLatestDataSourceFactory, globalConfig)).build();

        moviesRatingPagedList = (new LivePagedListBuilder(movieRatingDataSourceFactory, globalConfig)).build();

        moviesViewsgPagedList = (new LivePagedListBuilder(movieViewsDataSourceFactory, globalConfig)).build();

        moviesyearPagedList = (new LivePagedListBuilder(movieYearDataSourceFactory, globalConfig)).build();


    }



    final PagedList.Config config =
            (new PagedList.Config.Builder())
                    .setEnablePlaceholders(false)
                    .setPageSize(StreamDataSource.PAGE_SIZE)
                    .setPrefetchDistance(StreamDataSource.PAGE_SIZE)
                    .setInitialLoadSizeHint(StreamDataSource.PAGE_SIZE)
                    .build();


    final PagedList.Config byCongig =
            (new PagedList.Config.Builder())
                    .setEnablePlaceholders(false)
                    .setPageSize(ByGenreListDataSource.PAGE_SIZE)
                    .setPrefetchDistance(ByGenreListDataSource.PAGE_SIZE)
                    .setInitialLoadSizeHint(ByGenreListDataSource.PAGE_SIZE)
                    .build();

    public LiveData<PagedList<Media>> getGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            MoviesGenresListDataSourceFactory factory = mediaRepository.genresListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, config).build();
        });
    }


    public LiveData<PagedList<Media>> getByGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            ByGenresListDataSourceFactory factory = mediaRepository.byGenresListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, byCongig).build();
        });
    }

    public LiveData<PagedList<Media>> getSeriesGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            SeriesGenresListDataSourceFactory factory = mediaRepository.seriesGenresListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, config).build();
        });
    }


    public LiveData<PagedList<Media>> getAnimesGenresitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            AnimesGenresListDataSourceFactory factory = mediaRepository.animesGenresListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, config).build();
        });
    }




    // Fetch Movies Genres List
    public void getMoviesGenresList() {
        compositeDisposable.add(mediaRepository.getMoviesGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieDetailMutableLiveData::postValue, this::handleError)
        );
    }




    // Handle Errors
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
