package edu.uclm.es.GramolaJSV.model;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity //Todas las clases que sean entities tienen que tener un constructor sin parametros o no tener 
//(Si tiene parametros, tienes que hacer otro sin parametros) porque de manera automatica se llama al 
//constructor sin parametros y se ejecutan todos los metodos set que vaya encontrando
public class Token {
    @Id @Column(length=36)//Porque el UUID tiene 36 caracteres siempre
    private String id;
    private long creationtime;
    private long useTime=0;


    public Token(){
        this.id=  UUID.randomUUID().toString();
        this.creationtime= System.currentTimeMillis();
    }

    public void use(){
        this.useTime=System.currentTimeMillis();
    }

    //TO DO Hay que poner get y set

    public long getUseTime(){
        return this.useTime;
    }

    public void setUseTime(long useTime){
        this.useTime=useTime;
    }


    public long getCreationTime(){
        return this.creationtime;
    }


    public void setCreationTime(long creationTime){
        this.creationtime=creationTime;
    }  

        public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id=id;
    }


    public boolean isUsed(){
        return this.useTime!=0;
    }


}
