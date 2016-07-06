package radio.pps.android.com.radio.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prabhpreet on 01-11-2015.
 */
public class Station implements Parcelable {
    private String stationName;
    private String stationId;
    private String audioFormat;
    private String genre;
    private String ct;
    private String logoUrl;
    private String lc;
    private String bitrate;
    private boolean favoriteStation;

    public String getDateTimeWhenLastAccesed() {
        return dateTimeWhenLastAccesed;
    }

    public void setDateTimeWhenLastAccesed(String dateTimeWhenLastAccesed) {
        this.dateTimeWhenLastAccesed = dateTimeWhenLastAccesed;
    }

    private String dateTimeWhenLastAccesed;


    public Station() {

    }

    private Station(Parcel in) {
        stationName = in.readString();
        stationId = in.readString();
        audioFormat = in.readString();
        genre = in.readString();
        ct = in.readString();
        logoUrl = in.readString();
        lc = in.readString();
        bitrate = in.readString();
        favoriteStation = in.readByte() != 0;
        dateTimeWhenLastAccesed = in.readString();
    }


    public String getBitrate() {
        return bitrate;
    }

    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(String audioFormat) {
        this.audioFormat = audioFormat;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setLc(String lc) {
        this.lc = lc;
    }

    public String getLc() {
        return lc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(stationName);
        dest.writeString(stationId);
        dest.writeString(audioFormat);
        dest.writeString(genre);
        dest.writeString(ct);
        dest.writeString(logoUrl);
        dest.writeString(lc);
        dest.writeString(bitrate);
        dest.writeByte((byte) (favoriteStation ? 1 : 0));
        dest.writeString(dateTimeWhenLastAccesed);
    }

    public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() {
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        public Station[] newArray(int size) {
            return new Station[size];

        }
    };

    public boolean getFavoriteStation() {

        return favoriteStation;
    }

    public void setFavoriteStation(boolean favoriteStation) {
        this.favoriteStation = favoriteStation;
    }
}
