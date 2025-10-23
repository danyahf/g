package com.danya.user.api;

import com.danya.exception.ErrorDto;
import com.danya.security.authentication.AuthUser;
import com.danya.user.dto.PasswordChangeDto;
import com.danya.user.dto.ProfileStatusChangeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Users", description = "User operations")
@SuppressWarnings("unused")
public interface UserApi {
    @Operation(
            summary = "Change password for a user (admin)",
            description = "Allows an admin to change password for any user identified by username.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password changed"),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient role",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    ResponseEntity<Void> changePassword(
            @Parameter(description = "Target username", required = true)
            String username,
            @RequestBody(
                    required = true,
                    description = "New password payload",
                    content = @Content(schema = @Schema(implementation = PasswordChangeDto.class))
            )
            PasswordChangeDto payload
    );

    @Operation(
            summary = "Change own password",
            description = "Allows an authenticated trainee or trainer to change their own password.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password changed"),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient role",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
            }
    )
    ResponseEntity<Void> changePassword(
            @Parameter(hidden = true) AuthUser authUser,
            @RequestBody(
                    required = true,
                    description = "New password payload",
                    content = @Content(schema = @Schema(implementation = PasswordChangeDto.class))
            )
            PasswordChangeDto payload
    );

    @Operation(
            summary = "Change status for a user (admin)",
            description = "Activate or deactivate a user profile by username.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Status changed"),
                    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthenticated", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Insufficient role", content = @Content),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    ResponseEntity<Void> changeStatus(
            @Parameter(description = "Target username", required = true)
            String username,
            @RequestBody(
                    required = true,
                    description = "New profile status",
                    content = @Content(schema = @Schema(implementation = ProfileStatusChangeDto.class))
            )
            ProfileStatusChangeDto payload
    );

    @Operation(
            summary = "Change own status",
            description = "Allows a trainer or trainee to activate/deactivate their own profile.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Password changed"),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient role",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class))),
            }
    )
    ResponseEntity<Void> changeStatus(
            @Parameter(hidden = true) AuthUser authUser,
            @RequestBody(
                    required = true,
                    description = "New profile status",
                    content = @Content(schema = @Schema(implementation = ProfileStatusChangeDto.class))
            )
            ProfileStatusChangeDto payload
    );
}
