package com.beeecorptv.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.RequiresApi;


public class NetworkUtils {


    private NetworkUtils(){


    }


    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.M)
  public static boolean isWifiConnected(Context context) {

    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

   if (manager == null) return true;

   Network network = manager.getActiveNetwork();

    NetworkCapabilities capabilities = manager.getNetworkCapabilities(network);

        assert capabilities != null;
        return !capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI | NetworkCapabilities.TRANSPORT_CELLULAR);
  }



    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }



}
