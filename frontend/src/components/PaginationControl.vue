<script setup>
import { computed, ref, watch } from 'vue';
import { ChevronLeft, ChevronRight } from 'lucide-vue-next';

const props = defineProps({
  page: {
    type: Number,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(['update:page']);
const pageInput = ref(props.page + 1);

const safeTotalPages = computed(() => Math.max(1, props.totalPages || 1));

watch(
  () => props.page,
  (page) => {
    pageInput.value = page + 1;
  }
);

function goTo(page) {
  const nextPage = Math.min(Math.max(0, page), safeTotalPages.value - 1);
  emit('update:page', nextPage);
}

function commitInput() {
  goTo(Number(pageInput.value || 1) - 1);
}
</script>

<template>
  <nav aria-label="Paginação" class="pagination-control">
    <button :disabled="page <= 0" title="Página anterior" type="button" @click="goTo(page - 1)">
      <ChevronLeft :size="18" />
    </button>
    <label>
      Página
      <input
        v-model.number="pageInput"
        :max="safeTotalPages"
        min="1"
        type="number"
        @blur="commitInput"
        @keyup.enter="commitInput"
      />
      <span>de {{ safeTotalPages }}</span>
    </label>
    <button :disabled="page + 1 >= safeTotalPages" title="Próxima página" type="button" @click="goTo(page + 1)">
      <ChevronRight :size="18" />
    </button>
  </nav>
</template>
