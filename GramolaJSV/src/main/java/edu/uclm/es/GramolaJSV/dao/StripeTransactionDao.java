package edu.uclm.es.GramolaJSV.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.es.GramolaJSV.model.StripeTransaction;

@Repository
public interface StripeTransactionDao extends JpaRepository<StripeTransaction, String> { // La clave primaria de User es
                                                                                         // String
    // <Entidad que gestionamos, Tipo de la clave primaria>

}
