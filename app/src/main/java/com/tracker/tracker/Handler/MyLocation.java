package com.tracker.tracker.Handler;

import java.security.ProviderException;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

public class MyLocation {
    Timer timer;
    LocationManager locationManager;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;

    public boolean getLocation(Context context, LocationResult result) {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (ProviderException e) {

        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (ProviderException e) {

        }

        if (!gps_enabled && !network_enabled) {
            return false;
        }

        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        }
        if(network_enabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        }
        timer = new Timer();
        timer.schedule(new GetLastLocation(), 20000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            locationResult.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            locationManager.removeUpdates(locationListenerGps);
            locationManager.removeUpdates(locationListenerNetwork);

            Location net_loc=null, gps_loc=null;
            if(gps_enabled) {
                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(network_enabled) {
                net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            //if there are both values use the latest one
            if(gps_loc != null && net_loc != null) {
                if(gps_loc.getTime()>net_loc.getTime()) {
                    locationResult.gotLocation(gps_loc);
                } else {
                    locationResult.gotLocation(net_loc);
                }
                return;
            }

            if(gps_loc != null) {
                locationResult.gotLocation(gps_loc);
                return;
            }
            if(net_loc != null){
                locationResult.gotLocation(net_loc);
                return;
            }
            locationResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}
