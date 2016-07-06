package radio.pps.android.com.radio.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import radio.pps.android.com.radio.Constants.Constants;
import radio.pps.android.com.radio.Constants.GlobalLevelVariable;
import radio.pps.android.com.radio.Constants.SharedPref_Bundle_Keys_val;
import radio.pps.android.com.radio.LaunchScreenPhone;
import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.interfaces.NetworksStatusCallBack;
import radio.pps.android.com.radio.utils.GetStreamingUrlThroughPlsFile;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

/* This file contains the source code for examples discussed in Tutorials 1-9 of developerglowingpigs YouTube channel.
 *  The source code is for your convenience purposes only. The source code is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*/


public class PlayStreamService extends Service implements OnCompletionListener,
        OnPreparedListener, OnErrorListener, OnSeekCompleteListener,
        OnInfoListener, OnBufferingUpdateListener {

    private static final String TAG = "PLAYSTREAMSERVICE";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    // Change this int to some number specifically for this app
    private int notifyId = 26121992;

    //-- URL location of audio clip PUT YOUR AUDIO URL LOCATION HERE ---

    // Set up the notification ID
    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    // ---Variables for seekbar processing---
    int mediaPosition;
    int mediaMax;
    //Intent intent;
    private final Handler handler = new Handler();

    //	public static final String BROADCAST_ACTION = "com.glowingpigs.tutorialstreamaudiopart1b.seekprogress";
    Intent bufferIntent;
    //	Intent seekIntent;

    // Declare headsetSwitch variable
    private int headsetSwitch = 1;
    private Notification mBuilder;
    private String[] station;
    private Service service;
    private Intent stopService, playPauseIntent;
    private long callAfterSomeSeconds = 12000;
    private String song;
    private String artist;

    // OnCreate
    @Override
    public void onCreate() {
        Log.v(TAG, "Creating Service");
        service = this;
        bufferIntent = new Intent(Constants.BROADCAST_BUFFER);
        stopService = new Intent(Constants.BROADCAST_STOP_SERVICE);
        //setup Intent for play pause control through activity
        playPauseIntent = new Intent(Constants.BROADCAST_PLAY_PAUSE_STOP);

        IntentFilter info = new IntentFilter();
        info.addAction(Constants.BROADCAST_UPDATE_TITLE);

        mediaPlayer.setAudioSessionId(Constants.MEDIA_PLAYER_AUDIO_SESSION);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();

        // Register headset receiver
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        // Register headset receiver
        registerReceiver(broadcastStopServiceReceiver
                , new IntentFilter(
                        Constants.BROADCAST_STOP_SERVICE));

        registerReceiver(mUpdateStreamTitle, info);

        // Register headset receiver
        registerReceiver(playPauseBroadCastReceiver
                , new IntentFilter(
                        Constants.BROADCAST_PLAY_PAUSE_STOP));

    }

    BroadcastReceiver playPauseBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int playPause = intent.getIntExtra(SharedPref_Bundle_Keys_val.KEY_PLAY_PAUSE, 0);
            switch (playPause) {
                case Constants.STATUSSTOP:
                    stopMedia();
                    stopSelf();
                    break;
                case Constants.STATUSPLAY:
                    playMedia();
                    break;
                case Constants.STATUSPAUSE:
                    pauseMedia();
                    break;

            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Intent intentUpdateUi = new Intent(Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment);
        intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_NAME, "");
        intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_ARTIST, "");
        GlobalLevelVariable globalLevelVariable = (GlobalLevelVariable) getApplicationContext();
        globalLevelVariable.updateRecentStation();
        if (intent.getExtras().getBoolean(SharedPref_Bundle_Keys_val.KEY_DIRECTURL_BOOL, false)) {
            station = new String[2];
            station[0] = intent.getExtras().getString(SharedPref_Bundle_Keys_val.keyStationId);
            station[1] = intent.getExtras().getString(SharedPref_Bundle_Keys_val.keyStationName);
            station[2] = intent.getExtras().getString(SharedPref_Bundle_Keys_val.KEY_STATION_TUNE_URL);
            initPlayer(station);

        } else {
            String id = intent.getExtras().getString(SharedPref_Bundle_Keys_val.keyStationId);
            String name = intent.getExtras().getString(SharedPref_Bundle_Keys_val.keyStationName);
            String tuneUrl = intent.getExtras().getString(SharedPref_Bundle_Keys_val.KEY_STATION_TUNE_URL);
            System.out.println("Tune urls " + tuneUrl);

            GetStreamingUrlThroughPlsFile getStreamingUrlThroughPlsFile = new GetStreamingUrlThroughPlsFile(this, id, name, tuneUrl);
            getStreamingUrlThroughPlsFile.setListner(new NetworksStatusCallBack() {
                @Override
                public void onResultBack(Void result, String response, List<String> listData, String[] strData) {
                    super.onResultBack(result, response, listData, strData);
                    GlobalLevelVariable globalLevelVariable1 = (GlobalLevelVariable) getApplicationContext();
                    globalLevelVariable1.setCurrentStationData(strData);
                    initPlayer(strData);
                }
            });
        }
        sendBroadcast(intentUpdateUi);
        sendBufferCompleteBroadcast();
        return START_NOT_STICKY;
    }

    private void initPlayer(String[] strData) {
        {

            station = strData;
            Log.v(TAG, "Starting telephony");
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Log.v(TAG, "Starting listener");
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    // String stateString = "N/A";
                    Log.v(TAG, "Starting CallStateChange");
                    switch (state) {
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        case TelephonyManager.CALL_STATE_RINGING:
                            if (mediaPlayer != null) {
                                try {
                                    pauseMedia();
                                } catch (Exception e) {
                                }

                                isPausedInCall = true;
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            // Phone idle. Start playing.
                            if (mediaPlayer != null) {
                                if (isPausedInCall) {
                                    isPausedInCall = false;
                                    playMedia();
                                }
                            }
                            break;
                    }
                }
            };

            // Register the listener with the telephony manager
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);


            mediaPlayer.reset();
            // Set up the MediaPlayer data source using the strAudioLink value
            if (!mediaPlayer.isPlaying()) {
                try {
                    mediaPlayer.setDataSource(station[2]);

                    // Send message to Activity to display progress dialogue
                    sendBufferingBroadcast();
                    // Prepare mediaplayer
                    mediaPlayer.prepareAsync();
//					Toast.makeText(getApplicationContext(),"AudioSession id "+ mediaPlayer.getAudioSessionId(),Toast.LENGTH_LONG).show();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                }

                getNowPlaying(station[2]);
                startUpdateSongNameOnUI();
            }
        }
    }

    private void startUpdateSongNameOnUI() {
        Intent ints = new Intent();
        ints.setAction(Constants.BROADCAST_UPDATE_TITLE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(PlayStreamService.this, 0, ints, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + callAfterSomeSeconds, pendingIntent);
    }

    private void cancelUpdateSongNameOnUI() {
        Intent ints = new Intent();
        ints.setAction(Constants.BROADCAST_UPDATE_TITLE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(PlayStreamService.this, 0, ints, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    // ---Send seekbar info to activity----
    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            // // Log.d(TAG, "entered sendUpdatesToUI");
            LogMediaPosition();
            handler.postDelayed(this, 1000); // 2 seconds

        }
    };

    private void LogMediaPosition() {
        // // Log.d(TAG, "entered LogMediaPosition");
        if (mediaPlayer.isPlaying()) {
            mediaPosition = mediaPlayer.getCurrentPosition();
            // if (mediaPosition < 1) {
            // Toast.makeText(this, "Buffering...", Toast.LENGTH_SHORT).show();
            // }
            mediaMax = mediaPlayer.getDuration();
            //seekIntent.putExtra("time", new Date().toLocaleString());
//			seekIntent.putExtra("counter", String.valueOf(mediaPosition));
//			seekIntent.putExtra("mediamax", String.valueOf(mediaMax));
//			seekIntent.putExtra("song_ended", String.valueOf(songEnded));
//			sendBroadcast(seekIntent);
        }
    }

    // --Receive seekbar position if it has been changed by the user in the
//	// activity
    private BroadcastReceiver broadcastStopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean stopService;
            if (Constants.BROADCAST_STOP_SERVICE.equalsIgnoreCase(intent.getAction())) {
                stopService = intent.getBooleanExtra("stopService", false);
                if (stopService) {
                    stopSelf();
                }
            }
        }
    };

    private BroadcastReceiver mUpdateStreamTitle = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_UPDATE_TITLE)) {
                getNowPlaying(station[2]);
                startUpdateSongNameOnUI();
            }

