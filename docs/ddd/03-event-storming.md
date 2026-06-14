# Event Storming

Este documento descreve os fluxos principais em formato textual. Ele separa comandos, eventos, atores, políticas e
regras para deixar claro o comportamento do MVP.

## Fluxo de Criação e Acompanhamento da Ordem de Serviço

### Atores

- `ADMIN`: pode executar todos os comandos.
- `EMPLOYEE`: pode gerenciar clientes, veículos, serviços, peças e ordens de serviço.
- `CUSTOMER`: pode consultar suas próprias ordens e aprovar seu próprio orçamento.

### Comandos

- Criar cliente.
- Criar veículo.
- Criar ordem de serviço.
- Adicionar serviço à Ordem de Serviço.
- Adicionar peça à Ordem de Serviço.
- Gerar orçamento.
- Aprovar orçamento.
- Atualizar status da ordem de serviço.
- Consultar ordem de serviço.
- Listar ordens de serviço por cliente.

### Eventos

- ClienteIdentificado.
- VeiculoCadastrado.
- OrdemServicoCriada.
- ServicoIncluidoNaOrdem.
- PecaIncluidaNaOrdem.
- OrcamentoGerado.
- OrcamentoEnviado.
- OrcamentoAprovado.
- OrdemServicoEmExecucao.
- OrdemServicoFinalizada.
- VeiculoEntregue.
- EstoqueAtualizado.
- PecaBaixadaDoEstoque.

### Políticas

- Ao vincular uma peça a um orçamento, a peça pode ser reservada para impedir venda duplicada.
- Ao aprovar um orçamento, a reserva das peças é convertida em baixa definitiva de estoque.
- Se uma reserva for liberada, a quantidade volta a ficar disponível no estoque.
- Um cliente autenticado só pode consultar ordens vinculadas ao seu próprio `customerId`.
- Um cliente autenticado só pode aprovar orçamento de ordem vinculada ao seu próprio `customerId`.
- `ADMIN` e `EMPLOYEE` podem executar o fluxo operacional da oficina.

### Regras

- Uma ordem de serviço deve estar vinculada a um cliente e a um veículo.
- O veículo deve pertencer ao cliente informado na criação da ordem de serviço.
- O orçamento requer pelo menos um serviço ou uma peça.
- Uma ordem só pode iniciar execução depois do orçamento gerado e aprovado.
- Uma ordem só pode ser finalizada quando estiver em execução.
- Uma ordem só pode ser entregue depois de finalizada.
- Itens da ordem não podem ser alterados depois que a ordem estiver aguardando aprovação, em execução, finalizada ou
  entregue.

## Fluxo de Gestão de Peças e Insumos

### Atores

- `ADMIN`.
- `EMPLOYEE`.

### Comandos

- Criar peça ou insumo.
- Atualizar peça ou insumo.
- Atualizar estoque.
- Registrar entrada, saída ou venda isolada.
- Reservar peça para orçamento.
- Liberar reserva de peça.
- Confirmar reserva como baixa definitiva.
- Remover logicamente peça ou insumo.
- Consultar peças.
- Listar peças com filtro de baixo estoque.

### Eventos

- PecaCadastrada.
- PecaAtualizada.
- EstoqueAtualizado.
- PecaReservada.
- ReservaPecaLiberada.
- PecaBaixadaDoEstoque.
- EstoqueInsuficienteIdentificado.

### Políticas

- O estoque mínimo pode ser usado para identificar baixo estoque.
- A peça vinculada a orçamento é reservada primeiro.
- A baixa definitiva acontece quando o orçamento é aprovado ou quando uma saída/venda é registrada.

### Regras

- Estoque não pode ser negativo.
- Quantidade movimentada deve ser maior que zero.
- Não é permitido reduzir estoque acima da quantidade disponível.
- Preço unitário de peça deve ser maior que zero.

## Fluxo de Catálogo de Serviços

### Comandos

- Criar serviço da oficina.
- Atualizar serviço da oficina.
- Remover logicamente serviço da oficina.
- Consultar serviços da oficina.

### Eventos

- Serviço criado.
- Serviço atualizado.
- Serviço desativado.

### Regras

- Preço base deve ser maior que zero.
- Tempo estimado deve ser maior que zero.
- Nome e descrição são obrigatórios.
