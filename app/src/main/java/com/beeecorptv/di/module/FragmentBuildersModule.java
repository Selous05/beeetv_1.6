package com.beeecorptv.di.module;

import com.beeecorptv.ui.downloadmanager.ui.main.DownloadsFragment;
import com.beeecorptv.ui.downloadmanager.ui.main.FinishedDownloadsFragment;
import com.beeecorptv.ui.downloadmanager.ui.main.QueuedDownloadsFragment;
import com.beeecorptv.ui.home.HomeFragment;
import com.beeecorptv.ui.library.AnimesFragment;
import com.beeecorptv.ui.library.LibraryFragment;
import com.beeecorptv.ui.library.MoviesFragment;
import com.beeecorptv.ui.library.NetworksFragment;
import com.beeecorptv.ui.library.NetworksFragment2;
import com.beeecorptv.ui.library.SeriesFragment;
import com.beeecorptv.ui.mylist.AnimesListFragment;
import com.beeecorptv.ui.mylist.ListFragment;
import com.beeecorptv.ui.mylist.MoviesListFragment;
import com.beeecorptv.ui.mylist.SeriesListFragment;
import com.beeecorptv.ui.mylist.StreamingListFragment;
import com.beeecorptv.ui.search.DiscoverFragment;
import com.beeecorptv.ui.settings.SettingsActivity;
import com.beeecorptv.ui.streaming.StreamingFragment;
import com.beeecorptv.ui.upcoming.UpComingFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/*
 * @author Yobex.
 * */
@Module
public abstract class FragmentBuildersModule {




    @ContributesAndroidInjector
    abstract FinishedDownloadsFragment contributeFinishedDownloadsFragment();


    @ContributesAndroidInjector
    abstract QueuedDownloadsFragment contributeQueuedDownloadsFragment();

    @ContributesAndroidInjector
    abstract DownloadsFragment contributeDownloadsFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract UpComingFragment contributeUpcomingFragment();

    @ContributesAndroidInjector
    abstract DiscoverFragment contributeDiscoverFragment();

    @ContributesAndroidInjector
    abstract MoviesFragment contributeMoviesFragment();

    @ContributesAndroidInjector
    abstract SeriesFragment contributeSeriesFragment();

    @ContributesAndroidInjector
    abstract LibraryFragment contributeLibraryFragment();

    @ContributesAndroidInjector
    abstract MoviesListFragment contributeMyListMoviesFragment();

    @ContributesAndroidInjector
    abstract AnimesFragment contributeAnimesFragment();

    @ContributesAndroidInjector
    abstract StreamingFragment contributeLiveFragment();

    @ContributesAndroidInjector
    abstract SettingsActivity contributeSettingsFragment();

    @ContributesAndroidInjector
    abstract ListFragment contributeListFragment();

    @ContributesAndroidInjector
    abstract SeriesListFragment contributeSeriesListFragment();

    @ContributesAndroidInjector
    abstract AnimesListFragment contributeAnimesListFragment();


    @ContributesAndroidInjector
    abstract NetworksFragment contributeNetworksFragment();

    @ContributesAndroidInjector
    abstract NetworksFragment2 contributeNetworksFragment2();

    @ContributesAndroidInjector
    abstract StreamingListFragment contributeStreamingListFragment();

}
