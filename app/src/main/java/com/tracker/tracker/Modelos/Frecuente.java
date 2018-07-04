package com.tracker.tracker.Modelos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Objects;

public class Frecuente implements Parcelable {

    private String nombre;
    private String placeId;
    private String id;
    private Double latitud;
    private Double longitud;
    private String direccion;
    private Boolean isFrecuente;
    private boolean isNueva;

    public Frecuente (){

    }

    public boolean isNueva() {
        return isNueva;
    }

    public void setNueva(boolean nueva) {
        isNueva = nueva;
    }

    @NonNull
    public static Creator<Frecuente> getCREATOR() {
        return CREATOR;
    }

    public Frecuente(String Nombre, String id, Double Latitud, Double Longitud, String Direccion, boolean isNueva) {
        this.nombre = Nombre;
        this.id = id;
        this.latitud = Latitud;
        this.longitud = Longitud;
        this.direccion = Direccion;
        this.isFrecuente = false;
        this.isNueva = isNueva;
    }

    protected Frecuente(Parcel in) {
        this.nombre = in.readString();
        this.placeId = in.readString();
        this.latitud = in.readDouble();
        this.longitud = in.readDouble();
        this.direccion = in.readString();
        this.id = in.readString();
    }

    @NonNull
    public static Frecuente builder(DocumentSnapshot document) {
        String nombre = document.getString("nombre");
        String ID = document.getId();
        double lat = Objects.requireNonNull(document.getGeoPoint("coordenadas")).getLatitude();
        double log = Objects.requireNonNull(document.getGeoPoint("coordenadas")).getLongitude();
        String direccion = document.getString("direccion");
        Frecuente f = new Frecuente(nombre, ID, lat, log, direccion, false);
        f.setPlaceId(document.getString("placeId"));
        return f;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getFrecuente() {
        return isFrecuente;
    }

    public void setFrecuente(Boolean frecuente) {
        isFrecuente = frecuente;
    }

    @Override
    public String toString() {
        return this.nombre;
    }

    public static final Creator<Frecuente> CREATOR = new Creator<Frecuente>() {
        @Override
        public Frecuente createFromParcel(@NonNull Parcel in) {
            return new Frecuente(in);
        }

        @Override
        public Frecuente[] newArray(int size) {
            return new Frecuente[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.placeId);
        dest.writeDouble(this.latitud);
        dest.writeDouble(this.longitud);
        dest.writeString(this.direccion);
        dest.writeString(this.id);
    }

    @NonNull
    public HashMap<String, Object> toMap() {
        GeoPoint coordenadas = new GeoPoint(this.getLatitud(), this.getLongitud());
        HashMap<String, Object> frecuentes = new HashMap<>();
        frecuentes.put("nombre", this.nombre);
        frecuentes.put("placeId", this.placeId);
        frecuentes.put("coordenadas", coordenadas);
        frecuentes.put("direccion", this.direccion);
        frecuentes.put("isFrecuente", this.isFrecuente);
        frecuentes.put("id", this.id);
        return frecuentes;
    }
}
