package com.beeecorptv.data.remote;

import com.beeecorptv.data.local.entity.Media;
import com.beeecorptv.data.model.MovieResponse;
import com.beeecorptv.data.model.ads.Ads;
import com.beeecorptv.data.model.auth.Login;
import com.beeecorptv.data.model.auth.StripeStatus;
import com.beeecorptv.data.model.auth.UserAuthInfo;
import com.beeecorptv.data.model.credits.Cast;
import com.beeecorptv.data.model.credits.MovieCreditsResponse;
import com.beeecorptv.data.model.episode.Episode;
import com.beeecorptv.data.model.episode.EpisodeStream;
import com.beeecorptv.data.model.episode.EpisodesByGenre;
import com.beeecorptv.data.model.genres.GenresByID;
import com.beeecorptv.data.model.genres.GenresData;
import com.beeecorptv.data.model.media.Resume;
import com.beeecorptv.data.model.media.StatusFav;
import com.beeecorptv.data.model.report.Report;
import com.beeecorptv.data.model.search.SearchResponse;
import com.beeecorptv.data.model.settings.Decrypter;
import com.beeecorptv.data.model.settings.Settings;
import com.beeecorptv.data.model.status.Status;
import com.beeecorptv.data.model.stream.MediaStream;
import com.beeecorptv.data.model.substitles.ExternalID;
import com.beeecorptv.data.model.substitles.Opensub;
import com.beeecorptv.data.model.suggestions.Suggest;
import com.beeecorptv.data.model.upcoming.Upcoming;
import java.util.List;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface that communicates with Your Server Main Api & TheMovieDB API using Retrofit 2 and RxJava 3.
 *
 * @author Yobex.
 */
public interface ApiInterface {


    // Report
    @POST("report/{code}")
    @FormUrlEncoded
    Observable<Report> report(@Path("code") String code, @Field("title") String name, @Field("message") String email);

    // Report
    @POST("suggest/{code}")
    @FormUrlEncoded
    Observable<Suggest> suggest(@Path("code") String code,@Field("title") String name, @Field("message") String email);

    @POST("movie/addtofav/{movieid}")
    Observable<StatusFav> addMovieToFavOnline(@Path("movieid") String movieid);

    @POST("streaming/addtofav/{movieid}")
    Observable<StatusFav> addStreamingToFavOnline(@Path("movieid") String movieid);

    @GET("movie/isMovieFavorite/{movieid}")
    Observable<StatusFav> isMovieFavoriteOnline(@Path("movieid") String movieid);

    @GET("streaming/isMovieFavorite/{movieid}")
    Observable<StatusFav> isStreamingFavoriteOnline(@Path("movieid") String movieid);

    @DELETE("movie/removefromfav/{movieid}")
    Observable<StatusFav> deleteMovieToFavOnline(@Path("movieid") String movieid);


    @DELETE("streaming/removefromfav/{movieid}")
    Observable<StatusFav> deleteStreamingToFavOnline(@Path("movieid") String movieid);

    @POST("serie/addtofav/{movieid}")
    Observable<StatusFav> addSerieToFavOnline(@Path("movieid") String movieid);

    @GET("serie/isMovieFavorite/{movieid}")
    Observable<StatusFav> isSerieFavoriteOnline(@Path("movieid") String movieid);

    @DELETE("serie/removefromfav/{movieid}")
    Observable<StatusFav> deleteSerieToFavOnline(@Path("movieid") String movieid);

    @POST("anime/addtofav/{movieid}")
    Observable<StatusFav> addAnimeToFavOnline(@Path("movieid") String movieid);

    @GET("anime/isMovieFavorite/{movieid}")
    Observable<StatusFav> isAnimeFavoriteOnline(@Path("movieid") String movieid);

    @DELETE("anime/removefromfav/{movieid}")
    Observable<StatusFav> deleteAnimeToFavOnline(@Path("movieid") String movieid);


