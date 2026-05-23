# Bounded Contexts

## Customer and Vehicle Management

Contexto responsavel por manter clientes e veiculos.

### MVP

- Criar, atualizar, consultar, listar e desativar clientes.
- Criar, atualizar, consultar, listar e desativar veiculos.
- Listar veiculos de um cliente.
- Validar documento do cliente.
- Validar placa do veiculo.

### Fora do MVP

- Historico completo de propriedade de veiculos.
- Multiplo proprietario por veiculo.
- Integracao com bases externas de placa ou documento.

## Service Order Management

Contexto responsavel pelo ciclo de vida da ordem de servico.

### MVP

- Criar ordem de servico.
- Adicionar serviços.
- Adicionar pecas.
- Gerar orcamento.
- Aprovar orcamento.
- Atualizar status.
- Consultar ordens.
- Listar ordens por cliente.

### Fora do MVP

- Timeline detalhada de eventos.
- Comentarios e anexos.
- Notificacoes ao cliente.
- Assinatura digital de aprovacao.

## Inventory Management

Contexto responsavel por pecas, insumos e estoque.

### MVP

- Criar, atualizar, consultar, listar e desativar pecas.
- Atualizar estoque.
- Identificar baixo estoque por comparacao com estoque minimo.
- Reduzir estoque ao adicionar peca a ordem de servico.

### Fora do MVP

- Entrada fiscal.
- Lotes.
- Fornecedores.
- Cotacao automatica.
- Reserva de estoque.

## Service Catalog Management

Contexto responsavel pelo catalogo de serviços oferecidos pela oficina.

### MVP

- Criar, atualizar, consultar, listar e desativar serviços da oficina.
- Manter preco base e tempo estimado.

### Fora do MVP

- Precificacao dinamica.
- Pacotes de serviços.
- Promocoes.
- Variacao de preco por modelo de veiculo.

## Future Marketplace

Contexto futuro para conectar oficina, lojas de pecas e outros parceiros automotivos.

### MVP

Nao implementado.

### Evolucao Futura

- Marketplace de lojas de pecas.
- Cupons.
- Comparacao de precos.
- Integracao com lava-jatos.
- Servicos automotivos parceiros.
- Servicos 24h.
- Agendamento com parceiros.
