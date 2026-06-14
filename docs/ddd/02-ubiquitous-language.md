# Linguagem Ubíqua

Este documento define a linguagem compartilhada do domínio. Os termos abaixo devem ser usados de forma consistente por negócio, desenvolvimento, documentação, testes e interface da API.

## Cliente

Pessoa física ou jurídica atendida pela oficina. No MVP, um cliente possui nome, CPF/CNPJ, telefone, e-mail, endereço e status ativo/inativo.

## Documento

CPF ou CNPJ usado para identificar o cliente. O documento deve ser validado, normalizado e comparado sem máscara para evitar duplicidade.

## Veículo

Veículo associado a um cliente. No MVP, um veículo possui placa, marca, modelo, ano, quilometragem e status ativo/inativo.

## Placa

Identificador do veículo. A aplicação aceita o formato brasileiro antigo e o formato Mercosul, sempre normalizando antes de salvar.

## Ordem de Serviço

Registro que representa um atendimento da oficina para um veículo de um cliente. A Ordem de Serviço concentra diagnóstico, serviços solicitados, peças ou insumos, orçamento, status e datas relevantes do fluxo.

## Serviço

Atividade oferecida pela oficina, como troca de óleo, revisão ou troca de freio. No MVP, o serviço possui nome, descrição, preço base, tempo estimado e status ativo/inativo.

## Peça/Insumo

Item físico usado na execução de um serviço, como filtro, óleo, pastilha de freio ou fluido. No MVP, uma peça ou insumo possui SKU, categoria, marca, preço unitário, custo, quantidade em estoque, estoque mínimo e status ativo/inativo.

## Estoque

Quantidade disponível de uma peça ou insumo. O estoque pode ser movimentado por entrada, saída, venda isolada, reserva ou baixa vinculada a orçamento aprovado.

## Movimentação de estoque

Registro de alteração no estoque. Deve indicar peça, tipo de movimentação, quantidade, data e origem da alteração quando aplicável.

## Diagnóstico

Descrição inicial do problema informado pelo cliente ou observado pela oficina. No MVP, a Ordem de Serviço pode nascer com notas de diagnóstico e avançar para o status `EM_DIAGNOSTICO`.

## Orçamento

Valor calculado a partir dos serviços e peças associados a uma Ordem de Serviço. O orçamento deve calcular total de serviços, total de peças e total geral.

## Aprovação

Confirmação do cliente ou usuário autorizado para aceitar o orçamento. A aprovação libera a Ordem de Serviço para execução e confirma a baixa das peças reservadas.

## Execução

Momento em que a oficina realiza os serviços aprovados. No domínio, a execução corresponde ao status `EM_EXECUCAO`.

## Finalização

Momento em que a oficina conclui os serviços executados. No domínio, a finalização corresponde ao status `FINALIZADA`.

## Entrega

Etapa final em que o veículo é entregue ao cliente após a conclusão da Ordem de Serviço. No domínio, a entrega corresponde ao status `ENTREGUE`.

## Status da Ordem de Serviço

Estados controlados da Ordem de Serviço no MVP:

- `RECEBIDA`: Ordem de Serviço registrada e aguardando continuidade.
- `EM_DIAGNOSTICO`: diagnóstico iniciado pela oficina.
- `AGUARDANDO_APROVACAO`: orçamento gerado e aguardando aprovação do cliente.
- `EM_EXECUCAO`: execução iniciada após aprovação.
- `FINALIZADA`: serviço finalizado pela oficina.
- `ENTREGUE`: veículo entregue ao cliente.

Na interface REST, alguns códigos externos continuam em inglês para preservar compatibilidade com o contrato publicado, mas a documentação e a interface do usuário devem exibir os termos em português.
