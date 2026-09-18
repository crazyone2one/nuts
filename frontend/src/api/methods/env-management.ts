import {instance} from "/@/api";
import type {EnvDetailItem, EnvListItem, GlobalParams} from "/@/types/environmental.ts";
import {uploadFile} from "/@/api/methods/upload.ts";

export const envManagementAPi = {
    getDetailEnv: (id: string) => instance.Get<EnvDetailItem>(`/project/environment/getInfo/${id}`),
    /** 项目管理-环境-全局参数-详情 */
    getGlobalParamDetail: (id: string) => instance.Get<GlobalParams>(`/project/global/params/get//${id}`),
    listEnv: (data: {
        projectId: string;
        keyword: string
    }) => instance.Post<EnvListItem[]>(`/project/environment/list`, data),
    updateOrAddEnv: (data: { request: EnvDetailItem; fileList?: (File | Blob)[] }) => uploadFile(data.fileList ?? [],
        {
            url: data.request.id ? '/project/environment/update' : '/project/environment/save',
            request: data.request,
            fileFieldName: 'file',
        }, false)
}