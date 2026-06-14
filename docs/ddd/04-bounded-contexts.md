# Bounded Contexts

## Gestão de Clientes e Veículos

Contexto responsável por manter clientes e veículos.

### MVP

- Criar, atualizar, consultar, listar e desativar clientes.
- Criar, atualizar, consultar, listar e desativar veículos.
- Listar veículos de um cliente.
- Validar documento do cliente.
- Validar placa do veículo.

### Fora do MVP

- Histórico completo de propriedade de veículos.
- Múltiplo proprietário por veículo.
- Integração com bases externas de placa ou documento.

## Gestão de Ordens de Serviço

Contexto responsável pelo ciclo de vida da Ordem de Serviço.

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
- Comentários e anexos.
- Notificações ao cliente.
- Assinatura digital de aprovação.

## Gestão de Peças e Estoque

Contexto responsável por peças, insumos e estoque.

### MVP

- Criar, atualizar, consultar, listar e desativar peças.
- Atualizar estoque.
- Registrar entrada, saída e venda isolada.
- Reservar e liberar peças vinculadas a orçamentos.
- Identificar baixo estoque por comparação com estoque mínimo.
- Baixar estoque quando uma reserva é confirmada ou quando o orçamento é aprovado.

### Fora do MVP

- Entrada fiscal.
- Lotes.
- Fornecedores.
- Cotação automática.
- Lotes, fornecedores e rastreabilidade fiscal de entradas.

## Catálogo de Serviços

Contexto responsável pelo catálogo de serviços oferecidos pela oficina.

### MVP

- Criar, atualizar, consultar, listar e desativar serviços da oficina.
- Manter preço base e tempo estimado.

### Fora do MVP

- Precificação dinâmica.
- Pacotes de serviços.
- Promoções.
- Variação de preço por modelo de veículo.

## Future Marketplace

Contexto futuro para conectar oficina, lojas de peças e outros parceiros automotivos.

### MVP

Não implementado.

### Evolução Futura

- Marketplace de lojas de peças.
- Cupons.
- Comparação de preços.
- Integração com lava-jatos.
- Serviços automotivos parceiros.
- Serviços 24h.
- Agendamento com parceiros.
