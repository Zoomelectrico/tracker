package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Rutina implements Parcelable {

    private String nombre;
    private Frecuente destino;
    private ArrayList<Contacto> seresQueridos;
    private ArrayList<String> dias; // lu,ma,mi,ju,vi,sa,do
    private String hora; //HH:mm:ss
    private String id;
    private boolean isNueva;


    public Rutina(String nombre, Frecuente destino, ArrayList<Contacto> seresQueridos, ArrayList<String> dias, String hora, boolean nueva) {
        this.nombre = nombre;
        this.destino = destino;
        this.seresQueridos = seresQueridos;
        this.dias = dias;
        this.hora = hora;
        this.isNueva = nueva;
    }

    private Rutina(Parcel in) {
        this.nombre = in.readString();
        this.destino = in.readParcelable(Frecuente.class.getClassLoader());
        this.seresQueridos = in.createTypedArrayList(Contacto.CREATOR);
        this.id = in.readString();
        this.hora = in.readString();
        this.dias = new ArrayList<>();
        in.readStringList(this.dias);
        this.isNueva = in.readInt() == 1;
    }

    private Rutina() {

    }

    public static Rutina builder(@NonNull final DocumentSnapshot document, @NonNull Usuario u) {
        String nombre = document.getString("nombre");
        GeoPoint coordenadas = document.getGeoPoint("destino.coordenadas");
        Frecuente f = new Frecuente(document.getString("destino.nombre"), document.getString("destino.placeId"), Objects.requireNonNull(coordenadas).getLatitude(), coordenadas.getLongitude(), document.getString("destino.direccion"),false);
        Object objContacto = document.get("contactos");
        ArrayList<Contacto> sq = new ArrayList<>();
        if (objContacto instanceof ArrayList) {
            ArrayList<String> contactosVec = (ArrayList<String>) document.get("contactos");
            if(contactosVec != null) {
                for (String s: contactosVec) {
                    sq.add(u.getContactoById(s));
                }
            }
        }
        Object objDias = document.get("tiempo.dias");
        ArrayList<String> dias = new ArrayList<>();
        if(objDias instanceof ArrayList) {
            dias = (ArrayList<String>) document.get("tiempo.dias");

        }
        String hora = document.getString("tiempo.hora.hora") + ":" + document.getString("tiempo.hora.minutos") + ":" + document.getString("tiempo.hora.segundos");
        return new Rutina(nombre, f, sq, dias, hora, false);
    }

    @NonNull
    public ArrayList<String> getSeresQueridosName () {
        ArrayList<String> nombres = new ArrayList<>();
        for (Contacto contacto: seresQueridos) {
            nombres.add(contacto.getNombre());
        }
        return nombres;
    }

    @NonNull
    public static Creator<Rutina> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<Rutina> CREATOR = new Creator<Rutina>() {
        @Override
        public Rutina createFromParcel(@NonNull Parcel in) {
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
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeParcelable(destino, flags);
        dest.writeTypedList(seresQueridos);
        dest.writeString(id);
        dest.writeString(hora);
        dest.writeStringList(this.dias);
        if (isNueva) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public ArrayList<String> getDias() {
        return dias;
    }

    public void setDias(ArrayList<String> dias) {
        this.dias = dias;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNueva() {
        return isNueva;
    }

    public void setNueva(boolean nueva) {
        isNueva = nueva;
    }

    @NonNull
    public HashMap<String, Object> horaToMap() {
        HashMap<String, Object> map = new HashMap<>();
        String[] horaVec = hora.split(":");
        map.put("hora", horaVec[0]);
        map.put("minutos", horaVec[1]);
        map.put("segundos", horaVec[2]);
        return map;
    }

    @NonNull
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> contacts = new ArrayList<>();
        for(Contacto c: seresQueridos){
            contacts.add(c.getId());
        }
        map.put("contactos", contacts.toArray());
        map.put("destino", this.destino.toMap());
        map.put("id", this.id);
        map.put("nombre", this.nombre);
        HashMap<String, Object> tiempo = new HashMap<>();
        tiempo.put("dias", this.dias.toArray());
        tiempo.put("hora", this.horaToMap());
        map.put("tiempo", tiempo);
        return map;
    }
}
