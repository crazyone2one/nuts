import {userGroupApi} from "/@/api/methods/user-group.ts";
import {projectApi} from "/@/api/methods/project.ts";

export const UserRequestTypeEnum = {
    SYSTEM_USER_GROUP: 'SYSTEM_USER_GROUP',
    SYSTEM_PROJECT: 'SYSTEM_PROJECT',
    SYSTEM_PROJECT_ADMIN: 'SYSTEM_PROJECT_ADMIN',
    SYSTEM_ORGANIZATION_ADMIN: 'SYSTEM_ORGANIZATION_ADMIN'
} as const;

export type UserRequestType = typeof UserRequestTypeEnum[keyof typeof UserRequestTypeEnum];

export const initOptionsFunc = (type: string, params: Record<string, any>) => {
    if (type === UserRequestTypeEnum.SYSTEM_USER_GROUP) {
        return userGroupApi.getSystemUserGroupOption(params.roleId, params.keyword);
    }
    if (type === UserRequestTypeEnum.SYSTEM_ORGANIZATION_ADMIN || type === UserRequestTypeEnum.SYSTEM_PROJECT_ADMIN) {
        // 系统 - 【组织 或 项目】-添加管理员-下拉选项
        return projectApi.getAdminByOrganizationOrProject(params.keyword);
    }
    return projectApi.getUserByOrganizationOrProject(params.sourceId, params.keyword)
}