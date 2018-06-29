package com.tracker.tracker.Modelos.fecha;

import java.util.Arrays;

public class Dia {
    private static final String[] diasShort = {"lu", "ma", "mi", "ju", "vi", "sa", "do"};
    private static final String[] dias = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};

    public static int getCodeFromDiaShort(String diaShort) {
        return Arrays.binarySearch(diasShort, diaShort.toLowerCase());
    }

    public static String getDiaShortFromCode(int dia) {
        if(dia > diasShort.length) {
            return null;
        } else if (dia < 0) {
            return null;
        } else {
            return dias[dia];
        }
    }

    public static String getShortDiaFromDia(String dia) {
        int i = Arrays.binarySearch(dias, dia.toLowerCase());
        if(i < 0) {
            return null;
        } else {
            return diasShort[i];
        }
    }

    public static String getDiaFromDiaShort(String diaShort) {
        int i = Arrays.binarySearch(diasShort, diaShort.toLowerCase());
        if(i < 0) {
            return null;
        } else {
            return diasShort[i];
        }
    }

    public static String getDiaFromCode(int dia) {
        if(dia > dias.length) {
            return null;
        } else if (dia < 0) {
            return null;
        } else {
            return dias[dia];
        }
    }

    public static String[] getDias() {
        return dias;
    }
}
