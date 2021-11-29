package com.beeecorptv.data.datasource.series;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.genres.GenresData;
import com.beeecorptv.data.remote.ApiInterface;
import com.beeecorptv.ui.manager.SettingsManager;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SerieViewsDataSource extends PageKeyedDataSource<Integer, Media> {

    public static final int PAGE_SIZE = 12;
    private static final int FIRST_PAGE = 1;

    private final ApiInterface requestInterface;

    private final SettingsManager settingsManager;

    SerieViewsDataSource(ApiInterface requestInterface,SettingsManager settingsManager) {
        this.requestInterface = requestInterface;
        this.settingsManager = settingsManager;
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Media> callback) {

        Call<GenresData> call = requestInterface.getByViewstv(settingsManager.getSettings().getApiKey(),FIRST_PAGE);
        call.enqueue(new Callback<GenresData>() {

            @Override
            public void onResponse(@NotNull Call<GenresData> call, @NotNull Response<GenresData> response) {


                if (response.isSuccessful()) {

                    callback.onResult(response.body().getGlobaldata(), null, FIRST_PAGE+1);


                }
            }

            @Override
            public void onFailure(@NotNull Call<GenresData> call, @NotNull Throwable t) {
                //
            }
        });


    }

    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Media> callback) {

        Call<GenresData> call = requestInterface.getByViewstv(settingsManager.getSettings().getApiKey(),params.key);
        call.enqueue(new Callback<GenresData>() {

            @Override
            public void onResponse(@NotNull Call<GenresData> call, @NotNull Response<GenresData> response) {


                if (response.isSuccessful()) {

                    Integer key = (params.key > 1) ? params.key - 1 : null;
                    callback.onResult(response.body().getGlobaldata(), key);


                }
            }

            @Override
            public void onFailure(@NotNull Call<GenresData> call, @NotNull Throwable t) {
                //
            }
        });

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Media> callback) {



        Call<GenresData> call = requestInterface.getByViewstv(settingsManager.getSettings().getApiKey(),params.key);
        call.enqueue(new Callback<GenresData>() {

            @Override
            public void onResponse(Call<GenresData> call, Response<GenresData> response) {


                if (response.isSuccessful()) {

                    callback.onResult(response.body().getGlobaldata(), params.key + 1);


                }
            }

            @Override
            public void onFailure(@NonNull Call<GenresData> call, @NonNull Throwable t) {

                //
            }
        });



    }
}