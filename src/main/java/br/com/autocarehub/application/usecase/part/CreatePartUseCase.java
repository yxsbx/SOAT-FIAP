package br.com.autocarehub.application.usecase.part;

import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.domain.Money;
import br.com.autocarehub.domain.Part;

public class CreatePartUseCase {

    private final PartRepository partRepository;

    public CreatePartUseCase(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part execute(Command command) {
        Part part =
                new Part(
                        command.name(),
                        command.sku(),
                        command.category(),
                        command.subcategory(),
                        command.brand(),
                        command.costPrice(),
                        command.unitPrice(),
                        command.stockQuantity(),
                        command.minimumStock());
        return partRepository.save(part);
    }

    public record Command(
            String name,
            String sku,
            String category,
            String subcategory,
            String brand,
            Money costPrice,
            Money unitPrice,
            int stockQuantity,
            int minimumStock) {
    }
}
