import type {RouteRecordRaw} from "vue-router";
import type {IProjectListItem} from "/@/types/project.ts";
import type {EnvConfig} from "/@/types/environmental.ts";

export interface IAppState {
    menuCollapse: boolean;
    loading: boolean;
    loadingTip: string;
    topMenus: RouteRecordRaw[];
    currentTopMenu: RouteRecordRaw;
    // breadcrumbList: IBreadcrumbItem[];
    currentOrgId: string;
    currentProjectId: string;
    projectList: Partial<IProjectListItem>[];
    orgList: { id: string; name: string }[];
    currentMenuConfig: string[];
    collapsedWidth: number;
    menuWidth: number;
    fileMaxSize: number; // 文件上传最大限制
    showFooter: boolean;
    currentEnvConfig?: EnvConfig; // 当前环境配置信息
}