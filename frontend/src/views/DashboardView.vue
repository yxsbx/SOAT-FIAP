<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  ClipboardList,
  Copy,
  Gauge,
  LogOut,
  Package,
  Play,
  RefreshCw,
  Users,
  Wrench,
} from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth';
import { resources } from '@/services/api';
import { commandGroups } from '@/data/commands';

const router = useRouter();
const auth = useAuthStore();
const loading = ref(false);
const error = ref('');
const copiedCommand = ref('');
const data = reactive({
  customers: [],
  vehicles: [],
  services: [],
  parts: [],
  lowStockParts: [],
  serviceOrders: [],
  averageExecutionTime: null,
});

const roleLabel = computed(() => {
  const labels = {
    ADMIN: 'Administrador',
    EMPLOYEE: 'Atendimento e oficina',
    CUSTOMER: 'Cliente',
  };
  return labels[auth.role] || auth.role;
});

const visiblePanels = computed(() => {
  if (auth.role === 'CUSTOMER') {
    return ['orders'];
  }

  if (auth.role === 'EMPLOYEE') {
    return ['orders', 'services', 'parts'];
  }

  return ['summary', 'customers', 'orders', 'services', 'parts', 'commands'];
});

function listItems(payload) {
  return payload?.items || [];
}

async function loadDashboard() {
  loading.value = true;
  error.value = '';

  try {
    if (auth.role === 'CUSTOMER' && auth.customerId) {
      data.serviceOrders = listItems(await resources.customerServiceOrders(auth.customerId));
      return;
    }

    const [customers, vehicles, services, parts, lowStockParts, serviceOrders, average] =
      await Promise.all([
        resources.customers(),
        resources.vehicles(),
        resources.services(),
        resources.parts(),
        resources.lowStockParts(),
        resources.serviceOrders(),
        resources.averageExecutionTime(),
      ]);

    data.customers = listItems(customers);
    data.vehicles = listItems(vehicles);
    data.services = listItems(services);
    data.parts = listItems(parts);
    data.lowStockParts = listItems(lowStockParts);
    data.serviceOrders = listItems(serviceOrders);
    data.averageExecutionTime = average;
  } catch (err) {
    error.value = err.message || 'Nao foi possivel carregar os dados.';
  } finally {
    loading.value = false;
  }
}

async function copyCommand(command) {
  await navigator.clipboard.writeText(command);
  copiedCommand.value = command;
  window.setTimeout(() => {
    if (copiedCommand.value === command) {
      copiedCommand.value = '';
    }
  }, 1600);
}

function logout() {
  auth.logout();
  router.push({ name: 'login' });
}

onMounted(loadDashboard);
</script>

