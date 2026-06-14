package br.com.autocarehub.domain.exception;

import br.com.autocarehub.domain.enums.ServiceOrderStatus;

public class InvalidServiceOrderStatusTransitionException extends DomainException {

  public InvalidServiceOrderStatusTransitionException(String message) {
    super(message);
  }
}
