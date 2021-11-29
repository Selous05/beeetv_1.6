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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.beeecorptv.R;
import com.beeecorptv.data.model.genres.Genre;
import com.beeecorptv.databinding.LayoutGenresBinding;
import com.beeecorptv.di.Injectable;
import com.beeecorptv.ui.viewmodels.GenresViewModel;
import com.beeecorptv.util.ItemAnimation;
import com.beeecorptv.util.SpacingItemDecoration;
import com.beeecorptv.util.Tools;
import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;


public class MoviesFragment extends Fragment implements Injectable {

    LayoutGenresBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private GenresViewModel genresViewModel;
    ItemAdapter adapter;
    private List<String> provinceList;
    private static final int ANIMATION_TYPE = ItemAnimation.FADE_IN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_genres, container, false);

        genresViewModel = new ViewModelProvider(this, viewModelFactory).get(GenresViewModel.class);

        adapter = new ItemAdapter(getContext(), ANIMATION_TYPE);

        onLoadAllGenres();
        onLoadGenres();

        return binding.getRoot();


    }



    // Load Genres
    private void onLoadGenres() {

        genresViewModel.getMoviesGenresList();
        genresViewModel.movieDetailMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

            binding.filterBtn.setOnClickListener(v -> binding.planetsSpinner.performClick());

            if (!movieDetail.getGenresPlayer().isEmpty()) {

                binding.noMoviesFound.setVisibility(View.GONE);


                binding.planetsSpinner.setItem(movieDetail.getGenresPlayer());
                binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                        binding.planetsSpinner.setVisibility(View.GONE);
                        binding.filterBtn.setVisibility(View.VISIBLE);

                        Genre genre = (Genre) adapterView.getItemAtPosition(position);
                        int genreId = genre.getId();
                        String genreName = genre.getName();
                        binding.selectedGenre.setText(genreName);

                        genresViewModel.searchQuery.setValue(String.valueOf(genreId));
                        genresViewModel.getGenresitemPagedList().observe(getViewLifecycleOwner(), genresList -> {

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



    private void onLoadAllGenres() {

        provinceList = new ArrayList<>();
        provinceList.add(getString(R.string.all_genres));
        provinceList.add(getString(R.string.latest_added));
        provinceList.add(getString(R.string.by_rating));
        provinceList.add(getString(R.string.by_year));
        provinceList.add(getString(R.string.by_views));


        binding.filterBtnAllgenres.setOnClickListener(v -> binding.planetsSpinnerSort.performClick());

        binding.noMoviesFound.setVisibility(View.GONE);

        binding.planetsSpinnerSort.setItem(provinceList);
        binding.planetsSpinnerSort.setSelection(0);
        binding.planetsSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 0:
                        String all = provinceList.get(0);
                        binding.selectedGenreLeft.setText(all);

                        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                        binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                        genresViewModel.moviePagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));
                        binding.recyclerView.setAdapter(adapter);

                        break;
                    case 1:

                        String latest = provinceList.get(1);
                        binding.selectedGenreLeft.setText(latest);

                        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                        binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                        genresViewModel.moviesLastestPagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));
                        binding.recyclerView.setAdapter(adapter);

                        break;
                    case 2:

                        String rating = provinceList.get(2);
                        binding.selectedGenreLeft.setText(rating);

                        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                        binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                        genresViewModel.moviesRatingPagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));
                        binding.recyclerView.setAdapter(adapter);

                        break;
                    case 3:

                        String year = provinceList.get(3);
                        binding.selectedGenreLeft.setText(year);

                        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                        binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

                        genresViewModel.moviesyearPagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));


                        binding.recyclerView.setAdapter(adapter);


                        break;
                    case 4:

                        String views = provinceList.get(4);
                        binding.selectedGenreLeft.setText(views);


                        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                        binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

                        genresViewModel.moviesViewsgPagedList.observe(getViewLifecycleOwner(), favoriteMovies -> adapter.submitList(favoriteMovies));


                        binding.recyclerView.setAdapter(adapter);

                        break;
                    default:
                        break;
                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                // Nothting to refresh when no Item Selected

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
