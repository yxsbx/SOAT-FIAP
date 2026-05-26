# Event Storming

Este documento descreve os fluxos principais em formato textual. Ele separa comandos, eventos, atores, politicas e
regras para deixar claro o comportamento do MVP.

## Fluxo de Criação e Acompanhamento da Ordem de Servico

### Atores

- `ADMIN`: pode executar todos os comandos.
- `EMPLOYEE`: pode gerenciar clientes, veículos, serviços, peças e ordens de serviço.
- `CUSTOMER`: pode consultar suas proprias ordens e aprovar seu proprio orçamento.

### Comandos

- Criar cliente.
- Criar veículo.
- Criar ordem de serviço.
- Adicionar serviço a ordem de serviço.
- Adicionar peça a ordem de serviço.
- Gerar orçamento.
- Aprovar orçamento.
- Atualizar status da ordem de serviço.
- Consultar ordem de serviço.
- Listar ordens de serviço por cliente.

### Eventos

- Cliente criado.
- Veículo criado.
- Ordem de serviço criada.
- Servico adicionado a ordem de serviço.
- Peça adicionada a ordem de serviço.
- Estoque de peça reduzido.
- Orcamento gerado.
- Orcamento aprovado.
- Diagnostico iniciado.
- Execução iniciada.
- Ordem de serviço finalizada.
- Veículo entregue.

### Politicas

- Ao vincular uma peça a um orçamento, a peça pode ser reservada para impedir venda duplicada.
- Ao aprovar um orçamento, a reserva das peças e convertida em baixa definitiva de estoque.
- Se uma reserva for liberada, a quantidade volta a ficar disponivel no estoque.
- Um cliente autenticado so pode consultar ordens vinculadas ao seu proprio `customerId`.
- Um cliente autenticado so pode aprovar orçamento de ordem vinculada ao seu proprio `customerId`.
- `ADMIN` e `EMPLOYEE` podem executar o fluxo operacional da oficina.

### Regras

- Uma ordem de serviço deve estar vinculada a um cliente e a um veículo.
- O veículo deve pertencer ao cliente informado na criação da ordem de serviço.
- O orçamento requer pelo menos um serviço ou uma peça.
- Uma ordem so pode iniciar execução depois do orçamento gerado e aprovado.
- Uma ordem so pode ser finalizada quando estiver em execução.
- Uma ordem so pode ser entregue depois de finalizada.
- Itens da ordem nao podem ser alterados depois que a ordem estiver aguardando aprovação, em execução, finalizada ou
  entregue.

## Fluxo de Gestao de Peças e Insumos

### Atores

- `ADMIN`.
- `EMPLOYEE`.

### Comandos

- Criar peça ou insumo.
- Atualizar peça ou insumo.
- Atualizar estoque.
- Registrar entrada, saida ou venda isolada.
- Reservar peça para orçamento.
- Liberar reserva de peça.
- Confirmar reserva como baixa definitiva.
- Remover logicamente peça ou insumo.
- Consultar peças.
- Listar peças com filtro de baixo estoque.

### Eventos

- Peça criada.
- Peça atualizada.
- Estoque atualizado.
- Movimento de estoque registrado.
- Estoque reservado.
- Reserva liberada.
- Reserva confirmada como venda ou consumo.
- Peça desativada.
- Estoque reduzido por uso em ordem de serviço.

### Politicas

- O estoque minimo pode ser usado para identificar baixo estoque.
- A baixa de estoque acontece quando a peça e adicionada a ordem de serviço.

### Regras

- Estoque nao pode ser negativo.
- Quantidade movimentada deve ser maior que zero.
- Nao e permitido reduzir estoque acima da quantidade disponivel.
- Preco unitario de peça deve ser maior que zero.

## Fluxo de Catálogo de Serviços

### Comandos

- Criar serviço da oficina.
- Atualizar serviço da oficina.
- Remover logicamente serviço da oficina.
- Consultar serviços da oficina.

### Eventos

- Servico criado.
- Servico atualizado.
- Servico desativado.

### Regras

- Preco base deve ser maior que zero.
- Tempo estimado deve ser maior que zero.
- Nome e descrição sao obrigatorios.
