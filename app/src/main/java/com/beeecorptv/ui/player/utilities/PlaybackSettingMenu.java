package com.beeecorptv.ui.player.utilities;

import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.beeecorptv.R;
import com.beeecorptv.ui.player.activities.EasyPlexPlayerActivity;
import com.beeecorptv.ui.player.enums.PlaybackSpeed;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

public class PlaybackSettingMenu {

    private SimpleExoPlayer contentPlayer;

    private EasyPlexPlayerActivity activity;

    private AlertDialog mainDialog;

    public PlaybackSettingMenu() {
    }

    public PlaybackSettingMenu(@NonNull SimpleExoPlayer contentPlayer, @NonNull EasyPlexPlayerActivity playerActivity) {
        this.contentPlayer = contentPlayer;
        this.activity = playerActivity;
    }

    public void setContentPlayer(@NonNull SimpleExoPlayer contentPlayer) {
        this.contentPlayer = contentPlayer;
    }

    public void setActivity(@NonNull EasyPlexPlayerActivity activity) {
        this.activity = activity;
    }

    public void buildSettingMenuOptions() {
        ArrayList<MenuOption> menuOptions = new ArrayList<>();

        // Option can be separately injected from root activity if needed.
        // It requires dependencies: activityContext & contentSimpleExoPlayer.
        MenuOption playbackSpeedOption = new MenuOption(activity.getString(
                R.string.playback_setting_speed_title), new MenuOptionCallback() {
            @Override
            public void onClick() {
                ArrayList<String> playbackSpeedTexts = new ArrayList<>();
                ArrayList<Float> playbackSpeedValues = new ArrayList<>();

                for (PlaybackSpeed playbackSpeed : PlaybackSpeed.getAllPlaybackSpeedEnums()) {
                    playbackSpeedTexts.add(playbackSpeed.getText(activity));
                    playbackSpeedValues.add(playbackSpeed.getSpeedValue());
                }

                String[] speedOptionTextArray = playbackSpeedTexts.toArray(new String[0]);
                int currentSpeedPosition = PlaybackSpeed.getPlaybackSpeedPositionBySpeedValue(
                        contentPlayer.getPlaybackParameters().speed);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setSingleChoiceItems(
                        speedOptionTextArray,
                        currentSpeedPosition,   // When is -1, none will be selected.
                        (dialog, i) -> {
                            PlaybackParameters originParameters = contentPlayer.getPlaybackParameters();
                            PlaybackParameters updatedSpeedParameters = new PlaybackParameters(
                                    playbackSpeedValues.get(i),
                                    originParameters.pitch, // Keeping old values
                                    originParameters.skipSilence
                            );

                            contentPlayer.setPlaybackParameters(updatedSpeedParameters);


                            activity.getPlayerController().getCurrentSpeed("Speed (" + playbackSpeedTexts.get(i) + ")");
                            dialog.dismiss();
                        });

                AlertDialog chooseSpeedDialog = builder.create();
                setAlertDialogGravityBottomCenter(chooseSpeedDialog);
                alertDialogImmersiveShow(chooseSpeedDialog);
            }

            @Override
            public String getTitle(String defaultTitle) {
                // Dynamic title
                Float currentSpeedValue = contentPlayer.getPlaybackParameters().speed;
                PlaybackSpeed currentPlaybackSpeed = PlaybackSpeed.getPlaybackSpeedBySpeedValue(currentSpeedValue);
                if (currentPlaybackSpeed != null) {
                    defaultTitle = defaultTitle + " - " + currentPlaybackSpeed.getText(activity);
                }
                return defaultTitle;
            }
        });

        menuOptions.add(playbackSpeedOption);
    }

    public void show() {

        ArrayList<String> playbackSpeedTexts = new ArrayList<>();
        ArrayList<Float> playbackSpeedValues = new ArrayList<>();

        for (PlaybackSpeed playbackSpeed : PlaybackSpeed.getAllPlaybackSpeedEnums()) {
            playbackSpeedTexts.add(playbackSpeed.getText(activity));
            playbackSpeedValues.add(playbackSpeed.getSpeedValue());
        }

        String[] speedOptionTextArray = playbackSpeedTexts.toArray(new String[0]);
        int currentSpeedPosition = PlaybackSpeed.getPlaybackSpeedPositionBySpeedValue(
                contentPlayer.getPlaybackParameters().speed);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setSingleChoiceItems(
                speedOptionTextArray,
                currentSpeedPosition,
                (dialog, i) -> {
                    PlaybackParameters originParameters = contentPlayer.getPlaybackParameters();
                    PlaybackParameters updatedSpeedParameters = new PlaybackParameters(
                            playbackSpeedValues.get(i),
                            originParameters.pitch,
                            originParameters.skipSilence
                    );

                    contentPlayer.setPlaybackParameters(updatedSpeedParameters);


                    activity.getPlayerController().getCurrentSpeed("Speed ("+playbackSpeedTexts.get(i)+")");
                    dialog.dismiss();
                });

        AlertDialog chooseSpeedDialog = builder.create();
        setAlertDialogGravityBottomCenter(chooseSpeedDialog);
        alertDialogImmersiveShow(chooseSpeedDialog);
    }

    public void dismiss() {
        if (mainDialog != null) {
            mainDialog.dismiss();
        }
    }

    private void setAlertDialogGravityBottomCenter(AlertDialog alertDialog) {
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
        if (layoutParams != null) {
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        }
    }

    /*
     * This method is to keep the system navigation bar not appearing after dialog calling .show().
     *
     * More on this problem:
     * https://stackoverflow.com/questions/22794049/how-do-i-maintain-the-immersive-mode-in-dialogs
     */
    private void alertDialogImmersiveShow(AlertDialog alertDialog) {
        // Set dialog not-focusable, to avoid popping the nav bar.
        alertDialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        alertDialog.show();

        // Copy the hosted activity systemUI setting.
        alertDialog.getWindow().getDecorView().setSystemUiVisibility(
                activity.getWindow().getDecorView().getSystemUiVisibility());
        // Clear the not-focusable, which let the dialog re-gain focus.
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    interface MenuOptionCallback {
        void onClick();
        String getTitle(String defaultTitle);
    }

    static class MenuOption {
        private final String title;
        private final MenuOptionCallback callback;

        MenuOption(String title, MenuOptionCallback callback) {
            this.title = title;
            this.callback = callback;
        }

        void onClick() {
            callback.onClick();
        }

        String getTitle() {
            return callback.getTitle(title);
        }
    }
}
