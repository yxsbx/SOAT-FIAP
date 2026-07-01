<script setup>
import {ref} from 'vue';
import {X} from 'lucide-vue-next';

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '',
  },
  subtitle: {
    type: String,
    default: '',
  },
  dirty: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['close']);
const confirmCloseOpen = ref(false);

function requestClose() {
  if (confirmUnsaved()) {
    emit('close');
  }
}

function confirmUnsaved() {
  if (!props.dirty) {
    return true;
  }
  confirmCloseOpen.value = true;
  return false;
}

function cancelClose() {
  confirmCloseOpen.value = false;
}

function confirmClose() {
  confirmCloseOpen.value = false;
  emit('close');
}
</script>

<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="open" class="app-modal-backdrop" @click.self="requestClose">
        <section aria-modal="true" class="app-modal" role="dialog">
          <header class="app-modal-heading">
            <div>
              <span v-if="subtitle">{{ subtitle }}</span>
              <h2>{{ title }}</h2>
            </div>
            <button class="icon-button" type="button" @click="requestClose">
              <X :size="18" />
            </button>
          </header>
          <div class="app-modal-body">
            <slot />
          </div>
          <footer v-if="$slots.actions" class="app-modal-actions">
            <slot name="actions" />
          </footer>

          <div v-if="confirmCloseOpen" class="modal-confirm-panel">
            <strong>Descartar alterações?</strong>
            <span>Existem alterações não salvas neste formulário.</span>
            <div>
              <button class="secondary-button" type="button" @click="cancelClose">Continuar editando</button>
              <button class="primary-button danger-action" type="button" @click="confirmClose">Descartar</button>
            </div>
          </div>
        </section>
      </div>
    </Transition>
  </Teleport>
</template>
