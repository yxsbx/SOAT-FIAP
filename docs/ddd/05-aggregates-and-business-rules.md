# Agregados e Regras de Negócio

## Customer - Cliente

### Responsabilidade

Representa o cliente atendido pela oficina.

### Regras no MVP

- Nome é obrigatório.
- Documento é obrigatório e deve ser CPF ou CNPJ válido.
- Telefone é obrigatório.
- E-mail é obrigatório e deve conter `@`.
- Cliente pode ser ativado ou desativado.

## Vehicle - Veículo

### Responsabilidade

Representa um veículo pertencente a um cliente.

### Regras no MVP

- Veículo deve estar vinculado a um cliente.
- Placa é obrigatória e deve seguir o formato aceito pelo domínio.
- Marca e modelo são obrigatórios.
- Ano deve ser maior ou igual a 1900.
- Quilometragem não pode ser negativa.
- Quilometragem não pode diminuir quando atualizada por operação específica.
- Veículo pode ser ativado ou desativado.

## ServiceOrder - Ordem de Serviço

### Responsabilidade

Representa o atendimento da oficina para um veículo de um cliente. É o agregado principal do fluxo de atendimento.

### Regras da OS

- Ordem deve estar vinculada a um cliente.
- Ordem deve estar vinculada a um veículo.
- Veículo informado deve pertencer ao cliente informado.
- Ordem inicia com status `RECEBIDA`.
- Diagnóstico pode iniciar a partir de `RECEBIDA`.
- Itens da ordem podem ser alterados antes do orçamento ser gerado.
- Itens não podem ser alterados nos status `AGUARDANDO_APROVACAO`, `EM_EXECUCAO`, `FINALIZADA` ou `ENTREGUE`.

### Regras de Orçamento

- Orçamento exige pelo menos um serviço ou uma peça.
- Total do orçamento é calculado pela soma dos serviços e peças.
- Ao gerar orçamento, status muda para `AGUARDANDO_APROVACAO`.
- Aprovação só pode ocorrer quando a ordem estiver em `AGUARDANDO_APROVACAO`.
- Aprovação exige que o orçamento tenha sido gerado.

### Regras de Status

- `RECEBIDA` pode ir para `EM_DIAGNOSTICO`.
- `AGUARDANDO_APROVACAO` é definido ao gerar orçamento.
- `EM_EXECUCAO` exige orçamento aprovado.
- `FINALIZADA` exige status `EM_EXECUCAO`.
- `ENTREGUE` exige status `FINALIZADA`.
- Ordem não pode retornar para `RECEBIDA` via atualização de status.

## Part - Peça/Insumo

### Responsabilidade

Representa uma peça ou insumo usado pela oficina.

### Regras de Estoque

- Estoque não pode ser negativo.
- Estoque mínimo não pode ser negativo.
- Quantidade reservada não pode ser maior que o estoque total.
- Quantidade de movimentação deve ser maior que zero.
- Não é permitido reduzir estoque acima da quantidade disponível.
- Peça deve informar se há estoque disponível para uma quantidade solicitada.
- Reserva de estoque reduz a quantidade disponível, mas não reduz o estoque total.
- Confirmar uma reserva reduz o estoque total e a quantidade reservada.
- Liberar uma reserva devolve a quantidade para o estoque disponível.
- Reserva expirada pode ser liberada automaticamente quando a peça é consultada ou movimentada.
- Peça pode ser ativada ou desativada.

### Regras Comerciais

- Nome é obrigatório.
- SKU é obrigatório.
- Categoria é obrigatória.
- Marca é obrigatória.
- Preço unitário deve ser maior que zero.

## WorkshopService - Serviço

### Responsabilidade

Representa um serviço oferecido pela oficina.

### Regras no MVP

- Nome é obrigatório.
- Descrição é obrigatória.
- Preço base deve ser maior que zero.
- Tempo estimado deve ser maior que zero.
- Serviço pode ser ativado ou desativado.
