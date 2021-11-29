package com.beeecorptv.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.beeecorptv.R;
import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.search.SearchResponse;
import com.beeecorptv.data.repository.AnimeRepository;
import com.beeecorptv.data.repository.MediaRepository;
import com.beeecorptv.databinding.FragmentSearchBinding;
import com.beeecorptv.di.Injectable;
import com.beeecorptv.ui.downloadmanager.core.utils.Utils;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.viewmodels.SearchViewModel;
import com.beeecorptv.util.SpacingItemDecoration;
import com.beeecorptv.util.Tools;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import timber.log.Timber;
public class  DiscoverFragment extends Fragment implements Injectable {


    FragmentSearchBinding binding;


    @Inject
    TokenManager tokenManager;

    @Inject
    SharedPreferences preferences;

    @Inject
    AuthManager authManager;

    @Inject
    SettingsManager settingsManager;

    @Inject
    MediaRepository mediaRepository;

    @Inject
    AnimeRepository animeRepository;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SearchViewModel searchViewModel;
    private SearchAdapter searchAdapter;
    private List<Media> searchMoviesList;
    private TextWatcher textWatcher;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        requireActivity().setTheme(Utils.getAppTheme( requireActivity()));

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        searchViewModel = new ViewModelProvider(this, viewModelFactory).get(SearchViewModel.class);

        searchViewModel.getSuggestedMovies();


        searchAdapter = new SearchAdapter();


        onToolbarLoad();
        setupSearchRecycleView();
        setupSuggestedMovies();
        setupSuggestedNames();
        setsearch();
        setHasOptionsMenu(true);


        binding.progressBar.setVisibility(View.GONE);
        binding.rvSearch.setVisibility(View.GONE);
        binding.linearMovies.setVisibility(View.GONE);
        binding.btClear.setVisibility(View.GONE);


        // Clear the results
        binding.btClear.setOnClickListener(view -> {

            binding.rvSearch.setVisibility(View.GONE);
            binding.etSearch.setText("");
            binding.rvSuggested.setVisibility(View.VISIBLE);
            binding.linearMovies.setVisibility(View.GONE);
            binding.linearSuggested.setVisibility(View.VISIBLE);
            binding.noResults.setVisibility(View.GONE);
            binding.btClear.setVisibility(View.GONE);
            searchViewModel.getSuggestedMovies();
            searchViewModel.movieDetailMutableLiveData.observe(getViewLifecycleOwner(), suggested -> searchAdapter.setSearch(suggested.getSuggested(),getContext()
                    ,settingsManager,mediaRepository,authManager,tokenManager,animeRepository));

        });


        binding.scrollView.setOnTouchListener((v, event) -> {
            hideKeyboard();
            return true;
        });



        return binding.getRoot();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (Tools.checkIfHasNetwork(requireActivity())) {
            setupSuggestedMovies();
        }
    }

    private void setupSuggestedNames() {
       //
    }


    // Return Suggested Movies and Series
    private void setupSuggestedMovies() {




        binding.rvSuggested.setAdapter(searchAdapter);
        binding.rvSuggested.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvSuggested.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        binding.rvSuggested.setHasFixedSize(true);
        binding.rvSuggested.setItemViewCacheSize(8);
        searchViewModel.movieDetailMutableLiveData.observe(getViewLifecycleOwner(), suggested -> {
            searchAdapter.setSearch(suggested.getSuggested(), DiscoverFragment.this.getContext()
                    , settingsManager, mediaRepository, authManager, tokenManager, animeRepository);
        });

    }


    public Observable<String> fromView(EditText searchView) {

        final PublishSubject<String> subject = PublishSubject.create();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence c, int i, int i1, int i2) {

                if (c.toString().trim().length() == 0) {

                    binding.btClear.setVisibility(View.GONE);

                }else {

                    binding.btClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {

                //


            }


            @Override
            public void afterTextChanged(Editable s) {


                if (s.toString().equals("") && searchMoviesList !=null) {

                    searchMoviesList.clear();
                    binding.noResults.setVisibility(View.VISIBLE);
                    binding.linearMovies.setVisibility(View.GONE);
                    binding.btClear.setVisibility(View.GONE);


                }

                if (s.toString().isEmpty()){


                    hideKeyboard();



                }else {

                    binding.progressBar.setVisibility(View.VISIBLE);
                    subject.onNext(s.toString());

                }


            }
        });

        return subject;
    }





    // Launch the search when the user finish the typing with a Debounce time of 700 MILLISECONDS
    @SuppressLint("SetTextI18n")
    void setsearch() {
        binding.linearMovies.setVisibility(View.GONE);
        compositeDisposable.add(fromView(binding.etSearch)
                .debounce(700, TimeUnit.MILLISECONDS)
                .filter(text -> text.length() > 0)
                .distinctUntilChanged()
                .switchMap((Function<String, ObservableSource<SearchResponse>>) query -> searchViewModel
                        .search(query,settingsManager.getSettings().getApiKey())
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchResponse -> {

                    binding.btClear.setVisibility(View.VISIBLE);



                    if (searchResponse.getSearch() != null && !searchResponse.getSearch().isEmpty()) {

                        binding.titlesResult.setText(getString(R.string.search_results) + "(" + searchResponse.getSearch().size() + ")");

                        searchMoviesList = searchResponse.getSearch();

                        binding.progressBar.setVisibility(View.GONE);
                        binding.rvSearch.setVisibility(View.VISIBLE);
                        binding.rvSuggested.setVisibility(View.GONE);
                        binding.linearSuggested.setVisibility(View.GONE);
                        binding.linearMovies.setVisibility(View.VISIBLE);
                        binding.scrollView.setVisibility(View.VISIBLE);


                        searchAdapter.setSearch(searchResponse.getSearch(),requireActivity(),
                                settingsManager,mediaRepository,authManager,tokenManager,animeRepository);
                        binding.noResults.setVisibility(View.GONE);

                    } else {


                        binding.progressBar.setVisibility(View.GONE);
                        binding.rvSearch.setVisibility(View.GONE);

                        binding.rvSuggested.setVisibility(View.GONE);
                        binding.linearSuggested.setVisibility(View.GONE);
                        binding.linearMovies.setVisibility(View.GONE);
                        binding.noResults.setVisibility(View.VISIBLE);

                    }

                },throwable -> {
                    Timber.d(throwable);
                    binding.noResults.setVisibility(View.VISIBLE);

                }));
    }





    // Setup recycleview & Adapter for the results
    private void setupSearchRecycleView() {

        binding.rvSearch.setAdapter(searchAdapter);
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.rvSearch.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        binding.rvSearch.setHasFixedSize(true);
        binding.rvSearch.setItemViewCacheSize(8);

    }


    // Load Toolbar
    private void onToolbarLoad() {

        Tools.loadToolbar(((AppCompatActivity)requireActivity()),binding.toolbar,null);
        Tools.setSystemBarTransparent(getActivity());

    }



    // Hide Keyboard
    private void hideKeyboard() {
        View view = this.requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvSearch.setAdapter(null);
        binding.rvSuggested.setAdapter(null);
        if (textWatcher !=null) {

            textWatcher = null;
        }
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding =null;

    }
}

