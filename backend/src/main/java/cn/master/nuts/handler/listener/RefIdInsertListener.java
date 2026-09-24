package cn.master.nuts.handler.listener;

import cn.master.nuts.module.project.entity.FileMetadata;
import com.mybatisflex.annotation.InsertListener;

import java.util.Objects;

/**
 * @author : 11's papa
 * @since : 2026/9/24, 星期四
 **/
public class RefIdInsertListener implements InsertListener {
    @Override
    public void onInsert(Object entity) {
        FileMetadata fileMetadata = (FileMetadata) entity;
        if (Objects.nonNull(fileMetadata.getId())) {
            fileMetadata.setRefId(fileMetadata.getId());
        }
    }
}
