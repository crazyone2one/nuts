import type {UserState} from "/@/types/user.ts";
import {useAppStore} from "/@/store";
import {composePermissions} from "/@/utils/permission.ts";

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
        isAdmin(state: UserState): boolean {
            if (!state.userRolePermissions) return false;
            return state.userRolePermissions.findIndex((ur) => ur.userRole.id === 'admin') > -1;
        },
        currentRole(state: UserState): {
            projectPermissions: string[];
            orgPermissions: string[];
            systemPermissions: string[];
        } {
            const appStore = useAppStore();

            state.userRoleRelations?.forEach((ug) => {
                state.userRolePermissions?.forEach((gp) => {
                    if (gp.userRole.id === ug.roleId) {
                        ug.userRolePermissions = gp.userRolePermissions;
                        ug.userRole = gp.userRole;
                    }
                });
            });

            return {
                projectPermissions: composePermissions(state.userRoleRelations || [], 'PROJECT', appStore.currentProjectId),
                orgPermissions: composePermissions(state.userRoleRelations || [], 'ORGANIZATION', appStore.currentOrgId),
                systemPermissions: composePermissions(state.userRoleRelations || [], 'SYSTEM', 'global'),
            };
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