//            String action = intent.getAction();
//            String cmd = intent.getStringExtra("command");
//            Log.v("tag ", action + " / " + cmd);
//            String artist = intent.getStringExtra("artist");
//            String album = intent.getStringExtra("album");
//            String track = intent.getStringExtra("track");
//            Log.v("tag",artist+":"+album+":"+track);
//            Toast.makeText(context,"track "+track+"\n Artist "+artist,Toast.LENGTH_SHORT).show();
        }
    };


//	// --Receive seekbar position if it has been changed by the user in the
//	// activity
//	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			updateSeekPos(intent);
//		}
//	};
//
//	// Update seek position from Activity
//	public void updateSeekPos(Intent intent) {
//		int seekPos = intent.getIntExtra("seekpos", 0);
//		if (mediaPlayer.isPlaying()) {
//			handler.removeCallbacks(sendUpdatesToUI);
//			mediaPlayer.seekTo(seekPos);
//			setupHandler();
//		}
//
//	}

    // ---End of seekbar code

    // If headset gets unplugged, stop music and service.
    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Log.v(TAG, "ACTION_HEADSET_PLUG Intent received");
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    headsetSwitch = 0;
                    // Log.v(TAG, "State =  Headset disconnected");
                    // headsetDisconnected();
                } else if (!headsetConnected
                        && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    headsetSwitch = 1;
                    // Log.v(TAG, "State =  Headset connected");
                }

            }

            switch (headsetSwitch) {
                case (0):
                    headsetDisconnected();
                    break;
                case (1):
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager.isSpeakerphoneOn()) {
                        audioManager.setSpeakerphoneOn(false);
                    }
                    break;
            }
        }

    };

    private void headsetDisconnected() {
//		stopMedia();
//		stopSelf();
        if (mediaPlayer.isPlaying())
            pauseMedia();

    }

    // --- onDestroy, stop media player and release.  Also stop phoneStateListener, notification, receivers...---
    @Override
    public void onDestroy() {
        RadioSharedPreferences.getInstance(getApplicationContext()).isPlaying(Constants.STATUSSTOP);
        updateHomeScreenUI();
        cancelUpdateSongNameOnUI();
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();

        }

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }

        // Cancel the notification
        cancelNotification();

        // Unregister headsetReceiver
        unregisterReceiver(headsetReceiver);

        // Unregister seekbar receiver
        unregisterReceiver(broadcastStopServiceReceiver);

        unregisterReceiver(playPauseBroadCastReceiver);

        unregisterReceiver(mUpdateStreamTitle);
        // Stop the seekbar handler from sending updates to UI
