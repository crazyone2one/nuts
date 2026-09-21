import {instance} from "/@/api";
import type {editJobParamType, editType, TaskPageRes, TaskParamType, TaskQuery} from "/@/types/task-center.ts";

export const projectTaskApi = {
    getProjectScheduleList: (query: TaskQuery) => instance.Post<TaskPageRes>('/project/task-center/schedule/page', query),
    saveSchedule: (data: editType) => instance.Post('/schedule/save', data),
    updateProjectScheduleParam: (data: editJobParamType) => instance.Post('/project/task-center/schedule/param', data),
    projectScheduleSwitch: (id: string) => instance.Get(`/project/task-center/schedule/switch/${id}`),
    projectDeleteSchedule: (id: string) => instance.Get(`/project/task-center/schedule/delete/${id}`),
    projectGetScheduleParam: (id: string) => instance.Get<TaskParamType>(`/project/task-center/schedule/getParam/${id}`),
}