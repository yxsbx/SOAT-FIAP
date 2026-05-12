const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

function toQueryString(params = {}) {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      searchParams.set(key, value);
    }
  });

  const query = searchParams.toString();
  return query ? `?${query}` : '';
}

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem('autocare.token');
  const headers = {
    Accept: 'application/json',
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  };

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message = data?.message || `Erro HTTP ${response.status}`;
    const error = new Error(message);
    error.status = response.status;
    error.path = path;
    throw error;
  }

  return data;
}

export function login(username, password) {
  return apiRequest('/api/v1/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export const resources = {
  customers: (params) => apiRequest(`/api/v1/customers${toQueryString(params)}`),
  vehicles: (params) => apiRequest(`/api/v1/vehicles${toQueryString(params)}`),
  services: (params) => apiRequest(`/api/v1/workshop-services${toQueryString(params)}`),
  parts: (params) => apiRequest(`/api/v1/parts${toQueryString(params)}`),
  lowStockParts: (params = {}) => apiRequest(`/api/v1/parts${toQueryString({ ...params, lowStock: true })}`),
  serviceOrders: (params) => apiRequest(`/api/v1/service-orders${toQueryString(params)}`),
  averageExecutionTime: () => apiRequest('/api/v1/service-orders/metrics/average-execution-time'),
  customerServiceOrders: (customerId) =>
    apiRequest(`/api/v1/customers/${customerId}/service-orders`),
  customerVehicles: (customerId) => apiRequest(`/api/v1/customers/${customerId}/vehicles`),
  createCustomer: (payload) =>
    apiRequest('/api/v1/customers', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  createVehicle: (payload) =>
    apiRequest('/api/v1/vehicles', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  createPart: (payload) =>
    apiRequest('/api/v1/parts', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  updatePartStock: (partId, stockQuantity) =>
    apiRequest(`/api/v1/parts/${partId}/stock`, {
      method: 'PATCH',
      body: JSON.stringify({ stockQuantity: Number(stockQuantity) }),
    }),
  createWorkshopService: (payload) =>
    apiRequest('/api/v1/workshop-services', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  createServiceOrder: (payload) =>
    apiRequest('/api/v1/service-orders', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  addServiceToOrder: (serviceOrderId, payload) =>
    apiRequest(`/api/v1/service-orders/${serviceOrderId}/services`, {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  addPartToOrder: (serviceOrderId, payload) =>
    apiRequest(`/api/v1/service-orders/${serviceOrderId}/parts`, {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  generateBudget: (serviceOrderId) =>
    apiRequest(`/api/v1/service-orders/${serviceOrderId}/budget/generate`, {
      method: 'POST',
    }),
  approveBudget: (serviceOrderId) =>
    apiRequest(`/api/v1/service-orders/${serviceOrderId}/budget/approve`, {
      method: 'POST',
    }),
  updateOrderStatus: (serviceOrderId, status) =>
    apiRequest(`/api/v1/service-orders/${serviceOrderId}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    }),
};
