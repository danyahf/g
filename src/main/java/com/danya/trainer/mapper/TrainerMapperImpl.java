package com.danya.trainer.mapper;

import com.danya.trainee.Trainee;
import com.danya.trainer.Trainer;
import com.danya.trainer.dto.TrainerProfileTraineeDto;
import com.danya.trainer.dto.TrainerWithTraineesDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
public class TrainerMapperImpl implements TrainerMapper {
    @Override
    public TrainerWithTraineesDto toTrainerWithTraineesDto(Trainer trainer) {
        List<TrainerProfileTraineeDto> trainees = Stream.ofNullable(trainer.getTrainees())
                .flatMap(Collection::stream)
                .map(this::toTraineeDto)
                .toList();

        return TrainerWithTraineesDto.builder()
                .firstName(trainer.getUser().getFirstName())
                .firstName(trainer.getUser().getLastName())
                .specialization(trainer.getSpecialization())
                .isActive(trainer.getUser().isActive())
                .trainees(trainees)
                .build();
    }

    @Override
    public TrainerProfileTraineeDto toTraineeDto(Trainee trainee) {
        return TrainerProfileTraineeDto.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .username(trainee.getUsername())
                .build();
    }
}
