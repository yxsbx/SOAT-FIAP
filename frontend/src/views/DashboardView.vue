<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  AlertTriangle,
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
const homeSettingsOpen = ref(false);

const defaultHomeWidgetIds = [
  'orders-progress',
  'services-catalog',
  'active-customers',
  'vehicles-in-service',
  'pending-budgets',
  'waiting-contact',
  'ready-pickup',
];

const homePreferences = reactive({
  userWidgets: [...defaultHomeWidgetIds],
  globalWidgets: [...defaultHomeWidgetIds],
  showAlertsOnHome: false,
});

const statuses = ['RECEIVED', 'IN_DIAGNOSIS', 'WAITING_APPROVAL', 'IN_PROGRESS', 'FINISHED', 'DELIVERED'];

const statusLabels = {
  RECEIVED: 'Orçamento pendente',
  IN_DIAGNOSIS: 'Em diagnóstico',
  WAITING_APPROVAL: 'Orçamento enviado',
  IN_PROGRESS: 'Em execução',
  FINISHED: 'Pronto para retirada',
  DELIVERED: 'Entregue',
};

const orderScenarios = [
  {
    id: 'existing-customer-vehicle',
    label: 'Cliente e veículo cadastrados',
    text: 'Selecione cliente e veículo já existentes.',
  },
  {
    id: 'new-customer',
    label: 'Novo cliente',
    text: 'Cadastre cliente, veículo e abra a ordem.',
  },
  {
    id: 'existing-customer-new-vehicle',
    label: 'Cliente existente, veículo novo',
    text: 'Confirme o cliente e cadastre o veículo.',
  },
];

const orderSteps = [
  'Cenário',
  'Cliente',
  'Veículo',
  'Defeitos',
  'Valores',
  'Finalização',
];

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
  customers: { page: 0, size: 24, active: '' },
  vehicles: { page: 0, size: 24, active: '' },
  parts: { page: 0, size: 32, active: '', lowStock: '' },
  serviceOrders: { page: 0, size: 32, status: '' },
  services: { page: 0, size: 24, active: '' },
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
  orderWizard: {
    scenario: 'existing-customer-vehicle',
    step: 0,
    customerId: '',
    vehicleId: '',
    defects: '',
    initialValueNotes: '',
    serviceId: '',
    serviceQuantity: 1,
    partId: '',
    partQuantity: 1,
    contactRequested: false,
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
      plate: '',
      brand: '',
      model: '',
      year: new Date().getFullYear(),
      mileage: 0,
    },
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

const demoProfile = computed(() => {
  const profiles = {
    'master@autocarehub.com': {
      label: 'Admin Master',
      tabs: ['overview', 'orders', 'customers', 'vehicles', 'parts', 'services'],
    },
    'oficina.admin@autocarehub.com': {
      label: 'Admin de oficina',
      tabs: ['overview', 'orders', 'customers', 'vehicles', 'parts', 'services'],
    },
    'loja.admin@autocarehub.com': {
      label: 'Admin de loja de peças',
      tabs: ['overview', 'orders', 'parts', 'services'],
    },
    'oficina.funcionario@autocarehub.com': {
      label: 'Funcionário de oficina',
      tabs: ['overview', 'orders', 'vehicles', 'parts', 'services'],
    },
    'loja.funcionario@autocarehub.com': {
      label: 'Funcionário de loja de peças',
      tabs: ['overview', 'parts', 'services'],
    },
    'cliente@autocarehub.com': {
      label: 'Cliente final',
      tabs: ['overview', 'orders'],
    },
  };

  return profiles[auth.user?.username] || null;
});

const roleLabel = computed(() => {
  if (demoProfile.value?.label) {
    return demoProfile.value.label;
  }

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
      description: 'Frota, placas e status dos veículos',
      icon: Car,
      roles: ['ADMIN', 'EMPLOYEE'],
    },
    {
      id: 'parts',
      label: 'Estoque',
      description: 'Peças, avisos e reposição',
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

  return tabs.filter((tab) => {
    const allowedByRole = tab.roles.includes(auth.role);
    const allowedByProfile = !demoProfile.value || demoProfile.value.tabs.includes(tab.id);
    return allowedByRole && allowedByProfile;
  });
});

