package com.beeecorptv.ui.viewmodels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.beeecorptv.data.model.MovieResponse;
import com.beeecorptv.data.model.ads.Ads;
import com.beeecorptv.data.model.media.StatusFav;
import com.beeecorptv.data.model.settings.Settings;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.data.repository.SettingsRepository;
import com.beeecorptv.ui.manager.SettingsManager;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve App Settings
 *
 * @author Yobex.
 */
public class SettingsViewModel extends ViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SettingsRepository settingsRepository;
    private final MediaRepository mediaRepository;
    public final MutableLiveData<Settings> settingsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Ads> adsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> plansMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<com.easyplex.easyplexsupportedhosts.Sites.Status> cueMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Settings> installMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<StatusFav> appPasswordMutableLiveData = new MutableLiveData<>();


    @Inject
    SettingsManager settingsManager;


    @Inject
    SettingsViewModel(SettingsRepository settingsRepository,MediaRepository mediaRepository) {
        this.settingsRepository = settingsRepository;
        this.mediaRepository = mediaRepository;

    }



    public  void getPlans(){

        // Fetch Plans Details
        compositeDisposable.add(settingsRepository.getPlans()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(plansMutableLiveData::postValue, this::handleError));
    }



    public void getSettingsDetails() {

        // Fetch Settings Details
        compositeDisposable.add(settingsRepository.getSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(settingsMutableLiveData::postValue, this::handleError));

        // Fetch Plans Details
        compositeDisposable.add(settingsRepository.getPlans()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(plansMutableLiveData::postValue, this::handleError));

        // Fetch Ads Details
        compositeDisposable.add(settingsRepository.getAdsSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(adsMutableLiveData::setValue, this::handleError));

        compositeDisposable.add(mediaRepository.getCuePoint()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(cueMutableLiveData::setValue, this::handleError));

    }



    public void getInstalls() {

        compositeDisposable.add(settingsRepository.getInstalls()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(installMutableLiveData::postValue, this::handleError));

    }



    public void getAppPasswordCheck(String password) {

        compositeDisposable.add(settingsRepository.getAppPasswordCheck(password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(appPasswordMutableLiveData::postValue, this::handleError));

    }

        // Handle Errors
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    public void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
