package edu.uclm.es.GramolaJSV.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Cancion {
    @Id // Dice que es la clave principal
    private String idCancion;

    private String nombreCancion;

    private String autorCancion;

    private String bar;

    public String getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(String idCancion) {
        this.idCancion = idCancion;
    }

    public String getNombreCancion() {
        return nombreCancion;
    }

    public void setNombreCancion(String nombreCancion) {
        this.nombreCancion = nombreCancion;
    }

    public String getAutorCancion() {
        return autorCancion;
    }

    public void setAutorCancion(String autorCancion) {
        this.autorCancion = autorCancion;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

}
