package radio.pps.android.com.radio.Adapter;

/**
 * Created by Prabhpreet on 30-10-2015.
 */
/*
 * Copyright (C) 2014 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;

import radio.pps.android.com.radio.Constants.StaticMethods;
import radio.pps.android.com.radio.Pojo.Station;
import radio.pps.android.com.radio.Pojo.TuneInBase;
import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.data.RadioFavouriteProvider;
import radio.pps.android.com.radio.data.TableDataInterface;
import radio.pps.android.com.radio.interfaces.OnItemClickListener;
import radio.pps.android.com.radio.interfaces.UpdateUI;
import radio.pps.android.com.radio.utils.StationParser;

public class HomeScreenLayoutAdapter extends RecyclerView.Adapter<HomeScreenLayoutAdapter.SimpleViewHolder> implements Filterable {
    private static final String TAG = "HomeScreenLayoutAdapter";
    private final Context mContext;
    private final TuneInBase copyItem;
    private TuneInBase mItems;
    private int colorBG, colorCat;
    private int categoryCode = 0;
    private OnItemClickListener onClickListener;
    private StationFilter stationFilter;
    private UpdateUI onViewUpdateListener;
    private boolean favorite = false;

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        if (onClickListener != null) {
            this.onClickListener = onClickListener;
        } else {
            throw new NullPointerException("OnItemClickListener should not to be null Layout Adapter");
        }
    }


    @Override
    public Filter getFilter() {
        if (stationFilter == null)
            stationFilter = new StationFilter();
        return stationFilter;
    }

    public void setOnViewUpdateListener(UpdateUI onViewUpdateListener) {
        this.onViewUpdateListener = onViewUpdateListener;
    }

    //Compares list and returns new list
    private class StationFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            switch (categoryCode) {
                case StationParser.PARSER_STATION:
                    if (constraint == null || constraint.length() == 0) {
                        results.values = mItems.getStationsList();
                        results.count = mItems.getStationsList().size();

                    } else {
                        ArrayList<Station> n = new ArrayList<>();
                        for (Station c : mItems.getStationsList()) {
                            if (c.getStationName().toUpperCase().startsWith(constraint.toString().toUpperCase()) ||
                                    c.getGenre().toUpperCase().startsWith(constraint.toString().toUpperCase()) ||
                                    c.getBitrate().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                                n.add(c);
                        }
                        results.values = n;
                        results.count = n.size();
                    }
                    break;

            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults results) {

            switch (categoryCode) {
                case StationParser.PARSER_STATION:
                    copyItem.setStationsList((ArrayList<Station>) results.values);
                    break;

            }
            try {
                onViewUpdateListener.onUpdateUI(categoryCode, copyItem);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        private final TextView tvStationName;
        private final CardView cdViewItemBG;
        private final MaterialFavoriteButton btnFavorite;

        public SimpleViewHolder(View view, int colorCat, int colorBg, Context mContext) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvCategory);
            tvStationName = (TextView) view.findViewById(R.id.tvStationName);
            cdViewItemBG = (CardView) view.findViewById(R.id.cdviewHomeScreenListItem);
            btnFavorite = (MaterialFavoriteButton) view.findViewById(R.id.btnFavorite);
            title.setBackgroundColor(colorCat);
            cdViewItemBG.setCardBackgroundColor(colorBg);
            title.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            tvStationName.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
            btnFavorite.setColor(MaterialFavoriteButton.STYLE_WHITE);
            btnFavorite.setType(MaterialFavoriteButton.STYLE_HEART);
            btnFavorite.setRotationDuration(400);
            btnFavorite.setFavorite(true, false);
            btnFavorite.setFavorite(false, false);
        }
    }

    public HomeScreenLayoutAdapter(Context context, int categoryCode, TwoWayView recyclerView, TuneInBase tuneInBase, int colorBG, int colorCategory) {
        mContext = context;
        mItems = tuneInBase;
        copyItem = new TuneInBase();
        copyItem.setBaseXspf(mItems.getBaseXspf());
        copyItem.setBaseM3u(mItems.getBaseM3u());
        copyItem.setBasePls(mItems.getBasePls());
        copyItem.setLoadCategory(mItems.getLoadCategory());
        copyItem.setArtistInfoList(mItems.getArtistInfoList());
        copyItem.setGenreList(mItems.getGenreList());
        copyItem.setStationsList(mItems.getStationsList());
        copyItem.setListCountry(mItems.getListCountry());
        copyItem.setListLanguage(mItems.getListLanguage());
        this.colorBG = colorBG;
        this.categoryCode = categoryCode;
        colorCat = colorCategory;
    }

    public void addItem(int position) {
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.homescreentwowaylayout_item, parent, false);
        return new SimpleViewHolder(view, colorCat, colorBG, mContext);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        holder.cdViewItemBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener(v, copyItem, position);
            }
        });
        switch (categoryCode) {
            case StationParser.PARSER_STATION:
                final Station stationData = copyItem.getStationsList().get(position);

                holder.btnFavorite.setVisibility(View.VISIBLE);
                holder.tvStationName.setText(stationData.getStationName());
                holder.title.setText(StaticMethods.capitalizeFirstLetter(stationData.getGenre()));

                Cursor cursor = mContext.getContentResolver().query(RadioFavouriteProvider.Lists.withId(Long.parseLong(stationData.getStationId())), null, null, null, null);

                int fav = 0;

                if (cursor != null && cursor.moveToFirst()) {
                    fav = cursor.getInt(cursor.getColumnIndex(TableDataInterface.FAVORITE));
                }

                if (fav == 0) {
                    favorite = false;
                } else {
                    favorite = true;
                }


                holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (favorite) {

                            mContext.getContentResolver().delete(RadioFavouriteProvider.Lists.withId(Long.parseLong(stationData.getStationId())), null, null);

                            favorite = false;

                        } else {
                            ContentValues cv = new ContentValues();
                            cv.put("station_id", stationData.getStationId());
                            cv.put("station_name", stationData.getStationName());
                            cv.put("bitrate", stationData.getBitrate());
                            cv.put("genre", stationData.getGenre());
                            cv.put("favorite", 1);

                            mContext.getContentResolver().insert(RadioFavouriteProvider.Lists.LISTS, cv);

                            favorite = true;
                        }

/*                        if (stationData.getFavoriteStation()) {
                            new DataProvider(RadioSharedPreferences.getInstance(mContext).getUserDbPath())
                                    .removeFavorites(copyItem.getStationsList().get(position).getStationId());
                            copyItem.getStationsList().get(position).setFavoriteStation(false);
                            stationData.setFavoriteStation(false);
                        } else {
                            new DataProvider(RadioSharedPreferences.getInstance(mContext).getUserDbPath())
                                    .setFavorite(stationData.getStationId()
                                            , stationData.getStationName().toString()
                                            , stationData.getBitrate(),
                                            stationData.getGenre());
                            stationData.setFavoriteStation(true);
                            copyItem.getStationsList().get(position).setFavoriteStation(true);
                        }
                        holder.btnFavorite.setFavorite(stationData.getFavoriteStation(), true);*/

                        holder.btnFavorite.setFavorite(favorite, true);
                    }
                });

/*                UpdateFavoriteUI updateFavoriteUI = new UpdateFavoriteUI(mContext, position,
                        stationData.getStationId(), holder.btnFavorite);
                updateFavoriteUI.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                updateFavoriteUI.setCallBackListener(new IAsyncCallBack() {
                    @Override
                    public void callBack(int pos, boolean favValue, MaterialFavoriteButton btnFavorite) {
                        try {
                            copyItem.getStationsList().get(pos).setFavoriteStation(favValue);
                            btnFavorite.setFavorite(favValue, false);
                        } catch (IndexOutOfBoundsException indexOutOfBound) {
                            Log.i(TAG, "Index Out of Bound In updateFavoriteUI");
                        }
                    }
                });*/

                break;

        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        switch (categoryCode) {
            case StationParser.PARSER_STATION:
                size = copyItem.getStationsList().size();
                break;
        }
        return size;
    }

    private void setOnItemClickListener(View view, TuneInBase tuneInBase, int position) {
        onClickListener.onItemClickListener(view, tuneInBase, position);
    }
}
