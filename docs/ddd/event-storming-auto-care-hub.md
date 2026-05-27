# Event Storming - AutoCare Hub

Este documento descreve os fluxos de Event Storming exigidos para o MVP: criacao/acompanhamento da Ordem de Servico e
gestao de pecas e insumos.

## Fluxo 1 - Criacao e Acompanhamento da Ordem de Servico

### Atores

- Cliente final
- Atendente da oficina
- Mecanico
- Admin da oficina
- Sistema AutoCare Hub

### Comandos

- IdentificarCliente
- CadastrarCliente
- ConfirmarDadosCliente
- CadastrarVeiculo
- SelecionarVeiculo
- CriarOrdemServico
- IniciarDiagnostico
- IncluirServicoSolicitado
- IncluirPecaNaOrdem
- GerarOrcamento
- EnviarOrcamento
- AprovarOrcamento
- RecusarOrcamento
- IniciarExecucao
- FinalizarServico
- EntregarVeiculo
- ConsultarAcompanhamentoOS

### Eventos de Dominio

- ClienteIdentificado
- ClienteCadastrado
- VeiculoCadastrado
- VeiculoSelecionado
- OrdemServicoCriada
- DiagnosticoIniciado
- ServicoSolicitadoIncluido
- PecaIncluidaNaOrdem
- OrcamentoGerado
- OrcamentoEnviado
- OrcamentoAprovado
- OrcamentoRecusado
- OrdemServicoLiberadaParaExecucao
- ExecucaoIniciada
- ServicoFinalizado
- VeiculoEntregue
- AcompanhamentoOSConsultado

### Agregados Envolvidos

- Customer
- Vehicle
- ServiceOrder
- WorkshopService
- Part
- Budget

### Politicas

- Se o cliente nao existir, deve ser cadastrado antes da OS.
- Se o veiculo nao existir, deve ser cadastrado e vinculado ao cliente.
- Se o orçamento for gerado, a OS deve ir para `AGUARDANDO_APROVACAO`.
- Se o orçamento for aprovado, a OS pode iniciar execucao.
- Se houver pecas no orçamento, elas devem ser reservadas antes da aprovacao.
- Se a OS nao pertence ao cliente autenticado, a consulta deve ser negada.

### Sistemas Externos

No MVP nao ha integracao externa real. Possiveis sistemas futuros:

- gateway de pagamento;
- envio de e-mail;
- WhatsApp/SMS;
- catalogo externo de pecas.

### Regras de Negocio

- OS deve ter cliente e veiculo.
- OS deve ter ao menos um servico solicitado.
- Documento deve ser CPF/CNPJ valido.
- Placa deve estar em formato brasileiro antigo ou Mercosul.
- Orçamento deve calcular total de servicos, pecas e total geral.
- Execucao nao pode iniciar sem aprovacao.
- OS finalizada nao pode voltar para diagnostico.
- Itens da OS nao podem ser alterados apos geracao do orçamento.

### Possiveis Excecoes

- ClienteNaoEncontrado
- VeiculoNaoEncontrado
- VeiculoNaoPertenceAoCliente
- DocumentoInvalido
- PlacaInvalida
- ServicoInativo
- PecaInativa
- EstoqueInsuficiente
- TransicaoStatusInvalida
- AcessoNaoAutorizado

### Fluxo Principal

1. Atendente identifica cliente por CPF/CNPJ.
2. Sistema emite `ClienteIdentificado`.
3. Se necessario, atendente cadastra cliente.
4. Sistema emite `ClienteCadastrado`.
5. Atendente seleciona ou cadastra veiculo.
6. Sistema emite `VeiculoSelecionado` ou `VeiculoCadastrado`.
7. Atendente cria OS com defeitos percebidos.
8. Sistema emite `OrdemServicoCriada`.
9. Servicos e pecas sao incluidos.
10. Sistema emite `ServicoSolicitadoIncluido` e `PecaIncluidaNaOrdem`.
11. Orçamento e gerado.
12. Sistema emite `OrcamentoGerado` e `OrcamentoEnviado`.
13. Cliente aprova.
14. Sistema emite `OrcamentoAprovado`.
15. Oficina inicia execucao.
16. Sistema emite `ExecucaoIniciada`.
17. Servico e finalizado e veiculo entregue.
18. Sistema emite `ServicoFinalizado` e `VeiculoEntregue`.

