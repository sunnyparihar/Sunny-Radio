package radio.pps.android.com.radio.Constants;

import android.app.Application;
import android.widget.Toast;

import radio.pps.android.com.radio.Databases.DataProvider;
import radio.pps.android.com.radio.Pojo.Station;
import radio.pps.android.com.radio.Pojo.TuneInBase;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

public class GlobalLevelVariable extends Application {
    TuneInBase tuneInBase;
    int lastRecentPlayedStationPosition = 0;
    boolean alreadyRecentClickedOnce = false;


    public String[] getCurrentStationData() {
        return currentStationData;
    }

    public void setCurrentStationData(String[] currentStationData) {
        this.currentStationData = currentStationData;
    }

    String[] currentStationData = new String[3];


    public String[] getPreviousRecentStation() throws Exception {
        checkRecentStation();
        String[] userRecentStation = new String[3];
        if (tuneInBase == null || tuneInBase.getStationsList().isEmpty()) {
            Toast.makeText(this, "No Recent Station Found", Toast.LENGTH_LONG).show();
        } else {
            if (alreadyRecentClickedOnce || lastRecentPlayedStationPosition != 0) {
                int recentStationListSize = tuneInBase.getStationsList().size();
                if (recentStationListSize > 1) {
                    if (lastRecentPlayedStationPosition != 0)
                        lastRecentPlayedStationPosition = lastRecentPlayedStationPosition - 1;
                    Station tempStationData = tuneInBase.getStationsList().get(lastRecentPlayedStationPosition);
                    userRecentStation[0] = tempStationData.getStationId();
                    userRecentStation[1] = tempStationData.getStationName();
                    userRecentStation[2] = Constants.TUNE_A_STATION + "/sbin/tunein-station.pls" +
                            "?id=" + userRecentStation[0];
                    alreadyRecentClickedOnce = true;
                } else {
                    Toast.makeText(this, "No Recent Station Found", Toast.LENGTH_LONG).show();
                }
            } else {
                int recentStationListSize = tuneInBase.getStationsList().size();
                if (recentStationListSize > 1) {
                    lastRecentPlayedStationPosition = (--recentStationListSize) - 1;
                    Station tempStationData = tuneInBase.getStationsList().get(lastRecentPlayedStationPosition);
                    userRecentStation[0] = tempStationData.getStationId();
                    userRecentStation[1] = tempStationData.getStationName();
                    userRecentStation[2] = Constants.TUNE_A_STATION + "/sbin/tunein-station.pls" +
                            "?id=" + userRecentStation[0];
                    alreadyRecentClickedOnce = true;
                } else {
                    Toast.makeText(this, "No Recent Station Found", Toast.LENGTH_LONG).show();
                }
            }


        }
        return userRecentStation;
    }

    public String[] getNextRecentStation() throws Exception {
        checkRecentStation();
        String[] userRecentStation = new String[3];
        if (tuneInBase == null || tuneInBase.getStationsList().isEmpty()) {
            Toast.makeText(this, "No Recent Station Found", Toast.LENGTH_LONG).show();
        } else {
            int recentStationListSize = tuneInBase.getStationsList().size();
            if (alreadyRecentClickedOnce || lastRecentPlayedStationPosition != recentStationListSize - 1) {

                if (recentStationListSize > 1) {
                    if (lastRecentPlayedStationPosition != recentStationListSize - 1)
                        ++lastRecentPlayedStationPosition;
                    Station tempStationData = tuneInBase.getStationsList().get(lastRecentPlayedStationPosition);
                    userRecentStation[0] = tempStationData.getStationId();
                    userRecentStation[1] = tempStationData.getStationName();
                    userRecentStation[2] = Constants.TUNE_A_STATION + "/sbin/tunein-station.pls" +
                            "?id=" + userRecentStation[0];
                    alreadyRecentClickedOnce = true;
                } else {
                    Toast.makeText(this, "No Recent Station Found", Toast.LENGTH_LONG).show();
                }
            } else {
                if (recentStationListSize > 1) {
                    lastRecentPlayedStationPosition = 0;
                    Station tempStationData = tuneInBase.getStationsList().get(lastRecentPlayedStationPosition);
                    userRecentStation[0] = tempStationData.getStationId();
                    userRecentStation[1] = tempStationData.getStationName();
                    userRecentStation[2] = Constants.TUNE_A_STATION + "/sbin/tunein-station.pls" +
                            "?id=" + userRecentStation[0];
                    alreadyRecentClickedOnce = true;
                } else {
                    Toast.makeText(this, "No Recent Station Found", Toast.LENGTH_LONG).show();
                }
            }

        }
        return userRecentStation;
    }

    public void checkRecentStation() {
        if (tuneInBase == null || tuneInBase.getStationsList().isEmpty()) {
            DataProvider dataProvider = new DataProvider(RadioSharedPreferences.getInstance(this).getUserDbPath());
            tuneInBase = dataProvider.getRecentStationList();
        }

    }

    public void updateRecentStation() {
        DataProvider dataProvider = new DataProvider(RadioSharedPreferences.getInstance(this).getUserDbPath());
        tuneInBase = dataProvider.getRecentStationList();
    }


}
