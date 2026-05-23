<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ArrowLeft, Wrench, LogIn } from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const auth = useAuthStore();
const username = ref('admin@autocarehub.com');
const password = ref('autocare123');
const loading = ref(false);
const error = ref('');
const selectedProfile = ref('admin');

const demoProfiles = [
  {
    id: 'admin',
    label: 'Administrador',
    username: 'admin@autocarehub.com',
    password: 'autocare123',
    description: 'Acesso completo ao painel, cadastros, estoque e comandos.',
  },
  {
    id: 'employee',
    label: 'Funcionário',
    username: 'funcionario@autocarehub.com',
    password: 'autocare123',
    description: 'Operação da oficina: ordens, peças, serviços e atendimento.',
  },
  {
    id: 'customer',
    label: 'Cliente',
    username: 'cliente@autocarehub.com',
    password: 'autocare123',
    description: 'Visão do cliente Mariana Costa com seus carros e ordens.',
  },
];

function selectProfile(profile) {
  selectedProfile.value = profile.id;
  username.value = profile.username;
  password.value = profile.password;
  error.value = '';
}

async function submit() {
  loading.value = true;
  error.value = '';

  try {
    await auth.login(username.value, password.value);
    router.push({ name: 'dashboard' });
  } catch (err) {
    error.value = err.message || 'Não foi possível autenticar.';
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <main class="login-shell">
    <section class="login-panel">
      <RouterLink class="login-back-link" to="/preview">
        <ArrowLeft :size="17" />
        Voltar para a home
      </RouterLink>
      <div class="brand-mark">
        <Wrench :size="28" />
      </div>
      <h1>AutoCare Hub</h1>
      <p>Gestão operacional da oficina</p>

      <div class="profile-picker">
        <button
          v-for="profile in demoProfiles"
          :key="profile.id"
          type="button"
          :class="{ active: selectedProfile === profile.id }"
          @click="selectProfile(profile)"
        >
          <strong>{{ profile.label }}</strong>
          <span>{{ profile.description }}</span>
        </button>
      </div>

      <form class="login-form" @submit.prevent="submit">
        <label>
          Usuário
          <input v-model="username" autocomplete="username" type="email" required />
        </label>
        <label>
          Senha
          <input v-model="password" autocomplete="current-password" type="password" required />
        </label>
        <button class="primary-button" type="submit" :disabled="loading">
          <LogIn :size="18" />
          <span>{{ loading ? 'Entrando...' : 'Entrar' }}</span>
        </button>
        <p v-if="error" class="form-error">{{ error }}</p>
      </form>
    </section>
  </main>
</template>
