package com.danya.trainer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    @Query("""
            SELECT tr
            FROM Trainer tr
            LEFT JOIN tr.trainees tn
                 WITH tn.user.username = :username
            WHERE tn IS NULL
            """)
    List<Trainer> findUnassignedForTrainee(@Param("username") String traineeUsername);
}
