package radio.pps.android.com.radio.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import org.lucasr.twowayview.widget.TwoWayView;

import radio.pps.android.com.radio.Adapter.HomeScreenLayoutAdapter;
import radio.pps.android.com.radio.Constants.Constants;
import radio.pps.android.com.radio.Constants.SharedPref_Bundle_Keys_val;
import radio.pps.android.com.radio.Constants.StaticMethods;
import radio.pps.android.com.radio.Databases.DataProvider;
import radio.pps.android.com.radio.Pojo.TuneInBase;
import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.ToolbarInitializer;
import radio.pps.android.com.radio.interfaces.OnItemClickListener;
import radio.pps.android.com.radio.interfaces.UpdateUI;
import radio.pps.android.com.radio.services.PlayStreamService;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;
import radio.pps.android.com.radio.utils.StationParser;

/**
 * Created by Sunny on 24-10-2015.
 */
public class HomeScreenFragment extends CommonFragment {
    private TwoWayView lvTOP500;
    private TuneInBase listTop500;
    private Intent serviceIntent;
    private String TAG = "HomeScreenActivity";
    private Context mContext;
    private DataProvider dataProvider;
    private AdView adView;

    public HomeScreenFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        dataProvider = new DataProvider(getContext());
        dataProvider.closeSQLiteDatabase();
        this.mContext = getActivity();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_screen_fragment, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (mContext == null)
            mContext = getContext();
        serviceIntent = new Intent(mContext, PlayStreamService.class);

        listTop500 = getArguments().getParcelable("listTop500");
        intiUI(mContext);
        ToolbarInitializer toolbarInitializer = new ToolbarInitializer(mContext, (Toolbar) getView().findViewById(R.id.toolbar), getResources().getString(R.string.app_name_category));
        toolbarInitializer.setMenuToolbar(R.menu.menu_home_screenfragment, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_favorite:
                        StaticMethods.loadFragment(HomeScreenFragment.this
                                , new ShowFavoriteFragment()
                                , R.id.homeScreenContainer
                                , true, "ShowFavoriteFragment");

                        break;
                    case R.id.action_settings:
                        StaticMethods.loadFragment(HomeScreenFragment.this
                                , new SettingsFragment()
                                , R.id.homeScreenContainer
                                , true, "SettingsFragment");

                        break;

                }
                return false;
            }
        });

        super.onActivityCreated(savedInstanceState);
    }


    private void intiUI(final Context context) {

        lvTOP500 = (TwoWayView) getView().findViewById(R.id.listTop500);

        setFixedSize(lvTOP500);

        setAdapter(lvTOP500, StationParser.PARSER_STATION, listTop500, getResources().getColor(R.color.colorItemHomeScreenBgTop500), getResources().getColor(R.color.colorItemHomeScreenBgCountry));
    }

    private void setFixedSize(TwoWayView twoWayView) {
        twoWayView.setHasFixedSize(true);
        twoWayView.setLongClickable(true);
    }

    private HomeScreenLayoutAdapter setAdapter(TwoWayView twoWayView, int categoryCode, TuneInBase tuneInBase, int colorBg, int colorCategory) {
        HomeScreenLayoutAdapter layoutAdapter = new HomeScreenLayoutAdapter(mContext, categoryCode, twoWayView, tuneInBase, colorBg, colorCategory);
        twoWayView.setAdapter(layoutAdapter);
        layoutAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClickListener(View v, TuneInBase tuneInBase, int position) {
                switch (tuneInBase.getLoadCategory()) {
                    case StationParser.PARSER_STATION:
                        new DataProvider(RadioSharedPreferences.getInstance(mContext).getUserDbPath()).saveRecentStation(tuneInBase.getStationsList().get(position));
                        Intent intentUpdateUi = new Intent(Constants.BROADCASAT_UDPATE_UI_PlayerScreenFragment);
                        intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_UPDATE_TITLE, 1);
                        intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_NAME, "");
                        intentUpdateUi.putExtra(SharedPref_Bundle_Keys_val.KEY_SONG_ARTIST, "");
                        mContext.sendBroadcast(intentUpdateUi);
                        Intent intents = new Intent(Constants.BROADCAST_STOP_SERVICE);
                        mContext.sendBroadcast(intents);
                        try {
                            serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationId, tuneInBase.getStationsList().get(position).getStationId());
                            serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationName, tuneInBase.getStationsList().get(position).getStationName());
                            serviceIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_STATION_TUNE_URL, Constants.TUNE_A_STATION + tuneInBase.getBasePls() +
                                    "?id=" + tuneInBase.getStationsList().get(position).getStationId());
                            mContext.startService(serviceIntent);
                        } catch (Exception e) {

                            e.printStackTrace();
                            Toast.makeText(
                                    mContext.getApplicationContext(),
                                    e.getClass().getName() + " " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        break;

                }
            }

            @Override
            public void onFavItemClickListener(View v, Cursor cursor, int position) {

            }
        });

        layoutAdapter.setOnViewUpdateListener(new UpdateUI() {
            @Override
            public void onUpdateUI(int catCode, TuneInBase tuneInBase) {
                super.onUpdateUI(catCode, tuneInBase);
                switch (catCode) {
                    case StationParser.PARSER_STATION:
                        if (tuneInBase.getStationsList() == null || tuneInBase.getStationsList().isEmpty())
                            getView().findViewById(R.id.rLayoutTop500).setVisibility(View.GONE);
                        else
                            getView().findViewById(R.id.rLayoutTop500).setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
        return layoutAdapter;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mContext == null)
            mContext = getContext();
        if (!RadioSharedPreferences.getInstance(getContext()).getBackGroundPlay())
            stopMyPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mContext == null)
            mContext = getContext();
    }


    // --- Stop service (and music) ---
    private void stopMyPlayService() {

        try {
            mContext.stopService(serviceIntent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext.getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
