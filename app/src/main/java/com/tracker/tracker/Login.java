package com.tracker.tracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.tracker.tracker.tareas.UserData;


public class Login extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    // Constantes
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int MY_LOCATION_PERMISSION = 0;
    // Referencias a los servicios de Firebase
    private FirebaseAuth auth;
    private GoogleSignInClient googleSIClient;
    // Referencias para lograr la ubicación
    private FusedLocationProviderClient locationProviderClient;
    private Location currentLocation;
    private LocationManager locationManager;
    private boolean gps;
    // UI
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inicio la UI
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Agrego al event Listener al Boton
        this.btnLogin = (Button) findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(this);

        //Login with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        this.googleSIClient = GoogleSignIn.getClient(this, gso);
        this.auth = FirebaseAuth.getInstance();

        // GPS
        // Verificar GPS
        this.locationManager = (LocationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().LOCATION_SERVICE);
        gps = locationManager.isProviderEnabled(this.getApplicationContext().LOCATION_SERVICE);


        // Location
        this.getLocation();
    }

    /**
     * Método getLocation(): Este metodo verifica los permisos para obtener la Ubicación.
     * En caso de no tenerlos lo Pide.
     * Luego busca la locación de la persona y la va actualizando
     */
    public void getLocation() {
        Log.e("GPS|LOGIN", String.valueOf(gps));
        if(gps) {
            this.locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION);
            } else {
                // Permisos Finos
            }

            this.locationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                currentLocation = location;
                            } else {
                                Log.e("LOCATION|LOGIN", "NULL");
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Debes activar el GPS", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.getLocation();
                    Log.d("Permissions", "ALL GOD");
                } else {
                    Log.e("Permissions", "Permisos Fallaron");
                }
                return;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verificamos que el resultado de la actividad este relacionado al IS
        if (requestCode == RC_SIGN_IN) {
            // Creamos una tarea para
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Vamos a hacer la auth con Firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "Google sign in failed", e);
            }
        }
    }

    /**
     * Metodo firebaseAuthWithGoogle(): Este método se entiende con la API de firebase
     * para hacer login en su plataforma.
     * */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // Credenciales de Google para hacer el login en firebase
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        // Empieza la autenticación
        this.auth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Cuando completa la tarea del login pregunto por su resultado
                        if (task.isSuccessful()) {
                            // Como fue exitosa ya puedo obtener el usuario desde firebase
                            FirebaseUser user = auth.getCurrentUser();
                            // Pregunto si la location is null
                            if(currentLocation == null) {
                                Log.e("LOCATION|LOGIN", "NULL");
                                new UserData(null).execute(user);
                            } else {
                                new UserData(currentLocation).execute(user);
                            }
                            // Listo mi Login, creo un intento para ir al main activity
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Mato la actividad para evitar malgasto de recursos
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void signIn() {
        // Intento de Login
        Intent signInIntent = this.googleSIClient.getSignInIntent();
        // Comienza la actividad para obtener un resultado
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Listener del boton
    @Override
    public void onClick(View v) {
        this.signIn();
    }

}
