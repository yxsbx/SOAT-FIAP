package br.com.autocarehub.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import br.com.autocarehub.domain.model.Part;

public interface PartRepository {

    Part save(Part part);

    Optional<Part> findById(UUID id);

    List<Part> findAll();
}
