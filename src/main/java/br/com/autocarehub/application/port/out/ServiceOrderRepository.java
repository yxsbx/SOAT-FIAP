package br.com.autocarehub.application.port.out;

import br.com.autocarehub.domain.model.ServiceOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceOrderRepository {

    ServiceOrder save(ServiceOrder serviceOrder);

    Optional<ServiceOrder> findById(UUID id);

    List<ServiceOrder> findAll();

    List<ServiceOrder> findByCustomerId(UUID customerId);

    List<ServiceOrder> findCompletedWithExecutionTime();
}
