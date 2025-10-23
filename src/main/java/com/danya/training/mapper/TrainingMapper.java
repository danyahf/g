package com.danya.training.mapper;

import com.danya.training.Training;
import com.danya.training.dto.TraineeTrainingDto;
import com.danya.training.dto.TrainerTrainingDto;


public interface TrainingMapper {
    TraineeTrainingDto toTraineeTrainingDto(Training training);

    TrainerTrainingDto toTrainerTrainingDto(Training training);
}
