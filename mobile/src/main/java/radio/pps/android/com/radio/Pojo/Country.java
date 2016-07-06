package radio.pps.android.com.radio.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prabhpreet on 16-12-2015.
 */
public class Country implements Parcelable {
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String imageName;
    String countryName;
    String id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(imageName);
        dest.writeString(countryName);
        dest.writeString(id);
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        public Country[] newArray(int size) {
            return new Country[size];

        }
    };

    public Country() {

    }

    public Country(Parcel in) {
        imageName = in.readString();
        countryName = in.readString();
        id = in.readString();
    }
}
