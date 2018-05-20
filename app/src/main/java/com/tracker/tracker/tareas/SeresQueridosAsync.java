package com.tracker.tracker.tareas;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.MainActivity;
import com.tracker.tracker.dummy.DummyContent;
import com.tracker.tracker.seresQueridos;

import java.util.List;

import static android.content.ContentValues.TAG;
import static android.support.v4.content.ContextCompat.startActivity;

public class SeresQueridosAsync extends AsyncTask<FirebaseUser, Void, DocumentSnapshot> {

    private FirebaseFirestore db;

    public SeresQueridosAsync(){
        this.db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        this.db.setFirestoreSettings(settings);
    }

    @Override
    protected DocumentSnapshot doInBackground(FirebaseUser... users) {
        FirebaseUser user = users[0];
        if (user != null) {
            CollectionReference contactos = db.collection("users/" + user.getUid() + "/contactos");
            contactos.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.e(TAG, document.getId() + " => " + document.getString("nombre"));
                            DummyContent.DummyItem item = new DummyContent.DummyItem(document.getString("nombre"), document.getString("telf"), "1");
                            Log.e(TAG, "el id es: " + item.id );
                            DummyContent.addItem(item);
                            Log.e(TAG, "2. el id es: " + item.id );
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }
        return null;
    }


}
