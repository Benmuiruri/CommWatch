package com.communitywatch;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionCreator {
	SharedPreferences prefs;
    Editor editor;
    // an editor is used to edit your preferences
    Context context;
 
    // Shared Preference file name
    private static final String PREF_NAME = "Pref";
 
    // Shared Preferences Key
    private static final String IS_LOGIN = "IsLoggedIn";
 
    public static final String KEY_NAME = "name";
    public static final String KEY_PASS = "pass";
    // Context
    Context _context;
    @SuppressLint("CommitPrefEdits")
	public SessionCreator(Context context) {
        this.context = context;
        
        /*
         * Setting the mode as Private so that the preferences should only be
         * used in this application and not by any other application
         * also the preferences can be Shared Globally by using -
         *Activity.MODE_WORLD_READABLE - to read Application components data
         *globally and,
         *Activity.MODE_WORLD_WRITEABLE -file can be written globally by any
         *other application. 
         */
        // Constructor
       
 
        prefs = context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        /*
         * the same pref mode can be set to private by using 0 as a flag instead
         * of Acticity.MODE_PRIVATE
         */
        editor = prefs.edit();
    }
 
    // Creating a login session
    public void createLoginSession(String name, String pass) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASS, pass);
        editor.commit();
    }
 
    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
             
            // user is not logged in redirect him to Login Activity
            Intent  dashboard = new Intent(_context, LoginActivity.class);
             
            // Closing all the Activities from stack
            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
            dashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
            // Staring Login Activity
            _context.startActivity( dashboard);
             
            return true;
        }
        return false;
    }
 
    // Getting stored session data of user and returing this data as a HASH MAP
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, prefs.getString(KEY_NAME, null));
 
        // user email id
        user.put(KEY_PASS, prefs.getString(KEY_PASS, null));
        return user;
    }
 
    // Clearing a session data
    public void logoutUser() {
        editor.clear();
        editor.commit();
 
        Intent  dashboard = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 
        // Add new Flag to start new Activity
        dashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 
        context.startActivity( dashboard);
    }
 
    // Get Login State
    public boolean isLoggedIn() {
        return prefs.getBoolean(IS_LOGIN, false);
    }
}
