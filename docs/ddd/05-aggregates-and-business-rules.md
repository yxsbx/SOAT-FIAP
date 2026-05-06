# Aggregates and Business Rules

## Customer

### Responsabilidade

Representa o cliente atendido pela oficina.

### Regras no MVP

- Nome e obrigatorio.
- Documento e obrigatorio e deve ser CPF ou CNPJ valido.
- Telefone e obrigatorio.
- Email e obrigatorio e deve conter `@`.
- Cliente pode ser ativado ou desativado.

## Vehicle

### Responsabilidade

Representa um veiculo pertencente a um cliente.

### Regras no MVP

- Veiculo deve estar vinculado a um cliente.
- Placa e obrigatoria e deve seguir o formato aceito pelo dominio.
- Marca e modelo sao obrigatorios.
- Ano deve ser maior ou igual a 1900.
- Quilometragem nao pode ser negativa.
- Quilometragem nao pode diminuir quando atualizada por operacao especifica.
- Veiculo pode ser ativado ou desativado.

## ServiceOrder

### Responsabilidade

Representa o atendimento da oficina para um veiculo de um cliente.

### Regras da OS

- Ordem deve estar vinculada a um cliente.
- Ordem deve estar vinculada a um veiculo.
- Veiculo informado deve pertencer ao cliente informado.
- Ordem inicia com status `RECEIVED`.
- Diagnostico pode iniciar a partir de `RECEIVED`.
- Itens da ordem podem ser alterados antes do orcamento ser gerado.
- Itens nao podem ser alterados nos status `WAITING_APPROVAL`, `IN_PROGRESS`, `FINISHED` ou `DELIVERED`.

### Regras de Orcamento

- Orcamento exige pelo menos um servico ou uma peca.
- Total do orcamento e calculado pela soma dos servicos e pecas.
- Ao gerar orcamento, status muda para `WAITING_APPROVAL`.
- Aprovacao so pode ocorrer quando a ordem estiver em `WAITING_APPROVAL`.
- Aprovacao exige que o orcamento tenha sido gerado.

### Regras de Status

- `RECEIVED` pode ir para `IN_DIAGNOSIS`.
- `WAITING_APPROVAL` e definido ao gerar orcamento.
- `IN_PROGRESS` exige orcamento aprovado.
- `FINISHED` exige status `IN_PROGRESS`.
- `DELIVERED` exige status `FINISHED`.
- Ordem nao pode retornar para `RECEIVED` via atualizacao de status.

## Part

### Responsabilidade

Representa uma peca ou insumo usado pela oficina.

### Regras de Estoque

- Estoque nao pode ser negativo.
- Estoque minimo nao pode ser negativo.
- Quantidade de movimentacao deve ser maior que zero.
- Nao e permitido reduzir estoque acima da quantidade disponivel.
- Peca deve informar se ha estoque disponivel para uma quantidade solicitada.
- Peca pode ser ativada ou desativada.

### Regras Comerciais

- Nome e obrigatorio.
- SKU e obrigatorio.
- Categoria e obrigatoria.
- Marca e obrigatoria.
- Preco unitario deve ser maior que zero.

## WorkshopService

### Responsabilidade

Representa um servico oferecido pela oficina.

### Regras no MVP

- Nome e obrigatorio.
- Descricao e obrigatoria.
- Preco base deve ser maior que zero.
- Tempo estimado deve ser maior que zero.
- Servico pode ser ativado ou desativado.
