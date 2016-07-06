package radio.pps.android.com.radio.utils;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import radio.pps.android.com.radio.Pojo.TuneInBase;
import radio.pps.android.com.radio.interfaces.NetworksStatusCallBack;

/**
 * Created by Prabhpreet on 31-10-2015.
 */
public class DownloadStations {
    private DownloadStationsAsync downloadStationsAsync;

    public boolean isAsyncDownloading() {
        return asyncDownloading;
    }


    public boolean asyncDownloading = false;
    private static final String TAG = "Download_Stations";
    private String URl;
    private Context context;
    private int parsingCode;

    public DownloadStations() {

    }

    public DownloadStations(Context context, String URl, int parsingCode, NetworksStatusCallBack networksStatusCallBack) {
        try {
            java.net.URLEncoder.encode(URl, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        this.URl = URl.replace(" ", "%20");
        this.parsingCode = parsingCode;
        this.context = context;
        downloadStationsAsync = new DownloadStationsAsync(networksStatusCallBack);
        asyncDownloading = true;
        downloadStationsAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    private class DownloadStationsAsync extends AsyncTask<Void, Void, Void> {
        private TuneInBase stationList;
        private NetworksStatusCallBack networksStatusCallBack;
        private int result = 0;
        private String response = "";

        DownloadStationsAsync(NetworksStatusCallBack networksStatusCallBack) {
            this.networksStatusCallBack = networksStatusCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (RadioSharedPreferences.getInstance(context).getAppRunsForFirstTime()) {
                try {

                    Thread.sleep(4000);
                    RadioSharedPreferences.getInstance(context).setAppRunsForFirstTime(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            try {
                /* forming th java.net.URL object */
                URL url = new URL(URl);
                urlConnection = (HttpURLConnection) url.openConnection();
                 /* optional request header */
                urlConnection.setRequestProperty("Content-Type", "application/json");

                /* optional request header */
                urlConnection.setRequestProperty("Accept", "application/json");

                /* for Get request */
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    StationParser stationParser = new StationParser(parsingCode, context);
                    switch (parsingCode) {
                        case StationParser.PARSER_STATION:
                            stationList = stationParser.parseStationStream(inputStream);
                            break;
                        case StationParser.PARSER_GENERE:
                            stationList = stationParser.parseStationGenreStream(inputStream);
                            break;
                    }

                    response = "Successful";
//                    parseResult(response);
                    result = 1; // Successful
                } else {
                    response = "Failed to fetch data";
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
//                Log.d(TAG, e.getLocalizedMessage());
                response = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            networksStatusCallBack.getStatus(result, response, stationList);
            asyncDownloading = false;
        }

    }


    public void stopAsyncTask() {
        if (downloadStationsAsync != null)
            downloadStationsAsync.cancel(true);

    }

}
