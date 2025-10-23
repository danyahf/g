package com.danya.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorTest {

    private PasswordGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PasswordGenerator();
    }

    @Test
    void generateReturnsPasswordOfLengthTenAndOnlyAllowedChars() {
        String pw = generator.generate();

        assertEquals(10, pw.length(), "Password must be exactly 10 characters");

        String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz";
        for (char c : pw.toCharArray()) {
            assertTrue(pool.indexOf(c) >= 0,
                    "Character '" + c + "' must be from the allowed pool"
            );
        }
    }
}
