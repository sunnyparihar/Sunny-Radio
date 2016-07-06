package radio.pps.android.com.radio.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import radio.pps.android.com.radio.R;
import radio.pps.android.com.radio.utils.RadioSharedPreferences;

/**
 * Created by Prabhpreet on 26-11-2015.
 */
public class StaticMethods {

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String capitalizeFirstLetter(String original) {
        if (original.length() == 0)
            return original;
        return original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
    }

    public static void loadFragment(Fragment context, Fragment fragment, int homeScreenContainer, boolean addtoBackStack, String tag) {
        FragmentTransaction fragmentTransaction = context.getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,
                R.anim.push_right_in, R.anim.push_right_out);
        fragmentTransaction.replace(homeScreenContainer, fragment, tag);
        if (addtoBackStack)
            fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public static void loadFragment(AppCompatActivity context, Fragment fragment, int homeScreenContainer, boolean addtoBackStack, String tag) {
        FragmentTransaction fragmentTransaction = context.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out,
                R.anim.push_right_in, R.anim.push_right_out);
        fragmentTransaction.replace(homeScreenContainer, fragment, tag);
        if (addtoBackStack)
            fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public static void loadFragmentWithoutAnimation(AppCompatActivity context, Fragment fragment, int homeScreenContainer, boolean addtoBackStack, String tag) {
        FragmentTransaction fragmentTransaction = context.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(homeScreenContainer, fragment, tag);
        if (addtoBackStack)
            fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }


    public static void changeLayoutDirection(Activity context) throws Exception {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (context != null) {
                Configuration config = context.getResources().getConfiguration();
                if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    //in Right To Left layout
                    return;
                }

                if (RadioSharedPreferences.getInstance(context).getForceRTLSupport()) {
                    context.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    context.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }

            } else {

            }
        }
    }


    public static String MD5(String android_id) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(android_id.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

}