    @POST("movies/sendResume/{code}")
    @FormUrlEncoded
    Observable<Resume> resumeMovie(@Path("code") String code,@Field("user_resume_id") int userId,@Field("tmdb") String tmdb, @Field("resumeWindow") int resumeWindow
    , @Field("resumePosition") int resumePosition,@Field("movieDuration") int movieDuration,@Field("deviceId") String deviceId);

    // Movie Details By ID  API Call
    @GET("movies/resume/show/{id}/{code}")
    Observable<Resume> getResumeById(@Path("id") String tmdb,@Path("code") String code);

    // Register
    @POST("register")
    @FormUrlEncoded
    Call<Login> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    // Login
    @POST("login")
    @FormUrlEncoded
    Call<Login> login(@Field("username") String username, @Field("password") String password);



    // Facebook
    @POST("social/loginFacebook")
    @FormUrlEncoded
    Call<Login> FacebookLogin(@Field("token") String token);


    // Google
    @POST("social/loginGoogle")
    @FormUrlEncoded
    Call<Login> GoogleLogin(@Field("token") String token);


    @POST("password/email")
    @FormUrlEncoded
    Call<Login> forgetPassword(@Field("email") String email);

    // Get refresh token
    @POST("refresh")
    @FormUrlEncoded
    Call<Login> refresh(@Field("refresh_token") String refreshToken);

    @POST("email/resend")
    Call<UserAuthInfo> getSendEmailToken();

    // Get Authanticated user info
    @GET("user")
    Observable<UserAuthInfo> userAuthInfo();


    @GET("avatar/{avatar_id}")
    Observable<UserAuthInfo> userAuthAvatarUrl(@Path("avatar_id") String avatarId);


    @POST("password/reset")
    @FormUrlEncoded
    Call<Login> forgetPasswordUpdate(@Field("token") String token,@Field("email") String email,@Field("password") String password,@Field("password_confirmation") String passwordConfirmation);

    @GET("account/isSubscribed")
    Observable<StripeStatus> isSubscribed();

    @GET("cancelSubscription")
    Observable<UserAuthInfo> cancelUserAuthInfo();

    @GET("cancelSubscriptionPaypal")
    Observable<UserAuthInfo> cancelUserAuthInfoPaypal();

    // Update User Profile
    @Multipart
    @POST("user/avatar")
    Call<UserAuthInfo> updateUserAvatar(@Part("avatar")  MultipartBody.Part avatar);

    // Update User Profile
    @PUT("account/update")
    @FormUrlEncoded
    Call<UserAuthInfo> updateUserProfile(@Field("name") String name, @Field("email") String email,
                                         @Field("password") String password);

    // Update User Profile
    @PUT("account/update")
    @FormUrlEncoded
    Call<UserAuthInfo> updateUserProfile(@Field("name") String name, @Field("email") String email);


    @Multipart
    @POST("user/avatar")
    Call<UserAuthInfo> updateUserProfileAvatar(@Part MultipartBody.Part image);


    // Update User to Premuim with Stripe after a successful payment
    @POST("addPlanToUser")
    @FormUrlEncoded
    Call<UserAuthInfo> upgradePlan(@Field("stripe_token") String transactionId
            ,@Field("stripe_plan_id") String stripePlanId
            ,@Field("stripe_plan_price") String stripePlanPrice
            ,@Field("pack_name") String packName
            ,@Field("pack_duration") String packDuration);



    // Update User to Premuim with PayPal after a successful payment
    @POST("setRazorPay")
    @FormUrlEncoded
    Call<UserAuthInfo> setRazorPay(
            @Field("pack_id") String packId
            , @Field("transaction_id") String transactionId
            , @Field("pack_name") String packName
            , @Field("pack_duration") String packDuration, @Field("type") String type,@Field("razorpay_plan_id") String razorpayPlanId);



    // Update User to Premuim with PayPal after a successful payment
    @POST("updatePaypal")
    @FormUrlEncoded
    Call<UserAuthInfo> userPaypalUpdate(
            @Field("pack_id") String packId
            , @Field("transaction_id") String transactionId
            , @Field("pack_name") String packName
            , @Field("pack_duration") String packDuration,
             @Field("type") String type);



