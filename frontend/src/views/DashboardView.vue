<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  AlertTriangle,
  BarChart3,
  Car,
  CheckCircle2,
  ChevronLeft,
  ChevronRight,
  ClipboardList,
  Gauge,
  KeyRound,
  LogOut,
  Menu,
  Package,
  Plus,
  Search,
  ShieldCheck,
  TrendingUp,
  UserCog,
  UserPlus,
  Users,
  Wrench,
  X,
} from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth';
import { resources } from '@/services/api';

const router = useRouter();
const auth = useAuthStore();
const loading = ref(false);
const saving = ref(false);
const error = ref('');
const success = ref('');
const activeTab = ref('overview');
const mobileMenuOpen = ref(false);
const sidebarCollapsed = ref(true);
const profileMenuOpen = ref(false);
const globalSearch = ref('');

const statuses = ['RECEIVED', 'IN_DIAGNOSIS', 'WAITING_APPROVAL', 'IN_PROGRESS', 'FINISHED', 'DELIVERED'];

const statusLabels = {
  RECEIVED: 'Recebido',
  IN_DIAGNOSIS: 'Diagnóstico',
  WAITING_APPROVAL: 'Aprovação',
  IN_PROGRESS: 'Em execução',
  FINISHED: 'Finalizado',
  DELIVERED: 'Entregue',
};

const data = reactive({
  customers: [],
  vehicles: [],
  services: [],
  parts: [],
  lowStockParts: [],
  serviceOrders: [],
  averageExecutionTime: null,
});

const pagination = reactive({
  customers: { page: 0, size: 8, active: '' },
  vehicles: { page: 0, size: 8, active: '' },
  parts: { page: 0, size: 8, active: '', lowStock: '' },
  serviceOrders: { page: 0, size: 8, status: '' },
  services: { page: 0, size: 8, active: '' },
});

const forms = reactive({
  customer: {
    name: '',
    document: '',
    phone: '',
    email: '',
    address: {
      street: '',
      number: '',
      complement: '',
      neighborhood: '',
      city: '',
      state: 'SP',
      zipCode: '',
    },
  },
  vehicle: {
    customerId: '',
    plate: '',
    brand: '',
    model: '',
    year: new Date().getFullYear(),
    mileage: 0,
  },
  part: {
    name: '',
    sku: '',
    category: '',
    subcategory: '',
    brand: '',
    unitPrice: 0,
    stockQuantity: 0,
    minimumStock: 1,
  },
  service: {
    name: '',
    description: '',
    basePrice: 0,
    estimatedTimeInMinutes: 60,
  },
  order: {
    customerDocument: '',
    vehicleId: '',
    diagnosticNotes: '',
  },
  stock: {
    partId: '',
    stockQuantity: 0,
  },
  orderAction: {
    serviceOrderId: '',
    status: 'IN_DIAGNOSIS',
    serviceId: '',
    serviceQuantity: 1,
    partId: '',
    partQuantity: 1,
  },
});

const roleLabel = computed(() => {
  const labels = {
    ADMIN: 'Administrador',
    EMPLOYEE: 'Atendimento e oficina',
    CUSTOMER: 'Cliente',
  };
  return labels[auth.role] || auth.role;
});

const availableTabs = computed(() => {
  const tabs = [
    {
      id: 'overview',
      label: 'Painel',
      description: 'Visão geral operacional',
      icon: Gauge,
      roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'],
    },
    {
      id: 'orders',
      label: 'Ordens',
      description: 'Status, orçamentos e execução',
      icon: ClipboardList,
      roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'],
    },
    {
      id: 'customers',
      label: 'Clientes',
      description: 'Cadastro e lista de clientes',
      icon: Users,
      roles: ['ADMIN'],
    },
    {
      id: 'vehicles',
      label: 'Veículos',
      description: 'Frota, placas e status dos carros',
      icon: Car,
      roles: ['ADMIN', 'EMPLOYEE'],
    },
    {
      id: 'parts',
      label: 'Estoque',
      description: 'Peças, alertas e reposição',
      icon: Package,
      roles: ['ADMIN', 'EMPLOYEE'],
    },
    {
      id: 'services',
      label: 'Serviços',
      description: 'Catálogo e prazos previstos',
      icon: Wrench,
      roles: ['ADMIN', 'EMPLOYEE'],
    },
  ];

  return tabs.filter((tab) => tab.roles.includes(auth.role));
});

const activeTabMeta = computed(
  () => availableTabs.value.find((tab) => tab.id === activeTab.value) || availableTabs.value[0],
);

const userInitials = computed(() => {
  const fallback = auth.user?.username || 'Usuario AutoCare';
  const name = fallback.includes('@') ? fallback.split('@')[0] : fallback;
  const parts = name
    .replace(/[._-]+/g, ' ')
    .split(' ')
    .map((part) => part.trim())
    .filter(Boolean);

  return parts
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join('');
});

const statusCounts = computed(() =>
  statuses.map((status) => ({
    status,
    label: statusLabels[status] || status,
    count: data.serviceOrders.filter((order) => order.status === status).length,
  })),
);

const statusChart = computed(() => {
  const total = Math.max(data.serviceOrders.length, 1);
  const colors = {
    RECEIVED: '#0ea5e9',
    IN_DIAGNOSIS: '#2563eb',
    WAITING_APPROVAL: '#0891b2',
    IN_PROGRESS: '#06b6d4',
    FINISHED: '#0284c7',
    DELIVERED: '#64748b',
  };

  return statusCounts.value.map((item) => ({
    ...item,
    color: colors[item.status],
    percent: Math.round((item.count / total) * 100),
  }));
});

