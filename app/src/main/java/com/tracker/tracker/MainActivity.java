package com.tracker.tracker;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.tareas.ProfilePicture;
import com.tracker.tracker.tareas.UserData;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // Constantes
    private static final int PLACE_PICKER_REQUEST = 2;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";

    // Datos de Firebase
    private FirebaseUser user;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Datos de Ubicación
    private SettingsClient settingsClient;
    private FusedLocationProviderClient locationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private Boolean requestingLocationUpdate;

    // Botones
    private FloatingActionButton fabAdd, fabAddPerson, fabAddLocation;

    // Animaciones
    private Animation fabOpen, fabClose, fabRotateClockwise, fabRotateCounter;
    private boolean isOpen = false;

    //Opciones en Spinner
    private Spinner spinner;
    private ArrayList<Contacto> contactos;

    // Viaje
    private Location currentLocation;
    private Location destination;
    private Contacto contacto;
    private Place placeDestionation;
    private CardView tripDescription;
    private boolean isViajando = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // UI
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!gotPermissions()) {
            requestPermissions();
        }

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Navegacion
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Datos de la Aplicación
        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);

        updateValuesFromBundle(savedInstanceState);

        // Verificar GPS
        this.requestingLocationUpdate = true;
        this.settingsClient = LocationServices.getSettingsClient(this);
        this.locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        this.contactos = new ArrayList<>();
        this.fabConfig();
        this.createLocationCallback();
        this.createLocationRequest();
        this.buildLocationSettingsRequest();
        this.updateUI();
        this.startLocationUpdates();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.spinnerConfig();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                this.requestingLocationUpdate = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                this.currentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }
        }
    }

    private void spinnerConfig() {
        if(isViajando) {
            this.spinner.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txtContacto)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btnFindPlace)).setVisibility(View.GONE);
            Log.d("Spinner", "viajando");
        } else {
            final Context context= this;
            this.contactos.clear();
            this.spinner = findViewById(R.id.spSeresQueridos);
            CollectionReference contactosRef = db.collection("users/" + user.getUid() + "/contactos");
            contactosRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Contacto c = new Contacto(document.getString("nombre"), document.getString("telf"));
                            contactos.add(c);
                        }
                        if(contactos.isEmpty()) {
                            // Si no tiene a nadie lo mando a agregar uno
                            Intent intent = new Intent(context, AddSerQuerido.class);
                            startActivity(intent);
                            Toast.makeText(context, "Añade un ser Querido para empezar a usar la App", Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayAdapter<Contacto> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, contactos);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                            spinner.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
            this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (contactos.size() == 0) return;
                    if(!isViajando) {
                        contacto = contactos.get(pos);
                        findViewById(R.id.txtContacto).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.txtContacto)).setText(contacto.getNombre());
                    } else {
                        Toast.makeText(context,"Cancelar el viaje para escoger otro ser querido", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void fabConfig() {
        // Botones
        fabAdd = findViewById(R.id.fabAdd);
        fabAddPerson = findViewById(R.id.fabAddPerson);
        fabAddLocation = findViewById(R.id.fabAddLocation);

        // Animaciones
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotateClockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        fabRotateCounter = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_counterclockwise);

        fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddSerQuerido.class);
                startActivityForResult(intent, 0);
                onStop();
            }
        });

        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Navegar a la actividad para agregar ubicacion
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    fabAdd.startAnimation(fabRotateCounter);
                    fabAddPerson.startAnimation(fabClose);
                    fabAddLocation.startAnimation(fabClose);
                    fabAddPerson.setClickable(false);
                    fabAddLocation.setClickable(false);
                    isOpen = false;
                } else {
                    fabAdd.startAnimation(fabRotateClockwise);
                    fabAddPerson.startAnimation(fabOpen);
                    fabAddLocation.startAnimation(fabOpen);
                    fabAddPerson.setClickable(true);
                    fabAddLocation.setClickable(true);
                    isOpen = true;
                }
            }
        });
    }

    private void updateUI() {
        final Activity activity = this;
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        this.tripDescription = findViewById(R.id.estadoViaje);
        ((Button) tripDescription.findViewById(R.id.btnCancelarViaje)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripDescription.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.btnFindPlace)).setVisibility(View.VISIBLE);
                isViajando = false;
                contacto = null;
                destination = null;
                placeDestionation = null;
            }
        });

        // Personalizar la UI
        ((TextView) header.findViewById(R.id.txtNombre)).setText(this.user.getDisplayName());
        ((TextView) header.findViewById(R.id.txtEmail)).setText(this.user.getEmail());

        // Tarea Especial para la foto
        new ProfilePicture((ImageView) header.findViewById(R.id.imgProfilePhoto)).execute(this.user.getPhotoUrl().toString());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                // Construct an intent for the place picker
                try {
                    Intent intent = builder.build(activity);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }  catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        };
        findViewById(R.id.btnFindPlace).setOnClickListener(listener);
    }

    private void createLocationCallback() {
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                updateLocation(user, currentLocation);
                if(destination != null) {
                    ((TextView) tripDescription.findViewById(R.id.txtDistance))
                            .setText(String.valueOf(currentLocation.distanceTo(destination)));
                    if(currentLocation.distanceTo(destination) <= 50.0) {
                        sendSMS();
                    }
                }
            }
        };
    }

    private void sendSMS() {
        if(contacto != null && destination != null && placeDestionation != null) {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(DELIVERED), 0);

            //Cuando se envía el mensaje de texto-
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS enviado",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), "Generic failure",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), "No service",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Null PDU",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), "Radio off",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(SENT));

            //---Cuando se recibe el mensaje de texto---
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS recibido",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(getBaseContext(), "SMS not delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));

            //Envío del mensaje de texto
            String sms = "Hola " + contacto.getNombre() + ", ya llegue al destino, " + placeDestionation.getAddress() + ". Mensaje enviado con tracker app";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contacto.getTelf(), null, sms, sentPI, deliveredPI);
            //Retornar la vista Main a la vista principal
            this.tripDescription.setVisibility(View.INVISIBLE);
            this.isViajando = false;
            contacto = null;
            destination = null;
            placeDestionation = null;
            spinnerConfig();
            ((Button) findViewById(R.id.btnFindPlace)).setVisibility(View.VISIBLE);
        }
    }

    private void createLocationRequest() {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(this.locationRequest);
        this.locationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        this.settingsClient.checkLocationSettings(this.locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("", "All location settings are satisfied.");
                        try {
                            locationProviderClient.requestLocationUpdates(locationRequest,
                                    locationCallback, Looper.myLooper());
                        } catch (SecurityException e) {
                            Log.e("Main", "Security Exception", e);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("", errorMessage);
                                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                requestingLocationUpdate = false;
                        }
                        updateUI();
                    }
                });
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdate);
        savedInstanceState.putParcelable(KEY_LOCATION, currentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("", "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("", "User chose not to make required location settings changes.");
                        this.requestingLocationUpdate = false;
                        break;
                }
                break;
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    this.placeDestionation = place;
                    this.destination = new Location("Google Place");
                    this.isViajando = true;
                    this.destination.setLatitude(place.getLatLng().latitude);
                    this.destination.setLongitude(place.getLatLng().longitude);
                    if(contacto != null) {
                        if(currentLocation == null) {
                            Log.e("CurrentLocation", "null");
                        } else {
                            tripDescription = findViewById(R.id.estadoViaje);
                            tripDescription.setVisibility(View.VISIBLE);
                            ((TextView) tripDescription.findViewById(R.id.txtContactoNombre)).setText(contacto.getNombre());
                            ((TextView) tripDescription.findViewById(R.id.txtDestino)).setText(placeDestionation.getName());
                            ((TextView) tripDescription.findViewById(R.id.txtDistance)).setText(String.valueOf(currentLocation.distanceTo(destination)));
                        }
                    } else {
                        Toast.makeText(this, "Debes escoger un ser querido primero", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.add_place :
                break;
            case R.id.add_seres:
                intent = new Intent(this, AddSerQuerido.class);
                startActivityForResult(intent, 0);
                this.spinnerConfig();
                break;
            case R.id.seres:
                intent = new Intent(this, seresQueridos.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.logout:
                this.auth.signOut();
                intent = new Intent(this, Login.class);
                startActivityForResult(intent, 0);
                finish();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateLocation(FirebaseUser user, Location l) {
        new UserData(l).execute(user);
    }

    public boolean gotPermissions() {
        boolean a = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean b = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean c = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        return a && b && c;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("", "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i("", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestingLocationUpdate) {
                    Log.i("", "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            }
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}

