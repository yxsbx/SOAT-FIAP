<script setup>
import {computed, onMounted, reactive, ref} from 'vue';
import {useRouter} from 'vue-router';
import {
  AlertTriangle,
  BadgePercent,
  BarChart3,
  Car,
  CheckCircle2,
  ChevronLeft,
  ChevronRight,
  ClipboardList,
  DollarSign,
  Gauge,
  KeyRound,
  LogOut,
  Menu,
  Package,
  Plus,
  Search,
  ShieldCheck,
  ShoppingCart,
  TrendingUp,
  UserCog,
  UserPlus,
  Users,
  Wrench,
  X,
} from 'lucide-vue-next';
import {useAuthStore} from '@/stores/auth';
import {resources} from '@/services/api';
import {calculatePlatformFee} from '@/utils/platformFee';

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
const selectedRecord = ref(null);
const selectedRecordType = ref('');
const modalDraft = reactive({
  customer: {},
  partner: {},
});
const currentUser = ref(null);
const customerPartnerSearch = ref('');
const customerPartSearch = ref('');
const selectedCustomerPart = ref(null);
const API_MAX_PAGE_SIZE = 100;

const defaultHomeWidgetIds = [
  'orders-progress',
  'services-catalog',
  'active-customers',
  'vehicles-in-service',
  'pending-budgets',
  'waiting-contact',
  'ready-pickup',
];

const defaultStoreHomeWidgetIds = [
  'store-month-sales',
  'store-pending-quotes',
  'store-sent-carts',
  'store-parts-stock',
  'store-low-stock',
  'store-waiting-contact',
  'store-active-orders',
];

const defaultMasterHomeWidgetIds = [
  'master-customers',
  'master-workshops',
  'master-stores',
  'master-workshop-gross',
  'master-store-gross',
  'master-platform-fee',
  'master-workshop-fee',
  'master-store-fee',
];

const permissionDefinitions = [
  {id: 'VIEW_BILLING', label: 'Ver faturamento'},
  {id: 'CREATE_ORDER', label: 'Criar ordem'},
  {id: 'EDIT_ORDER', label: 'Editar ordem'},
  {id: 'MANAGE_STOCK', label: 'Gerenciar estoque'},
  {id: 'CREATE_BUDGET', label: 'Criar orçamento'},
  {id: 'EDIT_EMPLOYEES', label: 'Editar funcionários'},
  {id: 'VIEW_STATS', label: 'Ver estatísticas'},
];

const employeeSubRoleLabels = {
  MECHANIC: 'Mecânico',
  ATTENDANT: 'Atendente',
  UNSPECIFIED: 'Funcionário sem especificação',
  '': 'Funcionário sem especificação',
};

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
  users: [],
  customers: [],
  vehicles: [],
  services: [],
  parts: [],
  lowStockParts: [],
  serviceOrders: [],
  demoLeads: [],
  averageExecutionTime: null,
});

const storeQuotes = ref([]);
const storeQuotesInitialized = ref(false);

const pagination = reactive({
  users: {page: 0, size: 10, active: '', role: '', profileType: '', search: '', sortBy: 'fullName', sortDir: 'asc'},
  customers: {page: 0, size: 10, active: '', search: '', sortBy: 'name', sortDir: 'asc'},
  vehicles: {page: 0, size: 10, active: '', search: '', sortBy: 'plate', sortDir: 'asc'},
  parts: {page: 0, size: 10, active: '', lowStock: '', search: '', sortBy: 'name', sortDir: 'asc'},
  serviceOrders: {page: 0, size: 10, status: '', search: '', sortBy: 'createdAt', sortDir: 'desc'},
  services: {page: 0, size: 10, active: '', search: '', sortBy: 'name', sortDir: 'asc'},
  masterCustomers: {page: 0, size: 10, search: '', sortBy: 'name', sortDir: 'asc'},
  masterWorkshops: {page: 0, size: 10, search: '', sortBy: 'name', sortDir: 'asc'},
});

