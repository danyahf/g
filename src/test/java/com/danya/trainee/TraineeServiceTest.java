package com.danya.trainee;


import com.danya.exception.EntityNotFoundException;
import com.danya.trainee.dto.CreateTraineeDto;
import com.danya.trainee.dto.TraineeProfileTrainerDto;
import com.danya.trainee.dto.TraineeWithTrainersDto;
import com.danya.trainee.dto.UpdateTraineeDto;
import com.danya.trainee.mapper.TraineeMapper;
import com.danya.user.PasswordGenerator;
import com.danya.user.User;
import com.danya.user.UserService;
import com.danya.user.dto.CredentialsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeService traineeService;

    @Test
    void createProfileGeneratesUsernameAndPasswordAndSavesTrainee() {
        Date dob = new Date();
        String addr = "123 Main St";
        CreateTraineeDto dto = new CreateTraineeDto("John", "Smith", dob, addr);

        when(userService.generateUsername("John", "Smith"))
                .thenReturn("John.Smith");
        when(passwordGenerator.generate())
                .thenReturn("password");

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);

        when(traineeRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CredentialsDto result = traineeService.createProfile(dto);

        verify(userService, times(1))
                .generateUsername("John", "Smith");
        verify(passwordGenerator, times(1))
                .generate();

        verify(traineeRepository, times(1)).save(any(Trainee.class));

        Trainee saved = captor.getValue();
        assertEquals("John", saved.getUser().getFirstName());
        assertEquals("Smith", saved.getUser().getLastName());
        assertEquals("password", saved.getUser().getPassword());
        assertEquals("John.Smith", saved.getUsername());
        assertEquals(dob, saved.getDateOfBirth());
        assertEquals(addr, saved.getAddress());

        assertSame(saved.getUser().getUsername(), result.username());
    }

    @Test
    void getProfileByUsernameReturnsDtoWhenFound() {
        String username = "alice.doe";
        TraineeProfileTrainerDto trainerDto = TraineeProfileTrainerDto.builder().build();

        TraineeWithTrainersDto expectedDto = new TraineeWithTrainersDto(
                "Alice",
                "Doe",
                new Date(1),
                "Kyiv, Ukraine",
                true,
                List.of(trainerDto)
        );

        Trainee traineeEntity = new Trainee();

        when(traineeRepository.findWithTrainersByUserUsername(username)).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toTraineeWithTrainersDto(traineeEntity)).thenReturn(expectedDto);

        TraineeWithTrainersDto actualDto = traineeService.getProfileByUsername(username);

        assertSame(expectedDto, actualDto, "Should return trainee dto");
        verify(traineeRepository, times(1)).findWithTrainersByUserUsername(username);
        verify(traineeMapper, times(1)).toTraineeWithTrainersDto(traineeEntity);
    }

    @Test
    void getProfileByUsernameThrowsEntityNotFoundExceptionWhenNotFound() {
        String username = "alice.doe";

        when(traineeRepository.findWithTrainersByUserUsername(username)).thenReturn(Optional.empty());

        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> traineeService.getProfileByUsername(username));
        assertEquals("Trainee profile not found", ex.getMessage());

        verify(traineeRepository, times(1)).findWithTrainersByUserUsername(username);
    }

    @Test
    void updateProfileUpdatesFieldsAndSavesTrainee() {
        String username = "alice.doe";
        User user = new User("Alice", "Doe", username, "secret");
        Trainee existing = new Trainee(user, new Date(1L), "address");

        when(traineeRepository.findWithTrainersByUserUsername(username)).thenReturn(Optional.of(existing));

        Date newDob = new Date();
        String newAddr = "New Address";
        UpdateTraineeDto payload = new UpdateTraineeDto(
                "NewFirst", "NewLast", newDob, newAddr, true
        );

        TraineeWithTrainersDto expectedDto = new TraineeWithTrainersDto(
                "NewFirst", "NewLast", newDob, newAddr, true, List.of()
        );
        when(traineeMapper.toTraineeWithTrainersDto(existing)).thenReturn(expectedDto);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);

        TraineeWithTrainersDto result = traineeService.updateProfile(username, payload);

        verify(traineeRepository).findWithTrainersByUserUsername(username);
        verify(traineeRepository).save(captor.capture());
        verify(traineeMapper).toTraineeWithTrainersDto(existing);

        Trainee saved = captor.getValue();
        assertSame(existing, saved, "Service should save the same instance returned by findByUsername");
        assertEquals("NewFirst", saved.getUser().getFirstName());
        assertEquals("NewLast", saved.getUser().getLastName());
        assertEquals(newDob, saved.getDateOfBirth());
        assertEquals(newAddr, saved.getAddress());
        assertTrue(saved.getUser().isActive());

        assertSame(expectedDto, result, "Should return dto");
    }


    @Test
    void updateProfileThrowsEntityNotFoundExceptionWhenNotFound() {
        String username = "alice.doe";
        when(traineeRepository.findWithTrainersByUserUsername(username)).thenReturn(Optional.empty());

        UpdateTraineeDto payload = new UpdateTraineeDto(
                "First", "Last", new Date(), "Addr", false
        );

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.updateProfile(username, payload)
        );

        assertEquals("Unable to update. Trainee does not exist", ex.getMessage());
    }


    @Test
    void deleteByUsernameReturnsVoidWhenEntityDeleted() {
        String username = "username";
        Trainee trainee = new Trainee();

        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        doNothing().when(traineeRepository).delete(trainee);

        traineeService.deleteByUsername(username);

        verify(traineeRepository).findByUserUsername(username);
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void deleteByUsernameThrowsEntityNotFoundExceptionWhenNotFound() {
        String username = "username";
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.deleteByUsername(username)
        );

        assertEquals("Unable to delete. Trainee does not exist", ex.getMessage());
        verify(traineeRepository).findByUserUsername(username);
        verify(traineeRepository, never()).delete(any());
    }
}
