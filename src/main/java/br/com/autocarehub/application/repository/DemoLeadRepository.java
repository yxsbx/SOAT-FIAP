package br.com.autocarehub.application.repository;

import br.com.autocarehub.domain.DemoLead;
import java.util.List;

public interface DemoLeadRepository {

  DemoLead save(DemoLead demoLead);

  List<DemoLead> findAll();
}