<template>
  <main class="app-shell">
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="brand-mark"><Wrench :size="22" /></div>
        <div>
          <strong>AutoCare Hub</strong>
          <span>{{ roleLabel }}</span>
        </div>
      </div>

      <nav class="nav-list">
        <a href="#resumo" v-if="visiblePanels.includes('summary')"><Gauge :size="18" />Resumo</a>
        <a href="#clientes" v-if="visiblePanels.includes('customers')"><Users :size="18" />Clientes</a>
        <a href="#ordens" v-if="visiblePanels.includes('orders')"><ClipboardList :size="18" />Ordens</a>
        <a href="#catalogo" v-if="visiblePanels.includes('services')"><Wrench :size="18" />Catalogo</a>
        <a href="#estoque" v-if="visiblePanels.includes('parts')"><Package :size="18" />Estoque</a>
        <a href="#comandos" v-if="visiblePanels.includes('commands')"><Play :size="18" />Comandos</a>
      </nav>

      <button class="ghost-button" type="button" @click="logout">
        <LogOut :size="18" />
        <span>Sair</span>
      </button>
    </aside>

    <section class="content">
      <header class="topbar">
        <div>
          <p>{{ auth.user?.username }}</p>
          <h1>Painel {{ roleLabel }}</h1>
        </div>
        <button class="icon-button" type="button" title="Atualizar dados" @click="loadDashboard">
          <RefreshCw :size="19" />
        </button>
      </header>

      <p v-if="error" class="alert">{{ error }}</p>

      <section v-if="visiblePanels.includes('summary')" id="resumo" class="metric-grid">
        <article class="metric-card">
          <span>Clientes</span>
          <strong>{{ data.customers.length }}</strong>
        </article>
        <article class="metric-card">
          <span>Veiculos</span>
          <strong>{{ data.vehicles.length }}</strong>
        </article>
        <article class="metric-card">
          <span>Ordens</span>
          <strong>{{ data.serviceOrders.length }}</strong>
        </article>
        <article class="metric-card">
          <span>Media execucao</span>
          <strong>{{ data.averageExecutionTime?.averageExecutionTimeInMinutes ?? 0 }} min</strong>
        </article>
      </section>

      <section v-if="visiblePanels.includes('customers')" id="clientes" class="section-block">
        <div class="section-heading">
          <h2>Clientes recentes</h2>
          <span>{{ loading ? 'Carregando' : `${data.customers.length} registros` }}</span>
        </div>
        <div class="table-list">
          <article v-for="customer in data.customers" :key="customer.id" class="row-item">
            <div>
              <strong>{{ customer.name }}</strong>
              <span>{{ customer.email }}</span>
            </div>
            <span>{{ customer.document }}</span>
          </article>
        </div>
      </section>

      <section v-if="visiblePanels.includes('orders')" id="ordens" class="section-block">
        <div class="section-heading">
          <h2>Ordens de servico</h2>
          <span>{{ data.serviceOrders.length }} abertas ou recentes</span>
        </div>
        <div class="table-list">
          <article v-for="order in data.serviceOrders" :key="order.id" class="row-item">
            <div>
              <strong>{{ order.status }}</strong>
              <span>{{ order.diagnosticNotes }}</span>
            </div>
            <span>R$ {{ Number(order.totalAmount || 0).toFixed(2) }}</span>
          </article>
          <p v-if="!data.serviceOrders.length && !loading" class="empty-state">Nenhuma ordem encontrada.</p>
        </div>
      </section>

      <section v-if="visiblePanels.includes('services')" id="catalogo" class="section-block">
        <div class="section-heading">
          <h2>Servicos da oficina</h2>
          <span>{{ data.services.length }} itens</span>
        </div>
        <div class="catalog-grid">
          <article v-for="service in data.services" :key="service.id" class="catalog-item">
            <strong>{{ service.name }}</strong>
            <span>R$ {{ Number(service.basePrice || 0).toFixed(2) }}</span>
            <small>{{ service.estimatedTimeInMinutes }} min</small>
          </article>
        </div>
      </section>

      <section v-if="visiblePanels.includes('parts')" id="estoque" class="section-block">
        <div class="section-heading">
          <h2>Estoque</h2>
          <span>{{ data.lowStockParts.length }} em baixo estoque</span>
        </div>
        <div class="table-list">
          <article v-for="part in data.parts" :key="part.id" class="row-item">
            <div>
              <strong>{{ part.name }}</strong>
              <span>{{ part.sku }} - {{ part.brand }}</span>
            </div>
            <span>{{ part.stockQuantity }} un.</span>
          </article>
        </div>
      </section>

      <section v-if="visiblePanels.includes('commands')" id="comandos" class="section-block">
        <div class="section-heading">
          <h2>Comandos do projeto</h2>
          <span>Copie e rode no terminal</span>
        </div>
        <div class="command-groups">
          <article v-for="group in commandGroups" :key="group.title" class="command-group">
            <h3>{{ group.title }}</h3>
            <button
              v-for="item in group.items"
              :key="item.command"
              class="command-row"
              type="button"
              :title="`Copiar: ${item.command}`"
              @click="copyCommand(item.command)"
            >
              <div>
                <strong>{{ item.label }}</strong>
                <code>{{ item.command }}</code>
                <span>{{ item.detail }}</span>
              </div>
              <Copy :size="18" />
            </button>
          </article>
        </div>
        <p v-if="copiedCommand" class="copy-feedback">Comando copiado.</p>
      </section>
    </section>
  </main>
</template>
