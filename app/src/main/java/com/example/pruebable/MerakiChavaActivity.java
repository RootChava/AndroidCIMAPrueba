package com.example.pruebable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

public class MerakiChavaActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("INFO:","%%%%%%%%%%ENTRANDO A ACTIVIDAD CHAVA");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meraki_two);
    }
}
