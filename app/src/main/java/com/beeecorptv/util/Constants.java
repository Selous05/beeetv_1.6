package com.beeecorptv.util;

import android.util.Base64;

import com.stringcare.library.SC;

import java.nio.charset.StandardCharsets;

import static com.beeecorptv.BuildConfig.APP_STARUP;

/**
 * Application CONSTANTS.
 *
 * @author Yobex.
 */
public abstract class Constants {

    private Constants(){


    }


    // this is your laravel main Server api ( Change this encoded value to Your Server Encoded URL ( BASE64 ) )
    // for ex http://192.168.1.130/public/api/          !note : url must be ending with /
    public static final String SERVER_ENCODED = SC.obfuscate("aHR0cDovLzU0Ljg3LjIxOS4xODgvdXBkYXRlL3B1YmxpYy9hcGkv");



    // Your Purchase key from codecanyon
    public static final String PURCHASE_KEY = SC.obfuscate("1f3db360-32b5-40e8-a827-af08fa6ed07e");



    // Enter your Google Client Id
    public static final String GOOGLE_CLIENT_ID = SC.obfuscate("691992077344-63kp82td2pmqrr81q556flv4so8ffelf.apps.googleusercontent.com");

    // this is your Authorisation Bearer to access to your api inside the app without it all requests will be rejected
    // if not Authorisation Header is correctly passed
    // encode this using the base64
    // add the correct non encrypted key to your .env file in the TOKEN field

    // for ex : if you are using a key like that :  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
    // this is non ecrypted key to use inside the .env file
    // but here in the AUTHORISATION_BEARER value you must enter the encoded value
    // take a look at the upgrade 1.5 page http://demo.yobdev.live/upgrade1.5/
    // if you don't want to work with the Authorisation Bearer you can just download the AuthenticateOptionally.php file from the upgrade 1.5 and you can skip this option
    public static final String AUTHORISATION_BEARER = SC.obfuscate("YmVlZWNvcnB0dmFuZHJvaWRhcHA=");



    // this is not your laravel api , this key will be used to authorise all api calls inside your app ( this key must be the same as API_KEY value in your .env file otherwise you will get
    //  "Invalid access key" error in your api calls )
    // for ex http://192.168.1.130/public/api/movies/featured/p2lbgWkFrykA4QyUmpHihzmc5BNzIABq

    public static final String API_KEY =  SC.obfuscate("p2lbgWkFrykA4QyUmpHihzmc5BNzIABq");


    // Don't Change
    public static final String IMDB_ENCODED = "aHR0cHM6Ly9hcGkudGhlbW92aWVkYi5vcmcvMy8=";


    // Don't Change
    public static final String SUBS_ENCODED = "aHR0cHM6Ly9yZXN0Lm9wZW5zdWJ0aXRsZXMub3JnLw==";

    // Don't Change
    public static final String HXFILE_ENCODED = SC.obfuscate("aHR0cDovL2h4ZmlsZS5jby9hcGkvZmlsZS8=");

    // Don't Change
    public static final String IMDB_BASE_URL = decodeImdbApi();


    // Don't Change
    public static final String SERVER_BASE_URL = decodeServerMainApi();


    // Don't Change
    public static final String SERVER_OPENSUBS_URL = decodeSubsApi();


    // Don't Change
    public static final String AUTHORISATION_BEARER_STRING = decodeAuthorisationBearer();


    // Don't Change
    public static final String HXFILE = decodeHXFILE();


