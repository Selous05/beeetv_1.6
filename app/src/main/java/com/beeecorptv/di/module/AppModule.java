package com.beeecorptv.di.module;

import static android.content.Context.MODE_PRIVATE;
import static com.beeecorptv.util.Constants.PREF_FILE;
import static java.lang.String.valueOf;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import androidx.annotation.Nullable;
import androidx.room.Room;
import com.easyplex.easyplexsupportedhosts.Utils.AdPlaying;
import com.beeecorptv.data.local.EasyPlexDatabase;
import com.beeecorptv.data.local.dao.AnimesDao;
import com.beeecorptv.data.local.dao.DownloadDao;
import com.beeecorptv.data.local.dao.HistoryDao;
import com.beeecorptv.data.local.dao.MoviesDao;
import com.beeecorptv.data.local.dao.ResumeDao;
import com.beeecorptv.data.local.dao.SeriesDao;
import com.beeecorptv.data.local.dao.StreamListDao;
import com.beeecorptv.data.model.ads.AdMediaModel;
import com.beeecorptv.data.model.ads.AdRetriever;
import com.beeecorptv.data.model.ads.CuePointsRetriever;
import com.beeecorptv.data.model.media.MediaModel;
import com.beeecorptv.data.model.status.Status;
import com.beeecorptv.data.remote.ApiInterface;
import com.beeecorptv.data.remote.ServiceGenerator;
import com.beeecorptv.di.ViewModelModule;
import com.beeecorptv.ui.manager.AdsManager;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.StatusManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.player.bindings.PlayerController;
import com.beeecorptv.ui.player.controller.PlayerAdLogicController;
import com.beeecorptv.ui.player.controller.PlayerUIController;
import com.beeecorptv.ui.player.fsm.callback.AdInterface;
import com.beeecorptv.ui.player.fsm.callback.Callback;
import com.beeecorptv.ui.player.fsm.callback.CuePointCallBack;
import com.beeecorptv.ui.player.fsm.callback.CuePointCallBackAd;
import com.beeecorptv.ui.player.fsm.callback.RetrieveAdCallback;
import com.beeecorptv.ui.player.fsm.concrete.FetchCuePointState;
import com.beeecorptv.ui.player.fsm.concrete.factory.StateFactory;
import com.beeecorptv.ui.player.fsm.listener.AdPlayingMonitor;
import com.beeecorptv.ui.player.fsm.listener.CuePointMonitor;
import com.beeecorptv.ui.player.fsm.state_machine.Fsm;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayer;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayerApi;
import com.beeecorptv.ui.player.fsm.state_machine.FsmPlayerImperial;
import com.beeecorptv.ui.player.interfaces.VpaidClient;
import com.beeecorptv.ui.player.utilities.PlaybackSettingMenu;
import com.beeecorptv.ui.player.utilities.TrackSelectionDialog;

import org.jetbrains.annotations.NotNull;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import timber.log.Timber;


/**
 * Application-wide dependencies.
 * Module means the class which contains methods who will provide dependencies.
 *
 * @author Yobex.
 */


@Module(includes = ViewModelModule.class)
public
class AppModule {


    private String uniqueID = null;

    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";



