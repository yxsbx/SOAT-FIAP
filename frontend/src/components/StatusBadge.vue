<script setup>
import {computed} from 'vue';

const props = defineProps({
  value: {
    type: [String, Boolean, Number],
    default: '',
  },
  label: {
    type: String,
    default: '',
  },
});

const statusMap = {
  WAITING_APPROVAL: {label: 'Aguardando aprovação', tone: 'warning'},
  IN_PROGRESS: {label: 'Em execução', tone: 'info'},
  IN_DIAGNOSIS: {label: 'Em diagnóstico', tone: 'info'},
  DELIVERED: {label: 'Entregue', tone: 'success'},
  FINISHED: {label: 'Finalizada', tone: 'success'},
  RECEIVED: {label: 'Recebida', tone: 'neutral'},
  ACTIVE: {label: 'Ativo', tone: 'success'},
  INACTIVE: {label: 'Inativo', tone: 'danger'},
  AVAILABLE: {label: 'Disponível', tone: 'success'},
  RESERVED: {label: 'Com reserva', tone: 'info'},
  LOW_STOCK: {label: 'Baixo estoque', tone: 'warning'},
  OUT_OF_STOCK: {label: 'Sem estoque', tone: 'danger'},
  DRAFT: {label: 'Rascunho', tone: 'neutral'},
  SENT: {label: 'Enviado', tone: 'info'},
  APPROVED: {label: 'Aprovado', tone: 'success'},
  REFUSED: {label: 'Recusado', tone: 'danger'},
  EXPIRED: {label: 'Expirado', tone: 'warning'},
};

const normalized = computed(() => {
  if (typeof props.value === 'boolean') {
    return props.value ? 'ACTIVE' : 'INACTIVE';
  }
  return String(props.value || '').trim().toUpperCase();
});

const status = computed(() => {
  const mapped = statusMap[normalized.value];
  if (mapped) {
    return mapped;
  }
  return {
    label: props.label || String(props.value || 'Neutro'),
    tone: 'neutral',
  };
});
</script>

<template>
  <span :class="['status-badge', `status-badge--${status.tone}`]">
    {{ label || status.label }}
  </span>
</template>
