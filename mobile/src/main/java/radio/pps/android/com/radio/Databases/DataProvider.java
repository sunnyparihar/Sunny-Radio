package radio.pps.android.com.radio.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import radio.pps.android.com.radio.Pojo.Artist_Info;
import radio.pps.android.com.radio.Pojo.Country;
import radio.pps.android.com.radio.Pojo.Language;
import radio.pps.android.com.radio.Pojo.Station;
import radio.pps.android.com.radio.Pojo.TuneInBase;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;
import radio.pps.android.com.radio.utils.StationParser;

/**
 * Created by Prabhpreet on 27-11-2015.
 */
public class DataProvider {

    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Data provider for opening database to insert or Query DB
     */
    public DataProvider(Context context) {
        try {
            File dbFile = new File(RadioSharedPreferences.getInstance(context).getAppDbPath());
            mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } catch (SQLiteDatabaseLockedException sdle) {
            sdle.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataProvider(String userDb) {
        try {
            File dbFile = new File(userDb);
            mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } catch (SQLiteDatabaseLockedException sdle) {
            sdle.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


/*
    public TuneInBase getFavoriteStationList() {
        TuneInBase tuneInBase = new TuneInBase();
        String sql = "SELECT * from favorite";
        ArrayList<Station> stationsList = new ArrayList<>();
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Station station = new Station();
            station.setFavoriteStation(true);
            station.setBitrate(cursor.getString(cursor.getColumnIndex("bitrate")));
            station.setGenre(cursor.getString(cursor.getColumnIndex("genre")));
            station.setStationId(cursor.getString(cursor.getColumnIndex("station_id")));
            station.setStationName(cursor.getString(cursor.getColumnIndex("StationName")));
            stationsList.add(station);
        }
        tuneInBase.setLoadCategory(StationParser.PARSER_STATION);
        tuneInBase.setStationsList(stationsList);
        if (cursor != null)
            cursor.close();
        closeSQLiteDatabase();
        return tuneInBase;
    }
*/


    public boolean saveRecentStation(Station station) {
        boolean insertedSuccessfully = false;
        boolean alreadyInRecent = false;
        // Station alreadyRecentStation=null;
        try {
            String sqlQueryFindAlreadyInRecent = "Select * from recent_station where station_name='"
                    + station.getStationName() + "'" + " And genre='"
                    + station.getGenre() + "'";
            Cursor cursor = mSQLiteDatabase.rawQuery(sqlQueryFindAlreadyInRecent, null);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                alreadyInRecent = true;

                break;
            }

            if (cursor != null)
                cursor.close();
            ContentValues cv = new ContentValues();
            cv.put("br", station.getBitrate());
            cv.put("genre", station.getGenre());
            cv.put("station_id", station.getStationId());
            cv.put("station_name", station.getStationName());
            cv.put("date", getDateTime());
            if (alreadyInRecent) {
                mSQLiteDatabase.update("recent_station", cv, "station_name=" + station.getStationName(), null);
            } else {
                mSQLiteDatabase.insert("recent_station", null, cv);
            }

            closeSQLiteDatabase();
            insertedSuccessfully = true;
        } catch (Exception e) {
            insertedSuccessfully = false;

        }

        return insertedSuccessfully;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public TuneInBase getRecentStationList() {
        TuneInBase tuneInBase = new TuneInBase();
        tuneInBase.setLoadCategory(StationParser.PARSER_STATION);
        ArrayList<Station> recentStationList = new ArrayList<>();
        String sqlQuery = "Select * from recent_station";
        Cursor cursor = mSQLiteDatabase.rawQuery(sqlQuery, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Station station = new Station();
            station.setBitrate(cursor.getString(cursor.getColumnIndex("br")));
            station.setGenre(cursor.getString(cursor.getColumnIndex("genre")));
            station.setStationId(cursor.getString(cursor.getColumnIndex("station_id")));
            station.setStationName(cursor.getString(cursor.getColumnIndex("station_name")));
            station.setDateTimeWhenLastAccesed(cursor.getString(cursor.getColumnIndex("date")));
            recentStationList.add(station);
        }
        tuneInBase.setStationsList(recentStationList);
        return tuneInBase;
    }


    public void closeSQLiteDatabase() {
        if (mSQLiteDatabase != null)
            mSQLiteDatabase.close();
    }
}