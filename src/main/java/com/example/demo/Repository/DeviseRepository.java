package com.example.demo.Repository;

import com.example.demo.Entity.Devise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour l'entité Devise (code ISO comme clé primaire).
 */
@Repository
public interface DeviseRepository extends JpaRepository<Devise, String> {

    /** Vérifie l'existence d'une devise par son code. */
    boolean existsByCode(String code);

    /** Retourne toutes les devises actives. */
    java.util.List<Devise> findByActifTrue();
}
