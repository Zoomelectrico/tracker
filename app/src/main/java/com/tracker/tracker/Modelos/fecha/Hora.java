package com.tracker.tracker.Modelos.fecha;

import android.util.Log;

public class Hora  {

    public static long getHora(String hora) {
        if(hora.contains(":")){
            String[] vec = hora.split(":");
            long l;
            try {
                l = Long.parseLong(vec[0]);
            } catch (NumberFormatException e) {
                l = -1;
                Log.e("", "");
            }
            return l;
        } else {
            return -1;
        }
    }

    public static long getMinutos(String hora) {
        if(hora.contains(":")){
            String[] vec = hora.split(":");
            long l;
            try {
                l = Long.parseLong(vec[1]);
            } catch (NumberFormatException e) {
                l = -1;
                Log.e("", "");
            }
            return l;
        } else {
            return -1;
        }
    }

    public static long getSegundos(String hora) {
        if(hora.contains(":")){
            String[] vec = hora.split(":");
            long l;
            try {
                l = Long.parseLong(vec[2]);
            } catch (NumberFormatException e) {
                l = -1;
                Log.e("", "");
            }
            return l;
        } else {
            return -1;
        }
    }

}
