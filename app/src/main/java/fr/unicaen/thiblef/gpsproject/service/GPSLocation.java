package fr.unicaen.thiblef.gpsproject.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Maxime on 11/03/15.
 */
public class GPSLocation extends Service implements LocationListener {
    /*Toutes les 30 secondes*/
    private static final long MIN_TIME_BW_UPDATES = 30000;

    protected Context context;
    protected Location location;
    protected LocationManager locationManager;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Log.i("GPSLocation : ","Initialisation");
        context = this;
        location = getLocation(context);

        return START_NOT_STICKY;
    }

    public Location getLocation(Context context) {
        Log.i("GPSLocation : ","Initialisation du manager");
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            Log.i("No gps and No Network ", "No gps and No Network is enabled, enable either one of them");
            Toast.makeText(this, "Enable either Network or GPS", Toast.LENGTH_LONG).show();
        } else {
            this.canGetLocation = true;
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, 0, this);
                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, 0, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        }
        return location;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("GPSLocation : ","Changement de Location");
        Intent intent1 = new Intent("fr.unicaen.thiblef.GPSProject");
        intent1.putExtra("latitude", location.getLatitude());
        intent1.putExtra("longitude", location.getLongitude());
        sendBroadcast(intent1);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        stopSelf();
        stopUsingGPS();
        super.onDestroy();
    }

    public void stopUsingGPS() {
        Log.i("GPSLocation : ","StopUsing location manager");
        if (locationManager != null) {
            locationManager.removeUpdates(GPSLocation.this);
        }
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }
}