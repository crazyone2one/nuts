package cn.master.nuts.module.project.service;

import cn.master.nuts.constants.ModuleConstants;
import cn.master.nuts.dto.BaseTreeNode;
import cn.master.nuts.dto.filemanagement.FileModuleCreateRequest;
import cn.master.nuts.dto.filemanagement.FileModuleUpdateRequest;
import cn.master.nuts.handler.exception.NSException;
import cn.master.nuts.module.log.service.FileModuleLogService;
import cn.master.nuts.module.project.entity.FileModule;
import cn.master.nuts.module.project.mapper.FileModuleMapper;
import cn.master.nuts.module.system.service.CleanupProjectResourceService;
import cn.master.nuts.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.master.nuts.module.project.entity.table.FileModuleTableDef.FILE_MODULE;

/**
 * 文件管理模块 服务层实现。
 *
 * @author 11's papa
 * @since 2026-09-22
 */
@Service
@RequiredArgsConstructor
public class FileModuleService extends ModuleTreeService implements CleanupProjectResourceService {
    private final FileModuleLogService fileModuleLogService;
    private final FileManagementService fileManagementService;
    private final FileModuleMapper fileModuleMapper;
    public static final long DEFAULT_NODE_INTERVAL_POS = 4096;

    public String add(FileModuleCreateRequest request, String operator) {
        FileModule fileModule = new FileModule();
        fileModule.setName(request.getName().trim());
        fileModule.setParentId(request.getParentId());
        fileModule.setProjectId(request.getProjectId());
        fileModule.setModuleType(ModuleConstants.NODE_TYPE_DEFAULT);
        checkDataValidity(fileModule);
        fileModule.setPos(countPos(request.getParentId(), ModuleConstants.NODE_TYPE_DEFAULT));
        fileModule.setCreateUser(operator);
        fileModule.setUpdateUser(operator);
        fileModuleMapper.insertSelective(fileModule);
        fileModuleLogService.saveAddLog(fileModule, operator);
        return fileModule.getId();
    }

    public void update(FileModuleUpdateRequest request, String userId) {
        FileModule module = fileModuleMapper.selectOneById(request.id());
        if (module == null) {
            throw new NSException("file_module.not.exist");
        }
        FileModule updateModule = new FileModule();
        updateModule.setId(request.id());
        updateModule.setName(request.name().trim());
        updateModule.setParentId(module.getParentId());
        updateModule.setModuleType(module.getModuleType());
        updateModule.setProjectId(module.getProjectId());
        checkDataValidity(updateModule);
        fileModuleMapper.update(updateModule);
        FileModule newModule = fileModuleMapper.selectOneById(request.id());
        // 记录日志
        fileModuleLogService.saveUpdateLog(module, newModule, module.getProjectId(), userId);
    }

    public void deleteModule(String deleteId, String currentUser) {
        FileModule deleteModule = fileModuleMapper.selectOneById(deleteId);
        if (Objects.nonNull(deleteModule)) {
            deleteModule(Collections.singletonList(deleteId));
            fileModuleLogService.saveDeleteLog(deleteModule, currentUser);
        }
    }

    public List<BaseTreeNode> getTree(String projectId) {
        List<BaseTreeNode> fileModuleList = QueryChain.of(FileModule.class).select(FILE_MODULE.ID, FILE_MODULE.NAME, FILE_MODULE.PARENT_ID)
                .select("'module' AS type")
                .where(FILE_MODULE.PROJECT_ID.eq(projectId).and(FILE_MODULE.MODULE_TYPE.eq(ModuleConstants.NODE_TYPE_DEFAULT)))
                .orderBy(FILE_MODULE.POS.desc())
                .listAs(BaseTreeNode.class);
        return super.buildTreeAndCountResource(fileModuleList, true, Translator.get("file.module.default.name"));
    }

    private void deleteModule(List<String> deleteIds) {
        if (CollectionUtils.isEmpty(deleteIds)) {
            return;
        }
        fileModuleMapper.deleteBatchByIds(deleteIds);
        fileManagementService.deleteByModuleIds(deleteIds);
        List<String> childrenIds = QueryChain.of(FileModule.class).select(FILE_MODULE.ID).where(FILE_MODULE.PARENT_ID.in(deleteIds)).listAs(String.class);
        if (CollectionUtils.isNotEmpty(childrenIds)) {
            deleteModule(childrenIds);
        }
    }

    private Long countPos(String parentId, String fileType) {
        Long maxPos = QueryChain.of(FileModule.class).select(QueryMethods.max(FILE_MODULE.POS)).where(FILE_MODULE.PARENT_ID.eq(parentId).and(FILE_MODULE.MODULE_TYPE.eq(fileType)))
                .oneAs(Long.class);
        if (maxPos == null) {
            return DEFAULT_NODE_INTERVAL_POS;
        } else {
            return maxPos + DEFAULT_NODE_INTERVAL_POS;
        }
    }

    private void checkDataValidity(FileModule fileModule) {
        QueryChain<FileModule> queryChain = QueryChain.of(FileModule.class);
        if (!Strings.CI.equals(fileModule.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
            queryChain.where(FILE_MODULE.ID.eq(fileModule.getParentId()));
            if (!queryChain.exists()) {
                throw new NSException(Translator.get("parent.node.not_blank"));
            }
            queryChain.clear();
            if (StringUtils.isNotBlank(fileModule.getProjectId())) {
                queryChain.where(FILE_MODULE.PROJECT_ID.eq(fileModule.getProjectId())
                        .and(FILE_MODULE.MODULE_TYPE.eq(fileModule.getModuleType()))
                        .and(FILE_MODULE.ID.eq(fileModule.getParentId())));
                if (!queryChain.exists()) {
                    throw new NSException(Translator.get("project.cannot.match.parent"));
                }
                queryChain.clear();
            }
        }
        queryChain.where(FILE_MODULE.PARENT_ID.eq(fileModule.getParentId())
                .and(FILE_MODULE.NAME.eq(fileModule.getName()))
                .and(FILE_MODULE.MODULE_TYPE.eq(fileModule.getModuleType()))
                .and(FILE_MODULE.PROJECT_ID.eq(fileModule.getProjectId()))
                .and(FILE_MODULE.ID.ne(fileModule.getId())));
        if (queryChain.exists()) {
            throw new NSException(Translator.get("node.name.repeat"));
        }
    }

    @Override
    public void deleteResources(String projectId) {
        List<String> fileModuleIdList = QueryChain.of(FileModule.class).select(FILE_MODULE.ID).where(FILE_MODULE.PROJECT_ID.eq(projectId)).listAs(String.class);
        if (CollectionUtils.isNotEmpty(fileModuleIdList)) {
            deleteModule(fileModuleIdList);
        }
    }
}
