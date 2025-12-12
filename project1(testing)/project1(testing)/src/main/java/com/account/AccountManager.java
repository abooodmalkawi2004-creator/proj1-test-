package com.account;

public class AccountManager {

    private final IUserRepository userRepository;
    private final IPasswordEncoder passwordEncoder;
    private final ILogger logger;

    public AccountManager(IUserRepository userRepository,
                          IPasswordEncoder passwordEncoder,
                          ILogger logger) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logger = logger;
    }

    public RegistrationStatus registerUser(String username, String rawPassword) {
        //US-02
        if (username == null || username.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            return RegistrationStatus.INVALID_INPUT;
        }
        //US-03
        if (username.length() < 5 || username.length() > 20) {
            return RegistrationStatus.INVALID_FORMAT;
        }
        if (rawPassword.length() < 8 || !rawPassword.matches(".*\\d.*")) {
            return RegistrationStatus.INVALID_FORMAT;
        }
        //US-04
        if (userRepository.userExists(username)) {
            return RegistrationStatus.INVALID_USERNAME_ALREADY_EXISTS;
        }
        //US-01
        String hashedPassword = passwordEncoder.encode(rawPassword);
        userRepository.saveUser(username, hashedPassword);
        logger.logInfo("User registered successfully: " + username);
        return RegistrationStatus.SUCCESS;
    }
}