package com.tracker.tracker.tareas;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;

public class AddSerQueridoAsync extends AsyncTask<FirebaseUser, Void, DocumentSnapshot> {

    private String nombre;
    private String telf;
    private FirebaseFirestore db;

    public AddSerQueridoAsync(String nombre, String telf) {
        this.nombre = nombre;
        this.telf = telf;
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);
    }

    @Override
    protected DocumentSnapshot doInBackground(FirebaseUser... users) {
        FirebaseUser user = users[0];

        if(user != null) {
            DocumentReference dbUser = db.collection("users").document(user.getUid());
            final DocumentSnapshot[] document = new DocumentSnapshot[1];
            dbUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        document[0] = task.getResult();
                        if(document[0] != null) {
                            DocumentSnapshot user = document[0];
                            CollectionReference contactos = db.collection("users/"+user.get("UID")+"/contactos");
                            HashMap<String, String> ser = new HashMap<>();
                            ser.put("nombre", nombre);
                            ser.put("telf", telf);
                            contactos.add(ser);
                        } else {
                            Log.e("EL PEO", "DOCUMENT NULL");
                        }
                    } else {
                        Log.e("Getting User DB:", task.getException().getMessage());
                    }
                }
            });
            return document[0];
        } else {
            Log.e("FIREBASEUSER", "NULL");
            return null;
        }
    }

    @Override
    protected void onPostExecute(DocumentSnapshot user) { }

    @Override
    protected void onPreExecute() { }

}
