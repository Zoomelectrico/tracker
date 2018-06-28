package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tracker.tracker.tareas.ProfilePicture;
import com.tracker.tracker.tareas.SaveUserData;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Clase Usuario: esta es la clase usuario, que modela al objeto Usuario en la DB
 */
public class Usuario implements Parcelable {
    private String nombre;
    private String email;
    private String photo;
    private String UID;
    private ArrayList<Contacto> contactos;
    private ArrayList<Frecuente> frecuentes;
    private ArrayList<Rutina> rutinas;

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
        this.frecuentes = new ArrayList<>();
        this.rutinas = new ArrayList<>();
    }

    /**
     * Constructor de la clase:
     * @param in {Parcel}
     */
    private Usuario(Parcel in) {
       this.nombre = in.readString();
       this.email = in.readString();
       this.photo = in.readString();
       this.UID = in.readString();
       this.contactos = new ArrayList<>();
       this.frecuentes = new ArrayList<>();
       this.rutinas = new ArrayList<>();
       in.readTypedList(contactos, Contacto.CREATOR);
       in.readTypedList(frecuentes, Frecuente.CREATOR);
       in.readTypedList(rutinas, Rutina.CREATOR);
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
        this.frecuentes = new ArrayList<>();
        this.rutinas = new ArrayList<>();
    }

    public static void build(@NonNull DocumentSnapshot document, Usuario u) {
        String nombre = document.getString("nombre");
        String email = document.getString("email");
        String photo = document.getString("photo");
        String UID = document.getString("UID");
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPhoto(photo);
        u.setUID(UID);
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

    public void setContactos(ArrayList<Contacto> contactos) {
        this.contactos = contactos;
    }

    /**
     * Método getContacto:
     * @param i el indice en que se encuentra el contacto
     * @return el i-esimo contacto de la lista
     */
    public Contacto getContacto(int i) {
        return this.contactos.get(i);
    }

    public Contacto getContactoById(String id){
        Contacto contact = null;
        for(Contacto c: this.contactos){
            if (c.getId().equals(id)) {
                contact = c;
            }
        }
        return contact;
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
     * @param nombre {String}
     * @param telf {String}
     */
    public void eliminarContacto(@NonNull String nombre, @NonNull String telf){
        for (Contacto c: contactos) {
            if(nombre.equals(c.getNombre()) && telf.equals(c.getTelf())) {
                contactos.remove(c);
                break;
            }
        }
    }

    /**
     * Método getFrecuentes devuelve la lista de lugares frecuentes que tiene el usuario
     * Se usa para poder mostrar la lista de lugares frecuentes en el fragment list
     * @return
     */
    public ArrayList<Frecuente> getFrecuentes() {
        return frecuentes;
    }

    /**
     * Metodo getFrecuente: devuelve un lugar frecuente ubicado en la posicion que se pasa por parámetro
     * @param posicion
     * @return
     */
    public Frecuente getFrecuente(int posicion){
        return this.frecuentes.get(posicion);
    }

    /**
     * getFrecuenteById permite obtener un LugarFrecuente del usuario mediante el id de
     * Firebase
     * @param id
     * @return
     */
    public Frecuente getFrecuenteById(String id){
        Frecuente frecuent = null;
        for(Frecuente f: this.getFrecuentes()){
            if (f.getId().equals(id)) {
                frecuent = f;
                break;
            }
        }
        return frecuent;
    }

    /**
     * modifyFrecuenteById: permite modificar los lugares frecuentes por el Id de firebase,
     * de esta forma es más fácil realizar los cambios en el dispositivo sin tener que
     * requerir de consumo de datos.
     * @param id
     * @param frecuente
     */
    public void modifyFrecuenteById(String id, Frecuente frecuente){
        int i = 0;
        for(Frecuente f: this.getFrecuentes()){
            if (f.getId().equals(id)) {
                frecuentes.set(i, frecuente);
                break;
            }
            i++;
        }
    }

    /**
     * deleteFrecuentesById, permite eliminar un LugarFrecuente de la lista con el
     * id de firebase.
     * @param id
     */
    public void deleteFrecuenteById(String id){
        int i = 0;
        for(Frecuente f: this.getFrecuentes()){
            if (f.getId().equals(id)) {
                frecuentes.remove(i);
                break;
            }
            i++;
        }
    }

    /**
     * Metodo haveFrecuentes: permite saber si la lista esta vacia
     * @return
     */
    public boolean haveFrecuentes(){ return ! this.frecuentes.isEmpty();}

    /**
     * Metodo setFrecuentes: establece una lista de lugares frecuentes al usuario.
     * @param frecuentes
     */
    public void setFrecuentes(ArrayList<Frecuente> frecuentes) {
        this.frecuentes = frecuentes;
    }

    /**
     * metodo addFrecuntes: agrega un lugar frecuente al final de la lista
     * @param f
     */
    public void addFrecuentes(Frecuente f){
        this.frecuentes.add(f);
    }

    /**
     * Método haveRutinas
     * @return true si la persona tiene viaje rutinarios o false en caso contrario
     * */
    public boolean haveRutinas() {
        return !(this.rutinas.isEmpty());
    }

    /**
     * metodo getRutinas: permite obtener la lista de rutinas del usuario
     * @return
     */
    public ArrayList<Rutina> getRutinas() {
        return rutinas;
    }

    /**
     * Metodo setRutinas: determina una lista de rutinas
     * @param rutinas
     */
    public void setRutinas(ArrayList<Rutina> rutinas) {
        this.rutinas = rutinas;
    }

    /**
     * Método addRutina
     * @param rutina {Rutina}
     */
    public void addRutina(Rutina rutina){
        this.rutinas.add(rutina);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.email);
        dest.writeString(this.photo);
        dest.writeString(this.UID);
        dest.writeTypedList(contactos);
        dest.writeTypedList(frecuentes);
        dest.writeTypedList(rutinas);
    }

    public static final Parcelable.Creator<Usuario> CREATOR
            = new Parcelable.Creator<Usuario>() {
        public Usuario createFromParcel(@NonNull Parcel in) {
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
    public void saveData(@NonNull FirebaseFirestore db) {
        new SaveUserData(db).execute(this);
    }

    /**
     * Método imageConfig:
     * @param imageView {ImageView}
     */
    public void imageConfig(ImageView imageView) {
        new ProfilePicture(imageView).execute(this.photo);
    }

    @NonNull
    @Override
    public String toString() {
        return this.nombre + " " + this.UID;
    }
}
