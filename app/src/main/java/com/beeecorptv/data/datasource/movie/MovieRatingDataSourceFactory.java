package com.beeecorptv.data.datasource.movie;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.remote.ApiInterface;
import com.beeecorptv.ui.manager.SettingsManager;

import javax.inject.Inject;

public class MovieRatingDataSourceFactory extends DataSource.Factory {

    private final MutableLiveData<PageKeyedDataSource<Integer, Media>> itemLiveDataSource = new MutableLiveData<>();

    private final ApiInterface requestInterface;
    private final SettingsManager settingsManager;


    @Inject
    public MovieRatingDataSourceFactory(ApiInterface requestInterface,SettingsManager settingsManager) {
        this.requestInterface = requestInterface;
        this.settingsManager = settingsManager;
    }

    @Override
    public DataSource create() {

        MovieRatingDataSource movieRatingDataSource = new MovieRatingDataSource(requestInterface,settingsManager);
        itemLiveDataSource.postValue(movieRatingDataSource);


        return movieRatingDataSource;

    }

    public MutableLiveData<PageKeyedDataSource<Integer, Media>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }

}
