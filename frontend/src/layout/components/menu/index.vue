<script setup lang="ts">
import type {MenuOption} from "naive-ui";
import {useMenuTree} from "/@/layout/components/menu/use-menu-tree.ts";
import {type RouteRecordRaw, RouterLink} from "vue-router";
import {getFirstRouterNameByCurrentRoute} from "/@/utils/permission.ts";
import {listenerRouteChange} from "/@/utils/route-listener.ts";
import {useAppStore} from "/@/store";
import PersonCenter from "/@/layout/components/menu/PersonCenter.vue";

const currentRoute = useRoute();
const appStore = useAppStore();
const menuOptions = computed(() => {
  return renderSubMenu()
})
const renderMenuIcon = (option: MenuOption) => {
  if (option.key === 'sheep-man') {
    return true
  }
  return h('div', {class: 'i-carbon:assignment-action-usage'})
}
const activeKey = ref(currentRoute.name as string)
const openKeys = ref<string[]>([]);
const {menuTree} = useMenuTree();
const renderSubMenu = () => {
  const travel = (_route: (RouteRecordRaw | null)[] | null, nodes: Array<MenuOption> = []) => {
    if (_route) {
      _route.forEach(element => {
        const node: MenuOption = {
          icon: element?.meta?.icon ? () => h('div', {class: `${element?.meta?.icon}`}) : undefined,
          key: element?.name as string,
          label: element?.meta?.hideChildrenInMenu
              ? () => h(RouterLink,
                  {to: {name: element?.meta?.hideChildrenInMenu ? getFirstRouterNameByCurrentRoute(element.name as string) : element?.name}},
                  {default: () => element?.meta?.locale})
              : element?.meta?.locale,
        }
        if (element?.children && element?.children.length !== 0) {
          node.children = travel(element.children);
        }
        nodes.push(node);
      })
    }
    return nodes
  }
  return travel(menuTree.value);
}
const findMenuOpenKeys = (target: string) => {
  const result: string[] = [];
  let isFind = false;
  const backtrack = (item: RouteRecordRaw | null, keys: string[]) => {
    if (target.includes(item?.name as string)) {
      result.push(...keys);
      if (result.length >= 2) {
        // 由于目前存在三级子路由，所以至少会匹配到三层才算结束
        isFind = true;
        return;
      }
    }
    if (item?.children?.length) {
      item.children.forEach((el) => {
        backtrack(el, [...keys, el.name as string]);
      });
    }
  };

  menuTree.value?.forEach((el: RouteRecordRaw | null) => {
    if (isFind) return; // 节省性能
    backtrack(el, [el?.name as string]);
  });
  return result;
}
listenerRouteChange(route => {
  const {requiresAuth, activeMenu, hideInMenu} = route.meta;
  if (requiresAuth !== false && (!hideInMenu || activeMenu)) {
    const menuOpenKeys = findMenuOpenKeys((activeMenu || route.name) as string);
    const keySet = new Set([...menuOpenKeys, ...openKeys.value]);
    openKeys.value = [...keySet];
    activeKey.value = [activeMenu || menuOpenKeys[menuOpenKeys.length - 1]][0];
  }
}, true)
</script>

<template>
  <n-layout-sider bordered
                  content-style="padding-top: 24px;"
                  collapse-mode="width"
                  :collapsed-width="appStore.collapsedWidth"
                  :width="appStore.menuWidth"
                  show-trigger
                  :collapsed="appStore.menuCollapse"
                  @collapse="appStore.toggleMenu(true)"
                  @expand="appStore.toggleMenu(false)">
    <n-flex vertical class="h-full">
      <div class="mt-1 flex-1 overflow-y-auto">
        <n-menu
            v-model:value="activeKey"
            :collapsed="appStore.menuCollapse"
            :collapsed-width="appStore.collapsedWidth"
            :collapsed-icon-size="24"
            :options="menuOptions"
            :render-icon="renderMenuIcon"
        />
      </div>

      <div class="mb-5 shrink-0 flex justify-center">
        <person-center/>
      </div>
    </n-flex>
  </n-layout-sider>

</template>

<style scoped>

</style>