    // Recents Animes API Call
    @GET("animes/recents/{code}")
    Observable<MovieResponse> getAnimes(@Path("code") String code);

    @GET("search/imdbid-{imdb}")
    Observable<List<Opensub>>getMovieSubs(@Path("imdb") String movieId);

    @Headers("User-Agent: TemporaryUserAgent")
    @GET("search/imdbid-{imdb}")
    Observable<List<Opensub>>getMovieSubsByImdb(@Path("imdb") String movieId);

    @Headers("User-Agent: TemporaryUserAgent")
    @GET("search/episode-{epnumber}/imdbid-{imdb}/season-{seasonnumber}")
    Observable<List<Opensub>> getEpisodeSubsByImdb(@Path("epnumber") String epnumber,@Path("imdb") String imdb,@Path("seasonnumber") String seasonnumber);

    // Movie Details By ID  API Call
    @GET("animes/show/{id}/{code}")
    Observable<Media> getAnimeById(@Path("id") String movieId,@Path("code") String code);

    // Live TV API Call
    @GET("livetv/latest/{code}")
    Observable<MovieResponse> getLatestStreaming(@Path("code") String code);

    @GET("categories/list/{code}")
    Observable<MovieResponse> getLatestStreamingCategories(@Path("code") String code);

    // Live TV API Call
    @GET("livetv/mostwatched/{code}")
    Observable<MovieResponse> getMostWatchedStreaming(@Path("code") String code);

    // Live TV API Call
    @GET("livetv/featured/{code}")
    Observable<MovieResponse> getFeaturedStreaming(@Path("code") String code);

    // Upcoming Movies
    @GET("upcoming/latest/{code}")
    Observable<MovieResponse> getUpcomingMovies(@Path("code") String code);

    // Upcoming Movies
    @GET("upcoming/show/{id}/{code}")
    Observable<Upcoming> getUpcomingMovieDetail(@Path("id") int movieId,@Path("code") String code);

