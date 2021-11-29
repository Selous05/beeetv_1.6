package com.beeecorptv.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easyplex.easyplexsupportedhosts.Utils.Uti;
import com.beeecorptv.data.model.MovieResponse;
import com.beeecorptv.data.model.genres.GenresByID;
import com.beeecorptv.data.model.suggestions.Suggest;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.ui.manager.SettingsManager;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


/**
 * ViewModel to cache, retrieve data for HomeFragment
 *
 * @author Yobex.
 */

public class HomeViewModel extends ViewModel {

    private final MediaRepository mediaRepository;
    private final SettingsManager settingsManager;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<MovieResponse>  movieChoosedMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  movieRecommendedMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  movieTrendingMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  movieLatestMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  popularSeriesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> latestSeriesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> latestAnimesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> thisweekMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> popularMoviesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> featuredMoviesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Uti> paramsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Suggest> suggestMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> latestEpisodesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> latestMoviesSeriesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> pinnedMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> popularCastersMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<GenresByID> genresMutableLiveData = new MutableLiveData<>();


    @Inject
    HomeViewModel(MediaRepository mediaRepository,SettingsManager settingsManager) {
        this.mediaRepository = mediaRepository;
        this.settingsManager = settingsManager;
    }




    public void featured() {

        compositeDisposable.add(mediaRepository.getMoviesGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(genresMutableLiveData::postValue, this::handleError)
        );

        compositeDisposable.add(mediaRepository.getPopularCasters()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(popularCastersMutableLiveData::postValue, this::handleError)
        );

        compositeDisposable.add(mediaRepository.getFeatured()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(featuredMoviesMutableLiveData::postValue, this::handleError)

        );

        compositeDisposable.add(mediaRepository.getPinned()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(pinnedMutableLiveData::postValue, this::handleError)
          );



          compositeDisposable.add(mediaRepository.getLatestMoviesSeries()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(latestMoviesSeriesMutableLiveData::postValue, this::handleError)
            );

            compositeDisposable.add(mediaRepository.getLatestEpisodes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(latestEpisodesMutableLiveData::postValue, this::handleError)
            );




            compositeDisposable.add(mediaRepository.getRecommended()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(movieRecommendedMutableLiveData::postValue, this::handleError)
            );



            compositeDisposable.add(mediaRepository.getTrending()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(movieTrendingMutableLiveData::postValue, this::handleError)
            );

            compositeDisposable.add(mediaRepository.getPopularMovies()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(popularMoviesMutableLiveData::postValue, this::handleError)
            );

            compositeDisposable.add(mediaRepository.getChoosed()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(movieChoosedMutableLiveData::postValue, this::handleError)
            );





            compositeDisposable.add(mediaRepository.getThisWeek()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(thisweekMutableLiveData::postValue, this::handleError)
            );



            compositeDisposable.add(mediaRepository.getAnimes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(latestAnimesMutableLiveData::postValue, this::handleError)
            );




            compositeDisposable.add(mediaRepository.getLatestSeries()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(latestSeriesMutableLiveData::postValue, this::handleError)
            );

            compositeDisposable.add(mediaRepository.getPopularSeries()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(popularSeriesMutableLiveData::postValue, this::handleError)
            );


            compositeDisposable.add(mediaRepository.getLatestMovies()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(movieLatestMutableLiveData::postValue, this::handleError)
            );

    }


    public void getLatestParams(String title, String message) {
        compositeDisposable.add(mediaRepository.getParams(title,message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(paramsMutableLiveData::postValue, this::handleError)
        );
    }




    public void sendSuggestion (String title,String message) {
        compositeDisposable.add(mediaRepository.getSuggest(settingsManager.getSettings().getApiKey(),title,message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(suggestMutableLiveData::postValue, this::handleError)
        );
    }



    // HandleError
    private void handleError(Throwable e) {

        Timber.i("In onError()%s", e.getMessage()
         + " - " + e.getCause());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();

    }


}
