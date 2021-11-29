package com.beeecorptv.ui.payment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.beeecorptv.R;
import com.beeecorptv.data.model.plans.Plan;
import com.beeecorptv.data.remote.ErrorHandling;
import com.beeecorptv.data.repository.AuthRepository;
import com.beeecorptv.ui.manager.SettingsManager;
import com.beeecorptv.ui.splash.SplashActivity;
import com.beeecorptv.ui.viewmodels.LoginViewModel;
import com.beeecorptv.util.DialogHelper;
import com.beeecorptv.util.Tools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButton;
import com.razorpay.Checkout;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent.Status;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
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


public class Payment extends AppCompatActivity {


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.paypal_method)
    PaymentButton payPalButton;

    private Unbinder unbinder;


    @Inject
    AuthRepository authRepository;

    private LoginViewModel loginViewModel;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.stripe_top)
    RelativeLayout relativeLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.cardInputWidget)
    CardInputWidget cardInputWidget;




    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.sumbit_subscribe)
    Button sumbitSubscribe;



    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.form_container)
    LinearLayout formContainer;


    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.loader)
    ProgressBar loader;


    private Stripe mStripe;


    @Inject
    SettingsManager settingsManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private Plan plan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.payment_activity);

        unbinder = ButterKnife.bind(this);


        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);

        Intent intent = getIntent();
        plan = intent.getParcelableExtra(ARG_PAYMENT);

        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);

        Checkout.preload(this);


        if (settingsManager.getSettings().getStripePublishableKey() !=null ) {
            PaymentConfiguration.init(Payment.this, settingsManager.getSettings().getStripePublishableKey());
        }

        if (settingsManager.getSettings().getStripePublishableKey() !=null ) {
            mStripe = new Stripe(Payment.this, settingsManager.getSettings().getStripePublishableKey());
        }

            // Hook up the pay button to the card widget and stripe instance

            sumbitSubscribe.setOnClickListener(v -> {

                formContainer.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);

                cardInputWidget = findViewById(R.id.cardInputWidget);
                PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

                cardInputWidget.setCardHint("12121");

                if (params == null) {
                    return;
                }
                mStripe.createPaymentMethod(params, new ApiResultCallback<>() {
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onSuccess(@NonNull PaymentMethod result) {


                        onSuccessPayment(result.id, null);

                        Toast.makeText(Payment.this, "Success : " + result.id, Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onError(@NonNull Exception e) {


                        Toast.makeText(Payment.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


            });

        onLoadPaypal();

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
                                                    .value(plan.getPrice())
                                                    .build()
                                    ).description("Service")
                                    .build()
                    );
                    Order order = new Order(  OrderIntent.CAPTURE,new AppContext.Builder()
                            .userAction(UserAction.PAY_NOW)
                            .build(),purchaseUnits,null);
                    createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                },
                approval -> approval.getOrderActions().capture(result -> {
                    Timber.i("CaptureOrderResult: %s", result);

                    loginViewModel.setSubscription(String.valueOf(plan.getId()), "1", plan.getName(), plan.getPackDuration(), "paypal").observe(Payment.this, login -> {

                        if (login.status == ErrorHandling.Status.SUCCESS) {


                            formContainer.setVisibility(View.GONE);
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

                                Intent intent = new Intent(Payment.this, SplashActivity.class);
                                startActivity(intent);
                                finish();


                            });


                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> {
                                Intent intent = new Intent(Payment.this, SplashActivity.class);
                                startActivity(intent);
                                finish();
                            });
                            dialog.show();
                            dialog.getWindow().setAttributes(lp);


                        } else {


                            formContainer.setVisibility(View.VISIBLE);
                            loader.setVisibility(View.GONE);

                            DialogHelper.erroPayment(Payment.this);


                        }

                    });
                })


        );
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Handle the result of stripe.confirmPayment
        mStripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
        super.onActivityResult(requestCode, resultCode, data);

    }




    private class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        private final WeakReference<Payment> activityRef;

        PaymentResultCallback(@NonNull Payment activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final Payment activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            Status status = paymentIntent.getStatus();
            if (status == Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String data = gson.toJson(paymentIntent);

                onSuccessPayment(data, status);



            } else if (status == Status.RequiresPaymentMethod) {
                Toast.makeText(activity, "Payment failed", Toast.LENGTH_SHORT).show();
            }
        }





        @Override
        public void onError(@NotNull Exception e) {


            //

        }
    }



    public void onSuccessPayment(@Nullable String paymentMethodId, Status status){

        loginViewModel.getSubscribePlan(paymentMethodId, String.valueOf(plan.getId()), plan.getPrice(), plan.getName(), plan.getPackDuration()).observe(this, login -> {

            if (login.status == ErrorHandling.Status.SUCCESS) {


                formContainer.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);


                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.dialog_success_payment);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WRAP_CONTENT;
                lp.height = WRAP_CONTENT;

                dialog.findViewById(R.id.btn_start_watching).setOnClickListener(v -> {

                    Intent intent = new Intent(Payment.this, SplashActivity.class);
                    startActivity(intent);
                    finish();


                });


                dialog.show();
                dialog.getWindow().setAttributes(lp);


            } else {


                formContainer.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);

                DialogHelper.erroPayment(this);


            }

        });


    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();

        unbinder.unbind();


    }
}