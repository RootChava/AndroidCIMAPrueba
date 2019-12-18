package com.example.pruebable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class ViewGeneratorActivity extends Activity {

    /**
     * Construye la vista de acuerdo al mensaje que recibe en el intent.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String mensaje = intent.getStringExtra("mensajeNotificacion");
        String urlImagen = intent.getStringExtra("urlImagen");
        String nombreLugar = intent.getStringExtra("nombreLugar");
        setContentView(R.layout.meraki_one);
        Log.d("INFO","******************************* MENSAJE: " + mensaje);
        TextView tv1 = findViewById(R.id.textView2);
        TextView tv2 = findViewById(R.id.textView3);
        String newImageUrl = null;
        try {
            newImageUrl = URLDecoder.decode(urlImagen, "UTF-8");
            Log.d("INFO","******************************* NEW URL: " + newImageUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ImageView imageView = findViewById(R.id.imageView2);
        new DownloadImageTask(imageView)
                .execute(newImageUrl);
        tv1.setText(mensaje);
        tv2.setText(nombreLugar);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            /*int dimensionInPixel = 1500;
            bmImage.getLayoutParams().height = dimensionInPixel;
            bmImage.getLayoutParams().width = dimensionInPixel;
            bmImage.requestLayout();*/
            bmImage.setImageBitmap(result);
        }
    }



}
