package com.danya.trainer;

import com.danya.exception.EntityNotFoundException;
import com.danya.trainee.mapper.TraineeMapper;
import com.danya.trainer.dto.CreateTrainerDto;
import com.danya.trainer.dto.TrainerWithTraineesDto;
import com.danya.trainer.dto.UpdateTrainerDto;
import com.danya.trainer.mapper.TrainerMapper;
import com.danya.trainingType.TrainingType;
import com.danya.trainingType.TrainingTypeRepository;
import com.danya.trainingType.TrainingTypeName;
import com.danya.user.PasswordGenerator;
import com.danya.user.User;
import com.danya.user.UserService;
import com.danya.user.dto.CredentialsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    private TrainerRepository trainerRepository;
    private TrainingTypeRepository trainingTypeRepository;
    private UserService userService;
    private PasswordGenerator passwordGenerator;
    private TrainerMapper trainerMapper;
    private TraineeMapper traineeMapper;
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerRepository = mock(TrainerRepository.class);
        trainingTypeRepository = mock(TrainingTypeRepository.class);
        userService = mock(UserService.class);
        passwordGenerator = mock(PasswordGenerator.class);
        trainerMapper = mock(TrainerMapper.class);
        traineeMapper = mock(TraineeMapper.class);

        trainerService = new TrainerService(
                trainerRepository,
                trainingTypeRepository,
                userService,
                passwordGenerator,
                trainerMapper,
                traineeMapper
        );
    }

    @Test
    void createProfileGeneratesUsernameAndPasswordAndSavesTrainer() {
        CreateTrainerDto dto = new CreateTrainerDto("John", "Smith", TrainingTypeName.FITNESS);

        when(userService.generateUsername("John", "Smith")).thenReturn("John.Smith");
        when(passwordGenerator.generate()).thenReturn("secretPwd");
        when(trainingTypeRepository.findByTrainingTypeName(TrainingTypeName.FITNESS))
                .thenReturn(Optional.of(new TrainingType(1, TrainingTypeName.FITNESS)));

        ArgumentCaptor<Trainer> savedCaptor = ArgumentCaptor.forClass(Trainer.class);
        when(trainerRepository.save(savedCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        CredentialsDto result = trainerService.createProfile(dto);

        verify(userService).generateUsername("John", "Smith");
        verify(passwordGenerator).generate();
        verify(trainerRepository).save(any(Trainer.class));
        verifyNoInteractions(trainerMapper);

        Trainer saved = savedCaptor.getValue();
        assertEquals("John", saved.getUser().getFirstName());
        assertEquals("Smith", saved.getUser().getLastName());
        assertEquals("John.Smith", saved.getUsername());
        assertEquals("secretPwd", saved.getUser().getPassword());
        assertEquals(TrainingTypeName.FITNESS, saved.getSpecialization().getTrainingTypeName());
        assertSame(saved.getUsername(), result.username());
    }

    @Test
    void getProfileByUsernameReturnsDtoWhenFound() {
        String username = "username";
        User user = new User("Alice", "Doe", username, "secret");
        TrainingType type = new TrainingType(1, TrainingTypeName.YOGA);
        Trainer entity = new Trainer(user, type);

        TrainerWithTraineesDto expectedDto = new TrainerWithTraineesDto("Alice", "Doe", type, true, java.util.List.of());

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(entity));
        when(trainerMapper.toTrainerWithTraineesDto(entity)).thenReturn(expectedDto);

        TrainerWithTraineesDto actual = trainerService.getProfileByUsername(username);

        assertSame(expectedDto, actual);
        verify(trainerRepository).findByUserUsername(username);
        verify(trainerMapper).toTrainerWithTraineesDto(entity);
    }

    @Test
    void getProfileByUsernameThrowsEntityNotFoundWhenMissing() {
        String username = "missing";
        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.getProfileByUsername(username)
        );
        assertEquals("Trainer profile not found", ex.getMessage());
        verify(trainerRepository).findByUserUsername(username);
        verifyNoInteractions(trainerMapper);
    }

    @Test
    void updateProfileUpdatesFieldsSavesAndReturnsDto() {
        String username = "john.smith";
        User user = new User("OldFirst", "OldLast", username, "pass");
        user.setActive(false);
        TrainingType type = new TrainingType(1, TrainingTypeName.YOGA);
        Trainer existing = new Trainer(user, type);

        UpdateTrainerDto payload = new UpdateTrainerDto("NewFirst", "NewLast", true);
        TrainerWithTraineesDto expectedDto = new TrainerWithTraineesDto("NewFirst", "NewLast", type, true, java.util.List.of());

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.of(existing));
        when(trainerRepository.save(existing)).thenAnswer(inv -> inv.getArgument(0));
        when(trainerMapper.toTrainerWithTraineesDto(existing)).thenReturn(expectedDto);

        TrainerWithTraineesDto result = trainerService.updateProfile(username, payload);

        // entity mutated
        assertEquals("NewFirst", existing.getUser().getFirstName());
        assertEquals("NewLast", existing.getUser().getLastName());
        assertTrue(existing.getUser().isActive());

        assertSame(expectedDto, result);

        verify(trainerRepository).findByUserUsername(username);
        verify(trainerRepository).save(existing);
        verify(trainerMapper).toTrainerWithTraineesDto(existing);
        verifyNoMoreInteractions(trainerRepository);
    }

    @Test
    void updateProfileThrowsEntityNotFoundWhenMissing() {
        String username = "unknown";
        UpdateTrainerDto payload = new UpdateTrainerDto("First", "Last", false);

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.updateProfile(username, payload)
        );
        assertEquals("Unable to update. Trainer does not exist", ex.getMessage());

        verify(trainerRepository).findByUserUsername(username);
        verifyNoMoreInteractions(trainerRepository);
        verifyNoInteractions(trainerMapper);
    }
}
