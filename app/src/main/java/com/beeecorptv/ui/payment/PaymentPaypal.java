package com.beeecorptv.ui.payment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.beeecorptv.util.Constants.ARG_PAYMENT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.beeecorptv.BuildConfig;
import com.beeecorptv.R;
import com.beeecorptv.data.model.plans.Plan;
import com.beeecorptv.data.remote.ErrorHandling;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.splash.SplashActivity;
import com.beeecorptv.ui.viewmodels.LoginViewModel;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.Tools;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButton;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * HoneyStream - Android Movie Portal App
 * @package     HoneyStream - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2020 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


public class PaymentPaypal extends AppCompatActivity {


    private Unbinder unbinder;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.paypal_method)
    PaymentButton payPalButton;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.loader)
    ProgressBar loader;


    @Inject
    SettingsManager settingsManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel loginViewModel;


    private String planId;
    private String planPrice;
    private String planName;
    private String planDuraction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.payment_paypal);

        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);

        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        Plan plan = intent.getParcelableExtra(ARG_PAYMENT);

        this.planId =  plan.getstripePlanId();
        this.planPrice = plan.getStripePlanPrice();
        this.planName = plan.getName();
        this.planDuraction = plan.getPackDuration();

        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);

        onLoadPaypal();

        payPalButton.performClick();

    }

    private void onLoadPaypal() {

        payPalButton.setup(
                createOrderActions -> {
                    ArrayList purchaseUnits = new ArrayList<>();

                    purchaseUnits.add(
                            new PurchaseUnit.Builder()
                                    .amount(
                                            new Amount.Builder()
                                                    .currencyCode(CurrencyCode.USD)
                                                    .value("0.01")
                                                    .build()
                                    )
                                    .build()
                    );
                    Order order = new Order(  OrderIntent.CAPTURE,new AppContext.Builder()
                            .userAction(UserAction.PAY_NOW)
                            .build(),purchaseUnits,null);
                    createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                },
                approval -> approval.getOrderActions().capture(result -> {
                    Timber.i("CaptureOrderResult: %s", result);


                    loginViewModel.setSubscription(planId, "1", planName, planDuraction, "paypal").observe(PaymentPaypal.this, login -> {

                        if (login.status == ErrorHandling.Status.SUCCESS) {

                            loader.setVisibility(View.GONE);


                            final Dialog dialog = new Dialog(this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_success_payment);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());

                            lp.gravity = Gravity.BOTTOM;
                            lp.width = MATCH_PARENT;
                            lp.height = MATCH_PARENT;


                            dialog.findViewById(R.id.btn_start_watching).setOnClickListener(v -> {

                                Intent intent = new Intent(PaymentPaypal.this, SplashActivity.class);
                                startActivity(intent);
                                finish();


                            });


                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> {
                                Intent intent = new Intent(PaymentPaypal.this, SplashActivity.class);
                                startActivity(intent);
                                finish();
                            });
                            dialog.show();
                            dialog.getWindow().setAttributes(lp);


                        } else {

                            loader.setVisibility(View.GONE);
                            DialogHelper.erroPayment(PaymentPaypal.this);


                        }

                    });
                })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}