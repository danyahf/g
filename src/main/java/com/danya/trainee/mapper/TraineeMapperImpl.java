package com.danya.trainee.mapper;

import com.danya.trainee.Trainee;
import com.danya.trainee.dto.TraineeProfileTrainerDto;
import com.danya.trainee.dto.TraineeWithTrainersDto;
import com.danya.trainer.Trainer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
public class TraineeMapperImpl implements TraineeMapper {

    @Override
    public TraineeWithTrainersDto toTraineeWithTrainersDto(Trainee trainee) {
        List<TraineeProfileTrainerDto> trainers = Stream.ofNullable(trainee.getTrainers())
                .flatMap(Collection::stream)
                .map(this::toTrainerDto)
                .toList();

        return TraineeWithTrainersDto.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.getUser().isActive())
                .trainers(trainers)
                .build();
    }

    @Override
    public TraineeProfileTrainerDto toTrainerDto(Trainer trainer) {
        return TraineeProfileTrainerDto.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .specialization(trainer.getSpecialization())
                .build();
    }
}
