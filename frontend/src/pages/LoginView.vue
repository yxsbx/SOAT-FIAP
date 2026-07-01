<script setup>
import {ref} from 'vue';
import {useRouter} from 'vue-router';
import {ArrowLeft, LogIn, Wrench} from 'lucide-vue-next';
import {useAuthStore} from '@/stores/auth';

const router = useRouter();
const auth = useAuthStore();
const username = ref('master@autocarehub.com');
const demoPassword = import.meta.env.VITE_DEMO_PASSWORD || 'autocare123';
const password = ref(demoPassword);
const loading = ref(false);
const error = ref('');
const selectedProfile = ref('master');

const demoProfiles = [
  {
    id: 'master',
    label: 'Admin Master',
    subtitle: 'Dona da AutoCare Hub',
    username: 'master@autocarehub.com',
    routeName: 'dashboard',
  },
  {
    id: 'workshop-admin',
    label: 'Admin de oficina',
    subtitle: 'Gestão da oficina',
    username: 'oficina.admin@autocarehub.com',
    routeName: 'dashboard',
  },
  {
    id: 'parts-admin',
    label: 'Admin de loja de peças',
    subtitle: 'Loja de peças',
    username: 'loja.admin@autocarehub.com',
    routeName: 'dashboard',
  },
  {
    id: 'workshop-employee',
    label: 'Funcionário de oficina',
    subtitle: 'Operação e atendimento',
    username: 'oficina.funcionario@autocarehub.com',
    routeName: 'dashboard',
  },
  {
    id: 'parts-employee',
    label: 'Funcionário de loja de peças',
    subtitle: 'Estoque e peças',
    username: 'loja.funcionario@autocarehub.com',
    routeName: 'dashboard',
  },
  {
    id: 'customer',
    label: 'Cliente final',
    subtitle: 'Dono de veículo',
    username: 'cliente@autocarehub.com',
    routeName: 'dashboard',
  },
];

function selectProfile(profile) {
  selectedProfile.value = profile.id;
  username.value = profile.username;
  password.value = demoPassword;
  error.value = '';
}

async function submit() {
  loading.value = true;
  error.value = '';
  const targetRouteName =
    demoProfiles.find((profile) => profile.id === selectedProfile.value)?.routeName || 'dashboard';

  try {
    await auth.login(username.value, password.value);
    router.push({name: targetRouteName});
  } catch (err) {
    error.value = err.message || 'Não foi possível autenticar.';
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <main class="login-shell">
    <header class="login-navbar">
      <div class="login-brand">
        <div class="brand-mark">
          <Wrench :size="20" />
        </div>
        <strong>AutoCare Hub</strong>
      </div>

      <RouterLink class="login-back-link" to="/">
        <ArrowLeft :size="17" />
        Voltar para a home
      </RouterLink>
    </header>

    <section class="login-content">
      <div class="login-panel">
        <section class="login-copy">
          <h1>Acesse sua área</h1>
          <p>Use login manual ou escolha um perfil acadêmico de demonstração.</p>
          <p class="demo-password-hint">
            Senha demo: <strong>{{ demoPassword }}</strong>
          </p>

          <form class="login-form" @submit.prevent="submit">
            <label>
              Usuário
              <input v-model="username" autocomplete="username" required type="email" />
            </label>
            <label>
              Senha
              <input v-model="password" autocomplete="current-password" required type="password" />
            </label>
            <button :disabled="loading" class="primary-button" type="submit">
              <LogIn :size="18" />
              <span>{{ loading ? 'Entrando...' : 'Entrar' }}</span>
            </button>
            <p v-if="error" class="form-error">{{ error }}</p>
          </form>
        </section>

        <section aria-label="Logins rápidos de demonstração" class="login-demo-panel">
          <span>Entrar como</span>
          <div class="profile-picker">
            <button
              v-for="profile in demoProfiles"
              :key="profile.id"
              :class="{ active: selectedProfile === profile.id }"
              :disabled="loading"
              type="button"
              @click="selectProfile(profile)"
            >
              <strong>{{ profile.label }}</strong>
              <small>{{ profile.subtitle }}</small>
            </button>
          </div>
        </section>
      </div>
    </section>
  </main>
</template>
