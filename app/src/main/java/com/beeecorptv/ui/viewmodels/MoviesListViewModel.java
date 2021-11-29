package com.beeecorptv.ui.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.beeecorptv.data.local.entity.Animes;
import com.beeecorptv.data.local.entity.Download;
import com.beeecorptv.data.local.entity.History;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.local.entity.Series;
import com.beeecorptv.data.repository.MediaRepository;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MyList
 *
 */
public class MoviesListViewModel extends ViewModel {


    private final MediaRepository mediaRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final LiveData<List<Media>> favoriteMoviesLiveData;
    private final LiveData<List<Series>> favoriteSeriesLiveData;
    private final LiveData<List<Animes>> favoriteAnimesLiveData;
    private final LiveData<List<Download>> downloadMoviesLiveData;
    private final LiveData<List<History>> historyWatchLiveData;

    public int tmdb;

    @Inject
    MoviesListViewModel(MediaRepository mediaRepository) {

        this.mediaRepository = mediaRepository;


        // Get a list of Favorite Movies from the Database
        favoriteMoviesLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getFavorites()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));


        favoriteSeriesLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getFavoritesSeries()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));



        favoriteAnimesLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getFavoritesAnimes()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));



        // Get a list of Favorite Movies from the Database
        downloadMoviesLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getDownloads()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));



        // Get a list of Favorite Movies from the Database
        historyWatchLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getwatchHistory()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));



        // Get a list of Favorite Movies from the Database
    }



    // Return Movies & Series & Animes in MyList
    public LiveData<List<Media>> getMoviesFavorites() {
        return favoriteMoviesLiveData;
    }


    public LiveData<List<Series>> getSeriesFavorites() {
        return favoriteSeriesLiveData;
    }


    public LiveData<List<Animes>> getAnimesFavorites() {
        return favoriteAnimesLiveData;
    }



    // Return Movies & Series & Animes in MyList
    public LiveData<List<Download>> getMoviesDownloads() {
        return downloadMoviesLiveData;
    }





    // Return Movies & Series & Animes in MyList
    public LiveData<List<History>> getHistoryWatch() {
        return historyWatchLiveData;
    }


    // Delete All Movies from MyList
    public void deleteAllMovies() {
        Timber.i("MyList has been cleared...");
        compositeDisposable.add(Completable.fromAction(mediaRepository::deleteAllFromFavorites)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    // Delete All Movies from MyList
    public void deleteHistory() {
        Timber.i("History has been cleared...");

        compositeDisposable.add(Completable.fromAction(mediaRepository::deleteAllHistory)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }



    public void deleteResume() {
        Timber.i("Resume has been cleared...");

        compositeDisposable.add(Completable.fromAction(mediaRepository::deleteAllResume)
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
