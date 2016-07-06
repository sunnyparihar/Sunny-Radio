package radio.pps.android.com.radio;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import radio.pps.android.com.radio.Constants.Constants;
import radio.pps.android.com.radio.Constants.StaticMethods;
import radio.pps.android.com.radio.Pojo.TuneInBase;
import radio.pps.android.com.radio.interfaces.NetworksStatusCallBack;
import radio.pps.android.com.radio.utils.DownloadStations;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;
import radio.pps.android.com.radio.utils.StationParser;

public class LaunchScreenPhone extends AppCompatActivity {
    private static final String LOG_TAG = "LaunchScreenPhone";
    private Context mContext;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private RippleBackground rippleBackground;
    private View mLayout;
    private int registeredVersion;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private int currentVersion;
    private boolean mainDBCopiedSuccesfully = false, userDbCopiedSuccesfully = false;
    private long recived;
    private long send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

// Get traffic data
//            recived = TrafficStats.getUidRxBytes(Process.myUid());
//            send = TrafficStats.getUidTxBytes(Process.myUid());
//            Toast.makeText(getApplicationContext(),"Uid "+ Process.myUid()+
//                    "\n Recieved bytes "+recived +"\n Transmit bytes "+send ,Toast.LENGTH_LONG).show();
//            System.out.println("Uid "+ Process.myUid()+
//                    "\n Recieved bytes "+recived +"\n Transmit bytes "+send );
//            String rxMb=StaticMethods.humanReadableByteCount(recived,true);
//            String txMb=StaticMethods.humanReadableByteCount(send,true);
//            System.out.println("Uid "+ Process.myUid()+
//                    "\n Recieved mb "+rxMb +"\n Transmit mb "+txMb );

