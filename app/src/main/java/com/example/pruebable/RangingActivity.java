package com.example.pruebable;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class RangingActivity extends Activity implements BeaconConsumer, RangeNotifier {
    protected static final String TAG = "RangingActivity";
    private BeaconManager mBeaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_ranging);
        setContentView(R.layout.meraki_two);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        // En este ejemplo vamos a usar el protocolo Eddystone, así que tenemos que definirlo aquí
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"/*BeaconParser.URI_BEACON_LAYOUT*/));
        //Layout iBeacon: m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24
        // Bindea esta actividad al BeaconService
        mBeaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d("INFO", "########## CONSTRUIR BEACON");
        // Encapsula un identificador de un beacon de una longitud arbitraria de bytes
        ArrayList<Identifier> identifiers = new ArrayList<>();

        // Asignar null para indicar que queremos buscar cualquier beacon
        identifiers.add(null);
        // Representa un criterio de campos utilizados para buscar beacons
        Region region = new Region("AllBeaconsRegion", identifiers);
        try {
            // Ordena al BeaconService empezar a buscar beacons que coincida con el objeto Region pasado
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // Especifica una clase que debería ser llamada cada vez que BeaconsService obtiene datos, una vez por segundo por defecto
        mBeaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d("INFO", "########## DETECCIÓN DE BEACON");
        //Log.d("INFO", "########## DETECCIÓN DE BEACON: " + beacons.iterator().next().getServiceUuid());
        if (beacons.size() > 0) {
            //Log.d("INFO", "########## El primer beacon detectado se encuentra a una distancia de "+beacons.iterator().next().getDistance()+" metros.");
/*            String info = beacons.iterator().next().getBluetoothAddress() + ", " + beacons.iterator().next().getBluetoothName() + ", " + beacons.iterator().next().getBeaconTypeCode() + ", "
                    + beacons.iterator().next().getServiceUuid() + ", " + beacons.iterator().next().getDistance() + ", " + beacons.iterator().next().getManufacturer() +
                    ", " + beacons.iterator().next().getDataFields() + ", " + beacons.iterator().next().getId1() + ", " + beacons.iterator().next().getId2() +
                    ", " + beacons.iterator().next().getId3();
            Log.d("INFO","DETECTADO: " + info);*/
            for (Beacon b : beacons) {
                String info = b.getBluetoothAddress() + ", " + b.getBluetoothName() + ", " + b.getBeaconTypeCode() + ", "
                        + b.getServiceUuid() + ", " + b.getDistance() + ", " + b.getManufacturer() +
                        ", " + b.getDataFields() + ", " + b.getId1() + ", " + b.getId2() +
                        ", " + b.getId3();
                Log.d("INFO","Detectado: " + info);
            }
        }
        //F0:F8:F2:D5:DE:A2 88:15:44:CA:B9:A0
    }
}