package com.beeecorptv.data.datasource.anime;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import com.beeecorptv.data.model.episode.Episode;
import com.beeecorptv.ui.manager.SettingsManager;

public class AnimeSeasonsListDataSourceFactory extends DataSource.Factory<Integer, Episode> {

    private final String query;
    private final SettingsManager settingsManager;

    public AnimeSeasonsListDataSourceFactory(String query, SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
        this.query = query;
    }

    @NonNull
    @Override
    public DataSource<Integer, Episode> create() {
        return new AnimeSeasonsListDataSource(query, settingsManager);
    }
}