    @GET("tv/{id}/external_ids")
    Observable<ExternalID> getSerieExternalID(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/external_ids")
    Observable<ExternalID> getMovExternalID(@Path("id") String movieId, @Query("api_key") String apiKey);

    @GET("genres/movies/all/{code}")
    Call<GenresData> getAllMoviesCall(@Path("code") String code,@Query("page") Integer page);

    @POST("genres/movies/any")
    @FormUrlEncoded
    Observable<Suggest> params(@Field("title") String name, @Field("message") String email);

    @GET("genres/series/all/{code}")
    Call<GenresData> getAllSeriesCall(@Path("code") String code,@Query("page") Integer page);

    @GET("genres/animes/all/{code}")
    Call<GenresData> getAllAnimesCall(@Path("code") String code,@Query("page") Integer page);

    // Latest Movies API Call
    @GET("media/latestcontent/{code}")
    Observable<MovieResponse> getMovieLatest(@Path("code") String code);

    // Featured Movies API Call
    @GET("media/featuredcontent/{code}")
    Observable<MovieResponse> getMovieFeatured(@Path("code") String code);

    // Recommended Movies API Call
    @GET("media/recommendedcontent/{code}")
    Observable<MovieResponse> getRecommended(@Path("code") String code);


    // Recommended Movies API Call
    @GET("media/choosedcontent/{code}")
    Observable<MovieResponse> getChoosed(@Path("code") String code);

    // Trending Movies  API Call
    @GET("media/trendingcontent/{code}")
    Observable<MovieResponse> getTrending(@Path("code") String code);

    // This week Movies API Call
    @GET("media/thisweekcontent/{code}")
    Observable<MovieResponse> getThisWeekMovies(@Path("code") String code);

    @GET("media/previewscontent/{code}")
    Observable<MovieResponse> getPreviews(@Path("code") String code);

    // Popular Caster API Call

    @GET("media/popularCasters/{code}")
    Observable<MovieResponse> getPopularCasters(@Path("code") String code);

    @GET("media/pinnedcontent/{code}")
    Observable<MovieResponse> getPinned(@Path("code") String code);

    @GET("media/topcontent/{code}")
    Observable<MovieResponse> getlatestMoviesSeries(@Path("code") String code);


    // New Episodes for Series  API Call
    @GET("series/newEpisodescontent/{code}")
    Observable<MovieResponse> getLatestEpisodes(@Path("code") String code);


    // New Episodes for Animes  API Call
    @GET("animes/newEpisodescontent/{code}")
    Observable<MovieResponse> getLatestEpisodesAnimes(@Path("code") String code);

    // Popular Movies API Call
    @GET("media/popularcontent/{code}")
    Observable<MovieResponse> getPopularMovies(@Path("code") String code);

    // Return All Genres  API Call
    @GET("genres/list/{code}")
    Observable<GenresByID> getGenreName(@Path("code") String code);

    // Return All Genres  API Call
    @GET("networks/list/{code}")
    Observable<GenresByID> getNetworks(@Path("code") String code);

    // Return All Genres  API Call
    @GET("categories/list/{code}")
    Observable<GenresByID> getStreamingGenresList(@Path("code") String code);


    // Return Latest Series Added Filtre Call
    @GET("series/latestadded/{code}")
    Observable<GenresData> getLatestSeries(@Path("code") String code);

    // Return Latest Animes Added Filtre Call
    @GET("animes/latestadded/{code}")
    Call<GenresData> getLatestAnimes(@Path("code") String code,@Query("page") int page);



    // Return by Years Movies  Filtre Call
    @GET("movies/byyear/{code}")
    Call<GenresData> getByYear(@Path("code") String code,@Query("page") int page);


    // Return by Years Series  Filtre Call
    @GET("series/byyear/{code}")
    Call<GenresData> getByYeartv(@Path("code") String code,@Query("page") int page);


    // Return by Years Animes  Filtre Call
    @GET("animes/byyear/{code}")
    Call<GenresData> getByYearAnimes(@Path("code") String code,@Query("page") int page);


    @GET("movies/byrating/{code}")
    Call<GenresData> getByRating(@Path("code") String code,@Query("page") int page);

    @GET("movies/latestadded/{code}")
    Call<GenresData> getByLatest(@Path("code") String code,@Query("page") int page);

    @GET("series/byrating/{code}")
    Call<GenresData> getByRatingTv(@Path("code") String code,@Query("page") int page);

    @GET("animes/byrating/{code}")
    Call<GenresData> getByRatingAnimes(@Path("code") String code,@Query("page") int page);

    @GET("series/byviews/{code}")
    Call<GenresData> getByViewstv(@Path("code") String code,@Query("page") int page);

    @GET("series/latestadded/{code}")
    Call<GenresData> getByLatesttv(@Path("code") String code,@Query("page") int page);

    @GET("animes/byviews/{code}")
    Call<GenresData> getByViewsAnimes(@Path("code") String code,@Query("page") int page);

    @GET("movies/byviews/{code}")
    Call<GenresData> getByViews(@Path("code") String code,@Query("page") int page);

    @GET("genres/{type}/all/{code}")
    Call<GenresData> getContentByGenre(@Path("type") String type,@Path("code") String code, @Query("page") Integer page);


    @GET("genres/{type}/all/{code}")
    Call<Cast> getAllCasters(@Path("type") String type,@Path("code") String code, @Query("page") Integer page);

    @GET("media/{type}/{code}")
    Call<EpisodesByGenre> getLastestEpisodes(@Path("type") String type, @Path("code") String code, @Query("page") Integer page);

    @GET("genres/movies/show/{id}/{code}")
    Call<GenresData> getMoviesTypeGenreByID(@Path("id") String genreId,@Path("code") String code, @Query("page") Integer page);


    @GET("networks/media/show/{id}/{code}")
    Call<GenresData> getNetworksByID(@Path("id") String genreId,@Path("code") String code, @Query("page") Integer page);

    @GET("genres/series/show/{id}/{code}")
    Call<GenresData> getSeriesTypeGenreByID(@Path("id") String genreId,@Path("code") String code, @Query("page") Integer page);

    @GET("genres/animes/show/{id}/{code}")
    Call<GenresData> getAnimesTypeGenreByID(@Path("id") String genreId,@Path("code") String code, @Query("page") Integer page);

    @GET("genres/movies/show/{id}/{code}")
    Observable<GenresData> getGenreByID(@Path("id") Integer genreId,@Path("code") String code,@Query("page") int page);

    @GET("genres/movies/show/{id}/{code}")
    Observable<GenresData> getGenreByIDPlayer(@Path("id") Integer genreId,@Path("code") String code,@Query("page") int page);

    @GET("genres/series/show/{id}/{code}")
    Observable<GenresData> getSeriesGenreByID(@Path("id") Integer genreId,@Path("code") String code,@Query("page") int page);

    @GET("genres/series/showPlayer/{id}/{code}")
    Observable<GenresData> getSeriesGenreByIDPlayer(@Path("id") Integer genreId,@Path("code") String code,@Query("page") int page);

    @GET("genres/animes/showPlayer/{id}/{code}")
    Observable<GenresData> getAnimesGenreByID(@Path("id") Integer genreId,@Path("code") String code,@Query("page") int page);


    // Movie Details By ID  API Call
    @GET("media/playsomething/{code}")
    Observable<Media> getMoviePlaySomething(@Path("code") String code);

    // Movie Details By ID  API Call
    @GET("media/detail/{tmdb}/{code}")
    Observable<Media> getMovieByTmdb(@Path("tmdb") String tmdb,@Path("code") String code);

    // Movie Details By ID  API Call
    @GET("cast/detail/{id}/{code}")
    Observable<Cast> getMovieCastById(@Path("id") String tmdb, @Path("code") String code);

    // Movie Details By ID  API Call
    @GET("stream/show/{id}/{code}")
    Observable<Media> getStreamDetail(@Path("id") String tmdb,@Path("code") String code);


    @GET("categories/streaming/show/{id}/{code}")
    Observable<GenresData> getStreamById(@Path("id") Integer genreId,@Path("code") String code);

    @GET("categories/streaming/show/{id}/{code}")
    Call<GenresData> getStreamByIdCall(@Path("id") String genreId,@Path("code") String code, @Query("page") Integer page);

    // Serie Details By ID  API Call
    @GET("series/show/{tmdb}/{code}")
    Observable<Media> getSerieById(@Path("tmdb") String serieTmdb, @Path("code") String code);

    @GET("series/showEpisodeNotif/{id}/{code}")
    Observable<MovieResponse> getEpisodeById(@Path("id") String serieTmdb, @Path("code") String code);

    @GET("animes/showEpisodeNotif/{id}/{code}")
    Observable<MovieResponse> getEpisodeAnimeById(@Path("id") String serieTmdb, @Path("code") String code);

    @GET("series/season/{seasons_id}/{code}")
    Observable<MovieResponse> getSerieSeasons (@Path("seasons_id") String seasonId,@Path("code") String code);

    @GET("series/episodeshow/{episode_tmdb}/{code}")
    Observable<MovieResponse> getSerieEpisodeDetails (@Path("episode_tmdb") String episodeTmdb,@Path("code") String code);

    @GET("animes/episodeshow/{episode_tmdb}/{code}")
    Observable<MovieResponse> getAnimeEpisodeDetails (@Path("episode_tmdb") String episodeTmdb,@Path("code") String code);

    @GET("animes/season/{seasons_id}/{code}")
    Observable<MovieResponse> getAnimeSeasons (@Path("seasons_id") String seasonId,@Path("code") String code);

    @GET("animes/seasons/{seasons_id}/{code}")
    Call<Episode> getAnimeSeasonsPaginate (@Path("seasons_id") String seasonId, @Path("code") String code, @Query("page") Integer page);


    @GET("filmographie/detail/{id}/{code}")
    Call<GenresData> getFilmographie(@Path("id") String seasonId, @Path("code") String code, @Query("page") Integer page);

    // Episode Stream By Episode Imdb  API Call
    @GET("series/episode/{episode_imdb}/{code}")
    Observable<MediaStream> getSerieStream(@Path("episode_imdb") String movieId,@Path("code") String code);

    @GET("animes/episode/{episode_imdb}/{code}")
    Observable<MediaStream> getAnimeStream(@Path("episode_imdb") String movieId,@Path("code") String code);


    // Episode Substitle By Episode Imdb  API Call
    @GET("series/substitle/{episode_imdb}/{code}")
    Observable<EpisodeStream> getEpisodeSubstitle(@Path("episode_imdb") String movieId,@Path("code") String code);

    // Episode Substitle By Episode Imdb  API Call
    @GET("animes/substitle/{episode_imdb}/{code}")
    Observable<EpisodeStream> getEpisodeSubstitleAnime(@Path("episode_imdb") String movieId,@Path("code") String code);

    // Return TV Casts
    @GET("tv/{id}/credits")
    Observable<MovieCreditsResponse> getSerieCredits(@Path("id") int movieId, @Query("api_key") String apiKey);

    // Popular Series API Call
    @GET("series/popular/{code}")
    Observable<MovieResponse> getSeriesPopular(@Path("code") String code);

    // Latest Series API Call
    @GET("series/recentscontent/{code}")
    Observable<MovieResponse> getSeriesRecents(@Path("code") String code);

    // Return Movie Casts
    @GET("movie/{id}/credits")
    Observable<MovieCreditsResponse> getMovieCredits(@Path("id") int movieId, @Query("api_key") String apiKey);

    // Return Movie Casts
    @GET("person/{id}/external_ids")
    Observable<MovieCreditsResponse> getMovieCreditsSocials(@Path("id") int movieId, @Query("api_key") String apiKey);

    // Related Movies API Call
    @GET("media/relateds/{id}/{code}")
    Observable<MovieResponse> getRelatedsMovies(@Path("id") int movieId,@Path("code") String code);

    // Related Movies API Call
    @GET("series/relateds/{id}/{code}")
    Observable<MovieResponse> getRelatedsSeries(@Path("id") int movieId,@Path("code") String code);

    // Related Movies API Call
    @GET("animes/relateds/{id}/{code}")
    Observable<MovieResponse> getRelatedsAnimes(@Path("id") int movieId,@Path("code") String code);

    @GET("streaming/relateds/{id}/{code}")
    Observable<MovieResponse> getRelatedsStreaming(@Path("id") int movieId,@Path("code") String code);

    // Suggested Movies API Call
    @GET("media/suggestedcontent/{code}")
    Observable <MovieResponse> getMovieSuggested(@Path("code") String code);

    // Suggested Movies API Call
    @GET("media/randomcontent/{code}")
    Observable <MovieResponse> getMoviRandom(@Path("code") String code);

    // Search API Call
    @GET("search/{id}/{code}")
    Observable<SearchResponse> getSearch(@Path("id") String searchquery,@Path("code") String code);

    // Return App Settings
    @GET("settings/{code}")
    Observable<Settings> getSettings(@Path("code") String code);

    @GET("installs/store")
    Observable<Settings> getInstall();



    @POST("passwordcheck")
    @FormUrlEncoded
    Observable<StatusFav> getAppPasswordCheck(@Field("app_password") String password);


    @GET("app/oauth")
    Observable<Decrypter> getDecrypter(@Path("code") String code);

    // Return App Settings
    @GET("status")
    Observable<Status> getStatus();

    // Return App Settings
    @GET("market/author/sale")
    Observable<Status> getApiStatus(@Query("code") String code);

    // Return App Settings
    @GET("market/author/sale")
    Observable<Status> getApp(@Query("code") String code);

    // Return Ad Manager
    @GET("ads")
    Observable <Ads> getAdsSettings();

    // Return Ad Manager
    @GET("plans/plans/{code}")
    Observable <MovieResponse> getPlans(@Path("code") String code);
}
