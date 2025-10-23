package com.danya.training.mapper;

import com.danya.training.Training;
import com.danya.training.dto.TraineeTrainingDto;
import com.danya.training.dto.TrainerTrainingDto;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapperImpl implements TrainingMapper {
    @Override
    public TraineeTrainingDto toTraineeTrainingDto(Training training) {
        return TraineeTrainingDto.builder()
                .trainingName(training.getTrainingName())
                .duration(training.getDuration())
                .trainerId(training.getTrainer().getId())
                .trainingType(training.getTrainingType())
                .date(training.getDate())
                .build();
    }

    @Override
    public TrainerTrainingDto toTrainerTrainingDto(Training training) {
        return TrainerTrainingDto.builder()
                .trainingName(training.getTrainingName())
                .duration(training.getDuration())
                .traineeId(training.getTrainee().getId())
                .trainingType(training.getTrainingType())
                .date(training.getDate())
                .build();
    }
}
