# Product Context

## Problema da Oficina

Oficinas mecanicas lidam com clientes, veiculos, diagnosticos, orcamentos, pecas, serviços executados e acompanhamento
de status. Quando esses dados ficam em planilhas, mensagens ou controles manuais, surgem problemas de rastreabilidade,
perda de historico, dificuldade para controlar estoque e pouca clareza para o cliente sobre o andamento da ordem de
servico.

O problema central do dominio e organizar o ciclo de atendimento da oficina desde o cadastro do cliente e do veiculo ate
a criacao, composicao, orcamento, aprovacao, execucao e entrega da ordem de servico.

## Objetivo do MVP

O MVP do AutoCare Hub tem como objetivo oferecer uma API REST para centralizar os principais cadastros e fluxos
operacionais de uma oficina mecanica.

O foco do MVP e backend: disponibilizar casos de uso essenciais por API, com contrato OpenAPI, persistencia relacional,
autenticacao JWT, controle basico de perfis e migrations versionadas.

## Escopo Incluido no MVP

- Autenticacao com JWT.
- Perfis `ADMIN`, `EMPLOYEE` e `CUSTOMER`.
- Cadastro e manutencao de clientes.
- Cadastro e manutencao de veículos.
- Cadastro e manutencao de serviços oferecidos pela oficina.
- Cadastro e manutencao de pecas e insumos.
- Controle simples de estoque de pecas.
- Criacao de ordem de servico.
- Associacao de serviços e pecas a uma ordem de servico.
- Geracao de orcamento.
- Aprovacao de orcamento.
- Atualizacao de status da ordem de servico.
- Consulta de ordens de servico.
- Restricao para cliente consultar suas proprias ordens e aprovar seu proprio orcamento.

## Escopo Fora do MVP

- Aplicativo mobile do cliente.
- Interface web administrativa da oficina.
- Pagamentos online.
- Emissao fiscal.
- Notificacoes por email, SMS ou WhatsApp.
- Agenda de horarios.
- Marketplace de pecas.
- Cupons e campanhas comerciais.
- Serviços 24h.
- Integracao com guincho, lava-jatos, seguradoras ou outros parceiros.
- Controle financeiro completo.
- Multi-oficina ou franquias.
- Auditoria detalhada de eventos.

## Visao Futura do Produto

A visao futura e transformar o AutoCare Hub em uma plataforma automotiva mais ampla. A oficina teria um painel
operacional para acompanhar atendimentos, estoque, serviços, agenda e indicadores. O cliente teria um aplicativo para
consultar veiculos, aprovar orcamentos, acompanhar status e contratar serviços.

Tambem existe potencial de evolucao para marketplace de lojas de pecas, cupons, agendamento online, atendimento 24h e
integracoes com lava-jatos e outros servicos automotivos. Essas possibilidades nao fazem parte do MVP atual e devem ser
tratadas como novos contextos ou extensoes futuras.
