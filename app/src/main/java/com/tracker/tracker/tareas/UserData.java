package com.tracker.tracker.tareas;

import android.location.Location;
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
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class UserData extends AsyncTask<FirebaseUser, Integer, DocumentSnapshot> {

    private FirebaseUser user;
    private FirebaseFirestore db;
    private Location currentLocation;

    public UserData(Location location) {
        this.currentLocation = location;
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);
    }

    @Override
    protected DocumentSnapshot doInBackground(FirebaseUser... users) {
        this.user = users[0];

        DocumentReference dbUser = db.collection("users").document(user.getUid());
        final DocumentSnapshot[] document = new DocumentSnapshot[1];
        dbUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    document[0] = task.getResult();
                } else {
                    Log.e("Getting User DB:", task.getException().getMessage());
                }
            }
        });
        return document[0];
    }

    @Override
    protected void onPostExecute(DocumentSnapshot user) {
        CollectionReference usersRef = this.db.collection("users");
        Map<String, Object> u = new HashMap<>();
        u.put("nombre",this.user.getDisplayName());
        u.put("email", this.user.getEmail());
        u.put("photo", this.user.getPhotoUrl().toString());
        u.put("UID", this.user.getUid());
        if(currentLocation == null) {
            Log.e("LOCATION|USERDATA", "LOCATION IS NULL");
        } else {
            u.put("UbicacionActual", new GeoPoint(this.currentLocation.getLatitude(),this.currentLocation.getLongitude()));
            Log.e("LOCATION|USERDATA", "ALL GOOD");
        }
        usersRef.document(this.user.getUid()).set(u);
    }

    @Override
    protected void onPreExecute() { }

    @Override
    protected void onProgressUpdate(Integer... values) { }

}