const inventoryHealth = computed(() => {
  const total = Math.max(data.parts.length, 1);
  const low = Math.min(data.lowStockParts.length, total);
  const healthy = Math.max(data.parts.length - low, 0);

  return [
    {
      label: 'Saudável',
      value: healthy,
      percent: Math.round((healthy / total) * 100),
      color: '#0ea5e9',
    },
    {
      label: 'Comprar',
      value: low,
      percent: Math.round((low / total) * 100),
      color: '#f59e0b',
    },
  ];
});

const quickInsights = computed(() => [
  {
    label: 'Ordens em andamento',
    value: data.serviceOrders.filter((order) =>
      ['RECEIVED', 'IN_DIAGNOSIS', 'WAITING_APPROVAL', 'IN_PROGRESS'].includes(order.status),
    ).length,
    icon: TrendingUp,
  },
  {
    label: 'Itens criticos',
    value: data.lowStockParts.length,
    icon: AlertTriangle,
  },
  {
    label: 'Serviços no catálogo',
    value: data.services.length,
    icon: Wrench,
  },
]);

const operationalHighlights = computed(() => [
  {
    label: 'Clientes ativos',
    value: data.customers.length,
    detail: 'base carregada',
    tone: 'green',
  },
  {
    label: 'Carros em acompanhamento',
    value: data.serviceOrders.filter((order) => order.status !== 'DELIVERED').length,
    detail: 'ordens não entregues',
    tone: 'blue',
  },
  {
    label: 'Risco de estoque',
    value: data.lowStockParts.length,
    detail: 'itens no minimo',
    tone: 'orange',
  },
]);

const searchResults = computed(() => {
  const query = normalize(globalSearch.value);
  if (!query) {
    return [];
  }

  const pageResults = availableTabs.value.map((tab) => ({
    type: 'Página',
    label: tab.label,
    detail: tab.description,
    tabId: tab.id,
    icon: tab.icon,
  }));

  const entityResults = [
    ...data.customers.map((customer) => ({
      type: 'Cliente',
      label: customer.name,
      detail: `${customer.email} - ${customer.document}`,
      tabId: 'customers',
      icon: Users,
    })),
    ...data.vehicles.map((vehicle) => ({
      type: 'Veículo',
      label: `${vehicle.plate} - ${vehicle.brand} ${vehicle.model}`,
      detail: `${vehicle.year} - ${vehicle.mileage} km`,
      tabId: 'vehicles',
      icon: Car,
    })),
    ...data.parts.map((part) => ({
      type: 'Peça',
      label: part.name,
      detail: `${part.sku} - ${part.category}`,
      tabId: 'parts',
      icon: Package,
    })),
    ...data.serviceOrders.map((order) => ({
      type: 'Ordem',
      label: order.status,
      detail: order.diagnosticNotes,
      tabId: 'orders',
      icon: ClipboardList,
    })),
  ];

  return [...pageResults, ...entityResults]
    .filter((item) => normalize(`${item.type} ${item.label} ${item.detail}`).includes(query))
    .slice(0, 8);
});

const healthyParts = computed(() =>
  data.parts.filter((part) => part.stockQuantity > part.minimumStock),
);

const averageExecutionLabel = computed(() =>
  formatDuration(data.averageExecutionTime?.averageExecutionTimeInMinutes || 0),
);

const serviceSlaComparisons = computed(() => {
  const servicesById = new Map(data.services.map((service) => [service.id, service]));
  const stats = new Map();

  data.serviceOrders
    .filter((order) => order.startedAt && (order.deliveredAt || order.finishedAt))
    .forEach((order) => {
      const serviceItems = order.services || [];
      if (!serviceItems.length) {
        return;
      }

      const actualMinutes = diffInMinutes(order.startedAt, order.deliveredAt || order.finishedAt);
      const plannedTotal = serviceItems.reduce((total, item) => {
        const service = servicesById.get(item.serviceId);
        return total + (service?.estimatedTimeInMinutes || 0) * (item.quantity || 1);
      }, 0);

      serviceItems.forEach((item) => {
        const service = servicesById.get(item.serviceId);
        const plannedMinutes = (service?.estimatedTimeInMinutes || 0) * (item.quantity || 1);
        const allocatedActual =
          plannedTotal > 0 ? actualMinutes * (plannedMinutes / plannedTotal) : actualMinutes;

        if (!stats.has(item.serviceId)) {
          stats.set(item.serviceId, {
            serviceId: item.serviceId,
            name: item.name,
            planned: [],
            actual: [],
          });
        }

        stats.get(item.serviceId).planned.push(plannedMinutes);
        stats.get(item.serviceId).actual.push(allocatedActual);
      });
    });

  return Array.from(stats.values())
    .map((item) => {
      const plannedAverage = average(item.planned);
      const actualAverage = average(item.actual);
      const difference = actualAverage - plannedAverage;

      return {
        ...item,
        count: item.actual.length,
        plannedAverage,
        actualAverage,
        difference,
        plannedLabel: formatDuration(plannedAverage),
        actualLabel: formatDuration(actualAverage),
        differenceLabel: `${difference >= 0 ? '+' : '-'}${formatDuration(Math.abs(difference))}`,
        status: difference <= 0 ? 'No prazo' : 'Atraso médio',
        actualPercent:
          plannedAverage > 0 ? Math.min(100, Math.round((actualAverage / plannedAverage) * 100)) : 0,
      };
    })
    .sort((a, b) => b.difference - a.difference);
});

