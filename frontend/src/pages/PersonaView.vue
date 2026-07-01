<script setup>
import {computed, ref} from 'vue';
import {
  BarChart3,
  Building2,
  Car,
  CheckCircle2,
  ClipboardList,
  DollarSign,
  Eye,
  MapPin,
  Package,
  ShieldCheck,
  UserCog,
  Users,
  Wrench,
} from 'lucide-vue-next';

const props = defineProps({
  persona: {
    type: String,
    required: true,
  },
});

const permissions = ref({
  orders: true,
  stock: true,
  customers: false,
  billing: false,
});

const customerZipCode = ref('01310-100');

const configs = {
  workshopAdmin: {
    label: 'Admin de oficina',
    title: 'Gestão completa para dono, gerente e administradores da oficina',
    text: 'Centraliza acessos, permissões, operação, estoque, clientes, veículos, faturamento e indicadores internos.',
    icon: ShieldCheck,
    accent: 'blue',
    stats: [
      ['R$ 86.420', 'Faturamento mensal'],
      ['14', 'Usuários ativos'],
      ['38', 'Ordens abertas'],
      ['7', 'Alertas de estoque'],
    ],
    cards: [
      ['Perfis e permissões', 'Crie funcionários, administradores e defina acesso por módulo.', UserCog],
      ['Operação da oficina', 'Acompanhe ordens, prazos, aprovações e entrega dos veículos.', ClipboardList],
      ['Financeiro interno', 'Resumo de faturamento, tickets e serviços mais vendidos.', DollarSign],
    ],
  },
  employee: {
    label: 'Funcionário',
    title: 'Rotina por usuário com acesso limitado por função e permissão',
    text: 'A tela muda conforme o perfil do funcionário, evitando acesso a faturamento, usuários ou configurações sensíveis quando não permitido.',
    icon: Users,
    accent: 'cyan',
    stats: [
      ['22', 'Ordens permitidas'],
      ['5', 'Aguardando aprovação'],
      ['3', 'Peças pendentes'],
      ['0', 'Áreas bloqueadas visíveis'],
    ],
    cards: [
      ['Permissões aplicadas', 'Exemplo de tela onde cada módulo aparece conforme o acesso.', ShieldCheck],
      ['Atendimento e pátio', 'Atualize status, inclua diagnóstico e acompanhe veículos.', Car],
      ['Estoque operacional', 'Baixa e consulta de peças sem liberar configurações administrativas.', Package],
    ],
  },
  master: {
    label: 'Admin Master',
    title: 'Sua visão de plataforma, clientes, receita e interesse comercial',
    text: 'Painel para acompanhar oficinas ativas, uso por cliente, faturamento da plataforma, interessados da demo, assinaturas e saúde da base.',
    icon: BarChart3,
    accent: 'violet',
    stats: [
      ['128', 'Empresas ativas'],
      ['842', 'Usuários totais'],
      ['R$ 54.900', 'Receita recorrente mensal'],
      ['31', 'Interessados na demo'],
    ],
    cards: [
      ['Clientes ativos', 'Detalhe por empresa, plano, uso, faturamento e usuários.', Building2],
      ['Interessados e assinaturas', 'Empresas que entraram em contato, converteram ou abandonaram.', Eye],
      ['Saúde da plataforma', 'Uso por cliente, cancelamentos, crescimento, receita e alertas.', BarChart3],
    ],
  },
};

const config = computed(() => configs[props.persona] || configs.workshopAdmin);

const permissionRows = computed(() => [
  {key: 'orders', label: 'Ordens de serviço', enabled: permissions.value.orders},
  {key: 'stock', label: 'Estoque', enabled: permissions.value.stock},
  {key: 'customers', label: 'Clientes', enabled: permissions.value.customers},
  {key: 'billing', label: 'Financeiro', enabled: permissions.value.billing},
]);

const nearbyPlaces = computed(() => {
  const suffix = customerZipCode.value.replace(/\D/g, '').slice(-2) || '00';

  return [
    ['Oficina Vila Auto', `1,${Number(suffix[0] || 1)} km`, 'Revisão, freios e suspensão'],
    ['Peças Centro Sul', `2,${Number(suffix[1] || 4)} km`, 'Filtros, óleo, bateria e retirada rápida'],
    ['Auto Elétrica Premium', '3,1 km', 'Diagnóstico elétrico e ar-condicionado'],
  ];
});
</script>

<template>
  <main class="persona-shell">
    <header class="persona-header">
      <strong>
        <Wrench :size="22"/>
        AutoCare Hub</strong
      >
    </header>

    <section :class="`accent-${config.accent}`" class="persona-hero">
      <div>
        <span>{{ config.label }}</span>
        <h1>{{ config.title }}</h1>
        <p>{{ config.text }}</p>
      </div>
      <component :is="config.icon" :size="96"/>
    </section>

    <section class="persona-stats">
      <article v-for="stat in config.stats" :key="stat[1]">
        <strong>{{ stat[0] }}</strong>
        <span>{{ stat[1] }}</span>
      </article>
    </section>

    <section class="persona-grid">
      <article v-for="card in config.cards" :key="card[0]" class="persona-card">
        <component :is="card[2]" :size="28"/>
        <h2>{{ card[0] }}</h2>
        <p>{{ card[1] }}</p>
      </article>

      <article v-if="persona === 'workshopAdmin' || persona === 'employee'" class="persona-card permission-card">
        <ShieldCheck :size="28"/>
        <h2>Exemplo de permissões por usuário</h2>
        <p>
          Esse bloco mostra como a tela de funcionário deve variar conforme as permissões definidas pelo administrador.
        </p>
        <div class="permission-list">
          <label v-for="row in permissionRows" :key="row.key">
            <input v-model="permissions[row.key]" type="checkbox"/>
            <span>{{ row.label }}</span>
            <CheckCircle2 v-if="row.enabled" :size="16"/>
          </label>
        </div>
      </article>

      <article v-if="persona === 'master'" class="persona-card span-2">
        <BarChart3 :size="28"/>
        <h2>Indicadores que cabem na sua visão</h2>
        <p>
          Empresas por plano, receita recorrente, interessados da demo, conversão, inadimplência, oficinas mais ativas,
          usuários por empresa, uso de estoque e ordens criadas por período.
        </p>
      </article>

      <article v-if="persona === 'customer'" class="persona-card span-2 customer-cep-card">
        <MapPin :size="28"/>
        <h2>Busca por CEP após cadastro</h2>
        <p>
          O cliente não acessa uma demo pública. Depois do cadastro, ele informa o CEP para encontrar oficinas e lojas
          próximas e acompanhar atendimentos vinculados ao seu veículo.
        </p>
        <label>
          CEP
          <input v-model="customerZipCode" placeholder="00000-000"/>
        </label>
        <div class="nearby-list">
          <article v-for="place in nearbyPlaces" :key="place[0]">
            <strong>{{ place[0] }}</strong>
            <span>{{ place[1] }}</span>
            <small>{{ place[2] }}</small>
          </article>
        </div>
      </article>
    </section>
  </main>
</template>
