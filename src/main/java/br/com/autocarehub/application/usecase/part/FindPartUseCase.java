package br.com.autocarehub.application.usecase.part;

import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.domain.Part;

import java.util.UUID;

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
