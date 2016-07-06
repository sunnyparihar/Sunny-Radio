package radio.pps.android.com.radio.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prabhpreet on 27-11-2015.
 */
public class Artist_Info implements Parcelable {
    private int id;
    private String artistName;
    private String artistType;
    private String wikiUrl;
    private String artistWebsite;
    private String imageName;
    private String imageSource;

    public Artist_Info() {

    }

    public Artist_Info(Parcel in) {
        id = in.readInt();
        artistName = in.readString();
        artistType = in.readString();
        wikiUrl = in.readString();
        artistWebsite = in.readString();
        imageName = in.readString();
        imageSource = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtistType() {
        return artistType;
    }

    public void setArtistType(String artistType) {
        this.artistType = artistType;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    public String getArtistWebsite() {
        return artistWebsite;
    }

    public void setArtistWebsite(String artistWebsite) {
        this.artistWebsite = artistWebsite;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(artistName);
        dest.writeString(artistType);
        dest.writeString(wikiUrl);
        dest.writeString(artistWebsite);
        dest.writeString(imageName);
        dest.writeString(imageSource);


    }

    public static final Parcelable.Creator<Artist_Info> CREATOR = new Parcelable.Creator<Artist_Info>() {
        public Artist_Info createFromParcel(Parcel in) {
            return new Artist_Info(in);
        }

        public Artist_Info[] newArray(int size) {
            return new Artist_Info[size];

        }
    };
}
