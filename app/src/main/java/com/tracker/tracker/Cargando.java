package com.tracker.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Usuario;

/**
 * Clase Cargando esta clase se encarga se cargar los datos de la app al inicio
 */
public class Cargando extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    /**
     * Método onCrate:
     * @param savedInstanceState {Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargando);

        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);

        if(user != null) {
            final String UID = this.user.getUid();
            final Usuario usuario = new Usuario();
            final DocumentReference user = db.document("users/"+UID);
            final CollectionReference contactos = db.collection("users/"+UID+"/contactos");
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
                                            Intent intent = new Intent(Cargando.this, MainActivity.class);
                                            intent.putExtra("user", usuario);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            this.finish();
        }
    }

}
