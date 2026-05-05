package br.com.autocarehub.application.repository;

import br.com.autocarehub.domain.Part;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartRepository {

    Part save(Part part);

    Optional<Part> findById(UUID id);

    List<Part> findAll();
}
