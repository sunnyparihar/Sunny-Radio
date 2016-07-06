package radio.pps.android.com.radio.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by sunnyparihar on 25/12/15.
 */

@Database(version = RadioFavouriteDatabase.VERSION)
public class RadioFavouriteDatabase {

    public static final int VERSION = 1;

    @Table(TableDataInterface.class)
    public static final String LISTS = "lists";
}
