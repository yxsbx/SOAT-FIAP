<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Wrench, LogIn } from 'lucide-vue-next';
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
    label: 'Funcionario',
    username: 'funcionario@autocarehub.com',
    password: 'autocare123',
    description: 'Operacao da oficina: ordens, pecas, servicos e atendimento.',
  },
  {
    id: 'customer',
    label: 'Cliente',
    username: 'cliente@autocarehub.com',
    password: 'autocare123',
    description: 'Visao do cliente Mariana Costa com seus carros e ordens.',
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
    error.value = err.message || 'Nao foi possivel autenticar.';
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <main class="login-shell">
    <section class="login-panel">
      <div class="brand-mark">
        <Wrench :size="28" />
      </div>
      <h1>AutoCare Hub</h1>
      <p>Gestao operacional da oficina</p>

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
          Usuario
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
