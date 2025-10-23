package com.danya.trainer.api;

import com.danya.security.authentication.AuthUser;
import com.danya.security.authentication.AuthenticatedUser;
import com.danya.security.authorization.RequiredRoles;
import com.danya.trainer.TrainerService;
import com.danya.trainer.dto.CreateTrainerDto;
import com.danya.trainer.dto.TrainerWithTraineesDto;
import com.danya.trainer.dto.UpdateTrainerDto;
import com.danya.training.TrainingService;
import com.danya.training.dto.TrainerTrainingDto;
import com.danya.training.dto.TrainerTrainingFilterDto;
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
@RequestMapping("/trainers")
public class TrainerController implements TrainerApi {
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @PostMapping
    public ResponseEntity<CredentialsDto> createProfile(@RequestBody @Valid CreateTrainerDto payload) {
        CredentialsDto credentials = trainerService.createProfile(payload);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(credentials);
    }

    @GetMapping("/{username}")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<TrainerWithTraineesDto> getProfile(@PathVariable String username) {
        TrainerWithTraineesDto traineeProfile = trainerService.getProfileByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeProfile);
    }

    @GetMapping("/me")
    @RequiredRoles({"TRAINER"})
    public ResponseEntity<TrainerWithTraineesDto> getProfile(@AuthenticatedUser AuthUser authUser) {
        TrainerWithTraineesDto traineeProfile = trainerService.getProfileByUsername(authUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeProfile);
    }

    @PutMapping("/{username}")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<TrainerWithTraineesDto> updateProfile(
            @PathVariable String username,
            @RequestBody @Valid UpdateTrainerDto payload
    ) {
        TrainerWithTraineesDto traineeProfile = trainerService.updateProfile(username, payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeProfile);
    }

    @PutMapping("/me")
    @RequiredRoles({"TRAINER"})
    public ResponseEntity<TrainerWithTraineesDto> updateProfile(
            @AuthenticatedUser AuthUser authUser,
            @RequestBody @Valid UpdateTrainerDto payload
    ) {
        TrainerWithTraineesDto traineeProfile = trainerService.updateProfile(authUser.getUsername(), payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(traineeProfile);
    }

    @GetMapping("/{username}/trainings")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<List<TrainerTrainingDto>> getAllTrainerTrainings(
            @PathVariable String username,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String traineeUsername
    ) {
        TrainerTrainingFilterDto payload = TrainerTrainingFilterDto.builder()
                .trainerUsername(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeUsername(traineeUsername)
                .build();
        List<TrainerTrainingDto> trainings = trainingService.getTrainerTrainings(payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainings);
    }

    @GetMapping("/me/trainings")
    @RequiredRoles({"TRAINER"})
    public ResponseEntity<List<TrainerTrainingDto>> getAllTrainerTrainings(
            @AuthenticatedUser AuthUser authUser,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(required = false) String traineeUsername
    ) {
        TrainerTrainingFilterDto payload = TrainerTrainingFilterDto.builder()
                .trainerUsername(authUser.getUsername())
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeUsername(traineeUsername)
                .build();
        List<TrainerTrainingDto> trainings = trainingService.getTrainerTrainings(payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainings);
    }
}
