package edu.uclm.es.GramolaJSV.model;

import java.util.Random;

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
        Random rad = new Random();
        int numeroAleatorio = rad.nextInt(1000); // Genera de 0 a 999
        this.idCancion = idCancion + "-" + numeroAleatorio;
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
