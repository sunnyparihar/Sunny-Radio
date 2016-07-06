package radio.pps.android.com.radio.AsynTask;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import radio.pps.android.com.radio.Databases.DataProvider;
import radio.pps.android.com.radio.data.RadioFavouriteProvider;
import radio.pps.android.com.radio.data.TableDataInterface;
import radio.pps.android.com.radio.interfaces.IAsyncCallBack;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

/**
 * Created by Prabhpreet on 10-12-2015.
 */
public class UpdateFavoriteUI extends AsyncTask<Void, Void, Integer> {
    private int mPosition;
    private Context context;
    private DataProvider dataProvider;
    private String userDbPath;
    private String stationId;
    private boolean favorite;
    private MaterialFavoriteButton btnFavorite;
    private IAsyncCallBack callBackListener;
    private boolean status = false;


    public UpdateFavoriteUI(Context context, int position, String id, MaterialFavoriteButton btnFavorite) {
        this.context = context;
        mPosition = position;
        this.stationId = id;
        this.btnFavorite = btnFavorite;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int pos = mPosition;
        try {
            userDbPath = RadioSharedPreferences.getInstance(context).getUserDbPath();
            //dataProvider = new DataProvider(userDbPath);

            Cursor cursor = context.getContentResolver().query(RadioFavouriteProvider.Lists.withId(Long.parseLong(stationId)), null, null, null, null);

            int fav = 0;

            if (cursor != null && cursor.moveToFirst()) {
                fav = cursor.getInt(cursor.getColumnIndex(TableDataInterface.FAVORITE));
            }

            if (fav == 0) {
                favorite = false;
            } else {
                favorite = true;
            }

            //favorite = dataProvider.getFavorite(stationId);

            status = true;
        } catch (Exception e) {
            status = false;
            Log.i("UpdateFavoriteUI ", "Inside Asynctask favorite fetcher");
        }

        return pos;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (status)
            callBackListener.callBack(integer.intValue(), favorite, btnFavorite);
    }

    public void setCallBackListener(IAsyncCallBack callBackListener) {
        this.callBackListener = callBackListener;
    }
}