### Fluxos Alternativos

- Cliente nao existe: cadastrar cliente antes da OS.
- Veiculo nao existe: cadastrar veiculo antes da OS.
- Cliente recusa orçamento: manter OS sem execucao e liberar pecas reservadas.
- Estoque insuficiente: impedir inclusao/reserva da peca e alertar atendente.
- Cliente consulta OS de outro cliente: retornar acesso negado.

### Diagrama

```mermaid
flowchart TD
    C1["Comando: IdentificarCliente"] --> E1["Evento: ClienteIdentificado"]
    E1 --> P1{Cliente existe?}
    P1 -- Nao --> C2["Comando: CadastrarCliente"]
    C2 --> E2["Evento: ClienteCadastrado"]
    P1 -- Sim --> C3["Comando: SelecionarVeiculo"]
    E2 --> C3
    C3 --> P2{Veiculo existe?}
    P2 -- Nao --> C4["Comando: CadastrarVeiculo"]
    C4 --> E3["Evento: VeiculoCadastrado"]
    P2 -- Sim --> E4["Evento: VeiculoSelecionado"]
    E3 --> C5["Comando: CriarOrdemServico"]
    E4 --> C5
    C5 --> E5["Evento: OrdemServicoCriada"]
    E5 --> C6["Comando: IncluirServicoSolicitado"]
    C6 --> E6["Evento: ServicoSolicitadoIncluido"]
    E6 --> C7["Comando: IncluirPecaNaOrdem"]
    C7 --> E7["Evento: PecaIncluidaNaOrdem"]
    E7 --> C8["Comando: GerarOrcamento"]
    C8 --> E8["Evento: OrcamentoGerado"]
    E8 --> E9["Evento: OrcamentoEnviado"]
    E9 --> C9["Comando: AprovarOrcamento"]
    C9 --> E10["Evento: OrcamentoAprovado"]
    E10 --> C10["Comando: IniciarExecucao"]
    C10 --> E11["Evento: ExecucaoIniciada"]
    E11 --> C11["Comando: FinalizarServico"]
    C11 --> E12["Evento: ServicoFinalizado"]
    E12 --> C12["Comando: EntregarVeiculo"]
    C12 --> E13["Evento: VeiculoEntregue"]
```

## Fluxo 2 - Gestao de Pecas e Insumos

### Atores

- Admin da oficina
- Funcionario autorizado
- Sistema AutoCare Hub

### Comandos

- CadastrarPeca
- EditarPeca
- RegistrarEntradaEstoque
- RegistrarSaidaEstoque
- VenderPecaIsolada
- ReservarPeca
- LiberarReservaPeca
- BaixarPecaDoEstoque
- ConfigurarPrazoReserva
- ConsultarEstoqueBaixo

### Eventos de Dominio

- PecaCadastrada
- PecaAtualizada
- EstoqueAtualizado
- EntradaEstoqueRegistrada
- SaidaEstoqueRegistrada
- VendaPecaRegistrada
- PecaReservada
- ReservaPecaLiberada
- PecaBaixadaDoEstoque
- PrazoReservaConfigurado
- EstoqueBaixoIdentificado
- EstoqueInsuficienteIdentificado

### Agregados Envolvidos

- Part
- StockMovement
- ServiceOrder
- Budget

### Politicas

- Quantidade nao pode ser negativa.
- Preco nao pode ser negativo.
- Estoque disponivel considera estoque total menos reservado.
- Peca vinculada a orçamento deve ser reservada, nao baixada imediatamente.
- Peca e baixada quando orçamento e aprovado ou venda e registrada.
- Reserva deve ser liberada se orçamento for recusado ou expirar.
- Estoque baixo deve ser identificado quando disponibilidade for menor ou igual ao estoque minimo.

