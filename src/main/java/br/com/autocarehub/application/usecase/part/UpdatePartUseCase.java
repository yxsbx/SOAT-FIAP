package br.com.autocarehub.application.usecase.part;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.domain.Money;
import br.com.autocarehub.domain.Part;

import java.util.UUID;

public class UpdatePartUseCase {

    private final PartRepository partRepository;

    public UpdatePartUseCase(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part execute(Command command) {
        Part part =
                partRepository
                        .findById(command.partId())
                        .orElseThrow(() -> new ResourceNotFoundException("Part not found"));
        part.update(
                command.name(),
                command.description(),
                command.sku(),
                command.category(),
                command.subcategory(),
                command.brand(),
                command.costPrice(),
                command.unitPrice(),
                command.minimumStock());
        if (command.active()) {
            part.activate();
        } else {
            part.deactivate();
        }
        return partRepository.save(part);
    }

    public record Command(
            UUID partId,
            String name,
            String description,
            String sku,
            String category,
            String subcategory,
            String brand,
            Money costPrice,
            Money unitPrice,
            int minimumStock,
            boolean active) {
    }
}
