import type {RouteRecordNormalized} from 'vue-router';

const modules = import.meta.glob('./modules/*.ts', {eager: true});
export const formatModules = (_modules: any, result: RouteRecordNormalized[]) => {
    Object.keys(_modules).forEach((key) => {
        const defaultModule = _modules[key].default;
        if (!defaultModule) return;
        const moduleList = Array.isArray(defaultModule) ? [...defaultModule] : [defaultModule];
        result.push(...moduleList);
    });
    return result;
}
const appRoutes: RouteRecordNormalized[] = formatModules(modules, []);

export default appRoutes;
export const appClientMenus=[...appRoutes].map(r=>{
    const {name, path, meta, redirect, children} = r;
    return {
        name,
        path,
        meta,
        redirect,
        children,
    };
})