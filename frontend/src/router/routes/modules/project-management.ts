import type {IAppRouteRecordRaw} from "/@/router/routes/types.ts";
import {ProjectManagementRouteEnum} from "/@/enums/route-enum.ts";

const ProjectManagement: IAppRouteRecordRaw = {
    path: '/project-management',
    name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT,
    component: null as unknown as Component,
    redirect: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_FILE_MANAGEMENT,
    meta: {
        locale: '项目管理',
        icon: 'i-mdi:chart-bar-stacked',
        order: 1,
        hideChildrenInMenu: true,
        roles: ['*'],
    },
    children: [
        {
            path: 'fileManagement',
            name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_FILE_MANAGEMENT,
            component: () => import('/@/views/project-management/file-management/index.vue'),
            meta: {
                locale: '文件管理',
                roles: ['*'],
                isTopMenu: true,
            },
        },
        {
            path: 'environmentManagement',
            name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_ENVIRONMENT_MANAGEMENT,
            component: () => import('/@/views/project-management/environmental/index.vue'),
            meta: {
                locale: '环境管理',
                roles: ['*'],
                isTopMenu: true,
            },
        },
        {
            path: 'taskCenter',
            name: ProjectManagementRouteEnum.PROJECT_MANAGEMENT_TASK_CENTER,
            component: () => import('/@/views/project-management/task-center/index.vue'),
            meta: {
                locale: '任务中心',
                roles: ['*'],
                isTopMenu: true,
            },
        },
    ]
}
export default ProjectManagement;