import type {editType, IProjectListItem, ProjectPageRes, ProjectQuery} from "/@/types/project.ts";
import {instance} from "/@/api";
import type {UserExcludeOptionDTO} from "/@/types/user.ts";

export const projectApi = {
    createOrUpdateProject: (data: editType) =>
        instance.Post(data.id ? '/system/project/update' : '/system/project/save', data),
    enableOrDisableProject: (id: string, isEnable = true) =>
        instance.Post(isEnable ? `/system/project/enable/${id}` : `/system/project/disable/${id}`),
    renameProject: (data: { id: string; name: string; organizationId: string }) =>
        instance.Post('/system/project/rename', data),
    getProjectPage: (query: ProjectQuery) => instance.Post<ProjectPageRes>('/system/project/page', query),
    deleteProject: (id: string) => instance.Get(`/system/project/remove/${id}`),
    getUserByOrganizationOrProject: (sourceId: string, keyword: string) => instance.Get<Array<UserExcludeOptionDTO>>(`/system/organization/get-option/${sourceId}`, {params: keyword}),
    getAdminByOrganizationOrProject: (keyword: string) => instance.Get<Array<UserExcludeOptionDTO>>(`/system/project/user-list`, {params: keyword}),
    getProjectList: (organizationId: string) => {
        const method = instance.Get<Array<Partial<IProjectListItem>>>(`/project/list/options/${organizationId}`);
        method.meta = {
            authRole: null
        };
        return method
    },
    switchProject: (data: { projectId: string; userId: string }) => instance.Post('/project/switch', data),
}