# Evolução Futura

Este documento descreve possibilidades futuras. Os itens abaixo não devem ser interpretados como funcionalidades já
implementadas no MVP.

## App do Cliente

Evolução futura para permitir que clientes acompanhem veículos, ordens de serviço, orçamentos e histórico de
atendimentos por aplicativo.

Possíveis capacidades:

- Login do cliente.
- Consulta das próprias ordens.
- Aprovação de orçamento.
- Acompanhamento de status.
- Recebimento de notificações.
- Histórico de manutenções.

No MVP, existe apenas API para login, consulta de ordens próprias e aprovação de orçamento próprio.

## Painel da Oficina

Evolução futura para uma interface web administrativa usada por funcionários e administradores.

Possíveis capacidades:

- Dashboard operacional.
- Fila de ordens de serviço.
- Indicadores de estoque.
- Indicadores de faturamento.
- Cadastro assistido de clientes e veículos.
- Acompanhamento visual de status.

O MVP avaliado permanece focado no backend. Existe um frontend demonstrativo no repositório para apoiar apresentação e
validação visual, mas a entrega obrigatória do Tech Challenge deve ser avaliada pelas APIs, contrato OpenAPI, testes e
documentação backend.

## Marketplace de Lojas de Peças

Evolução futura para conectar oficinas a lojas de peças e fornecedores.

Possíveis capacidades:

- Busca de peças em lojas parceiras.
- Comparação de preço e prazo.
- Reserva ou compra de peças.
- Cotação automática durante montagem do orçamento.

No MVP, peças e estoque são internos da oficina.

## Cupons

Evolução futura para campanhas promocionais.

Possíveis capacidades:

- Cupons por cliente.
- Cupons por serviço.
- Cupons por parceiro.
- Descontos por período.

No MVP, não há motor de desconto ou cupom.

## Agendamento

Evolução futura para permitir marcação de horários.

Possíveis capacidades:

- Agenda da oficina.
- Disponibilidade por mecânico ou box.
- Reagendamento.
- Confirmação de horário.

No MVP, a ordem de serviço não possui agenda.

## Serviços 24h

Evolução futura para atendimento emergencial.

Possíveis capacidades:

- Solicitar atendimento fora do horário comercial.
- Acionar guincho ou parceiro.
- Prioridade de atendimento.

No MVP, não há operação emergencial.

## Integração com Lava-Jatos e Outros Serviços Automotivos

Evolução futura para ampliar o ecossistema automotivo.

Possíveis capacidades:

- Oferecer lava-jato como serviço parceiro.
- Integrar funilaria, pintura, estética automotiva e guincho.
- Combinar serviços em pacotes.
- Gerenciar repasse financeiro entre parceiros.

No MVP, o catálogo de serviços pertence somente à oficina e não há integração externa.
