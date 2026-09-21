import type {PageQuery, PageResult} from "/@/types/common.ts";

export interface TaskCenterSystemTaskItem {
    organizationName: string; // 所属组织名称
    projectName: string; // 所属项目名称
    projectId: string; // 项目ID
    organizationId: string; // 组织ID
    id: string;
    reportId: string;
    name: string;
    resourceId: string; // 资源ID
    num: number;
    resourceType: string; // 资源类型
    resourceNum: number; // 资源num
    cronExpression: string;
    nextTime: number;
    enable: boolean;
    createUserId: string;
    createUserName: string;
    createTime: number;

    [key: string]: any;
}

export type TaskPageRes = PageResult<TaskCenterSystemTaskItem>
export type TaskQuery = PageQuery<TaskCenterSystemTaskItem>;
export type editType = Partial<TaskCenterSystemTaskItem>

export type TaskParameterType = 'string' | 'number' | 'boolean';
export interface TaskParameterItem {
    label: string;
    key: string;
    type: TaskParameterType;
    value: string | number | boolean;
    enabled: boolean;
}

export type TaskParameterMap = Record<string, TaskParameterItem>;
export type editJobParamType = { id: string, parameters: TaskParameterMap }
export type TaskParamType = { id: string, config: { parameters: TaskParameterMap } }
