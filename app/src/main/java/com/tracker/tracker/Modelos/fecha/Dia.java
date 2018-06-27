package com.tracker.tracker.Modelos.fecha;

import java.util.Arrays;

public class Dia {
    private int dia;
    private final String[] dias = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"};

    public Dia(String codDia) {
        this.dia = Arrays.binarySearch(dias, codDia) + 1;
    }

    public Dia(int dia) {
        this.dia = dia;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public String[] getDias() {
        return dias;
    }

    public boolean isBefore(Dia d) {
        return this.dia < d.getDia();
    }

    public boolean isAfter(Dia d) {
        return !(this.isBefore(d));
    }

    public boolean isEqual(Dia d) {
        return this.dia == d.getDia();
    }
}
