<script setup>
import {computed, reactive, ref} from 'vue';
import {
  AlertTriangle,
  ArrowLeft,
  Building2,
  Car,
  CheckCircle2,
  ClipboardList,
  Package,
  Plus,
  Wrench,
} from 'lucide-vue-next';
import {createDemoLead} from '@/services/api';

const leadRegistered = ref(false);
const saving = ref(false);
const error = ref('');

const leadForm = reactive({
  demoProfile: 'workshop',
  contactName: '',
  companyName: '',
  email: '',
  phone: '',
  cnpj: '',
  city: '',
  message: '',
});

const orderForm = reactive({
  customerId: '1',
  service: 'Diagnóstico rápido',
});

const customers = ref([
  {id: '1', name: 'Marina Lopes', company: 'Frota Marina', vehicle: 'Honda Fit', plate: 'MRA2E19'},
  {id: '2', name: 'Carlos Vieira', company: 'Vieira Log', vehicle: 'Fiat Strada', plate: 'VRA7B42'},
  {id: '3', name: 'Aline Costa', company: 'Costa Eventos', vehicle: 'Jeep Renegade', plate: 'ALC4D08'},
]);

const orders = ref([
  {id: 101, plate: 'MRA2E19', service: 'Troca de pastilhas', status: 'Em execução', eta: 'Hoje'},
  {id: 102, plate: 'VRA7B42', service: 'Revisão preventiva', status: 'Recebida', eta: 'Amanhã'},
  {id: 103, plate: 'ALC4D08', service: 'Diagnóstico elétrico', status: 'Aguardando aprovação', eta: '2 dias'},
]);

const parts = ref([
  {id: 1, name: 'Pastilha de freio', stock: 18, min: 8},
  {id: 2, name: 'Filtro de óleo', stock: 6, min: 10},
  {id: 3, name: 'Correia dentada', stock: 14, min: 6},
]);

const demoProfiles = [
  {
    id: 'workshop',
    title: 'Oficina',
    text: 'Fluxo simplificado para ordens, veículos, serviços e peças.',
  },
  {
    id: 'partsStore',
    title: 'Loja de peças',
    text: 'Fluxo simplificado para pedidos, estoque, fornecedores e oficinas atendidas.',
  },
];

const demoContext = computed(() => {
  if (leadForm.demoProfile === 'partsStore') {
    return {
      eyebrow: 'Demo para loja de peças',
      title: 'Sua loja de peças pode vender mais com atendimento organizado.',
      companyPlaceholder: 'Loja de Peças Exemplo',
      servicePlaceholder: 'Pedido ou peça',
      heroLabel: 'Visão resumida da loja',
      heroText: 'Fluxo reduzido para experimentar pedidos, estoque e atendimento a oficinas sem expor todos os recursos internos.',
      ordersLabel: 'Pedidos ativos',
      customersLabel: 'Oficinas demo',
      actionTitle: 'Criar pedido rápido',
      listTitle: 'Pedidos em acompanhamento',
      stockTitle: 'Estoque da loja',
      partnerTypeLabel: 'loja de peças',
    };
  }

  return {
    eyebrow: 'Demo para oficina',
    title: 'Sua oficina pode receber clientes e gerenciar orçamentos em um só lugar.',
    companyPlaceholder: 'Oficina Exemplo',
    servicePlaceholder: 'Serviço',
    heroLabel: 'Visão resumida da oficina',
    heroText: 'Fluxo reduzido para experimentar a operação sem expor todos os recursos internos.',
    ordersLabel: 'Ordens ativas',
    customersLabel: 'Clientes demo',
    actionTitle: 'Criar ordem rápida',
    listTitle: 'Ordens em acompanhamento',
    stockTitle: 'Estoque da oficina',
    partnerTypeLabel: 'oficina',
  };
});

const cnpjIsValid = computed(() => /^[A-Za-z0-9./-]{6,40}$/.test(leadForm.cnpj.trim()));
const lowStockCount = computed(() => parts.value.filter((part) => part.stock < part.min).length);
const activeOrders = computed(() => orders.value.filter((order) => order.status !== 'Finalizada').length);

