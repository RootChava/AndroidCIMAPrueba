package com.example.pruebable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("INFO", "SERVICE STOPS");
        /*context.startService(new Intent(context, BluetoothService.class));*/
        context.startService(new Intent(context, BeaconingService.class));
    }
}
