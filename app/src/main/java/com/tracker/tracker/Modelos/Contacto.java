package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Contacto implements Parcelable {

    private String nombre;
    private String telf;

    public Contacto(String nombre, String telf) {
        this.nombre = nombre;
        this.telf = telf;
    }

    public  Contacto(Parcel in) {
        this.nombre = in.readString();
        this.telf = in.readString();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelf() {
        return telf;
    }

    public void setTelf(String telf) {
        this.telf = telf;
    }

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
