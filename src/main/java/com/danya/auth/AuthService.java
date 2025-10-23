package com.danya.auth;

import com.danya.exception.BadCredentialsException;
import com.danya.user.User;
import com.danya.user.UserRepository;
import com.danya.user.dto.CredentialsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public TokenDto authenticate(CredentialsDto payload) {
        User user = userRepository.findByUsername(payload.username())
                .orElseThrow(() -> {
                    log.warn("User with {} username does not exist", payload.username());
                    return new BadCredentialsException();
                });

        boolean matched = user.getPassword().equals(payload.password());
        boolean active = user.isActive();
        if (!matched || !active) {
            log.warn("Bad credentials");
            throw new BadCredentialsException();
        }

        String accessToken = jwtService.generateToken(user);
        return new TokenDto(accessToken);
    }
}
