package com.danya.training;


import com.danya.exception.EntityNotFoundException;
import com.danya.exception.TrainerDoesNotMatchTrainingTypeException;
import com.danya.trainee.Trainee;
import com.danya.trainee.TraineeRepository;
import com.danya.trainer.Trainer;
import com.danya.trainer.TrainerRepository;
import com.danya.training.dto.CreateTrainingDto;
import com.danya.trainingType.TrainingType;
import com.danya.trainingType.TrainingTypeRepository;
import com.danya.trainingType.TrainingTypeName;
import com.danya.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingService trainingService;


    @Test
    void createProfileSavesTrainingProfile() {
        CreateTrainingDto dto = CreateTrainingDto.builder()
                .traineeUsername("trainee_username")
                .trainerUsername("trainer_username")
                .trainingTypeName(TrainingTypeName.YOGA)
                .name("yoga training for beginners")
                .trainingDate(LocalDate.now())
                .duration(120)
                .build();

        TrainingType trainingType = new TrainingType(1, TrainingTypeName.YOGA);

        User u1 = new User("test", "test", "trainee_username", "password");
        Trainee trainee = new Trainee(u1, new Date(), "address");

        when(traineeRepository.findWithTrainersByUserUsername(dto.traineeUsername()))
                .thenReturn(Optional.of(trainee));

        User u2 = new User("test", "test", "trainer_username", "password");
        Trainer trainer = new Trainer(u2, trainingType);
        when(trainerRepository.findByUserUsername(dto.trainerUsername()))
                .thenReturn(Optional.of(trainer));

        when(trainingTypeRepository.findByTrainingTypeName(dto.trainingTypeName()))
                .thenReturn(Optional.of(trainingType));

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        when(trainingRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Long result = trainingService.createProfile(dto);

        verify(trainingRepository, times(1)).save(any(Training.class));

        Training saved = captor.getValue();
        assertEquals(dto.name(), saved.getTrainingName());
        assertEquals(dto.trainingDate(), saved.getDate());
        assertEquals(dto.duration(), saved.getDuration());
        assertEquals(dto.trainingTypeName(), saved.getTrainingType().getTrainingTypeName());
        assertEquals(dto.traineeUsername(), saved.getTrainee().getUsername());
        assertEquals(dto.trainerUsername(), saved.getTrainer().getUsername());

        assertSame(saved.getId(), result);
    }

    @Test
    void createProfileThrowsEntityNotFoundExceptionWhenTraineeNotFound() {
        CreateTrainingDto dto = CreateTrainingDto.builder()
                .traineeUsername("trainee_username")
                .trainerUsername("trainer_username")
                .trainingTypeName(TrainingTypeName.YOGA)
                .name("yoga training for beginners")
                .trainingDate(LocalDate.now())
                .duration(120)
                .build();

        when(traineeRepository.findWithTrainersByUserUsername(dto.traineeUsername()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> trainingService.createProfile(dto));

        assertEquals("Trainee profile not found", ex.getMessage());
        verify(trainingRepository, never()).save(any());
        verify(trainerRepository, never()).findByUserUsername(any());
    }

    @Test
    void createProfileThrowsEntityNotFoundExceptionWhenTrainerNotFound() {
        CreateTrainingDto dto = CreateTrainingDto.builder()
                .traineeUsername("trainee_username")
                .trainerUsername("trainer_username")
                .trainingTypeName(TrainingTypeName.YOGA)
                .name("yoga training for beginners")
                .trainingDate(LocalDate.now())
                .duration(120)
                .build();

        User u1 = new User("test", "test", "trainee_username", "password");
        Trainee trainee = new Trainee(u1, new Date(), "address");

        when(traineeRepository.findWithTrainersByUserUsername(dto.traineeUsername()))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findByUserUsername(dto.trainerUsername()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> trainingService.createProfile(dto));

        assertEquals("Trainer profile not found", ex.getMessage());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createProfileThrowsEntityNotFoundExceptionWhenTrainingTypeNotFound() {
        CreateTrainingDto dto = CreateTrainingDto.builder()
                .traineeUsername("trainee_username")
                .trainerUsername("trainer_username")
                .trainingTypeName(TrainingTypeName.YOGA)
                .name("yoga training for beginners")
                .trainingDate(LocalDate.now())
                .duration(120)
                .build();

        TrainingType trainingType = new TrainingType(1, TrainingTypeName.YOGA);

        User u1 = new User("test", "test", "trainee_username", "password");
        Trainee trainee = new Trainee(u1, new Date(), "address");

        when(traineeRepository.findWithTrainersByUserUsername(dto.traineeUsername()))
                .thenReturn(Optional.of(trainee));

        User u2 = new User("test", "test", "trainer_username", "password");
        Trainer trainer = new Trainer(u2, trainingType);
        when(trainerRepository.findByUserUsername(dto.trainerUsername()))
                .thenReturn(Optional.of(trainer));

        when(trainingTypeRepository.findByTrainingTypeName(dto.trainingTypeName()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> trainingService.createProfile(dto));

        assertEquals("Training type not found", ex.getMessage());
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void createProfileThrowsTrainerDoesNotMatchTrainingTypeException() {
        CreateTrainingDto dto = CreateTrainingDto.builder()
                .traineeUsername("trainee_username")
                .trainerUsername("trainer_username")
                .trainingTypeName(TrainingTypeName.YOGA)
                .name("yoga training for beginners")
                .trainingDate(LocalDate.now())
                .duration(120)
                .build();

        TrainingType expectedType = new TrainingType(1, TrainingTypeName.YOGA);
        TrainingType trainerSpecialization = new TrainingType(2, TrainingTypeName.STRETCHING);

        User u1 = new User("first", "last", "trainee_username", "pw");
        Trainee trainee = new Trainee(u1, new Date(), "addr");
        when(traineeRepository.findWithTrainersByUserUsername(dto.traineeUsername()))
                .thenReturn(Optional.of(trainee));

        User u2 = new User("first", "last", "trainer_username", "pw");
        Trainer trainer = new Trainer(u2, trainerSpecialization);
        when(trainerRepository.findByUserUsername(dto.trainerUsername()))
                .thenReturn(Optional.of(trainer));

        when(trainingTypeRepository.findByTrainingTypeName(dto.trainingTypeName()))
                .thenReturn(Optional.of(expectedType));

        assertThrows(
                TrainerDoesNotMatchTrainingTypeException.class,
                () -> trainingService.createProfile(dto)
        );

        verify(trainingRepository, never()).save(any());
    }
}
