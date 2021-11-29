/**
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @package  EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2021 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/

package com.beeecorptv.ui.downloadmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.beeecorptv.ui.downloadmanager.core.utils.Utils;
import com.beeecorptv.ui.downloadmanager.service.DownloadService;

/*
 * The receiver for actions of foreground notification, added by service.
 */

public class NotificationReceiver extends BroadcastReceiver
{
    public static final String NOTIFY_ACTION_SHUTDOWN_APP = "com.easyplexdemoapp.Receiver.NotificationReceiver.NOTIFY_ACTION_SHUTDOWN_APP";
    public static final String NOTIFY_ACTION_PAUSE_ALL = "com.easyplexdemoapp.Receiver.NotificationReceiver.NOTIFY_ACTION_PAUSE_ALL";
    public static final String NOTIFY_ACTION_RESUME_ALL = "com.easyplexdemoapp.Receiver.NotificationReceiver.NOTIFY_ACTION_RESUME_ALL";
    public static final String NOTIFY_ACTION_PAUSE_RESUME = "com.easyplexdemoapp.Receiver.NotificationReceiver.NOTIFY_ACTION_PAUSE_RESUME";
    public static final String NOTIFY_ACTION_CANCEL = "com.easyplexdemoapp.Receiver.NotificationReceiver.NOTIFY_ACTION_CANCEL";
    public static final String NOTIFY_ACTION_REPORT_APPLYING_PARAMS_ERROR = "com.easyplexdemoapp.Receiver.NotificationReceiver.NOTIFY_ACTION_REPORT_APPLYING_PARAMS_ERROR";
    public static final String TAG_ID = "id";
    public static final String TAG_ERR = "err";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (action == null)
            return;
        Intent mainIntent, serviceIntent;
        switch (action) {
            /* Send action to the already running service */
            case NOTIFY_ACTION_SHUTDOWN_APP:

                mainIntent = new Intent(context.getApplicationContext(), DownloadService.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.setAction(NOTIFY_ACTION_SHUTDOWN_APP);
                context.startActivity(mainIntent);


                serviceIntent = new Intent(context.getApplicationContext(), DownloadService.class);
                serviceIntent.setAction(NOTIFY_ACTION_SHUTDOWN_APP);
                context.startService(serviceIntent);
                break;
            case NOTIFY_ACTION_PAUSE_ALL:
                serviceIntent = new Intent(context.getApplicationContext(), DownloadService.class);
                serviceIntent.setAction(NOTIFY_ACTION_PAUSE_ALL);
                context.startService(serviceIntent);
                break;
            case NOTIFY_ACTION_RESUME_ALL:
                serviceIntent = new Intent(context.getApplicationContext(), DownloadService.class);
                serviceIntent.setAction(NOTIFY_ACTION_RESUME_ALL);
                context.startService(serviceIntent);
                break;
            case NOTIFY_ACTION_PAUSE_RESUME:
                serviceIntent = new Intent(context.getApplicationContext(), DownloadService.class);
                serviceIntent.setAction(NOTIFY_ACTION_PAUSE_RESUME);
                serviceIntent.putExtra(TAG_ID, intent.getSerializableExtra(TAG_ID));
                context.startService(serviceIntent);
                break;
            case NOTIFY_ACTION_CANCEL:
                serviceIntent = new Intent(context.getApplicationContext(), DownloadService.class);
                serviceIntent.setAction(NOTIFY_ACTION_CANCEL);
                serviceIntent.putExtra(TAG_ID, intent.getSerializableExtra(TAG_ID));
                context.startService(serviceIntent);
                break;
            case NOTIFY_ACTION_REPORT_APPLYING_PARAMS_ERROR:
                Throwable e = (Throwable)intent.getSerializableExtra(TAG_ERR);
                if (e != null)
                    Utils.reportError(e, null);
                break;
        }
    }
}
