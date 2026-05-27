package br.com.autocarehub.domain;

import java.util.Locale;

public record Plate(String value) {

    private static final String OLD_BR_PLATE_PATTERN = "^[A-Z]{3}[0-9]{4}$";
    private static final String MERCOSUR_PLATE_PATTERN = "^[A-Z]{3}[0-9][A-Z][0-9]{2}$";

    public Plate {
        if (value.isBlank()) {
            throw new DomainException("Plate is required");
        }
        value = value.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
        if (!value.matches(OLD_BR_PLATE_PATTERN) && !value.matches(MERCOSUR_PLATE_PATTERN)) {
            throw new DomainException("Invalid plate");
        }
    }
}
