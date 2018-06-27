package com.tracker.tracker.Modelos.fecha;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class DiasHoras implements Parcelable{
    private ArrayList<String> dias;
    private Hora hora;

    public DiasHoras(ArrayList<String> dias, String hora) {
        this.dias = dias;
        this.hora = new Hora(hora);
    }

    private DiasHoras(Parcel in) {
        in.readStringList(this.getDias());
        this.hora = in.readParcelable(Hora.class.getClassLoader());
    }

    public void addDia(String dia) {
        this.dias.add(dia);
    }

    public ArrayList<String> getDias() {
        return dias;
    }

    public void setDias(ArrayList<String> dias) {
        this.dias = dias;
    }

    public Hora getHora() {
        return hora;
    }

    public void setHora(Hora hora) {
        this.hora = hora;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.dias);
        dest.writeParcelable(this.hora, flags);
    }

    public static final Creator<DiasHoras> CREATOR = new Creator<DiasHoras>() {
        @Override
        public DiasHoras createFromParcel(Parcel in) {
            return new DiasHoras(in);
        }

        @Override
        public DiasHoras[] newArray(int size) {
            return new DiasHoras[size];
        }
    };

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("dias", this.dias.toArray());
        map.put("hora", this.hora.toMap());
        return map;
    }



}
