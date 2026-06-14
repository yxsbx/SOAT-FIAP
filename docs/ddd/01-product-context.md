# Contexto do Produto

## Problema da Oficina

Oficinas mecânicas lidam com clientes, veículos, diagnósticos, orçamentos, peças, serviços executados e acompanhamento
de status. Quando esses dados ficam em planilhas, mensagens ou controles manuais, surgem problemas de rastreabilidade,
perda de histórico, dificuldade para controlar estoque e pouca clareza para o cliente sobre o andamento da ordem de
serviço.

O problema central do domínio é organizar o ciclo de atendimento da oficina desde o cadastro do cliente e do veículo até
a criação, composição, orçamento, aprovação, execução e entrega da ordem de serviço.

## Objetivo do MVP

O MVP do AutoCare Hub tem como objetivo oferecer uma API REST para centralizar os principais cadastros e fluxos
operacionais de uma oficina mecânica.

O foco do MVP é backend: disponibilizar casos de uso essenciais por API, com contrato OpenAPI, persistência relacional,
autenticação JWT, controle básico de perfis e migrations versionadas.

## Escopo Incluído no MVP

- Autenticação com JWT.
- Perfis `ADMIN`, `EMPLOYEE` e `CUSTOMER`.
- Cadastro e manutenção de clientes.
- Cadastro e manutenção de veículos.
- Cadastro e manutenção de serviços oferecidos pela oficina.
- Cadastro e manutenção de peças e insumos.
- Controle simples de estoque de peças.
- Criação de ordem de serviço.
- Associação de serviços e peças a uma ordem de serviço.
- Geração de orçamento.
- Aprovação de orçamento.
- Atualização de status da ordem de serviço.
- Consulta de ordens de serviço.
- Restrição para cliente consultar suas próprias ordens e aprovar seu próprio orçamento.

## Escopo Fora do MVP

- Aplicativo mobile do cliente.
- Interface web administrativa da oficina.
- Pagamentos online.
- Emissão fiscal.
- Notificações por e-mail, SMS ou WhatsApp.
- Agenda de horários.
- Marketplace de peças.
- Cupons e campanhas comerciais.
- Serviços 24h.
- Integração com guincho, lava-jatos, seguradoras ou outros parceiros.
- Controle financeiro completo.
- Multi-oficina ou franquias.
- Auditoria detalhada de eventos.

## Visão Futura do Produto

A visão futura é transformar o AutoCare Hub em uma plataforma automotiva mais ampla. A oficina teria um painel
operacional para acompanhar atendimentos, estoque, serviços, agenda e indicadores. O cliente teria um aplicativo para
consultar veículos, aprovar orçamentos, acompanhar status e contratar serviços.

Também existe potencial de evolução para marketplace de lojas de peças, cupons, agendamento online, atendimento 24h e
integrações com lava-jatos e outros serviços automotivos. Essas possibilidades não fazem parte do MVP atual e devem ser
tratadas como novos contextos ou extensões futuras.
