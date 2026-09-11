import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import type {App} from "vue";
import useAppStore from "/@/store/modules/app";
import useUserStore from "/@/store/modules/user";

const store = createPinia().use(piniaPluginPersistedstate);
export const setupStore = (app: App<Element>) => {
    app.use(store)
}
export {useAppStore, useUserStore};
export default store