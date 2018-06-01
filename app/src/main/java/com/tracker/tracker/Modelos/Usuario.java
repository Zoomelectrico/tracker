package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.R;
import com.tracker.tracker.tareas.ProfilePicture;
import com.tracker.tracker.tareas.SaveUserData;

import java.util.ArrayList;

public class Usuario implements Parcelable {
    private String nombre;
    private String email;
    private String photo;
    private String UID;
    private ArrayList<Contacto> contactos;

    public Usuario(String nombre, String email, String photo, String UID) {
        this.nombre = nombre;
        this.email = email;
        this.photo = photo;
        this.UID = UID;
        this.contactos = new ArrayList<>();
    }

    public Usuario(Parcel in) {
       this.nombre = in.readString();
       this.email = in.readString();
       this.photo = in.readString();
       this.UID = in.readString();
       this.contactos = new ArrayList<>();
       in.readTypedList(contactos, Contacto.CREATOR);
    }

    public Usuario() {
        this.nombre = "";
        this.email = "";
        this.photo = "";
        this.UID = "";
        this.contactos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUID() {
        return UID;
    }

    public boolean haveContactos(){
        return ! this.contactos.isEmpty();
    }

    public ArrayList<Contacto> getContactos() {
        return contactos;
    }

    /**
     * Método getContacto:
     * @param i el indice en que se encuentra el contacto
     * @return el i-esimo contacto de la lista
     */
    public Contacto getContacto(int i) {
        return this.contactos.get(i);
    }

    public void addContacto(Contacto c) {
        this.contactos.add(c);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.email);
        dest.writeString(this.photo);
        dest.writeString(this.UID);
        dest.writeTypedList(contactos);
    }

    public static final Parcelable.Creator<Usuario> CREATOR
            = new Parcelable.Creator<Usuario>() {
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void saveData(FirebaseFirestore db) {
        new SaveUserData(db).execute(this);
    }

    public void imageConfig(View header) {
        new ProfilePicture((ImageView) header.findViewById(R.id.imgProfilePhoto)).execute(this.photo);
    }

    @Override
    public String toString() {
        return this.nombre + " " + this.UID;
    }
}
