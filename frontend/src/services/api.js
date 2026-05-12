const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

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
    throw new Error(message);
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
  customers: () => apiRequest('/api/v1/customers?size=5'),
  vehicles: () => apiRequest('/api/v1/vehicles?size=5'),
  services: () => apiRequest('/api/v1/workshop-services?size=5'),
  parts: () => apiRequest('/api/v1/parts?size=5'),
  lowStockParts: () => apiRequest('/api/v1/parts?lowStock=true&size=5'),
  serviceOrders: () => apiRequest('/api/v1/service-orders?size=5'),
  averageExecutionTime: () => apiRequest('/api/v1/service-orders/metrics/average-execution-time'),
  customerServiceOrders: (customerId) =>
    apiRequest(`/api/v1/customers/${customerId}/service-orders`),
};
