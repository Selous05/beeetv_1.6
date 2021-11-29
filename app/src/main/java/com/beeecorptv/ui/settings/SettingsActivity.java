package com.beeecorptv.ui.settings;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.beeecorptv.util.Constants.AUTO_PLAY;
import static com.beeecorptv.util.Constants.EXTENTIONS;
import static com.beeecorptv.util.Constants.PLAYER_ASPECT_RATIO;
import static com.beeecorptv.util.Constants.PREMUIM;
import static com.beeecorptv.util.Constants.SUBSCRIPTIONS;
import static com.beeecorptv.util.Constants.SUBS_BACKGROUND;
import static com.beeecorptv.util.Constants.SUBS_SIZE;
import static com.beeecorptv.util.Constants.SWITCH_PUSH_NOTIFICATION;
import static com.beeecorptv.util.Constants.WIFI_CHECK;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beeecorptv.R;
import com.beeecorptv.data.model.auth.UserAuthInfo;
import com.beeecorptv.data.repository.AuthRepository;
import com.beeecorptv.databinding.ActivitySettingBinding;
import com.beeecorptv.ui.login.LoginActivity;
import com.beeecorptv.ui.manager.AdsManager;
import com.beeecorptv.ui.manager.AuthManager;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.plans.PlansAdapter;
import com.beeecorptv.ui.profile.EditProfileActivity;
import com.beeecorptv.ui.splash.SplashActivity;
import com.beeecorptv.ui.viewmodels.LoginViewModel;
import com.beeecorptv.ui.viewmodels.MoviesListViewModel;
import com.beeecorptv.ui.viewmodels.SettingsViewModel;
import com.beeecorptv.util.SpacingItemDecoration;
import com.beeecorptv.util.Tools;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class SettingsActivity extends AppCompatActivity {


    ActivitySettingBinding binding;


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MoviesListViewModel moviesListViewModel;

    @Inject
    AuthRepository authRepository;

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TokenManager tokenManager;

    @Inject
    SettingsManager settingsManager;

    @Inject
    AdsManager adsManager;

    @Inject
    AuthManager authManager;


    private LoginViewModel loginViewModel;

    private SettingsViewModel settingsViewModel;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(binding.coordinator, settingsManager.getSettings().getAppName() + getString(R.string.need_storage_permission), LENGTH_INDEFINITE)
                    .setAction(R.string.grant, v -> ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 199)).show();
        }


        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);

        settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);

        moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);

        Tools.setSystemBarTransparent(this);
        Tools.hideSystemPlayerUi(this,true,0);

        onLoadAppLogo();
        setButtonsUtilities();
        onLoadAppBar();
        onCheckAuth();
        onLogout();
        onLoadAboutUs();
        onLoadPrivacyPolicy();
        onClearCache();
        onLoadEditProfile();
        onClearRoomDatabase();
        onClearWatchHistory();

        settingsViewModel.getSettingsDetails();
        settingsViewModel.getPlans();
        binding.subcribeButton.setOnClickListener(v -> settingsViewModel.plansMutableLiveData.observe(this, plans -> {

               final Dialog dialog = new Dialog(this);
               dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
               dialog.setContentView(R.layout.dialog_plans_display);
               dialog.setCancelable(true);
               dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

               WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
               lp.copyFrom(dialog.getWindow().getAttributes());

               lp.gravity = Gravity.BOTTOM;
               lp.width = MATCH_PARENT;
               lp.height = MATCH_PARENT;

               RecyclerView recyclerViewPlans = dialog.findViewById(R.id.recycler_view_plans);
               PlansAdapter plansAdapter = new PlansAdapter();
               recyclerViewPlans.setHasFixedSize(true);
               recyclerViewPlans.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
               recyclerViewPlans.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));
               recyclerViewPlans.setAdapter(plansAdapter);
               plansAdapter.addCasts(plans.getPlans(),settingsManager);


               dialog.findViewById(R.id.bt_close).setOnClickListener(x ->

               dialog.dismiss());

               dialog.show();
               dialog.getWindow().setAttributes(lp);


           }));

        binding.btnLogin.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, LoginActivity.class)));




        if (tokenManager.getToken().getAccessToken() == null) {
            binding.btnLogin.setVisibility(View.VISIBLE);

        }else {

            binding.btnLogin.setVisibility(GONE);
        }



        binding.cacehSize.setText(getString(R.string.sub_setting_clear_cache_start) + " " + Tools.readableFileSize((Tools.getDirSize(getCacheDir())) + Tools.getDirSize(getExternalCacheDir())) + " " + getString(R.string.sub_setting_clear_cache_end));

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(binding.coordinator, settingsManager.getSettings().getAppName() + getString(R.string.need_storage_permission), LENGTH_INDEFINITE)
                    .setAction(R.string.grant, v -> ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 199)).show();
        }

    }


    boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private void onClearWatchHistory() {

        binding.clearMyWatchHistory.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_mylist);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {

                moviesListViewModel.deleteHistory();

                Toast.makeText(SettingsActivity.this, R.string.history_cleared, Toast.LENGTH_SHORT).show();

                dialog.dismiss();


            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });

    }



    private void onLoadEditProfile() {

        binding.btnEditProfile.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class)));
    }


    private void onClearRoomDatabase() {

        binding.ClearMyList.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_mylist);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {

                moviesListViewModel.deleteAllMovies();

                Toast.makeText(this, "My List has been cleared !", Toast.LENGTH_SHORT).show();

                dialog.dismiss();


            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });


    }

    public void onClearCache() {

        binding.linearLayoutCleaCache.setOnClickListener(v -> {


            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_cache);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {
                deleteCache(this);

                Toast.makeText(this, "The App cache has been cleared !", Toast.LENGTH_SHORT).show();


                dialog.dismiss();

            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);


        });


    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteFile(dir);
        } catch (Exception e) {

            Timber.d("Error Deleting : %s", e.getMessage());
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String child : children) {
                    deletedAll = deleteFile(new File(file, child)) && deletedAll;
                }
            } else {

                boolean deleted = file.delete();
                if (!deleted) {

                    Timber.i("File Is not Deleted");
                } else {

                    Timber.i("File Deleted");
                }
            }
        }

        return deletedAll;
    }


    private void onLoadPrivacyPolicy() {


        binding.privacyPolicy.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_gdpr_basic);
            dialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            TextView reportMovieName = dialog.findViewById(R.id.tv_content);
            reportMovieName.setText(settingsManager.getSettings().getPrivacyPolicy());

            dialog.findViewById(R.id.bt_accept).setOnClickListener(v1 -> dialog.dismiss());

            dialog.findViewById(R.id.bt_decline).setOnClickListener(v12 -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);


        });


    }

    @SuppressLint("DefaultLocale")
    private void onLoadAboutUs() {


        // About Us - EasyPlex
        binding.aboutus.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_about);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            ImageView imageView = dialog.findViewById(R.id.logo_aboutus);
            TextView textView = dialog.findViewById(R.id.app_version);
            textView.setText("Version : "+settingsManager.getSettings().getLatestVersion());
            Tools.loadMainLogo(this,imageView);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.findViewById(R.id.bt_getcode).setOnClickListener(v15 -> {
                if (settingsManager.getSettings().getAppUrlAndroid().isEmpty()) {


                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://codecanyon.net/user/yobex")));

                } else {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getAppUrlAndroid())));

                }

            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(v14 -> dialog.dismiss());

            dialog.findViewById(R.id.app_url).setOnClickListener(v13 -> {

                if(settingsManager.getSettings().getAppUrlAndroid() != null && !settingsManager.getSettings().getAppUrlAndroid().trim().isEmpty()) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getAppUrlAndroid())));


                }else {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://codecanyon.net/user/yobex")));

                }

            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });



        if (tokenManager.getToken().getAccessToken() != null) {


            binding.btnEditProfile.setVisibility(View.VISIBLE);
            binding.logout.setVisibility(View.VISIBLE);

        }else {

            binding.btnEditProfile.setVisibility(GONE);
            binding.logout.setVisibility(View.GONE);
            binding.subcribeButton.setVisibility(GONE);

        }


    }

    private void onLogout() {

        binding.logout.setOnClickListener(v -> {

            tokenManager.deleteToken();
            authManager.deleteAuth();
            settingsManager.deleteSettings();
            adsManager.deleteAds();
            moviesListViewModel.deleteHistory();
            deleteCache(this);
            moviesListViewModel.deleteAllMovies();
            startActivity(new Intent(this, SplashActivity.class));
            finish();

        });

    }

    private void onCheckAuth() {



        loginViewModel.getAuthDetails();
        loginViewModel.authDetailMutableLiveData.observe(this, auth -> {


            if (auth !=null) {


                if (auth.getPremuim() == 1) {

                    try {
                        onCancelSubscription(auth);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                binding.authName.setVisibility(View.VISIBLE);
                binding.authEmail.setVisibility(View.VISIBLE);
                binding.authName.setText(auth.getName());
                binding.authEmail.setText(auth.getEmail());
                binding.btnLogin.setVisibility(GONE);

                if (auth.getPremuim() == 1) {

                    binding.btnPremuim.setVisibility(View.VISIBLE);
                    sharedPreferencesEditor.putInt(PREMUIM, 1).apply();
                    binding.btnPremuim.setVisibility(View.VISIBLE);
                    binding.btnPremuim.setText(auth.getPackName());
                    binding.membershipExpireIn.setVisibility(View.VISIBLE);

                    if (auth.getExpiredIn() != null && !auth.getExpiredIn().trim().isEmpty()) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date releaseDate = sdf1.parse(auth.getExpiredIn());
                            assert releaseDate != null;
                            binding.membershipExpireIn.setText("Valid until : "+sdf1.format(releaseDate));
                        } catch (ParseException e) {

                            Timber.d("%s", Arrays.toString(e.getStackTrace()));

                        }
                    } else {
                        binding.membershipExpireIn.setText("");
                    }


                    binding.subcribeButton.setVisibility(GONE);
                    binding.cancelSubcriptionButton.setVisibility(View.VISIBLE);
                    binding.cancelSubcriptionButton.setOnClickListener(v -> {


                        final Dialog dialog = new Dialog(SettingsActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_confirm_cancel_subscription);
                        dialog.setCancelable(true);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


                        dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {


                            if (auth.getType() !=null && !auth.getType().isEmpty() && auth.getType().equals("paypal")) {

                                authRepository.cancelAuthSubcriptionPaypal().subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<>() {
                                            @Override
                                            public void onSubscribe(@NotNull Disposable d) {

                                                //

                                            }

                                            @Override
                                            public void onNext(@NotNull UserAuthInfo userAuthInfo) {

                                                Toast.makeText(SettingsActivity.this, SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(SettingsActivity.this, SplashActivity.class));
                                                finish();
                                            }

                                            @SuppressLint("ClickableViewAccessibility")
                                            @Override
                                            public void onError(@NotNull Throwable e) {

                                                //
                                            }

                                            @Override
                                            public void onComplete() {

                                                //

                                            }
                                        });

                            }else if (auth.getType() !=null && !auth.getType().isEmpty() && auth.getType().equals("stripe")) {


                                authRepository.cancelAuthSubcription().subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<>() {
                                            @Override
                                            public void onSubscribe(@NotNull Disposable d) {

                                                //

                                            }

                                            @Override
                                            public void onNext(@NotNull UserAuthInfo userAuthInfo) {

                                                Toast.makeText(SettingsActivity.this, SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(SettingsActivity.this, SplashActivity.class));
                                                finish();
                                            }

                                            @SuppressLint("ClickableViewAccessibility")
                                            @Override
                                            public void onError(@NotNull Throwable e) {

                                                //
                                            }

                                            @Override
                                            public void onComplete() {

                                                //

                                            }
                                        });

                            }


                            dialog.dismiss();

                        });

                        dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


                        dialog.show();
                        dialog.getWindow().setAttributes(lp);


                    });

                } else {

                    binding.btnPremuim.setVisibility(GONE);
                    sharedPreferencesEditor.putInt(PREMUIM, 0).apply();
                    binding.authFree.setVisibility(View.VISIBLE);
                    binding.subcribeButton.setVisibility(View.VISIBLE);

                }

            }else {

                binding.authEmail.setVisibility(GONE);
                binding.authEmail.setVisibility(View.GONE);
                binding.btnLogin.setVisibility(View.VISIBLE);

            }



        });


    }


    private void onCancelSubscription(UserAuthInfo auth) throws ParseException {


        if (auth.getType() !=null && !auth.getType().isEmpty()) {

            if (auth.getType().equals("paypal")) {

                long millis=System.currentTimeMillis();
                java.sql.Date date=new java.sql.Date(millis);

                if (authManager.getUserInfo().getExpiredIn() != null && !authManager.getUserInfo().getExpiredIn().trim().isEmpty()) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    try {

                        Date releaseDate1 = sdf1.parse(authManager.getUserInfo().getExpiredIn());
                        Date releaseDate2 = sdf1.parse(String.valueOf(date));

                        assert releaseDate1 != null;
                        int result =  releaseDate1.compareTo(releaseDate2);

                        if (result  <= 0) {

                            loginViewModel.cancelAuthSubscription();
                            loginViewModel.authCancelPlanMutableLiveData.observe(this, authx -> {

                                if (authx !=null) {

                                    Toast.makeText(SettingsActivity.this, SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();

                                }

                            });
                        }


                    } catch (ParseException e) {

                        Timber.d("%s", Arrays.toString(e.getStackTrace()));

                    }
                }


            }else if (auth.getType().equals("stripe")){


                loginViewModel.getAuthDetails();
                loginViewModel.getStripeSubStatusDetails();

                loginViewModel.stripeStatusDetailMutableLiveData.observe(this, authx -> {

                    if (authx.getActive() <= 0 ) {

                        loginViewModel.cancelAuthSubscription();
                        loginViewModel.authCancelPlanMutableLiveData.observe(this, cancelsubs -> {

                            if (cancelsubs !=null) {

                                Toast.makeText(this, SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();

                            }

                        });

                    }

                });
            }else {


                long millis=System.currentTimeMillis();
                java.sql.Date date=new java.sql.Date(millis);

                if (authManager.getUserInfo().getExpiredIn() != null && !authManager.getUserInfo().getExpiredIn().trim().isEmpty()) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    try {

                        Date releaseDate1 = sdf1.parse(authManager.getUserInfo().getExpiredIn());
                        Date releaseDate2 = sdf1.parse(String.valueOf(date));

                        assert releaseDate1 != null;
                        int result =  releaseDate1.compareTo(releaseDate2);

                        if (result  <= 0) {

                            loginViewModel.cancelAuthSubscription();
                            loginViewModel.authCancelPlanMutableLiveData.observe(this, authx -> {

                                if (authx !=null) {

                                    Toast.makeText(this, SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();

                                }

                            });
                        }


                    } catch (ParseException e) {

                        Timber.d("%s", Arrays.toString(e.getStackTrace()));

                    }
                }
            }

        }

    }

    // Load AppBar
    private void onLoadAppBar() {

        Tools.loadAppBar(binding.scrollView, binding.toolbar);

    }


    // Load App Logo
    private void onLoadAppLogo() {

    Tools.loadMiniLogo(this,binding.logoImageTop);

    }



    private void setButtonsUtilities() {

        onLoadwifiSwitch();
        onLoadNotificationPushSwitch();
        setAutoPlaySwitch();
        setExtentions();

        binding.currentSize.setText(String.format("current size : %s", sharedPreferences.getString(SUBS_SIZE, "16f")));

        binding.substitleSize.setOnClickListener(v -> {

            ArrayList<String> fontSize = new ArrayList<>();


            fontSize.add("10f");
            fontSize.add("12f");
            fontSize.add("14f");
            fontSize.add("16f");
            fontSize.add("20f");
            fontSize.add("24f");
            fontSize.add("30f");

            String[] charSequenceSubsSize = new String[fontSize.size()];
            for (int i = 0; i < fontSize.size(); i++) {
                charSequenceSubsSize[i] = String.valueOf(fontSize.get(i));

            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
            builder.setTitle("Font Size..");
            builder.setCancelable(true);
            builder.setItems(charSequenceSubsSize, (dialogInterface, wich) -> {

                sharedPreferencesEditor.putString(SUBS_SIZE, fontSize.get(wich)).apply();
                sharedPreferences.getString(SUBS_SIZE, "16f");
                binding.currentSize.setText(String.format("Current Size : %s", sharedPreferences.getString(SUBS_SIZE, "16f")));
                dialogInterface.dismiss();


            });

            builder.show();

        });


        binding.currentBackgroundColor.setText(String.format("Current color : %s", sharedPreferences.getString(SUBS_BACKGROUND, "Transparent")));


        binding.substitlesBackground.setOnClickListener(v -> {

            ArrayList<String> fontSize = new ArrayList<>();

            fontSize.add("Transparent");
            fontSize.add("Black");
            fontSize.add("Grey");
            fontSize.add("Red");
            fontSize.add("Yellow");
            fontSize.add("Green");
            fontSize.add("Blue");

            String[] charSequenceSubsSize = new String[fontSize.size()];
            for (int i = 0; i < fontSize.size(); i++) {
                charSequenceSubsSize[i] = String.valueOf(fontSize.get(i));

            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
            builder.setTitle("Backgrond Font Color..");
            builder.setCancelable(true);
            builder.setItems(charSequenceSubsSize, (dialogInterface, wich) -> {

                sharedPreferencesEditor.putString(SUBS_BACKGROUND, fontSize.get(wich)).apply();
                sharedPreferences.getString(SUBS_BACKGROUND, "Transparent");
                binding.currentBackgroundColor.setText(String.format("Current color : %s", sharedPreferences.getString(SUBS_BACKGROUND, "Transparent")));

                dialogInterface.dismiss();


            });

            builder.show();

        });








        binding.currentPlayerAspectRatio.setText(String.format("Current Aspect Ratio : %s", sharedPreferences.getString(PLAYER_ASPECT_RATIO, "default")));


        binding.playerAspectRatio.setOnClickListener(v -> {

            ArrayList<String> fontSize = new ArrayList<>();

            fontSize.add(getString(R.string.player_default));
            fontSize.add(getString(R.string.player_43));
            fontSize.add(getString(R.string.player_169));
            fontSize.add(getString(R.string.player_fullscreen));
            fontSize.add(getString(R.string.player_room));

            String[] charSequenceSubsSize = new String[fontSize.size()];
            for (int i = 0; i < fontSize.size(); i++) {
                charSequenceSubsSize[i] = String.valueOf(fontSize.get(i));

            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
            builder.setTitle("Aspect Ratio..");
            builder.setCancelable(true);
            builder.setItems(charSequenceSubsSize, (dialogInterface, wich) -> {

                sharedPreferencesEditor.putString(PLAYER_ASPECT_RATIO, fontSize.get(wich)).apply();
                sharedPreferences.getString(PLAYER_ASPECT_RATIO, "default");
                binding.currentPlayerAspectRatio.setText(String.format("Current Aspect Ratio : %s", sharedPreferences.getString(PLAYER_ASPECT_RATIO, "default")));

                dialogInterface.dismiss();


            });

            builder.show();

        });


    }

    private void setExtentions() {


        if (!sharedPreferences.getBoolean(EXTENTIONS, false)) {
            binding.switchPushExtentions.setChecked(false);
        }

        binding.switchPushExtentions.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(EXTENTIONS, true).apply();


            } else {

                sharedPreferencesEditor.putBoolean(EXTENTIONS, false).apply();

            }

        });

    }

    private void setAutoPlaySwitch() {

        // Detect AutoPlay ON/OFF Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(AUTO_PLAY, true)) {
            binding.autoplaySwitch.setChecked(false);
        }

        binding.autoplaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(AUTO_PLAY, true).apply();


            } else {

                sharedPreferencesEditor.putBoolean(AUTO_PLAY, false).apply();

            }

        });

    }

    private void onLoadNotificationPushSwitch() {

        // Detect Notification ON/OFF Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(SWITCH_PUSH_NOTIFICATION, true)) {

            binding.switchPushNotification.setChecked(false);
        }


        binding.switchPushNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(SWITCH_PUSH_NOTIFICATION, true).apply();

                FirebaseMessaging.getInstance().subscribeToTopic("/topics/all");

            } else {

                sharedPreferencesEditor.putBoolean(SWITCH_PUSH_NOTIFICATION, false).apply();

                FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/all");

            }

        });

    }

    private void onLoadwifiSwitch() {

        // Detect Wifi-Only Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(WIFI_CHECK, true)) {

            binding.wifiSwitch.setChecked(false);

        }

        binding.wifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(WIFI_CHECK, true).apply();

            } else {

                sharedPreferencesEditor.putBoolean(WIFI_CHECK, false).apply();
            }

        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;

    }
}
