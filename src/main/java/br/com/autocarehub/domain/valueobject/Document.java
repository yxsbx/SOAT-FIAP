package br.com.autocarehub.domain.valueobject;

import br.com.autocarehub.domain.enums.DocumentType;
import br.com.autocarehub.domain.exception.DomainException;
import java.util.Objects;

public record Document(DocumentType type, String value) {

    public Document {
        Objects.requireNonNull(type, "type is required");
        value = normalize(value);
        if (!isValid(type, value)) {
            throw new DomainException("Invalid document");
        }
    }

    public static Document from(String value) {
        String normalized = normalize(value);
        if (normalized.length() == 11) {
            return new Document(DocumentType.CPF, normalized);
        }
        if (normalized.length() == 14) {
            return new Document(DocumentType.CNPJ, normalized);
        }
        throw new DomainException("Document must be CPF or CNPJ");
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }

    private static boolean isValid(DocumentType type, String value) {
        return switch (type) {
            case CPF -> isValidCpf(value);
            case CNPJ -> isValidCnpj(value);
        };
    }

    private static boolean isValidCpf(String value) {
        if (value.length() != 11 || hasSameDigits(value)) {
            return false;
        }
        int firstDigit = calculateCpfDigit(value, 9);
        int secondDigit = calculateCpfDigit(value, 10);
        return firstDigit == Character.getNumericValue(value.charAt(9))
                && secondDigit == Character.getNumericValue(value.charAt(10));
    }

    private static int calculateCpfDigit(String value, int length) {
        int sum = 0;
        for (int index = 0; index < length; index++) {
            sum += Character.getNumericValue(value.charAt(index)) * (length + 1 - index);
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean isValidCnpj(String value) {
        if (value.length() != 14 || hasSameDigits(value)) {
            return false;
        }
        int firstDigit = calculateCnpjDigit(value, 12);
        int secondDigit = calculateCnpjDigit(value, 13);
        return firstDigit == Character.getNumericValue(value.charAt(12))
                && secondDigit == Character.getNumericValue(value.charAt(13));
    }

    private static int calculateCnpjDigit(String value, int length) {
        int[] weights = length == 12
                ? new int[] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
                : new int[] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int index = 0; index < length; index++) {
            sum += Character.getNumericValue(value.charAt(index)) * weights[index];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean hasSameDigits(String value) {
        char first = value.charAt(0);
        for (int index = 1; index < value.length(); index++) {
            if (value.charAt(index) != first) {
                return false;
            }
        }
        return true;
    }
}