const vehiclesWithCurrentStatus = computed(() =>
  data.vehicles.map((vehicle) => {
    const order = data.serviceOrders.find((item) => item.vehicleId === vehicle.id);
    return {
      ...vehicle,
      currentStatus: order?.status || 'SEM_ORDEM',
      diagnosticNotes: order?.diagnosticNotes || '',
    };
  }),
);

function listItems(payload) {
  return payload?.items || [];
}

function money(value) {
  return Number(value || 0).toFixed(2);
}

function normalize(value) {
  return String(value || '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase();
}

function average(values) {
  if (!values.length) {
    return 0;
  }

  return values.reduce((total, value) => total + value, 0) / values.length;
}

function diffInMinutes(start, end) {
  const startTime = new Date(start).getTime();
  const endTime = new Date(end).getTime();

  if (Number.isNaN(startTime) || Number.isNaN(endTime) || endTime <= startTime) {
    return 0;
  }

  return Math.round((endTime - startTime) / 60000);
}

function formatDuration(minutes) {
  const roundedMinutes = Math.max(0, Math.round(minutes || 0));
  const hours = roundedMinutes / 60;

  if (hours < 1) {
    return `${roundedMinutes} min`;
  }

  if (hours < 24) {
    return `${hours.toFixed(hours >= 10 ? 0 : 1)} h`;
  }

  const days = hours / 24;
  return `${days.toFixed(days >= 10 ? 0 : 1)} d`;
}

function resetMessage() {
  error.value = '';
  success.value = '';
}

async function loadDashboard() {
  loading.value = true;
  resetMessage();

  try {
    if (auth.role === 'CUSTOMER' && auth.customerId) {
      const [serviceOrders, vehicles] = await Promise.allSettled([
        resources.customerServiceOrders(auth.customerId),
        resources.customerVehicles(auth.customerId),
      ]);

      data.serviceOrders =
        serviceOrders.status === 'fulfilled' ? listItems(serviceOrders.value) : [];
      data.vehicles = vehicles.status === 'fulfilled' ? listItems(vehicles.value) : [];

      const failed = [serviceOrders, vehicles].filter((request) => request.status === 'rejected');
      if (failed.length) {
        error.value = failed.map((request) => request.reason.message).join(' | ');
      }
      return;
    }

    const requests = await Promise.allSettled([
      auth.role === 'ADMIN' ? resources.customers(pagination.customers) : Promise.resolve(null),
      resources.vehicles(pagination.vehicles),
      resources.services(pagination.services),
      resources.parts(pagination.parts),
      resources.lowStockParts({ size: 20 }),
      resources.serviceOrders(pagination.serviceOrders),
      resources.averageExecutionTime(),
    ]);

    const [customers, vehicles, services, parts, lowStockParts, serviceOrders, average] = requests;

    data.customers =
      customers.status === 'fulfilled' && customers.value ? listItems(customers.value) : [];
    data.vehicles = vehicles.status === 'fulfilled' ? listItems(vehicles.value) : [];
    data.services = services.status === 'fulfilled' ? listItems(services.value) : [];
    data.parts = parts.status === 'fulfilled' ? listItems(parts.value) : [];
    data.lowStockParts = lowStockParts.status === 'fulfilled' ? listItems(lowStockParts.value) : [];
    data.serviceOrders =
      serviceOrders.status === 'fulfilled' ? listItems(serviceOrders.value) : [];
    data.averageExecutionTime = average.status === 'fulfilled' ? average.value : null;

    const failed = requests.filter((request) => {
      if (request.status !== 'rejected') {
        return false;
      }

      const isCustomersRequest = request.reason.path?.startsWith('/api/v1/customers');
      return request.reason.status !== 403 || (auth.role === 'ADMIN' && isCustomersRequest);
    });
    if (failed.length) {
      error.value = failed.map((request) => request.reason.message).join(' | ');
    }
  } catch (err) {
    error.value = err.message || 'Não foi possível carregar os dados.';
  } finally {
    loading.value = false;
  }
}

async function runAction(action, message) {
  saving.value = true;
  resetMessage();

  try {
    await action();
    success.value = message;
    await loadDashboard();
  } catch (err) {
    error.value = err.message || 'Não foi possível concluir a operação.';
  } finally {
    saving.value = false;
  }
}

function createCustomer() {
  return runAction(async () => {
    await resources.createCustomer(forms.customer);
    forms.customer.name = '';
    forms.customer.document = '';
    forms.customer.phone = '';
    forms.customer.email = '';
    forms.customer.address = {
      street: '',
      number: '',
      complement: '',
      neighborhood: '',
      city: '',
      state: 'SP',
      zipCode: '',
    };
  }, 'Cliente cadastrado.');
}

function createVehicle() {
  return runAction(async () => {
    await resources.createVehicle({
      ...forms.vehicle,
      year: Number(forms.vehicle.year),
      mileage: Number(forms.vehicle.mileage),
    });
    forms.vehicle.customerId = '';
    forms.vehicle.plate = '';
    forms.vehicle.brand = '';
    forms.vehicle.model = '';
    forms.vehicle.year = new Date().getFullYear();
    forms.vehicle.mileage = 0;
  }, 'Veículo cadastrado.');
}

function createPart() {
  return runAction(async () => {
    await resources.createPart({
      ...forms.part,
      unitPrice: Number(forms.part.unitPrice),
      stockQuantity: Number(forms.part.stockQuantity),
      minimumStock: Number(forms.part.minimumStock),
    });
    Object.assign(forms.part, {
      name: '',
      sku: '',
      category: '',
      subcategory: '',
      brand: '',
      unitPrice: 0,
      stockQuantity: 0,
      minimumStock: 1,
    });
  }, 'Peça cadastrada.');
}

function createWorkshopService() {
  return runAction(async () => {
    await resources.createWorkshopService({
      ...forms.service,
      basePrice: Number(forms.service.basePrice),
      estimatedTimeInMinutes: Number(forms.service.estimatedTimeInMinutes),
    });
    Object.assign(forms.service, {
      name: '',
      description: '',
      basePrice: 0,
      estimatedTimeInMinutes: 60,
    });
  }, 'Serviço cadastrado.');
}

function createOrder() {
  return runAction(async () => {
    await resources.createServiceOrder(forms.order);
    forms.order.customerDocument = '';
    forms.order.vehicleId = '';
    forms.order.diagnosticNotes = '';
  }, 'Ordem de serviço criada.');
}

function updateStock() {
  return runAction(async () => {
    await resources.updatePartStock(forms.stock.partId, forms.stock.stockQuantity);
    forms.stock.partId = '';
    forms.stock.stockQuantity = 0;
  }, 'Estoque atualizado.');
}

function updateOrderStatus() {
  return runAction(async () => {
    await resources.updateOrderStatus(forms.orderAction.serviceOrderId, forms.orderAction.status);
  }, 'Status da ordem atualizado.');
}

function generateBudget() {
  return runAction(async () => {
    await resources.generateBudget(forms.orderAction.serviceOrderId);
  }, 'Orçamento gerado.');
}

function approveBudget() {
  return runAction(async () => {
    await resources.approveBudget(forms.orderAction.serviceOrderId);
  }, 'Orçamento aprovado.');
}

function addServiceToOrder() {
  return runAction(async () => {
    await resources.addServiceToOrder(forms.orderAction.serviceOrderId, {
      serviceId: forms.orderAction.serviceId,
      quantity: Number(forms.orderAction.serviceQuantity),
    });
  }, 'Serviço adicionado a ordem.');
}

function addPartToOrder() {
  return runAction(async () => {
    await resources.addPartToOrder(forms.orderAction.serviceOrderId, {
      partId: forms.orderAction.partId,
      quantity: Number(forms.orderAction.partQuantity),
    });
  }, 'Peça adicionada a ordem.');
}

function changePage(resource, direction) {
  pagination[resource].page = Math.max(0, pagination[resource].page + direction);
  loadDashboard();
}

function selectTab(tabId) {
  activeTab.value = tabId;
  mobileMenuOpen.value = false;
}

function selectSearchResult(result) {
  selectTab(result.tabId);
  globalSearch.value = '';
}

function showProfileAction(action) {
  profileMenuOpen.value = false;
  success.value = `${action} estará disponível quando o backend expuser esse fluxo.`;
}

function logout() {
  profileMenuOpen.value = false;
  auth.logout();
  router.push({ name: 'login' });
}

onMounted(loadDashboard);
</script>

<template>
  <main class="app-shell" :class="{ 'mobile-sidebar-open': mobileMenuOpen }">
    <header class="site-navbar">
      <div class="navbar-inner">
        <button
          class="menu-button"
          type="button"
          title="Abrir menu"
          @click="mobileMenuOpen = !mobileMenuOpen"
        >
          <X v-if="mobileMenuOpen" :size="22" />
          <Menu v-else :size="22" />
        </button>

        <div class="navbar-brand">
          <div class="brand-mark"><Wrench :size="22" /></div>
          <div>
            <strong>AutoCare Hub</strong>
            <span>{{ roleLabel }}</span>
          </div>
        </div>

        <div class="navbar-search">
          <Search :size="17" />
          <input
            v-model="globalSearch"
            type="search"
            placeholder="Buscar clientes, placas, peças, ordens..."
            aria-label="Busca global"
          />
          <div v-if="searchResults.length" class="search-popover">
            <button
              v-for="result in searchResults"
              :key="`${result.type}-${result.label}-${result.detail}`"
              type="button"
              @click="selectSearchResult(result)"
            >
              <component :is="result.icon" :size="17" />
              <span>
                <strong>{{ result.label }}</strong>
                <small>{{ result.type }} - {{ result.detail }}</small>
              </span>
            </button>
          </div>
        </div>

        <div class="navbar-actions">
          <div class="profile-menu">
            <button
              class="profile-trigger"
              type="button"
              title="Menu do usuário"
              :aria-expanded="profileMenuOpen"
              aria-haspopup="menu"
              @click="profileMenuOpen = !profileMenuOpen"
            >
              {{ userInitials }}
            </button>
            <div v-if="profileMenuOpen" class="profile-popover" role="menu">
              <button type="button" role="menuitem" @click="showProfileAction('Editar informações do usuário')">
                <UserCog :size="17" />
                <span>Editar informações do usuário</span>
              </button>
              <button type="button" role="menuitem" @click="showProfileAction('Alterar senha')">
                <KeyRound :size="17" />
                <span>Alterar senha</span>
              </button>
              <button type="button" role="menuitem" @click="logout">
                <LogOut :size="17" />
                <span>Sair</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <div class="app-layout" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <aside class="app-sidebar" :class="{ collapsed: sidebarCollapsed }">
        <button
          class="sidebar-toggle"
          type="button"
          :title="sidebarCollapsed ? 'Expandir menu' : 'Recolher menu'"
          @click="sidebarCollapsed = !sidebarCollapsed"
        >
          <ChevronRight v-if="sidebarCollapsed" :size="18" />
          <ChevronLeft v-else :size="18" />
          <span>{{ sidebarCollapsed ? 'Expandir' : 'Recolher' }}</span>
        </button>

        <nav class="side-nav" aria-label="Acessos principais">
          <button
            v-for="tab in availableTabs"
            :key="tab.id"
            type="button"
            :class="{ active: activeTab === tab.id }"
            :title="tab.label"
            @click="selectTab(tab.id)"
          >
            <component :is="tab.icon" :size="20" />
            <span>
              <strong>{{ tab.label }}</strong>
            </span>
          </button>
        </nav>
      </aside>

      <button
        v-if="mobileMenuOpen"
        class="sidebar-backdrop"
        type="button"
        aria-label="Fechar menu"
        @click="mobileMenuOpen = false"
      ></button>

      <section class="content">
        <section class="hero-panel">
          <div>
            <span class="eyebrow"><ShieldCheck :size="16" /> {{ auth.user?.username }}</span>
            <h1>Controle inteligente para oficinas modernas</h1>
            <p>
              Acompanhe ordens, estoque, clientes e veículos em um painel unico para atendimento,
              compras e execução dos serviços.
            </p>
          </div>
          <div class="hero-kpis">
            <article v-for="insight in quickInsights" :key="insight.label">
              <component :is="insight.icon" :size="20" />
              <strong>{{ insight.value }}</strong>
              <span>{{ insight.label }}</span>
            </article>
          </div>
        </section>

        <section class="workspace-header" :aria-label="activeTabMeta.description">
          <div class="highlight-strip">
            <article
              v-for="item in operationalHighlights"
              :key="item.label"
              :class="`tone-${item.tone}`"
            >
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
              <small>{{ item.detail }}</small>
            </article>
          </div>
        </section>

        <p v-if="error" class="alert error">{{ error }}</p>
        <p v-if="success" class="alert success">{{ success }}</p>

        <section v-if="activeTab === 'overview'" class="screen-stack">
          <div class="metric-grid">
            <article class="metric-card">
              <span>Clientes</span>
              <strong>{{ data.customers.length }}</strong>
            </article>
            <article class="metric-card">
              <span>Veículos</span>
              <strong>{{ data.vehicles.length }}</strong>
            </article>
            <article class="metric-card warning">
              <span>Peças para comprar</span>
              <strong>{{ data.lowStockParts.length }}</strong>
            </article>
            <article class="metric-card">
              <span>Estoque saudável</span>
              <strong>{{ healthyParts.length }}</strong>
            </article>
            <article class="metric-card">
              <span>Média execução</span>
              <strong>{{ averageExecutionLabel }}</strong>
            </article>
          </div>

          <section class="section-block">
            <div class="section-heading">
              <h2>Alertas de compra</h2>
              <span>Estoque abaixo do minimo</span>
            </div>
            <div class="table-list">
              <article v-for="part in data.lowStockParts" :key="part.id" class="row-item danger">
                <div>
                  <strong>{{ part.name }}</strong>
                  <span>{{ part.sku }} - minimo {{ part.minimumStock }}</span>
                </div>
                <span>{{ part.stockQuantity }} un.</span>
              </article>
              <p v-if="!data.lowStockParts.length" class="empty-state">Nenhum alerta de estoque.</p>
            </div>
          </section>

          <section class="analytics-grid">
            <article class="section-block chart-panel">
              <div class="section-heading">
                <h2>Status atual dos carros</h2>
                <span>{{ data.serviceOrders.length }} ordens monitoradas</span>
              </div>
              <div class="bar-chart">
                <div v-for="item in statusChart" :key="item.status" class="bar-row">
                  <span>{{ item.label }}</span>
                  <div class="bar-track">
                    <i :style="{ width: `${item.percent}%`, background: item.color }"></i>
                  </div>
                  <strong>{{ item.count }}</strong>
                </div>
              </div>
            </article>

            <article class="section-block chart-panel">
              <div class="section-heading">
                <h2>Saúde do estoque</h2>
                <span>Itens abaixo do mínimo</span>
              </div>
              <div class="donut-wrap">
                <div
                  class="donut-chart"
                  :style="{
                    background: `conic-gradient(#f59e0b 0 ${inventoryHealth[1].percent}%, #0ea5e9 ${inventoryHealth[1].percent}% 100%)`,
                  }"
                >
                  <span>{{ inventoryHealth[1].percent }}%</span>
                </div>
                <div class="legend-list">
                  <span v-for="item in inventoryHealth" :key="item.label">
                    <i :style="{ background: item.color }"></i>
                    {{ item.label }}: {{ item.value }}
                  </span>
                </div>
              </div>
            </article>

            <article class="section-block chart-panel">
              <div class="section-heading">
                <h2>Resumo por status</h2>
                <span>Distribuição rápida</span>
              </div>
              <div class="status-grid">
                <article v-for="item in statusCounts" :key="item.status" class="status-card">
                  <strong>{{ item.count }}</strong>
                  <span>{{ item.label }}</span>
                </article>
              </div>
            </article>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Prazo previsto x realizado por serviço</h2>
              <span>Média por serviço em ordens finalizadas ou entregues</span>
            </div>
            <div class="comparison-list">
              <article
                v-for="item in serviceSlaComparisons"
                :key="item.serviceId"
                class="comparison-row"
                :class="{ late: item.difference > 0 }"
              >
                <div>
                  <strong>{{ item.name }}</strong>
                  <span>{{ item.count }} ordem(ns) analisada(s)</span>
                </div>
                <div class="comparison-bars">
                  <label>
                    Previsto
                    <i><b class="planned" style="width: 100%"></b></i>
                    <span>{{ item.plannedLabel }}</span>
                  </label>
                  <label>
                    Realizado
                    <i><b :style="{ width: `${item.actualPercent}%` }"></b></i>
                    <span>{{ item.actualLabel }}</span>
                  </label>
                </div>
                <span class="badge" :class="{ danger: item.difference > 0 }">
                  {{ item.status }} {{ item.differenceLabel }}
                </span>
              </article>
              <p v-if="!serviceSlaComparisons.length" class="empty-state">
                Ainda não há ordens finalizadas suficientes para comparar prazos.
              </p>
            </div>
            <p class="hint">
              Quando uma ordem possui vários serviços, o tempo real é distribuído proporcionalmente ao
              tempo previsto de cada item.
            </p>
          </section>

          <section class="section-block info-panel">
            <BarChart3 :size="24" />
            <div>
              <h2>Fluxo sugerido</h2>
              <p>
                Cadastre o cliente, associe o veículo, crie a ordem de serviço, adicione peças e
                serviços, gere o orçamento e acompanhe o status até a entrega.
              </p>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'customers'" class="screen-stack">
          <section v-if="auth.role === 'ADMIN'" class="section-block">
            <div class="section-heading">
              <h2>Cadastro e conta do cliente</h2>
              <span>Cria o cadastro base usado por veículos e ordens</span>
            </div>
            <form class="form-grid" @submit.prevent="createCustomer">
              <input v-model="forms.customer.name" placeholder="Nome" required />
              <input v-model="forms.customer.document" placeholder="CPF/CNPJ somente números" required />
              <input v-model="forms.customer.phone" placeholder="Telefone" required />
              <input v-model="forms.customer.email" placeholder="E-mail" type="email" required />
              <input v-model="forms.customer.address.street" placeholder="Rua" required />
              <input v-model="forms.customer.address.number" placeholder="Número" required />
              <input v-model="forms.customer.address.neighborhood" placeholder="Bairro" required />
              <input v-model="forms.customer.address.city" placeholder="Cidade" required />
              <input v-model="forms.customer.address.state" placeholder="UF" maxlength="2" required />
              <input v-model="forms.customer.address.zipCode" placeholder="CEP" required />
              <input v-model="forms.customer.address.complement" placeholder="Complemento" />
              <button class="primary-button" type="submit" :disabled="saving">
                <UserPlus :size="18" />
                <span>Cadastrar cliente</span>
              </button>
            </form>
            <p class="hint">
              O backend atual ainda não expõe criação de credenciais para login de cliente; este fluxo
              cria o cadastro do cliente na API.
            </p>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Clientes</h2>
              <span>Página {{ pagination.customers.page + 1 }}</span>
            </div>
            <div class="filters">
              <select v-model="pagination.customers.active">
                <option value="">Todos</option>
                <option value="true">Ativos</option>
                <option value="false">Inativos</option>
              </select>
              <button class="secondary-button" type="button" @click="loadDashboard">
                <Search :size="17" />
                Filtrar
              </button>
            </div>
            <div class="data-table">
              <div class="data-table-header customers-grid">
                <span>Cliente</span>
                <span>Contato</span>
                <span>Documento</span>
                <span>Status</span>
              </div>
              <article
                v-for="customer in data.customers"
                :key="customer.id"
                class="data-table-row customers-grid"
              >
                <strong>{{ customer.name }}</strong>
                <span>{{ customer.email }}<small>{{ customer.phone }}</small></span>
                <code>{{ customer.document }}</code>
                <span class="badge"><CheckCircle2 :size="14" /> Ativo</span>
              </article>
            </div>
            <div class="pager">
              <button type="button" @click="changePage('customers', -1)">Anterior</button>
              <button type="button" @click="changePage('customers', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'vehicles'" class="screen-stack">
          <section v-if="auth.role === 'ADMIN'" class="section-block">
            <div class="section-heading">
              <h2>Cadastro de veículo</h2>
              <span>Vinculado ao cliente</span>
            </div>
            <form class="form-grid compact" @submit.prevent="createVehicle">
              <input v-model="forms.vehicle.customerId" placeholder="ID do cliente" required />
              <input v-model="forms.vehicle.plate" placeholder="Placa ABC1D23" required />
              <input v-model="forms.vehicle.brand" placeholder="Marca" required />
              <input v-model="forms.vehicle.model" placeholder="Modelo" required />
              <input v-model.number="forms.vehicle.year" type="number" placeholder="Ano" required />
              <input v-model.number="forms.vehicle.mileage" type="number" placeholder="Km" required />
              <button class="primary-button" type="submit" :disabled="saving">
                <Plus :size="18" />
                <span>Cadastrar veículo</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Status dos carros</h2>
              <span>Página {{ pagination.vehicles.page + 1 }}</span>
            </div>
            <div class="filters">
              <select v-model="pagination.vehicles.active">
                <option value="">Todos</option>
                <option value="true">Ativos</option>
                <option value="false">Inativos</option>
              </select>
              <button class="secondary-button" type="button" @click="loadDashboard">
                <Search :size="17" />
                Filtrar
              </button>
            </div>
            <div class="data-table">
              <div class="data-table-header vehicles-grid">
                <span>Placa</span>
                <span>Veículo</span>
                <span>Km</span>
                <span>Status</span>
              </div>
              <article
                v-for="vehicle in vehiclesWithCurrentStatus"
                :key="vehicle.id"
                class="data-table-row vehicles-grid"
              >
                <strong>{{ vehicle.plate }}</strong>
                <span>{{ vehicle.brand }} {{ vehicle.model }}<small>{{ vehicle.year }}</small></span>
                <span>{{ vehicle.mileage }} km</span>
                <span class="badge">{{ vehicle.currentStatus }}</span>
              </article>
            </div>
            <div class="pager">
              <button type="button" @click="changePage('vehicles', -1)">Anterior</button>
              <button type="button" @click="changePage('vehicles', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'parts'" class="screen-stack">
          <section v-if="auth.role === 'ADMIN'" class="section-block">
            <div class="section-heading">
              <h2>Cadastro de peças</h2>
              <span>Catálogo e minimo de estoque</span>
            </div>
            <form class="form-grid" @submit.prevent="createPart">
              <input v-model="forms.part.name" placeholder="Nome" required />
              <input v-model="forms.part.sku" placeholder="SKU" required />
              <input v-model="forms.part.category" placeholder="Categoria" required />
              <input v-model="forms.part.subcategory" placeholder="Subcategoria" />
              <input v-model="forms.part.brand" placeholder="Marca" required />
              <input v-model.number="forms.part.unitPrice" type="number" min="0" step="0.01" placeholder="Preço unitário" required />
              <input v-model.number="forms.part.stockQuantity" type="number" min="0" placeholder="Estoque" required />
              <input v-model.number="forms.part.minimumStock" type="number" min="0" placeholder="Estoque minimo" required />
              <button class="primary-button" type="submit" :disabled="saving">
                <Plus :size="18" />
                <span>Cadastrar peça</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Atualizar estoque</h2>
              <span>Reposição ou ajuste</span>
            </div>
            <form class="form-grid compact" @submit.prevent="updateStock">
              <select v-model="forms.stock.partId" required>
                <option value="">Selecione uma peça</option>
                <option v-for="part in data.parts" :key="part.id" :value="part.id">
                  {{ part.name }} - {{ part.sku }}
                </option>
              </select>
              <input v-model.number="forms.stock.stockQuantity" type="number" min="0" placeholder="Nova quantidade" required />
              <button class="primary-button" type="submit" :disabled="saving">
                <Package :size="18" />
                <span>Atualizar</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Estoque</h2>
              <span>{{ data.lowStockParts.length }} alertas de compra</span>
            </div>
            <div class="filters">
              <select v-model="pagination.parts.lowStock">
                <option value="">Todos</option>
                <option value="true">Somente baixo estoque</option>
              </select>
              <select v-model="pagination.parts.active">
                <option value="">Todos</option>
                <option value="true">Ativos</option>
                <option value="false">Inativos</option>
              </select>
              <button class="secondary-button" type="button" @click="loadDashboard">
                <Search :size="17" />
                Filtrar
              </button>
            </div>
            <div class="data-table">
              <div class="data-table-header parts-grid">
                <span>Peça</span>
                <span>Categoria</span>
                <span>Estoque</span>
                <span>Preço</span>
                <span>Sinal</span>
              </div>
              <article
                v-for="part in data.parts"
                :key="part.id"
                class="data-table-row parts-grid"
                :class="{ danger: part.stockQuantity <= part.minimumStock }"
              >
                <strong>{{ part.name }}<small>{{ part.sku }}</small></strong>
                <span>{{ part.category }}<small>{{ part.brand }}</small></span>
                <span>{{ part.stockQuantity }} un.<small>Min. {{ part.minimumStock }}</small></span>
                <span>R$ {{ money(part.unitPrice) }}</span>
                <span class="badge" :class="{ danger: part.stockQuantity <= part.minimumStock }">
                  {{ part.stockQuantity <= part.minimumStock ? 'Comprar' : 'Ok' }}
                </span>
              </article>
            </div>
            <div class="pager">
              <button type="button" @click="changePage('parts', -1)">Anterior</button>
              <button type="button" @click="changePage('parts', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'orders'" class="screen-stack">
          <section v-if="auth.role !== 'CUSTOMER'" class="section-block">
            <div class="section-heading">
              <h2>Criar ordem de serviço</h2>
              <span>Entrada do carro na oficina</span>
            </div>
            <form class="form-grid compact" @submit.prevent="createOrder">
              <input v-model="forms.order.customerDocument" placeholder="CPF/CNPJ do cliente" required />
              <select v-model="forms.order.vehicleId" required>
                <option value="">Selecione o veículo</option>
                <option v-for="vehicle in data.vehicles" :key="vehicle.id" :value="vehicle.id">
                  {{ vehicle.plate }} - {{ vehicle.brand }} {{ vehicle.model }}
                </option>
              </select>
              <textarea v-model="forms.order.diagnosticNotes" placeholder="Diagnóstico inicial" required></textarea>
              <button class="primary-button" type="submit" :disabled="saving">
                <Plus :size="18" />
                <span>Criar ordem</span>
              </button>
            </form>
          </section>

          <section v-if="auth.role !== 'CUSTOMER'" class="section-block">
            <div class="section-heading">
              <h2>Operações da ordem</h2>
              <span>Status, orçamento, peças e serviços</span>
            </div>
            <form class="form-grid" @submit.prevent="updateOrderStatus">
              <select v-model="forms.orderAction.serviceOrderId" required>
                <option value="">Selecione a ordem</option>
                <option v-for="order in data.serviceOrders" :key="order.id" :value="order.id">
                  {{ order.status }} - {{ order.diagnosticNotes }}
                </option>
              </select>
              <select v-model="forms.orderAction.status">
                <option v-for="status in statuses" :key="status" :value="status">{{ status }}</option>
              </select>
              <button class="primary-button" type="submit" :disabled="saving">Atualizar status</button>
              <button class="secondary-button" type="button" :disabled="saving || !forms.orderAction.serviceOrderId" @click="generateBudget">
                Gerar orçamento
              </button>
              <button class="secondary-button" type="button" :disabled="saving || !forms.orderAction.serviceOrderId" @click="approveBudget">
                Aprovar orçamento
              </button>
            </form>
            <form class="form-grid compact" @submit.prevent="addServiceToOrder">
              <select v-model="forms.orderAction.serviceId" required>
                <option value="">Serviço</option>
                <option v-for="service in data.services" :key="service.id" :value="service.id">{{ service.name }}</option>
              </select>
              <input v-model.number="forms.orderAction.serviceQuantity" type="number" min="1" placeholder="Qtd." required />
              <button class="secondary-button" type="submit" :disabled="saving || !forms.orderAction.serviceOrderId">Adicionar serviço</button>
            </form>
            <form class="form-grid compact" @submit.prevent="addPartToOrder">
              <select v-model="forms.orderAction.partId" required>
                <option value="">Peça</option>
                <option v-for="part in data.parts" :key="part.id" :value="part.id">{{ part.name }}</option>
              </select>
              <input v-model.number="forms.orderAction.partQuantity" type="number" min="1" placeholder="Qtd." required />
              <button class="secondary-button" type="submit" :disabled="saving || !forms.orderAction.serviceOrderId">Adicionar peça</button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Ordens de serviço</h2>
              <span>Status atual dos carros</span>
            </div>
            <div v-if="auth.role !== 'CUSTOMER'" class="filters">
              <select v-model="pagination.serviceOrders.status">
                <option value="">Todos os status</option>
                <option v-for="status in statuses" :key="status" :value="status">{{ status }}</option>
              </select>
              <button class="secondary-button" type="button" @click="loadDashboard">
                <Search :size="17" />
                Filtrar
              </button>
            </div>
            <div class="data-table">
              <div class="data-table-header orders-grid">
                <span>Status</span>
                <span>Nota</span>
                <span>Itens</span>
                <span>Total</span>
              </div>
              <article
                v-for="order in data.serviceOrders"
                :key="order.id"
                class="data-table-row orders-grid"
              >
                <span class="badge">{{ order.status }}</span>
                <span>{{ order.diagnosticNotes }}<small>{{ order.id }}</small></span>
                <span>
                  {{ order.services?.length || 0 }} serviços
                  <small>{{ order.parts?.length || 0 }} peças</small>
                </span>
                <strong>R$ {{ money(order.totalAmount) }}</strong>
              </article>
              <p v-if="!data.serviceOrders.length && !loading" class="empty-state">Nenhuma ordem encontrada.</p>
            </div>
            <div v-if="auth.role !== 'CUSTOMER'" class="pager">
              <button type="button" @click="changePage('serviceOrders', -1)">Anterior</button>
              <button type="button" @click="changePage('serviceOrders', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'services'" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Cadastro de serviços</h2>
              <span>Catálogo da oficina</span>
            </div>
            <form v-if="auth.role === 'ADMIN'" class="form-grid compact" @submit.prevent="createWorkshopService">
              <input v-model="forms.service.name" placeholder="Nome" required />
              <input v-model.number="forms.service.basePrice" type="number" min="0" step="0.01" placeholder="Preço base" required />
              <input v-model.number="forms.service.estimatedTimeInMinutes" type="number" min="1" placeholder="Tempo em minutos" required />
              <textarea v-model="forms.service.description" placeholder="Descrição" required></textarea>
              <button class="primary-button" type="submit" :disabled="saving">
                <Plus :size="18" />
                <span>Cadastrar serviço</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Serviços cadastrados</h2>
              <span>Página {{ pagination.services.page + 1 }}</span>
            </div>
            <div class="data-table">
              <div class="data-table-header services-grid">
                <span>Serviço</span>
                <span>Prazo previsto</span>
                <span>Preço base</span>
              </div>
              <article
                v-for="service in data.services"
                :key="service.id"
                class="data-table-row services-grid"
              >
                <strong>{{ service.name }}<small>{{ service.description }}</small></strong>
                <span>{{ formatDuration(service.estimatedTimeInMinutes) }}</span>
                <span>R$ {{ money(service.basePrice) }}</span>
              </article>
            </div>
            <div class="pager">
              <button type="button" @click="changePage('services', -1)">Anterior</button>
              <button type="button" @click="changePage('services', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <div v-if="loading" class="loading-bar">Carregando dados...</div>
      </section>
    </div>
  </main>
</template>
