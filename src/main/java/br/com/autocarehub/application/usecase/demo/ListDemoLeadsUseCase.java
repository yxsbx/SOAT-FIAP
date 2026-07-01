package br.com.autocarehub.application.usecase.demo;

import java.util.List;

import br.com.autocarehub.application.port.out.DemoLeadRepository;
import br.com.autocarehub.domain.model.DemoLead;

public class ListDemoLeadsUseCase {

    private final DemoLeadRepository repository;

    public ListDemoLeadsUseCase(DemoLeadRepository repository) {
        this.repository = repository;
    }

    public List<DemoLead> execute() {
        return repository.findAll();
    }
}
