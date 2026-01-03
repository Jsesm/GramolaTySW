package edu.uclm.es.GramolaJSV.model;

import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class StripeTransaction {

    @Id
    @Column(length = 36)
    private String id;

    @Column(columnDefinition = "json")
    private String data;
    private String email;

    @Transient
    private long precio;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StripeTransaction() {
        this.id = UUID.randomUUID().toString();
    }

    public Map<String, Object> getData() {
        return new JSONObject(this.data).toMap();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setData(JSONObject jsoData) {
        this.data = jsoData.toString();
    }

    public long getPrecio() {
        return precio;
    }

    public void setPrecio(long precio) {
        this.precio = precio;
    }
}
