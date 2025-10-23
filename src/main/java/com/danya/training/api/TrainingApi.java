package com.danya.training.api;

import com.danya.exception.ErrorDto;
import com.danya.training.dto.CreateTrainingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Trainings", description = "Training scheduling operations")
@SuppressWarnings("unused")
public interface TrainingApi {

    @Operation(
            summary = "Create a training",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient role",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "404", description = "Trainee/Trainer/TrainingType not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "409", description = "Trainer specialization mismatch with trainingType",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    ResponseEntity<Long> create(
            @RequestBody(
                    required = true,
                    description = "Training creation payload",
                    content = @Content(schema = @Schema(implementation = CreateTrainingDto.class))
            )
            CreateTrainingDto payload
    );
}