const availableTabIds = computed(() => new Set(availableTabs.value.map((tab) => tab.id)));

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

const orderCountByStatus = (status) =>
  data.serviceOrders.filter((order) => order.status === status).length;

const ordersWaitingContact = computed(() =>
  data.serviceOrders.filter((order) => normalize(order.diagnosticNotes).includes('contato')).length,
);

const orderFlowStats = computed(() => [
  {
    label: 'Clientes ativos',
    value: data.customers.length,
  },
  {
    label: 'Veículos em andamento',
    value: data.serviceOrders.filter((order) => !['FINISHED', 'DELIVERED'].includes(order.status))
      .length,
  },
  {
    label: 'Clientes aguardando envio de orçamento',
    value: orderCountByStatus('RECEIVED'),
  },
  {
    label: 'Clientes que querem ser contatados',
    value: ordersWaitingContact.value,
  },
]);

const selectedOrderCustomer = computed(() =>
  data.customers.find((customer) => customer.id === forms.orderWizard.customerId),
);

const orderCustomerVehicles = computed(() =>
  data.vehicles.filter((vehicle) => vehicle.customerId === forms.orderWizard.customerId),
);

const selectedOrderVehicle = computed(() =>
  data.vehicles.find((vehicle) => vehicle.id === forms.orderWizard.vehicleId),
);

const selectedOrderService = computed(() =>
  data.services.find((service) => service.id === forms.orderWizard.serviceId),
);

const selectedOrderPart = computed(() =>
  data.parts.find((part) => part.id === forms.orderWizard.partId),
);

const estimatedOrderTotal = computed(() => {
  const serviceTotal = selectedOrderService.value
    ? Number(selectedOrderService.value.basePrice || 0) * Number(forms.orderWizard.serviceQuantity || 0)
    : 0;
  const partTotal = selectedOrderPart.value
    ? Number(selectedOrderPart.value.unitPrice || 0) * Number(forms.orderWizard.partQuantity || 0)
    : 0;
  return serviceTotal + partTotal;
});

const isNewCustomerScenario = computed(() => forms.orderWizard.scenario === 'new-customer');

const needsNewVehicle = computed(() =>
  ['new-customer', 'existing-customer-new-vehicle'].includes(forms.orderWizard.scenario),
);

