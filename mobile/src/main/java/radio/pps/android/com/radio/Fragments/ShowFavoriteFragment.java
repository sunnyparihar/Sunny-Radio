package radio.pps.android.com.radio.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.lucasr.twowayview.widget.TwoWayView;

import radio.pps.android.com.radio.Adapter.ShowFavoritesLayoutAdapter;
import radio.pps.android.com.radio.Constants.Constants;
import radio.pps.android.com.radio.Constants.SharedPref_Bundle_Keys_val;
import radio.pps.android.com.radio.Databases.DataProvider;
import radio.pps.android.com.radio.Pojo.TuneInBase;
import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.ToolbarInitializer;
import radio.pps.android.com.radio.data.RadioFavouriteProvider;
import radio.pps.android.com.radio.data.TableDataInterface;
import radio.pps.android.com.radio.interfaces.OnItemClickListener;
import radio.pps.android.com.radio.services.PlayStreamService;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

/**
 * Created by Prabhpreet on 09-12-2015.
 */
public class ShowFavoriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private TwoWayView mRecyclerView;
    private ProgressBar progressBar;
    private TuneInBase stationList;
    private ShowFavoritesLayoutAdapter adapter;
    private Intent serviceIntent;
    private Context mContext;
    private Cursor saveData;
    private static final int LOADER_NOTES = 20;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        serviceIntent = new Intent(getContext(), PlayStreamService.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.showfavoritefragmentlayout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getContext();

        // Initialize recycler view
        mRecyclerView = (TwoWayView) getView().findViewById(R.id.rcViewShowFavoriteFragment);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBarShowFavorite);
        progressBar.setVisibility(View.VISIBLE);

        ToolbarInitializer toolbarInitializer = new ToolbarInitializer(mContext
                , (Toolbar) getView().findViewById(R.id.toolbar)
                , getResources().getString(R.string.app_name_category)
                , "Favorite");
        toolbarInitializer.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        getLoaderManager().initLoader(LOADER_NOTES, null, this);


/*        adapter = new ShowFavoritesLayoutAdapter(mContext
                , stationList
                , getResources().getColor(R.color.colorItemHomeScreenBgTop500)
                , getResources().getColor(R.color.colorItemHomeScreenCategoryTop500));*/

/*        mRecyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClickListener(View v, TuneInBase tuneInBase, int position) {

                Intent intents = new Intent(Constants.BROADCAST_STOP_SERVICE);
                mContext.sendBroadcast(intents);
                try {
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationId, stationList.getStationsList().get(position).getStationId());
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationName, stationList.getStationsList().get(position).getStationName());
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_STATION_TUNE_URL, Constants.TUNE_A_STATION + "/sbin/tunein-station.pls" +
                            "?id=" + stationList.getStationsList().get(position).getStationId());
                    mContext.startService(serviceIntent);
                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(mContext.getApplicationContext(),
                            e.getClass().getName() + " " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                RadioFavouriteProvider.Lists.LISTS,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        this.saveData = data;

        progressBar.setVisibility(View.GONE);

        if (saveData != null && saveData.getCount() != 0) {

        } else {
            Toast.makeText(mContext, R.string.noStationFound, Toast.LENGTH_LONG).show();
            return;
        }

        adapter = new ShowFavoritesLayoutAdapter(mContext, saveData);

        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClickListener(View v, TuneInBase tuneInBase, int position) {

            }

            @Override
            public void onFavItemClickListener(View v, Cursor cursor, int position) {
                Intent intents = new Intent(Constants.BROADCAST_STOP_SERVICE);
                mContext.sendBroadcast(intents);
                try {
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationId, cursor.getString(cursor.getColumnIndex(TableDataInterface.STATION_ID)));
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.keyStationName, cursor.getString(cursor.getColumnIndex(TableDataInterface.STATION_NAME)));
                    serviceIntent.putExtra(SharedPref_Bundle_Keys_val.KEY_STATION_TUNE_URL, Constants.TUNE_A_STATION + "/sbin/tunein-station.pls" +
                            "?id=" + cursor.getString(cursor.getColumnIndex(TableDataInterface.STATION_ID)));
                    mContext.startService(serviceIntent);
                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(mContext.getApplicationContext(),
                            e.getClass().getName() + " " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
