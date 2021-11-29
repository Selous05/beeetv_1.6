package com.beeecorptv.data.datasource.genreslist;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.beeecorptv.data.model.episode.LatestEpisodes;
import com.beeecorptv.ui.manager.SettingsManager;

public class ByEpisodesDataSourceFactory extends DataSource.Factory<Integer, LatestEpisodes> {

    private final  SettingsManager settingsManager;
    private final String query;

    public ByEpisodesDataSourceFactory(String query, SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
        this.query = query;
    }

    @NonNull
    @Override
    public DataSource<Integer, LatestEpisodes> create() {
        return new ByEpisodeDataSource(query,settingsManager);
    }
}
