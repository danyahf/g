package com.danya.training;

import com.danya.exception.EntityNotFoundException;
import com.danya.exception.TrainerDoesNotMatchTrainingTypeException;
import com.danya.trainee.Trainee;
import com.danya.trainee.TraineeRepository;
import com.danya.trainer.Trainer;
import com.danya.trainer.TrainerRepository;
import com.danya.training.dto.*;
import com.danya.training.mapper.TrainingMapper;
import com.danya.trainingType.TrainingType;
import com.danya.trainingType.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper trainingMapper;

    @Transactional
    public Long createProfile(CreateTrainingDto payload) {
        log.info("Attempting to create training with name '{}'", payload.name());

        Trainee trainee = traineeRepository.findWithTrainersByUserUsername(payload.traineeUsername())
                .orElseThrow(() -> {
                    log.warn("Unable to create training profile. Trainee with '{}' username does not exist",
                            payload.traineeUsername()
                    );
                    return new EntityNotFoundException("Trainee profile not found");
                });

        Trainer trainer = trainerRepository.findByUserUsername(payload.trainerUsername())
                .orElseThrow(() -> {
                    log.warn("Unable to create training profile. Trainer with '{}' username does not exist",
                            payload.trainerUsername()
                    );
                    return new EntityNotFoundException("Trainer profile not found");
                });

        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(payload.trainingTypeName())
                .orElseThrow(() -> {
                    log.warn("Unable to create training profile. Training type with '{}' name does not exist",
                            payload.trainingTypeName()
                    );
                    return new EntityNotFoundException("Training type not found");
                });

        if (!trainingType.equals(trainer.getSpecialization())) {
            log.warn("Unable to create training profile." +
                    " Trainer's specialization does not match the Training's training type");
            throw new TrainerDoesNotMatchTrainingTypeException();
        }

        Training training = Training.builder()
                .trainingName(payload.name())
                .date(payload.trainingDate())
                .duration(payload.duration())
                .trainingType(trainingType)
                .trainee(trainee)
                .trainer(trainer)
                .build();

        Training saved = trainingRepository.save(training);
        trainee.getTrainers().add(trainer);
        log.info("Successfully created training with id {} and name '{}'", saved.getId(), saved.getTrainingName());

        return saved.getId();
    }

    public List<TraineeTrainingDto> getTraineeTrainings(TraineeTrainingFilterDto filter) {
        return trainingRepository.findByTraineeCriteria(
                        filter.traineeUsername(),
                        filter.fromDate(),
                        filter.toDate(),
                        filter.trainerUsername(),
                        filter.trainingTypeId()
                )
                .stream()
                .map(trainingMapper::toTraineeTrainingDto)
                .toList();
    }

    public List<TrainerTrainingDto> getTrainerTrainings(TrainerTrainingFilterDto filter) {
        return trainingRepository.findByTrainerCriteria(
                        filter.trainerUsername(),
                        filter.fromDate(),
                        filter.toDate(),
                        filter.traineeUsername()
                )
                .stream()
                .map(trainingMapper::toTrainerTrainingDto)
                .toList();
    }
}
