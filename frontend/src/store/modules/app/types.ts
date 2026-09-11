import type {RouteRecordRaw} from "vue-router";

export interface IAppState {
    menuCollapse: boolean;
    loading: boolean;
    loadingTip: string;
    topMenus: RouteRecordRaw[];
    currentTopMenu: RouteRecordRaw;
    // breadcrumbList: IBreadcrumbItem[];
    currentOrgId: string;
    currentProjectId: string;
    projectList: { id: string; name: string }[];
    orgList: { id: string; name: string }[];
    currentMenuConfig: string[];
    collapsedWidth: number;
    menuWidth: number;
    fileMaxSize: number; // 文件上传最大限制
    showFooter: boolean;
}