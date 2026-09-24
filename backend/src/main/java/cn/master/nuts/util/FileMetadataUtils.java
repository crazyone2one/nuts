package cn.master.nuts.util;

import cn.master.nuts.dto.filemanagement.FileMetadataTableRequest;
import cn.master.nuts.module.project.entity.FileMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
public class FileMetadataUtils {
    public static final String FILE_TYPE_EMPTY = "unknown";

    private FileMetadataUtils() {
    }

    public static boolean isUnknownFile(String filePath) {
        return Strings.CS.endsWith(filePath, ".") || Strings.CI.endsWithAny(filePath, ".unknown") || filePath.indexOf(".") < 1;
    }

    public static void transformEmptyFileType(List<String> fileTypes) {
        if (fileTypes.contains(StringUtils.EMPTY)) {
            fileTypes.remove(StringUtils.EMPTY);
            fileTypes.add(FILE_TYPE_EMPTY);
        }
    }

    public static void transformRequestFileType(FileMetadataTableRequest request) {
        if (Strings.CI.equals(request.getFileType(), FILE_TYPE_EMPTY)) {
            request.setFileType("");
        }
    }

    public static String getFileName(FileMetadata fileMetadata) {
        if (StringUtils.isBlank(fileMetadata.getType())) {
            return fileMetadata.getName();
        }
        return fileMetadata.getName() + "." + fileMetadata.getType();
    }
}
