package radio.pps.android.com.radio.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import radio.pps.android.com.radio.Constants.Constants;
import radio.pps.android.com.radio.Constants.StaticMethods;
import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

/**
 * Created by Prabhpreet on 22-01-2016.
 */
public class CommonFragment extends Fragment {
    protected Context mContext = getContext();
    private AdView adView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        if (mContext == null)
            mContext = getContext();
        if (!RadioSharedPreferences.getInstance(mContext).getSettingRemoveAds()) {
            adView = (AdView) getView().findViewById(R.id.adView);
            try {
                AdRequest adRequest;

                String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                String deviceId = StaticMethods.MD5(android_id).toUpperCase();
                adRequest = new AdRequest.Builder().addTestDevice(deviceId).build();

                adView.loadAd(adRequest);
                adView.setVisibility(View.GONE);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        adView.setVisibility(View.VISIBLE);
                        adView.requestLayout();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            (getView().findViewById(R.id.adView)).setVisibility(View.GONE);
        }

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adView != null)
            adView.pause();
    }
}