    @Provides
    @Singleton
    @Named("vpn")
    boolean checkVpn (){
        String vpn = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())){
                if (networkInterface.isUp())
                    vpn = networkInterface.getName();
                if ( vpn.contains("tun") || vpn.contains("ppp") || vpn.contains("pptp")) {
                    return true;
                }
            }
        }catch (SocketException e){
            e.printStackTrace();
        }
        return false;
    }



    @Provides
    @Singleton
    @Named("root")
    @Nullable
    ApplicationInfo provideRoot(Application application) {
        ApplicationInfo getInstalledApps = null;
        final PackageManager pm = application.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.thirdparty.superuser") ||
                    packageInfo.packageName.equals("org.adaway") ||
                    packageInfo.packageName.equals("eu.chainfire.supersu") ||
                    packageInfo.packageName.equals("com.noshufou.android.su") ||
                    packageInfo.packageName.equals("com.koushikdutta.superuser") ||
                    packageInfo.packageName.equals("com.zachspong.temprootremovejb") ||
                    packageInfo.packageName.equals("com.ramdroid.appquarantine") ||
                    packageInfo.packageName.equals("com.topjohnwu.magisk")
            ) {
                getInstalledApps = packageInfo;
            }
        }
        return getInstalledApps;
    }


    @Provides
    @Singleton
    @Named("sniffer")
    @Nullable
    ApplicationInfo provideApplicationInfo(Application application){
        ApplicationInfo restrictPackageInfo = null;
        final PackageManager pm = application.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.guoshi.httpcanary") ||
                    packageInfo.packageName.equals("org.adblockplus.browser") ||
                    packageInfo.packageName.equals("org.blokada.alarm.dnschanger") ||
                    packageInfo.packageName.equals("com.adguard.android.contentblocker") ||
                    packageInfo.packageName.equals("com.protectstar.adblocker") ||
                    packageInfo.packageName.equals("tech.httptoolkit.android") ||
                    packageInfo.packageName.equals("tech.httptoolkit.android.v1") ||
                    packageInfo.packageName.equals("com.guoshi.httpcanary.premium") ||
                    packageInfo.packageName.equals("com.hsv.freeadblockerbrowser") ||
                    packageInfo.packageName.equals("s.sdownload.adblockerultimatebrowser") ||
                    packageInfo.packageName.equals("com.egorovandreyrm.pcapremote") ||
                    packageInfo.packageName.equals("com.packagesniffer.frtparlak") ||
                    packageInfo.packageName.equals("jp.co.taosoftware.android.packetcapture") ||
                    packageInfo.packageName.equals("app.greyshirts.sslcapture") ||
                    packageInfo.packageName.equals("com.minhui.networkcapture.pro") ||
                    packageInfo.packageName.equals("com.minhui.networkcapture") ||
                    packageInfo.packageName.equals("com.evbadroid.wicapdemo") ||
                    packageInfo.packageName.equals("com.evbadroid.wicap") ||
                    packageInfo.packageName.equals("com.luckypatchers.luckypatcherinstaller") ||
                    packageInfo.packageName.equals("ru.UbLBBRLf.jSziIaUjL") ||
                    packageInfo.packageName.equals("com.emanuelef.remote_capture") ||
                    packageInfo.packageName.equals("com.minhui.wifianalyzer") ||
                    packageInfo.packageName.equals("com.evbadroid.proxymon")
            ) {
                restrictPackageInfo = packageInfo;
            }
        }

        return restrictPackageInfo;
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }


    @Provides
    @Singleton
    @Named("package_name")
    String packName(Application application) {
     Timber.i("%s", application.getPackageName());
     return application.getPackageName();
    }


    @Provides
    @Singleton
    PlayerController providePlayerController() {

        return new PlayerController();
    }

    @Provides
    @Singleton
    TrackSelectionDialog provideTrackSelectionDialog(){
        return new TrackSelectionDialog();

    }


    @Provides
    @Singleton
    ApiInterface provideMoviesService() {
        return ServiceGenerator.createService(ApiInterface.class);

    }




    @Provides
    @Singleton
    @Named("main")
    ApiInterface provideServiceMain() {
        return ServiceGenerator.createServiceMain(ApiInterface.class);

    }


    @Provides
    @Singleton
    @Named("app")
    ApiInterface provideServiceApp() {
        return ServiceGenerator.createServiceApp(ApiInterface.class);

    }

    @Provides
    @Singleton
    @Named("status")
    ApiInterface provideServiceStatus(SettingsManager tokenManager) {
        return ServiceGenerator.createServiceWithStatus(ApiInterface.class,tokenManager);

    }


    @Provides
    @Singleton
    @Named("imdb")
    ApiInterface provideMoviesServiceImdb() {
        return ServiceGenerator.createServiceImdb(ApiInterface.class);

    }


    @Provides
    @Singleton
    @Named("hxfile")
    ApiInterface provideMoviesServiceHxfile() {
        return ServiceGenerator.createServiceHxfile(ApiInterface.class);

    }




    @Provides
    @Singleton
    @Named("opensubs")
    ApiInterface provideMoviesServiceOpenSubs() {
        return ServiceGenerator.createServiceOpenSubs(ApiInterface.class);

    }


    @Provides
    @Singleton
    @Named("Auth")
    ApiInterface provideServiceAuth(TokenManager tokenManager) {
        return ServiceGenerator.createServiceWithAuth(ApiInterface.class,tokenManager);

    }


    @Singleton
    @Provides
    EasyPlexDatabase provideDb(Application app) {
        return Room.databaseBuilder(app, EasyPlexDatabase.class, "easyplex.db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }

    @Singleton
    @Provides
    MoviesDao provideFavMoviesDao(EasyPlexDatabase db) {
        return db.favoriteDao();
    }


    @Singleton
    @Provides
    DownloadDao provideProgressDao(EasyPlexDatabase db){
        return db.progressDao();

    }



    @Provides
    @Singleton
    @Named("player")
    ApiInterface provideplayerAdLogicControllerApi() {
        return FsmPlayerApi.playerAdLogicControllerApi(ApiInterface.class);

    }


    @Provides
    @Singleton
    @Named("cuePointPlayer")
    ApiInterface provideCuePointCallBackAd() {
        return CuePointCallBackAd.cuePointRetrieve(ApiInterface.class);

    }


    @Provides
    @Singleton
    @Named("adplaying")
    ApiInterface provideAdPlaying() {
        return AdPlaying.adPlaying(ApiInterface.class);

    }


    @Singleton
    @Provides
    ResumeDao provideResumeDao(EasyPlexDatabase db) {

        return db.resumeDao();
    }

    @Singleton
    @Provides
    StreamListDao provideStreamyDao(EasyPlexDatabase db){
        return db.streamListDao();

    }

    @Singleton
    @Provides
    HistoryDao provideHistoryDao(EasyPlexDatabase db){

        return db.historyDao();

    }


    @Singleton
    @Provides
    SeriesDao provideSeriesDao(EasyPlexDatabase db){

        return db.seriesDao();

    }


    @Singleton
    @Provides
    AnimesDao provideAnimesDao(EasyPlexDatabase db) {

        return db.animesDao();

    }

    @Provides
    @Singleton
    @Named("device_id")
    public  String deviceId(Application application) {

        if (uniqueID == null) {
            uniqueID = provideSharedPreferences(application).getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                providesSharedPreferencesEditor(application).putString(PREF_UNIQUE_ID, uniqueID);
                providesSharedPreferencesEditor(application).apply();
            }
        }
        return uniqueID;
    }




    @Provides
    @Singleton
    @Named("mainplayer")
    public String mainPlayer(Application application) {
        return provideSharedPreferences(application).getString(provideCuePointY(), valueOf(provideCuePointN().equals(provideCuePointN())));

    }



    @Provides
    @Singleton
    @Named("appsettingloaded")
    public String mainAppSettingLoaded(Application application) {
        return provideSharedPreferences(application).getString(provideadplayingY(), valueOf(provideadplayingN().equals(provideadplayingN())));

    }


    @Provides
    @Singleton
    @Named("ready")
    public  boolean provideReady(Application application) {
        return !mainAppSettingLoaded(application).equals(provideadplayingN());
    }


    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Application application) {

        return application.getSharedPreferences(PREF_FILE, MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public TokenManager provideTokenManager(Application application){

        return new TokenManager(application.getSharedPreferences(PREF_FILE,MODE_PRIVATE));
    }

    @Provides
    @Singleton
    AdsManager provideAdsManager(Application application) {

        return new AdsManager(application.getSharedPreferences(PREF_FILE,MODE_PRIVATE));
    }

    @Provides
    @Singleton
    @Named("callback")
    ApiInterface provideCallback() {
        return Callback.callback(ApiInterface.class);

    }


    @Provides
    @Singleton
    com.easyplex.easyplexsupportedhosts.ApiInterface provideUtilsC() {
        return com.easyplex.easyplexsupportedhosts.Utils.EasyPlexAppUtils.createServiceMain(com.easyplex.easyplexsupportedhosts.ApiInterface.class);

    }

    @Provides
    @Singleton
    StatusManager provideStatusManager(Application application) {
     return new StatusManager(application.getSharedPreferences(PREF_FILE,MODE_PRIVATE));
    }

    @Provides
    @Singleton
    @Named("loading")
    com.easyplex.easyplexsupportedhosts.Utils.Fsm playerLoading() {
        return FsmPlayerApi.playerLoading(com.easyplex.easyplexsupportedhosts.Utils.Fsm.class);

    }

    @Provides
    @Singleton
    SettingsManager provideSettingsManager(Application application){

        return new SettingsManager(application.getSharedPreferences(PREF_FILE,MODE_PRIVATE));
    }

    @Provides
    @Singleton
    AuthManager provideAuthManager(Application application){

        return new AuthManager(application.getSharedPreferences(PREF_FILE,MODE_PRIVATE));
    }

    @Provides
    @Singleton
    SharedPreferences.Editor providesSharedPreferencesEditor(Application application) {

        return application.getSharedPreferences(PREF_FILE, MODE_PRIVATE).edit();
    }

    @Provides
    @Singleton
    CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @Singleton
    StateFactory provideStateFactory() {
        return new StateFactory();
    }

    @Provides
    @Singleton
    FsmPlayer provideFsmPlayer(StateFactory factory) {
        return new FsmPlayerImperial(factory) {
            @Override
            public @NotNull Class initializeState() {
                return FetchCuePointState.class;
            }

            @Override
            public Observable<Status> getFsm(String code) {

                return null;
            }

            @Override
            public void updateSelfUi() {

                //
            }
        };
    }

    @Provides
    @Singleton
    PlayerUIController provideController() {
        return new PlayerUIController();
    }

    @Provides
    @Singleton
    PlayerAdLogicController provideComponentController() {
        return new PlayerAdLogicController();
    }

    @Provides
    @Singleton
    AdRetriever provideAdRetriever() {
        return new AdRetriever();
    }

    @Provides
    @Singleton
    CuePointsRetriever provideCuePointsRetriever() {
        return new CuePointsRetriever();
    }

    @Provides
    @Singleton
    @Named("player")
    Fsm playerAdLogicControllerApi() {
        return FsmPlayerApi.playerLoading(Fsm.class);

    }

    @Provides
    @Singleton
    AdPlayingMonitor provideAdPlayingMonitor(FsmPlayer player) {
        return new AdPlayingMonitor(player);
    }

    @Provides
    @Singleton
    @Named("cuepoint")
    String provideCuePoint(Application application){
        return provideSharedPreferences(application).getString("cuepoint",StateFactory.cuePoint());

    }


    @Provides
    @Singleton
    @Named("cuepointUrl")
    String provideCuePointUrl(Application application){
        return provideSharedPreferences(application).getString("cuepointUrl",StateFactory.cuePointUrl());

    }

    @Provides
    @Singleton
    @Named("cuepointY")
    String provideCuePointY(){
        return CuePointCallBackAd.cuepoint1();

    }

    @Provides
    @Singleton
    @Named("cuepointN")
    String provideCuePointN(){
    return CuePointCallBackAd.cuepoint2();
    }

    @Provides
    @Singleton
    @Named("cuepointW")
    String provideCuePointW(){
        return CuePointCallBackAd.cuepoint3();

    }


    @Provides
    @Singleton
    @Named("cuepointZ")
    String provideCuePointZ(){
        return CuePointCallBackAd.cuepoint5();

    }






    @Provides
    @Singleton
    @Named("adplayingY")
    String provideadplayingY(){
        return CuePointCallBackAd.cuepoint1();

    }

    @Provides
    @Singleton
    @Named("adplayingN")
    String provideadplayingN(){

        return AdPlaying.cuepoint2();

    }

    @Provides
    @Singleton
    @Named("adplayingW")
    String provideadplayingW(){
        return AdPlaying.cuepoint3();

    }


    @Provides
    @Singleton
    @Named("adplayingZ")
    String provideadplayingZ(){
        return AdPlaying.cuepoint5();

    }


    @Provides
    @Singleton
    CuePointMonitor provideCuePointMonitor(FsmPlayer fsmPlayer) {

        return new CuePointMonitor(fsmPlayer) {
            @Override
            public int networkingAhead() {
                return 5000;
            }
        };
    }

    @Provides
    @Singleton
    AdMediaModel provideAdMediaModel() {
        MediaModel ad1 = MediaModel
                .ad("https://iab-publicfiles.s3.amazonaws.com/vast/VAST-4.0-Short-Intro.mp4",
                        "https://codecanyon.net/user/yobex", false);

        final List<MediaModel> list = new ArrayList<>();
        list.add(ad1);

        return  new  AdMediaModel(list) {
            @Nullable
            @Override
            public MediaModel nextAD() {
                return !list.isEmpty() ? list.get(0) : null;
            }
        };

    }

    @Provides
    @Singleton
    AdInterface provideAdInterfaceNoPreroll() {

        // using the fake generated AdMediaModel to do has the returned data.
        return new AdInterface() {
            @Override
            public void fetchAd(AdRetriever retriever, RetrieveAdCallback callback) {
                callback.onReceiveAd(provideAdMediaModel());
            }

            @Override
            public void fetchQuePoint(CuePointsRetriever retriever, CuePointCallBack callBack) {

                callBack.onCuePointReceived(null);

            }
        };
    }

    @Provides
    @Singleton
    VpaidClient provideVpaidClient() {
        return new VpaidClient() {
            @Override
            public void init(MediaModel ad) {


                //

            }

            @Override
            public void notifyAdError(int code, String error) {

                //

            }

            @Override
            public void notifyVideoEnd() {

                //

            }

            @Override
            public String getVastXml() {
                return null;
            }
        };
    }




    @Provides
    @Singleton
    PlaybackSettingMenu providePlaybackSettingMenu() {
        return new PlaybackSettingMenu();
    }

}
