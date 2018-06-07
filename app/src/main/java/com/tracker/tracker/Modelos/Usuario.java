package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tracker.tracker.R;
import com.tracker.tracker.tareas.ProfilePicture;
import com.tracker.tracker.tareas.SaveUserData;

import java.util.ArrayList;
/**
 * Clase Usuario: esta es la clase usuario, que modela al objeto Usuario en la DB
 */
public class Usuario implements Parcelable {
    private String nombre;
    private String email;
    private String photo;
    private String UID;
    private ArrayList<Contacto> contactos;

    /**
     * Constructor de la Clase
     * @param nombre {String}
     * @param email {String}
     * @param photo {String}
     * @param UID {String}
     */
    public Usuario(String nombre, String email, String photo, String UID) {
        this.nombre = nombre;
        this.email = email;
        this.photo = photo;
        this.UID = UID;
        this.contactos = new ArrayList<>();
    }

    /**
     * Constructor de la clase:
     * @param in {Parcel}
     */
    public Usuario(Parcel in) {
       this.nombre = in.readString();
       this.email = in.readString();
       this.photo = in.readString();
       this.UID = in.readString();
       this.contactos = new ArrayList<>();
       in.readTypedList(contactos, Contacto.CREATOR);
    }
    /**
     * Constructor de la Clase:
     */
    public Usuario() {
        this.nombre = "";
        this.email = "";
        this.photo = "";
        this.UID = "";
        this.contactos = new ArrayList<>();
    }

    /**
     * getNombre:
     * @return nombre {String}
     * */
    public String getNombre() {
        return nombre;
    }

    /**
     * getPhoto:
     * @return photo {String}
     * */
    public String getPhoto() {
        return photo;
    }

    /**
     * getUID:
     * @return UID {String}
     */
    public String getUID() {
        return UID;
    }

    /**
     * getEmail:
     * @return Email {String}
     */
    public String getEmail() {
        return email;
    }

    /**
     * setEmail:
     * @param email {String}
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * setEmail:
     * @param nombre {String}
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * setEmail:
     * @param photo {String}
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * setEmail:
     * @param UID {String}
     */
    public void setUID(String UID) {
        this.UID = UID;
    }


    /**
     * haveContactos:
     * @return true si tiene contactos, false en caso contrario {boolean}
     * */
    public boolean haveContactos(){
        return ! this.contactos.isEmpty();
    }

    /**
     * getContactos:
     * @return contactos {ArrayList}
     * */
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

    /**
     * Método addContacto: Añade un contacto a la lista de contactos.
     * @param c {Contacto}
     * */
    public void addContacto(Contacto c) {
        this.contactos.add(c);
    }

    /**
     * modificarContacto: permite modificar la información de un serQuerido,
     * el método va a la posición del contacto y modifica los datos segun los parámetros
     * nombre y telf.
     * @param position
     * @param nombre
     * @param telf
     */
    public void modificarContacto(int position, String nombre, String telf){
        this.contactos.get(position).setNombre(nombre);
        this.contactos.get(position).setTelf(telf);
    }

    /**
     * eliminarContacto: elimina un contacto de la lista de contactos según la posición que se
     * especifique.
     * @param position
     */
    public void eliminarContacto(int position){
        this.contactos.remove(position);
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
    /**
     * Método saveData:
     * @param db {FirebaseFirestore}
     */
    public void saveData(FirebaseFirestore db) {
        new SaveUserData(db).execute(this);
    }

    /**
     * Método imageConfig:
     * @param header {View}
     */
    public void imageConfig(View header) {
        new ProfilePicture((ImageView) header.findViewById(R.id.imgProfilePhoto)).execute(this.photo);
    }

    @Override
    public String toString() {
        return this.nombre + " " + this.UID;
    }
}
