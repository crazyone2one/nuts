package cn.master.nuts.module.project.service;

import cn.master.nuts.constants.ModuleConstants;
import cn.master.nuts.dto.BaseTreeNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/9/23, 星期三
 **/

public abstract class ModuleTreeService {
    public BaseTreeNode getDefaultModule(String name) {
        // 默认模块下不允许创建子模块。  它本身也就是叶子节点。
        return new BaseTreeNode(ModuleConstants.DEFAULT_NODE_ID, name, ModuleConstants.NODE_TYPE_DEFAULT, ModuleConstants.ROOT_NODE_PARENT_ID);
    }

    protected List<BaseTreeNode> buildTreeAndCountResource(List<BaseTreeNode> traverseList, boolean haveVirtualRootNode, String virtualRootName) {
        List<BaseTreeNode> baseTreeNodeList = new ArrayList<>();
        if (haveVirtualRootNode) {
            BaseTreeNode defaultNode = this.getDefaultModule(virtualRootName);
            defaultNode.genModulePath(null);
            baseTreeNodeList.add(defaultNode);
        }
        int lastSize = 0;
        Map<String, BaseTreeNode> baseTreeNodeMap = new HashMap<>();

        // 根节点预先处理
        baseTreeNodeList.addAll(traverseList.stream().filter(treeNode -> Strings.CI.equals(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)).toList());
        baseTreeNodeList.forEach(item -> baseTreeNodeMap.put(item.getId(), item));
        traverseList = (List<BaseTreeNode>) CollectionUtils.removeAll(traverseList, baseTreeNodeList);

        // 循环处理子节点
        while (CollectionUtils.isNotEmpty(traverseList) && traverseList.size() != lastSize) {
            lastSize = traverseList.size();
            List<BaseTreeNode> notMatchedList = new ArrayList<>();
            for (BaseTreeNode treeNode : traverseList) {
                if (!baseTreeNodeMap.containsKey(treeNode.getParentId()) && !Strings.CI.equals(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                    notMatchedList.add(treeNode);
                    continue;
                }
                BaseTreeNode node = new BaseTreeNode(treeNode.getId(), treeNode.getName(), treeNode.getType(), treeNode.getParentId());
                node.genModulePath(baseTreeNodeMap.get(treeNode.getParentId()));
                baseTreeNodeMap.put(treeNode.getId(), node);

                if (Strings.CI.equals(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                    baseTreeNodeList.add(node);
                } else if (baseTreeNodeMap.containsKey(treeNode.getParentId())) {
                    baseTreeNodeMap.get(treeNode.getParentId()).addChild(node);
                }
            }
            traverseList = notMatchedList;
        }
        return baseTreeNodeList;
    }
}
