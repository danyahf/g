package com.danya.trainee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    @EntityGraph(attributePaths = {"trainers", "trainers.specialization"})
    Optional<Trainee> findWithTrainersByUserUsername(String username);
}

