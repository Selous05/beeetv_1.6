package com.beeecorptv.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.beeecorptv.R;
import com.beeecorptv.data.model.networks.Network;
import com.beeecorptv.databinding.LayoutGenresBinding;
import com.beeecorptv.di.Injectable;
import com.beeecorptv.ui.viewmodels.NetworksViewModel;
import com.beeecorptv.util.ItemAnimation;
import com.beeecorptv.util.SpacingItemDecoration;
import com.beeecorptv.util.Tools;

import javax.inject.Inject;


public class NetworksFragment extends Fragment implements Injectable {

    LayoutGenresBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private NetworksViewModel networksViewModel;
    ItemAdapter adapter;
    private static final int ANIMATION_TYPE = ItemAnimation.FADE_IN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_genres, container, false);

        networksViewModel = new ViewModelProvider(this, viewModelFactory).get(NetworksViewModel.class);

        binding.filterBtnAllgenres.setVisibility(View.GONE);

        adapter = new ItemAdapter(getContext(), ANIMATION_TYPE);

        onLoadGenres();

        return binding.getRoot();


    }



    // Load Genres
    private void onLoadGenres() {

        networksViewModel.getNetworks();
        networksViewModel.networkMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

            binding.filterBtn.setOnClickListener(v -> binding.planetsSpinner.performClick());

            if (!movieDetail.getNetworks().isEmpty()) {

                binding.noMoviesFound.setVisibility(View.GONE);


                binding.planetsSpinner.setItem(movieDetail.getNetworks());
                binding.planetsSpinner.setSelection(0,true);
                binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                        binding.planetsSpinner.setVisibility(View.GONE);
                        binding.filterBtn.setVisibility(View.VISIBLE);
                        Network network = (Network) adapterView.getItemAtPosition(position);
                        binding.selectedGenre.setText(network.getName());

                        networksViewModel.searchQuery.setValue(String.valueOf(network.getId()));
                        networksViewModel.getGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

                            if (genresList !=null) {

                                binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                                binding.recyclerView.setItemViewCacheSize(12);
                                adapter.submitList(genresList);
                                binding.recyclerView.setAdapter(adapter);

                            }

                        });



                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                        // Nothting to refresh when no Item Selected

                    }
                });


            }else {


                binding.noMoviesFound.setVisibility(View.VISIBLE);

            }


        });



    }


    // On Fragment Detach clear binding views & adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding = null;
    }

}
