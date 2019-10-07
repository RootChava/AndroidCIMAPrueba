package com.example.pruebable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class FilterMerakiActivity extends AppCompatActivity {

    private BLEReceiver receiver;
    private String target_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("INFO:","&&&&&&&&&&&&&& ENTRANDO A ACTIVIDAD DE FILTRADO DE MAC ADDRESS");
        super.onCreate(savedInstanceState);
        receiver = new BLEReceiver();
        registerReceiver(receiver, new IntentFilter("PRINT_MESSAGE"));  //<----Register
        if (target_view.equals("v1")) {
            setContentView(R.layout.meraki_one);
        } else if (target_view.equals("v2")){
            setContentView(R.layout.meraki_two);
        } else {
            Log.d("INFO:","&&&&&&&&&&&&&& Ocurrió un error al generar la vista");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(receiver);           //<-- Unregister to avoid memoryleak
    }

    class BLEReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("PRINT_MESSAGE")) {
                String mac = intent.getStringExtra("MERAKI_MAC");
                switch(mac){
                    case "46:09:8D:25:54:0E"/*"88:15:44:CA:B9:A0"*/:
                        target_view = "v1";
                        break;
                    case "E0:CB:BC:BF:9C:E3":
                        target_view = "v2";
                        break;
                    default:
                        Log.d("INFO:","&&&&&&&&&&&&&& NO SE HA RECIBIDO NADA");
                        break;
                }
            }
        }
    }

}
