import type {UserState} from "/@/types/user.ts";

const useUserStore = defineStore('user', {
    persist: true,
    state: (): UserState => ({
        name: '',
        avatar: '',
        email: '',
        phone: '',
        id: '',
        userRolePermissions: [],
        userRoles: [],
        userRoleRelations: [],
        lastOrganizationId: '',
        lastProjectId: '',
    }),
    getters: {
        userInfo(state: UserState): UserState {
            return {...state};
        },
    },
    actions: {
        setInfo(partial: UserState) {
            this.$patch(partial);
        },
        resetInfo() {
            this.$reset();
        },
    }
})
export default useUserStore