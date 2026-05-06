# Ubiquitous Language

Este documento define a linguagem compartilhada do dominio. Os termos abaixo devem ser usados de forma consistente por negocio, desenvolvimento, documentacao e testes.

## Cliente

Pessoa fisica ou juridica atendida pela oficina. No MVP, um cliente possui nome, documento, telefone, email, endereco e status ativo/inativo.

## Veiculo

Automovel associado a um cliente. No MVP, um veiculo possui placa, marca, modelo, ano, quilometragem e status ativo/inativo.

## Ordem de Servico

Registro que representa um atendimento da oficina para um veiculo de um cliente. A ordem de servico concentra diagnostico, servicos, pecas, orcamento, status e datas relevantes do fluxo.

## Servico

Atividade oferecida pela oficina, como troca de oleo, revisao ou troca de freio. No MVP, o servico possui nome, descricao, preco base, tempo estimado e status ativo/inativo.

## Peca/Insumo

Item fisico usado na execucao de um servico, como filtro, oleo, pastilha de freio ou fluido. No MVP, uma peca possui SKU, categoria, marca, preco unitario, quantidade em estoque, estoque minimo e status ativo/inativo.

## Estoque

Quantidade disponivel de uma peca ou insumo. No MVP, o estoque e reduzido quando uma peca e adicionada a uma ordem de servico e pode ser atualizado por endpoint proprio.

## Diagnostico

Descricao inicial do problema informado ou observado no veiculo. No MVP, a ordem de servico nasce com notas de diagnostico.

## Orcamento

Valor calculado a partir dos servicos e pecas associados a uma ordem de servico. No MVP, o orcamento so pode ser gerado quando ha pelo menos um servico ou uma peca na ordem.

## Aprovacao

Confirmacao do cliente ou usuario autorizado para aceitar o orcamento. No MVP, a aprovacao registra data de aprovacao e permite que a ordem avance para execucao.

## Execucao

Momento em que a oficina inicia a realizacao dos servicos aprovados. No MVP, a execucao corresponde ao status `IN_PROGRESS`.

## Entrega

Etapa final em que o veiculo e entregue apos a conclusao da ordem de servico. No MVP, a entrega corresponde ao status `DELIVERED`.

## Status da Ordem de Servico

Estados controlados da ordem de servico no MVP:

- `RECEIVED`: ordem recebida.
- `IN_DIAGNOSIS`: diagnostico iniciado.
- `WAITING_APPROVAL`: orcamento gerado e aguardando aprovacao.
- `IN_PROGRESS`: execucao iniciada.
- `FINISHED`: servico finalizado.
- `DELIVERED`: veiculo entregue.
