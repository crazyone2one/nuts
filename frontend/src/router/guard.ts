import type {LocationQueryRaw, Router} from "vue-router";
import {setRouteEmitter} from "/@/utils/route-listener.ts";
import {useAppStore} from "/@/store";
import {MENU_LEVEL, type PathMapRoute} from "/@/router/path-map.ts";
import {usePathMap} from "/@/hooks/use-path-map.ts";

const setupPageGuard = (router: Router) => {
    const {getRouteLevelByKey} = usePathMap();
    router.beforeEach((to, _from) => {
        setRouteEmitter(to);
        const appStore = useAppStore();
        const urlOrgId = to.query.orgId;
        const urlProjectId = to.query.pId;
        if (urlOrgId) {
            appStore.setCurrentOrgId(urlOrgId as string);
        }
        if (urlProjectId) {
            appStore.setCurrentProjectId(urlProjectId as string);
        }
        switch (getRouteLevelByKey(to.name as PathMapRoute)) {
            case MENU_LEVEL[1]:
                if (urlOrgId === undefined) {
                    const newQuery = {
                        ...to.query,
                        orgId: appStore.currentOrgId,
                    };
                    // Redirect to same route but include orgId in query to avoid infinite loop
                    return {path: to.path, query: newQuery};
                }
                break;
            case MENU_LEVEL[2]: // 项目级别的页面，需要给页面携带上组织 ID和项目 ID
                if (urlOrgId === undefined && urlProjectId === undefined) {
                    const newQuery = {
                        ...to.query,
                        orgId: appStore.currentOrgId,
                        pId: appStore.currentProjectId,
                    };
                    // Redirect to same route but include orgId and pId in query to avoid infinite loop
                    return {path: to.path, query: newQuery};
                }
                break;
            case MENU_LEVEL[0]: // 系统级别的页面，无需携带组织ID和项目ID
            default:
                break;
        }
        return;
    })
};
const setupUserLoginInfoGuard = (router: Router) => {
    router.beforeEach((to, _) => {

        const token = !!localStorage.getItem('accessToken')
        if (to.name !== 'login' && token) {
            return;
        } else {
            if (to.name === 'login') {
                return;
            }
        }
        // If there's a redirect query (from previous navigation) use it, otherwise stay on the target path
        return {
            name: 'login',
            query: {
                redirect: to.name,
                ...to.query,
            } as LocationQueryRaw,
        }
    })
}
export const createRouterGuards = (router: Router) => {
    setupPageGuard(router);
    setupUserLoginInfoGuard(router);
    router.afterEach((to) => {
        const items = ['nuts']
        to.meta.locale != null && items.unshift(to.meta.locale as string)
        document.title = items.join(' | ')
    })
}