//		handler.removeCallbacks(sendUpdatesToUI);

        // Service ends, need to tell activity to display "Play" button
//		resetButtonPlayStopBroadcast();
    }

    //	// Send a message to Activity that audio is being prepared and buffering
//	// started.
    private void sendBufferingBroadcast() {
        // Log.v(TAG, "BufferStartedSent");
        bufferIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_BUFFER_SATUS, 1);
        bufferIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_BUFFER_TITLE, getString(R.string.buffering));
        bufferIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_BUFFER_MESSAGE, getString(R.string.pleaseWait));

        sendBroadcast(bufferIntent);
    }

    // Send a message to Activity that audio is prepared and ready to start
    // playing.
    private void sendBufferCompleteBroadcast() {
        // Log.v(TAG, "BufferCompleteSent");
        bufferIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_BUFFER_SATUS, 0);
        sendBroadcast(bufferIntent);
    }

//	// Send a message to Activity to reset the play button.
//	private void resetButtonPlayStopBroadcast() {
//		// Log.v(TAG, "BufferCompleteSent");
//		bufferIntent.putExtra("buffering", "2");
//		sendBroadcast(bufferIntent);
//	}

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
        // TODO Auto-generated method stub
        System.out.println("Buffering " + arg1);
    }

    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

        if (!mediaPlayer.isPlaying()) {
            playMedia();
//			Toast.makeText(this,
//					"SeekComplete", Toast.LENGTH_SHORT).show();
        }

    }


    //---Error processing ---
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this,
                        "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra,
                        Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//				Toast.makeText(this, "MEDIA ERROR SERVER DIED " + extra,