### Sistemas Externos

Nao ha sistemas externos no MVP. Futuramente podem existir fornecedores, marketplaces e sistemas fiscais.

### Regras de Negocio

- Nome, SKU, categoria, marca e preco de venda sao obrigatorios.
- Estoque minimo nao pode ser negativo.
- Baixa maior que estoque disponivel deve ser bloqueada.
- Reserva maior que disponibilidade deve ser bloqueada.
- Confirmacao de reserva reduz estoque total e reserva.
- Liberacao de reserva reduz apenas a quantidade reservada.

### Possiveis Excecoes

- PecaNaoEncontrada
- PecaInativa
- QuantidadeInvalida
- PrecoInvalido
- EstoqueInsuficiente
- ReservaInexistente
- PrazoReservaInvalido

### Fluxo Principal

1. Admin cadastra peca.
2. Sistema emite `PecaCadastrada`.
3. Funcionario registra entrada.
4. Sistema emite `EntradaEstoqueRegistrada` e `EstoqueAtualizado`.
5. Peca e incluida em orçamento.
6. Sistema tenta reservar.
7. Se houver disponibilidade, emite `PecaReservada`.
8. Cliente aprova orçamento.
9. Sistema emite `PecaBaixadaDoEstoque`.
10. Sistema atualiza quantidade disponivel.

### Fluxos Alternativos

- Estoque insuficiente: emitir `EstoqueInsuficienteIdentificado` e impedir reserva/baixa.
- Orçamento recusado: liberar reserva.
- Venda isolada: baixar estoque sem OS.
- Entrada de estoque: aumentar quantidade total.
- Saida administrativa: reduzir quantidade, respeitando disponibilidade.

### Diagrama

```mermaid
flowchart TD
    C1["Comando: CadastrarPeca"] --> E1["Evento: PecaCadastrada"]
    E1 --> C2["Comando: RegistrarEntradaEstoque"]
    C2 --> E2["Evento: EntradaEstoqueRegistrada"]
    E2 --> E3["Evento: EstoqueAtualizado"]
    E3 --> C3["Comando: ReservarPeca"]
    C3 --> P1{Ha estoque disponivel?}
    P1 -- Sim --> E4["Evento: PecaReservada"]
    P1 -- Nao --> E5["Evento: EstoqueInsuficienteIdentificado"]
    E4 --> P2{Orçamento aprovado?}
    P2 -- Sim --> C4["Comando: BaixarPecaDoEstoque"]
    C4 --> E6["Evento: PecaBaixadaDoEstoque"]
    E6 --> E7["Evento: EstoqueAtualizado"]
    P2 -- Nao --> C5["Comando: LiberarReservaPeca"]
    C5 --> E8["Evento: ReservaPecaLiberada"]
```

## Visao Integrada dos Eventos

```mermaid
sequenceDiagram
    participant Atendimento
    participant OS as OrdemServico
    participant Orc as Orçamento
    participant Est as Estoque
    participant Cliente

    Atendimento->>OS: CriarOrdemServico
    OS-->>Atendimento: OrdemServicoCriada
    Atendimento->>OS: IncluirServicoSolicitado
    Atendimento->>OS: IncluirPecaNaOrdem
    Atendimento->>Orc: GerarOrcamento
    Orc->>Est: ReservarPeca
    Est-->>Orc: PecaReservada
    Orc-->>Cliente: OrcamentoEnviado
    Cliente->>Orc: AprovarOrcamento
    Orc->>Est: BaixarPecaDoEstoque
    Est-->>Orc: EstoqueAtualizado
    Orc-->>OS: OrcamentoAprovado
    Atendimento->>OS: IniciarExecucao
    Atendimento->>OS: FinalizarServico
    Atendimento->>OS: EntregarVeiculo
```
