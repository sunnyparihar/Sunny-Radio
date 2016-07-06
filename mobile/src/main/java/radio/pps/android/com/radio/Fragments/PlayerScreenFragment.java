package radio.pps.android.com.radio.Fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import radio.pps.android.com.radio.Constants.Constants;
import radio.pps.android.com.radio.Constants.GlobalLevelVariable;
import radio.pps.android.com.radio.Constants.SharedPref_Bundle_Keys_val;
import radio.pps.android.com.radio.CustomWidgets.progressgoogle.ProgressLayout;
import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.services.PlayStreamService;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

/**
 * Created by Prabhpreet on 11-11-2015.
 */
public class PlayerScreenFragment extends Fragment {
    private ProgressDialog pdBuff;
    private final String TAG = "PlayerScreenFragment";
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private Toolbar bottomToolBar;
    private int playPause;
    private Context mContext;
    private Intent playPauseBroadCast = new Intent(Constants.BROADCAST_PLAY_PAUSE_STOP);
    private ProgressLayout progressLayout;
    private FloatingActionButton floatingActionButton, floatiingSharebutton;
    private TextView tvSongName;
    private ImageView btnCollapsedExapandSlidingPannel;
    private TextView tvArtistName;
    private boolean mRegisteredBroadcastReciever = false;
    private boolean showBufferDialogue = false;
    private AudioManager audioManager;
    private AppCompatSeekBar volumeSeekbar;
    private FloatingActionButton syncRadio;
    private Button btnNext;
    private Button btnPrevious;
    private long TIME_INTERVAL = 5000;
    private SettingsContentObserver mSettingsContentObserver;

    public void setActivityContext(Context context) {
        this.mContext = context;

    }

    public void setSlidingPanelContext(SlidingUpPanelLayout slidingUpPanelLayout) {
        this.slidingUpPanelLayout = slidingUpPanelLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (mContext == null)
            mContext = getContext();
        try {
            mContext.registerReceiver(broadcastUpdateUi, new IntentFilter((Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment)));
        } catch (Exception e) {

        }
        try {
            mContext.registerReceiver(broadcastReceiverProgressBar, new IntentFilter(
                    Constants.BROADCAST_BUFFER));
            mSettingsContentObserver = new SettingsContentObserver(mContext, new Handler());
            mContext.getContentResolver().registerContentObserver(
                    android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
        } catch (Exception e) {

        }
        if (getActivity() != null)
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }


    public class SettingsContentObserver extends ContentObserver {
        private AudioManager audioManager;
        private long mBackPressed;

        public SettingsContentObserver(Context context, Handler handler) {
            super(handler);
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volumeSeekbar != null)
                volumeSeekbar.setProgress(currentVolume);
            Log.d(TAG, "Volume now " + currentVolume);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Toast.makeText(getContext(), "onConfigChange  "+ newConfig.orientation,Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.playerscreenfragment, container, false);
    }


    private void collapsedExpandSlidingUpPanel() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            slidingUpPanelLayout.requestLayout();
        } else {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            slidingUpPanelLayout.requestLayout();
        }
    }


    // Method to share either text or URL.
    private void shareTextUrl(String title, String url) {
        if (title == null || title.equalsIgnoreCase("")) {
            Toast.makeText(mContext, "Sharing is not available right now", Toast.LENGTH_LONG).show();
            return;
        } else if (url == null || url.equalsIgnoreCase("")) {
            Toast.makeText(mContext, "Sharing is not available right now", Toast.LENGTH_LONG).show();
            return;
        }
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));


