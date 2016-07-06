package radio.pps.android.com.radio.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by sunnyparihar on 25/12/15.
 */
public interface TableDataInterface {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT)
    @NotNull
    String STATION_ID = "station_id";

    @DataType(TEXT)
    @NotNull
    String STATION_NAME = "station_name";

    @DataType(TEXT)
    @NotNull
    String BITRATE = "bitrate";

    @DataType(TEXT)
    String GENRE = "genre";

    @DataType(INTEGER)
    String FAVORITE = "favorite";

    @DataType(INTEGER)
    String NOW_PLAYING = "now_playing";

}
