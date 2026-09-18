import {instance} from "/@/api";
import type {UserExcludeOptionDTO} from "/@/types/user.ts";

export const userGroupApi = {
    getSystemUserGroupOption: (id: string, keyword: string) =>
        instance.Get<Array<UserExcludeOptionDTO>>(`/user/role/relation/global/user/option/${id}`, {params: {keyword}}),
}