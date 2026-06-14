package br.com.autocarehub.domain.model;

import br.com.autocarehub.domain.exception.DomainException;
import br.com.autocarehub.domain.service.DomainValidation;

import java.time.LocalDateTime;
import java.util.UUID;

public record DemoLead(
    UUID id,
    String contactName,
    String companyName,
    String demoProfile,
    String email,
    String phone,
    String cnpj,
    String city,
    String message,
    LocalDateTime createdAt) {}
