import type {PageQuery, PageResult} from "/@/types/common.ts";

export type SystemScopeType = 'PROJECT' | 'ORGANIZATION' | 'SYSTEM';

export interface IUserItem {
    id: string;
    name: string;
    email: string;
    password?: string;
    avatar?: string;
    enable?: boolean;
    createTime?: number;
    updateTime?: number;
    language?: string; // 语言
    lastOrganizationId?: string; // 当前组织ID
    phone?: string;
    source?: string; // 来源：LOCAL OIDC CAS OAUTH2
    lastProjectId?: string; // 当前项目ID
    createUser?: string;
    updateUser?: string;
    selectUserGroupVisible?: boolean;
    adminFlag?: boolean;
    memberFlag?: boolean;
}

export interface UserRole {
    createTime: number;
    updateTime: number;
    createUser: string;
    description?: string;
    id: string;
    name: string;
    scopeId: string; // 项目/组织/系统 id
    type: SystemScopeType;
}

export interface permissionsItem {
    id: string;
    permissionId: string;
    roleId: string;
}

export interface UserRolePermissions {
    userRole: UserRole;
    userRolePermissions: permissionsItem[];
}

export interface UserRoleRelation {
    id: string;
    userId: string;
    roleId: string;
    sourceId: string;
    organizationId: string;
    createTime: number;
    createUser: string;
    userRolePermissions: permissionsItem[];
    userRole: UserRole;
}

export type addType = Omit<IUserItem, keyof IUserItem>
export type editType = Pick<IUserItem, "id"> & Partial<Omit<IUserItem, "id">>

export type UserPageRes = PageResult<IUserItem>
export type UserQuery = PageQuery<IUserItem>;
export type UserState =
    { userRolePermissions?: UserRolePermissions[], userRoles?: UserRole[], userRoleRelations?: UserRoleRelation[] }
    & Pick<IUserItem, keyof IUserItem>
export type UserExcludeOptionDTO = {
    id: string,
    name: string,
    email: string,
    exclude: boolean
}