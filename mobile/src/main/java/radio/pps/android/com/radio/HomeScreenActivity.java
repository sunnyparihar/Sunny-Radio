package radio.pps.android.com.radio;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import radio.pps.android.com.radio.Constants.StaticMethods;
import radio.pps.android.com.radio.Fragments.HomeScreenFragment;
import radio.pps.android.com.radio.Fragments.PlayerScreenFragment;
import radio.pps.android.com.radio.Fragments.SettingsFragment;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

/**
 * Created by Prabhpreet on 24-10-2015.
 */
public class HomeScreenActivity extends AppCompatActivity {
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private HomeScreenFragment homeScreenFragment;
    private PlayerScreenFragment playerScreenFragment;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            StaticMethods.changeLayoutDirection(this);
        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        setContentView(R.layout.home_screen_phone);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        if (savedInstanceState == null) {
            homeScreenFragment = new HomeScreenFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("listTop500", getIntent().getParcelableExtra("listTop500"));
            homeScreenFragment.setArguments(bundle);
            StaticMethods.loadFragment(this, homeScreenFragment, R.id.homeScreenContainer, false, "homeScreenFragment");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (playerScreenFragment == null) {
                        playerScreenFragment = new PlayerScreenFragment();
                        playerScreenFragment.setActivityContext(HomeScreenActivity.this);
                        playerScreenFragment.setSlidingPanelContext(slidingUpPanelLayout);
                        StaticMethods.loadFragmentWithoutAnimation(HomeScreenActivity.this, playerScreenFragment, R.id.frmlyt_playerscreen_container, false, "playerScreenFragment");
                    }
                } catch (Exception e) {

                }
            }
        }, 400);

        RadioSharedPreferences.getInstance(mContext).setAppStartCounter();

    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    private FragmentManager.OnBackStackChangedListener
            mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag("SettingsFragment");
            if (fragment != null) {
                if (slidingUpPanelLayout == null) {
                    slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                }
                slidingUpPanelLayout.setPanelHeight(0);
            } else {
                if (slidingUpPanelLayout == null) {
                    slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                }
                slidingUpPanelLayout.setPanelHeight(android.R.attr.actionBarSize);
            }

        }
    };

    @Override
    public void onBackPressed() {

        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Toast.makeText(mContext, "Press once again to exit", Toast.LENGTH_SHORT).show();
                }

                mBackPressed = System.currentTimeMillis();
            } else {
                super.onBackPressed();
                return;
            }

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("SettingsFragment");
        if (fragment != null) {
            ((SettingsFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
    }
}
