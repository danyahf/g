package com.danya.trainingType.api;


import com.danya.security.authorization.RequiredRoles;
import com.danya.trainingType.TrainingType;
import com.danya.trainingType.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/training-types")
public class TrainingTypeController implements TrainingTypeApi {
    private final TrainingTypeRepository trainingTypeRepository;

    @GetMapping
    @RequiredRoles({"ADMIN", "TRAINEE", "TRAINER"})
    public ResponseEntity<List<TrainingType>> getAll() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK)
                .body(trainingTypes);
    }
}
