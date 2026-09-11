import type {UserPageRes, UserQuery} from "/@/types/user.ts";
import {instance} from "/@/api";

export const userApi = {
    getUserPage: (query: UserQuery) => instance.Post<UserPageRes>('/user/page', query),
    getUserList: () => instance.Get('/system/user/list')
}