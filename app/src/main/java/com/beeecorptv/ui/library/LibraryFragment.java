package com.beeecorptv.ui.library;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.beeecorptv.data.repository.SettingsRepository;
import com.beeecorptv.di.Injectable;
import com.beeecorptv.ui.manager.SettingsManager;
import com.google.android.material.tabs.TabLayout;
import com.beeecorptv.R;
import com.beeecorptv.databinding.BrowseFragmentBinding;
import com.beeecorptv.ui.base.BaseActivity;
import com.beeecorptv.util.Tools;
import com.google.android.material.tabs.TabLayoutMediator;

import javax.inject.Inject;

public class LibraryFragment extends Fragment implements Injectable {

    BrowseFragmentBinding binding;


    @Inject
    SettingsRepository authRepository;

    @Inject
    SettingsManager settingsManager;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.browse_fragment, container, false);

        onLoadToolbar();
        onLoadAppLogo();
        onSetupTabs();

        setHasOptionsMenu(true);

        return binding.getRoot();


    }


    @SuppressLint("ResourceAsColor")
    private void onSetupTabs() {

        setupViewPager(binding.viewPager);


        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {

                    if(position == 0)
                        tab.setText(getResources().getString(R.string.movies));
                    else if (position == 1) {
                        tab.setText(getResources().getString(R.string.series));
                    } else if (position == 2){

                     if (settingsManager.getSettings().getAnime() == 1){

                         tab.setText(getResources().getString(R.string.animes));

                     }else if (settingsManager.getSettings().getNetworks() == 1){

                         tab.setText(getResources().getString(R.string.networks));
                     }
                    } else if (position == 3){

                        tab.setText(getResources().getString(R.string.networks));

                    }
                }).attach();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                // on Tab Selected


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                // on Tab UnSelected

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


                // on Tab ReSelected

            }
        });

    }


    // Load Logo
    private void onLoadAppLogo() {

        Tools.loadMiniLogo(getActivity(),binding.logoImageTop);

    }

    private void onLoadToolbar() {

        Tools.loadToolbar(((AppCompatActivity) requireActivity()),binding.toolbar,null);

    }

    private void setupViewPager(ViewPager2 viewPager) {

        SectionsPagerAdapter viewPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(),getLifecycle());
        viewPagerAdapter.addFragment(new MoviesFragment());
        viewPagerAdapter.addFragment(new SeriesFragment());

        if (settingsManager.getSettings().getAnime() == 1) {

            viewPagerAdapter.addFragment(new AnimesFragment());
        }

        if (settingsManager.getSettings().getNetworks() == 1) {

            if (settingsManager.getSettings().getDefault_layout_networks().equals("Layout1")){

                viewPagerAdapter.addFragment(new NetworksFragment2());

            }else {

                viewPagerAdapter.addFragment(new NetworksFragment());
            }

        }
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        viewPagerAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getActivity(), BaseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;


        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.viewPager.setSaveFromParentEnabled(true);
        binding.viewPager.setAdapter(null);
        binding = null;

    }

}
