package com.beeecorptv.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.beeecorptv.ui.viewmodels.AnimeViewModel;
import com.beeecorptv.ui.viewmodels.CastersViewModel;
import com.beeecorptv.ui.viewmodels.HomeViewModel;
import com.beeecorptv.ui.viewmodels.GenresViewModel;
import com.beeecorptv.ui.viewmodels.LoginViewModel;
import com.beeecorptv.ui.viewmodels.MovieDetailViewModel;
import com.beeecorptv.ui.viewmodels.MoviesListViewModel;
import com.beeecorptv.ui.viewmodels.NetworksViewModel;
import com.beeecorptv.ui.viewmodels.PlayerViewModel;
import com.beeecorptv.ui.viewmodels.RegisterViewModel;
import com.beeecorptv.ui.viewmodels.SearchViewModel;
import com.beeecorptv.ui.viewmodels.SerieDetailViewModel;
import com.beeecorptv.ui.viewmodels.SettingsViewModel;
import com.beeecorptv.ui.viewmodels.StreamingDetailViewModel;
import com.beeecorptv.ui.viewmodels.StreamingGenresViewModel;
import com.beeecorptv.ui.viewmodels.UpcomingViewModel;
import com.beeecorptv.viewmodel.MoviesViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/*
 * @author Yobex.
 * */
@Module
public abstract class ViewModelModule {


    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UpcomingViewModel.class)
    abstract ViewModel bindUpcomingViewModel(UpcomingViewModel upcomingViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailViewModel.class)
    abstract ViewModel bindMovieDetailViewModel(MovieDetailViewModel movieDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SerieDetailViewModel.class)
    abstract ViewModel bindSerieDetailViewModel(SerieDetailViewModel serieDetailViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel.class)
    abstract ViewModel bindRegisterViewModel(RegisterViewModel registerViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(GenresViewModel.class)
    abstract ViewModel bindGenresViewModel(GenresViewModel genresViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel.class)
    abstract ViewModel bindSettingsViewModel(SettingsViewModel settingsViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(MoviesListViewModel.class)
    abstract ViewModel bindMoviesListViewModel(MoviesListViewModel moviesListViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(AnimeViewModel.class)
    abstract ViewModel bindAnimeViewModel(AnimeViewModel animeViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(StreamingDetailViewModel.class)
    abstract ViewModel bindStreamingDetailViewModel(StreamingDetailViewModel streamingDetailViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(StreamingGenresViewModel.class)
    abstract ViewModel bindStreamingGenresViewModel(StreamingGenresViewModel streamingGenresViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel.class)
    abstract ViewModel bindStreamingPlayerViewModel(PlayerViewModel playerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CastersViewModel.class)
    abstract ViewModel bindCastersViewModel(CastersViewModel castersViewModel);


    @Binds
    @IntoMap
    @ViewModelKey(NetworksViewModel.class)
    abstract ViewModel bindNetworksViewModel(NetworksViewModel networksViewModel);


    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MoviesViewModelFactory factory);


}
