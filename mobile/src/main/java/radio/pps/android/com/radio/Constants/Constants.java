package radio.pps.android.com.radio.Constants;

import android.content.Intent;

/**
 * Created by Prabhpreet on 31-10-2015.
 */
public class Constants {
    public static final String APIKEY = "HhcLRSiBeU2RmJaW";
    //public static final String APIKEY="xGjodyO5UpzPTOVt";

    public static final String SHOUTCAST_TOP500_URL = "http://api.shoutcast.com/legacy/Top500?k=";

    //public static final String SHOUTCAST_STATION_SEARCH_URL = "http://api.shoutcast.com/legacy/stationsearch?k=" + APIKEY + "&search=";


    //public static final String SHOUTCAST_NOWPLAYING_URL = "http://api.shoutcast.com/station/nowplaying?k=" + APIKEY + "&ct=";

    //public static final String SHOUTCAST_STATIONBY_GENREID_URL = "http://api.shoutcast.com/station/advancedsearch?genre_id=1&limit=10&f=xml&k=" + APIKEY;

    public static final String SHOUTCAST_GENRELIST_URL = "http://api.shoutcast.com/legacy/genrelist?k=" + APIKEY;
    //public static final String SHOUTCAST_GET_STATION_BY_GENRE = "http://api.shoutcast.com/legacy/genresearch?k=" + APIKEY + "&genre=";

    //private static final String SHOUTCAST_ARTIST_URL = "http://api.shoutcast.com/station/nowplaying?k=" + APIKEY + "&ct=pussy&f=xml";

    public static final String TUNE_A_STATION = "http://yp.shoutcast.com";
    public static final String APPUSERDB = "Userdb.db";
    public static String APPDB_PATH = "";
    public static final String APPDB_NAME = "Radio.db";
    public static Intent intentServiceStream;

    // --Set up constant ID for broadcast of seekbar position--
    public static final String BROADCAST_SEEKBAR = "com.glowingpigs.tutorialstreamaudiopart1b.sendseekbar";

    //These are usejava.lang.Stringd for play pause status to save the Playing Stream sattaus;
    public static final int STATUSPAUSE = 2;
    public static final int STATUSPLAY = 1;
    public static final int STATUSSTOP = 0;

    //Broadcast Receiver
    public static final String BROADCASAT_UDPATE_UI_PlayerScreenFragment = "radio.pps.broadcast.update.song.title.playPause";


    public static final String BROADCAST_UPDATE_TITLE = "com.android.pps.broadcastupdatetitle";

    // Set up broadcast identifier and intent
    public static final String BROADCAST_BUFFER = "com.android.pps.broadcastbuffer";
    // Set up broadcast identifier and intent
    public static final String BROADCAST_STOP_SERVICE = "com.android.pps.broadcaststopservice";
    // Set up broadcast identifier and intent
    public static final String BROADCAST_PLAY_PAUSE_STOP = "com.android.pps.play_pause.broadcast";

    //Url for Artist images
    public static final String ARTIST_URL_IMAGE = "https://storage.googleapis.com/artist_images_fm_radio/Artist%20-%20Images/";
    // Base 64 key
    public static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjXUBan2IVvQcl3MenP6Qvr4XeBIR0moFAiHRPW99+fdAD3wI7kvpC1QjreoNYlTd8McNM8mu0bArv4gGRmfwSpHITSMg9/3/gHz39/ib3+MMCw+D0RyAp/Qm6agmcFoXWdC+BQPpahhVreovu7fcc8AupLLwlAH10ih/11Dl+35qE9MsnsRB/RBaiDX5U8PswTwvLRQySkm+KPJTRLss86Y7fVdm1lcnqz+MQ+xau+1FDiGcHVgdfgAoxQd5w0JpCROQIygUcUsRAHo5m3rvYbgG3Nri6nD3oxEMMSNPGDopJ8zsrRPbuWq0lRcAMpX3YQpiOkdJeR9o2Kc0Xda8OQIDAQAB";

    public static int MEDIA_PLAYER_AUDIO_SESSION = 26121992;
}
