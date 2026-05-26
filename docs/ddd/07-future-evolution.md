# Future Evolution

Este documento descreve possibilidades futuras. Os itens abaixo nao devem ser interpretados como funcionalidades ja
implementadas no MVP.

## App do Cliente

Evolução futura para permitir que clientes acompanhem veículos, ordens de serviço, orçamentos e historico de
atendimentos por aplicativo.

Possiveis capacidades:

- Login do cliente.
- Consulta das proprias ordens.
- Aprovação de orçamento.
- Acompanhamento de status.
- Recebimento de notificacoes.
- Historico de manutencoes.

No MVP, existe apenas API para login, consulta de ordens proprias e aprovação de orçamento proprio.

## Painel da Oficina

Evolução futura para uma interface web administrativa usada por funcionarios e administradores.

Possiveis capacidades:

- Dashboard operacional.
- Fila de ordens de serviço.
- Indicadores de estoque.
- Indicadores de faturamento.
- Cadastro assistido de clientes e veículos.
- Acompanhamento visual de status.

O MVP avaliado permanece focado no backend. Existe um frontend demonstrativo no repositorio para apoiar apresentação e
validação visual, mas a entrega obrigatoria do Tech Challenge deve ser avaliada pelas APIs, contrato OpenAPI, testes e
documentação backend.

## Marketplace de Lojas de Peças

Evolução futura para conectar oficinas a lojas de peças e fornecedores.

Possiveis capacidades:

- Busca de peças em lojas parceiras.
- Comparação de preco e prazo.
- Reserva ou compra de peças.
- Cotação automatica durante montagem do orçamento.

No MVP, peças e estoque sao internos da oficina.

## Cupons

Evolução futura para campanhas promocionais.

Possiveis capacidades:

- Cupons por cliente.
- Cupons por serviço.
- Cupons por parceiro.
- Descontos por periodo.

No MVP, nao ha motor de desconto ou cupom.

## Agendamento

Evolução futura para permitir marcação de horarios.

Possiveis capacidades:

- Agenda da oficina.
- Disponibilidade por mecanico ou box.
- Reagendamento.
- Confirmação de horario.

No MVP, a ordem de serviço nao possui agenda.

## Serviços 24h

Evolução futura para atendimento emergencial.

Possiveis capacidades:

- Solicitar atendimento fora do horario comercial.
- Acionar guincho ou parceiro.
- Prioridade de atendimento.

No MVP, nao ha operação emergencial.

## Integração com Lava-Jatos e Outros Serviços Automotivos

Evolução futura para ampliar o ecossistema automotivo.

Possiveis capacidades:

- Oferecer lava-jato como serviço parceiro.
- Integrar funilaria, pintura, estetica automotiva e guincho.
- Combinar serviços em pacotes.
- Gerenciar repasse financeiro entre parceiros.

No MVP, o catalogo de serviços pertence somente a oficina e nao ha integração externa.
