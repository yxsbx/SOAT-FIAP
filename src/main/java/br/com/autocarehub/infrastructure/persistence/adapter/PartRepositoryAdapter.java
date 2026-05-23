package br.com.autocarehub.infrastructure.persistence.adapter;

import br.com.autocarehub.application.repository.PartRepository;
import br.com.autocarehub.domain.Part;
import br.com.autocarehub.infrastructure.persistence.mapper.PartJpaMapper;
import br.com.autocarehub.infrastructure.persistence.repository.PartJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PartRepositoryAdapter implements PartRepository {

    private final PartJpaRepository partJpaRepository;

    public PartRepositoryAdapter(PartJpaRepository partJpaRepository) {
        this.partJpaRepository = partJpaRepository;
    }

    @Override
    public Part save(Part part) {
        return PartJpaMapper.toDomain(partJpaRepository.save(PartJpaMapper.toEntity(part)));
    }

    @Override
    public Optional<Part> findById(UUID id) {
        return partJpaRepository.findById(id).map(PartJpaMapper::toDomain);
    }

    @Override
    public List<Part> findAll() {
        return partJpaRepository.findAll().stream().map(PartJpaMapper::toDomain).toList();
    }
}
