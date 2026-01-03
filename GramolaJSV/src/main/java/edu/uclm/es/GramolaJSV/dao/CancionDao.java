package edu.uclm.es.GramolaJSV.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.es.GramolaJSV.model.Cancion;

@Repository
public interface CancionDao extends JpaRepository<Cancion, String> {

}
