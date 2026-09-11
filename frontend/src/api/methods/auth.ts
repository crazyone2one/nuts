import {instance} from "/@/api";
import type {IUserItem} from "/@/types/user.ts";

type LoginResponse =
    { accessToken: string }
    & Pick<IUserItem, 'name' | 'email' | 'id' | 'lastProjectId' | 'lastOrganizationId'>
export const authApi = {
    login: (param: { username: string, password: string }) => {
        const method = instance.Post<LoginResponse>('/login', param);
        method.meta = {
            login: true
        };
        return method
    },
    refresh: () => {
        const method = instance.Post<{ accessToken: string }>('/refresh')
        method.meta = {
            refreshToken: true
        };
        return method;
    },
    logout: () => {
        const method = instance.Post('/logout');
        method.meta = {
            logout: true
        };
        return method;
    }
}