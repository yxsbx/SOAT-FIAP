# Event Storming

Este documento descreve os fluxos principais em formato textual. Ele separa comandos, eventos, atores, politicas e
regras para deixar claro o comportamento do MVP.

## Fluxo de Criacao e Acompanhamento da Ordem de Servico

### Atores

- `ADMIN`: pode executar todos os comandos.
- `EMPLOYEE`: pode gerenciar clientes, veiculos, serviços, pecas e ordens de servico.
- `CUSTOMER`: pode consultar suas proprias ordens e aprovar seu proprio orcamento.

### Comandos

- Criar cliente.
- Criar veiculo.
- Criar ordem de servico.
- Adicionar servico a ordem de servico.
- Adicionar peca a ordem de servico.
- Gerar orcamento.
- Aprovar orcamento.
- Atualizar status da ordem de servico.
- Consultar ordem de servico.
- Listar ordens de servico por cliente.

### Eventos

- Cliente criado.
- Veiculo criado.
- Ordem de servico criada.
- Servico adicionado a ordem de servico.
- Peca adicionada a ordem de servico.
- Estoque de peca reduzido.
- Orcamento gerado.
- Orcamento aprovado.
- Diagnostico iniciado.
- Execucao iniciada.
- Ordem de servico finalizada.
- Veiculo entregue.

### Politicas

- Ao adicionar uma peca a ordem de servico, o estoque da peca e reduzido.
- Um cliente autenticado so pode consultar ordens vinculadas ao seu proprio `customerId`.
- Um cliente autenticado so pode aprovar orcamento de ordem vinculada ao seu proprio `customerId`.
- `ADMIN` e `EMPLOYEE` podem executar o fluxo operacional da oficina.

### Regras

- Uma ordem de servico deve estar vinculada a um cliente e a um veiculo.
- O veiculo deve pertencer ao cliente informado na criacao da ordem de servico.
- O orcamento requer pelo menos um servico ou uma peca.
- Uma ordem so pode iniciar execucao depois do orcamento gerado e aprovado.
- Uma ordem so pode ser finalizada quando estiver em execucao.
- Uma ordem so pode ser entregue depois de finalizada.
- Itens da ordem nao podem ser alterados depois que a ordem estiver aguardando aprovacao, em execucao, finalizada ou
  entregue.

## Fluxo de Gestao de Pecas e Insumos

### Atores

- `ADMIN`.
- `EMPLOYEE`.

### Comandos

- Criar peca ou insumo.
- Atualizar peca ou insumo.
- Atualizar estoque.
- Remover logicamente peca ou insumo.
- Consultar pecas.
- Listar pecas com filtro de baixo estoque.

### Eventos

- Peca criada.
- Peca atualizada.
- Estoque atualizado.
- Peca desativada.
- Estoque reduzido por uso em ordem de servico.

### Politicas

- O estoque minimo pode ser usado para identificar baixo estoque.
- A baixa de estoque acontece quando a peca e adicionada a ordem de servico.

### Regras

- Estoque nao pode ser negativo.
- Quantidade movimentada deve ser maior que zero.
- Nao e permitido reduzir estoque acima da quantidade disponivel.
- Preco unitario de peca deve ser maior que zero.

## Fluxo de Catálogo de Serviços

### Comandos

- Criar servico da oficina.
- Atualizar servico da oficina.
- Remover logicamente servico da oficina.
- Consultar serviços da oficina.

### Eventos

- Servico criado.
- Servico atualizado.
- Servico desativado.

### Regras

- Preco base deve ser maior que zero.
- Tempo estimado deve ser maior que zero.
- Nome e descricao sao obrigatorios.
