import {type IPathMapItem, MENU_LEVEL, pathMap, type PathMapRoute} from "/@/router/path-map.ts";
import {findNodeByKey, mapTree, type TreeNode} from "/@/utils";

export const usePathMap = () => {
    const getRouteLevelByKey = (name: PathMapRoute) => {
        const pathNode = findNodeByKey<IPathMapItem>(pathMap, name, 'route');
        if (pathNode) {
            return pathNode.level;
        }
        return null;
    };
    const getPathMapByLevel = <T>(
        level: (typeof MENU_LEVEL)[number],
        customNodeFn: (node: TreeNode<T>) => TreeNode<T> | null = (node) => node
    ) => {
        return mapTree(pathMap, (e) => {
            let isValid = true; // 默认是系统级别
            if (level === MENU_LEVEL[1]) {
                // 组织级别只展示组织、项目
                isValid = e.level !== MENU_LEVEL[0];
            } else if (level === MENU_LEVEL[2]) {
                // 项目级别只展示项目
                isValid = e.level !== MENU_LEVEL[0] && e.level !== MENU_LEVEL[1];
            }
            if (isValid && !e.hideInModule) {
                return typeof customNodeFn === 'function' ? customNodeFn(e) : e;
            }
            return null;
        });
    };
    return {
        getPathMapByLevel,
        getRouteLevelByKey,
    };
}