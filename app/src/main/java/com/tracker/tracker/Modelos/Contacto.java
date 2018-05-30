package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.HashMap;

public class Contacto implements Parcelable {

    private String nombre;
    private String telf;
    private boolean nuevo;

    public Contacto(String nombre, String telf, boolean nuevo) {
        this.nombre = nombre;
        this.telf = telf;
        this.nuevo = nuevo;
    }

    private Contacto(@NonNull Parcel in) {
        this.nombre = in.readString();
        this.telf = in.readString();
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelf() {
        return telf;
    }

    public boolean isNuevo() { return this.nuevo; }

    @Override
    public String toString() {
        return this.nombre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.telf);
    }

    public static final Parcelable.Creator<Contacto> CREATOR
            = new Parcelable.Creator<Contacto>() {
        public Contacto createFromParcel(Parcel in) {
            return new Contacto(in);
        }

        public Contacto[] newArray(int size) {
            return new Contacto[size];
        }
    };

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> contacto = new HashMap<>();
        contacto.put("nombre", this.nombre);
        contacto.put("telf", this.telf);
        return contacto;
    }

}
