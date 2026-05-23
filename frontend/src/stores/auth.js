import {defineStore} from 'pinia';
import {login as loginRequest} from '@/services/api';

function parseJwt(token) {
    try {
        const [, payload] = token.split('.');
        const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
        return JSON.parse(window.atob(normalized));
    } catch {
        return null;
    }
}

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: null,
        user: null,
    }),
    getters: {
        isAuthenticated: (state) => Boolean(state.token && state.user),
        role: (state) => state.user?.role,
        customerId: (state) => state.user?.customerId,
    },
    actions: {
        restore() {
            if (this.token && this.user) {
                return;
            }

            const token = localStorage.getItem('autocare.token');
            if (!token) {
                return;
            }

            const payload = parseJwt(token);
            if (!payload || (payload.exp && payload.exp * 1000 < Date.now())) {
                this.logout();
                return;
            }

            this.token = token;
            this.user = {
                id: payload.userId,
                username: payload.sub,
                role: payload.role,
                customerId: payload.customerId,
            };
        },
        async login(username, password) {
            const response = await loginRequest(username, password);
            localStorage.setItem('autocare.token', response.accessToken);
            this.token = response.accessToken;
            const payload = parseJwt(response.accessToken);
            this.user = {
                id: payload.userId,
                username: payload.sub,
                role: payload.role,
                customerId: payload.customerId,
            };
        },
        logout() {
            localStorage.removeItem('autocare.token');
            this.token = null;
            this.user = null;
        },
    },
});
