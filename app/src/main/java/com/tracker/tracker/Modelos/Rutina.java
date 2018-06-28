package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.tracker.tracker.Modelos.fecha.DiasHoras;
import com.tracker.tracker.Modelos.fecha.Hora;
import com.tracker.tracker.SeresQueridos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class Rutina implements Parcelable {

    private String nombre;
    private DiasHoras tiempo;
    private Frecuente destino;
    private ArrayList<Contacto> seresQueridos;
    private String id;
    private boolean isNueva;

    public Rutina(String nombre, Frecuente destino, ArrayList<Contacto> seresQueridos, ArrayList<String> dias, String tiempo, boolean nueva) {
        this.nombre = nombre;
        this.destino = destino;
        this.seresQueridos = seresQueridos;
        //this.tiempo = new DiasHoras(dias, tiempo);
        this.isNueva = nueva;
    }

    private Rutina(Parcel in) {
        this.nombre = in.readString();
        this.destino = in.readParcelable(Frecuente.class.getClassLoader());
        this.seresQueridos = in.createTypedArrayList(Contacto.CREATOR);
        this.tiempo = in.readParcelable(DiasHoras.class.getClassLoader());
        int bool = in.readInt();
        isNueva = bool == 1;
        this.id = in.readString();
    }

    private Rutina() {

    }

    private void setRutina(Rutina r) {
        this.nombre = r.getNombre();
        this.seresQueridos = r.getSeresQueridos();
        this.destino = r.getDestino();
        this.tiempo = r.getTiempo();
        this.isNueva = r.isNueva();
        this.id = r.getId();
    }

    public boolean isNueva() {
        return isNueva;
    }

    public void setNueva(boolean nueva) {
        isNueva = nueva;
    }


    public static Rutina builder(@NonNull final DocumentSnapshot document, Usuario user, FirebaseFirestore db) {
        Usuario usuario = user;
        String nombre = document.getString("nombre");
        ArrayList<String> contactos = (ArrayList<String>) document.get("contactos");
        HashMap<String, Object> destinoO = (HashMap <String, Object>) document.get("destino");
        HashMap<String, Object> tiempo = (HashMap <String, Object>) document.get("tiempo");
        //String destino = document.getString("destinationFId");
        //final String[] seresQueridos = (String[]) document.get("contactos");


        ArrayList<Contacto> rContactosList = new ArrayList<>();
        for (String contacto : contactos) {
            rContactosList.add(usuario.getContactoById(contacto));
        }
        String nombreP = (String) destinoO.get("nombre");
        String placeId = (String) destinoO.get("placeId");
        String direccion = (String) destinoO.get("direccion");
        GeoPoint coordenadas = (GeoPoint) destinoO.get("coordenadas");
        Frecuente f = new Frecuente(nombreP, placeId, coordenadas.getLatitude(), coordenadas.getLongitude(), direccion, false);
        ArrayList<String> dias = (ArrayList<String>) tiempo.get("dias");
        HashMap<String, Object> horaMap = (HashMap<String, Object>) tiempo.get("hora");
        String horas = horaMap.get("hora")+":"+horaMap.get("minutos")+":"+horaMap.get("segundos");
        Rutina rutina = new Rutina(nombre, f, rContactosList, dias, horas, false);
        rutina.setId(document.getId());
        return rutina;

        /*
        DocumentReference destinoRef = db.document("users/"+UID+"/frecuentes/"+destino);
        destinoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult() != null) {
                        ArrayList<Contacto> contactos = new ArrayList<>();
                        if(seresQueridos != null) {
                            for (String s: seresQueridos) {
                                String[] vec = s.split("-");
                                contactos.add(new Contacto(vec[0], vec[1], false));
                            }
                        }
                        DocumentSnapshot documentD = task.getResult();
                        String nombreP = documentD.getString("nombre");
                        String placeId = documentD.getString("placeId");
                        String direccion = documentD.getString("direccion");
                        GeoPoint coordenadas = documentD.getGeoPoint("coordenadas");
                        Frecuente f = new Frecuente(nombreP, placeId, coordenadas.getLatitude(), coordenadas.getLongitude(), direccion, false);
                        ArrayList<String> dias = (ArrayList<String>) tiempo.get("dias");
                        HashMap<String, Object> horaMap = (HashMap<String, Object>) tiempo.get("hora");
                        String horas = horaMap.get("hora")+":"+horaMap.get("minutos")+":"+horaMap.get("segundos");
                        Rutina rutina2 = new Rutina(nombre, f, contactos, dias, horas, false);
                        rutina.setRutina(rutina2);
                    }
                }
            }
        })*/
        //return rutina;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public DiasHoras getTiempo() {
        return tiempo;
    }

    public void setTiempo(DiasHoras tiempo) {
        this.tiempo = tiempo;
    }

    public Frecuente getDestino() {
        return destino;
    }

    public void setDestino(Frecuente destino) {
        this.destino = destino;
    }

    public ArrayList<Contacto> getSeresQueridos() {
        return seresQueridos;
    }

    public void setSeresQueridos(ArrayList<Contacto> seresQueridos) {
        this.seresQueridos = seresQueridos;
    }

    public ArrayList<String> getSeresQueridosName () {
        ArrayList<String> nombres = new ArrayList<>();
        for (Contacto contacto: seresQueridos) {
            nombres.add(contacto.getNombre());
        }
        return nombres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Creator<Rutina> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<Rutina> CREATOR = new Creator<Rutina>() {
        @Override
        public Rutina createFromParcel(Parcel in) {
            return new Rutina(in);
        }

        @Override
        public Rutina[] newArray(int size) {
            return new Rutina[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeParcelable(destino, flags);
        dest.writeTypedList(seresQueridos);
        dest.writeParcelable(tiempo, flags);
        dest.writeString(id);
        if (isNueva) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        /*String[] contactos = new String[this.seresQueridos.size()];
        for (int i = 0; i < this.seresQueridos.size(); i++) {
            contactos[i] = this.seresQueridos.get(i).toStringFirebase();
        }*/
        map.put("nombre", this.nombre);
        map.put("destino", this.destino.toMap());
        ArrayList<String> contacts = new ArrayList<>();
        for(Contacto c: seresQueridos){
            contacts.add(c.getId());
        }
        map.put("contactos", contacts);

        //map.put("tiempo", this.tiempo.toMap());
        map.put("id", this.id);
        return map;
    }
}
