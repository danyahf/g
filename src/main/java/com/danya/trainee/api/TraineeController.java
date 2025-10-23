package com.danya.trainee.api;

import com.danya.security.authentication.AuthUser;
import com.danya.security.authentication.AuthenticatedUser;
import com.danya.security.authorization.RequiredRoles;
import com.danya.trainee.TraineeService;
import com.danya.trainee.dto.CreateTraineeDto;
import com.danya.trainee.dto.TraineeProfileTrainerDto;
import com.danya.trainee.dto.TraineeWithTrainersDto;
import com.danya.trainee.dto.UpdateTraineeDto;
import com.danya.trainer.TrainerService;
import com.danya.training.TrainingService;
import com.danya.training.dto.TraineeTrainingDto;
import com.danya.training.dto.TraineeTrainingFilterDto;
import com.danya.user.dto.CredentialsDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/trainees")
public class TraineeController implements TraineeApi {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<CredentialsDto> createProfile(@RequestBody @Valid CreateTraineeDto payload) {
        CredentialsDto credentials = traineeService.createProfile(payload);
        return ResponseEntity.status(HttpStatus.CREATED).
                body(credentials);
    }

    @GetMapping("/{username}")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<TraineeWithTrainersDto> getProfile(
            @PathVariable String username,
            @AuthenticatedUser AuthUser authUser
    ) {
        TraineeWithTrainersDto traineeProfile = traineeService.getProfileByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeProfile);
    }

    @GetMapping("/me")
    @RequiredRoles({"TRAINEE"})
    public ResponseEntity<TraineeWithTrainersDto> getProfile(
            @AuthenticatedUser AuthUser authUser
    ) {
        TraineeWithTrainersDto traineeProfile = traineeService.getProfileByUsername(authUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeProfile);
    }

    @PutMapping("/{username}")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<TraineeWithTrainersDto> updateProfile(
            @PathVariable String username,
            @RequestBody @Valid UpdateTraineeDto payload
    ) {
        TraineeWithTrainersDto traineeProfile = traineeService.updateProfile(username, payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeProfile);
    }

    @PutMapping("/me")
    @RequiredRoles({"TRAINEE"})
    public ResponseEntity<TraineeWithTrainersDto> updateProfile(
            @RequestBody @Valid UpdateTraineeDto payload,
            @AuthenticatedUser AuthUser authUser
    ) {
        TraineeWithTrainersDto traineeProfile = traineeService.updateProfile(authUser.getUsername(), payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeProfile);
    }

    @DeleteMapping("/{username}")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<Void> deleteProfile(@PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/me")
    @RequiredRoles({"TRAINEE"})
    public ResponseEntity<Void> deleteProfile(@AuthenticatedUser AuthUser authUser) {
        traineeService.deleteByUsername(authUser.getUsername());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{username}/available-trainers")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<List<TraineeProfileTrainerDto>> getAvailableTrainers(@PathVariable String username) {
        List<TraineeProfileTrainerDto> trainers = trainerService.findUnassignedForTrainee(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainers);
    }

    @GetMapping("/{username}/trainings")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<List<TraineeTrainingDto>> getAllTraineeTrainings(
            @PathVariable String username,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String trainerUsername,
            @RequestParam(required = false) Integer trainingTypeId
    ) {
        TraineeTrainingFilterDto payload = TraineeTrainingFilterDto.builder()
                .traineeUsername(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerUsername(trainerUsername)
                .trainingTypeId(trainingTypeId)
                .build();
        List<TraineeTrainingDto> trainings = trainingService.getTraineeTrainings(payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainings);
    }

    @GetMapping("/me/trainings")
    @RequiredRoles({"TRAINEE"})
    public ResponseEntity<List<TraineeTrainingDto>> getAllTraineeTrainings(
            @AuthenticatedUser AuthUser authUser,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String trainerUsername,
            @RequestParam(required = false) Integer trainingTypeId
    ) {
        TraineeTrainingFilterDto payload = TraineeTrainingFilterDto.builder()
                .traineeUsername(authUser.getUsername())
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerUsername(trainerUsername)
                .trainingTypeId(trainingTypeId)
                .build();
        List<TraineeTrainingDto> trainings = trainingService.getTraineeTrainings(payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainings);
    }
}
