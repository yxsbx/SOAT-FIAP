package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.repository.ServiceOrderRepository;
import br.com.autocarehub.domain.ServiceOrder;
import br.com.autocarehub.domain.ServiceOrderStatus;
import java.util.List;

public class ListServiceOrdersUseCase {

    private final ServiceOrderRepository serviceOrderRepository;

    public ListServiceOrdersUseCase(ServiceOrderRepository serviceOrderRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public List<ServiceOrder> execute() {
        return serviceOrderRepository.findAll();
    }

    public List<ServiceOrder> execute(ServiceOrderStatus status) {
        if (status == null) {
            return execute();
        }
        return serviceOrderRepository.findAll().stream()
                .filter(serviceOrder -> serviceOrder.status() == status)
                .toList();
    }

    public List<ServiceOrder> execute(Query query) {
        return execute(query.status());
    }

    public record Query(ServiceOrderStatus status) {
    }
}
