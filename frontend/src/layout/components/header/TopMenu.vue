<script setup lang="ts">
import {useAppStore} from "/@/store";
import {type RouteRecordName, type RouteRecordRaw, RouterLink} from "vue-router";
import {cloneDeep} from "lodash-es";
import {appClientMenus} from "/@/router/routes";
import {listenerRouteChange} from "/@/utils/route-listener.ts";
import type {MenuOption} from "naive-ui";

const appStore = useAppStore();
const activeMenus: Ref<RouteRecordName[]> = ref([]);
const copyRouters = cloneDeep(appClientMenus) as RouteRecordRaw[];
const setCurrentTopMenu = (key: string) => {
  // 先判断全等，避免同级路由出现命名包含情况
  const secParentFullSame = appStore.topMenus.find((route: RouteRecordRaw) => {
    return key === route?.name;
  });

  // 非全等的情况下，一定是父子路由包含关系
  const secParentLike = appStore.topMenus.find((route: RouteRecordRaw) => {
    return key.includes(route?.name as string);
  });

  if (secParentFullSame) {
    appStore.setCurrentTopMenu(secParentFullSame);
  } else if (secParentLike) {
    appStore.setCurrentTopMenu(secParentLike);
  }
}
listenerRouteChange(newRoute => {
  const {name} = newRoute;
  for (let i = 0; i < copyRouters.length; i++) {
    const firstRoute = copyRouters[i];
    if (name && firstRoute?.name && (name as string).includes(firstRoute.name as string)) {
      // 先判断二级菜单是否顶部菜单
      let currentParent = firstRoute?.children?.some((item) => item.meta?.isTopMenu)
          ? (firstRoute as RouteRecordRaw)
          : undefined;

      if (!currentParent) {
        // 二级菜单非顶部菜单，则判断三级菜单是否有顶部菜单
        currentParent = firstRoute?.children?.find(
            (item) => name && item?.name && (name as string).includes(item.name as string)
        );
      }
      let filterMenuTopRouter = currentParent?.children?.filter((item: any) => item.meta?.isTopMenu) || [];
      appStore.setTopMenus(filterMenuTopRouter);
      setCurrentTopMenu(name as string);
      return;
    }
  }
  appStore.setTopMenus([]);
  setCurrentTopMenu('');
}, true)
watch(
    () => appStore.getCurrentTopMenu?.name,
    (val) => {
      activeMenus.value = [val || ''];
    }, {immediate: true,}
);
const travel = (_route: (RouteRecordRaw | null)[] | null, nodes: Array<MenuOption> = []) => {
  if (_route) {
    _route.forEach(element => {
      const node: MenuOption = {
        key: element?.name as string,
        label: () => h(RouterLink, {to: {name: element?.name}}, {default: () => element?.meta?.locale}),
      }
      if (element?.children && element?.children.length !== 0) {
        node.children = travel(element.children);
      }
      nodes.push(node);
    })
  }
  return nodes
}
const topMenu = computed(() => {
  return appStore.getTopMenus
})
const menus = computed(() => {
  return topMenu.value ? travel(topMenu.value) : []
})
</script>

<template>
  <n-menu :options="menus" mode="horizontal" :value="activeMenus[0] as string"/>
</template>

<style scoped>

</style>