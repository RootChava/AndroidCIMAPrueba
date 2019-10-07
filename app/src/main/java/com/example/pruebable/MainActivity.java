package com.example.pruebable;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Intent intent;
    private int bandera = 0; // 0: Notificación no lanzada, 1: Notificación lanzada
    private int banderaInit = 0;
    private int banderaPermisoUbicacion = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("INFO ","####### App iniciada");
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d("INFO ","####### Permiso: " +  permissionCheck);
        banderaPermisoUbicacion = 1;
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("INFO ","####### GPS no permitido");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            if(areLocationServicesEnabled(this)) {
                Log.d("INFO ","####### GPS activo, no se requieren acciones");
                mHandler = new Handler();
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                    finish();
                }
                intent = new Intent(this, BeaconingService.class);
                if(!isMyServiceRunning(BeaconingService.class)) startService(intent);
                /*intent = new Intent(this, RangingActivity.class);
                startActivity(intent);*/
            } else {
                Log.d("INFO ","####### SIN ACCESO AL GPS DEL DISPOSITIVO");
                //Se ejecuta una notificación por razones desconocidas
                // que cambia el valor de bandera a 1 y llega a resume
                /*Regresa de la notificación espontánea y setea bandera a 0, bandera Init a 1*/
                buildAlertMessageNoGps();
                bandera = 1;
                /*bandera = 1 -> resume*/

            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy() {
        stopService(intent);
        Log.d("INFO ", "onDestroy!");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d("INFO ", "######## P A U S A");
        super.onPause();
        /*if(bandera == 0 && banderaInit == 1) {
            Log.d("INFO ", "######## APLICACIÓN PAUSADA EN ESPERA DE CONEXIÓN");
        } else if (bandera == 0 && banderaInit == 0) {
            Log.d("INFO ", "######## APLICACIÓN PAUSADA, SIN PROBLEMAS DE CONEXION");
        }*/
    }

    @Override
    protected void onResume() {
        //Log.d("INFO ", "######## R E S U M E. bandera: " + bandera + ", banderaInit: " + banderaInit);
        Log.d("INFO ", "######## R E S U M E");
        super.onResume();
        if(bandera == 0 && banderaInit == 1) {
            Log.d("INFO ","####### Corroborando ubicación al resumir app");
            if(!areLocationServicesEnabled(this)) {
                buildAlertMessageNoGps();
            } else {
                Log.d("INFO ","####### GPS activo, comenzando el escaneo...");
                mHandler = new Handler();
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                    finish();
                }
                intent = new Intent(this, BeaconingService.class);
                if(!isMyServiceRunning(BeaconingService.class)) startService(intent);
                /*intent = new Intent(this, BluetoothService.class);
                if(!isMyServiceRunning(BluetoothService.class)) startService(intent);*/
                bandera = 0;
                banderaInit = 0;
            }
        } else if (bandera == 1 && banderaInit == 0){
            Log.d("INFO ","####### Caso de error de inicio de aplicación");
            bandera = 0;
            banderaInit = 1;
        }
    }

    public boolean areLocationServicesEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) /*|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)*/;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void buildAlertMessageNoGps() {
        Log.d("INFO ","####### Notificando al usuario...");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("La aplicación requiere tener activada la ubicación del dispositivo, ¿Deseas activarla?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}