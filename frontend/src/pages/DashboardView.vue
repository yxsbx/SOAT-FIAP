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
import AppModal from '@/components/AppModal.vue';
import PaginationControl from '@/components/PaginationControl.vue';
import StatusBadge from '@/components/StatusBadge.vue';
import ToastAlert from '@/components/ToastAlert.vue';

const router = useRouter();
const auth = useAuthStore();
const loading = ref(false);
const saving = ref(false);
const error = ref('');
const success = ref('');
let toastTimer = null;
const activeTab = ref('overview');
const mobileMenuOpen = ref(false);
const sidebarCollapsed = ref(true);
const profileMenuOpen = ref(false);
const globalSearch = ref('');
const employeeMetricSearch = ref('');
const homeSettingsOpen = ref(false);
const userModalOpen = ref(false);
const customerModalOpen = ref(false);
const vehicleModalOpen = ref(false);
const partModalOpen = ref(false);
const serviceModalOpen = ref(false);
const storeQuoteModalOpen = ref(false);
const orderModalOpen = ref(false);
const confirmDialogOpen = ref(false);
const confirmDialog = reactive({
  title: '',
  message: '',
  confirmLabel: 'Confirmar',
  cancelLabel: 'Cancelar',
  tone: 'default',
  onConfirm: null,
});
const selectedRecord = ref(null);
const selectedRecordType = ref('');
const userFormInitial = ref('');
const accountInitial = ref('');
const modalDraft = reactive({
  customer: {},
  vehicle: {
    customerId: '',
    plate: '',
    brand: '',
    model: '',
    year: new Date().getFullYear(),
    mileage: 0,
    active: true,
  },
  partner: {},
  order: {
    diagnosticNotes: '',
    status: 'RECEIVED',
  },
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

const roleDisplayLabels = {
  ADMIN: 'Administrador',
  EMPLOYEE: 'Funcionário',
  CUSTOMER: 'Cliente',
};

const profileTypeLabels = {
  MASTER_ADMIN: 'Admin Master',
  WORKSHOP_ADMIN: 'Admin de oficina',
  PARTS_STORE_ADMIN: 'Admin de loja de peças',
  WORKSHOP_EMPLOYEE: 'Funcionário de oficina',
  PARTS_STORE_EMPLOYEE: 'Funcionário de loja de peças',
  CUSTOMER_OWNER: 'Cliente final',
};

const companyTypeLabels = {
  WORKSHOP: 'Oficina',
  PARTS_STORE: 'Loja de peças',
};

function roleDisplayLabel(role) {
  return roleDisplayLabels[role] || role || '-';
}

function profileTypeLabel(profileType) {
  return profileTypeLabels[profileType] || profileType || '-';
}

function companyTypeLabel(companyType) {
  return companyTypeLabels[companyType] || companyType || 'Sem empresa vinculada';
}

const detailFieldLabels = {
  fullName: 'Nome',
  username: 'E-mail',
  role: 'Permissão',
  profileType: 'Perfil',
  companyName: 'Empresa',
  companyType: 'Tipo de empresa',
  employeeSubRole: 'Função',
  active: 'Status',
  name: 'Nome',
  email: 'E-mail',
  phone: 'Telefone',
  document: 'Documento',
  status: 'Status',
  totalAmount: 'Valor total',
};

const hiddenDetailFields = new Set([
  'id',
  'customerId',
  'vehicleId',
  'password',
  'passwordHash',
  'createdBy',
  'updatedBy',
]);

function displayRecordValue(key, value) {
  if (key === 'role') {
    return roleDisplayLabel(value);
  }
  if (key === 'profileType') {
    return profileTypeLabel(value);
  }
  if (key === 'companyType') {
    return companyTypeLabel(value);
  }
  if (key === 'employeeSubRole') {
    return employeeSubRoleLabels[value] || value;
  }
  if (key === 'active') {
    return value === false ? 'Inativo' : 'Ativo';
  }
  if (key === 'status') {
    return statusLabels[value] || value;
  }
  if (key === 'totalAmount') {
    return `R$ ${money(value)}`;
  }
  if (Array.isArray(value)) {
    return value.length ? `${value.length} registro(s)` : '';
  }
  if (typeof value === 'object') {
    return '';
  }
  return value;
}

function displayRecordEntries(record) {
  return Object.entries(record || {})
    .filter(([key, value]) => !hiddenDetailFields.has(key) && value !== null && value !== undefined && value !== '')
    .map(([key, value]) => ({
      key,
      label: detailFieldLabels[key] || key,
      value: displayRecordValue(key, value),
    }))
    .filter((entry) => entry.value !== '');
}

const homePreferences = reactive({
  userWidgets: [...defaultHomeWidgetIds],
  globalWidgets: [...defaultHomeWidgetIds],
  showAlertsOnHome: false,
});
const homePreferenceDraft = reactive({
  userWidgets: [...defaultHomeWidgetIds],
  globalWidgets: [...defaultHomeWidgetIds],
  showAlertsOnHome: false,
});
const homePreferencesSaving = ref(false);

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

const orderSteps = ['Cenário', 'Cliente', 'Veículo', 'Defeitos', 'Valores', 'Finalização'];

const data = reactive({
  users: [],
  companies: [],
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
  masterStores: {page: 0, size: 10, search: '', sortBy: 'name', sortDir: 'asc'},
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
    costPrice: '0,00',
    unitPrice: '0,00',
    stockQuantity: 0,
    originalStockQuantity: 0,
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
    password: '',
    role: 'EMPLOYEE',
    profileType: 'WORKSHOP_EMPLOYEE',
    companyId: '',
    companyName: '',
    companyType: '',
    createCompany: false,
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
    discountPercent: 0,
    items: [],
  },
});

const demoProfile = computed(() => {
  const profiles = {
    'master@autocarehub.com': {
      label: 'Admin Master',
      tabs: [
        'overview',
        'master-customers',
        'master-workshops',
        'master-stores',
        'master-leads',
        'master-admins',
        'users',
      ],
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

const isWorkshopAdmin = computed(() => auth.role === 'ADMIN' && currentUser.value?.profileType === 'WORKSHOP_ADMIN');

const isMasterAdmin = computed(() => auth.role === 'ADMIN' && currentUser.value?.profileType === 'MASTER_ADMIN');

const isPartsStoreAdmin = computed(
  () => auth.role === 'ADMIN' && currentUser.value?.profileType === 'PARTS_STORE_ADMIN'
);

const isPartsStoreProfile = computed(() =>
  ['PARTS_STORE_ADMIN', 'PARTS_STORE_EMPLOYEE'].includes(currentUser.value?.profileType)
);

const isCustomerProfile = computed(
  () => auth.role === 'CUSTOMER' || currentUser.value?.profileType === 'CUSTOMER_OWNER'
);

const userPermissions = computed(() => currentUser.value?.permissions || []);

const userProfileOptions = computed(() => {
  if (isMasterAdmin.value) {
    return [
      {value: 'MASTER_ADMIN', label: 'Admin Master'},
      {value: 'WORKSHOP_ADMIN', label: 'Admin de oficina'},
      {value: 'PARTS_STORE_ADMIN', label: 'Admin de loja de peças'},
      {value: 'WORKSHOP_EMPLOYEE', label: 'Funcionário de oficina'},
      {value: 'PARTS_STORE_EMPLOYEE', label: 'Funcionário de loja de peças'},
      {value: 'CUSTOMER_OWNER', label: 'Cliente final'},
    ];
  }
  if (isPartsStoreAdmin.value) {
    return [{value: 'PARTS_STORE_EMPLOYEE', label: 'Funcionário de loja de peças'}];
  }
  return [{value: 'WORKSHOP_EMPLOYEE', label: 'Funcionário de oficina'}];
});

const canEditUserCompanyFields = computed(() => isMasterAdmin.value);

const companyOptions = computed(() =>
  data.companies.filter((company) => {
    if (forms.user.profileType === 'WORKSHOP_ADMIN' || forms.user.profileType === 'WORKSHOP_EMPLOYEE') {
      return company.type === 'WORKSHOP';
    }
    if (forms.user.profileType === 'PARTS_STORE_ADMIN' || forms.user.profileType === 'PARTS_STORE_EMPLOYEE') {
      return company.type === 'PARTS_STORE';
    }
    if (forms.user.profileType === 'MASTER_ADMIN') {
      return company.type === 'PLATFORM';
    }
    return true;
  })
);

function selectedUserCompany() {
  return data.companies.find((company) => company.id === forms.user.companyId);
}

function selectUserCompany() {
  const company = selectedUserCompany();
  if (!company) {
    return;
  }
  forms.user.companyName = company.name;
  forms.user.companyType = company.type;
}

function ensureUserCompanySelection() {
  if (!isMasterAdmin.value || forms.user.createCompany || forms.user.profileType === 'CUSTOMER_OWNER') {
    return;
  }
  const currentSelection = selectedUserCompany();
  if (currentSelection && companyOptions.value.some((company) => company.id === currentSelection.id)) {
    selectUserCompany();
    return;
  }
  const firstCompany = companyOptions.value[0];
  forms.user.companyId = firstCompany?.id || '';
  forms.user.companyName = firstCompany?.name || '';
}

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
      description: 'Receita, taxa e faixas',
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
      description: 'Interessados e parceiros potenciais',
      icon: TrendingUp,
      roles: ['ADMIN'],
      masterOnly: true,
    },
    {
      id: 'master-admins',
      label: 'Administradores parceiros',
      description: 'Cadastrar administradores de oficina e loja',
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
      label: 'Contas',
      description: 'Contas, perfis e permissões',
      icon: UserCog,
      roles: ['ADMIN'],
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
    return (
      allowedByRole &&
      allowedByProfile &&
      allowedByAdminType &&
      allowedByStoreAdminType &&
      allowedByStoreType &&
      allowedByMasterType &&
      allowedByCustomerType
    );
  });
});

const availableTabIds = computed(() => new Set(availableTabs.value.map((tab) => tab.id)));

const accountDirty = computed(() => (forms.account.fullName || '') !== (accountInitial.value || ''));

const passwordDirty = computed(() => Boolean(forms.password.currentPassword || forms.password.newPassword));

function comparableUserForm() {
  return JSON.stringify({
    id: forms.user.id || '',
    fullName: forms.user.fullName || '',
    username: forms.user.username || '',
    password: forms.user.password || '',
    role: forms.user.role || '',
    profileType: forms.user.profileType || '',
    companyId: forms.user.companyId || '',
    companyName: forms.user.companyName || '',
    companyType: forms.user.companyType || '',
    createCompany: Boolean(forms.user.createCompany),
    employeeSubRole: forms.user.employeeSubRole || '',
    permissions: [...(forms.user.permissions || [])].sort(),
    customerId: forms.user.customerId || '',
    active: forms.user.active !== false,
  });
}

const userFormDirty = computed(() => comparableUserForm() !== userFormInitial.value);

const homePreferenceDirty = computed(
  () =>
    JSON.stringify({
      userWidgets: [...homePreferences.userWidgets].sort(),
      globalWidgets: [...homePreferences.globalWidgets].sort(),
      showAlertsOnHome: homePreferences.showAlertsOnHome,
    }) !==
    JSON.stringify({
      userWidgets: [...homePreferenceDraft.userWidgets].sort(),
      globalWidgets: [...homePreferenceDraft.globalWidgets].sort(),
      showAlertsOnHome: homePreferenceDraft.showAlertsOnHome,
    })
);

const userInitials = computed(() => {
  const fallback = currentUser.value?.fullName || auth.user?.username || 'Usuário AutoCare';
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

const orderCountByStatus = (status) => data.serviceOrders.filter((order) => order.status === status).length;

const ordersWaitingContact = computed(
  () => data.serviceOrders.filter((order) => normalize(order.diagnosticNotes).includes('contato')).length
);

const orderFlowStats = computed(() => [
  {
    label: 'Clientes ativos',
    value: data.customers.length,
  },
  {
    label: 'Veículos em andamento',
    value: data.serviceOrders.filter((order) => !['FINISHED', 'DELIVERED'].includes(order.status)).length,
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
  data.customers.find((customer) => customer.id === forms.orderWizard.customerId)
);

const orderCustomerVehicles = computed(() =>
  data.vehicles.filter((vehicle) => vehicle.customerId === forms.orderWizard.customerId)
);

const selectedOrderVehicle = computed(() =>
  data.vehicles.find((vehicle) => vehicle.id === forms.orderWizard.vehicleId)
);

const selectedOrderService = computed(() =>
  data.services.find((service) => service.id === forms.orderWizard.serviceId)
);

const selectedOrderPart = computed(() => data.parts.find((part) => part.id === forms.orderWizard.partId));

const orderWizardDirty = computed(
  () =>
    forms.orderWizard.step > 0 ||
    Boolean(
      forms.orderWizard.customerId ||
      forms.orderWizard.vehicleId ||
      forms.orderWizard.defects ||
      forms.orderWizard.initialValueNotes ||
      forms.orderWizard.serviceId ||
      forms.orderWizard.partId ||
      forms.orderWizard.customer.name ||
      forms.orderWizard.vehicle.plate
    )
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

function orderVehicle(order) {
  return order.vehicle || data.vehicles.find((vehicle) => vehicle.id === order.vehicleId) || {};
}

function orderCustomer(order) {
  const vehicle = orderVehicle(order);
  return (
    order.customer ||
    data.customers.find((customer) => customer.id === order.customerId) ||
    data.customers.find((customer) => customer.id === vehicle.customerId) ||
    data.customers.find((customer) => customer.document === order.customerDocument) ||
    {}
  );
}

function orderCustomerName(order) {
  return orderCustomer(order).name || order.customerName || 'Cliente não informado';
}

function orderVehicleLabel(order) {
  const vehicle = orderVehicle(order);
  const brandModel = [vehicle.brand, vehicle.model].filter(Boolean).join(' ');
  return brandModel || order.vehicleDescription || 'Veículo não informado';
}

function orderPlate(order) {
  return orderVehicle(order).plate || order.plate || '-';
}

function orderDate(order) {
  const value = order.createdAt || order.updatedAt || order.budgetGeneratedAt || order.approvedAt;
  if (!value) {
    return '-';
  }
  return new Date(value).toLocaleDateString('pt-BR');
}

function orderBudgetStatus(order) {
  if (order.approvedAt || ['IN_PROGRESS', 'FINISHED', 'DELIVERED'].includes(order.status)) {
    return 'Orçamento aprovado';
  }
  if (order.budgetGeneratedAt || order.status === 'WAITING_APPROVAL') {
    return 'Aguardando aprovação';
  }
  return 'Orçamento pendente';
}

function onlyDigits(value) {
  return String(value || '').replace(/\D/g, '');
}

function hasSameDigits(value) {
  return value.split('').every((digit) => digit === value[0]);
}

function cpfCheckDigit(value, length) {
  const sum = value
    .slice(0, length)
    .split('')
    .reduce((total, digit, index) => total + Number(digit) * (length + 1 - index), 0);
  const remainder = sum % 11;
  return remainder < 2 ? 0 : 11 - remainder;
}

function cnpjCheckDigit(value, weights) {
  const sum = weights.reduce((total, weight, index) => total + Number(value[index]) * weight, 0);
  const remainder = sum % 11;
  return remainder < 2 ? 0 : 11 - remainder;
}

function isValidCpf(value) {
  const document = onlyDigits(value);
  return (
    document.length === 11 &&
    !hasSameDigits(document) &&
    cpfCheckDigit(document, 9) === Number(document[9]) &&
    cpfCheckDigit(document, 10) === Number(document[10])
  );
}

function isValidCnpj(value) {
  const document = onlyDigits(value);
  const firstWeights = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
  const secondWeights = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
  return (
    document.length === 14 &&
    !hasSameDigits(document) &&
    cnpjCheckDigit(document, firstWeights) === Number(document[12]) &&
    cnpjCheckDigit(document, secondWeights) === Number(document[13])
  );
}

function isValidCustomerDocument(value) {
  return isValidCpf(value) || isValidCnpj(value);
}

function canGenerateBudget(order) {
  return Boolean(order?.id) && !order.budgetGeneratedAt && ['RECEIVED', 'IN_DIAGNOSIS'].includes(order.status);
}

function canApproveBudget(order) {
  return Boolean(order?.id) && !order.approvedAt && order.status === 'WAITING_APPROVAL';
}

const isNewCustomerScenario = computed(() => forms.orderWizard.scenario === 'new-customer');

const needsNewVehicle = computed(() =>
  ['new-customer', 'existing-customer-new-vehicle'].includes(forms.orderWizard.scenario)
);

const criticalParts = computed(() =>
  data.parts.filter((part) => (part.availableQuantity ?? part.stockQuantity) <= part.minimumStock)
);

const reservedParts = computed(() => data.parts.filter((part) => Number(part.reservedQuantity || 0) > 0));

const partDuplicateMatches = computed(() => {
  const query = normalize(forms.part.name);
  if (forms.part.id || query.length < 2) {
    return [];
  }
  return data.parts.filter((part) => normalize(`${part.name} ${part.sku} ${part.brand}`).includes(query)).slice(0, 5);
});

const serviceDuplicateMatches = computed(() => {
  const query = normalize(forms.service.name);
  if (forms.service.id || query.length < 2) {
    return [];
  }
  return data.services
    .filter((service) => normalize(`${service.name} ${service.description}`).includes(query))
    .slice(0, 5);
});

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
      ['RECEIVED', 'IN_DIAGNOSIS', 'WAITING_APPROVAL', 'IN_PROGRESS'].includes(order.status)
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
    value: data.serviceOrders.filter((order) => !['FINISHED', 'DELIVERED'].includes(order.status)).length,
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
        : homeWidgetDefinitions.value
  ).filter((widget) => widget.roles.includes(auth.role) && (!widget.tabId || availableTabIds.value.has(widget.tabId)))
);

const homeWidgets = computed(() =>
  availableHomeWidgetDefinitions.value.filter((widget) => visibleHomeWidgetIds.value.has(widget.id))
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
  const sentBudgets = orders.filter(
    (order) =>
      order.budgetGeneratedAt || ['WAITING_APPROVAL', 'IN_PROGRESS', 'FINISHED', 'DELIVERED'].includes(order.status)
  ).length;
  const approvedBudgets = orders.filter(
    (order) => order.approvedAt || ['IN_PROGRESS', 'FINISHED', 'DELIVERED'].includes(order.status)
  ).length;
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

const storeSales = computed(() => storeQuotes.value.filter((quote) => quote.status === 'APPROVED'));

const storePendingQuotes = computed(() =>
  storeQuotes.value.filter((quote) => ['DRAFT', 'SENT'].includes(quote.status))
);

const storeActiveOrders = computed(() => storeQuotes.value.filter((quote) => ['DRAFT', 'SENT'].includes(quote.status)));

const storeWaitingContact = computed(() =>
  storeQuotes.value.filter((quote) => quote.contactRequested && quote.status !== 'APPROVED')
);

const storeRevenueGross = computed(() => storeSales.value.reduce((total, quote) => total + storeQuoteTotal(quote), 0));

const storeBillingSummary = computed(() => {
  const gross = storeRevenueGross.value;
  const sentQuotes = storeQuotes.value.filter((quote) =>
    ['SENT', 'APPROVED', 'REFUSED', 'EXPIRED'].includes(quote.status)
  ).length;
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
  data.users.filter(
    (user) =>
      ['PARTS_STORE_EMPLOYEE', 'PARTS_STORE_ADMIN'].includes(user.profileType) &&
      (isMasterAdmin.value || user.companyName === currentUser.value?.companyName)
  )
);

const masterCustomers = computed(() =>
  data.customers.map((customer) => {
    const vehicles = data.vehicles.filter((vehicle) => vehicle.customerId === customer.id);
    const orders = data.serviceOrders.filter((order) => order.customerId === customer.id);
    const spent = orders.reduce((total, order) => total + Number(order.totalAmount || 0), 0);
    const partners = new Set([
      ...orders.map(() => 'Oficina Central AutoCare'),
      ...storeQuotes.value
        .filter((quote) => normalize(quote.customerName).includes(normalize(customer.name)))
        .map(() => 'Loja Peças Prime'),
    ]);
    return {
      ...customer,
      vehiclesCount: vehicles.length,
      spent,
      frequency: orders.length,
      partners: [...partners],
    };
  })
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

const masterPlatformWidgets = computed(() => homeWidgets.value.filter((widget) => widget.category === 'platform'));

const masterFinancialWidgets = computed(() => homeWidgets.value.filter((widget) => widget.category === 'finance'));

const masterTopPlatformRevenuePartners = computed(() =>
  [...workshopPartners.value, ...storePartners.value].sort((a, b) => b.feeAmount - a.feeAmount).slice(0, 5)
);

const masterFrequentCustomers = computed(() =>
  [...masterCustomers.value].sort((a, b) => b.frequency - a.frequency).slice(0, 5)
);

const masterTopSpenders = computed(() => [...masterCustomers.value].sort((a, b) => b.spent - a.spent).slice(0, 5));

const masterVehicleOwners = computed(() =>
  [...masterCustomers.value].sort((a, b) => b.vehiclesCount - a.vehiclesCount).slice(0, 5)
);

const masterPotentialPartners = computed(() =>
  data.demoLeads
    .map((lead) => ({
      ...lead,
      potential:
        lead.demoProfile === 'workshop' ? data.serviceOrders.length + 12 : data.parts.length + storeQuotes.value.length,
    }))
    .sort((a, b) => b.potential - a.potential)
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
  data.users.filter(
    (user) =>
      user.role === 'EMPLOYEE' &&
      user.profileType === 'WORKSHOP_EMPLOYEE' &&
      (isMasterAdmin.value || user.companyName === currentUser.value?.companyName)
  )
);

function storeEmployeeMetrics(user) {
  const employeeQuotes = storeQuotes.value.filter((quote) => quote.employeeId === user.id);
  const sent = employeeQuotes.filter((quote) =>
    ['SENT', 'APPROVED', 'REFUSED', 'EXPIRED'].includes(quote.status)
  ).length;
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
  const approved = data.serviceOrders.filter(
    (order) => order.approvedAt || ['IN_PROGRESS', 'FINISHED', 'DELIVERED'].includes(order.status)
  ).length;
  const sent =
    data.serviceOrders.filter((order) => order.budgetGeneratedAt || order.status === 'WAITING_APPROVAL').length ||
    approved;
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
    {
      label: 'Ordens em aberto',
      value: data.serviceOrders.filter((order) => !['FINISHED', 'DELIVERED'].includes(order.status)).length,
    },
    {label: 'Orçamentos enviados', value: sent},
    {label: 'Serviços concluídos', value: completed},
    {label: 'Permissões ativas', value: user.permissions?.length || 0},
  ];
}

function employeePerformanceScore(user) {
  return employeeMetrics(user).reduce((total, metric) => {
    const numericValue = Number(String(metric.value).replace(/[^\d]/g, '')) || 0;
    return total + numericValue;
  }, 0);
}

const employeeMetricCards = computed(() => {
  const query = normalize(employeeMetricSearch.value);
  return workshopEmployees.value
    .map((employee) => ({
      employee,
      metrics: employeeMetrics(employee),
      score: employeePerformanceScore(employee),
    }))
    .filter(
      ({employee}) =>
        !query || normalize(`${employee.fullName} ${employee.username} ${employee.employeeSubRole}`).includes(query)
    )
    .sort((first, second) => second.score - first.score);
});

const featuredWorkshopEmployees = computed(() =>
  workshopEmployees.value
    .map((employee) => ({
      employee,
      metrics: employeeMetrics(employee),
      score: employeePerformanceScore(employee),
    }))
    .sort((first, second) => second.score - first.score)
    .slice(0, 3)
);

const userModalIsWorkshopEmployee = computed(() => forms.user.profileType === 'WORKSHOP_EMPLOYEE');
const userModalIsStoreEmployee = computed(() => forms.user.profileType === 'PARTS_STORE_EMPLOYEE');
const userModalIsEmployee = computed(() => userModalIsWorkshopEmployee.value || userModalIsStoreEmployee.value);

const userModalEmployeeMetrics = computed(() => {
  if (!forms.user.id || !userModalIsEmployee.value) {
    return [];
  }
  if (userModalIsStoreEmployee.value) {
    const employee = storeEmployees.value.find((item) => item.id === forms.user.id);
    return employee ? storeEmployeeMetrics(employee) : [];
  }
  const employee = workshopEmployees.value.find((item) => item.id === forms.user.id);
  return employee ? employeeMetrics(employee) : [];
});

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
  })
);

function vehicleOwner(vehicle) {
  return data.customers.find((customer) => customer.id === vehicle.customerId) || {};
}

function vehicleStatusValue(vehicle) {
  if (vehicle.active === false) {
    return false;
  }
  return vehicle.currentStatus || 'ACTIVE';
}

function vehicleStatusLabel(vehicle) {
  if (vehicle.active === false) {
    return 'Inativo';
  }
  if (vehicle.currentStatus && vehicle.currentStatus !== 'SEM_ORDEM') {
    return statusLabels[vehicle.currentStatus] || vehicle.currentStatus;
  }
  return 'Ativo';
}

const customerVehicles = computed(() => vehiclesWithCurrentStatus.value);

const customerOrders = computed(() => data.serviceOrders);

const customerBudgetAlerts = computed(() =>
  customerOrders.value.filter((order) => order.status === 'WAITING_APPROVAL')
);

const customerFinishedAlerts = computed(() => customerOrders.value.filter((order) => order.status === 'DELIVERED'));

const customerReadyAlerts = computed(() => customerOrders.value.filter((order) => order.status === 'FINISHED'));

const customerRecentHistory = computed(() =>
  [...customerOrders.value].sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0)).slice(0, 5)
);

const workshopDirectory = computed(() =>
  data.users
    .filter((user) => user.profileType === 'WORKSHOP_ADMIN')
    .map((user) => ({
      ...user,
      name: user.companyName || user.fullName,
      location: 'São Paulo - SP',
      specialty: 'Diagnóstico, revisão, manutenção preventiva e orçamento de veículos',
    }))
);

const storeDirectory = computed(() =>
  data.users
    .filter((user) => user.profileType === 'PARTS_STORE_ADMIN')
    .map((user) => ({
      ...user,
      name: user.companyName || user.fullName,
      location: 'São Paulo - SP',
      specialty: 'Peças, filtros, freios, suspensão, óleo e acessórios',
    }))
);

const filteredWorkshopDirectory = computed(() =>
  workshopDirectory.value.filter((partner) =>
    smartMatch(customerPartnerSearch.value, [partner.name, partner.location, partner.specialty, partner.fullName])
  )
);

const filteredStoreDirectory = computed(() =>
  storeDirectory.value.filter((partner) =>
    smartMatch(customerPartnerSearch.value, [partner.name, partner.location, partner.specialty, partner.fullName])
  )
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
    smartMatch(customerPartSearch.value, [
      part.name,
      part.brand,
      part.category,
      part.subcategory,
      part.sku,
      vehicleTerms,
    ])
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
    availableQuantity: Math.max(
      0,
      Number(selectedCustomerPart.value.availableQuantity ?? selectedCustomerPart.value.stockQuantity) - index
    ),
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
  masterStores: storePartners.value,
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
  masterStores: ['name', 'adminName', 'status', 'topProducts'],
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
      if (resource === 'serviceOrders') {
        return smartMatch(config.search, [
          item.status,
          statusLabels[item.status],
          item.diagnosticNotes,
          item.id,
          orderCustomerName(item),
          orderVehicleLabel(item),
          orderPlate(item),
        ]);
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
    if (resource === 'serviceOrders') {
      return smartMatch(config.search, [
        item.status,
        statusLabels[item.status],
        item.diagnosticNotes,
        item.id,
        orderCustomerName(item),
        orderVehicleLabel(item),
        orderPlate(item),
      ]);
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
  return Number(value || 0).toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}

function mergeById(currentItems, incomingItems) {
  const itemsById = new Map();
  currentItems.filter(Boolean).forEach((item) => itemsById.set(item.id, item));
  incomingItems.filter(Boolean).forEach((item) => itemsById.set(item.id, item));
  return Array.from(itemsById.values());
}

async function enrichServiceOrderRelations() {
  const knownVehicleIds = new Set(data.vehicles.map((vehicle) => vehicle.id));
  const missingVehicleIds = [
    ...new Set(
      data.serviceOrders
        .map((order) => order.vehicleId)
        .filter((vehicleId) => vehicleId && !knownVehicleIds.has(vehicleId))
    ),
  ];

  if (missingVehicleIds.length) {
    const vehicleResults = await Promise.allSettled(missingVehicleIds.map((vehicleId) => resources.vehicle(vehicleId)));
    data.vehicles = mergeById(
      data.vehicles,
      vehicleResults
        .filter((result) => result.status === 'fulfilled')
        .map((result) => result.value)
    );
  }

  const knownCustomerIds = new Set(data.customers.map((customer) => customer.id));
  const vehicleCustomerIds = data.vehicles.map((vehicle) => vehicle.customerId);
  const orderCustomerIds = data.serviceOrders.map((order) => order.customerId);
  const currentCustomerIds = auth.customerId ? [auth.customerId] : [];
  const missingCustomerIds = [
    ...new Set([...orderCustomerIds, ...vehicleCustomerIds, ...currentCustomerIds].filter(Boolean)),
  ].filter((customerId) => !knownCustomerIds.has(customerId));

  if (!missingCustomerIds.length) {
    return;
  }

  const customerResults = await Promise.allSettled(
    missingCustomerIds.map((customerId) => resources.customer(customerId))
  );
  data.customers = mergeById(
    data.customers,
    customerResults
      .filter((result) => result.status === 'fulfilled')
      .map((result) => result.value)
  );
}

function customerQuoteSubtotal() {
  return forms.customerQuote.items.reduce((total, item) => total + item.quantity * item.estimatedPrice, 0);
}

function customerQuoteDiscountAmount() {
  const percent = Math.min(100, Math.max(0, Number(forms.customerQuote.discountPercent || 0)));
  return customerQuoteSubtotal() * (percent / 100);
}

function customerQuoteTotal() {
  return Math.max(0, customerQuoteSubtotal() - customerQuoteDiscountAmount());
}

function formatDecimalInput(value) {
  return money(value).replace('.', ',');
}

function normalizeDecimalInput(value) {
  return String(value || '0').replace(',', '.');
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
  if (toastTimer) {
    window.clearTimeout(toastTimer);
    toastTimer = null;
  }
}

function showToast(type, message) {
  error.value = '';
  success.value = '';
  if (toastTimer) {
    window.clearTimeout(toastTimer);
    toastTimer = null;
  }
  if (type === 'error') {
    error.value = message;
  } else {
    success.value = message;
  }
  toastTimer = window.setTimeout(() => {
    error.value = '';
    success.value = '';
    toastTimer = null;
  }, 3800);
}

function showSuccess(message) {
  showToast('success', message);
}

function showError(message) {
  showToast('error', message);
}

function userHomeKey() {
  return `autocare.home.${auth.user?.id || auth.user?.username || 'guest'}`;
}

function homeDefaultWidgetIds() {
  const username = auth.user?.username || '';
  if (isMasterAdmin.value || username.includes('master@')) {
    return defaultMasterHomeWidgetIds;
  }
  return username.includes('loja.') || isPartsStoreProfile.value ? defaultStoreHomeWidgetIds : defaultHomeWidgetIds;
}

function readStoredJson(key, fallback) {
  try {
    return JSON.parse(localStorage.getItem(key)) || fallback;
  } catch {
    return fallback;
  }
}

function validHomeWidgetIds() {
  return new Set(availableHomeWidgetDefinitions.value.map((widget) => widget.id));
}

function sanitizeHomeWidgets(widgetIds, fallback = []) {
  if (!Array.isArray(widgetIds)) {
    return [...fallback];
  }
  const validWidgetIds = validHomeWidgetIds();
  return widgetIds.filter((widgetId) => validWidgetIds.has(widgetId));
}

function syncHomePreferenceDraft() {
  homePreferenceDraft.userWidgets = [...homePreferences.userWidgets];
  homePreferenceDraft.globalWidgets = [...homePreferences.globalWidgets];
  homePreferenceDraft.showAlertsOnHome = homePreferences.showAlertsOnHome;
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

  homePreferences.globalWidgets = sanitizeHomeWidgets(globalConfig.widgets, defaults);
  homePreferences.showAlertsOnHome = Boolean(globalConfig.showAlertsOnHome);
  homePreferences.userWidgets = sanitizeHomeWidgets(userConfig.widgets, homePreferences.globalWidgets);

  try {
    const preference = await resources.homePreferences();
    homePreferences.userWidgets = sanitizeHomeWidgets(preference.widgets, defaults);
    homePreferences.showAlertsOnHome = Boolean(preference.showAlertsOnHome);
  } catch {
    homePreferences.userWidgets = sanitizeHomeWidgets(homePreferences.userWidgets, defaults);
  }
  syncHomePreferenceDraft();
}

function persistHomePreferencesLocally() {
  localStorage.setItem(userHomeKey(), JSON.stringify({widgets: homePreferences.userWidgets}));
  localStorage.setItem(
    'autocare.home.workshop.global',
    JSON.stringify({
      widgets: homePreferences.globalWidgets,
      showAlertsOnHome: homePreferences.showAlertsOnHome,
    })
  );
}

function buildCustomerPayload(customer) {
  return {
    name: customer.name,
    document: onlyDigits(customer.document),
    phone: onlyDigits(customer.phone),
    email: customer.email,
    address: {
      street: customer.address?.street || '',
      number: customer.address?.number || '',
      complement: customer.address?.complement || '',
      neighborhood: customer.address?.neighborhood || '',
      city: customer.address?.city || '',
      state: customer.address?.state || 'SP',
      zipCode: onlyDigits(customer.address?.zipCode || ''),
    },
    active: customer.active !== false,
  };
}

function toggleHomeSettings() {
  if (!homeSettingsOpen.value) {
    syncHomePreferenceDraft();
  }
  homeSettingsOpen.value = !homeSettingsOpen.value;
}

function toggleHomeWidget(widgetId, scope = 'user') {
  const target = scope === 'global' ? homePreferenceDraft.globalWidgets : homePreferenceDraft.userWidgets;
  const index = target.indexOf(widgetId);

  if (index >= 0) {
    target.splice(index, 1);
  } else if (index < 0) {
    target.push(widgetId);
  }
}

function toggleHomeAlerts() {
  homePreferenceDraft.showAlertsOnHome = !homePreferenceDraft.showAlertsOnHome;
}

function isHomeWidgetSelected(widgetId, scope = 'user') {
  return (scope === 'global' ? homePreferenceDraft.globalWidgets : homePreferenceDraft.userWidgets).includes(widgetId);
}

async function saveHomePreferenceDraft() {
  if (!homePreferenceDirty.value) {
    return;
  }

  const nextUserWidgets = sanitizeHomeWidgets(homePreferenceDraft.userWidgets);
  const nextGlobalWidgets = sanitizeHomeWidgets(homePreferenceDraft.globalWidgets);
  const nextShowAlertsOnHome = Boolean(homePreferenceDraft.showAlertsOnHome);

  homePreferencesSaving.value = true;
  try {
    await resources.saveHomePreferences({
      widgets: nextUserWidgets,
      showAlertsOnHome: nextShowAlertsOnHome,
    });
    homePreferences.userWidgets = [...nextUserWidgets];
    homePreferences.globalWidgets = [...nextGlobalWidgets];
    homePreferences.showAlertsOnHome = nextShowAlertsOnHome;
    persistHomePreferencesLocally();
    syncHomePreferenceDraft();
    homeSettingsOpen.value = false;
    showSuccess('Preferências da home salvas.');
  } catch (err) {
    showError(err.message || 'Não foi possível salvar as preferências da home.');
  } finally {
    homePreferencesSaving.value = false;
  }
}

async function loadDashboard(options = {}) {
  const {silent = false} = options;
  loading.value = true;
  if (!silent) {
    resetMessage();
  }

  try {
    if (auth.role === 'CUSTOMER' && auth.customerId) {
      const [serviceOrders, vehicles, user, parts, partners] = await Promise.allSettled([
        resources.customerServiceOrders(auth.customerId),
        resources.customerVehicles(auth.customerId),
        resources.currentUser(),
        resources.parts({active: true, size: API_MAX_PAGE_SIZE}),
        resources.partners(),
      ]);

      data.serviceOrders = serviceOrders.status === 'fulfilled' ? listItems(serviceOrders.value) : [];
      data.vehicles = vehicles.status === 'fulfilled' ? listItems(vehicles.value) : [];
      currentUser.value = user.status === 'fulfilled' ? user.value : currentUser.value;
      data.parts = parts.status === 'fulfilled' ? listItems(parts.value) : [];
      data.users = partners.status === 'fulfilled' ? listItems(partners.value) : [];
      await enrichServiceOrderRelations();
      if (currentUser.value) {
        forms.account.fullName = currentUser.value.fullName;
        accountInitial.value = currentUser.value.fullName || '';
      }

      const failed = [serviceOrders, vehicles, user, parts, partners].filter(
        (request) => request.status === 'rejected'
      );
      if (failed.length) {
        showError(failed.map((request) => request.reason.message).join(' | '));
      }
      return;
    }

    const requests = await Promise.allSettled([
      resources.currentUser(),
      auth.role === 'ADMIN'
        ? resources.users({
          active: pagination.users.active,
          role: pagination.users.role,
          profileType: pagination.users.profileType,
          search: pagination.users.search,
        })
        : Promise.resolve(null),
      auth.role !== 'CUSTOMER'
        ? resources.customers({
          active: pagination.customers.active,
          size: API_MAX_PAGE_SIZE,
        })
        : Promise.resolve(null),
      resources.vehicles({active: pagination.vehicles.active, size: API_MAX_PAGE_SIZE}),
      resources.services({active: pagination.services.active, size: API_MAX_PAGE_SIZE}),
      resources.parts({
        active: pagination.parts.active,
        lowStock: pagination.parts.lowStock,
        size: API_MAX_PAGE_SIZE,
      }),
      resources.lowStockParts({size: 20}),
      resources.serviceOrders({status: pagination.serviceOrders.status, size: API_MAX_PAGE_SIZE}),
      resources.averageExecutionTime(),
      auth.role === 'ADMIN' ? resources.demoLeads() : Promise.resolve([]),
      auth.role === 'ADMIN' ? resources.companies() : Promise.resolve([]),
    ]);

    const [user, users, customers, vehicles, services, parts, lowStockParts, serviceOrders, average, demoLeads, companies] =
      requests;

    currentUser.value = user.status === 'fulfilled' ? user.value : currentUser.value;
    if (currentUser.value) {
      forms.account.fullName = currentUser.value.fullName;
      accountInitial.value = currentUser.value.fullName || '';
    }
    data.users = users.status === 'fulfilled' && users.value ? listItems(users.value) : [];
    data.customers = customers.status === 'fulfilled' && customers.value ? listItems(customers.value) : [];
    data.vehicles = vehicles.status === 'fulfilled' ? listItems(vehicles.value) : [];
    data.services = services.status === 'fulfilled' ? listItems(services.value) : [];
    data.parts = parts.status === 'fulfilled' ? listItems(parts.value) : [];
    data.lowStockParts = lowStockParts.status === 'fulfilled' ? listItems(lowStockParts.value) : [];
    data.serviceOrders = serviceOrders.status === 'fulfilled' ? listItems(serviceOrders.value) : [];
    data.averageExecutionTime = average.status === 'fulfilled' ? average.value : null;
    data.demoLeads = demoLeads.status === 'fulfilled' ? demoLeads.value || [] : [];
    data.companies = companies.status === 'fulfilled' && companies.value ? listItems(companies.value) : [];
    await enrichServiceOrderRelations();
    ensureStoreQuotes();

    const failed = requests.filter((request) => {
      if (request.status !== 'rejected') {
        return false;
      }

      const isCustomersRequest = request.reason.path?.startsWith('/api/v1/customers');
      return request.reason.status !== 403 || (auth.role === 'ADMIN' && isCustomersRequest);
    });
    if (failed.length) {
      showError(failed.map((request) => request.reason.message).join(' | '));
    }
  } catch (err) {
    showError(err.message || 'Não foi possível carregar os dados.');
  } finally {
    loading.value = false;
  }
}

async function runAction(action, message) {
  saving.value = true;
  resetMessage();

  try {
    await action();
    await loadDashboard({silent: true});
    showSuccess(message);
  } catch (err) {
    showError(err.message || 'Não foi possível concluir a operação.');
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

function openOrderModal() {
  resetOrderWizard();
  orderModalOpen.value = true;
}

function closeOrderModal() {
  orderModalOpen.value = false;
  resetOrderWizard();
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

    if (!isNewCustomerScenario.value && !selectedOrderCustomer.value?.id) {
      throw new Error('Selecione um cliente para criar a ordem.');
    }

    if (!needsNewVehicle.value && !selectedOrderVehicle.value?.id) {
      throw new Error('Selecione um veículo para criar a ordem.');
    }

    const customer = isNewCustomerScenario.value
      ? forms.orderWizard.customer
      : await resources.customer(selectedOrderCustomer.value.id);
    const customerDocument = customer?.document;

    if (!isValidCustomerDocument(customerDocument)) {
      throw new Error('Informe um CPF ou CNPJ válido para criar a ordem.');
    }

    const order = await resources.createServiceOrder({
      customerDocument: onlyDigits(customerDocument),
      customer: isNewCustomerScenario.value
        ? {
          name: customer.name,
          phone: customer.phone,
          email: customer.email,
          address: customer.address,
        }
        : undefined,
      vehicleId: needsNewVehicle.value ? undefined : selectedOrderVehicle.value.id,
      vehicle: needsNewVehicle.value
        ? {
          ...forms.orderWizard.vehicle,
          year: Number(forms.orderWizard.vehicle.year),
          mileage: Number(forms.orderWizard.vehicle.mileage),
        }
        : undefined,
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
    } else {
      pagination.serviceOrders.status = 'RECEIVED';
    }

    orderModalOpen.value = false;
    resetOrderWizard();
    await loadDashboard({silent: true});
    showSuccess(createBudgetNow ? 'Ordem salva e orçamento gerado.' : 'Ordem salva como orçamento pendente.');
  } catch (err) {
    showError(err.message || 'Não foi possível criar a ordem.');
  } finally {
    saving.value = false;
  }
}

function createCustomer() {
  return runAction(
    async () => {
      const payload = buildCustomerPayload(forms.customer);
      if (forms.customer.id) {
        await resources.updateCustomer(forms.customer.id, payload);
      } else {
        await resources.createCustomer(payload);
      }
      customerModalOpen.value = false;
      resetCustomerForm();
    },
    forms.customer.id ? 'Cliente atualizado.' : 'Cliente cadastrado.'
  );
}

function createVehicle() {
  return runAction(
    async () => {
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
      vehicleModalOpen.value = false;
      resetVehicleForm();
    },
    forms.vehicle.id ? 'Veículo atualizado.' : 'Veículo cadastrado.'
  );
}

function resetCustomerForm() {
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
}

function resetVehicleForm() {
  forms.vehicle.id = '';
  forms.vehicle.customerId = '';
  forms.vehicle.plate = '';
  forms.vehicle.brand = '';
  forms.vehicle.model = '';
  forms.vehicle.year = new Date().getFullYear();
  forms.vehicle.mileage = 0;
  forms.vehicle.active = true;
}

function openCreateCustomerModal() {
  resetCustomerForm();
  customerModalOpen.value = true;
}

function closeCustomerModal() {
  customerModalOpen.value = false;
  resetCustomerForm();
}

function openCreateVehicleModal() {
  resetVehicleForm();
  vehicleModalOpen.value = true;
}

function closeVehicleModal() {
  vehicleModalOpen.value = false;
  resetVehicleForm();
}

function createPart() {
  const wasEditing = Boolean(forms.part.id);
  return runAction(
    async () => {
      const targetStockQuantity = Number(forms.part.stockQuantity);
      const currentStockQuantity = Number(forms.part.originalStockQuantity || 0);
      const payload = {
        name: forms.part.name,
        description: forms.part.description,
        sku: forms.part.sku,
        category: forms.part.category,
        subcategory: forms.part.subcategory,
        brand: forms.part.brand,
        costPrice: Number(normalizeDecimalInput(forms.part.costPrice)),
        unitPrice: Number(normalizeDecimalInput(forms.part.unitPrice)),
        stockQuantity: wasEditing ? currentStockQuantity : targetStockQuantity,
        minimumStock: Number(forms.part.minimumStock),
        active: forms.part.active !== false,
      };
      if (forms.part.id) {
        await resources.updatePart(forms.part.id, payload);
        if (targetStockQuantity !== currentStockQuantity) {
          await resources.updatePartStock(forms.part.id, targetStockQuantity);
        }
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
        costPrice: '0,00',
        unitPrice: '0,00',
        stockQuantity: 0,
        originalStockQuantity: 0,
        minimumStock: 1,
        active: true,
        reservationDays: 3,
      });
      partModalOpen.value = false;
    },
    wasEditing ? 'Peça atualizada.' : 'Peça cadastrada.'
  );
}

function resetPartForm() {
  Object.assign(forms.part, {
    id: '',
    name: '',
    description: '',
    sku: '',
    category: '',
    subcategory: '',
    brand: '',
    costPrice: '0,00',
    unitPrice: '0,00',
    stockQuantity: 0,
    originalStockQuantity: 0,
    minimumStock: 1,
    active: true,
    reservationDays: 3,
  });
}

function openCreatePartModal() {
  resetPartForm();
  partModalOpen.value = true;
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
    costPrice: formatDecimalInput(part.costPrice),
    unitPrice: formatDecimalInput(part.unitPrice),
    stockQuantity: Number(part.stockQuantity || 0),
    originalStockQuantity: Number(part.stockQuantity || 0),
    minimumStock: Number(part.minimumStock || 0),
    active: part.active !== false,
    reservationDays: Number(part.reservationDays || 3),
  });
  partModalOpen.value = true;
}

function selectExistingPartForModal(part) {
  editPart(part);
}

function closePartModal() {
  partModalOpen.value = false;
  resetPartForm();
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
    0
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

function openCreateStoreQuoteModal() {
  resetStoreQuoteForm();
  storeQuoteModalOpen.value = true;
}

function closeStoreQuoteModal() {
  storeQuoteModalOpen.value = false;
  resetStoreQuoteForm();
}

function addStoreQuoteItem() {
  const part = data.parts.find((item) => item.id === forms.storeQuote.partId);
  if (!part) {
    showError('Selecione uma peça para adicionar ao carrinho.');
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

function saveStoreQuote({closeModal = true, notify = true} = {}) {
  if (!forms.storeQuote.customerName || !forms.storeQuote.items.length) {
    showError('Informe o cliente e ao menos uma peça para salvar o carrinho.');
    return null;
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
  if (closeModal) {
    storeQuoteModalOpen.value = false;
    resetStoreQuoteForm();
  }
  if (notify) {
    showSuccess('Carrinho salvo.');
  }
  return quote;
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
  closeRecord();
  selectTab('store-quotes');
  storeQuoteModalOpen.value = true;
}

function storeQuoteMainItem(quote) {
  const items = quote.items || [];
  if (!items.length) {
    return 'Sem peças vinculadas';
  }
  const first = items[0];
  const suffix = items.length > 1 ? ` + ${items.length - 1}` : '';
  return `${first.quantity || 1}x ${first.name}${suffix}`;
}

function storeQuoteUpdatedAt(quote) {
  return quote.updatedAt ? new Date(quote.updatedAt).toLocaleDateString('pt-BR') : '-';
}

function saveStoreQuoteStatusFromModal(status) {
  const quote = saveStoreQuote({closeModal: false, notify: false});
  if (!quote) {
    return null;
  }
  return updateStoreQuoteStatus(quote, status);
}

function updateStoreQuoteStatus(quote, status) {
  return runAction(async () => {
    if (status === 'SENT' && quote.status !== 'SENT') {
      await Promise.all(quote.items.map((item) => resources.reservePart(item.partId, Number(item.quantity))));
    }
    if (status === 'APPROVED') {
      await Promise.all(
        quote.items.map((item) =>
          resources.commitPartReservation(item.partId, {
            quantity: Number(item.quantity),
            reason: `Carrinho ${quote.id} aprovado`,
          })
        )
      );
    }
    if (['REFUSED', 'EXPIRED'].includes(status) && quote.status === 'SENT') {
      await Promise.all(
        quote.items.map((item) => resources.releasePartReservation(item.partId, Number(item.quantity)))
      );
    }
    quote.status = status;
    quote.updatedAt = new Date().toISOString();
    persistStoreQuotes();
    storeQuoteModalOpen.value = false;
    resetStoreQuoteForm();
  }, `Carrinho marcado como ${storeQuoteStatusLabels[status]}.`);
}

function selectCustomerPart(part) {
  selectedCustomerPart.value = part;
  openRecord('Peça para comparar', part);
}

function addCustomerPartRequest(part, store = null) {
  const existing = forms.customerQuote.items.find(
    (item) => item.partId === part.id && item.storeName === (store?.name || '')
  );
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
  showSuccess('Peça adicionada à solicitação.');
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
    showError('Escolha uma loja ou oficina para enviar a solicitação.');
    return;
  }
  if (!forms.customerQuote.items.length && !forms.customerQuote.problemDescription.trim()) {
    showError('Adicione uma peça ou descreva o problema do veículo.');
    return;
  }
  showSuccess('Solicitação enviada. O parceiro poderá responder com um orçamento.');
  forms.customerQuote = {
    storeName: '',
    storeContact: '',
    workshopName: '',
    vehicleId: '',
    problemDescription: '',
    discountPercent: 0,
    items: [],
  };
}

function createWorkshopService() {
  const wasEditing = Boolean(forms.service.id);
  return runAction(
    async () => {
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
      serviceModalOpen.value = false;
    },
    wasEditing ? 'Serviço atualizado.' : 'Serviço cadastrado.'
  );
}

function resetServiceForm() {
  Object.assign(forms.service, {
    id: '',
    name: '',
    description: '',
    basePrice: 0,
    estimatedTimeInMinutes: 60,
    active: true,
  });
}

function openCreateServiceModal() {
  resetServiceForm();
  serviceModalOpen.value = true;
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

function generateBudgetFromSelectedOrder() {
  if (!selectedRecord.value?.id) {
    return Promise.resolve();
  }
  if (!canGenerateBudget(selectedRecord.value)) {
    showError('Esta ordem não está em um status que permita gerar orçamento.');
    return Promise.resolve();
  }
  return runAction(async () => {
    await resources.generateBudget(selectedRecord.value.id);
    selectedRecord.value.status = 'WAITING_APPROVAL';
    modalDraft.order.status = 'WAITING_APPROVAL';
    if (pagination.serviceOrders.status) {
      pagination.serviceOrders.status = 'WAITING_APPROVAL';
    }
  }, 'Orçamento gerado.');
}

function approveBudget() {
  return runAction(async () => {
    await resources.approveBudget(forms.orderAction.serviceOrderId);
  }, 'Orçamento aprovado.');
}

function approveBudgetFromSelectedOrder() {
  if (!selectedRecord.value?.id) {
    return Promise.resolve();
  }
  if (!canApproveBudget(selectedRecord.value)) {
    showError('Esta ordem não está aguardando aprovação de orçamento.');
    return Promise.resolve();
  }
  return runAction(async () => {
    const approvedOrder = await resources.approveBudget(selectedRecord.value.id);
    Object.assign(selectedRecord.value, approvedOrder);
    modalDraft.order.status = approvedOrder.status;
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

function setListPage(resource, page) {
  pagination[resource].page = Math.min(listTotalPages(resource) - 1, Math.max(0, Number(page || 0)));
}

function resetListPage(resource) {
  pagination[resource].page = 0;
}

const isCustomerDetail = computed(() => selectedRecord.value && selectedRecordType.value === 'Cliente');

const isWorkshopDetail = computed(
  () => selectedRecord.value && ['Oficina', 'Oficina parceira', 'Loja parceira'].includes(selectedRecordType.value)
);

const isOrderDetail = computed(
  () => selectedRecord.value && ['Ordem de serviço', 'Histórico'].includes(selectedRecordType.value)
);

const isVehicleDetail = computed(() => selectedRecord.value && selectedRecordType.value === 'Veículo');

const selectedPartnerUser = computed(() => data.users.find((user) => user.id === selectedRecord.value?.id));

const detailModalTitle = computed(() => {
  if (!selectedRecord.value) {
    return 'Detalhes';
  }
  if (isOrderDetail.value) {
    return `Ordem de serviço - ${orderCustomerName(selectedRecord.value)}`;
  }
  return (
    selectedRecord.value.name ||
    selectedRecord.value.fullName ||
    selectedRecord.value.plate ||
    statusLabels[selectedRecord.value.status] ||
    selectedRecord.value.status ||
    'Detalhes'
  );
});

const detailModalDirty = computed(() => {
  if (isCustomerDetail.value) {
    const original = selectedRecord.value;
    const draft = modalDraft.customer;
    return (
      JSON.stringify({
        name: original.name,
        document: original.document,
        phone: original.phone,
        email: original.email,
        address: original.address || {},
        active: original.active !== false,
      }) !== JSON.stringify(draft)
    );
  }

  if (isWorkshopDetail.value) {
    const user = selectedPartnerUser.value;
    if (!user) {
      return false;
    }
    return (
      JSON.stringify({
        companyName: user.companyName || '',
        fullName: user.fullName || '',
        active: user.active !== false,
      }) !== JSON.stringify(modalDraft.partner)
    );
  }

  if (isOrderDetail.value) {
    return (
      JSON.stringify({
        diagnosticNotes: selectedRecord.value.diagnosticNotes || '',
        status: selectedRecord.value.status || 'RECEIVED',
      }) !== JSON.stringify(modalDraft.order)
    );
  }

  if (isVehicleDetail.value) {
    return (
      JSON.stringify({
        customerId: selectedRecord.value.customerId || '',
        plate: selectedRecord.value.plate || '',
        brand: selectedRecord.value.brand || '',
        model: selectedRecord.value.model || '',
        year: selectedRecord.value.year || new Date().getFullYear(),
        mileage: selectedRecord.value.mileage || 0,
        active: selectedRecord.value.active !== false,
      }) !== JSON.stringify(modalDraft.vehicle)
    );
  }

  return false;
});

async function openRecord(type, record) {
  selectedRecordType.value = type;
  selectedRecord.value = record;
  if (type === 'Cliente') {
    let customer = record;
    try {
      customer = await resources.customer(record.id);
      selectedRecord.value = customer;
    } catch (err) {
      showError(err.message || 'Não foi possível carregar os dados completos do cliente.');
    }
    Object.assign(modalDraft.customer, {
      name: customer.name || '',
      document: customer.document || '',
      phone: customer.phone || '',
      email: customer.email || '',
      address: {...(customer.address || forms.customer.address)},
      active: customer.active !== false,
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
  if (['Ordem de serviço', 'Histórico'].includes(type)) {
    Object.assign(modalDraft.order, {
      diagnosticNotes: record.diagnosticNotes || '',
      status: record.status || 'RECEIVED',
    });
    forms.orderAction.serviceOrderId = record.id;
    forms.orderAction.status = record.status || 'RECEIVED';
  }
  if (type === 'Veículo') {
    Object.assign(modalDraft.vehicle, {
      customerId: record.customerId || '',
      plate: record.plate || '',
      brand: record.brand || '',
      model: record.model || '',
      year: record.year || new Date().getFullYear(),
      mileage: record.mileage || 0,
      active: record.active !== false,
    });
  }
}

function closeRecord() {
  selectedRecord.value = null;
  selectedRecordType.value = '';
  Object.assign(modalDraft.customer, {});
  Object.assign(modalDraft.partner, {});
  Object.assign(modalDraft.order, {diagnosticNotes: '', status: 'RECEIVED'});
  Object.assign(modalDraft.vehicle, {
    customerId: '',
    plate: '',
    brand: '',
    model: '',
    year: new Date().getFullYear(),
    mileage: 0,
    active: true,
  });
}

async function saveDetailModal() {
  if (isCustomerDetail.value) {
    await runAction(async () => {
      await resources.updateCustomer(selectedRecord.value.id, buildCustomerPayload(modalDraft.customer));
      closeRecord(true);
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
      closeRecord(true);
    }, 'Oficina atualizada.');
    return;
  }

  if (isOrderDetail.value) {
    await runAction(async () => {
      const statusChanged = modalDraft.order.status !== selectedRecord.value.status;
      if (statusChanged) {
        await resources.updateOrderStatus(selectedRecord.value.id, modalDraft.order.status);
      }
      selectedRecord.value.diagnosticNotes = modalDraft.order.diagnosticNotes;
      selectedRecord.value.status = modalDraft.order.status;
      if (statusChanged && pagination.serviceOrders.status) {
        pagination.serviceOrders.status = modalDraft.order.status;
      }
      closeRecord(true);
    }, 'Ordem atualizada.');
    return;
  }

  if (isVehicleDetail.value) {
    await runAction(async () => {
      await resources.updateVehicle(selectedRecord.value.id, {
        ...modalDraft.vehicle,
        year: Number(modalDraft.vehicle.year),
        mileage: Number(modalDraft.vehicle.mileage),
      });
      closeRecord(true);
    }, 'Veículo atualizado.');
  }
}

async function editCustomer(customer) {
  let customerDetail = customer;
  try {
    customerDetail = await resources.customer(customer.id);
  } catch (err) {
    showError(err.message || 'Não foi possível carregar os dados completos do cliente.');
  }
  Object.assign(forms.customer, {
    ...customerDetail,
    address: {...(customerDetail.address || forms.customer.address)},
    active: customerDetail.active !== false,
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
  serviceModalOpen.value = true;
}

function selectExistingServiceForModal(service) {
  editService(service);
}

function closeServiceModal() {
  serviceModalOpen.value = false;
  resetServiceForm();
}

function editUser(user) {
  Object.assign(forms.user, {
    id: user.id,
    fullName: user.fullName,
    username: user.username,
    password: '',
    role: user.role,
    profileType: user.profileType,
    companyId: user.companyId || '',
    companyName: user.companyName || '',
    companyType: user.companyType || '',
    createCompany: false,
    employeeSubRole: user.employeeSubRole || 'UNSPECIFIED',
    permissions: [...(user.permissions || [])],
    customerId: user.customerId || '',
    active: user.active,
  });
  userFormInitial.value = comparableUserForm();
  userModalOpen.value = true;
}

function resetUserForm() {
  const defaultProfileType = isPartsStoreAdmin.value ? 'PARTS_STORE_EMPLOYEE' : 'WORKSHOP_EMPLOYEE';
  const defaultCompanyType =
    isMasterAdmin.value || !currentUser.value?.companyType ? 'WORKSHOP' : currentUser.value.companyType;
  Object.assign(forms.user, {
    id: '',
    fullName: '',
    username: '',
    password: '',
    role: 'EMPLOYEE',
    profileType: defaultProfileType,
    companyId: isMasterAdmin.value ? '' : currentUser.value?.companyId || '',
    companyName: isMasterAdmin.value ? '' : currentUser.value?.companyName || '',
    companyType: defaultCompanyType,
    createCompany: false,
    employeeSubRole: 'UNSPECIFIED',
    permissions: ['CREATE_ORDER', 'EDIT_ORDER', 'CREATE_BUDGET'],
    customerId: '',
    active: true,
  });
  userFormInitial.value = comparableUserForm();
}

function openCreateUserModal() {
  resetUserForm();
  syncUserProfileDefaults();
  userModalOpen.value = true;
}

function openCreatePartnerAdminModal(profileType = 'WORKSHOP_ADMIN') {
  resetUserForm();
  Object.assign(forms.user, {
    role: 'ADMIN',
    profileType,
    companyType: profileType === 'WORKSHOP_ADMIN' ? 'WORKSHOP' : 'PARTS_STORE',
    createCompany: false,
    employeeSubRole: '',
    permissions: ['VIEW_BILLING', 'MANAGE_STOCK', 'CREATE_BUDGET', 'EDIT_EMPLOYEES', 'VIEW_STATS'],
  });
  ensureUserCompanySelection();
  userFormInitial.value = comparableUserForm();
  userModalOpen.value = true;
}

function openCreateWorkshopEmployeeModal() {
  resetUserForm();
  Object.assign(forms.user, {
    role: 'EMPLOYEE',
    profileType: 'WORKSHOP_EMPLOYEE',
    companyType: 'WORKSHOP',
    companyId: isMasterAdmin.value ? forms.user.companyId : currentUser.value?.companyId || '',
    companyName: isMasterAdmin.value ? forms.user.companyName : currentUser.value?.companyName || '',
    employeeSubRole: 'UNSPECIFIED',
    permissions: ['CREATE_ORDER', 'EDIT_ORDER', 'CREATE_BUDGET'],
  });
  userFormInitial.value = comparableUserForm();
  userModalOpen.value = true;
}

function openCreateStoreEmployeeModal() {
  resetUserForm();
  Object.assign(forms.user, {
    role: 'EMPLOYEE',
    profileType: 'PARTS_STORE_EMPLOYEE',
    companyType: 'PARTS_STORE',
    companyId: isMasterAdmin.value ? forms.user.companyId : currentUser.value?.companyId || '',
    companyName: isMasterAdmin.value ? forms.user.companyName : currentUser.value?.companyName || '',
    employeeSubRole: 'ATTENDANT',
    permissions: ['MANAGE_STOCK', 'CREATE_BUDGET'],
  });
  userFormInitial.value = comparableUserForm();
  userModalOpen.value = true;
}

function closeUserModal() {
  userModalOpen.value = false;
  resetUserForm();
}

function syncUserProfileDefaults() {
  if (!isMasterAdmin.value) {
    forms.user.companyId = currentUser.value?.companyId || '';
    forms.user.companyName = currentUser.value?.companyName || '';
    forms.user.companyType = currentUser.value?.companyType || forms.user.companyType;
    forms.user.createCompany = false;
    if (isPartsStoreAdmin.value) {
      forms.user.profileType = 'PARTS_STORE_EMPLOYEE';
      forms.user.role = 'EMPLOYEE';
      forms.user.companyType = 'PARTS_STORE';
    } else {
      forms.user.profileType = 'WORKSHOP_EMPLOYEE';
      forms.user.role = 'EMPLOYEE';
      forms.user.companyType = 'WORKSHOP';
    }
    forms.user.employeeSubRole = forms.user.employeeSubRole || 'UNSPECIFIED';
    return;
  }
  if (forms.user.profileType === 'MASTER_ADMIN') {
    forms.user.role = 'ADMIN';
    const platform = data.companies.find((company) => company.type === 'PLATFORM');
    forms.user.companyId = platform?.id || '';
    forms.user.companyName = platform?.name || 'AutoCare Hub';
    forms.user.companyType = 'PLATFORM';
    forms.user.createCompany = false;
    forms.user.employeeSubRole = '';
    return;
  }
  if (forms.user.profileType === 'WORKSHOP_ADMIN') {
    forms.user.role = 'ADMIN';
    forms.user.companyType = 'WORKSHOP';
    forms.user.employeeSubRole = '';
    ensureUserCompanySelection();
    return;
  }
  if (forms.user.profileType === 'PARTS_STORE_ADMIN') {
    forms.user.role = 'ADMIN';
    forms.user.companyType = 'PARTS_STORE';
    forms.user.employeeSubRole = '';
    ensureUserCompanySelection();
    return;
  }
  if (forms.user.profileType === 'WORKSHOP_EMPLOYEE') {
    forms.user.role = 'EMPLOYEE';
    forms.user.companyType = 'WORKSHOP';
    forms.user.employeeSubRole = forms.user.employeeSubRole || 'UNSPECIFIED';
    ensureUserCompanySelection();
    return;
  }
  if (forms.user.profileType === 'PARTS_STORE_EMPLOYEE') {
    forms.user.role = 'EMPLOYEE';
    forms.user.companyType = 'PARTS_STORE';
    forms.user.employeeSubRole = forms.user.employeeSubRole || 'UNSPECIFIED';
    ensureUserCompanySelection();
    return;
  }
  if (forms.user.profileType === 'CUSTOMER_OWNER') {
    forms.user.role = 'CUSTOMER';
    forms.user.companyId = '';
    forms.user.companyType = '';
    forms.user.companyName = '';
    forms.user.createCompany = false;
    forms.user.employeeSubRole = '';
  }
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
  if (forms.user.id && !userFormDirty.value) {
    return Promise.resolve();
  }

  const isEmployee = ['WORKSHOP_EMPLOYEE', 'PARTS_STORE_EMPLOYEE'].includes(forms.user.profileType);
  const successMessage = forms.user.id
    ? isEmployee
      ? 'Funcionário atualizado.'
      : 'Conta atualizada.'
    : isEmployee
      ? 'Funcionário criado.'
      : 'Conta criada.';
  return runAction(async () => {
    syncUserProfileDefaults();
    const payload = {
      fullName: forms.user.fullName,
      username: forms.user.username,
      role: forms.user.role,
      profileType: forms.user.profileType,
      companyId: forms.user.createCompany ? null : forms.user.companyId || null,
      companyName: forms.user.companyName,
      companyType: forms.user.companyType,
      createCompany: Boolean(forms.user.createCompany),
      employeeSubRole: forms.user.employeeSubRole,
      permissions: forms.user.permissions,
      customerId: forms.user.customerId || null,
      active: forms.user.active,
    };
    if (forms.user.id) {
      await resources.updateUser(forms.user.id, payload);
      if (forms.user.password) {
        await resources.resetUserPassword(forms.user.id, forms.user.password);
      }
    } else {
      await resources.createUser({...payload, password: forms.user.password});
    }
    closeUserModal();
  }, successMessage);
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
  if (!accountDirty.value) {
    return Promise.resolve();
  }

  return runAction(async () => {
    currentUser.value = await resources.updateCurrentUser({fullName: forms.account.fullName});
  }, 'Dados do usuário atualizados.');
}

function changePassword() {
  if (!passwordDirty.value) {
    return Promise.resolve();
  }

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

function openConfirmDialog(options) {
  Object.assign(confirmDialog, {
    title: options.title || 'Confirmar ação',
    message: options.message || 'Deseja continuar?',
    confirmLabel: options.confirmLabel || 'Confirmar',
    cancelLabel: options.cancelLabel || 'Cancelar',
    tone: options.tone || 'default',
    onConfirm: options.onConfirm || null,
  });
  confirmDialogOpen.value = true;
}

function closeConfirmDialog() {
  confirmDialogOpen.value = false;
  confirmDialog.onConfirm = null;
}

function confirmDialogAction() {
  const action = confirmDialog.onConfirm;
  closeConfirmDialog();
  if (typeof action === 'function') {
    action();
  }
}

function showProfileAction(action) {
  profileMenuOpen.value = false;
  if (['Editar informações do usuário', 'Alterar senha', 'Minha conta'].includes(action)) {
    activeTab.value = 'account';
    return;
  }
}

function logout() {
  profileMenuOpen.value = false;
  openConfirmDialog({
    title: 'Sair da conta?',
    message: 'Você será direcionado para a tela de login.',
    confirmLabel: 'Sair',
    tone: 'danger',
    onConfirm: () => {
      auth.logout();
      router.push({name: 'login'});
    },
  });
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
        <button class="menu-button" title="Abrir menu" type="button" @click="mobileMenuOpen = !mobileMenuOpen">
          <X v-if="mobileMenuOpen" :size="22" />
          <Menu v-else :size="22" />
        </button>

        <div class="navbar-brand">
          <div class="brand-mark">
            <Wrench :size="22" />
          </div>
          <div>
            <strong>AutoCare Hub</strong>
            <span>{{ roleLabel }}</span>
          </div>
        </div>

        <div class="navbar-search">
          <Search :size="17" />
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
              <button role="menuitem" type="button" @click="showProfileAction('Minha conta')">
                <UserCog :size="17" />
                <span>Minha conta</span>
              </button>
              <button role="menuitem" type="button" @click="showProfileAction('Alterar senha')">
                <KeyRound :size="17" />
                <span>Alterar senha</span>
              </button>
              <button role="menuitem" type="button" @click="logout">
                <LogOut :size="17" />
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
          <ChevronRight v-if="sidebarCollapsed" :size="18" />
          <ChevronLeft v-else :size="18" />
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
            <component :is="tab.icon" :size="20" />
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
        <ToastAlert :message="success" type="success" @close="resetMessage" />
        <ToastAlert :message="error" type="error" @close="resetMessage" />

        <section v-if="activeTab === 'overview'" class="screen-stack">
          <div class="home-toolbar">
            <span><ShieldCheck :size="16" /> {{ auth.user?.username }}</span>
            <button v-if="!isCustomerProfile" class="secondary-button" type="button" @click="toggleHomeSettings">
              <Plus :size="17" />
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
                <button
                  v-for="order in customerBudgetAlerts"
                  :key="`budget-${order.id}`"
                  type="button"
                  @click="openRecord('Orçamento pendente', order)"
                >
                  <AlertTriangle :size="18" />
                  <span>Orçamento pendente de aprovação</span>
                  <strong>R$ {{ money(order.totalAmount) }}</strong>
                </button>
                <button
                  v-for="order in customerReadyAlerts"
                  :key="`ready-${order.id}`"
                  type="button"
                  @click="openRecord('Veículo pronto', order)"
                >
                  <CheckCircle2 :size="18" />
                  <span>Veículo pronto para retirada</span>
                  <strong>{{ statusLabels[order.status] }}</strong>
                </button>
                <button
                  v-for="order in customerFinishedAlerts"
                  :key="`finished-${order.id}`"
                  type="button"
                  @click="openRecord('Veículo concluído', order)"
                >
                  <CheckCircle2 :size="18" />
                  <span>Atendimento concluído</span>
                  <strong>{{ statusLabels[order.status] }}</strong>
                </button>
                <p
                  v-if="!customerBudgetAlerts.length && !customerReadyAlerts.length && !customerFinishedAlerts.length"
                  class="empty-state"
                >
                  Nenhum alerta ativo.
                </p>
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
                <StatusBadge :value="order.status" />
                <span
                >{{ order.diagnosticNotes }}<small>{{ order.id }}</small></span
                >
                <span
                >{{ order.services?.length || 0 }} serviços<small>{{ order.parts?.length || 0 }} peças</small></span
                >
                <strong>R$ {{ money(order.totalAmount) }}</strong>
              </article>
            </div>
          </section>

          <section v-if="homeSettingsOpen" class="home-settings-panel">
            <div>
              <strong>Meus widgets</strong>
              <div class="home-option-grid">
                <label
                  v-for="widget in availableHomeWidgetDefinitions"
                  :key="widget.id"
                  :class="{
                    'is-active': isHomeWidgetSelected(widget.id),
                    'is-inactive': !isHomeWidgetSelected(widget.id),
                  }"
                >
                  <input
                    :checked="isHomeWidgetSelected(widget.id)"
                    type="checkbox"
                    @change="toggleHomeWidget(widget.id)"
                  />
                  <span>{{ widget.label }}</span>
                  <small>{{ isHomeWidgetSelected(widget.id) ? 'Ativo' : 'Inativo' }}</small>
                </label>
              </div>
            </div>
            <div v-if="auth.role === 'ADMIN'">
              <strong>{{ isPartsStoreProfile ? 'Configuração da loja' : 'Configuração da oficina' }}</strong>
              <label class="home-alert-toggle">
                <input :checked="homePreferenceDraft.showAlertsOnHome" type="checkbox" @change="toggleHomeAlerts" />
                <span>Exibir avisos críticos de estoque para a equipe</span>
              </label>
              <div class="home-option-grid">
                <label
                  v-for="widget in availableHomeWidgetDefinitions"
                  :key="`global-${widget.id}`"
                  :class="{
                    'is-active': isHomeWidgetSelected(widget.id, 'global'),
                    'is-inactive': !isHomeWidgetSelected(widget.id, 'global'),
                  }"
                >
                  <input
                    :checked="isHomeWidgetSelected(widget.id, 'global')"
                    type="checkbox"
                    @change="toggleHomeWidget(widget.id, 'global')"
                  />
                  <span>{{ widget.label }}</span>
                  <small>{{ isHomeWidgetSelected(widget.id, 'global') ? 'Ativo' : 'Inativo' }}</small>
                </label>
              </div>
            </div>
            <div class="home-settings-actions">
              <button
                :disabled="homePreferencesSaving || !homePreferenceDirty"
                class="primary-button home-save-button"
                type="button"
                @click="saveHomePreferenceDraft"
              >
                Salvar preferências
              </button>
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
              <component :is="widget.icon" :size="22" />
              <strong>{{ widget.value }}</strong>
              <span>{{ widget.label }}</span>
            </button>
          </section>

          <section v-if="isWorkshopAdmin && featuredWorkshopEmployees.length" class="section-block">
            <div class="section-heading">
              <h2>Funcionários em destaque</h2>
              <span>Melhores desempenhos recentes da oficina</span>
            </div>
            <div class="employee-metrics-grid featured-employee-grid">
              <article
                v-for="{ employee, metrics, score } in featuredWorkshopEmployees"
                :key="`featured-${employee.id}`"
                class="employee-card employee-card--highlight clickable-row"
                @click="editUser(employee)"
              >
                <div>
                  <strong>{{ employee.fullName }}</strong>
                  <span>{{ employeeSubRoleLabels[employee.employeeSubRole] || employee.employeeSubRole }}</span>
                </div>
                <dl>
                  <template v-for="metric in metrics.slice(0, 3)" :key="metric.label">
                    <dt>{{ metric.label }}</dt>
                    <dd>{{ metric.value }}</dd>
                  </template>
                  <dt>Desempenho</dt>
                  <dd>{{ score }}</dd>
                </dl>
              </article>
            </div>
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
                  <component :is="widget.icon" :size="22" />
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
                  <component :is="widget.icon" :size="22" />
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
                <div><strong>Parceiros com maior potencial</strong><span>Interessados da demo</span></div>
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
                <DollarSign :size="22" />
                <strong>R$ {{ money(storeBillingSummary.gross) }}</strong>
                <span>Vendas aprovadas</span>
              </article>
              <article class="metric-card">
                <BadgePercent :size="22" />
                <strong>{{ storeBillingSummary.conversion }}%</strong>
                <span>Taxa de conversão</span>
              </article>
              <article class="metric-card">
                <ShoppingCart :size="22" />
                <strong>R$ {{ money(storeBillingSummary.ticket) }}</strong>
                <span>Ticket médio</span>
              </article>
              <article class="metric-card">
                <Package :size="22" />
                <strong>{{ storeTopProducts[0]?.name || 'Sem vendas' }}</strong>
                <span>Produto mais vendido</span>
              </article>
            </div>
          </section>

          <section
            v-if="!isPartsStoreProfile && !isMasterAdmin && !isCustomerProfile"
            aria-label="Status atual dos veículos"
            class="vehicle-status-grid"
          >
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

        <section v-if="activeTab === 'billing' && isWorkshopAdmin" class="screen-stack billing-screen">
          <section class="billing-hero-grid">
            <button class="billing-card tone-green" type="button" @click="selectTab('orders')">
              <DollarSign :size="22" />
              <span>Faturamento bruto</span>
              <strong>R$ {{ money(billingSummary.gross) }}</strong>
              <small>Ordens e orçamentos aprovados</small>
            </button>
            <article class="billing-card tone-blue">
              <BadgePercent :size="22" />
              <span>Taxa atual AutoCare Hub</span>
              <strong>{{ billingSummary.feeRateLabel }}</strong>
              <small>Aplicada sobre o faturamento mensal</small>
            </article>
            <article class="billing-card tone-teal billing-card--featured">
              <CheckCircle2 :size="22" />
              <span>Líquido com taxa descontada</span>
              <strong>R$ {{ money(billingSummary.net) }}</strong>
              <small>Valor estimado já descontando a taxa da plataforma</small>
            </article>
            <button class="billing-card tone-amber" type="button" @click="selectTab('orders')">
              <TrendingUp :size="22" />
              <span>Ticket médio</span>
              <strong>R$ {{ money(billingSummary.ticket) }}</strong>
              <small>Baseado nos orçamentos aprovados</small>
            </button>
          </section>

          <section class="billing-tier-panel">
            <div>
              <span class="tier-chip tier-chip--done">Taxa atual {{ billingSummary.feeRateLabel }}</span>
              <h2>Quanto maior o faturamento mensal, menor pode ser a taxa.</h2>
              <p>
                A AutoCare Hub calcula a taxa por faixa de faturamento. Ao atingir a próxima faixa, a oficina passa a
                trabalhar com uma taxa menor sobre vendas e orçamentos aprovados.
              </p>
            </div>
            <div class="billing-tier-summary">
              <span>
                Valor líquido
                <strong>R$ {{ money(billingSummary.net) }}</strong>
              </span>
              <span>
                Próxima taxa
                <strong>{{ billingSummary.nextTierLabel }}</strong>
              </span>
              <span>
                Falta para a próxima faixa
                <strong>{{
                    billingSummary.nextTierGap > 0 ? `R$ ${money(billingSummary.nextTierGap)}` : 'Menor taxa ativa'
                  }}</strong>
              </span>
            </div>
            <div class="billing-progress-track">
              <b :style="{ width: `${Math.min(100, Math.max(8, ((billingSummary.gross % 5000) / 5000) * 100))}%` }"></b>
            </div>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Resumo financeiro</h2>
              <span>Receita, conversão e serviços concluídos</span>
            </div>
            <div class="analytics-grid">
              <button class="metric-card billing-metric tone-blue" type="button" @click="selectTab('orders')">
                <BadgePercent :size="22" />
                <strong>{{ billingSummary.sentBudgets }}</strong>
                <span>Orçamentos enviados</span>
              </button>
              <button class="metric-card billing-metric tone-green" type="button" @click="selectTab('orders')">
                <CheckCircle2 :size="22" />
                <strong>{{ billingSummary.approvedBudgets }}</strong>
                <span>Orçamentos aprovados</span>
              </button>
              <button class="metric-card billing-metric tone-cyan" type="button" @click="selectTab('orders')">
                <Wrench :size="22" />
                <strong>{{ billingSummary.completed }}</strong>
                <span>Serviços concluídos</span>
              </button>
              <article class="metric-card billing-metric tone-violet">
                <DollarSign :size="22" />
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
            <div class="comparison-bars revenue-bars billing-revenue-bars">
              <div v-for="month in monthlyRevenue" :key="month.month">
                <span>{{ month.month }}</span>
                <div>
                  <b :style="{ width: `${Math.max(6, (month.value / Math.max(1, billingSummary.gross)) * 100)}%` }"></b>
                </div>
                <strong>R$ {{ money(month.value) }}</strong>
              </div>
            </div>
            <article class="selected-record billing-tier-callout">
              <BarChart3 :size="20" />
              <strong>
                {{
                  billingSummary.nextTierGap > 0
                    ? `Faltam R$ ${money(billingSummary.nextTierGap)}`
                    : 'Melhor faixa atingida'
                }}
              </strong>
              <span>
                {{
                  billingSummary.nextTierGap > 0
                    ? `Para atingir a próxima faixa de taxa (${billingSummary.nextTierLabel}).`
                    : 'A oficina já está na menor taxa disponível.'
                }}
              </span>
            </article>
          </section>
        </section>

        <section v-if="activeTab === 'employees' && isWorkshopAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Funcionários</h2>
              <span>{{ workshopEmployees.length }} pessoas na oficina</span>
              <button class="primary-button" type="button" @click="openCreateWorkshopEmployeeModal">
                <UserPlus :size="18" />
                <span>Criar funcionário</span>
              </button>
            </div>
            <div class="data-table">
              <div class="data-table-header employee-grid">
                <span>Funcionário</span>
                <span>Subfunção</span>
                <span>Permissões</span>
                <span>Status</span>
              </div>
              <article
                v-for="employee in workshopEmployees"
                :key="employee.id"
                class="data-table-row employee-grid clickable-row"
                @click="editUser(employee)"
              >
                <strong
                >{{ employee.fullName }}<small>{{ employee.username }}</small></strong
                >
                <span>{{ employeeSubRoleLabels[employee.employeeSubRole] || employee.employeeSubRole }}</span>
                <span>{{ employee.permissions?.length || 0 }} permissões</span>
                <StatusBadge :value="employee.active" />
              </article>
            </div>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Métricas por funcionário</h2>
              <span>Indicadores ordenados do maior para o menor desempenho</span>
            </div>
            <div class="filters">
              <input v-model="employeeMetricSearch" placeholder="Buscar funcionário nas métricas" type="search" />
            </div>
            <div class="employee-metrics-grid">
              <article
                v-for="{ employee, metrics, score } in employeeMetricCards"
                :key="`metrics-${employee.id}`"
                class="employee-card employee-card--metric clickable-row"
                @click="editUser(employee)"
              >
                <div>
                  <strong>{{ employee.fullName }}</strong>
                  <span>{{ employeeSubRoleLabels[employee.employeeSubRole] || employee.employeeSubRole }}</span>
                </div>
                <dl>
                  <template v-for="metric in metrics" :key="metric.label">
                    <dt>{{ metric.label }}</dt>
                    <dd>{{ metric.value }}</dd>
                  </template>
                  <dt>Desempenho</dt>
                  <dd>{{ score }}</dd>
                </dl>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'store-billing' && isPartsStoreAdmin" class="screen-stack billing-screen">
          <section class="billing-hero-grid">
            <button class="billing-card tone-green" type="button" @click="selectTab('store-quotes')">
              <DollarSign :size="22" />
              <span>Faturamento bruto</span>
              <strong>R$ {{ money(storeBillingSummary.gross) }}</strong>
              <small>Carrinhos e orçamentos aprovados</small>
            </button>
            <article class="billing-card tone-blue">
              <BadgePercent :size="22" />
              <span>Taxa atual AutoCare Hub</span>
              <strong>{{ storeBillingSummary.feeRateLabel }}</strong>
              <small>Aplicada sobre o faturamento mensal</small>
            </article>
            <article class="billing-card tone-teal billing-card--featured">
              <CheckCircle2 :size="22" />
              <span>Líquido com taxa descontada</span>
              <strong>R$ {{ money(storeBillingSummary.net) }}</strong>
              <small>Valor estimado já descontando a taxa da plataforma</small>
            </article>
            <button class="billing-card tone-amber" type="button" @click="selectTab('store-quotes')">
              <TrendingUp :size="22" />
              <span>Ticket médio</span>
              <strong>R$ {{ money(storeBillingSummary.ticket) }}</strong>
              <small>Baseado nos orçamentos aprovados</small>
            </button>
          </section>

          <section class="billing-tier-panel">
            <div>
              <span class="tier-chip tier-chip--done">Taxa atual {{ storeBillingSummary.feeRateLabel }}</span>
              <h2>Quanto maior o faturamento mensal, menor pode ser a taxa.</h2>
              <p>
                A AutoCare Hub calcula a taxa por faixa de faturamento. Ao atingir a próxima faixa, a loja passa a
                trabalhar com uma taxa menor sobre vendas e orçamentos aprovados.
              </p>
            </div>
            <div class="billing-tier-summary">
              <span>
                Valor líquido
                <strong>R$ {{ money(storeBillingSummary.net) }}</strong>
              </span>
              <span>
                Próxima taxa
                <strong>{{ storeBillingSummary.nextTierLabel }}</strong>
              </span>
              <span>
                Falta para a próxima faixa
                <strong>{{
                    storeBillingSummary.nextTierGap > 0
                      ? `R$ ${money(storeBillingSummary.nextTierGap)}`
                      : 'Menor taxa ativa'
                  }}</strong>
              </span>
            </div>
            <div class="billing-progress-track">
              <b
                :style="{ width: `${Math.min(100, Math.max(8, ((storeBillingSummary.gross % 5000) / 5000) * 100))}%` }"
              ></b>
            </div>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Métricas comerciais</h2>
              <span>Orçamentos, conversão e vendas de peças</span>
            </div>
            <div class="analytics-grid">
              <button class="metric-card billing-metric tone-blue" type="button" @click="selectTab('store-quotes')">
                <ClipboardList :size="22" />
                <strong>{{ storeBillingSummary.sentQuotes }}</strong>
                <span>Orçamentos enviados</span>
              </button>
              <button class="metric-card billing-metric tone-green" type="button" @click="selectTab('store-quotes')">
                <CheckCircle2 :size="22" />
                <strong>{{ storeBillingSummary.approvedQuotes }}</strong>
                <span>Orçamentos aprovados</span>
              </button>
              <button class="metric-card billing-metric tone-cyan" type="button" @click="selectTab('store-quotes')">
                <BadgePercent :size="22" />
                <strong>{{ storeBillingSummary.conversion }}%</strong>
                <span>Taxa de conversão</span>
              </button>
              <article class="metric-card billing-metric tone-violet">
                <DollarSign :size="22" />
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
            <div class="comparison-bars revenue-bars billing-revenue-bars">
              <div v-for="month in storeMonthlyRevenue" :key="month.month">
                <span>{{ month.month }}</span>
                <div>
                  <b
                    :style="{ width: `${Math.max(6, (month.value / Math.max(1, storeBillingSummary.gross)) * 100)}%` }"
                  ></b>
                </div>
                <strong>R$ {{ money(month.value) }}</strong>
              </div>
            </div>
            <article class="selected-record billing-tier-callout">
              <BarChart3 :size="20" />
              <strong>
                {{
                  storeBillingSummary.nextTierGap > 0
                    ? `Faltam R$ ${money(storeBillingSummary.nextTierGap)}`
                    : 'Melhor faixa atingida'
                }}
              </strong>
              <span>
                {{
                  storeBillingSummary.nextTierGap > 0
                    ? `Para atingir a próxima faixa de taxa (${storeBillingSummary.nextTierLabel}).`
                    : 'A loja já está na menor taxa disponível.'
                }}
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
          <section class="section-block action-hero">
            <div class="section-heading">
              <h2>Funcionários da loja</h2>
              <span>Atendentes, administradores e permissões comerciais</span>
            </div>
            <button class="primary-button action-hero-button" type="button" @click="openCreateStoreEmployeeModal">
              <UserPlus :size="18" />
              <span>Criar funcionário</span>
            </button>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Equipe da loja</h2>
              <span>{{ storeEmployees.length }} pessoas cadastradas</span>
            </div>
            <div class="data-table">
              <div class="data-table-header employee-grid employee-grid--no-action">
                <span>Funcionário</span>
                <span>Subfunção</span>
                <span>Permissões</span>
                <span>Status</span>
              </div>
              <article
                v-for="employee in storeEmployees"
                :key="employee.id"
                class="data-table-row employee-grid employee-grid--no-action clickable-row"
                @click="editUser(employee)"
              >
                <strong
                >{{ employee.fullName }}<small>{{ employee.username }}</small></strong
                >
                <span>{{
                    employee.profileType === 'PARTS_STORE_ADMIN'
                      ? 'Administrador'
                      : employeeSubRoleLabels[employee.employeeSubRole]
                }}</span>
                <span>{{ employee.permissions?.length || 0 }} permissões</span>
                <StatusBadge :value="employee.active" />
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
                  <span>{{
                      employee.profileType === 'PARTS_STORE_ADMIN'
                        ? 'Administrador'
                        : employeeSubRoleLabels[employee.employeeSubRole]
                    }}</span>
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

          <section v-if="auth.role === 'ADMIN' || can('CREATE_BUDGET')" class="section-block action-hero">
            <div class="section-heading">
              <h2>Carrinhos e orçamentos da loja</h2>
              <span>Monte solicitações de peças, negocie valores e acompanhe a aprovação do cliente</span>
            </div>
            <button class="primary-button action-hero-button" type="button" @click="openCreateStoreQuoteModal">
              <ShoppingCart :size="18" />
              <span>Novo carrinho/orçamento</span>
            </button>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Carrinhos e orçamentos</h2>
              <span>{{ storeQuotes.length }} registros comerciais</span>
            </div>
            <div class="data-table">
              <div class="data-table-header store-quotes-grid">
                <span>Cliente</span>
                <span>Peças solicitadas</span>
                <span>Status</span>
                <span>Atualização</span>
                <span>Total</span>
              </div>
              <article
                v-for="quote in storeQuotes"
                :key="quote.id"
                class="data-table-row store-quotes-grid clickable-row"
                @click="editStoreQuote(quote)"
              >
                <span>
                  {{ quote.customerName }}
                  <small>{{ quote.customerContact || 'Sem contato informado' }} · {{ quote.id }}</small>
                </span>
                <span>
                  {{ storeQuoteMainItem(quote) }}
                  <small>{{
                      quote.contactRequested ? 'Cliente aguardando contato' : `${quote.items.length} item(ns) no carrinho`
                    }}</small>
                </span>
                <StatusBadge :label="storeQuoteStatusLabels[quote.status]" :value="quote.status" />
                <span class="table-center">{{ storeQuoteUpdatedAt(quote) }}</span>
                <strong class="table-money">R$ {{ money(storeQuoteTotal(quote)) }}</strong>
              </article>
            </div>
          </section>

          <AppModal
            :dirty="
              Boolean(
                forms.storeQuote.customerName || forms.storeQuote.customerContact || forms.storeQuote.items.length
              )
            "
            :open="storeQuoteModalOpen"
            :title="forms.storeQuote.id ? 'Editar carrinho/orçamento' : 'Novo carrinho/orçamento'"
            subtitle="Fluxo comercial da loja de peças"
            @close="closeStoreQuoteModal"
          >
            <form class="modal-form store-quote-modal-form" @submit.prevent="saveStoreQuote()">
              <label class="form-field">
                <span>Cliente</span>
                <input v-model="forms.storeQuote.customerName" placeholder="Nome do cliente ou empresa" required />
              </label>
              <label class="form-field">
                <span>Contato do cliente</span>
                <input v-model="forms.storeQuote.customerContact" placeholder="Telefone ou e-mail" />
              </label>
              <label class="form-field">
                <span>Status atual</span>
                <input :value="storeQuoteStatusLabels[forms.storeQuote.status] || forms.storeQuote.status" disabled />
              </label>
              <label class="check-row">
                <input v-model="forms.storeQuote.contactRequested" type="checkbox" />
                <span>Cliente quer ser contatado pela loja</span>
              </label>

              <div class="quote-item-builder">
                <label class="form-field">
                  <span>Peça solicitada</span>
                  <select v-model="forms.storeQuote.partId">
                    <option value="">Selecione uma peça</option>
                    <option v-for="part in data.parts" :key="part.id" :value="part.id">
                      {{ part.name }} - R$ {{ money(part.unitPrice) }} -
                      {{ part.availableQuantity ?? part.stockQuantity }} un.
                    </option>
                  </select>
                </label>
                <label class="form-field">
                  <span>Quantidade</span>
                  <input v-model.number="forms.storeQuote.quantity" min="1" type="number" />
                </label>
                <label class="form-field">
                  <span>Valor unitário negociado</span>
                  <input v-model.number="forms.storeQuote.quotedPrice" min="0" step="0.01" type="number" />
                </label>
                <button class="secondary-button" type="button" @click="addStoreQuoteItem">
                  <Plus :size="18" />
                  Adicionar peça
                </button>
              </div>

              <div class="quote-items store-quote-items">
                <button
                  v-for="(item, index) in forms.storeQuote.items"
                  :key="`${item.partId}-${index}`"
                  type="button"
                  @click="removeStoreQuoteItem(index)"
                >
                  <span>
                    <strong>{{ item.quantity }}x {{ item.name }}</strong>
                    <small>R$ {{ money(item.quotedPrice) }} por unidade</small>
                  </span>
                  <b>Remover</b>
                </button>
              </div>

              <div class="modal-readonly-grid store-quote-summary">
                <span
                >Peças no carrinho<strong>{{ forms.storeQuote.items.length }}</strong></span
                >
                <span
                >Total negociado<strong>R$ {{ money(storeQuoteTotal(forms.storeQuote)) }}</strong></span
                >
                <span
                >Status comercial<strong>{{ storeQuoteStatusLabels[forms.storeQuote.status] }}</strong></span
                >
                <span>Fluxo de estoque<strong>Reserva ao enviar, baixa ao aprovar</strong></span>
              </div>

              <div class="quote-status-actions modal-save">
                <button :disabled="saving" class="primary-button" type="submit">
                  <ShoppingCart :size="18" />
                  <span>{{ forms.storeQuote.id ? 'Salvar ajustes' : 'Criar carrinho' }}</span>
                </button>
                <button
                  v-if="forms.storeQuote.status === 'DRAFT'"
                  :disabled="saving"
                  class="secondary-button"
                  type="button"
                  @click="saveStoreQuoteStatusFromModal('SENT')"
                >
                  Enviar orçamento
                </button>
                <button
                  v-if="forms.storeQuote.status === 'SENT'"
                  :disabled="saving"
                  class="secondary-button"
                  type="button"
                  @click="saveStoreQuoteStatusFromModal('APPROVED')"
                >
                  Aprovar venda
                </button>
                <button
                  v-if="forms.storeQuote.status === 'SENT'"
                  :disabled="saving"
                  class="secondary-button"
                  type="button"
                  @click="saveStoreQuoteStatusFromModal('REFUSED')"
                >
                  Recusar orçamento
                </button>
                <button
                  v-if="forms.storeQuote.status === 'SENT'"
                  :disabled="saving"
                  class="secondary-button"
                  type="button"
                  @click="saveStoreQuoteStatusFromModal('EXPIRED')"
                >
                  Marcar expirado
                </button>
              </div>
            </form>
          </AppModal>
        </section>

        <section v-if="activeTab === 'master-customers' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Clientes</h2>
              <span>{{ listTotal('masterCustomers') }} clientes</span>
            </div>
            <div class="filters">
              <input
                v-model="pagination.masterCustomers.search"
                placeholder="Buscar cliente, documento ou parceiro"
                type="search"
                @input="resetListPage('masterCustomers')"
              />
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
              <div class="data-table-header master-customers-grid">
                <span>Cliente</span>
                <span>Veículos</span>
                <span>Gasto total</span>
                <span>Interações</span>
              </div>
              <article
                v-for="customer in listRows('masterCustomers')"
                :key="customer.id"
                class="data-table-row master-customers-grid clickable-row"
                @click="openRecord('Cliente', customer)"
              >
                <strong
                >{{ customer.name }}<small>{{ customer.email }} · {{ customer.phone }}</small></strong
                >
                <span class="centered-number">{{ customer.vehiclesCount }}</span>
                <strong class="money-value">R$ {{ money(customer.spent) }}</strong>
                <span class="interaction-cell"
                >{{ customer.frequency }} interações<small>{{
                    customer.partners.join(', ') || 'Sem parceiro vinculado'
                  }}</small></span
                >
              </article>
            </div>
            <PaginationControl
              :page="pagination.masterCustomers.page"
              :total-pages="listTotalPages('masterCustomers')"
              @update:page="setListPage('masterCustomers', $event)"
            />
          </section>
        </section>

        <section v-if="activeTab === 'master-workshops' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Oficinas parceiras</h2>
              <span>{{ listTotal('masterWorkshops') }} oficinas</span>
            </div>
            <div class="filters">
              <input
                v-model="pagination.masterWorkshops.search"
                placeholder="Buscar oficina ou administrador"
                type="search"
                @input="resetListPage('masterWorkshops')"
              />
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
              <div class="data-table-header master-partner-grid">
                <span>Oficina</span>
                <span>Faturamento</span>
                <span>Taxa</span>
                <span>AutoCare Hub</span>
                <span>Status</span>
              </div>
              <article
                v-for="partner in listRows('masterWorkshops')"
                :key="partner.id"
                class="data-table-row master-partner-grid clickable-row"
                @click="openRecord('Oficina parceira', partner)"
              >
                <strong
                >{{
                    partner.name
                  }}<small
                  >{{ partner.adminName }} · {{ partner.customersServed }} clientes ·
                    {{ partner.vehiclesServed }} veículos</small
                  ></strong
                >
                <span class="finance-stack">
                  <strong>R$ {{ money(partner.gross) }}</strong>
                  <small class="net-highlight">Líquido com taxa descontada R$ {{ money(partner.net) }}</small>
                </span>
                <span class="rate-stack">
                  <b class="fee-chip">{{ partner.feeRateLabel }}</b>
                  <small
                    :class="partner.nextTierGap > 0 ? 'tier-chip tier-chip--pending' : 'tier-chip tier-chip--done'"
                  >
                    {{
                      partner.nextTierGap > 0
                        ? `Faltam R$ ${money(partner.nextTierGap)} para ${partner.nextTierLabel}`
                        : 'Menor taxa ativa'
                    }}
                  </small>
                </span>
                <strong class="platform-fee-value">R$ {{ money(partner.feeAmount) }}</strong>
                <StatusBadge :label="partner.status" :value="partner.status" />
              </article>
            </div>
            <PaginationControl
              :page="pagination.masterWorkshops.page"
              :total-pages="listTotalPages('masterWorkshops')"
              @update:page="setListPage('masterWorkshops', $event)"
            />
          </section>
        </section>

        <section v-if="activeTab === 'master-stores' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Lojas de peças parceiras</h2>
              <span>{{ listTotal('masterStores') }} lojas</span>
            </div>
            <div class="filters">
              <input
                v-model="pagination.masterStores.search"
                placeholder="Buscar loja, administrador ou produto"
                type="search"
                @input="resetListPage('masterStores')"
              />
              <select v-model.number="pagination.masterStores.size" @change="resetListPage('masterStores')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select v-model="pagination.masterStores.sortBy">
                <option value="name">Ordenar por nome</option>
                <option value="gross">Ordenar por faturamento</option>
                <option value="feeAmount">Ordenar por taxa</option>
              </select>
              <select v-model="pagination.masterStores.sortDir">
                <option value="asc">Crescente</option>
                <option value="desc">Decrescente</option>
              </select>
            </div>
            <div class="data-table">
              <div class="data-table-header master-partner-grid">
                <span>Loja</span>
                <span>Faturamento</span>
                <span>Taxa</span>
                <span>AutoCare Hub</span>
                <span>Status</span>
              </div>
              <article
                v-for="partner in listRows('masterStores')"
                :key="partner.id"
                class="data-table-row master-partner-grid clickable-row"
                @click="openRecord('Loja parceira', partner)"
              >
                <strong
                >{{ partner.name }}<small>{{ partner.salesCount }} vendas · {{ partner.topProducts }}</small></strong
                >
                <span class="finance-stack">
                  <strong>R$ {{ money(partner.gross) }}</strong>
                  <small class="net-highlight">Líquido com taxa descontada R$ {{ money(partner.net) }}</small>
                </span>
                <span class="rate-stack">
                  <b class="fee-chip">{{ partner.feeRateLabel }}</b>
                  <small
                    :class="partner.nextTierGap > 0 ? 'tier-chip tier-chip--pending' : 'tier-chip tier-chip--done'"
                  >
                    {{
                      partner.nextTierGap > 0
                        ? `Faltam R$ ${money(partner.nextTierGap)} para ${partner.nextTierLabel}`
                        : 'Menor taxa ativa'
                    }}
                  </small>
                </span>
                <strong class="platform-fee-value">R$ {{ money(partner.feeAmount) }}</strong>
                <StatusBadge :label="partner.status" :value="partner.status" />
              </article>
            </div>
            <PaginationControl
              :page="pagination.masterStores.page"
              :total-pages="listTotalPages('masterStores')"
              @update:page="setListPage('masterStores', $event)"
            />
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
                <strong
                >{{
                    lead.companyName
                  }}<small>{{ lead.cnpj }} · {{ lead.city || 'Cidade não informada' }}</small></strong
                >
                <span
                >{{
                    lead.contactName
                  }}<small>{{ lead.email }} · {{ lead.phone }} · {{ lead.message || 'Sem mensagem' }}</small></span
                >
                <StatusBadge :label="lead.demoProfile === 'workshop' ? 'Oficina' : 'Loja de peças'" value="NEUTRAL" />
                <span>{{ new Date(lead.createdAt).toLocaleDateString('pt-BR') }}</span>
              </article>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'master-admins' && isMasterAdmin" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Cadastro de administrador parceiro</h2>
              <span>O cadastro foi centralizado na aba de contas para evitar duplicidade</span>
            </div>
            <div class="account-cta-row">
              <button class="primary-button" type="button" @click="openCreatePartnerAdminModal('WORKSHOP_ADMIN')">
                <UserPlus :size="18" />
                <span>Criar administrador de oficina</span>
              </button>
              <button class="secondary-button" type="button" @click="openCreatePartnerAdminModal('PARTS_STORE_ADMIN')">
                <UserPlus :size="18" />
                <span>Criar administrador de loja</span>
              </button>
            </div>
          </section>
        </section>

        <section v-if="activeTab === 'customer-partners' && isCustomerProfile" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Oficinas e lojas</h2>
              <span>Busque por nome, localização, especialidade ou produto</span>
            </div>
            <div class="filters">
              <input v-model="customerPartnerSearch" placeholder="Buscar parceiro" type="search" />
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
                  <Wrench :size="20" />
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
                  <Package :size="20" />
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
              <input v-model="customerPartSearch" placeholder="Ex.: filtro, freio, civic, onix" type="search" />
            </div>
            <div class="customer-part-grid">
              <button
                v-for="part in filteredCustomerParts"
                :key="part.id"
                type="button"
                @click="selectCustomerPart(part)"
              >
                <Package :size="20" />
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
                <strong
                >{{ store.name }}<small>{{ store.location }}</small></strong
                >
                <span>R$ {{ money(store.price) }}</span>
                <StatusBadge
                  :label="store.availableQuantity > 0 ? `${store.availableQuantity} un.` : 'Indisponível'"
                  :value="store.availableQuantity > 0 ? 'AVAILABLE' : 'OUT_OF_STOCK'"
                />
                <div class="row-actions">
                  <button
                    class="secondary-button compact-action"
                    type="button"
                    @click="openRecord('Contato da loja', store)"
                  >
                    Contato
                  </button>
                  <button
                    class="secondary-button compact-action"
                    type="button"
                    @click="addCustomerPartRequest(selectedCustomerPart, store)"
                  >
                    Solicitar
                  </button>
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
              <input v-model="forms.customerQuote.storeName" placeholder="Loja selecionada" />
              <input v-model="forms.customerQuote.workshopName" placeholder="Oficina selecionada" />
              <textarea
                v-model="forms.customerQuote.problemDescription"
                placeholder="Descreva o problema do veículo ou observações da compra"
              ></textarea>
              <label class="form-field">
                <span>Desconto negociado (%)</span>
                <input v-model.number="forms.customerQuote.discountPercent" max="100" min="0" step="0.01" type="number" />
              </label>
              <div class="quote-items quote-items--receipt">
                <article
                  v-for="(item, index) in forms.customerQuote.items"
                  :key="`${item.partId}-${index}`"
                  class="quote-receipt-item"
                >
                  <strong>{{ item.name }}</strong>
                  <span>{{ item.storeName || 'Loja a definir' }}</span>
                  <span>{{ item.quantity }} x R$ {{ money(item.estimatedPrice) }}</span>
                  <b>R$ {{ money(item.quantity * item.estimatedPrice) }}</b>
                  <button class="icon-action danger" type="button" @click="removeCustomerQuoteItem(index)">
                    Remover
                  </button>
                </article>
              </div>
              <article class="selected-record quote-total-card">
                <span>Subtotal<strong>R$ {{ money(customerQuoteSubtotal()) }}</strong></span>
                <span>Desconto<strong>R$ {{ money(customerQuoteDiscountAmount()) }}</strong></span>
                <span>Total final<strong>R$ {{ money(customerQuoteTotal()) }}</strong></span>
                <span>Simulação de compra. O parceiro pode responder com outro valor.</span>
              </article>
              <button class="primary-button" type="submit">
                <ShoppingCart :size="18" />
                <span>Enviar solicitação</span>
              </button>
            </form>
          </section>
        </section>

        <section v-if="activeTab === 'account'" class="screen-stack account-screen">
          <section class="section-block account-profile-card">
            <div class="section-heading">
              <h2>Minha conta</h2>
              <span>{{ currentUser?.username }}</span>
            </div>
            <form class="form-grid account-form" @submit.prevent="saveAccount">
              <label class="form-field">
                <span>Nome completo</span>
                <input v-model="forms.account.fullName" placeholder="Nome completo" required />
              </label>
              <label class="form-field">
                <span>E-mail de login</span>
                <input :value="currentUser?.username || auth.user?.username || ''" disabled />
              </label>
              <button :disabled="saving || !accountDirty" class="primary-button account-save-button" type="submit">
                Salvar dados
              </button>
            </form>
            <form class="form-grid account-form" @submit.prevent="changePassword">
              <label class="form-field">
                <span>Senha atual</span>
                <input v-model="forms.password.currentPassword" placeholder="Senha atual" required type="password" />
              </label>
              <label class="form-field">
                <span>Nova senha</span>
                <input
                  v-model="forms.password.newPassword"
                  minlength="6"
                  placeholder="Nova senha"
                  required
                  type="password"
                />
              </label>
              <button :disabled="saving || !passwordDirty" class="secondary-button" type="submit">Alterar senha</button>
            </form>
          </section>
        </section>

        <section v-if="activeTab === 'users'" class="screen-stack">
          <section v-if="auth.role === 'ADMIN'" class="section-block">
            <div class="section-heading">
              <h2>Contas</h2>
              <span>{{ listTotal('users') }} contas</span>
              <button class="primary-button" type="button" @click="openCreateUserModal">
                <UserPlus :size="18" />
                <span>Criar nova conta</span>
              </button>
            </div>
            <div class="filters">
              <input
                v-model="pagination.users.search"
                placeholder="Buscar usuário"
                type="search"
                @input="resetListPage('users')"
              />
              <select v-model.number="pagination.users.size" @change="resetListPage('users')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select
                v-model="pagination.users.role"
                @change="
                  resetListPage('users');
                  loadDashboard();
                "
              >
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
                <span>Empresa</span>
                <span>Status</span>
              </div>
              <article
                v-for="user in listRows('users')"
                :key="user.id"
                class="data-table-row users-grid clickable-row"
                @click="editUser(user)"
              >
                <strong
                >{{ user.fullName }}<small>{{ user.username }}</small></strong
                >
                <span>{{ roleDisplayLabel(user.role) }}</span>
                <span>{{ profileTypeLabel(user.profileType) }}</span>
                <span
                >{{ user.companyName || '-' }}<small>{{ companyTypeLabel(user.companyType) }}</small></span
                >
                <StatusBadge :value="user.active" />
              </article>
            </div>
            <PaginationControl
              :page="pagination.users.page"
              :total-pages="listTotalPages('users')"
              @update:page="setListPage('users', $event)"
            />
          </section>
        </section>

        <section v-if="activeTab === 'customers'" class="screen-stack">
          <section v-if="auth.role === 'ADMIN'" class="section-block action-hero">
            <div class="section-heading">
              <h2>Clientes</h2>
              <span>Cria o cadastro base usado por veículos e ordens</span>
            </div>
            <button class="primary-button action-hero-button" type="button" @click="openCreateCustomerModal">
              <UserPlus :size="18" />
              <span>Cadastrar cliente</span>
            </button>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Clientes</h2>
              <span>{{ listTotal('customers') }} registros</span>
            </div>
            <div class="filters">
              <input
                v-model="pagination.customers.search"
                placeholder="Buscar cliente, e-mail ou documento"
                type="search"
                @input="resetListPage('customers')"
              />
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
                <span
                >{{ customer.email }}<small>{{ customer.phone }}</small></span
                >
                <code>{{ customer.document }}</code>
                <StatusBadge :value="customer.active" />
              </article>
            </div>
            <PaginationControl
              :page="pagination.customers.page"
              :total-pages="listTotalPages('customers')"
              @update:page="setListPage('customers', $event)"
            />
          </section>
        </section>

        <section v-if="activeTab === 'vehicles'" class="screen-stack">
          <section v-if="auth.role === 'ADMIN'" class="section-block action-hero">
            <div class="section-heading">
              <h2>Veículos</h2>
              <span>Vinculado ao cliente</span>
            </div>
            <button class="primary-button action-hero-button" type="button" @click="openCreateVehicleModal">
              <Plus :size="18" />
              <span>Cadastrar veículo</span>
            </button>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Status dos veículos</h2>
              <span>{{ listTotal('vehicles') }} registros</span>
            </div>
            <div class="filters">
              <input
                v-model="pagination.vehicles.search"
                placeholder="Buscar placa, marca ou status"
                type="search"
                @input="resetListPage('vehicles')"
              />
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
                <span>Veículo</span>
                <span>Placa</span>
                <span>Cliente</span>
                <span>Documento</span>
                <span>Status</span>
              </div>
              <article
                v-for="vehicle in listRows('vehicles')"
                :key="vehicle.id"
                class="data-table-row vehicles-grid clickable-row"
                @click="openRecord('Veículo', vehicle)"
              >
                <span
                >{{ vehicle.brand }} {{ vehicle.model }}<small>{{ vehicle.year }}</small></span
                >
                <strong class="plate-chip">{{ vehicle.plate }}</strong>
                <strong>{{ vehicleOwner(vehicle).name || 'Cliente não informado' }}</strong>
                <span>{{ vehicleOwner(vehicle).document || '-' }}</span>
                <StatusBadge :label="vehicleStatusLabel(vehicle)" :value="vehicleStatusValue(vehicle)" />
              </article>
            </div>
            <PaginationControl
              :page="pagination.vehicles.page"
              :total-pages="listTotalPages('vehicles')"
              @update:page="setListPage('vehicles', $event)"
            />
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
              <h2>Cadastro de peças</h2>
              <span>Catálogo, custo, venda e reserva</span>
              <button class="primary-button" type="button" @click="openCreatePartModal">
                <Plus :size="18" />
                <span>Cadastrar peça</span>
              </button>
            </div>
            <p class="hint">
              Use o cadastro guiado para criar uma peça nova ou localizar uma peça existente antes de salvar.
            </p>
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
              <input
                v-model.number="forms.stockMovement.quantity"
                min="1"
                placeholder="Quantidade"
                required
                type="number"
              />
              <input
                v-model.number="forms.stockMovement.unitCost"
                min="0"
                placeholder="Custo unitário"
                step="0.01"
                type="number"
              />
              <input
                v-model.number="forms.stockMovement.unitPrice"
                min="0"
                placeholder="Venda unitária"
                step="0.01"
                type="number"
              />
              <input v-model="forms.stockMovement.reason" placeholder="Motivo ou observação" />
              <button :disabled="saving" class="primary-button" type="submit">
                <Package :size="18" />
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
              <input
                v-model="pagination.parts.search"
                placeholder="Buscar peça, SKU, categoria ou marca"
                type="search"
                @input="resetListPage('parts')"
              />
              <select v-model.number="pagination.parts.size" @change="resetListPage('parts')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select
                v-model="pagination.parts.lowStock"
                @change="
                  resetListPage('parts');
                  loadDashboard();
                "
              >
                <option value="">Todos</option>
                <option value="true">Somente baixo estoque</option>
              </select>
              <select
                v-model="pagination.parts.active"
                @change="
                  resetListPage('parts');
                  loadDashboard();
                "
              >
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
              </div>
              <article
                v-for="part in listRows('parts')"
                :key="part.id"
                :class="{ danger: (part.availableQuantity ?? part.stockQuantity) <= part.minimumStock }"
                class="data-table-row parts-grid clickable-row"
                @click="editPart(part)"
              >
                <strong>
                  {{ part.name }}
                  <small>{{ part.sku }} · {{ part.category }} · {{ part.brand }}</small>
                </strong>
                <span class="stock-number">
                  {{ part.availableQuantity ?? part.stockQuantity }} un.
                  <small>Total {{ part.stockQuantity }} · Min. {{ part.minimumStock }}</small>
                </span>
                <span class="stock-number">
                  {{ part.reservedQuantity || 0 }} un.
                  <small>{{
                      part.reservationExpiresAt
                        ? `Até ${new Date(part.reservationExpiresAt).toLocaleDateString('pt-BR')}`
                        : `${part.reservationDays || 3} dias`
                    }}</small>
                </span>
                <span>
                  Venda R$ {{ money(part.unitPrice) }}
                  <small>Custo R$ {{ money(part.costPrice) }}</small>
                </span>
                <StatusBadge
                  :label="stockStatusLabels[part.stockStatus] || part.stockStatus || 'Disponível'"
                  :value="part.stockStatus || 'AVAILABLE'"
                />
              </article>
            </div>
            <PaginationControl
              :page="pagination.parts.page"
              :total-pages="listTotalPages('parts')"
              @update:page="setListPage('parts', $event)"
            />
          </section>
        </section>

        <section v-if="activeTab === 'orders'" class="screen-stack">
          <section v-if="auth.role !== 'CUSTOMER'" class="order-flow-stats">
            <article v-for="item in orderFlowStats" :key="item.label">
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </article>
          </section>

          <section v-if="auth.role !== 'CUSTOMER' && can('CREATE_ORDER')" class="section-block action-hero">
            <div class="section-heading">
              <h2>Ordens de serviço</h2>
              <span>Crie uma nova OS em um fluxo guiado.</span>
            </div>
            <button class="primary-button action-hero-button" type="button" @click="openOrderModal">
              <Plus :size="18" />
              <span>Criar nova ordem de serviço</span>
            </button>
          </section>

          <AppModal
            :dirty="orderWizardDirty"
            :open="orderModalOpen"
            :subtitle="orderSteps[forms.orderWizard.step]"
            title="Criar nova ordem de serviço"
            @close="closeOrderModal"
          >
            <div class="order-wizard-modal">
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
                    <input v-model="forms.orderWizard.customer.name" placeholder="Nome do cliente" required />
                    <input
                      v-model="forms.orderWizard.customer.document"
                      placeholder="CPF/CNPJ somente números"
                      required
                    />
                    <input v-model="forms.orderWizard.customer.phone" placeholder="Telefone" required />
                    <input v-model="forms.orderWizard.customer.email" placeholder="E-mail" required type="email" />
                    <small class="form-hint"
                    >O cliente poderá acompanhar a OS usando este e-mail e a senha inicial admin.</small
                    >
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
                    <input v-model.number="forms.orderWizard.vehicle.year" placeholder="Ano" required type="number" />
                    <input v-model.number="forms.orderWizard.vehicle.mileage" placeholder="Km" required type="number" />
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
                  <input
                    v-model.number="forms.orderWizard.serviceQuantity"
                    min="1"
                    placeholder="Qtd. serviço"
                    type="number"
                  />
                  <select v-model="forms.orderWizard.partId">
                    <option value="">Peça inicial prevista</option>
                    <option v-for="part in data.parts" :key="part.id" :value="part.id">
                      {{ part.name }} - R$ {{ money(part.unitPrice) }}
                    </option>
                  </select>
                  <input
                    v-model.number="forms.orderWizard.partQuantity"
                    min="1"
                    placeholder="Qtd. peça"
                    type="number"
                  />
                  <textarea
                    v-model="forms.orderWizard.initialValueNotes"
                    placeholder="Observações de valores iniciais, se houver"
                  ></textarea>
                </div>
                <article class="selected-record">
                  <strong>R$ {{ money(estimatedOrderTotal) }}</strong>
                  <span>Estimativa inicial baseada nos itens selecionados.</span>
                </article>
              </div>

              <div v-if="forms.orderWizard.step === 5" class="wizard-panel">
                <article class="order-review">
                  <strong>Revisão da ordem antes de salvar</strong>
                  <div class="order-review-grid">
                    <span>
                      Cliente
                      <b>{{ selectedOrderCustomer?.name || forms.orderWizard.customer.name || 'Cliente novo' }}</b>
                    </span>
                    <span>
                      Veículo
                      <b>{{
                          selectedOrderVehicle
                            ? `${selectedOrderVehicle.brand} ${selectedOrderVehicle.model}`
                            : `${forms.orderWizard.vehicle.brand} ${forms.orderWizard.vehicle.model}`
                        }}</b>
                    </span>
                    <span>
                      Placa
                      <b>{{ selectedOrderVehicle?.plate || forms.orderWizard.vehicle.plate || '-' }}</b>
                    </span>
                    <span>
                      Problema relatado
                      <b>{{ forms.orderWizard.defects || 'Não informado' }}</b>
                    </span>
                    <span>
                      Serviço previsto
                      <b>{{ selectedOrderService?.name || 'Nenhum serviço selecionado' }}</b>
                    </span>
                    <span>
                      Peça prevista
                      <b>{{ selectedOrderPart?.name || 'Nenhuma peça inicial' }}</b>
                    </span>
                    <span>
                      Orçamento
                      <b>R$ {{ money(estimatedOrderTotal) }}</b>
                    </span>
                    <span>
                      Status
                      <b
                      >Sem orçamento: {{ statusLabels.RECEIVED }} · Com orçamento:
                        {{ statusLabels.WAITING_APPROVAL }}</b
                      >
                    </span>
                  </div>
                </article>
                <div class="wizard-actions">
                  <button
                    :disabled="saving"
                    class="secondary-button"
                    type="button"
                    @click="createOrderFromWizard(false)"
                  >
                    Salvar como orçamento pendente
                  </button>
                  <button :disabled="saving" class="primary-button" type="button" @click="createOrderFromWizard(true)">
                    <Plus :size="18" />
                    <span>Salvar e criar orçamento agora</span>
                  </button>
                </div>
              </div>

              <div class="wizard-actions">
                <button
                  :disabled="forms.orderWizard.step === 0 || saving"
                  class="secondary-button"
                  type="button"
                  @click="previousOrderStep"
                >
                  Voltar
                </button>
                <button
                  :disabled="forms.orderWizard.step === orderSteps.length - 1 || saving"
                  class="secondary-button"
                  type="button"
                  @click="nextOrderStep"
                >
                  Avançar
                </button>
              </div>
            </div>
          </AppModal>

          <section class="section-block">
            <div class="section-heading">
              <h2>Ordens de serviço</h2>
              <span>{{ listTotal('serviceOrders') }} registros</span>
            </div>
            <div v-if="auth.role !== 'CUSTOMER'" class="filters">
              <input
                v-model="pagination.serviceOrders.search"
                placeholder="Buscar cliente, veículo, placa, status ou ID"
                type="search"
                @input="resetListPage('serviceOrders')"
              />
              <select v-model.number="pagination.serviceOrders.size" @change="resetListPage('serviceOrders')">
                <option :value="5">5 por página</option>
                <option :value="10">10 por página</option>
                <option :value="20">20 por página</option>
              </select>
              <select
                v-model="pagination.serviceOrders.status"
                @change="
                  resetListPage('serviceOrders');
                  loadDashboard();
                "
              >
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
              <div class="data-table-header service-orders-grid">
                <span>Cliente</span>
                <span>Veículo</span>
                <span>Placa</span>
                <span>Status</span>
                <span>Data</span>
                <span>Total</span>
              </div>
              <article
                v-for="order in listRows('serviceOrders')"
                :key="order.id"
                class="data-table-row service-orders-grid clickable-row"
                @click="openRecord('Ordem de serviço', order)"
              >
                <strong
                >{{
                    orderCustomerName(order)
                  }}<small>{{
                      orderCustomer(order).phone || orderCustomer(order).email || 'Sem contato'
                    }}</small></strong
                >
                <span
                >{{
                    orderVehicleLabel(order)
                  }}<small
                  >{{ order.services?.length || 0 }} serviços · {{ order.parts?.length || 0 }} peças</small
                  ></span
                >
                <strong class="plate-chip">{{ orderPlate(order) }}</strong>
                <StatusBadge :value="order.status" />
                <span>{{ orderDate(order) }}</span>
                <strong>R$ {{ money(order.totalAmount) }}</strong>
              </article>
              <p v-if="!listTotal('serviceOrders') && !loading" class="empty-state">Nenhuma ordem encontrada.</p>
            </div>
            <PaginationControl
              v-if="auth.role !== 'CUSTOMER'"
              :page="pagination.serviceOrders.page"
              :total-pages="listTotalPages('serviceOrders')"
              @update:page="setListPage('serviceOrders', $event)"
            />
          </section>
        </section>

        <section v-if="activeTab === 'services'" class="screen-stack">
          <section class="section-block">
            <div class="section-heading">
              <h2>Cadastro de serviços</h2>
              <span>Catálogo da oficina</span>
              <button v-if="auth.role === 'ADMIN'" class="primary-button" type="button" @click="openCreateServiceModal">
                <Plus :size="18" />
                <span>Cadastrar serviço</span>
              </button>
            </div>
            <p class="hint">
              Cadastre novos serviços em uma janela dedicada ou clique em um serviço da lista para editar.
            </p>
          </section>

          <section class="section-block">
            <div class="section-heading">
              <h2>Serviços cadastrados</h2>
              <span>{{ listTotal('services') }} registros</span>
            </div>
            <div class="filters">
              <input
                v-model="pagination.services.search"
                placeholder="Buscar serviço"
                type="search"
                @input="resetListPage('services')"
              />
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
                <span>Status</span>
              </div>
              <article
                v-for="service in listRows('services')"
                :key="service.id"
                class="data-table-row services-grid clickable-row"
                @click="editService(service)"
              >
                <strong
                >{{ service.name }}<small>{{ service.description }}</small></strong
                >
                <span class="centered-number">{{ formatDuration(service.estimatedTimeInMinutes) }}</span>
                <strong class="money-value">R$ {{ money(service.basePrice) }}</strong>
                <StatusBadge :value="service.active !== false" />
              </article>
            </div>
            <PaginationControl
              :page="pagination.services.page"
              :total-pages="listTotalPages('services')"
              @update:page="setListPage('services', $event)"
            />
          </section>
        </section>

        <AppModal
          :open="customerModalOpen"
          subtitle="Dados cadastrais usados por veículos e ordens"
          title="Cadastrar cliente"
          @close="closeCustomerModal"
        >
          <form class="modal-form" @submit.prevent="createCustomer">
            <label class="form-field">
              <span>Nome</span>
              <input v-model="forms.customer.name" placeholder="Nome completo" required />
            </label>
            <label class="form-field">
              <span>CPF/CNPJ</span>
              <input v-model="forms.customer.document" placeholder="Somente números" required />
            </label>
            <label class="form-field">
              <span>Telefone</span>
              <input v-model="forms.customer.phone" placeholder="Telefone" required />
            </label>
            <label class="form-field">
              <span>E-mail</span>
              <input v-model="forms.customer.email" placeholder="email@exemplo.com" required type="email" />
              <small>Ao cadastrar um novo cliente, o backend cria login inicial com este e-mail e senha admin.</small>
            </label>
            <label class="form-field">
              <span>Rua</span>
              <input v-model="forms.customer.address.street" placeholder="Rua" required />
            </label>
            <label class="form-field">
              <span>Número</span>
              <input v-model="forms.customer.address.number" placeholder="Número" required />
            </label>
            <label class="form-field">
              <span>Bairro</span>
              <input v-model="forms.customer.address.neighborhood" placeholder="Bairro" required />
            </label>
            <label class="form-field">
              <span>Cidade</span>
              <input v-model="forms.customer.address.city" placeholder="Cidade" required />
            </label>
            <label class="form-field">
              <span>UF</span>
              <input v-model="forms.customer.address.state" maxlength="2" placeholder="SP" required />
            </label>
            <label class="form-field">
              <span>CEP</span>
              <input v-model="forms.customer.address.zipCode" placeholder="00000-000" required />
            </label>
            <label class="form-field">
              <span>Complemento</span>
              <input v-model="forms.customer.address.complement" placeholder="Complemento" />
            </label>
            <label class="check-row">
              <input v-model="forms.customer.active" type="checkbox" />
              <span>Cliente ativo</span>
            </label>
            <button :disabled="saving" class="primary-button modal-save" type="submit">
              <UserPlus :size="18" />
              <span>Cadastrar cliente</span>
            </button>
          </form>
        </AppModal>

        <AppModal
          :open="vehicleModalOpen"
          subtitle="Vincule o veículo a um cliente"
          title="Cadastrar veículo"
          @close="closeVehicleModal"
        >
          <form class="modal-form" @submit.prevent="createVehicle">
            <label class="form-field">
              <span>Cliente dono</span>
              <select v-model="forms.vehicle.customerId" required>
                <option value="">Selecione o cliente</option>
                <option v-for="customer in data.customers" :key="customer.id" :value="customer.id">
                  {{ customer.name }} · {{ customer.document }}
                </option>
              </select>
            </label>
            <label class="form-field">
              <span>Placa</span>
              <input v-model="forms.vehicle.plate" placeholder="ABC1D23" required />
            </label>
            <label class="form-field">
              <span>Marca</span>
              <input v-model="forms.vehicle.brand" placeholder="Marca" required />
            </label>
            <label class="form-field">
              <span>Modelo</span>
              <input v-model="forms.vehicle.model" placeholder="Modelo" required />
            </label>
            <label class="form-field">
              <span>Ano</span>
              <input v-model.number="forms.vehicle.year" min="1900" placeholder="Ano" required type="number" />
            </label>
            <label class="form-field">
              <span>Quilometragem</span>
              <input v-model.number="forms.vehicle.mileage" min="0" placeholder="Km" required type="number" />
            </label>
            <label class="check-row">
              <input v-model="forms.vehicle.active" type="checkbox" />
              <span>Veículo ativo</span>
            </label>
            <button :disabled="saving" class="primary-button modal-save" type="submit">
              <Plus :size="18" />
              <span>Cadastrar veículo</span>
            </button>
          </form>
        </AppModal>

        <AppModal
          :open="serviceModalOpen"
          :title="forms.service.id ? 'Editar serviço' : 'Cadastrar serviço'"
          subtitle="Catálogo da oficina"
          @close="closeServiceModal"
        >
          <form class="modal-form service-modal-form" @submit.prevent="createWorkshopService">
            <label class="form-field">
              <span>Nome do serviço</span>
              <input v-model="forms.service.name" placeholder="Ex.: Revisão preventiva" required />
            </label>
            <label class="form-field">
              <span>Preço base</span>
              <input v-model.number="forms.service.basePrice" min="0" required step="0.01" type="number" />
            </label>

            <div v-if="serviceDuplicateMatches.length" class="duplicate-suggestions">
              <strong>Serviços parecidos já cadastrados</strong>
              <button
                v-for="service in serviceDuplicateMatches"
                :key="`service-match-${service.id}`"
                type="button"
                @click="selectExistingServiceForModal(service)"
              >
                <span
                >{{ service.name }}<small>{{ service.description }}</small></span
                >
                <b>Editar existente</b>
              </button>
            </div>

            <label class="form-field">
              <span>Tempo previsto</span>
              <input v-model.number="forms.service.estimatedTimeInMinutes" min="1" required type="number" />
            </label>
            <label class="form-field service-description-field">
              <span>Descrição</span>
              <textarea
                v-model="forms.service.description"
                placeholder="Descrição do serviço, escopo e observações"
                required
              ></textarea>
            </label>
            <label class="check-row">
              <input v-model="forms.service.active" type="checkbox" />
              <span>Serviço ativo</span>
            </label>
            <button :disabled="saving" class="primary-button modal-save" type="submit">
              <Wrench :size="18" />
              <span>{{ forms.service.id ? 'Salvar serviço' : 'Cadastrar serviço' }}</span>
            </button>
          </form>
        </AppModal>

        <AppModal
          :open="partModalOpen"
          :title="forms.part.id ? 'Editar peça' : 'Cadastrar peça'"
          subtitle="Estoque, preços e identificação da peça"
          @close="closePartModal"
        >
          <form class="modal-form part-modal-form" @submit.prevent="createPart">
            <label class="form-field">
              <span>Nome da peça</span>
              <input v-model="forms.part.name" placeholder="Ex.: Filtro de óleo" required />
            </label>
            <label class="form-field">
              <span>SKU</span>
              <input v-model="forms.part.sku" placeholder="Código interno ou SKU" required />
            </label>

            <div v-if="partDuplicateMatches.length" class="duplicate-suggestions">
              <strong>Peças parecidas já cadastradas</strong>
              <button
                v-for="part in partDuplicateMatches"
                :key="`match-${part.id}`"
                type="button"
                @click="selectExistingPartForModal(part)"
              >
                <span
                >{{ part.name }}<small>{{ part.sku }} · {{ part.brand }}</small></span
                >
                <b>Editar existente</b>
              </button>
            </div>

            <label class="form-field">
              <span>Categoria</span>
              <input v-model="forms.part.category" placeholder="Categoria" required />
            </label>
            <label class="form-field">
              <span>Subcategoria</span>
              <input v-model="forms.part.subcategory" placeholder="Subcategoria" />
            </label>
            <label class="form-field">
              <span>Marca</span>
              <input v-model="forms.part.brand" placeholder="Marca" required />
            </label>
            <label class="form-field">
              <span>Valor de custo</span>
              <input
                v-model="forms.part.costPrice"
                inputmode="decimal"
                placeholder="0,00"
                required
                @blur="forms.part.costPrice = formatDecimalInput(normalizeDecimalInput(forms.part.costPrice))"
              />
            </label>
            <label class="form-field">
              <span>Valor de venda</span>
              <input
                v-model="forms.part.unitPrice"
                inputmode="decimal"
                placeholder="0,00"
                required
                @blur="forms.part.unitPrice = formatDecimalInput(normalizeDecimalInput(forms.part.unitPrice))"
              />
            </label>
            <label class="form-field">
              <span>Quantidade em estoque</span>
              <input v-model.number="forms.part.stockQuantity" min="0" required type="number" />
            </label>
            <label class="form-field">
              <span>Estoque mínimo</span>
              <input v-model.number="forms.part.minimumStock" min="0" required type="number" />
            </label>
            <label class="form-field">
              <span>Dias de bloqueio em orçamento</span>
              <input v-model.number="forms.part.reservationDays" min="1" required type="number" />
            </label>
            <label class="form-field part-description-field">
              <span>Descrição</span>
              <textarea
                v-model="forms.part.description"
                placeholder="Descrição da peça, aplicação ou observações"
                required
              ></textarea>
            </label>
            <label class="check-row">
              <input v-model="forms.part.active" type="checkbox" />
              <span>Peça ativa</span>
            </label>
            <button :disabled="saving" class="primary-button modal-save" type="submit">
              <Package :size="18" />
              <span>{{ forms.part.id ? 'Salvar peça' : 'Cadastrar peça' }}</span>
            </button>
          </form>
        </AppModal>

        <AppModal
          :open="userModalOpen"
          :subtitle="userModalIsEmployee ? 'Dados, função, permissões e status' : 'Conta do sistema'"
          :title="
            forms.user.id
              ? userModalIsEmployee
                ? 'Editar funcionário'
                : 'Editar conta'
              : userModalIsEmployee
                ? 'Criar funcionário'
                : 'Criar nova conta'
          "
          @close="closeUserModal"
        >
          <form class="modal-form account-modal-form" @submit.prevent="saveUser">
            <label class="form-field">
              <span>Nome completo</span>
              <input v-model="forms.user.fullName" placeholder="Ex.: Carlos Atendimento" required />
            </label>
            <label class="form-field">
              <span>E-mail de login</span>
              <input v-model="forms.user.username" placeholder="email@autocarehub.com" required type="email" />
            </label>
            <label class="form-field">
              <span>{{ forms.user.id ? 'Nova senha' : 'Senha inicial' }}</span>
              <input
                v-model="forms.user.password"
                :placeholder="forms.user.id ? 'Preencha apenas se quiser alterar' : 'Mínimo 8 caracteres'"
                :required="!forms.user.id"
                minlength="8"
                type="password"
              />
            </label>
            <label class="form-field">
              <span>Perfil</span>
              <select v-model="forms.user.profileType" @change="syncUserProfileDefaults">
                <option v-for="option in userProfileOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </label>
            <label v-if="canEditUserCompanyFields" class="form-field">
              <span>Role geral</span>
              <select v-model="forms.user.role" disabled>
                <option value="ADMIN">Administrador</option>
                <option value="EMPLOYEE">Funcionário</option>
                <option value="CUSTOMER">Cliente</option>
              </select>
            </label>
            <label
              v-if="canEditUserCompanyFields && !['MASTER_ADMIN', 'CUSTOMER_OWNER'].includes(forms.user.profileType)"
              class="check-row"
            >
              <input v-model="forms.user.createCompany" type="checkbox" @change="syncUserProfileDefaults" />
              <span>Criar nova empresa para esta conta</span>
            </label>
            <label
              v-if="
                canEditUserCompanyFields &&
                !forms.user.createCompany &&
                !['CUSTOMER_OWNER'].includes(forms.user.profileType)
              "
              class="form-field"
            >
              <span>Empresa existente</span>
              <select v-model="forms.user.companyId" required @change="selectUserCompany">
                <option disabled value="">Selecione uma empresa</option>
                <option v-for="company in companyOptions" :key="company.id" :value="company.id">
                  {{ company.name }} · {{ companyTypeLabel(company.type) }}
                </option>
              </select>
            </label>
            <label
              v-if="
                canEditUserCompanyFields &&
                forms.user.createCompany &&
                !['MASTER_ADMIN', 'CUSTOMER_OWNER'].includes(forms.user.profileType)
              "
              class="form-field"
            >
              <span>Nome da nova empresa</span>
              <input v-model="forms.user.companyName" placeholder="Nome da oficina ou loja" required />
            </label>
            <label
              v-if="canEditUserCompanyFields && !['CUSTOMER_OWNER'].includes(forms.user.profileType)"
              class="form-field"
            >
              <span>Tipo de empresa</span>
              <select v-model="forms.user.companyType" disabled>
                <option value="PLATFORM">Plataforma AutoCare Hub</option>
                <option value="WORKSHOP">Oficina</option>
                <option value="PARTS_STORE">Loja de peças</option>
              </select>
            </label>
            <div v-else class="modal-readonly-grid">
              <span
              >Empresa vinculada<strong>{{ forms.user.companyName || currentUser?.companyName || '-' }}</strong></span
              >
              <span
              >Tipo de empresa<strong>{{
                  companyTypeLabel(forms.user.companyType || currentUser?.companyType)
                }}</strong></span
              >
            </div>
            <label v-if="userModalIsEmployee" class="form-field">
              <span>Função do funcionário</span>
              <select v-model="forms.user.employeeSubRole">
                <option value="">Sem subfunção</option>
                <option v-if="!userModalIsStoreEmployee" value="MECHANIC">Mecânico</option>
                <option value="ATTENDANT">Atendente</option>
                <option value="UNSPECIFIED">Funcionário sem especificação</option>
              </select>
            </label>
            <label v-if="forms.user.profileType === 'CUSTOMER_OWNER'" class="form-field">
              <span>Cliente vinculado</span>
              <input v-model="forms.user.customerId" placeholder="Informe apenas se já existir vínculo com cliente" />
            </label>
            <label class="check-row">
              <input v-model="forms.user.active" type="checkbox" />
              <span>{{ userModalIsEmployee ? 'Funcionário ativo' : 'Conta ativa' }}</span>
            </label>
            <div v-if="userModalEmployeeMetrics.length" class="modal-readonly-grid employee-modal-metrics">
              <span v-for="metric in userModalEmployeeMetrics" :key="`modal-${metric.label}`">
                {{ metric.label }}<strong>{{ metric.value }}</strong>
              </span>
            </div>
            <div class="permission-grid account-permission-grid">
              <label v-for="permission in permissionDefinitions" :key="`account-${permission.id}`">
                <input
                  :checked="forms.user.permissions.includes(permission.id)"
                  type="checkbox"
                  @change="toggleUserPermission(permission.id)"
                />
                <span>{{ permission.label }}</span>
              </label>
            </div>
            <button
              :disabled="saving || (Boolean(forms.user.id) && !userFormDirty)"
              class="primary-button modal-save"
              type="submit"
            >
              <UserPlus :size="18" />
              <span>{{
                  forms.user.id
                    ? userModalIsEmployee
                      ? 'Salvar funcionário'
                      : 'Salvar conta'
                    : userModalIsEmployee
                      ? 'Criar funcionário'
                      : 'Criar conta'
                }}</span>
            </button>
          </form>
        </AppModal>

        <AppModal
          :dirty="detailModalDirty"
          :open="Boolean(selectedRecord)"
          :subtitle="selectedRecordType"
          :title="detailModalTitle"
          @close="closeRecord"
        >
          <form v-if="isCustomerDetail" class="modal-form" @submit.prevent="saveDetailModal">
            <input v-model="modalDraft.customer.name" placeholder="Nome" />
            <input v-model="modalDraft.customer.document" placeholder="CPF/CNPJ" />
            <input v-model="modalDraft.customer.phone" placeholder="Telefone" />
            <input v-model="modalDraft.customer.email" placeholder="E-mail" type="email" />
            <input v-model="modalDraft.customer.address.street" placeholder="Rua" />
            <input v-model="modalDraft.customer.address.number" placeholder="Número" />
            <input v-model="modalDraft.customer.address.neighborhood" placeholder="Bairro" />
            <input v-model="modalDraft.customer.address.city" placeholder="Cidade" />
            <input v-model="modalDraft.customer.address.state" maxlength="2" placeholder="UF" />
            <input v-model="modalDraft.customer.address.zipCode" placeholder="CEP" />
            <input v-model="modalDraft.customer.address.complement" placeholder="Complemento" />
            <label class="check-row">
              <input v-model="modalDraft.customer.active" type="checkbox" />
              <span>Cliente ativo</span>
            </label>
            <button :disabled="!detailModalDirty || saving" class="primary-button modal-save" type="submit">
              Salvar alterações
            </button>
          </form>

          <form v-else-if="isWorkshopDetail" class="modal-form" @submit.prevent="saveDetailModal">
            <input
              v-model="modalDraft.partner.companyName"
              :placeholder="selectedRecordType === 'Loja parceira' ? 'Nome da loja' : 'Nome da oficina'"
            />
            <input v-model="modalDraft.partner.fullName" placeholder="Responsável" />
            <label class="check-row">
              <input v-model="modalDraft.partner.active" type="checkbox" />
              <span>{{ selectedRecordType === 'Loja parceira' ? 'Loja ativa' : 'Oficina ativa' }}</span>
            </label>
            <div class="modal-readonly-grid">
              <span
              >Faturamento bruto<strong>R$ {{ money(selectedRecord.gross) }}</strong></span
              >
              <span
              >Taxa AutoCare Hub<strong>{{ selectedRecord.feeRateLabel || '-' }}</strong></span
              >
              <span
              >Valor líquido<strong>R$ {{ money(selectedRecord.net) }}</strong></span
              >
              <span
              >Status<strong>{{ selectedRecord.status || 'Ativa' }}</strong></span
              >
            </div>
            <button :disabled="!detailModalDirty || saving" class="primary-button modal-save" type="submit">
              Salvar alterações
            </button>
          </form>

          <form v-else-if="isVehicleDetail" class="modal-form" @submit.prevent="saveDetailModal">
            <label class="form-field">
              <span>Cliente dono</span>
              <select v-model="modalDraft.vehicle.customerId" required>
                <option value="">Selecione o cliente</option>
                <option v-for="customer in data.customers" :key="customer.id" :value="customer.id">
                  {{ customer.name }} · {{ customer.document }}
                </option>
              </select>
            </label>
            <label class="form-field">
              <span>Placa</span>
              <input v-model="modalDraft.vehicle.plate" placeholder="ABC1D23" required />
            </label>
            <label class="form-field">
              <span>Marca</span>
              <input v-model="modalDraft.vehicle.brand" placeholder="Marca" required />
            </label>
            <label class="form-field">
              <span>Modelo</span>
              <input v-model="modalDraft.vehicle.model" placeholder="Modelo" required />
            </label>
            <label class="form-field">
              <span>Ano</span>
              <input v-model.number="modalDraft.vehicle.year" min="1900" required type="number" />
            </label>
            <label class="form-field">
              <span>Quilometragem</span>
              <input v-model.number="modalDraft.vehicle.mileage" min="0" required type="number" />
            </label>
            <div class="modal-readonly-grid">
              <span
              >Cliente atual<strong>{{ vehicleOwner(selectedRecord).name || '-' }}</strong></span
              >
              <span
              >Documento<strong>{{ vehicleOwner(selectedRecord).document || '-' }}</strong></span
              >
              <span
              >Status atual<strong>{{ vehicleStatusLabel(selectedRecord) }}</strong></span
              >
              <span
              >OS vinculada<strong>{{ selectedRecord.diagnosticNotes || 'Sem ordem ativa' }}</strong></span
              >
            </div>
            <label class="check-row">
              <input v-model="modalDraft.vehicle.active" type="checkbox" />
              <span>Veículo ativo</span>
            </label>
            <button :disabled="!detailModalDirty || saving" class="primary-button modal-save" type="submit">
              Salvar veículo
            </button>
          </form>

          <form v-else-if="isOrderDetail" class="modal-form order-detail-form" @submit.prevent="saveDetailModal">
            <div class="modal-readonly-grid order-detail-summary">
              <span
              >Cliente<strong>{{ orderCustomerName(selectedRecord) }}</strong></span
              >
              <span
              >Contato<strong>{{
                  orderCustomer(selectedRecord).phone || orderCustomer(selectedRecord).email || '-'
                }}</strong></span
              >
              <span
              >Veículo<strong>{{ orderVehicleLabel(selectedRecord) }}</strong></span
              >
              <span
              >Placa<strong>{{ orderPlate(selectedRecord) }}</strong></span
              >
              <span
              >Data<strong>{{ orderDate(selectedRecord) }}</strong></span
              >
              <span
              >Orçamento<strong>{{ orderBudgetStatus(selectedRecord) }}</strong></span
              >
            </div>

            <label class="form-field">
              <span>Status atual</span>
              <select v-model="modalDraft.order.status">
                <option v-for="status in statuses" :key="`detail-${status}`" :value="status">
                  {{ statusLabels[status] || status }}
                </option>
              </select>
            </label>
            <label class="form-field">
              <span>Valor total</span>
              <input :value="`R$ ${money(selectedRecord.totalAmount)}`" disabled />
            </label>
            <label class="form-field order-notes-field">
              <span>Problema relatado e observações</span>
              <textarea
                v-model="modalDraft.order.diagnosticNotes"
                placeholder="Descreva o problema relatado, diagnóstico inicial e observações da recepção"
              ></textarea>
            </label>

            <div class="order-detail-items">
              <article>
                <strong>Serviços solicitados</strong>
                <span v-if="!selectedRecord.services?.length">Nenhum serviço vinculado.</span>
                <ul v-else>
                  <li v-for="service in selectedRecord.services" :key="service.serviceId || service.id || service.name">
                    {{ service.name || service.serviceName || service.serviceId }} · {{ service.quantity || 1 }} un.
                  </li>
                </ul>
              </article>
              <article>
                <strong>Peças e insumos</strong>
                <span v-if="!selectedRecord.parts?.length">Nenhuma peça vinculada.</span>
                <ul v-else>
                  <li v-for="part in selectedRecord.parts" :key="part.partId || part.id || part.name">
                    {{ part.name || part.partName || part.partId }} · {{ part.quantity || 1 }} un.
                  </li>
                </ul>
              </article>
            </div>

            <div class="detail-actions modal-save">
              <button :disabled="!detailModalDirty || saving" class="primary-button" type="submit">
                Salvar ajustes
              </button>
              <button
                v-if="!isCustomerProfile && can('CREATE_BUDGET') && canGenerateBudget(selectedRecord)"
                :disabled="saving"
                class="secondary-button"
                type="button"
                @click="generateBudgetFromSelectedOrder"
              >
                Gerar orçamento
              </button>
              <button
                v-if="!isCustomerProfile && canApproveBudget(selectedRecord)"
                :disabled="saving"
                class="secondary-button"
                type="button"
                @click="approveBudgetFromSelectedOrder"
              >
                Aprovar orçamento
              </button>
            </div>
          </form>

          <dl v-else>
            <template v-for="entry in displayRecordEntries(selectedRecord)" :key="entry.key">
              <dt>{{ entry.label }}</dt>
              <dd>{{ entry.value }}</dd>
            </template>
          </dl>

          <div class="detail-actions">
            <button
              v-if="selectedRecordType === 'Usuário'"
              class="secondary-button"
              type="button"
              @click="editUser(selectedRecord)"
            >
              Editar usuário
            </button>
            <button
              v-if="selectedRecordType === 'Funcionário da loja'"
              class="secondary-button"
              type="button"
              @click="editUser(selectedRecord)"
            >
              Editar funcionário
            </button>
            <button
              v-if="selectedRecordType === 'Carrinho da loja'"
              class="secondary-button"
              type="button"
              @click="editStoreQuote(selectedRecord)"
            >
              Editar carrinho
            </button>
            <button
              v-if="selectedRecordType === 'Peça para comparar'"
              class="secondary-button"
              type="button"
              @click="addCustomerPartRequest(selectedRecord)"
            >
              Adicionar à solicitação
            </button>
            <button
              v-if="selectedRecordType === 'Loja de peças'"
              class="secondary-button"
              type="button"
              @click="requestStoreQuote(selectedRecord)"
            >
              Solicitar orçamento
            </button>
            <button
              v-if="selectedRecordType === 'Oficina'"
              class="secondary-button"
              type="button"
              @click="contactWorkshop(selectedRecord)"
            >
              Contatar oficina
            </button>
          </div>
        </AppModal>

        <AppModal
          :open="confirmDialogOpen"
          :title="confirmDialog.title"
          subtitle="Confirmação"
          @close="closeConfirmDialog"
        >
          <div :class="`tone-${confirmDialog.tone}`" class="confirmation-content">
            <p>{{ confirmDialog.message }}</p>
            <div class="confirmation-actions">
              <button class="secondary-button" type="button" @click="closeConfirmDialog">
                {{ confirmDialog.cancelLabel }}
              </button>
              <button
                :class="{ 'danger-action': confirmDialog.tone === 'danger' }"
                class="primary-button"
                type="button"
                @click="confirmDialogAction"
              >
                {{ confirmDialog.confirmLabel }}
              </button>
            </div>
          </div>
        </AppModal>

        <div v-if="loading" class="loading-bar">Carregando dados...</div>
      </section>
    </div>
  </main>
</template>
