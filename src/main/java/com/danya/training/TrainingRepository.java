package com.danya.training;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("""
            SELECT DISTINCT tr
            FROM Training tr
            JOIN FETCH tr.trainer t
            JOIN tr.trainee tn
            JOIN tr.trainingType tt
            WHERE tn.user.username = :traineeUsername
              AND (:fromDate IS NULL OR tr.date >= :fromDate)
              AND (:toDate IS NULL OR tr.date <= :toDate)
              AND (:trainerUsername IS NULL OR t.user.username = :trainerUsername)
              AND (:trainingTypeId IS NULL OR tt.id = :trainingTypeId)
            """)
    List<Training> findByTraineeCriteria(@Param("traineeUsername") String traineeUsername,
                                         @Param("fromDate") LocalDate fromDate,
                                         @Param("toDate") LocalDate toDate,
                                         @Param("trainerUsername") String trainerUsername,
                                         @Param("trainingTypeId") Integer trainingTypeId);

    @Query("""
            SELECT DISTINCT tr
            FROM Training tr
            JOIN FETCH tr.trainee tn
            JOIN tr.trainer t
            WHERE t.user.username = :trainerUsername
              AND (:fromDate IS NULL OR tr.date >= :fromDate)
              AND (:toDate IS NULL OR tr.date <= :toDate)
              AND (:traineeUsername IS NULL OR tn.user.username = :traineeUsername)
            """)
    List<Training> findByTrainerCriteria(@Param("trainerUsername") String trainerUsername,
                                         @Param("fromDate") LocalDate fromDate,
                                         @Param("toDate") LocalDate toDate,
                                         @Param("traineeUsername") String traineeUsername);
}
