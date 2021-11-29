package com.beeecorptv.ui.manager;

import android.content.SharedPreferences;

import com.beeecorptv.data.model.auth.Login;


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




public class TokenManager {

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String PASSWORD = "ACCESS_TOKEN";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public TokenManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }


    public void saveToken(Login token){
        editor.putString(ACCESS_TOKEN, token.getAccessToken()).commit();
        editor.putString(REFRESH_TOKEN, token.getRefresh()).commit();
        editor.apply();
    }



    public void deleteToken(){
        editor.remove(ACCESS_TOKEN).commit();
        editor.remove(REFRESH_TOKEN).commit();
    }

    public Login getToken(){
        Login token = new Login();
        token.setAccessToken(prefs.getString(ACCESS_TOKEN, null));
        token.setRefresh(prefs.getString(REFRESH_TOKEN, null));
        return token;
    }



}
