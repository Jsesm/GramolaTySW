package edu.uclm.es.GramolaJSV.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.es.GramolaJSV.model.PagoGramola;

@Repository
public interface PagoGramolaDao extends JpaRepository<PagoGramola, Integer> {

}