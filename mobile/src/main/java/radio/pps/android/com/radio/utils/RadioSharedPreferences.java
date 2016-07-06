package radio.pps.android.com.radio.utils;

import android.content.Context;
import android.content.SharedPreferences;

import radio.pps.android.com.radio.Constants.SharedPref_Bundle_Keys_val;

/**
 * Created by Prabhpreet on 01-11-2015.
 */
public class RadioSharedPreferences {
    private static RadioSharedPreferences radioSharedPreferences;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    /*Keys for SharedPreferences*/

    private RadioSharedPreferences() {

    }

    private RadioSharedPreferences(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(SharedPref_Bundle_Keys_val.KEY_PREFERENCES, 0);
            editor = sharedPreferences.edit();
        }
    }

    public final static RadioSharedPreferences getInstance(Context context) {
        if (radioSharedPreferences == null) {
            radioSharedPreferences = new RadioSharedPreferences(context);
        }
        return radioSharedPreferences;
    }


    public void saveCurrentPlayingStation(String id, String stationName, String Streamurl) {
        editor.putString(SharedPref_Bundle_Keys_val.keyStationId, id);
        editor.putString(SharedPref_Bundle_Keys_val.keyStationName, stationName);
        editor.putString(SharedPref_Bundle_Keys_val.KEY_STREAMURL, Streamurl);
        editor.apply();
    }

    public String[] getCurrentPlayingStation() {
        String[] listIdName = new String[3];
        listIdName[0] = sharedPreferences.getString(SharedPref_Bundle_Keys_val.keyStationId, "");
        listIdName[1] = sharedPreferences.getString(SharedPref_Bundle_Keys_val.keyStationName, "");
        listIdName[2] = sharedPreferences.getString(SharedPref_Bundle_Keys_val.KEY_STREAMURL, "");
        return listIdName;

    }

    public void isPlaying(int isPlaying) {
        // Set the isPlaying preference to true
        editor.putInt(SharedPref_Bundle_Keys_val.KEY_ISPLAYING, isPlaying);
        editor.apply();
    }


    public int isGetPlaying() {
        return sharedPreferences.getInt(SharedPref_Bundle_Keys_val.KEY_ISPLAYING, 0);
    }

    public void saveAppDbPath(String appDBPath) {

        editor.putString(SharedPref_Bundle_Keys_val.Key_APPDBPATH, appDBPath);
        editor.apply();
    }

    public String getAppDbPath() {
        return sharedPreferences.getString(SharedPref_Bundle_Keys_val.Key_APPDBPATH, "");
    }

    public String getUserDbPath() {
        return sharedPreferences.getString(SharedPref_Bundle_Keys_val.KEY_APPUSERDBPATH, "");
    }

    public void saveUserDbPath(String appDBPath) {

        editor.putString(SharedPref_Bundle_Keys_val.KEY_APPUSERDBPATH, appDBPath);
        editor.apply();
    }

    public void saveSearchOnline(boolean val) {
        editor.putBoolean(SharedPref_Bundle_Keys_val.KEY_SEARCH_ONLINE_OFFLINE, val);
        editor.apply();
    }

    public boolean getSearchOnlineOfflineStatus() {
        return sharedPreferences.getBoolean(SharedPref_Bundle_Keys_val.KEY_SEARCH_ONLINE_OFFLINE, false);
    }

    public void setBackGroundPlay(boolean backGroundPlay) {
        editor.putBoolean(SharedPref_Bundle_Keys_val.KEY_BACKGROUNDPLAY, backGroundPlay);
        editor.apply();
    }

    public boolean getBackGroundPlay() {
        return sharedPreferences.getBoolean(SharedPref_Bundle_Keys_val.KEY_BACKGROUNDPLAY, true);
    }

    public void setBubblePlayerShowSetting(boolean showBubblePlayerInBg) {
        editor.putBoolean(SharedPref_Bundle_Keys_val.KEY_BUBBLEPLAYER, showBubblePlayerInBg);
        editor.apply();
    }

    public boolean getBubblePlayerShowSetting() {
        return sharedPreferences.getBoolean(SharedPref_Bundle_Keys_val.KEY_BUBBLEPLAYER, false);
    }


    public void setShakeDetectorServiceSetting(boolean startShakeDetectorService) {
        editor.putBoolean(SharedPref_Bundle_Keys_val.KEY_SHAKEDETECTORSERVICE, startShakeDetectorService);
        editor.apply();
    }

    public boolean getShakeDetectorServiceSetting() {
        return sharedPreferences.getBoolean(SharedPref_Bundle_Keys_val.KEY_SHAKEDETECTORSERVICE, false);
    }

    public void setForceRTLSupport(boolean enableRTL_Layout) {
        editor.putBoolean(SharedPref_Bundle_Keys_val.KEY_MIRRORVIEW, enableRTL_Layout);
        editor.apply();
    }

    public boolean getForceRTLSupport() {
        return sharedPreferences.getBoolean(SharedPref_Bundle_Keys_val.KEY_MIRRORVIEW, false);
    }

    public int getAppRegisteredVersion() {
        return sharedPreferences.getInt(SharedPref_Bundle_Keys_val.KEY_APPREGISTERED_VERSION, Integer.MIN_VALUE);
    }

    public void setAppRegisteredVersion(int currentVersion) {
        editor.putInt(SharedPref_Bundle_Keys_val.KEY_APPREGISTERED_VERSION, currentVersion);
        editor.apply();
    }

    public boolean getAppRunsForFirstTime() {
        return sharedPreferences.getBoolean(SharedPref_Bundle_Keys_val.KEY_APP_RUNS_FOR_FIRST_TIME, true);
    }

    public void setAppRunsForFirstTime(boolean isFirstTimeRun) {
        editor.putBoolean(SharedPref_Bundle_Keys_val.KEY_APP_RUNS_FOR_FIRST_TIME, isFirstTimeRun);
        editor.apply();
    }


    public boolean getVolumeIsNotLoaded() {
        return sharedPreferences.getBoolean(SharedPref_Bundle_Keys_val.KEY_VOLUMELOADED, true);
    }

    public void setVolumeIsNotLoaded(boolean isLoaded) {
        editor.putBoolean(SharedPref_Bundle_Keys_val.KEY_VOLUMELOADED, isLoaded);
        editor.apply();
    }


    public boolean getSettingRemoveAds() {
        return sharedPreferences.getBoolean(SharedPref_Bundle_Keys_val.KEY_REMOVE_ADS, false);
    }

    public void setSettingRemoveAds(boolean isRemoveAds) {
        editor.putBoolean(SharedPref_Bundle_Keys_val.KEY_REMOVE_ADS, isRemoveAds);
        editor.apply();
    }

    public int getAppStartCounter() {
        return sharedPreferences.getInt(SharedPref_Bundle_Keys_val.KEY_APP_START_COUNTER, 0);
    }

    public void setAppStartCounter() {
        int counter = getAppStartCounter();
        editor.putInt(SharedPref_Bundle_Keys_val.KEY_APP_START_COUNTER, ++counter);
        editor.apply();
    }

}
