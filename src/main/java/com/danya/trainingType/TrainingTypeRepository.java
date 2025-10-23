package com.danya.trainingType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Integer> {

    Optional<TrainingType> findByTrainingTypeName(TrainingTypeName trainingTypeName);
}
