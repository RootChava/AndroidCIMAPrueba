package com.example.pruebable;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.constraints.Util;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

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
    private NotificationHelper notificationHelper;
    private Calendar cal;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private Map<Beacon, Date> seenBeacons = new HashMap();

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
                setBeaconLayout(Util.iBeacon));
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() > 0) {
            for (Beacon b : beacons) {
                String UUID = b.getId1().toString();
                String major = b.getId2().toString();
                String minor = b.getId3().toString();

                Map<String, String> jsonBody = new HashMap<String, String>();
                jsonBody.put("uuid", UUID);
                jsonBody.put("major", major);
                jsonBody.put("minor", minor);

                if (UUID.equals(Util.UUID)){
                    try {
                        Date fechaEncuentro = sdf.parse(sdf.format(new Date()));
                        if (seenBeacons.get(b) == null) {
                            seenBeacons.put(b,fechaEncuentro);
                            Log.d("INFO","########## NUEVO " + seenBeacons.get(b));
                            mandarNotificacion(b, jsonBody);
                        } else {
                            Date fechaActual = sdf.parse(sdf.format(new Date()));
                            long millis = (fechaActual.getTime()/1000)/60;
                            Date fechaGuardada = (Date)seenBeacons.get(b);
                            Log.d("INFO","########## BEACON YA EXISTENTE CON FECHA " + seenBeacons.get(b));
                            long millisGuardada = (fechaGuardada.getTime()/1000)/60;
                            if ((millis - millisGuardada) >= 3) {
                                Log.d("INFO","########## BEACON ACTUALIZADOS");
                                mandarNotificacion(b, jsonBody);
                                seenBeacons.replace(b,fechaActual);
                            } else {
                                //Log.d("INFO","########## Beacon vigente " + b.toString() + ", tiempo restante: " + (millis - millisGuardada));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void mandarNotificacion(final Beacon b, Map<String, String> jsonBody) {
        Log.d("INFO","########## BEACON NUEVO DETECTADO: " + jsonBody.toString());
        //JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, Util.server, new JSONObject(jsonBody), new Response.Listener<JSONObject>() {
        String url = Util.server;
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonBody), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("INFO","########## RESPUESTA: " + response.toString());
                    notificationHelper.createNotification("CDMX BLE", response.getString("descripcion"), response.getString("urlImagen"), 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("INFO","########## ERROR HTTP: " + error.toString());
                Toast.makeText(getApplicationContext(), "Response:  " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {

            /**@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", Util.serverToken);
                return headers;
            }**/
        };
        Volley.newRequestQueue(getApplicationContext()).add(jsonObject);
    }

    public void mandarNotificacionGet(final Beacon b, Map<String, String> jsonBody) {
        Log.d("INFO","########## BEACON NUEVO DETECTADO: " + jsonBody.toString());
        String url = Util.server + "getbeacondata?uuid=" + b.getId1() + "&major=" + b.getId2() + "&minor=" + b.getId3();
        JsonObjectRequest jsonObject = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(jsonBody), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("INFO","########## RESPUESTA: " + response.toString());
                    notificationHelper.createNotification("CDMX BLE", response.getString("descripcion"), response.getString("urlImagen"), 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("INFO","########## ERROR HTTP: " + error.toString());
                Toast.makeText(getApplicationContext(), "Response:  " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        Volley.newRequestQueue(getApplicationContext()).add(jsonObject);
    }
}
