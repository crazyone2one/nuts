import {appClientMenus} from "/@/router/routes";
import type {RouteRecordNormalized, RouteRecordRaw} from "vue-router";
import {cloneDeep} from "lodash-es";

export const useMenuTree = () => {
    const menuTree = computed(() => {
        const copyRouter = cloneDeep(appClientMenus) as RouteRecordNormalized[];
        copyRouter.sort((a: RouteRecordNormalized, b: RouteRecordNormalized) => {
            return (a.meta?.order || 0) - (b.meta?.order || 0);
        });
        const travel = (_routes: RouteRecordRaw[], layer: number) => {
            if (!_routes) return null;
            const collector = _routes.map(element => {
                if (element.meta?.hideInMenu === true) {
                    return null;
                }
                if (element.meta?.hideChildrenInMenu || !element.children) {
                    element.children = [];
                    return element;
                }
                element.children = element.children.filter((x) => x.meta?.hideInMenu !== true);
                const subItem = travel(element.children, layer + 1);
                if (subItem && subItem.length) {
                    element.children = subItem as RouteRecordRaw[];
                    return element;
                }

                if (layer > 1) {
                    element.children = subItem as RouteRecordRaw[];
                    return element;
                }

                if (element.meta?.hideInMenu === false) {
                    return element;
                }
                return null;
            })
            return collector.filter(Boolean);
        }
        return travel(copyRouter, 0);
    });
    return {menuTree};
}