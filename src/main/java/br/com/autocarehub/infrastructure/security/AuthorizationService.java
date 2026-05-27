package br.com.autocarehub.infrastructure.security;

import br.com.autocarehub.application.repository.CustomerRepository;
import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.Document;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class AuthorizationService {

    private final CustomerRepository customerRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public AuthorizationService(
            CustomerRepository customerRepository, ServiceOrderRepository serviceOrderRepository) {
        this.customerRepository = customerRepository;
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

    public boolean canTrackServiceOrders(UUID serviceOrderId, String customerDocument) {
        AuthenticatedUser user = currentUser();
        if (user == null) {
            return false;
        }
        if (hasRole(user, "ADMIN") || hasRole(user, "EMPLOYEE")) {
            return true;
        }
        if (serviceOrderId != null) {
            return canAccessServiceOrder(serviceOrderId);
        }
        if (user.customerId() == null || customerDocument == null || customerDocument.isBlank()) {
            return false;
        }
        try {
            Document document = Document.from(customerDocument);
            return customerRepository
                    .findById(user.customerId())
                    .map(customer -> customer.document().equals(document))
                    .orElse(false);
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return null;
        }
        return user;
    }

    private boolean hasRole(AuthenticatedUser user, String role) {
        return role.equals(user.role());
    }
}