async function submitLead() {
  error.value = '';

  if (!cnpjIsValid.value) {
    error.value = 'Informe um CNPJ válido. Ele pode conter letras, números, ponto, barra ou hífen.';
    return;
  }

  saving.value = true;

  try {
    await createDemoLead({...leadForm});
    leadRegistered.value = true;
  } catch (requestError) {
    error.value = requestError.message || 'Não foi possível liberar a demo agora.';
  } finally {
    saving.value = false;
  }
}

function addOrder() {
  const customer = customers.value.find((item) => item.id === orderForm.customerId);

  if (!customer) {
    return;
  }

  orders.value.unshift({
    id: Date.now(),
    plate: customer.plate,
    service: orderForm.service,
    status: 'Recebida',
    eta: 'Hoje',
  });
}

function moveOrder(order) {
  const flow = ['Recebida', 'Em execução', 'Aguardando aprovação', 'Finalizada'];
  const currentIndex = flow.indexOf(order.status);
  order.status = flow[(currentIndex + 1) % flow.length];
}

function incrementPart(part, quantity) {
  part.stock = Math.max(0, part.stock + quantity);
}
</script>

<template>
  <main class="demo-shell">
    <header class="demo-header">
      <RouterLink to="/preview">
        <ArrowLeft :size="18"/>
        Voltar
      </RouterLink>
      <strong>
        <Wrench :size="22"/>
        AutoCare Hub</strong>
    </header>

    <section v-if="!leadRegistered" class="demo-gate">
      <div>
        <span class="preview-eyebrow">Demo simplificada</span>
        <h1>{{ demoContext.title }}</h1>
        <p>
          A AutoCare Hub conecta oficinas e lojas de peças a clientes, com ferramentas para
          receber contatos, montar orçamentos, controlar estoque, acompanhar vendas e organizar a operação.
        </p>
        <div class="partner-benefits">
          <span><CheckCircle2 :size="16"/> Receber clientes</span>
          <span><CheckCircle2 :size="16"/> Gerenciar orçamentos</span>
          <span><CheckCircle2 :size="16"/> Controlar estoque</span>
          <span><CheckCircle2 :size="16"/> Acompanhar vendas</span>
          <span><CheckCircle2 :size="16"/> Organizar ordens de serviço</span>
          <span><CheckCircle2 :size="16"/> Ver faturamento</span>
        </div>
      </div>

      <form class="demo-form" @submit.prevent="submitLead">
        <div class="demo-profile-picker span-2">
          <button
              v-for="profile in demoProfiles"
              :key="profile.id"
              :class="{ active: leadForm.demoProfile === profile.id }"
              type="button"
              @click="leadForm.demoProfile = profile.id"
          >
            <strong>{{ profile.title }}</strong>
            <span>{{ profile.text }}</span>
          </button>
        </div>
        <label>
          Nome da pessoa
          <input v-model.trim="leadForm.contactName" maxlength="120" placeholder="Seu nome" required/>
        </label>
        <label>
          Nome da empresa
          <input
              v-model.trim="leadForm.companyName" :placeholder="demoContext.companyPlaceholder" maxlength="120"
              required
          />
        </label>
        <label>
          E-mail
          <input v-model.trim="leadForm.email" maxlength="160" placeholder="contato@empresa.com" required type="email"/>
        </label>
        <label>
          Telefone
          <input v-model.trim="leadForm.phone" maxlength="30" minlength="8" placeholder="(11) 99999-9999" required/>
        </label>
        <label>
          Cidade
          <input v-model.trim="leadForm.city" maxlength="120" placeholder="São Paulo - SP"/>
        </label>
        <label class="span-2">
          CNPJ
          <input
              v-model.trim="leadForm.cnpj"
              :aria-invalid="leadForm.cnpj && !cnpjIsValid"
              maxlength="40"
              placeholder="Pode conter letras e números"
              required
          />
        </label>
        <label class="span-2">
          Mensagem
          <textarea
              v-model.trim="leadForm.message"
              :placeholder="`Conte rapidamente como sua ${demoContext.partnerTypeLabel} quer usar a AutoCare Hub`"
              maxlength="500"
          ></textarea>
        </label>
        <p v-if="error" class="alert error span-2">{{ error }}</p>
        <button :disabled="saving" class="primary-button">
          {{ saving ? 'Registrando contato...' : 'Quero ser parceiro' }}
        </button>
        <button :disabled="saving" class="secondary-button" type="submit">
          Solicitar demonstração
        </button>
      </form>
    </section>

    <section v-else class="demo-workspace">
      <div class="demo-alert">
        <AlertTriangle :size="20"/>
        <div>
          <strong>Apenas uma amostra do sistema</strong>
          <span>
            As alterações abaixo ficam somente na memória desta tela. Ao atualizar com F5, os dados
            temporários somem.
          </span>
        </div>
      </div>

      <div class="demo-hero-card">
        <div>
          <span>Visão resumida</span>
          <h1>{{ leadForm.companyName }}</h1>
          <p>{{ demoContext.heroText }}</p>
        </div>
        <div class="demo-kpis">
          <article>
            <ClipboardList :size="21"/>
            <strong>{{ activeOrders }}</strong>
            <span>{{ demoContext.ordersLabel }}</span>
          </article>
          <article>
            <Package :size="21"/>
            <strong>{{ lowStockCount }}</strong>
            <span>Itens em alerta</span>
          </article>
          <article>
            <Car :size="21"/>
            <strong>{{ customers.length }}</strong>
            <span>{{ demoContext.customersLabel }}</span>
          </article>
        </div>
      </div>

      <div class="demo-grid">
        <section class="demo-panel">
          <div class="panel-heading">
            <div>
              <span>Atendimento</span>
              <h2>{{ demoContext.actionTitle }}</h2>
            </div>
            <Plus :size="20"/>
          </div>
          <form class="demo-inline-form" @submit.prevent="addOrder">
            <select v-model="orderForm.customerId">
              <option v-for="customer in customers" :key="customer.id" :value="customer.id">
                {{ customer.name }} - {{ customer.plate }}
              </option>
            </select>
            <input v-model.trim="orderForm.service" :placeholder="demoContext.servicePlaceholder" required/>
            <button class="primary-button">Adicionar</button>
          </form>
          <div class="demo-list">
            <button v-for="order in orders" :key="order.id" class="demo-row" @click="moveOrder(order)">
              <span>{{ order.plate }}</span>
              <strong>{{ order.service }}</strong>
              <em>{{ order.status }}</em>
              <small>{{ order.eta }}</small>
            </button>
          </div>
        </section>

        <section class="demo-panel">
          <div class="panel-heading">
            <div>
              <span>Estoque</span>
              <h2>{{ demoContext.stockTitle }}</h2>
            </div>
            <Package :size="20"/>
          </div>
          <div class="demo-parts">
            <article v-for="part in parts" :key="part.id" :class="{ warning: part.stock < part.min }">
              <div>
                <strong>{{ part.name }}</strong>
                <span>Mínimo {{ part.min }} unidades</span>
              </div>
              <div class="stock-stepper">
                <button @click="incrementPart(part, -1)">-</button>
                <b>{{ part.stock }}</b>
                <button @click="incrementPart(part, 1)">+</button>
              </div>
            </article>
          </div>
        </section>

        <section class="demo-panel span-2">
          <div class="panel-heading">
            <div>
              <span>Clientes</span>
              <h2>Carteira reduzida</h2>
            </div>
            <Building2 :size="20"/>
          </div>
          <div class="demo-customers">
            <article v-for="customer in customers" :key="customer.id">
              <CheckCircle2 :size="18"/>
              <div>
                <strong>{{ customer.name }}</strong>
                <span>{{ customer.company }} - {{ customer.vehicle }} - {{ customer.plate }}</span>
              </div>
            </article>
          </div>
        </section>
      </div>
    </section>
  </main>
</template>
