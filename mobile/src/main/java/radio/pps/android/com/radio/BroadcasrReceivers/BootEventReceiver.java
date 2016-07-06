package radio.pps.android.com.radio.BroadcasrReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import radio.pps.android.com.radio.Constants.StaticMethods;

/**
 * Created by Prabhpreet on 17-01-2016.
 */
public class BootEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String returnActionFilter = intent.getAction();
        if (returnActionFilter.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)
                || returnActionFilter.equalsIgnoreCase(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE)) {

        }
    }
}
