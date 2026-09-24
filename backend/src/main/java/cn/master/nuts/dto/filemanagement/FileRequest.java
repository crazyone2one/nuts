package cn.master.nuts.dto.filemanagement;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
@Data
@NoArgsConstructor
public class FileRequest {
    private String folder;

    // 存储类型
    private String storage;

    // 文件名称
    private String fileName;
    public FileRequest(String folder, String storage, String fileName) {
        this.folder = folder;
        this.storage = storage;
        this.fileName = fileName;
    }
}