//						Toast.LENGTH_SHORT).show();
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
//				Toast.makeText(this, "MEDIA ERROR UNKNOWN " + extra,
//						Toast.LENGTH_SHORT).show();
                Toast.makeText(this, station[1] + " is temporarily unavailable, please try later or choose another station ", Toast.LENGTH_SHORT).show();
                break;

        }
        RadioSharedPreferences.getInstance(service).isPlaying(Constants.STATUSSTOP);
        sendBufferCompleteBroadcast();
        cancelUpdateSongNameOnUI();
        cancelNotification();
        stopMedia();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer arg0) {

        // Send a message to activity to end progress dialogue

        sendBufferCompleteBroadcast();
        playMedia();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // When song ends, need to tell activity to display "Play" button
        stopMedia();
        stopSelf();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            RadioSharedPreferences.getInstance(getApplicationContext()).isPlaying(Constants.STATUSPLAY);
            updateHomeScreenUI();
            startUpdateSongNameOnUI();
            initNotification(RadioSharedPreferences.getInstance(PlayStreamService.this).getCurrentPlayingStation()[1]
                    , song + (artist.length() == 0 ? "" : ", " + artist));
        }
    }

    // Add for Telephony Manager
    public void pauseMedia() {
        // Log.v(TAG, "Pause Media");
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                RadioSharedPreferences.getInstance(service).isPlaying(Constants.STATUSPAUSE);
                updateHomeScreenUI();
                cancelUpdateSongNameOnUI();
                initNotification(RadioSharedPreferences.getInstance(PlayStreamService.this).getCurrentPlayingStation()[1]
                        , song + (artist.length() == 0 ? "" : ", " + artist));
            }
        } catch (IllegalStateException illegalStateException) {
            Log.i(TAG, "IllegalStateException while pauseMedia()");
        }


    }

    public void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            RadioSharedPreferences.getInstance(service).isPlaying(Constants.STATUSSTOP);
            updateHomeScreenUI();
            cancelUpdateSongNameOnUI();
        }
    }

    private void updateHomeScreenUI() {
        Intent intent = new Intent(Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment);
        sendBroadcast(intent);
    }

    // Create Notification
    private void initNotification(String notificationTitle, String notificationSubtile) {

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        switch (currentApiVersion) {
            case Build.VERSION_CODES.JELLY_BEAN:
                jellyBeanNotification(notificationTitle, notificationSubtile);
                break;
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
                break;
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
                break;
            default:
                notification(notificationTitle, notificationSubtile);
        }

    }

    private void jellyBeanNotification(String notificationTitle, String notificationSubtile) {

    }


    private void notification(String notificationTitle, String notificationSubtile) {
        int drawable = 0;
        String strPlayPause = null;
        PendingIntent playPausePendingIntent = null;
        PendingIntent stopPendingIntent = null;
        Intent notificationIntent = new Intent(getApplicationContext(), LaunchScreenPhone.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 1000,
                notificationIntent, 0);
        int playPause = RadioSharedPreferences.getInstance(getApplicationContext()).isGetPlaying();
        Intent intentPlayPause = new Intent(Constants.BROADCAST_PLAY_PAUSE_STOP);
        Intent intentStop = new Intent(Constants.BROADCAST_PLAY_PAUSE_STOP);
        intentStop.putExtra(SharedPref_Bundle_Keys_val.KEY_PLAY_PAUSE, Constants.STATUSSTOP);
        stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext()
                , 1001
                , intentPlayPause, 0);
        switch (playPause) {
            case Constants.STATUSPLAY:
                intentPlayPause.putExtra(SharedPref_Bundle_Keys_val.KEY_PLAY_PAUSE, Constants.STATUSPAUSE);
                strPlayPause = "Pause";
                drawable = android.R.drawable.ic_media_pause;
                playPausePendingIntent = PendingIntent.getBroadcast(getApplicationContext()
                        , 1002
                        , intentPlayPause, 0);
                break;
            case Constants.STATUSPAUSE:
                drawable = android.R.drawable.ic_media_play;
                intentPlayPause.putExtra(SharedPref_Bundle_Keys_val.KEY_PLAY_PAUSE, Constants.STATUSPLAY);
                strPlayPause = "Play";
                playPausePendingIntent = PendingIntent.getBroadcast(getApplicationContext()
                        , 1003
                        , intentPlayPause, 0);
                break;
        }

        try {
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle(notificationTitle)
//					.setTicker("Radio Music Player")
                    .setContentText(notificationSubtile)
                    .setSmallIcon(R.drawable.ic_radio_white_24dp)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(contentIntent)
                    .setOngoing(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(R.drawable.ic_media_stop,
                            "Stop", stopPendingIntent)
                    .addAction(drawable, strPlayPause,
                            playPausePendingIntent).build();
//				.addAction(android.R.drawable.ic_media_next, "Next",
//						contentIntent).build();
            startForeground(notifyId,
                    notification);
        } catch (Exception e) {

        }

    }

    // Cancel Notification
    private void cancelNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.cancel(notifyId);
    }

    public void getNowPlaying(final String streamUrl) {
        Log.w("getNowPlaying", "fired");
        song = "";
        artist = "";
        new Thread(new Runnable() {
            String songInfo = "";

            public void run() {
                String title = null, djName = null;
                try {
                    URL updateURL = new URL(streamUrl);
                    URLConnection conn = updateURL.openConnection();
                    conn.setRequestProperty("Icy-MetaData", "1");
                    int interval = Integer
                            .valueOf(conn.getHeaderField("icy-metaint")); // You can get more headers if you wish. There is other useful data.

                    InputStream is = conn.getInputStream();

                    int skipped = 0;
                    while (skipped < interval) {
                        skipped += is.skip(interval - skipped);
                    }

                    int metadataLength = is.read() * 16;

                    int bytesRead = 0;
                    int offset = 0;
                    byte[] bytes = new byte[metadataLength];

                    while (bytesRead < metadataLength && bytesRead != -1) {
                        bytesRead = is.read(bytes, offset, metadataLength);
                        offset = bytesRead;
                    }

                    songInfo = new String(bytes).trim();
                    String[] metaData = songInfo.split("'");
                    String[] metaData1 = metaData[1].split("-");
                    if (metaData1.length == 2) {
                        song = metaData1[1].trim();
                        artist = metaData1[0].trim();

                    } else {
                        song = metaData[1].trim();
                        artist = "";
                    }
                    Intent intentUpdateUi = new Intent(Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment);
                    intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_UPDATE_TITLE, 1);
                    intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_NAME, song);
                    intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_ARTIST, artist);
                    sendBroadcast(intentUpdateUi);
                    initNotification(RadioSharedPreferences.getInstance(PlayStreamService.this).getCurrentPlayingStation()[1]
                            , song + (artist.length() == 0 ? "" : ", " + artist));
//                    title = metaData.substring(metaData.indexOf("StreamTitle='") + 13, metaData.indexOf(" / ", metaData.indexOf("StreamTitle='"))).trim();
//                    djName = metaData.substring(metaData.indexOf(" / ", metaData.indexOf("StreamTitle='")) + 3, metaData.indexOf("';", metaData.indexOf("StreamTitle='"))).trim();
                    Log.w("metadata", songInfo);
                    is.close();
                    return;
                } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBound) {
//                    Log.i(TAG,arrayIndexOutOfBound.getMessage());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intentUpdateUi = new Intent(Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment);
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_UPDATE_TITLE, 1);
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_NAME, "");
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_ARTIST, "");
                sendBroadcast(intentUpdateUi);
            }
        }).start();
    }

}
