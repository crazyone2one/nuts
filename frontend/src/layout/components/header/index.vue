<script setup lang="ts">

import TopMenu from "/@/layout/components/header/TopMenu.vue";
</script>

<template>
  <n-layout-header bordered class="nav" style="grid-template-columns: calc(272px - var(--side-padding)) 1fr auto;">
    <n-text class="ui-logo">
      <img src="/favicon.svg" alt="logo"/>
      <span class="bold">Nuts</span>
    </n-text>
    <div class="flex items-center">
      <div class="w-[216px] ml-[24px] mr-[12px] shrink-0">
        <n-select/>
      </div>
      <div class="nav-menu">
        <top-menu/>
      </div>
    </div>
    <div class="nav-end">
      <n-button type="info" text class="nav-picker">Nuts1</n-button>
      <n-button type="success" text class="nav-picker">Nuts2</n-button>
      <n-text class="nav-picker padded">1.x</n-text>
    </div>
  </n-layout-header>
</template>

<style scoped>
.nav {
  display: grid;
  grid-template-rows: calc(var(--header-height) - 1px);
  padding: 0 var(--side-padding);
}

.nav, .ui-logo {
  align-items: center;
}

.ui-logo {
  cursor: pointer;
  display: flex;
  font-size: 18px
}

.nav-menu {
  flex-grow: 0;
  flex-shrink: 1;
  overflow: hidden;
  padding-left: 36px
}

.nav-end {
  align-items: center;
  display: flex
}

.nav-picker {
  margin-right: 4px
}

.nav-picker.padded {
  padding: 0 10px
}

.nav-picker:last-child {
  margin-right: 0
}

.ui-logo > img {
  height: 32px;
  margin-right: 12px;
  width: 32px
}
</style>