//        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//        sharingIntent.setType("text/html");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<!DOCTYPE html><html><head><title>"+title+"</title><br></head><body><a href="+url+">"+title+"</a></body></html>"));
//        startActivity(Intent.createChooser(sharingIntent,"Share using"));
    }

    @Nullable
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mContext == null)
            mContext = getContext();

        progressLayout = (ProgressLayout) getView().findViewById(R.id.stripToolbarSlidingPanel);
        progressLayout.setColorScheme(R.color.colorWhite,
                R.color.colorPrimaryDark,
                R.color.colorWhite,
                R.color.colorPrimaryDark);

        floatingActionButton = (FloatingActionButton) getView().findViewById(R.id.fab_play);
        floatiingSharebutton = (FloatingActionButton) getView().findViewById(R.id.fab_share);
        floatiingSharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalLevelVariable globalLevelVariable = (GlobalLevelVariable) mContext.getApplicationContext();
                shareTextUrl(globalLevelVariable.getCurrentStationData()[1], globalLevelVariable.getCurrentStationData()[2]);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPause = RadioSharedPreferences.getInstance(mContext).isGetPlaying();
                switch (playPause) {
                    case Constants.STATUSSTOP:
                        Toast.makeText(mContext, "Select Station", Toast.LENGTH_SHORT).show();
                        break;

                    case Constants.STATUSPLAY:
                        floatingActionButton.setImageResource(R.drawable.ic_pause_white_48dp);
                        playPauseBroadCast.putExtra(SharedPref_Bundle_Keys_val.KEY_PLAY_PAUSE, Constants.STATUSPAUSE);
                        mContext.sendBroadcast(playPauseBroadCast);
                        break;
                    case Constants.STATUSPAUSE:
                        floatingActionButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        playPauseBroadCast.putExtra(SharedPref_Bundle_Keys_val.KEY_PLAY_PAUSE, Constants.STATUSPLAY);
                        mContext.sendBroadcast(playPauseBroadCast);
                        break;
                }
            }
        });
        bottomToolBar = (Toolbar) getView().findViewById(R.id.toolbarSlidingPannel);
        bottomToolBar.inflateMenu(R.menu.menu_launch_screen_phone);//changed
        btnCollapsedExapandSlidingPannel = (ImageView) getView().findViewById(R.id.ivCollapsedExpandSlidingPanel);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        tvSongName = new TextView(getActivity());
        tvArtistName = new TextView(getActivity());
        tvSongName.setText("No Station Selected");
        tvArtistName.setText("");

        tvArtistName.setTextColor(getResources().getColor(R.color.colorWhite));
        tvSongName.setTextColor(getResources().getColor(R.color.colorWhite));
        tvSongName.setMaxLines(1);
        tvArtistName.setMaxLines(1);
        tvSongName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.songTitle));
        tvArtistName.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.artistTitle));
