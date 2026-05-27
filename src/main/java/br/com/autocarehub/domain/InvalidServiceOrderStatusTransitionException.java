package br.com.autocarehub.domain;

public class InvalidServiceOrderStatusTransitionException extends DomainException {

    public InvalidServiceOrderStatusTransitionException(String message) {
        super(message);
    }
}
