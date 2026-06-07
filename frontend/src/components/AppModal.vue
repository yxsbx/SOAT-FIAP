<script setup>
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

function requestClose() {
  if (confirmUnsaved()) {
    emit('close');
  }
}

function confirmUnsaved() {
  return !props.dirty || window.confirm('Existem alterações não salvas. Deseja fechar mesmo assim?');
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
          <slot />
          <footer v-if="$slots.actions" class="app-modal-actions">
            <slot name="actions" />
          </footer>
        </section>
      </div>
    </Transition>
  </Teleport>
</template>
