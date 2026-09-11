import {cloneDeep} from "lodash-es";

export interface TreeNode<T> {
    children?: TreeNode<T>[];

    [key: string]: any;
}

export function findNodeByKey<T>(
    trees: TreeNode<T>[],
    targetKey: string | number,
    customKey = 'key',
    dataKey: string | undefined = undefined
): TreeNode<T> | T | null {
    for (let i = 0; i < trees.length; i++) {
        const node = trees[i];
        if (dataKey ? node[dataKey]?.[customKey] === targetKey : node[customKey] === targetKey) {
            return node; // 如果当前节点的 key 与目标 key 匹配，则返回当前节点
        }

        if (Array.isArray(node.children) && node.children.length > 0) {
            const _node = findNodeByKey(node.children, targetKey, customKey, dataKey); // 递归在子节点中查找
            if (_node) {
                return _node; // 如果在子节点中找到了匹配的节点，则返回该节点
            }
        }
    }
    return null; // 如果在整个树形数组中都没有找到匹配的节点，则返回 null
}

export function mapTree<T>(
    tree: TreeNode<T> | TreeNode<T>[] | T | T[],
    customNodeFn: (node: TreeNode<T>, path: string, _level: number) => TreeNode<T> | null = (node) => node,
    customChildrenKey = 'children',
    parentPath = '',
    level = 0,
    parent: TreeNode<T> | null = null
): T[] {
    let cloneTree = cloneDeep(tree);
    if (!Array.isArray(cloneTree)) {
        cloneTree = [cloneTree];
    }

    function mapFunc(
        _tree: TreeNode<T> | TreeNode<T>[] | T | T[],
        _parentPath = '',
        _level = 0,
        _parent: TreeNode<T> | null = null
    ): T[] {
        if (!Array.isArray(_tree)) {
            _tree = [_tree];
        }
        return _tree
            .map((node: TreeNode<T>, i: number) => {
                const fullPath = node.path ? `${_parentPath}/${node.path}`.replace(/\/+/g, '/') : '';
                node.sort = i + 1; // sort 从 1 开始
                node.parent = _parent || undefined; // 没有父节点说明是树的第一层
                const newNode = typeof customNodeFn === 'function' ? customNodeFn(node, fullPath, _level) : node;
                if (newNode) {
                    newNode.level = _level;
                    if (newNode[customChildrenKey] && newNode[customChildrenKey].length > 0) {
                        newNode[customChildrenKey] = mapFunc(newNode[customChildrenKey], fullPath, _level + 1, newNode);
                    }
                }
                return newNode;
            })
            .filter((node: TreeNode<T> | null) => node !== null);
    }

    return mapFunc(cloneTree, parentPath, level, parent);
}
let lastTimestamp = 0;
let sequence = 0;
export const getGenerateId = () => {
    let timestamp = new Date().getTime();
    if (timestamp === lastTimestamp) {
        sequence++;
        if (sequence >= 100000) {
            // 如果超过999，则重置为0，等待下一秒
            sequence = 0;
            while (timestamp <= lastTimestamp) {
                timestamp = new Date().getTime();
            }
        }
    } else {
        sequence = 0;
    }

    lastTimestamp = timestamp;

    return timestamp.toString() + sequence.toString().padStart(5, '0');
}

export const getHashParameters = () => {
    const query = window.location.hash.split('?')[1]; // 获取 URL 哈希参数部分
    const paramsArray = query?.split('&') || []; // 将哈希参数字符串分割成数组
    const params: Record<string, string> = {};

    // 遍历数组并解析参数
    paramsArray.forEach((param) => {
        const [key, value] = param.split('=');
        if (key && value) {
            params[key] = decodeURIComponent(value); // 解码参数值
        }
    });
    return params;
}
export const getQueryVariable = (variable: string) => {
    const urlString = window.location.href;
    const queryIndex = urlString.indexOf('?');
    if (queryIndex !== -1) {
        const query = urlString.substring(queryIndex + 1);

        // 分割查询参数
        const params = query.split('&');
        // 遍历参数，找到 _token 参数的值
        let variableValue;
        params.forEach((param) => {
            const equalIndex = param.indexOf('=');
            const variableName = param.substring(0, equalIndex);
            if (variableName === variable) {
                variableValue = param.substring(equalIndex + 1);
            }
        });
        return variableValue;
    }
}