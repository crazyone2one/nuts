import type {EnvConfig, EnvDetailItem, GlobalParams} from "/@/types/environmental.ts";
import {cloneDeep} from "lodash-es";
import {useAppStore} from "/@/store";
import {envManagementAPi} from "/@/api/methods/env-management.ts";

const envParamsDefaultConfig: EnvConfig = {
    commonVariables: [],
    httpConfig: [],
    dataSources: [],
    hostConfig: {
        enable: false,
        hosts: [],
    },
    ftpConfig: {
        ip: '',
        port: '',
        username: 'root',
        password: '',
        localPath: '',
        remotePath: ''
    },
    assertionConfig: {assertions: []},
    pluginConfigMap: {},
    commonParams: {
        requestTimeout: 60000,
        responseTimeout: 60000,
    },
};
const defaultAllParams = {
    projectId: '',
    globalParams: {
        headers: [],
        commonVariables: [],
    },
};
export const ALL_PARAM = 'allParam';
export const NEW_ENV_PARAM = 'newEnvParam';
export const NEW_ENV_PARAM_COPY = 'newEnvParamCopy';
export const NEW_ENV_GROUP = 'newEnvGroup';
const useProjectEnvStore = defineStore('projectEnv', () => {
    const currentId = ref<string>('');
    const currentGroupId = ref<string>('');
    // 当前选中的环境详情
    const currentEnvDetailInfo = ref<EnvDetailItem>({
        projectId: '',
        name: '',
        description: '',
        config: envParamsDefaultConfig,
    });
    const backupEnvDetailInfo = ref<EnvDetailItem>({
        projectId: '',
        name: '',
        description: '',
        config: cloneDeep(envParamsDefaultConfig),
    });
    const allParamDetailInfo = ref<GlobalParams>(defaultAllParams); // 全局参数详情

    const backupAllParamDetailInfo = ref<GlobalParams>(defaultAllParams);
    const httpNoWarning = ref(true);
    const getHttpNoWarning = computed(() => httpNoWarning.value);

    // 设置选中项
    function setCurrentId(id: string) {
        currentId.value = id;
    }

    // 设置选中项目组
    function setCurrentGroupId(id: string) {
        currentGroupId.value = id;
    }

    // 设置http提醒
    function setHttpNoWarning(noWarning: boolean) {
        httpNoWarning.value = noWarning;
    }

    // 设置全局参数
    function setAllParamDetailInfo(item: GlobalParams) {
        allParamDetailInfo.value = item;
    }

    function setDetailInfo(detailInfo: EnvDetailItem) {
        const appStore = useAppStore();

        nextTick(() => {
            backupEnvDetailInfo.value = cloneDeep(detailInfo);
            appStore.currentEnvConfig = cloneDeep(detailInfo.config);
            appStore.currentEnvConfig.id = detailInfo.id;
        });
    }

    async function copyCurrentEnv(copyId: string) {
        try {
            const tmpObj = await envManagementAPi.getDetailEnv(copyId).send(true);
            currentEnvDetailInfo.value = {...tmpObj};
            currentEnvDetailInfo.value.id = '';
            let copyName = `copy_${currentEnvDetailInfo.value.name}`;
            if (copyName.length > 255) {
                copyName = copyName.slice(0, 255);
            }
            currentEnvDetailInfo.value.name = copyName;
            setDetailInfo(currentEnvDetailInfo.value);
        } catch (error) {
            console.log(error);
        }
    }

    // 初始化环境详情
    async function initEnvDetail(copyId = '') {
        const id = currentId.value;
        const appStore = useAppStore();
        try {
            if (id === NEW_ENV_PARAM) {
                currentEnvDetailInfo.value = {
                    projectId: appStore.currentProjectId,
                    name: '',
                    config: cloneDeep(envParamsDefaultConfig),
                };
                backupEnvDetailInfo.value = {
                    projectId: appStore.currentProjectId,
                    name: '',
                    config: cloneDeep(envParamsDefaultConfig),
                };
            } else if (id === ALL_PARAM) {
                const res = await envManagementAPi.getGlobalParamDetail(appStore.currentProjectId).send(true);
                allParamDetailInfo.value = cloneDeep(res || defaultAllParams);
                await nextTick(() => {
                    backupAllParamDetailInfo.value = cloneDeep(allParamDetailInfo.value);
                });
            } else if (id === NEW_ENV_PARAM_COPY && copyId) {
                await copyCurrentEnv(copyId);
            } else if (id && id !== ALL_PARAM && id !== NEW_ENV_PARAM_COPY) {
                const tmpObj = await envManagementAPi.getDetailEnv(id).send(true);
                currentEnvDetailInfo.value = {...tmpObj};
                setDetailInfo(currentEnvDetailInfo.value);
            }
        } catch (e) {
            // eslint-disable-next-line no-console
            console.log(e);
        }
    }

    return {
        currentId,
        currentGroupId,
        getHttpNoWarning,
        httpNoWarning,
        allParamDetailInfo,
        currentEnvDetailInfo,
        backupEnvDetailInfo,
        setCurrentId,
        setCurrentGroupId,
        setHttpNoWarning,
        setAllParamDetailInfo,
        backupAllParamDetailInfo,
        initEnvDetail,
    };
}, {
    persist: {
        key: 'projectEnv',
        pick: ['httpNoWarning'],
    },
})
export default useProjectEnvStore;