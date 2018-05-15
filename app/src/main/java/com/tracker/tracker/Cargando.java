package com.tracker.tracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tracker.tracker.tareas.UserData;
import com.tracker.tracker.Handler.*;


public class Cargando extends Activity {

    private static final int MY_LOCATION_PERMISSION = 0;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FusedLocationProviderClient locationProviderClient;
    private Location currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargando);
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();

        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION);
        } else {

        }

        this.locationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                        } else {
                            Log.e("COÑO MARICO QUE PEO", "JAJDAJFNDSKFEWJFNSD");
                        }
                    }
                });

        Intent intent;
        if(user != null) {
            new UserData(this.currentLocation).execute(this.user);
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, Login.class);
        }

        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", "ALL GOD");
                } else {
                    Log.d("NO LO SE RICK", "JEJEJEJEJEJ");
                }
                return;
            }
        }
    }


}
