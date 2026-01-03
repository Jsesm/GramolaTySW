package edu.uclm.es.GramolaJSV.model;

import edu.uclm.es.GramolaJSV.utils.StringEncryptor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

@Entity
public class User {
    @Id // Dice que es la clave principal
    private String email;
    private String pwd;
    private String nombre;
    private String clientId;
    private String clientSecret;
    private String latitud;
    private String longitud;
    private long precioCancion;
    @Lob
    private String firma;

    @Transient // No se guarda en la base de datos, esto va a ir para el token de spotify
    private SpotiToken spotiSimpleToken;

    // Fecth.TYPE.LAZY solo se cargan cuando lo pides
    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "creationtokenid") // El nombre de la columna en la tabla user que hace referencia a la tabla
                                          // token
    private Token creationtokenid;

    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = StringEncryptor.encrypt(pwd);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public long getPrecioCancion() {
        return precioCancion;
    }

    public void setPrecioCancion(double precioCancion) {
        this.precioCancion = (long) (precioCancion * 100);
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public Token getCreationtoken() {
        return creationtokenid;
    }

    public void setCreationtoken(Token creationTokenId) {
        this.creationtokenid = creationTokenId;
    }

    public SpotiToken getSpotiSimpleToken() {
        return spotiSimpleToken;
    }

    public void setSpotiSimpleToken(SpotiToken token) {
        this.spotiSimpleToken = token;
    }

}
