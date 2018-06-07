package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.HashMap;

public class Contacto implements Parcelable {

    private String nombre;
    private String telf;
    private String id;
    private int position;
    private boolean nuevo;

    public Contacto(String nombre, String telf, boolean nuevo) {
        this.nombre = nombre;
        this.telf = telf;
        this.nuevo = nuevo;
    }

    private Contacto(@NonNull Parcel in) {
        this.nombre = in.readString();
        this.telf = in.readString();
        this.id = in.readString();
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelf() {
        return telf;
    }

    public String getId(){ return id; }

    public void setId(String id){this.id = id;}

    public void setPosition(Integer position){this.position = position;}

    public void setNombre(String nombre){this.nombre = nombre;}

    public void setTelf(String telf){this.telf = telf;}

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
        dest.writeString(this.id);
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
        contacto.put("id", this.id);
        return contacto;
    }

}
