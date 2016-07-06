package radio.pps.android.com.radio.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import radio.pps.android.com.radio.Constants.StaticMethods;
import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.ToolbarInitializer;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

/**
 * Created by Prabhpreet on 10-01-2016.
 */
public class SettingsFragment extends CommonFragment {
    private static final String TAG = "SetttingsFragment";
    private AppCompatCheckBox chkBoxPlayInBg;
    private View mLayout;
    private final int RC_MEDIAPLAYER_AUDIOSESSION = 52626;
    private String pkgId = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_fragment, container
                , false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mLayout = getView().findViewById(R.id.main_layout);
        try {
            if (getActivity() == null)
                return;
            StaticMethods.changeLayoutDirection(getActivity());
        } catch (Exception e) {

        }

        ToolbarInitializer toolbarInitializer = new ToolbarInitializer(mContext, (Toolbar) getView().findViewById(R.id.toolbar), getResources().getString(R.string.app_name_category));
        toolbarInitializer.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        chkBoxPlayInBg = (AppCompatCheckBox) getView().findViewById(R.id.checkboxBackgroundPlay);

        chkBoxPlayInBg.setChecked(RadioSharedPreferences.getInstance(mContext).getBackGroundPlay());
        chkBoxPlayInBg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                if (((AppCompatCheckBox) v).isChecked()) {

                    RadioSharedPreferences.getInstance(mContext).setBackGroundPlay(true);
                } else {
                    RadioSharedPreferences.getInstance(mContext).setBackGroundPlay(false);
                }

            }
        });

        super.onActivityCreated(savedInstanceState);
    }



}
