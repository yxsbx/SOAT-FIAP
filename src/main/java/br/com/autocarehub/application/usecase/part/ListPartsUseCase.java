package br.com.autocarehub.application.usecase.part;

import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.domain.Part;
import java.util.List;

public class ListPartsUseCase {

    private final PartRepository partRepository;

    public ListPartsUseCase(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<Part> execute() {
        return partRepository.findAll();
    }
}
