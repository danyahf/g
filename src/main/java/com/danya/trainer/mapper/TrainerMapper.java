package com.danya.trainer.mapper;


import com.danya.trainee.Trainee;
import com.danya.trainer.Trainer;
import com.danya.trainer.dto.TrainerProfileTraineeDto;
import com.danya.trainer.dto.TrainerWithTraineesDto;

public interface TrainerMapper {
    TrainerWithTraineesDto toTrainerWithTraineesDto(Trainer trainer);

    TrainerProfileTraineeDto toTraineeDto(Trainee trainee);
}
