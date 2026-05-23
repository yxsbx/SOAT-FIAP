package br.com.autocarehub.infrastructure.security;

import br.com.autocarehub.application.repository.ServiceOrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("authorizationService")
public class AuthorizationService {

    private final ServiceOrderRepository serviceOrderRepository;

    public AuthorizationService(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public boolean canAccessCustomer(UUID customerId) {
        AuthenticatedUser user = currentUser();
        return user != null && user.customerId() != null && user.customerId().equals(customerId);
    }

    public boolean canAccessServiceOrder(UUID serviceOrderId) {
        AuthenticatedUser user = currentUser();
        if (user == null || user.customerId() == null) {
            return false;
        }
        return serviceOrderRepository
                .findById(serviceOrderId)
                .map(serviceOrder -> serviceOrder.customerId().equals(user.customerId()))
                .orElse(false);
    }

    private AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return null;
        }
        return user;
    }
}
