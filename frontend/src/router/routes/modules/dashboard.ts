import type {IAppRouteRecordRaw} from "/@/router/routes/types.ts";
import {WorkbenchRouteEnum} from "/@/enums/route-enum.ts";

const DashboardManagement: IAppRouteRecordRaw = {
    path: '/dashboard',
    name: WorkbenchRouteEnum.WORKBENCH,
    component: null as unknown as Component,
    meta: {
        locale: '工作台',
        icon: 'i-mdi:chart-bar-stacked',
        order: 0,
        hideChildrenInMenu: true,
        roles: ['*'],
    },
    children: [
        {
            path: 'dashboard',
            name: WorkbenchRouteEnum.WORKBENCH_INDEX,
            component: () => import('/@/views/dashboard/index.vue'),
            meta: {
                locale: '首页',
                roles: ['*'],
                isTopMenu: true,
            },
        },
    ]
}
export default DashboardManagement;