    // Don't Change
    private static String decodeServerMainApi(){
        byte[] valueDecoded;
        valueDecoded = Base64.decode(SC.reveal(SERVER_ENCODED).getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    // Don't Change
    private static String decodeAuthorisationBearer() {

        byte[] valueDecoded;
        valueDecoded = Base64.decode(SC.reveal(AUTHORISATION_BEARER).getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    // Don't Change
    private static String decodeImdbApi(){

        byte[] valueDecoded;
        valueDecoded = Base64.decode(IMDB_ENCODED.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    // Don't Change
    private static String decodeSubsApi(){

        byte[] valueDecoded;
        valueDecoded = Base64.decode(SUBS_ENCODED.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }


    // Don't Change
    private static String decodeHXFILE(){

        byte[] valueDecoded;
        valueDecoded = Base64.decode(SC.reveal(HXFILE_ENCODED).getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }




    //Minimum Video you want to buffer while Playing
    public static final int MIN_BUFFER_DURATION = 2000;
    //Max Video you want to buffer during PlayBack
    public static final int MAX_BUFFER_DURATION = 5000;
    //Min Video you want to buffer before start Playing it
    public static final int MIN_PLAYBACK_START_BUFFER = 1500;
    //Min video You want to buffer when user resumes video
    public static final int MIN_PLAYBACK_RESUME_BUFFER = 2000;


    public static final String MOVIE ="MOVIE";
    public static final String SERIE ="SERIE";
    public static final String EMPTY_URL ="about:blank";
    public static final String PAYPAL_CLIENT_ID = "clientid";
    public static final String PAYMENT = "payment";
    public static final String SUBS_SIZE = "subs_size";
    public static final String SUBS_BACKGROUND = "subs_background";
    public static final String PLAYER_ASPECT_RATIO = "player_aspect_ratio";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String ARG_MOVIE = "movie";
    public static final String ARG_CAST= "cast";
    public static final String ARG_MOVIE_HISTORY = "history";
    public static final String ARG_PAYMENT = "payment";
    public static final String SUBSCRIPTIONS = "You Subscription has ended !";
    public static final String MOVIE_LINK = "link";


    // Buttons Switch Constants
    public static final String WIFI_CHECK = "wifi_check";
    public static final String SWITCH_PUSH_NOTIFICATION = "switch_push_notification";
    public static final String AUTO_PLAY = "autoplay_check";
    public static final String EXTENTIONS = "enable_extentions";


    // Auth Constants
    public static final String PREMUIM = "premuim";
    public static final String PREMUIM_MANUAL = "premuim_manual";
    public static final String AUTH_NAME = "name";
    public static final String AUTH_EMAIL = "email";
    public static final String AUTH_ID = "id";
    public static final String AUTH_EXPIRED_DATE = "expired_in";
    public static final String ERROR = "Error";


    // Ads Constants
    public static final String ADS_LINK = "link";
    public static final String ADS_CLICKTHROUGHURL = "clickThroughUrl";


    // Admob



    // Remote
    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCEPT = "Accept";

    // Substitles


    public static final String ZIP_FILE_NAME4 = "1.ass";
    public static final String ZIP_FILE_NAME = "1.srt";
    public static final String ZIP_FILE_NAME2 = "1.vtt";
    public static final String ZIP_FILE_NAME3 = "1.ssa";
    public static final String SUBSTITLE_LOCATION = "file:///storage/emulated/0/Android/data/";
    public static final String SUBSTITLE_SUB_FILENAME_ZIP = "/subs.zip";


    // Settings Constants
    public static final String APP_VERSION = "latestVersion";
    public static final String APP_NAME = "app_name";
    public static final String AD_FACEBOOK_INTERSTITIAL_SHOW = "facebook_show_interstitial";
    public static final String AD_FACEBOOK_NATIVE_ENABLE = "ad_face_audience_native";
    public static final String AD_FACEBOOK_NATIVE_UNIT_ID = "ad_unit_id_facebook_native_audience";
    public static final String AD_INTERSTITIAL_SHOW = "ad_show_interstitial";
    public static final String AD_INTERSTITIAL = "ad_interstitial";
    public static final String AD_INTERSTITIAL_UNIT = "ad_unit_id_interstitial";
    public static final String AD_BANNER = "ad_banner";
    public static final String AD_BANNER_UNIT = "ad_unit_id_banner";
    public static final String TMDB = "tmdb_api_key";
    public static final String APP_URL_ANDROID = "app_url_android";
    public static final String PRIVACY_POLICY = "privacy_policy";
    public static final String LATEST_VERSION = "latestVersion";
    public static final String UPDATE_TITLE = "update_title";
    public static final String RELEASE_NOTES = "releaseNotes";
    public static final String PAYPAL_AMOUNT = "paypal_amount";
    public static final String FEATURED_HOME_NUMBERS = "featured_home_numbers";
    public static final String IMDB_COVER_PATH = "imdb_cover_path";
    public static final String AUTOSUBSTITLES = "autosubstitles";
    public static final String ANIME = "anime";
    public static final String ENABLE_STREAMING = "streaming";
    public static final String ADS_SETTINGS = "ads";
    public static final String AD_INTERSTITIAL_FACEEBOK_ENABLE = "ad_face_audience_interstitial";
    public static final String AD_INTERSTITIAL_FACEEBOK_UNIT_ID = "ad_unit_id_facebook_interstitial_audience";
    public static final String AD_INTERSTITIAL_APPODEAL_UNIT_ID = "ad_unit_id__appodeal_rewarded";
    public static final String AD_BANNER_APPODEAL_UNIT_ID = "ad_unit_id_appodeal_banner_audience";
    public static final String AD_NATIVEADS_ADMOB_UNIT_ID = "admob_native_ads";
    public static final String AD_NATIVEADS_ADMOB_ENABLE = "ad_unit_id_native_enable";
    public static final String PAYPAL_CURRENCY = "paypal_currency";
    public static final String AD_INTERSTITIAL_APPOBEAL_ENABLE = "ad_unit_id_native";
    public static final String AD_INTERSTITIAL_APPOBEAL_SHOW = "appodeal_show_interstitial";
    public static final String DEFAULT_PAYMENT = "default_payment";
    public static final String PREFS2 = APP_STARUP;
    public static final String AD_BANNER_FACEEBOK_ENABLE = "ad_face_audience_banner";
    public static final String AD_BANNER_FACEEBOK_UNIT_ID = "ad_unit_id_facebook_banner_audience";
    public static final String DEFAULT_NETWORK = "default_network";
    public static final String DEFAULT_NETWORK_PLAYER = "default_network_player";
    public static final String STARTAPP_ID = "startapp_id";
    public static final String ADMOB_REWARD = "ad_unit_id_rewarded";
    public static final String FACEBOOK_REWARD = "ad_unit_id__facebook_rewarded";
    public static final String UNITY_GAME_ID = "unity_game_id";
    public static final String WATCH_ADS_TO_UNLOCK = "wach_ads_to_unlock";
    public static final String WATCH_ADS_TO_UNLOCK_PLAYER = "wach_ads_to_unlock_player";
    public static final String CUSTOM_MESSAGE = "custom_message";
    public static final String ENABLE_CUSTOM_MESSAGE = "enable_custom_message";
    public static final String STRIPE_PUBLISHABLE_KEY = "stripe_publishable_key";
    public static final String STRIPE_SECRET_KEY = "stripe_secret_key";
    public static final String APPODEAL_REWARD = "ad_unit_id__appodeal_rewarded";
    public static final String APPODEAL_BANNER = "appodeal_banner";
    public static final String DOWNLOADS_PREMUIM_ONLY = "download_premuim_only";
    public static final String NEXT_EPISODE_TIMER = "next_episode_timer";
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";
    public static final String INSTAGRAM = "instagram";
    public static final String YOUTUBE = "youtube";
    public static final String ENABLE_SERVER_DIALOG_SELECTION = "server_dialog_selection";
    public static final String ENABLE_CUSTOM_BANNER = "enable_custom_banner";
    public static final String CUSTOM_BANNER_IMAGE = "custom_banner_image";
    public static final String CUSTOM_BANNER_IMAGE_LINK = "custom_banner_image_link";
    public static final String MANTENANCE_MESSAGE = "mantenance_mode_message";
    public static final String MANTENANCE_MODE = "mantenance_mode";
    public static final String SPLASH_IMAGE = "splash_image";
    public static final String DEFAULT_YOUTUBE_QUALITY = "default_youtube_quality";
    public static final String ALLOW_ADM_DOWNLOADS = "allow_adm";
    public static final String DEFAULT_DOWNLOADS_OPTION = "default_downloads_options";
    public static final String STARTAPP_BANNER = "startapp_banner";
    public static final String STARTAPP_INTER = "startapp_interstitial";
    public static final String VLC = "vlc";
    public static final String OFFLINE_RESUME = "resume_offline";
    public static final String PINNED = "enable_pinned";
    public static final String UPCOMING = "enable_upcoming";
    public static final String PREVIEWS = "enable_previews";
    public static final String USER_AGENT = "user_agent";
    public static final String UNITYADS_BANNER = "unityads_banner";
    public static final String UNITYADS_INTER = "unityads_interstitial";
    public static final String ENABLE_BOTTOM_ADS_HOME = "enable_banner_bottom";
    public static final String DEFAULT_MEDIA_COVER = "default_media_placeholder_path";
    public static final String DEFAULT_DOWNLOAD_DIRECTORY = "cache";
    public static final String ENABLE_WEBVIEW = "enable_webview";
    public static final String ENABLE_LEFT_NAVBAR = "leftnavbar";
    public static final String ENABLE_FAVONLINE = "favoriteonline";
    public static final String TRAILER_OPTIONS = "default_trailer_default";
    public static final String NOTIFICATION_SEPARATED = "notification_separated";
    public static final String PACKAGE_NAME_ANDROID_APP = "app_packagename";
    public static final String DEFAULT_CAST_OPTION = "default_cast_option";
    public static final String DOWNLOAD_SEPARATED = "separate_download";
    public static final String DOWNLOAD_ENABLE = "enable_download";
    public static final String ENABLE_VPN_DETECTION = "vpn";
    public static final String ENABLE_ROOT_DETECTION = "root_detection";
    public static final String ENABLE_DIRECT_STREAM_FROM_NOTIFICATION = "notification_style";
    public static final String APPNEXT_BANNER = "appnext_banner";
    public static final String APPNEXT_INTERSTITIAL = "appnext_interstitial";
    public static final String APPNEXT_PLACEMENT_ID = "appnext_placementid";
    public static final String APPNEXT_INTERSTITIAL_SHOW = "appnext_interstitial_show";
    public static final String LIVE_MULTI_SERVERS = "livetv_multi_servers";
    public static final String ENABLE_FORCE_LOGIN = "force_login";
    public static final String FORCE_SUGGEST_AUTH_USERS = "suggest_auth";
    public static final String NETWORKS = "networks";
    public static final String WEBVIEW_LINK_REWARD = "webview_link";
    public static final String VUNGLE_APP_ID = "vungle_appid";
    public static final String VUNGLE_BANNER_ENABLE = "vungle_interstitial";
    public static final String VUNGLE_INTERSTITIAL_ENABLE = "appnext_interstitial";
    public static final String VUNGLE_INTERSTITIAL_PLACEMENT_ID = "appnext_placementid";
    public static final String VUNGLE_BANNER_PLACEMENT_ID = "appnext_placementid";
    public static final String VUNGLE_REWARDS_PLACEMENT_ID = "appnext_placementid";
    public static final String VUNGLE_INTERSTITIAL_SHOW = "vungle_interstitial_show";
    public static final String ENABLE_FLAG_SECURE = "enable_networks";
    public static final String FORCE_EMAIL_CONFIRMATION = "email_verify";
    public static final String FORCE_UPDATE = "force_update";
    public static final String HXFILE_KEY = "hxfile_api_key";
    public static final String SEASONS_STYLE = "season_style";
    public static final String UNITY_SHOW_FREQUENCY = "unity_show";
    public static final String FORCE_PASSWORD_ACCESS = "force_password_access";
    public static final String FORCE_INAPPUPDATE = "force_inappupdate";
    public static final String NETWORKS_LAYOUT_OPTIONS = "default_layout_networks";

    public static final String APPLOVIN_BANNER_ENABLE = "applovin_banner";
    public static final String APPLOVIN_BANNER_UNIT_ID = "applovin_banner_unitid";
    public static final String APPLOVIN_ENABLE_INTERSTITIAL = "applovin_interstitial";
    public static final String APPLOVIN_INTERSTITIAL_UNIT_ID = "applovin_interstitial_unitid";
    public static final String APPLOVIN_REWARD_UNIT_ID = "applovin_reward_unitid";
    public static final String APPLOVIN_INTERSTITIAL_SHOW = "applovin_interstitial_show";
    public static final String UNITY_REWARD_UNIT_ID = "unity_banner_placement_id";
    public static final String UNITY_BANNER_UNIT_ID = "unity_interstitial_placement_id";
    public static final String UNITY_INTERSTITIAL_UNIT_ID = "unity_reward_placement_id";


    // Status
    public static final String CODE = "code";




    public static String PLAYER_HEADER = "";
    public static String PLAYER_USER_AGENT = "EasyPlex";
    public static String APP_PASSWORD = "";


    // TV-SERIES
    public static final String SPECIALS = "Specials";
    public static final String SEASONS = "Seasons: ";
    public static final int UNITY_ADS_BANNER_WIDTH = 320;
    public static final int UNITY_ADS_BANNER_HEIGHT = 50;


    // Player Constants
    public static final long DEFAULT_WEBVIEW_ADS_RUNNING = 10000;

    public static final int CUSTOM_SEEK_CONTROL_STATE = 2; // Every time long press left/right will enter this state
    public static final int EDIT_CUSTOM_SEEK_CONTROL_STATE = 3; // After long press left/right will enter this state
    public static final long DEFAULT_FREQUENCY = 1000;
    public static final String FSMPLAYER_TESTING = "FSM_LOGGING";
    public static final String UPNEXT = "Up Next in : ";
    public static final String EP = "EP";
    public static final String S0 = "S0";
    public static final String E = "E";
    public static final String STREAMING = "streaming";
    public static final String BRX = "brx";
    public static final String TRANSPARENT = "Transparent";
    public static final String FIRST_INSTALL = "first_install";
    public static final String FIRST_PASSWORD_CHECK = "first_password_check";




    // Shared Preferences Constants
    public static final String PREF_FILE = "Preferences";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
}
