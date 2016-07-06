package radio.pps.android.com.radio.Adapter;

/**
 * Created by Prabhpreet on 28-11-2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import radio.pps.android.com.radio.Databases.DataProvider;
import radio.pps.android.com.radio.Pojo.Station;
import radio.pps.android.com.radio.Pojo.TuneInBase;
import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.data.RadioFavouriteProvider;
import radio.pps.android.com.radio.data.TableDataInterface;
import radio.pps.android.com.radio.interfaces.OnItemClickListener;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

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

public class ShowFavoritesLayoutAdapter extends RecyclerView.Adapter<ShowFavoritesLayoutAdapter.SimpleViewHolder> {
    private final Context mContext;
    //private TuneInBase tuneInBase;
    private int colorBG, colorCat;
    private Cursor mCursor;

    CursorAdapter mCursorAdapter;

    private OnItemClickListener onClickListener;

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        if (onClickListener != null) {
            this.onClickListener = onClickListener;
        } else {
            throw new NullPointerException("OnItemClickListener should not to be null Layout Adapter");
        }
    }

    public ShowFavoritesLayoutAdapter(Context context, Cursor c) {

        mContext = context;

        mCursorAdapter = new CursorAdapter(mContext, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                final View view = LayoutInflater.from(mContext).inflate(R.layout.homescreentwowaylayout_item, parent, false);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                // Binding operations
            }
        };
    }


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        private final TextView tvStationName;
        private final CardView cdViewItemBG;
        private final MaterialFavoriteButton btnFavorite;

        public SimpleViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvCategory);
            tvStationName = (TextView) view.findViewById(R.id.tvStationName);
            cdViewItemBG = (CardView) view.findViewById(R.id.cdviewHomeScreenListItem);
            btnFavorite = (MaterialFavoriteButton) view.findViewById(R.id.btnFavorite);
            btnFavorite.setColor(MaterialFavoriteButton.STYLE_WHITE);
            btnFavorite.setType(MaterialFavoriteButton.STYLE_HEART);
            btnFavorite.setRotationDuration(400);
            btnFavorite.setFavorite(true, false);
        }
    }

/*    public ShowFavoritesLayoutAdapter(Context context, TuneInBase tuneInBase, int colorBG, int colorCategory) {
        mContext = context;
        this.tuneInBase = tuneInBase;
        this.colorBG = colorBG;
        colorCat = colorCategory;
    }*/

/*    public ShowFavoritesLayoutAdapter(Context context, Cursor cursor, int colorBG, int colorCategory) {
        mContext = context;
        this.mCursor = cursor;
        this.colorBG = colorBG;
        colorCat = colorCategory;
    }*/

    public void addItem(int position) {
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        //final Station stationData = tuneInBase.getStationsList().get(position);

        // Passing the binding operation to cursor loader
        mCursorAdapter.getCursor().moveToPosition(position); //EDITED: added this line as suggested in the comments below, thanks :)
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());

        final String stationId = mCursorAdapter.getCursor().getString(mCursorAdapter.getCursor().getColumnIndex(TableDataInterface.STATION_ID));
        final String stationName = mCursorAdapter.getCursor().getString(mCursorAdapter.getCursor().getColumnIndex(TableDataInterface.STATION_NAME));
        final String stationBitrate = mCursorAdapter.getCursor().getString(mCursorAdapter.getCursor().getColumnIndex(TableDataInterface.BITRATE));
        final String stationGenre = mCursorAdapter.getCursor().getString(mCursorAdapter.getCursor().getColumnIndex(TableDataInterface.GENRE));
        final int stationFav = mCursorAdapter.getCursor().getInt(mCursorAdapter.getCursor().getColumnIndex(TableDataInterface.FAVORITE));

        holder.title.setBackgroundColor(mContext.getResources().getColor(R.color.colorItemHomeScreenBgTop500));
        holder.cdViewItemBG.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorItemHomeScreenCategoryTop500));
        holder.title.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        holder.tvStationName.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));

        holder.tvStationName.setText(stationName);
        holder.title.setText(stationGenre);

        holder.btnFavorite.setVisibility(View.VISIBLE);

        holder.btnFavorite.setFavorite((stationFav == 1) ? true : false, false);

        holder.cdViewItemBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener(v, mCursorAdapter.getCursor(), position);
            }
        });

        holder.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (stationFav == 1) {

/*                    new DataProvider(RadioSharedPreferences.getInstance(mContext).getUserDbPath())
                            .removeFavorites(stationData.getStationId());

                    tuneInBase.getStationsList().get(position).setFavoriteStation(false);

                    stationData.setFavoriteStation(false);*/

                    mContext.getContentResolver().delete(RadioFavouriteProvider.Lists.withId(Long.parseLong(stationId)), null, null);

                    holder.btnFavorite.setFavorite(false, true);


                } else {
/*                    new DataProvider(RadioSharedPreferences.getInstance(mContext).getUserDbPath())
                            .setFavorite(stationData.getStationId()
                                    , stationData.getStationName().toString()
                                    , stationData.getBitrate(),
                                    stationData.getGenre());
                    stationData.setFavoriteStation(true);
                    tuneInBase.getStationsList().get(position).setFavoriteStation(true);*/


                    ContentValues cv = new ContentValues();
                    cv.put("station_id", stationId);
                    cv.put("station_name", stationName);
                    cv.put("bitrate", stationBitrate);
                    cv.put("genre", stationGenre);
                    cv.put("favorite", 1);

                    mContext.getContentResolver().insert(RadioFavouriteProvider.Lists.LISTS, cv);

                    holder.btnFavorite.setFavorite(true, true);

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    private void setOnItemClickListener(View view, Cursor cursor, int position) {
        onClickListener.onFavItemClickListener(view, cursor, position);
    }

}
