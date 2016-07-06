package radio.pps.android.com.radio.interfaces;

import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;

import radio.pps.android.com.radio.Pojo.TuneInBase;

/**
 * Created by Prabhpreet on 06-12-2015.
 */
public interface OnItemClickListener
{
    void onItemClickListener(View v, TuneInBase tuneInBase, int position);

    void onFavItemClickListener(View v, Cursor cursor, int position);
}
