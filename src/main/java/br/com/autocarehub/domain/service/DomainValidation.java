package br.com.autocarehub.domain.service;

import java.time.Year;
import java.util.Locale;
import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

import br.com.autocarehub.domain.exception.DomainException;

public final class DomainValidation {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,11}$");
    private static final Pattern STATE_PATTERN = Pattern.compile("^[A-Z]{2}$");

    private DomainValidation() {

    }

    public static String requireText(String value, String message, int maxLength) {
        if (value.isBlank()) {
            throw new DomainException(message);
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new DomainException("Text exceeds maximum length");
        }
        return normalized;
    }

    public static @Nullable String optionalText(@Nullable String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > 80) {
            throw new DomainException("Text exceeds maximum length");
        }
        return normalized;
    }

    public static String requireEmail(String value) {
        String email = requireText(value, "Email is required", 120).toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new DomainException("Invalid email");
        }
        return email;
    }

    public static String requirePhone(String value) {
        String phone = requireText(value, "Phone is required", 20).replaceAll("\\D", "");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new DomainException("Invalid phone");
        }
        return phone;
    }

    public static String requireState(String value) {
        String state = requireText(value, "State is required", 2).toUpperCase(Locale.ROOT);
        if (!STATE_PATTERN.matcher(state).matches()) {
            throw new DomainException("State must have two characters");
        }
        return state;
    }

    public static int requireVehicleYear(int value) {
        int nextModelYear = Year.now().getValue() + 1;
        if (value < 1900 || value > nextModelYear) {
            throw new DomainException("Invalid year");
        }
        return value;
    }
}
