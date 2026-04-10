<template>
  <section class="card-wrap">
    <header v-if="title" class="card-head">
      <div class="card-title">{{ title }}</div>
    </header>
    <div class="card-body">
      <slot></slot>
    </div>
  </section>
</template>

<script lang="ts" setup>
defineProps<{
  title?: string
}>()
</script>

<style lang="scss" scoped>
.card-wrap {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 22px;
  border: 1px solid rgba(132, 163, 151, 0.16);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 22px 42px rgba(68, 94, 86, 0.08);
  backdrop-filter: blur(14px);
}

.card-head {
  display: flex;
  align-items: center;
}

.card-title {
  position: relative;
  padding-left: 18px;
  font-family: var(--font-display);
  font-size: 1.2rem;
  line-height: 1.2;
  color: var(--app-text);
}

.card-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: var(--app-primary);
  transform: translateY(-50%);
  box-shadow: 0 0 0 6px rgba(108, 174, 152, 0.14);
}

.card-body {
  min-width: 0;
}
</style>
