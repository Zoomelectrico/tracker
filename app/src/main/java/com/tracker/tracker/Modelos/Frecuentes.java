package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Frecuentes implements Parcelable {

    private String nombre;
    private String coordenadas;
    private String direccion;

    public Frecuentes (String Nombre, String Coordenadas, String Direccion){
        this.nombre = Nombre;
        this.coordenadas = Coordenadas;
        this.direccion = Direccion;
    }

    protected Frecuentes(Parcel in) {
        this.nombre = in.readString();
        this.coordenadas = in.readString();
        this.direccion = in.readString();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public static final Creator<Frecuentes> CREATOR = new Creator<Frecuentes>() {
        @Override
        public Frecuentes createFromParcel(Parcel in) {
            return new Frecuentes(in);
        }

        @Override
        public Frecuentes[] newArray(int size) {
            return new Frecuentes[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.coordenadas);
        dest.writeString(this.direccion);
    }
}
