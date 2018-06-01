package com.tracker.tracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Usuario;

/**
 *
 */
public class Login extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    // Constantes
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSIClient;
    private Button btnLogin;

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inicio la UI
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Agrego al event Listener al Boton
        this.btnLogin = findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(this);

        //Login with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        this.googleSIClient = GoogleSignIn.getClient(this, gso);
        this.auth = FirebaseAuth.getInstance();

        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);

    }

    /**
     *
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
                            FirebaseUser user = auth.getCurrentUser();
                            final Usuario usuario = new Usuario();
                            if(user != null) {
                                usuario.setNombre(user.getDisplayName());
                                usuario.setEmail(user.getEmail());
                                usuario.setPhoto(user.getPhotoUrl().toString());
                                usuario.setUID(user.getUid());
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
                                            Intent intent = new Intent(Login.this, MainActivity.class);
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
     *
     */
    private void signIn() {
        Intent signInIntent = this.googleSIClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     *
     */
    @Override
    public void onClick(View v) {
        this.signIn();
    }
}
