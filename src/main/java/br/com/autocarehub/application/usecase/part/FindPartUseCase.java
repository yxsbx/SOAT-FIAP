package br.com.autocarehub.application.usecase.part;

import java.util.UUID;

import br.com.autocarehub.application.exception.ResourceNotFoundException;
import br.com.autocarehub.application.port.out.PartRepository;
import br.com.autocarehub.domain.model.Part;

public class FindPartUseCase {

    private final PartRepository partRepository;

    public FindPartUseCase(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public Part execute(UUID partId) {
        return partRepository
                .findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found"));
    }
}
