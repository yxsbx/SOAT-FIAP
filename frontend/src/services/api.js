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
        ...(options.body ? {'Content-Type': 'application/json'} : {}),
        ...(token ? {Authorization: `Bearer ${token}`} : {}),
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
        body: JSON.stringify({username, password}),
    });
}

export function createDemoLead(payload) {
    return apiRequest('/api/v1/demo-leads', {
        method: 'POST',
        body: JSON.stringify(payload),
    });
}

export const resources = {
    currentUser: () => apiRequest('/api/v1/users/me'),
    updateCurrentUser: (payload) =>
        apiRequest('/api/v1/users/me', {
            method: 'PUT',
            body: JSON.stringify(payload),
        }),
    changeCurrentPassword: (payload) =>
        apiRequest('/api/v1/users/me/password', {
            method: 'PATCH',
            body: JSON.stringify(payload),
        }),
    homePreferences: () => apiRequest('/api/v1/users/me/preferences/home'),
    demoLeads: () => apiRequest('/api/v1/demo-leads'),
    saveHomePreferences: (payload) =>
        apiRequest('/api/v1/users/me/preferences/home', {
            method: 'PUT',
            body: JSON.stringify(payload),
        }),
    users: (params) => apiRequest(`/api/v1/users${toQueryString(params)}`),
    partners: () => apiRequest('/api/v1/users/partners'),
    createUser: (payload) =>
        apiRequest('/api/v1/users', {
            method: 'POST',
            body: JSON.stringify(payload),
        }),
    updateUser: (userId, payload) =>
        apiRequest(`/api/v1/users/${userId}`, {
            method: 'PUT',
            body: JSON.stringify(payload),
        }),
    resetUserPassword: (userId, newPassword) =>
        apiRequest(`/api/v1/users/${userId}/password`, {
            method: 'PATCH',
            body: JSON.stringify({newPassword}),
        }),
    customers: (params) => apiRequest(`/api/v1/customers${toQueryString(params)}`),
    updateCustomer: (customerId, payload) =>
        apiRequest(`/api/v1/customers/${customerId}`, {
            method: 'PUT',
            body: JSON.stringify(payload),
        }),
    vehicles: (params) => apiRequest(`/api/v1/vehicles${toQueryString(params)}`),
    updateVehicle: (vehicleId, payload) =>
        apiRequest(`/api/v1/vehicles/${vehicleId}`, {
            method: 'PUT',
            body: JSON.stringify(payload),
        }),
    services: (params) => apiRequest(`/api/v1/workshop-services${toQueryString(params)}`),
    updateWorkshopService: (serviceId, payload) =>
        apiRequest(`/api/v1/workshop-services/${serviceId}`, {
            method: 'PUT',
            body: JSON.stringify(payload),
        }),
    parts: (params) => apiRequest(`/api/v1/parts${toQueryString(params)}`),
    lowStockParts: (params = {}) => apiRequest(`/api/v1/parts${toQueryString({...params, lowStock: true})}`),
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
    updatePart: (partId, payload) =>
        apiRequest(`/api/v1/parts/${partId}`, {
            method: 'PUT',
            body: JSON.stringify(payload),
        }),
    updatePartStock: (partId, stockQuantity) =>
        apiRequest(`/api/v1/parts/${partId}/stock`, {
            method: 'PATCH',
            body: JSON.stringify({stockQuantity: Number(stockQuantity)}),
        }),
    registerStockMovement: (partId, payload) =>
        apiRequest(`/api/v1/parts/${partId}/stock-movement`, {
            method: 'PATCH',
            body: JSON.stringify(payload),
        }),
    configurePartReservation: (partId, reservationDays) =>
        apiRequest(`/api/v1/parts/${partId}/reservation`, {
            method: 'PATCH',
            body: JSON.stringify({reservationDays: Number(reservationDays)}),
        }),
    reservePart: (partId, quantity) =>
        apiRequest(`/api/v1/parts/${partId}/reserve`, {
            method: 'PATCH',
            body: JSON.stringify({quantity: Number(quantity)}),
        }),
    releasePartReservation: (partId, quantity) =>
        apiRequest(`/api/v1/parts/${partId}/release-reservation`, {
            method: 'PATCH',
            body: JSON.stringify({quantity: Number(quantity)}),
        }),
    commitPartReservation: (partId, payload) =>
        apiRequest(`/api/v1/parts/${partId}/commit-reservation`, {
            method: 'PATCH',
            body: JSON.stringify(payload),
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
            body: JSON.stringify({status}),
        }),
};
