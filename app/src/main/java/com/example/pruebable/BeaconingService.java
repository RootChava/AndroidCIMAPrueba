package com.example.pruebable;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BeaconingService extends Service implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;
    private final String iBeacon = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private NotificationHelper notificationHelper;
    private Calendar cal;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private Map map = new HashMap();

    public BeaconingService() {
        super();
    }

    /**
     * Métodos para servicio
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationHelper = new NotificationHelper(this);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(iBeacon));
        mBeaconManager.bind(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("INFO", "########## Destruyendo servicio");
        /*Intent broadcastIntent = new Intent(this, RestartReceiver.class);
        sendBroadcast(broadcastIntent);*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("INFO", "########## onBind");
        return null;
    }

    /**
     * Métodos para escaneo de beacons
     */
    @Override
    public void onBeaconServiceConnect() {
        Log.d("INFO", "########## Construyendo beacon");
        ArrayList<Identifier> identifiers = new ArrayList<>();
        identifiers.add(null);
        Region region = new Region("AllBeaconsRegion", identifiers);
        try {
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        int i = 0;
        //Log.d("INFO", "########## Detección de beacon(s)");
        if (beacons.size() > 0) {
            for (Beacon b : beacons) {
                String UUID = b.getId1().toString();
                /*String major = b.getId2().toString();
                String minor = b.getId3().toString();
                String MAC = b.getBluetoothAddress();
                String info = "UUID: " + UUID + ", Major: " + major + ", Minor: " + minor +
                        ", MAC: " + MAC;
                Log.d("INFO","########## Beacon iBeacon detectado: " + info);*/
                if (UUID.equals("978ea1fa-ca7b-41ab-9100-446482bff79f")){
                    try {
                        Date fechaEncuentro = sdf.parse(sdf.format(new Date()));
                        if (map.get(b) == null) {
                            mandarNotificacion(b,i);
                            map.put(b,fechaEncuentro);
                            Log.d("INFO","########## Beacon nuevo detectado " + b.toString());
                            i += 1;
                        } else {
                            Date fechaActual = sdf.parse(sdf.format(new Date()));
                            long millis = (fechaActual.getTime()/1000)/60;
                            Date fechaGuardada = (Date)map.get(b);
                            long millisGuardada = (fechaGuardada.getTime()/1000)/60;
                            if ((millis - millisGuardada) >= 30) {
                                Log.d("INFO","########## Beacon actualizado");
                                mandarNotificacion(b,i);
                                map.replace(b,fechaActual);
                                i += 1;
                            } else {
                                Log.d("INFO","########## Beacon vigente " + b.toString() + ", tiempo restante: " + (millis - millisGuardada));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void mandarNotificacion(Beacon b, int idNot) {
        switch (b.getId2().toString()) {
            case "15":
                notificationHelper.createNotification("CDMX BLE", "¡Bienvenid@ a Reforma!", idNot);
                break;
            case "3":
                notificationHelper.createNotification("CDMX BLE", "¡Bienvenid@ a Coyoacán!", idNot);
                break;
            default:
                break;
        }
    }
}
