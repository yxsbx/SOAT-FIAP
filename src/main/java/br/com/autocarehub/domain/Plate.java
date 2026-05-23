package br.com.autocarehub.domain;

import java.util.Locale;

public record Plate(String value) {

    private static final String PLATE_PATTERN = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$";

    public Plate {
        if (value == null || value.isBlank()) {
            throw new DomainException("Plate is required");
        }
        value = value.replace("-", "").replace(" ", "").toUpperCase(Locale.ROOT);
        if (!value.matches(PLATE_PATTERN)) {
            throw new DomainException("Invalid plate");
        }
    }
}
