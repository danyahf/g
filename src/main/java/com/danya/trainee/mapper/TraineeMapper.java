package com.danya.trainee.mapper;

import com.danya.trainee.Trainee;
import com.danya.trainee.dto.TraineeProfileTrainerDto;
import com.danya.trainee.dto.TraineeWithTrainersDto;
import com.danya.trainer.Trainer;

public interface TraineeMapper {
    TraineeWithTrainersDto toTraineeWithTrainersDto(Trainee trainee);

    TraineeProfileTrainerDto toTrainerDto(Trainer trainer);
}