const homeWidgetDefinitions = computed(() => [
  {
    id: 'orders-progress',
    label: 'Ordens em andamento',
    value: data.serviceOrders.filter((order) =>
      ['RECEIVED', 'IN_DIAGNOSIS', 'WAITING_APPROVAL', 'IN_PROGRESS'].includes(order.status),
    ).length,
    icon: TrendingUp,
    tabId: 'orders',
    roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'],
  },
  {
    id: 'services-catalog',
    label: 'Serviços no catálogo',
    value: data.services.length,
    icon: Wrench,
    tabId: 'services',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
  {
    id: 'active-customers',
    label: 'Clientes ativos',
    value: data.customers.length,
    icon: Users,
    tabId: 'customers',
    roles: ['ADMIN'],
  },
  {
    id: 'vehicles-in-service',
    label: 'Veículos em atendimento',
    value: data.serviceOrders.filter((order) => !['FINISHED', 'DELIVERED'].includes(order.status))
      .length,
    icon: Car,
    tabId: 'vehicles',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
  {
    id: 'pending-budgets',
    label: 'Orçamentos pendentes',
    value: orderCountByStatus('WAITING_APPROVAL'),
    icon: ClipboardList,
    tabId: 'orders',
    statusFilter: 'WAITING_APPROVAL',
    roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'],
  },
  {
    id: 'waiting-contact',
    label: 'Clientes aguardando contato',
    value: orderCountByStatus('RECEIVED'),
    icon: Users,
    tabId: 'orders',
    statusFilter: 'RECEIVED',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
  {
    id: 'ready-pickup',
    label: 'Veículos prontos para retirada',
    value: orderCountByStatus('FINISHED'),
    icon: CheckCircle2,
    tabId: 'orders',
    statusFilter: 'FINISHED',
    roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'],
  },
]);

const visibleHomeWidgetIds = computed(() => new Set(homePreferences.userWidgets));

const availableHomeWidgetDefinitions = computed(() =>
  homeWidgetDefinitions.value.filter(
    (widget) =>
      widget.roles.includes(auth.role) && (!widget.tabId || availableTabIds.value.has(widget.tabId)),
  ),
);

const homeWidgets = computed(() =>
  availableHomeWidgetDefinitions.value.filter((widget) => visibleHomeWidgetIds.value.has(widget.id)),
);

const vehicleStatusWidgets = computed(() => [
  {
    label: 'Em diagnóstico',
    value: orderCountByStatus('IN_DIAGNOSIS'),
    statusFilter: 'IN_DIAGNOSIS',
  },
  {
    label: 'Aguardando orçamento',
    value: orderCountByStatus('RECEIVED'),
    statusFilter: 'RECEIVED',
  },
  {
    label: 'Orçamento enviado',
    value: orderCountByStatus('WAITING_APPROVAL'),
    statusFilter: 'WAITING_APPROVAL',
  },
  {
    label: 'Aguardando aprovação',
    value: orderCountByStatus('WAITING_APPROVAL'),
    statusFilter: 'WAITING_APPROVAL',
  },
  {
    label: 'Em execução',
    value: orderCountByStatus('IN_PROGRESS'),
    statusFilter: 'IN_PROGRESS',
  },
  {
    label: 'Concluído',
    value: orderCountByStatus('DELIVERED'),
    statusFilter: 'DELIVERED',
  },
  {
    label: 'Pronto para retirada',
    value: orderCountByStatus('FINISHED'),
    statusFilter: 'FINISHED',
  },
]);

const showHomeAlerts = computed(() => homePreferences.showAlertsOnHome);

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

function userHomeKey() {
  return `autocare.home.${auth.user?.id || auth.user?.username || 'guest'}`;
}

function readStoredJson(key, fallback) {
  try {
    return JSON.parse(localStorage.getItem(key)) || fallback;
  } catch {
    return fallback;
  }
}

function loadHomePreferences() {
  const globalConfig = readStoredJson('autocare.home.workshop.global', {
    widgets: defaultHomeWidgetIds,
    showAlertsOnHome: false,
  });
  const userConfig = readStoredJson(userHomeKey(), {
    widgets: globalConfig.widgets || defaultHomeWidgetIds,
  });

  homePreferences.globalWidgets = [...(globalConfig.widgets || defaultHomeWidgetIds)];
  homePreferences.showAlertsOnHome = Boolean(globalConfig.showAlertsOnHome);
  homePreferences.userWidgets = [...(userConfig.widgets ?? homePreferences.globalWidgets)];
}

function saveUserHomePreferences() {
  localStorage.setItem(userHomeKey(), JSON.stringify({ widgets: homePreferences.userWidgets }));
}

function saveGlobalHomePreferences() {
  localStorage.setItem(
    'autocare.home.workshop.global',
    JSON.stringify({
      widgets: homePreferences.globalWidgets,
      showAlertsOnHome: homePreferences.showAlertsOnHome,
    }),
  );
}

function toggleHomeWidget(widgetId, scope = 'user') {
  const target = scope === 'global' ? homePreferences.globalWidgets : homePreferences.userWidgets;
  const index = target.indexOf(widgetId);

  if (index >= 0) {
    target.splice(index, 1);
  } else if (index < 0) {
    target.push(widgetId);
  }

  if (scope === 'global') {
    saveGlobalHomePreferences();
    return;
  }

  saveUserHomePreferences();
}

function toggleHomeAlerts() {
  homePreferences.showAlertsOnHome = !homePreferences.showAlertsOnHome;
  saveGlobalHomePreferences();
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
      auth.role !== 'CUSTOMER' ? resources.customers(pagination.customers) : Promise.resolve(null),
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

function resetOrderWizard() {
  Object.assign(forms.orderWizard, {
    scenario: 'existing-customer-vehicle',
    step: 0,
    customerId: '',
    vehicleId: '',
    defects: '',
    initialValueNotes: '',
    serviceId: '',
    serviceQuantity: 1,
    partId: '',
    partQuantity: 1,
    contactRequested: false,
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
      plate: '',
      brand: '',
      model: '',
      year: new Date().getFullYear(),
      mileage: 0,
    },
  });
}

function selectOrderScenario(scenario) {
  forms.orderWizard.scenario = scenario;
  forms.orderWizard.customerId = '';
  forms.orderWizard.vehicleId = '';
  forms.orderWizard.step = 1;
}

function nextOrderStep() {
  forms.orderWizard.step = Math.min(orderSteps.length - 1, forms.orderWizard.step + 1);
}

function previousOrderStep() {
  forms.orderWizard.step = Math.max(0, forms.orderWizard.step - 1);
}

function buildOrderNotes() {
  const notes = [`Defeitos percebidos: ${forms.orderWizard.defects.trim()}`];

  if (forms.orderWizard.initialValueNotes.trim()) {
    notes.push(`Valores iniciais previstos: ${forms.orderWizard.initialValueNotes.trim()}`);
  }

  if (estimatedOrderTotal.value > 0) {
    notes.push(`Estimativa inicial cadastrada: R$ ${money(estimatedOrderTotal.value)}`);
  }

  if (forms.orderWizard.contactRequested) {
    notes.push('Cliente solicitou contato antes do envio do orçamento.');
  }

  return notes.join('\n');
}

async function createOrderFromWizard(createBudgetNow) {
  saving.value = true;
  resetMessage();

  try {
    let customer = selectedOrderCustomer.value;
    if (isNewCustomerScenario.value) {
      customer = await resources.createCustomer(forms.orderWizard.customer);
    }

    let vehicle = selectedOrderVehicle.value;
    if (needsNewVehicle.value) {
      vehicle = await resources.createVehicle({
        ...forms.orderWizard.vehicle,
        customerId: customer.id,
        year: Number(forms.orderWizard.vehicle.year),
        mileage: Number(forms.orderWizard.vehicle.mileage),
      });
    }

    const order = await resources.createServiceOrder({
      customerDocument: customer.document,
      vehicleId: vehicle.id,
      diagnosticNotes: buildOrderNotes(),
    });

    if (forms.orderWizard.serviceId) {
      await resources.addServiceToOrder(order.id, {
        serviceId: forms.orderWizard.serviceId,
        quantity: Number(forms.orderWizard.serviceQuantity),
      });
    }

    if (forms.orderWizard.partId) {
      await resources.addPartToOrder(order.id, {
        partId: forms.orderWizard.partId,
        quantity: Number(forms.orderWizard.partQuantity),
      });
    }

    forms.orderAction.serviceOrderId = order.id;
    if (createBudgetNow) {
      await resources.generateBudget(order.id);
      pagination.serviceOrders.status = 'WAITING_APPROVAL';
      success.value = 'Ordem salva e orçamento gerado.';
    } else {
      pagination.serviceOrders.status = 'RECEIVED';
      success.value = 'Ordem salva como orçamento pendente.';
    }

    resetOrderWizard();
    await loadDashboard();
  } catch (err) {
    error.value = err.message || 'Não foi possível criar a ordem.';
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

function openHomeWidget(widget) {
  if (widget.statusFilter) {
    pagination.serviceOrders.status = widget.statusFilter;
  }

  selectTab(widget.tabId);
}

function openStatusWidget(statusFilter) {
  pagination.serviceOrders.status = statusFilter;
  selectTab('orders');
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

onMounted(() => {
  loadHomePreferences();
  loadDashboard();
});
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
        <p v-if="error" class="alert error">{{ error }}</p>
        <p v-if="success" class="alert success">{{ success }}</p>

        <section v-if="activeTab === 'overview'" class="screen-stack">
          <div class="home-toolbar">
            <span><ShieldCheck :size="16" /> {{ auth.user?.username }}</span>
            <button class="secondary-button" type="button" @click="homeSettingsOpen = !homeSettingsOpen">
              <Plus :size="17" />
              Personalizar home
            </button>
          </div>

          <section v-if="homeSettingsOpen" class="home-settings-panel">
            <div>
              <strong>Meus widgets</strong>
              <div class="home-option-grid">
                <label v-for="widget in availableHomeWidgetDefinitions" :key="widget.id">
                  <input
                    type="checkbox"
                    :checked="homePreferences.userWidgets.includes(widget.id)"
                    @change="toggleHomeWidget(widget.id)"
                  />
                  <span>{{ widget.label }}</span>
                </label>
              </div>
            </div>
            <div v-if="auth.role === 'ADMIN'">
              <strong>Configuração da oficina</strong>
              <label class="home-alert-toggle">
                <input
                  type="checkbox"
                  :checked="homePreferences.showAlertsOnHome"
                  @change="toggleHomeAlerts"
                />
                <span>Exibir avisos críticos de estoque para a equipe</span>
              </label>
              <div class="home-option-grid">
                <label v-for="widget in availableHomeWidgetDefinitions" :key="`global-${widget.id}`">
                  <input
                    type="checkbox"
                    :checked="homePreferences.globalWidgets.includes(widget.id)"
                    @change="toggleHomeWidget(widget.id, 'global')"
                  />
                  <span>{{ widget.label }}</span>
                </label>
              </div>
            </div>
          </section>

          <section class="home-summary-grid">
            <button
              v-for="widget in homeWidgets"
              :key="widget.id"
              class="home-widget"
              type="button"
              @click="openHomeWidget(widget)"
            >
              <component :is="widget.icon" :size="22" />
              <strong>{{ widget.value }}</strong>
              <span>{{ widget.label }}</span>
            </button>
          </section>

          <section class="vehicle-status-grid" aria-label="Status atual dos veículos">
            <button
              v-for="item in vehicleStatusWidgets"
              :key="item.label"
              class="status-widget"
              type="button"
              @click="openStatusWidget(item.statusFilter)"
            >
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </button>
          </section>

          <section v-if="showHomeAlerts && data.lowStockParts.length" class="home-alerts-panel">
            <div class="home-alerts-heading">
              <AlertTriangle :size="22" />
              <strong>Atenção necessária</strong>
            </div>
            <div class="home-alert-list">
              <button
                v-for="part in data.lowStockParts.slice(0, 4)"
                :key="part.id"
                type="button"
                @click="selectTab('parts')"
              >
                <strong>{{ part.name }}</strong>
                <span>{{ part.stockQuantity }} un. disponíveis · mínimo {{ part.minimumStock }}</span>
              </button>
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
              <h2>Status dos veículos</h2>
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
              <span>{{ data.lowStockParts.length }} avisos de compra</span>
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
          <section v-if="auth.role !== 'CUSTOMER'" class="order-flow-stats">
            <article v-for="item in orderFlowStats" :key="item.label">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </article>
          </section>

          <section v-if="auth.role !== 'CUSTOMER'" class="section-block">
            <div class="section-heading">
              <h2>Nova ordem de serviço</h2>
              <span>{{ orderSteps[forms.orderWizard.step] }}</span>
            </div>

            <div class="order-stepper" aria-label="Etapas da ordem">
              <button
                v-for="(step, index) in orderSteps"
                :key="step"
                type="button"
                :class="{ active: forms.orderWizard.step === index, done: forms.orderWizard.step > index }"
                @click="forms.orderWizard.step = index"
              >
                <span>{{ index + 1 }}</span>
                {{ step }}
              </button>
            </div>

            <div v-if="forms.orderWizard.step === 0" class="scenario-grid">
              <button
                v-for="scenario in orderScenarios"
                :key="scenario.id"
                type="button"
                :class="{ active: forms.orderWizard.scenario === scenario.id }"
                @click="selectOrderScenario(scenario.id)"
              >
                <strong>{{ scenario.label }}</strong>
                <span>{{ scenario.text }}</span>
              </button>
            </div>

            <div v-if="forms.orderWizard.step === 1" class="wizard-panel">
              <template v-if="isNewCustomerScenario">
                <div class="form-grid">
                  <input v-model="forms.orderWizard.customer.name" placeholder="Nome do cliente" required />
                  <input v-model="forms.orderWizard.customer.document" placeholder="CPF/CNPJ somente números" required />
                  <input v-model="forms.orderWizard.customer.phone" placeholder="Telefone" required />
                  <input v-model="forms.orderWizard.customer.email" type="email" placeholder="E-mail" required />
                  <input v-model="forms.orderWizard.customer.address.street" placeholder="Rua" required />
                  <input v-model="forms.orderWizard.customer.address.number" placeholder="Número" required />
                  <input v-model="forms.orderWizard.customer.address.neighborhood" placeholder="Bairro" required />
                  <input v-model="forms.orderWizard.customer.address.city" placeholder="Cidade" required />
                  <input v-model="forms.orderWizard.customer.address.state" maxlength="2" placeholder="UF" required />
                  <input v-model="forms.orderWizard.customer.address.zipCode" placeholder="CEP" required />
                  <input v-model="forms.orderWizard.customer.address.complement" placeholder="Complemento" />
                </div>
              </template>
              <template v-else>
                <select v-model="forms.orderWizard.customerId" required>
                  <option value="">Selecione o cliente</option>
                  <option v-for="customer in data.customers" :key="customer.id" :value="customer.id">
                    {{ customer.name }} - {{ customer.document }}
                  </option>
                </select>
                <article v-if="selectedOrderCustomer" class="selected-record">
                  <strong>{{ selectedOrderCustomer.name }}</strong>
                  <span>{{ selectedOrderCustomer.email }} · {{ selectedOrderCustomer.phone }}</span>
                  <small>{{ selectedOrderCustomer.document }}</small>
                </article>
              </template>
            </div>

            <div v-if="forms.orderWizard.step === 2" class="wizard-panel">
              <template v-if="needsNewVehicle">
                <article v-if="selectedOrderCustomer" class="selected-record">
                  <strong>{{ selectedOrderCustomer.name }}</strong>
                  <span>Cliente confirmado para cadastro do veículo.</span>
                </article>
                <div class="form-grid compact">
                  <input v-model="forms.orderWizard.vehicle.plate" placeholder="Placa ABC1D23" required />
                  <input v-model="forms.orderWizard.vehicle.brand" placeholder="Marca" required />
                  <input v-model="forms.orderWizard.vehicle.model" placeholder="Modelo" required />
                  <input v-model.number="forms.orderWizard.vehicle.year" type="number" placeholder="Ano" required />
                  <input v-model.number="forms.orderWizard.vehicle.mileage" type="number" placeholder="Km" required />
                </div>
              </template>
              <template v-else>
                <select v-model="forms.orderWizard.vehicleId" required>
                  <option value="">Selecione o veículo</option>
                  <option v-for="vehicle in orderCustomerVehicles" :key="vehicle.id" :value="vehicle.id">
                    {{ vehicle.plate }} - {{ vehicle.brand }} {{ vehicle.model }}
                  </option>
                </select>
                <article v-if="selectedOrderVehicle" class="selected-record">
                  <strong>{{ selectedOrderVehicle.plate }}</strong>
                  <span>{{ selectedOrderVehicle.brand }} {{ selectedOrderVehicle.model }}</span>
                  <small>{{ selectedOrderVehicle.mileage }} km</small>
                </article>
                <p v-if="forms.orderWizard.customerId && !orderCustomerVehicles.length" class="empty-state">
                  Este cliente ainda não possui veículos cadastrados. Volte e escolha o cenário de veículo novo.
                </p>
              </template>
            </div>

            <div v-if="forms.orderWizard.step === 3" class="wizard-panel">
              <textarea
                v-model="forms.orderWizard.defects"
                placeholder="Defeitos percebidos inicialmente, relatos do cliente, sintomas e observações da recepção"
                required
              ></textarea>
              <label class="check-row">
                <input v-model="forms.orderWizard.contactRequested" type="checkbox" />
                <span>Cliente quer ser contatado antes do envio do orçamento</span>
              </label>
            </div>

            <div v-if="forms.orderWizard.step === 4" class="wizard-panel">
              <div class="form-grid compact">
                <select v-model="forms.orderWizard.serviceId">
                  <option value="">Serviço inicial previsto</option>
                  <option v-for="service in data.services" :key="service.id" :value="service.id">
                    {{ service.name }} - R$ {{ money(service.basePrice) }}
                  </option>
                </select>
                <input v-model.number="forms.orderWizard.serviceQuantity" type="number" min="1" placeholder="Qtd. serviço" />
                <select v-model="forms.orderWizard.partId">
                  <option value="">Peça inicial prevista</option>
                  <option v-for="part in data.parts" :key="part.id" :value="part.id">
                    {{ part.name }} - R$ {{ money(part.unitPrice) }}
                  </option>
                </select>
                <input v-model.number="forms.orderWizard.partQuantity" type="number" min="1" placeholder="Qtd. peça" />
                <textarea v-model="forms.orderWizard.initialValueNotes" placeholder="Observações de valores iniciais, se houver"></textarea>
              </div>
              <article class="selected-record">
                <strong>R$ {{ money(estimatedOrderTotal) }}</strong>
                <span>Estimativa inicial baseada nos itens selecionados.</span>
              </article>
            </div>

            <div v-if="forms.orderWizard.step === 5" class="wizard-panel">
              <article class="order-review">
                <strong>Status ao salvar</strong>
                <span>Sem orçamento agora: {{ statusLabels.RECEIVED }}.</span>
                <span>Com orçamento agora: {{ statusLabels.WAITING_APPROVAL }}.</span>
              </article>
              <div class="wizard-actions">
                <button class="secondary-button" type="button" :disabled="saving" @click="createOrderFromWizard(false)">
                  Salvar como orçamento pendente
                </button>
                <button class="primary-button" type="button" :disabled="saving" @click="createOrderFromWizard(true)">
                  <Plus :size="18" />
                  <span>Salvar e criar orçamento agora</span>
                </button>
              </div>
            </div>

            <div class="wizard-actions">
              <button class="secondary-button" type="button" :disabled="forms.orderWizard.step === 0 || saving" @click="previousOrderStep">
                Voltar
              </button>
              <button class="secondary-button" type="button" :disabled="forms.orderWizard.step === orderSteps.length - 1 || saving" @click="nextOrderStep">
                Avançar
              </button>
            </div>
          </section>

          <section v-if="auth.role !== 'CUSTOMER'" class="section-block">
            <div class="section-heading">
              <h2>Orçamento e execução</h2>
              <span>Criar agora ou depois</span>
            </div>
            <form class="form-grid" @submit.prevent="updateOrderStatus">
              <select v-model="forms.orderAction.serviceOrderId" required>
                <option value="">Selecione a ordem</option>
                <option v-for="order in data.serviceOrders" :key="order.id" :value="order.id">
                  {{ statusLabels[order.status] || order.status }} - {{ order.diagnosticNotes }}
                </option>
              </select>
              <select v-model="forms.orderAction.status">
                <option v-for="status in statuses" :key="status" :value="status">
                  {{ statusLabels[status] || status }}
                </option>
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
              <span>Status atual dos veículos</span>
            </div>
            <div v-if="auth.role !== 'CUSTOMER'" class="filters">
              <select v-model="pagination.serviceOrders.status">
                <option value="">Todos os status</option>
                <option v-for="status in statuses" :key="status" :value="status">
                  {{ statusLabels[status] || status }}
                </option>
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
                <span class="badge">{{ statusLabels[order.status] || order.status }}</span>
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
