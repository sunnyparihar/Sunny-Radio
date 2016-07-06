package radio.pps.android.com.radio.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.data.RadioFavouriteProvider;
import radio.pps.android.com.radio.data.TableDataInterface;

public class RadioRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                // This is the same query from MyStocksActivity
                data = getContentResolver().query(
                        RadioFavouriteProvider.Lists.LISTS,
                        new String[]{
                                TableDataInterface._ID,
                                TableDataInterface.STATION_NAME,
                                TableDataInterface.GENRE,
                                TableDataInterface.BITRATE,

                        },
                        TableDataInterface.NOW_PLAYING + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
                Log.d("data", data.toString());
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                Log.d("count", String.valueOf(data.getCount()));
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                // Get the layout
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.homescreentwowaylayout_item);

                // Bind data to the views
                views.setTextViewText(R.id.tvCategory, data.getString(data.getColumnIndex(TableDataInterface.GENRE)));


                views.setTextViewText(R.id.tvStationName, data.getString(data.getColumnIndex(TableDataInterface.STATION_NAME)));

/*                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(getResources().getString(R.string.string_symbol), data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);*/

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null; // use the default loading view
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                // Get the row ID for the view at the specified position
                if (data != null && data.moveToPosition(position)) {
                    final int QUOTES_ID_COL = 0;
                    return data.getLong(QUOTES_ID_COL);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}