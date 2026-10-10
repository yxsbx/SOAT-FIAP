package br.com.autocarehub.application.usecase.serviceorder;

import br.com.autocarehub.application.exception.ResourceNotFoundException;
import br.com.autocarehub.application.port.out.PartRepository;
import br.com.autocarehub.domain.model.Part;
import br.com.autocarehub.domain.model.ServiceOrder;
import java.util.Comparator;
import java.util.function.ObjIntConsumer;

final class BudgetStock {
    private BudgetStock() {}

    static void update(ServiceOrder order, PartRepository repository, ObjIntConsumer<Part> operation) {
        order.parts().stream()
                .sorted(Comparator.comparing(ServiceOrder.ServiceOrderPart::partId))
                .forEach(item -> {
                    Part part = repository
                            .findById(item.partId())
                            .orElseThrow(() -> new ResourceNotFoundException("Part not found"));
                    operation.accept(part, item.quantity());
                    repository.save(part);
                });
    }
}
