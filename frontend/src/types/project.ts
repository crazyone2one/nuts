// 项目列表项
import type {IUserItem} from "/@/types/user.ts";
import type {PageQuery, PageResult} from "/@/types/common.ts";

export interface IProjectListItem {
    id: string;
    name: string;
    description: string;
    enable: boolean;
    adminList: IUserItem[];
    organizationId: string;
    organizationName: string;
    num: string;
    updateTime: number;
    createTime: number;
    memberCount: number;
    userIds: string[];
    resourcePoolIds: string[];
    orgAdmins: Record<string, any>;
    moduleIds?: string[];
    allResourcePool: boolean;
}

export type editType = Partial<IProjectListItem>
export type ProjectPageRes = PageResult<IProjectListItem>
export type ProjectQuery = PageQuery<IProjectListItem>;