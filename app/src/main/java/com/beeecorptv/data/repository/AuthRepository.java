package com.beeecorptv.data.repository;

import static com.beeecorptv.util.Constants.ERROR;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.beeecorptv.data.model.auth.Login;
import com.beeecorptv.data.model.auth.StripeStatus;
import com.beeecorptv.data.model.auth.UserAuthInfo;
import com.beeecorptv.data.model.media.StatusFav;
import com.beeecorptv.data.remote.ApiInterface;
import com.beeecorptv.data.remote.ErrorHandling;
import com.beeecorptv.ui.manager.TokenManager;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import javax.inject.Named;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class AuthRepository {


    @Inject
    @Named("Auth")
    ApiInterface requestAuth;


    final ApiInterface requestMainApi;


    @Inject
    TokenManager tokenManager;


    @Inject
    AuthRepository (ApiInterface requestLogin, TokenManager tokenManager,ApiInterface requestMainApi) {
        this.tokenManager = tokenManager;
        this.requestAuth = requestLogin;
        this.requestMainApi = requestMainApi;
    }





    public LiveData<ErrorHandling<UserAuthInfo>> editUserImage(MultipartBody.Part avatar) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> userAvatar = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.updateUserAvatar(avatar);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(@NotNull Call<UserAuthInfo> call, @NotNull Response<UserAuthInfo> response) {
                if (response.body() != null) {
                    UserAuthInfo body = response.body();
                    userAvatar.setValue(ErrorHandling.success(body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserAuthInfo> call, @NotNull Throwable t) {
                userAvatar.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return userAvatar;
    }


    public LiveData<ErrorHandling<UserAuthInfo>> editUserProfile2(String name,String email) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> register = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.updateUserProfile(name,email);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(@NotNull Call<UserAuthInfo> call, @NotNull Response<UserAuthInfo> response) {
                if (response.body() != null) {
                    UserAuthInfo body = response.body();
                    register.setValue(ErrorHandling.success(body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserAuthInfo> call, @NotNull Throwable t) {
                register.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return register;
    }

    public LiveData<ErrorHandling<UserAuthInfo>> editUserProfile(String name,String email, String password) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> register = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.updateUserProfile(name,email,password);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(@NotNull Call<UserAuthInfo> call, @NotNull Response<UserAuthInfo> response) {
                if (response.body() != null) {
                    UserAuthInfo body = response.body();
                    register.setValue(ErrorHandling.success(body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserAuthInfo> call, @NotNull Throwable t) {
                register.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return register;
    }



    public LiveData<ErrorHandling<UserAuthInfo>> editUserAvatar(MultipartBody.Part avatar) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> register = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.updateUserProfileAvatar(avatar);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(@NotNull Call<UserAuthInfo> call, @NotNull Response<UserAuthInfo> response) {
                if (response.body() != null) {
                    UserAuthInfo body = response.body();
                    register.setValue(ErrorHandling.success(body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserAuthInfo> call, @NotNull Throwable t) {
                register.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return register;
    }





    // Update User to Premuim
    public LiveData<ErrorHandling<UserAuthInfo>> getUpgradePlan(String transactionId,String stripePlanId,String stripePlanPrice,String packName,String packDuration) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> login = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.upgradePlan(transactionId,stripePlanId,stripePlanPrice,packName,packDuration);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(@NotNull Call<UserAuthInfo> call, @NotNull Response<UserAuthInfo> response) {

                if (response.isSuccessful()) {
                    UserAuthInfo body = response.body();
                    assert body != null;
                    login.setValue(ErrorHandling.success(body));


                }else {

                    UserAuthInfo body = response.body();

                    login.setValue(ErrorHandling.error(ERROR,body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserAuthInfo> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return login;
    }



    public LiveData<ErrorHandling<UserAuthInfo>> getUpgradePaypal(String packId,String transactionId,String packName,String packDuration,String type) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> login = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.userPaypalUpdate(packId,transactionId,packName,packDuration,type);
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<UserAuthInfo> call, @NonNull Response<UserAuthInfo> response) {


                if (response.isSuccessful()) {
                    UserAuthInfo body = response.body();
                    login.setValue(ErrorHandling.success(body));


                } else {

                    UserAuthInfo body = response.body();

                    login.setValue(ErrorHandling.error("Error", body));

                    Timber.i("Errror"+body);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserAuthInfo> call, @NonNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(), null));
            }
        });

        return login;
    }

    public LiveData<ErrorHandling<UserAuthInfo>> setSubscriptionRazorPay(String packId,String transactionId,String packName,String packDuration ,String type
    ,String razorpayPlanId) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> login = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.setRazorPay(packId,transactionId,packName,packDuration,type,razorpayPlanId);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(@NotNull Call<UserAuthInfo> call, @NotNull Response<UserAuthInfo> response) {


                if (response.isSuccessful()) {
                    UserAuthInfo body = response.body();
                    assert body != null;
                    login.setValue(ErrorHandling.success(body));


                }else {

                    UserAuthInfo body = response.body();

                    login.setValue(ErrorHandling.error("Error",body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserAuthInfo> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return login;
    }

    public LiveData<ErrorHandling<UserAuthInfo>> setSubscription(String packId,String transactionId,String packName,String packDuration ,String type) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> login = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.userPaypalUpdate(packId,transactionId,packName,packDuration,type);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(@NotNull Call<UserAuthInfo> call, @NotNull Response<UserAuthInfo> response) {


                Timber.i("Response is : %s", response.body());

                if (response.isSuccessful()) {
                    UserAuthInfo body = response.body();
                    assert body != null;
                    login.setValue(ErrorHandling.success(body));


                }else {

                    Timber.i("Response is : %s", response.body());

                    UserAuthInfo body = response.body();

                    login.setValue(ErrorHandling.error(ERROR,body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserAuthInfo> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return login;
    }




    public Observable<StatusFav> getAddMovieOnline(String movieid) {
        return requestAuth.addMovieToFavOnline(movieid);
    }

    public Observable<StatusFav> getAddStreamingOnline(String movieid) {
        return requestAuth.addStreamingToFavOnline(movieid);
    }


    public Observable<StatusFav> getAddSerieOnline(String movieid) {
        return requestAuth.addSerieToFavOnline(movieid);
    }


    public Observable<StatusFav> getAddAnimeOnline(String movieid) {
        return requestAuth.addAnimeToFavOnline(movieid);
    }




    public Observable<StatusFav> getisMovieFavoriteOnline(String movieid) {
        return requestAuth.isMovieFavoriteOnline(movieid);
    }


    public Observable<StatusFav> getisStreamingFavoriteOnline(String movieid) {
        return requestAuth.isStreamingFavoriteOnline(movieid);
    }


    public Observable<StatusFav> getisSerieFavoriteOnline(String movieid) {
        return requestAuth.isSerieFavoriteOnline(movieid);
    }



    public Observable<StatusFav> getisAnimeFavoriteOnline(String movieid) {
        return requestAuth.isAnimeFavoriteOnline(movieid);
    }



    public Observable<StatusFav> getDeleteMovieOnline(String movieid) {
        return requestAuth.deleteMovieToFavOnline(movieid);
    }



    public Observable<StatusFav> getDeleteStreamingOnline(String movieid) {
        return requestAuth.deleteStreamingToFavOnline(movieid);
    }




    public Observable<StatusFav> getDeleteSerieOnline(String movieid) {
        return requestAuth.deleteSerieToFavOnline(movieid);
    }



    public Observable<StatusFav> getDeleteAnimeOnline(String movieid) {
        return requestAuth.deleteAnimeToFavOnline(movieid);
    }




    // Return Authenticated User with informations(Name,Email,etc...)
    public Observable<UserAuthInfo> getAuth() {
        return requestAuth.userAuthInfo();
    }




    public Observable<UserAuthInfo> getAuthAvatarUrl(String avatar) {
        return requestMainApi.userAuthAvatarUrl(avatar);
    }

    public Observable<StripeStatus> getStripeStatus() {
        return requestAuth.isSubscribed();
    }



    // Cancel User Subcription
    public Observable<UserAuthInfo> cancelAuthSubcription() {
        return requestAuth.cancelUserAuthInfo();
    }


    // Cancel User Subcription
    public Observable<UserAuthInfo> cancelAuthSubcriptionPaypal() {
        return requestAuth.cancelUserAuthInfoPaypal();
    }




    public LiveData<ErrorHandling<Login>> getFacebookLogin(String token) {


        final MutableLiveData<ErrorHandling<Login>> login = new MutableLiveData<>();

        Call<Login> call = requestAuth.FacebookLogin(token);
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NotNull Call<Login> call, @NotNull Response<Login> response) {


                if (response.isSuccessful()) {
                    Login body = response.body();
                    login.setValue(ErrorHandling.success(body));

                  Timber.i(""+response);

                } else {

                    Login body = response.body();
                    login.setValue(ErrorHandling.error(response.message(), body));

                    Timber.i(""+response);

                }
            }

            @Override
            public void onFailure(@NotNull Call<Login> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(), null));
                Timber.i("%s", t.getMessage());

            }
        });

        return login;
    }



    public LiveData<ErrorHandling<Login>> getGoogleLogin(String token) {


        final MutableLiveData<ErrorHandling<Login>> login = new MutableLiveData<>();

        Call<Login> call = requestAuth.GoogleLogin(token);
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NotNull Call<Login> call, @NotNull Response<Login> response) {


                if (response.isSuccessful()) {
                    Login body = response.body();
                    login.setValue(ErrorHandling.success(body));

                    Timber.i(""+response);

                } else {

                    Login body = response.body();
                    login.setValue(ErrorHandling.error(response.message(), body));

                    Timber.i(""+response);

                }
            }

            @Override
            public void onFailure(@NotNull Call<Login> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(), null));
                Timber.i("%s", t.getMessage());

            }
        });

        return login;
    }



    // Handle User Login
    public LiveData<ErrorHandling<Login>> getDetail(String username,String password) {
        final MutableLiveData<ErrorHandling<Login>> login = new MutableLiveData<>();


        Call<Login> call = requestAuth.login(username,password);
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NotNull Call<Login> call, @NotNull Response<Login> response) {


                if (response.isSuccessful()) {
                    Login body = response.body();
                    login.setValue(ErrorHandling.success(body));


                } else {

                    Login body = response.body();
                    login.setValue(ErrorHandling.error(response.message(), body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<Login> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(), null));
            }
        });

        return login;
    }



    public LiveData<ErrorHandling<Login>> getForgetPassword(String username) {
        final MutableLiveData<ErrorHandling<Login>> login = new MutableLiveData<>();


        Call<Login> call = requestAuth.forgetPassword(username);
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NotNull Call<Login> call, @NotNull Response<Login> response) {


                if (response.isSuccessful()) {
                    Login body = response.body();
                    login.setValue(ErrorHandling.success(body));


                } else {

                    Login body = response.body();

                    login.setValue(ErrorHandling.error(response.message(), body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<Login> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(), null));
            }
        });

        return login;
    }





    public LiveData<ErrorHandling<UserAuthInfo>> getVerifyEmail() {

        final MutableLiveData<ErrorHandling<UserAuthInfo>> login = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.getSendEmailToken();
        call.enqueue(new Callback<>() {

            @Override
            public void onResponse(@NotNull Call<UserAuthInfo> call, @NotNull Response<UserAuthInfo> response) {


                if (response.isSuccessful()) {
                    UserAuthInfo body = response.body();
                    login.setValue(ErrorHandling.success(body));


                } else {

                    UserAuthInfo body = response.body();

                    assert response.body() != null;
                    login.setValue(ErrorHandling.error(response.body().getMessage(), body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserAuthInfo> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(), null));
            }
        });

        return login;
    }



    public LiveData<ErrorHandling<Login>> getPasswordUpdate(String token,String email,String password,String passwordConfirm) {
        final MutableLiveData<ErrorHandling<Login>> login = new MutableLiveData<>();

        Call<Login> call = requestAuth.forgetPasswordUpdate(token,email,password,passwordConfirm);
        call.enqueue(new Callback<Login>() {

            @Override
            public void onResponse(@NotNull Call<Login> call, @NotNull Response<Login> response) {


                if (response.isSuccessful()) {
                    Login body = response.body();
                    assert body != null;
                    login.setValue(ErrorHandling.success(body));


                }else {

                    Login body = response.body();

                    login.setValue(ErrorHandling.error("Error",body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<Login> call, @NotNull Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return login;
    }



    // Handle User Registration
    public LiveData<ErrorHandling<Login>> getRegisterDetail(String name,String email, String password) {
        final MutableLiveData<ErrorHandling<Login>> register = new MutableLiveData<>();


        Call<Login> call = requestAuth.register(name,email,password);
        call.enqueue(new Callback<Login>() {

            @Override
            public void onResponse(@NotNull Call<Login> call, @NotNull Response<Login> response) {


                if (response.isSuccessful()) {
                    Login body = response.body();
                    assert body != null;
                    register.setValue(ErrorHandling.success(body));


                }else {

                    Login body = response.body();

                    register.setValue(ErrorHandling.error("Error : ",body));
                }
            }

            @Override
            public void onFailure(@NotNull Call<Login> call, @NotNull Throwable t) {
                register.setValue(ErrorHandling.error(t.getMessage(), null));
            }
        });

        return register;
    }

}

