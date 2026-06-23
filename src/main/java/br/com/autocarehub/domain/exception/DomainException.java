package br.com.autocarehub.domain.exception;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
