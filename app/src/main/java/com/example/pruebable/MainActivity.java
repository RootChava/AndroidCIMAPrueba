package com.example.pruebable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler;
    private boolean mScanning;
    private BluetoothLeScanner bleScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final UUID MERAKI_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private NotificationHelper notificationHelper;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            if(areLocationServicesEnabled(this)) {
                mHandler = new Handler();

                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                    finish();
                }
                notificationHelper = new NotificationHelper(this);
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                if (bleScanner == null) bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
                scanLeDevice(true);
            }
        }
    }

    public boolean hasBlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestBlePermissions(final Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    public boolean checkGrantResults(String[] permissions, int[] grantResults) {
        int granted = 0;

        if (grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        granted++;
                    }
                }
            }
        } else { // if cancelled
            return false;
        }

        return granted == 2;
    }

    public boolean areLocationServicesEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            Log.d("INFO", "VOY A EMPEZAR A ESCANEAR");
            mScanning = true;
            bleScanner.startScan(leScanCallback);
            //mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String nombre = device.getName() != null ? device.getName(): "UNKNOWN NAME";
            String address = device.getAddress() != null ? device.getAddress(): "UNKNOWN ADDRESS";
            if( address.equals("88:15:44:CA:B9:A0") || address.equals("E0:CB:BC:BF:9C:E3")){
                ParcelUuid[] uuids = device.getUuids() != null ? device.getUuids() : new ParcelUuid[0];
                Log.d("INFO: ES CONECTABLE", String.valueOf(result.isConnectable()));
                Log.d("INFO: NOMBRE", nombre);
                Log.d("INFO: MAC", address);
                Log.d("INFO: TOSTRING", result.toString());
                for (int i = 0; i < uuids.length; i++ ){
                    Log.d("INFO: UUIDS", uuids[i].toString());
                }
                notificationHelper.createNotification("ROOT BLE APP", "Bienvenido a Root");
            }
        }
    };

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ParcelUuid[] puu = device.getUuids();

                            if( device.getAddress().equals("88:15:44:CA:B9:A0") || device.getAddress().equals("E0:CB:BC:BF:9C:E3")){
                                Log.d("INFO: ->>>>", device.getAddress());
                                Log.d("INFO: TIPO", String.valueOf(device.getType()));
                                for (int x = 0; x < scanRecord.length; x++){
                                    Log.d("INFO: SCAN RECORDS ", String.valueOf(scanRecord[x]));
                                }
                                if(device.getName() != null){
                                    Log.d("INFO: NOMBRE", device.getName());
                                }
                                if(puu != null){
                                    Log.d("INFO: NEW DEVICE", String.valueOf(puu.length));
                                    for (int i = 0; i < puu.length; i++){
                                        Log.d("INFO UID CONTENTS", puu[i].toString());
                                    }
                                }


//                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                                PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "com.root.pruebable")
//                                        .setSmallIcon(R.drawable.notification_icon)
//                                        .setContentTitle(textTitle)
//                                        .setContentText(textContent)
//                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            }else {
                                if(device.getName() != null){
                                    Log.d("INFO: NOMBRE", device.getName());
                                }
                                if(puu != null){
                                    Log.d("INFO: NEW DEVICE", String.valueOf(puu.length));
                                    for (int i = 0; i > puu.length; i++){
                                        Log.d("INFO UID CONTENTS", puu[i].toString());
                                    }
                                }
                                Log.d("INFO: RSSI", String.valueOf(rssi));
                                Log.d("INFO: NEW DEVICE", device.getAddress());
                            }
                        }
                    });
                }
            };

}