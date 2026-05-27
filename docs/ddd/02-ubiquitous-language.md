# Ubiquitous Language

Este documento define a linguagem compartilhada do domínio. Os termos abaixo devem ser usados de forma consistente por
negócio, desenvolvimento, documentação e testes.

## Cliente

Pessoa física ou juridica atendida pela oficina. No MVP, um cliente possui nome, documento, telefone, email, endereco e
status ativo/inativo.

## Veículo

Automovel associado a um cliente. No MVP, um veículo possui placa, marca, modelo, ano, quilometragem e status
ativo/inativo.

## Ordem de Servico

Registro que representa um atendimento da oficina para um veículo de um cliente. A ordem de serviço concentra
diagnostico, serviços, peças, orçamento, status e datas relevantes do fluxo.

## Servico

Atividade oferecida pela oficina, como troca de oleo, revisao ou troca de freio. No MVP, o serviço possui nome,
descrição, preco base, tempo estimado e status ativo/inativo.

## Peça/Insumo

Item fisico usado na execução de um serviço, como filtro, oleo, pastilha de freio ou fluido. No MVP, uma peça possui
SKU, categoria, marca, preco unitario, quantidade em estoque, estoque minimo e status ativo/inativo.

## Estoque

Quantidade disponivel de uma peça ou insumo. No MVP, o estoque pode ser movimentado por entrada, saida ou venda isolada.
peças vinculadas a orçamentos podem ficar reservadas; a baixa definitiva ocorre quando a reserva e confirmada ou quando
o orçamento e aprovado.

## Diagnostico

Descrição inicial do problema informado ou observado no veículo. No MVP, a ordem de serviço nasce com notas de
diagnostico.

## Orçamento

Valor calculado a partir dos serviços e peças associados a uma ordem de serviço. No MVP, o orçamento so pode ser gerado
quando ha pelo menos um serviço ou uma peça na ordem.

## Aprovação

Confirmação do cliente ou usuario autorizado para aceitar o orçamento. No MVP, a aprovação registra data de aprovação e
permite que a ordem avance para execução.

## Execução

Momento em que a oficina inicia a realização dos serviços aprovados. No domínio, a execução corresponde ao status
`EM_EXECUCAO`.

## Entrega

Etapa final em que o veículo e entregue apos a conclusao da ordem de serviço. No domínio, a entrega corresponde ao
status `ENTREGUE`.

## Status da Ordem de Servico

Estados controlados da ordem de serviço no MVP:

- `RECEBIDA`: ordem recebida.
- `EM_DIAGNOSTICO`: diagnostico iniciado.
- `AGUARDANDO_APROVACAO`: orçamento gerado e aguardando aprovação.
- `EM_EXECUCAO`: execução iniciada.
- `FINALIZADA`: serviço finalizado.
- `ENTREGUE`: veículo entregue.

Na interface REST, esses estados continuam mapeados para os codigos externos em ingles para preservar compatibilidade
com o contrato publicado.
