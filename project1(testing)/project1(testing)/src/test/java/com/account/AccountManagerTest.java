package com.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AccountManagerTest {

    @Mock private IUserRepository userRepository;
    @Mock private IPasswordEncoder passwordEncoder;
    @Mock private ILogger logger;

    @InjectMocks private AccountManager accountManager;

    @Test
    public void shouldReturnSuccess_WhenCredentialsAreValid() {
        String username = "validUser";
        String password = "StrongPassword1";
        String hashedPassword = "hashed_StrongPassword1";

        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

        RegistrationStatus status = accountManager.registerUser(username, password);

        assertEquals(RegistrationStatus.SUCCESS, status);
        verify(passwordEncoder).encode(password);
        verify(userRepository).saveUser(username, hashedPassword);
        verify(logger).logInfo(contains(username));
    }
    @Test
    // US-02
    public void shouldReturnInvalidInput_WhenCredentialsAreNullOrEmpty() {
        assertEquals(RegistrationStatus.INVALID_INPUT, accountManager.registerUser(null, "pass"));
        assertEquals(RegistrationStatus.INVALID_INPUT, accountManager.registerUser("", "pass"));
        assertEquals(RegistrationStatus.INVALID_INPUT, accountManager.registerUser("user", null));
        assertEquals(RegistrationStatus.INVALID_INPUT, accountManager.registerUser("user", ""));


        verifyNoInteractions(userRepository, passwordEncoder, logger);
    }
    @Test
    // US-03
    public void shouldReturnInvalidFormat_WhenCredentialsAreWeak() {

        assertEquals(RegistrationStatus.INVALID_FORMAT, accountManager.registerUser("ali", "StrongPass1"));
        assertEquals(RegistrationStatus.INVALID_FORMAT, accountManager.registerUser("validUser", "short"));
        assertEquals(RegistrationStatus.INVALID_FORMAT, accountManager.registerUser("validUser", "NoDigitsHere"));

        verifyNoInteractions(userRepository, passwordEncoder, logger);
    }

    @Test
    // US-04
    public void shouldReturnAlreadyExists_WhenUserExists() {
        String username = "existingUser";


        when(userRepository.userExists(username)).thenReturn(true);


        RegistrationStatus status = accountManager.registerUser(username, "StrongPass1");
        assertEquals(RegistrationStatus.INVALID_USERNAME_ALREADY_EXISTS, status);

        verify(userRepository).userExists(username);
        verify(userRepository, never()).saveUser(anyString(), anyString());
    }
}