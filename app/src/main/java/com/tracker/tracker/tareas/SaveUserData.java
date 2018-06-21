package com.tracker.tracker.tareas;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tracker.tracker.Modelos.Contacto;
import com.tracker.tracker.Modelos.Usuario;

import java.util.HashMap;

public class SaveUserData extends AsyncTask<Usuario, Void, Void> {

    private FirebaseFirestore db;

    public SaveUserData(@NonNull FirebaseFirestore db) {
        this.db = db;
    }

    @Nullable
    @Override
    protected Void doInBackground(@NonNull Usuario... usuarios) {
        final Usuario u = usuarios[0];
        DocumentReference uRef =  this.db.document("users/"+u.getUID());
        HashMap<String, Object> user = new HashMap<>();
        user.put("UID", u.getUID());
        user.put("nombre", u.getNombre());
        user.put("email", u.getEmail());
        user.put("photo", u.getPhoto());
        uRef.set(user);
        if(u.haveContactos()) {
            for (Contacto c: u.getContactos()) {
                if(c.isNuevo()) {
                    db.collection("users/"+u.getUID()+"/contactos").add(c.toMap());
                }
            }
        }
        return null;
    }
}
