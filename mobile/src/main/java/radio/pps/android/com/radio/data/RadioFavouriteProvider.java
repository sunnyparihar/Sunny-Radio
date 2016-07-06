package radio.pps.android.com.radio.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by sunnyparihar on 25/12/15.
 */


@ContentProvider(authority = RadioFavouriteProvider.AUTHORITY, database = RadioFavouriteDatabase.class)
public class RadioFavouriteProvider {

    public static final String AUTHORITY = "radio.pps.android.com.radio.data.RadioFavouriteProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    @TableEndpoint(table = RadioFavouriteDatabase.LISTS)
    public static class Lists {

        @ContentUri(
                path = RadioFavouriteDatabase.LISTS,
                type = "vnd.android.cursor.dir/list",
                defaultSort = TableDataInterface.STATION_ID + " ASC")
        public static final Uri LISTS = Uri.parse("content://" + AUTHORITY + "/lists");


        @InexactContentUri(
                path = RadioFavouriteDatabase.LISTS + "/#",
                name = "STATION_ID",
                type = "vnd.android.cursor.item/list",
                whereColumn = TableDataInterface.STATION_ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RadioFavouriteDatabase.LISTS, String.valueOf(id));
        }

    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }
}
