
import type {RouteRecordRaw} from "vue-router";
import type {IAppState} from "/@/store/modules/app/types.ts";
import {cloneDeep} from "lodash-es";

const useAppStore = defineStore('app', {
    state: (): IAppState => ({
        menuCollapse: false,
        loading: false,
        loadingTip: '',
        currentOrgId: '',
        currentProjectId: '',
        topMenus: [],
        currentTopMenu: {} as RouteRecordRaw,
        projectList: [],
        orgList: [],
        fileMaxSize: 50,
        currentMenuConfig: [],
        collapsedWidth: 64,
        menuWidth: 220,
        showFooter: false,
    }),
    getters: {
        appCurrentSetting(state: IAppState) {
            return {...state}
        },
        getTopMenus(state: IAppState): RouteRecordRaw[] {
            return state.topMenus;
        },
        getCurrentTopMenu(state: IAppState): RouteRecordRaw {
            return state.currentTopMenu;
        },
        getCurrentOrgId(state: IAppState): string {
            return state.currentOrgId;
        },
        getCurrentProjectId(state: IAppState): string {
            return state.currentProjectId;
        },
        getFileMaxSize(state: IAppState): number {
            return state.fileMaxSize;
        },
    },
    actions: {
        resetInfo() {
            this.$reset();
        },
        async setCurrentMenuConfig(menuConfig: string[]) {
            this.currentMenuConfig = menuConfig;
        },
        toggleMenu(value: boolean) {
            this.menuCollapse = value;
        },
        showLoading(tip = '') {
            this.loading = true;
            this.loadingTip = tip || '加载中...';
        },
        hideLoading() {
            this.loading = false;
            this.loadingTip = '加载中...';
        },
        setTopMenus(menus: RouteRecordRaw[] | undefined) {
            this.topMenus = menus ? [...menus] : [];
        },
        setCurrentTopMenu(menu: RouteRecordRaw) {
            this.currentTopMenu = cloneDeep(menu);
        },
        setCurrentOrgId(id: string) {
            this.currentOrgId = id;
        },
        setCurrentProjectId(id: string) {
            this.currentProjectId = id;
        },
    },
    persist: {
        pick: ['currentOrgId', 'currentProjectId', 'menuCollapse', 'isDarkTheme'],
    },
})
export default useAppStore;