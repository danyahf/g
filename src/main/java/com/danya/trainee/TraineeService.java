package com.danya.trainee;

import com.danya.exception.EntityNotFoundException;
import com.danya.trainee.dto.CreateTraineeDto;
import com.danya.trainee.dto.TraineeWithTrainersDto;
import com.danya.trainee.dto.UpdateTraineeDto;
import com.danya.trainee.mapper.TraineeMapper;
import com.danya.user.PasswordGenerator;
import com.danya.user.User;
import com.danya.user.UserService;
import com.danya.user.dto.CredentialsDto;
import com.danya.user.role.Role;
import com.danya.user.role.RoleName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final PasswordGenerator passwordGenerator;
    private final TraineeMapper traineeMapper;

    @Transactional
    public CredentialsDto createProfile(CreateTraineeDto payload) {
        log.info("Creating trainee with firstname '{}' and lastname '{}'",
                payload.firstName(), payload.lastName());

        String username = userService.generateUsername(payload.firstName(), payload.lastName());
        Role role = userService.findRoleByName(RoleName.TRAINEE);
        String password = passwordGenerator.generate();
        User user = new User(payload.firstName(), payload.lastName(), username, password);
        user.addRole(role);
        Trainee trainee = new Trainee(user, payload.dateOfBirth(), payload.address());
        Trainee saved = traineeRepository.save(trainee);

        log.info("Successfully created trainee with id {} and username '{}'",
                saved.getId(), saved.getUsername());

        return new CredentialsDto(saved.getUsername(), saved.getUser().getPassword());
    }

    @Transactional
    public TraineeWithTrainersDto getProfileByUsername(String username) {
        Trainee trainee = traineeRepository.findWithTrainersByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee with {} username does not exist", username);
                    return new EntityNotFoundException("Trainee profile not found");
                });
        return traineeMapper.toTraineeWithTrainersDto(trainee);
    }

    @Transactional
    public TraineeWithTrainersDto updateProfile(String username, UpdateTraineeDto payload) {
        log.info("Attempting to update trainee with username '{}'", username);

        Trainee trainee = traineeRepository.findWithTrainersByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Unable to update. Trainee with '{}' username does not exist", username);
                    return new EntityNotFoundException("Unable to update. Trainee does not exist");
                });

        trainee.getUser().setFirstName(payload.firstName());
        trainee.getUser().setLastName(payload.lastName());
        trainee.setDateOfBirth(payload.dateOfBirth());
        trainee.setAddress(payload.address());
        trainee.getUser().setActive(payload.isActive());

        traineeRepository.save(trainee);

        log.info("Successfully updated trainee with username '{}'", username);
        return traineeMapper.toTraineeWithTrainersDto(trainee);
    }

    @Transactional
    public void deleteByUsername(String username) {
        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Unable to delete. Trainee with '{}' username does not exist", username);
                    return new EntityNotFoundException("Unable to delete. Trainee does not exist");
                });

        trainee.getTrainers().clear();

        traineeRepository.delete(trainee);
        log.info("Successfully removed trainee with username '{}'", username);
    }
}
