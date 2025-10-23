package com.danya.trainer;

import com.danya.exception.EntityNotFoundException;
import com.danya.trainee.dto.TraineeProfileTrainerDto;
import com.danya.trainee.mapper.TraineeMapper;
import com.danya.trainer.dto.CreateTrainerDto;
import com.danya.trainer.dto.TrainerWithTraineesDto;
import com.danya.trainer.dto.UpdateTrainerDto;
import com.danya.trainer.mapper.TrainerMapper;
import com.danya.trainingType.TrainingType;
import com.danya.trainingType.TrainingTypeRepository;
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

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final UserService userService;
    private final PasswordGenerator passwordGenerator;
    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;

    @Transactional
    public CredentialsDto createProfile(CreateTrainerDto payload) {
        log.info("Attempting to create trainer with firstname '{}' and lastname '{}'",
                payload.firstName(), payload.lastName());

        TrainingType trainingType = trainingTypeRepository
                .findByTrainingTypeName(payload.specialization())
                .orElseThrow(() -> new EntityNotFoundException("Invalid training type"));

        String username = userService.generateUsername(payload.firstName(), payload.lastName());
        Role role = userService.findRoleByName(RoleName.TRAINER);
        String password = passwordGenerator.generate();
        User user = new User(payload.firstName(), payload.lastName(), username, password);
        user.addRole(role);
        Trainer trainer = new Trainer(user, trainingType);

        Trainer saved = trainerRepository.save(trainer);
        log.info("Successfully created trainer with id {}, username '{}', and specialization '{}'",
                saved.getId(), saved.getUsername(), saved.getSpecialization().getTrainingTypeName());

        return new CredentialsDto(saved.getUsername(), saved.getUser().getPassword());
    }

    @Transactional
    public TrainerWithTraineesDto getProfileByUsername(String username) {
        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer with {} username does not exist", username);
                    return new EntityNotFoundException("Trainer profile not found");
                });
        return trainerMapper.toTrainerWithTraineesDto(trainer);
    }

    @Transactional
    public TrainerWithTraineesDto updateProfile(String username, UpdateTrainerDto payload) {
        log.info("Attempting to update trainer with username '{}'", username);

        Trainer trainer = trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> {
                    log.warn("Unable to update. Trainer with '{}' username does not exist", username);
                    return new EntityNotFoundException("Unable to update. Trainer does not exist");
                });

        trainer.getUser().setFirstName(payload.firstName());
        trainer.getUser().setLastName(payload.lastName());
        trainer.getUser().setActive(payload.isActive());

        Trainer updated = trainerRepository.save(trainer);
        log.info("Successfully updated trainer with username '{}'", updated.getUsername());

        return trainerMapper.toTrainerWithTraineesDto(trainer);
    }

    public List<TraineeProfileTrainerDto> findUnassignedForTrainee(String traineeUsername) {
        return trainerRepository.findUnassignedForTrainee(traineeUsername).stream()
                .filter(t -> t.getUser().isActive())
                .map(traineeMapper::toTrainerDto)
                .toList();
    }
}
