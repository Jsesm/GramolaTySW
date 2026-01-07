package edu.uclm.es.GramolaJSV.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PagoGramola {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long precioMensual;

    private Long precioAnual;

    public Long getPrecioMensual() {
        return precioMensual;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPrecioMensual(Long precioMensual) {
        this.precioMensual = precioMensual;
    }

    public Long getPrecioAnual() {
        return precioAnual;
    }

    public void setPrecioAnual(Long precioAnual) {
        this.precioAnual = precioAnual;
    }
}
