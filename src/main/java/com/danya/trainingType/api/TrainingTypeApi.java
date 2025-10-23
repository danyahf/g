package com.danya.trainingType.api;

import com.danya.exception.ErrorDto;
import com.danya.trainingType.TrainingType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Training Types", description = "Read available training types")
@SuppressWarnings("unused")
public interface TrainingTypeApi {

    @Operation(
            summary = "List training types",
            description = "Returns all available training types.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = TrainingType.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient role",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    ResponseEntity<List<TrainingType>> getAll();
}
