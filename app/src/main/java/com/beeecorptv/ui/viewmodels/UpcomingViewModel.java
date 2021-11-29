package com.beeecorptv.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.beeecorptv.data.model.MovieResponse;
import com.beeecorptv.data.model.upcoming.Upcoming;
import com.beeecorptv.data.remote.ApiInterface;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.ui.manager.SettingsManager;

import javax.inject.Inject;
import javax.inject.Named;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class UpcomingViewModel extends ViewModel {


    private final MediaRepository mediaRepository;
    private final SettingsManager settingsManager;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<Upcoming> upcomingMutableLiveData;
    public final MutableLiveData<MovieResponse> upcomingResponseMutableLive;


    @Inject
    @Named("status")
    ApiInterface requestStatusApi;

    @Inject
    UpcomingViewModel(MediaRepository mediaRepository,SettingsManager settingsManager) {
        this.mediaRepository = mediaRepository;
        this.settingsManager = settingsManager;
        upcomingMutableLiveData = new MutableLiveData<>();
        upcomingResponseMutableLive = new MutableLiveData<>();


    }



    // Fetch Upcoming Movies
    public void getUpcomingMovie() {
        compositeDisposable.add(mediaRepository.getUpcoming()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(upcomingResponseMutableLive::postValue, this::handleError)
        );
    }



    // Fetch Upcoming Movie Detail
    public void getUpcomingMovieDetail(int movieId) {

        compositeDisposable.add(mediaRepository.getUpcomingById(movieId,settingsManager.getSettings().getApiKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(upcomingMutableLiveData::postValue, this::handleError)
        );
    }


    // Handle Errors
    private void handleError(Throwable e) {
        e.printStackTrace();
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}
