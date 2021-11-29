package com.beeecorptv.ui.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.beeecorptv.R;
import com.beeecorptv.data.remote.ErrorHandling;
import com.beeecorptv.ui.base.BaseActivity;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.manager.TokenManager;
import com.beeecorptv.ui.viewmodels.LoginViewModel;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.GlideApp;
import com.beeecorptv.util.Tools;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Objects;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.beeecorptv.util.Constants.SERVER_BASE_URL;

public class PasswordForget extends AppCompatActivity implements HasAndroidInjector {

    private Unbinder unbinder;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    TokenManager tokenManager;

    @Inject
    SettingsManager settingsManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    private LoginViewModel loginViewModel;

    AwesomeValidation validator;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.til_email)
    TextInputLayout tilEmail;



    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.logo_image_top)
    ImageView logoimagetop;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.splash_image)
    ImageView splashImage;



    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.form_container)
    LinearLayout formContainer;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.loader)
    ProgressBar loader;




    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.emailForget)
    LinearLayout emailForget;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tokenEnter)
    LinearLayout tokenEnter;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.token_user)
    TextInputLayout tokenUser;



    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.token_user_email)
    TextInputLayout tokenUserEmail;



    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.token_user_password)
    TextInputLayout tokenUserPassword;




    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.token_user_password_confirmation)
    TextInputLayout tokenUserPasswordConfirmation;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.close)
    ImageView close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password_forget);

        unbinder = ButterKnife.bind(this);

        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);


        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);

        onLoadAppLogo();
        onLoadSplashImage();
        onLoadValitator();
        onSetupRules();


        if (tokenManager.getToken().getAccessToken() != null) {
            startActivity(new Intent(PasswordForget.this, BaseActivity.class));
            finish();

        }


    }

    private void onLoadSplashImage() {

        GlideApp.with(getApplicationContext()).asBitmap().load(settingsManager.getSettings().getSplashImage())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(splashImage);

    }



    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_login)
    void sendEmail() {

        String email = Objects.requireNonNull(tilEmail.getEditText()).getText().toString();
        tilEmail.setError(null);
        validator.clear();


        if (validator.validate()) {


            hideKeyboard();
            loader.setVisibility(View.VISIBLE);
            emailForget.setVisibility(View.GONE);

            loginViewModel.getPasswordForget(email).observe(PasswordForget.this, login -> {

                if (login.status == ErrorHandling.Status.SUCCESS) {

                    emailForget.setVisibility(View.GONE);
                    tokenEnter.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);


                } else {


                    Toast.makeText(this, "No Email Match was found", Toast.LENGTH_SHORT).show();
                    loader.setVisibility(View.GONE);
                    emailForget.setVisibility(View.VISIBLE);

                }

            });

        }

    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.close)
    void uclose() {


        startActivity(new Intent(PasswordForget.this, LoginActivity.class));
        finish();

    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_update_password)
    void updatePassword() {

        String token = tokenUser.getEditText().getText().toString();
        String email = tokenUserEmail.getEditText().getText().toString();
        String password = tokenUserPassword.getEditText().getText().toString();
        String passwordConfirmation = tokenUserPasswordConfirmation.getEditText().getText().toString();

        tilEmail.setError(null);
        tokenUser.setError(null);
        tokenUserPassword.setError(null);
        tokenUserPasswordConfirmation.setError(null);
        validator.clear();

            hideKeyboard();
             loader.setVisibility(View.VISIBLE);
             tokenEnter.setVisibility(View.GONE);

            loginViewModel.getPasswordUpdate(token,email,password,passwordConfirmation).observe(PasswordForget.this, login -> {


                if (login.status == ErrorHandling.Status.SUCCESS) {

                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                    dialog.setContentView(R.layout.dialog_password_updated);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WRAP_CONTENT;
                    lp.height = WRAP_CONTENT;
                    dialog.findViewById(R.id.bt_getcode).setOnClickListener(v -> {

                        startActivity(new Intent(PasswordForget.this, LoginActivity.class));
                        finish();
                    });
                    dialog.findViewById(R.id.bt_close).setOnClickListener(v -> {
                        startActivity(new Intent(PasswordForget.this, LoginActivity.class));
                        finish();
                    });


                    dialog.show();
                    dialog.getWindow().setAttributes(lp);


                } else {

                    loader.setVisibility(View.GONE);
                    tokenEnter.setVisibility(View.VISIBLE);
                    DialogHelper.erroLogin(this);


                }

            });


    }



    private void onLoadValitator() {

        validator = new AwesomeValidation(TEXT_INPUT_LAYOUT);
        validator.setTextInputLayoutErrorTextAppearance(R.style.TextInputLayoutErrorStyle);
    }


    // Display Main Logo
    private void onLoadAppLogo() {

        GlideApp.with(getApplicationContext()).asBitmap().load(SERVER_BASE_URL +"image/minilogo")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(logoimagetop);

    }



    // Hide Keyboard on Submit
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    // Input Email & Password Validation
    public void onSetupRules() {
        validator.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoimagetop = null;
        unbinder.unbind();
    }
}
