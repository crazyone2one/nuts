import {instance} from "/@/api";
import type {TaskPageRes, TaskQuery} from "/@/types/task-center.ts";

export const systemTaskApi = {
    getSystemScheduleList: (query: TaskQuery) => instance.Post<TaskPageRes>('/system/task-center/schedule/page', query),
}