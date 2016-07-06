package radio.pps.android.com.radio.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prabhpreet on 16-12-2015.
 */
public class Language implements Parcelable {
    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String languageName;
    String id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(languageName);
        dest.writeString(id);
    }

    public static final Parcelable.Creator<Language> CREATOR = new Parcelable.Creator<Language>() {
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        public Language[] newArray(int size) {
            return new Language[size];

        }
    };

    public Language() {
    }

    public Language(Parcel in) {
        languageName = in.readString();
        id = in.readString();
    }
}
