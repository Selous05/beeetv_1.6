package com.beeecorptv.ui.player.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.mediarouter.app.MediaRouteButton;

import com.beeecorptv.R;
import com.beeecorptv.databinding.UiControllerViewBinding;
import com.beeecorptv.ui.player.activities.ChromeCastActivity;
import com.beeecorptv.ui.player.bindings.PlayerController;
import com.beeecorptv.ui.player.cast.GoogleServicesHelper;
import com.beeecorptv.ui.player.utilities.ExoPlayerLogger;
import com.google.android.exoplayer2.Player;
import com.google.android.gms.cast.framework.CastButtonFactory;

public class UIControllerView extends FrameLayout {

    PlayerController playerController;
    private static final String TAG = UIControllerView.class.getSimpleName();
    private static final int TIME_TO_HIDE_CONTROL = 5000;
    private UiControllerViewBinding binding;
    private Handler countdownHandler;
    private final Context mContext;

    /**
     *  Hide Controllers (buttons play - forward - back - settings)
     */


    // Lambda Runnable
    final Runnable hideUIAction = () -> {

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        binding.controllerPanel.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                //

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                binding.controllerPanel.setVisibility(GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


                //

            }
        });
    };




    public UIControllerView(final Context context) {
        this(context, null);
    }

    public UIControllerView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIControllerView(Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initLayout(context);

        mContext = context;


    }

    public UIControllerView setPlayerController(PlayerController playerController) {
        if (playerController == null) {
            ExoPlayerLogger.w(TAG, "setUserController()--> param passed in null");
            return null;
        }


        this.playerController = playerController;

        if (binding != null) {
            binding.setController(playerController);


        }



        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ExoPlayerLogger.w(TAG, "onDetachedFromWindow");
        countdownHandler.removeCallbacks(hideUIAction);
    }


    private void initLayout(Context context) {

        binding = DataBindingUtil
                .inflate(LayoutInflater.from(context), R.layout.ui_controller_view, this, true);

        countdownHandler = new Handler(Looper.getMainLooper());

        new Thread(hideUIAction).start();


        initCast();

    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        countdownHandler.removeCallbacks(hideUIAction);

        if (binding.controllerPanel.getVisibility() == VISIBLE) {


            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation.setDuration(500);
            binding.controllerPanel.startAnimation(alphaAnimation);

            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {


                    //
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    binding.controllerPanel.setVisibility(GONE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //
                }
            });






        } else {

            if (playerController.playerPlaybackState.get() != Player.STATE_IDLE) {

                binding.controllerPanel.setVisibility(VISIBLE);
                if (!playerController.isUserDraggingSeekBar.get()){
                    hideUiTimeout();
                }
            }


        }



        return super.onTouchEvent(event);
    }


    private void initCast() {

        if (mContext instanceof ChromeCastActivity
                && ((ChromeCastActivity) mContext).isChromeCastEnable()
                && GoogleServicesHelper.available(getContext())) {
            try {
                MediaRouteButton mMediaRouteButton = (MediaRouteButton) findViewById(
                        R.id.view_controller_chromecast_ib);
                CastButtonFactory.setUpMediaRouteButton(getContext(), mMediaRouteButton);

                mMediaRouteButton.setOnTouchListener((v, event) -> {
                    if (!playerController.isUserDraggingSeekBar.get()){
                        hideUiTimeout();
                    }
                    return false;
                });
                mMediaRouteButton.setVisibility(VISIBLE);

            } catch (Exception exception) {
                ExoPlayerLogger.e("ChromeCast", "Cast media route button failed to initialize");
            }
        }
    }

    private void hideUiTimeout() {
        countdownHandler.postDelayed(hideUIAction, TIME_TO_HIDE_CONTROL);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        binding.controllerPanel.startAnimation(alphaAnimation);

    }

}
