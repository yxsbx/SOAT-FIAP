package br.com.autocarehub.domain.exception;

public class InvalidServiceOrderStatusTransitionException extends DomainException {

    public InvalidServiceOrderStatusTransitionException(String message) {
        super(message);
    }
}
