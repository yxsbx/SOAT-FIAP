package br.com.autocarehub.application.port.out;

import java.util.List;

import br.com.autocarehub.domain.model.DemoLead;

public interface DemoLeadRepository {

    DemoLead save(DemoLead demoLead);

    List<DemoLead> findAll();
}
