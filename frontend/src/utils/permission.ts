import type {RouteRecordNormalized} from "vue-router";
import appRoutes from "/@/router/routes";

export const findRouteByName = (name: string) => {
    const queue: RouteRecordNormalized[] = [...appRoutes];
    while (queue.length > 0) {
        const currentRoute = queue.shift();
        if (!currentRoute) {
            return;
        }
        if (currentRoute.name === name) {
            return currentRoute;
        }
        if (currentRoute.children) {
            queue.push(...(currentRoute.children as RouteRecordNormalized[]));
        }
    }
    return null;
}
export const getFirstRouterNameByCurrentRoute = (parentName: string) => {
    const currentRoute = findRouteByName(parentName);
    if (currentRoute) {
        return currentRoute.children[0].name;
    }
    return parentName;
}
export const routerNameHasPermission = (routerName: string, routerList: RouteRecordNormalized[]) => {
    const currentRoute = routerList.find((item) => item.name === routerName);
    return !!currentRoute;
}

export const getFirstRouteNameByPermission = (routerList: RouteRecordNormalized[]) => {
    const currentRoute = routerList
        // .filter((item) => hasAnyPermission(item.meta.roles || [])) // 排除没有权限的路由
        .sort((a, b) => {
            // 如果 a 和 b 都有 order，按照 order 的值进行升序排序
            if (a.meta.order !== undefined && b.meta.order !== undefined) {
                return a.meta.order - b.meta.order;
            }
            // 如果 a 有 order 但是 b 没有 order，a 排前面
            if (a.meta.order !== undefined && b.meta.order === undefined) {
                return -1;
            }
            // 如果 a 没有 order 但是 b 有 order，b 排前面
            if (a.meta.order === undefined && b.meta.order !== undefined) {
                return 1;
            }
            // 如果 a 和 b 都没有 order，它们的位置不变
            return 0;
        })[0];
    return currentRoute?.name;
}