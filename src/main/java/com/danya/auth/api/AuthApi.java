package com.danya.auth.api;


import com.danya.auth.TokenDto;
import com.danya.exception.ErrorDto;
import com.danya.user.dto.CredentialsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("unused")
@Tag(name = "Auth", description = "Authentication endpoints")
public interface AuthApi {

    @Operation(
            summary = "Login",
            description = "Authenticates a user and returns a JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TokenDto.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials or disabled user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    ResponseEntity<TokenDto> login(
            @RequestBody(
                    required = true,
                    description = "Username and password",
                    content = @Content(schema = @Schema(implementation = CredentialsDto.class))
            )
            CredentialsDto payload
    );
}
