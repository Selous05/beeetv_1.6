package com.beeecorptv.ui.manager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import com.beeecorptv.data.model.auth.UserAuthInfo;

import static com.beeecorptv.util.Constants.AUTH_EMAIL;
import static com.beeecorptv.util.Constants.AUTH_EXPIRED_DATE;
import static com.beeecorptv.util.Constants.AUTH_ID;
import static com.beeecorptv.util.Constants.AUTH_NAME;
import static com.beeecorptv.util.Constants.PREMUIM;
import static com.beeecorptv.util.Constants.PREMUIM_MANUAL;


/**
 * EasyPlex - Android Movie Portal App
 * @package     EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2021 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/




public class AuthManager {


    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;


    @SuppressLint("CommitPrefEdits")
    public AuthManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public void saveSettings(UserAuthInfo userAuthInfo){

        editor.putInt(PREMUIM, userAuthInfo.getPremuim()).commit();
        editor.putInt(PREMUIM_MANUAL, userAuthInfo.getManualPremuim()).commit();
        editor.putString(AUTH_NAME, userAuthInfo.getName()).commit();
        editor.putString(AUTH_EMAIL, userAuthInfo.getEmail()).commit();
        editor.putInt(AUTH_ID, userAuthInfo.getId()).commit();
        editor.putString(AUTH_EXPIRED_DATE, userAuthInfo.getExpiredIn()).commit();
        editor.apply();
    }

    public void deleteAuth(){
        editor.remove(PREMUIM).commit();
        editor.remove(AUTH_NAME).commit();
        editor.remove(PREMUIM_MANUAL).commit();
        editor.remove(AUTH_ID).commit();
        editor.remove(AUTH_EXPIRED_DATE).commit();
        editor.remove(AUTH_EMAIL).commit();
    }

    public UserAuthInfo getUserInfo() {
        UserAuthInfo userAuthInfo = new UserAuthInfo();
        userAuthInfo.setPremuim(prefs.getInt(PREMUIM, 0));
        userAuthInfo.setManualPremuim(prefs.getInt(PREMUIM_MANUAL, 0));
        userAuthInfo.setName(prefs.getString(AUTH_NAME, null));
        userAuthInfo.setEmail(prefs.getString(AUTH_EMAIL, null));
        userAuthInfo.setId(prefs.getInt(AUTH_ID, 0));
        userAuthInfo.setExpiredIn(prefs.getString(AUTH_EXPIRED_DATE, null));
        return userAuthInfo;
    }


}
