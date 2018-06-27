package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.tracker.tracker.Modelos.fecha.DiasHoras;
import com.tracker.tracker.Modelos.fecha.Hora;

import java.util.ArrayList;
import java.util.HashMap;

public class Rutina implements Parcelable {

    private String nombre;
    private DiasHoras tiempo;
    private Frecuente destino;
    private ArrayList<Contacto> seresQueridos;
    private boolean isNueva;

    public Rutina(String nombre, Frecuente destino, ArrayList<Contacto> seresQueridos, ArrayList<String> dias, String tiempo, boolean nueva) {
        this.nombre = nombre;
        this.destino = destino;
        this.seresQueridos = seresQueridos;
        this.tiempo = new DiasHoras(dias, tiempo);
        this.isNueva = nueva;
    }

    private Rutina(Parcel in) {
        this.nombre = in.readString();
        this.destino = in.readParcelable(Frecuente.class.getClassLoader());
        this.seresQueridos = in.createTypedArrayList(Contacto.CREATOR);
        this.tiempo = in.readParcelable(DiasHoras.class.getClassLoader());
        int bool = in.readInt();
        isNueva = bool == 1;

    }

    public boolean isNueva() {
        return isNueva;
    }

    public void setNueva(boolean nueva) {
        isNueva = nueva;
    }

    public static Rutina builder(DocumentSnapshot document) {
        String nombre = document.getString("nombre");
        // DiasHoras diasHoras = new DiasHoras(document.getDocumentReference("tiempo"));
        Rutina rutina = null; // = new Rutina();
        return rutina;
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
        if (isNueva) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> frecuentes = new HashMap<>();
        frecuentes.put("nombre", this.nombre);
        frecuentes.put("destino", this.destino.toMap());
        frecuentes.put("SeresQueridos", this.seresQueridos.toArray());
        return frecuentes;
    }
}
