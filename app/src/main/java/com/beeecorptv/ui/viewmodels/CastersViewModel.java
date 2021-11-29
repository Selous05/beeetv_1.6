package com.beeecorptv.ui.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import com.beeecorptv.data.datasource.filmographie.CastersListDataSourceFactory;
import com.beeecorptv.data.datasource.genreslist.ByGenreListDataSource;
import com.beeecorptv.data.model.credits.Cast;
import com.beeecorptv.data.model.genres.GenresByID;
import com.beeecorptv.data.remote.ApiInterface;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.ui.manager.SettingsManager;
import javax.inject.Inject;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MoviesFragment & SeriesFragment
 *
 * @author Yobex.
 */
public class CastersViewModel extends ViewModel {

    private final MediaRepository mediaRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<GenresByID> movieDetailMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    public final MutableLiveData<String> type = new MutableLiveData<>();


    @Inject
    CastersViewModel(MediaRepository mediaRepository, ApiInterface requestInterface, SettingsManager settingsManager) {
        this.mediaRepository = mediaRepository;


    }

    final PagedList.Config config =
            (new PagedList.Config.Builder())
                    .setEnablePlaceholders(false)
                    .setPageSize(ByGenreListDataSource.PAGE_SIZE)
                    .setPrefetchDistance(ByGenreListDataSource.PAGE_SIZE)
                    .setInitialLoadSizeHint(ByGenreListDataSource.PAGE_SIZE)
                    .build();





    public LiveData<PagedList<Cast>> getByCastersitemPagedList() {
        return Transformations.switchMap(searchQuery, query -> {
            CastersListDataSourceFactory factory = mediaRepository.castersListDataSourceFactory(query);
            return new LivePagedListBuilder<>(factory, config).build();
        });
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
