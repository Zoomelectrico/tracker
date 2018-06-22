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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.thomashaertel.widget.MultiSpinner;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Frecuente;
import com.tracker.tracker.Modelos.Usuario;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.android.gms.location.places.Places.getGeoDataClient;

/**
 * Controlador de la actividad principal
 * Esta clase configura el menú, permite crear viaje, maneja el tema de la ubicación
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int PLACE_PICKER_REQUEST = 2;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Datos de Ubicación
    private SettingsClient settingsClient;
    private FusedLocationProviderClient locationProviderClient;
    @Nullable
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
    private MultiSpinner spinner;
    private ArrayList<Contacto> contactos;
    private Spinner spinnerLugares;

    // Viaje
    @Nullable
    private Location currentLocation = null;
    @Nullable
    private Frecuente placeDestination = null;
    private boolean isViajando = false;
    private boolean isLocationEnable = false;

    public Usuario usuario;

    /**
     * Metodo onCreate:
     * @param savedInstanceState {Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        this.firebaseConfig();
        this.getUserData();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.navigationConfig();
        this.permissionConfig();

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
    }

    @Override
    public void onResume() {
        super.onResume();
        this.spinnerConfig();
        this.spinnerLugaresConfig();
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Metodo: getUserData: Este metodo se encarga de obtener el Objeto Parcelable del Usuario
     */
    private void getUserData() {
        this.usuario = this.getIntent().getParcelableExtra("user");
    }

    /**
     * Metodo: updateValuesFromBundle este metódo se encargada de actualizar valores relacionados a la ubicación
     * @param savedInstanceState {Bundle}
     */
    private void updateValuesFromBundle(@Nullable Bundle savedInstanceState) {
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

    /**
     * Metodo firebaseConfig: Este metodo se encarga de iniciar la DB y obtener una instacia del servicio de Auth y se obtiene el usuario actual del sistema
     */
    private void firebaseConfig() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);
    }

    /**
     * Metodo navigationConfig: Este metodo se encarga de crear el Toolbar y el menú de Hamburguesa
     */
    private void navigationConfig() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Metodo spinnerConfig: se encarga de configurar el Spinner para seleccionar a los Seres Queridos
     * Se instancia el spinner, se cambia la visual, le configura el onClick
     */
    private void spinnerConfig() {
        this.spinner = findViewById(R.id.spinnerMulti);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        if(this.usuario.haveContactos()) {
            for (Contacto c : this.usuario.getContactos()) {
                adapter.add(c.getNombre());
            }
            this.spinner.setAdapter(adapter, false, new MultiSpinner.MultiSpinnerListener() {
                @Override
                public void onItemsSelected(@NonNull boolean[] selected) {
                    if(!isLocationEnable) {
                        startLocationUpdates();
                        isLocationEnable = true;
                    }
                    contactos.clear();
                    for (int i = 0; i < selected.length; i++) {
                        if(selected[i]) {
                            contactos.add(usuario.getContacto(i));
                        }
                    }
                    if(placeDestination != null) {
                        isViajando = true;
                        configTrip();
                    } else {
                        Log.e("Contacto", "contacto good");
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Debe añadir un ser Querido", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Metodo spinnerLugaresConfig: se encarga de configurar el Spinner para seleccionar a los lugares Frecuentes
     * Se instancia el spinner, se cambia la visual, le configura el onClick
     */
    private void spinnerLugaresConfig(){
        this.spinnerLugares = findViewById(R.id.spinnerLugares);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item);
        adapter.add("");
        if(this.usuario.haveFrecuentes()) {
            for (Frecuente f : this.usuario.getFrecuentes()) {
                adapter.add(f.getNombre());
            }
            this.spinnerLugares.setAdapter(adapter);
            this.spinnerLugares.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position > 0){
                        if(!isLocationEnable) {
                            startLocationUpdates();
                            isLocationEnable = true;
                        }
                        position--;
                        placeDestination = usuario.getFrecuente(position);
                        placeDestination.setFrecuente(true);

                        if(placeDestination != null && !contactos.isEmpty()) {
                            isViajando = true;
                            configTrip();
                        } else {
                            Log.e("Lugar Frecuente", "Lugar good");
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    /**
     * Metodo fabConfig: Este metodo se encarga de configurar el botón fab
     */
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
                Intent intent = new Intent(MainActivity.this, AddSerQuerido.class);
                Bundle bundle = new Bundle();
                intent.putExtra("user", usuario);
                startActivity(intent);
            }
        });

        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Esta Funcionalidad no esta Disponible", Toast.LENGTH_SHORT).show();
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

    /**
     * Metodo updateUI: este metodo se encarga de configurar la barra de navegación, el boton de places,
     * el boton de cancelar viaje, y de adaptar la UI a una persona
     */
    private void updateUI() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        //Se procede a configurar el boton de añadir un lugar frecuente una vez comenzado el viaje.
        findViewById(R.id.btnAddLugarFrecuente).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putBoolean("haveDestino", true);
                args.putString("id", placeDestination.getPlaceId());
                args.putDouble("destLat", placeDestination.getLatitud());
                args.putDouble("destLon", placeDestination.getLongitud());
                args.putString("destDireccion", placeDestination.getDireccion());
                //Muestra un FragmentDialog para añadir el nombre del lugar
                AddLugarFrecuenteDialog addLF = new AddLugarFrecuenteDialog();
                addLF.setArguments(args);
                addLF.show(getFragmentManager(), "AddLugarFrecuenteDialogFragment");
                findViewById(R.id.btnAddLugarFrecuente).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.btnCancelarViaje).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
                isViajando = false;
                configTrip();
                contactos.clear();
                placeDestination = null;
                boolean[] bool = new boolean[usuario.getContactos().size()];
                Arrays.fill(bool, false);
                spinner.setSelected(bool);
                spinnerLugaresConfig();
            }
        });

        // Personalizar la UI
        ((TextView) findViewById(R.id.txtUserName)).setText(this.usuario.getNombre());
        ((TextView) header.findViewById(R.id.txtNombre)).setText(this.usuario.getNombre());
        ((TextView) header.findViewById(R.id.txtEmail)).setText(this.usuario.getEmail());

        if(this.usuario.getPhoto() != null){
            if(this.usuario.getPhoto() != null || this.usuario.getPhoto().length() > 0) {
                this.usuario.imageConfig((ImageView) findViewById(R.id.imgPhoto));
                this.usuario.imageConfig((ImageView) header.findViewById(R.id.imgProfilePhoto));
            }
        }
         else {
            Log.e("IMAGE", "MAMAGUEVO"); //ok
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLocationEnable) {
                    startLocationUpdates();
                    isLocationEnable = true;
                }
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = builder.build(MainActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }  catch (@NonNull GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
        };
        findViewById(R.id.btnFindPlace).setOnClickListener(listener);
    }

    /**
     * Metodo createLocationCallback: este metodo se encarga de crear el listener para los cambios de ubicación
     */
    private void createLocationCallback() {
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                if(placeDestination != null) {
                    Location destination = new Location("Google Place");
                    destination.setLatitude(placeDestination.getLatitud());
                    destination.setLongitude(placeDestination.getLongitud());
                    ((TextView) findViewById(R.id.txtDistance))
                            .setText(String.valueOf(currentLocation.distanceTo(destination)));
                    if(currentLocation.distanceTo(destination) <= 50.0) {
                        Log.e("placeArrival", contactos.toString());
                        placeArrival();
                    }
                }
            }
        };
    }

    /**
     * Metodo sendSMS: este metodo se encarga de enviar un mensaje de texto a una persona.
     * @param contacto {Contacto} el objecto contacto que referencia el destinatario del mensaje.
     */
    private void sendSMS(@NonNull Contacto contacto) {
        if(placeDestination != null) {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS enviado", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), "Sin señal", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(SENT));

            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS recibido", Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));

            //Estructuración del contenido del sms.
            String sms = "Hola " + contacto.getNombre() + ", ya llegue al destino. Mensaje enviado con Tracker app.";
            //En caso que exista dirección, se coloca la dirección
            if(placeDestination.getDireccion() != null){
                sms = "Hola " + contacto.getNombre() + ", ya llegue al destino, " + placeDestination.getDireccion() + ". Mensaje enviado con Tracker App";
            }
            //Envío del mensaje de texto. El texto está condicionado a la existencia del caracter ° en el nombre, en caso de que exista
            boolean grado = placeDestination.getNombre().contains("°");
            if(!grado) {
                if(placeDestination.getNombre() != null){
                    sms = "Hola " + contacto.getNombre() + ", ya llegue a " + placeDestination.getNombre() + ". Mensaje enviado desde Tracker App";
                }
            }

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contacto.getTelf(), null, sms, sentPI, deliveredPI);
        }
    }

    /**
     * Metodo placeArrival: este metodo es llamado cuando un persona llega a un determinado placeDestination.
     * Utilizar el metodo sendSMS para los mensajes de texto.
     * Adicionalmente se encarga de actualizar la UI
     */
    private void placeArrival() {
        for (Contacto contacto: contactos) {
            this.sendSMS(contacto);
        }
        isViajando = false;
        stopLocationUpdates();
        placeDestination = null;
        contactos.clear();
        boolean[] bool = new boolean[this.usuario.getContactos().size()];
        Arrays.fill(bool, false);
        this.spinner.setSelected(bool);
        this.configTrip();
        this.spinnerLugaresConfig();
    }

    /**
     * Metodo createLocationRequest: este metodo se encarga de crear un objecto para hacer la petición de la Ubicación
     */
    private void createLocationRequest() {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Metodo buildLocationSettingsRequest: este metodo se encarga de configurar las peticiones de ubicación
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(this.locationRequest);
        this.locationSettingsRequest = builder.build();
    }

    /**
     * Metodo startLocationUpdates: este metodo se encarga de empezar a escuchar los cambios de ubicación en el telefono
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        this.settingsClient.checkLocationSettings(this.locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("", "All location settings are satisfied.");
                        try {
                            locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
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
                    }
                });
    }

    /**
     * Metodo stopLocationUpdates: este metodo se encarga de terminar de escuchar los cambios de ubicación en el telefono
     */
    private void stopLocationUpdates() {
        isLocationEnable = false;
        if(locationCallback != null) {
            this.locationProviderClient.removeLocationUpdates(this.locationCallback);
        } else {
            Log.e("Location Callback", "IS NULL");
        }
    }

    /**
     *
     */
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdate);
        savedInstanceState.putParcelable(KEY_LOCATION, currentLocation);
        savedInstanceState.putParcelable("user", usuario);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Metodo onActivyResult: este metodo se encarga de decidir que hacer despues de que se lleva a cabo un intento
     * @param requestCode {int} el código de la petición
     * @param resultCode {int} el código del resultado (OK | NOOK);
     * @param data {Intent} el intento que inicia la actividad
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("", "User agreed to make required location settings changes.");
                        break;
                    case Activity.RESULT_CANCELED:
                        this.requestingLocationUpdate = false;
                        break;
                }
                break;
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this, data);
                    this.placeDestination = new Frecuente(String.valueOf(place.getName()), place.getId(),place.getLatLng().latitude, place.getLatLng().longitude, String.valueOf(place.getAddress()));
                    if(contactos.isEmpty()) {
                        Log.e("Destino", "DESTINO GUARDADO");
                        Toast.makeText(MainActivity.this, "Destino guardado", Toast.LENGTH_LONG).show();
                    } else {
                        this.isViajando = true;
                        this.configTrip();
                    }
                }
                break;
        }
    }

    /**
     * Metodo configTrip: establece los valores que toman los TextFields de la vista que se muestra
     * cuando se tiene un viaje en curso.
     * Se le pasa el nombre del lugar placeDestination como parámetro
     */
    private void configTrip() {
        TextView txtDestino = findViewById(R.id.txtDestino);
        TextView txtDistance = findViewById(R.id.txtDistance);
        TextView txtContactos = findViewById(R.id.txtContactos);
        TextView txtWelcome = findViewById(R.id.txtWelcome);
        if(isViajando) {
            findViewById(R.id.btnFindPlace).setVisibility(View.GONE);
            findViewById(R.id.btnCancelarViaje).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutDestino).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutContacto).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutDistancia).setVisibility(View.VISIBLE);
            txtWelcome.setVisibility(View.GONE);
            txtDestino.setVisibility(View.VISIBLE);
            txtDistance.setVisibility(View.VISIBLE);
            txtContactos.setVisibility(View.VISIBLE);
            txtContactos.setText(getContactosText());
            if(placeDestination != null) {
                txtDestino.setText(placeDestination.getNombre());
                Location destination = new Location("Google Place");
                destination.setLatitude(this.placeDestination.getLatitud());
                destination.setLongitude(this.placeDestination.getLongitud());
                txtDistance.setText(String.valueOf(destination.distanceTo(currentLocation)));
                if(!placeDestination.getFrecuente()){
                    findViewById(R.id.btnAddLugarFrecuente).setVisibility(View.VISIBLE);
                }
            }
        } else {
            txtWelcome.setVisibility(View.VISIBLE);
            findViewById(R.id.btnFindPlace).setVisibility(View.VISIBLE);
            findViewById(R.id.btnCancelarViaje).setVisibility(View.GONE);
            findViewById(R.id.btnAddLugarFrecuente).setVisibility(View.GONE);
            findViewById(R.id.layoutDestino).setVisibility(View.GONE);
            findViewById(R.id.layoutDistancia).setVisibility(View.GONE);
            findViewById(R.id.layoutContacto).setVisibility(View.GONE);
        }

    }

    /**
     *
     */
    private String getContactosText(){
        StringBuilder sb = new StringBuilder();
        if(contactos.size() > 3) {
            sb.append(contactos.get(0).getNombre());
            sb.append(", ");
            sb.append(contactos.get(1).getNombre());
            sb.append("...");
        } else {
            for (Contacto c: contactos) {
              sb.append(c.getNombre());
              sb.append(", ");
            }
            if(sb.length() > 4) {
                sb.substring(0, sb.length() - 3);
            }
        }
        return sb.toString();
    }

    /**
     * Metodo onBackPressed:
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Metodo onCreateOptionsMenu:
     * @param menu {Menu}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Metodo onOptionsItemSelected
     * @param item {MenuItem}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            intent.putExtra("user", usuario);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo onNavigationItemSelected
     * @param item {MenuItem}
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(isViajando) {
            Toast.makeText(this, "Don't Text and Drive", Toast.LENGTH_SHORT).show();
        } else {
            int id = item.getItemId();
            Intent intent = null;
            switch (id) {
                case R.id.rutine:
                    intent = new Intent(this, Rutine.class);
                    intent.putExtra("user", usuario);
                    finish();
                    break;
                case R.id.frecuent_place:
                    intent = new Intent(this, LugaresFrecuentes.class);
                    intent.putExtra("user", usuario);
                    finish();
                    break;
                case R.id.add_seres:
                    intent = new Intent(this, AddSerQuerido.class);
                    intent.putExtra("user", usuario);
                    finish();
                    break;
                case R.id.seres:
                    intent = new Intent(this, SeresQueridos.class);
                    intent.putExtra("user", usuario);
                    finish();
                    break;
                case R.id.logout:
                    this.auth.signOut();
                    intent = new Intent(this, Cargando.class);
                    break;
            }
            if (intent != null) {
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Esta funcionalidad no esta Disponible", Toast.LENGTH_SHORT).show();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Metodo permissionConfig: este metodo se encarga de revisar si se pidieron los permisos y si no se tienen pedirlos
     */
    private void permissionConfig() {
        if(!gotPermissions()) {
            requestPermissions();
        }
    }

    /**
     * Metodo gotPermissions: este es un metdo auxiliar que se encarga de revisar que se tengan los permisos
     * Los permisos revisados son los siguiente: Ubicación y Mensaje de Texto
     */
    private boolean gotPermissions() {
        boolean a = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean b = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean c = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        return a && b && c;
    }

    /**
     * Metodo requestPermissions: este es un metodo auxiliar que se encarga de perdir los permisos necesarios
     * Los permisos solicitados son los siguiente: Ubicación y Mensaje de Texto
     */
    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

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

    /**
     * Metodo onRequestPermissionsResult
     * @param requestCode {int}
     * @param permissions {String[]}
     * @param grantResults {int[]}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i("", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (requestingLocationUpdate) {
                    Log.d("Location Permission", "Granted");
                }
            }
        }
    }

    /**
     * Metodo showSnackbar:
     * @param mainTextStringId {int}
     * @param actionStringId {int}
     * @param listener {View.OnClickListener}
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
}

