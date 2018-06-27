package com.tracker.tracker.Modelos.fecha;

public class Fecha {
    private Dia dia ;
    private Mes mes;
    private long anio;

    public Fecha(String dia, String mes, String anio) {
        this.dia = new Dia(dia);
        this.mes = new Mes(mes);
        try {
            this.anio = Long.parseLong(anio);
        } catch (NumberFormatException e) {
            this.anio = 0;
            e.printStackTrace();
        }
    }

    public Fecha(int dia, int mes, long anio) {
        this.dia = new Dia(dia);
        this.mes = new Mes(mes);
        this.anio = anio;
    }

    public Dia getDia() {
        return dia;
    }

    public void setDia(Dia dia) {
        this.dia = dia;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public long getAnio() {
        return anio;
    }

    public void setAnio(long anio) {
        this.anio = anio;
    }

    public boolean isBefore(Fecha f) {
        boolean dia = this.dia.isBefore(f.getDia());
        boolean mes = this.mes.isBefore(f.getMes());
        if (this.anio < f.getAnio()) {
            return true;
        } else if (this.anio == f.getAnio()) {
            if(mes) {
                return true;
            } else {
                return dia;
            }
        } else {
            return false;
        }
    }

    public boolean isAfter(Fecha f) {
        return !(this.isBefore(f));
    }

    public boolean isEqual(Fecha f) {
        boolean dia = this.dia.isEqual(f.getDia());
        boolean mes = this.mes.isEqual(f.getMes());
        return (this.anio == f.getAnio()) && mes && dia;
    }

}
