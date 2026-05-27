package br.com.autocarehub.application.usecase.demo;

import br.com.autocarehub.application.repository.DemoLeadRepository;
import br.com.autocarehub.domain.DemoLead;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterDemoLeadUseCase {

    private final DemoLeadRepository repository;

    public RegisterDemoLeadUseCase(DemoLeadRepository repository) {
        this.repository = repository;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    public DemoLead execute(Command command) {
        DemoLead demoLead =
                new DemoLead(
                        UUID.randomUUID(),
                        command.contactName().trim(),
                        command.companyName().trim(),
                        command.demoProfile().trim(),
                        command.email().trim().toLowerCase(),
                        command.phone().trim(),
                        command.cnpj().trim().toUpperCase(),
                        normalize(command.city()),
                        normalize(command.message()),
                        LocalDateTime.now());

        return repository.save(demoLead);
    }

    public record Command(
            String contactName,
            String companyName,
            String demoProfile,
            String email,
            String phone,
            String cnpj,
            String city,
            String message) {
    }
}
