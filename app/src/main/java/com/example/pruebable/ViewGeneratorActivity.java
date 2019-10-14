package com.example.pruebable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ViewGeneratorActivity extends Activity {

    /**
     * Construye la vista de acuerdo al mensaje que recibe en el intent.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String id = intent.getStringExtra("majorMeraki");
        String mensaje = intent.getStringExtra("mensajeNotificacion");
        Log.d("INFO","########## MENSAJE: " + id);
        if (id.contains("3")){
            setContentView(R.layout.meraki_one);
            TextView tv1 = findViewById(R.id.textView2);
            tv1.setText(mensaje);
        } else if(id.contains("15")){
            setContentView(R.layout.meraki_two);
        } else {
            Log.d("INFO","########## Hubo un problema al generar la vista");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(this , MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
