package com.tracker.tracker.Modelos.fecha;

import java.util.Arrays;

public class Mes {
    private int mes;
    private final String[] codMes = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic" };

    public Mes(String mes) {
        this.mes = Arrays.binarySearch(codMes, mes) + 1;
    }

    public Mes(int mes) {
        this.mes = mes;
    }

    public boolean isBefore(Mes m) {
        return this.mes < m.getMes();
    }

    public boolean isAfter(Mes m) {
        return !(this.isBefore(m));
    }

    public boolean isEqual(Mes m) {
        return this.mes == m.getMes();
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String[] getCodMes() {
        return codMes;
    }
}