        try {
            StaticMethods.changeLayoutDirection(this);
        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen_phone);
        mContext = getApplicationContext();
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        mLayout = findViewById(R.id.main_layout);
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            rippleBackground.startRippleAnimation();
        } else {
            rippleBackground.stopRippleAnimation();
        }

        // Check if the WriteStorage permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            RadioSharedPreferences.getInstance(mContext).saveAppDbPath(getFilesDir() + "/" + Constants.APPDB_NAME);
            RadioSharedPreferences.getInstance(mContext).saveUserDbPath(getFilesDir() + "/" + Constants.APPUSERDB);
            if (copyDbEvaluate()) {

                mainDBCopiedSuccesfully = copyDb(RadioSharedPreferences.getInstance(mContext).getAppDbPath());
                userDbCopiedSuccesfully = copyUserDb(RadioSharedPreferences.getInstance(mContext).getUserDbPath());

            } else {
                File fileRadioDB = new File(RadioSharedPreferences.getInstance(mContext).getAppDbPath());
                File fileUserDB = new File(RadioSharedPreferences.getInstance(mContext).getUserDbPath());
                if (!fileRadioDB.exists())
                    mainDBCopiedSuccesfully = copyDb(RadioSharedPreferences.getInstance(mContext).getAppDbPath());
                if (!fileUserDB.exists())
                    userDbCopiedSuccesfully = copyUserDb(RadioSharedPreferences.getInstance(mContext).getUserDbPath());
            }

            if (mainDBCopiedSuccesfully && userDbCopiedSuccesfully) {
                RadioSharedPreferences.getInstance(mContext).setVolumeIsNotLoaded(true);
                downloadStations();
            } else {
                if (RadioSharedPreferences.getInstance(mContext).getVolumeIsNotLoaded()) {
                    RadioSharedPreferences.getInstance(mContext).setVolumeIsNotLoaded(false);
                    Snackbar.make(mLayout,
                            "Problem occurs please try after sometime.",
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    downloadStations();
                }

            }

        } else {
            // Permission is missing and must be requested.
            requestCameraPermission();
        }


    }


    /**
     * used to copy the ipkgDb and SSO database in the application private folder
     */
    private boolean copyDb(String dbName) {
        try {
            InputStream inputStream = getResources().getAssets().open(Constants.APPDB_NAME);
            OutputStream outputStream = new FileOutputStream(dbName);
            copyFile(inputStream, outputStream);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * used to copy the ipkgDb and SSO database in the application private folder
     */
    private boolean copyUserDb(String dbName) {
        try {
            InputStream inputStream = getResources().getAssets().open(Constants.APPUSERDB);
            OutputStream outputStream = new FileOutputStream(dbName);
            copyFile(inputStream, outputStream);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * used to copy the zip file into SDCARD it takes input as InputStream and
     * OutputStream where to copy that file
     */
    private void copyFile(InputStream fromFile, OutputStream toFile)
            throws IOException {
        byte[] buffer = new byte[1024];
        int iLength;
        try {
            while ((iLength = fromFile.read(buffer)) > 0) {
                toFile.write(buffer, 0, iLength);
            }
        }
        // Close the streams
        finally {
            try {
                if (toFile != null) {
                    try {
                        toFile.flush();
                    } finally {
                        toFile.close();
                    }
                }
            } finally {
                if (fromFile != null) {
                    fromFile.close();
                }
            }
        }
    }


    /**
     * Requests the {@link Manifest.permission#CAMERA} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, "Write external storage access is required to write database files on Sd card.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(LaunchScreenPhone.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    "Permission is not available. Requesting Write External Storage permission.",
                    Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RadioSharedPreferences.getInstance(mContext).saveAppDbPath(getFilesDir() + "/" + Constants.APPDB_NAME);
                RadioSharedPreferences.getInstance(mContext).saveUserDbPath(getFilesDir() + "/" + Constants.APPUSERDB);
                // Permission has been granted. Start Downloda station and launch next activity Activity.
                if (copyDbEvaluate()) {

                    mainDBCopiedSuccesfully = copyDb(RadioSharedPreferences.getInstance(mContext).getAppDbPath());
                    userDbCopiedSuccesfully = copyUserDb(RadioSharedPreferences.getInstance(mContext).getUserDbPath());

                } else {
                    File fileRadioDB = new File(RadioSharedPreferences.getInstance(mContext).getAppDbPath());
                    File fileUserDB = new File(RadioSharedPreferences.getInstance(mContext).getUserDbPath());
                    if (!fileRadioDB.exists())
                        mainDBCopiedSuccesfully = copyDb(RadioSharedPreferences.getInstance(mContext).getAppDbPath());
                    if (!fileUserDB.exists())
                        userDbCopiedSuccesfully = copyUserDb(RadioSharedPreferences.getInstance(mContext).getUserDbPath());
                }

                if (mainDBCopiedSuccesfully && userDbCopiedSuccesfully) {
                    //this means database loaded succesfully and set flag to false.
                    RadioSharedPreferences.getInstance(mContext).setVolumeIsNotLoaded(false);
                    downloadStations();
                } else {
                    if (RadioSharedPreferences.getInstance(mContext).getVolumeIsNotLoaded()) {
                        RadioSharedPreferences.getInstance(mContext).setVolumeIsNotLoaded(false);
                        Snackbar.make(mLayout,
                                "Problem occurs please try after sometime.",
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        downloadStations();
                    }
                }

            } else {
                // Permission request was denied.
                requestCameraPermission();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void downloadStations() {
        new DownloadStations(mContext, Constants.SHOUTCAST_TOP500_URL + Constants.APIKEY, StationParser.PARSER_STATION
                , new NetworksStatusCallBack() {
            @Override
            public void getStatus(int result, String strResponse, final TuneInBase stationList) {
                super.getStatus(result, strResponse);
                rippleBackground.stopRippleAnimation();
                switch (result) {
                    case 0:
                        Toast.makeText(getApplicationContext(), getString(R.string.fetching_station_error), Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        new DownloadStations(LaunchScreenPhone.this, Constants.SHOUTCAST_GENRELIST_URL, StationParser.PARSER_GENERE, new NetworksStatusCallBack() {
                            @Override
                            public void getStatus(int result, String strResponse, TuneInBase genreList) {
                                super.getStatus(result, strResponse);
                                switch (result) {
                                    case 0:
                                        Toast.makeText(mContext, getString(R.string.fetching_station_error), Toast.LENGTH_LONG).show();
                                        break;
                                    case 1:
                                        Intent intentHomeScreen = new Intent(mContext, HomeScreenActivity.class);
                                        intentHomeScreen.putExtra("listTop500", stationList);
                                        intentHomeScreen.putExtra("listGenre", genreList);
                                        startActivity(intentHomeScreen);
                                        finish();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * @return true when app runs first time
     * @retrun true when app version code has been change after update app
     **/
    public boolean copyDbEvaluate() {
        boolean volumeIsNotLoaded = RadioSharedPreferences.getInstance(mContext).getVolumeIsNotLoaded();
        // Get app version
        registeredVersion = RadioSharedPreferences.getInstance(mContext).getAppRegisteredVersion();
        currentVersion = StaticMethods.getAppVersion(mContext);
        System.out.println("registered version " + registeredVersion + "current version " + currentVersion);
        if (volumeIsNotLoaded) {
            if (registeredVersion != currentVersion) {
                Log.i(LOG_TAG, "App version changed.");
                RadioSharedPreferences.getInstance(mContext).setAppRegisteredVersion(StaticMethods.getAppVersion(mContext));
            }
            return true;
        }
        if (registeredVersion != currentVersion) {
            Log.i(LOG_TAG, "App version changed.");
            RadioSharedPreferences.getInstance(mContext).setAppRegisteredVersion(StaticMethods.getAppVersion(mContext));
            return true;
        }
        return false;
    }
}
