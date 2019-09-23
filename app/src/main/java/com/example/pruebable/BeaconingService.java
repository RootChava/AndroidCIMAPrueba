package com.example.pruebable;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.UUID;

public class BeaconingService extends Service {

    private boolean mScanning;
    private BluetoothLeScanner bleScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private static final UUID MERAKI_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private NotificationHelper notificationHelper;

    public BeaconingService() {
        super();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        notificationHelper = new NotificationHelper(this);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (bleScanner == null) bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
        Log.d("INFO","Entrando al IntentService...");
        scanLeDevice(true);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("INFO", "DESTRUYENDO EL SERVICIO");
        Intent broadcastIntent = new Intent(this, RestartReceiver.class);
        sendBroadcast(broadcastIntent);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("INFO", "ONBIND");
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            Log.d("INFO", "VOY A EMPEZAR A ESCANEAR");
            mScanning = true;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    bleScanner.startScan(leScanCallback);
                }
            });
        } else {
            bleScanner.stopScan(leScanCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String nombre = device.getName() != null ? device.getName(): "UNKNOWN NAME";
            String address = device.getAddress() != null ? device.getAddress(): "UNKNOWN ADDRESS";
            if( address.equals("88:15:44:CA:B9:A0") || address.equals("E0:CB:BC:BF:9C:E3") || true){
//            if(true){
                ParcelUuid[] uuids = device.getUuids() != null ? device.getUuids() : new ParcelUuid[0];
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

}
