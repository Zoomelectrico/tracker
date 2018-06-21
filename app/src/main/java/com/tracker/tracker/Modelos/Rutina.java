package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class Rutina implements Parcelable {

    private String rNombre;
    private Frecuente rDestino;
    private ArrayList<Contacto> rSeresQueridos;

    public Rutina(String rNombre, Frecuente rDestino, ArrayList<Contacto> rSeresQueridos) {
        this.rNombre = rNombre;
        this.rDestino = rDestino;
        this.rSeresQueridos = rSeresQueridos;
    }

    private Rutina(Parcel in) {
        rNombre = in.readString();
        rDestino = in.readParcelable(Frecuente.class.getClassLoader());
        rSeresQueridos = in.createTypedArrayList(Contacto.CREATOR);
    }

    public String getrNombre() {
        return rNombre;
    }

    public void setrNombre(String rNombre) {
        this.rNombre = rNombre;
    }

    public Frecuente getrDestino() {
        return rDestino;
    }

    public void setrDestino(Frecuente rDestino) {
        this.rDestino = rDestino;
    }

    public ArrayList<Contacto> getrSeresQueridos() {
        return rSeresQueridos;
    }

    public ArrayList<String> getrSeresQueridosName() {
        Log.e(TAG, "Entra a getrSeFWS");
        ArrayList<String> seresQueridos = new ArrayList<>();
        for(Contacto c : this.rSeresQueridos){
            Log.e(TAG, "EL nombre del ser querido es: " + c.getNombre() );
            seresQueridos.add(c.getNombre());
        }
        return seresQueridos;
    }

    public void setrSeresQueridos(ArrayList<Contacto> rSeresQueridos) {
        this.rSeresQueridos = rSeresQueridos;
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
        dest.writeString(rNombre);
        dest.writeParcelable(rDestino, flags);
        dest.writeTypedList(rSeresQueridos);
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> frecuentes = new HashMap<>();
        frecuentes.put("nombre", this.rNombre);
        frecuentes.put("destino", this.rDestino);
        frecuentes.put("SeresQueridos", this.rSeresQueridos);
        return frecuentes;
    }
}
