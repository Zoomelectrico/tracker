package com.tracker.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Frecuente;
import com.tracker.tracker.Modelos.Usuario;

/**
 * Clase Cargando esta clase se encarga se cargar los datos de la app al inicio
 */
public class Cargando extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth auth;
    @Nullable
    private FirebaseUser user;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSIClient;
    private Button btnLogin;

    /**
     * Método onCreate:
     * @param savedInstanceState {Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargando);

        this.googleConfig();

        if(user != null) {
            loadUserData();
        } else {
            this.btnLogin = findViewById(R.id.btnLogin);
            this.btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
            findViewById(R.id.pbLoading).setVisibility(View.GONE);
            this.btnLogin.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Método googleConfig: este método se encarga de configurar los objetos necesarios
     * para hacer el login con google, para comunicarse con la base de datos y
     * con el módulo de autenticación de firebase.
     */
    private void googleConfig() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        this.googleSIClient = GoogleSignIn.getClient(this, gso);

        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);
    }

    /**
     * Método loadUserData: este método se encargar de ir a la base de datos en firebase y
     * obtener los datos de un usuario en caso de que el mismo ya este registrado en nuestra plataforma
     */
    private void loadUserData() {
        final String UID = this.user.getUid();
        final Usuario usuario = new Usuario();
        final DocumentReference user = db.document("users/"+UID);
        final CollectionReference contactos = db.collection("users/"+UID+"/contactos");
        final CollectionReference frecuentes = db.collection("users/"+UID+"/frecuentes");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if(document != null) {
                        usuario.setNombre(document.getString("nombre"));
                        usuario.setEmail(document.getString("email"));
                        usuario.setPhoto(document.getString("photo"));
                        usuario.setUID(document.getString("UID"));
                        contactos.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    if(task.getResult() != null) {
                                        for (DocumentSnapshot documentC: task.getResult()) {
                                            Contacto serQuerido = new Contacto(documentC.getString("nombre"), documentC.getString("telf"), false);
                                            serQuerido.setId(documentC.getId());
                                            usuario.addContacto(serQuerido);
                                        }
                                        frecuentes.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    for (DocumentSnapshot documentF : task.getResult()) {
                                                        Frecuente frecuente = new Frecuente(documentF.getString("nombre"), documentF.getGeoPoint("coordenadas").getLatitude(), documentF.getGeoPoint("coordenadas").getLongitude(), documentF.getString("direccion"));
                                                        usuario.addFrecuentes(frecuente);
                                                    }
                                                    Intent intent = new Intent(Cargando.this, MainActivity.class);
                                                    intent.putExtra("user", usuario);
                                                    startActivity(intent);
                                                    finish();
                                                    Toast.makeText(getApplicationContext(), "Se cargaron los lugares frecuentes", Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "No se cargaron los lugares frecuentes", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    /**
     * Método onActivityResult:
     * @param requestCode {int}
     * @param resultCode {int}
     * @param data {Intent}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e(TAG, "Google sign in failed", e);
            }
        }
    }

    /**
     * Metodo firebaseAuthWithGoogle(): Este método se entiende con la API de firebase para hacer login en su plataforma.
     * Una vez que se ha hecho el login se guardan los datos del usuario en la db
     * @param acct {GoogleSignInAccount} acct _ SE
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        this.auth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            btnLogin.setVisibility(View.GONE);
                            findViewById(R.id.pbLoading).setVisibility(View.VISIBLE);
                            FirebaseUser user = auth.getCurrentUser();
                            final Usuario usuario = new Usuario();
                            if(user != null) {
                                usuario.setNombre(user.getDisplayName());
                                usuario.setEmail(user.getEmail());
                                usuario.setUID(user.getUid());
                                if(user.getPhotoUrl() != null) {
                                    usuario.setPhoto(user.getPhotoUrl().toString());
                                }
                                usuario.saveData(db);
                            }
                            final CollectionReference contactos = db.collection("users/"+user.getUid()+"/contactos");
                            contactos.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        if(task.getResult() != null) {
                                            for (DocumentSnapshot documentC: task.getResult()) {
                                                usuario.addContacto(new Contacto(documentC.getString("nombre"), documentC.getString("telf"), false));
                                            }
                                            Intent intent = new Intent(Cargando.this, MainActivity.class);
                                            intent.putExtra("user", usuario);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    /**
     * Método signIn: este método se encarga de crear el intento de autenticación con Firebae
     */
    private void signIn() {
        Intent signInIntent = this.googleSIClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
