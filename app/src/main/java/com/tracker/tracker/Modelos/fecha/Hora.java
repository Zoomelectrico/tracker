package com.tracker.tracker.Modelos.fecha;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Hora implements Parcelable {

    private int hora;
    private int minutos;
    private int segundos;

    public Hora(String hora) {
        String[] datos = hora.split(":");
        this.hora = Integer.parseInt(datos[0]);
        this.minutos = Integer.parseInt(datos[1]);
        this.segundos = Integer.parseInt(datos[2]);
    }

    public Hora(int hora, int minutos, int segundos) {
        this.hora = hora;
        this.minutos = minutos;
        this.segundos = segundos;
    }

    private Hora(Parcel in) {
        this.hora = in.readInt();
        this.minutos = in.readInt();
        this.segundos = in.readInt();
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public int getSegundos() {
        return segundos;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(hora));
        sb.append(":");
        sb.append(String.valueOf(minutos));
        sb.append(":");
        sb.append(String.valueOf(segundos));
        return sb.toString();
    }

    public boolean isBefore(Hora h) {
        if (h.getHora() > this.hora) {
            return true;
        } else if (h.getHora() == this.hora) {
            if (h.getMinutos() > this.minutos) {
                return true;
            } else if (h.getMinutos() == this.minutos) {
                return h.getSegundos() > this.segundos;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isAfter(Hora h) {
        return !(this.isBefore(h));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.hora);
        dest.writeInt(this.minutos);
        dest.writeInt(this.segundos);
    }

    private static final Creator<Hora> CREATOR = new Creator<Hora>() {
        @Override
        public Hora createFromParcel(Parcel source) {
            return new Hora(source);
        }

        @Override
        public Hora[] newArray(int size) {
            return new Hora[size];
        }
    };

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("hora", this.hora);
        map.put("minutos", this.minutos);
        map.put("segundos", this.segundos);
        return map;
    }
}
