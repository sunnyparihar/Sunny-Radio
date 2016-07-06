package radio.pps.android.com.radio.Pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prabhpreet on 31-10-2015.
 */
public class TuneInBase implements Parcelable {
    private String basePls;
    private String baseM3u;
    private String baseXspf;
    private ArrayList<Genre> genreList = new ArrayList<>();
    private List<Artist_Info> artistInfoList = new ArrayList<>();

    public List<Country> getListCountry() {
        return listCountry;
    }

    public void setListCountry(List<Country> listCountry) {
        this.listCountry = listCountry;
    }

    private List<Country> listCountry = new ArrayList<>();

    public List<Language> getListLanguage() {
        return listLanguage;
    }

    public void setListLanguage(List<Language> listLanguage) {
        this.listLanguage = listLanguage;
    }

    private List<Language> listLanguage = new ArrayList<>();

    public TuneInBase() {
    }

    private TuneInBase(Parcel in) {
        basePls = in.readString();
        baseM3u = in.readString();
        baseXspf = in.readString();
        loadCategory = in.readInt();
        in.readTypedList(stationsList, Station.CREATOR);
        in.readTypedList(getGenreList(), Genre.CREATOR);
        in.readTypedList(getArtistInfoList(), Artist_Info.CREATOR);
        in.readTypedList(listCountry, Country.CREATOR);
        in.readTypedList(listLanguage, Language.CREATOR);
    }

    public int getLoadCategory() {
        return loadCategory;
    }

    public void setLoadCategory(int loadCategory) {
        this.loadCategory = loadCategory;
    }

    private int loadCategory;
    ArrayList<Station> stationsList = new ArrayList<>();

    public ArrayList<Genre> getGenreList() {
        return genreList;
    }

    public void setGenreList(ArrayList<Genre> genreList) {
        this.genreList = genreList;
    }

    public String getBasePls() {
        return basePls;
    }

    public void setBasePls(String basePls) {
        this.basePls = basePls;
    }

    public String getBaseM3u() {
        return baseM3u;
    }

    public void setBaseM3u(String baseM3u) {
        this.baseM3u = baseM3u;
    }

    public String getBaseXspf() {
        return baseXspf;
    }

    public void setBaseXspf(String baseXspf) {
        this.baseXspf = baseXspf;
    }

    public ArrayList<Station> getStationsList() {
        return stationsList;
    }

    public void setStationsList(ArrayList<Station> stationsList) {
        this.stationsList = stationsList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(basePls);
        dest.writeString(baseM3u);
        dest.writeString(baseXspf);
        dest.writeInt(loadCategory);
        dest.writeTypedList(stationsList);
        dest.writeTypedList(genreList);
        dest.writeTypedList(artistInfoList);
        dest.writeTypedList(listCountry);
        dest.writeTypedList(listLanguage);

    }

    public static final Parcelable.Creator<TuneInBase> CREATOR = new Parcelable.Creator<TuneInBase>() {
        public TuneInBase createFromParcel(Parcel in) {
            return new TuneInBase(in);
        }

        public TuneInBase[] newArray(int size) {
            return new TuneInBase[size];

        }
    };

    public List<Artist_Info> getArtistInfoList() {
        return artistInfoList;
    }

    public void setArtistInfoList(List<Artist_Info> artistInfoList) {
        this.artistInfoList = artistInfoList;
    }
}
