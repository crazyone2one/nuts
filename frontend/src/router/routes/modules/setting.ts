import {SettingRouteEnum} from "/@/enums/route-enum.ts";
import type {IAppRouteRecordRaw} from "/@/router/routes/types.ts";

const Setting: IAppRouteRecordRaw = {
    path: '/setting',
    name: SettingRouteEnum.SETTING,
    component: null as unknown as Component,
    meta: {
        locale: '系统设置',
        icon: 'i-mdi:tools',
        order: 8,
        roles: ['*'],
        hideChildrenInMenu: true,
    },
    children: [
        {
            path: 'user',
            name: SettingRouteEnum.SETTING_SYSTEM_USER_SINGLE,
            component: () => import('/@/views/setting/user/index.vue'),
            meta: {
                locale: '用户',
                roles: ['SYSTEM_USER:READ'],
                isTopMenu: true,
            },
        },
        {
            path: 'usergroup',
            name: SettingRouteEnum.SETTING_SYSTEM_USER_GROUP,
            component: () => import('/@/views/setting/user-group/index.vue'),
            meta: {
                locale: '用户组',
                roles: ['SYSTEM_USER_ROLE:READ'],
                isTopMenu: true,
            },
        },
    ]
}
export default Setting;