package com.georent.validation;

import com.georent.dto.LoginRequestDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LoginRequestDTOValidator implements Validator { // NOPMD ciclomatic complexity.
    /**
     * @param MIN_USERNAME min username length.
     */
    private static final int MIN_USERNAME = 4;
    /**
     * @param MIN_PASSWORD min password length.
     */
    private static final int MIN_PASSWORD = 8;
    /**
     * @param MIN_USERNAME min username length.
     */
    private static final int MAX_USERNAME = 16;
    /**
     * @param MAX_PASSWORD max password length.
     */
    private static final int MAX_PASSWORD = 64;
    /**
     * @param REGEXP_VALIDATOR password and name validation regexp.
     */
    private static final String REGEXP_VALIDATOR = "[A-Za-z0-9_-]+";

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginRequestDTO.class.isAssignableFrom(clazz);
    }

    /**
     * Validate Login request.
     *
     * @param target request.
     * @param errors errors.
     */
    @Override
    public void validate(Object target, Errors errors) { // NOPMD cyclomatic complexity 6.
        LoginRequestDTO loginRequestDTO = (LoginRequestDTO) target;
        validateUsernameField(loginRequestDTO.getEmail(), errors);
        validatePasswordField(loginRequestDTO.getPassword(), errors);
    }

    /**
     * Validate username for max(16), min(4) length, pattern(A-Za-z_-).
     *
     * @param username username.
     * @param errors   errors.
     */
    private void validateUsernameField(String username, Errors errors) {

        String fieldName = "username";
        if (username == null || username.length() == 0) {
            errors.rejectValue(fieldName, "username should not be null", "Username should "
                    +
                    "not be blank");
            return;
        }

        final String chars = " characters";
        final String charsDt = " characters.";
        if (username.length() < MIN_USERNAME) {
            errors.rejectValue(fieldName, "username should not be less than " + MIN_USERNAME
                    +
                    chars, "Username should not be less than " + MIN_USERNAME
                    +
                    charsDt);
            return;
        }
        if (username.length() > MAX_USERNAME) {
            errors.rejectValue(fieldName, "username should not be more than " + MAX_USERNAME
                    +
                    chars, "Username should not be more than " + MAX_USERNAME
                    +
                    charsDt);
            return;
        }

        if (!username.matches(REGEXP_VALIDATOR)) {
            errors.rejectValue(fieldName, "Username contain forbidden characters, allowed characters "
                    +
                    "are A-Za-z0-9_-", "Username contain forbidden characters, allowed characters "
                    +
                    "are A-Za-z0-9_- .");
            return;
        }

    }

    /**
     * Validate password for max(64), min(8) length, pattern(A-Za-z_-).
     *
     * @param password password.
     * @param errors   errors.
     */
    private void validatePasswordField(final String password, final Errors errors) {
        final String fieldName = "password";
        if (password == null || password.length() == 0) {
            errors.rejectValue(fieldName, "password should not be null", "Password should "
                    +
                    "not be blank.");
            return;
        }
        final String chars = " characters";
        final String charsDt = " characters.";

        if (password.length() < MIN_PASSWORD) {
            errors.rejectValue(fieldName, "password should not be less than " + MIN_PASSWORD
                    + chars, "Password should not be less than " + MIN_PASSWORD
                    + charsDt);
        }
        if (password.length() > MAX_PASSWORD) {
            errors.rejectValue(fieldName, "password should not be more than " + MAX_PASSWORD
                    +
                    chars, "Password should not be more than " + MAX_PASSWORD
                    +
                    charsDt);
            return;

        }

        if (!password.matches(REGEXP_VALIDATOR)) {
            errors.rejectValue(fieldName, "Password contain forbidden characters, allowed characters "
                    +
                    "are A-Za-z0-9_-", "Password contain forbidden characters, allowed characters "
                    +
                    "are A-Za-z0-9_- .");
            return;
        }

    }
}
