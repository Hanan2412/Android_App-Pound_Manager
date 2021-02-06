package BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BatteryLevelReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent batteryIntent = new Intent("batteryLevel");
        if(intent.getAction()!=null) {
            String TAG = "BATTERY BROADCAST RECEIVER";
            switch (intent.getAction()) {
                case Intent.ACTION_BATTERY_LOW: {
                    batteryIntent.putExtra("battery","Low");
                    Log.d(TAG,"battery is low");
                    break;
                }
                case Intent.ACTION_BATTERY_OKAY: {
                    batteryIntent.putExtra("battery","Okay");
                    Log.d(TAG,"battery is okay");
                    break;
                }
                default: {
                    Log.d(TAG, "error in battery broadcast receiver");
                }
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(batteryIntent);
        }
    }
}
