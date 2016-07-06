package radio.pps.android.com.radio.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import radio.pps.android.com.radio.interfaces.NetworksStatusCallBack;

public class GetStreamingUrlThroughPlsFile {

    private String response;
    private static String LOGTAG = "GetStreamingUrlThroughPlsFile";
    private Context mContext;
    private String stationId;
    private String stationName;
    private NetworksStatusCallBack networksStatusCallBack;

    public GetStreamingUrlThroughPlsFile(Context context, String stationId, String stationName, String url) {
        Log.i(LOGTAG, "call to constructor");
        this.mContext = context;
        this.stationId = stationId;
        this.stationName = stationName;
        MyAsyncTask myAsyncTask = new MyAsyncTask(url);
        myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

    }

    public void setListner(NetworksStatusCallBack networksStatusCallBack) {
        this.networksStatusCallBack = networksStatusCallBack;
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private String url;
        List<String> list;

        public MyAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            list = getStreamingUrl(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (list != null && !list.isEmpty()) {
                    if (networksStatusCallBack != null) {
                        String[] strData = new String[3];
                        strData[0] = stationId;
                        strData[1] = stationName;
                        strData[2] = list.get(0);
                        networksStatusCallBack.onResultBack(result, response, list, strData);
                        RadioSharedPreferences.getInstance(mContext).saveCurrentPlayingStation(stationId, stationName, strData[2]);
                    }
                    for (String url : list) {
                        System.out.println(url);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public List<String> getStreamingUrl(String url) {

        Log.i(LOGTAG, "get streaming url " + url);
        final BufferedReader br;
        String mUrls = null;
        List<String> murls = null;
        try {
            URLConnection mUrl = new URL(url).openConnection();
            br = new BufferedReader(
                    new InputStreamReader(mUrl.getInputStream()));
            murls = new ArrayList<>();
            while (true) {
                try {
                    String line = br.readLine();

                    if (line == null) {
                        break;
                    }
                    mUrls = parseLine(line);
                    if (mUrls != null && !mUrls.equals("")) {
                        murls.add(mUrls);
                        response = "Succesful";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    response = "Failed to fetch data";
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            response = "Failed to fetch data";
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            response = "Failed to fetch data";
            e.printStackTrace();
        }
        Log.i(LOGTAG, "url to stream :" + mUrls);
        return murls;
    }

    private String parseLine(String line) {
        if (line == null) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.indexOf("http") >= 0) {
            return trimmed.substring(trimmed.indexOf("http"));
        }
        return "";
    }
}