//        tvArtistName.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
//        tvSongName.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(tvSongName);
        linearLayout.addView(tvArtistName);
        bottomToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout != null)
                    collapsedExpandSlidingUpPanel();
            }
        });
        btnCollapsedExapandSlidingPannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingUpPanelLayout != null)
                    collapsedExpandSlidingUpPanel();
            }
        });
        bottomToolBar.addView(linearLayout);
        playPause = RadioSharedPreferences.getInstance(mContext).isGetPlaying();
        //toolbar2 menu items CallBack listener
        bottomToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                if (arg0.getItemId() == R.id.action_settings) {
                    playPause = RadioSharedPreferences.getInstance(mContext).isGetPlaying();
                    switch (playPause) {
                        case Constants.STATUSSTOP:
                            Toast.makeText(mContext, "Select Station", Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.STATUSPLAY:
                            floatingActionButton.setImageResource(R.drawable.ic_pause_white_48dp);
                            playPauseBroadCast.putExtra(SharedPref_Bundle_Keys_val.KEY_PLAY_PAUSE, Constants.STATUSPAUSE);
                            mContext.sendBroadcast(playPauseBroadCast);
                            break;
                        case Constants.STATUSPAUSE:
                            floatingActionButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                            playPauseBroadCast.putExtra(SharedPref_Bundle_Keys_val.KEY_PLAY_PAUSE, Constants.STATUSPLAY);
                            mContext.sendBroadcast(playPauseBroadCast);
                            break;
                    }
                }
                return false;
            }

        });

        switch (playPause) {
            case Constants.STATUSPLAY:
                floatingActionButton.setImageResource(R.drawable.ic_pause_white_48dp);
                bottomToolBar.getMenu().findItem(R.id.action_settings).setIcon(R.drawable.ic_pause_white_48dp);
                break;
            default:
                floatingActionButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                bottomToolBar.getMenu().findItem(R.id.action_settings).setIcon(R.drawable.ic_play_arrow_white_48dp);
                break;
        }

        if (slidingUpPanelLayout != null) {
            slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                int value = 1;

                @Override
                public void onPanelSlide(View panel, float slideOffset) {

                    float alphaValue = value - slideOffset;
//                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                    bottomToolBar.setAlpha(alphaValue);
                    tvSongName.setAlpha(alphaValue);
                    progressLayout.setAlpha(alphaValue);
                }

                @Override
                public void onPanelExpanded(View panel) {
                    Log.i(TAG, "onPanelExpanded");
                    bottomToolBar.getMenu().clear();
                    btnCollapsedExapandSlidingPannel.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPanelCollapsed(View panel) {
                    Log.i(TAG, "onPanelCollapsed");
                    btnCollapsedExapandSlidingPannel.setVisibility(View.GONE);
                    bottomToolBar.getMenu().clear();
                    bottomToolBar.inflateMenu(R.menu.menu_launch_screen_phone);//changed
                    playPause = RadioSharedPreferences.getInstance(mContext).isGetPlaying();
                    switch (playPause) {
                        case Constants.STATUSPLAY:
                            floatingActionButton.setImageResource(R.drawable.ic_pause_white_48dp);
                            bottomToolBar.getMenu().findItem(R.id.action_settings).setIcon(R.drawable.ic_pause_white_48dp);
                            break;
                        default:
                            floatingActionButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                            bottomToolBar.getMenu().findItem(R.id.action_settings).setIcon(R.drawable.ic_play_arrow_white_48dp);
                            break;
                    }

                }

                @Override
                public void onPanelAnchored(View panel) {
                    Log.i(TAG, "onPanelAnchored");
                }

                @Override
                public void onPanelHidden(View panel) {
                    Log.i(TAG, "onPanelHidden");
                }
            });

            //below line of code used when this fragment is destroyed and also sliding panel is opened then set toolbar alpha value to transparent.
            if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
                bottomToolBar.setAlpha(0);
        }
        initControls();
    }


    private void initControls() {
        syncRadio = (FloatingActionButton) getView().findViewById(R.id.fab_sync);
        btnNext = (Button) getView().findViewById(R.id.btnNext);
        btnPrevious = (Button) getView().findViewById(R.id.btnPrevious);
        final GlobalLevelVariable appState = ((GlobalLevelVariable) getContext().getApplicationContext());
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] nextRecentStation = null;
                try {
                    nextRecentStation = appState.getNextRecentStation();
                    if (nextRecentStation == null || nextRecentStation[0] == null || nextRecentStation[1] == null)
                        return;
                } catch (Exception e) {

                    e.printStackTrace();
                    return;
                }

                Intent intentUpdateUi = new Intent(Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment);
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_UPDATE_TITLE, 1);
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_NAME, "");
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_ARTIST, "");
                getContext().sendBroadcast(intentUpdateUi);
                Intent intents = new Intent(Constants.BROADCAST_STOP_SERVICE);
                mContext.sendBroadcast(intents);
                try {
                    Intent serviceIntent = new Intent(mContext, PlayStreamService.class);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationId, nextRecentStation[0]);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationName, nextRecentStation[1]);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_STATION_TUNE_URL, nextRecentStation[2]);
                    mContext.startService(serviceIntent);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });

        syncRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] station = RadioSharedPreferences.getInstance(getContext()).getCurrentPlayingStation();
                Intent intents = new Intent(Constants.BROADCAST_STOP_SERVICE);
                mContext.sendBroadcast(intents);
                try {
                    Intent serviceIntent = new Intent(mContext, PlayStreamService.class);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationId, station[0]);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationName, station[1]);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_STATION_TUNE_URL, Constants.TUNE_A_STATION + "/sbin/tunein-station.pls" +
                            "?id=" + station[0]);
                    mContext.startService(serviceIntent);
                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(
                            mContext.getApplicationContext(),
                            e.getClass().getName() + " " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] previousRecentStation = null;
                try {
                    previousRecentStation = appState.getPreviousRecentStation();
                    if (previousRecentStation == null || previousRecentStation[0] == null || previousRecentStation[1] == null)
                        return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                Intent intentUpdateUi = new Intent(Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment);
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_UPDATE_TITLE, 1);
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_NAME, "");
                intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_ARTIST, "");
                getContext().sendBroadcast(intentUpdateUi);
                Intent intents = new Intent(Constants.BROADCAST_STOP_SERVICE);
                mContext.sendBroadcast(intents);
                try {
                    Intent serviceIntent = new Intent(mContext, PlayStreamService.class);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationId, previousRecentStation[0]);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationName, previousRecentStation[1]);
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_STATION_TUNE_URL, previousRecentStation[2]);
                    mContext.startService(serviceIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            volumeSeekbar = (AppCompatSeekBar) getView().findViewById(R.id.skbar_volume);
            audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUIForTitle_AndPlayPause(Intent intent) {
        try {
            switch (intent.getIntExtra(SharedPref_Bundle_Keys_val.KEY_UPDATE_TITLE, 0)) {
                case 0:
                    playPause = RadioSharedPreferences.getInstance(mContext).isGetPlaying();
                    switch (playPause) {
                        case Constants.STATUSPLAY:
                            floatingActionButton.setImageResource(R.drawable.ic_pause_white_48dp);
                            try {
                                bottomToolBar.getMenu().findItem(R.id.action_settings).setIcon(R.drawable.ic_pause_white_48dp);
                            } catch (NullPointerException npe) {
                                Log.i(TAG, "NullPointerException while setting icon bottom toolbar mentu play pause ");
                            }

                            break;
                        default:
                            floatingActionButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                            try {
                                bottomToolBar.getMenu().findItem(R.id.action_settings).setIcon(R.drawable.ic_play_arrow_white_48dp);
                            } catch (NullPointerException npe) {
                                Log.i(TAG, "NullPointerException while setting icon bottom toolbar mentu play pause ");
                            }

                            break;
                    }
                    break;

                case 1:
                    String songName = intent.getStringExtra(SharedPref_Bundle_Keys_val.KEY_SONG_NAME);
                    String artistName = intent.getStringExtra(SharedPref_Bundle_Keys_val.KEY_SONG_ARTIST);


                    TextView tvArtist = ((TextView) getView().findViewById(R.id.tvArtistName));
                    TextView tvSongName1 = ((TextView) getView().findViewById(R.id.tvSongName));
                    TextView tvStationName = ((TextView) getView().findViewById(R.id.tvStationName));
                    TextView tvCategoryName = ((TextView) getView().findViewById(R.id.tvCategoryName));
                    if (artistName != null && !artistName.equalsIgnoreCase("")) {
                        tvArtist.setText(artistName);
                        tvArtist.setVisibility(View.VISIBLE);
                        tvArtistName.setText(artistName);
                    } else {
                        tvArtistName.setText("");
                        tvArtist.setVisibility(View.GONE);
                    }

                    if (songName != null && !songName.equalsIgnoreCase("")) {
                        tvSongName1.setText(songName);
                        tvSongName1.setVisibility(View.VISIBLE);
                        tvSongName.setText(songName);
                    } else {
                        tvSongName.setText("Unknown Track");
                        tvSongName1.setVisibility(View.GONE);
                    }
                    tvStationName.setText(RadioSharedPreferences.getInstance(getActivity()).getCurrentPlayingStation()[1]);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date = new Date();
                    tvCategoryName.setText(dateFormat.format(date));

                    break;
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception in " + TAG + " " + e.getMessage().toString());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mContext.unregisterReceiver(broadcastUpdateUi);
        } catch (Exception e) {
        }
        try {
            mContext.unregisterReceiver(broadcastReceiverProgressBar);
            mContext.getContentResolver().unregisterContentObserver(mSettingsContentObserver);
        } catch (Exception e) {
        }


    }

    private Intent updateUIIntentForTitleAndPlaypause;
    private BroadcastReceiver broadcastUpdateUi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment.equalsIgnoreCase(intent.getAction())) {
                updateUIIntentForTitleAndPlaypause = intent;
                updateUIForTitle_AndPlayPause(intent);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (updateUIIntentForTitleAndPlaypause != null)
            updateUIForTitle_AndPlayPause(updateUIIntentForTitleAndPlaypause);

        if (showBufferDialogue) {
            if (!progressLayout.isRefreshing())
                progressLayout.setRefreshing(true);
        } else {
            if (pdBuff != null) {
                pdBuff.dismiss();
            }
            if (progressLayout.isRefreshing())
                progressLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (showBufferDialogue) {
        } else {
            if (pdBuff != null) {
                pdBuff.dismiss();
            }
        }

    }

    // Progress dialogue...
    private void BufferDialogue(String contentData, String message) {

        if (pdBuff == null) {
            pdBuff = ProgressDialog.show(getContext(), contentData,
                    message, true);
            pdBuff.setCancelable(true);
            pdBuff.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    pdBuff = null;
                }
            });
        } else {
            pdBuff.dismiss();
            pdBuff.show();
        }
    }

    // Handle progress dialogue for buffering...
    private void showPD(Intent bufferIntent) {
        int bufferIntValue = bufferIntent.getIntExtra(SharedPref_Bundle_Keys_val.KEY_BUFFER_SATUS, 0);
        String title = bufferIntent.getStringExtra(SharedPref_Bundle_Keys_val.KEY_BUFFER_TITLE);
        String message = bufferIntent.getStringExtra(SharedPref_Bundle_Keys_val.KEY_BUFFER_MESSAGE);

        // When the broadcasted "buffering" value is 1, show "Buffering"
        // progress dialogue.
        // When the broadcasted "buffering" value is 0, dismiss the progress
        // dialogue.

        switch (bufferIntValue) {
            case 0:
                showBufferDialogue = false;
                // Log.v(TAG, "BufferIntValue=0 RemoveBufferDialogue");
                // txtBuffer.setText("");
                progressLayout.setRefreshing(false);
                if (pdBuff != null) {
                    pdBuff.dismiss();
                }
                break;

            case 1:
                showBufferDialogue = true;
                progressLayout.setRefreshing(true);
                BufferDialogue(title, message);
                break;

            // Listen for "2" to reset the button to a play button
            case 2:
//                buttonPlayStop.setBackgroundResource(R.drawable.playbuttonsm);
                break;
        }
    }

    // Set up broadcast receiver
    private BroadcastReceiver broadcastReceiverProgressBar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };
}
