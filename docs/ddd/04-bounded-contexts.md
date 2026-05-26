# Bounded Contexts

## Customer and Vehicle Management

Contexto responsavel por manter clientes e veículos.

### MVP

- Criar, atualizar, consultar, listar e desativar clientes.
- Criar, atualizar, consultar, listar e desativar veículos.
- Listar veículos de um cliente.
- Validar documento do cliente.
- Validar placa do veículo.

### Fora do MVP

- Historico completo de propriedade de veículos.
- Multiplo proprietario por veículo.
- Integração com bases externas de placa ou documento.

## Service Order Management

Contexto responsavel pelo ciclo de vida da ordem de serviço.

### MVP

- Criar ordem de serviço.
- Adicionar serviços.
- Adicionar peças.
- Gerar orçamento.
- Aprovar orçamento.
- Atualizar status.
- Consultar ordens.
- Listar ordens por cliente.

### Fora do MVP

- Timeline detalhada de eventos.
- Comentarios e anexos.
- Notificacoes ao cliente.
- Assinatura digital de aprovação.

## Inventory Management

Contexto responsavel por peças, insumos e estoque.

### MVP

- Criar, atualizar, consultar, listar e desativar peças.
- Atualizar estoque.
- Registrar entrada, saida e venda isolada.
- Reservar e liberar peças vinculadas a orçamentos.
- Identificar baixo estoque por comparação com estoque minimo.
- Baixar estoque quando uma reserva e confirmada ou quando o orçamento e aprovado.

### Fora do MVP

- Entrada fiscal.
- Lotes.
- Fornecedores.
- Cotação automatica.
- Lotes, fornecedores e rastreabilidade fiscal de entradas.

## Service Catalog Management

Contexto responsavel pelo catalogo de serviços oferecidos pela oficina.

### MVP

- Criar, atualizar, consultar, listar e desativar serviços da oficina.
- Manter preco base e tempo estimado.

### Fora do MVP

- Precificação dinamica.
- Pacotes de serviços.
- Promocoes.
- Variação de preco por modelo de veículo.

## Future Marketplace

Contexto futuro para conectar oficina, lojas de peças e outros parceiros automotivos.

### MVP

Nao implementado.

### Evolução Futura

- Marketplace de lojas de peças.
- Cupons.
- Comparação de precos.
- Integração com lava-jatos.
- Serviços automotivos parceiros.
- Serviços 24h.
- Agendamento com parceiros.
