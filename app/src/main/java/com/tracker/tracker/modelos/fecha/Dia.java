package com.tracker.tracker.modelos.fecha;

import java.util.ArrayList;

public class Dia {
    private static final String[] diasShort = {"do", "lu", "ma", "mi", "ju", "vi", "sa"};
    private static final String[] dias = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};

    public static int getCodeFromDiaShort(String diaShort) {
        int i = -1;
        switch (diaShort) {
            case "do":
                i = 1;
                break;
            case "lu":
                i = 2;
                break;
            case "ma":
                i = 3;
                break;
            case "mi":
                i = 4;
                break;
            case "ju":
                i = 5;
                break;
            case "vi":
                i = 6;
                break;
            case "sa":
                i = 7;
                break;
        }
        return i;
    }


    public static String getShortDiaFromDia(String dia) {
        int i;
        for (i = 0; i < dias.length; i++) {
            if(dia.equalsIgnoreCase(dias[i])) {
                break;
            }
        }
        if(i >= 7) {
            return null;
        } else {
            return diasShort[i];
        }
    }

    public static String getDiaFromDiaShort(String diaShort) {
        int i;
        for (i = 0; i < diasShort.length; i++) {
            if (diasShort[i].equalsIgnoreCase(diaShort)) {
                break;
            }
        }
        if (i >= 7) {
            return null;
        } else {
            return dias[i];
        }

    }

    public static String[] getDias() {
        return dias;
    }

    public static String[] getDiasShort() {
        return diasShort;
    }

}
