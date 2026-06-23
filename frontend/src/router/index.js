import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '@/pages/LoginView.vue';
import DashboardView from '@/pages/DashboardView.vue';
import PreviewView from '@/pages/PreviewView.vue';
import DemoView from '@/pages/DemoView.vue';
import PersonaView from '@/pages/PersonaView.vue';
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: PreviewView, meta: { public: true } },
    { path: '/preview', redirect: '/' },
    { path: '/demo', name: 'demo', component: DemoView, meta: { public: true } },
    {
      path: '/oficina/admin',
      name: 'workshop-admin-view',
      component: PersonaView,
      props: { persona: 'workshopAdmin' },
      meta: { public: true },
    },
    {
      path: '/oficina/usuario',
      name: 'employee-view',
      component: PersonaView,
      props: { persona: 'employee' },
      meta: { public: true },
    },
    {
      path: '/master',
      name: 'master-view',
      component: PersonaView,
      props: { persona: 'master' },
      meta: { public: true },
    },
    {
      path: '/loja-peças',
      name: 'parts-store-view',
      component: PersonaView,
      props: { persona: 'partsStore' },
      meta: { public: true },
    },
    {
      path: '/cliente',
      name: 'customer-view',
      component: PersonaView,
      props: { persona: 'customer' },
      meta: { public: true },
    },
    { path: '/login', name: 'login', component: LoginView, meta: { public: true } },
    { path: '/app', name: 'dashboard', component: DashboardView },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  auth.restore();

  if (!to.meta.public && !auth.isAuthenticated) {
    return { name: 'login' };
  }

  if (to.name === 'login' && auth.isAuthenticated) {
    return { name: 'dashboard' };
  }

  return true;
});

export default router;
