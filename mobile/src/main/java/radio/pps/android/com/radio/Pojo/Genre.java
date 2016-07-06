package radio.pps.android.com.radio.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prabhpreet on 14-11-2015.
 */
public class Genre implements Parcelable {
    String genreName;

    public Genre() {

    }

    public Genre(Parcel in) {
        setGenreName(in.readString());
        setCount(in.readString());
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    String count;

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(getGenreName());
        dest.writeString(getCount());
    }

    public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        public Genre[] newArray(int size) {
            return new Genre[size];

        }
    };
}
