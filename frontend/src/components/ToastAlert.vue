<script setup>
import {computed} from 'vue';
import {AlertCircle, CheckCircle2, X} from 'lucide-vue-next';

const props = defineProps({
  type: {
    type: String,
    default: 'success',
  },
  message: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['close']);

const icon = computed(() => (props.type === 'error' ? AlertCircle : CheckCircle2));
</script>

<template>
  <Transition name="toast-fade">
    <aside v-if="message" :class="['toast-alert', `toast-alert--${type}`]" role="status">
      <component :is="icon" :size="20"/>
      <span>{{ message }}</span>
      <button aria-label="Fechar alerta" type="button" @click="emit('close')">
        <X :size="16"/>
      </button>
    </aside>
  </Transition>
</template>
