package com.example.pruebable;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.UUID;

public class BeaconingService extends IntentService {

    private boolean mScanning;
    private BluetoothLeScanner bleScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private static final UUID MERAKI_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    private NotificationHelper notificationHelper;

    public BeaconingService() {
        super("Meh");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        notificationHelper = new NotificationHelper(this);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (bleScanner == null) bleScanner = mBluetoothAdapter.getBluetoothLeScanner();
        Log.d("INFO:","Entrando al IntentService...");
        scanLeDevice(true);
        //stopSelf();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            Log.d("INFO", "VOY A EMPEZAR A ESCANEAR");
            mScanning = true;
            bleScanner.startScan(leScanCallback);
            //mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            bleScanner.stopScan(leScanCallback);
            /*mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            stopSelf();
            scanLeDevice(true);*/
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String nombre = device.getName() != null ? device.getName(): "UNKNOWN NAME";
            String address = device.getAddress() != null ? device.getAddress(): "UNKNOWN ADDRESS";
            if( address.equals("88:15:44:CA:B9:A0") || address.equals("E0:CB:BC:BF:9C:E3")){
                ParcelUuid[] uuids = device.getUuids() != null ? device.getUuids() : new ParcelUuid[0];
                //Log.d("INFO: ES CONECTABLE", String.valueOf(result.isConnectable()));
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

/*    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     final byte[] scanRecord) {
                    BeaconingService.this.runOnUiThread(new Runnable() {
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
            };*/
}
