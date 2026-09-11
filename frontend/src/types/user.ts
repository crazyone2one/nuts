import type {PageQuery, PageResult} from "/@/types/common.ts";

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

export type addType = Omit<IUserItem, keyof IUserItem>
export type editType = Pick<IUserItem, "id"> & Partial<Omit<IUserItem, "id">>

export type UserPageRes = PageResult<IUserItem>
export type UserQuery = PageQuery<IUserItem>;
export type UserState =
    { userRolePermissions?: [], userRoles?: [], userRoleRelations?: [] }
    & Pick<IUserItem, keyof IUserItem>
