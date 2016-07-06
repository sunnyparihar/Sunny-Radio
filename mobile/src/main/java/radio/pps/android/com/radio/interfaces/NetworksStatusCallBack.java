package radio.pps.android.com.radio.interfaces;

import java.util.List;

import radio.pps.android.com.radio.Pojo.Artist_Info;
import radio.pps.android.com.radio.Pojo.TuneInBase;

/**
 * Created by Prabhpreet on 31-10-2015.
 */
public abstract class NetworksStatusCallBack {
    public void getStatus(int result, String strResponse, TuneInBase stationList) {
    }


    public void getStatus(int result, String strResponse, List<Artist_Info> artistInfoList) {
    }


    public void getStatus(int result, String strResponse) {
    }


    public void onResultBack(Void result, String response, List<String> listData, String[] arrayData) {

    }


}
