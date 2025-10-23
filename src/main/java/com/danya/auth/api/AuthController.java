package com.danya.auth.api;

import com.danya.auth.AuthService;
import com.danya.auth.TokenDto;
import com.danya.user.dto.CredentialsDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody @Valid CredentialsDto payload) {
        TokenDto tokenDto = authService.authenticate(payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(tokenDto);
    }
}
