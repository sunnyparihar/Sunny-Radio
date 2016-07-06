package radio.pps.android.com.radio.utils;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import radio.pps.android.com.radio.Pojo.Genre;
import radio.pps.android.com.radio.Pojo.Station;
import radio.pps.android.com.radio.Pojo.TuneInBase;

/**
 * Created by Prabhpreet on 31-10-2015.
 */
public class StationParser {
    private static final String TAG = "Station_Parser";
    public static final int PARSER_STATION = 0;
    public static final int PARSER_GENERE = 1;
    public static final int PARSER_ARTIST = 2;
    public static final int PARSER_COUNTRY = 3;
    public static final int PARSER_LANGUAGE = 4;
    private final int stationParser;
    XmlPullParserFactory xmlFactoryObject;
    XmlPullParser myParser;

    public StationParser(int stationParser) {
        this.stationParser = stationParser;
        xmlFactoryObject = null;
        XmlPullParser myParser = null;
    }

    public StationParser(int stationParser, Context context) {
        this.stationParser = stationParser;
        xmlFactoryObject = null;
        XmlPullParser myParser = null;
    }

    public TuneInBase parseStationStream(InputStream inputStream) {

        TuneInBase tuneinBase = new TuneInBase();
        tuneinBase.setLoadCategory(stationParser);
        ArrayList<Station> stationsList = new ArrayList<>();
        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myParser = xmlFactoryObject.newPullParser();
            myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myParser.setInput(inputStream, null);

            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("tunein")) {
                            tuneinBase.setBasePls(myParser.getAttributeValue(null, "base"));
                            tuneinBase.setBaseM3u(myParser.getAttributeValue(null, "base-m3u"));
                            tuneinBase.setBaseXspf(myParser.getAttributeValue(null, "base-xspf"));
                        } else if (name.equals("station")) {
                            Station station = new Station();
                            station.setStationName(myParser.getAttributeValue(null, "name"));
                            station.setStationId(myParser.getAttributeValue(null, "id"));
                            station.setLogoUrl(myParser.getAttributeValue(null, "logo"));
                            station.setAudioFormat(myParser.getAttributeValue(null, "mt"));
                            station.setGenre(myParser.getAttributeValue(null, "genre"));
                            station.setCt(myParser.getAttributeValue(null, "ct"));
                            station.setLc(myParser.getAttributeValue(null, "lc"));
                            station.setBitrate(myParser.getAttributeValue(null, "br"));
//                            station.setFavoriteStation(new DataProvider(RadioSharedPreferences.getInstance(context)
//                                    .getUserDbPath()).getFavorite(myParser.getAttributeValue(null, "id")));
                            stationsList.add(station);
                        }
                        break;
                }
                event = myParser.next();
            }
            if (stationsList != null) {
                tuneinBase.setStationsList(stationsList);
            }
        } catch (XmlPullParserException e) {
            Log.i(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tuneinBase;
    }

    public TuneInBase parseStationGenreStream(InputStream inputStream) {

        ArrayList<Genre> listGenre = new ArrayList<>();
        TuneInBase tuneInBase = new TuneInBase();
        tuneInBase.setLoadCategory(stationParser);
        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myParser = xmlFactoryObject.newPullParser();
            myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            myParser.setInput(inputStream, null);

            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                Genre genre = new Genre();
                String name = myParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("genre")) {
                            genre.setGenreName(myParser.getAttributeValue(null, "name"));
                            genre.setCount(myParser.getAttributeValue(null, "count"));
                            if (Integer.parseInt(genre.getCount()) != 0) {
                                listGenre.add(genre);
                            }

                        }
                        break;
                }
                event = myParser.next();
            }
            if (listGenre != null) {
                tuneInBase.setGenreList(listGenre);
            }
        } catch (XmlPullParserException e) {
            Log.i(TAG, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tuneInBase;
    }
}