const forms = reactive({
  customer: {
    id: '',
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
    active: true,
  },
  vehicle: {
    id: '',
    customerId: '',
    plate: '',
    brand: '',
    model: '',
    year: new Date().getFullYear(),
    mileage: 0,
    active: true,
  },
  part: {
    id: '',
    name: '',
    description: '',
    sku: '',
    category: '',
    subcategory: '',
    brand: '',
    costPrice: 0,
    unitPrice: 0,
    stockQuantity: 0,
    minimumStock: 1,
    active: true,
    reservationDays: 3,
  },
  stockMovement: {
    partId: '',
    type: 'ENTRY',
    quantity: 1,
    unitCost: 0,
    unitPrice: 0,
    reason: '',
  },
  service: {
    id: '',
    name: '',
    description: '',
    basePrice: 0,
    estimatedTimeInMinutes: 60,
    active: true,
  },
  user: {
    id: '',
    fullName: '',
    username: '',
    password: '123456',
    role: 'EMPLOYEE',
    profileType: 'WORKSHOP_EMPLOYEE',
    companyName: '',
    companyType: '',
    employeeSubRole: 'UNSPECIFIED',
    permissions: ['CREATE_ORDER', 'EDIT_ORDER', 'CREATE_BUDGET'],
    customerId: '',
    active: true,
  },
  account: {
    fullName: '',
  },
  password: {
    currentPassword: '',
    newPassword: '',
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
  storeQuote: {
    id: '',
    customerName: '',
    customerContact: '',
    status: 'DRAFT',
    contactRequested: false,
    partId: '',
    quantity: 1,
    quotedPrice: 0,
    items: [],
  },
  customerQuote: {
    storeName: '',
    storeContact: '',
    workshopName: '',
    vehicleId: '',
    problemDescription: '',
    items: [],
  },
});

const demoProfile = computed(() => {
  const profiles = {
    'master@autocarehub.com': {
      label: 'Admin Master',
      tabs: ['overview', 'master-customers', 'master-workshops', 'master-stores', 'master-leads', 'master-admins', 'users'],
    },
    'oficina.admin@autocarehub.com': {
      label: 'Admin de oficina',
      tabs: ['overview', 'billing', 'employees', 'users', 'orders', 'customers', 'vehicles', 'parts', 'services'],
    },
    'loja.admin@autocarehub.com': {
      label: 'Admin de loja de peças',
      tabs: ['overview', 'store-billing', 'store-employees', 'store-quotes', 'parts', 'users'],
    },
    'oficina.funcionario@autocarehub.com': {
      label: 'Funcionário de oficina',
      tabs: ['overview', 'users', 'orders', 'vehicles', 'parts', 'services'],
    },
    'loja.funcionario@autocarehub.com': {
      label: 'Funcionário de loja de peças',
      tabs: ['overview', 'store-quotes', 'parts', 'users'],
    },
    'cliente@autocarehub.com': {
      label: 'Cliente',
      tabs: ['overview', 'customer-partners', 'customer-parts', 'customer-cart', 'orders', 'users'],
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

const isWorkshopAdmin = computed(() =>
    auth.role === 'ADMIN' && currentUser.value?.profileType === 'WORKSHOP_ADMIN',
);

const isMasterAdmin = computed(() =>
    auth.role === 'ADMIN' && currentUser.value?.profileType === 'MASTER_ADMIN',
);

const isPartsStoreAdmin = computed(() =>
    auth.role === 'ADMIN' && currentUser.value?.profileType === 'PARTS_STORE_ADMIN',
);

const isPartsStoreProfile = computed(() =>
    ['PARTS_STORE_ADMIN', 'PARTS_STORE_EMPLOYEE'].includes(currentUser.value?.profileType),
);

const isCustomerProfile = computed(() =>
    auth.role === 'CUSTOMER' || currentUser.value?.profileType === 'CUSTOMER_OWNER',
);

const userPermissions = computed(() => currentUser.value?.permissions || []);

function can(permission) {
  return isWorkshopAdmin.value || userPermissions.value.includes(permission);
}

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
      id: 'billing',
      label: 'Faturamento',
      description: 'Receita, taxa e tiers',
      icon: DollarSign,
      roles: ['ADMIN'],
      workshopAdminOnly: true,
    },
    {
      id: 'master-customers',
      label: 'Clientes',
      description: 'Gastos, veículos e frequência',
      icon: Users,
      roles: ['ADMIN'],
      masterOnly: true,
    },
    {
      id: 'master-workshops',
      label: 'Oficinas',
      description: 'Parceiros e faturamento',
      icon: Wrench,
      roles: ['ADMIN'],
      masterOnly: true,
    },
    {
      id: 'master-stores',
      label: 'Lojas',
      description: 'Peças, vendas e receita',
      icon: Package,
      roles: ['ADMIN'],
      masterOnly: true,
    },
    {
      id: 'master-leads',
      label: 'Interessados',
      description: 'Leads e parceiros potenciais',
      icon: TrendingUp,
      roles: ['ADMIN'],
      masterOnly: true,
    },
    {
      id: 'master-admins',
      label: 'Admins parceiros',
      description: 'Cadastrar admins de oficina e loja',
      icon: UserPlus,
      roles: ['ADMIN'],
      masterOnly: true,
    },
    {
      id: 'employees',
      label: 'Funcionários',
      description: 'Equipe, permissões e metas',
      icon: UserCog,
      roles: ['ADMIN'],
      workshopAdminOnly: true,
    },
    {
      id: 'store-billing',
      label: 'Faturamento',
      description: 'Receita, taxa e vendas da loja',
      icon: DollarSign,
      roles: ['ADMIN'],
      partsStoreAdminOnly: true,
    },
    {
      id: 'store-employees',
      label: 'Funcionários',
      description: 'Equipe comercial e permissões',
      icon: UserCog,
      roles: ['ADMIN'],
      partsStoreAdminOnly: true,
    },
    {
      id: 'store-quotes',
      label: 'Carrinhos',
      description: 'Orçamentos de peças e vendas',
      icon: ShoppingCart,
      roles: ['ADMIN', 'EMPLOYEE'],
      partsStoreOnly: true,
    },
    {
      id: 'users',
      label: 'Conta',
      description: 'Dados do usuário e permissões',
      icon: UserCog,
      roles: ['ADMIN', 'EMPLOYEE', 'CUSTOMER'],
    },
    {
      id: 'customer-partners',
      label: 'Oficinas e lojas',
      description: 'Parceiros para contato e orçamento',
      icon: Wrench,
      roles: ['CUSTOMER'],
      customerOnly: true,
    },
    {
      id: 'customer-parts',
      label: 'Buscar peças',
      description: 'Peças, preços e disponibilidade',
      icon: Package,
      roles: ['CUSTOMER'],
      customerOnly: true,
    },
    {
      id: 'customer-cart',
      label: 'Solicitações',
      description: 'Carrinho e pedidos de orçamento',
      icon: ShoppingCart,
      roles: ['CUSTOMER'],
      customerOnly: true,
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
    const allowedByAdminType = !tab.workshopAdminOnly || isWorkshopAdmin.value;
    const allowedByStoreAdminType = !tab.partsStoreAdminOnly || isPartsStoreAdmin.value;
    const allowedByStoreType = !tab.partsStoreOnly || isPartsStoreProfile.value;
    const allowedByMasterType = !tab.masterOnly || isMasterAdmin.value;
    const allowedByCustomerType = !tab.customerOnly || isCustomerProfile.value;
    return allowedByRole && allowedByProfile && allowedByAdminType && allowedByStoreAdminType && allowedByStoreType && allowedByMasterType && allowedByCustomerType;
  });
});

const availableTabIds = computed(() => new Set(availableTabs.value.map((tab) => tab.id)));

const userInitials = computed(() => {
  const fallback = currentUser.value?.fullName || auth.user?.username || 'Usuario AutoCare';
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

const criticalParts = computed(() =>
  data.parts.filter((part) => (part.availableQuantity ?? part.stockQuantity) <= part.minimumStock),
);

const reservedParts = computed(() =>
  data.parts.filter((part) => Number(part.reservedQuantity || 0) > 0),
);

const stockStatusLabels = {
  AVAILABLE: 'Disponível',
  RESERVED: 'Com reserva',
  LOW_STOCK: 'Baixo estoque',
  OUT_OF_STOCK: 'Sem estoque',
  INACTIVE: 'Inativa',
};

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
    (isMasterAdmin.value
      ? masterHomeWidgetDefinitions.value
      : isPartsStoreProfile.value
        ? storeHomeWidgetDefinitions.value
        : homeWidgetDefinitions.value).filter(
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

const billingSummary = computed(() => {
  const orders = data.serviceOrders;
  const gross = orders.reduce((total, order) => total + Number(order.totalAmount || 0), 0);
  const sentBudgets = orders.filter((order) => order.budgetGeneratedAt || ['WAITING_APPROVAL', 'IN_PROGRESS', 'FINISHED', 'DELIVERED'].includes(order.status)).length;
  const approvedBudgets = orders.filter((order) => order.approvedAt || ['IN_PROGRESS', 'FINISHED', 'DELIVERED'].includes(order.status)).length;
  const completed = orders.filter((order) => ['FINISHED', 'DELIVERED'].includes(order.status)).length;
  const ticket = approvedBudgets ? gross / approvedBudgets : 0;
  const fee = calculatePlatformFee(gross);
  return {
    gross,
    feeRate: fee.feeRate,
    feeRateLabel: fee.feeRateLabel,
    feeAmount: fee.feeAmount,
    net: fee.net,
    sentBudgets,
    approvedBudgets,
    completed,
    ticket,
    nextTierGap: fee.nextTierGap,
    nextTierLabel: fee.nextTierLabel,
  };
});

const monthlyRevenue = computed(() => {
  const months = new Map();
  data.serviceOrders.forEach((order) => {
    const date = order.createdAt ? new Date(order.createdAt) : new Date();
    const key = date.toLocaleDateString('pt-BR', {month: 'short', year: '2-digit'});
    months.set(key, (months.get(key) || 0) + Number(order.totalAmount || 0));
  });
  return [...months.entries()].slice(-6).map(([month, value]) => ({month, value}));
});

const storeQuoteStatusLabels = {
  DRAFT: 'Em montagem',
  SENT: 'Enviado ao cliente',
  APPROVED: 'Aprovado',
  REFUSED: 'Recusado',
  EXPIRED: 'Expirado',
};

const storeSales = computed(() =>
    storeQuotes.value.filter((quote) => quote.status === 'APPROVED'),
);

const storePendingQuotes = computed(() =>
    storeQuotes.value.filter((quote) => ['DRAFT', 'SENT'].includes(quote.status)),
);

const storeActiveOrders = computed(() =>
    storeQuotes.value.filter((quote) => ['DRAFT', 'SENT'].includes(quote.status)),
);

const storeWaitingContact = computed(() =>
    storeQuotes.value.filter((quote) => quote.contactRequested && quote.status !== 'APPROVED'),
);

const storeRevenueGross = computed(() =>
    storeSales.value.reduce((total, quote) => total + storeQuoteTotal(quote), 0),
);

const storeBillingSummary = computed(() => {
  const gross = storeRevenueGross.value;
  const sentQuotes = storeQuotes.value.filter((quote) => ['SENT', 'APPROVED', 'REFUSED', 'EXPIRED'].includes(quote.status)).length;
  const approvedQuotes = storeSales.value.length;
  const ticket = approvedQuotes ? gross / approvedQuotes : 0;
  const fee = calculatePlatformFee(gross);
  return {
    gross,
    feeRate: fee.feeRate,
    feeRateLabel: fee.feeRateLabel,
    feeAmount: fee.feeAmount,
    net: fee.net,
    nextTierGap: fee.nextTierGap,
    nextTierLabel: fee.nextTierLabel,
    sentQuotes,
    approvedQuotes,
    conversion: sentQuotes ? Math.round((approvedQuotes / sentQuotes) * 100) : 0,
    ticket,
  };
});

const storeMonthlyRevenue = computed(() => {
  const months = new Map();
  storeSales.value.forEach((quote) => {
    const date = quote.updatedAt ? new Date(quote.updatedAt) : new Date();
    const key = date.toLocaleDateString('pt-BR', {month: 'short', year: '2-digit'});
    months.set(key, (months.get(key) || 0) + storeQuoteTotal(quote));
  });
  return [...months.entries()].slice(-6).map(([month, value]) => ({month, value}));
});

const storeTopProducts = computed(() => {
  const products = new Map();
  storeSales.value.forEach((quote) => {
    quote.items.forEach((item) => {
      const current = products.get(item.partId) || {name: item.name, quantity: 0, total: 0};
      current.quantity += Number(item.quantity || 0);
      current.total += Number(item.quantity || 0) * Number(item.quotedPrice || 0);
      products.set(item.partId, current);
    });
  });
  return [...products.values()].sort((a, b) => b.quantity - a.quantity).slice(0, 5);
});

const storeFrequentCustomers = computed(() => {
  const customers = new Map();
  storeSales.value.forEach((quote) => {
    const current = customers.get(quote.customerName) || {name: quote.customerName, count: 0, total: 0};
    current.count += 1;
    current.total += storeQuoteTotal(quote);
    customers.set(quote.customerName, current);
  });
  return [...customers.values()].sort((a, b) => b.count - a.count).slice(0, 5);
});

const storeEmployees = computed(() =>
    data.users.filter((user) =>
        ['PARTS_STORE_EMPLOYEE', 'PARTS_STORE_ADMIN'].includes(user.profileType),
    ),
);

const masterCustomers = computed(() =>
    data.customers.map((customer) => {
      const vehicles = data.vehicles.filter((vehicle) => vehicle.customerId === customer.id);
      const orders = data.serviceOrders.filter((order) => order.customerId === customer.id);
      const spent = orders.reduce((total, order) => total + Number(order.totalAmount || 0), 0);
      const partners = new Set(
          [
            ...orders.map(() => 'Oficina Central AutoCare'),
            ...storeQuotes.value
                .filter((quote) => normalize(quote.customerName).includes(normalize(customer.name)))
                .map(() => 'Loja Peças Prime'),
          ],
      );
      return {
        ...customer,
        vehiclesCount: vehicles.length,
        spent,
        frequency: orders.length,
        partners: [...partners],
      };
    }),
);

const workshopPartners = computed(() => {
  const admins = data.users.filter((user) => user.profileType === 'WORKSHOP_ADMIN');
  return admins.map((admin) => {
    const gross = data.serviceOrders.reduce((total, order) => total + Number(order.totalAmount || 0), 0);
    const fee = calculatePlatformFee(gross);
    return {
      id: admin.id,
      name: admin.companyName || admin.fullName,
      adminName: admin.fullName,
      gross,
      feeRate: fee.feeRate,
      feeRateLabel: fee.feeRateLabel,
      feeAmount: fee.feeAmount,
      net: fee.net,
      nextTierGap: fee.nextTierGap,
      nextTierLabel: fee.nextTierLabel,
      customersServed: new Set(data.serviceOrders.map((order) => order.customerId).filter(Boolean)).size,
      vehiclesServed: new Set(data.serviceOrders.map((order) => order.vehicleId).filter(Boolean)).size,
      status: admin.active ? 'Ativa' : 'Inativa',
    };
  });
});

const storePartners = computed(() => {
  const admins = data.users.filter((user) => user.profileType === 'PARTS_STORE_ADMIN');
  return admins.map((admin) => {
    const gross = storeRevenueGross.value;
    const fee = calculatePlatformFee(gross);
    return {
      id: admin.id,
      name: admin.companyName || admin.fullName,
      adminName: admin.fullName,
      gross,
      feeRate: fee.feeRate,
      feeRateLabel: fee.feeRateLabel,
      feeAmount: fee.feeAmount,
      net: fee.net,
      nextTierGap: fee.nextTierGap,
      nextTierLabel: fee.nextTierLabel,
      salesCount: storeSales.value.length,
      topProducts: storeTopProducts.value.map((product) => product.name).join(', ') || 'Sem vendas',
      status: admin.active ? 'Ativa' : 'Inativa',
    };
  });
});

const masterSummary = computed(() => {
  const workshopGross = workshopPartners.value.reduce((total, partner) => total + partner.gross, 0);
  const storeGross = storePartners.value.reduce((total, partner) => total + partner.gross, 0);
  const workshopFee = workshopPartners.value.reduce((total, partner) => total + partner.feeAmount, 0);
  const storeFee = storePartners.value.reduce((total, partner) => total + partner.feeAmount, 0);
  return {
    customers: data.customers.length,
    workshops: workshopPartners.value.length,
    stores: storePartners.value.length,
    workshopGross,
    storeGross,
    workshopFee,
    storeFee,
    platformFee: workshopFee + storeFee,
  };
});

const masterHomeWidgetDefinitions = computed(() => [
  {
    id: 'master-customers',
    category: 'platform',
    tone: 'blue',
    label: 'Clientes cadastrados',
    value: masterSummary.value.customers,
    icon: Users,
    tabId: 'master-customers',
    roles: ['ADMIN'],
  },
  {
    id: 'master-workshops',
    category: 'platform',
    tone: 'cyan',
    label: 'Oficinas parceiras',
    value: masterSummary.value.workshops,
    icon: Wrench,
    tabId: 'master-workshops',
    roles: ['ADMIN'],
  },
  {
    id: 'master-stores',
    category: 'platform',
    tone: 'violet',
    label: 'Lojas de peças',
    value: masterSummary.value.stores,
    icon: Package,
    tabId: 'master-stores',
    roles: ['ADMIN'],
  },
  {
    id: 'master-workshop-gross',
    category: 'finance',
    tone: 'green',
    label: 'Faturamento bruto das oficinas',
    value: `R$ ${money(masterSummary.value.workshopGross)}`,
    icon: DollarSign,
    tabId: 'master-workshops',
    roles: ['ADMIN'],
  },
  {
    id: 'master-store-gross',
    category: 'finance',
    tone: 'amber',
    label: 'Faturamento bruto das lojas',
    value: `R$ ${money(masterSummary.value.storeGross)}`,
    icon: ShoppingCart,
    tabId: 'master-stores',
    roles: ['ADMIN'],
  },
  {
    id: 'master-platform-fee',
    category: 'finance',
    tone: 'rose',
    label: 'Receita total AutoCare Hub',
    value: `R$ ${money(masterSummary.value.platformFee)}`,
    icon: BadgePercent,
    tabId: 'master-leads',
    roles: ['ADMIN'],
  },
  {
    id: 'master-workshop-fee',
    category: 'finance',
    tone: 'indigo',
    label: 'Taxas recebidas de oficinas',
    value: `R$ ${money(masterSummary.value.workshopFee)}`,
    icon: BadgePercent,
    tabId: 'master-workshops',
    roles: ['ADMIN'],
  },
  {
    id: 'master-store-fee',
    category: 'finance',
    tone: 'teal',
    label: 'Taxas recebidas de lojas',
    value: `R$ ${money(masterSummary.value.storeFee)}`,
    icon: BadgePercent,
    tabId: 'master-stores',
    roles: ['ADMIN'],
  },
]);

const masterPlatformWidgets = computed(() =>
    homeWidgets.value.filter((widget) => widget.category === 'platform'),
);

const masterFinancialWidgets = computed(() =>
    homeWidgets.value.filter((widget) => widget.category === 'finance'),
);

const masterTopPlatformRevenuePartners = computed(() =>
    [...workshopPartners.value, ...storePartners.value]
        .sort((a, b) => b.feeAmount - a.feeAmount)
        .slice(0, 5),
);

const masterFrequentCustomers = computed(() =>
    [...masterCustomers.value].sort((a, b) => b.frequency - a.frequency).slice(0, 5),
);

const masterTopSpenders = computed(() =>
    [...masterCustomers.value].sort((a, b) => b.spent - a.spent).slice(0, 5),
);

const masterVehicleOwners = computed(() =>
    [...masterCustomers.value].sort((a, b) => b.vehiclesCount - a.vehiclesCount).slice(0, 5),
);

const masterPotentialPartners = computed(() =>
    data.demoLeads.map((lead) => ({
      ...lead,
      potential:
          lead.demoProfile === 'workshop'
              ? data.serviceOrders.length + 12
              : data.parts.length + storeQuotes.value.length,
    })).sort((a, b) => b.potential - a.potential),
);

const storeHomeWidgetDefinitions = computed(() => [
  {
    id: 'store-month-sales',
    label: 'Vendas do mês',
    value: `R$ ${money(storeRevenueGross.value)}`,
    icon: DollarSign,
    tabId: 'store-billing',
    roles: ['ADMIN'],
  },
  {
    id: 'store-pending-quotes',
    label: 'Orçamentos pendentes',
    value: storePendingQuotes.value.length,
    icon: ClipboardList,
    tabId: 'store-quotes',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
  {
    id: 'store-sent-carts',
    label: 'Carrinhos/orçamentos enviados',
    value: storeQuotes.value.filter((quote) => quote.status === 'SENT').length,
    icon: ShoppingCart,
    tabId: 'store-quotes',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
  {
    id: 'store-parts-stock',
    label: 'Peças em estoque',
    value: data.parts.length,
    icon: Package,
    tabId: 'parts',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
  {
    id: 'store-low-stock',
    label: 'Peças com estoque baixo',
    value: data.lowStockParts.length,
    icon: AlertTriangle,
    tabId: 'parts',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
  {
    id: 'store-waiting-contact',
    label: 'Clientes aguardando contato',
    value: storeWaitingContact.value.length,
    icon: Users,
    tabId: 'store-quotes',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
  {
    id: 'store-active-orders',
    label: 'Pedidos em andamento',
    value: storeActiveOrders.value.length,
    icon: TrendingUp,
    tabId: 'store-quotes',
    roles: ['ADMIN', 'EMPLOYEE'],
  },
]);

const workshopEmployees = computed(() =>
    data.users.filter((user) => user.role === 'EMPLOYEE' && user.profileType === 'WORKSHOP_EMPLOYEE'),
);

function storeEmployeeMetrics(user) {
  const employeeQuotes = storeQuotes.value.filter((quote) => quote.employeeId === user.id);
  const sent = employeeQuotes.filter((quote) => ['SENT', 'APPROVED', 'REFUSED', 'EXPIRED'].includes(quote.status)).length;
  const approved = employeeQuotes.filter((quote) => quote.status === 'APPROVED');
  const gross = approved.reduce((total, quote) => total + storeQuoteTotal(quote), 0);
  return [
    {label: 'Vendas', value: `R$ ${money(gross)}`},
    {label: 'Orçamentos enviados', value: sent},
    {label: 'Orçamentos aprovados', value: approved.length},
    {label: 'Taxa de conversão', value: `${sent ? Math.round((approved.length / sent) * 100) : 0}%`},
  ];
}

function employeeMetrics(user) {
  const index = Math.max(1, workshopEmployees.value.findIndex((employee) => employee.id === user.id) + 1);
  const approved = data.serviceOrders.filter((order) => order.approvedAt || ['IN_PROGRESS', 'FINISHED', 'DELIVERED'].includes(order.status)).length;
  const sent = data.serviceOrders.filter((order) => order.budgetGeneratedAt || order.status === 'WAITING_APPROVAL').length || approved;
  const completed = data.serviceOrders.filter((order) => ['FINISHED', 'DELIVERED'].includes(order.status)).length;
  if (user.employeeSubRole === 'ATTENDANT') {
    return [
      {label: 'Clientes contatados', value: ordersWaitingContact.value + index * 3},
      {label: 'Orçamentos enviados', value: sent},
      {label: 'Orçamentos aprovados', value: approved},
      {label: 'Taxa de conversão', value: `${sent ? Math.round((approved / sent) * 100) : 0}%`},
    ];
  }
  if (user.employeeSubRole === 'MECHANIC') {
    return [
      {label: 'Veículos atendidos', value: completed + index},
      {label: 'Serviços concluídos', value: completed},
      {label: 'Tempo médio', value: formatDuration(data.averageExecutionTime?.averageExecutionTimeInMinutes || 0)},
      {label: 'Meta concluídos', value: `${Math.min(100, Math.round(((completed + index) / 12) * 100))}%`},
    ];
  }
  return [
    {label: 'Ordens em aberto', value: data.serviceOrders.filter((order) => !['FINISHED', 'DELIVERED'].includes(order.status)).length},
    {label: 'Orçamentos enviados', value: sent},
    {label: 'Serviços concluídos', value: completed},
    {label: 'Permissões ativas', value: user.permissions?.length || 0},
  ];
}

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
    ...storeQuotes.value.map((quote) => ({
      type: 'Carrinho',
      label: quote.customerName,
      detail: `${quote.id} - ${storeQuoteStatusLabels[quote.status] || quote.status}`,
      tabId: 'store-quotes',
      icon: ShoppingCart,
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

const customerVehicles = computed(() => vehiclesWithCurrentStatus.value);

const customerOrders = computed(() => data.serviceOrders);

const customerBudgetAlerts = computed(() =>
    customerOrders.value.filter((order) => order.status === 'WAITING_APPROVAL'),
);

const customerFinishedAlerts = computed(() =>
    customerOrders.value.filter((order) => order.status === 'DELIVERED'),
);

const customerReadyAlerts = computed(() =>
    customerOrders.value.filter((order) => order.status === 'FINISHED'),
);

const customerRecentHistory = computed(() =>
    [...customerOrders.value]
        .sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))
        .slice(0, 5),
);

const workshopDirectory = computed(() =>
    data.users
        .filter((user) => user.profileType === 'WORKSHOP_ADMIN')
        .map((user) => ({
          ...user,
          name: user.companyName || user.fullName,
          location: 'São Paulo - SP',
          specialty: 'Diagnóstico, revisão, manutenção preventiva e orçamento de veículos',
        })),
);

const storeDirectory = computed(() =>
    data.users
        .filter((user) => user.profileType === 'PARTS_STORE_ADMIN')
        .map((user) => ({
          ...user,
          name: user.companyName || user.fullName,
          location: 'São Paulo - SP',
          specialty: 'Peças, filtros, freios, suspensão, óleo e acessórios',
        })),
);

const filteredWorkshopDirectory = computed(() =>
    workshopDirectory.value.filter((partner) =>
        smartMatch(
            customerPartnerSearch.value,
            [partner.name, partner.location, partner.specialty, partner.fullName],
        ),
    ),
);

const filteredStoreDirectory = computed(() =>
    storeDirectory.value.filter((partner) =>
        smartMatch(
            customerPartnerSearch.value,
            [partner.name, partner.location, partner.specialty, partner.fullName],
        ),
    ),
);

const uniqueCustomerParts = computed(() => {
  const parts = new Map();
  data.parts.forEach((part) => {
    const key = normalize(`${part.name}-${part.brand}-${part.category}`);
    if (!parts.has(key)) {
      parts.set(key, part);
    }
  });
  return [...parts.values()];
});

const filteredCustomerParts = computed(() => {
  const vehicleTerms = customerVehicles.value
      .map((vehicle) => `${vehicle.brand} ${vehicle.model} ${vehicle.plate}`)
      .join(' ');
  return uniqueCustomerParts.value.filter((part) =>
      smartMatch(
          customerPartSearch.value,
          [part.name, part.brand, part.category, part.subcategory, part.sku, vehicleTerms],
      ),
  );
});

const selectedPartStores = computed(() => {
  if (!selectedCustomerPart.value) {
    return [];
  }
  return storeDirectory.value.map((store, index) => ({
    ...store,
    partId: selectedCustomerPart.value.id,
    partName: selectedCustomerPart.value.name,
    price: Number(selectedCustomerPart.value.unitPrice || 0) * (1 + index * 0.04),
    availableQuantity: Math.max(0, Number(selectedCustomerPart.value.availableQuantity ?? selectedCustomerPart.value.stockQuantity) - index),
  }));
});

const listSources = computed(() => ({
  users: data.users,
  customers: data.customers,
  vehicles: vehiclesWithCurrentStatus.value,
  parts: data.parts,
  serviceOrders: data.serviceOrders,
  services: data.services,
  masterCustomers: masterCustomers.value,
  masterWorkshops: workshopPartners.value,
}));

const listSearchFields = {
  users: ['fullName', 'username', 'role', 'profileType'],
  customers: ['name', 'email', 'phone', 'document'],
  vehicles: ['plate', 'brand', 'model', 'currentStatus'],
  parts: ['name', 'sku', 'category', 'brand', 'stockStatus'],
  serviceOrders: ['status', 'diagnosticNotes', 'id'],
  services: ['name', 'description'],
  masterCustomers: ['name', 'email', 'phone', 'document', 'partners'],
  masterWorkshops: ['name', 'adminName', 'status'],
};

function listRows(resource) {
  const config = pagination[resource];
  const query = normalize(config.search || '');
  const fields = listSearchFields[resource] || [];
  const rows = [...(listSources.value[resource] || [])]
      .filter((item) => {
        if (!query) {
          return true;
        }
        return fields.some((field) => normalize(item[field]).includes(query));
      })
      .sort((a, b) => {
        const left = a[config.sortBy];
        const right = b[config.sortBy];
        const direction = config.sortDir === 'desc' ? -1 : 1;
        if (typeof left === 'number' || typeof right === 'number') {
          return (Number(left || 0) - Number(right || 0)) * direction;
        }
        return String(left || '').localeCompare(String(right || ''), 'pt-BR') * direction;
      });
  const start = config.page * config.size;
  return rows.slice(start, start + config.size);
}

function listTotal(resource) {
  const config = pagination[resource];
  const query = normalize(config.search || '');
  const fields = listSearchFields[resource] || [];
  return (listSources.value[resource] || []).filter((item) => {
    if (!query) {
      return true;
    }
    return fields.some((field) => normalize(item[field]).includes(query));
  }).length;
}

function listTotalPages(resource) {
  return Math.max(1, Math.ceil(listTotal(resource) / pagination[resource].size));
}

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

function smartMatch(query, values) {
  const tokens = normalize(query)
      .split(/\s+/)
      .map((token) => token.trim())
      .filter(Boolean);
  if (!tokens.length) {
    return true;
  }
  const haystack = normalize(values.filter(Boolean).join(' '));
  return tokens.every((token) => haystack.includes(token));
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

function homeDefaultWidgetIds() {
  const username = auth.user?.username || '';
  if (isMasterAdmin.value || username.includes('master@')) {
    return defaultMasterHomeWidgetIds;
  }
  return username.includes('loja.') || isPartsStoreProfile.value
      ? defaultStoreHomeWidgetIds
      : defaultHomeWidgetIds;
}

function readStoredJson(key, fallback) {
  try {
    return JSON.parse(localStorage.getItem(key)) || fallback;
  } catch {
    return fallback;
  }
}

async function loadHomePreferences() {
  const defaults = homeDefaultWidgetIds();
  const globalConfig = readStoredJson('autocare.home.workshop.global', {
    widgets: defaults,
    showAlertsOnHome: false,
  });
  const userConfig = readStoredJson(userHomeKey(), {
    widgets: globalConfig.widgets || defaults,
  });

  homePreferences.globalWidgets = [...(globalConfig.widgets || defaults)];
  homePreferences.showAlertsOnHome = Boolean(globalConfig.showAlertsOnHome);
  homePreferences.userWidgets = [...(userConfig.widgets ?? homePreferences.globalWidgets)];

  try {
    const preference = await resources.homePreferences();
    const validWidgetIds = new Set(availableHomeWidgetDefinitions.value.map((widget) => widget.id));
    const preferredWidgets = (preference.widgets || []).filter((widgetId) => validWidgetIds.has(widgetId));
    homePreferences.userWidgets = [...preferredWidgets];
    if (!homePreferences.userWidgets.length) {
      homePreferences.userWidgets = [...defaults];
    }
    homePreferences.showAlertsOnHome = Boolean(preference.showAlertsOnHome);
  } catch {
    saveUserHomePreferences();
  }
}

async function saveUserHomePreferences() {
  localStorage.setItem(userHomeKey(), JSON.stringify({widgets: homePreferences.userWidgets}));
  try {
    await resources.saveHomePreferences({
      widgets: homePreferences.userWidgets,
      showAlertsOnHome: homePreferences.showAlertsOnHome,
    });
    success.value = 'Preferências da home salvas.';
  } catch (err) {
    error.value = err.message || 'Preferências salvas apenas localmente.';
  }
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
    saveUserHomePreferences();
    return;
  }

  saveUserHomePreferences();
}

function toggleHomeAlerts() {
  homePreferences.showAlertsOnHome = !homePreferences.showAlertsOnHome;
  saveGlobalHomePreferences();
  saveUserHomePreferences();
}

async function loadDashboard() {
  loading.value = true;
  resetMessage();

  try {
    if (auth.role === 'CUSTOMER' && auth.customerId) {
      const [serviceOrders, vehicles, user, parts, partners] = await Promise.allSettled([
        resources.customerServiceOrders(auth.customerId),
        resources.customerVehicles(auth.customerId),
        resources.currentUser(),
        resources.parts({active: true, size: API_MAX_PAGE_SIZE}),
        resources.partners(),
      ]);

      data.serviceOrders =
          serviceOrders.status === 'fulfilled' ? listItems(serviceOrders.value) : [];
      data.vehicles = vehicles.status === 'fulfilled' ? listItems(vehicles.value) : [];
      currentUser.value = user.status === 'fulfilled' ? user.value : currentUser.value;
      data.parts = parts.status === 'fulfilled' ? listItems(parts.value) : [];
      data.users = partners.status === 'fulfilled' ? listItems(partners.value) : [];
      if (currentUser.value) {
        forms.account.fullName = currentUser.value.fullName;
      }

      const failed = [serviceOrders, vehicles, user, parts, partners].filter((request) => request.status === 'rejected');
      if (failed.length) {
        error.value = failed.map((request) => request.reason.message).join(' | ');
      }
      return;
    }

    const requests = await Promise.allSettled([
      resources.currentUser(),
      auth.role === 'ADMIN' ? resources.users({active: pagination.users.active, role: pagination.users.role, profileType: pagination.users.profileType, search: pagination.users.search}) : Promise.resolve(null),
      auth.role !== 'CUSTOMER' ? resources.customers({active: pagination.customers.active, size: API_MAX_PAGE_SIZE}) : Promise.resolve(null),
      resources.vehicles({active: pagination.vehicles.active, size: API_MAX_PAGE_SIZE}),
      resources.services({active: pagination.services.active, size: API_MAX_PAGE_SIZE}),
      resources.parts({active: pagination.parts.active, lowStock: pagination.parts.lowStock, size: API_MAX_PAGE_SIZE}),
      resources.lowStockParts({size: 20}),
      resources.serviceOrders({status: pagination.serviceOrders.status, size: API_MAX_PAGE_SIZE}),
      resources.averageExecutionTime(),
      auth.role === 'ADMIN' ? resources.demoLeads() : Promise.resolve([]),
    ]);

    const [user, users, customers, vehicles, services, parts, lowStockParts, serviceOrders, average, demoLeads] = requests;

    currentUser.value = user.status === 'fulfilled' ? user.value : currentUser.value;
    if (currentUser.value) {
      forms.account.fullName = currentUser.value.fullName;
    }
    data.users = users.status === 'fulfilled' && users.value ? listItems(users.value) : [];
    data.customers =
        customers.status === 'fulfilled' && customers.value ? listItems(customers.value) : [];
    data.vehicles = vehicles.status === 'fulfilled' ? listItems(vehicles.value) : [];
    data.services = services.status === 'fulfilled' ? listItems(services.value) : [];
    data.parts = parts.status === 'fulfilled' ? listItems(parts.value) : [];
    data.lowStockParts = lowStockParts.status === 'fulfilled' ? listItems(lowStockParts.value) : [];
    data.serviceOrders =
        serviceOrders.status === 'fulfilled' ? listItems(serviceOrders.value) : [];
    data.averageExecutionTime = average.status === 'fulfilled' ? average.value : null;
    data.demoLeads = demoLeads.status === 'fulfilled' ? demoLeads.value || [] : [];
    ensureStoreQuotes();

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
    if (!forms.orderWizard.serviceId) {
      throw new Error('Selecione ao menos um serviço para criar a ordem.');
    }

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
      services: [
        {
          serviceId: forms.orderWizard.serviceId,
          quantity: Number(forms.orderWizard.serviceQuantity),
        },
      ],
      parts: forms.orderWizard.partId
        ? [
            {
              partId: forms.orderWizard.partId,
              quantity: Number(forms.orderWizard.partQuantity),
            },
          ]
        : [],
      generateBudget: createBudgetNow,
    });

    forms.orderAction.serviceOrderId = order.id;
    if (createBudgetNow) {
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
    if (forms.customer.id) {
      await resources.updateCustomer(forms.customer.id, forms.customer);
    } else {
      await resources.createCustomer(forms.customer);
    }
    forms.customer.name = '';
    forms.customer.id = '';
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
    forms.customer.active = true;
  }, forms.customer.id ? 'Cliente atualizado.' : 'Cliente cadastrado.');
}

function createVehicle() {
  return runAction(async () => {
    const payload = {
      ...forms.vehicle,
      year: Number(forms.vehicle.year),
      mileage: Number(forms.vehicle.mileage),
    };
    if (forms.vehicle.id) {
      await resources.updateVehicle(forms.vehicle.id, payload);
    } else {
      await resources.createVehicle(payload);
    }
    forms.vehicle.id = '';
    forms.vehicle.customerId = '';
    forms.vehicle.plate = '';
    forms.vehicle.brand = '';
    forms.vehicle.model = '';
    forms.vehicle.year = new Date().getFullYear();
    forms.vehicle.mileage = 0;
    forms.vehicle.active = true;
  }, forms.vehicle.id ? 'Veículo atualizado.' : 'Veículo cadastrado.');
}

function createPart() {
  return runAction(async () => {
    const payload = {
      ...forms.part,
      costPrice: Number(forms.part.costPrice),
      unitPrice: Number(forms.part.unitPrice),
      stockQuantity: Number(forms.part.stockQuantity),
      minimumStock: Number(forms.part.minimumStock),
    };
    if (forms.part.id) {
      await resources.updatePart(forms.part.id, payload);
      await resources.configurePartReservation(forms.part.id, forms.part.reservationDays);
    } else {
      const part = await resources.createPart(payload);
      await resources.configurePartReservation(part.id, forms.part.reservationDays);
    }
    Object.assign(forms.part, {
      id: '',
      name: '',
      description: '',
      sku: '',
      category: '',
      subcategory: '',
      brand: '',
      costPrice: 0,
      unitPrice: 0,
      stockQuantity: 0,
      minimumStock: 1,
      active: true,
      reservationDays: 3,
    });
  }, forms.part.id ? 'Peça atualizada.' : 'Peça cadastrada.');
}

function editPart(part) {
  Object.assign(forms.part, {
    id: part.id,
    name: part.name,
    description: part.description || '',
    sku: part.sku,
    category: part.category,
    subcategory: part.subcategory || '',
    brand: part.brand,
    costPrice: Number(part.costPrice || 0),
    unitPrice: Number(part.unitPrice || 0),
    stockQuantity: Number(part.stockQuantity || 0),
    minimumStock: Number(part.minimumStock || 0),
    active: Boolean(part.active),
    reservationDays: Number(part.reservationDays || 3),
  });
  selectTab('parts');
}

function registerStockMovement() {
  return runAction(async () => {
    await resources.registerStockMovement(forms.stockMovement.partId, {
      type: forms.stockMovement.type,
      quantity: Number(forms.stockMovement.quantity),
      unitCost: Number(forms.stockMovement.unitCost || 0),
      unitPrice: Number(forms.stockMovement.unitPrice || 0),
      reason: forms.stockMovement.reason,
    });
    Object.assign(forms.stockMovement, {
      partId: '',
      type: 'ENTRY',
      quantity: 1,
      unitCost: 0,
      unitPrice: 0,
      reason: '',
    });
  }, 'Movimentação registrada.');
}

function storeQuoteTotal(quote) {
  return (quote.items || []).reduce(
      (total, item) => total + Number(item.quantity || 0) * Number(item.quotedPrice || 0),
      0,
  );
}

function ensureStoreQuotes() {
  if (storeQuotesInitialized.value || !data.parts.length) {
    return;
  }

  const storedQuotes = readStoredJson('autocare.partsStore.quotes', []);
  if (storedQuotes.length) {
    storeQuotes.value = storedQuotes;
    storeQuotesInitialized.value = true;
    return;
  }

  const [firstPart, secondPart, thirdPart] = data.parts;
  storeQuotes.value = [
    {
      id: 'CART-1001',
      customerName: 'Oficina Avenida',
      customerContact: 'compras@oficinaavenida.com',
      status: 'SENT',
      contactRequested: true,
      employeeId: currentUser.value?.id || '',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      items: [
        {
          partId: firstPart.id,
          name: firstPart.name,
          quantity: 2,
          quotedPrice: Number(firstPart.unitPrice || 0),
        },
      ],
    },
    {
      id: 'CART-1002',
      customerName: 'Cliente Balção',
      customerContact: '11988887777',
      status: 'APPROVED',
      contactRequested: false,
      employeeId: currentUser.value?.id || '',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      items: [
        {
          partId: secondPart?.id || firstPart.id,
          name: secondPart?.name || firstPart.name,
          quantity: 1,
          quotedPrice: Number(secondPart?.unitPrice || firstPart.unitPrice || 0),
        },
        {
          partId: thirdPart?.id || firstPart.id,
          name: thirdPart?.name || firstPart.name,
          quantity: 3,
          quotedPrice: Number(thirdPart?.unitPrice || firstPart.unitPrice || 0),
        },
      ],
    },
  ];
  storeQuotesInitialized.value = true;
  persistStoreQuotes();
}

function persistStoreQuotes() {
  localStorage.setItem('autocare.partsStore.quotes', JSON.stringify(storeQuotes.value));
}

function resetStoreQuoteForm() {
  Object.assign(forms.storeQuote, {
    id: '',
    customerName: '',
    customerContact: '',
    status: 'DRAFT',
    contactRequested: false,
    partId: '',
    quantity: 1,
    quotedPrice: 0,
    items: [],
  });
}

function addStoreQuoteItem() {
  const part = data.parts.find((item) => item.id === forms.storeQuote.partId);
  if (!part) {
    error.value = 'Selecione uma peça para adicionar ao carrinho.';
    return;
  }

  forms.storeQuote.items.push({
    partId: part.id,
    name: part.name,
    quantity: Number(forms.storeQuote.quantity || 1),
    quotedPrice: Number(forms.storeQuote.quotedPrice || part.unitPrice || 0),
  });
  forms.storeQuote.partId = '';
  forms.storeQuote.quantity = 1;
  forms.storeQuote.quotedPrice = 0;
}

function removeStoreQuoteItem(index) {
  forms.storeQuote.items.splice(index, 1);
}

function saveStoreQuote() {
  if (!forms.storeQuote.customerName || !forms.storeQuote.items.length) {
    error.value = 'Informe o cliente e ao menos uma peça para salvar o carrinho.';
    return;
  }

  const quote = {
    id: forms.storeQuote.id || `CART-${Date.now().toString().slice(-6)}`,
    customerName: forms.storeQuote.customerName,
    customerContact: forms.storeQuote.customerContact,
    status: forms.storeQuote.status || 'DRAFT',
    contactRequested: forms.storeQuote.contactRequested,
    employeeId: currentUser.value?.id || '',
    createdAt: forms.storeQuote.createdAt || new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    items: [...forms.storeQuote.items],
  };
  const index = storeQuotes.value.findIndex((item) => item.id === quote.id);
  if (index >= 0) {
    storeQuotes.value[index] = quote;
  } else {
    storeQuotes.value.unshift(quote);
  }
  persistStoreQuotes();
  resetStoreQuoteForm();
  success.value = 'Carrinho salvo.';
}

function editStoreQuote(quote) {
  Object.assign(forms.storeQuote, {
    id: quote.id,
    customerName: quote.customerName,
    customerContact: quote.customerContact,
    status: quote.status,
    contactRequested: quote.contactRequested,
    partId: '',
    quantity: 1,
    quotedPrice: 0,
    items: quote.items.map((item) => ({...item})),
  });
  selectTab('store-quotes');
}

function updateStoreQuoteStatus(quote, status) {
  return runAction(async () => {
    if (status === 'SENT' && quote.status !== 'SENT') {
      await Promise.all(
          quote.items.map((item) => resources.reservePart(item.partId, Number(item.quantity))),
      );
    }
    if (status === 'APPROVED') {
      await Promise.all(
          quote.items.map((item) =>
              resources.commitPartReservation(item.partId, {
                quantity: Number(item.quantity),
                reason: `Carrinho ${quote.id} aprovado`,
              }),
          ),
      );
    }
    if (['REFUSED', 'EXPIRED'].includes(status) && quote.status === 'SENT') {
      await Promise.all(
          quote.items.map((item) => resources.releasePartReservation(item.partId, Number(item.quantity))),
      );
    }
    quote.status = status;
    quote.updatedAt = new Date().toISOString();
    persistStoreQuotes();
  }, `Carrinho marcado como ${storeQuoteStatusLabels[status]}.`);
}

function selectCustomerPart(part) {
  selectedCustomerPart.value = part;
  openRecord('Peça para comparar', part);
}

function addCustomerPartRequest(part, store = null) {
  const existing = forms.customerQuote.items.find((item) => item.partId === part.id && item.storeName === (store?.name || ''));
  if (existing) {
    existing.quantity += 1;
  } else {
    forms.customerQuote.items.push({
      partId: part.id,
      name: part.name,
      quantity: 1,
      estimatedPrice: Number(store?.price || part.unitPrice || 0),
      storeName: store?.name || '',
      storeContact: store?.username || '',
    });
  }
  if (store) {
    forms.customerQuote.storeName = store.name;
    forms.customerQuote.storeContact = store.username;
  }
  success.value = 'Peça adicionada à solicitação.';
}

function removeCustomerQuoteItem(index) {
  forms.customerQuote.items.splice(index, 1);
}

function requestStoreQuote(store = null) {
  if (store) {
    forms.customerQuote.storeName = store.name;
    forms.customerQuote.storeContact = store.username;
  }
  selectTab('customer-cart');
}

function contactWorkshop(workshop) {
  forms.customerQuote.workshopName = workshop.name;
  selectTab('customer-cart');
}

function sendCustomerQuoteRequest() {
  if (!forms.customerQuote.storeName && !forms.customerQuote.workshopName) {
    error.value = 'Escolha uma loja ou oficina para enviar a solicitação.';
    return;
  }
  if (!forms.customerQuote.items.length && !forms.customerQuote.problemDescription.trim()) {
    error.value = 'Adicione uma peça ou descreva o problema do veículo.';
    return;
  }
  success.value = 'Solicitação enviada. O parceiro poderá responder com um orçamento.';
  forms.customerQuote = {
    storeName: '',
    storeContact: '',
    workshopName: '',
    vehicleId: '',
    problemDescription: '',
    items: [],
  };
}

function createWorkshopService() {
  return runAction(async () => {
    const payload = {
      ...forms.service,
      basePrice: Number(forms.service.basePrice),
      estimatedTimeInMinutes: Number(forms.service.estimatedTimeInMinutes),
    };
    if (forms.service.id) {
      await resources.updateWorkshopService(forms.service.id, payload);
    } else {
      await resources.createWorkshopService(payload);
    }
    Object.assign(forms.service, {
      id: '',
      name: '',
      description: '',
      basePrice: 0,
      estimatedTimeInMinutes: 60,
      active: true,
    });
  }, forms.service.id ? 'Serviço atualizado.' : 'Serviço cadastrado.');
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
  pagination[resource].page = Math.min(
      listTotalPages(resource) - 1,
      Math.max(0, pagination[resource].page + direction),
  );
}

function resetListPage(resource) {
  pagination[resource].page = 0;
}

const isCustomerDetail = computed(() =>
    selectedRecord.value && selectedRecordType.value === 'Cliente',
);

const isWorkshopDetail = computed(() =>
    selectedRecord.value && ['Oficina', 'Oficina parceira'].includes(selectedRecordType.value),
);

const selectedPartnerUser = computed(() =>
    data.users.find((user) => user.id === selectedRecord.value?.id),
);

const detailModalDirty = computed(() => {
  if (isCustomerDetail.value) {
    const original = selectedRecord.value;
    const draft = modalDraft.customer;
    return JSON.stringify({
      name: original.name,
      document: original.document,
      phone: original.phone,
      email: original.email,
      address: original.address || {},
      active: original.active !== false,
    }) !== JSON.stringify(draft);
  }

  if (isWorkshopDetail.value) {
    const user = selectedPartnerUser.value;
    if (!user) {
      return false;
    }
    return JSON.stringify({
      companyName: user.companyName || '',
      fullName: user.fullName || '',
      active: user.active !== false,
    }) !== JSON.stringify(modalDraft.partner);
  }

  return false;
});

function openRecord(type, record) {
  selectedRecordType.value = type;
  selectedRecord.value = record;
  if (type === 'Cliente') {
    Object.assign(modalDraft.customer, {
      name: record.name || '',
      document: record.document || '',
      phone: record.phone || '',
      email: record.email || '',
      address: {...(record.address || forms.customer.address)},
      active: record.active !== false,
    });
  }
  if (['Oficina', 'Oficina parceira'].includes(type)) {
    const user = data.users.find((item) => item.id === record.id) || record;
    Object.assign(modalDraft.partner, {
      companyName: user.companyName || record.name || '',
      fullName: user.fullName || record.adminName || '',
      active: user.active !== false && record.status !== 'Inativa',
    });
  }
}

function closeRecord() {
  selectedRecord.value = null;
  selectedRecordType.value = '';
  Object.assign(modalDraft.customer, {});
  Object.assign(modalDraft.partner, {});
}

async function saveDetailModal() {
  if (isCustomerDetail.value) {
    await runAction(async () => {
      await resources.updateCustomer(selectedRecord.value.id, modalDraft.customer);
      closeRecord();
    }, 'Cliente atualizado.');
    return;
  }

  if (isWorkshopDetail.value && selectedPartnerUser.value) {
    const user = selectedPartnerUser.value;
    await runAction(async () => {
      await resources.updateUser(user.id, {
        username: user.username,
        role: user.role,
        customerId: user.customerId || null,
        fullName: modalDraft.partner.fullName,
        profileType: user.profileType,
        companyName: modalDraft.partner.companyName,
        companyType: user.companyType || 'WORKSHOP',
        employeeSubRole: user.employeeSubRole || '',
        permissions: user.permissions || [],
        active: modalDraft.partner.active,
      });
      closeRecord();
    }, 'Oficina atualizada.');
  }
}

function editCustomer(customer) {
  Object.assign(forms.customer, {
    ...customer,
    address: {...(customer.address || forms.customer.address)},
    active: customer.active !== false,
  });
  selectTab('customers');
}

function editVehicle(vehicle) {
  Object.assign(forms.vehicle, {
    id: vehicle.id,
    customerId: vehicle.customerId,
    plate: vehicle.plate,
    brand: vehicle.brand,
    model: vehicle.model,
    year: vehicle.year,
    mileage: vehicle.mileage,
    active: vehicle.active !== false,
  });
  selectTab('vehicles');
}

function editService(service) {
  Object.assign(forms.service, {
    id: service.id,
    name: service.name,
    description: service.description,
    basePrice: Number(service.basePrice || 0),
    estimatedTimeInMinutes: Number(service.estimatedTimeInMinutes || 60),
    active: service.active !== false,
  });
  selectTab('services');
}

function editUser(user) {
  Object.assign(forms.user, {
    id: user.id,
    fullName: user.fullName,
    username: user.username,
    password: '123456',
    role: user.role,
    profileType: user.profileType,
    companyName: user.companyName || '',
    companyType: user.companyType || '',
    employeeSubRole: user.employeeSubRole || 'UNSPECIFIED',
    permissions: [...(user.permissions || [])],
    customerId: user.customerId || '',
    active: user.active,
  });
  if (user.profileType === 'WORKSHOP_EMPLOYEE') {
    selectTab('employees');
    return;
  }
  if (['PARTS_STORE_EMPLOYEE', 'PARTS_STORE_ADMIN'].includes(user.profileType)) {
    selectTab('store-employees');
    return;
  }
  selectTab('users');
}

function toggleUserPermission(permissionId) {
  const index = forms.user.permissions.indexOf(permissionId);
  if (index >= 0) {
    forms.user.permissions.splice(index, 1);
    return;
  }
  forms.user.permissions.push(permissionId);
}

function saveUser() {
  return runAction(async () => {
    const payload = {
      fullName: forms.user.fullName,
      username: forms.user.username,
      role: forms.user.role,
      profileType: forms.user.profileType,
      companyName: forms.user.companyName,
      companyType: forms.user.companyType,
      employeeSubRole: forms.user.employeeSubRole,
      permissions: forms.user.permissions,
      customerId: forms.user.customerId || null,
      active: forms.user.active,
    };
    if (forms.user.id) {
      await resources.updateUser(forms.user.id, payload);
    } else {
      await resources.createUser({...payload, password: forms.user.password});
    }
    Object.assign(forms.user, {
      id: '',
      fullName: '',
      username: '',
      password: '123456',
      role: 'EMPLOYEE',
      profileType: 'WORKSHOP_EMPLOYEE',
      companyName: '',
      companyType: '',
      employeeSubRole: 'UNSPECIFIED',
      permissions: ['CREATE_ORDER', 'EDIT_ORDER', 'CREATE_BUDGET'],
      customerId: '',
      active: true,
    });
  }, forms.user.id ? 'Usuário atualizado.' : 'Usuário criado.');
}

function saveStoreEmployee() {
  forms.user.role = 'EMPLOYEE';
  forms.user.profileType = 'PARTS_STORE_EMPLOYEE';
  if (!forms.user.id && forms.user.permissions.includes('CREATE_ORDER')) {
    forms.user.permissions = ['MANAGE_STOCK', 'CREATE_BUDGET', 'VIEW_STATS'];
  }
  return saveUser();
}

function saveAccount() {
  return runAction(async () => {
    currentUser.value = await resources.updateCurrentUser({fullName: forms.account.fullName});
  }, 'Dados do usuário atualizados.');
}

function changePassword() {
  return runAction(async () => {
    await resources.changeCurrentPassword(forms.password);
    forms.password.currentPassword = '';
    forms.password.newPassword = '';
  }, 'Senha alterada.');
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
  if (action === 'Editar informações do usuário') {
    activeTab.value = 'users';
    return;
  }
  if (action === 'Alterar senha') {
    activeTab.value = 'users';
  }
}

function logout() {
  profileMenuOpen.value = false;
  auth.logout();
  router.push({name: 'login'});
}

onMounted(async () => {
  await loadDashboard();
  await loadHomePreferences();
});
</script>

<template>
  <main :class="{ 'mobile-sidebar-open': mobileMenuOpen }" class="app-shell">
    <header class="site-navbar">
      <div class="navbar-inner">
        <button
            class="menu-button"
            title="Abrir menu"
            type="button"
            @click="mobileMenuOpen = !mobileMenuOpen"
        >
          <X v-if="mobileMenuOpen" :size="22"/>
          <Menu v-else :size="22"/>
        </button>

        <div class="navbar-brand">
          <div class="brand-mark">
            <Wrench :size="22"/>
          </div>
          <div>
            <strong>AutoCare Hub</strong>
            <span>{{ roleLabel }}</span>
          </div>
        </div>

        <div class="navbar-search">
          <Search :size="17"/>
          <input
              v-model="globalSearch"
              aria-label="Busca global"
              placeholder="Buscar clientes, placas, peças, ordens..."
              type="search"
          />
          <div v-if="searchResults.length" class="search-popover">
            <button
                v-for="result in searchResults"
                :key="`${result.type}-${result.label}-${result.detail}`"
                type="button"
                @click="selectSearchResult(result)"
            >
              <component :is="result.icon" :size="17"/>
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
                :aria-expanded="profileMenuOpen"
                aria-haspopup="menu"
                class="profile-trigger"
                title="Menu do usuário"
                type="button"
                @click="profileMenuOpen = !profileMenuOpen"
            >
              {{ userInitials }}
            </button>
            <div v-if="profileMenuOpen" class="profile-popover" role="menu">
              <button role="menuitem" type="button" @click="showProfileAction('Editar informações do usuário')">
                <UserCog :size="17"/>
                <span>Editar informações do usuário</span>
              </button>
              <button role="menuitem" type="button" @click="showProfileAction('Alterar senha')">
                <KeyRound :size="17"/>
                <span>Alterar senha</span>
              </button>
              <button role="menuitem" type="button" @click="logout">
                <LogOut :size="17"/>
                <span>Sair</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <div :class="{ 'sidebar-collapsed': sidebarCollapsed }" class="app-layout">
      <aside :class="{ collapsed: sidebarCollapsed }" class="app-sidebar">
        <button
            :title="sidebarCollapsed ? 'Expandir menu' : 'Recolher menu'"
            class="sidebar-toggle"
            type="button"
            @click="sidebarCollapsed = !sidebarCollapsed"
        >
          <ChevronRight v-if="sidebarCollapsed" :size="18"/>
          <ChevronLeft v-else :size="18"/>
          <span>{{ sidebarCollapsed ? 'Expandir' : 'Recolher' }}</span>
        </button>

        <nav aria-label="Acessos principais" class="side-nav">
          <button
              v-for="tab in availableTabs"
              :key="tab.id"
              :class="{ active: activeTab === tab.id }"
              :title="tab.label"
              type="button"
              @click="selectTab(tab.id)"
          >
            <component :is="tab.icon" :size="20"/>
            <span>
              <strong>{{ tab.label }}</strong>
            </span>
          </button>
        </nav>
      </aside>

      <button
          v-if="mobileMenuOpen"
          aria-label="Fechar menu"
          class="sidebar-backdrop"
          type="button"
          @click="mobileMenuOpen = false"
      ></button>

      <section class="content">
        <p v-if="error" class="alert error">{{ error }}</p>
        <p v-if="success" class="alert success">{{ success }}</p>

        <section v-if="activeTab === 'overview'" class="screen-stack">
          <div class="home-toolbar">
            <span><ShieldCheck :size="16"/> {{ auth.user?.username }}</span>
            <button v-if="!isCustomerProfile" class="secondary-button" type="button" @click="homeSettingsOpen = !homeSettingsOpen">
              <Plus :size="17"/>
              Personalizar home
            </button>
          </div>

          <section v-if="isCustomerProfile" class="customer-home-grid">
            <article class="section-block">
              <div class="section-heading">
                <h2>Meus veículos</h2>
                <span>Status atual e últimas observações</span>
              </div>
              <div class="customer-vehicle-list">
                <button
                    v-for="vehicle in customerVehicles"
                    :key="vehicle.id"
                    type="button"
                    @click="openRecord('Veículo', vehicle)"
                >
                  <strong>{{ vehicle.plate }} · {{ vehicle.brand }} {{ vehicle.model }}</strong>
                  <span>{{ statusLabels[vehicle.currentStatus] || vehicle.currentStatus || 'Sem ordem ativa' }}</span>
                  <small>{{ vehicle.diagnosticNotes || `${vehicle.year} · ${vehicle.mileage} km` }}</small>
                </button>
                <p v-if="!customerVehicles.length" class="empty-state">Nenhum veículo vinculado à sua conta.</p>
              </div>
            </article>

            <article class="section-block">
              <div class="section-heading">
                <h2>Avisos</h2>
                <span>Orçamentos e retirada</span>
              </div>
              <div class="customer-alert-list">
                <button v-for="order in customerBudgetAlerts" :key="`budget-${order.id}`" type="button" @click="openRecord('Orçamento pendente', order)">
                  <AlertTriangle :size="18"/>
                  <span>Orçamento pendente de aprovação</span>
                  <strong>R$ {{ money(order.totalAmount) }}</strong>
                </button>
                <button v-for="order in customerReadyAlerts" :key="`ready-${order.id}`" type="button" @click="openRecord('Veículo pronto', order)">
                  <CheckCircle2 :size="18"/>
                  <span>Veículo pronto para retirada</span>
                  <strong>{{ statusLabels[order.status] }}</strong>
                </button>
                <button v-for="order in customerFinishedAlerts" :key="`finished-${order.id}`" type="button" @click="openRecord('Veículo concluído', order)">
                  <CheckCircle2 :size="18"/>
                  <span>Atendimento concluído</span>
                  <strong>{{ statusLabels[order.status] }}</strong>
                </button>
                <p v-if="!customerBudgetAlerts.length && !customerReadyAlerts.length && !customerFinishedAlerts.length" class="empty-state">Nenhum alerta ativo.</p>
              </div>
            </article>
          </section>

          <section v-if="isCustomerProfile" class="section-block">
            <div class="section-heading">
              <h2>Histórico recente</h2>
              <span>Últimos serviços e orçamentos</span>
            </div>
            <div class="data-table">
              <div class="data-table-header orders-grid">
                <span>Status</span>
                <span>Descrição</span>
                <span>Itens</span>
                <span>Total</span>
              </div>
              <article
                  v-for="order in customerRecentHistory"
                  :key="order.id"
                  class="data-table-row orders-grid clickable-row"
                  @click="openRecord('Histórico', order)"
              >
                <span class="badge">{{ statusLabels[order.status] || order.status }}</span>
                <span>{{ order.diagnosticNotes }}<small>{{ order.id }}</small></span>
                <span>{{ order.services?.length || 0 }} serviços<small>{{ order.parts?.length || 0 }} peças</small></span>
                <strong>R$ {{ money(order.totalAmount) }}</strong>
              </article>
            </div>
          </section>

          <section v-if="homeSettingsOpen" class="home-settings-panel">
            <div>
              <strong>Meus widgets</strong>
              <div class="home-option-grid">
                <label v-for="widget in availableHomeWidgetDefinitions" :key="widget.id">
                  <input
                      :checked="homePreferences.userWidgets.includes(widget.id)"
                      type="checkbox"
                      @change="toggleHomeWidget(widget.id)"
                  />
                  <span>{{ widget.label }}</span>
                </label>
              </div>
            </div>
            <div v-if="auth.role === 'ADMIN'">
              <strong>{{ isPartsStoreProfile ? 'Configuração da loja' : 'Configuração da oficina' }}</strong>
              <label class="home-alert-toggle">
                <input
                    :checked="homePreferences.showAlertsOnHome"
                    type="checkbox"
                    @change="toggleHomeAlerts"
                />
                <span>Exibir avisos críticos de estoque para a equipe</span>
              </label>
              <div class="home-option-grid">
                <label v-for="widget in availableHomeWidgetDefinitions" :key="`global-${widget.id}`">
                  <input
                      :checked="homePreferences.globalWidgets.includes(widget.id)"
                      type="checkbox"
                      @change="toggleHomeWidget(widget.id, 'global')"
                  />
                  <span>{{ widget.label }}</span>
                </label>
              </div>
            </div>
          </section>

          <section v-if="!isMasterAdmin && !isCustomerProfile" class="home-summary-grid">
            <button
                v-for="widget in homeWidgets"
                :key="widget.id"
                class="home-widget"
                type="button"
                @click="openHomeWidget(widget)"
            >
              <component :is="widget.icon" :size="22"/>
              <strong>{{ widget.value }}</strong>
              <span>{{ widget.label }}</span>
            </button>
          </section>

          <section v-if="isMasterAdmin" class="master-widget-sections">
            <div v-if="masterPlatformWidgets.length" class="widget-group">
              <div class="section-heading compact-heading">
                <h2>Operação da plataforma</h2>
                <span>Base ativa e parceiros cadastrados</span>
              </div>
              <div class="home-summary-grid">
                <button
                    v-for="widget in masterPlatformWidgets"
                    :key="widget.id"
                    :class="`tone-${widget.tone}`"
                    class="home-widget"
                    type="button"
                    @click="openHomeWidget(widget)"
                >
                  <component :is="widget.icon" :size="22"/>
                  <strong>{{ widget.value }}</strong>
                  <span>{{ widget.label }}</span>
                </button>
              </div>
            </div>
            <div v-if="masterFinancialWidgets.length" class="widget-group">
              <div class="section-heading compact-heading">
                <h2>Financeiro</h2>
                <span>Faturamento, taxas e receita da AutoCare Hub</span>
              </div>
              <div class="home-summary-grid">
                <button
                    v-for="widget in masterFinancialWidgets"
                    :key="widget.id"
                    :class="`tone-${widget.tone}`"
                    class="home-widget"
                    type="button"
                    @click="openHomeWidget(widget)"
                >
                  <component :is="widget.icon" :size="22"/>
                  <strong>{{ widget.value }}</strong>
                  <span>{{ widget.label }}</span>
                </button>
              </div>
            </div>
          </section>

          <section v-if="isMasterAdmin" class="section-block">
            <div class="section-heading">
              <h2>Ranking da plataforma</h2>
              <span>Clientes, parceiros e potenciais comerciais</span>
            </div>
            <div class="employee-metrics-grid">
              <article class="employee-card">
                <div><strong>Clientes mais frequentes</strong><span>Serviços e compras</span></div>
                <dl>
                  <template v-for="customer in masterFrequentCustomers" :key="`freq-${customer.id}`">
                    <dt>{{ customer.name }}</dt>
                    <dd>{{ customer.frequency }}</dd>
                  </template>
                </dl>
              </article>
              <article class="employee-card">
                <div><strong>Clientes que mais gastaram</strong><span>Valor acumulado</span></div>
                <dl>
                  <template v-for="customer in masterTopSpenders" :key="`spent-${customer.id}`">
                    <dt>{{ customer.name }}</dt>
                    <dd>R$ {{ money(customer.spent) }}</dd>
                  </template>
                </dl>
              </article>
              <article class="employee-card">
                <div><strong>Maior número de veículos</strong><span>Clientes</span></div>
                <dl>
                  <template v-for="customer in masterVehicleOwners" :key="`vehicles-${customer.id}`">
                    <dt>{{ customer.name }}</dt>
                    <dd>{{ customer.vehiclesCount }}</dd>
                  </template>
                </dl>
              </article>
              <article class="employee-card">
                <div><strong>Parceiros que mais geram receita</strong><span>Taxas para a plataforma</span></div>
                <dl>
                  <template v-for="partner in masterTopPlatformRevenuePartners" :key="`fee-${partner.id}`">
                    <dt>{{ partner.name }}</dt>
                    <dd>R$ {{ money(partner.feeAmount) }}</dd>
                  </template>
                </dl>
              </article>
              <article class="employee-card">
                <div><strong>Parceiros com maior potencial</strong><span>Leads da demo</span></div>
                <dl>
                  <template v-for="lead in masterPotentialPartners.slice(0, 5)" :key="lead.id">
                    <dt>{{ lead.companyName }}</dt>
                    <dd>{{ lead.demoProfile === 'workshop' ? 'Oficina' : 'Loja' }}</dd>
                  </template>
                </dl>
              </article>
            </div>
          </section>

          <section v-if="isPartsStoreProfile && !isMasterAdmin" class="section-block">
            <div class="section-heading">
              <h2>Resumo comercial</h2>
              <span>Vendas, conversão e itens com maior giro</span>
            </div>
            <div class="analytics-grid">
              <article class="metric-card">
                <DollarSign :size="22"/>
                <strong>R$ {{ money(storeBillingSummary.gross) }}</strong>
                <span>Vendas aprovadas</span>
              </article>
              <article class="metric-card">
                <BadgePercent :size="22"/>
                <strong>{{ storeBillingSummary.conversion }}%</strong>
                <span>Taxa de conversão</span>
              </article>
              <article class="metric-card">
                <ShoppingCart :size="22"/>
                <strong>R$ {{ money(storeBillingSummary.ticket) }}</strong>
                <span>Ticket médio</span>
              </article>
              <article class="metric-card">
                <Package :size="22"/>
                <strong>{{ storeTopProducts[0]?.name || 'Sem vendas' }}</strong>
                <span>Produto mais vendido</span>
              </article>
            </div>
          </section>

          <section v-if="!isPartsStoreProfile && !isMasterAdmin && !isCustomerProfile" aria-label="Status atual dos veículos" class="vehicle-status-grid">
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
              <AlertTriangle :size="22"/>
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

        <section v-if="activeTab === 'billing' && isWorkshopAdmin" class="screen-stack">
          <section class="order-flow-stats">
            <article>
              <strong>R$ {{ money(billingSummary.gross) }}</strong>
              <span>Faturamento bruto</span>
            </article>
            <article>
              <strong>{{ billingSummary.feeRateLabel }}</strong>
              <span>Taxa AutoCare Hub</span>
            </article>
            <article>
              <strong>R$ {{ money(billingSummary.net) }}</strong>
              <span>Líquido estimado já com taxa AutoCare Hub descontada</span>
            </article>
            <article>
              <strong>R$ {{ money(billingSummary.ticket) }}</strong>
              <span>Ticket médio</span>
            </article>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Resumo financeiro</h2>
              <span>Receita, conversão e serviços concluídos</span>
            </div>
            <div class="analytics-grid">
              <article class="metric-card">
                <BadgePercent :size="22"/>
                <strong>{{ billingSummary.sentBudgets }}</strong>
                <span>Orçamentos enviados</span>
              </article>
              <article class="metric-card">
                <CheckCircle2 :size="22"/>
                <strong>{{ billingSummary.approvedBudgets }}</strong>
                <span>Orçamentos aprovados</span>
              </article>
              <article class="metric-card">
                <Wrench :size="22"/>
                <strong>{{ billingSummary.completed }}</strong>
                <span>Serviços concluídos</span>
              </article>
              <article class="metric-card">
                <DollarSign :size="22"/>
                <strong>R$ {{ money(billingSummary.feeAmount) }}</strong>
                <span>Taxa estimada da plataforma</span>
              </article>
            </div>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Evolução mensal</h2>
              <span>Faturamento por mês nas ordens carregadas</span>
            </div>
            <div class="comparison-bars revenue-bars">
              <div v-for="month in monthlyRevenue" :key="month.month">
                <span>{{ month.month }}</span>
                <div>
                  <b :style="{ width: `${Math.max(6, (month.value / Math.max(1, billingSummary.gross)) * 100)}%` }"></b>
                </div>
                <strong>R$ {{ money(month.value) }}</strong>
              </div>
            </div>
            <article class="selected-record">
              <BarChart3 :size="20"/>
              <strong>
                {{ billingSummary.nextTierGap > 0 ? `Faltam R$ ${money(billingSummary.nextTierGap)}` : 'Melhor tier atingida' }}
              </strong>
              <span>
                {{ billingSummary.nextTierGap > 0 ? `Para atingir a próxima tier de taxa (${billingSummary.nextTierLabel}).` : 'A oficina já está na menor taxa disponível.' }}
              </span>
            </article>
          </section>
        </section>

        <section v-if="activeTab === 'employees' && isWorkshopAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>{{ forms.user.id ? 'Editar funcionário' : 'Novo funcionário' }}</h2>
              <span>Role geral, subrole e permissões da oficina</span>
            </div>
            <form class="form-grid" @submit.prevent="forms.user.role = 'EMPLOYEE'; forms.user.profileType = 'WORKSHOP_EMPLOYEE'; saveUser()">
              <input v-model="forms.user.fullName" placeholder="Nome completo" required/>
              <input v-model="forms.user.username" placeholder="E-mail" required type="email"/>
              <input v-if="!forms.user.id" v-model="forms.user.password" minlength="6" placeholder="Senha inicial" required type="password"/>
              <select v-model="forms.user.employeeSubRole">
                <option value="MECHANIC">Mecânico</option>
                <option value="ATTENDANT">Atendente</option>
                <option value="UNSPECIFIED">Funcionário sem especificação</option>
              </select>
              <label class="check-row">
                <input v-model="forms.user.active" type="checkbox"/>
                <span>Funcionário ativo</span>
              </label>
              <div class="permission-grid">
                <label v-for="permission in permissionDefinitions" :key="permission.id">
                  <input
                      :checked="forms.user.permissions.includes(permission.id)"
                      type="checkbox"
                      @change="toggleUserPermission(permission.id)"
                  />
                  <span>{{ permission.label }}</span>
                </label>
              </div>
              <button :disabled="saving" class="primary-button" type="submit">
                <UserPlus :size="18"/>
                <span>{{ forms.user.id ? 'Salvar funcionário' : 'Criar funcionário' }}</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Funcionários</h2>
              <span>{{ workshopEmployees.length }} pessoas na oficina</span>
            </div>
            <div class="data-table">
              <div class="data-table-header employee-grid">
                <span>Funcionário</span>
                <span>Subrole</span>
                <span>Permissões</span>
                <span>Status</span>
                <span>Ação</span>
              </div>
              <article
                  v-for="employee in workshopEmployees"
                  :key="employee.id"
                  class="data-table-row employee-grid clickable-row"
                  @click="openRecord('Funcionário', employee)"
              >
                <strong>{{ employee.fullName }}<small>{{ employee.username }}</small></strong>
                <span>{{ employeeSubRoleLabels[employee.employeeSubRole] || employee.employeeSubRole }}</span>
                <span>{{ employee.permissions?.length || 0 }} permissões</span>
                <span class="badge">{{ employee.active ? 'Ativo' : 'Inativo' }}</span>
                <button class="secondary-button compact-action" type="button" @click.stop="editUser(employee)">Editar</button>
              </article>
            </div>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Métricas por funcionário</h2>
              <span>Indicadores por subrole</span>
            </div>
            <div class="employee-metrics-grid">
              <article v-for="employee in workshopEmployees" :key="`metrics-${employee.id}`" class="employee-card">
                <div>
                  <strong>{{ employee.fullName }}</strong>
                  <span>{{ employeeSubRoleLabels[employee.employeeSubRole] || employee.employeeSubRole }}</span>
                </div>
                <dl>
                  <template v-for="metric in employeeMetrics(employee)" :key="metric.label">
                    <dt>{{ metric.label }}</dt>
                    <dd>{{ metric.value }}</dd>
                  </template>
                </dl>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'store-billing' && isPartsStoreAdmin" class="screen-stack">
          <section class="order-flow-stats">
            <article>
              <strong>R$ {{ money(storeBillingSummary.gross) }}</strong>
              <span>Faturamento bruto</span>
            </article>
            <article>
              <strong>{{ storeBillingSummary.feeRateLabel }}</strong>
              <span>Taxa AutoCare Hub</span>
            </article>
            <article>
              <strong>R$ {{ money(storeBillingSummary.net) }}</strong>
              <span>Líquido estimado já com taxa AutoCare Hub descontada</span>
            </article>
            <article>
              <strong>R$ {{ money(storeBillingSummary.ticket) }}</strong>
              <span>Ticket médio</span>
            </article>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Métricas comerciais</h2>
              <span>Orçamentos, conversão e vendas de peças</span>
            </div>
            <div class="analytics-grid">
              <article class="metric-card">
                <ClipboardList :size="22"/>
                <strong>{{ storeBillingSummary.sentQuotes }}</strong>
                <span>Orçamentos enviados</span>
              </article>
              <article class="metric-card">
                <CheckCircle2 :size="22"/>
                <strong>{{ storeBillingSummary.approvedQuotes }}</strong>
                <span>Orçamentos aprovados</span>
              </article>
              <article class="metric-card">
                <BadgePercent :size="22"/>
                <strong>{{ storeBillingSummary.conversion }}%</strong>
                <span>Taxa de conversão</span>
              </article>
              <article class="metric-card">
                <DollarSign :size="22"/>
                <strong>R$ {{ money(storeBillingSummary.feeAmount) }}</strong>
                <span>Taxa estimada da plataforma</span>
              </article>
            </div>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Evolução mensal</h2>
              <span>Vendas aprovadas nos carrinhos da loja</span>
            </div>
            <div class="comparison-bars revenue-bars">
              <div v-for="month in storeMonthlyRevenue" :key="month.month">
                <span>{{ month.month }}</span>
                <div>
                  <b :style="{ width: `${Math.max(6, (month.value / Math.max(1, storeBillingSummary.gross)) * 100)}%` }"></b>
                </div>
                <strong>R$ {{ money(month.value) }}</strong>
              </div>
            </div>
            <article class="selected-record">
              <BarChart3 :size="20"/>
              <strong>
                {{ storeBillingSummary.nextTierGap > 0 ? `Faltam R$ ${money(storeBillingSummary.nextTierGap)}` : 'Melhor tier atingida' }}
              </strong>
              <span>
                {{ storeBillingSummary.nextTierGap > 0 ? `Para atingir a próxima tier de taxa (${storeBillingSummary.nextTierLabel}).` : 'A loja já está na menor taxa disponível.' }}
              </span>
            </article>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Produtos e clientes</h2>
              <span>Itens mais vendidos e clientes frequentes</span>
            </div>
            <div class="employee-metrics-grid">
              <article class="employee-card">
                <div>
                  <strong>Produtos mais vendidos</strong>
                  <span>{{ storeTopProducts.length }} itens com venda aprovada</span>
                </div>
                <dl>
                  <template v-for="product in storeTopProducts" :key="product.name">
                    <dt>{{ product.name }}</dt>
                    <dd>{{ product.quantity }} un.</dd>
                  </template>
                </dl>
              </article>
              <article class="employee-card">
                <div>
                  <strong>Clientes mais frequentes</strong>
                  <span>{{ storeFrequentCustomers.length }} clientes recorrentes</span>
                </div>
                <dl>
                  <template v-for="customer in storeFrequentCustomers" :key="customer.name">
                    <dt>{{ customer.name }}</dt>
                    <dd>{{ customer.count }} compras</dd>
                  </template>
                </dl>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'store-employees' && isPartsStoreAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>{{ forms.user.id ? 'Editar funcionário da loja' : 'Novo funcionário da loja' }}</h2>
              <span>Atendentes, admin e permissões comerciais</span>
            </div>
            <form class="form-grid" @submit.prevent="saveStoreEmployee">
              <input v-model="forms.user.fullName" placeholder="Nome completo" required/>
              <input v-model="forms.user.username" placeholder="E-mail" required type="email"/>
              <input v-if="!forms.user.id" v-model="forms.user.password" minlength="6" placeholder="Senha inicial" required type="password"/>
              <select v-model="forms.user.employeeSubRole">
                <option value="ATTENDANT">Atendente</option>
                <option value="UNSPECIFIED">Funcionário sem especificação</option>
              </select>
              <label class="check-row">
                <input v-model="forms.user.active" type="checkbox"/>
                <span>Funcionário ativo</span>
              </label>
              <div class="permission-grid">
                <label v-for="permission in permissionDefinitions" :key="permission.id">
                  <input
                      :checked="forms.user.permissions.includes(permission.id)"
                      type="checkbox"
                      @change="toggleUserPermission(permission.id)"
                  />
                  <span>{{ permission.label }}</span>
                </label>
              </div>
              <button :disabled="saving" class="primary-button" type="submit">
                <UserPlus :size="18"/>
                <span>{{ forms.user.id ? 'Salvar funcionário' : 'Criar funcionário' }}</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Equipe da loja</h2>
              <span>{{ storeEmployees.length }} pessoas cadastradas</span>
            </div>
            <div class="data-table">
              <div class="data-table-header employee-grid">
                <span>Funcionário</span>
                <span>Subrole</span>
                <span>Permissões</span>
                <span>Status</span>
                <span>Ação</span>
              </div>
              <article
                  v-for="employee in storeEmployees"
                  :key="employee.id"
                  class="data-table-row employee-grid clickable-row"
                  @click="openRecord('Funcionário da loja', employee)"
              >
                <strong>{{ employee.fullName }}<small>{{ employee.username }}</small></strong>
                <span>{{ employee.profileType === 'PARTS_STORE_ADMIN' ? 'Admin' : employeeSubRoleLabels[employee.employeeSubRole] }}</span>
                <span>{{ employee.permissions?.length || 0 }} permissões</span>
                <span class="badge">{{ employee.active ? 'Ativo' : 'Inativo' }}</span>
                <button class="secondary-button compact-action" type="button" @click.stop="editUser(employee)">Editar</button>
              </article>
            </div>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Métricas por funcionário</h2>
              <span>Vendas e conversão da equipe comercial</span>
            </div>
            <div class="employee-metrics-grid">
              <article v-for="employee in storeEmployees" :key="`store-metrics-${employee.id}`" class="employee-card">
                <div>
                  <strong>{{ employee.fullName }}</strong>
                  <span>{{ employee.profileType === 'PARTS_STORE_ADMIN' ? 'Admin' : employeeSubRoleLabels[employee.employeeSubRole] }}</span>
                </div>
                <dl>
                  <template v-for="metric in storeEmployeeMetrics(employee)" :key="metric.label">
                    <dt>{{ metric.label }}</dt>
                    <dd>{{ metric.value }}</dd>
                  </template>
                </dl>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'store-quotes' && isPartsStoreProfile" class="screen-stack">
          <section class="order-flow-stats">
            <article>
              <strong>{{ storePendingQuotes.length }}</strong>
              <span>Orçamentos pendentes</span>
            </article>
            <article>
              <strong>{{ storeQuotes.filter((quote) => quote.status === 'SENT').length }}</strong>
              <span>Carrinhos enviados</span>
            </article>
            <article>
              <strong>{{ storeSales.length }}</strong>
              <span>Vendas aprovadas</span>
            </article>
            <article>
              <strong>{{ storeWaitingContact.length }}</strong>
              <span>Clientes aguardando contato</span>
            </article>
          </section>

          <section v-if="auth.role === 'ADMIN' || can('CREATE_BUDGET')" class="section-block">
            <div class="section-heading">
              <h2>{{ forms.storeQuote.id ? 'Editar carrinho' : 'Novo orçamento/carrinho' }}</h2>
              <span>Monte o carrinho do cliente e ajuste preços quando necessário</span>
            </div>
            <form class="form-grid" @submit.prevent="saveStoreQuote">
              <input v-model="forms.storeQuote.customerName" placeholder="Cliente" required/>
              <input v-model="forms.storeQuote.customerContact" placeholder="Contato do cliente"/>
              <select v-model="forms.storeQuote.status">
                <option v-for="(label, status) in storeQuoteStatusLabels" :key="status" :value="status">
                  {{ label }}
                </option>
              </select>
              <label class="check-row">
                <input v-model="forms.storeQuote.contactRequested" type="checkbox"/>
                <span>Cliente quer ser contatado</span>
              </label>
              <select v-model="forms.storeQuote.partId">
                <option value="">Selecione uma peça</option>
                <option v-for="part in data.parts" :key="part.id" :value="part.id">
                  {{ part.name }} - R$ {{ money(part.unitPrice) }} - {{ part.availableQuantity ?? part.stockQuantity }} un.
                </option>
              </select>
              <input v-model.number="forms.storeQuote.quantity" min="1" placeholder="Quantidade" type="number"/>
              <input v-model.number="forms.storeQuote.quotedPrice" min="0" placeholder="Preço negociado" step="0.01" type="number"/>
              <button class="secondary-button" type="button" @click="addStoreQuoteItem">
                <Plus :size="18"/>
                Adicionar peça
              </button>
              <div class="quote-items">
                <button
                    v-for="(item, index) in forms.storeQuote.items"
                    :key="`${item.partId}-${index}`"
                    type="button"
                    @click="removeStoreQuoteItem(index)"
                >
                  <strong>{{ item.quantity }}x {{ item.name }}</strong>
                  <span>R$ {{ money(item.quotedPrice) }}</span>
                </button>
              </div>
              <article class="selected-record">
                <strong>R$ {{ money(storeQuoteTotal(forms.storeQuote)) }}</strong>
                <span>Total do carrinho atual.</span>
              </article>
              <button :disabled="saving" class="primary-button" type="submit">
                <ShoppingCart :size="18"/>
                <span>{{ forms.storeQuote.id ? 'Salvar carrinho' : 'Criar carrinho' }}</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Carrinhos e orçamentos</h2>
              <span>{{ storeQuotes.length }} registros comerciais</span>
            </div>
            <div class="data-table">
              <div class="data-table-header store-quotes-grid">
                <span>Status</span>
                <span>Cliente</span>
                <span>Itens</span>
                <span>Total</span>
                <span>Ações</span>
              </div>
              <article
                  v-for="quote in storeQuotes"
                  :key="quote.id"
                  class="data-table-row store-quotes-grid clickable-row"
                  @click="openRecord('Carrinho da loja', quote)"
              >
                <span class="badge">{{ storeQuoteStatusLabels[quote.status] || quote.status }}</span>
                <span>{{ quote.customerName }}<small>{{ quote.customerContact }} · {{ quote.id }}</small></span>
                <span>
                  {{ quote.items.length }} peças
                  <small>{{ quote.contactRequested ? 'Aguardar contato' : 'Sem contato pendente' }}</small>
                </span>
                <strong>R$ {{ money(storeQuoteTotal(quote)) }}</strong>
                <div class="row-actions">
                  <button class="secondary-button compact-action" type="button" @click.stop="editStoreQuote(quote)">Editar</button>
                  <button v-if="quote.status === 'DRAFT'" class="secondary-button compact-action" type="button" @click.stop="updateStoreQuoteStatus(quote, 'SENT')">Enviar</button>
                  <button v-if="quote.status === 'SENT'" class="secondary-button compact-action" type="button" @click.stop="updateStoreQuoteStatus(quote, 'APPROVED')">Aprovar</button>
                  <button v-if="quote.status === 'SENT'" class="secondary-button compact-action" type="button" @click.stop="updateStoreQuoteStatus(quote, 'REFUSED')">Recusar</button>
                </div>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'master-customers' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Clientes</h2>
              <span>{{ listTotal('masterCustomers') }} clientes</span>
            </div>
            <div class="filters">
              <input v-model="pagination.masterCustomers.search" placeholder="Buscar cliente, documento ou parceiro" type="search" @input="resetListPage('masterCustomers')"/>
              <select v-model.number="pagination.masterCustomers.size" @change="resetListPage('masterCustomers')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.masterCustomers.sortBy">
                <option value="name">Ordenar por nome</option>
                <option value="spent">Ordenar por gasto</option>
                <option value="frequency">Ordenar por frequência</option>
                <option value="vehiclesCount">Ordenar por veículos</option>
              </select>
              <select v-model="pagination.masterCustomers.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header customers-grid">
                <span>Cliente</span>
                <span>Veículos</span>
                <span>Gasto total</span>
                <span>Interações</span>
              </div>
              <article
                  v-for="customer in listRows('masterCustomers')"
                  :key="customer.id"
                  class="data-table-row customers-grid clickable-row"
                  @click="openRecord('Cliente', customer)"
              >
                <strong>{{ customer.name }}<small>{{ customer.email }} · {{ customer.phone }}</small></strong>
                <span>{{ customer.vehiclesCount }}</span>
                <strong>R$ {{ money(customer.spent) }}</strong>
                <span>{{ customer.frequency }}<small>{{ customer.partners.join(', ') || 'Sem parceiro vinculado' }}</small></span>
              </article>
            </div>
            <div class="pager">
              <button :disabled="pagination.masterCustomers.page === 0" type="button" @click="changePage('masterCustomers', -1)">Anterior</button>
              <span>Página {{ pagination.masterCustomers.page + 1 }} de {{ listTotalPages('masterCustomers') }}</span>
              <button :disabled="pagination.masterCustomers.page + 1 >= listTotalPages('masterCustomers')" type="button" @click="changePage('masterCustomers', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'master-workshops' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Oficinas parceiras</h2>
              <span>{{ listTotal('masterWorkshops') }} oficinas</span>
            </div>
            <div class="filters">
              <input v-model="pagination.masterWorkshops.search" placeholder="Buscar oficina ou admin" type="search" @input="resetListPage('masterWorkshops')"/>
              <select v-model.number="pagination.masterWorkshops.size" @change="resetListPage('masterWorkshops')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.masterWorkshops.sortBy">
                <option value="name">Ordenar por nome</option>
                <option value="gross">Ordenar por faturamento</option>
                <option value="feeAmount">Ordenar por taxa</option>
              </select>
              <select v-model="pagination.masterWorkshops.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header partner-grid">
                <span>Oficina</span>
                <span>Faturamento</span>
                <span>Taxa</span>
                <span>AutoCare Hub</span>
                <span>Status</span>
              </div>
              <article
                  v-for="partner in listRows('masterWorkshops')"
                  :key="partner.id"
                  class="data-table-row partner-grid clickable-row"
                  @click="openRecord('Oficina parceira', partner)"
              >
                <strong>{{ partner.name }}<small>{{ partner.adminName }} · {{ partner.customersServed }} clientes · {{ partner.vehiclesServed }} veículos</small></strong>
                <span>R$ {{ money(partner.gross) }}<small>Líquido com taxa descontada R$ {{ money(partner.net) }}</small></span>
                <span>{{ partner.feeRateLabel }}<small>{{ partner.nextTierGap > 0 ? `Faltam R$ ${money(partner.nextTierGap)} para ${partner.nextTierLabel}` : 'Menor taxa ativa' }}</small></span>
                <strong>R$ {{ money(partner.feeAmount) }}</strong>
                <span class="badge">{{ partner.status }}</span>
              </article>
            </div>
            <div class="pager">
              <button :disabled="pagination.masterWorkshops.page === 0" type="button" @click="changePage('masterWorkshops', -1)">Anterior</button>
              <span>Página {{ pagination.masterWorkshops.page + 1 }} de {{ listTotalPages('masterWorkshops') }}</span>
              <button :disabled="pagination.masterWorkshops.page + 1 >= listTotalPages('masterWorkshops')" type="button" @click="changePage('masterWorkshops', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'master-stores' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Lojas de peças parceiras</h2>
              <span>Vendas, peças mais vendidas e taxa da plataforma</span>
            </div>
            <div class="data-table">
              <div class="data-table-header partner-grid">
                <span>Loja</span>
                <span>Faturamento</span>
                <span>Taxa</span>
                <span>AutoCare Hub</span>
                <span>Status</span>
              </div>
              <article
                  v-for="partner in storePartners"
                  :key="partner.id"
                  class="data-table-row partner-grid clickable-row"
                  @click="openRecord('Loja parceira', partner)"
              >
                <strong>{{ partner.name }}<small>{{ partner.salesCount }} vendas · {{ partner.topProducts }}</small></strong>
                <span>R$ {{ money(partner.gross) }}<small>Líquido com taxa descontada R$ {{ money(partner.net) }}</small></span>
                <span>{{ partner.feeRateLabel }}<small>{{ partner.nextTierGap > 0 ? `Faltam R$ ${money(partner.nextTierGap)} para ${partner.nextTierLabel}` : 'Menor taxa ativa' }}</small></span>
                <strong>R$ {{ money(partner.feeAmount) }}</strong>
                <span class="badge">{{ partner.status }}</span>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'master-leads' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Interessados e parceiros potenciais</h2>
              <span>Empresas que pediram demo ou contato</span>
            </div>
            <div class="data-table">
              <div class="data-table-header leads-grid">
                <span>Empresa</span>
                <span>Contato</span>
                <span>Tipo</span>
                <span>Data</span>
              </div>
              <article
                  v-for="lead in masterPotentialPartners"
                  :key="lead.id"
                  class="data-table-row leads-grid clickable-row"
                  @click="openRecord('Interessado', lead)"
              >
                <strong>{{ lead.companyName }}<small>{{ lead.cnpj }} · {{ lead.city || 'Cidade não informada' }}</small></strong>
                <span>{{ lead.contactName }}<small>{{ lead.email }} · {{ lead.phone }} · {{ lead.message || 'Sem mensagem' }}</small></span>
                <span class="badge">{{ lead.demoProfile === 'workshop' ? 'Oficina' : 'Loja de peças' }}</span>
                <span>{{ new Date(lead.createdAt).toLocaleDateString('pt-BR') }}</span>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'master-admins' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Cadastrar admin parceiro</h2>
              <span>Crie acesso inicial para oficina ou loja de peças</span>
            </div>
            <form class="form-grid" @submit.prevent="saveUser">
              <input v-model="forms.user.fullName" placeholder="Nome do admin" required/>
              <input v-model="forms.user.username" placeholder="E-mail" required type="email"/>
              <input v-if="!forms.user.id" v-model="forms.user.password" minlength="6" placeholder="Senha inicial" required type="password"/>
              <input v-model="forms.user.companyName" placeholder="Empresa vinculada" required/>
              <select v-model="forms.user.profileType" @change="forms.user.companyType = forms.user.profileType === 'WORKSHOP_ADMIN' ? 'WORKSHOP' : 'PARTS_STORE'; forms.user.role = 'ADMIN'">
                <option value="WORKSHOP_ADMIN">Admin de oficina</option>
                <option value="PARTS_STORE_ADMIN">Admin de loja de peças</option>
              </select>
              <select v-model="forms.user.companyType">
                <option value="WORKSHOP">Oficina</option>
                <option value="PARTS_STORE">Loja de peças</option>
              </select>
              <label class="check-row">
                <input v-model="forms.user.active" type="checkbox"/>
                <span>Admin ativo</span>
              </label>
              <div class="permission-grid">
                <label v-for="permission in permissionDefinitions" :key="`master-${permission.id}`">
                  <input
                      :checked="forms.user.permissions.includes(permission.id)"
                      type="checkbox"
                      @change="toggleUserPermission(permission.id)"
                  />
                  <span>{{ permission.label }}</span>
                </label>
              </div>
              <button
                  :disabled="saving"
                  class="primary-button"
                  type="submit"
                  @click="forms.user.role = 'ADMIN'; forms.user.employeeSubRole = ''; forms.user.permissions = forms.user.permissions.length ? forms.user.permissions : ['VIEW_BILLING','MANAGE_STOCK','CREATE_BUDGET','EDIT_EMPLOYEES','VIEW_STATS']"
              >
                <UserPlus :size="18"/>
                <span>{{ forms.user.id ? 'Salvar admin' : 'Criar admin parceiro' }}</span>
              </button>
            </form>
          </section>
        </section>

        <section v-if="activeTab === 'customer-partners' && isCustomerProfile" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Oficinas e lojas</h2>
              <span>Busque por nome, localização, especialidade ou produto</span>
            </div>
            <div class="filters">
              <input v-model="customerPartnerSearch" placeholder="Buscar parceiro" type="search"/>
            </div>
            <div class="customer-partner-grid">
              <article>
                <h3>Oficinas</h3>
                <button
                    v-for="workshop in filteredWorkshopDirectory"
                    :key="workshop.id"
                    type="button"
                    @click="openRecord('Oficina', workshop)"
                >
                  <Wrench :size="20"/>
                  <span>
                    <strong>{{ workshop.name }}</strong>
                    <small>{{ workshop.location }} · {{ workshop.specialty }}</small>
                  </span>
                  <b @click.stop="contactWorkshop(workshop)">Pedir orçamento</b>
                </button>
              </article>
              <article>
                <h3>Lojas de peças</h3>
                <button
                    v-for="store in filteredStoreDirectory"
                    :key="store.id"
                    type="button"
                    @click="openRecord('Loja de peças', store)"
                >
                  <Package :size="20"/>
                  <span>
                    <strong>{{ store.name }}</strong>
                    <small>{{ store.location }} · {{ store.specialty }}</small>
                  </span>
                  <b @click.stop="requestStoreQuote(store)">Solicitar orçamento</b>
                </button>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'customer-parts' && isCustomerProfile" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Buscar peças</h2>
              <span>Pesquise por nome, marca, categoria ou modelo do veículo</span>
            </div>
            <div class="filters">
              <input v-model="customerPartSearch" placeholder="Ex.: filtro, freio, civic, onix" type="search"/>
            </div>
            <div class="customer-part-grid">
              <button
                  v-for="part in filteredCustomerParts"
                  :key="part.id"
                  type="button"
                  @click="selectCustomerPart(part)"
              >
                <Package :size="20"/>
                <span>
                  <strong>{{ part.name }}</strong>
                  <small>{{ part.brand }} · {{ part.category }} · R$ {{ money(part.unitPrice) }}</small>
                </span>
                <b>{{ part.availableQuantity ?? part.stockQuantity }} un.</b>
              </button>
              <p v-if="!filteredCustomerParts.length" class="empty-state">Nenhuma peça encontrada.</p>
            </div>
          </section>

          <section v-if="selectedCustomerPart" class="section-block">
            <div class="section-heading">
              <h2>Comparar preço</h2>
              <span>{{ selectedCustomerPart.name }}</span>
            </div>
            <div class="data-table">
              <div class="data-table-header customer-price-grid">
                <span>Loja</span>
                <span>Preço</span>
                <span>Disponibilidade</span>
                <span>Ação</span>
              </div>
              <article
                  v-for="store in selectedPartStores"
                  :key="`${store.id}-${store.partId}`"
                  class="data-table-row customer-price-grid"
              >
                <strong>{{ store.name }}<small>{{ store.location }}</small></strong>
                <span>R$ {{ money(store.price) }}</span>
                <span class="badge">{{ store.availableQuantity > 0 ? `${store.availableQuantity} un.` : 'Indisponível' }}</span>
                <div class="row-actions">
                  <button class="secondary-button compact-action" type="button" @click="openRecord('Contato da loja', store)">Contato</button>
                  <button class="secondary-button compact-action" type="button" @click="addCustomerPartRequest(selectedCustomerPart, store)">Solicitar</button>
                </div>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'customer-cart' && isCustomerProfile" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Solicitação de orçamento</h2>
              <span>Envie peças ou detalhes do problema para um parceiro</span>
            </div>
            <form class="form-grid" @submit.prevent="sendCustomerQuoteRequest">
              <select v-model="forms.customerQuote.vehicleId">
                <option value="">Veículo relacionado</option>
                <option v-for="vehicle in customerVehicles" :key="vehicle.id" :value="vehicle.id">
                  {{ vehicle.plate }} - {{ vehicle.brand }} {{ vehicle.model }}
                </option>
              </select>
              <input v-model="forms.customerQuote.storeName" placeholder="Loja selecionada"/>
              <input v-model="forms.customerQuote.workshopName" placeholder="Oficina selecionada"/>
              <textarea v-model="forms.customerQuote.problemDescription" placeholder="Descreva o problema do veículo ou observações da compra"></textarea>
              <div class="quote-items">
                <button
                    v-for="(item, index) in forms.customerQuote.items"
                    :key="`${item.partId}-${index}`"
                    type="button"
                    @click="removeCustomerQuoteItem(index)"
                >
                  <strong>{{ item.quantity }}x {{ item.name }}</strong>
                  <span>{{ item.storeName || 'Loja a definir' }} · R$ {{ money(item.estimatedPrice) }}</span>
                </button>
              </div>
              <article class="selected-record">
                <strong>R$ {{ money(forms.customerQuote.items.reduce((total, item) => total + item.quantity * item.estimatedPrice, 0)) }}</strong>
                <span>Simulação de compra. O parceiro pode responder com outro valor.</span>
              </article>
              <button class="primary-button" type="submit">
                <ShoppingCart :size="18"/>
                <span>Enviar solicitação</span>
              </button>
            </form>
          </section>
        </section>

        <section v-if="activeTab === 'users'" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Minha conta</h2>
              <span>{{ currentUser?.username }}</span>
            </div>
            <form class="form-grid compact" @submit.prevent="saveAccount">
              <input v-model="forms.account.fullName" placeholder="Nome completo" required/>
              <button :disabled="saving" class="primary-button" type="submit">Salvar dados</button>
            </form>
            <form class="form-grid compact" @submit.prevent="changePassword">
              <input v-model="forms.password.currentPassword" placeholder="Senha atual" required type="password"/>
              <input v-model="forms.password.newPassword" minlength="6" placeholder="Nova senha" required type="password"/>
              <button :disabled="saving" class="secondary-button" type="submit">Alterar senha</button>
            </form>
          </section>

          <section v-if="auth.role === 'ADMIN'" class="section-block">
            <div class="section-heading">
              <h2>{{ forms.user.id ? 'Editar usuário' : 'Criar nova conta' }}</h2>
              <span>Perfis e permissões dos funcionários</span>
            </div>
            <form class="form-grid" @submit.prevent="saveUser">
              <input v-model="forms.user.fullName" placeholder="Nome completo" required/>
              <input v-model="forms.user.username" placeholder="E-mail de login" required type="email"/>
              <input v-if="!forms.user.id" v-model="forms.user.password" minlength="6" placeholder="Senha inicial" required type="password"/>
              <select v-model="forms.user.role">
                <option value="ADMIN">Administrador</option>
                <option value="EMPLOYEE">Funcionário</option>
                <option value="CUSTOMER">Cliente</option>
              </select>
              <select v-model="forms.user.profileType">
                <option value="MASTER_ADMIN">Admin Master</option>
                <option value="WORKSHOP_ADMIN">Admin de oficina</option>
                <option value="PARTS_STORE_ADMIN">Admin de loja de peças</option>
                <option value="WORKSHOP_EMPLOYEE">Funcionário de oficina</option>
                <option value="PARTS_STORE_EMPLOYEE">Funcionário de loja de peças</option>
                <option value="CUSTOMER_OWNER">Cliente</option>
              </select>
              <input v-model="forms.user.customerId" placeholder="ID do cliente, se for conta de cliente"/>
              <label class="check-row">
                <input v-model="forms.user.active" type="checkbox"/>
                <span>Usuário ativo</span>
              </label>
              <button :disabled="saving" class="primary-button" type="submit">
                <UserPlus :size="18"/>
                <span>{{ forms.user.id ? 'Salvar usuário' : 'Criar conta' }}</span>
              </button>
            </form>
          </section>

          <section v-if="auth.role === 'ADMIN'" class="section-block">
            <div class="section-heading">
              <h2>Usuários</h2>
              <span>{{ listTotal('users') }} contas</span>
            </div>
            <div class="filters">
              <input v-model="pagination.users.search" placeholder="Buscar usuário" type="search" @input="resetListPage('users')"/>
              <select v-model.number="pagination.users.size" @change="resetListPage('users')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.users.role" @change="resetListPage('users'); loadDashboard()">
                <option value="">Todos os perfis</option>
                <option value="ADMIN">Administradores</option>
                <option value="EMPLOYEE">Funcionários</option>
                <option value="CUSTOMER">Clientes</option>
              </select>
              <select v-model="pagination.users.sortBy">
                <option value="fullName">Ordenar por nome</option>
                <option value="username">Ordenar por e-mail</option>
                <option value="role">Ordenar por permissão</option>
              </select>
              <select v-model="pagination.users.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header users-grid">
                <span>Usuário</span>
                <span>Permissão</span>
                <span>Perfil</span>
                <span>Status</span>
                <span>Ação</span>
              </div>
              <article
                  v-for="user in listRows('users')"
                  :key="user.id"
                  class="data-table-row users-grid clickable-row"
                  @click="openRecord('Usuário', user)"
              >
                <strong>{{ user.fullName }}<small>{{ user.username }}</small></strong>
                <span>{{ user.role }}</span>
                <span>{{ user.profileType }}</span>
                <span class="badge">{{ user.active ? 'Ativo' : 'Inativo' }}</span>
                <button class="secondary-button compact-action" type="button" @click.stop="editUser(user)">Editar</button>
              </article>
            </div>
            <div class="pager">
              <button :disabled="pagination.users.page === 0" type="button" @click="changePage('users', -1)">Anterior</button>
              <span>Página {{ pagination.users.page + 1 }} de {{ listTotalPages('users') }}</span>
              <button :disabled="pagination.users.page + 1 >= listTotalPages('users')" type="button" @click="changePage('users', 1)">Próxima</button>
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
              <input v-model="forms.customer.name" placeholder="Nome" required/>
              <input v-model="forms.customer.document" placeholder="CPF/CNPJ somente números" required/>
              <input v-model="forms.customer.phone" placeholder="Telefone" required/>
              <input v-model="forms.customer.email" placeholder="E-mail" required type="email"/>
              <input v-model="forms.customer.address.street" placeholder="Rua" required/>
              <input v-model="forms.customer.address.number" placeholder="Número" required/>
              <input v-model="forms.customer.address.neighborhood" placeholder="Bairro" required/>
              <input v-model="forms.customer.address.city" placeholder="Cidade" required/>
              <input v-model="forms.customer.address.state" maxlength="2" placeholder="UF" required/>
              <input v-model="forms.customer.address.zipCode" placeholder="CEP" required/>
              <input v-model="forms.customer.address.complement" placeholder="Complemento"/>
              <label class="check-row">
                <input v-model="forms.customer.active" type="checkbox"/>
                <span>Cliente ativo</span>
              </label>
              <button :disabled="saving" class="primary-button" type="submit">
                <UserPlus :size="18"/>
                <span>{{ forms.customer.id ? 'Salvar cliente' : 'Cadastrar cliente' }}</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Clientes</h2>
              <span>{{ listTotal('customers') }} registros</span>
            </div>
            <div class="filters">
              <input v-model="pagination.customers.search" placeholder="Buscar cliente, e-mail ou documento" type="search" @input="resetListPage('customers')"/>
              <select v-model.number="pagination.customers.size" @change="resetListPage('customers')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.customers.active" @change="resetListPage('customers')">
                <option value="">Todos</option>
                <option value="true">Ativos</option>
                <option value="false">Inativos</option>
              </select>
              <select v-model="pagination.customers.sortBy">
                <option value="name">Ordenar por nome</option>
                <option value="email">Ordenar por e-mail</option>
                <option value="document">Ordenar por documento</option>
              </select>
              <select v-model="pagination.customers.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header customers-grid">
                <span>Cliente</span>
                <span>Contato</span>
                <span>Documento</span>
                <span>Status</span>
              </div>
              <article
                  v-for="customer in listRows('customers')"
                  :key="customer.id"
                  class="data-table-row customers-grid clickable-row"
                  @click="openRecord('Cliente', customer)"
              >
                <strong>{{ customer.name }}</strong>
                <span>{{ customer.email }}<small>{{ customer.phone }}</small></span>
                <code>{{ customer.document }}</code>
                <span class="badge"><CheckCircle2 :size="14"/> {{ customer.active ? 'Ativo' : 'Inativo' }}</span>
              </article>
            </div>
            <div class="pager">
              <button :disabled="pagination.customers.page === 0" type="button" @click="changePage('customers', -1)">Anterior</button>
              <span>Página {{ pagination.customers.page + 1 }} de {{ listTotalPages('customers') }}</span>
              <button :disabled="pagination.customers.page + 1 >= listTotalPages('customers')" type="button" @click="changePage('customers', 1)">Próxima</button>
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
              <input v-model="forms.vehicle.customerId" placeholder="ID do cliente" required/>
              <input v-model="forms.vehicle.plate" placeholder="Placa ABC1D23" required/>
              <input v-model="forms.vehicle.brand" placeholder="Marca" required/>
              <input v-model="forms.vehicle.model" placeholder="Modelo" required/>
              <input v-model.number="forms.vehicle.year" placeholder="Ano" required type="number"/>
              <input v-model.number="forms.vehicle.mileage" placeholder="Km" required type="number"/>
              <label class="check-row">
                <input v-model="forms.vehicle.active" type="checkbox"/>
                <span>Veículo ativo</span>
              </label>
              <button :disabled="saving" class="primary-button" type="submit">
                <Plus :size="18"/>
                <span>{{ forms.vehicle.id ? 'Salvar veículo' : 'Cadastrar veículo' }}</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Status dos veículos</h2>
              <span>{{ listTotal('vehicles') }} registros</span>
            </div>
            <div class="filters">
              <input v-model="pagination.vehicles.search" placeholder="Buscar placa, marca ou status" type="search" @input="resetListPage('vehicles')"/>
              <select v-model.number="pagination.vehicles.size" @change="resetListPage('vehicles')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.vehicles.active" @change="resetListPage('vehicles')">
                <option value="">Todos</option>
                <option value="true">Ativos</option>
                <option value="false">Inativos</option>
              </select>
              <select v-model="pagination.vehicles.sortBy">
                <option value="plate">Ordenar por placa</option>
                <option value="brand">Ordenar por marca</option>
                <option value="mileage">Ordenar por km</option>
                <option value="currentStatus">Ordenar por status</option>
              </select>
              <select v-model="pagination.vehicles.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header vehicles-grid">
                <span>Placa</span>
                <span>Veículo</span>
                <span>Km</span>
                <span>Status</span>
              </div>
              <article
                  v-for="vehicle in listRows('vehicles')"
                  :key="vehicle.id"
                  class="data-table-row vehicles-grid clickable-row"
                  @click="openRecord('Veículo', vehicle)"
              >
                <strong>{{ vehicle.plate }}</strong>
                <span>{{ vehicle.brand }} {{ vehicle.model }}<small>{{ vehicle.year }}</small></span>
                <span>{{ vehicle.mileage }} km</span>
                <span class="badge">{{ vehicle.currentStatus }}</span>
              </article>
            </div>
            <div class="pager">
              <button :disabled="pagination.vehicles.page === 0" type="button" @click="changePage('vehicles', -1)">Anterior</button>
              <span>Página {{ pagination.vehicles.page + 1 }} de {{ listTotalPages('vehicles') }}</span>
              <button :disabled="pagination.vehicles.page + 1 >= listTotalPages('vehicles')" type="button" @click="changePage('vehicles', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'parts'" class="screen-stack">
          <section class="order-flow-stats">
            <article>
              <strong>{{ data.parts.length }}</strong>
              <span>Peças cadastradas</span>
            </article>
            <article>
              <strong>{{ criticalParts.length }}</strong>
              <span>Peças críticas</span>
            </article>
            <article>
              <strong>{{ reservedParts.length }}</strong>
              <span>Peças com reserva</span>
            </article>
            <article>
              <strong>{{ data.lowStockParts.length }}</strong>
              <span>Avisos de compra</span>
            </article>
          </section>

          <section v-if="auth.role === 'ADMIN' || can('MANAGE_STOCK')" class="section-block">
            <div class="section-heading">
              <h2>{{ forms.part.id ? 'Editar peça' : 'Cadastro de peças' }}</h2>
              <span>Catálogo, custo, venda e reserva</span>
            </div>
            <form class="form-grid" @submit.prevent="createPart">
              <input v-model="forms.part.name" placeholder="Nome" required/>
              <input v-model="forms.part.description" placeholder="Descrição" required/>
              <input v-model="forms.part.sku" placeholder="SKU" required/>
              <input v-model="forms.part.category" placeholder="Categoria" required/>
              <input v-model="forms.part.subcategory" placeholder="Subcategoria"/>
              <input v-model="forms.part.brand" placeholder="Marca" required/>
              <input v-model.number="forms.part.costPrice" min="0" placeholder="Valor de custo" required
                     step="0.01" type="number"/>
              <input v-model.number="forms.part.unitPrice" min="0" placeholder="Valor de venda" required
                     step="0.01" type="number"/>
              <input v-model.number="forms.part.stockQuantity" min="0" placeholder="Estoque" required type="number"/>
              <input v-model.number="forms.part.minimumStock" min="0" placeholder="Estoque minimo" required
                     type="number"/>
              <input v-model.number="forms.part.reservationDays" min="1" placeholder="Dias de bloqueio" required
                     type="number"/>
              <button :disabled="saving" class="primary-button" type="submit">
                <Plus :size="18"/>
                <span>{{ forms.part.id ? 'Salvar peça' : 'Cadastrar peça' }}</span>
              </button>
            </form>
          </section>

          <section v-if="can('MANAGE_STOCK')" class="section-block">
            <div class="section-heading">
              <h2>Movimentar estoque</h2>
              <span>Entrada, saída ou venda avulsa</span>
            </div>
            <form class="form-grid" @submit.prevent="registerStockMovement">
              <select v-model="forms.stockMovement.partId" required>
                <option value="">Selecione uma peça</option>
                <option v-for="part in data.parts" :key="part.id" :value="part.id">
                  {{ part.name }} - {{ part.sku }}
                </option>
              </select>
              <select v-model="forms.stockMovement.type" required>
                <option value="ENTRY">Entrada de estoque</option>
                <option value="EXIT">Saída de estoque</option>
                <option value="SALE">Venda avulsa</option>
              </select>
              <input v-model.number="forms.stockMovement.quantity" min="1" placeholder="Quantidade" required
                     type="number"/>
              <input v-model.number="forms.stockMovement.unitCost" min="0" placeholder="Custo unitário"
                     step="0.01" type="number"/>
              <input v-model.number="forms.stockMovement.unitPrice" min="0" placeholder="Venda unitária"
                     step="0.01" type="number"/>
              <input v-model="forms.stockMovement.reason" placeholder="Motivo ou observação"/>
              <button :disabled="saving" class="primary-button" type="submit">
                <Package :size="18"/>
                <span>Registrar</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Acompanhamento de estoque</h2>
              <span>{{ criticalParts.length }} peças críticas</span>
            </div>
            <div class="filters">
              <input v-model="pagination.parts.search" placeholder="Buscar peça, SKU, categoria ou marca" type="search" @input="resetListPage('parts')"/>
              <select v-model.number="pagination.parts.size" @change="resetListPage('parts')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.parts.lowStock" @change="resetListPage('parts'); loadDashboard()">
                <option value="">Todos</option>
                <option value="true">Somente baixo estoque</option>
              </select>
              <select v-model="pagination.parts.active" @change="resetListPage('parts'); loadDashboard()">
                <option value="">Todos</option>
                <option value="true">Ativos</option>
                <option value="false">Inativos</option>
              </select>
              <select v-model="pagination.parts.sortBy">
                <option value="name">Ordenar por nome</option>
                <option value="stockQuantity">Ordenar por estoque</option>
                <option value="reservedQuantity">Ordenar por reserva</option>
                <option value="unitPrice">Ordenar por preço</option>
              </select>
              <select v-model="pagination.parts.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header parts-grid">
                <span>Peça</span>
                <span>Disponível</span>
                <span>Reservado</span>
                <span>Valores</span>
                <span>Status</span>
                <span>Ação</span>
              </div>
              <article
                  v-for="part in listRows('parts')"
                  :key="part.id"
                  :class="{ danger: (part.availableQuantity ?? part.stockQuantity) <= part.minimumStock }"
                  class="data-table-row parts-grid clickable-row"
                  @click="openRecord('Peça', part)"
              >
                <strong>
                  {{ part.name }}
                  <small>{{ part.sku }} · {{ part.category }} · {{ part.brand }}</small>
                </strong>
                <span>
                  {{ part.availableQuantity ?? part.stockQuantity }} un.
                  <small>Total {{ part.stockQuantity }} · Min. {{ part.minimumStock }}</small>
                </span>
                <span>
                  {{ part.reservedQuantity || 0 }} un.
                  <small>{{ part.reservationExpiresAt ? `Até ${new Date(part.reservationExpiresAt).toLocaleDateString('pt-BR')}` : `${part.reservationDays || 3} dias` }}</small>
                </span>
                <span>
                  Venda R$ {{ money(part.unitPrice) }}
                  <small>Custo R$ {{ money(part.costPrice) }}</small>
                </span>
                <span
                    :class="{ danger: ['LOW_STOCK', 'OUT_OF_STOCK'].includes(part.stockStatus) }"
                    class="badge"
                >
                  {{ stockStatusLabels[part.stockStatus] || part.stockStatus || 'Disponível' }}
                </span>
                <button v-if="auth.role === 'ADMIN'" class="secondary-button compact-action" type="button" @click.stop="editPart(part)">
                  Editar
                </button>
              </article>
            </div>
            <div class="pager">
              <button :disabled="pagination.parts.page === 0" type="button" @click="changePage('parts', -1)">Anterior</button>
              <span>Página {{ pagination.parts.page + 1 }} de {{ listTotalPages('parts') }}</span>
              <button :disabled="pagination.parts.page + 1 >= listTotalPages('parts')" type="button" @click="changePage('parts', 1)">Próxima</button>
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

          <section v-if="auth.role !== 'CUSTOMER' && can('CREATE_ORDER')" class="section-block">
            <div class="section-heading">
              <h2>Nova ordem de serviço</h2>
              <span>{{ orderSteps[forms.orderWizard.step] }}</span>
            </div>

            <div aria-label="Etapas da ordem" class="order-stepper">
              <button
                  v-for="(step, index) in orderSteps"
                  :key="step"
                  :class="{ active: forms.orderWizard.step === index, done: forms.orderWizard.step > index }"
                  type="button"
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
                  :class="{ active: forms.orderWizard.scenario === scenario.id }"
                  type="button"
                  @click="selectOrderScenario(scenario.id)"
              >
                <strong>{{ scenario.label }}</strong>
                <span>{{ scenario.text }}</span>
              </button>
            </div>

            <div v-if="forms.orderWizard.step === 1" class="wizard-panel">
              <template v-if="isNewCustomerScenario">
                <div class="form-grid">
                  <input v-model="forms.orderWizard.customer.name" placeholder="Nome do cliente" required/>
                  <input v-model="forms.orderWizard.customer.document" placeholder="CPF/CNPJ somente números" required/>
                  <input v-model="forms.orderWizard.customer.phone" placeholder="Telefone" required/>
                  <input v-model="forms.orderWizard.customer.email" placeholder="E-mail" required type="email"/>
                  <input v-model="forms.orderWizard.customer.address.street" placeholder="Rua" required/>
                  <input v-model="forms.orderWizard.customer.address.number" placeholder="Número" required/>
                  <input v-model="forms.orderWizard.customer.address.neighborhood" placeholder="Bairro" required/>
                  <input v-model="forms.orderWizard.customer.address.city" placeholder="Cidade" required/>
                  <input v-model="forms.orderWizard.customer.address.state" maxlength="2" placeholder="UF" required/>
                  <input v-model="forms.orderWizard.customer.address.zipCode" placeholder="CEP" required/>
                  <input v-model="forms.orderWizard.customer.address.complement" placeholder="Complemento"/>
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
                  <input v-model="forms.orderWizard.vehicle.plate" placeholder="Placa ABC1D23" required/>
                  <input v-model="forms.orderWizard.vehicle.brand" placeholder="Marca" required/>
                  <input v-model="forms.orderWizard.vehicle.model" placeholder="Modelo" required/>
                  <input v-model.number="forms.orderWizard.vehicle.year" placeholder="Ano" required type="number"/>
                  <input v-model.number="forms.orderWizard.vehicle.mileage" placeholder="Km" required type="number"/>
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
                <input v-model="forms.orderWizard.contactRequested" type="checkbox"/>
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
                <input v-model.number="forms.orderWizard.serviceQuantity" min="1" placeholder="Qtd. serviço"
                       type="number"/>
                <select v-model="forms.orderWizard.partId">
                  <option value="">Peça inicial prevista</option>
                  <option v-for="part in data.parts" :key="part.id" :value="part.id">
                    {{ part.name }} - R$ {{ money(part.unitPrice) }}
                  </option>
                </select>
                <input v-model.number="forms.orderWizard.partQuantity" min="1" placeholder="Qtd. peça" type="number"/>
                <textarea v-model="forms.orderWizard.initialValueNotes"
                          placeholder="Observações de valores iniciais, se houver"></textarea>
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
                <button :disabled="saving" class="secondary-button" type="button" @click="createOrderFromWizard(false)">
                  Salvar como orçamento pendente
                </button>
                <button :disabled="saving" class="primary-button" type="button" @click="createOrderFromWizard(true)">
                  <Plus :size="18"/>
                  <span>Salvar e criar orçamento agora</span>
                </button>
              </div>
            </div>

            <div class="wizard-actions">
              <button :disabled="forms.orderWizard.step === 0 || saving" class="secondary-button" type="button"
                      @click="previousOrderStep">
                Voltar
              </button>
              <button :disabled="forms.orderWizard.step === orderSteps.length - 1 || saving" class="secondary-button"
                      type="button" @click="nextOrderStep">
                Avançar
              </button>
            </div>
          </section>

          <section v-if="auth.role !== 'CUSTOMER' && (can('EDIT_ORDER') || can('CREATE_BUDGET'))" class="section-block">
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
              <button :disabled="saving" class="primary-button" type="submit">Atualizar status</button>
              <button :disabled="saving || !forms.orderAction.serviceOrderId" class="secondary-button" type="button"
                      @click="generateBudget">
                Gerar orçamento
              </button>
              <button :disabled="saving || !forms.orderAction.serviceOrderId" class="secondary-button" type="button"
                      @click="approveBudget">
                Aprovar orçamento
              </button>
            </form>
            <form class="form-grid compact" @submit.prevent="addServiceToOrder">
              <select v-model="forms.orderAction.serviceId" required>
                <option value="">Serviço</option>
                <option v-for="service in data.services" :key="service.id" :value="service.id">{{ service.name }}
                </option>
              </select>
              <input v-model.number="forms.orderAction.serviceQuantity" min="1" placeholder="Qtd." required
                     type="number"/>
              <button :disabled="saving || !forms.orderAction.serviceOrderId" class="secondary-button" type="submit">
                Adicionar serviço
              </button>
            </form>
            <form class="form-grid compact" @submit.prevent="addPartToOrder">
              <select v-model="forms.orderAction.partId" required>
                <option value="">Peça</option>
                <option v-for="part in data.parts" :key="part.id" :value="part.id">{{ part.name }}</option>
              </select>
              <input v-model.number="forms.orderAction.partQuantity" min="1" placeholder="Qtd." required type="number"/>
              <button :disabled="saving || !forms.orderAction.serviceOrderId" class="secondary-button" type="submit">
                Adicionar peça
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Ordens de serviço</h2>
              <span>{{ listTotal('serviceOrders') }} registros</span>
            </div>
            <div v-if="auth.role !== 'CUSTOMER'" class="filters">
              <input v-model="pagination.serviceOrders.search" placeholder="Buscar status, nota ou ID" type="search" @input="resetListPage('serviceOrders')"/>
              <select v-model.number="pagination.serviceOrders.size" @change="resetListPage('serviceOrders')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.serviceOrders.status" @change="resetListPage('serviceOrders'); loadDashboard()">
                <option value="">Todos os status</option>
                <option v-for="status in statuses" :key="status" :value="status">
                  {{ statusLabels[status] || status }}
                </option>
              </select>
              <select v-model="pagination.serviceOrders.sortBy">
                <option value="createdAt">Ordenar por data</option>
                <option value="status">Ordenar por status</option>
                <option value="totalAmount">Ordenar por total</option>
              </select>
              <select v-model="pagination.serviceOrders.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header orders-grid">
                <span>Status</span>
                <span>Nota</span>
                <span>Itens</span>
                <span>Total</span>
              </div>
              <article
                  v-for="order in listRows('serviceOrders')"
                  :key="order.id"
                  class="data-table-row orders-grid clickable-row"
                  @click="openRecord('Ordem de serviço', order)"
              >
                <span class="badge">{{ statusLabels[order.status] || order.status }}</span>
                <span>{{ order.diagnosticNotes }}<small>{{ order.id }}</small></span>
                <span>
                  {{ order.services?.length || 0 }} serviços
                  <small>{{ order.parts?.length || 0 }} peças</small>
                </span>
                <strong>R$ {{ money(order.totalAmount) }}</strong>
              </article>
              <p v-if="!listTotal('serviceOrders') && !loading" class="empty-state">Nenhuma ordem encontrada.</p>
            </div>
            <div v-if="auth.role !== 'CUSTOMER'" class="pager">
              <button :disabled="pagination.serviceOrders.page === 0" type="button" @click="changePage('serviceOrders', -1)">Anterior</button>
              <span>Página {{ pagination.serviceOrders.page + 1 }} de {{ listTotalPages('serviceOrders') }}</span>
              <button :disabled="pagination.serviceOrders.page + 1 >= listTotalPages('serviceOrders')" type="button" @click="changePage('serviceOrders', 1)">Próxima</button>
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
              <input v-model="forms.service.name" placeholder="Nome" required/>
              <input v-model.number="forms.service.basePrice" min="0" placeholder="Preço base" required step="0.01"
                     type="number"/>
              <input v-model.number="forms.service.estimatedTimeInMinutes" min="1" placeholder="Tempo em minutos"
                     required type="number"/>
              <textarea v-model="forms.service.description" placeholder="Descrição" required></textarea>
              <label class="check-row">
                <input v-model="forms.service.active" type="checkbox"/>
                <span>Serviço ativo</span>
              </label>
              <button :disabled="saving" class="primary-button" type="submit">
                <Plus :size="18"/>
                <span>{{ forms.service.id ? 'Salvar serviço' : 'Cadastrar serviço' }}</span>
              </button>
            </form>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Serviços cadastrados</h2>
              <span>{{ listTotal('services') }} registros</span>
            </div>
            <div class="filters">
              <input v-model="pagination.services.search" placeholder="Buscar serviço" type="search" @input="resetListPage('services')"/>
              <select v-model.number="pagination.services.size" @change="resetListPage('services')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.services.sortBy">
                <option value="name">Ordenar por nome</option>
                <option value="basePrice">Ordenar por preço</option>
                <option value="estimatedTimeInMinutes">Ordenar por prazo</option>
              </select>
              <select v-model="pagination.services.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header services-grid">
                <span>Serviço</span>
                <span>Prazo previsto</span>
                <span>Preço base</span>
              </div>
              <article
                  v-for="service in listRows('services')"
                  :key="service.id"
                  class="data-table-row services-grid clickable-row"
                  @click="openRecord('Serviço', service)"
              >
                <strong>{{ service.name }}<small>{{ service.description }}</small></strong>
                <span>{{ formatDuration(service.estimatedTimeInMinutes) }}</span>
                <span>R$ {{ money(service.basePrice) }}</span>
              </article>
            </div>
            <div class="pager">
              <button :disabled="pagination.services.page === 0" type="button" @click="changePage('services', -1)">Anterior</button>
              <span>Página {{ pagination.services.page + 1 }} de {{ listTotalPages('services') }}</span>
              <button :disabled="pagination.services.page + 1 >= listTotalPages('services')" type="button" @click="changePage('services', 1)">Próxima</button>
            </div>
          </section>
        </section>

        <div v-if="selectedRecord" class="detail-modal-backdrop" @click.self="closeRecord">
          <section class="detail-modal" role="dialog" aria-modal="true">
            <div class="detail-drawer-heading">
              <div>
                <span>{{ selectedRecordType }}</span>
                <h2>
                  {{ selectedRecord.name || selectedRecord.fullName || selectedRecord.plate || selectedRecord.status }}
                </h2>
              </div>
              <button class="icon-button" type="button" @click="closeRecord">
                <X :size="18"/>
              </button>
            </div>

            <form v-if="isCustomerDetail" class="modal-form" @submit.prevent="saveDetailModal">
              <input v-model="modalDraft.customer.name" placeholder="Nome"/>
              <input v-model="modalDraft.customer.document" placeholder="CPF/CNPJ"/>
              <input v-model="modalDraft.customer.phone" placeholder="Telefone"/>
              <input v-model="modalDraft.customer.email" placeholder="E-mail" type="email"/>
              <input v-model="modalDraft.customer.address.street" placeholder="Rua"/>
              <input v-model="modalDraft.customer.address.number" placeholder="Número"/>
              <input v-model="modalDraft.customer.address.neighborhood" placeholder="Bairro"/>
              <input v-model="modalDraft.customer.address.city" placeholder="Cidade"/>
              <input v-model="modalDraft.customer.address.state" maxlength="2" placeholder="UF"/>
              <input v-model="modalDraft.customer.address.zipCode" placeholder="CEP"/>
              <input v-model="modalDraft.customer.address.complement" placeholder="Complemento"/>
              <label class="check-row">
                <input v-model="modalDraft.customer.active" type="checkbox"/>
                <span>Cliente ativo</span>
              </label>
              <button :disabled="!detailModalDirty || saving" class="primary-button modal-save" type="submit">
                Salvar alterações
              </button>
            </form>

            <form v-else-if="isWorkshopDetail" class="modal-form" @submit.prevent="saveDetailModal">
              <input v-model="modalDraft.partner.companyName" placeholder="Nome da oficina"/>
              <input v-model="modalDraft.partner.fullName" placeholder="Responsável"/>
              <label class="check-row">
                <input v-model="modalDraft.partner.active" type="checkbox"/>
                <span>Oficina ativa</span>
              </label>
              <div class="modal-readonly-grid">
                <span>Faturamento bruto<strong>R$ {{ money(selectedRecord.gross) }}</strong></span>
                <span>Taxa AutoCare Hub<strong>{{ selectedRecord.feeRateLabel || '-' }}</strong></span>
                <span>Valor líquido<strong>R$ {{ money(selectedRecord.net) }}</strong></span>
                <span>Status<strong>{{ selectedRecord.status || 'Ativa' }}</strong></span>
              </div>
              <button :disabled="!detailModalDirty || saving" class="primary-button modal-save" type="submit">
                Salvar alterações
              </button>
            </form>

            <dl v-else>
              <template v-for="(value, key) in selectedRecord" :key="key">
                <dt>{{ key }}</dt>
                <dd>{{ typeof value === 'object' ? JSON.stringify(value) : value }}</dd>
              </template>
            </dl>

            <div class="detail-actions">
              <button v-if="selectedRecordType === 'Veículo'" class="secondary-button" type="button" @click="editVehicle(selectedRecord)">
                Editar veículo
              </button>
              <button v-if="selectedRecordType === 'Peça'" class="secondary-button" type="button" @click="editPart(selectedRecord)">
                Editar peça
              </button>
              <button v-if="selectedRecordType === 'Serviço'" class="secondary-button" type="button" @click="editService(selectedRecord)">
                Editar serviço
              </button>
              <button v-if="selectedRecordType === 'Usuário'" class="secondary-button" type="button" @click="editUser(selectedRecord)">
                Editar usuário
              </button>
              <button v-if="selectedRecordType === 'Funcionário da loja'" class="secondary-button" type="button" @click="editUser(selectedRecord)">
                Editar funcionário
              </button>
              <button v-if="selectedRecordType === 'Carrinho da loja'" class="secondary-button" type="button" @click="editStoreQuote(selectedRecord)">
                Editar carrinho
              </button>
              <button v-if="selectedRecordType === 'Peça para comparar'" class="secondary-button" type="button" @click="addCustomerPartRequest(selectedRecord)">
                Adicionar à solicitação
              </button>
              <button v-if="selectedRecordType === 'Loja de peças'" class="secondary-button" type="button" @click="requestStoreQuote(selectedRecord)">
                Solicitar orçamento
              </button>
              <button v-if="selectedRecordType === 'Oficina'" class="secondary-button" type="button" @click="contactWorkshop(selectedRecord)">
                Contatar oficina
              </button>
            </div>
          </section>
        </div>

        <div v-if="loading" class="loading-bar">Carregando dados...</div>
      </section>
    </div>
  </main>
</template>
